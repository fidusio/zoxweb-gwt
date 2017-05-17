package org.zoxweb.client.widget;

import org.zoxweb.client.widget.CustomPushButtonWidget;
import org.zoxweb.client.widget.NVEntityWidget;
import org.zoxweb.shared.data.events.ClearActionListener;
import org.zoxweb.shared.data.events.SaveActionListener;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class NVEntityControlPanelBaseWidget
	extends Composite
{
	
	protected NVEntityWidget nveWidget;
	protected VerticalPanel vpControlPanel;
	protected CustomPushButtonWidget cpbSave;
	protected CustomPushButtonWidget cpbClear;
	protected AutoCloseable autoCloseable;
	protected boolean readOnly = false;
	
	public NVEntityControlPanelBaseWidget(NVEntityWidget nveWidget)
	{
		this(nveWidget, null);
	}
	
	public NVEntityControlPanelBaseWidget(NVEntityWidget nveWidget, AutoCloseable autoCloseable)
	{
		this.nveWidget = nveWidget;	
		this.autoCloseable = autoCloseable;
		
		setup();
	}
	
	private void setup()
	{
		vpControlPanel = new VerticalPanel();
		vpControlPanel.setSize("3EM", "4EM");
		vpControlPanel.setSpacing(5);
		vpControlPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		vpControlPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		initWidget(vpControlPanel);
		
		cpbSave = new CustomPushButtonWidget(WidgetConst.ImageURL.SAVE.getValue(), WidgetConst.ImageURL.SAVE.getName());
		cpbClear = new CustomPushButtonWidget(WidgetConst.ImageURL.CLEAR.getValue(), WidgetConst.ImageURL.CLEAR.getName());
		
		if (nveWidget instanceof SaveActionListener)
		{
			cpbSave.addClickHandler(new ClickHandler()
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void onClick(ClickEvent event)
				{
					((SaveActionListener) nveWidget).actionSave(autoCloseable);
				}
				
			});
			
			addButton(cpbSave);
		}
		
		if (nveWidget instanceof ClearActionListener)
		{
			cpbClear.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					((ClearActionListener<?>) nveWidget).actionClear(null);
				}
				
			});
			
			addButton(cpbClear);
		}
	}
	
	protected void addButton(CustomPushButtonWidget cpb)
	{
		vpControlPanel.add(cpb);
	}
	
	protected void clear()
	{
		vpControlPanel.clear();
	}
	
	public boolean isReadOnly()
	{
		return readOnly;
	}
	
	public abstract void setReadOnly(boolean readOnly);
	
	public abstract void setPreviewMode(boolean previewMode);
	
}