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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ScrollPanel;

public class PopupContent 
	extends Composite
{

	private static PopupContentUiBinder uiBinder 
		= GWT.create(PopupContentUiBinder.class);
	
	interface PopupContentUiBinder 
		extends UiBinder<Widget, PopupContent> 
	{
	}
	
	@UiField Label labelHeader;
	@UiField HTML hMessage;
	@UiField HorizontalPanel hpControl;
	@UiField Label labelClose;
	@UiField ScrollPanel spContent;

	public PopupContent() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		labelClose.setTitle("Close");
	}
	
	public void setTitleMessage(String header, String message)
	{
		labelHeader.setText(header);
		hMessage.setHTML( message);
		spContent.clear();
		spContent.add(hMessage);
		hpControl.setVisible(true);
	}
	
	public void setTitleImage(String header, Image image)
	{
		labelHeader.setText(header);
		spContent.clear();
		hpControl.setVisible(false);
		image.setPixelSize(100, 100);
		spContent.add(image);
	}

	public void addButtons(Widget widget)
	{
		hpControl.add(widget);
	}
	
	public void setCloseButtonVisible(boolean value)
	{
		labelClose.setVisible(value);
	}
	
	public void addCloseButtonClickHandler(ClickHandler clickHandler)
	{
		labelClose.addClickHandler(clickHandler);
	}
	
}