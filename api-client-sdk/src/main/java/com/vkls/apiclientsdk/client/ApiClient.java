package com.vkls.apiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.vkls.apiclientsdk.model.User;
import com.vkls.apiclientsdk.utils.SignUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//调用第三方接口的客户端
public class ApiClient {

    private String accessKey;
    private String secretKey;

    private static final String GATEWAY_HOST="http://localhost:8090";
    public ApiClient(String accessKey, String secretKey){
        this.accessKey=accessKey;
        this.secretKey=secretKey;
    }
    public String getNameByGet(String name){
        return HttpUtil.get(GATEWAY_HOST+"/api/name"+name);
    }
    public String getNameByPost(String name){
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String,Object> paraMap=new HashMap<>();
        paraMap.put("name",name);
        return HttpUtil.post(GATEWAY_HOST+"/api/name", paraMap);
    }

    //生成请求头信息
    private Map<String,String> getHeaderMap(String body) throws UnsupportedEncodingException {
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("accessKey",accessKey);
        //防止中文乱码
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", URLEncoder.encode(body, StandardCharsets.UTF_8.name()));
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        hashMap.put("sign", SignUtil.getSign(body,secretKey));
        return hashMap;
    }

    public String getNameByPostWithJson(User user) throws UnsupportedEncodingException {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse response = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                //添加请求头信息
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println("response = " + response);
        System.out.println("status = " + response.getStatus());
        if (response.isOk()) {
            return response.body();
        }
        return "fail";
    }

}



















