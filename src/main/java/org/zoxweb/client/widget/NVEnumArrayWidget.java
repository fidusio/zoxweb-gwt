package org.zoxweb.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Label;

@SuppressWarnings("serial")
public class NVEnumArrayWidget 
	extends NVBaseWidget<List<Enum<?>>>
{

	private static NVEnumArrayWidgetUiBinder uiBinder 
		= GWT.create(NVEnumArrayWidgetUiBinder.class);
	
	interface NVEnumArrayWidgetUiBinder
		extends UiBinder<Widget, NVEnumArrayWidget> 
	{
	}
	
	@UiField PushButton pbAdd;
	@UiField PushButton pbUpdate;
	@UiField PushButton pbRemove;
	@UiField PushButton pbRemoveAll;
	@UiField PushButton pbMoveUp;
	@UiField PushButton pbMoveDown;
	@UiField(provided = true) ListBox lbFullList;
	@UiField ListBox lbSelectedList;
	@UiField HorizontalPanel hpMain;
	@UiField VerticalPanel vpButtons;
	@UiField VerticalPanel vpOptions;
	@UiField VerticalPanel vpSelections;
	@UiField Label labelSelectedItems;
	@UiField Label labelOptions;

	public NVEnumArrayWidget(NVConfig nvc) 
	{
		super(nvc);
				
		lbFullList = new ListBox();
		lbFullList.setMultipleSelect(true);
		initWidget(uiBinder.createAndBindUi(this));
		
		if (!nvc.isEditable())
		{
			setReadOnly(true);
		}
		
		Enum<?>[] enumValues = (Enum<?>[]) nvConfig.getMetaTypeBase().getEnumConstants();
		
		for (Enum<?> value : enumValues)
		{
			lbFullList.addItem(value.name());
		}
		
		pbAdd.setTitle("Select item(s) to add.");
		pbUpdate.setTitle("Update selected item");
		pbRemove.setTitle("Remove selected item.");
		pbRemoveAll.setTitle("Remove all selected items.");
		pbMoveUp.setTitle("Move selected item up.");
		pbMoveDown.setTitle("Move selected item down.");
	}

	@UiHandler("pbAdd")
	void onPBAddClick(ClickEvent event) 
	{
		for (int i = 0; i < lbFullList.getItemCount(); i++)
		{
			if (lbFullList.isItemSelected(i))
			{
				lbSelectedList.addItem(lbFullList.getItemText(i));
			}
		}
	}

	@UiHandler("pbUpdate")
	void onPBUpdateClick(ClickEvent event) 
	{
		int index = lbSelectedList.getSelectedIndex();
		String text = null;
		
		if (lbSelectedList.getSelectedIndex() != -1)
		{
			text = lbFullList.getItemText(lbFullList.getSelectedIndex());
		}
		
		if (text != null)
		{
			lbSelectedList.setItemText(index, text);
		}
	}
	
	@UiHandler("pbRemove")
	void onPBRemoveClick(ClickEvent event)
	{
		if (lbSelectedList.getSelectedIndex() != -1)
		{
			lbSelectedList.removeItem(lbSelectedList.getSelectedIndex());
		}
	}
	
	@UiHandler("pbRemoveAll")
	void onPBRemoveAllClick(ClickEvent event) 
	{
		lbSelectedList.clear();
	}

	@UiHandler("pbMoveUp")
	void onPBMoveUpClick(ClickEvent event) 
	{
		if (lbSelectedList.getSelectedIndex() != 0 || lbSelectedList.getSelectedIndex() != -1)
		{
			int initialIndex = lbSelectedList.getSelectedIndex();
			String initialText = null;
			
			if (initialIndex != -1)
			{
				initialText = lbSelectedList.getItemText(initialIndex);
			}
			
			int finalIndex = lbSelectedList.getSelectedIndex() - 1;
			String finalText = null;

			if (finalIndex > 0)
			{
				finalText = lbSelectedList.getItemText(finalIndex);
			}
			
			if (initialText != null && finalText != null)
			{
				lbSelectedList.setItemText(initialIndex, finalText);
				lbSelectedList.setItemText(finalIndex, initialText);
				lbSelectedList.setSelectedIndex(finalIndex);
			}
		}
	}
	
	@UiHandler("pbMoveDown")
	void onPBMoveDownClick(ClickEvent event) 
	{
		if (lbSelectedList.getSelectedIndex() != lbSelectedList.getItemCount() || lbSelectedList.getSelectedIndex() != -1)
		{
			int initialIndex = lbSelectedList.getSelectedIndex();
			String initialText = null;
			
			if (initialIndex != -1)
			{
				initialText = lbSelectedList.getItemText(initialIndex);
			}
			
			int finalIndex = lbSelectedList.getSelectedIndex() + 1;
			String finalText = lbSelectedList.getItemText(finalIndex);
			
			if (initialText != null && finalText != null)
			{
				lbSelectedList.setItemText(initialIndex, finalText);
				lbSelectedList.setItemText(finalIndex, initialText);
				lbSelectedList.setSelectedIndex(finalIndex);
			}
			
		}
	}

	@Override
	public void setWidgetValue(List<Enum<?>> value) 
	{
		if (value != null)
		{
			lbSelectedList.clear();
			
			for (int i = 0; i < value.size(); i++)
			{				
				lbSelectedList.addItem(value.get(i).name());
			}
		}
	}

	@Override
	public List<Enum<?>> getWidgetValue() 
	{
		List<Enum<?>> list = new ArrayList<Enum<?>>();
		
		for (int i = 0; i < lbSelectedList.getItemCount(); i++)
		{
			Enum<?> value = SharedUtil.lookupEnum(lbSelectedList.getItemText(i), (Enum<?>[]) nvConfig.getMetaTypeBase().getEnumConstants());
		
			if (value != null)
			{
				list.add(value);
			}
		}
		
		return list;
	}


	@Override
	public Widget getWidget() 
	{
		return null;
	}

	@Override
	public void setWidgetValue(String value) 
	{
		
	}
	
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
		
		if (readOnly)
		{
			vpOptions.setVisible(false);
			vpButtons.setVisible(false);
			vpSelections.setVisible(true);
			lbSelectedList.setEnabled(false);
			
			hpMain.setSize("15EM", "8EM");
			hpMain.setVisible(true);
			lbSelectedList.setSize("100%", "100%");
		}
		else
		{
			vpOptions.setVisible(true);
			vpButtons.setVisible(true);
			vpSelections.setVisible(true);
			lbSelectedList.setEnabled(true);
			
			hpMain.setSize("15EM", "16EM");
			hpMain.setVisible(true);
			lbSelectedList.setSize("100%", "100%");
		}
		
	}

	@Override
	public void clear() 
	{
		lbSelectedList.clear();
	}
	
}