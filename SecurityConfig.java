
package Vibee.Config;

import Vibee.Jwt.JwtHelper;
import Vibee.Jwt.util.JwtUtil;
import Vibee.Security.JwtAuthenticationFilter;
import Vibee.Security.JwtAuthorizationFilter;
import Vibee.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import Vibee.Jwt.JwtFilter;
import Vibee.Service.UserServiceImplUserDetailService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImplUserDetailService userServiceImplUserDetailService;
    @Autowired
    private JwtFilter jwtFilter;

    private final UserDetailsService userDetailsService;

    private final TokenService tokenService;

    private final JwtHelper jwtHelper;

    private final JwtUtil jwtUtil;

    public SecurityConfig(UserDetailsService userDetailsService, TokenService tokenService, JwtHelper jwtHelper, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.jwtHelper = jwtHelper;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImplUserDetailService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable().and().cors();
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/vibee/api/v1/admins/**").hasAuthority("ADMIN")
                .antMatchers("/vibee/api/v1/adminsStaff/**").hasAnyAuthority("ADMIN", "STAFF")
                .antMatchers("/vibee/api/v1/staff/**").hasAuthority("STAFF")
                .antMatchers("/vibee/api/v1/users/**").hasAuthority("USER")
                .antMatchers("/vibee/api/v1/staff/**").permitAll()
                .antMatchers("/vibee/api/v1/**").permitAll()
                .antMatchers("/vibee/api/v1/public/**").permitAll()
                .antMatchers("/vibee/api/v1/register").permitAll()
                .anyRequest().authenticated()
                .and()

//                .addFilter(new JwtAuthenticationFilter(authenticationManager(), tokenService, jwtHelper, jwtUtil))
                //              .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailsService, tokenService, jwtHelper, jwtUtil))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}