package de.greenstones.gsmr.msc.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${msc-viewer.jwt.secretKey}")
	String secretKey;

	@Value("${msc-viewer.jwt.algorithm}")
	String algorithm;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests((authorize) -> {
			authorize //
					.requestMatchers("/api/token").permitAll() //
					.requestMatchers("/api/test").permitAll() //
					.anyRequest().authenticated();
			// .anyRequest().permitAll();
		}).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {

		SecretKey originalKey = new SecretKeySpec(secretKey.getBytes(), algorithm);
		return NimbusJwtDecoder.withSecretKey(originalKey).build();
	}

}