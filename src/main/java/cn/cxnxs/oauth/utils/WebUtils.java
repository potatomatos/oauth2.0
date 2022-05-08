package cn.cxnxs.oauth.utils;

import cn.cxnxs.common.vo.response.Result;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-07 23:42
 **/
public class WebUtils {

    public static <T> void renderJSON(HttpServletResponse response,Object data) throws IOException {
        response.setHeader("Content-Type", "application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JSONObject.toJSONString(data));
    }

    public static void renderPage(HttpServletResponse response, String uri) throws IOException {
        response.setStatus(302);
        response.sendRedirect(uri);
    }
}
