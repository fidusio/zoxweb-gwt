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
package org.zoxweb.client.controller;

import org.zoxweb.client.data.events.AddControllerHandler;
import org.zoxweb.client.rpc.GenericRequestHandler;
import org.zoxweb.shared.data.DataConst.APIParameters;
import org.zoxweb.shared.http.HTTPMessageConfig;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.http.HTTPMethod;
import org.zoxweb.shared.http.HTTPMimeType;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.Const.ReturnType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class NVEntityArrayController 
	implements AddControllerHandler<String, NVEntity>
{
	private String uri = null;
	// uri = FSConst.URIs.API_DATA_MANAGER.getValue();
	
	@Override
	public void actionAdd(String v, AsyncCallback<NVEntity> callback)
	{
		HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(null, uri, HTTPMethod.POST);
		hcc.setContentType(HTTPMimeType.APPLICATION_JSON);
		hcc.getParameters().add(new NVPair(APIParameters.CLASS_NAME.getName(), v));
		
		new GenericRequestHandler<NVEntity>(hcc, ReturnType.NVENTITY, callback);	
	}

}