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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class PopupUtil 
{
	
	public static final PopupUtil SINGLETON = new PopupUtil();
	private PopupPanel popup = new PopupPanel(false);
	private PopupContent popupContent = new PopupContent();
	
	private PopupUtil()
	{
		popupContent.addCloseButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				popup.hide();
			}
		});
		
		popup.setModal(true);
		popup.setGlassEnabled(true);
		popup.setWidget(popupContent);
	}
	
	public void showPopup(String title, String msg)
	{
		popup.setTitle(title);
		popupContent.setTitleMessage(title, msg);
		popup.center();
	}
	
	
	public void showPopup(String title, Image image)
	{
		popup.setTitle(title);
		popupContent.setTitleImage(title, image);
		popup.center();
	}
	
	
	public void hide()
	{
		popup.hide();
	}
	
	public PopupPanel getPopupPanel()
	{
		return popup;
	}
	
	public PopupContent getPopupContent()
	{
		return popupContent;
	}
	
}