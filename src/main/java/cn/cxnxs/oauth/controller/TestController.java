package cn.cxnxs.oauth.controller;

import cn.cxnxs.common.vo.response.Result;
import cn.cxnxs.oauth.config.security.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-03 23:17
 **/
@RestController
@RequestMapping("user")
public class TestController {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @GetMapping("getByName")
    public Result<UserDetails> getByName() {
        return Result.success(userDetailService.loadUserByUsername("mengjinyuan"));
    }

    /**
     * 获取授权的用户信息
     *
     * @param principal 当前用户
     * @return 授权信息
     */
    @GetMapping("current/get")
    public Principal user(Principal principal) {
        return principal;
    }
}
