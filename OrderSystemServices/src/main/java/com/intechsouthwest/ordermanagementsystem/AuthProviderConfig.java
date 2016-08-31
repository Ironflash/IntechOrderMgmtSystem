package com.intechsouthwest.ordermanagementsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Created by gregorylaflash on 8/30/16.
 */
@Configuration
@EnableWebSecurity
public class AuthProviderConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO turn on security
        http.authorizeRequests().anyRequest().permitAll();
//                .authorizeRequests()
//                    .antMatchers("**/index.html","/api/swagger.json").permitAll()
//                    .antMatchers("/api/**").authenticated()
//                .and()
//                .csrf()
//                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(kerberosAuthenticationProvider());
    }

    @Bean
    public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
        KerberosAuthenticationProvider provider =
                new KerberosAuthenticationProvider();
        SunJaasKerberosClient client = new SunJaasKerberosClient();
        client.setDebug(true);
        provider.setKerberosClient(client);
        provider.setUserDetailsService(dummyUserDetailsService());
        return provider;
    }

    @Bean
    public DummyUserDetailsService dummyUserDetailsService() {
        return new DummyUserDetailsService();
    }
//
//    @Bean
//    public SpnegoEntryPoint spnegoEntryPoint() {
//        return new SpnegoEntryPoint("/login");
//    }
//
//    @Bean
//    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
//            AuthenticationManager authenticationManager) {
//        SpnegoAuthenticationProcessingFilter filter =
//                new SpnegoAuthenticationProcessingFilter();
//        filter.setAuthenticationManager(authenticationManager);
//        return filter;
//    }
//
//    @Bean
//    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
//        SunJaasKerberosTicketValidator ticketValidator =
//                new SunJaasKerberosTicketValidator();
//        ticketValidator.setServicePrincipal("HTTP/servicehost.example.org@EXAMPLE.ORG");
//        ticketValidator.setKeyTabLocation(new FileSystemResource("/tmp/service.keytab"));
//        ticketValidator.setDebug(true);
//        return ticketValidator;
//    }
}
