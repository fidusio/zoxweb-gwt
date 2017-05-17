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
import org.zoxweb.shared.security.CRUDManager;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVLongWidget
	extends NVBaseWidget<Long>
{
	private LongBox longBox = new LongBox();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NVLongWidget(CRUDManager crudManager, NVConfig nvConfig)
	{
		super(crudManager, nvConfig);
		
		if (!nvConfig.isEditable())
		{
			longBox.setReadOnly(true);
		}
		
		if (nvConfig.getValueFilter() != null && !nvConfig.isArray())
		{
			textWidgetController = new NVTextWidgetController(longBox, nvConfig.getValueFilter(), null);
		}
		else
		{
			textWidgetController = new NVTextWidgetController(longBox, FilterType.LONG, null);
		}
		
		initWidget(longBox);
		longBox.setName(nvConfig.getName());
	}

	@Override
	public Widget getWidget() 
	{
		return longBox;
	}
	
	@Override
	public void setWidgetValue(Long value) 
	{
		longBox.setValue(value);
	}

	@Override
	public Long getWidgetValue() 
	{
		
		textWidgetController.setStyle(true);
		
		if (nvConfig.isMandatory() && WidgetUtil.isNull(longBox))
		{
			textWidgetController.setStyle(false);
			throw new NullPointerException("Empty value:" + nvConfig);	
		}
		else
		{
			longBox.setValue(longBox.getValue());
		}
		
		if (longBox.getValue() == null)
		{
			textWidgetController.setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig);
		}
		
		
		
//		if (nvConfig.isMandatory() && SharedStringUtil.isEmpty(longBox.getText()))
//		{
//			textWidgetController.setStyle(false);
//			throw new NullPointerException("Empty value:" + nvConfig);	
//		}
//		
//		String value = longBox.getText();
//		
//		if (longBox.getText().contains(","))
//		{
//			value = longBox.getText().replaceAll(",", "");
//		}
//		
//		if (!textWidgetController.getValueFilter().isValid(value))
//		{
//			textWidgetController.setStyle(false);
//			throw new IllegalArgumentException("Invalid value:" + nvConfig);
//		}
		
		return longBox.getValue();
	}

	@Override
	public void setWidgetValue(String value) 
	{
		setWidgetValue(Long.parseLong(value));
		
	}
	
	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		longBox.setEnabled(!readOnly);		
	}

	@Override
	public void clear() 
	{
		longBox.setText("");
	}
	
}