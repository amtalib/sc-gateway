package bpi.api.gateway.utils.apitoken;

import java.io.IOException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/****
 * 
 * @author AMTalib
 *    - Tested on Java 1.7
 */
public class AMTCryptoRSATool {
	
	public enum KeySize {
		_1024 {
			@Override
			public int getSize() {
				return 1024;
			}
		}, _2048 {
			@Override
			public int getSize() {
				return 2048;
			}
		}, _3072 {
			@Override
			public int getSize() {
				return 3072;
			}
		}, _4096 {
			@Override
			public int getSize() {
				return 4096;
			}
		};
		
		public abstract int getSize();
	} // End enum
	
	/**
	 * Create a new RSA key pair, private and public keys.
	 *   - Private key - to be given to the data sender. 
	 *   - Public key - to be given to the data receiver. 
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public static KeyPair createNewRSAKeyPair(KeySize keySize) throws IOException, GeneralSecurityException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		
		keyGen.initialize(keySize.getSize(), random);
		KeyPair keypair = keyGen.genKeyPair();
		
		return keypair;
	} // End method
	
	public static String encodeToBase64(PublicKey publicKey) throws IOException, GeneralSecurityException {
		return AMTCryptoRSATool.encodeToBase64(publicKey.getEncoded() );
	}
	
	public static PublicKey decodePublicFromBase64(String encodedPublicKey) throws IOException, GeneralSecurityException {
        String publicKeyPEM = encodedPublicKey;
        
        // Remove the first and last lines
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        
        // Base64 decode data
        byte[] encoded = AMTCryptoRSATool.decodeFromBase64(publicKeyPEM);
        
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(encoded) );
        
        return pubKey;
	} // End method
	
	public static String encodeToBase64(PrivateKey privateKey) throws IOException, GeneralSecurityException {
		return AMTCryptoRSATool.encodeToBase64(privateKey.getEncoded() );
	}
	
	/**
	 * Constructs a private key (RSA) from the given string.
	 * */
	public static PrivateKey decodePrivateFromBase64(String encodedPrivateKey) throws IOException, GeneralSecurityException {
        String publicKeyPEM = encodedPrivateKey;
        
        // Remove the first and last lines
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PRIVATE KEY-----n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PRIVATE KEY-----", "");
        
        // Java 7
        // Base64 decode data
        // byte[] encoded = AMTCryptoRSATool.decodeFromBase64(encodedPrivateKey);
        
        // Java 8
        byte[] encoded = java.util.Base64.getDecoder().decode(publicKeyPEM);
        
        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
        java.security.PrivateKey privKey = kf.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(encoded) );
        
        return privKey;
    } // End method
	
	public static String encryptKey(String plainText, PublicKey publicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(plainText.getBytes("UTF-8"));
 
        return AMTCryptoRSATool.encodeToBase64(cipherText);
    } // End method
	
	public static String encryptKey(String plainText, String encodedPublicKey) throws Exception {
		return AMTCryptoRSATool.encryptKey(plainText, AMTCryptoRSATool.decodePublicFromBase64(encodedPublicKey) );
    } // End method

    public static String decryptKey(String cipherText, PrivateKey privateKey) throws Exception {
        byte[] bytes = decodeFromBase64(cipherText);
        
        Cipher decriptCipher = Cipher.getInstance("RSA");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return new String(decriptCipher.doFinal(bytes), "UTF-8");
    } // End method
	
    public static String decryptKey(String plainText, String encodedPrivateKey) throws Exception {
		return AMTCryptoRSATool.decryptKey(plainText, AMTCryptoRSATool.decodePrivateFromBase64(encodedPrivateKey) );
    } // End method
    
    /**
     * Returns the signature.
     * */
    public static String signWithRSA(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes("UTF-8") );

        byte[] signature = privateSignature.sign();

        return AMTCryptoRSATool.encodeToBase64(signature);
    } // End method

    public static boolean verifyRSASignature(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes("UTF-8"));

        // Base64 decode data
        byte[] signatureBytes = decodeFromBase64(signature);
        
        return publicSignature.verify(signatureBytes);
    } // End method
    
    /**
     * TODO
     * */
    public static String encodeToBase64(byte [] byteArray) {
    	// Java 8
		return Base64.getEncoder().encodeToString(byteArray);
    	
		// Java 7
		// return DatatypeConverter.printBase64Binary(byteArray);
    }
    
    public static byte[] decodeFromBase64(String str) {
        // Java 7
        // Base64 decode data
        // return  Base64.decode(str);
    	
        // Java 8
    	return Base64.getDecoder().decode(str.trim() );
    	
    	// URL Save Base 64 Decoding
    	// return Base64.getUrlDecoder().decode(str.trim() ); 
    }
    
    // Test
    public static void main(String... argv) throws Exception {
    	/* 
    	 *  Flow:
	    	1. Generate a symmetric key (3DES)
	    	2. Encrypt the data with the symmetric key
	    	3. Encrypt the symmetric key with rsa
	    	4. send the encrypted key and the data
	    	5. Decrypt the encrypted symmetric key with rsa
	    	6. dDcrypt the data with the symmetric key
	    */
    	
        //First generate a public/private key pair
        KeyPair pair = AMTCryptoRSATool.createNewRSAKeyPair(KeySize._2048);

        
        System.out.println("# Public Key "
        		+ "\n\t- To be used by the sender to encrypt the data or the random key of 3DES."	
        		+ "\n\t- " + AMTCryptoRSATool.encodeToBase64(pair.getPublic() ) 
        	);
        
        String encodedPrivateKey = AMTCryptoRSATool.encodeToBase64(pair.getPrivate() );
        
        System.out.println("\n# Private Key "
        		+ "\n\t- To be used by the receiver to decrypt the data or the random key of 3DES."	
        		+ "\n\t- " + encodedPrivateKey
        	);
     
      
        //Our secret message
        String _3DESkey = "the answer to life the universe and everything";

        //Encrypt the message
        String cipherKey = AMTCryptoRSATool.encryptKey(_3DESkey, pair.getPublic() );

        //Now decrypt it
        String decipheredKey = AMTCryptoRSATool.decryptKey(cipherKey, pair.getPrivate() );

        System.out.println(decipheredKey);
 
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println();
        
        // RSA Signing
        String mallId = "jwtMsg";
        String signature = signWithRSA(mallId, pair.getPrivate() );
        System.out.println("Signature: " + signature);
        
        System.out.println();
        // Let's check the signature
        boolean isCorrect = verifyRSASignature(mallId, signature, pair.getPublic() );
        System.out.println("Signature correct: " + isCorrect); 
        
        String bancNetPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJSt3g3BR+Nf8jD5UKWykPgHtSAL1wWtikPkaxSfCOMfo7Fo3koNkjy+OsHgab3rzzJtWtEO+LLyO10viNMcHQ9dluHAS4qSHY9ow57bbgdHXKYMtQYSGsAwnt4Wg0zksBKOHpfVV2yq+Y4ohfavlWuaZW4biECgsGZ3ubfBjjYQIDAQAB";
        
        String acctNo = "0019123123123";
        
        String encAcctno = AMTCryptoRSATool.encryptKey(acctNo, bancNetPublicKey);
        
        System.out.println("Acct No: " + acctNo);
        System.out.println("Encrypted Acct No: " + encAcctno);        
    } // 
 
    public static void mains(String[] args) throws Exception {
    	
    	
    	String encoded = AMTCryptoRSATool.encodeToBase64("IT131".getBytes() );
    	System.out.println(encoded);
    	String decoded = new String(AMTCryptoRSATool.decodeFromBase64(encoded) );
    	System.out.println(decoded);
    	
    	/*String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEfyyzfOTl9eILKmLbLQ3aqOGc77bM8w1qnqqwyjH++PLSs6mWrLebulxXGr0Yuu/e59Sos28Yrde0QdTZ2L2EIJ7ig3uTLWNhexvnVWvgNF2i759CVS9v4Ls2TY5TvxEpo9UTyaJRgQeTIPXbrQNkTNBxLuD5OAHFp+vCdMKMRQIDAQAB";
    	String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIR/LLN85OX14gsqYtstDdqo4ZzvtszzDWqeqrDKMf748tKzqZast5u6XFcavRi6797n1Kizbxit17RB1NnYvYQgnuKDe5MtY2F7G+dVa+A0XaLvn0JVL2/guzZNjlO/ESmj1RPJolGBB5Mg9dutA2RM0HEu4Pk4AcWn68J0woxFAgMBAAECgYAW48mbMIZy2+mzOB1f9Aaise5YKGmSOqMkukiOxj+g5u08TWGM8EXAF5AAVFQ6w4DRJ4frpSQaRV4we/cDs5f5zeOvdR69Uifh1XW4/mShHHMxIkJKTaIiZjlknc9K7qaTgllA6o4T8CjtMIPyEE9IzZpLnIRIu87rMbi1QFLfwQJBAL70a++X4CC8UgaWY/SBAXSHUFb8KaToeLKgaBBkRenDPgoIgZknyj0LhM3lgJJpaw1fsbGsiCfrETjuphJZH70CQQCxoR34XhrEisB3vgex090NP/hNChlHN6J4xTon6YGC93+AAWgXoSL1XqdpmpXi5y/mmaVsS1c9OlEcu5AJxkMpAkB/1R7g0Vpwb8G1uT4G88nmmaNV6A9030/VvP6NYqNO/65rhxBDWFjSDwED2Uz+NuY8mMBbQjYQCxSHeXPhupo9AkARGunO7Aj52j60iTkmd7d2ui1IBevvPr1Hkf6Kr2LIYNsHUZtbgynhpQ7MDZXXdtnXA/65EfxBqib7dFhniI8ZAkAjk5Zn0kld3fGJel8V0htqe9LYEdR32/UPZM43pKTrkfbYpDD3NDqof+j9aG88EirjLlroDu7s5k/VflANpcIY";
    	String pin = "118899";
    	
    	String encrypted = AMTCryptoRSATool.encryptKey(pin, publicKey);
    	
    	System.out.println("Encrypted: ");
    	System.out.println(encrypted);
    	
    	System.out.println("Decrypted: ");
    	String decrypted = AMTCryptoRSATool.decryptKey(encrypted, privateKey);
    	System.out.println(decrypted);*/
	}
    
}
