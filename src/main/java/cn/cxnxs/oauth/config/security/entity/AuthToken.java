package cn.cxnxs.oauth.config.security.entity;

import lombok.Data;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-04-11 23:24
 **/
@Data
public class AuthToken {

    private String accessToken;

    private String refreshToken;

    private String jwtToken;

}
