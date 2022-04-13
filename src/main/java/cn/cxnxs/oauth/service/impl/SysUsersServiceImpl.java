package cn.cxnxs.oauth.service.impl;

import cn.cxnxs.oauth.entity.SysUsers;
import cn.cxnxs.oauth.mapper.SysUsersMapper;
import cn.cxnxs.oauth.service.ISysUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author mengjinyuan
 * @since 2022-04-13
 */
@Service
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUsers> implements ISysUsersService {

}
