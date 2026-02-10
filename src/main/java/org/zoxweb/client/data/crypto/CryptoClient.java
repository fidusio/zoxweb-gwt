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
import org.zoxweb.shared.crypto.JWTCodec;
import org.zoxweb.shared.security.*;
import org.zoxweb.shared.security.JWT.JWTField;

import org.zoxweb.shared.util.*;
import org.zoxweb.shared.util.SharedBase64.Base64Type;


/**
 *
 * @author javaconsigliere
 *
 */
public class CryptoClient
        implements JWTCodec {

    public static final JWTCodec SINGLETON = new CryptoClient();
    //private long requestID = 0;

    protected CryptoClient() {

    }

//	private synchronized long nextID()
//	{
//	  return ++requestID;
//	}

    @Override
    public byte[] hash(String mdAlgo, byte[]... tokens)
            throws NullPointerException, AccessSecurityException {
        CryptoConst.HashType mdType = CryptoConst.HashType.lookup(mdAlgo);
        SUS.checkIfNulls("MD type not found", mdType);
        StringBuilder sb = new StringBuilder();

        switch (mdType) {
            case MD5:
                for (byte[] array : tokens) {
                    sb.append(SharedStringUtil.toString(array));
                }

                return SharedStringUtil.hexToBytes(hashMD5(sb.toString()));
            case SHA_256:
                for (byte[] array : tokens) {
                    sb.append(SharedStringUtil.toString(array));
                }
                return SharedStringUtil.hexToBytes(hashSHA256(sb.toString()));

            default:
                throw new AccessSecurityException("Digest not supported " + mdType);
        }
    }

    /**
     * @see JWTCodec#hash(java.lang.String, java.lang.String[])
     */
    @Override
    public byte[] hash(String mdAlgo, String... tokens)
            throws AccessSecurityException {
        CryptoConst.HashType mdType = CryptoConst.HashType.lookup(mdAlgo);
        SUS.checkIfNulls("MD type not found", mdType);
        StringBuilder sb = new StringBuilder();

        switch (mdType) {
            case MD5:
                for (String str : tokens) {
                    sb.append(str);
                }
                return SharedStringUtil.hexToBytes(hashMD5(sb.toString()));
            case SHA_256:
                for (String str : tokens) {
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

    public static native String hmacSHA512Native(String key, String data)
    /*-{
     
     	var bitArrayKey = $wnd.sjcl.codec.hex.toBits(key);
     	var bitArrayData = $wnd.sjcl.codec.hex.toBits(data);
     	var hash = $wnd.sjcl.hash.sha512;
     	var mac = (new $wnd.sjcl.misc.hmac(bitArrayKey, hash)).mac(bitArrayData);
     	
     	
     	return $wnd.sjcl.codec.hex.fromBits(mac);  
     }-*/;


    @Override
    public byte[] hmacSHA256(byte[] key, byte[] data) throws AccessSecurityException {

        String keyBytes = SharedStringUtil.bytesToHex(key);
        String dataBytes = SharedStringUtil.bytesToHex(data);
        String result = hmacSHA256Native(keyBytes, dataBytes);

        // TODO Auto-generated method stub
        return SharedStringUtil.hexToBytes(result);
    }

    @Override
    public String encode(byte[] key, JWT jwt) throws AccessSecurityException {
        SUS.checkIfNulls("Null jwt", jwt, jwt.getHeader(), jwt.getHeader().getJWTAlgorithm());

        StringBuilder sb = new StringBuilder();
        byte[] b64Header = SharedBase64.encode(Base64Type.URL, JSONClientUtil.toString(JSONClientUtil.toJSONGenericMap(jwt.getHeader().getProperties(), false)));
        byte[] b64Payload = SharedBase64.encode(Base64Type.URL, JSONClientUtil.toString(JSONClientUtil.toJSONGenericMap(jwt.getPayload().getProperties(), false)));
        sb.append(SharedStringUtil.toString(b64Header));
        sb.append(".");
        sb.append(SharedStringUtil.toString(b64Payload));

        // due to lib limitation only HS256 is supported
//		if (jwt.getHeader().getJWTAlgorithm() == JWTAlgorithm.HS512)
//		{
//			jwt.getHeader().setJWTAlgorithm(JWTAlgorithm.HS256);
//		}

        String b64Hash = null;
        switch (jwt.getHeader().getJWTAlgorithm()) {
            case HS256:
                SUS.checkIfNulls("Null key", key);
                b64Hash = SharedBase64.encodeAsString(Base64Type.URL, hmacSHA256(key, SharedStringUtil.getBytes(sb.toString())));
                break;
            case HS512:
                SUS.checkIfNulls("Null key", key);
                b64Hash = SharedBase64.encodeAsString(Base64Type.URL, hmacSHA512(key, SharedStringUtil.getBytes(sb.toString())));
                break;
            case none:
                break;
            case RS256:
            case RS512:
            case ES256:
            case ES512:
                throw new AccessSecurityException(jwt.getHeader().getJWTAlgorithm() + " is not supported");


        }
        sb.append(".");
        if (b64Hash != null)
            sb.append(b64Hash);

        return sb.toString();
    }

    @Override
    public JWT decode(byte[] key, String b64urlToken) throws AccessSecurityException {
        // TODO Auto-generated method stub
        String tokens[] = b64urlToken.split("\\.");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("token too short");
        }

        JWT jwt = parseJWT(b64urlToken);
        String hash = null;
        switch (jwt.getHeader().getJWTAlgorithm()) {
            case HS256:
                hash = SharedBase64.encodeAsString(Base64Type.URL, hmacSHA256(key, SharedStringUtil.getBytes(tokens[0] + "." + tokens[1])));

                break;
            case HS512:
                hash = SharedBase64.encodeAsString(Base64Type.URL, hmacSHA512(key, SharedStringUtil.getBytes(tokens[0] + "." + tokens[1])));

                break;
            case none:
                break;
            case RS256:
            case RS512:
            case ES256:
            case ES512:
                throw new AccessSecurityException(jwt.getHeader().getJWTAlgorithm() + " is not supported");
        }

        if (!hash.equals(jwt.getHash())) {
            throw new AccessSecurityException("Invalid jwt token");
        }


        return jwt;
    }


    public static JWT parseJWT(String token) throws NullPointerException, IllegalArgumentException, AccessException {
        SUS.checkIfNulls("Null token", token);
        String tokens[] = token.trim().split("\\.");

        if (tokens.length < 2 || tokens.length > 3) {
            throw new IllegalArgumentException("Invalid token JWT token");
        }

        NVGenericMap nvgmHeader = JSONClientUtil.fromJSONGenericMap(SharedBase64.decodeAsString(Base64Type.URL, tokens[JWTField.HEADER.ordinal()]), null);//JWTHeader.NVC_JWT_HEADER, Base64Type.URL);//GSONUtil.fromJSON(SharedBase64.decodeAsString(Base64Type.URL,tokens[JWTField.HEADER.ordinal()]), JWTHeader.class);
        NVGenericMap nvgmPayload = JSONClientUtil.fromJSONGenericMap(SharedBase64.decodeAsString(Base64Type.URL, tokens[JWTField.PAYLOAD.ordinal()]), null);
        if (nvgmPayload == null)
            throw new AccessException("Invalid JWT");
        JWT ret = new JWT();


        //jwtPayload = GSONUtil.fromJSON(SharedStringUtil.toString(SharedBase64.decode(Base64Type.URL,tokens[JWTToken.PAYLOAD.ordinal()])), JWTPayload.class);
        JWTPayload jwtPayload = ret.getPayload();
        jwtPayload.setProperties(nvgmPayload);
        JWTHeader jwtHeader = ret.getHeader();
        jwtHeader.setProperties(nvgmHeader);
        if (jwtHeader == null || jwtPayload == null) {
            throw new AccessException("Invalid JWT");
        }


        SUS.checkIfNulls("Null jwt header or parameters", jwtHeader, jwtHeader.getJWTAlgorithm());
//      JWT ret = new JWT();
        //ret.setHeader(jwtHeader);
        //ret.setPayload(jwtPayload);
        switch (jwtHeader.getJWTAlgorithm()) {
            case HS256:
            case HS512:
            case RS256:
            case RS512:
            case ES256:
            case ES512:
                if (tokens.length != JWTField.values().length) {
                    throw new IllegalArgumentException("Invalid token JWT token length expected 3");
                }
                ret.setHash(tokens[JWTField.HASH.ordinal()]);
                break;
            case none:
                if (tokens.length != JWTField.values().length - 1) {
                    throw new IllegalArgumentException("Invalid token JWT token length expected 2");
                }
                break;
        }


        return ret;
    }

    @Override
    public byte[] hmacSHA512(byte[] key, byte[] data) throws AccessSecurityException {
        // TODO Auto-generated method stub
        String keyBytes = SharedStringUtil.bytesToHex(key);
        String dataBytes = SharedStringUtil.bytesToHex(data);
        String result = hmacSHA512Native(keyBytes, dataBytes);

        // TODO Auto-generated method stub
        return SharedStringUtil.hexToBytes(result);
    }

    @Override
    public String encode(JWTEncoderData jed) {

        return encode(jed.getKey(), jed.getJWT());
    }

    @Override
    public JWT decode(JWTDecoderData jdd) {
        // TODO Auto-generated method stub
        return decode(jdd.getKey(), jdd.getToken());
    }

//	@Override
//	public byte[] hmacSHA256(byte[] key, byte[] data) throws AccessSecurityException {
//		// TODO Auto-generated method stub
//		return  HmacUtils.hmacSha256(key, data);
//	}

    public static AuthToken getAuthToken() {
        return ResourceManager.lookupResource((GetName) ResourceManager.Resource.AUTH_TOKEN);
    }

    public static void setAuthToken(AuthToken authToken) {
        ResourceManager.SINGLETON.register((GetName) ResourceManager.Resource.AUTH_TOKEN, authToken);
    }
}