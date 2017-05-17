package org.zoxweb.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.client.controller.CustomAnchorController;
import org.zoxweb.client.widget.CustomPushButtonWidget;
import org.zoxweb.shared.data.events.ValueSelectionListener;
import org.zoxweb.shared.widget.WidgetConst.ImageURL;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CustomPagerWidget 
	extends Composite
	implements ValueSelectionListener<Integer>
{

	private static CustomPagerWidgetUiBinder uiBinder 
		= GWT.create(CustomPagerWidgetUiBinder.class);

	interface CustomPagerWidgetUiBinder 
		extends UiBinder<Widget, CustomPagerWidget> 
	{
	}
	
	interface Style
		extends CssResource
	{		
		String pageNumber_Selected();
		String pageNumber_NotSelected();
	}
	
	@UiField Style style;
	@UiField VerticalPanel vpFirstPage;
	@UiField VerticalPanel vpPrevious;
	@UiField VerticalPanel vpNext;
	@UiField VerticalPanel vpLastPage;
	@UiField HorizontalPanel hpPages;
	
	private CustomPushButtonWidget cpbPrevious;
	private CustomPushButtonWidget cpbNext;
	private CustomPushButtonWidget cpbLast;
	private CustomPushButtonWidget cpbFirst;
	
	private int range = 10;
	private int selectedPageNumber = 1;
	private int totalSize;
	private List<CustomAnchorController<Integer>> customAnchorList;
	private ValueSelectionListener<Integer> valueSelectionListener;
	
	public CustomPagerWidget(int size)
	{
		this(size, false);
	}
	
	public CustomPagerWidget(int size, boolean clickHandlersProvided) 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		customAnchorList = new ArrayList<CustomAnchorController<Integer>>();
		
		cpbFirst = new CustomPushButtonWidget(ImageURL.SLIDER_FIRST.getValue(), ImageURL.SLIDER_FIRST.getName());
		cpbPrevious = new CustomPushButtonWidget(ImageURL.SLIDER_PREVIOUS.getValue(), ImageURL.SLIDER_PREVIOUS.getName());
		cpbNext = new CustomPushButtonWidget(ImageURL.SLIDER_NEXT.getValue(), ImageURL.SLIDER_NEXT.getName());
		cpbLast = new CustomPushButtonWidget(ImageURL.SLIDER_LAST.getValue(), ImageURL.SLIDER_LAST.getName());
		
		vpFirstPage.add(cpbFirst);
		vpPrevious.add(cpbPrevious);
		vpNext.add(cpbNext);
		vpLastPage.add(cpbLast);
		
		setPagerSize(size);
		setSelectedPageNumber(1);
		
		if (!clickHandlersProvided)
		{
			addFirstPageButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					setSelectedPageNumber(1);
				}
			});
			
			addLastPageButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					setSelectedPageNumber(totalSize);
				}
			});
			
			addPreviousButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (getSelectedPageNumber() != 1 && (getSelectedPageNumber() - 1) > 0)
					{
						setSelectedPageNumber(getSelectedPageNumber() - 1);
					}
				}
				
			});
			
			addNextButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (getSelectedPageNumber() != totalSize && (getSelectedPageNumber() + 1) < (totalSize + 1))
					{
						setSelectedPageNumber(getSelectedPageNumber() + 1);
					}
				}
			});
		}		
	}
	
	public void addValueSelectionListener(ValueSelectionListener<Integer> valueSelectionListener)
	{
		this.valueSelectionListener = valueSelectionListener;
	}
	
	@Override
	public void selectedValue(Integer value)
	{
		setSelectedPageNumber(value);
	}
	
	public int getPagerSize()
	{
		return totalSize;
	}
	
	public void setPagerSize(int size)
	{
		if (size >= 0)
		{
			totalSize = size;
			updateDisplay();
		}
	}
	
	public int getSelectedPageNumber()
	{
		return selectedPageNumber;
	}
	
	public void setSelectedPageNumber(int pageNumber)
	{
		if (pageNumber > 0)
		{
			//	Update style of previously selected page number
			if (getCustomAnchorPageNumber(selectedPageNumber) != null)
			{
				getCustomAnchorPageNumber(selectedPageNumber).setStyleName(style.pageNumber_NotSelected());
			}
			
			this.selectedPageNumber = pageNumber;
			updateDisplay();
			
			//	Update style of currently selected page number
			if (getCustomAnchorPageNumber(selectedPageNumber) != null)
			{
				getCustomAnchorPageNumber(selectedPageNumber).setStyleName(style.pageNumber_Selected());
			}
		}
		
		if (valueSelectionListener != null)
		{
			valueSelectionListener.selectedValue(pageNumber);
		}
	}
	
	private CustomAnchorController<Integer> addPage(int pageNumber)
	{
		CustomAnchorController<Integer> anchorPageNumber = new CustomAnchorController<Integer>(this, "" + pageNumber, pageNumber);
		anchorPageNumber.getAnchor().setSize("2EM", "100%");
		
		if (selectedPageNumber == pageNumber)
		{
			anchorPageNumber.getAnchor().setStyleName(style.pageNumber_Selected());
		}
		else
		{
			anchorPageNumber.getAnchor().setStyleName(style.pageNumber_NotSelected());
		}
		
		customAnchorList.add(anchorPageNumber);
		hpPages.add(anchorPageNumber.getAnchor());
		hpPages.setCellWidth(anchorPageNumber.getAnchor(), "2EM");
		
		return anchorPageNumber;
	}
	
	private Anchor getCustomAnchorPageNumber(int pageNumber)
	{
		for (CustomAnchorController<Integer> customAnchorPageNumber : customAnchorList)
		{
			if (pageNumber == customAnchorPageNumber.getValue())
			{
				return customAnchorPageNumber.getAnchor();
			}
		}
		
		return null;
	}
	
	private void updateDisplay()
	{
		clear();
		
		int startIndex = 1;
		int endIndex = range + 1;
		
		//	Set the start and end index
		if (totalSize <= range)
		{
			startIndex = 1;
			endIndex = startIndex + totalSize;
		}
		else if ((totalSize - range + 1 > 0) && selectedPageNumber + 4 > totalSize)
		{
			startIndex = totalSize - range + 1;
			endIndex = totalSize + 1;
		}	
		else if (selectedPageNumber - 5 > 0 && (startIndex + range) < totalSize)
		{
			startIndex = selectedPageNumber - 5;
			endIndex = startIndex + range;
		}
		
		for (int i = startIndex; i < endIndex; i++)
		{
			addPage(i);
		}
		
		//	Update position change
		cpbPrevious.setVisible(true);
		cpbNext.setVisible(true);
		cpbFirst.setVisible(true);
		cpbLast.setVisible(true);

		if (getSelectedPageNumber() == 1 && totalSize > 1)
		{
			cpbFirst.setVisible(false);
			cpbPrevious.setVisible(false);
		}
		else if (getSelectedPageNumber() == totalSize && totalSize > 1)
		{
			cpbNext.setVisible(false);
			cpbLast.setVisible(false);
		}
		else if (totalSize == 0 || totalSize == 1)
		{
			cpbFirst.setVisible(false);
			cpbPrevious.setVisible(false);
			cpbNext.setVisible(false);
			cpbLast.setVisible(false);
		}
	}
	
	private void clear()
	{
		customAnchorList.clear();
		
		for (CustomAnchorController<Integer> anchorPageNumber : customAnchorList)
		{
			anchorPageNumber.destroy();
		}
		
		hpPages.clear();
	}
	
	public HandlerRegistration addFirstPageButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbFirst.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addLastPageButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbLast.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addPreviousButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbPrevious.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addNextButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbNext.addClickHandler(clickHandler);
	}
	
}