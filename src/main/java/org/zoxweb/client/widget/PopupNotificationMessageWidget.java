package org.zoxweb.client.widget;

import org.zoxweb.client.widget.CustomPushButtonWidget;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupNotificationMessageWidget
	extends PopupMessageBaseWidget
{

	private static PopupNotificationMessageWidgetUiBinder uiBinder
		= GWT.create(PopupNotificationMessageWidgetUiBinder.class);

	interface PopupNotificationMessageWidgetUiBinder 
		extends UiBinder<Widget, PopupNotificationMessageWidget>
	{
	}
	
	@UiField HTML htmlTitle;
	@UiField HorizontalPanel vpContent;
	@UiField HTML htmlMessage;
	@UiField HorizontalPanel hpControl;
	@UiField VerticalPanel vp;
	@UiField HorizontalPanel hpHeader;
	@UiField CloseButtonWidget closeButton;

	private CustomPushButtonWidget cpbOk = new CustomPushButtonWidget(WidgetConst.ImageURL.OK.getValue(), WidgetConst.ImageURL.OK.getName());
	
	public PopupNotificationMessageWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		closeButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				hidePopup();
			}
			
		});
		
		setContent(this);
		
		hpControl.add(cpbOk);
	}

	@Override
	public void setMessage(String title, String message)
	{
		htmlTitle.setHTML(title);
		htmlMessage.setHTML(message);
		vpContent.clear();
		vpContent.add(htmlMessage);
		hpControl.setVisible(true);
	}

	@Override
	public void setImage(String title, Image image)
	{
		htmlTitle.setHTML(title);
		vpContent.clear();
		hpControl.setVisible(false);
		image.setPixelSize(100, 100);
		vpContent.add(image);
	}
	
	public void addButtons(Widget[] buttons) 
	{
		for (Widget button : buttons)
		{
			hpControl.add(button);
		}
	}
	
	public void clearButtonsPanel() 
	{
		hpControl.clear();
	}
	
	public void addButton(Widget button)
	{
		hpControl.add(button);
	}
	
	public HandlerRegistration addOkButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbOk.addClickHandler(clickHandler);
	}

}