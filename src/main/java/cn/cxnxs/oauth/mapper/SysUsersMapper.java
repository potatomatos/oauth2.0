package cn.cxnxs.oauth.mapper;

import cn.cxnxs.oauth.entity.SysUsers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-05-01
 */
public interface SysUsersMapper extends BaseMapper<SysUsers> {

    /**
     * 获取用户所有的客户端信息
     * @param userId 用户id
     * @return clientId列表
     */
    @Select("SELECT a.client_id FROM oauth_client_details a LEFT JOIN sys_user_client b ON a.client_id=b.client_id WHERE b.user_id=#{userId};")
    List<String> getUserClients(Integer userId);

    /**
     * 获取用户所有角色信息
     * @param userId 用户id
     * @return 角色代码列表
     */
    @Select("SELECT a.role_code FROM sys_role a LEFT JOIN sys_user_role b on a.id=b.role_id where b.user_id=#{userId};")
    List<String> getUserRoles(Integer userId);

    /**
     * 获取用户权限
     * @param userId 用户id
     * @return 角色编号，客户端id，权限接口地址
     */
    @Select("SELECT c.role_code roleCode,b.client_id clientId,b.api,b.`code` FROM sys_role_permission a,sys_permission b,sys_role c ,sys_user_role d WHERE a.permission_id=b.id AND a.role_id=c.id AND c.id=d.role_id AND d.user_id=#{userId};")
    List<Map<String,String>> getUserPermissions(Integer userId);
}
