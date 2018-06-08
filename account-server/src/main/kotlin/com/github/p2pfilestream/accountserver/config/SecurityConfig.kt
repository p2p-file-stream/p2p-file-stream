package com.github.p2pfilestream.accountserver.config

import com.auth0.spring.security.api.JwtWebSecurityConfigurer
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

/**
 * Configure Auth0 authentication
 * https://auth0.com/blog/developing-restful-apis-with-kotlin/
 */
@Configuration
@EnableWebSecurity
class WebSecurity : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/h2-console/*").permitAll()
            .anyRequest().authenticated()
            .and()
            .headers().frameOptions().disable()

        JwtWebSecurityConfigurer
            .forRS256(AUDIENCE, ISSUER)
            .configure(http)
    }
}