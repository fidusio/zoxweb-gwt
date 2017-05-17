package org.zoxweb.client.widget;

import java.util.List;

import org.zoxweb.client.widget.NVDoubleWidget;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings("serial")
public class NVDoubleArrayWidget
	extends NVArrayWidget<List<Double>>
{
	
	public NVDoubleArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVDoubleArrayWidget(NVConfig nvc, boolean enableScrolling) 
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVDoubleWidget nvDoubleWidget = new NVDoubleWidget(null, getNVConfig());
				addRow(nvDoubleWidget);
			}
			
		});	
		
	}

}