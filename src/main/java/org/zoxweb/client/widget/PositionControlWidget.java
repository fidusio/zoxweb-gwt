package org.zoxweb.client.widget;

import org.zoxweb.shared.widget.WidgetConst;
import org.zoxweb.shared.widget.WidgetConst.ImageURL;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;

public class PositionControlWidget 
	extends Composite
{

	private static PositionControlWidgetUiBinder uiBinder 
		= GWT.create(PositionControlWidgetUiBinder.class);
	
	interface PositionControlWidgetUiBinder 
		extends UiBinder<Widget, PositionControlWidget> 
	{
	}
	
	@UiField PushButton pbUp;
	@UiField PushButton pbDown;
	@UiField PushButton pbLeft;
	@UiField PushButton pbRight;
	
	private Image imageUp;
	private Image imageDown;
	private Image imageLeft;
	private Image imageRight;
	private Image imageUpHighlighted;
	private Image imageDownHighlighted;
	private Image imageLeftHighlighted;
	private Image imageRightHighlighted;
	
	private ImageURL imageUpURL = WidgetConst.ImageURL.UP_ARROW;
	private ImageURL imageDownURL = WidgetConst.ImageURL.DOWN_ARROW;
	private ImageURL imageLeftURL = WidgetConst.ImageURL.LEFT_ARROW;
	private ImageURL imageRightURL = WidgetConst.ImageURL.RIGHT_ARROW;
	private ImageURL imageUpHighlightedURL = WidgetConst.ImageURL.UP_ARROW_HIGHLIGHTED;
	private ImageURL imageDownHighlightedURL = WidgetConst.ImageURL.DOWN_ARROW_HIGHLIGHTED;
	private ImageURL imageLeftHighlightedURL = WidgetConst.ImageURL.LEFT_ARROW_HIGHLIGHTED;
	private ImageURL imageRightHighlightedURL = WidgetConst.ImageURL.RIGHT_ARROW_HIGHLIGHTED;

	public PositionControlWidget(boolean upVisible, boolean downVisible, boolean leftVisible, boolean rightVisible)
	{
		this();
		setVisible(upVisible, downVisible, leftVisible, rightVisible);
	}
	
	public PositionControlWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		imageUp = new Image();
		imageDown = new Image();
		imageLeft = new Image();
		imageRight = new Image();
		imageUpHighlighted = new Image();
		imageDownHighlighted = new Image();
		imageLeftHighlighted = new Image();
		imageRightHighlighted = new Image();
		
		imageUp.setSize("100%", "100%");
		imageDown.setSize("100%", "100%");
		imageLeft.setSize("100%", "100%");
		imageRight.setSize("100%", "100%");
		imageUpHighlighted.setSize("100%", "100%");
		imageDownHighlighted.setSize("100%", "100%");
		imageLeftHighlighted.setSize("100%", "100%");
		imageRightHighlighted.setSize("100%", "100%");
		
		pbUp.getUpFace().setImage(imageUp);
		pbDown.getUpFace().setImage(imageDown);
		pbLeft.getUpFace().setImage(imageLeft);
		pbRight.getUpFace().setImage(imageRight);
		
		setUpImageURL(imageUpURL);
		setUpHighlightedImageURL(imageUpHighlightedURL);
		
		setDownImageURL(imageDownURL);
		setDownHighlightedImageURL(imageDownHighlightedURL);
		
		setLeftImageURL(imageLeftURL);
		setLeftHighlightedImageURL(imageLeftHighlightedURL);
		
		setRightImageURL(imageRightURL);
		setRightHighlightedImageURL(imageRightHighlightedURL);
	}

	public void setVisible(boolean upVisible, boolean downVisible, boolean leftVisible, boolean rightVisible)
	{
		pbUp.setVisible(upVisible);
		pbDown.setVisible(downVisible);
		pbLeft.setVisible(leftVisible);
		pbRight.setVisible(rightVisible);
	}
	
	public void setUpImageURL(ImageURL imageUpURL)
	{
		this.imageUpURL = imageUpURL;
	}
	
	public void setUpHighlightedImageURL(ImageURL imageUpHighlightedURL)
	{
		this.imageUpHighlightedURL = imageUpHighlightedURL;
	}
	
	public void setDownImageURL(ImageURL imageDownURL)
	{
		this.imageDownURL = imageDownURL;
	}
	
	public void setDownHighlightedImageURL(ImageURL imageDownHighlightedURL)
	{
		this.imageDownHighlightedURL = imageDownHighlightedURL;
	}
	
	public void setLeftImageURL(ImageURL imageLeftURL)
	{
		this.imageLeftURL = imageLeftURL;
	}
	
	public void setLeftHighlightedImageURL(ImageURL imageLeftHighlightedURL)
	{
		this.imageLeftHighlightedURL = imageLeftHighlightedURL;
	}

	public void setRightImageURL(ImageURL imageRightURL)
	{
		this.imageRightURL = imageRightURL;
	}
	
	public void setRightHighlightedImageURL(ImageURL imageRightHighlightedURL)
	{
		this.imageRightHighlightedURL = imageRightHighlightedURL;
	}
	
	public HandlerRegistration addUpButtonClickHandler(ClickHandler clickHandler)
	{
		return pbUp.addClickHandler(clickHandler);
	}

	public HandlerRegistration addDownButtonClickHandler(ClickHandler clickHandler)
	{
		return pbDown.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addLeftButtonClickHandler(ClickHandler clickHandler)
	{
		return pbLeft.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addRightButtonClickHandler(ClickHandler clickHandler)
	{
		return pbRight.addClickHandler(clickHandler);
	}

	@UiHandler("pbUp")
	void onPbUpMouseOver(MouseOverEvent event) 
	{
		pbUp.getUpFace().setImage(imageUpHighlighted);
	}
	
	@UiHandler("pbUp")
	void onPbUpMouseOut(MouseOutEvent event) 
	{
		pbUp.getUpFace().setImage(imageUp);
	}
	
	@UiHandler("pbDown")
	void onPbDownMouseOver(MouseOverEvent event) 
	{
		pbDown.getUpFace().setImage(imageDownHighlighted);
	}
	
	@UiHandler("pbDown")
	void onPbDownMouseOut(MouseOutEvent event) 
	{
		pbDown.getUpFace().setImage(imageDown);
	}
	
	@UiHandler("pbLeft")
	void onPbLeftMouseOver(MouseOverEvent event) 
	{
		pbLeft.getUpFace().setImage(imageLeftHighlighted);
	}
	
	@UiHandler("pbLeft")
	void onPbLeftMouseOut(MouseOutEvent event) 
	{
		pbLeft.getUpFace().setImage(imageLeft);
	}
	
	@UiHandler("pbRight")
	void onPbRightMouseOver(MouseOverEvent event) 
	{
		pbRight.getUpFace().setImage(imageRightHighlighted);
	}
	
	@UiHandler("pbRight")
	void onPbRightMouseOut(MouseOutEvent event) 
	{
		pbRight.getUpFace().setImage(imageRight);
	}

}