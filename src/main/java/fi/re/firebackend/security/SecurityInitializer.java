package fi.re.firebackend.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;


public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
}
/*
SecurityInitializer 는 다음과 같은 일을 한다
어플리케이션의 모든 URL에 자동으로 springSecurityFilterChain 필터를 등록한다.
WebSecurityConfig를 로드하는 ContextLoaderListener를 추가한다

https://godekdls.github.io/Spring%20Security/javaconfiguration/
 */