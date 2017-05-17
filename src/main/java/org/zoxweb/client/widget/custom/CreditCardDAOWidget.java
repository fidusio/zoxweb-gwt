package org.zoxweb.client.widget.custom;

import java.util.Date;
import java.util.HashMap;

import org.zoxweb.client.widget.CustomDateWidget;
import org.zoxweb.client.widget.NVEntityIntermediateWidget;
import org.zoxweb.client.widget.ValueFilterHandler;
import org.zoxweb.client.widget.ValueFilterSetValidator;
import org.zoxweb.shared.data.CreditCardDAO;
import org.zoxweb.shared.data.CreditCardType;
import org.zoxweb.shared.data.SharedDataUtil;
import org.zoxweb.shared.filters.CreditCardNumberFilter;
import org.zoxweb.shared.filters.NotNullOrEmpty;
import org.zoxweb.shared.filters.NumberFilter;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVConfigMapUtil;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * The CreditCardDAO widget.
 * @author mzebib
 *
 */
@SuppressWarnings("serial")
public class CreditCardDAOWidget 
	extends NVEntityIntermediateWidget
{
	
	private static CreditCardDAOWidgetUiBinder uiBinder 
		= GWT.create(CreditCardDAOWidgetUiBinder.class);

	interface CreditCardDAOWidgetUiBinder 
		extends UiBinder<Widget, CreditCardDAOWidget>
	{
	}
	
	interface Style
		extends CssResource
	{
		String invalid();
		String noHighlight();
		String redHighlight();
	}

	@UiField Style style;
	@UiField HorizontalPanel hpLinkPlaceHolder;
	@UiField VerticalPanel vpMain;
	@UiField VerticalPanel vpCard;
	@UiField HorizontalPanel hpCardType;
	@UiField HorizontalPanel hpCardNumber;
	@UiField HorizontalPanel hpCardValidation;
	@UiField HorizontalPanel hpCardHolderName;
	@UiField HorizontalPanel hpCardDescription;
	@UiField Image imageCVV;
	@UiField Label labelCVV;
	@UiField VerticalPanel vpCardInfo;
	
	private TextBox tbName = new TextBox();
	private ListBox lbCardType = new ListBox();
	private TextBox tbCardHolderName = new TextBox();
	private TextBox tbCardNumber = new TextBox();
	private Anchor hrefLinkCardNumber = new Anchor();
	private CustomDateWidget expirationDate;
	private PasswordTextBox ptbSecurityCode = new PasswordTextBox();
	
	private int expirationStartYear = Integer.valueOf(DateTimeFormat.getFormat("yyyy").format(new Date()));
	private int expirationEndYear = expirationStartYear + 20;
	
	private ValueFilterSetValidator<String> setValidator = new ValueFilterSetValidator<String>();
	private ValueFilterHandler<String> cardHolderNameValidator;
	private ValueFilterHandler<String> cardNumberValidator;
	private ValueFilterHandler<String> securityCodeValidator;
	private HashMap<String, String> mapCardType = new HashMap<String, String>();
	private boolean cardNumberReadOnly = false;
	
	public CreditCardDAOWidget(CreditCardDAO creditCard)
	{
		this(creditCard, false, false);
	}
	
	public CreditCardDAOWidget(CreditCardDAO creditCard, boolean showSecurityCode)
	{
		this(creditCard, showSecurityCode, false);
	}
	
	public CreditCardDAOWidget(CreditCardDAO creditCard, boolean showSecurityCode, boolean showShortHand)
	{
		this((NVConfigEntity) creditCard.getNVConfig(), showSecurityCode, showShortHand);
		
		if (creditCard != null)
		{
			setValue(creditCard);
		}
	}
	
	public CreditCardDAOWidget(NVConfigEntity nvce) 
	{
		this(nvce, false);
	}
	
	public CreditCardDAOWidget(NVConfigEntity nvce, boolean showSecurityCode) 
	{
		this(nvce, false, false);
	}
	
	public CreditCardDAOWidget(NVConfigEntity nvce, boolean showSecurityCode, boolean showShortHand) 
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
		
		cardHolderNameValidator = new ValueFilterHandler<String>(tbCardHolderName, NotNullOrEmpty.SINGLETON, style.invalid());
        setValidator.add(cardHolderNameValidator);
        
        cardNumberValidator = new ValueFilterHandler<String>(tbCardNumber, CreditCardNumberFilter.SINGLETON, style.invalid());
        setValidator.add(cardNumberValidator);
        
        securityCodeValidator = new ValueFilterHandler<String>(ptbSecurityCode, NumberFilter.SINGLETON, style.invalid());
        setValidator.add(securityCodeValidator);
		
        lbCardType.addItem("Card type");
        
        for (CreditCardType type : CreditCardType.values())
        {
        	lbCardType.addItem(type.getDisplay());
        	mapCardType.put(type.getDisplay(), type.name());
        }
        
        lbCardType.setSelectedIndex(0);
        vpCardInfo.setVisible(false);
        
        lbCardType.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{		
				if (lbCardType.getSelectedIndex() == 0)
				{
					vpCardInfo.setVisible(false);
				}
				else
				{
					vpCardInfo.setVisible(true);
					clear();
				}
			}
			
		});
		
		expirationDate = new CustomDateWidget(expirationStartYear, expirationEndYear);
		expirationDate.setVisible(true, true, false);
		
		ptbSecurityCode.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event) 
			{
				String input = ptbSecurityCode.getText();
				CreditCardType type = getSelectedCardType();
				
				if (input != null && type != null)
				{
					int maxLength = 3;
					
					if (type == CreditCardType.AMEX)
					{
						maxLength = 4;
					}
					
					if (NumberFilter.SINGLETON.isValid("" + event.getCharCode()) && input.length() < maxLength || event.getUnicodeCharCode() == 0)
					{
						input += event.getCharCode();
					}
					else
					{
						ptbSecurityCode.cancelKey();
					}
				}
			}
		});
		
		tbName.setSize("100%", "1.5EM");
		tbCardNumber.setSize("100%", "1.5EM");
		hrefLinkCardNumber.setSize("100%", "1.5EM");
		expirationDate.setSize("10EM", "1.5EM");
		ptbSecurityCode.setSize("3EM", "1.5EM");
		tbCardHolderName.setSize("100%", "1.5EM");
		
		hrefLinkCardNumber.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				setCardNumberReadOnly(false, null);
			}
		});
		
		hpCardDescription.add(tbName);
		hpCardType.add(lbCardType);
		hpCardNumber.add(tbCardNumber);
		hpCardValidation.add(expirationDate);
		hpCardValidation.add(ptbSecurityCode);
		hpCardHolderName.add(tbCardHolderName);
		
		showSecurityCode(showSecurityCode);
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
			CreditCardDAO card = (CreditCardDAO) currentNVE;
			
			tbName.setValue(card.getName());
			setCardTypeValue(card.getCardType());
			tbCardHolderName.setValue(card.getCardHolderName());
			
			setCardNumberReadOnly(true, card.getCardNumber());
			
			if (card.getExpirationDate() == 0)
			{
				expirationDate.getMonthListBox().setSelectedIndex(0);
				expirationDate.getYearListBox().setSelectedIndex(0);
			}
			else
			{
				expirationDate.setValue(card.getExpirationDate());
				expirationDate.getYearListBox().setEnabled(true);
				expirationDate.getMonthListBox().setEnabled(true);
			}
			
			if (ptbSecurityCode.isVisible())
			{
				ptbSecurityCode.setValue(card.getSecurityCode());
			}

			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(card, getNVConfigAttributesMap(), false)))
			{
				setHrefText(card);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			
			if (SharedStringUtil.isEmpty(anchorLink.getText()) || anchorLink.getText().equals("0"))
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
		CreditCardDAO card = null;
	
		expirationDate.setPanelStyle(style.noHighlight());
		
		if (isCardNumberReadOnly() && currentNVE != null && cardHolderNameValidator.isValid() && securityCodeValidator.isValid() && expirationDate.isDateSelected() && validateCVV())
		{
			card = (CreditCardDAO) currentNVE;
			card.setName(tbName.getValue());
			
			if (getSelectedCardType() != null)
			{
				card.setCardType(getSelectedCardType());
			}
			
			card.setCardHolderName(tbCardHolderName.getValue());
			card.setExpirationDate(expirationDate.getValue());
			
			if (card.getCardNumber() != null && ptbSecurityCode.isVisible())
			{
				try
				{
					card.setSecurityCode(ptbSecurityCode.getValue());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(card, getNVConfigAttributesMap(), false)))
			{
				setHrefText(card);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			
			if (SharedStringUtil.isEmpty(anchorLink.getText()) || anchorLink.getText().equals("0"))
			{
				anchorLink.setText(WidgetConst.HREF_DEFAULT_TEXT);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
		}
		else if (setValidator.areAllValid(true) && expirationDate.isDateSelected() && validateCardNumber() && validateCVV())
		{			
			if (currentNVE == null)
			{
				card = new CreditCardDAO();
			}
			else
			{	
				card = (CreditCardDAO) currentNVE;
			}
			
			card.setName(tbName.getValue());
			
			if (getSelectedCardType() != null)
			{
				card.setCardType(getSelectedCardType());
			}
			
			card.setCardHolderName(tbCardHolderName.getValue());
			card.setExpirationDate(expirationDate.getValue());
			
			setCardNumberReadOnly(true, tbCardNumber.getValue());
			
			String cardNumber = SharedStringUtil.trimOrNull(tbCardNumber.getValue());
			cardNumber = cardNumber.replaceAll("[ -]", "");
			
			card.setCardNumber(cardNumber);
			
			if (card.getCardNumber() != null && ptbSecurityCode.isVisible() && ptbSecurityCode.getValue() != null)
			{
				try
				{
					card.setSecurityCode(ptbSecurityCode.getValue());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		
			if (!SharedStringUtil.isEmpty(NVConfigMapUtil.toString(card, getNVConfigAttributesMap(), false)))
			{
				setHrefText(card);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
			
			if (SharedStringUtil.isEmpty(anchorLink.getText()) || anchorLink.getText().equals("0"))
			{
				anchorLink.setText(WidgetConst.HREF_DEFAULT_TEXT);
				anchorLink.setTitle(WidgetConst.HREF_DEFAULT_TEXT);
			}
		}
		else if (!expirationDate.isDateSelected())
		{
			expirationDate.setPanelStyle(style.redHighlight());
		}
		
		return card;
	}
	
	private void setCardNumberReadOnly(boolean readOnly, String cardNumber)
	{
		hpCardNumber.clear();
		
		if (readOnly && !SharedStringUtil.isEmpty(cardNumber))
		{
			hrefLinkCardNumber.setText(SharedDataUtil.maskCreditCardNumber(cardNumber));
			hpCardNumber.add(hrefLinkCardNumber);
			
			setValidator.remove(cardNumberValidator);
			cardNumberReadOnly = true;
		}
		else
		{
			tbCardNumber.setValue("");
			hpCardNumber.add(tbCardNumber);
			
			setValidator.remove(cardNumberValidator);
			setValidator.add(cardNumberValidator);
			cardNumberReadOnly = false;
		}
	}
	
	private boolean isCardNumberReadOnly()
	{
		return cardNumberReadOnly;
	}
	
	private boolean validateCardNumber()
	{
		if (!SharedStringUtil.isEmpty(tbCardNumber.getValue()) && getSelectedCardType() != null)
		{
			CreditCardType validType = CreditCardType.lookup(tbCardNumber.getValue());
			
			if (validType != null && validType == getSelectedCardType())
			{
				tbCardNumber.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
				return true;
			}
		}
		
		tbCardNumber.setStyleName(style.invalid());
		return false;
	}
	
	private boolean validateCVV()
	{
		try
		{
			CreditCardNumberFilter.validateCVVByType(getSelectedCardType(), ptbSecurityCode.getValue());
			ptbSecurityCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
			return true;
		}
		catch (Exception e)
		{
			ptbSecurityCode.setStyleName(style.invalid());
		}
		
		return false;
	}

	private void setHrefText(CreditCardDAO card)
	{
		if (card != null)
		{
			String shortHandText = SharedDataUtil.getNVEntityShortHand(card);
			
			if (shortHandText != null)
			{
				anchorLink.setText(shortHandText);
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
	
	private CreditCardType getSelectedCardType()
	{
		CreditCardType type = null;
		
		if (lbCardType.getSelectedIndex() != 0 )
		{
			type = ((CreditCardType) SharedUtil.enumValue(CreditCardType.class, mapCardType.get(lbCardType.getItemText(lbCardType.getSelectedIndex()))));
		}
		
		return type;
	}
	
	public void showSecurityCode(boolean showSecurityCode)
	{
		labelCVV.setVisible(showSecurityCode);
		ptbSecurityCode.setVisible(showSecurityCode);
		imageCVV.setVisible(showSecurityCode);
	}

	@Override
	public void clear() 
	{
		tbName.setText("");
		tbCardHolderName.setText("");
		expirationDate.reset();
		tbCardNumber.setText("");
		setCardNumberReadOnly(false, null);
		ptbSecurityCode.setText("");
		
		tbCardHolderName.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
		tbCardNumber.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
		expirationDate.setPanelStyle(style.noHighlight());
		ptbSecurityCode.setStyleName(WidgetConst.CSSStyle.TEXTBOX_DEFAULT.getName());
	}
	
	@Override
	public boolean isContentVisible() 
	{
		return vpCard.isVisible();
	}
	
	@Override
	public void setContentVisible(boolean value)
	{
		vpCard.setVisible(value);
	}
	
	@Override
	public void hideHyperLink()
	{
		hpLinkPlaceHolder.setVisible(false);
		setContentVisible(true);
	}

	private void setCardTypeValue(CreditCardType type)
	{
		if (type == null)
		{
			lbCardType.setSelectedIndex(0);
		}
		else
		{
			for (int i = 1; i < lbCardType.getItemCount(); i++)
			{
				if (lbCardType.getValue(i).equals(type.getDisplay()))
				{
					lbCardType.setSelectedIndex(i);
					vpCardInfo.setVisible(true);
					break;
				}
			}
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		if (readOnly)
		{
			tbName.setEnabled(false);
			lbCardType.setEnabled(false);
			tbCardHolderName.setEnabled(false);
			tbCardNumber.setEnabled(false);
			expirationDate.setReadOnly(true);
			ptbSecurityCode.setEnabled(false);
		}
		else
		{
			tbName.setEnabled(true);
			lbCardType.setEnabled(true);
			tbCardHolderName.setEnabled(true);
			tbCardNumber.setEnabled(true);
			expirationDate.setReadOnly(false);
			ptbSecurityCode.setEnabled(true);
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