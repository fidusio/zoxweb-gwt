package org.zoxweb.client.controller;

import org.zoxweb.shared.util.SetValue;

import com.google.gwt.user.client.ui.RadioButton;

public class RadioButtonController
implements SetValue<RadioButton>
{
	private RadioButton[] rbs;
	
	
	public  RadioButtonController(RadioButton ...rbs)
	{
		this.rbs = rbs;
	}
	@Override
	public RadioButton getValue() {
		// TODO Auto-generated method stub
		for (RadioButton rb : rbs)
		{
			if (rb.getValue())
				return rb;
		}
		
		return null;
	}

	@Override
	public void setValue(RadioButton value) {
		// TODO Auto-generated method stub
		
	}

	
}
