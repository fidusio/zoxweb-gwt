package org.zoxweb.client.widget;

import org.zoxweb.client.data.ApplicationClientDAO;
import org.zoxweb.client.data.events.CRUDNVEntityEvent;
import org.zoxweb.client.data.events.SaveControllerHandler;
import org.zoxweb.client.rpc.CallBackHandler;
import org.zoxweb.shared.data.CRUDNVEntityDAO;
import org.zoxweb.shared.data.events.ClearActionListener;
import org.zoxweb.shared.data.events.EditActionListener;
import org.zoxweb.shared.data.events.PreviewActionListener;
import org.zoxweb.shared.data.events.SaveActionListener;
import org.zoxweb.shared.util.CRUD;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

@SuppressWarnings("serial")
public abstract class NVEntityIntermediateWidget 
	extends NVEntityWidget
	implements SaveActionListener<AutoCloseable>, ClearActionListener<AutoCloseable>, PreviewActionListener<AutoCloseable>, EditActionListener<AutoCloseable>
{
	
	private AutoCloseable autoCloseableSave = null;
	private SaveControllerHandler<NVEntityWidget, NVEntity> saveControllerHandler;
	private PopupWarningMessageWidget popupWarningMessage = new PopupWarningMessageWidget();
	
	public NVEntityIntermediateWidget()
	{
		popupWarningMessage.addOkButtonClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				clear();
				popupWarningMessage.hidePopup();
			}
	
		});
	}
	
	public SaveControllerHandler<NVEntityWidget, NVEntity> getSaveControllerHandler() 
	{
		return saveControllerHandler;
	}
	
	public void setSaveControllerHandler(SaveControllerHandler<NVEntityWidget, NVEntity> saveControllerHandler) 
	{
		this.saveControllerHandler = saveControllerHandler;
	}

	@Override
	public void actionSave(AutoCloseable autoCloseable)
	{
		this.autoCloseableSave = autoCloseable;
		
		if (saveControllerHandler != null)
		{
			saveControllerHandler.actionSave(this, new CallBackHandler<NVEntity>(new AsyncCallback<NVEntity>()
			{
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
						ApplicationClientDAO.DEFAULT.fireEvent(new CRUDNVEntityEvent(new CRUDNVEntityDAO(CRUD.UPDATE, result)));
						
						autoCloseSave();
					}
				}

				
			}));
		}
	}
	
	@Override
	public void actionClear(AutoCloseable autoCloseable)
	{
		popupWarningMessage.showPopup("Clear Confirmation", "Are you sure you want you clear the content of this form?");
	}

	@Override
	public void actionEdit(AutoCloseable autoCloseable) 
	{
		setReadOnly(false);
	}

	@Override
	public void actionPreview(AutoCloseable autoCloseable) 
	{
		setReadOnly(true);
	}
	
	public void autoCloseSave()
	{
		if (autoCloseableSave != null)
		{
			SharedUtil.close(autoCloseableSave);
		}
	}
	
}