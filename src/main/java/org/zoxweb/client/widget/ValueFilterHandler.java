/*
 * Copyright (c) 2012-2014 ZoxWeb.com LLC.
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

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.shared.filters.ValueFilter;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ValueBoxBase;

/**
 * 
 * @author mzebib
 *
 * @param <V>
 */
public class ValueFilterHandler<V>
	implements KeyUpHandler, FocusHandler

{
	
	private ValueBoxBase<V> widget;
	private String defaultStyleName;
	private String errorStyleName;
	private ValueFilter<V,V> vf;
	private List<HandlerRegistration> hrList;
	
	/**
	 * 
	 * @param widget
	 * @param vf
	 * @param errorStyleName
	 */
	public ValueFilterHandler(ValueBoxBase<V>  widget, ValueFilter <V,V> vf, String errorStyleName)
	{
		this.widget = widget;
		defaultStyleName = widget.getStyleName();
		this.errorStyleName = errorStyleName;
		this.vf = vf;
		
		hrList = new ArrayList<HandlerRegistration>();
		
		hrList.add(widget.addKeyUpHandler(this));
		hrList.add(widget.addFocusHandler(this));
	}
	
	@Override
	public void onKeyUp(KeyUpEvent event) 
	{
		isValid();
	}
	
	@Override
	public void onFocus(FocusEvent event) 
	{
		isValid();		
	}
	
	public boolean isValid()
	{
		if (vf != null)
		{
			if (!vf.isValid(widget.getValue()))
			{
				widget.setStyleName(errorStyleName);
				return false;
			}
			else
			{
				widget.setStyleName(defaultStyleName);
			}
		}
		
		return true;
	}
	
	public void applyStyle(boolean defaultStyle)
	{
		if (defaultStyle)
		{
			widget.setStyleName(defaultStyleName);
		}
		else
		{
			widget.setStyleName(errorStyleName);
		}
	}
	
	public V getValue()
	{
		V value = widget.getValue();
		
		if (vf != null)
		{
			return vf.validate(widget.getValue());
		}
		
		return value;
	}

	
	public void readdHandlers()
	{
		clear();

		hrList.add(widget.addKeyUpHandler(this));
		hrList.add(widget.addFocusHandler(this));		
	}
	
	public void clear()
	{
		for (HandlerRegistration hr : hrList)
		{
			if (hr != null)
			{
				hr.removeHandler();
			}
		}
		
		hrList.clear();
	}


}