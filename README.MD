## Spring Security实现RBAC权限管理

### 一简介

在企业应用中，认证和授权是非常重要的一部分内容，业界最出名的两个框架就是大名鼎鼎的
Shiro和Spring Security。由于Spring Boot非常的流行，选择Spring Security做认证和授权的
人越来越多，今天我们就来看看用Spring 和 Spring Security如何实现基于RBAC的权限管理。

### 二、基础概念RBAC

RBAC是Role Based Access Control的缩写，是基于角色的访问控制。一般都是分为用户（user），
角色（role），权限（permission）三个实体，角色（role）和权限（permission）是多对多的
关系，用户（user）和角色（role）也是多对多的关系。用户（user）和权限（permission）
之间没有直接的关系，都是通过角色作为代理，才能获取到用户（user）拥有的权限。一般情况下，
使用5张表就够了，3个实体表，2个关系表。具体的sql清参照项目示例。

### 三、集群部署

为了确保应用的高可用，一般都会将应用集群部署。但是，Spring Security的会话机制是基于session的，
做集群时对会话会产生影响。我们在这里使用Spring Session做分布式Session的管理。

### 四、技术选型

我们使用的技术框架如下：
+ Spring Boot
+ Spring Security
+ Spring Data Redis
+ Spring Session
+ Mybatis-3.4.6
+ Druid
+ Thymeleaf（第一次使用）

### 五、具体实现

首先，我们需要完成整个框架的整合，使用Spring Boot非常的方便，配置application.properties文件即可，
配置如下：
```properties
#数据源配置
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
spring.datasource.url=jdbc:mysql://localhost:3306/security_rbac?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai

#mybatis配置
#mybatis.mapper-locations=mybatis/*.xml
#mybatis.type-aliases-package=com.example.springsecurityrbac.model

#redis配置
#spring.redis.cluster.nodes=149.28.37.147:7000,149.28.37.147:7001,149.28.37.147:7002,149.28.37.147:7003,149.28.37.147:7004,149.28.37.147:7005
spring.redis.host=你的redis地址
spring.redis.password=你的redis密码

#spring-session配置
spring.session.store-type=redis
#thymeleaf配置
spring.thymeleaf.cache=false
```

然后，使用Mybatis Generator生成对应的实体和DAO，这里不赘述。

前面的这些都是准备工作，下面就要配置和使用Spring Security了，首先配置登录的页面和
密码的规则，以及授权使用的技术实现等。我们创建`MyWebSecurityConfig`继承`WebSecurityConfigurerAdapter`
，并复写`configure`方法，具体代码如下：
```java
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .and()
                .formLogin()
                .loginPage("/login").failureForwardUrl("/login-error")
//                .successForwardUrl("/index")
                .permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}
```
我们继承`WebSecurityConfigurerAdapter`，并在类上标明注解`@EnableWebSecurity`，然后复写`configure`方法，
由于我们的授权是采用注解方式的，所以这里只写了`authorizeRequests()`，并没有具体的授权信息。
接下来我们配置登录url和登录失败的url，并没有配置登录成功的url，因为如果指定了登录成功的url，
每次登录成功后都会跳转到这个url上。但是，我们大部分的业务场景都是登录成功后，跳转到登录页之前的
那个页面，登录页之前的这个页面是不定的。具体例子如下：
    
+ 你在未登录的情况下访问了购物车页，购物车页需要登录，跳转到了登录页，登录成功后你会返回购物车页。
+ 你又在未登录的情况下访问了订单详情页，订单详情页需要登录，跳转到了登录页，登录后你会跳转到订单详情页。

所以，这里不需要指定登录成功的url。

再来说说`PasswordEncoder`这个Bean，Spring Security扫描到`PasswordEncoder`这个Bean，
就会把它作为密码的加密规则，这个我们使用`NoOpPasswordEncoder`，没有密码加密规则，数据库中
存的是密码明文。如果需要其他加密规则可以参考`PasswordEncoder`的实现类，也可以自己实现
`PasswordEncoder`接口，完成自己的加密规则。

最后我们再类上标明注解`@EnableGlobalMethodSecurity(prePostEnabled = true)`，这样我们再
方法调用前会进行权限的验证。

Spring Security提供的认证方式有很多种，比如：内存方式、LDAP方式。但是这些都和我们方式不符，
我们希望使用自己的用户（User）来做认证，Spring Security也提供了这样的接口，方便了我们的开发。
首先，需要实现Spring Security的`UserDetails`接口，代码如下：
```java
public class User implements UserDetails {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Integer id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String username;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private String password;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    private Boolean locked;

    @Getter@Setter
    private Set<SimpleGrantedAuthority> permissions;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Integer getId() {
        return id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setId(Integer id) {
        this.id = id;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions;
    }

    public void setAuthorities(Set<SimpleGrantedAuthority> permissions){
        this.permissions = permissions;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public String getPassword() {
        return password;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public Boolean getLocked() {
        return locked;
    }

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
```
其中所有的`@Override`方法都是需要你自己实现的，其中有一个方法大家需要注意一下，那就是
`getAuthorities()`方法，它返回的是用户具体的权限，在权限判定时，需要调用这个方法。
所以我们再User类中定义了一个权限集合的变量
```java
    @Getter@Setter
    private Set<SimpleGrantedAuthority> permissions;
```
其中`SimpleGrantedAuthority`是Spring Security提供的一个简单的权限实体，它的构造函数只有一个
权限编码的字符串，大多数情况下，我们这个权限类就够用了。

然后，我们实现Spring Security的`UserDetailsService1`接口，完成用户以及用户权限的查询，
代码如下：
```java
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
```
这样，用户在登录时就会调用这个方法，完成用户以及用户权限的查询。

到此，用户认证过程就结束了，登录成功后，会跳到首页或者登录页的前一页（因为没有配置登录成功的url），
登录失败会跳到登录失败的url。

我们再看看权限判定的过程，我们在`MyWebSecurityConfig`类上标明了注解`@EnableGlobalMethodSecurity(prePostEnabled = true)`，这使得我们
可以在方法上使用注解进行权限判定。我们在用户登录过程中查询了用户的权限，系统知道了用户的权限，就可以进行权限的判定了。

我们看看方法上的权限注解，如下：
```java
    @PreAuthorize("hasAuthority(T(com.example.springsecurityrbac.config.PermissionContact).USER_VIEW)")
    @RequestMapping("/user/index")
    public String userIndex() {
        return "user/index";
    }
```
这是我们在Controller中的一段代码，使用注解`@PreAuthorize("hasAuthority(xxx)")`，其中我们使用
`hasAuthority(xxx)`指明具体的权限，其中xxx可以使用SPel表达式。如果不想指明具体的权限，仅仅使用
登录、任何人等权限的，可以如下：
+ isAnonymous() 
+ isAuthenticated()
+ isRememberMe()

还有其他的一些方法，请Spring Security官方文档。

如果用户不满足指定的权限，会返回403错误信息。

由于前段我们使用的是Thymeleaf，它对Spring Security的支持非常好，我们在pom.xml中添加如下配置：
```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity4</artifactId>
    <version>3.0.2.RELEASE</version>
</dependency>
```
并在页面中添加如下引用：
```html
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
      ........
</html>
```
th是Thymeleaf的基本标签，sec是Thymeleaf对Spring Security的扩展标签，在页面中我们进行权限的判定如下：
```html
<div class="logout" sec:authorize="isAuthenticated()">
    ............
</div>
```
只有用户在登录的情况下，才可以显示这个div下的内容。

到此，Spring Security就给大家介绍完了，具体的项目代码参照我的GitHub地址：
[https://github.com/liubo-tech/spring-security-rbac](https://github.com/liubo-tech/spring-security-rbac)
