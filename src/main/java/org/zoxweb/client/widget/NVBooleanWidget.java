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

import org.zoxweb.shared.security.CRUDManager;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVBooleanWidget
	extends NVBaseWidget<Boolean>
{
	
	private CheckBox checkBox = new CheckBox();
	
	public NVBooleanWidget(CRUDManager crudManager, NVConfig nvConfig) 
	{
		super(crudManager, nvConfig);
		
		if (!nvConfig.isEditable())
		{
			checkBox.setEnabled(false);
		}
		
		initWidget(checkBox);
		checkBox.setName(nvConfig.getName());
	}

	@Override
	public Widget getWidget() 
	{
		return checkBox;
	}
	
	@Override
	public void setWidgetValue(Boolean value) 
	{
		checkBox.setValue(value);
	}

	@Override
	public Boolean getWidgetValue() 
	{
		return checkBox.getValue();
	}

	@Override
	public void setWidgetValue(String value)
	{
		checkBox.setValue(Boolean.parseBoolean(value));
	}

	@Override
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
		
		checkBox.setEnabled(!readOnly);
	}


	@Override
	public void clear() 
	{
		checkBox.setValue(false);
	}
	
}