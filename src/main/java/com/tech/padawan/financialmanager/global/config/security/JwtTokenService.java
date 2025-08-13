package com.tech.padawan.financialmanager.global.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tech.padawan.financialmanager.user.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import io.github.cdimascio.dotenv.Dotenv;


@Service
public class JwtTokenService {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String SECRET = dotenv.get("SECRET_KEY");
    private static final String ISSUER = dotenv.get("JWT_ISSUER");

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate())
                    .withSubject(user.getEmail())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error generating token.", exception);
        }
    }

    public String getSubjectFromToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new JWTVerificationException("Expired or Invalid token");
        }
    }

    private Instant creationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
    }

    private Instant expirationDate(){
        return ZonedDateTime.now(ZoneId.of("America/Recife")).plusHours(4).toInstant();
    }
}
