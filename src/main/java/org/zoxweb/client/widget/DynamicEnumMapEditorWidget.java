package org.zoxweb.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.zoxweb.client.rpc.CallBackHandler;
import org.zoxweb.client.widget.WidgetController;
import org.zoxweb.client.widget.WidgetUtil;
import org.zoxweb.shared.util.CRUD;
import org.zoxweb.shared.util.DynamicEnumMap;
import org.zoxweb.shared.util.DynamicEnumMapManager;
import org.zoxweb.shared.util.NVBase;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.NotificationType;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.widget.WidgetConst;
import org.zoxweb.shared.data.CRUDNVBaseDAO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.zoxweb.client.data.ApplicationClientDAO;
import org.zoxweb.client.data.events.CRUDNVBaseEvent;
import org.zoxweb.client.data.events.CRUDNVBaseHandler;
import org.zoxweb.client.data.events.SaveControllerHandler;

public class DynamicEnumMapEditorWidget 
	extends Composite
	implements WidgetController,
			   CRUDNVBaseHandler
{
	
	private static DynamicEnumMapEditorWidgetUiBinder uiBinder 
		= GWT.create(DynamicEnumMapEditorWidgetUiBinder.class);
	
	interface DynamicEnumMapEditorWidgetUiBinder 
		extends UiBinder<Widget, DynamicEnumMapEditorWidget>
	{
	}
	
	interface Style
  		extends CssResource
  	{
		String flexTable_OddRow ();
		String flexTable_EvenRow();
		String flexTable_SelectedRow();
	}
	
	@UiField Style style;
	@UiField CheckBox cbSelectAll;
	@UiField ScrollPanel scrollPanel;
	@UiField FlexTable flexTable;
	@UiField Label labelEnumName;
	@UiField Button bDelete;
	@UiField Button bUpdate;
	@UiField Label labelName;
	@UiField Label labelValue;
	@UiField HorizontalPanel hpButtons;
	@UiField HorizontalPanel hpAdd;
	@UiField TextBox tbAddName;
	@UiField TextBox tbAddValue;
	@UiField Button bAdd;
	@UiField Label labelMessage;
	@UiField Label labelItemCount;
	@UiField VerticalPanel vpHeader;
	@UiField VerticalPanel vpFooter;
	@UiField Button bClose;
	@UiField PositionControlWidget pcMoveRow;
	
	private SaveControllerHandler<DynamicEnumMap, DynamicEnumMap> saveControllerHandler;
	private DynamicEnumMap dem;
	private String oddRowStyle = null;
	private String evenRowStyle = null;
	private String selectedRowStyle = null;
//	private WidgetUtilFactory.TableStyle oddRowStyle = WidgetUtilFactory.TableStyle.FLEX_TABLE_ODD_ROW;
//	private WidgetUtilFactory.TableStyle evenRowStyle = WidgetUtilFactory.TableStyle.FLEX_TABLE_EVEN_ROW;
//	private WidgetUtilFactory.TableStyle selectedRowStyle = WidgetUtilFactory.TableStyle.FLEX_TABLE_SELECTED_ROW;
	
	private static final int CHECKBOX_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	private static final int VALUE_COLUMN = 2;

	public DynamicEnumMapEditorWidget(DynamicEnumMap dem)
	{
		this();
		setDynamicEnumMap(dem);
	}
	
	public DynamicEnumMapEditorWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		oddRowStyle = style.flexTable_OddRow();
		evenRowStyle = style.flexTable_EvenRow();
		selectedRowStyle = style.flexTable_SelectedRow();
		
		scrollPanel.setAlwaysShowScrollBars(true);
		
        tbAddName.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Enter name");
        		}
		 	}
                 
        });
        
        tbAddValue.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Enter value");
        		}
		 	}
                 
        });
		
        labelMessage.setVisible(false);
        setItemCountLabel();
        
        pcMoveRow.setVisible(true, true, false, false);
        
        pcMoveRow.addUpButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				if (getCBCheckedList().size() == 1)
				{
					int checkedIndex = getCBCheckedList().get(0);
					
					if (checkedIndex != 0)
					{	
						CheckBox cbInitial = (CheckBox) WidgetUtil.lookupWidget(flexTable, checkedIndex, CHECKBOX_COLUMN);
						TextBox tbNameInitial = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex, NAME_COLUMN);
						TextBox tbValueInitial = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex, VALUE_COLUMN);
						
						CheckBox cbFinal = (CheckBox) WidgetUtil.lookupWidget(flexTable, checkedIndex - 1, CHECKBOX_COLUMN);
						TextBox tbNameFinal = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex - 1, NAME_COLUMN);
						TextBox tbValueFinal = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex - 1, VALUE_COLUMN);
						
						flexTable.setWidget(checkedIndex - 1, CHECKBOX_COLUMN, cbInitial);
						flexTable.setWidget(checkedIndex - 1, NAME_COLUMN, tbNameInitial);
						flexTable.setWidget(checkedIndex - 1, VALUE_COLUMN, tbValueInitial);
						
						flexTable.setWidget(checkedIndex, CHECKBOX_COLUMN, cbFinal);
						flexTable.setWidget(checkedIndex, NAME_COLUMN, tbNameFinal);
						flexTable.setWidget(checkedIndex, VALUE_COLUMN, tbValueFinal);
						
						updateRowStyle();
						applySelectedRowStyle(checkedIndex - 1);
					}
				}
			}
		});
        
        pcMoveRow.addDownButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				if (getCBCheckedList().size() == 1)
				{
					int checkedIndex = getCBCheckedList().get(0);
					
					if (checkedIndex != getItemCount() - 1)
					{	
						CheckBox cbInitial = (CheckBox) WidgetUtil.lookupWidget(flexTable, checkedIndex, CHECKBOX_COLUMN);
						TextBox tbNameInitial = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex, NAME_COLUMN);
						TextBox tbValueInitial = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex, VALUE_COLUMN);
						
						CheckBox cbFinal = (CheckBox) WidgetUtil.lookupWidget(flexTable, checkedIndex + 1, CHECKBOX_COLUMN);
						TextBox tbNameFinal = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex + 1, NAME_COLUMN);
						TextBox tbValueFinal = (TextBox) WidgetUtil.lookupWidget(flexTable, checkedIndex + 1, VALUE_COLUMN);
						
						flexTable.setWidget(checkedIndex + 1, CHECKBOX_COLUMN, cbInitial);
						flexTable.setWidget(checkedIndex + 1, NAME_COLUMN, tbNameInitial);
						flexTable.setWidget(checkedIndex + 1, VALUE_COLUMN, tbValueInitial);
						
						flexTable.setWidget(checkedIndex, CHECKBOX_COLUMN, cbFinal);
						flexTable.setWidget(checkedIndex, NAME_COLUMN, tbNameFinal);
						flexTable.setWidget(checkedIndex, VALUE_COLUMN, tbValueFinal);
						
						updateRowStyle();
						applySelectedRowStyle(checkedIndex + 1);
					}
				}
			}
		});

        ApplicationClientDAO.DEFAULT.getHandlerManager().addHandler(CRUDNVBaseEvent.TYPE, this);
	}
	
	public DynamicEnumMap getDynamicEnumMap()
	{
		return dem;
	}
	
	public void setDynamicEnumMap(DynamicEnumMap dem)
	{
		this.dem = dem;
		
		labelEnumName.setText(WidgetUtil.getDynamicEnumDisplayName(dem.getName()));
		
		int row = 0;
		
		for (NVPair nvp : dem.getValue())
		{
			TextBox tbName = new TextBox();
			tbName.setText(nvp.getName());
			
			TextBox tbValue = new TextBox();
			tbValue.setText(nvp.getValue());
			
			addRow(tbName, tbValue, row);
			row++;
		}
	}
	
	private void addRow(TextBox tbName, TextBox tbValue, int row)
	{
		tbName.setSize("7.5EM", "1.5EM");
		tbValue.setSize("12EM", "1.5EM");
		
		CheckBox cb = new CheckBox();
		
		flexTable.setWidget(row, CHECKBOX_COLUMN, cb);
		flexTable.setWidget(row, NAME_COLUMN, tbName);
		flexTable.setWidget(row, VALUE_COLUMN, tbValue);
		
		cb.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				boolean checked = true;
				
				for (int i = 0; i < flexTable.getRowCount(); i++)
				{
					TextBox tbNameCurrent = (TextBox) WidgetUtil.lookupWidget(flexTable, i, NAME_COLUMN);
					TextBox tbValueCurrent = (TextBox) WidgetUtil.lookupWidget(flexTable, i, VALUE_COLUMN);
					CheckBox cb = (CheckBox) WidgetUtil.lookupWidget(flexTable, i, CHECKBOX_COLUMN);
					checked = cb.getValue();
					
					if (checked)
					{
						applySelectedRowStyle(i);
						tbNameCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_HIGHLIGHTED.getName());
						tbValueCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_HIGHLIGHTED.getName());
					}
					else
					{
						applyRowStyle(i);
						tbNameCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
						tbValueCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
					}
				}
			}
		});
		
		applyRowStyle(row);
		setItemCountLabel();
		
		this.setVisible(true);
	}
	
	public void applyRowStyle(int row)
	{
		HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
		
		if ((row % 2) != 0)
    	{
			rf.setStyleName(row, oddRowStyle);
    	}
    	else 
    	{
    		rf.setStyleName(row, evenRowStyle);
    	}
	}
	
	public void applySelectedRowStyle(int row)
	{
		HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
		
    	rf.setStyleName(row, selectedRowStyle);
	}
	
	private void updateRowStyle()
	{
	    HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
	    
	    for (int row = 0; row < flexTable.getRowCount(); ++row) 
	    {
	    	if ((row % 2) != 0)
	    	{
	    		rf.setStyleName(row, oddRowStyle);
	    	}
	    	else 
	    	{
	    		rf.setStyleName(row, evenRowStyle);
	    	}
	    }
	}

	public void setRowStyle(String oddRowStyle, String evenRowStyle) 
	{
		this.oddRowStyle = oddRowStyle;
		this.evenRowStyle = evenRowStyle;

		updateRowStyle();
	}

	@UiHandler("bAdd")
	void onBAddClick(ClickEvent event) 
	{
		tbAddName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
		
		if (!SharedStringUtil.isEmpty(tbAddName.getText()))
		{
			TextBox tbName = new TextBox();
			tbName.setText(tbAddName.getText());
			
			TextBox tbValue = new TextBox();
			tbValue.setText(tbAddValue.getText());
			
			if (!SharedUtil.doesNameExistNVList(dem.getValue(), tbAddName.getText()))
			{
				addRow(tbName, tbValue, flexTable.getRowCount());
				scrollPanel.scrollToBottom();
				tbAddName.setText("");
				tbAddValue.setText("");
				tbAddName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
				labelMessage.setVisible(false);
				setItemCountLabel();
			}
			else
			{
				tbAddName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
				labelMessage.setText("Name already exists.");
				labelMessage.setVisible(true);
			}
		}
		else
		{
			tbAddName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
		}
	}
	
	@UiHandler("bDelete")
	void onBDeleteClick(ClickEvent event) 
	{
		deleteItem();
	}
	
	private List<Integer> getCBCheckedList()
	{
		List<Integer> indexList = new ArrayList<Integer>();
		boolean checked = false;
		
		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			CheckBox cb = (CheckBox) WidgetUtil.lookupWidget(flexTable, i, CHECKBOX_COLUMN);
			checked = cb.getValue();
			
			if (checked)
			{	
				indexList.add(i);
			}
		}
		
		return indexList;
	}
	
	private void deleteItem()
	{
		if (cbSelectAll.getValue())
		{
			flexTable.removeAllRows();
			cbSelectAll.setValue(false);
			setItemCountLabel();
		}
		else 
		{
			List<Integer> indexList = getCBCheckedList();
			
			for (int j = indexList.size() - 1; j >= 0; j--)
			{
				flexTable.removeRow(indexList.get(j));
				updateRowStyle();
				setItemCountLabel();
			}
		}
	}
	
	public void setSaveControllerHandler(SaveControllerHandler<DynamicEnumMap, DynamicEnumMap> saveControllerHandler)
	{
		this.saveControllerHandler = saveControllerHandler;
	}
	
	@UiHandler("bUpdate")
	void onBUpdateClick(ClickEvent event) 
	{
		HashSet<Integer> errorRows = checkDuplicate();
	
		if (errorRows.size() != 0)
		{
			Iterator<Integer> iter = errorRows.iterator();
			
			while (iter.hasNext()) 
			{
				int row = iter.next();
				TextBox tbName = (TextBox) flexTable.getWidget(row, NAME_COLUMN);
				tbName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_ERROR.getName());
			}
			
			setNotification(NotificationType.ERROR, "Fields highlighted in red are invalid.");
		}
		else
		{
			DynamicEnumMap dem = updateDynamicEnumMap();
			
			
//			HTTPCallConfigInterface hcc = HTTPCallConfig.createAndInit(null, FSConst.URIs.API_DYNAMIC_ENUM_MAP.getValue(), HTTPMethod.PATCH);
//			
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put(MetaToken.NAME.getName(), dem.getName());
//			map.put(APIParameters.NVPAIR_LIST.getName(), dem.getValue());
//			
//			hcc.setContentType(HTTPMimeType.APPLICATION_JSON);			
//			hcc.setContent(JSONClientUtil.toJSONMap(map).toString());
//			
//			new GenericRequestHandler<DynamicEnumMap>(hcc, ReturnType.DYNAMIC_ENUM_MAP,
//					new CallBackHandler<DynamicEnumMap>(new AsyncCallback<DynamicEnumMap>()
//			{
//
//				@Override
//				public void onFailure(Throwable caught) 
//				{
//					setNotification(NotificationType.ERROR, "Update unsuccessful.");
//				}
//	
//				@Override
//				public void onSuccess(DynamicEnumMap result) 
//				{
//					setNotification(NotificationType.INFORMATION, "Update successful.");
//					DynamicEnumMap dem = DynamicEnumMapManager.SINGLETON.addDynamicEnumMap(result);
//					ApplicationClientDAO.DEFAULT.fireEvent( new CRUDNVBaseEvent(new CRUDNVBaseDAO( CRUD.UPDATE, dem)));
//					//setDynamicEnumMap(dem);
//				}
//				
//			}));
			
			if (saveControllerHandler != null)
			{
				saveControllerHandler.actionSave(dem, new CallBackHandler<DynamicEnumMap>(new AsyncCallback<DynamicEnumMap>()
				{
					@Override
					public void onFailure(Throwable caught) 
					{
						setNotification(NotificationType.ERROR, "Update failed.");
					}
					
					@Override
					public void onSuccess(DynamicEnumMap result) 
					{
						setNotification(NotificationType.INFORMATION, "Update successful.");
						DynamicEnumMap dem = DynamicEnumMapManager.SINGLETON.addDynamicEnumMap(result);
						
						ApplicationClientDAO.DEFAULT.fireEvent( new CRUDNVBaseEvent(new CRUDNVBaseDAO( CRUD.UPDATE, dem)));
						//setDynamicEnumMap(dem);
					}
				}));
			}
		}
	}
	
	private DynamicEnumMap updateDynamicEnumMap()
	{
		ArrayList<NVPair> list = new ArrayList<NVPair>();
		
		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			TextBox tbName = (TextBox) WidgetUtil.lookupWidget(flexTable, i, NAME_COLUMN);
			TextBox tbValue = (TextBox) WidgetUtil.lookupWidget(flexTable, i, VALUE_COLUMN);
			
			list.add(new NVPair(tbName.getText(), tbValue.getText()));
		}
		
		dem.setValue(list);
		
		return dem;
	}
	
	@UiHandler("cbSelectAll")
	void onCbSelectAllClick(ClickEvent event)
	{
		boolean isChecked = cbSelectAll.getValue();
		
		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			TextBox tbNameCurrent = (TextBox) WidgetUtil.lookupWidget(flexTable, i, NAME_COLUMN);
			TextBox tbValueCurrent = (TextBox) WidgetUtil.lookupWidget(flexTable, i, VALUE_COLUMN);
			CheckBox cb = (CheckBox) WidgetUtil.lookupWidget(flexTable, i, CHECKBOX_COLUMN);
			cb.setValue(isChecked);
			
			if (isChecked)
			{
				applySelectedRowStyle(i);
				tbNameCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_HIGHLIGHTED.getName());
				tbValueCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_HIGHLIGHTED.getName());
			}
			else
			{
				applyRowStyle(i);
				tbNameCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
				tbValueCurrent.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			}
		}
	}
	
	private int getItemCount()
	{
		return flexTable.getRowCount();
	}
	
	private void setItemCountLabel()
	{
		labelItemCount.setText("Total: " + getItemCount());
	}
	
	/**
	 * This method checks for duplicate values in the name column. If duplicate values exist,
	 * returns a hash set of indexes that correspond to the rows with invalid names.
	 * @return
	 */
	private HashSet<Integer> checkDuplicate()
	{
		//	Algorithm
		//	1. Look up text box widget in first column which corresponds to enum name.
		//	2. If name is empty, add row index to the hash set of integers. 
		//	Otherwise, lookup the name in hash map with the name as the key and returns row index.
		//	3. If lookup returns null, add name and row index to hash map. 
		//	Otherwise, get the return value and current row index and add both to hash set.
		//	4. Return hash set which represents row indexes with errors.
		
		
		//	Return type which stores indexes of rows that are empty or duplicates.
		HashSet<Integer> errorIndexes = new HashSet<Integer>();
		
		//	Key is the name and value is the row.
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			TextBox tbName = (TextBox) WidgetUtil.lookupWidget(flexTable, i, NAME_COLUMN);
			
			//	Checks if the text box is empty, if yes adds the index to hash set.
			if (SharedStringUtil.isEmpty(tbName.getText()))
			{
				errorIndexes.add(i);
			}
			else
			{
				String name = tbName.getText();
				
				if (map.get(name) == null)
				{
					map.put(name, i);
					tbName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
				}
				else
				{
					errorIndexes.add(i);
					errorIndexes.add(map.get(name));
				}
			}
		}
		
		return errorIndexes;
	}

	/**
	 * Clears notification.
	 */
	@Override
	public void clear() 
	{
		setNotification(null, null);
	}

	/**
	 * Sets the notification based on type and message.
	 * @param type
	 * @param message
	 */
	@Override
	public void setNotification(NotificationType type, String message) 
	{
		if (!SharedStringUtil.isEmpty(message))
		{
			labelMessage.setVisible(true);
			labelMessage.setText(message);
			labelMessage.getElement().getStyle().setProperty("color", WidgetUtil.getNotificationColor(type));
		}
		else
		{
			labelMessage.setVisible(false);
			labelMessage.setText("");
		}
	}
	
	public void setCloseButtonVisible(boolean visible)
	{
		bClose.setVisible(visible);
	}
	
	public HandlerRegistration addCloseButtonClickHanlder(ClickHandler clickHandler)
	{
		return bClose.addClickHandler(clickHandler);
	}

	@Override
	public void applyCRUD(CRUD crud, NVBase<?> nvb)
	{
		if (nvb instanceof DynamicEnumMap)
		{
			switch(crud)
			{
				case CREATE:
					break;
				case DELETE:
					break;
				case READ:
					break;
				case UPDATE:
					if (nvb.getReferenceID().equals(dem.getReferenceID()))
					{
						// Update DEM value
						setDynamicEnumMap((DynamicEnumMap) nvb);
					}
					break;
				default:
					break;
			}
		}
	}
	
}