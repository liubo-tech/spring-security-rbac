package com.example.springsecurityrbac.dao;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class PermissionDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final Permission permission = new Permission();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> id = permission.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> permissionName = permission.permissionName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> permissionCode = permission.permissionCode;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class Permission extends SqlTable {
        public final SqlColumn<Integer> id = column("id", JDBCType.INTEGER);

        public final SqlColumn<String> permissionName = column("permission_name", JDBCType.VARCHAR);

        public final SqlColumn<String> permissionCode = column("permission_code", JDBCType.VARCHAR);

        public Permission() {
            super("permission");
        }
    }
}