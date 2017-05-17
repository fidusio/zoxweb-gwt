package org.zoxweb.client.widget;

import java.util.Date;

import org.zoxweb.shared.util.Const;
import org.zoxweb.client.data.DateTimeUtil;
import org.zoxweb.shared.util.SetValue;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;

/**
 * Customized date widget which includes year, month, and day.
 * @author mzebib
 *
 */
public class CustomDateWidget 
	extends Composite
	implements SetValue<Long>
{

	private static CustomDateWidgetUiBinder uiBinder 
		= GWT.create(CustomDateWidgetUiBinder.class);
	
	interface CustomDateWidgetUiBinder 
		extends UiBinder<Widget, CustomDateWidget> 
	{
	}
	
	interface Style
		extends CssResource
	{
		String valid();
		String error();
	}

	@UiField Style style;
	@UiField HorizontalPanel hp;
	@UiField ListBox lbYear;
	@UiField ListBox lbMonth;
	@UiField ListBox lbDay;
	
	private int startYear = 0;
	private int endYear = 0;
	private String timeZoneOffset = Const.DateTimePattern.GMT_ZZZ.getValue();
	private boolean isGMT = true;
//	private static String DEFAULT_PANEL_STYLE = "noHighlight";
//	private static String ERROR_PANEL_STYLE = "redHighlight";
	
	private static final DateTimeFormat DEFAULT_GMT_DATE_FORMAT = DateTimeFormat.getFormat(Const.DateTimePattern.YEAR_MONTH_DAY_TZ.getValue());
	
	/**
	 * Set the default date format to Year-Month-Day.
	 */
	private static final DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
	
	/**
	 * This constructor creates the DateWidget given the start and end year.
	 * @param startYear
	 * @param endYear
	 */
	public CustomDateWidget(int startYear, int endYear) 
	{
		this(startYear, endYear, true);
	}
	
	/**
	 * This constructor creates the DateWidget given the start, end year and
	 * whether to set UTC as the default time zone.
	 * @param startYear
	 * @param endYear
	 * @param isGMT
	 */
	public CustomDateWidget(int startYear, int endYear, boolean isGMT) 
	{	
		initWidget(uiBinder.createAndBindUi(this));
		
		this.isGMT = isGMT;
		
		setYearRange(startYear, endYear);
		
		lbMonth.addItem("Month");
		lbDay.addItem("Day");
		
		for(Const.Month month : Const.Month.values())
		{
			lbMonth.addItem(month.getName());
		}
				
		lbYear.addChangeHandler(new ChangeHandler()
		{

			@Override
			public void onChange(ChangeEvent event) 
			{
				lbDay.setItemSelected(0, true);
				lbDay.setEnabled(false);
				lbMonth.setSelectedIndex(0);
				
				if (lbYear.getSelectedIndex() == 0)
				{
					lbMonth.setItemSelected(0, true);
					lbMonth.setEnabled(false);
					lbDay.setItemSelected(0, true);
					lbDay.setEnabled(false);
					
				}
				else
				{
					lbMonth.setEnabled(true);
				}
			}
			
		});
			
		lbMonth.addChangeHandler(new ChangeHandler()
		{

			@Override
			public void onChange(ChangeEvent event)
			{
				if (lbMonth.getSelectedIndex() == 0)
				{
					lbDay.setItemSelected(0, true);
					lbDay.setEnabled(false);
				}
				else
				{
					lbDay.setEnabled(true);
				}
				
				setDays(lbMonth.getSelectedIndex());
			}
			
		});
		
		lbMonth.setEnabled(false);
		lbDay.setEnabled(false);
		
	}
	
	/**
	 * Sets the number of days based on the month.
	 * @param month
	 */
	public void setDays(int month)
	{
		clearDaysListBox();
		
		switch(month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				for (Const.DaysInMonth day : Const.DaysInMonth.values())
				{
					lbDay.addItem(String.valueOf(day.getValue()));
				}
				
				break;
			case 2:
				if (SharedUtil.isLeapYear(Integer.valueOf(lbYear.getValue(lbYear.getSelectedIndex()))))
				{
					for (Const.DaysInMonth day : Const.DaysInMonth.values())
					{
						if (day != Const.DaysInMonth.THIRTY && day != Const.DaysInMonth.THIRTY_ONE)
							lbDay.addItem(String.valueOf(day.getValue()));
					}
				}
				else
				{
					for (Const.DaysInMonth day : Const.DaysInMonth.values())
					{
						if (day != Const.DaysInMonth.TWENTY_NINE && day != Const.DaysInMonth.THIRTY && day != Const.DaysInMonth.THIRTY_ONE)
							lbDay.addItem(String.valueOf(day.getValue()));
					}
				}
				
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				for (Const.DaysInMonth day : Const.DaysInMonth.values())
				{
					if (day != Const.DaysInMonth.THIRTY_ONE)
						lbDay.addItem(String.valueOf(day.getValue()));
				}
				
				break;
		}	
		
	}
	
	/**
	 * Gets the number of days available.
	 * @return days
	 */
	public int getDays()
	{
		return lbDay.getItemCount() - 1;
	}
	
	/**
	 * Clears the day ListBox.
	 */
	private void clearDaysListBox()
	{		
		lbDay.clear();
		lbDay.addItem("Day");
	}
	
	/**
	 * Resets the year, month, and day.
	 */
	public void reset()
	{
		lbYear.setSelectedIndex(0);
		lbMonth.setSelectedIndex(0);
		lbDay.setSelectedIndex(0);
		
		lbMonth.setEnabled(false);
		lbDay.setEnabled(false);
	}
	
	/**
	 * Sets the year range from a start to an end year.
	 * @param startY
	 * @param endY
	 */
	public void setYearRange(int startY, int endY)
	{
		startYear = startY;
		endYear = endY;
		
		lbYear.clear();
		lbYear.addItem("Year");
		
		for (int i = startYear; i != endYear;)
		{
			lbYear.addItem(String.valueOf(i));
			
			if (startYear < endYear)
			{
				i++;
			}
			
			else
			{
				i--;
			}
		}
		
		if (startY == endY)
		{
			lbYear.addItem(String.valueOf(startY));
		}
	}

	/**
	 * Gets the selected date as a long value.
	 * @return long value
	 */
	@Override
	public Long getValue() 
	{
		if (isVisible(lbYear) && isVisible(lbMonth) && isVisible(lbDay))
		{
			SharedUtil.checkIfNulls("Year, month, and/or day not selected.", 
					lbYear.getValue(lbYear.getSelectedIndex()), 
					lbMonth.getSelectedIndex(), 
					lbDay.getValue(lbDay.getSelectedIndex())
					);
	
			
			if (lbYear.getSelectedIndex() != 0 && lbMonth.getSelectedIndex() != 0 && lbDay.getSelectedIndex() != 0)
			{
				
				if (getTimeZoneOffset() != null)
				{
					return DEFAULT_DATE_FORMAT.parse(lbYear.getValue(lbYear.getSelectedIndex()) + "-" + lbMonth.getSelectedIndex() + "-" + lbDay.getValue(lbDay.getSelectedIndex())).getTime();
				}
				else
				{
					return DEFAULT_GMT_DATE_FORMAT.parse(lbYear.getValue(lbYear.getSelectedIndex()) + "-" + lbMonth.getSelectedIndex() + "-" + lbDay.getValue(lbDay.getSelectedIndex()) + " " + getTimeZoneOffset()).getTime();
				}
				
			}
		}
		
		if (isVisible(lbYear) && isVisible(lbMonth) && !isVisible(lbDay))
		{
			SharedUtil.checkIfNulls("Year and/or month not selected.", 
					lbYear.getValue(lbYear.getSelectedIndex()), 
					lbMonth.getSelectedIndex()
					);
			
			lbDay.setValue(getDays(), String.valueOf(getDays()));
			
			if (lbYear.getSelectedIndex() != 0 && lbMonth.getSelectedIndex() != 0)
			{
				return DEFAULT_GMT_DATE_FORMAT.parse(lbYear.getValue(lbYear.getSelectedIndex()) + "-" + lbMonth.getSelectedIndex() + "-" + lbDay.getValue(getDays()) + " " + getTimeZoneOffset()).getTime();
			}
		}
		
		return null;
		
	}

	/**
	 * Sets the selected date as a long value.
	 * @param value
	 */
	@Override
	public void setValue(Long value) 
	{
		if (value != null)
		{
			if (isGMT)
			{
				if (isVisible(lbYear) && isVisible(lbMonth) && isVisible(lbDay))
				{
					lbYear.setSelectedIndex(lookupYearIndex(DateTimeFormat.getFormat("yyyy").format(new Date(value), DateTimeUtil.GMT_TZ)));
					lbMonth.setSelectedIndex(lookupMonthIndex(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value), DateTimeUtil.GMT_TZ))));
					setDays(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value), DateTimeUtil.GMT_TZ)));
					lbDay.setSelectedIndex(lookupDayIndex(Integer.valueOf(DateTimeFormat.getFormat("dd").format(new Date(value), DateTimeUtil.GMT_TZ))));
				}
				
				if (isVisible(lbYear) && isVisible(lbMonth) && !isVisible(lbDay))
				{
					lbYear.setSelectedIndex(lookupYearIndex(DateTimeFormat.getFormat("yyyy").format(new Date(value), DateTimeUtil.GMT_TZ)));
					lbMonth.setSelectedIndex(lookupMonthIndex(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value), DateTimeUtil.GMT_TZ))));
					setDays(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value), DateTimeUtil.GMT_TZ)));
					lbDay.setValue(getDays(), String.valueOf(getDays()));
				}
			}
			else
			{
				if (isVisible(lbYear) && isVisible(lbMonth) && isVisible(lbDay))
				{
					lbYear.setSelectedIndex(lookupYearIndex(DateTimeFormat.getFormat("yyyy").format(new Date(value))));
					lbMonth.setSelectedIndex(lookupMonthIndex(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value)))));
					setDays(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value))));
					lbDay.setSelectedIndex(lookupDayIndex(Integer.valueOf(DateTimeFormat.getFormat("dd").format(new Date(value)))));
				}
				
				if (isVisible(lbYear) && isVisible(lbMonth) && !isVisible(lbDay))
				{
					lbYear.setSelectedIndex(lookupYearIndex(DateTimeFormat.getFormat("yyyy").format(new Date(value))));
					lbMonth.setSelectedIndex(lookupMonthIndex(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value)))));
					setDays(Integer.valueOf(DateTimeFormat.getFormat("MM").format(new Date(value))));
					lbDay.setValue(getDays(), String.valueOf(getDays()));
				}
			}
			
			lbMonth.setEnabled(true);
			lbDay.setEnabled(true);
		}
	}	
	
	/**
	 * Sets visibility for year, month, and day ListBox.
	 * @param year
	 * @param month
	 * @param day
	 */
	public void setVisible(boolean year, boolean month, boolean day)
	{
		lbYear.setVisible(year);
		lbMonth.setVisible(month);
		lbDay.setVisible(day);
	}
	
	/**
	 * Checks whether widget is visible or not.
	 * @param widget
	 * @return true if visible
	 */
	public boolean isVisible(Widget widget)
	{
		return widget.isVisible();
	}
	
	/**
	 * Looks up the index of year in the year list box.
	 * @param year
	 * @return
	 */
	private int lookupYearIndex(String year)
	{
		if (year != null)
		{
			for (int i = 1; i < lbYear.getItemCount(); i++)
			{
				if (lbYear.getItemText(i).equals(year))
				{
					return i;
				}
			}
		}
		
		return 1;
	}
	
	/**
	 * Looks up the index of month in the month list box.
	 * @param month
	 * @return
	 */
	private int lookupMonthIndex(int month)
	{
		for (int i = 1; i < lbMonth.getItemCount(); i++)
		{
			if (i == month)
			{
				return month;
			}
		}
		
		return 10;
	}

	/**
	 * Looks up the index of day in the day list box.
	 * @param day
	 * @return
	 */
	private int lookupDayIndex(int day)
	{
		for (int i = 1; i < lbDay.getItemCount(); i++)
		{
			if (i == day)
			{
				return day;
			}
		}
	
		return 1;
	}
	
	/**
	 * Gets the year ListBox widget.
	 * @return year list box
	 */
	public ListBox getYearListBox()
	{
		return lbYear;
	}
	
	/**
	 * Gets the month ListBox widget.
	 * @return month list box
	 */
	public ListBox getMonthListBox()
	{
		return lbMonth;
	}
	
	/**
	 * Gets the day ListBox widget.
	 * @return day list box
	 */
	public ListBox getDayListBox()
	{
		return lbDay;
	}
	
	/**
	 * Checks if date is selected.
	 * @return true if date is selected
	 */
	public boolean isDateSelected()
	{
		if (isVisible(lbYear) && isVisible(lbMonth) && isVisible(lbDay))
		{
			if (lbYear.getSelectedIndex() > 0 && lbMonth.getSelectedIndex() > 0 && lbDay.getSelectedIndex() > 0)
			{
				return true;
			}
		}
		
		if (isVisible(lbYear) && isVisible(lbMonth) && !isVisible(lbDay))
		{
			if (lbYear.getSelectedIndex() > 0 && lbMonth.getSelectedIndex() > 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void setPanelStyle(String style)
	{
		hp.setStyleName(style);
	}
	
	public void setValidStyle(boolean value)
	{
		if (value)
		{
			setPanelStyle(style.valid());
		}
		else
		{
			setPanelStyle(style.error());
		}
	}
	
	public String getTimeZoneOffset()
	{
		return timeZoneOffset;
	}
	
	public void setTimeZoneOffset(String timeZoneOffset)
	{
		if (timeZoneOffset != null && !isGMT)
		{
			this.timeZoneOffset = timeZoneOffset;
		}
		else
		{
			this.timeZoneOffset = Const.DateTimePattern.GMT_ZZZ.getValue();
		}
	}
	
	public void setReadOnly(boolean readOnly)
	{
		lbYear.setEnabled(!readOnly);
		lbMonth.setEnabled(!readOnly);
		lbDay.setEnabled(!readOnly);
	}
	
	public void addChangeHandler(ChangeHandler changeHandler)
	{
		lbYear.addChangeHandler(changeHandler);
		lbMonth.addChangeHandler(changeHandler);
		lbDay.addChangeHandler(changeHandler);
	}
}
