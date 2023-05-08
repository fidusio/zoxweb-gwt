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

import java.util.HashMap;
import java.util.Map;

import org.zoxweb.client.data.JSONClientUtil;
import org.zoxweb.client.data.events.SaveControllerHandler;
import org.zoxweb.client.rpc.GenericRequestHandler;
import org.zoxweb.shared.data.DataConst.APIParameters;
import org.zoxweb.shared.http.HTTPMessageConfig;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.http.HTTPMethod;
import org.zoxweb.shared.http.HTTPMediaType;
import org.zoxweb.shared.util.Const.ReturnType;
import org.zoxweb.shared.util.DynamicEnumMap;
import org.zoxweb.shared.util.MetaToken;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DynamicEnumMapSaveController 
	implements SaveControllerHandler<DynamicEnumMap, DynamicEnumMap>
{

	@Override
	public void actionSave(DynamicEnumMap dem, AsyncCallback<DynamicEnumMap> callback)
	{
		HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(null, "api" /*FSConst.URIs.API_DYNAMIC_ENUM_MAP.getValue()*/, HTTPMethod.PATCH);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(MetaToken.NAME.getName(), dem.getName());
		map.put(APIParameters.NVPAIR_LIST.getName(), dem.getValue());
		
		hcc.setContentType(HTTPMediaType.APPLICATION_JSON);
		hcc.setContent(JSONClientUtil.toJSONMap(map).toString());
		
		new GenericRequestHandler<DynamicEnumMap>(hcc, ReturnType.DYNAMIC_ENUM_MAP, callback);
	}

}