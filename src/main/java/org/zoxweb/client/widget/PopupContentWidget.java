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

import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupContentWidget
	extends Composite
{

	private static PopupContentWidgetUiBinder uiBinder 
		= GWT.create(PopupContentWidgetUiBinder.class);
	
	interface PopupContentWidgetUiBinder 
		extends UiBinder<Widget, PopupContentWidget> 
	{
	}
	
	@UiField DockPanel dpHeader;
	@UiField HTML hTitle;
	@UiField ScrollPanel spContent;
	@UiField VerticalPanel vpContent;

	private PopupPanel popup;
	private Anchor close = new Anchor("X");

	public PopupContentWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		spContent.setAlwaysShowScrollBars(true);
		
		popup = new PopupPanel(false);
		popup.setModal(true);
		popup.setStyleName(WidgetConst.CSSStyle.POPUP.getName());
		popup.setWidget(this);
		
		close.setTitle("Close");
		close.setSize("1.5EM", "1.5EM");
		dpHeader.add(close, DockPanel.EAST);
		
		close.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				hide();
			}
			
		});
	}

	private void setTitleWidget(String title, Widget widget)
	{
		hTitle.setHTML(title);
		spContent.clear();
		spContent.add(widget);
	}
	
	public void showPopup(String title, Widget widget)
	{
		popup.setTitle(title);
		setTitleWidget(title, widget);
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
	
	public void setPopupSize(int width, int height)
	{
		vpContent.setSize(width + "EM", height + "EM");
		spContent.setHeight((height - 2) + "EM");
	}

	public void showScrollBars(boolean show)
	{
		spContent.setAlwaysShowScrollBars(show);
	}
}
