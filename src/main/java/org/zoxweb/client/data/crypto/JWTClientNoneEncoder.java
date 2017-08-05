package org.zoxweb.client.data.crypto;



import org.zoxweb.client.data.JSONClientUtil;
import org.zoxweb.shared.crypto.JWTEncoder;
import org.zoxweb.shared.security.AccessSecurityException;
import org.zoxweb.shared.security.JWT;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.util.SharedBase64.Base64Type;

public class JWTClientNoneEncoder 
implements JWTEncoder{

	public static final JWTClientNoneEncoder SINGLETON = new JWTClientNoneEncoder();
	
	
	
	private JWTClientNoneEncoder()
	{
		
	}
	
	@Override
	public String encodeJWT(byte[] key, JWT jwt) throws AccessSecurityException {
		SharedUtil.checkIfNulls("Null jwt", jwt);
        SharedUtil.checkIfNulls("Null jwt header", jwt.getHeader());
        SharedUtil.checkIfNulls("Null jwt algorithm", jwt.getHeader().getJWTAlgorithm());

		
		StringBuilder sb = new StringBuilder();
		byte[] b64Header = SharedBase64.encode(Base64Type.URL, JSONClientUtil.toString(JSONClientUtil.toJSON(jwt.getHeader(), false)));
		byte[] b64Payload = SharedBase64.encode(Base64Type.URL, JSONClientUtil.toString(JSONClientUtil.toJSON(jwt.getPayload(), false)));
		sb.append(SharedStringUtil.toString(b64Header));
		sb.append(".");
		sb.append(SharedStringUtil.toString(b64Payload));
		
	
		
		
		
		switch(jwt.getHeader().getJWTAlgorithm())
		{			
		case none:
			break;
		default:
			throw new AccessSecurityException(jwt.getHeader().getJWTAlgorithm() +" not supported.");
		}
		
		sb.append(".");	

		return sb.toString();
	}

}
