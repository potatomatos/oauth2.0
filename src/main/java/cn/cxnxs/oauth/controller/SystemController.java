package cn.cxnxs.oauth.controller;

import cn.cxnxs.oauth.service.ApiAuth2Service;
import cn.cxnxs.oauth.service.SystemService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * <p>系统管理</p>
 *
 * @author mengjinyuan
 * @date 2022-05-04 14:35
 **/
@Slf4j
@Controller
public class SystemController {


    @Autowired
    private SystemService systemService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private ApiAuth2Service apiAuth2Service;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    /**
     * 主页
     * @return
     */
    @RequestMapping("/index")
    public String callback(String code){
        log.info("code:{}",code);
        log.info("------开始获取token------");
        Map<String, String> accessToken = apiAuth2Service.getAccessToken(
                "authorization_code",
                "system",
                "123123",
                code,
                "http://localhost:9005/index");
        log.info("token信息：{}", JSON.toJSONString(accessToken));
        httpServletResponse.setHeader("access_token",accessToken.get("access_token"));
        return "redirect:/";
    }


    /**
     * 登录页面
     * @return
     */
    @RequestMapping("login.html")
    public String loginPage(){
        return "login";
    }

    /**
     * 验证码
     */
    @CrossOrigin(origins = "*")
    @RequestMapping("captcha")
    public void captcha() throws IOException {
        ByteArrayOutputStream bos = systemService.getCaptcha();
        httpServletResponse.setContentType("image/png");
        OutputStream os = httpServletResponse.getOutputStream();
        os.write(bos.toByteArray());
        os.flush();
        os.close();
    }

}
