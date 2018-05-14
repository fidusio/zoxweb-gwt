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

import org.zoxweb.client.data.JSONClientUtil;
import org.zoxweb.shared.api.APIError;
import org.zoxweb.shared.api.APIException;
import org.zoxweb.shared.data.NVEntityFactory;
import org.zoxweb.shared.data.ZWDataFactory;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.http.HTTPHeaderName;
import org.zoxweb.shared.http.HTTPHeaderValue;
import org.zoxweb.shared.http.HTTPStatusCode;
import org.zoxweb.shared.util.Const.ReturnType;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.NVEntityInstance;
import org.zoxweb.shared.util.QuickLZ;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author mnael
 * @param <T> The type of request
 */
public class GenericRequestHandler<T>
	implements RequestCallback
{
	
	public static NVEntityFactory DEFAULT_FACTORY = ZWDataFactory.SINGLETON;
	
	private NVEntityFactory nveFactory;
	private HTTPMessageConfigInterface httpCallConfig;
	private AsyncCallback<T> asyncCallBack;
	private NVEntityInstance nveInstance;
	private ReturnType returnType = ReturnType.NVENTITY;
	
	public GenericRequestHandler(HTTPMessageConfigInterface hcc, NVEntityInstance nvei, ReturnType retType, boolean autoSend, AsyncCallback<T> callBack)
	{
		asyncCallBack = callBack;
		httpCallConfig = hcc;
		nveInstance = nvei;
		this.returnType = retType != null ? retType : ReturnType.VOID;

		if (autoSend)
		{
			sendRequest(httpCallConfig);
		}
	}
	
	public GenericRequestHandler(HTTPMessageConfigInterface hcc, NVEntityInstance nvei, ReturnType retType, AsyncCallback<T> callBack)
	{
		this(hcc, nvei, retType,  true, callBack);		
	}
	
	public GenericRequestHandler(HTTPMessageConfigInterface hcc, ReturnType retType, AsyncCallback<T> callBack)
	{
		this(hcc, null, retType, true, callBack);
	}
	
	public void sendRequest(HTTPMessageConfigInterface hci)
	{
		if (hci == null)
		{
			hci = httpCallConfig;
		}

		HTTPWebRequest webRequest = new HTTPWebRequest(hci);

		try
		{
			webRequest.send(this);
		}
		catch (RequestException e)
		{
			if (asyncCallBack != null)
			{
				asyncCallBack.onFailure(e);
			}
			else
			{
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onResponseReceived(Request request, Response response)
	{
		if (response.getStatusCode() == HTTPStatusCode.OK.CODE)
		{
			try
			{
				Object value = null;
				
				if (!SharedStringUtil.isEmpty(getResponseContent(response)))
				{
					switch(returnType)
					{
					case BOOLEAN:
						value = Boolean.parseBoolean(getResponseContent(response));
						break;
					case DOUBLE:
						value = Double.parseDouble(getResponseContent(response));
						break;
					case FLOAT:
						value = Float.parseFloat(getResponseContent(response));
						break;
					case INTEGER:
						value = Integer.parseInt(getResponseContent(response));
						break;
					case LONG:
						value = Long.parseLong(getResponseContent(response));
						break;
					case NVENTITY:
						NVEntity nve = JSONClientUtil.fromJSON(nveInstance != null ? nveInstance.newInstance() : null, getResponseContent(response), getNVEFactory());
						value = nve;
						break;
					case NVENTITY_LIST:
						value = JSONClientUtil.fromJSONValues(getResponseContent(response), getNVEFactory());
						break;
					case NVENTITY_ARRAY:
						value = JSONClientUtil.fromJSONArray(getResponseContent(response), getNVEFactory());
						break;
					case STRING:
						value = getResponseContent(response);
						break;
					case MAP:
						value = JSONClientUtil.fromJSONMap(getResponseContent(response), getNVEFactory());
						break;
					case DYNAMIC_ENUM_MAP:
						value = JSONClientUtil.fromJSONDynamicEnumMap(getResponseContent(response));
						break;
					case DYNAMIC_ENUM_MAP_LIST:
						value = JSONClientUtil.fromJSONDynamicEnumMapList(getResponseContent(response));
						break;
					case NVGENERIC_MAP:
						value = JSONClientUtil.fromJSONGenericMap(getResponseContent(response), getNVEFactory());
						break;
					default:
						value = null;
						break;
					}
				}
				
				
				//NVEntity nve = JSONClientUtil.fromJSON(nveInstance != null ? nveInstance.newInstance() : null, getResponseContent(response), getNVEFactory());
				
				if (asyncCallBack != null)
				{
					asyncCallBack.onSuccess((T) value);
				}

				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				
				if (asyncCallBack != null)
				{
					asyncCallBack.onFailure(e);
				}
			}
		}
		else
		{
			// we must have an error
			
			try
			{
				Throwable throwable = null;
				
				if (!SharedStringUtil.isEmpty(getResponseContent(response)))
				{
					try
					{
						APIError apiError = JSONClientUtil.fromJSON(null, getResponseContent(response), getNVEFactory());
						throwable = apiError.toException();
					}
					catch(Exception e)
					{
						
					}
				}
				if (throwable == null)
				{
					//	This could indicate no content.
					throwable = new APIException("No content");
				}

				if (asyncCallBack != null)
				{
					asyncCallBack.onFailure(throwable);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static String getResponseContent(Response response)
	{

		if (response != null)
		{
			String ret = response.getText();

			if (SharedStringUtil.contains(response.getHeader(HTTPHeaderName.CONTENT_ENCODING.getName()), HTTPHeaderValue.CONTENT_ENCODING_LZ, true))
			{
				byte data[] = SharedBase64.decode(SharedStringUtil.getBytes(ret));
				byte unzipped[] = QuickLZ.decompress(data);
				ret = SharedStringUtil.toString(unzipped);
			}

			return ret;
		}
		
		return null;
	}

	@Override
	public void onError(Request request, Throwable exception)
	{
		if (asyncCallBack != null)
		{
			asyncCallBack.onFailure(exception);
		}
	}

	/**
	 * @return the nveFactory
	 */
	public NVEntityFactory getNVEFactory()
	{
		return nveFactory != null ? nveFactory : DEFAULT_FACTORY;
	}
	
	public void setNVEFactory(NVEntityFactory factory)
	{
		nveFactory = factory;
	}

	/**
	 * @return the httpCallConfig
	 */
	public HTTPMessageConfigInterface getHTTPCallConfig()
	{
		return httpCallConfig;
	}

	/**
	 * @param httpCallConfig the httpCallConfig to set
	 */
	public void setHTTPCallConfig(HTTPMessageConfigInterface httpCallConfig)
	{
		this.httpCallConfig = httpCallConfig;
	}

	/**
	 * @return the asyncCallBack
	 */
	public AsyncCallback<T> getAsyncCallBack()
	{
		return asyncCallBack;
	}

	/**
	 * @return the nveInstance
	 */
	public NVEntityInstance getNVEInstance()
	{
		return nveInstance;
	}

	/**
	 * @param nveInstance the nveInstance to set
	 */
	public void setNVEInstance(NVEntityInstance nveInstance)
	{
		this.nveInstance = nveInstance;
	}

}