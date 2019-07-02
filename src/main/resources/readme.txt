
spring security的简单原理：
使用众多的拦截器对url拦截，以此来管理权限。但是这么多拦截器，笔者不可能对其一一来讲，主要讲里面核心流程的两个。

首先，权限管理离不开登陆验证的，所以登陆验证拦截器AuthenticationProcessingFilter要讲； 
还有就是对访问的资源管理吧，所以资源管理拦截器AbstractSecurityInterceptor要讲；

但拦截器里面的实现需要一些组件来实现，所以就有了AuthenticationManager、accessDecisionManager等组件来支撑。

现在先大概过一遍整个流程，用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现，而且AuthenticationManager会调用ProviderManager来获取用户验证信息（不同的Provider调用的服务不同，因为这些信息可以是在数据库上，可以是在LDAP服务器上，可以是xml配置文件上等），如果验证通过后会将用户的权限信息封装一个User放到spring的全局缓存SecurityContextHolder中，以备后面访问资源时使用。 
访问资源（即授权管理），访问url时，会通过AbstractSecurityInterceptor拦截器拦截，其中会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，在调用授权管理器AccessDecisionManager，这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，还会获取被拦截的url和被拦截url所需的全部权限，然后根据所配的策略（有：一票决定，一票否定，少数服从多数等），如果权限足够，则返回，权限不够则报错并调用权限不足页面。



一，用户认证

  我们自定义一个实现类 实现UserDetailsService接口
   1.其中需要实现一个loadUserByUsername方法，用来读取用户的角色，即把数据库对应的角色存入，该方法返回UserDetail接口
     该接口中的一个方法getAuthorities()存放所有角色信息，我们一般自定义的用户类User实现UserDetail方法，用来存储用户信息与角色信息
   2.在这里我们需要从数据库中通过用户名来查询用户信息和用户所属的角色列表，其中自定义的Role类实现了GrantedAuthority接口，实现GrantedAuthority接口中的唯一方法，
    所有把查询出来的角色 赋值到自定义User类中的 setAuthorities()方法中
     
二，读取资源与所属角色
  1.需要自定义实现类实现FilterInvocationSecurityMetadataSource接口。
  通过loadResourceDefine方法可以实现资源与权限的对应关系。
  2.要使我们自定义的MFilterInvocationSecurityMetadataSource生效，我们还需要定义一个MyFilterSecurityInterceptor类。
这里的数据需要从数据库中取得。另外自定义接口UrlMatcher，实现类为AntUrlPathMatcher。

三，决策管理器

自定义一个决策管理器MyAccessDecisionManager实现AccessDecisionManager接口。其中的decide方法，决定某一个用户是否有权限访问某个url