package org.zoxweb.client.widget;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

public class CustomPushButtonWidget 
	extends Composite
	implements AutoCloseable
{
	private PushButton pb;
	private Image img;
	private HandlerRegistration clickHandlerRegistration;
	
	public CustomPushButtonWidget(String imageURL)
	{
		this(imageURL, null);
	}
	
	public CustomPushButtonWidget(String imageURL, String title) 
	{	
		img = new Image(imageURL);
		
		pb = new PushButton(img);
		pb.setSize("2EM", "2EM");
		
		if (title != null)
		{
			setPushButtonTitle(title);
		}
		
		initWidget(pb);
	}
	
	public PushButton getWidget()
	{
		return pb;
	}
	
	public void setPushButtonTitle(String title)
	{
		pb.setTitle(title);
	}
	
	public void setPushButtonSize(String width, String height)
	{
		pb.setSize(width, height);
		img.setSize(width, height);
	}
		
	public HandlerRegistration addClickHandler(ClickHandler clickHandler)
	{
		clickHandlerRegistration = pb.addClickHandler(clickHandler);
		
		return clickHandlerRegistration;
	}
	
	public boolean isEnabled()
	{
		return pb.isEnabled();
	}
	
	public void setEnabled(boolean enabled)
	{
		pb.setEnabled(enabled);
	}
	
	public boolean isVisible()
	{
		return pb.isVisible();
	}
	
	public void setVisible(boolean visible)
	{
		pb.setVisible(visible);
	}

	@Override
	public void close()
	{
		if (clickHandlerRegistration != null)
		{
			clickHandlerRegistration.removeHandler();
		}
	}
}