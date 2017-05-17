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

import java.util.Date;

import org.zoxweb.shared.security.CRUDManager;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

@SuppressWarnings("serial")
public class NVDateWidget 
	extends NVBaseWidget<Long>
{
	
	private DateBox dateBox = new DateBox();
	
	public NVDateWidget(CRUDManager crudManager, NVConfig nvConfig) 
	{
		super(crudManager, nvConfig);
		
		if (!nvConfig.isEditable())
		{
			dateBox.setEnabled(false);
		}
		
		initWidget(dateBox);
		// We need to set the name of the DateBox.
	}

	@Override
	public Widget getWidget() 
	{
		return dateBox;
	}

	@Override
	public void setWidgetValue(Long value) 
	{
		if (value != null)
		{
			dateBox.setValue(new Date(value));
		}
		else
		{
			dateBox.setValue(null);
		}
	}

	@Override
	public Long getWidgetValue() 
	{
		return dateBox.getValue().getTime();
	}

	@Override
	public void setWidgetValue(String value) 
	{
		dateBox.setValue(new Date(Long.parseLong(value)));
		
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		dateBox.setEnabled(!readOnly);
	}
	
	@Override
	public void clear() 
	{
		dateBox.setValue(new Date(System.currentTimeMillis()));
	}
	
}