package de.greenstones.gsmr.msc.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.Setter;

@Component
@Setter
public class JwtService {

	@Value("${msc-viewer.jwt.secretKey}")
	String secretKey;

	@Value("${msc-viewer.jwt.algorithm}")
	String algorithm;
	
	@Value("${msc-viewer.jwt.issuer:http://localhost}")
	String issuer;
	
	@Value("${msc-viewer.jwt.expirationInMinutes:60}")
	long expirationInMinutes;
	

	public String generateJWT(Map<String, Object> claims) {

		var header = new JWSHeader(JWSAlgorithm.parse(algorithm));
		var claimsSet = buildClaimsSet(claims);
		var jwt = new SignedJWT(header, claimsSet);

		try {
			var signer = new MACSigner(secretKey);
			jwt.sign(signer);
		} catch (JOSEException e) {
			throw new RuntimeException("Unable to generate JWT", e);
		}
		return jwt.serialize();
	}

	private JWTClaimsSet buildClaimsSet(Map<String, Object> claims) {

		var issuedAt = Instant.now();
		var expirationTime = issuedAt.plus(expirationInMinutes, ChronoUnit.MINUTES);

		var builder = new JWTClaimsSet.Builder().issuer(issuer).issueTime(Date.from(issuedAt))
				.expirationTime(Date.from(expirationTime));

		claims.forEach(builder::claim);
		return builder.build();
	}

}