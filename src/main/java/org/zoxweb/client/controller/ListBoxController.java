package org.zoxweb.client.controller;

import org.zoxweb.shared.util.SetValue;
import org.zoxweb.shared.util.ValueToString;

import com.google.gwt.user.client.ui.ListBox;

public abstract class ListBoxController<V>
implements ValueToString<V>, SetValue<V>
{
	private ListBox lb;
	private V values[];
	
	
	@SafeVarargs
	public ListBoxController(ListBox lb, V ...vals)
	{
		this.lb = lb;
		setValues(vals);
	}
	
	
	public V getValue()
	{
		
		return values[lb.getSelectedIndex()];
		
		
//		int length = lb.getSelectedIndex();
//		for(int i = 0; i < length; i++)
//		{
//			if(lb.getItemText(i).equals(toString(values[i])))
//			{
//				return values[i];
//			}
//		}
//		return null;
	}
	
	public void setValue(V value)
	{
		int length = lb.getItemCount();
		String toMatch = toString(value);
		for(int i = 0; i < length; i++)
		{
			if(lb.getItemText(i).equals(toMatch))
			{
				lb.setSelectedIndex(i);
				break;
			}
		}
	}
	
	public V[] getValues()
	{
		return values;
	}
	
	
	@SuppressWarnings("unchecked")
	public void setValues(V ...vals)
	{
		values = vals;
		lb.clear();
		for (V v : values)
		{
			lb.addItem(toString(v));
		}
	
	}
	
	public ListBox getListBox()
	{
		return lb;
	}
	
}
