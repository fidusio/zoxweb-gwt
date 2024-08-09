package org.zoxweb.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.client.widget.CustomPushButtonWidget;
import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.WidgetUtil;
import org.zoxweb.shared.util.ExceptionCollection;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVConfigMapUtil;
import org.zoxweb.shared.util.NVConfigNameMap;
import org.zoxweb.shared.util.UpdateValue;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.uibinder.client.UiHandler;

@SuppressWarnings("serial")
public class NVArrayWidget<V>
	extends NVBaseWidget<V>
{
	
	private static NVArrayWidgetUiBinder uiBinder 
		= GWT.create(NVArrayWidgetUiBinder.class);
	
	interface NVArrayWidgetUiBinder 
		extends UiBinder<Widget, NVArrayWidget<?>> 
	{
	}
	
	interface Style 
	  	extends CssResource 
	{
		String flexTableRowDivider();
		String scrollPanel();
	}
	
	@UiField Style style;
	@UiField VerticalPanel vpContent;
	@UiField VerticalPanel vpTable;
	@UiField HorizontalPanel hpButtons;
	@UiField HTML htmlHeader;
	@UiField Hyperlink hLinkShowHide;
	
	private ScrollPanel scrollPanel;
	protected FlexTable flexTable = new FlexTable();
	
	protected static final int WIDGET_COLUMN = 0;
	protected static final int REMOVE_COLUMN = 1;
	
	private CustomPushButtonWidget cpbAdd;
	private CustomPushButtonWidget cpbSave;
	private CustomPushButtonWidget cpbReset;
	private UpdateValue<V> updateValue;
	
	public NVArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVArrayWidget(NVConfig nvc, boolean enableScrolling)
	{
		this(nvc, null, enableScrolling);
	}
	
	public NVArrayWidget(NVConfig nvc, NVConfigNameMap nvcnm)
	{
		this(nvc, nvcnm, true);
	}
	
	public NVArrayWidget(NVConfig nvc, NVConfigNameMap nvcnm, boolean enableScrolling)
	{
		super(nvc);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		if (enableScrolling)
		{
			scrollPanel = new ScrollPanel();
			vpTable.setSize("100%", "15EM");
			scrollPanel.setSize("100%", "15EM");
			flexTable.setSize("98.5%", "100%");
			
			scrollPanel.setStyleName(style.scrollPanel());
			scrollPanel.setAlwaysShowScrollBars(false);
			
			vpTable.add(scrollPanel);
			scrollPanel.add(flexTable);
		}
		else
		{
			flexTable.setSize("98.5%", "100%");
			vpTable.add(flexTable);
		}
		
		if (!nvc.isEditable())
		{
			setReadOnly(true);
		}
		
		setUpdateValue(new UpdateValue<V>()
		{
			@Override
			public void updateValue(V value) 
			{				
				WidgetUtil.logToConsole("List updated");	
			}
			
		});
		
		cpbAdd = new CustomPushButtonWidget(WidgetConst.ImageURL.ADD.getValue(), WidgetConst.ImageURL.ADD.getName());
		cpbSave = new CustomPushButtonWidget(WidgetConst.ImageURL.SAVE.getValue(), WidgetConst.ImageURL.SAVE.getName());
		cpbReset = new CustomPushButtonWidget(WidgetConst.ImageURL.RESET.getValue(), WidgetConst.ImageURL.RESET.getName());
		
		cpbAdd.setSize("1.5EM", "1.5EM");
		cpbAdd.setPushButtonSize("1.5EM", "1.5EM");
		
		cpbSave.setSize("1.5EM", "1.5EM");
		cpbSave.setPushButtonSize("1.5EM", "1.5EM");
		
		cpbReset.setSize("1.5EM", "1.5EM");		
		cpbReset.setPushButtonSize("1.5EM", "1.5EM");	
		
		hpButtons.add(cpbAdd);
		hpButtons.add(cpbSave);
		hpButtons.add(cpbReset);
		
		setArrayHeader(NVConfigMapUtil.toDisplayName(nvc, nvcnm));
		setContentVisible(false);
	}
	
	public void setArrayHeader(String header)
	{
		htmlHeader.setText(header);
	}
	
	@Override
	public Widget getWidget()
	{
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setWidgetValue(V value) 
	{				
		if (value != null && value instanceof List<?>)
		{
			removeAll();
			
			List<?> list = (List<?>) value;
			
			for (int i = 0; i < list.size(); i++)
			{
				NVBaseWidget<Object> nvbw =  (NVBaseWidget<Object>) NVCWidgetFactory.DEFAULT.createWidgetFromArrayBase(getNVConfig(), true);
				nvbw.setValue(list.get(i));
				
				addRow(nvbw);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getWidgetValue() 
	{
		List<Object> list = new ArrayList<Object>();
		ExceptionCollection ec = new ExceptionCollection("Invalid field value");

		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			NVBaseWidget<Object> nvbw = (NVBaseWidget<Object>) WidgetUtil.lookupWidget(flexTable, i, WIDGET_COLUMN);
			
			if (nvbw != null)
			{
				try
				{					
					if (nvbw.getValue() != null)
					{
						list.add(nvbw.getValue());
					}
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
		
		return (V) list;
	}

	public void addRow(NVBaseWidget<?> widget)
	{
		addRow(widget, false);
	}
	
	public void addRow(NVBaseWidget<?> widget, boolean isFixed)
	{
		int rowIndex = flexTable.getRowCount();
		
		flexTable.setWidget(rowIndex, WIDGET_COLUMN, widget);
		
		if (readOnly)
		{
			widget.setReadOnly(readOnly);
		}
		else if (!readOnly || !isFixed)
		{
			Anchor anchorRemove = new Anchor("X");
			anchorRemove.setTitle("Remove");
			anchorRemove.setSize("1.5EM", "1.5EM");			
			anchorRemove.addClickHandler(new ClickHandler()
			{
				private Anchor remove;
				
				@Override
				public void onClick(ClickEvent event) 
				{
					removeRow(WidgetUtil.getRowIndexByWidget(flexTable, remove, REMOVE_COLUMN));
					setCountDisplay();
				}
				
				ClickHandler init(Anchor remove)
				{
					this.remove = remove;
					return this;
				}
					
			}.init(anchorRemove));
			
			flexTable.setWidget(rowIndex, REMOVE_COLUMN, anchorRemove);
			
			flexTable.getFlexCellFormatter().setVerticalAlignment(rowIndex, WIDGET_COLUMN, HasVerticalAlignment.ALIGN_MIDDLE);
			flexTable.getFlexCellFormatter().setVerticalAlignment(rowIndex, REMOVE_COLUMN, HasVerticalAlignment.ALIGN_TOP);
		}
		
		if (rowIndex > 0)
		{
			flexTable.insertRow(rowIndex);
			flexTable.getFlexCellFormatter().setColSpan(rowIndex, WIDGET_COLUMN, 2);
			flexTable.getFlexCellFormatter().setStyleName(rowIndex, WIDGET_COLUMN, style.flexTableRowDivider());
		}
		
		if (widget instanceof NVEntityDefaultWidget)
		{			
			((NVEntityDefaultWidget) widget).setContentVisible(false);
		}
		
		if (widget instanceof NVEntityWidget)
		{
			((NVEntityWidget) widget).setWidgetWidth("100%");
		}
		
		setCountDisplay();
	}
	
	private void removeRow(int row)
	{
		if (row != -1)
		{
			if (row > 1)
			{
				flexTable.removeRow(row);
				flexTable.removeRow(row - 1);
			}
			else if (row == 0)
			{
				flexTable.removeRow(row);
				
				if (flexTable.getRowCount() > 0)
				{
					flexTable.removeRow(row);
				}
			}
		}
	}
	
	protected void removeAll()
	{
		flexTable.removeAllRows();
		
		setAddButtonEnabled(true);
		//setSaveButtonEnabled(false);
	}
	
	private int getCount()
	{
		int counter = 0;
		
		for (int row = 0; row < flexTable.getRowCount(); row++)
		{			
			NVBaseWidget<?> widget = (NVBaseWidget<?>) flexTable.getWidget(row, WIDGET_COLUMN);
			
			if (widget != null)
			{
				counter++;
			}
		}
		
		return counter;
	}
	
	private void setCountDisplay()
	{
		if (getCount() == 0)
		{
			hLinkShowHide.setText("Empty");
		}
		else if (vpTable.isVisible())
		{
			hLinkShowHide.setText("Hide (" + getCount() + ")");
		}
		else
		{
			hLinkShowHide.setText("Show (" + getCount() + ")");
		}
	}
	
	@Override
	public void setWidgetValue(String value) 
	{
		
	}
	
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
		
		hpButtons.setVisible(!readOnly);
		
		if (flexTable.getRowCount() > 0)
		{
			for (int row = 0; row < flexTable.getRowCount(); row++)
			{
				if (flexTable.getCellCount(row) > 0)
				{
					NVBaseWidget<?> nvbw = (NVBaseWidget<?>) flexTable.getWidget(row, WIDGET_COLUMN);
					
					if (nvbw != null)
					{
						nvbw.setReadOnly(readOnly);
					}
				}
				
				if (flexTable.getCellCount(row) > 1)
				{
					Anchor anchorRemove = (Anchor) flexTable.getWidget(row, REMOVE_COLUMN);
					
					if (anchorRemove != null)
					{
						anchorRemove.setVisible(!readOnly);
					}
				}
			}
		}
	}
	
	public HandlerRegistration addAddButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbAdd.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addSaveButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbSave.addClickHandler(clickHandler);
	}
	
	public HandlerRegistration addResetButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbReset.addClickHandler(clickHandler);
	}
	
	public void setAddButtonEnabled(boolean enabled)
	{
		cpbAdd.setEnabled(enabled);
	}
	
//	public void setSaveButtonEnabled(boolean enabled)
//	{
//		cpbSave.setEnabled(enabled);
//	}
	
	public void setAddButtonVisibleOnly(boolean visible)
	{		
		hpButtons.clear();
		
		if (visible)
		{
			hpButtons.add(cpbAdd);
		}
	}
	
	public void setVisibleButtons(boolean addButtonVisible, boolean saveButtonVisible, boolean resetButtonVisible)
	{
		hpButtons.clear();
		
		if (addButtonVisible)
		{
			hpButtons.add(cpbAdd);
		}
		
		if (saveButtonVisible)
		{
			hpButtons.add(cpbSave);
		}
		
		if (resetButtonVisible)
		{
			hpButtons.add(cpbReset);
		}
	}
	
	public void setWidgetWidth(int widthInEM)
	{
		vpContent.setWidth(widthInEM + "EM");
		flexTable.setWidth((widthInEM - 2) + "EM");
	}
	
	public void setWidgetSize(String width, String height)
	{
		if (scrollPanel != null)
		{
			scrollPanel.setSize("100%", height);
		}
		
		vpTable.setSize(width, height);
		flexTable.setSize("98.5%", "100%");
	}
	
	public void setUpdateValue(UpdateValue<V> value)
	{
		updateValue = value;
	}

	public UpdateValue<V> getUpdateValue()
	{
		return updateValue;
	}
	
	public void updateValue()
	{
		if (getUpdateValue() != null)
		{
			try
			{
				getUpdateValue().updateValue(getValue());
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (ExceptionCollection e)
			{
				for (Exception exception : e.getExceptionList())
				{
					exception.printStackTrace();
				}
				
				e.printStackTrace();
			}
		}
	}

	@UiHandler("hLinkShowHide")
	void onHLinkShowHideClick(ClickEvent event) 
	{
		setContentVisible(!vpTable.isVisible());
	}
	
	public void setContentVisible(boolean visible)
	{
		if (getCount() == 0)
		{
			hLinkShowHide.setText("Empty");
			
			vpTable.setVisible(true);
			hpButtons.setVisible(true);
		}
		else
		{
			if (visible)
			{
				hLinkShowHide.setText("Hide (" + getCount() + ")");
			}
			else
			{
				hLinkShowHide.setText("Show (" + getCount() + ")");
			}
			
			vpTable.setVisible(visible);
			hpButtons.setVisible(visible);
		}
	}

	@Override
	public void clear() 
	{
		if (flexTable.getRowCount() > 0)
		{
			for (int row = 0; row < flexTable.getRowCount(); row++)
			{
				if (flexTable.getCellCount(row) > 0)
				{
					NVBaseWidget<?> nvbw = (NVBaseWidget<?>) flexTable.getWidget(row, WIDGET_COLUMN);
					
					if (nvbw != null)
					{
						nvbw.clear();
					}
				}
			}
		}
	}
	
}