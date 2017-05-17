package org.zoxweb.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CloseButtonWidget 
	extends Composite
{

	private static CloseButtonWidgetUiBinder uiBinder 
		= GWT.create(CloseButtonWidgetUiBinder.class);

	interface CloseButtonWidgetUiBinder 
		extends UiBinder<Widget, CloseButtonWidget> 
	{
	}
	
	interface Style 
		extends CssResource 
	{
		String closeButton_MouseOut();
		String closeButton_MouseOver();
	}

	@UiField Style style;
	@UiField Label labelClose;
	
	private HandlerRegistration clickHandlerRegistration;
	
	public CloseButtonWidget(ClickHandler clickHandler) 
	{
		this();
		
		addClickHandler(clickHandler);
	}
	
	public CloseButtonWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		labelClose.setTitle("Close");
	}
	
	public void addClickHandler(ClickHandler clickHandler)
	{
		clickHandlerRegistration = labelClose.addClickHandler(clickHandler);
	}
	
	public void removeClickHandler()
	{
		if (clickHandlerRegistration != null)
		{
			clickHandlerRegistration.removeHandler();
		}
	}
	
	@UiHandler("labelClose")
	void onLabelCloseMouseOver(MouseOverEvent event) 
	{
		labelClose.setStyleName(style.closeButton_MouseOver());
	}
	
	@UiHandler("labelClose")
	void onLabelCloseMouseOut(MouseOutEvent event) 
	{
		labelClose.setStyleName(style.closeButton_MouseOut());
	}

}