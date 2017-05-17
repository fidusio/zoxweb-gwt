package org.zoxweb.client.widget.custom;

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.ValueFilterHandler;
import org.zoxweb.client.widget.ValueFilterSetValidator;
import org.zoxweb.shared.data.DataConst;
import org.zoxweb.shared.filters.FilterType;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;

/**
 * The email widget.
 * @author mzebib
 *
 */
@SuppressWarnings("serial")
public class EmailWidget
	extends NVBaseWidget<NVPair>
{

	private static EmailWidgetUiBinder uiBinder 
		= GWT.create(EmailWidgetUiBinder.class);
	
	interface EmailWidgetUiBinder 
		extends UiBinder<Widget, EmailWidget>
	{
	}
	
	interface Style
		extends CssResource
	{
		String invalid();
	}

	@UiField Style style;
	@UiField Anchor hrefLink;
	@UiField HorizontalPanel hpEmail;
	
	private ListBox lbType = new ListBox();
	private TextBox tbEmail = new TextBox();
	private ValueFilterSetValidator<String> setValidator = new ValueFilterSetValidator<String>();
	private ValueFilterHandler<String> emailValidator;
	
	public EmailWidget(NVPair nvp)
	{
		this();
		
		if (nvp != null)
		{
			setValue(nvp);
		}
	}
	
	public EmailWidget() 
	{
		super(null, null, true);
		initWidget(uiBinder.createAndBindUi(this));

		for (DataConst.EmailType type : DataConst.EmailType.values())
		{
			lbType.addItem(type.getName(), type.getName());
		}
		
		tbEmail.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "e.g. someone@example.com");
        		}
        	}
        });
	
		lbType.setSize("7EM", "100%");
		tbEmail.setSize("18EM", "1.5EM");
		
		hpEmail.add(lbType);
		hpEmail.add(tbEmail);
		
		emailValidator = new ValueFilterHandler<String>(tbEmail, FilterType.EMAIL, style.invalid());
        setValidator.add(emailValidator);
        
        hrefLink.getElement().getStyle().setCursor(Cursor.POINTER);
        
        hpEmail.setVisible(false);
	}

	@Override
	public Widget getWidget()
	{
		return this;
	}

	@Override
	public void setWidgetValue(NVPair value) 
	{
		if (value != null)
		{
			setSelectedType(value.getName());
			tbEmail.setValue(value.getValue());
			
			if (!SharedStringUtil.isEmpty(value.getValue()))
			{

				hrefLink.setText(value.getValue());
				hrefLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			else
			{
				setDefaultEditMessage();
			}
		}
		else
		{
			clear();
		}
	}

	@Override
	public NVPair getWidgetValue() 
	{
		NVPair nvp = new NVPair();
		
		nvp.setName(getSelectedType());
		
		if (!SharedStringUtil.isEmpty(tbEmail.getValue()))
		{
			nvp.setValue(tbEmail.getValue());
		}
		
		if (!SharedStringUtil.isEmpty(nvp.getValue()))
		{
			hrefLink.setText(nvp.getValue());
			hrefLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
		}
		else
		{
			setDefaultEditMessage();
		}
		
		return nvp;
	}

	@Override
	public void setWidgetValue(String value) 
	{
		
	}
	
	private void setSelectedType(String type)
	{
		int index = 0;
		
		if (type != null)
		{
			for (int i = 0; i < lbType.getItemCount(); i++)
			{
				if (lbType.getValue(i).equals(type))
				{
					index = i;
					break;
				}
			}
		}
		
		lbType.setSelectedIndex(index);
	}
	
	private String getSelectedType()
	{
		if (lbType.getSelectedIndex() != -1)
		{
			return lbType.getValue(lbType.getSelectedIndex());
		}
		
		return lbType.getValue(0);
	}

	@UiHandler("hrefLink")
	void onHrefLinkClick(ClickEvent event) 
	{
		if (!isContentVisible())
		{
			setContentVisible(true);
			setDefaultEditMessage();
		}
		else if (isValid())
		{
			setContentVisible(false);
			getWidgetValue();
		}
	}

	public boolean isValid()
	{
		return setValidator.areAllValid(true);
	}
	
	@Override
	public void clear()
	{
		lbType.setSelectedIndex(0);
		tbEmail.setValue("");
	}
	
	public Anchor getHrefShortHand()
	{
		return hrefLink;
	}
	
	public boolean isContentVisible()
	{
		return hpEmail.isVisible();
	}
	
	public void setContentVisible(boolean value)
	{
		hpEmail.setVisible(value);
	}
	
	public void setDefaultEditMessage()
	{
		hrefLink.setText(WidgetConst.HREF_DONE_TEXT);
		hrefLink.setTitle(WidgetConst.HREF_DONE_TITLE);
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		lbType.setEnabled(!readOnly);
		tbEmail.setEnabled(!readOnly);
	}

}