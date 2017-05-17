package org.zoxweb.client.widget;

import java.util.Date;

import org.zoxweb.shared.util.SetValue;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Customized combination of date and time widgets.
 * @author mzebib
 *
 */
public class CustomDateTimeWidget 
	extends Composite
	implements SetValue<Long>
{
	
	private HorizontalPanel hpDefaultMode;
	private VerticalPanel vpCompressedMode;
	private CustomDateWidget dateWidget;
	private CustomTimeWidget timeWidget;
	
	private static int startYear = Integer.valueOf(DateTimeFormat.getFormat("yyyy").format(new Date()));
	private static int endYear = startYear + 5;

	public CustomDateTimeWidget()
	{
		this(startYear, endYear);
	}
	
	public CustomDateTimeWidget(String timeZoneOffset)
	{
		this(timeZoneOffset, false);
	}
	
	public CustomDateTimeWidget(String timeZoneOffset, boolean compressedMode)
	{
		this(timeZoneOffset, compressedMode, false);
	}
	
	public CustomDateTimeWidget(String timeZoneOffset, boolean compressedMode, boolean militaryTime)
	{
		this(timeZoneOffset, startYear, endYear, compressedMode, militaryTime);
	}
	
	public CustomDateTimeWidget(String timeZoneOffset, int startYear, int endYear, boolean compressedMode, boolean militaryTime)
	{
		this(startYear, endYear, compressedMode, militaryTime);
		
		if (timeZoneOffset != null)
		{
			dateWidget.setTimeZoneOffset(timeZoneOffset);
		}
	}
	
	public CustomDateTimeWidget(int startYear, int endYear)
	{
		this(startYear, endYear, false);
	}
	
	public CustomDateTimeWidget(int startYear, int endYear, boolean militaryTime)
	{
		this(startYear, endYear, false, militaryTime);
	}
	
	public CustomDateTimeWidget(int startYear, int endYear, boolean compressedMode, boolean militaryTime) 
	{
		dateWidget = new CustomDateWidget(startYear, endYear, false);
		timeWidget = new CustomTimeWidget(militaryTime);
		
		if (compressedMode)
		{
			vpCompressedMode = new VerticalPanel();
			vpCompressedMode.setSize("15EM", "5EM");
			vpCompressedMode.setSpacing(5);
			
	
			HorizontalPanel hpTime = new HorizontalPanel();
			hpTime.setSize("8EM", "2.5EM");
			
			vpCompressedMode.add(dateWidget);
			hpTime.add(timeWidget);
			vpCompressedMode.add(hpTime);
			
			vpCompressedMode.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			
			dateWidget.setSize("100%", "100%");
			timeWidget.setSize("100%", "100%");
			
			initWidget(vpCompressedMode);
		}
		else
		{
			hpDefaultMode = new HorizontalPanel();
			hpDefaultMode.setSize("25EM", "2.5EM");
			hpDefaultMode.setSpacing(5);
			
			hpDefaultMode.add(dateWidget);
			hpDefaultMode.add(timeWidget);
			
			dateWidget.setSize("100%", "100%");
			timeWidget.setSize("100%", "100%");
			
			initWidget(hpDefaultMode);
		}
	}
	
	@Override
	public Long getValue() 
	{		
		try
		{
			if (dateWidget.getValue() != null && timeWidget.getValue() != null)
			{
				return dateWidget.getValue() + timeWidget.getValue();
			}
		}
		catch (Exception e)
		{
			
		}

		return null;
	}

	@Override
	public void setValue(Long value) 
	{
		dateWidget.setValue(value);
		timeWidget.setValue(value);
	}
	
	public void addDateChangeHandler(ChangeHandler changeHandler)
	{
		dateWidget.addChangeHandler(changeHandler);
	}
	
	public void addTimeChangeHandler(ChangeHandler changeHandler)
	{
		timeWidget.addChangeHandler(changeHandler);
	}
}
