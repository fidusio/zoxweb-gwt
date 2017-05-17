package org.zoxweb.client.widget;

import org.zoxweb.client.widget.CustomPushButtonWidget;
import org.zoxweb.shared.data.FileInfoDAO;
import org.zoxweb.shared.data.events.AddActionListener;
import org.zoxweb.shared.data.events.ClearActionListener;
import org.zoxweb.shared.data.events.DeleteActionListener;
import org.zoxweb.shared.data.events.EditActionListener;
import org.zoxweb.shared.data.events.ExportFormListener;
import org.zoxweb.shared.data.events.PreviewActionListener;
import org.zoxweb.shared.data.events.SaveActionListener;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.widget.WidgetConst;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class NVEntityContainerControlPanelWidget
	extends NVEntityControlPanelBaseWidget
{
	
	private CustomPushButtonWidget cpbAdd;
	private CustomPushButtonWidget cpbDelete;
	private CustomPushButtonWidget cpbPreview;
	private CustomPushButtonWidget cpbEdit;
	private CustomPushButtonWidget cpbExport;
	private ValueChangeHandler<NVEntity> valueChangeHandler;
	
	public NVEntityContainerControlPanelWidget(NVEntityContainerDAOWidget nveWidget)
	{
		this(nveWidget, null);
	}
	
	public NVEntityContainerControlPanelWidget(NVEntityContainerDAOWidget nveWidget, AutoCloseable autoCloseable)
	{
		super(nveWidget, autoCloseable);
		
		setup();
	}

	private void setup()
	{
		cpbAdd = new CustomPushButtonWidget(WidgetConst.ImageURL.ADD.getValue(), WidgetConst.ImageURL.ADD.getName());
		cpbDelete = new CustomPushButtonWidget(WidgetConst.ImageURL.DELETE.getValue(), WidgetConst.ImageURL.DELETE.getName());
		cpbPreview = new CustomPushButtonWidget(WidgetConst.ImageURL.PREVIEW.getValue(), WidgetConst.ImageURL.PREVIEW.getName());
	 	cpbEdit = new CustomPushButtonWidget(WidgetConst.ImageURL.EDIT.getValue(), WidgetConst.ImageURL.EDIT.getName());
	 	cpbExport = new CustomPushButtonWidget(WidgetConst.ImageURL.EXPORT_FORM_ICON.getValue(), WidgetConst.ImageURL.EXPORT_FORM_ICON.getName());
	 	
	 	
		valueChangeHandler = new ValueChangeHandler<NVEntity>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<NVEntity> event) 
			{
				if (event.getValue() != null && event.getValue() instanceof FileInfoDAO)
				{
					cpbExport.setVisible(false);
				}
				else
				{
					cpbExport.setVisible(true);
				}
			}
	
		};
		
		((NVEntityContainerDAOWidget) nveWidget).addValueChangeHandler(valueChangeHandler);
	 	
	 	
		if (nveWidget instanceof AddActionListener)
		{			
			cpbAdd.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					((AddActionListener<?>) nveWidget).actionAdd(null);
				}
				
			});
			
			addButton(cpbAdd);
		}
		
		if (nveWidget instanceof DeleteActionListener)
		{			
			cpbDelete.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					((DeleteActionListener<?>) nveWidget).actionDelete(null);
				}
				
			});
			
			addButton(cpbDelete);
		}
		
		if (nveWidget instanceof PreviewActionListener)
		{
			cpbPreview.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (((NVEntityContainerDAOWidget) nveWidget).getContainerSize() > 0)
					{
						((PreviewActionListener<?>) nveWidget).actionPreview(null);
						setPreviewMode(true);
					}
				}
				
			});
			
			addButton(cpbPreview);
		}
		
		if (nveWidget instanceof EditActionListener)
		{
			cpbEdit.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (((NVEntityContainerDAOWidget) nveWidget).getContainerSize() > 0)
					{
						((EditActionListener<?>) nveWidget).actionEdit(null);
						setPreviewMode(false);
					}
				}
			});
		}
		
		if (nveWidget instanceof ExportFormListener)
		{
			cpbExport.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					if (((NVEntityContainerDAOWidget) nveWidget).getContainerSize() > 0)
					{
						((ExportFormListener<?>) nveWidget).actionExportForm(null);
					}
				}
			});
			
			addButton(cpbExport);
		}
	}
	

	@Override
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}
	
	@Override
	public void setPreviewMode(boolean previewMode)
	{
		clear();
		
		if (readOnly)
		{
			
		}
		else
		{
			if (previewMode)
			{
				if (nveWidget instanceof EditActionListener)
				{
					addButton(cpbEdit);
				}
			}
			else
			{
				if (nveWidget instanceof SaveActionListener)
				{
					addButton(cpbSave);
				}
				
				if (nveWidget instanceof ClearActionListener)
				{
					addButton(cpbClear);
				}
				
				if (nveWidget instanceof AddActionListener)
				{
					addButton(cpbAdd);
				}
				
				if (nveWidget instanceof DeleteActionListener)
				{
					addButton(cpbDelete);
				}
				
				if (nveWidget instanceof PreviewActionListener)
				{
					addButton(cpbPreview);
				}
				
				if (nveWidget instanceof ExportFormListener)
				{
					addButton(cpbExport);
				}
			}
		}
	}
	
}