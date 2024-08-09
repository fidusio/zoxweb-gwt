package org.zoxweb.client.widget;

import java.math.BigDecimal;
import java.util.List;

import org.zoxweb.client.widget.NVBigDecimalWidget;
import org.zoxweb.shared.util.NVConfig;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * 
 * @author mzebib
 *
 */
@SuppressWarnings("serial")
public class NVBigDecimalArrayWidget
	extends NVArrayWidget<List<BigDecimal>>
{
	public NVBigDecimalArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVBigDecimalArrayWidget(NVConfig nvc, boolean enableScrolling) 
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVBigDecimalWidget nvBigDecimalWidget = new NVBigDecimalWidget(getNVConfig());
				addRow(nvBigDecimalWidget);
			}
			
		});	

	}
	
}