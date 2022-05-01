package cn.cxnxs.oauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-05-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRole extends Model<SysRole> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String roleName;

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    private Long createdAt;

    private Long updatedAt;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
