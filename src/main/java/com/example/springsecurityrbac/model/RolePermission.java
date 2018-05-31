package com.example.springsecurityrbac.model;

import javax.annotation.Generated;

public class RolePermission {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer roleId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer permissionId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getRoleId() {
        return roleId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getPermissionId() {
        return permissionId;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
}