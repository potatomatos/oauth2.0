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
 * 菜单表
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-05-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 菜单标题
     */
    private String menuName;

    /**
     * 路径
     */
    private String url;

    /**
     * 菜单排序
     */
    private Integer sortNo;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 权限状态(0无效1有效)
     */
    private Integer state;

    /**
     * 创建时间
     */
    private Long createdAt;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
