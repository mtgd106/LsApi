package com.vkls.apiinterface.controller;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.vkls.apiclientsdk.model.User;
import com.vkls.apiclientsdk.utils.SignUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 名称 API
 * 查询名称接口
 * @author JinZe
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "Get 你的名字是"+name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "Post 你的名字是"+name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) throws UnsupportedEncodingException {


        String accessKey=request.getHeader("accessKey");
        //使用utf-8对body进行解码
        String body= URLDecoder.decode(request.getHeader("body"), StandardCharsets.UTF_8.name());
        //获取请求参数中的签名
        String sign=request.getHeader("sign");
        String nonce=request.getHeader("nonce");
        String timestamp= request.getHeader("timestamp");
        boolean hasBlank= StrUtil.hasBlank(accessKey,body,sign,nonce,timestamp);
        if(hasBlank){
            return "没有权限";
        }
        // TODO 使用accessKey字段去user表中查询secretKey
        // 这里假设查到的secretKey是vkls，然后进行加密得到sign
        String secretKey="vkls";
        String sign1= SignUtil.getSign(body,secretKey);
        //和用户传入的sign进行对比，
        if(!StrUtil.equals(sign,sign1)){
            return "密码不正确，没有权限";
        }

        //TODO 判断随机数字nonce

        //时间戳是否为数字
        if(!NumberUtil.isNumber(timestamp)){
            return "时间戳错误，没有权限";
        }
        // 五分钟内的请求有效
        if (System.currentTimeMillis() - Long.parseLong(timestamp) > 5 * 60 * 1000) {
            return "五分钟内不能重复请求";
        }
        //如果所有的校验都通过，则返回数据
        return "发送POST请求 JSON中你的名字是：" + user.getUsername();
    }
}