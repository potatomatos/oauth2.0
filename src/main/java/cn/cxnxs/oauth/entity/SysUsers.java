package cn.cxnxs.oauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-05-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUsers extends Model<SysUsers> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    /**
     * 头像
     */
    private String avatar;

    @TableField("phoneNumber")
    private String phoneNumber;

    private String email;

    private String encryptedPassword;

    private String resetPasswordToken;

    private LocalDateTime resetPasswordSentTime;

    private Integer loginCount;

    private LocalDateTime currentLoginTime;

    private LocalDateTime lastLoginTime;

    private String currentLoginIp;

    private String lastLoginIp;

    private Boolean admin;

    private Integer failedAttempts;

    private String unlockToken;

    private LocalDateTime lockedTime;

    private String confirmationToken;

    private LocalDateTime confirmedTime;

    private LocalDateTime confirmationSentTime;

    private String unconfirmedEmail;

    /**
     * 0-启用 1-停用 2-账号过期 3-认证过期 4-账号锁定
     */
    private Boolean state;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
