/*
 * Copyright (c) 2012-Oct 2, 2015 ZoxWeb.com LLC.
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
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author mzebib
 *
 */
@SuppressWarnings("serial")
public class NVStringEmailWidget
	extends NVBaseWidget<String>
{
	
	private TextBox textBox = new TextBox();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NVStringEmailWidget(CRUDManager crudManager, NVConfig nvConfig)
	{
		super(crudManager, nvConfig);
		
		if (!nvConfig.isEditable())
		{
			textBox.setReadOnly(true);
		}
		
		textWidgetController = new NVTextWidgetController(textBox, FilterType.CLEAR, null);
		
		initWidget(textBox);
		textBox.setName(nvConfig.getName());
	}

	@Override
	public Widget getWidget() 
	{
		return textBox;
	}

	@Override
	public void setWidgetValue(String value) 
	{
		textBox.setText(value);
	}
	
	@Override
	public String getWidgetValue() 
	{
		textWidgetController.setStyle(true);
	
		if (nvConfig.isMandatory() && SharedStringUtil.isEmpty(textBox.getText()))
		{
			textWidgetController.setStyle(false);
			throw new NullPointerException("Empty value:" + nvConfig);
		}
		
		if (!FilterType.EMAIL.isValid(textBox.getText()))
		{
			textWidgetController.setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig);
		}
		
		return textBox.getValue();
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		textBox.setEnabled(!readOnly);
	}
	
	@Override
	public void clear()
	{
		textBox.setText("");
	}
	
}