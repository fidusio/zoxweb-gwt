package org.zoxweb.client.widget.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The canonical ID widget.
 * @author mzebib
 *
 */
public class CanonicalIDWidget
	extends Composite 
{

	private static CanonicalIDWidgetUiBinder uiBinder 
		= GWT.create(CanonicalIDWidgetUiBinder.class);
	
	interface CanonicalIDWidgetUiBinder
		extends UiBinder<Widget, CanonicalIDWidget> 
	{
	}
	
	@UiField TextBox tbCanonicalID;

	public CanonicalIDWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public String getCanonicalID()
	{
		return tbCanonicalID.getText();
	}
	
	public void setCanonicalID(String canonicalID)
	{
		tbCanonicalID.setText(canonicalID);
	}
	
	public TextBox getCanonicalIDTextBox()
	{
		return tbCanonicalID;
	}
	
	public void setReadOnly(boolean readOnly)
	{
		tbCanonicalID.setReadOnly(readOnly);
	}
	
}