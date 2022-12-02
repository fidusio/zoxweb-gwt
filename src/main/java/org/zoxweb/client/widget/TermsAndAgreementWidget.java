/*
 * Copyright (c) 2012-2015 ZoxWeb.com LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zoxweb.client.widget;

import org.zoxweb.client.data.JSONClientUtil;
import org.zoxweb.client.rpc.HTTPWebRequest;
import org.zoxweb.shared.data.AgreementDAO;
import org.zoxweb.shared.data.ZWDataFactory;
import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.http.HTTPHeader;
import org.zoxweb.shared.http.HTTPHeaderValue;
import org.zoxweb.shared.http.HTTPStatusCode;
import org.zoxweb.shared.util.QuickLZ;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * 
 * @author mzebib
 *
 */
public class TermsAndAgreementWidget 
	extends Composite 
{

	private static TermsAndAgreementWidgetUiBinder uiBinder = GWT
			.create(TermsAndAgreementWidgetUiBinder.class);
	
	@UiField CheckBox cbAgreement;
	@UiField Hyperlink hrefName;
	@UiField TextArea taContent;
	
	private HTTPMessageConfigInterface httpCallConfig = null;

	/**
	 * 
	 * @author mzebib
	 *
	 */
	interface TermsAndAgreementWidgetUiBinder 
		extends UiBinder<Widget, TermsAndAgreementWidget> 
	{
	}

	/**
	 * This is the default constructor.
	 */
	public TermsAndAgreementWidget() 
	{
		initWidget(uiBinder.createAndBindUi(this));

		taContent.setReadOnly(true);
		taContent.setVisible(false);
	
	}
	
	
	/**
	 * This method returns the agreement title.
	 * @return the agreement title.
	 */
	public String getAgreementTitle()
	{
		return hrefName.getText();
	}
	
	/**
	 * This method sets the agreement title.
	 * @param title
	 */
	public void setAgreementTitle(String title)
	{
		if (title != null)
		{
			hrefName.setText(title);
		}
	}
	
	/**
	 * This method returns the agreement content.
	 * @return the agreement content.
	 */
	public String getAgreementContent()
	{
		return taContent.getText();
	}
	
	/**
	 * This method sets the agreement content.
	 * @param content
	 */
	public void setAgreementContent(String content)
	{
		if (content != null)
		{
			taContent.setText(content);
		}
	}
	
	/**
	 * This method returns the check box title.
	 * @return the check box title.
	 */
	public String getCheckBoxTitle()
	{
		return cbAgreement.getText();
	}
	
	/**
	 * This method sets the check box title.
	 * @param title
	 */
	public void setCheckBoxTitle(String title)
	{
		cbAgreement.setText(title);
	}
	
	
	/**
	 * This method handles click events on hyperlink.
	 * @param event
	 */
	@UiHandler("hrefName")
	void onHrefNameClick(ClickEvent event) 
	{
		taContent.setVisible(!taContent.isVisible());
		
		retrieveAgreementByHTTP();
		
		if (taContent.isVisible())
		{
			this.setHeight("10EM");
		}
		else
		{
			this.setHeight("2EM");
		}
	}
	
	public void retrieveAgreementByHTTP()
	{
		if (httpCallConfig != null && getAgreementContent().length() == 0)
		{
			HTTPWebRequest webRequest = new HTTPWebRequest(httpCallConfig);
			
			try 
			{
				webRequest.send(new RequestCallback()
				{

					@Override
					public void onResponseReceived(Request request, Response response)
					{
						HTTPStatusCode code = HTTPStatusCode.statusByCode(response.getStatusCode());
						
						if (code == HTTPStatusCode.OK)
						{
							String contentEncoding = response.getHeader(HTTPHeader.CONTENT_ENCODING.getName());
							String json = null;
							
							if (SharedStringUtil.contains(contentEncoding, HTTPHeaderValue.CONTENT_ENCODING_LZ, true))
							{
								byte[] data = SharedBase64.decode(SharedStringUtil.getBytes(response.getText()));
								byte[] unzipped = QuickLZ.decompress(data);
								json = SharedStringUtil.toString(unzipped);								
							}
							else
							{
								json = response.getText();
							}
							
							AgreementDAO result = JSONClientUtil.fromJSON(null, json, ZWDataFactory.SINGLETON);
							
							if (result != null)
							{
								setAgreementTitle(result.getAgreementTitle());
								setAgreementContent(result.getAgreementContent());
							}
						}
					}

					@Override
					public void onError(Request request, Throwable exception)
					{
						
					}
					
				});
			}
			catch (Exception e)
			{
				
			}
		}
	}
	
	/**
	 * This method returns the agreement check box value.
	 * @return true if checked
	 */
	public boolean isChecked()
	{
		return cbAgreement.getValue();
	}
	
	/**
	 * This method returns the agreement check box.
	 * @return the agreement check box.
	 */
	public CheckBox getAgreementCheckBox()
	{
		return cbAgreement;
	}
	
	/**
	 * This method returns the agreement hyperlink.
	 * @return the agreement hyperlink.
	 */
	public Hyperlink getHrefName()
	{
		return hrefName;
	}
	
	public void setHTTPMessageConfig(HTTPMessageConfigInterface httpCallConfig)
	{
		this.httpCallConfig = httpCallConfig;
	}
	
}