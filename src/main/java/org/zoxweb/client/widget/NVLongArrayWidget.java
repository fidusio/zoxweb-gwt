package org.zoxweb.client.widget;

import java.util.List;

import org.zoxweb.client.widget.NVLongWidget;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings("serial")
public class NVLongArrayWidget
	extends NVArrayWidget<List<Long>>
{
	
	public NVLongArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVLongArrayWidget(NVConfig nvc, boolean enableScrolling)
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVLongWidget nvLongWidget = new NVLongWidget(getNVConfig());
				addRow(nvLongWidget);
			}
			
		});	
		
	}
	
}