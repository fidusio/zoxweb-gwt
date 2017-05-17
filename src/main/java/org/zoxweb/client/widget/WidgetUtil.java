/*
 * Copyright 2012 ZoxWeb.com LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zoxweb.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NotificationType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mzebib
 *
 */
public class WidgetUtil 
{
	/**
	 * 
	 */
	 private WidgetUtil()
	 {
	    	
	 }
	 
	/**
	 * Invoke get on the uri the url=GWT.getModuleBaseURL() + uri it the uri is a full url it will be used as is
	 * @param uri
	 */
	public static void getURL(String uri)
	{
		Window.Location.assign(formatURL(uri));
	}
	 
	public static String formatURL(String uri)
	{
		if (uri.indexOf("://") == -1)
		{
			uri = GWT.getModuleBaseURL() + uri;
		}
		
		return uri;
	}
	 
	/**
     * The HTML templates used to render the cell.
     */
    interface Templates 
    	extends SafeHtmlTemplates
    {
      /**
       * The template for this Cell, which includes styles and a value.
       * 
       * @param styles the styles to include in the style attribute of the div
       * @param value the safe value. Since the value type is {@link SafeHtml},
       *          it will not be escaped before including it in the template.
       *          Alternatively, you could make the value type String, in which
       *          case the value would be escaped.
       * @return a {@link SafeHtml} instance
       */
      @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
      SafeHtml cell(SafeStyles styles, SafeHtml value);
    }

    /**
     * Create a singleton instance of the templates used to render the cell.
     */
    public final static Templates STYLE_TEMPLATES = GWT.create(Templates.class);
	
	/**
	 * 
	 * @param color
	 * @param text
	 * @return SafeHtml
	 */
    public static SafeHtml renderText(String color, String text)
    {
    	SafeHtml safeValue = SafeHtmlUtils.fromString(color);
    	// Use the template to create the Cell's html.
    	SafeStyles styles = SafeStylesUtils.forTrustedColor(safeValue.asString());
    	return STYLE_TEMPLATES.cell(styles, SafeHtmlUtils.fromString(text));
    }
    
    /**
     * 
     * @param type
     * @return color 
     */
    public static String getNotificationColor(NotificationType type)
    {
    	 switch (type)
    	 {
			case ERROR:
				return "red";
			case INFORMATION:
				return "blue";
			default:
    	 }
    	 
    	 return "grey";
    }
    
    
    /**
     * This is javascript code to log messages to the console.
     * Note: This method is used on the client side only.
     * @param message
     */
    native public static void logToConsole(String message) 
    /*-{
    	try 
	    {
	        console.log(message);
	    } 
	    catch (e) 
	    {
	    	
	    }
	}-*/;

    /**
     * Returns list item index based on list name.
     * @param lb
     * @param itemName
     * @return index
     */
	public static int indexOfListItemByName(ListBox lb, String itemName)
	{
		if (itemName != null)
		{
			for (int i = 0; i < lb.getItemCount(); i++)
			{
				if (itemName.equals(lb.getItemText(i)))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns list item index based on item value.
	 * @param lb
	 * @param itemValue
	 * @return index
	 */
	public static int indexOfListItemByValue(ListBox lb, String itemValue)
	{
		if (itemValue != null)
		{
			for (int i = 0; i < lb.getItemCount(); i++)
			{
				if (itemValue.equals(lb.getValue(i)))
				{
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public static <T> boolean isNull(ValueBoxBase<T> valueBox)
	{
		if (valueBox.getValue() == null && valueBox.getText().length() == 0)
		{
			return true;
		}
		
		return false;
	}
	
//	private static NVBaseWidget<?> lookupNVBaseWidget(FlexTable flexTable, NVConfig nvc)
//	{
//		if (flexTable != null && nvc != null)
//		{
//			return lookupNVBaseWidget(flexTable, nvc.getName());
//		}
//		
//		return null;
//	}

//	private static NVBaseWidget<?> lookupNVBaseWidget(FlexTable flexTable, String nvcName)
//	{
//		if (flexTable != null && nvcName != null)
//		{
//			for (int row = 0; row < flexTable.getRowCount(); row++)
//			{
//				if (flexTable.getCellCount(row) > 1)
//				{
//					Widget widget = flexTable.getWidget(row, 1);
//					
//					if (widget != null & widget instanceof NVBaseWidget)
//					{
//						NVBaseWidget<?> nvbw = (NVBaseWidget<?>) widget;
//						
//						if (nvbw.getNVConfig() != null && nvcName.equals(nvbw.getNVConfig().getName()))
//						{
//							return nvbw;
//						}
//					}
//				}
//			}
//		}
//		
//		return null;
//	}
	
//	public static NVBaseWidget<?> lookupNVBaseWidget(ComplexPanel panel, NVConfig nvc)
//	{
//		NVBaseWidget<?> ret = null;
//		
//		if (panel != null && nvc != null)
//		{			
//			for (int i = 0; i < panel.getWidgetCount(); i++)
//			{
//				Widget widget = panel.getWidget(i);
//				
//				if (widget != null)
//				{
//					if (widget instanceof NVBaseWidget)
//					{
//						NVBaseWidget<?> nvbw = (NVBaseWidget<?>) widget;
//						
//						if (nvbw.getNVConfig() != null && nvc.getName().equals(nvbw.getNVConfig().getName()))
//						{
//							ret = nvbw;
//						}
//					}
//					else if (widget instanceof FlexTable)
//					{
//						ret = lookupNVBaseWidgetByNVC((FlexTable) widget, nvc);
//					}
//				}
//			}
//		}
//		
//		return ret;
//	}
	
	public static NVBaseWidget<?> lookupNVBaseWidget(ComplexPanel panel, NVConfig nvc)
	{		
		if (panel != null && nvc != null)
		{			
			for (int i = 0; i < panel.getWidgetCount(); i++)
			{
				Widget widget = panel.getWidget(i);
				
				if (widget != null)
				{
					if (widget instanceof NVBaseWidget)
					{
						NVBaseWidget<?> nvbw = (NVBaseWidget<?>) widget;
						
						if (nvbw.getNVConfig() != null && nvc.getName().equals(nvbw.getNVConfig().getName()))
						{
							return nvbw;
						}
					}
					else if (widget instanceof FlexTable)
					{
						NVBaseWidget<?> nvbw = lookupNVBaseWidgetByNVC((FlexTable) widget, nvc);
						
						if (nvbw != null)
						{
							return nvbw;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static NVBaseWidget<?> lookupNVBaseWidgetByNVC(FlexTable flexTable, NVConfig nvc)
	{
		if (flexTable != null && flexTable.getRowCount() > 0 && nvc != null)
		{
			for (int row = 0; row < flexTable.getRowCount(); row++)
			{
				int columnCount = flexTable.getCellCount(row);
				
				if (columnCount > 0)
				{
					for (int column = 0; column < columnCount; column++)
					{
						Widget widget = flexTable.getWidget(row, column);
						
						if (widget != null & widget instanceof NVBaseWidget)
						{
							NVBaseWidget<?> nvbw = (NVBaseWidget<?>) widget;
							
							if (nvbw.getNVConfig() != null && nvc.getName().equals(nvbw.getNVConfig().getName()))
							{
								return nvbw;
							}
						}
						
					}
				}
			}
		}
		
		return null;
	}
	
	public static NVBaseWidget<?> lookupNVBaseWidgetByNVC(FlexTable flexTable, NVConfig nvc, int column)
	{
		if (flexTable != null && nvc != null && column > -1)
		{
			for (int row = 0; row < flexTable.getRowCount(); row++)
			{
				if (flexTable.getCellCount(row) > column)
				{
					Widget widget = flexTable.getWidget(row, column);
					
					if (widget != null & widget instanceof NVBaseWidget)
					{
						NVBaseWidget<?> nvbw = (NVBaseWidget<?>) widget;
						
						if (nvbw.getNVConfig() != null && nvc.getName().equals(nvbw.getNVConfig().getName()))
						{
							return nvbw;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static Widget lookupWidget(FlexTable flexTable, int row, int column)
	{
		return flexTable.getWidget(row, column);
	}
	
	public static List<Integer> getRowsByWidgetName(FlexTable flexTable, String name)
	{
		if (flexTable != null && name != null)
		{
			List<Integer> list = new ArrayList<Integer>();
			
			for (int row = 0; row < flexTable.getRowCount(); row++)
			{
				if (flexTable.getCellCount(row) > 1)
				{
					Widget widget = flexTable.getWidget(row, 1);
					
					if (widget instanceof NVBaseWidget)
					{
						NVBaseWidget<?> nvbw = (NVBaseWidget<?>) widget;
						
						if (nvbw.getName() != null && name.equals(nvbw.getName()))
						{
							list.add(row);
						}
					}
				}
			}
			
			return list;
		}
		
		return null;
	}
	
	public static int getRowIndexByWidget(FlexTable flexTable, Widget widget, int columnIndex)
	{
		if (flexTable != null && widget != null)
		{
			for (int row = 0; row < flexTable.getRowCount(); row++)
			{
				if (flexTable.getCellCount(row) > columnIndex)
				{
					if (widget == flexTable.getWidget(row, columnIndex))
					{
						return row;
					}
				}
			}
		}
		
		return -1;
	}
	
	
	public static void popupWindow(String url)
	{
		if (url != null)
		{				
			Window.open(url, "_blank", "");
			//Window.open(url, "_parent", "");
		}
	}
	
	public static String getDynamicEnumDisplayName(String name)
	{		
		return name.substring(name.indexOf(":") + 1, name.length());
	}
	
}