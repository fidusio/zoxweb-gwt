package org.zoxweb.client.widget.custom;

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.NVDynamicEnumWidget;
import org.zoxweb.client.widget.NVEntityIntermediateWidget;
import org.zoxweb.client.widget.NVStringWidget;
import org.zoxweb.shared.data.AddressDAO;
import org.zoxweb.shared.data.SharedDataUtil;
import org.zoxweb.shared.filters.AddressFilterType;
import org.zoxweb.shared.util.*;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * The AddressDAO widget.
 * @author mzebib
 */
@SuppressWarnings("serial")
public class AddressDAOWidget 
	extends NVEntityIntermediateWidget
{

	private static AddressDAOWidgetUiBinder uiBinder
		= GWT.create(AddressDAOWidgetUiBinder.class);

	interface AddressDAOWidgetUiBinder 
		extends UiBinder<Widget, AddressDAOWidget> 
	{
	}
	
	interface Style
		extends CssResource
	{
		String invalid();
	}

	@UiField Style style;
	@UiField HorizontalPanel hpLinkPlaceHolder;
	@UiField VerticalPanel vpMain;
	@UiField VerticalPanel vpAddress;
	@UiField HorizontalPanel hpDescription;
	@UiField HorizontalPanel hpStreetAddress;
	@UiField HorizontalPanel hpCity;
	@UiField HorizontalPanel hpStateAndZipCode;
	@UiField HorizontalPanel hpStateOrProvince;
	@UiField HorizontalPanel hpZipOrPostalCode;
	@UiField HorizontalPanel hpCountry;

	private TextBox tbName = new TextBox();
	private TextBox tbStreetAddress = new TextBox();
	private TextBox tbCity = new TextBox();
	private NVBaseWidget<String> stateOrProvince;
	private TextBox tbZipOrPostalCode = new TextBox();
	private NVDynamicEnumWidget country;
	//private ValueFilterSetValidator<String> setValidator;
	//private ValueFilterHandler<String> streetAddressValidator;
	//private ValueFilterHandler<String> cityValidator;
	//private ValueFilterHandler<String> zipOrPostalCodeValidator;
	//private ValueFilterHandler<String> zipCodeValidator;
	//private ValueFilterHandler<String> postalCodeValidator;
	
	public AddressDAOWidget(AddressDAO address)
	{
		this(address, false);
	}
	
	public AddressDAOWidget(AddressDAO address, boolean showShortHand)
	{
		this((NVConfigEntity) address.getNVConfig(), showShortHand);
		
		if (address != null)
		{
			setValue(address);
		}
	}
	
	public AddressDAOWidget(NVConfigEntity nvce)
	{
		this(nvce, false);
	}
	
	@SuppressWarnings("unchecked")
	public AddressDAOWidget(NVConfigEntity nvce, boolean showShortHand) 
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
        
		stateOrProvince =  new NVStringWidget(AddressDAO.Param.STATE_PROVINCE.getNVConfig());
		country = new NVDynamicEnumWidget(AddressDAO.Param.COUNTRY.getNVConfig(), Const.NVDisplayProp.VALUE, false);
		
		((ListBox) country.getWidget()).addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{		
				hpStateOrProvince.clear();
				stateOrProvince = (NVBaseWidget<String>) StateListWidgetFilter.SINGLETON.validate(country.getValue());
				hpStateOrProvince.add(stateOrProvince);
				
				//applyZipPostalCodeValidator();
				
				tbStreetAddress.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
				tbCity.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
				tbZipOrPostalCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			}
		});
		
		tbName.setSize("24EM", "1.5EM");
		tbStreetAddress.setSize("24EM", "1.5EM");
		tbCity.setSize("15EM", "1.5EM");
		stateOrProvince.setSize("100%", "1.5EM");
		tbZipOrPostalCode.setSize("100%", "1.5EM");
		country.setSize("15EM", "2EM");
		country.setWidgetSize(15, 2);
		country.setEditVisible(false);
		
		hpDescription.add(tbName);
		hpStreetAddress.add(tbStreetAddress);
		hpCity.add(tbCity);
		hpStateOrProvince.add(stateOrProvince);
		hpZipOrPostalCode.add(tbZipOrPostalCode);
		hpCountry.add(country);
		
		//setValidator = new ValueFilterSetValidator<String>();
		
		//streetAddressValidator = new ValueFilterHandler<String>(tbStreetAddress, NotNullOrEmpty.SINGLETON, style.invalid());
        //setValidator.add(streetAddressValidator);
		
		//cityValidator = new ValueFilterHandler<String>(tbCity, NotNullOrEmpty.SINGLETON, style.invalid());
        //setValidator.add(cityValidator);
        
        //zipOrPostalCodeValidator = new ValueFilterHandler<String>(tbZipOrPostalCode, NotNullOrEmpty.SINGLETON, style.invalid());
        //zipCodeValidator = new ValueFilterHandler<String>(tbZipOrPostalCode, AddressFilterType.US_ZIP_CODE, style.invalid());
        //postalCodeValidator = new ValueFilterHandler<String>(tbZipOrPostalCode, AddressFilterType.CANADA_POSTAL_CODE, style.invalid());
        
        
		hpStateOrProvince.clear();
		stateOrProvince = (NVBaseWidget<String>) StateListWidgetFilter.SINGLETON.validate(country.getValue());
		hpStateOrProvince.add(stateOrProvince);
        //applyZipPostalCodeValidator();
	}
	
//	private void applyZipPostalCodeValidator()
//	{
////		if (country.getValue().equals("USA") || country.getValue().equals("CAN"))
////		{
////			setValidator.add(zipOrPostalCodeValidator);
////		}
////		else
////		{
////			if (zipOrPostalCodeValidator != null)
////			{
////				zipOrPostalCodeValidator.clear();
////				setValidator.remove(zipOrPostalCodeValidator);
////			}
////			
////			tbZipOrPostalCode.setStyleName(style.invalid());
////		}
//	}

	@Override
	public Widget getWidget() 
	{
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setWidgetValue(NVEntity value) 
	{
		currentNVE = value;
		
		if (currentNVE != null)
		{
			AddressDAO address = (AddressDAO) currentNVE;
			
			tbName.setValue(address.getName());
			tbStreetAddress.setValue(address.getStreet());
			tbCity.setValue(address.getCity());
			
			tbZipOrPostalCode.setValue(address.getZIPOrPostalCode());
			country.setValue(address.getCountry());

			hpStateOrProvince.clear();
			stateOrProvince = (NVBaseWidget<String>) StateListWidgetFilter.SINGLETON.validate(country.getValue());
			stateOrProvince.setValue(address.getStateOrProvince());
			hpStateOrProvince.add(stateOrProvince);
			
			if (SUS.isNotEmpty(NVConfigMapUtil.toString(address, getNVConfigAttributesMap(), false)))
			{
				setHrefText(address);
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
		AddressDAO address = null;
		
		if (isValid())
		{
			if (currentNVE != null)
			{
				address = (AddressDAO) currentNVE;
			}
			else
			{
				address = new AddressDAO();
			}
			
			address.setName(tbName.getValue());
			address.setStreet(tbStreetAddress.getValue());
			address.setCity(tbCity.getValue());
			address.setCountry(country.getValue());
			
			if (country.getValue().equals("USA") || country.getValue().equals("CAN"))
			{
				if (SUS.isNotEmpty(stateOrProvince.getValue()))
				{
					address.setStateOrProvince((String) stateOrProvince.getValue());
				}
				
				address.setZIPOrPostalCode(tbZipOrPostalCode.getValue().toUpperCase());
			}
			else
			{
				address.setStateOrProvince((String) stateOrProvince.getValue());
				address.setZIPOrPostalCode(tbZipOrPostalCode.getValue());
			}
			
			if (SUS.isNotEmpty(NVConfigMapUtil.toString(address, getNVConfigAttributesMap(), false)))
			{
				setHrefText(address);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			else
			{
				setLinkDefaultText();
			}
		}
		
		return address;
	}
	
	public boolean isValid()
	{
		boolean ret = false;
		boolean streetAddressValid = validateStreetAddress();
		boolean cityValid = validateCity();
		boolean zipValid = validateZipOrPostalCode();
		
		if (!isMandatory() &&
				((!streetAddressValid && !cityValid && (country.getValue().equals("USA") || country.getValue().equals("CAN") ? !zipValid : true))))
		{
			tbStreetAddress.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			tbCity.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			tbZipOrPostalCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			
			ret = false;
		}
		else if (streetAddressValid && cityValid && zipValid)
		{
			ret = true;
		}
		
		return ret;
	}
	
	public boolean byPassValidationPermitted()
	{
		boolean streetAddressValid = validateStreetAddress();
		boolean cityValid = validateCity();
		boolean zipValid = validateZipOrPostalCode();
		
		if (!isMandatory() &&
				((!streetAddressValid && !cityValid && (country.getValue().equals("USA") || country.getValue().equals("CAN") ? !zipValid : true))))
		{
			tbStreetAddress.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			tbCity.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			tbZipOrPostalCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			
			return true;
		}
		
		return false;
	}
	
	public boolean validate()
	{
		boolean streetAddressValid = false;
		boolean cityValid = false;
		boolean zipValid = false;
		
		if (SUS.isNotEmpty(tbStreetAddress.getValue()) && SUS.isNotEmpty(tbCity.getValue()))
		{
			streetAddressValid = true;
		}

		if (SUS.isNotEmpty(tbCity.getValue()))
		{
			cityValid = true;
		}
			
		if (country.getValue().equals("USA"))
		{
			zipValid = AddressFilterType.US_ZIP_CODE.isValid(tbZipOrPostalCode.getValue());
		}
		else if (country.getValue().equals("CAN"))
		{
			zipValid = AddressFilterType.CANADA_POSTAL_CODE.isValid(tbZipOrPostalCode.getValue());
		}
		else
		{
			zipValid = true;
		}
		
		if (streetAddressValid && cityValid && zipValid)
		{
			return true;
		}
		
		return false;
	}
	
	private boolean validateStreetAddress()
	{
		boolean ret = false;
		
		if (SUS.isNotEmpty(tbStreetAddress.getValue()))
		{
			ret = true;
		}

		if (ret)
		{
			tbStreetAddress.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
		}
		else
		{
			tbStreetAddress.setStyleName(style.invalid());
		}
		
		return ret;
	}
	
	private boolean validateCity()
	{
		boolean ret = false;
		
		if (SUS.isNotEmpty(tbCity.getValue()))
		{
			ret = true;
		}

		if (ret)
		{
			tbCity.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
		}
		else
		{
			tbCity.setStyleName(style.invalid());
		}
		
		return ret;
	}
	
	private boolean validateZipOrPostalCode()
	{
		boolean ret = false;
		
		if (country.getValue().equals("USA"))
		{
			ret = AddressFilterType.US_ZIP_CODE.isValid(tbZipOrPostalCode.getValue());
		}
		else if (country.getValue().equals("CAN"))
		{
			ret = AddressFilterType.CANADA_POSTAL_CODE.isValid(tbZipOrPostalCode.getValue());
		}
		else
		{
			ret = true;
		}
		
		if (ret)
		{
			tbZipOrPostalCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
		}
		else
		{
			tbZipOrPostalCode.setStyleName(style.invalid());
		}
		
		return ret;
	}
	
	private void setHrefText(AddressDAO address)
	{
		if (address != null)
		{
			String text = SharedDataUtil.getNVEntityShortHand(address);
			
			if (text != null)
			{
				anchorLink.setText(SharedDataUtil.getNVEntityShortHand(address));
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

	@SuppressWarnings("unchecked")
	@Override
	public void clear()
	{
		tbName.setValue("");
		tbStreetAddress.setValue("");
		tbCity.setValue("");
		tbZipOrPostalCode.setValue("");
		
		if (stateOrProvince.getWidget() instanceof ValueBoxBase<?>)
		{
			((ValueBoxBase<String>) stateOrProvince.getWidget()).setValue("");
		}
	}
	
	@Override
	public void setContentVisible(boolean value)
	{
		vpAddress.setVisible(value);
	}
	
	@Override
	public boolean isContentVisible()
	{
		return vpAddress.isVisible();
	}
	
	@Override
	public void hideHyperLink()
	{
		hpLinkPlaceHolder.setVisible(false);
		setContentVisible(true);
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;	
		
		if (readOnly)
		{
			tbName.setEnabled(false);
			tbStreetAddress.setEnabled(false);
			tbCity.setEnabled(false);
			stateOrProvince.setReadOnly(true);
			tbZipOrPostalCode.setEnabled(false);
			country.setReadOnly(true);
		}
		else
		{
			tbName.setEnabled(true);
			tbStreetAddress.setEnabled(true);
			tbCity.setEnabled(true);
			stateOrProvince.setReadOnly(false);
			tbZipOrPostalCode.setEnabled(true);
			country.setReadOnly(false);
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
		if (SUS.isNotEmpty(tbName.getValue()))
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