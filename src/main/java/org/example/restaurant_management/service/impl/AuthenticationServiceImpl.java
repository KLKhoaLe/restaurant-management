package org.example.restaurant_management.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.restaurant_management.configuration.CacheConfig;
import org.example.restaurant_management.dto.request.IntrospectRequest;
import org.example.restaurant_management.dto.request.LoginRequest;
import org.example.restaurant_management.dto.response.IntrospectResponse;
import org.example.restaurant_management.dto.response.LoginResponse;
import org.example.restaurant_management.dto.response.RestaurantSessionResponse;
import org.example.restaurant_management.entity.InvalidatedToken;
import org.example.restaurant_management.entity.User;
import org.example.restaurant_management.entity.UserRestaurantRole;
import org.example.restaurant_management.exception.AppException;
import org.example.restaurant_management.exception.ErrorCode;
import org.example.restaurant_management.repository.InvalidatedTokenRepository;
import org.example.restaurant_management.repository.UserRepository;
import org.example.restaurant_management.repository.UserRestaurantRoleRepository;
import org.example.restaurant_management.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import com.nimbusds.jwt.SignedJWT;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @Value("${jwt.access-duration}")        // ví dụ: 604800 (7 ngày, tính bằng giây)
    long ACCESS_DURATION;

    @Value("${jwt.context-duration}")       // ví dụ: 28800 (8 tiếng, tính bằng giây)
    long CONTEXT_DURATION;

    final UserRepository userRepository;
    final UserRestaurantRoleRepository membershipRepository;
    final InvalidatedTokenRepository invalidatedTokenRepository;
    final PasswordEncoder passwordEncoder;
    final CacheManager cacheManager;

    // ------------------------------------------------------------------ //
    //  LOGIN — trả về Access Token (chỉ chứa userId, không có role/restaurant)
    // ------------------------------------------------------------------ //
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new AppException(ErrorCode.USER_INACTIVE);
        }

        boolean matched = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matched) throw new AppException(ErrorCode.WRONG_PASSWORD);

        return LoginResponse.builder()
                .accessToken(generateAccessToken(user))
                .tokenType("Bearer")
                .build();
    }


    // ------------------------------------------------------------------ //
    //  TẠO CONTEXT TOKEN — sau khi user chọn nhà hàng muốn làm việc
    // ------------------------------------------------------------------ //
    @Override
    public RestaurantSessionResponse createRestaurantSession(Long userId, Long restaurantId) {
        // Lấy membership từ cache hoặc DB
        UserRestaurantRole membership = getMembership(userId, restaurantId);

        String contextToken = generateContextToken(
                userId, restaurantId, membership.getRole().name());

        return RestaurantSessionResponse.builder()
                .contextToken(contextToken)
                .restaurantName(membership.getRestaurant().getName())
                .role(membership.getRole().name())
                .tokenType("Bearer")
                .build();
    }

    // ------------------------------------------------------------------ //
    //  LOGOUT — blacklist access token
    // ------------------------------------------------------------------ //
    @Override
    public void logout(String accessToken) throws JOSEException, ParseException {
        SignedJWT jwt = verifyToken(accessToken, "access");
        String jwtId = jwt.getJWTClaimsSet().getJWTID();
        Date expiryTime = jwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidated = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidated);
    }

    // ------------------------------------------------------------------ //
    //  INTROSPECT — kiểm tra token còn hợp lệ không (dùng cho resource server)
    // ------------------------------------------------------------------ //
    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            // Chấp nhận cả access lẫn context token
            String token = request.getToken();
            SignedJWT jwt = SignedJWT.parse(token);
            String tokenType = jwt.getJWTClaimsSet().getStringClaim("tokenType");
            verifyToken(token, tokenType);
            return IntrospectResponse.builder().valid(true).build();
        } catch (Exception e) {
            return IntrospectResponse.builder().valid(false).build();
        }
    }

    // ================================================================== //
    //  TOKEN GENERATION
    // ================================================================== //

    @Override
    public String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("restaurant.com")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(ACCESS_DURATION, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("tokenType", "access")
                .claim("userId", user.getId())
                // KHÔNG có restaurantId, KHÔNG có role
                .build();

        return sign(header, claims);
    }

    @Override
    public String generateContextToken(Long userId, Long restaurantId, String role) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(userId))
                .issuer("restaurant.com")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(CONTEXT_DURATION, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("tokenType", "context")
                .claim("userId", userId)
                .claim("restaurantId", restaurantId)
                .claim("role", role)
                // Context token có thời gian ngắn hơn → không cần refresh
                // Nếu hết hạn, client gọi lại POST /restaurants/{id}/session
                .build();

        return sign(header, claims);
    }

    // ================================================================== //
    //  TOKEN VERIFICATION
    // ================================================================== //

    @Override
    public SignedJWT verifyToken(String token, String expectedType)
            throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        // 1. Kiểm tra chữ ký
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 2. Kiểm tra hết hạn
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiryTime == null || expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 3. Kiểm tra đúng loại token không
        String actualType = signedJWT.getJWTClaimsSet().getStringClaim("tokenType");
        if (!expectedType.equals(actualType)) {
            throw new AppException(ErrorCode.INVALID_TOKEN_TYPE);  // thêm error code này
        }

        // 4. Kiểm tra blacklist (chỉ cần check access token vì context token ngắn hạn)
        if ("access".equals(expectedType) &&
                invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    // ================================================================== //
    //  PRIVATE HELPERS
    // ================================================================== //

    private String sign(JWSHeader header, JWTClaimsSet claims) {
        Payload payload = new Payload(claims.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot sign JWT", e);
        }
    }

    @Cacheable(value = CacheConfig.MEMBERSHIP_CACHE, key = "#userId + '_' + #restaurantId")
    public UserRestaurantRole getMembership(Long userId, Long restaurantId) {
        return membershipRepository
                .findByUser_IdAndRestaurant_IdAndStatus(
                        userId, restaurantId, UserRestaurantRole.MemberStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_A_MEMBER));
    }

    // Gọi khi admin kick nhân viên — xóa cache ngay lập tức
    @CacheEvict(value = CacheConfig.MEMBERSHIP_CACHE, key = "#userId + '_' + #restaurantId")
    public void evictMembershipCache(Long userId, Long restaurantId) { }
}
