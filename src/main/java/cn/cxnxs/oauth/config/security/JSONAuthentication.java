package cn.cxnxs.oauth.config.security;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>JSON处理器</p>
 *
 * @author mengjinyuan
 * @date 2022-04-14 00:16
 **/
public class JSONAuthentication {

    public void writeJSON(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,Object result) throws IOException {
        httpServletResponse.setHeader("Content-Type","application/json");
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        httpServletResponse.getWriter().write(JSONObject.toJSONString(result));
    }
}
