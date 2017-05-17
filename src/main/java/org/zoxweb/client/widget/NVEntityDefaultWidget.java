package org.zoxweb.client.widget;

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.NVStringWidget;
import org.zoxweb.client.widget.WidgetUtil;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.NVConfigNameMap;
import org.zoxweb.shared.util.NVConfigMapUtil;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.ExceptionCollection;
import org.zoxweb.shared.data.DataConst.DataParam;
import org.zoxweb.shared.util.ArrayValues;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

@SuppressWarnings("serial")
public class NVEntityDefaultWidget 
	extends NVEntityIntermediateWidget
{
	
	private static NVEntityDefaultWidgetUiBinder uiBinder
		= GWT.create(NVEntityDefaultWidgetUiBinder.class);
	
	interface NVEntityDefaultWidgetUiBinder 
		extends UiBinder<Widget, NVEntityDefaultWidget> 
	{
	}
	
	interface Style 
	  	extends CssResource 
	{
		String flexTableRowDivider();
	}

	@UiField Style style;
	@UiField FlexTable flexTable;
	@UiField VerticalPanel vpMain;
	@UiField VerticalPanel vpTable;
	@UiField HorizontalPanel hpLinkPlaceHolder;
	@UiField VerticalPanel vpContent;
	@UiField ScrollPanel scrollPanel;
	@UiField Label labelHeader;
	@UiField HorizontalPanel hpHeader;
	
	private static final int COLUMN_ZERO = 0;
	private static final int COLUMN_ONE = 1;

	public NVEntityDefaultWidget(NVEntity nve)
	{
		this(nve, false);
	}
	
	public NVEntityDefaultWidget(NVEntity nve, boolean showShortHand)
	{
		this(showShortHand);
		
		if (nve != null)
		{
			setValue(nve);
		}
	}
	
	public NVEntityDefaultWidget(NVConfigEntity nvce)
	{
		this(nvce, false);
	}
	
	public NVEntityDefaultWidget(NVConfigEntity nvce, boolean showShortHand)
	{
		this(showShortHand);
		setNVConfigEntity(nvce);
	}
	
	public NVEntityDefaultWidget(boolean showShortHand) 
	{
		super();
		
		initWidget(uiBinder.createAndBindUi(this));
	    
		if (showShortHand)
		{
			setContentVisible(false);
			
			anchorLink.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event) 
				{
					if (!isContentVisible())
					{
						setContentVisible(true);
						setLinkDefaultText();
					}
					else
					{
						if (getWidgetValue() != null)
						{
							setContentVisible(false);
						}
					}
				}
			});
		}
		else
		{
			hideHyperLink();
		}
	    
	    hpLinkPlaceHolder.add(anchorLink);
	}
	
	public NVConfigEntity getNVConfigEntity()
	{
		return (NVConfigEntity) nvConfig;
	}
	
	public void setNVConfigEntity(NVConfigEntity newNVCE)
	{
		if (newNVCE != nvConfig)
		{
			if (nvConfig != null)
			{
				if (((NVConfigEntity) nvConfig).isReferenced())
				{
					// this is a referenced NVEntiy
					// cannot override
					return;
				}
			}
			
			this.nvConfig = newNVCE;
			
			for (NVConfig nvc : ((NVConfigEntity) nvConfig).getAttributes())
			{
				NVConfigNameMap nvcNameMap = null;
				
				if (getNVConfigAttributesMap() != null)
				{
					nvcNameMap = getNVConfigAttributesMap().lookup(nvc.getName());
				}
				
				if (!nvc.isHidden())
				{
					NVBaseWidget<?> widget = NVCWidgetFactory.DEFAULT.createWidget(nvc, nvcNameMap, false);
					
					if (widget instanceof NVEntityDefaultWidget)
					{
						((NVEntityDefaultWidget) widget).vpTable.setVisible(true);
						//((NVEntityDefaultWidget) widget).hpButtons.setVisible(false);
					}
					
					if (widget instanceof NVArrayWidget)
					{
						//((NVArrayWidget) widget).setWidgetVisible(isExpanded());
					}
					
					addRow(widget);
				}
			}
		}
	}
	
	public void setHeader(String text)
	{
		labelHeader.setText(text);
	}
	
	public void setHeaderVisible(boolean visible)
	{
		hpHeader.setVisible(visible);
	}
	
	private void addRow(NVBaseWidget<?> widget)
	{
		int rowIndex = flexTable.getRowCount();
		
		if (readOnly)
		{
			widget.setReadOnly(readOnly);
		}
		
		if (widget instanceof NVEntityWidget)
		{
			((NVEntityWidget) widget).setWidgetWidth("100%");
			vpContent.add(widget);
		}
		else if (widget instanceof NVArrayWidget)
		{
			((NVArrayWidget<?>) widget).setVisibleButtons(true, false, false);
			widget.setWidth("100%");
			vpContent.add(widget);
		}
		else
		{
			widget.setWidth("100%");
			flexTable.setWidget(rowIndex, COLUMN_ZERO, new Label(NVConfigMapUtil.toDisplayName(widget.getNVConfig(), widget.getNVConfigNameMap())));
			flexTable.setWidget(rowIndex, COLUMN_ONE, widget);
			flexTable.getCellFormatter().setWidth(rowIndex, COLUMN_ONE, "100%");
			flexTable.getCellFormatter().setHorizontalAlignment(rowIndex, COLUMN_ONE, HasHorizontalAlignment.ALIGN_LEFT);
			flexTable.getFlexCellFormatter().setVerticalAlignment(rowIndex, COLUMN_ZERO, HasVerticalAlignment.ALIGN_TOP);
			
			if (rowIndex > 0)
			{
				flexTable.insertRow(rowIndex);
				flexTable.getFlexCellFormatter().setColSpan(rowIndex, COLUMN_ZERO, 2);
				flexTable.getFlexCellFormatter().setStyleName(rowIndex, COLUMN_ZERO, style.flexTableRowDivider());
			}	
		}
	}

	@Override
	public Widget getWidget() 
	{
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setWidgetValue(NVEntity value) 
	{
		if (value != null)
		{			
			currentNVE = value;
			setNVConfigEntity((NVConfigEntity) currentNVE.getNVConfig());
			// Name must be set here
			//setTableName(nvConfig.getDisplayName());
			
			NVConfigEntity nvce = (NVConfigEntity) currentNVE.getNVConfig();
			
			for (NVConfig nvc : nvce.getAttributes())
			{
				if (!nvc.isHidden())
				{
					NVBaseWidget<Object> nvbw = (NVBaseWidget<Object>) WidgetUtil.lookupNVBaseWidget(vpContent, nvc);
					
					if (nvbw != null)
					{
						NVBaseWidget<?> temp = nvbw;
						
						if (temp instanceof NVStringArrayValuesWidget)
						{
							if (currentNVE.lookup(nvc) instanceof ArrayValues<?>)
							{
								((NVStringArrayValuesWidget) temp).setFixed(((ArrayValues<NVPair>) currentNVE.lookup(nvc)).isFixed());
								
								nvbw.setValue(currentNVE.lookup(nvc));
							}
						}
						else
						{
							if (currentNVE.lookup(nvc) instanceof ArrayValues<?>)
							{
								nvbw.setValue(currentNVE.lookup(nvc));
							}
							else
							{
								nvbw.setValue(currentNVE.lookupValue(nvc));
							}
						}
						
//						if (currentNVE.lookup(nvc) instanceof NVPairList ||currentNVE.lookup(nvc) instanceof NVPairGetNameMap )
//						{
//							nvbw.setValue(currentNVE.lookup(nvc));
//						}
					}
				}
			}			
			
			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(currentNVE, getNVConfigAttributesMap(), false)))
			{
				anchorLink.setText(getHrefLinkText(currentNVE));
			}
			else
			{
				anchorLink.setText("Click here to fill in empty fields.");
			}
			
		}
		else
		{
			NVConfigEntity nvce = (NVConfigEntity) getNVConfig();
			
			for (NVConfig nvc : nvce.getAttributes())
			{
				if (!nvc.isHidden())
				{
					NVBaseWidget<Object> nvbw = (NVBaseWidget<Object>) WidgetUtil.lookupNVBaseWidget(vpContent, nvc);
					
					if (nvbw != null)
					{
						nvbw.setValue(null);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public NVEntity getWidgetValue()
	{
		if (currentNVE != null)
		{
			NVConfigEntity nvce = (NVConfigEntity) currentNVE.getNVConfig();
			ExceptionCollection ec = new ExceptionCollection("Invalid field value");
			
			for (NVConfig nvc : nvce.getAttributes())
			{
				NVBaseWidget<Object> nvbw = (NVBaseWidget<Object>) WidgetUtil.lookupNVBaseWidget(vpContent, nvc);
				
				if (nvbw != null)
				{					
					try
					{
						if (nvc.getMetaTypeBase() == String.class && nvc.isArray())
						{							
							ArrayValues<NVPair> innerValues = (ArrayValues<NVPair>) currentNVE.lookup(nvc);
							ArrayValues<NVPair> widgetValues = (ArrayValues<NVPair>) nvbw.getValue();
							
							innerValues.clear();
							
							for (NVPair nvp : widgetValues.values())
							{
								innerValues.add(nvp);
							}
						}
						else
						{
							currentNVE.setValue(nvc, nvbw.getValue());
						}
					}
					catch (NullPointerException e)
					{
						ec.getExceptionList().add(e);
					}
					catch (IllegalArgumentException e)
					{
						ec.getExceptionList().add(e);
					}
					catch (ExceptionCollection e)
					{
						ec.add(e);
					}
				}
			}
			
			if (!isMissingValueAllowed() && ec.getExceptionList().size() > 0)
			{
				throw ec;
			}
			
			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(currentNVE, getNVConfigAttributesMap(), false)))
			{
				anchorLink.setText(getHrefLinkText(currentNVE));
			}
			else
			{
				anchorLink.setText("Click here to fill in empty fields");
			}
			
		}
		
		return currentNVE;
	}
	
	public void clearWidgetValue()
	{
		setWidgetValue((NVEntity) null);		
	}
	
	@Override
	public void setWidgetValue(String value) 
	{
		
	}
	
	@Override
	public void hideHyperLink()
	{
		hpLinkPlaceHolder.setVisible(false);
		setContentVisible(true);
	}

	@Override
	public void clear() 
	{
		for (int i = 0; i < vpContent.getWidgetCount(); i++)
		{
			Widget widget = vpContent.getWidget(i);
			
			if (widget != null)
			{
				if (widget instanceof FlexTable)
				{
					for (int row = 0; row < flexTable.getRowCount(); row++)
					{
						if (flexTable.getCellCount(row) > 1)
						{
							NVBaseWidget<?> nvbw = (NVBaseWidget<?>) flexTable.getWidget(row, COLUMN_ONE);
							
							if (nvbw != null)
							{
								nvbw.clear();
							}
						}
					}
				}
				else if (widget instanceof NVBaseWidget<?>)
				{
					((NVBaseWidget<?>) widget).clear();
				}
			}
		}
	}

	@Override
	public boolean isContentVisible() 
	{
		return vpTable.isVisible();
	}
	
	public void setContentVisible(boolean value)
	{
		vpTable.setVisible(value);
	}

	@Override
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
		
		for (int i = 0; i < vpContent.getWidgetCount(); i++)
		{
			Widget widget = vpContent.getWidget(i);
			
			if (widget != null)
			{
				if (widget instanceof FlexTable)
				{
					for (int row = 0; row < flexTable.getRowCount(); row++)
					{
						if (flexTable.getCellCount(row) > 1)
						{
							NVBaseWidget<?> nvbw = (NVBaseWidget<?>) flexTable.getWidget(row, COLUMN_ONE);
							
							if (nvbw != null)
							{
								nvbw.setReadOnly(readOnly);
							}
						}
					}
				}
				else if (widget instanceof NVBaseWidget<?>)
				{
					((NVBaseWidget<?>) widget).setReadOnly(readOnly);
				}
			}
		}
		
	}
	
	@Override
	public void setWidgetWidth(String width)
	{
		vpMain.setWidth(width);
	}

	@Override
	public void setWidgetHeight(String height) 
	{
		vpMain.setHeight(height);
	}
	
	@Override
	public boolean isOuterScrollEnabled() 
	{
		return true;
	}
	
	@Override
	public String getFormName()
	{
		NVBaseWidget<?> nvbw = WidgetUtil.lookupNVBaseWidgetByNVC(flexTable, DataParam.NAME.getNVConfig(), COLUMN_ONE);
		
		if (nvbw != null && nvbw instanceof NVStringWidget)
		{
			return ((NVStringWidget) nvbw).getValue();
		}
		
		return null;
	}
	
	@Override
	public String getFormDescription()
	{		
		NVBaseWidget<?> nvbw = WidgetUtil.lookupNVBaseWidgetByNVC(flexTable, DataParam.DESCRIPTION.getNVConfig(), COLUMN_ONE);
		
		if (nvbw != null && nvbw instanceof NVStringWidget)
		{
			return ((NVStringWidget) nvbw).getValue();
		}
		
		return null;
	}
	
	@Override
	public boolean isNameIncluded() 
	{
		return true;
	}
	
	@Override
	public boolean isDescriptionIncluded() 
	{
		return true;
	}
	
}