package org.zoxweb.client.controller;


import org.zoxweb.client.data.JSONClientUtil;
import org.zoxweb.client.rpc.GenericRequestHandler;
import org.zoxweb.client.widget.PopupUtil;
import org.zoxweb.client.widget.net.NIWidget;
import org.zoxweb.shared.http.HTTPMessageConfig;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.http.HTTPMethod;
import org.zoxweb.shared.net.NIConfigDAO;
import org.zoxweb.shared.util.Const.ReturnType;
import org.zoxweb.shared.util.GetName;
import org.zoxweb.shared.util.NVGenericMap;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.UpdateOnSelect;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RadioButton;

public class NIConfigController
extends ControllerBase<NIWidget>
implements UpdateOnSelect
{
  
  
  public enum Param
    implements GetName
  {
    TAG,
    //URL,
    NI_CONFIG_ACTIVATE,
    NI_CONFIG_DEACTIVATE,
    NI_CONFIG_SET,
    NI_CONFIG,
    NI_CONFIG_READ,
    NI_INFO_READ,
    ;

    public String getName()
    {
      return name().toLowerCase();
    }
  }
  
  
  private RadioButtonController radioButtonController;
  
  public NIConfigController(String url, NIWidget niWidget, NVGenericMap nvgm)
  {
	super(url, niWidget, nvgm);
	
    
	setup();
  
  }
  
  
  protected void setup()
  {
	    widget.getNetworkInterfaces().getListBox().addChangeHandler(new ChangeHandler() {
	        
	        @Override
	        public void onChange(ChangeEvent event) {
	            // TODO Auto-generated method stub
	            readNIConfigData();
	        }
	      });     
	   
	    
	      widget.bCancel.addClickHandler(new ClickHandler() {
	          
	          @Override
	          public void onClick(ClickEvent event) {
	              readNIConfigData();
	              
	          }
	      });
	    
	    
	    
	      widget.bSave.addClickHandler(new ClickHandler(){
	    
	          @Override
	          public void onClick(ClickEvent event) {
	              // TODO Auto-generated method stub
	              try
	              {
	                  NIConfigDAO nicd = widget.getValue();
	                  updateNIConfigData(nicd);
	              }
	              catch(Exception e)
	              {
	                  PopupUtil.SINGLETON.showPopup("ConfigError", e.getMessage());
	              }
	              
	          }
	      });
	    
	    
	      widget.bCancel.addClickHandler(new ClickHandler(){
	    
	          @Override
	          public void onClick(ClickEvent event) {
	              // TODO Auto-generated method stub
	              
	                  readNIConfigData();
	              
	              
	          }
	          
	      });
	      
	      radioButtonController = new RadioButtonController(widget.rbNIActivate, widget.rbNIDeactivate, widget.rbNIConfigOnly);
	      
	      readNIConfigData();
  }
  
  public void readNIConfigData()
  {
      HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(url,
                                      SharedStringUtil.embedText((String)config.getValue((GetName)Param.NI_CONFIG_READ), (String)config.getValue((GetName)Param.TAG), widget.getNetworkInterfaces().getValue()),
                                      HTTPMethod.GET);
      
     
      controlsEnabled(false);
      new GenericRequestHandler<NIConfigDAO>(hcc, ReturnType.NVENTITY, new AsyncCallback<NIConfigDAO>(){

         
          @Override
          public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
              caught.printStackTrace();
              controlsEnabled(true);
          }

          @Override
          public void onSuccess(NIConfigDAO result) {
              // TODO Auto-generated method stub
              //System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
              
              widget.setValue(result);
              controlsEnabled(true);
              readNIInfo();
          }});
  }
  
  public void readNIInfo()
  {
      HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(url, 
                                       SharedStringUtil.embedText((String)config.getValue((GetName)Param.NI_INFO_READ), (String)config.getValue((GetName)Param.TAG), widget.getNetworkInterfaces().getValue()),
                                       HTTPMethod.GET);
      
     
      controlsEnabled(false);
      new GenericRequestHandler<NIConfigDAO>(hcc, ReturnType.NVENTITY, new AsyncCallback<NIConfigDAO>(){

          @Override
          public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
              //caught.printStackTrace();
              
        	  widget.setInfo(null);
        	  controlsEnabled(true);
          }

          @Override
          public void onSuccess(NIConfigDAO result) {
              // TODO Auto-generated method stub
              //System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
              
        	  controlsEnabled(true);
              widget.setInfo(result);
          }});
  }
  
  public  void updateNIConfigData(NIConfigDAO nicd)
  {
	  Param command = null;
	  // get the radio button value
	  // convert it to activate deactivate or config
	  RadioButton rb = radioButtonController.getValue();
	  if (rb == widget.rbNIActivate)
	  {
		  command = Param.NI_CONFIG_ACTIVATE;
	  }
	  else if (rb == widget.rbNIDeactivate)
	  {
		  command = Param.NI_CONFIG_DEACTIVATE;
	  }
	  else if (rb == widget.rbNIConfigOnly)
	  {
		  command = Param.NI_CONFIG_SET;
	  }
	  
	  System.out.println("command:" + command);
	  
      HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(url,
    		  						   (String)config.getValue((GetName)command),
                                       HTTPMethod.POST);
      
      //hcc.setContentType(HTTPMimeType.APPLICATION_JSON);
      
      String json = JSONClientUtil.toString(nicd);
      
      
      hcc.setContent(json);
      controlsEnabled(false);
      new GenericRequestHandler<NIConfigDAO>(hcc, ReturnType.NVENTITY, new AsyncCallback<NIConfigDAO>(){

         
          
          @Override
          public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
              caught.printStackTrace();
              controlsEnabled(true);
          }

          @Override
          public void onSuccess(NIConfigDAO result) {
              // TODO Auto-generated method stub
              //System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
        	  controlsEnabled(true);
              readNIInfo();
          }});
  }
  
  private void controlsEnabled(boolean stat)
  {
	  
	  
	  widget.bSave.setEnabled(stat);
	  widget.bCancel.setEnabled(stat);
	  
  }


	@Override
	public void updateOnSelect() {
		// TODO Auto-generated method stub
		readNIConfigData();
		
	}
  
}
