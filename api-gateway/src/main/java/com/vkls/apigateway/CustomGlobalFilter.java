package com.vkls.apigateway;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.vkls.apiclientsdk.utils.SignUtil;
import com.vkls.apicommon.model.entity.InterfaceInfo;
import com.vkls.apicommon.model.entity.User;
import com.vkls.apicommon.service.InnerInterfaceInfoService;
import com.vkls.apicommon.service.InnerUserInterfaceInfoService;
import com.vkls.apicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    //定义访问白名单
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1","127.0.0.2");
    private static final String INTERFACE_HOST = "http://localhost:8090";
    Long FIVE_MINUTES=60*5L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).toString();
        //1.记录请求日志
        log.info("请求id:{}",request.getId());
        log.info("请求路径: {}", request.getPath());
        log.info("请求方法: {}", request.getMethod());
        log.info("请求参数: {}", request.getQueryParams());
        log.info("请求头: {}", request.getHeaders());
        String remoteAddress = request.getRemoteAddress().getHostString();
        log.info("请求地址: {}", remoteAddress);

        //2.访问控制，这里使用白名单，更加安全
        //如果请求地址不在白名单中，则拦截掉，设置状态码为403
        ServerHttpResponse response = exchange.getResponse();
        if(!IP_WHITE_LIST.contains(remoteAddress)){
            return handleNoAuth(response);
        }
        //3.用户鉴权
        HttpHeaders headers=request.getHeaders();
        String accessKey=headers.getFirst("accessKey");
        // 防止中文乱码
        String body = null;
        try {
            body = URLDecoder.decode(headers.getFirst("body"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String sign = headers.getFirst("sign");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        // 判断是否为空
        boolean hasBlank = StrUtil.hasBlank(accessKey, body, sign, nonce, timestamp);
        if (hasBlank) {
            return handleInvokeError(response);
        }
        // 这里使用accessKey去数据库查询secretKey
        User invokeUser=null;
        try{
            invokeUser=innerUserService.getInvokeUser(accessKey);
        }catch (Exception e) {
            log.error("getInvokeUser error",e);
        }
        if(invokeUser==null){
            return handleInvokeError(response);
        }
        //获得secretKey，然后进行加密
        String secretKey = invokeUser.getSecretKey();
        String sign1 = SignUtil.getSign(body, secretKey);
        //如果加密后发现不相等
        if (!StrUtil.equals(sign, sign1)) {
            return handleInvokeError(response);
        }
        // TODO 判断随机数nonce
        // 判断时间戳是否为数字
        if (!NumberUtil.isNumber(timestamp)) {
            return handleInvokeError(response);
        }

        // 五分钟内的请求有效
        if (System.currentTimeMillis() - Long.parseLong(timestamp) > FIVE_MINUTES) {
            return handleInvokeError(response);
        }
        //4.判断请求的模拟接口是否存在
        InterfaceInfo invokeInterfaceInfo=null;
        try{
            invokeInterfaceInfo=innerInterfaceInfoService.getInterfaceInfo(path,method);
        }catch (Exception e) {
            log.error("getInvokeInterfaceInfo error",e);
        }
        if(invokeInterfaceInfo == null){
            return handleInvokeError(response);
        }
        //判断是否还有调用次数
        if (!innerUserInterfaceInfoService.hasInvokeNum(invokeUser.getId(), invokeInterfaceInfo.getId())) {
            return handleInvokeError(response);
        }
        /**
         * 我们可以从**数据库**中查询模拟接口是否存在，以及请求方法是否匹配（还可以校验请求参数）
         * 因为网关项目没引入MyBatis等操作数据库的类库，如果该操作较为复杂，可以由backend增删改查项目提供接口，我们直接调用，不用再重复写逻辑了。
         * - HTTP请求（用HTTPClient、用RestTemplate、Feign)
         * - RPC(Dubbo)
         */

        // 5. 请求转发，调用模拟接口
        // 6. 响应日志
        return handleResponse(exchange, chain,invokeUser.getId(),invokeInterfaceInfo.getId());
//
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long userId, long interfaceInfoId) {
        try {
            // 从交换机拿到原始response
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓冲区工厂 拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到状态码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 对象是响应式的
                        if (body instanceof Flux) {
                            // 我们拿到真正的body
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里面写数据
                            // 拼接字符串
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 7. 调用成功，接口调用次数+1
                                try {
                                    innerUserInterfaceInfoService.invokeCount(userId, interfaceInfoId);
                                } catch (Exception e) {
                                    log.error("invokeInterfaceCount error", e);
                                }
                                // data从这个content中读取
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);// 释放掉内存
                                // 6.构建日志
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);// data
                                rspArgs.add(data);
                                log.info("<--- status:{} data:{}"// data
                                        , rspArgs.toArray());// log.info("<-- {} {}", originalResponse.getStatusCode(), data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            // 8.调用失败返回错误状态码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);// 降级处理返回数据
        } catch (Exception e) {
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }

    }

}