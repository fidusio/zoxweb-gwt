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

import java.math.BigDecimal;

import org.zoxweb.shared.filters.FilterType;

import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVBigDecimalWidget 
	extends NVBaseWidget<BigDecimal>
	implements KeyPressHandler
{
	private TextBox textBox = new TextBox();
	
	public NVBigDecimalWidget() 
	{
		this(null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NVBigDecimalWidget(NVConfig nvc)
	{
		super(nvc, true);
		
		if (nvc != null && !nvc.isEditable())
		{
			textBox.setReadOnly(true);
		}
		
		if (nvc != null && nvc.getValueFilter() != null && !nvc.isArray())
		{
			textWidgetController = new NVTextWidgetController(textBox, nvc.getValueFilter(), this);
		}
		else
		{
			textWidgetController = new NVTextWidgetController(textBox, FilterType.BIG_DECIMAL, this);
		}
		
		initWidget(textBox);
		
		if (nvc != null)
		{
			textBox.setName(nvc.getName());
		}
	}

	@Override
	public Widget getWidget() 
	{
		return textBox;
	}

	@Override
	public void setWidgetValue(BigDecimal value) 
	{
		if (value != null)
		{
			textBox.setValue("" + value);
		}
		else
		{
			textBox.setValue(null);
		}
	}

	@Override
	public BigDecimal getWidgetValue() 
	{
		textWidgetController.setStyle(true);
		
		if (nvConfig != null && nvConfig.isMandatory() && WidgetUtil.isNull(textBox))
		{
			textWidgetController.setStyle(false);
			throw new NullPointerException("Empty value:" + nvConfig);	
		}
		else
		{
			textBox.setValue(textBox.getValue());
		}
		
		if (SharedStringUtil.isEmpty(textBox.getValue()) || WidgetUtil.isNull(textBox))
		{
			textWidgetController.setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig);
		}
		
		return new BigDecimal(textBox.getValue());
	}


	@Override
	public void onKeyPress(KeyPressEvent event) 
	{
		String input = textBox.getValue();
		
		if (event.getCharCode() == '.')
		{
			input += ".0";
		}
		else
		{
			input += event.getCharCode();
		}
		
		if (!FilterType.BIG_DECIMAL.isValid(input))
		{
			textBox.cancelKey();
		}
	}


	@Override
	public void setWidgetValue(String value) 
	{
		textBox.setValue(value);
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