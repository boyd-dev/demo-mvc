package com.foo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
	
			
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(new String[]{"/login/**", "/signup", "/resources/**"}).permitAll()	
				.anyRequest().authenticated())
		    .httpBasic(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
			.logout(logout -> logout.logoutUrl("/signout").deleteCookies("JSESSIONID"))
			.exceptionHandling(t -> t.accessDeniedPage("/resources/error.html"))
			.csrf(c -> c.disable()); 
		    		
		return http.build();
	}	
		
	
	@Bean
	public UserDetailsService userDetailsService() {
		
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();		
		
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		UserDetails userDetails = User.withUsername("scott")
				                      .passwordEncoder((p)-> encoder.encode(p))
		                              //.password(encoder.encode("5555"))
				                      .password("5555")
		                              .roles("USER")
		                              .build();
		
		manager.createUser(userDetails);		
		return manager;
	}
	
}
