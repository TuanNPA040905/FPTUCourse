package vn.tuannpa.coursefpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import jakarta.servlet.DispatcherType;
import vn.tuannpa.coursefpt.service.CustomerUserDetailsService;
import vn.tuannpa.coursefpt.service.UserService;

@Configuration
@EnableMethodSecurity(securedEnabled = true) // Bật phân quyền ở method level (vd: @Secured("ROLE_ADMIN"))
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Load thông tin user từ database khi đăng nhập
    /*Flow:
        1. User nhập email/password    
        2. Spring security gọi CustomerUserDetailsService
        3. Service này load user từ DB qua UserService
        4. Trả về thông tin user để Spring Security kiểm tra so sánh password
    */
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new CustomerUserDetailsService(userService);
    }


    @Bean
    public DaoAuthenticationProvider authProvider(
        PasswordEncoder passwordEncoder, 
        UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); //cách load user
        authProvider.setPasswordEncoder(passwordEncoder);      //cách mã hóa password
        return authProvider;
    }


    //Xử lý sau khi đăng nhập thành công (redirect theo role)
    @Bean
    public AuthenticationSuccessHandler customerSuccessHandler() {
        return new CustomerSuccessHandler();
    }


    //Tự động đăng nhập lại khi đóng/mở trình duyệt (remember me)
    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);  // Luôn nhớ đăng nhập
        return rememberMeServices;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD,
                    DispatcherType.INCLUDE
                ).permitAll()
                .requestMatchers("/", "login", "/courses/", "/courses/**", "/register", "/css/**", "/js/**", "/images/**")
                .permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
            ).sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) //luôn tạo session
                        .invalidSessionUrl("/logout?expired")       //Redirect khi session hết hạn
                        .maximumSessions(1)                         //Chỉ cho phép 1 session/user
                        .maxSessionsPreventsLogin(false))           //Session mới đẩy session cũ ra ngoài
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")      // Xóa cookie session
                .invalidateHttpSession(true))     // Hủy session
            .rememberMe(r -> r.rememberMeServices(rememberMeServices()))    // Sử dụng remember me service đã config ở trên
            .formLogin(formLogin -> formLogin
                .loginPage("/login")                            // Trang login custom   
                .failureUrl("/login?error")         //Redirect kh8i nhập sai
                .successHandler(customerSuccessHandler())               // Xử lý sau khi đăng nhập thành công
                .permitAll())                      // Cho phép tất cả truy cập trang login              
            .exceptionHandling(ex -> ex.accessDeniedPage("/access-deny"));
        return http.build();
    }
}
