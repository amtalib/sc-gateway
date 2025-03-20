package bpi.api.gateway.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Abdulhamid Talib
 * @since 2025-03-07
 * 
 * A fallback Endpoint called by circuit-breaker.
 * A fallback endpoint is returned when circuit-breaker is activated due to error returned from the destination service. 
 * */
@RestController
public class FallbackController {

	// TODO Log to File and DB
	private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);
	
	@GetMapping("/fallback503")
	public ResponseEntity<String> fallback(@RequestHeader("X-TranRef-No") String tranRefNo) {
		// TODO Log to File and DB
		FallbackController.logger.info("TraRefNo [{}]. Service is temporarily unavailable, please try again later.", tranRefNo);
		
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Service is temporarily unavailable. Please try again later. TranRefNo [" + tranRefNo + "]");
	} // End method

}
