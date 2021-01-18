package org.zoxweb.client.controller;


import org.zoxweb.shared.util.NVGenericMap;
import org.zoxweb.shared.util.ResourceManager;

import com.google.gwt.user.client.ui.Widget;

public abstract class ControllerBase<V extends Widget> 
{
	protected NVGenericMap config;

	protected V widget;
	
	protected ControllerBase (V widget, NVGenericMap config)
	{
		this.widget = widget;
		this.config = config;
		ResourceManager.SINGLETON.map(widget, this);
		ResourceManager.SINGLETON.map(this, widget);
	}
	
	
	abstract protected void setup();




}


