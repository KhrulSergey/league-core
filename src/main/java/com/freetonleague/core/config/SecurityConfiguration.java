package com.freetonleague.core.config;

import com.freetonleague.core.restclient.SessionCloudClient;
import com.freetonleague.core.security.AuthenticationCustomFilter;
import com.freetonleague.core.service.SessionService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final SessionService sessionService;
    private final SessionCloudClient sessionCloudClient;

    @Value("${spring.session.token-name}")
    private final String headerAuthTokenName = "token";

    //Initialization of request filtering component
    @Bean
    public AuthenticationCustomFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationCustomFilter authenticationTokenFilter = new AuthenticationCustomFilter(sessionService, userService, sessionCloudClient);
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .cors().disable()
                .csrf().disable();
        //Do pre filtering to all request by authenticationTokenFilterBean
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}

