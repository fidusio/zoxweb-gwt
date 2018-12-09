package org.zoxweb.client.widget.net;

import java.io.IOException;
import org.zoxweb.client.controller.ListBoxController;
import org.zoxweb.shared.net.InetProp.InetProto;
import org.zoxweb.shared.net.NIConfigDAO;
import org.zoxweb.shared.net.SharedNetUtil;
import org.zoxweb.shared.util.SetValue;
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;



import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;




public class NIWidget extends Composite
implements SetValue<NIConfigDAO>
{

	private static final String WIDTH = "10EM";
	
	private static final NIConfigDAO.Param PARAMS[] = {NIConfigDAO.Param.GATEWAY, NIConfigDAO.Param.NETMASK, NIConfigDAO.Param.ADDRESS};

	private static NIWidgetUiBinder uiBinder = GWT.create(NIWidgetUiBinder.class);
	@UiField ListBox lbNetwokInterface;
	@UiField ListBox lbInetProto;
	@UiField Grid gData;
	public @UiField Button bSave;
	public @UiField Button bCancel;
	@UiField Grid gInfo;
	//public @UiField CheckBox cbActivateSetting;
	public @UiField RadioButton rbNIActivate;
	public @UiField RadioButton rbNIDeactivate;
	public @UiField RadioButton rbNIConfigOnly;
	
	private ListBoxController<String> lbcNI = null;
	private ListBoxController<InetProto> lbcIntetProto = null;	

	interface NIWidgetUiBinder extends UiBinder<Widget, NIWidget> {
	}

	public NIWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		lbcNI = new ListBoxController<String>(lbNetwokInterface, "wan")
		{

			@Override
			public String toString(String value) {
				// TODO Auto-generated method stub
				return value;
			}
	
		};
		lbNetwokInterface.setName(NIConfigDAO.Param.NI_NAME.getName());
		lbcIntetProto = new ListBoxController<InetProto>(lbInetProto, InetProto.DHCP, InetProto.STATIC)
		{

			@Override
			public String toString(InetProto value) {
				// TODO Auto-generated method stub
				return value.getName();
			}

		};
		lbInetProto.setName(NIConfigDAO.Param.INET_PROTO.getName());
	}
	
	public NIWidget(String...interfaces)
	{
		this();
		lbcNI.setValues(interfaces);
	}
	
	public ListBoxController<String> getNetworkInterfaces()
	{
		return lbcNI;
	}

	public ListBoxController<InetProto> getNIProtocol()
	{
		return lbcIntetProto;
	}
	
	
	
	
	
	
	
	

	@UiHandler("lbInetProto")
	void onLbInetProtoChange(ChangeEvent event)
	{
		updatePropperties(lbcIntetProto.getValue(), null);
		
	}
	
	private void updatePropperties(InetProto proto, NIConfigDAO nicd)
	{
		final int HEADER = 2;
		int rows = gData.getRowCount();
		for (int i = HEADER; i < rows; i++)
		{
			gData.removeRow(HEADER);
		}
		switch(proto)
		{
		case DHCP:
			
			break;
		case LOOPBACK:
			break;
		case MANUAL:
			break;
		case NONE:
			break;
		case STATIC:
		
			for (NIConfigDAO.Param p : PARAMS)
			{
				gData.insertRow(HEADER);
				gData.setWidget(HEADER, 0, new Label(p.getNVConfig().getDisplayName()));
				TextBox tb = new TextBox();
				tb.setName(p.getName());
				tb.setWidth(WIDTH);
				if (nicd != null)
				{
					//GWT quirk for generic values
					String val = nicd.getProperties().getValue(p.getName());
					tb.setValue(val);
				}
				gData.setWidget(HEADER, 1,tb);
			}
			break;
		default:
			break;
		
		}
	}
	
	
//	@UiHandler("lbNIProtocol")
//	void onLbNIProtocolClick(ClickEvent event) 
//	{
//		System.out.println("clicked");
//	}
//	@UiHandler("bSave")
//	void onBSaveClick(ClickEvent event) 
//	{
//		
//		try
//		{
//			getValue();
//		}
//		catch(Exception e)
//		{
//			PopupUtil.SINGLETON.showPopup("CongigError", e.getMessage());
//		}
//	}
	
	
	

	@Override
	public NIConfigDAO getValue() {
		// TODO Auto-generated method stub
		NIConfigDAO nicd = new NIConfigDAO();
		nicd.setName(lbcNI.getValue());
		nicd.setNIName(lbcNI.getValue());
		nicd.setInteProtocol(lbcIntetProto.getValue());
		
		for(int i = 1; i < gData.getRowCount(); i++)
		{
			Widget w = gData.getWidget(i, 1);
			if (w instanceof TextBox)
			{
				TextBox tb = (TextBox)w;
				if (!SharedStringUtil.isEmpty(tb.getValue()))
				{
					try
					{
						SharedNetUtil.getV4Address(tb.getValue());
					}
					catch(Exception e)
					{
						tb.setFocus(true);
						throw new IllegalArgumentException(tb.getName() + " has invalid value " + tb.getValue());
						
					}
					
					nicd.getProperties().add(tb.getName(), tb.getValue());
				}
			}
		}
		
		try {
			if (!SharedNetUtil.validateNIConfig(nicd))
				throw new IllegalArgumentException("Invalid config");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			throw new IllegalArgumentException("Invalid config");
		}
		
		return nicd;
	}

	@Override
	public void setValue(NIConfigDAO nid) {
		// TODO Auto-generated method stub
		lbcNI.setValue(nid.getName());
		lbcIntetProto.setValue(nid.getInetProtocol());
		updatePropperties(nid.getInetProtocol(), nid);
	}
	
	public void setInfo(NIConfigDAO nicd)
	{
		
		// clear
		gInfo.resizeRows(0);
		
		// repopulate
		if (nicd != null)
		{
			gInfo.resize(3,2);
			int row = 0;
			gInfo.setWidget(row, 0, new Label("Interface:"));
			gInfo.setWidget(row++, 1, new Label(nicd.getNIName()));
			
			gInfo.setWidget(row, 0, new Label("Address:"));
			try {
				gInfo.setWidget(row++, 1, new Label(nicd.getAddressNetmask()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			gInfo.setWidget(row, 0, new Label("Gateway:"));
			gInfo.setWidget(row++, 1, new Label(nicd.getGateway()));
			
		}
		
	}

	
	
}
