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
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVPasswordWidget 
	extends NVBaseWidget<String>
{
	private PasswordTextBox passwordTextBox = new PasswordTextBox();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NVPasswordWidget(NVConfig nvConfig)
	{
		super(nvConfig);
		
		if (!nvConfig.isEditable())
		{
			passwordTextBox.setReadOnly(true);
		}
		
		if (nvConfig.getValueFilter() != null)
		{
			textWidgetController = new NVTextWidgetController(passwordTextBox, nvConfig.getValueFilter(), null);
		}
		else
		{
			textWidgetController = new NVTextWidgetController(passwordTextBox, FilterType.PASSWORD, null);
		}
		
		
		initWidget(passwordTextBox);
		passwordTextBox.setName(nvConfig.getName());
	}

	@Override
	public Widget getWidget() 
	{
		return passwordTextBox;
	}
	
	@Override
	public void setWidgetValue(String value) 
	{
		passwordTextBox.setText(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getWidgetValue() 
	{
		textWidgetController.setStyle(true);

		if (nvConfig.isMandatory() && SharedStringUtil.isEmpty(passwordTextBox.getText()))
		{
			textWidgetController.setStyle(false);
			throw new NullPointerException("Empty value:" + nvConfig);
		}
		
		if (!textWidgetController.getValueFilter().isValid(passwordTextBox.getText()))
		{
			textWidgetController.setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig);
		}
		
		return passwordTextBox.getValue();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		passwordTextBox.setEnabled(!readOnly);
		
	}
	
	@Override
	public void clear() 
	{
		passwordTextBox.setText("");
	}
	
}