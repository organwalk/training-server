package com.training.progress.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class JSONUtils {
    public JSONObject getJSONObj(byte[] messageBody){
        String jsonString = new String(messageBody);
        return JSONObject.parseObject(jsonString);
    }

    public byte[] getJSONObjectByte(JSONObject jsonObject){
        String jsonString = jsonObject.toJSONString();
        return jsonString.getBytes();
    }
}
