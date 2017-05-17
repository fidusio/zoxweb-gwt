package org.zoxweb.client.widget;

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.WidgetUtil;
import org.zoxweb.shared.data.DataConst.FormMode;
import org.zoxweb.shared.data.FormInfoDAO;
import org.zoxweb.shared.util.ExceptionCollection;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVConfigAttributesMap;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVConfigMapUtil;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.UpdateValue;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.user.client.ui.Anchor;

@SuppressWarnings("serial")
public abstract class NVEntityWidget 
	extends NVBaseWidget<NVEntity>
{
	
	private NVConfigAttributesMap nvcam;
	protected NVEntity currentNVE;
	private boolean isMissingValueAllowed = false;
	protected Anchor anchorLink = new Anchor(WidgetConst.HREF_DONE_TEXT);
	protected UpdateValue<NVEntity> updateValue;
	private FormInfoDAO associatedFormInfo = null;
	protected FormMode formMode = FormMode.DESIGN;
	
	protected NVEntityWidget() 
	{
		super(null, null, true);
		
		setUpdateValue(new UpdateValue<NVEntity>()
		{
			@Override
			public void updateValue(NVEntity value) 
			{
				NVConfigEntity nvce = (NVConfigEntity) value.getNVConfig();
				StringBuilder sb = new StringBuilder();
				
				for (NVConfig nvc : nvce.getAttributes())
				{
					sb.append(nvc.getName() +	": " + value.lookupValue(nvc) + "\n");
				}
				
				WidgetUtil.logToConsole(sb.toString());	
			}
			
		});

		anchorLink.setStyleName(WidgetConst.CSSStyle.DEFAULT_SHORTHAND_LINK.getName());
	}
	
	public String getHrefLinkText(NVEntity nve)
	{
		return NVConfigMapUtil.toString(nve, nvcam, false);
	}
	
	public NVConfigAttributesMap getNVConfigAttributesMap()
	{
		return nvcam;
	}
	
	public void setNVConfigAttributesMap(NVConfigAttributesMap nvcam)
	{
		this.nvcam = nvcam;
	}
	
	public void setContentVisible(boolean value)
	{
		
	}
	
	public abstract boolean isContentVisible();
	
	public NVEntity getNVEntity()
	{
		return currentNVE;
	}
	
	public boolean isMissingValueAllowed() 
	{
		return isMissingValueAllowed;
	}
	
	public void setMissingValueAllowed(boolean isMissingValueAllowed) 
	{
		this.isMissingValueAllowed = isMissingValueAllowed;
	}
	
	//public abstract void clear();
	
	public void hideHyperLink()
	{
		getHyperLink().setVisible(false);
	}
	
	public Anchor getHyperLink()
	{
		return anchorLink;
	}
	
	protected void setLinkDefaultText()
	{
		anchorLink.setText(WidgetConst.HREF_DONE_TEXT);
		anchorLink.setTitle(WidgetConst.HREF_DONE_TITLE);
	}
	
	public abstract String getFormName();
	public abstract String getFormDescription();
	public abstract boolean isNameIncluded();
	public abstract boolean isDescriptionIncluded();
	
	public abstract void setWidgetWidth(String width);
	public abstract void setWidgetHeight(String height);
	
	public abstract boolean isOuterScrollEnabled();
	
	public boolean isExistingNVEntity()
	{
		if (getAssociatedFormInfo() != null && getAssociatedFormInfo().getReferenceID() != null)
		{
			return true;
		}
		
		return false;
	}
	
	public FormInfoDAO getAssociatedFormInfo()
	{
		return associatedFormInfo;
	}
	
	public void setAssociatedFormInfo(FormInfoDAO associatedFormInfo)
	{
		this.associatedFormInfo = associatedFormInfo;
	}
	
	public FormMode getFormMode()
	{
		return formMode;
	}
	
	public void setFormMode(FormMode mode)
	{
		this.formMode = mode;
	}
	
	public void setUpdateValue(UpdateValue<NVEntity> value)
	{
		updateValue = value;
	}

	public UpdateValue<NVEntity> getUpdateValue()
	{
		return updateValue;
	}
	
	public void updateValue()
	{
		if (getUpdateValue() != null)
		{
			try
			{
				getUpdateValue().updateValue(getValue());
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (ExceptionCollection e)
			{
				for (Exception exception : e.getExceptionList())
				{
					exception.printStackTrace();
				}
				
				e.printStackTrace();
			}
		}
	}
	
}