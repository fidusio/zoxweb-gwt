package org.zoxweb.client.widget;

import org.zoxweb.client.widget.WidgetUtil;
import org.zoxweb.shared.util.ArrayValues;
import org.zoxweb.shared.util.ExceptionCollection;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

@SuppressWarnings("serial")
public class NVStringArrayValuesWidget
	extends NVArrayWidget<ArrayValues<NVPair>>
{
	
	private boolean fixed = false;
	
	public NVStringArrayValuesWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVStringArrayValuesWidget(NVConfig nvc, boolean enableScrolling) 
	{
		super(nvc, enableScrolling);
		
		addAddButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				NVPairWidget nvPairWidget = new NVPairWidget(getNVConfig());
				addRow(nvPairWidget);
			}
			
		});
	}
	
	public boolean isFixed()
	{
		return fixed;
	}
	
	public void setFixed(boolean fixed)
	{
		this.fixed = fixed;
		
		setAddButtonVisibleOnly(!fixed);
	}
	
	@Override
	public void setWidgetValue(ArrayValues<NVPair> value) 
	{		
		if (value != null)
		{
			removeAll();
			
			for (NVPair nvp : value.values())
			{
				NVPairWidget nvpw = new NVPairWidget(getNVConfig());
				nvpw.setFixedMode(isFixed());				
				nvpw.setWidgetValue(nvp);
				
				addRow(nvpw, isFixed());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayValues<NVPair> getWidgetValue() 
	{
		ArrayValues<NVPair> ret = (ArrayValues<NVPair>) SharedUtil.metaConfigToNVBase(getNVConfig());
		ExceptionCollection ec = new ExceptionCollection("Invalid value.");

		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			NVPairWidget nvpw = (NVPairWidget) WidgetUtil.lookupWidget(flexTable, i, WIDGET_COLUMN);
			
			if (nvpw != null)
			{
				try
				{					
					ret.add(nvpw.getValue());
				}
				catch (NullPointerException e)
				{
					e.printStackTrace();
					ec.getExceptionList().add(e);
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					ec.getExceptionList().add(e);
				}
			}
		}
		
		if (ec.getExceptionList().size() > 0)
		{
			throw ec;
		}

		return ret;
	}
	
}