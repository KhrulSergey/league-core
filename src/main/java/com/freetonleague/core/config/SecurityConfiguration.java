package com.freetonleague.core.config;

import com.freetonleague.core.security.AuthenticationCustomFilter;
import com.freetonleague.core.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(1)
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SessionService sessionService;

    @Value("${freetonleague.session.header-token-name:X-Auth-Token}")
    private final String headerTokenName;

    @Value("${freetonleague.session.service-token-name:service_token}")
    private final String serviceTokenName;

    //Initialization of request filtering component
    @Bean
    public AuthenticationCustomFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationCustomFilter authenticationTokenFilter = new AuthenticationCustomFilter(sessionService,
                headerTokenName, serviceTokenName);
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .csrf().disable();
        //Do pre filtering to all request by authenticationTokenFilterBean
        http
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
}

