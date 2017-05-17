package org.zoxweb.client.widget;

import org.zoxweb.client.widget.NVEntityWidget;

public class NVEntityDefaultControlPanelWidget
	extends NVEntityControlPanelBaseWidget
{

	public NVEntityDefaultControlPanelWidget(NVEntityWidget nveWidget)
	{
		this(nveWidget, null);
	}
	
	public NVEntityDefaultControlPanelWidget(NVEntityWidget nveWidget, AutoCloseable autoCloseable)
	{
		super(nveWidget, autoCloseable);
	}

	@Override
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	@Override
	public void setPreviewMode(boolean previewMode)
	{
		
	}

}