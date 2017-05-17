/*
 * Copyright (c) 2012-Dec 18, 2015 ZoxWeb.com LLC.
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

import java.util.HashMap;
import java.util.List;

import org.zoxweb.client.data.events.SearchControllerHandler;
import org.zoxweb.client.rpc.CallBackHandler;
import org.zoxweb.shared.data.NVEntityContainerDAO;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author mzebib
 *
 */
public class NVConfigEntitySelectionWidget 
	extends Composite 
{
	
	private static NVConfigEntitySelectionWidgetUiBinder uiBinder 
		= GWT.create(NVConfigEntitySelectionWidgetUiBinder.class);

	interface NVConfigEntitySelectionWidgetUiBinder 
		extends UiBinder<Widget, NVConfigEntitySelectionWidget>
	{
	}

	@UiField ListBox lbNVConfigEntityTypes;
	@UiField CloseButtonWidget closeButton;
	@UiField HorizontalPanel hpButtons;
	@UiField Label labelHeader;
	
	private HashMap<String, NVConfigEntity> nvceMap = new HashMap<String, NVConfigEntity>();
	private PopupPanel popup;
	private CustomPushButtonWidget cpbOk;
	private CustomPushButtonWidget cpbCancel;
	
	public NVConfigEntitySelectionWidget(NVConfigEntity... selectionNVCEs)
	{
		this(null, selectionNVCEs);
	}
	
	public NVConfigEntitySelectionWidget(SearchControllerHandler<NVEntityContainerDAOWidget, List<NVEntity>> searchControllerHandler, NVConfigEntity... selectionNVCEs)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		popup = new PopupPanel();
		popup.setStyleName("popup");
		popup.setModal(true);
		popup.setWidget(this);
		
		ClickHandler closePopupClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				hidePopup();
			}
			
		};
		
		closeButton.addClickHandler(closePopupClickHandler);
		
		cpbOk = new CustomPushButtonWidget(WidgetConst.ImageURL.SAVE.getValue(), "Ok");
		cpbCancel = new CustomPushButtonWidget(WidgetConst.ImageURL.CANCEL.getValue(), "Cancel");
		cpbCancel.addClickHandler(closePopupClickHandler);
		
		hpButtons.add(cpbOk);
		hpButtons.add(cpbCancel);
		
		
		if ((selectionNVCEs == null || selectionNVCEs.length == 0) && searchControllerHandler != null)
		{
			searchControllerHandler.actionSearch(null, new CallBackHandler<List<NVEntity>>(new AsyncCallback<List<NVEntity>>()
			{
				@Override
				public void onFailure(Throwable caught)
				{
					
				}
	
				@Override
				public void onSuccess(List<NVEntity> result)
				{
					if (result != null)
					{
						for (NVEntity nve : result)
						{
							if (!(nve instanceof NVEntityContainerDAO))
							{
								NVConfigEntity nvce = (NVConfigEntity) nve.getNVConfig();
				
								if (nvce != null)
								{
									if (!SharedStringUtil.isEmpty(nvce.getDisplayName()))
									{
										lbNVConfigEntityTypes.addItem(nvce.getDisplayName());
										nvceMap.put(nvce.getDisplayName(), nvce);
									}
									else
									{
										lbNVConfigEntityTypes.addItem(nvce.getName());
										nvceMap.put(nvce.getName(), nvce);
									}
								}
							}
						}
					}
				}
		
			}));
		}
		else
		{
			setNVCEs(selectionNVCEs);
		}
		
		lbNVConfigEntityTypes.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				if (lbNVConfigEntityTypes.getItemCount() > 0 && lbNVConfigEntityTypes.getSelectedIndex() != -1)
				{
					cpbOk.setEnabled(true);
				}
			}
			
		});
		
		cpbOk.setEnabled(false);
	}
	
	public void setNVCEs(NVConfigEntity[] nvces)
	{
		lbNVConfigEntityTypes.clear();
		nvceMap.clear();
		
		if (nvces != null && nvces.length > 0)
		{
			for (NVConfigEntity nvce : nvces)
			{
				if (nvce != null)
				{
					if (!SharedStringUtil.isEmpty(nvce.getDisplayName()))
					{
						lbNVConfigEntityTypes.addItem(nvce.getDisplayName());
						nvceMap.put(nvce.getDisplayName(), nvce);
					}
					else
					{
						lbNVConfigEntityTypes.addItem(nvce.getName());
						nvceMap.put(nvce.getName(), nvce);
					}
				}
			}
		}
	}
	
	public void setHeader(String text)
	{
		labelHeader.setText(text);
	}
	
	public HandlerRegistration addOkButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbOk.addClickHandler(clickHandler);
	}
	
	public void addSelectedNVConfigEntityDoubleClickHandler(DoubleClickHandler doubleClickHandler)
	{
		lbNVConfigEntityTypes.addDoubleClickHandler(doubleClickHandler);
	}
	
	public NVConfigEntity getSelectedNVConfigEntity()
	{
		if (lbNVConfigEntityTypes.getSelectedIndex() != -1)
		{
			if (lbNVConfigEntityTypes.getItemText(lbNVConfigEntityTypes.getSelectedIndex()) != null)
			{
				NVConfigEntity nvce = nvceMap.get(lbNVConfigEntityTypes.getItemText(lbNVConfigEntityTypes.getSelectedIndex()));
				
				if (nvce != null)
				{
					return nvce;
				}
			}
		}
		
		return null;
	}

	public void showPopup()
	{
		popup.center();
	}
	
	public void hidePopup()
	{
		popup.hide();
	}

}