package app.Quiz.jwzpQuizappProject.service;

import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final UserRepository userRepository;

    public TokenService(JwtEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication, long timeAmount, String timeUnit) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(timeAmount, ChronoUnit.valueOf(timeUnit)))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String getEmailFromToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        Pattern pattern = Pattern.compile("\"sub\":\"(.*@.*?)\"");
        var matcher = pattern.matcher(payload);
        matcher.find();
        return matcher.group(1);
//        xD
    }

    public UserModel getUserFromToken(String token) {
        String email = getEmailFromToken(token);
        var user = userRepository.findByEmail(email);
        return user.get();  // we are sure that user is present, because of JWT (or someone forged JWT so well :^])
    }
}
