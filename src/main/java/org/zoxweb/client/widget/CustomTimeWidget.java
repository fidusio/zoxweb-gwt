package org.zoxweb.client.widget;

import java.util.Date;

import org.zoxweb.shared.util.Const;
import org.zoxweb.shared.util.SetValue;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.util.Const.DayPeriod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

/**
 * Customized time which includes hour, minutes, seconds, and AM/PM marker.
 * @author mzebib
 *
 */
public class CustomTimeWidget 
	extends Composite
	implements SetValue<Long>
{
	
	private static CustomTimeWidgetUiBinder uiBinder 
		= GWT.create(CustomTimeWidgetUiBinder.class);

	interface CustomTimeWidgetUiBinder 
		extends UiBinder<Widget, CustomTimeWidget>
	{
	}
	
	@UiField HorizontalPanel hp;
	@UiField ListBox lbHour;
	@UiField ListBox lbMinutes;
	@UiField ListBox lbDayPeriod;
	@UiField ListBox lbSeconds;
	@UiField Label labelSecondsDivider;
	
	private static final DateTimeFormat HOUR_FORMAT = DateTimeFormat.getFormat("HH");
	private static final DateTimeFormat MINUTES_FORMAT = DateTimeFormat.getFormat("mm");
	private static final DateTimeFormat SECONDS_FORMAT = DateTimeFormat.getFormat("ss");
	
	private boolean militaryTime = false;
	private boolean showSeconds = false;
	
	public CustomTimeWidget()
	{
		this(false, false);
	}
	
	public CustomTimeWidget(boolean militaryTime)
	{
		this(militaryTime, false);
	}
	
	public CustomTimeWidget(boolean militaryTime, boolean showSeconds) 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		this.militaryTime = militaryTime;
		this.showSeconds = showSeconds;
		
		setup();
	}
	
	private void setup()
	{
		if (!showSeconds)
		{
			labelSecondsDivider.setVisible(false);
			lbSeconds.setVisible(false);
		}
		
		//	Hours
		if (militaryTime)
		{
			//	Hour values between 0-24
			for (int hour = 0; hour < 24; hour++)
			{
				lbHour.addItem("" + hour, "" + hour);
			}
		}
		else 
		{	
			//	Hour values between 1-12
			for (int hour = 1; hour < 13; hour++)
			{
				lbHour.addItem("" + hour, "" + hour);
				
			}
			
			lbHour.setSelectedIndex(lbHour.getItemCount() - 1);
		}
		
		//	Minutes and seconds
		for (int value = 0; value < 60; value++)
		{
			//	Minutes and seconds values between 0-59
			if (value < 10)
			{
				lbMinutes.addItem("0" + value, ""  + value);
				lbSeconds.addItem("0" + value, ""  + value);
			}
			else
			{
				lbMinutes.addItem("" + value, ""  + value);
				lbSeconds.addItem("" + value, ""  + value);
			}
		}
		
		//	AM/PM marker
		if (militaryTime)
		{
			lbDayPeriod.setVisible(false);
		}
		else
		{
			//	AM and PM
			for (Const.DayPeriod period : Const.DayPeriod.values())
			{
				lbDayPeriod.addItem(period.getName(), period.getName());
			}
		}
	}
	
	public void setHour(String hour)
	{
		if (!SharedStringUtil.isEmpty(hour))
		{
			for (int i = 0; i < lbHour.getItemCount(); i++)
			{
				if (lbHour.getValue(i).equals(hour))
				{
					lbHour.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	public void setMinutes(String minutes)
	{
		if (!SharedStringUtil.isEmpty(minutes))
		{
			for (int i = 0; i < lbMinutes.getItemCount(); i++)
			{
				if (lbMinutes.getValue(i).equals(minutes))
				{
					lbMinutes.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	public void setSeconds(String seconds)
	{
		if (!SharedStringUtil.isEmpty(seconds))
		{
			for (int i = 0; i < lbSeconds.getItemCount(); i++)
			{
				if (lbSeconds.getValue(i).equals(seconds))
				{
					lbSeconds.setSelectedIndex(i);
					break;
				}
			}
		}
	}
	
	public void setDayPeriod(DayPeriod period)
	{
		if (!militaryTime)
		{
			for (int i = 0; i < lbDayPeriod.getItemCount(); i++)
			{
				if (lbDayPeriod.getValue(i).equals(period.getName()))
				{
					lbDayPeriod.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private long getHoursInMillis()
	{
		long value = Long.valueOf(lbHour.getValue(lbHour.getSelectedIndex()));
		
		if (!militaryTime)
		{
			if (value == 12 && lbDayPeriod.getItemText(lbDayPeriod.getSelectedIndex()).equals(Const.DayPeriod.AM.getName()))
			{
				value = 0;
			}
			else if (value != 12 && lbDayPeriod.getItemText(lbDayPeriod.getSelectedIndex()).equals(Const.DayPeriod.PM.getName()))
			{
				value = value + 12;
			}
		}
		
		return value*Const.TimeInMillis.HOUR.MILLIS;
	}
	
	private long getMinutesInMillis()
	{
		long value = Long.valueOf(lbMinutes.getValue(lbMinutes.getSelectedIndex()));
		
		return value*Const.TimeInMillis.MINUTE.MILLIS;
	}
	
	private long getSecondsInMillis()
	{
		long value = Long.valueOf(lbSeconds.getValue(lbSeconds.getSelectedIndex()));
		
		return value*Const.TimeInMillis.SECOND.MILLIS;
	}
	
	@Override
	public Long getValue() 
	{
		SharedUtil.checkIfNulls("Hour and/or minutes not selected.", 
				lbHour.getValue(lbHour.getSelectedIndex()), 
				lbMinutes.getValue(lbMinutes.getSelectedIndex())
				);
		
		if (!militaryTime)
		{
			SharedUtil.checkIfNulls("Day period not selected.",
					lbDayPeriod.getValue(lbDayPeriod.getSelectedIndex())
					);
		}

		if (showSeconds)
		{
			SharedUtil.checkIfNulls("Seconds not selected.", 
					lbSeconds.getValue(lbSeconds.getSelectedIndex())
					);
			
			return getHoursInMillis() + getMinutesInMillis() + getSecondsInMillis();
		}
		
		return getHoursInMillis() + getMinutesInMillis();
	}

	@Override
	public void setValue(Long value) 
	{
		Date date = new Date(value);
		long hour = Long.valueOf(HOUR_FORMAT.format(date));
		long minutes = Long.valueOf(MINUTES_FORMAT.format(date));
		long seconds = Long.valueOf(SECONDS_FORMAT.format(date));
		
		if (!militaryTime)
		{			
			if ((hour / 12) > 0)
			{
				setDayPeriod(Const.DayPeriod.PM);
				
				hour = hour - 12;
			}
			else
			{
				setDayPeriod(Const.DayPeriod.AM);
			}
			
			if (hour == 0)
			{
				hour = 12;
			}
		}
		
		setHour("" + hour);
		setMinutes("" + minutes);
		setSeconds("" + seconds);
	}
	
	public void setEnabled(boolean value)
	{
		lbHour.setEnabled(value);
		lbMinutes.setEnabled(value);
		lbSeconds.setEnabled(value);
		lbDayPeriod.setEnabled(value);
	}
	
	public void setSize(String width, String height)
	{
		hp.setSize(width, height);
		lbHour.setHeight("100%");
		lbMinutes.setHeight("100%");
		lbDayPeriod.setHeight("100%");
		lbSeconds.setHeight("100%");
	}
	
	public void addChangeHandler(ChangeHandler changeHandler)
	{
		lbHour.addChangeHandler(changeHandler);
		lbMinutes.addChangeHandler(changeHandler);
		
		if (!militaryTime)
		{
			lbDayPeriod.addChangeHandler(changeHandler);
		}
		
		if (showSeconds)
		{
			lbSeconds.addChangeHandler(changeHandler);
		}
	}

}