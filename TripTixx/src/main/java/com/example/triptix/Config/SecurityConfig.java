package com.example.triptix.Config;
;
import com.example.triptix.Util.AccessDeniedHandlerImpl;
import com.example.triptix.Util.CustomAuthenticationEntryPoint;
import com.example.triptix.Util.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true) //dùng cho phép dùng annotation autho
//        The prePostEnabled property enables Spring Security pre/post annotations.
//        The securedEnabled property determines if the @Secured annotation should be enabled.
//        The jsr250Enabled property allows us to use the @RoleAllowed annotation.
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //Authentication: xác thực
    //Authorization: phân quyền
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandlerImpl;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private Environment env;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {  //hàm này để lấy info của user để authen ấy
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        //ta đã cho class extends uerDetailService r nên security sẽ biết cách để lấy info user
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String urlSwaggerCustom = env.getProperty("url_swagger_custom");

        // --- jwt ---
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class); //check đầu tiên hết, chủ yếu là check token để xem xét autho

        // --- cors ---
        http.cors().and().csrf().disable();
        // --- security ---
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/route").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/province-city").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/province-city/detail").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/config-system").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/config-system/detail").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/bus-companys").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/bus-companys/detail").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/buses").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/buses/detail").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/stations").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/stations/detail").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.POST, "/booking/guest").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/booking/get-tick-type-of-trip").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/station-timecome").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/station-timecome/detail").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/trip-stop").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/trip-stop/detail").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/trip-stop/allStationOfTrip").permitAll();

        http.authorizeRequests().antMatchers(
                        "/usersystem/login/**", "/usersystem/register/**", "/usersystem/register-2/**" //cấp quyền cho các api có thể trực típ access
                        , "/v3/api-docs/**", "/swagger-ui/**", "/api-docs/**", urlSwaggerCustom
                        , "/error/403"

                        ,"/payment/**"

                        ,"/otp/**"

                        ,"/time-convert/**"

                        ,"/trips/search/**"
                        ,"/trips/find-seat/**"
                        ,"/trips/detail/**"

//                        ,"/route"
                        ,"/route/detail/**"
                        ,"/route/recommend-for-customer/**"
                        ,"/route/route-hot/**"

                        ,"/news"
                        ,"/news/detail/**"

                        ,"/privateBTB/**"
                        ,"/privateReservation/**"
                )               //cấp quyền để chạy test API
                .permitAll();

        // autho = annotation @RolesAllow hết r

        http.authorizeRequests().anyRequest().authenticated();

        // Đăng ký xử lý viên (handler) cho các lỗi authen, autho
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandlerImpl);  //autho
        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);  //authen
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * CORS là viết tắt của "Cross-Origin Resource Sharing" và là một cơ chế trong trình duyệt web cho phép các
     * trang web yêu cầu tài nguyên từ một nguồn gốc khác (origin) so với trang web hiện tại
     */
    //cấu hình cros cho phép gọi từ ng lạ
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configurationSource = new CorsConfiguration();
        configurationSource.setAllowedOrigins(List.of("*"));
        configurationSource.setAllowedMethods(List.of("*"));
        configurationSource.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configurationSource);
        return source;
    }


}
