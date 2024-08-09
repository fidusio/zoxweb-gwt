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

import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVIntegerWidget
	extends NVBaseWidget<Integer>
{
	private IntegerBox integerBox = new IntegerBox();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NVIntegerWidget(NVConfig nvConfig)
	{
		super(nvConfig);
		
		if (!nvConfig.isEditable())
		{
			integerBox.setReadOnly(true);
		}
		
		if (nvConfig.getValueFilter() != null && !nvConfig.isArray())
		{
			textWidgetController = new NVTextWidgetController(integerBox, nvConfig.getValueFilter(), null);
		}
		else
		{
			textWidgetController = new NVTextWidgetController(integerBox, FilterType.INTEGER, null);
		}
		
		initWidget(integerBox);
		integerBox.setName(nvConfig.getName());
	}

	@Override
	public Widget getWidget() 
	{
		return integerBox;
	}
	
	@Override
	public void setWidgetValue(Integer value) 
	{
		integerBox.setValue(value);		
	}

	@Override
	public Integer getWidgetValue()
	{
		textWidgetController.setStyle(true);
		
		if (nvConfig.isMandatory() && WidgetUtil.isNull(integerBox))
		{
			textWidgetController.setStyle(false);
			throw new NullPointerException("Empty value:" + nvConfig);	
		}
		else
		{
			integerBox.setValue(integerBox.getValue());
		}
		
		if (integerBox.getValue() == null)
		{
			textWidgetController.setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig);
		}
		
		return integerBox.getValue();
	}

	@Override
	public void setWidgetValue(String value) 
	{
		setWidgetValue(Integer.parseInt(value));	
	}
	
	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		integerBox.setEnabled(!readOnly);
	}
	
	@Override
	public void clear() 
	{
		integerBox.setText("");
	}
	
}