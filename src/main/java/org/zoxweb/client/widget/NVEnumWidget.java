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

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVEnumWidget
	extends NVBaseWidget<Enum<?>>
{
	private ListBox listBox = new ListBox();
	private boolean titleIncluded = true;
	private String listTitle = "Select:";
	
	public NVEnumWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVEnumWidget(NVConfig nvc, boolean titleIncluded) 
	{
		super(null, nvc);
		
		if (!nvc.isEditable())
		{
			listBox.setEnabled(false);
		}
		
		if (!nvc.isEnum())
		{
			throw new IllegalArgumentException("Not enum type.");
		}
		
		Enum<?>[] enumValues = (Enum<?>[]) nvc.getMetaTypeBase().getEnumConstants();
		
		this.titleIncluded = titleIncluded;
		
		if (titleIncluded)
		{
			listBox.addItem(listTitle);
		}
		
		for (Enum<?> value : enumValues)
		{			
			listBox.addItem(value.name());
		}
		
		initWidget(listBox);
		
		listBox.setName(nvc.getName());
	}

	@Override
	public Widget getWidget() 
	{
		return listBox;
	}
	
	@Override
	public void setWidgetValue(Enum<?> value)
	{
		if (value == null)
		{
			listBox.setSelectedIndex(0);
		}
		else
		{
			for (int i = 1; i < listBox.getItemCount(); i++)
			{
				if (listBox.getValue(i).equals(value.name()))
				{
					listBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	@Override
	public Enum<?> getWidgetValue()
	{	
		if (listBox.getSelectedIndex() != 0)
		{
			return SharedUtil.enumValue(getNVConfig().getMetaType(), listBox.getValue(listBox.getSelectedIndex()));
		}
		else if (getNVConfig().isMandatory())
		{
			throw new NullPointerException("Empty value: " + getNVConfig());
		}

		return null;
	}

	@Override
	public void setWidgetValue(String value) 
	{
		setWidgetValue(SharedUtil.enumValue(getNVConfig().getMetaType(), value));
	}
	
	public void setListTitle(String title)
	{
		listTitle = title;
		//listBox.setItemText(0, title);
		setTitleIncluded(true);
	}
	
	public void setTitleIncluded(boolean titleIncluded)
	{
		this.titleIncluded = titleIncluded;
		
		refresh();
	}
	
	public boolean isTitleIncluded()
	{
		return titleIncluded;
	}
	
	private void refresh()
	{
		Enum<?>[] enumValues = (Enum<?>[]) getNVConfig().getMetaTypeBase().getEnumConstants();
		
		listBox.clear();
		
		if (titleIncluded)
		{
			listBox.addItem(listTitle);
		}
		
		for (Enum<?> value : enumValues)
		{			
			listBox.addItem(value.name());
		}
	}
	
	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		listBox.setEnabled(!readOnly);
	}
	
	@Override
	public void clear() 
	{
		listBox.setSelectedIndex(0);
	}
	
}