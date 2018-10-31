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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NIConfigController 
{
  
  
  public enum Param
    implements GetName
  {
    URL,
    NI_READ,
    NI_INFO,
    NI_CONFIG,
    NI_CONFIG_ACTIVATE,
    TAG,
    ;

    public String getName()
    {
      return name().toLowerCase();
    }
  }
  
  private NIWidget niWidget;
  private NVGenericMap commands;
  
  public NIConfigController(NIWidget widget, NVGenericMap nvgm)
  {
    commands = nvgm;
    niWidget = widget;
    
    
    
    niWidget.getNetworkInterfaces().getListBox().addChangeHandler(new ChangeHandler() {
      
      @Override
      public void onChange(ChangeEvent event) {
          // TODO Auto-generated method stub
          readNIConfigData();
      }
  });     
 
  
  niWidget.bCancel.addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
          readNIConfigData();
          
      }
  });
  
  
  
  niWidget.bSave.addClickHandler(new ClickHandler(){

      @Override
      public void onClick(ClickEvent event) {
          // TODO Auto-generated method stub
          try
          {
              NIConfigDAO nicd = niWidget.getValue();
              updateNIConfigData(nicd);
          }
          catch(Exception e)
          {
              PopupUtil.SINGLETON.showPopup("ConfigError", e.getMessage());
          }
          
      }
  });
  
  
  niWidget.bCancel.addClickHandler(new ClickHandler(){

      @Override
      public void onClick(ClickEvent event) {
          // TODO Auto-generated method stub
          
              readNIConfigData();
          
          
      }
      
  });
  }
  
  
  
  public void readNIConfigData()
  {
      HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(
              commands.getValue(Param.URL),
              SharedStringUtil.embedText(commands.getValue(Param.NI_INFO), (String)commands.getValue(Param.TAG), niWidget.getNetworkInterfaces().getValue()), 
              HTTPMethod.GET
              );
      
     
      
      new GenericRequestHandler<NIConfigDAO>(hcc, ReturnType.NVENTITY, new AsyncCallback<NIConfigDAO>(){

         
          @Override
          public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
              caught.printStackTrace();
          }

          @Override
          public void onSuccess(NIConfigDAO result) {
              // TODO Auto-generated method stub
              //System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
              
              niWidget.setValue(result);
              readNIInfo();
          }});
  }
  
  public void readNIInfo()
  {
      HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(
              commands.getValue(Param.URL), 
              SharedStringUtil.embedText(commands.getValue(Param.NI_CONFIG), (String)commands.getValue(Param.TAG), niWidget.getNetworkInterfaces().getValue()), 
              HTTPMethod.GET
              );
      
     
      
      new GenericRequestHandler<NIConfigDAO>(hcc, ReturnType.NVENTITY, new AsyncCallback<NIConfigDAO>(){

          @Override
          public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
              //caught.printStackTrace();
              niWidget.setInfo(null);
          }

          @Override
          public void onSuccess(NIConfigDAO result) {
              // TODO Auto-generated method stub
              //System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
              
              niWidget.setInfo(result);
          }});
  }
  
  public  void updateNIConfigData(NIConfigDAO nicd)
  {
    
    
      String uri = niWidget.cbActivateSetting.getValue() ? commands.getValue(Param.NI_CONFIG_ACTIVATE) : commands.getValue(Param.NI_CONFIG);
      HTTPMessageConfigInterface hcc = HTTPMessageConfig.createAndInit(
          commands.getValue(Param.URL),
          uri,
              HTTPMethod.POST
              );
      
      //hcc.setContentType(HTTPMimeType.APPLICATION_JSON);
      
      String json = JSONClientUtil.toString(nicd);
      
      
      hcc.setContent(json);
      
      new GenericRequestHandler<String>(hcc, ReturnType.STRING, new AsyncCallback<String>(){

         
          
          @Override
          public void onFailure(Throwable caught) {
              // TODO Auto-generated method stub
              caught.printStackTrace();
          }

          @Override
          public void onSuccess(String result) {
              // TODO Auto-generated method stub
              //System.out.println(SharedUtil.toCanonicalID(',', result.getNIName(), result.getInetProtocol(), result.getAddress(), result.getNetmask()));
              System.out.println(result);
              readNIInfo();
          }});
  }
  
}
