package org.zoxweb.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.client.data.ApplicationClientDAO;
import org.zoxweb.client.data.events.CRUDNVEntityEvent;
import org.zoxweb.client.data.events.CRUDNVEntityHandler;
import org.zoxweb.client.data.events.ExportFormControllerHandler;
import org.zoxweb.client.data.events.SaveControllerHandler;
import org.zoxweb.client.data.events.SearchControllerHandler;
import org.zoxweb.client.rpc.CallBackHandler;
import org.zoxweb.client.widget.CustomPagerWidget;
import org.zoxweb.client.widget.NVEntityDefaultWidget;
import org.zoxweb.client.widget.NVEntityWidget;
import org.zoxweb.client.widget.NVStringArrayValuesWidget;
import org.zoxweb.client.widget.PopupUtil;
import org.zoxweb.shared.data.CRUDNVEntityDAO;
import org.zoxweb.shared.data.FormInfoDAO;
import org.zoxweb.shared.data.NVEntityContainerDAO;
import org.zoxweb.shared.data.SystemInfoDAO;
import org.zoxweb.shared.data.events.AddActionListener;
import org.zoxweb.shared.data.events.ClearActionListener;
import org.zoxweb.shared.data.events.DeleteActionListener;
import org.zoxweb.shared.data.events.EditActionListener;
import org.zoxweb.shared.data.events.ExportFormListener;
import org.zoxweb.shared.data.events.PreviewActionListener;
import org.zoxweb.shared.data.events.SaveActionListener;
import org.zoxweb.shared.data.events.ValueSelectionListener;
import org.zoxweb.shared.util.CRUD;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;
import org.zoxweb.shared.data.DataConst.FormMode;
import org.zoxweb.shared.data.FolderInfoDAO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("serial")
public class NVEntityContainerDAOWidget 
	extends NVEntityWidget
	implements ValueSelectionListener<Integer>, CRUDNVEntityHandler,
		   SaveActionListener<AutoCloseable>, ClearActionListener<AutoCloseable>, AddActionListener<AutoCloseable>, DeleteActionListener<AutoCloseable>,
		   PreviewActionListener<AutoCloseable>, EditActionListener<AutoCloseable>, ExportFormListener<AutoCloseable>, HasValueChangeHandlers<NVEntity>
{
	
	private static NVEntityContainerDAOWidgetUiBinder uiBinder 
		= GWT.create(NVEntityContainerDAOWidgetUiBinder.class);

	interface NVEntityContainerDAOWidgetUiBinder 
		extends UiBinder<Widget, NVEntityContainerDAOWidget>
	{
	}
	
	interface Style 
  		extends CssResource 
	{
		String scrollPanel();
		String flexTable_RowDivider();
		String header();
	}

	@UiField Style style;
	@UiField FlexTable flexTable;

	private Label labelName = new Label();
	private TextBox tbName = new TextBox();
	private Label labelDescription = new Label();
	private TextBox tbDescription = new TextBox();
	private Label labelCanonicalID = new Label();
	private TextBox tbCanonicalID = new TextBox();
	private ScrollPanel scrollPanel;
	private HorizontalPanel hpPager;
	private CustomPagerWidget customPager;
	private NVEntityWidget contentWidget = null;
	private int currentSlideNumber = 1;
	private AddNVESlideWidget addNVEntitySlide;
	private PopupWarningMessageWidget popupDeleteConfirmation;
	private List<NVEntity> contentList = new ArrayList<NVEntity> ();
	private boolean previewMode = false;
	private boolean addOrDeleteSlide = false;
	private List<NVConfigEntity> selectionNVCEs = new ArrayList<NVConfigEntity>();
	private NVConfigEntity nvce = null;
	private NVStringArrayValuesWidget applicationProperties = null;
	private SimplePanel spContent = null;
	private SaveControllerHandler<NVEntityContainerDAOWidget, NVEntity> saveControllerHandler;
	private ExportFormControllerHandler<NVEntityContainerDAOWidget, ?> exportControllerHandler;
	private SearchControllerHandler<NVEntityContainerDAOWidget, List<NVEntity>> searchControllerHandler;
	private AutoCloseable autoCloseableSave;
	private FolderInfoDAO entryFolder;
	
	public NVEntityContainerDAOWidget(NVEntityContainerDAO nveContainer, NVConfigEntity... nvces)
	{
		this(nveContainer, false, nvces);
	}
	
	public NVEntityContainerDAOWidget(NVEntityContainerDAO nveContainer, boolean readOnly, NVConfigEntity... nvces)
	{
		this((NVConfigEntity) nveContainer.getNVConfig(), readOnly, nvces);
		
		if (nveContainer != null)
		{
			setValue(nveContainer);
		}
	}
	
	public NVEntityContainerDAOWidget(NVConfigEntity nvce, NVConfigEntity... nvces)
	{
		this(nvce, false, nvces);
	}
	
	public NVEntityContainerDAOWidget(NVConfigEntity nvce, boolean readOnly, NVConfigEntity... nvces)
	{
		super();
		initWidget(uiBinder.createAndBindUi(this));
		
		this.nvce = nvce;
		
		setSelectionNVCEs(nvces);
		
		popupDeleteConfirmation = new PopupWarningMessageWidget();
		popupDeleteConfirmation.addOkButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				deleteSlide();
				popupDeleteConfirmation.hidePopup();
			}
			
		});
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(style.scrollPanel());
		scrollPanel.setSize("100%", "35EM");
		
		hpPager = new HorizontalPanel();
		hpPager.setSize("100%", "4EM");
		hpPager.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hpPager.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);		
		
		labelName.setSize("10EM", "1.5EM");
		labelDescription.setSize("10EM", "1.5EM");
		
		labelName.setStyleName(style.header());
		labelDescription.setStyleName(style.header());
		
		tbName.setSize("99%", "1.5EM");
		tbDescription.setSize("99%", "1.5EM");
		
		if (nvce.getMetaType() == SystemInfoDAO.class)
		{
			labelName.setText("System Name");
			labelDescription.setText("System Description");
			
			//	row = 0
			int row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, labelName);
			flexTable.setWidget(row, 1, tbName);
			flexTable.getCellFormatter().setHeight(row, 0, "2EM");
			flexTable.getCellFormatter().setHeight(row, 1, "2EM");
			flexTable.getCellFormatter().setWidth(row, 0, "10EM");
			flexTable.getCellFormatter().setWidth(row, 1, "100%");
			flexTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);			
			
			//	row = 1
			row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, labelDescription);
			flexTable.setWidget(row, 1, tbDescription);
			flexTable.getCellFormatter().setHeight(row, 0, "2EM");
			flexTable.getCellFormatter().setHeight(row, 1, "2EM");
			flexTable.getCellFormatter().setWidth(row, 0, "10EM");
			flexTable.getCellFormatter().setWidth(row, 1, "100%");
			flexTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);
			
			labelCanonicalID.setText("Canonical ID");
			
			labelCanonicalID.setSize("10EM", "1.5EM");
			labelCanonicalID.setStyleName(style.header());
			tbCanonicalID.setSize("99%", "1.5EM");
			tbCanonicalID.setReadOnly(true);
			
			//	row = 2
			row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, labelCanonicalID);
			flexTable.setWidget(row, 1, tbCanonicalID);
			flexTable.getCellFormatter().setHeight(row, 0, "2EM");
			flexTable.getCellFormatter().setHeight(row, 1, "2EM");
			flexTable.getCellFormatter().setWidth(row, 0, "10EM");
			flexTable.getCellFormatter().setWidth(row, 1, "100%");
			flexTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);
			
			//	row = 3
			row = flexTable.getRowCount();
			applicationProperties = new NVStringArrayValuesWidget(SystemInfoDAO.APPLICATION_PROPS, false);
			applicationProperties.setVisibleButtons(true, false, false);
			applicationProperties.setArrayHeader("Application Properties");
			applicationProperties.setWidgetSize("100%", "8EM");
			
			spContent = new SimplePanel();
			spContent.setSize("100%", "100%");
			
			FlexTable flexTablePlaceHolder = new FlexTable();
			flexTablePlaceHolder.setSize("100%", "100%");
			flexTablePlaceHolder.setWidget(0, 0, applicationProperties);
			flexTablePlaceHolder.setWidget(1, 0, spContent);
			flexTablePlaceHolder.getCellFormatter().setWidth(0, 0, "100%");
			flexTablePlaceHolder.getCellFormatter().setWidth(1, 0, "100%");

			scrollPanel.add(flexTablePlaceHolder);
			
			flexTable.setWidget(row, 0, scrollPanel);
			flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
			flexTable.getCellFormatter().setHeight(row, 0, "100%");
			flexTable.getCellFormatter().setWidth(row, 0, "100%");
			
			//	row = 4
			row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, hpPager);
			flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
			flexTable.getCellFormatter().setHeight(row, 0, "4EM");
			flexTable.getCellFormatter().setWidth(row, 0, "100%");
		}
		else
		{
			labelName.setText("Dossier Name");
			labelDescription.setText("Dossier Description");

			//	row = 0
			int row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, labelName);
			flexTable.setWidget(row, 1, tbName);
			flexTable.getCellFormatter().setHeight(row, 0, "2EM");
			flexTable.getCellFormatter().setHeight(row, 1, "2EM");
			flexTable.getCellFormatter().setWidth(row, 0, "10EM");
			flexTable.getCellFormatter().setWidth(row, 1, "100%");
			flexTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);
			
			//	row = 1
			row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, labelDescription);
			flexTable.setWidget(row, 1, tbDescription);
			flexTable.getCellFormatter().setHeight(row, 0, "2EM");
			flexTable.getCellFormatter().setHeight(row, 1, "2EM");
			flexTable.getCellFormatter().setWidth(row, 0, "10EM");
			flexTable.getCellFormatter().setWidth(row, 1, "100%");
			flexTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);
			
			//	row = 2
			row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, scrollPanel);
			flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
			flexTable.getCellFormatter().setHeight(row, 0, "100%");
			flexTable.getCellFormatter().setWidth(row, 0, "100%");
			
			//	row = 3
			row = flexTable.getRowCount();
			flexTable.setWidget(row, 0, hpPager);
			flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
			flexTable.getCellFormatter().setHeight(row, 0, "4EM");
			flexTable.getCellFormatter().setWidth(row, 0, "100%");
			
			flexTable.insertRow(2);
			flexTable.getFlexCellFormatter().setColSpan(2, 0, 2);
			flexTable.getFlexCellFormatter().setStyleName(2, 0, style.flexTable_RowDivider());
		}
		
		
		tbName.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Name");
        		}
        	}
        });
		
		tbDescription.addAttachHandler(new Handler()
        {
        	public void onAttachOrDetach(AttachEvent event)
        	{
        		if (event.isAttached()) 
        		{
        			((UIObject) event.getSource()).getElement().setAttribute("placeHolder", "Description (optional)");
        		}
        	}
        });
		
		setReadOnly(readOnly);
		
		customPager = new CustomPagerWidget(0, !readOnly);
		customPager.addValueSelectionListener(this);
		
		if (!readOnly)
		{
			customPager.addFirstPageButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (isCurrentNVEValid())
					{
						customPager.setSelectedPageNumber(1);
					}
				}
			});
			
			customPager.addLastPageButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (isCurrentNVEValid())
					{
						customPager.setSelectedPageNumber(customPager.getPagerSize());
					}
				}
			});
			
			customPager.addPreviousButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (isCurrentNVEValid() && customPager.getSelectedPageNumber() != 1 && (customPager.getSelectedPageNumber() - 1) > 0)
					{
						customPager.setSelectedPageNumber(customPager.getSelectedPageNumber() - 1);
					}
				}
			});
			
			customPager.addNextButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (isCurrentNVEValid() && customPager.getSelectedPageNumber() != customPager.getPagerSize() && (customPager.getSelectedPageNumber() + 1) < (customPager.getPagerSize() + 1))
					{
						customPager.setSelectedPageNumber(customPager.getSelectedPageNumber() + 1);
					}
				}
			});
		}
		
		hpPager.add(customPager);
		
		ApplicationClientDAO.DEFAULT.getHandlerManager().addHandler(CRUDNVEntityEvent.TYPE, this);
	}
	
	public synchronized void setSelectionNVCEs(NVConfigEntity... nvces)
	{
		selectionNVCEs.clear();
		
		if (nvces != null)
		{
			for (NVConfigEntity nvce : nvces)
			{
				if (nvce != null)
				{
					selectionNVCEs.add(nvce);
				}
			}
		}
	}
	
	private void addSlide(NVEntity nve)
	{
		if (currentSlideNumber > 0)
		{
			//	Get index of current slide
			int index = currentSlideNumber - 1;
			
			if (index >= 0)
			{
				addOrDeleteSlide = true;
				
				if (getContainerSize() > 0)
				{
					//	Add nve to list		
					contentList.add(index + 1, nve);
					
					//	Update pager size
					customPager.setPagerSize(getContainerSize());
					
					//	Update selected slide
					customPager.selectedValue(currentSlideNumber + 1);
				}
				else
				{
					//	Add nve to list		
					contentList.add(0, nve);
					
					//	Update pager size
					customPager.setPagerSize(getContainerSize());
					
					//	Update selected slide
					customPager.selectedValue(currentSlideNumber);
				}
			}
		}
	}
	
	private void deleteSlide()
	{
		if (currentSlideNumber > 0 && currentSlideNumber <= getContainerSize() && getContainerSize() > 0)
		{
			//	Get index of current slide
			int index = currentSlideNumber - 1;
		
			if (index >= 0)
			{
				//	Store total size before removing slide
				int totalSizeBefore = getContainerSize();
				
				//	Remove nve from list				
				contentList.remove(index);
				
				//	Update pager size
				customPager.setPagerSize(getContainerSize());
				
				addOrDeleteSlide = true;
				
				//	Update selected slide
				if (getContainerSize() > 0)
				{
					if (currentSlideNumber == totalSizeBefore)
					{
						customPager.selectedValue(currentSlideNumber - 1);
					}
					else
					{
						customPager.selectedValue(currentSlideNumber);
					}
				}
				else
				{
					customPager.selectedValue(0);
				}
			}
		}
	}
	
	public int getContainerSize()
	{
		 return contentList.size();
	}
	
	@Override
	public void selectedValue(Integer value) 
	{		
		if (readOnly || addOrDeleteSlide)
		{
			addOrDeleteSlide = false;
			changeSlide(value);
		}
		else if (isCurrentNVEValid())
		{
			updateContentSlideChange(value);
		}
	}
	
	public boolean isCurrentNVEValid()
	{
		switch (formMode)
		{
		case DESIGN:
		case READ:
			return true;
		case EDIT:
			if (contentWidget != null)
			{
				try
				{
					if (contentWidget.getValue() != null)
					{
						return true;
					}
				}
				catch (Exception e)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private NVEntity getSelectedNVEntity()
	{
		if (currentSlideNumber > 0)
		{
			int index = currentSlideNumber - 1;
		
			if (contentList.size() > 0 && index >= 0 && index < contentList.size())
			{
				return contentList.get(index);
			}
		}
		
		return null;
	}

	private void updateContentSlideChange(int slideNumber)
	{
		if (slideNumber > 0)
		{
			//	Save current content before switching slide
			if (contentWidget != null && formMode != FormMode.READ)
			{
				//	Algorithm
				//	1. Check if widget NVEntity exists or new
				//	2. If exists, save update value in container, then switch slide.
				//	3. If new, update value in container then switch slide.
				
				if (currentSlideNumber > 0)
				{
					int index = currentSlideNumber - 1;
					
					try
					{
						if (contentWidget.getValue() != null && contentList.get(index) != null )
						{
							if (contentList.get(index).getReferenceID() != null
									&& contentWidget.getValue().getReferenceID() != null
									&& contentWidget.getValue().getReferenceID().equals(contentList.get(index).getReferenceID())
								)
							{
								contentList.set(index, contentWidget.getValue());
							}
							else
							{
								contentList.set(index, contentWidget.getValue());
							}
							
						}
					}
					catch (Exception e)
					{
						
					}
				}
			}
			
			changeSlide(slideNumber);
		}
		else
		{
			if (spContent != null)
			{
				spContent.clear();
			}
			else
			{
				scrollPanel.clear();
			}
		}
	}
	
	private void changeSlide(int slideNumber)
	{
		currentSlideNumber = slideNumber;
		
		if (getSelectedNVEntity() != null)
		{
			contentWidget = NVCWidgetFactory.DEFAULT.createNVEntityWidget(getSelectedNVEntity());
			contentWidget.hideHyperLink();
			
			if (contentWidget != null)
			{
				if (readOnly || previewMode)
				{
					contentWidget.setReadOnly(true);
				}
				
				contentWidget.setSize("98.5%", "100%");
				
				if (spContent != null)
				{
					spContent.clear();
					spContent.add(contentWidget);
				}
				else
				{
					scrollPanel.clear();
					scrollPanel.add(contentWidget);
				}
				
				if (contentWidget instanceof NVEntityDefaultWidget)
				{
					((NVEntityDefaultWidget) contentWidget).setHeader(contentWidget.getNVEntity().getNVConfig().getDisplayName());
				}
				
				ValueChangeEvent.fire(this, getSelectedNVEntity());
			}
		}
	}

	@Override
	public boolean isContentVisible() 
	{
		return false;
	}

	@Override
	public void clear()
	{
		
	}

	@Override
	public void setWidgetWidth(String width)
	{
		flexTable.setWidth(width);
		
		if (contentWidget != null)
		{
			contentWidget.setWidgetWidth("98.5%");
		}
	}

	@Override
	public void setWidgetHeight(String height)
	{
		flexTable.setHeight(height);
		
		if (spContent != null)
		{
			spContent.setHeight("100%");
		}
		else
		{
			scrollPanel.setHeight("100%");
		}
		
		if (contentWidget != null)
		{
			contentWidget.setWidgetHeight("100%");
		}
	}

	@Override
	public boolean isOuterScrollEnabled()
	{
		return false;
	}

	@Override
	public Widget getWidget()
	{
		return null;
	}
	
	@Override
	public void setWidgetValue(NVEntity value) 
	{
		currentNVE = value;
		
		if (currentNVE != null)
		{
			if (nvce.getMetaType() == SystemInfoDAO.class)
			{
				SystemInfoDAO systemInfo = (SystemInfoDAO) currentNVE;
				
				customPager.setPagerSize(systemInfo.getContent().size());
				
				if (!SharedStringUtil.isEmpty(systemInfo.getName()))
				{
					tbName.setValue(systemInfo.getName());
				}
				
				if (!SharedStringUtil.isEmpty(systemInfo.getDescription()))
				{
					tbDescription.setValue(systemInfo.getDescription());
				}
				
				if (!SharedStringUtil.isEmpty(systemInfo.getCanonicalID()))
				{
					labelCanonicalID.setVisible(true);
					tbCanonicalID.setVisible(true);
					
					tbCanonicalID.setText(systemInfo.getCanonicalID());
				}
				else
				{
					labelCanonicalID.setVisible(false);
					tbCanonicalID.setVisible(false);
				}
				
				if (applicationProperties != null && systemInfo.getApplicationProperties() != null && systemInfo.getApplicationProperties().size() > 0)
				{					
					applicationProperties.setValue(systemInfo.getApplicationProperties());
				}
				
				if (systemInfo.getContentAsList() != null)
				{
					contentList = systemInfo.getContentAsList();
				}
			}
			else
			{
				NVEntityContainerDAO nveContainer = (NVEntityContainerDAO) currentNVE;
				
				customPager.setPagerSize(nveContainer.getContent().size());
				
				if (!SharedStringUtil.isEmpty(nveContainer.getName()))
				{
					tbName.setValue(nveContainer.getName());
				}
				
				if (!SharedStringUtil.isEmpty(nveContainer.getDescription()))
				{
					tbDescription.setValue(nveContainer.getDescription());
				}
				
				if (nveContainer.getContentAsList() != null)
				{
					contentList = nveContainer.getContentAsList();
				}
			}
			
			if (getSelectedNVEntity() != null)
			{
				customPager.selectedValue(currentSlideNumber);
			}
			else
			{
				customPager.selectedValue(0);
			}
		}
	}

	@Override
	public NVEntity getWidgetValue()
	{
		if (nvce.getMetaType() == SystemInfoDAO.class)
		{
			SystemInfoDAO systemInfo;
			
			if (currentNVE != null)
			{
				systemInfo = (SystemInfoDAO) currentNVE;
			}
			else
			{	
				systemInfo = new SystemInfoDAO();
			}
			
			systemInfo.setName(tbName.getValue());
			systemInfo.setDescription(tbDescription.getValue());
			systemInfo.setContent(contentList);
			
			if (applicationProperties != null)
			{
				for (NVPair nvp : applicationProperties.getValue().values())
				{
					systemInfo.getApplicationProperties().add(nvp);
				}
			}
			
			return systemInfo;
			
		}
		else
		{
			NVEntityContainerDAO nveContainer;
			
			if (currentNVE != null)
			{
				nveContainer = (NVEntityContainerDAO) currentNVE;
			}
			else
			{	
				nveContainer = new NVEntityContainerDAO();
			}
			
			nveContainer.setName(tbName.getValue());
			nveContainer.setDescription(tbDescription.getValue());
			nveContainer.setContent(contentList);
			
			return nveContainer;
		}
	}

	@Override
	public void setWidgetValue(String value) 
	{
		
	}
	
	@Override
	public void setReadOnly(boolean readOnly) 
	{
		this.readOnly = readOnly;
		
		if (readOnly)
		{			
			tbName.setReadOnly(true);
			tbDescription.setReadOnly(true);
			
			if (contentWidget != null)
			{
				contentWidget.setReadOnly(true);
			}
		}
		else
		{			
			tbName.setReadOnly(false);
			tbDescription.setReadOnly(false);

			if (contentWidget != null)
			{
				contentWidget.setReadOnly(false);
			}
		}
	}
	
	@Override
	public void setFormMode(FormMode mode)
	{
		super.setFormMode(mode);
		
		if (mode == FormMode.READ)
		{
			setReadOnly(true);
		}
	}

	public void setSaveControllerHandler(SaveControllerHandler<NVEntityContainerDAOWidget, NVEntity> saveControllerHandler)
	{
		this.saveControllerHandler = saveControllerHandler;
	}
	
	public void setExportFormControllerHandler(ExportFormControllerHandler<NVEntityContainerDAOWidget, ?> exportControllerHandler)
	{
		this.exportControllerHandler = exportControllerHandler;
	}
	
	public void setSearchControllerHandler(SearchControllerHandler<NVEntityContainerDAOWidget, List<NVEntity>> searchControllerHandler)
	{
		this.searchControllerHandler = searchControllerHandler;
	}
	
	@Override
	public void actionSave(AutoCloseable autoCloseable)
	{
		if (currentSlideNumber > 0 && contentWidget != null && formMode != FormMode.READ)
		{
			int index = currentSlideNumber - 1;
				
			try
			{
				if (contentWidget.getValue() != null && contentList.get(index) != null)
				{
					contentList.set(index, contentWidget.getValue());
					saveAll(autoCloseable);
				}
			}
			catch (Exception e)
			{
				
			}
		}
		else
		{
			saveAll(autoCloseable);
		}
	}
	
	private void saveAll(AutoCloseable autoCloseable)
	{
		if (isCurrentNVEValid() && saveControllerHandler != null)
		{
			autoCloseableSave = autoCloseable;
			
			saveControllerHandler.actionSave(this, new CallBackHandler<NVEntity>(
					new AsyncCallback<NVEntity>()
			{
				
				private AutoCloseable autoCloseable;
				
				@Override
				public void onFailure(Throwable caught) 
				{
					PopupUtil.SINGLETON.showPopup("Update Failed", caught.getMessage());
				}
	
				@Override
				public void onSuccess(NVEntity result) 
				{
					if (result != null)
					{
						if (autoCloseable != null)
						{
							if (spContent != null)
							{
								spContent.clear();
							}
							else
							{
								scrollPanel.clear();
							}
									
							SharedUtil.close(autoCloseable);
						}
						
						ApplicationClientDAO.DEFAULT.fireEvent(new CRUDNVEntityEvent(new CRUDNVEntityDAO(CRUD.UPDATE, result)));
					}
				}
				
				AsyncCallback<NVEntity> init(AutoCloseable autoCloseable)
				{
					this.autoCloseable = autoCloseable;
					return this;
				}
				
			}.init(autoCloseable)));
		}	
	}
	
	public void autoCloseSave()
	{
		if (autoCloseableSave != null)
		{
			if (spContent != null)
			{
				spContent.clear();
			}
			else
			{
				scrollPanel.clear();
			}
			
			SharedUtil.close(autoCloseableSave);
		}
	}
	
	@Override
	public void actionClear(AutoCloseable autoCloseable)
	{
		if (contentWidget != null)
		{
			contentWidget.clear();
		}
	}
	
	@Override
	public void actionAdd(AutoCloseable autoCloseable) 
	{
		if (isCurrentNVEValid())
		{
			addNVEntitySlide = new AddNVESlideWidget(searchControllerHandler, entryFolder, selectionNVCEs.toArray(new NVConfigEntity[0]));
			addNVEntitySlide.addOkButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event) 
				{
					if (addNVEntitySlide.getSelectedNVEntity() != null)
					{
						addSlide(addNVEntitySlide.getSelectedNVEntity());
						addNVEntitySlide.hidePopup();
					}
				}
				
			});
			addNVEntitySlide.addSelectedNVEntityDoubleClickHandler(new DoubleClickHandler()
			{
				@Override
				public void onDoubleClick(DoubleClickEvent event) 
				{
					if (addNVEntitySlide.getSelectedNVEntity() != null)
					{
						addSlide(addNVEntitySlide.getSelectedNVEntity());
						addNVEntitySlide.hidePopup();
					}
				}
				
			});
			
			addNVEntitySlide.showPopup();
		}
	}
	
	@Override
	public void actionDelete(AutoCloseable autoCloseable)
	{
		popupDeleteConfirmation.showPopup("Delete Slide", "Are you sure you want to delete the current slide?");
	}

	@Override
	public void actionPreview(AutoCloseable v) 
	{
		setPreviewMode(true);
	}
	
	@Override
	public void actionEdit(AutoCloseable v)
	{
		setPreviewMode(false);
	}
	
	@Override
	public void actionExportForm(AutoCloseable v) 
	{
		if (exportControllerHandler != null)
		{
			exportControllerHandler.actionExport(this, null);
		}
	}
	
	public boolean isPreviewMode()
	{
		return previewMode;
	}
	
	private void setPreviewMode(boolean previewMode)
	{		
		if (contentWidget != null)
		{
			this.previewMode = previewMode;
			
			contentWidget.setReadOnly(previewMode);
			
//			if (applicationProperties != null)
//			{
//				applicationProperties.setReadOnly(previewMode);
//			}
		}
	}	
	
	private void updateContainedNVEntity(NVEntity nve, CRUD crud)
	{
		for (int index = 0; index < contentList.size(); index++)
		{
			if (contentList.get(index).getReferenceID() != null && nve.getReferenceID() != null && nve.getReferenceID().equals(contentList.get(index).getReferenceID()))
			{
				if (crud == CRUD.UPDATE)
				{
					if (contentWidget.getValue() != null && contentWidget.getValue().getReferenceID() != null && nve.getReferenceID().equals(contentWidget.getValue().getReferenceID()))
					{
						contentList.set(index, nve);
						contentWidget.setValue(nve);
					}
					else
					{
						contentList.set(index, nve);
					}
					break;
				}
				else if (crud == CRUD.DELETE)
				{
					if (contentWidget.getValue() != null && contentWidget.getValue().getReferenceID() != null && nve.getReferenceID().equals(contentWidget.getValue().getReferenceID()))
					{
						deleteSlide();
					}
					else
					{
						contentList.remove(index);
						customPager.setPagerSize(getContainerSize());
					}
					break;
				}
			}
		}
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
		if (!SharedStringUtil.isEmpty(tbDescription.getValue()))
		{
			return tbDescription.getValue();
		}
		
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
		return true;
	}
	
	public NVEntityWidget getContentWidget()
	{
		return contentWidget;
	}
	
	public void setEntryFolder(FolderInfoDAO entryFolder)
	{
		this.entryFolder = entryFolder;
	}
    
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<NVEntity> handler)
	{
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	@Override
	public void applyCRUD(CRUD crud, NVEntity v) 
	{		
		switch (crud)
		{
			case CREATE:
				break;
			case DELETE:
				if (v instanceof FormInfoDAO && ((FormInfoDAO) v).getFormReference() != null)
				{
					if (((FormInfoDAO) v).getFormReference() instanceof NVEntityContainerDAO && SharedUtil.equals(((FormInfoDAO) v).getFormReference(), getValue()))
					{
						//	Form was deleted, therefore NVEntityContainer no longer exists and widget must be deleted.
						//setValue(((FormInfoDAO) v).getFormReference());
					}
					else
					{
						updateContainedNVEntity(((FormInfoDAO) v).getFormReference(), CRUD.DELETE);	
					}
				}
				else if (v.getReferenceID() != null)
				{
					updateContainedNVEntity(v, CRUD.DELETE);	
				}
				break;
			case READ:
				break;
			case UPDATE:
				if (v instanceof FormInfoDAO)
				{
					if (SharedUtil.equals(v, getAssociatedFormInfo()))
					{
						setAssociatedFormInfo((FormInfoDAO) v);
					}
					
					if (((FormInfoDAO) v).getFormReference() != null)
					{
						if (((FormInfoDAO) v).getFormReference() instanceof NVEntityContainerDAO && SharedUtil.equals(((FormInfoDAO) v).getFormReference(), getValue()))
						{
							setValue(((FormInfoDAO) v).getFormReference());
						}
						else
						{
							updateContainedNVEntity(((FormInfoDAO) v).getFormReference(), CRUD.UPDATE);	
						}
					}
				}
				else if (v.getReferenceID() != null)
				{
					updateContainedNVEntity(v, CRUD.UPDATE);	
				}
				break;
			case MOVE:
				break;
			case SHARE:
			case EXEC:
				break;
		}
	}

}