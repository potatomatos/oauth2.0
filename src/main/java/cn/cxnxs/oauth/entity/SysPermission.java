package cn.cxnxs.oauth.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-05-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission extends Model<SysPermission> {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口地址
     */
    private String api;

    /**
     * 编码
     */
    private String code;

    /**
     * 状态：0启用，1禁用
     */
    private Integer state;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Long createdAt;

    /**
     * 更新时间
     */
    private Long updateAt;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
