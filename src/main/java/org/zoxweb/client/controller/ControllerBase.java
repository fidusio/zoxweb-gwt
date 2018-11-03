package org.zoxweb.client.controller;

import org.zoxweb.shared.util.NVGenericMap;

import com.google.gwt.user.client.ui.Widget;

public abstract class ControllerBase<V extends Widget> 
{
	protected NVGenericMap config;
	protected String url;
	protected V widget;
	
	protected ControllerBase (String url, V widget, NVGenericMap config)
	{
		this.url = url;
		this.widget = widget;
		this.config = config;
	}
	
	
	abstract protected void setup();
}


