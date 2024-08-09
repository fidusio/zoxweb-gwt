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

import org.zoxweb.shared.filters.FilterType;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVFloatWidget 
	extends NVBaseWidget<Float>
	implements KeyPressHandler
{

	private DoubleBox doubleBox = new DoubleBox();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NVFloatWidget(NVConfig nvConfig)
	{
		super(nvConfig);
		
		if (!nvConfig.isEditable())
		{
			doubleBox.setReadOnly(true);
		}
		
		if (nvConfig.getValueFilter() != null && !nvConfig.isArray())
		{
			textWidgetController = new NVTextWidgetController(doubleBox, nvConfig.getValueFilter(), this);
		}
		else
		{
			textWidgetController = new NVTextWidgetController(doubleBox, FilterType.FLOAT, this);
		}
		
		initWidget(doubleBox);
		doubleBox.setName(nvConfig.getName());
	}
	
	@Override
	public Widget getWidget() 
	{
		return doubleBox;
	}
	
	@Override
	public void setWidgetValue(Float value) 
	{
		if (value != null)
		{
			String temp = String.valueOf(value);
			doubleBox.setValue(Double.valueOf(temp));
		}
		else
		{
			doubleBox.setValue(null);
		}
	}

	@Override
	public Float getWidgetValue() 
	{
		textWidgetController.setStyle(true);
		
		if (nvConfig.isMandatory() && WidgetUtil.isNull(doubleBox))
		{
			textWidgetController.setStyle(false);
			throw new NullPointerException("Empty value:" + nvConfig);	
		}
		else
		{
			doubleBox.setValue(doubleBox.getValue());
		}
		
		if (doubleBox.getValue() == null)
		{
			textWidgetController.setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig);
		}

		return Float.valueOf(doubleBox.getText());

	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) 
	{
		String input = doubleBox.getText();
		
		if (event.getCharCode() == '.')
		{
			input += ".0";
		}
		else
		{
			input += event.getCharCode();
		}
		
		if (!FilterType.FLOAT.isValid(input))
		{
			doubleBox.cancelKey();
		}
	}

	@Override
	public void setWidgetValue(String value) 
	{
		setWidgetValue(Float.parseFloat(value));
		
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		doubleBox.setEnabled(!readOnly);
	}

	@Override
	public void clear() 
	{
		doubleBox.setText("");
	}
	
}