package org.zoxweb.client.widget;

import java.util.List;

import org.zoxweb.client.widget.NVBooleanWidget;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings("serial")
public class NVBooleanArrayWidget
	extends NVArrayWidget<List<Boolean>>
{

	public NVBooleanArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVBooleanArrayWidget(NVConfig nvc, boolean enableScrolling) 
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVBooleanWidget nvBooleanWidget = new NVBooleanWidget(null, getNVConfig());
				addRow(nvBooleanWidget);
			}
			
		});
		
	}

}
