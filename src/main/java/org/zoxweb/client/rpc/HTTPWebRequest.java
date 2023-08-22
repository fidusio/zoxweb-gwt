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
package org.zoxweb.client.rpc;

import org.zoxweb.shared.http.HTTPAuthScheme;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;

import org.zoxweb.shared.util.ArrayValues;
import org.zoxweb.shared.util.GetNameValue;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.core.client.GWT;


import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

/**
 *
 */
public class HTTPWebRequest {

	private  HTTPMessageConfigInterface hcc;
	
	public HTTPWebRequest(HTTPMessageConfigInterface hcc)
	{
		SharedUtil.checkIfNulls("Null HTTPCallConfigInterface", hcc);
		this.hcc = hcc;
	}

	/**
	 *
	 * @param callBack
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 * @throws RequestException
	 */
	public void send(RequestCallback callBack)
        throws NullPointerException, IllegalArgumentException, RequestException
	{
		ZWRequestBuilder builder = new ZWRequestBuilder(hcc.getMethod(), URL.encode(formatFullURL(hcc)));

		for (GetNameValue<String> gnvHeader : hcc.getHeaders().asArrayValuesString().values())
		{
			builder.setHeader(gnvHeader.getName(), gnvHeader.getValue());
		}

		builder.setTimeoutMillis(hcc.getReadTimeout());
		String data = null;

		if (hcc.getContent() != null && hcc.getContent().length > 0)
		{
			data = SharedStringUtil.toString(hcc.getContent());
		}
		
		GetNameValue<String> authorizationHeader = null;//HTTPAuthScheme.BASIC.toHTTPHeader(hcc.getUser(), hcc.getPassword());

		if (hcc.getAuthorization() != null)
		{
			authorizationHeader = hcc.getAuthorization().toHTTPHeader();
		}

		if (authorizationHeader != null)
		{
			builder.setHeader(authorizationHeader.getName(), authorizationHeader.getValue());
		}
		
		builder.sendRequest(data, callBack);	
	}

	/**
	 *
	 * @param hcc
	 * @return
	 */
	public static String formatFullURL(final HTTPMessageConfigInterface hcc)
	{
		if (hcc.getURL() == null)
		{
			hcc.setURL(GWT.getModuleBaseURL());
		}
		
		String fullURL = SharedStringUtil.concat(hcc.getURL(), hcc.getURI(), "/");
		ArrayValues<GetNameValue<String>> params = hcc.getParameters().asArrayValuesString();
		String parameters =  hcc.getHTTPParameterFormatter().format(null, params.values());
				//SharedStringUtil.format(hcc.getParameters(), "=", false, "&");

		if (!SharedStringUtil.isEmpty(parameters))
		{
			switch(hcc.getHTTPParameterFormatter())
			{
			case URI_REST_ENCODED:
				fullURL = SharedStringUtil.concat(fullURL, parameters, "/");
				break;
			case URL_ENCODED:
				fullURL += "?" + parameters;
				break;
            case HEADER:
              break;
			}
		}
		
		return fullURL;
	}

	
//	public static  RequestBuilder.Method toMethod(HTTPMethod httpMethod) 
//		throws NullPointerException, IllegalArgumentException
//	{
//		
//		switch(httpMethod)
//		{
//		case DELETE:
//			return RequestBuilder.DELETE;		
//		case GET:
//			return RequestBuilder.GET;
//		case HEAD:
//			return RequestBuilder.HEAD;
//		case POST:
//			return RequestBuilder.POST;
//		case PUT:
//			return RequestBuilder.PUT;
//			
//			
//		default:
//			throw new IllegalArgumentException("Unsupported method " + httpMethod);
//		}		
//	}

}