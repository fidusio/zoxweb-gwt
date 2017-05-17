package org.zoxweb.client.widget;

import org.zoxweb.client.data.events.AddControllerHandler;
import org.zoxweb.client.rpc.CallBackHandler;
import org.zoxweb.client.widget.PopupUtil;
import org.zoxweb.shared.util.ArrayValues;
import org.zoxweb.shared.util.ExceptionCollection;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

@SuppressWarnings("serial")
public class NVEntityArrayWidget
	extends NVArrayWidget<ArrayValues<NVEntity>>
{
	
	private AddControllerHandler<String, NVEntity> addControllerHandler;
	private NVConfigEntitySelectionWidget nvceSelectionWidget = null;
	//private boolean enableScrolling;
	
	public NVEntityArrayWidget(NVConfig nvc)
	{
		this(nvc, true);
	}
	
	public NVEntityArrayWidget(NVConfig nvc, boolean enableScrolling) 
	{
		super(nvc, enableScrolling);
		
		//this.enableScrolling = enableScrolling;
		
		if (getNVConfig().getMetaTypeBase() == NVEntity.class)
		{
			nvceSelectionWidget = new NVConfigEntitySelectionWidget();
			nvceSelectionWidget.addOkButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event) 
				{
					if (nvceSelectionWidget.getSelectedNVConfigEntity() != null)
					{
						createNVEntity(nvceSelectionWidget.getSelectedNVConfigEntity());
						nvceSelectionWidget.hidePopup();
					}
				}
		
			});
			nvceSelectionWidget.addSelectedNVConfigEntityDoubleClickHandler(new DoubleClickHandler()
			{
				@Override
				public void onDoubleClick(DoubleClickEvent event) 
				{
					if (nvceSelectionWidget.getSelectedNVConfigEntity() != null)
					{
						createNVEntity(nvceSelectionWidget.getSelectedNVConfigEntity());
						nvceSelectionWidget.hidePopup();
					}
				}
		
			});
			
			addAddButtonClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event) 
				{
					
					nvceSelectionWidget.showPopup();
				}
			});			
		}
		else
		{
			addAddButtonClickHandler(new ClickHandler()
			{
				
				@Override
				public void onClick(ClickEvent event) 
				{
					createNVEntity(getNVConfig());
				}
				
			});
		}

//		addCPBAddClickHandler(new ClickHandler()
//		{
//			@Override
//			public void onClick(ClickEvent event) 
//			{
//				HTTPCallConfigInterface hcc = HTTPCallConfig.createAndInit(null, FSConst.URIs.API_DATA_MANAGER.getValue(), HTTPMethod.POST);
//				hcc.setContentType(HTTPMimeType.APPLICATION_JSON);
//				hcc.getParameters().add(new NVPair(APIParameters.CLASS_NAME.getName(), getNVConfig().getMetaTypeBase().getName()));
//				
//				new GenericRequestHandler<NVEntity>(hcc, ReturnType.NVENTITY,
//						 new CallBackHandler<NVEntity>(new AsyncCallback<NVEntity>()
//				{
//					@Override
//					public void onFailure(Throwable caught) 
//					{
//						PopupUtil.SINGLETON.showPopup(FSStatusMessage.CREATE_FAILED.getName(), caught.getMessage());
//					}
//
//					@Override
//					public void onSuccess(NVEntity result) 
//					{
//						NVEntityWidget nveWidget = WidgetUtilFactory.createNVEntityWidget(result, true);
//						nveWidget.setValue(result);
//						nveWidget.setContentVisible(true);
//						nveWidget.setWidgetWidth("100%");
//						
//						addRow(nveWidget);
//					}
//					
//				}));
//				
//			}
//			
//		});
	}
	
	private void createNVEntity(NVConfig nvc)
	{
		if (addControllerHandler != null)
		{
			String className = null;
			
			if (getNVConfig().getMetaTypeBase() == NVEntity.class)
			{
				className = nvc.getMetaType().getName();
			}
			else
			{
				className = getNVConfig().getMetaTypeBase().getName();
			}
			
			addControllerHandler.actionAdd(className, new CallBackHandler<NVEntity>(new AsyncCallback<NVEntity>()
			{
				@Override
				public void onFailure(Throwable caught) 
				{
					PopupUtil.SINGLETON.showPopup("Creation Failed", caught.getMessage());
				}
				
				@Override
				public void onSuccess(NVEntity result) 
				{
					NVEntityWidget nveWidget = NVCWidgetFactory.DEFAULT.createNVEntityWidget(result, true);
					
					if (nveWidget != null)
					{
						nveWidget.setValue(result);
						nveWidget.setContentVisible(true);
						nveWidget.setWidgetWidth("100%");
						
						addRow(nveWidget);
					}
				}
				
			}));
		}
//		else
//		{
//			NVEntityWidget nveWidget = null;
//			
//			if (getNVConfig().getMetaTypeBase() == NVEntity.class)
//			{
//				nveWidget = (NVEntityWidget) NVCWidgetFactory.DEFAULT.createWidget(nvc, null, enableScrolling);
//			}
//			else
//			{
//				nveWidget = (NVEntityWidget) NVCWidgetFactory.DEFAULT.createWidget(nvc, null, enableScrolling);
//				//nveWidget = getNVConfig().getMetaTypeBase().getName();
//			}
//			
//			if (nveWidget != null)
//			{
//				nveWidget.setContentVisible(true);
//				nveWidget.setWidgetWidth("100%");
//				
//				addRow(nveWidget);
//			}
//		}
	}
	
	public void setNVCEs(NVConfigEntity[] nvces)
	{
		if (nvceSelectionWidget != null)
		{
			nvceSelectionWidget.setNVCEs(nvces);
		}
	}
	
	public void setAddControllerHandler(AddControllerHandler<String, NVEntity> addControllerHandler)
	{
		this.addControllerHandler = addControllerHandler;
	}
	
	@Override
	public void setWidgetValue(ArrayValues<NVEntity> value)
	{
		if (value != null)
		{
			removeAll();
			
			for (NVEntity nve : value.values())
			{
				NVEntityWidget nvew = NVCWidgetFactory.DEFAULT.createNVEntityWidget(nve, true);			
				nvew.setWidgetValue(nve);
				
				addRow(nvew);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayValues<NVEntity> getWidgetValue()
	{
		ArrayValues<NVEntity> ret = (ArrayValues<NVEntity>) SharedUtil.metaConfigToNVBase(getNVConfig());
		ExceptionCollection ec = new ExceptionCollection("Invalid value.");

		for (int i = 0; i < flexTable.getRowCount(); i++)
		{
			NVEntityWidget nvew = (NVEntityWidget) WidgetUtil.lookupWidget(flexTable, i, WIDGET_COLUMN);
			
			if (nvew != null)
			{
				try
				{
					ret.add(nvew.getValue());
				}
				catch (NullPointerException e)
				{
					e.printStackTrace();
					ec.getExceptionList().add(e);
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					ec.getExceptionList().add(e);
				}
			}
		}
		
		if (ec.getExceptionList().size() > 0)
		{
			throw ec;
		}

		return ret;
	}
	
}