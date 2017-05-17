/*
 * Copyright (c) 2012-2017 ZoxWeb.com LLC.
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
package org.zoxweb.shared.widget;

import org.zoxweb.shared.util.GetName;
import org.zoxweb.shared.util.SetNameValue;

public class WidgetConst
{
	
	public enum CSSStyle
        implements GetName
    {
		TEXTBOX_DEFAULT("gwt-TextBox"),
		TEXTBOX_ERROR("gwt-TextBox-error"),
		TEXTBOX_HIGHLIGHTED("textBox-Highlighted"),
		DEFAULT_SHORTHAND_LINK("default-Shorthand-Link"),
		POPUP("popup"),
		
		;
	
		private String name;
		
		CSSStyle(String name)
        {
			this.name = name;
		}
		
		@Override
		public String getName()
        {
			return name;
		}
	}
	
	public enum ImageURL
        implements SetNameValue<String>
    {

		ADD("Add", "images/add-icon.png"),
		RESET("Reset", "images/desktop-toolbar-icons/refresh-icon.png"),
		SAVE("Save", "images/save-icon.png"),
		EDIT("Edit", "images/desktop-toolbar-icons/edit-icon.png"),
		CANCEL("Cancel", "images/cancel-icon.png"),
		OK("Ok", "images/save-icon.png"),
		DELETE("Delete", "images/desktop-toolbar-icons/delete-icon.png"),
		PROPERTIES("Properties", "images/desktop-toolbar-icons/properties-icon.png"),
		REFRESH("Refresh", "images/desktop-toolbar-icons/refresh-icon.png"),
		SHARE("Share", "images/desktop-toolbar-icons/share-icon.png"),
		UPDATE("Update", "images/desktop-toolbar-icons/update-icon.png"),
		PREVIEW("Preview", "images/desktop-toolbar-icons/preview-icon.png"),
		CLEAR("Clear", "images/clear-icon.png"),
		SETTINGS("Settings", "images/settings-icon.png"),
		EXPORT_FORM_ICON("Export Form", "images/export-form-icon.png"),
		
		UP_ARROW("Move Up", "images/arrow-up.png"),
		DOWN_ARROW("Move Down", "images/arrow-down.png"),
		LEFT_ARROW("Move Left", "images/arrow-left.png"),
		RIGHT_ARROW("Move Right", "images/arrow-right.png"),
		
		UP_ARROW_HIGHLIGHTED("Move Up", "images/arrow-up-mouseOver.png"),
		DOWN_ARROW_HIGHLIGHTED("Move Down", "images/arrow-down-mouseOver.png"),
		LEFT_ARROW_HIGHLIGHTED("Move Left", "images/arrow-left-mouseOver.png"),
		RIGHT_ARROW_HIGHLIGHTED("Move Right", "images/arrow-right-mouseOver.png"),
		
		SLIDER_FIRST("First", "images/slider-first.png"),
		SLIDER_LAST("Last", "images/slider-last.png"),
		SLIDER_PREVIOUS("Previous", "images/slider-previous.png"),
		SLIDER_NEXT("Next", "images/slider-next.png"),
		
		;
		
		private String name;
		private String value;
		
		ImageURL(String name, String value)
        {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String getName() 
		{
			return name;
		}
		
		@Override
		public void setName(String name)
        {
			this.name = name;
		}		
		
		@Override
		public String getValue()
        {
			return value;
		}
		
		@Override
		public void setValue(String value)
        {
			this.value = value;
		}
		
	}
	
	public static final String HREF_DEFAULT_TEXT = "Click here to edit";
	public static final String HREF_DONE_TEXT = "Done";
	public static final String HREF_DONE_TITLE = "Click here when done";
	
}