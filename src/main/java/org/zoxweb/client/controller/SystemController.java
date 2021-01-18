package org.zoxweb.client.controller;

import org.zoxweb.client.data.crypto.CryptoClient;
import org.zoxweb.client.rpc.GenericRequestHandler;
import org.zoxweb.client.widget.net.SystemWidget;
import org.zoxweb.shared.data.SimpleDocumentDAO;
import org.zoxweb.shared.http.HTTPMessageConfig;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.http.HTTPMethod;
import org.zoxweb.shared.util.GetName;
import org.zoxweb.shared.util.NVGenericMap;

import org.zoxweb.shared.util.Const.ReturnType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SystemController
extends ControllerBase<SystemWidget>
{
	public enum Param
	implements GetName
	{
		REBOOT,
		SHUTDOWN,
		;

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name().toLowerCase();
		}
	}
	
	public SystemController(String url, SystemWidget widget, NVGenericMap config)
	{
		super(widget, config);
		setup();
	}
	
	protected void setup()
	{
		widget.bSend.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
				if (widget.rbReboot.getValue())
				{
					command(Param.REBOOT);
				}
				else if(widget.rbShutdown.getValue())
				{
					command(Param.SHUTDOWN);
				}
			}
		} );
	}
	
	private void command(Param command)
	{	
		HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(CryptoClient.getAuthToken().getURL(),
                						 (String)config.getValue((GetName)command),
                						 HTTPMethod.POST);



		new GenericRequestHandler<SimpleDocumentDAO>(hcc, ReturnType.NVENTITY, new AsyncCallback<SimpleDocumentDAO>(){
		
		
		@Override
		public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub
		
		
		}
		
		@Override
		public void onSuccess(SimpleDocumentDAO result) {
		// TODO Auto-generated method stub
		//System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
		
		
		}});
		}
	
}
