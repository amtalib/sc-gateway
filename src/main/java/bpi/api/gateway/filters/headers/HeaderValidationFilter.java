package bpi.api.gateway.filters.headers;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import bpi.api.gateway.dto.JWTResponseFlag;
import bpi.api.gateway.dto.ServiceResponse;
import bpi.api.gateway.filters.FilterUtil;
import bpi.api.gateway.filters.logger.LoggingFilter;
import bpi.api.gateway.utils.apitoken.TokenService;
import jakarta.annotation.PostConstruct;
import net.minidev.json.JSONObject;
import reactor.core.publisher.Mono;

/**
 * @author Abdulhamid Talib
 * @since 2025-03-07
 * 
 * A custom filter that validates api-token. Requests with invalid api-token are blocked with response status HTTP 401 - UNAUTHORIZED. 
 * */
@Component
public class HeaderValidationFilter implements GlobalFilter, Ordered {

	public static String HEADER_NAME_API_TOKEN = "api_token";
	public static String HEADER_NAME_JWT_ISS = "X-JWT-ISS";
	public static String HEADER_NAME_JWT_SUB = "X-JWT-SUB";
	public static String HEADER_NAME_JWT_AUTH = "X-JWT-AUTH";
	
	// TODO Log to File and DB
	private static final Logger logger = LoggerFactory.getLogger(HeaderValidationFilter.class);
	
	@Value("${validation.api-token.enabled}")
	private Boolean isAPITokenValidationEnabled;
	
	@Value("${headers.forwarding.jwt-iss-enabled}")
	private Boolean headerForwardingJWTISSEnabled;
	
	@Value("${headers.forwarding.jwt-sub-enabled}")
	private Boolean headerForwardingJWTSUBEnabled;
	
	@Value("${headers.forwarding.jwt-authorization-enabled}")
	private Boolean headerForwardingJWTAuthorizationEnabled;
	
	@Autowired
	private TokenService tokenSrv;
	
	@PostConstruct
	public void validate() {
		if (isAPITokenValidationEnabled == null) {
			// TODO Log to file and db.
			HeaderValidationFilter.logger.error("Paramter validation.api-token.enabled was not properly set.");
			
			throw new IllegalStateException("Paramter validation.api-token.enabled was not properly set.");
		}
		
		// TODO Log to file and db.
		HeaderValidationFilter.logger.info("Parameter validation.api-token.enabled={}", isAPITokenValidationEnabled);
		
		if (headerForwardingJWTISSEnabled == null) {
			// TODO Log to file and db.
			HeaderValidationFilter.logger.error("Paramter headers.forwarding.jwt-iss-enabled was not properly set.");
			
			throw new IllegalStateException("Paramter headers.forwarding.jwt-iss-enabled was not properly set.");
		}
		
		// TODO Log to file and db.
		HeaderValidationFilter.logger.info("Parameter headers.forwarding.jwt-iss-enabled={}", headerForwardingJWTISSEnabled);
	} // End method
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String tranRefNo = FilterUtil.getHeader(exchange, LoggingFilter.HEADER_NAME_TRAN_REF_NO);
		
		// TODO Log to file and db.
		HeaderValidationFilter.logger.info("Validating api-token for request {}", tranRefNo);
		
		if (isAPITokenValidationEnabled) {
			// Extract api-token from header
			String apiToken = FilterUtil.getHeader(exchange, HeaderValidationFilter.HEADER_NAME_API_TOKEN);

			// Decode api-token
			// TODO Get rBank PubKey from Git Repo
			String rPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgqcTn9Vl/Z165A7bsmHDV6UGdt6rvDacA7zUmkKDiyj3+SnfL69/XC6CGOhvuvgE5fsQ7sVdF/94lZvUF4hjuJCevsK4bC2/hIcHcgen2e0UIVEDjVHpSSSi4rsw9FHB03hIeqeGjbFMe9kAWQgVHsmYLQL1I5rQ+fwkx1zvr5o/JTw8tSBJJ+j9OB1yKOUyZH4NxJQM6xEAkxTmM2pkJ5vQTYPBXo2L95TaCIm12ycLBkdkX2DxU6z2kWxneuR+RdgCMNKIkn+VGv5AW0Lh2WtVVL2K/+BjmUFcsdmzbFMpaK9Ncu9xHsXR7UL9BN2iEJOebIt/+j4z0p2ZfBncFQIDAQAB";
			                   
			ServiceResponse<JSONObject> parseRes = this.tokenSrv.parseEncodedJwt(apiToken, rPubKey);
			
			if (JWTResponseFlag.JWT001.name().equalsIgnoreCase(parseRes.getFlag() ) == false) {
				return handleErrorResponse(exchange
						, parseRes.toString()
						, HttpStatus.BAD_REQUEST
						, tranRefNo
					);
			}
			
			// Get JWT ISS from the api-token
			if (headerForwardingJWTISSEnabled) {				 
				// Attach JWT_ISS to the headers. 
				exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(HEADER_NAME_JWT_ISS, (parseRes.getData().getAsString("iss") != null ? parseRes.getData().getAsString("iss") : parseRes.getData().getAsString("username") ) ) );				
			}
			
			// Get JWT ISS from the api-token
			if (headerForwardingJWTSUBEnabled) {				 
				// Attach JWT_SUB to the headers. 
				exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(HEADER_NAME_JWT_SUB, parseRes.getData().getAsString("sub") ) );				
			}
			
			// Get JWT ISS from the api-token
			if (headerForwardingJWTAuthorizationEnabled) {				 
				// Attach JWT_SUB to the headers. 
				exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(HEADER_NAME_JWT_AUTH, parseRes.getData().getAsString("authorization") ) );				
			}
			
			// Validate api_token.
			ServiceResponse<Boolean> valRes = this.tokenSrv.validateAcquiredToken(parseRes.getData() );
			
			if (valRes.getData() == false) {
				return handleErrorResponse(exchange
						, valRes.toString()
						, HttpStatus.UNAUTHORIZED
						, tranRefNo
					);
			}
		} // End if
		
		// Proceed if validation passes
		return chain.filter(exchange);
	} // End method

	private Mono<Void> handleErrorResponse(ServerWebExchange exchange, String message, HttpStatus status, String tranRefNo) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);

		// TODO log to file and audit trail
		HeaderValidationFilter.logger.warn("Validation of api-token result was UNSUCCESSFUL. Cause[{}], TranRefNo[{}], HttpStatus[{}]", message, tranRefNo, status);
		
		DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8) );
		return response.writeWith(Mono.just(buffer) );
	} // End method

	@Override
	public int getOrder() {
		return -2; // Ensure it runs before forwarding requests
	} // End method

} // End class
