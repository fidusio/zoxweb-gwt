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
package org.zoxweb.client.data.events;

import org.zoxweb.shared.util.CRUDNVEntity;

import com.google.gwt.event.shared.GwtEvent;

public class CRUDNVEntityEvent
	extends GwtEvent<CRUDNVEntityHandler>
{
	public final static Type<CRUDNVEntityHandler> TYPE = new Type<CRUDNVEntityHandler>();

	private final CRUDNVEntity crudNVEntity;
	
	public CRUDNVEntityEvent(CRUDNVEntity crudNVEntity)
	{
		this.crudNVEntity = crudNVEntity;
	}

	@Override
	public Type<CRUDNVEntityHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(CRUDNVEntityHandler handler)
	{
		handler.applyCRUD(crudNVEntity.getCRUD(), crudNVEntity.getNVEntity());
	}
	
}