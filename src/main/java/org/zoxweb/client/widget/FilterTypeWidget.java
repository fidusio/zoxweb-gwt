package org.zoxweb.client.widget;

import org.zoxweb.shared.filters.FilterType;
import org.zoxweb.shared.filters.ValueFilter;
import org.zoxweb.shared.util.DynamicEnumMap;
import org.zoxweb.shared.util.DynamicEnumMapManager;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

/**
 * This is filter type widget which builds a list box of value filters that include:
 * basic filters and dynamic enum map filters.
 * @author mzebib
 *
 */
public class FilterTypeWidget
	extends Composite
{
	
	private ListBox lbFilterType;
	private int basicFilterCount;
	
	public FilterTypeWidget()
	{
		lbFilterType = new ListBox();
		lbFilterType.setSize("8EM", "2EM");
		lbFilterType.setVisibleItemCount(1);
		lbFilterType.addItem("Select:");
		lbFilterType.addItem("Filter Types");
		
		for (FilterType ft : FilterType.values())
		{
			if (ft != FilterType.ENCRYPT || ft != FilterType.ENCRYPT_MASK || ft != FilterType.HIDDEN || ft != FilterType.BINARY)
			{
				lbFilterType.addItem(ft.name());
			}
		}
		
		basicFilterCount = lbFilterType.getItemCount();
		
		lbFilterType.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		lbFilterType.getElement().getElementsByTagName("option").getItem(1).setAttribute("disabled", "disabled");
		
		lbFilterType.addItem("Dynamic Enums");
		lbFilterType.getElement().getElementsByTagName("option").getItem(lbFilterType.getItemCount() - 1).setAttribute("disabled", "disabled");		
		
		for (DynamicEnumMap dem : DynamicEnumMapManager.SINGLETON.getAll())
		{
			lbFilterType.addItem(dem.getDisplayName());
		}
		
		initWidget(lbFilterType);
	}
	
	public HandlerRegistration addListBoxChangeHandler(ChangeHandler changeHandler)
	{
		return lbFilterType.addChangeHandler(changeHandler);
	}
	
	/**
	 * This method returns the selected value filter.
	 * @return ValueFilter
	 */
	@SuppressWarnings("unchecked")
	public ValueFilter<String, String> getSelectedFilter()
	{
		int index = lbFilterType.getSelectedIndex();
		
		if (index < basicFilterCount)
		{
			return (ValueFilter<String, String>) SharedUtil.lookupEnum(FilterType.values(), lbFilterType.getItemText(index));
		}
		else
		{
			return DynamicEnumMapManager.SINGLETON.lookup(lbFilterType.getItemText(index));
		}
	}
	
	/**
	 * This method sets the selected value filter.
	 * @param vf
	 */
	public void setSelectedFilter(ValueFilter<String, String> vf)
	{
		for (int i = 0; i < lbFilterType.getItemCount(); i++)
		{
			if (vf instanceof FilterType && SharedUtil.lookupEnum(FilterType.values(), lbFilterType.getValue(i)) == vf)
			{
				lbFilterType.setItemSelected(i, true);
				break;
			}
			
			if (vf instanceof DynamicEnumMap && DynamicEnumMapManager.SINGLETON.lookup(lbFilterType.getValue(i)) == vf)
			{
				lbFilterType.setItemSelected(i, true);
				break;
			}
		}
	}
	
	public void setReadOnly(boolean readOnly)
	{
		lbFilterType.setEnabled(!readOnly);
	}
	
}