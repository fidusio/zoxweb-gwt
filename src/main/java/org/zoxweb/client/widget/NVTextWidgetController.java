/*
 * Copyright (c) 2012-2015 ZoxWeb.com LLC.
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
package org.zoxweb.client.widget;

import org.zoxweb.shared.filters.ValueFilter;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ValueBoxBase;

public class NVTextWidgetController<T>
	implements KeyPressHandler
{
	
	private ValueBoxBase<T> valueBox;
	private ValueFilter<Object, Object> vf;
	private String defaultStyleName;
	private HandlerRegistration handlerRegistration;
	
	public NVTextWidgetController(ValueBoxBase<T> valueBox, ValueFilter<Object, Object> vf, KeyPressHandler keyPressHandler)
		throws NullPointerException
	{
		SharedUtil.checkIfNulls("Null values.", valueBox, vf);
		
		this.valueBox = valueBox;
		this.vf = vf;
		defaultStyleName = valueBox.getStyleName();
		
		if (keyPressHandler != null)
		{
			handlerRegistration = valueBox.addKeyPressHandler(keyPressHandler);
		}
		else
		{
			handlerRegistration = valueBox.addKeyPressHandler(this);
		}
		
	}

	@Override
	public void onKeyPress(KeyPressEvent event) 
	{
		if (vf != null && event.getCharCode() != '\0')
		{
			String input = "" + event.getCharCode();
			
			if (!vf.isValid(input))
			{					
				valueBox.cancelKey();
			}
		}
		
	}
	
	public String getDefaultStyleName()
	{
		return defaultStyleName;
	}
	
	public ValueFilter<Object, Object> getValueFilter()
	{
		return vf;
	}
	
	public void setValueFilter(ValueFilter<Object, Object> vf)
	{
		this.vf = vf;
		
		if (handlerRegistration != null)
		{
			handlerRegistration.removeHandler();
		}
		
		handlerRegistration = valueBox.addKeyPressHandler(this);
	}
	
	public void setStyle(boolean valid)
	{
		if (valid)
		{
			valueBox.setStyleName(defaultStyleName);
		}
		else
		{
			valueBox.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
		}
	}
	
}