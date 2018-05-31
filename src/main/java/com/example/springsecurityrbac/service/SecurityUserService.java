package com.example.springsecurityrbac.service;

import com.example.springsecurityrbac.dao.*;
import com.example.springsecurityrbac.model.Permission;
import com.example.springsecurityrbac.model.User;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.equalTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;


@Service
public class SecurityUserService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SelectStatementProvider selectStatement = select(UserDynamicSqlSupport.id,UserDynamicSqlSupport.username,UserDynamicSqlSupport.password,UserDynamicSqlSupport.locked)
                .from(UserDynamicSqlSupport.user)
                .where(UserDynamicSqlSupport.username,isEqualTo(username))
                .build().render(RenderingStrategy.MYBATIS3);


        Map<String,Object> parameter = new HashMap<>();
        parameter.put("#{username}",username);
        User user = userMapper.selectOne(selectStatement);
        if (user == null) throw new UsernameNotFoundException(username);

        SelectStatementProvider manyPermission = select(PermissionDynamicSqlSupport.id,PermissionDynamicSqlSupport.permissionCode,PermissionDynamicSqlSupport.permissionName)
                .from(PermissionDynamicSqlSupport.permission)
                .join(RolePermissionDynamicSqlSupport.rolePermission).on(RolePermissionDynamicSqlSupport.permissionId,equalTo(PermissionDynamicSqlSupport.id))
                .join(UserRoleDynamicSqlSupport.userRole).on(UserRoleDynamicSqlSupport.roleId,equalTo(RolePermissionDynamicSqlSupport.roleId))
                .where(UserRoleDynamicSqlSupport.userId,isEqualTo(user.getId()))
                .build()
                .render(RenderingStrategy.MYBATIS3);
        List<Permission> permissions = permissionMapper.selectMany(manyPermission);
        if (!CollectionUtils.isEmpty(permissions)){
            Set<SimpleGrantedAuthority> sga = new HashSet<>();
            permissions.forEach(p->{
                sga.add(new SimpleGrantedAuthority(p.getPermissionCode()));
            });
            user.setAuthorities(sga);
        }



        return user;
    }
}
