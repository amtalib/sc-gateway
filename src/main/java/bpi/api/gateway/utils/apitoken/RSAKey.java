package bpi.api.gateway.utils.apitoken;

/***
 * @author Abdulhamid M. Talib
 * @since 07-04-2014
 * @version 1.0
 * */
public class RSAKey {

	private String id;
	private String publicKey;
	private String privateKey;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Override
	public String toString() {
		return "RSAKey [id=" + id + ", publicKey=" + publicKey
				+ ", privateKey=" + privateKey + "]";
	}
	
}
