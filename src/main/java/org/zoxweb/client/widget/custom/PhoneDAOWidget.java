package org.zoxweb.client.widget.custom;

import org.zoxweb.client.widget.NVDynamicEnumWidget;
import org.zoxweb.client.widget.NVEntityIntermediateWidget;
import org.zoxweb.client.widget.ValueFilterHandler;
import org.zoxweb.client.widget.ValueFilterSetValidator;
import org.zoxweb.shared.data.PhoneDAO;
import org.zoxweb.shared.data.SharedDataUtil;
import org.zoxweb.shared.filters.NumberFilter;
import org.zoxweb.shared.util.Const;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVConfigMapUtil;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

/**
 * The PhoneDAO widget.
 * @author mzebib
 *
 */
@SuppressWarnings("serial")
public class PhoneDAOWidget 
	extends NVEntityIntermediateWidget
{
	
	private static PhoneDAOWidgetUiBinder uiBinder 
		= GWT.create(PhoneDAOWidgetUiBinder.class);
	
	interface PhoneDAOWidgetUiBinder
		extends UiBinder<Widget, PhoneDAOWidget> 
	{
	}
	
	interface Style
		extends CssResource
	{
		String invalid();
	}

	@UiField Style style;
	@UiField HorizontalPanel hpLinkPlaceHolder;
	@UiField HorizontalPanel hpPhoneNumber;
	@UiField VerticalPanel vpMain;
	@UiField HorizontalPanel hpPhoneType;
	@UiField VerticalPanel vpPhone;
	@UiField HorizontalPanel hpName;

	private TextBox tbName = new TextBox();
	private NVDynamicEnumWidget phoneType;
	private NVDynamicEnumWidget countryCode;
	private TextBox tbAreaCode = new TextBox();
	private TextBox tbNumber = new TextBox();
	private TextBox tbExtension = new TextBox();
	
	private ValueFilterSetValidator<String> setValidator = new ValueFilterSetValidator<String>();
	private ValueFilterHandler<String> areaCodeValidator;
	private ValueFilterHandler<String> numberValidator;
	
	public PhoneDAOWidget(PhoneDAO phone)
	{
		this(phone, false);
	}
	
	public PhoneDAOWidget(PhoneDAO phone, boolean showShortHand)
	{
		this((NVConfigEntity) phone.getNVConfig(), showShortHand);
		
		if (phone != null)
		{
			setValue(phone);
		}
	}
	
	public PhoneDAOWidget(NVConfigEntity nvce)
	{
		this(nvce, false);
	}
	
	public PhoneDAOWidget(NVConfigEntity nvce, boolean showShortHand)
	{
		super();
		initWidget(uiBinder.createAndBindUi(this));
		
		if (showShortHand)
		{
			setContentVisible(false);
			
			anchorLink.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event) 
				{
					if (!isContentVisible())
					{
						setContentVisible(true);
						setLinkDefaultText();
					}
					else
					{
						if (getWidgetValue() != null)
						{
							setContentVisible(false);
						}
					}
				}
			});
		}
		else
		{
			hideHyperLink();
		}
		
		hpLinkPlaceHolder.add(anchorLink);
		
		phoneType = new NVDynamicEnumWidget(PhoneDAO.Param.PHONE_TYPE.getNVConfig(), Const.NVDisplayProp.VALUE, false);
		countryCode = new NVDynamicEnumWidget(PhoneDAO.Param.COUNTRY_CODE.getNVConfig(), Const.NVDisplayProp.VALUE, false);
		
		areaCodeValidator = new ValueFilterHandler<String>(tbAreaCode, NumberFilter.SINGLETON, style.invalid());
        setValidator.add(areaCodeValidator);

		numberValidator = new ValueFilterHandler<String>(tbNumber, NumberFilter.SINGLETON, style.invalid());
        setValidator.add(numberValidator);

        tbAreaCode.addKeyPressHandler(new KeyPressHandler()
        {
			@Override
			public void onKeyPress(KeyPressEvent event) 
			{
				if (event.getCharCode() != '\0')
				{
					String input = "" + event.getCharCode();
					
					if (!NumberFilter.SINGLETON.isValid(input))
					{					
						tbAreaCode.cancelKey();
					}
				}
			}
        	
        }); 
        
        tbNumber.addKeyPressHandler(new KeyPressHandler()
        {
			@Override
			public void onKeyPress(KeyPressEvent event) 
			{
				if (event.getCharCode() != '\0')
				{
					String input = "" + event.getCharCode();
					
					if (!NumberFilter.SINGLETON.isValid(input))
					{					
						tbNumber.cancelKey();
					}
				}
			}
        	
        }); 
		
		tbName.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Phone description (optional)");
        		}
        	}
        });
		
		tbName.setSize("100%", "1.5EM");
		
		hpName.add(tbName);
		
		phoneType.setEditVisible(false);
		phoneType.setSize("5EM", "2EM");
		phoneType.getWidget().setSize("5EM", "2EM");
		
		countryCode.setEditVisible(false);
		countryCode.setSize("5EM", "2EM");
		countryCode.getWidget().setSize("5EM", "2EM");
		
		tbAreaCode.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Area code");
        		}
        	}
        });
		
		tbAreaCode.setSize("5EM", "1.5EM");
		
		tbNumber.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Number");
        		}
        	}
        });
		
		tbNumber.setSize("6EM", "1.5EM");
		
		tbExtension.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Ext.");
        		}
        	}
        });
		
		tbExtension.setSize("2EM", "1.5EM");
		
		hpPhoneType.add(phoneType);
		hpPhoneNumber.add(countryCode);
		hpPhoneNumber.add(tbAreaCode);
		hpPhoneNumber.add(tbNumber);
		hpPhoneNumber.add(tbExtension);
	}
	
	@Override
	public Widget getWidget() 
	{
		return this;
	}
	
	@Override
	public void setWidgetValue(NVEntity value) 
	{
		currentNVE = value;
		
		if (currentNVE != null)
		{
			PhoneDAO phone = (PhoneDAO) currentNVE;
			
			tbName.setValue(phone.getName());
			phoneType.setValue(phone.getPhoneType());
			countryCode.setValue(phone.getCountryCode());
			tbAreaCode.setValue(phone.getAreaCode());
			tbNumber.setValue(phone.getNumber());
			tbExtension.setValue(phone.getExtension());
			
			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(phone, getNVConfigAttributesMap(), false)))
			{
				setHrefText(phone);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			else
			{
				setLinkDefaultText();
			}
		}
		else
		{
			clear();
		}
	}
	
	@Override
	public NVEntity getWidgetValue()
	{
		PhoneDAO phone = null;
		
		if (isValid())
		{
			if (currentNVE != null)
			{
				phone = (PhoneDAO) currentNVE;
			}
			else
			{	
				phone = new PhoneDAO();
			}
			
			phone.setName(tbName.getValue());
			phone.setPhoneType(phoneType.getValue());
			phone.setCountryCode(countryCode.getValue());
			phone.setAreaCode(tbAreaCode.getValue());
			phone.setNumber(tbNumber.getValue());
			phone.setExtension(tbExtension.getValue());
			
			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(phone, getNVConfigAttributesMap(), false)))
			{
				setHrefText(phone);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			else
			{
				setLinkDefaultText();
			}
		}
		
		return phone;
	}
	
	private boolean isValid()
	{
		boolean ret = false;
		
		if (!isMandatory() &&
				(!areaCodeValidator.isValid() && !numberValidator.isValid()))
		{
			tbAreaCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			tbNumber.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			
			ret = false;
		}
		else
		{
			ret = setValidator.areAllValid(true);
		}
		
		return ret;
	}
	
	private void setHrefText(PhoneDAO phone)
	{
		if (phone != null)
		{
			String text = SharedDataUtil.getNVEntityShortHand(phone);
			
			if (text != null)
			{
				anchorLink.setText(text);
			}
			else
			{
				setLinkDefaultText();
			}
		}
	}

	@Override
	public void setWidgetValue(String value) 
	{
		
	}
	
	@Override
	public void clear()
	{
		tbName.setValue("");
		tbAreaCode.setValue("");
		tbNumber.setValue("");
		tbExtension.setValue("");
	}
	
	@Override
	public void setContentVisible(boolean value)
	{
		vpPhone.setVisible(value);
	}
	
	@Override
	public void hideHyperLink()
	{
		hpLinkPlaceHolder.setVisible(false);
		setContentVisible(true);
	}

	@Override
	public boolean isContentVisible() 
	{
		return vpPhone.isVisible();
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		if (readOnly)
		{
			tbName.setEnabled(false);
			phoneType.setReadOnly(true);
			countryCode.setReadOnly(true);
			tbAreaCode.setEnabled(false);
			tbNumber.setEnabled(false);
			tbExtension.setEnabled(false);
		}
		else
		{
			tbName.setEnabled(true);
			phoneType.setReadOnly(false);
			countryCode.setReadOnly(false);
			tbAreaCode.setEnabled(true);
			tbNumber.setEnabled(true);
			tbExtension.setEnabled(true);
		}
	}
	
	@Override
	public void setWidgetWidth(String width) 
	{
		vpMain.setWidth(width);
	}

	@Override
	public void setWidgetHeight(String height) 
	{
		vpMain.setHeight(height);
	}
	
	@Override
	public boolean isOuterScrollEnabled() 
	{
		return true;
	}
	
	@Override
	public String getFormName()
	{
		if (!SharedStringUtil.isEmpty(tbName.getValue()))
		{
			return tbName.getValue();
		}
		
		return null;
	}
	
	@Override
	public String getFormDescription()
	{
		return null;
	}
	
	@Override
	public boolean isNameIncluded() 
	{
		return true;
	}

	@Override
	public boolean isDescriptionIncluded() 
	{
		return false;
	}
	
}