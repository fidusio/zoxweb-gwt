package org.zoxweb.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.zoxweb.client.data.events.SearchControllerHandler;
import org.zoxweb.client.rpc.CallBackHandler;
import org.zoxweb.shared.data.FileInfoDAO;
import org.zoxweb.shared.data.FolderInfoDAO;
import org.zoxweb.shared.data.FormInfoDAO;
import org.zoxweb.shared.data.NVEntityContainerDAO;
import org.zoxweb.shared.data.SharedDataUtil;
import org.zoxweb.shared.data.ZWDataFactory;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.SUS;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class AddNVESlideWidget
	extends Composite
{

	private static AddNVESlideWidgetUiBinder uiBinder
		= GWT.create(AddNVESlideWidgetUiBinder.class);

	interface AddNVESlideWidgetUiBinder 
		extends UiBinder<Widget, AddNVESlideWidget>
	{
	}
	
	@UiField ListBox lbNVConfigEntityTypes;
	@UiField ListBox lbNVEntitySelections;
	@UiField CloseButtonWidget closeButton;
	@UiField HorizontalPanel hpButtons;
	
	private HashMap<String, NVConfigEntity> nvceMap = new HashMap<String, NVConfigEntity>();
	private HashMap<Integer, NVEntity> nveMap = new HashMap<Integer, NVEntity>();
	private PopupPanel popup;
	private CustomPushButtonWidget cpbOk;
	private CustomPushButtonWidget cpbCancel;
	private FolderInfoDAO entryFolder;
	
	
	public AddNVESlideWidget(NVConfigEntity... selectionNVCEs)
	{
		this(null, selectionNVCEs);
	}
	
	public AddNVESlideWidget(SearchControllerHandler<NVEntityContainerDAOWidget, List<NVEntity>> searchControllerHandler,  FolderInfoDAO entryFolder, NVConfigEntity... selectionNVCEs)
	{
		this(searchControllerHandler, selectionNVCEs);
		
		if (entryFolder != null)
		{
			setEntryFolder(entryFolder);
		}
	}
	
	public AddNVESlideWidget(SearchControllerHandler<NVEntityContainerDAOWidget, List<NVEntity>> searchControllerHandler, NVConfigEntity... selectionNVCEs)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		popup = new PopupPanel();
		popup.setStyleName("popup");
		popup.setModal(true);
		popup.setWidget(this);
		
		ClickHandler closePopupClickHandler = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				hidePopup();
			}
			
		};
		
		closeButton.addClickHandler(closePopupClickHandler);
		
		cpbOk = new CustomPushButtonWidget(WidgetConst.ImageURL.SAVE.getValue(), "Ok");
		cpbCancel = new CustomPushButtonWidget(WidgetConst.ImageURL.CANCEL.getValue(), "Cancel");
		cpbCancel.addClickHandler(closePopupClickHandler);
		
		hpButtons.add(cpbOk);
		hpButtons.add(cpbCancel);
		
		if ((selectionNVCEs == null || selectionNVCEs.length == 0) && searchControllerHandler != null)
		{
			searchControllerHandler.actionSearch(null, new CallBackHandler<List<NVEntity>>(new AsyncCallback<List<NVEntity>>()
			{
				@Override
				public void onFailure(Throwable caught)
				{
					
				}
	
				@Override
				public void onSuccess(List<NVEntity> result)
				{
					if (result != null)
					{
						for (NVEntity nve : result)
						{
							if (!(nve instanceof NVEntityContainerDAO))
							{
								NVConfigEntity nvce = (NVConfigEntity) nve.getNVConfig();
				
								if (nvce != null)
								{
									if (SUS.isNotEmpty(nvce.getDisplayName()))
									{
										lbNVConfigEntityTypes.addItem(nvce.getDisplayName());
										nvceMap.put(nvce.getDisplayName(), nvce);
									}
									else
									{
										lbNVConfigEntityTypes.addItem(nvce.getName());
										nvceMap.put(nvce.getName(), nvce);
									}
								}
							}
						}
					}
					
					lbNVConfigEntityTypes.addItem("File");
					nvceMap.put("File", FileInfoDAO.NVC_FILE_INFO_DAO);
				}
		
			}));
		}
		else
		{
			setNVCEs(selectionNVCEs);
		}
		
		lbNVEntitySelections.setVisible(false);
		
		lbNVConfigEntityTypes.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				cpbOk.setEnabled(false);
				updateSelections();
			}
			
		});
		
		lbNVEntitySelections.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				if (lbNVEntitySelections.getItemCount() > 0 && lbNVEntitySelections.getSelectedIndex() != -1)
				{
					cpbOk.setEnabled(true);
				}
			}
			
		});
		
		cpbOk.setEnabled(false);
	}
	
	public void setNVCEs(NVConfigEntity[] nvces)
	{
		lbNVConfigEntityTypes.clear();
		nvceMap.clear();
		
		if (nvces != null && nvces.length > 0)
		{
			for (NVConfigEntity nvce : nvces)
			{
				if (nvce != null)
				{
					if (SUS.isNotEmpty(nvce.getDisplayName()))
					{
						lbNVConfigEntityTypes.addItem(nvce.getDisplayName());
						nvceMap.put(nvce.getDisplayName(), nvce);
					}
					else
					{
						lbNVConfigEntityTypes.addItem(nvce.getName());
						nvceMap.put(nvce.getName(), nvce);
					}
				}
			}
			
			lbNVConfigEntityTypes.addItem("File");
			nvceMap.put("File", FileInfoDAO.NVC_FILE_INFO_DAO);
		}
	}
	
	public FolderInfoDAO getEntryFolder()
	{
		return entryFolder;
	}
	
	public void setEntryFolder(FolderInfoDAO entryFolder)
	{
		this.entryFolder = entryFolder;
	}
	
	public HandlerRegistration addOkButtonClickHandler(ClickHandler clickHandler)
	{
		return cpbOk.addClickHandler(clickHandler);
	}
	
	public void addSelectedNVEntityDoubleClickHandler(DoubleClickHandler doubleClickHandler)
	{
		lbNVEntitySelections.addDoubleClickHandler(doubleClickHandler);
	}
	
	private void updateSelections()
	{
		if (getSelectedNVConfigEntity() != null)
		{
			if (getSelectedNVConfigEntity().getMetaType().equals(FileInfoDAO.NVC_FILE_INFO_DAO.getMetaType()))
			{
				lbNVEntitySelections.setVisible(true);
				
				lbNVEntitySelections.clear();
				lbNVEntitySelections.addItem("Add referenced file");
				lbNVEntitySelections.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
				
				Set<NVEntity> results = SharedDataUtil.searchFolderByNVConfigEntity(getEntryFolder(), getSelectedNVConfigEntity());
				
				nveMap.clear();
				
				if (results != null)
				{
					for (NVEntity nve : results)
					{
						lbNVEntitySelections.addItem(nve.getName());
						
						nveMap.put(lbNVEntitySelections.getItemCount() - 1, nve);
					}
				}
			}
			else
			{
				lbNVEntitySelections.setVisible(true);
				
				lbNVEntitySelections.clear();
				lbNVEntitySelections.addItem("Add new " + getSelectedNVConfigEntity().getDisplayName());
				lbNVEntitySelections.addItem("Add referenced " + getSelectedNVConfigEntity().getDisplayName());
				lbNVEntitySelections.getElement().getElementsByTagName("option").getItem(1).setAttribute("disabled", "disabled");
				
				Set<NVEntity> results = SharedDataUtil.searchFolderByNVConfigEntity(getEntryFolder(), getSelectedNVConfigEntity());
				
				nveMap.clear();
				
				if (results != null)
				{
					for (NVEntity nve : results)
					{
						if (nve instanceof FormInfoDAO)
						{
							FormInfoDAO formInfo = (FormInfoDAO) nve;
							
							if (SUS.isNotEmpty(formInfo.getName()))
							{
								lbNVEntitySelections.addItem(formInfo.getName());
							}
							else if (formInfo.getFormReference() != null && SUS.isNotEmpty(formInfo.getFormReference().getName()))
							{
								lbNVEntitySelections.addItem(formInfo.getFormReference().getName());
							}
							else if (formInfo.getFormReference() != null && SUS.isNotEmpty(formInfo.getFormReference().getNVConfig().getName()))
							{
								lbNVEntitySelections.addItem(formInfo.getFormReference().getNVConfig().getName());
							}
						}
						else
						{
							if (SUS.isNotEmpty(nve.getName()))
							{
								lbNVEntitySelections.addItem(nve.getName());
							}
							else
							{
								lbNVEntitySelections.addItem(nve.getNVConfig().getDisplayName());
							}
						}
						
						nveMap.put(lbNVEntitySelections.getItemCount() - 1, nve);
					}
				}
			}
		}
		else
		{
			lbNVEntitySelections.setVisible(false);
		}
	}
	
	private NVConfigEntity getSelectedNVConfigEntity()
	{
		if (lbNVConfigEntityTypes.getSelectedIndex() != -1)
		{
			if (lbNVConfigEntityTypes.getItemText(lbNVConfigEntityTypes.getSelectedIndex()) != null)
			{
				NVConfigEntity nvce = nvceMap.get(lbNVConfigEntityTypes.getItemText(lbNVConfigEntityTypes.getSelectedIndex()));
				
				if (nvce != null)
				{
					return nvce;
				}
			}
		}
		
		return null;
	}
	
	public NVEntity getSelectedNVEntity()
	{
		NVEntity nve = null;
		
		if (getSelectedNVConfigEntity() != null && getSelectedNVConfigEntity().getMetaType().equals(FileInfoDAO.NVC_FILE_INFO_DAO.getMetaType()))
		{
			if (lbNVEntitySelections.getSelectedIndex() != -1 && lbNVEntitySelections.getSelectedIndex() != 0)
			{		
				nve = nveMap.get(lbNVEntitySelections.getSelectedIndex());
			}
		}
		else
		{
			if (lbNVEntitySelections.getSelectedIndex() != -1 && lbNVEntitySelections.getSelectedIndex() != 1)
			{			
				if (lbNVEntitySelections.getSelectedIndex() == 0)
				{
					nve = ZWDataFactory.SINGLETON.createNVEntity(getSelectedNVConfigEntity().getMetaType().getName());
				}
				else
				{
					nve = nveMap.get(lbNVEntitySelections.getSelectedIndex());
				}
			}	
		}
		
		return nve;
	}

	public void showPopup()
	{
		popup.center();
	}
	
	public void hidePopup()
	{
		popup.hide();
	}
	
}