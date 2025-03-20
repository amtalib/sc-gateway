package bpi.api.gateway.utils.apitoken;

/**
 * Libraries: jcip-annotations-1.0-1.jar, nimbus-jose-jwt-5.0.jar, json-smart-2.3.jar, bcprov-jdk15on-1.59.jar, accessors-smart-1.2.jar
 * */
import java.util.*;

import javax.crypto.SecretKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.*;
import java.sql.Timestamp;
import java.text.ParseException;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jwt.*;

/**
 * @author Abdulhamid M. Talib
 * @since July 6, 2018
 * */
public class JWTTools {
	
	public static void mains(String[] args) throws Exception {
		
		Timestamp ts=new Timestamp(1542180100319l);  
        Date date=new Date(ts.getTime());  
        System.out.println("> " + date);     
        
    
        Timestamp tss=new Timestamp(System.currentTimeMillis());
        System.out.println(":" + tss.getTime());
        Date dates=new Date(tss.getTime());  
        System.out.println(dates);     
        
		
		 // ---------- ENCRYPT --------------
		KeyPair keyPair = AMTCryptoRSATool.createNewRSAKeyPair(AMTCryptoRSATool.KeySize._2048);
		
		String audiencePublicRSAKey = AMTCryptoRSATool.encodeToBase64(keyPair.getPublic() );
		
		System.out.println("Public Key to be used to encrypt the plain-text [" + audiencePublicRSAKey + "]");
		String issuer = "10.86.12.272";
		String audience = "https://goloans.robinsonsbank.com.ph"; // aud
		String subject = "data-entry-cc"; // sub
		 
		Map<String, Object> payloadMap = new HashMap<String, Object>();
		payloadMap.put("trxn-limit", "1");
		
		String encryptedJwtString = encrypt(issuer, subject, audience, audiencePublicRSAKey, payloadMap, 100);
		
		System.out.println("Encrypted: " + encryptedJwtString);
		
		// ---------- DECRYPT --------------
		String audiencePrivateRSAKey = AMTCryptoRSATool.encodeToBase64(keyPair.getPrivate() );
		System.out.println("Private Key to be used to decrypt the encrypted data [" + audiencePrivateRSAKey + "]");
		payloadMap = decrypt(encryptedJwtString, audiencePrivateRSAKey);
		
		System.out.println("Decrypted data");
		
		for (Map.Entry<String, Object> entry : payloadMap.entrySet() ) {
			System.out.println(entry.getKey() + ": " + entry.getValue() );
        }
		
		// ---------- SIGN --------------
		String signedJwtString = signAndEncode("" , issuer, subject, audience, audiencePrivateRSAKey, payloadMap, 60);
		
		System.out.println("\n\nSigned: " + signedJwtString);
		
		// ---------- VERIFY SIGNATURE --------------
		payloadMap = verifyAndDecode(signedJwtString, audiencePublicRSAKey);
		
		for (Map.Entry<String, Object> entry : payloadMap.entrySet() ) {
			System.out.println(entry.getKey() + ": " + entry.getValue() );
        } 
	} // End method
	
	public static void main(String[] args) throws Exception {
		String api_token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhcGl0b2tlbiIsInNlc3Npb24iOiIxNzQxOjE3NDE3NzM1Mzc4MjY6MTYwMDY2NDYzNjI0NzAwMjMiLCJsYXN0U2Vzc2lvbkFwcENvZGUiOiJFUE4iLCJsYXN0U2Vzc2lvbkRlc3Ryb3lUaW1lIjoiMTc0MTc3MzUzNyIsImxhc3RTZXNzaW9uQ3JlYXRlVGltZSI6IjE3NDE3NzM1MzYiLCJhdXRob3JpemF0aW9uIjpbeyJhdXRob3JpdHkiOiJFUE5fQ0hBU1NJUyJ9LHsicHJpdmlsZWdlIjoiZjFfc21zX3NlbmQifSx7InByaXZpbGVnZSI6ImYxX2VtYWlsX3NlbmQifSx7ImF1dGhvcml0eSI6IkNVTVNfQ0xJRU5UX0FQUCJ9LHsiYXV0aG9yaXR5IjoiTE9DT19VU0VSIn0seyJhdXRob3JpdHkiOiJNQ19BQ0NFUFRBTkNFIn0seyJwcml2aWxlZ2UiOiJjdXN0X3ZpZXdlciJ9LHsicHJpdmlsZWdlIjoibWNsb2FuX3ZpZXdlciJ9LHsicHJpdmlsZWdlIjoiZG9jX3ZpZXdlciJ9LHsicHJpdmlsZWdlIjoibm90ZV92aWV3ZXIifSx7InByaXZpbGVnZSI6Im5vdGVfY3JlYXRvciJ9LHsicHJpdmlsZWdlIjoibWNfZXZhbF92aWV3ZXIifSx7InByaXZpbGVnZSI6Im1jbG9hbl9yZXR1cm5fZHJhZnQifSx7InByaXZpbGVnZSI6Im1jX2FjY2VwdGFuY2Vfdmlld2VyIn0seyJwcml2aWxlZ2UiOiJtY19hY2NlcHRhbmNlIn0seyJwcml2aWxlZ2UiOiJtY19jdXN0X3ZpZXdlciJ9LHsicHJpdmlsZWdlIjoidXBkYXRlX2NyZWRlbnRpYWxzIn0seyJhdXRob3JpdHkiOiJNQ19DT0xMQVRfT0ZGSUNFUiJ9LHsicHJpdmlsZWdlIjoiY3Btc19wYXJhbV92aWV3ZXIifSx7InByaXZpbGVnZSI6Im1jX2NvbGxhdF92aWV3ZXIifSx7InByaXZpbGVnZSI6Im1jX2NvbGxhdF9vZmZpY2VyIn1dLCJhdWQiOiJbaHR0cHM6XC9cL21jLWJlc3Qucm9iaW5zb25zYmFuay5jb20ucGhdIiwidXNlckVuYyI6IkVYMVVSWTZqcXZiNERzWlpseG1yOEE9PSIsImxhc3RTZXNzaW9uQXBwRGVzYyI6IkVsZWN0cm9uaWMgUHJvbWlzc29yeSBOb3RlIFN5c3RlbSIsImV4cCI6MTc0MTg4MTU2NSwiaWF0IjoxNzQxODQ0NzI1LCJqdGkiOiJlZTQ2YTJmOGY4ZDU0ZDQ2YmQyM2Q5ODk4ZGJhZGU5NSIsInVzZXJuYW1lIjoiZXBuY2hhc3NpcyJ9.HAKIdBYHjJZ8LD-oAnaqRmAh8SSqWOhS62OQ8EuOygNB5cYmUht5H8_cX9FJVlwPVwwy7IjVrISWxLPQvQVXLZbXLLZ769GlX64Tmv5QSDbQl1UQsJsNwD-DTkmvhXUZmtkiMsAcMegXsLavBDyGw3aANJNWUlfEokSgOPCgeiug14ybSdKzDh5KVsiDcobR49v6AJlAMZg4hqhnXige93_fThcdrOtrcKNiV4gAWOPkri290hBDlJ3MtsjjVzZx5zVj5lZSjjfpOUIqrLGxaAYXvLltxXyollNpTTa-lMB3pWLez5drruQX89-En_gRvyRl7aIo5oly6IflBQnD_g";
		String audiencePublicRSAKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgqcTn9Vl/Z165A7bsmHDV6UGdt6rvDacA7zUmkKDiyj3+SnfL69/XC6CGOhvuvgE5fsQ7sVdF/94lZvUF4hjuJCevsK4bC2/hIcHcgen2e0UIVEDjVHpSSSi4rsw9FHB03hIeqeGjbFMe9kAWQgVHsmYLQL1I5rQ+fwkx1zvr5o/JTw8tSBJJ+j9OB1yKOUyZH4NxJQM6xEAkxTmM2pkJ5vQTYPBXo2L95TaCIm12ycLBkdkX2DxU6z2kWxneuR+RdgCMNKIkn+VGv5AW0Lh2WtVVL2K/+BjmUFcsdmzbFMpaK9Ncu9xHsXR7UL9BN2iEJOebIt/+j4z0p2ZfBncFQIDAQAB";
		
		
		Map<String, Object> map = JWTTools.verifyAndDecode(api_token, audiencePublicRSAKey);
		
		System.out.println(map);
	}
	
	public static boolean verify(SignedJWT signedJWTVer, String issuerPublicRSAKey) throws Exception {
		// Create a decrypter with the specified private RSA key
		JWSVerifier verifier = new RSASSAVerifier( (RSAPublicKey) AMTCryptoRSATool.decodePublicFromBase64(issuerPublicRSAKey) );
		
		return signedJWTVer.verify(verifier);
	} // End method
	
	public static Map<String, Object> verifyAndDecode(String jwtString, String issuerPublicRSAKey) throws Exception {
		// Create a decrypter with the specified public RSA key
		JWSVerifier verifier = new RSASSAVerifier( (RSAPublicKey) AMTCryptoRSATool.decodePublicFromBase64(issuerPublicRSAKey) );
		
		SignedJWT signedJWTVer = SignedJWT.parse(jwtString);
		
		boolean isVerified = signedJWTVer.verify(verifier);
		
		if (isVerified) {
			return signedJWTVer.getJWTClaimsSet().getClaims();	
		}
		else {
			throw new JOSEException("Unable to verify token.");
		}
	} // End method
	
	public static String signAndEncode(String jwtId, String issuer, String subject, String audience, String issuerPrivateRSAKey, Map<String, Object> payloadMap, int expiryInMinutes) throws Exception {
		// Compose the JWT claims set
		Date expiryDate = new Date(new Date().getTime() + 1000 * 60 * expiryInMinutes);
		 
		return signAndEncode(jwtId, issuer, subject, audience, issuerPrivateRSAKey, payloadMap, expiryDate);
	} // End method
	
	public static String signAndEncode(String jwtId, String issuer, String subject, String audience, String issuerPrivateRSAKey, Map<String, Object> payloadMap, Date expiryDate) throws Exception {
		String jwtString = null;
		
		// Compose the JWT claims set
		Date now = new Date();
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
		
		if (payloadMap != null) {
			for (Map.Entry<String, Object> entry : payloadMap.entrySet()) {
				builder.claim(entry.getKey(), entry.getValue() );
			}
		}
		
		if (subject != null) {
			builder.subject(subject);
		}
		
		if (issuer != null) {
			builder.issuer(issuer);			
		}
		
		if (audience != null) {
			builder.audience(audience);
		}
		
		if (expiryDate != null) {
			builder.expirationTime(expiryDate); // Expires in 10 minutes
		}
		
		builder.issueTime(now);
		builder.jwtID(jwtId);
		
	    // Create RSA-signer with the private key
		JWSSigner signer = new RSASSASigner(  (RSAPrivateKey) AMTCryptoRSATool.decodePrivateFromBase64(issuerPrivateRSAKey) );
	 
		SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), builder.build() );
		
		// Compute the RSA signature
		try {
			signedJWT.sign(signer);
		} catch (JOSEException e) {
			throw new RuntimeException("Failed to generate token.", e);
		}

		jwtString = signedJWT.serialize() ;
		
		return jwtString;
	} // End method
	
	public static Map<String, Object> decrypt(String jwtString, String audiencePrivateRSAKey) throws Exception {
		// Parse JWT string.
		com.nimbusds.jwt.EncryptedJWT jwt = com.nimbusds.jwt.EncryptedJWT.parse(jwtString);
		
		// Create a decrypter with the specified private RSA key
		com.nimbusds.jose.crypto.RSADecrypter decrypter = new com.nimbusds.jose.crypto.RSADecrypter( (RSAPrivateKey) AMTCryptoRSATool.decodePrivateFromBase64(audiencePrivateRSAKey) );

		// Decrypt JWE.
		jwt.decrypt(decrypter);
		
		return jwt.getJWTClaimsSet().getClaims();
	} // End method
	
	public static String decryptAsJSONString(String jwtString, String audiencePrivateRSAKey) throws Exception {
		// Parse JWT string.
		com.nimbusds.jwt.EncryptedJWT jwt = com.nimbusds.jwt.EncryptedJWT.parse(jwtString);
		
		// Create a decrypter with the specified private RSA key
		com.nimbusds.jose.crypto.RSADecrypter decrypter = new com.nimbusds.jose.crypto.RSADecrypter( (RSAPrivateKey) AMTCryptoRSATool.decodePrivateFromBase64(audiencePrivateRSAKey) );

		// Decrypt JWE.
		jwt.decrypt(decrypter);
		
		return jwt.getJWTClaimsSet().toJSONObject().toJSONString();
	} // End method
	
	public static String encrypt(String issuer, String subject, String audience, String audiencePublicRSAKey, java.util.Map<String, Object> payload, int expiryInMinutes) throws Exception {
		String jwtString = null;
				
		// Compose the JWT claims set
		java.util.Date now = new java.util.Date();

		// Prepare JWT with claims set.
		com.nimbusds.jwt.JWTClaimsSet.Builder builder = new com.nimbusds.jwt.JWTClaimsSet.Builder();
	    
	    if (payload != null) {
	        for (java.util.Map.Entry<String, Object> entry : payload.entrySet() ) {
	            builder.claim(entry.getKey(), entry.getValue());
	        }
	    }
	    
	    com.nimbusds.jwt.JWTClaimsSet jwtClaims = builder
				.issuer(issuer)
				.subject(subject)
				.audience(audience)
				.expirationTime(new java.util.Date(now.getTime() + 1000 * 60 * expiryInMinutes) ) // Expires in 10 minutes
				.notBeforeTime(now)
				.issueTime(now)
				.jwtID(java.util.UUID.randomUUID().toString() )
				.build(); 
		 

		// Request JWT encrypted with RSA-OAEP-256 and AES in Galois/Counter Mode (GCM) (NIST.800-38D) using a 256 bit key(recommended).
	    com.nimbusds.jose.JWEHeader header = new com.nimbusds.jose.JWEHeader(com.nimbusds.jose.JWEAlgorithm.RSA_OAEP_256, com.nimbusds.jose.EncryptionMethod.A256GCM);
		
		// Create the encrypted JWT object
	    com.nimbusds.jwt.EncryptedJWT jwt = new com.nimbusds.jwt.EncryptedJWT(header, jwtClaims);

		// Create an encrypter with the specified public RSA key.
	    com.nimbusds.jose.JWEEncrypter encrypter = new com.nimbusds.jose.crypto.RSAEncrypter( (java.security.interfaces.RSAPublicKey) AMTCryptoRSATool.decodePublicFromBase64(audiencePublicRSAKey) );
		 
		// Do the actual encryption encrypter.
		jwt.encrypt(encrypter);
		
		// Serialize to JWT compact form
		jwtString = jwt.serialize();
		
		return jwtString;
	} // End method
	
	
	// Converts RSA encoded key to JWK (JSON Web Key)
	public static com.nimbusds.jose.jwk.RSAKey convertEncodedKeyToJWK(String rsaEncodedPublicKey, String rsaEncodedPrivateKey) throws IOException, GeneralSecurityException {
		
		RSAPublicKey pubKey = (java.security.interfaces.RSAPublicKey) AMTCryptoRSATool.decodePublicFromBase64(rsaEncodedPublicKey);
		RSAPrivateKey priKey = (RSAPrivateKey) AMTCryptoRSATool.decodePrivateFromBase64(rsaEncodedPrivateKey);
		
		// Convert to JWK format
		return new com.nimbusds.jose.jwk.RSAKey.Builder(pubKey)
			.privateKey(priKey)
		    .keyUse(KeyUse.SIGNATURE)
		    .keyID(UUID.randomUUID().toString() )
		    .build();
	}
	
	// Converts RSA encoded key to JWK (JSON Web Key)
	public static com.nimbusds.jose.jwk.RSAKey convertEncodedPublicKeyToJWK(String rsaEncodedPublicKey) throws IOException, GeneralSecurityException {
		
		RSAPublicKey pubKey = (java.security.interfaces.RSAPublicKey) AMTCryptoRSATool.decodePublicFromBase64(rsaEncodedPublicKey);
		
		// Convert to JWK format
		return new com.nimbusds.jose.jwk.RSAKey.Builder(pubKey)
		    .keyUse(KeyUse.SIGNATURE)
		    .keyID(UUID.randomUUID().toString() )
		    .build();
	}
	
	public static SignedJWT createSignedJWT(String issuer, String subject, String audience, com.nimbusds.jose.jwk.RSAKey senderPrivateJWK, java.util.Map<String, Object> payload, int expiryInMinutes) throws Exception {
		java.util.Date now = new java.util.Date();
				
		// Prepare JWT with claims set.
		com.nimbusds.jwt.JWTClaimsSet.Builder builder = new com.nimbusds.jwt.JWTClaimsSet.Builder();
	    
	    if (payload != null) {
	        for (java.util.Map.Entry<String, Object> entry : payload.entrySet() ) {
	            builder.claim(entry.getKey(), entry.getValue() );
	        }
	    }	    
	    
		// Create JWT
		SignedJWT signedJWT = new SignedJWT(
		    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(senderPrivateJWK.getKeyID() ).build(),
		    	 builder.issueTime(now)
		    	 .expirationTime(new java.util.Date(now.getTime() + 1000 * 60 * expiryInMinutes) ) // Expires in 10 minutes
		    	 .issuer(issuer)
		    	 .audience(audience)
		    	 .subject(subject)
		    	 .jwtID(java.util.UUID.randomUUID().toString() )
		    	 .build()
		    );
		
		// Sign the JWT
		signedJWT.sign(new RSASSASigner(senderPrivateJWK) );
		
		return signedJWT;
	}
	
	public static String createJWEUsingAESEncryption(SecretKey sharedAESKey, SignedJWT signedJWT) throws Exception {
		 
		// Create the header
		JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A256GCM)
	        .contentType("JWT") // required to indicate nested JWT
	        .build();
	        
		// Set the plain text
		Payload payload = new Payload(signedJWT);

		// Create the JWE object and encrypt it
		JWEObject jweObject = new JWEObject(header, payload);
		jweObject.encrypt(new DirectEncrypter(sharedAESKey) );

		// Serialise to compact JOSE form...
		String jweString = jweObject.serialize();
		
		return jweString;
	}
	
	public static String decryptJWEUsingAESEncryption(SecretKey sharedAESKey, String rsaEncodedPublicKey_sender, String encryptedSignedJWE) throws JOSEException, IOException, GeneralSecurityException, ParseException, AuthException {
		 
		// Parse the JWE string
		JWEObject jweObject = JWEObject.parse(encryptedSignedJWE);
		
		// Decrypt with Shared Key
		jweObject.decrypt(new DirectDecrypter(sharedAESKey) );
		
		// Extract payload
		SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
		
		com.nimbusds.jose.jwk.RSAKey senderPublicJWK = JWTTools.convertEncodedPublicKeyToJWK(rsaEncodedPublicKey_sender);
		
		// Check the signature
		boolean verifed = signedJWT.verify(new RSASSAVerifier(senderPublicJWK) );
		
		if (verifed == false) {
			throw new AuthException("Unable to verify JWT.");
		}
		
		return signedJWT.getJWTClaimsSet().toString();
	}
	
	 
	
}
