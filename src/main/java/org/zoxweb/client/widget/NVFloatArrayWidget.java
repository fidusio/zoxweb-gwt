package org.zoxweb.client.widget;

import java.util.List;

import org.zoxweb.client.widget.NVFloatWidget;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings("serial")
public class NVFloatArrayWidget 
	extends NVArrayWidget<List<Float>>
{

	public NVFloatArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVFloatArrayWidget(NVConfig nvc, boolean enableScrolling) 
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVFloatWidget nvFloatWidget = new NVFloatWidget(getNVConfig());
				addRow(nvFloatWidget);
			}
			
		});	
		
	}

}