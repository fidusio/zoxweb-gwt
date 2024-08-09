package org.zoxweb.client.widget;

import java.util.List;

import org.zoxweb.client.widget.NVIntegerWidget;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings("serial")
public class NVIntegerArrayWidget 
	extends NVArrayWidget<List<Integer>>
{
	
	public NVIntegerArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVIntegerArrayWidget(NVConfig nvc, boolean enableScrolling)
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVIntegerWidget nvIntegerWidget = new NVIntegerWidget(getNVConfig());
				addRow(nvIntegerWidget);
			}
			
		});			
	}
}
