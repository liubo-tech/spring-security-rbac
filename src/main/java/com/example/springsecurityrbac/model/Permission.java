package com.example.springsecurityrbac.model;

import javax.annotation.Generated;

public class Permission {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String permissionName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String permissionCode;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPermissionName() {
        return permissionName;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName == null ? null : permissionName.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPermissionCode() {
        return permissionCode;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode == null ? null : permissionCode.trim();
    }
}