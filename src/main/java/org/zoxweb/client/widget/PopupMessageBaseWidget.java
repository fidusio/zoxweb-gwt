package org.zoxweb.client.widget;

import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class PopupMessageBaseWidget 
	extends Composite
{	
	private PopupPanel popup;
	private SimplePanel sp = new SimplePanel();

	protected PopupMessageBaseWidget() 
	{
		popup = new PopupPanel(false);
		popup.setModal(true);
		popup.setGlassEnabled(true);
		popup.setStyleName(WidgetConst.CSSStyle.POPUP.getName());
		popup.setWidget(sp);
	}
	
	public void setContent(Widget widget)
	{
		sp.setWidget(widget);
	}
	
	public void showPopup(String title, String message)
	{
		setMessage(title, message);
		popup.center();
	}
	
	public void showPopup(String title, Image image)
	{
		setImage(title, image);
		popup.center();
	}
	
	public void hidePopup()
	{
		popup.hide();
	}

	public boolean isPopupShowing()
	{
		return popup.isShowing();
	}
	
	public PopupPanel getPopupPanel()
	{
		return popup;
	}
	
	public abstract void setMessage(String title, String message);
	
	public abstract void setImage(String title, Image image);
	
}