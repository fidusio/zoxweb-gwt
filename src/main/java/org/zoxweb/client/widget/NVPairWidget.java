package org.zoxweb.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.zoxweb.shared.filters.FilterType;
import org.zoxweb.shared.filters.ValueFilter;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.SUS;
import org.zoxweb.shared.widget.WidgetConst;

import java.util.Date;

@SuppressWarnings("serial")
public class NVPairWidget
	extends NVBaseWidget<NVPair>
{

	private static NVPairWidgetUiBinder uiBinder 
		= GWT.create(NVPairWidgetUiBinder.class);
	
	interface NVPairWidgetUiBinder 
		extends UiBinder<Widget, NVPairWidget>
	{
	}
	
	@UiField TextBox tbName;
	@UiField HorizontalPanel hpCurrentValueWidget;
	@UiField HorizontalPanel hpFilterWidget;
	
	private NVBaseWidget<?> currentValueWidget;
	private FilterTypeWidget filterWidget = new FilterTypeWidget();
	private String defaultStyle;

	public NVPairWidget(NVConfig nvConfig)
	{
		this(nvConfig, false);
	}
	
	public NVPairWidget(NVConfig nvConfig, boolean fixed) 
	{		
		super(nvConfig);
		initWidget(uiBinder.createAndBindUi(this));
		
		defaultStyle = tbName.getStyleName();
		
		currentValueWidget = new NVStringWidget(nvConfig);
		
		filterWidget.setSelectedFilter(FilterType.CLEAR);
		
		hpCurrentValueWidget.setSize("9EM", "1.5EM");
		currentValueWidget.setSize("100%", "1.5EM");
		hpCurrentValueWidget.add(currentValueWidget);

		filterWidget.setSize("100%", "100%");
		hpFilterWidget.add(filterWidget);
		
		filterWidget.addListBoxChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				filterWidgetChange();
			}
		});		
		
		setFixedMode(fixed);
	}

	private void filterWidgetChange()
	{
		ValueFilter<String, String> vf = filterWidget.getSelectedFilter();

		NVBaseWidget<?> tempWidget = currentValueWidget;
		
		if (vf != null)
		{
			currentValueWidget = NVCWidgetFactory.DEFAULT.mapFilterTypeToWidget(vf, getNVConfig());
			
			if (currentValueWidget instanceof NVDynamicEnumWidget)
			{
				((NVDynamicEnumWidget) currentValueWidget).setWidgetSize(15, 2);
			}
		}
		
		if (currentValueWidget != null)
		{
			hpCurrentValueWidget.remove(tempWidget);
			hpCurrentValueWidget.setSize("9EM", "1.5EM");
			currentValueWidget.setSize("100%", "1.5EM");
			hpCurrentValueWidget.add(currentValueWidget);
		}
	}
	
	
	@Override
	public Widget getWidget() 
	{
		return this;
	}

	@Override
	public void setWidgetValue(NVPair value) 
	{		
		if (value.getValueFilter() != null)
		{			
//			if (value.getValueFilter() instanceof DynamicEnumMap)
//			{
//				value.setValueFilter(DynamicEnumMapManager.SINGLETON.lookup(value.getValueFilter().toCanonicalID()));
//			}
			
			filterWidget.setSelectedFilter(value.getValueFilter());
			filterWidgetChange();
		}
		
		tbName.setValue(value.getName());
		currentValueWidget.setWidgetValue(value.getValue());
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public NVPair getWidgetValue() 
	{
		tbName.setStyleName(defaultStyle);
		
		NVTextWidgetController<?> nvtwc = currentValueWidget.getTextWidgetController();
		
		if (nvtwc != null)
		{	
			currentValueWidget.getTextWidgetController().setStyle(true);
		}
		
		NVPair nvp = new NVPair();
		
		if (SUS.isEmpty(tbName.getText()))
		{
			tbName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
			throw new NullPointerException("Empty value:" + nvConfig);
		}
		
		try 
		{
			if (nvtwc != null)
			{
				currentValueWidget.getTextWidgetController().getValueFilter().validate("" + currentValueWidget.getValue());
			}
		}
		catch (NullPointerException e)
		{
			currentValueWidget.getTextWidgetController().setStyle(false);
			throw new NullPointerException("Invalid value:" + nvConfig + " name" + tbName.getText());
		}
		catch (IllegalArgumentException e)
		{
			currentValueWidget.getTextWidgetController().setStyle(false);
			throw new IllegalArgumentException("Invalid value:" + nvConfig + " name" + tbName.getText());
		}

		nvp.setName(tbName.getValue());
		
		Object temp = currentValueWidget.getValue();
		
		if (temp instanceof Date)
		{
			temp = ((Date) temp).getTime();
		}		
		
		nvp.setValue("" + temp);
		nvp.setValueFilter(filterWidget.getSelectedFilter());

		return nvp;
	}

	@Override
	public void setWidgetValue(String value) 
	{

	}
	
	public void setFixedMode(boolean value)
	{
		if (value)
		{
			tbName.setReadOnly(true);
			filterWidget.setVisible(false);
		}
		else
		{
			tbName.setReadOnly(false);
			filterWidget.setVisible(true);
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		tbName.setEnabled(!readOnly);
		currentValueWidget.setReadOnly(readOnly);
		filterWidget.setReadOnly(readOnly);
	}

	@Override
	public void clear() 
	{
		tbName.setText("");
		currentValueWidget.clear();
	}

}