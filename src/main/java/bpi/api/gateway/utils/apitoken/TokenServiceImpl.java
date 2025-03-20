package bpi.api.gateway.utils.apitoken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;

import bpi.api.gateway.dto.JWTResponseFlag;
import bpi.api.gateway.dto.ResponseStatus;
import bpi.api.gateway.dto.ServiceResponse;
import bpi.api.gateway.utils.DateUtility;

/***
 * @author Abdulhamid M. Talib
 * @since 07-04-2014
 * @version 1.0
 * */
@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private static final Map<String, RSAKey> RSA_KEY_MAP = new HashMap<String, RSAKey>();
	
	@Override
	public ServiceResponse<JSONObject> parseEncryptedJwt(String reqJwt, String privatekey) {
		ServiceResponse<JSONObject> response = new ServiceResponse<JSONObject>();
		
		try {
			if (reqJwt == null || reqJwt.trim().isEmpty() ) {
				response.setStatus(JWTResponseFlag.JWT001, reqJwt);
				return response;
			}
			
			Map<String, Object> map = JWTTools.decrypt(reqJwt, privatekey);
			
			response.setData(new JSONObject(map) );
			response.setStatus(JWTResponseFlag.JWT001);
			
		} catch (ParseException e) {
			response.setStatusToException(JWTResponseFlag.JWT996, e.getMessage(), reqJwt);
		} catch (IllegalArgumentException e) {
			response.setStatusToException(JWTResponseFlag.JWT996, e.getMessage(), reqJwt);
		} catch (JOSEException e) {
			response.setStatusToException(JWTResponseFlag.JWT100, e.getMessage(), reqJwt);
		} catch (Exception e) {
			response.setStatusToException(JWTResponseFlag.JWT999, e.getMessage(), reqJwt);
		}
		
		return response;
	} // End method

	@Override
	public ServiceResponse<JSONObject> parseEncodedJwt(String reqJwt, String key) {
		ServiceResponse<JSONObject> response = new ServiceResponse<JSONObject>();
		
		try {
			if (reqJwt == null || reqJwt.trim().isEmpty() ) {
				response.setStatus(JWTResponseFlag.JWT996, reqJwt);
				return response;
			}
			
			Map<String, Object> map = JWTTools.verifyAndDecode(reqJwt, key);
			
			response.setData(new JSONObject(map) );
			response.setStatus(JWTResponseFlag.JWT001);
		} catch (ParseException e) {
			response.setStatusToException(JWTResponseFlag.JWT996, e.getMessage(), reqJwt);
		} catch (IllegalArgumentException e) {
			response.setStatusToException(JWTResponseFlag.JWT996, e.getMessage(), reqJwt);
		} catch (JOSEException e) {
			response.setStatusToException(JWTResponseFlag.JWT100, e.getMessage(), reqJwt);
		} catch (Exception e) {
			response.setStatusToException(JWTResponseFlag.JWT999, e.getMessage(), reqJwt);
		}
		
		return response;
	} // End method

	@Override
	public ServiceResponse<Boolean> validateAcquiredToken(JSONObject jwt) {
		ServiceResponse<Boolean> response = new ServiceResponse<Boolean>();
		
		try {
			if (jwt == null) {
				response.setStatus(JWTResponseFlag.JWT100);
				response.setData(Boolean.FALSE);
				return response;
			}
			
			// Validate username
			if (jwt.get("username") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No username claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			// Validate password
			if (jwt.get("authorization") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No authorization claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			// Validate subject
			if (jwt.get("sub") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No sub claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			// Validate aud claim
			if (jwt.get("aud") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No aud claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			// Validate jti
			if (jwt.get("jti") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No jti claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			if (jwt.getAsString("jti").length() < 30) {
				response.setStatusToException(JWTResponseFlag.JWT100, "Invalid JWT ID.", jwt.getAsString("jti") );
				response.setData(Boolean.FALSE);
				return response;
			}
			
			// Validate iat 
			if (jwt.get("iat") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No iat  claim.");
				response.setData(Boolean.FALSE);
				return response;
			}
			
			if (jwt.get("exp") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No exp claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			// Validate expiration date.
			Date currDate = new Date();
			
			try {
				Date exp = (Date) jwt.get("exp");
				
				if (currDate.equals(exp) || currDate.after(exp) ) {
					response.setStatusToException(JWTResponseFlag.JWT100, "Expired api_token.", DateUtility.extractDateDetails("yyyy/MM/dd hh:mm:ss", exp) );
					response.setData(Boolean.FALSE);
					return response;	
				} 
			} catch (Exception e) {
				response.setStatusToException(JWTResponseFlag.JWT100, "Expired api_token.", (String) jwt.get("exp") );
			}
			
			// Validate userEnc
			if (jwt.get("userEnc") == null) {
				response.setStatus(JWTResponseFlag.JWT100, "No userEnc claim.");
				response.setData(Boolean.FALSE);
				return response;	
			}
			
			response.setData(true);
			response.setStatus(JWTResponseFlag.JWT001);
		}
		catch (Exception e) {
			response.setData(Boolean.FALSE);
			response.setStatus(JWTResponseFlag.JWT999, AMTLogger.getErrMsg(e) );
		}
		
		return response;
	} // End method
	
	@Override
	public ServiceResponse<RSAKey> getKey(String id) {
		ServiceResponse<RSAKey> response = new ServiceResponse<RSAKey>();
		
		if (RSA_KEY_MAP.get(id) != null) {
			response.setData(RSA_KEY_MAP.get(id) );
			response.setStatus(ResponseStatus.SUCCESS);
		}
		else {
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage("@ID[" + id + "] not defined in RSA-Keys.txt");
		}
		
		return response;
	} // End method
	
	@Override
	public void populateMapKey(String rsaKeysFilePath) throws Exception {
		File f = new File(rsaKeysFilePath);
		
		if (f.isFile() == false) {
			throw new FileNotFoundException("RSA Keys File doesn't exists. @Path[" + rsaKeysFilePath + "]");
		}
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(f) );
			String str = null;
			
			while ( (str = br.readLine() ) != null) {
				if (str.trim().isEmpty() ) {
					continue;
				}
				
				if (str.startsWith("--") ) {
					// Skip comment.
					continue;
				}
				
				String strArr [] = str.split("#");
				
				if (strArr.length > 0) {
					RSAKey r = new RSAKey();
					
					r.setId(strArr[0].trim() );
					
					if (strArr.length > 1) {
						r.setPublicKey(strArr[1].trim() );
					}
					else {
						continue;	
					}
					
					if (strArr.length > 2) {
						r.setPrivateKey(strArr[2].trim() );
					}
					
					RSA_KEY_MAP.put(r.getId(), r);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	} // End method
	
}
