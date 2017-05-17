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

import org.zoxweb.shared.data.events.ValueSelectionListener;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;

public class CustomAnchorController<V>
    implements ClickHandler
{
	
	private Anchor anchor;
	private V value;
	private ValueSelectionListener<V> valueSelectionListener;
	private HandlerRegistration clickHandlerRegistration;
	
	public CustomAnchorController(ValueSelectionListener<V> valueSelectionListener, String text, V value)
    {
		this();
		addValueSelectionListener(valueSelectionListener);
		setAnchorText(text);
		setValue(value);
	}
	
	public CustomAnchorController()
    {
		 anchor = new Anchor();
		 anchor.getElement().getStyle().setCursor(Cursor.POINTER);
		 
		 clickHandlerRegistration = anchor.addClickHandler(this);
	}
	
	public Anchor getAnchor()
    {
		return anchor;
	}
	
	public void setAnchorText(String text)
    {
		anchor.setText(text);
	}

	public V getValue()
    {
		return value;
	}
	
	public void setValue(V value)
    {
		this.value = value;
	}

	public void addValueSelectionListener(ValueSelectionListener<V> valueSelectionListener)
    {
		this.valueSelectionListener = valueSelectionListener;
	}
	
	public void destroy()
    {
		if (clickHandlerRegistration != null)
		{
			clickHandlerRegistration.removeHandler();
		}
	}
	
	@Override
	public void onClick(ClickEvent event)
    {
		if (valueSelectionListener != null)
		{
			valueSelectionListener.selectedValue(getValue());
		}
	}

}