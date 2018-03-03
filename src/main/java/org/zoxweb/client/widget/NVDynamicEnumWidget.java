package org.zoxweb.client.widget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.zoxweb.shared.util.CRUD;
import org.zoxweb.shared.util.Const;
import org.zoxweb.shared.util.DynamicEnumMap;
import org.zoxweb.shared.util.NVBase;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.ValueDecoderComparator.StringValueDecoderComparator;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.logical.shared.AttachEvent;

import org.zoxweb.client.data.ApplicationClientDAO;
import org.zoxweb.client.data.events.CRUDNVBaseEvent;
import org.zoxweb.client.data.events.CRUDNVBaseHandler;
import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.PopupContentWidget;

@SuppressWarnings("serial")
public class NVDynamicEnumWidget
	extends NVBaseWidget<String>
	implements CRUDNVBaseHandler, AttachEvent.Handler
{
	
	private ListBox listBox = new ListBox();
	private DynamicEnumMap dynamicEnumMap;
	private HashMap<String, String> map = new HashMap<String, String>();
	private HorizontalPanel hpDEM = new HorizontalPanel();
	private Image imgEditor = new Image(WidgetConst.ImageURL.EDIT.getValue());
	private PushButton pbEditor = new PushButton();
	private PopupContentWidget popup = new PopupContentWidget();
	private Const.NVDisplayProp displayProp;
	private String listTitle = "Select:";
	private boolean isTitleAvailable = true;
	private HandlerRegistration handlerRegistration;
	private StringValueDecoderComparator<NVPair> comparator;
	
	public NVDynamicEnumWidget(NVConfig nvConfig, Const.NVDisplayProp displayProp)
	{
		this(nvConfig, displayProp, true);
	}
	
	public NVDynamicEnumWidget(NVConfig nvConfig, Const.NVDisplayProp displayProp, boolean isTitleAvailable)
	{
		super(null, nvConfig);
		setTitleAvailable(isTitleAvailable);
		
		if (!nvConfig.isEditable())
		{
			listBox.setEnabled(false);
		}
		
		if (!(nvConfig.getValueFilter() instanceof DynamicEnumMap))
		{
			throw new IllegalArgumentException("Not dynamic enum map type.");
		}
		
		dynamicEnumMap = (DynamicEnumMap) nvConfig.getValueFilter();
		
		setupWidget(nvConfig.getName(), displayProp);
	}
	
	public NVDynamicEnumWidget(String name, DynamicEnumMap dem, Const.NVDisplayProp displayProp)
	{
		this(name, dem, displayProp, true);
	}
	
	public NVDynamicEnumWidget(String name, DynamicEnumMap dem, Const.NVDisplayProp displayProp, boolean isTitleAvailable)
	{
		this(name, dem, displayProp, isTitleAvailable, null);
	}
	
	public NVDynamicEnumWidget(String name, DynamicEnumMap dem, Const.NVDisplayProp displayProp, boolean isTitleAvailable, StringValueDecoderComparator<NVPair> comparator)
	{
		super(null, null, true);
		
		this.comparator = comparator;
		
		setTitleAvailable(isTitleAvailable);
		
		dynamicEnumMap = dem;
		
		setupWidget(name, displayProp);
	
		setEditVisible(false);
	}
	
	private void setupWidget(String name, Const.NVDisplayProp displayProp)
	{
		pbEditor.addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event) 
			{
				DynamicEnumMapEditorWidget demEditorWidget = new DynamicEnumMapEditorWidget(dynamicEnumMap);
				
				demEditorWidget.addCloseButtonClickHanlder(new ClickHandler()
				{

					@Override
					public void onClick(ClickEvent event) 
					{
						popup.hide();
					}
					
				});
				
				popup.showPopup(dynamicEnumMap.getDisplayName(), demEditorWidget);
				
			}
			
		});
		
		setDefaultSize();
		
		hpDEM.add(listBox);
		
		pbEditor.getUpFace().setImage(imgEditor);
		hpDEM.add(pbEditor);
		
		refreshListItems(displayProp);
		initWidget(hpDEM);
		listBox.setName(name);
		this.displayProp = displayProp;
		//handlerRegistration = FidusStoreHandlerManager.SINGLETON.getHandlerManager().addHandler(CRUDNVBaseEvent.TYPE, this);
		
		addAttachHandler(this);
	}
	

	@Override
	public Widget getWidget()
	{
		return listBox;
	}

	@Override
	public void setWidgetValue(String value)
	{
		if (value == null)
		{
			listBox.setSelectedIndex(0);
		}
		else
		{	// Iterate through HashMap
			for (Map.Entry<String, String> entry : map.entrySet()) 
			{
				//	Check if input matches value in HashMap
				if (entry.getValue().equals(value))
				{
					//	Iterate through list box
					for (int i = 1; i < listBox.getItemCount(); i++)
					{
						//	Check if key matches any item text in list box
						if (listBox.getValue(i).equals(entry.getKey()))
						{
							//	Set selected item
							listBox.setSelectedIndex(i);
							break;
						}
					}
				}
			}
		}
		
	}

	@Override
	public String getWidgetValue() 
	{
		if (isTitleAvailable)
		{
			if (listBox.getSelectedIndex() > 0)
			{
				return map.get(listBox.getValue(listBox.getSelectedIndex()));
				
				//return listBox.getValue(listBox.getSelectedIndex());
			}
			else
			{
				return null;
			}
		}
		else if (listBox.getSelectedIndex() != -1)
		{
			return map.get(listBox.getValue(listBox.getSelectedIndex()));
			
		}
		
		return null;
		
//		else if (nvConfig.isMandatory())
//		{
//			throw new NullPointerException("Empty value: " + nvConfig);
//		}

	}

	public void setListTitle(String title)
	{
		listTitle = title;
		listBox.setItemText(0, title);
	}
	
	public void setTitleAvailable(boolean value)
	{
		isTitleAvailable = value;
	}
	
	public void setEditVisible(boolean value)
	{
		pbEditor.setVisible(value);
	}
	
	private void refreshListItems(Const.NVDisplayProp displayProp)
	{
		String currentSelection = getValue();
		
		map.clear();
		listBox.clear();
		
		if (isTitleAvailable)
		{
			listBox.addItem(listTitle);
		}
		
		NVPair[] values = dynamicEnumMap.values();
		
		if (comparator != null)
		{
			Arrays.sort(values, comparator);
		}
		
		for (NVPair nvp : values)
		{
			switch(displayProp)
			{
			case DEFAULT:
					if (!SharedStringUtil.isEmpty(nvp.getValue()))
					{
						listBox.addItem(nvp.getValue());
						map.put(nvp.getValue(), nvp.getName());
					}
					else
					{
						listBox.addItem(nvp.getName());
						map.put(nvp.getName(), nvp.getName());
					}
				break;
			case NAME:
					listBox.addItem(nvp.getName());
					map.put(nvp.getName(), nvp.getName());
				break;
			case NAME_VALUE:
					if (!SharedStringUtil.isEmpty(nvp.getValue()))
					{
						listBox.addItem(nvp.getName() + "-" + nvp.getValue());
						map.put(nvp.getName() + "-" + nvp.getValue(), nvp.getName());
					}
					else
					{
						listBox.addItem(nvp.getName());
						map.put(nvp.getName(), nvp.getName());
					}
				break;
			case VALUE:
					if (!SharedStringUtil.isEmpty(nvp.getValue()))
					{
						listBox.addItem(nvp.getValue());
						map.put(nvp.getValue(), nvp.getName());
					}
					else
					{
						listBox.addItem(nvp.getName());
						map.put(nvp.getName(), nvp.getName());
					}
				break;
			}
		}
		
		if (currentSelection != null)
		{
			NVPair nvp = dynamicEnumMap.lookup(currentSelection);
			
			if (nvp != null)
			{
				setValue(nvp.getName());
			}
		}
	}
	
	public void setDefaultSize()
	{
		hpDEM.setSize("10EM", "2EM");
		listBox.setSize("9EM", "2EM");
		pbEditor.setSize("0.75EM", "0.75EM");
		imgEditor.setSize("100%", "100%");
	}
	
	public void setWidgetSize(int widthInEM, int heightInEM)
	{
		hpDEM.setSize(widthInEM + "EM", heightInEM + "EM");
		listBox.setSize((widthInEM - 1) + "EM", heightInEM + "EM");
	}

	@Override
	public void applyCRUD(CRUD crud, NVBase<?> nvb) 
	{
		//NVBase<?> nvb = crudNVBase.getNVBase();
		if ( nvb instanceof DynamicEnumMap)
		{
			//CRUD crud = crudNVBase.getCRUD();
			switch(crud)
			{
			case CREATE:
				break;
			case DELETE:
				break;
			case READ:
				break;
			case UPDATE:
				
				if(nvb.getReferenceID() !=null && nvb.getReferenceID().equals(dynamicEnumMap.getReferenceID()))
				{
					// we have to update our self
					//
					
					refreshListItems(displayProp);
				}
				break;
			default:
				break;
			
			}
		}
	}

	@Override
	public void onAttachOrDetach(AttachEvent event) 
	{
		if (event.isAttached())
		{
			if (handlerRegistration != null)
			{
				handlerRegistration.removeHandler();
			}
			
			handlerRegistration = ApplicationClientDAO.DEFAULT.getHandlerManager().addHandler(CRUDNVBaseEvent.TYPE, this);
		}
		else
		{
			if (handlerRegistration != null)
			{
				handlerRegistration.removeHandler();
				handlerRegistration = null;
			}
		}
		
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		listBox.setEnabled(!readOnly);
	}
	
	@Override
	public void clear() 
	{
		listBox.setSelectedIndex(0);
	}
	
}