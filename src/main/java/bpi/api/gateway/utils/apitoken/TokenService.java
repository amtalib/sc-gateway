package bpi.api.gateway.utils.apitoken;

import bpi.api.gateway.dto.ServiceResponse;
import net.minidev.json.JSONObject;

/***
 * @author Abdulhamid M. Talib
 * @since 07-04-2014
 * @version 1.0
 */
public interface TokenService {

	public ServiceResponse<JSONObject> parseEncryptedJwt(String reqJwtm, String privatekey);
	
	public ServiceResponse<JSONObject> parseEncodedJwt(String reqJwt, String key);
	
	public ServiceResponse<Boolean> validateAcquiredToken(JSONObject jwt);
	
	// ------------------------------------------//
	public void populateMapKey(String rsaKeysFilePath) throws Exception;
	
	public ServiceResponse<RSAKey> getKey(String id);
}
