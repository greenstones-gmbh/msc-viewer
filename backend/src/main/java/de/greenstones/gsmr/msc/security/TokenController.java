package de.greenstones.gsmr.msc.security;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.greenstones.gsmr.msc.MscViewerProperties;
import de.greenstones.gsmr.msc.MscViewerProperties.User;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@Slf4j
public class TokenController {

	@Autowired
	JwtService jwtService;

	@Autowired
	MscViewerProperties props;

	@PostMapping("/api/token")
	public ResponseEntity<?> createToken(@RequestBody TokenRequest request) {
		log.info("create token {}", request);

		try {

			User user = props.getUsers().stream().filter(u -> u.getUser().equals(request.getUsername())).findAny()
					.orElse(null);

			if (user == null) {
				throw new RuntimeException("User " + request.getUsername() + " not found");
			}

			if (!user.getPassword().equals(request.getPassword())) {
				throw new RuntimeException("You have entered an invalid username or password");
			}

			Map<String, Object> claims = Collections.singletonMap("user", request.username);
			TokenResponse tokenResponse = TokenResponse.builder().token(jwtService.generateJWT(claims)).build();

			return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, tokenResponse.getToken()).body(tokenResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}

	}

	@GetMapping("/api/validate")
	public TokenResponse testToken(@AuthenticationPrincipal Jwt o) {
		return TokenResponse.builder().token(o.getTokenValue()).build();
	}

	@GetMapping("/api/test")
	public String test() {
		return "ok";
	}

	@Data
	public static class TokenRequest {
		String username;
		String password;
	}

	@Data
	@Builder
	public static class TokenResponse {
		String token;
	}

}
