/*
 * Copyright (c) 2012-2017 ZoxWeb.com LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zoxweb.client.data.crypto;



import org.zoxweb.client.data.JSONClientUtil;
import org.zoxweb.shared.crypto.CryptoConst;
import org.zoxweb.shared.crypto.CryptoInterface;
import org.zoxweb.shared.security.AccessSecurityException;
import org.zoxweb.shared.security.JWT;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.util.SharedBase64.Base64Type;

/**
 *
 */
public class CryptoClient
	implements CryptoInterface
{

	public static final CryptoInterface SINGLETON = new CryptoClient();

	protected CryptoClient()
	{

	}

	@Override
	public byte[] hash(String mdAlgo, byte[]... tokens)
        throws NullPointerException, AccessSecurityException
	{
		CryptoConst.MDType mdType = CryptoConst.MDType.lookup(mdAlgo);
		SharedUtil.checkIfNulls("MD type not found", mdType);
		StringBuilder sb = new StringBuilder();

		switch (mdType)
		{
		case MD5:
			for (byte[] array : tokens)
			{
				sb.append(SharedStringUtil.toString(array));
			}
			return SharedStringUtil.hexToBytes(hashMD5(sb.toString()));
		case SHA_256:
			for (byte[] array : tokens)
			{
				sb.append(SharedStringUtil.toString(array));
			}
			return SharedStringUtil.hexToBytes(hashSHA256(sb.toString()));
			
			default:
				throw new AccessSecurityException("Digest not supported " + mdType);
		}
	}

	/**
	 * @see CryptoInterface#hash(java.lang.String, java.lang.String[])
	 */
	@Override
	public byte[] hash(String mdAlgo, String... tokens)
        throws AccessSecurityException
	{
		CryptoConst.MDType mdType = CryptoConst.MDType.lookup(mdAlgo);
		SharedUtil.checkIfNulls("MD type not found", mdType);
		StringBuilder sb = new StringBuilder();

		switch(mdType)
		{
		case MD5:
			for (String str : tokens)
			{
				sb.append(str);
			}
			return SharedStringUtil.hexToBytes(hashMD5(sb.toString()));
		case SHA_256:
			for (String str : tokens)
			{
				sb.append(str);
			}
			return SharedStringUtil.hexToBytes(hashSHA256(sb.toString()));
			
			default:
				throw new AccessSecurityException("Digest not supported " + mdType);
		}
	}
	
	
	public static native String hashSHA256(String ptext)
    /*-{
     	var bitArray = $wnd.sjcl.hash.sha256.hash(ptext);
     	return $wnd.sjcl.codec.hex.fromBits(bitArray);  
     }-*/;
	
	
	public static native String hashMD5(String ptext)
    /*-{
     	return $wnd.hex_md5(ptext);
     }-*/;
	
	
	
	public static native String hmacSHA256Native(String key, String data)
    /*-{
     
     	var bitArrayKey = $wnd.sjcl.codec.hex.toBits(key);
     	var bitArrayData = $wnd.sjcl.codec.hex.toBits(data);
     	var hash = $wnd.sjcl.hash.sha256;
     	var mac = (new $wnd.sjcl.misc.hmac(bitArrayKey, hash)).mac(bitArrayData);
     	
     	
     	return $wnd.sjcl.codec.hex.fromBits(mac);  
     }-*/;

	@Override
	public byte[] hmacSHA256(byte[] key, byte[] data) throws AccessSecurityException 
	{
		
		String keyBytes = SharedStringUtil.bytesToHex(key);
		String dataBytes = SharedStringUtil.bytesToHex(data);
		String result = hmacSHA256Native(keyBytes, dataBytes);
		
		// TODO Auto-generated method stub
		return SharedStringUtil.hexToBytes(result);
	}

	@Override
	public String encodeJWT(byte[] key, JWT jwt) throws AccessSecurityException {
		SharedUtil.checkIfNulls("Null jwt", jwt, jwt.getHeader(), jwt.getHeader().getJWTAlgorithm());
		
		StringBuilder sb = new StringBuilder();
		byte[] b64Header = SharedBase64.encode(Base64Type.URL, JSONClientUtil.toString(JSONClientUtil.toJSON(jwt.getHeader(), false)));
		byte[] b64Payload = SharedBase64.encode(Base64Type.URL, JSONClientUtil.toString(JSONClientUtil.toJSON(jwt.getPayload(), false)));
		sb.append(SharedStringUtil.toString(b64Header));
		sb.append(".");
		sb.append(SharedStringUtil.toString(b64Payload));
		
		byte[] b64Hash = null;
		switch(jwt.getHeader().getJWTAlgorithm())
		{
		case HS256:
			SharedUtil.checkIfNulls("Null key", key);
			b64Hash = SharedBase64.encode(Base64Type.URL, hmacSHA256(key, SharedStringUtil.getBytes(sb.toString())));
			break;
		case none:
			break;
		}
		sb.append(".");
		if(b64Hash != null)
			sb.append(SharedStringUtil.toString(b64Hash));

		return sb.toString();
	}

	@Override
	public JWT decodeJWT(byte[] key, String b64urlToken) throws AccessSecurityException {
		// TODO Auto-generated method stub
		return null;
	}
	

}