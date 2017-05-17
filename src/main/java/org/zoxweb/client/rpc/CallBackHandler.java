/*
 * Copyright (c) 2012-2017 ZoxWeb.com LLC.
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
package org.zoxweb.client.rpc;

import org.zoxweb.shared.data.events.CallbackListener;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class CallBackHandler<T>
	implements AsyncCallback<T>, CallbackListener<Throwable, T>
{

	protected AsyncCallback<T> callback;
	protected CallBackHandlerListener listener;

	public CallBackHandler(AsyncCallback<T> callback)
	{
		this.callback = callback;
	}
	
	public CallBackHandler(CallBackHandlerListener listener, AsyncCallback<T> callback)
	{
		this.callback = callback;
		this.listener = listener;
		
		if (listener != null)
		{
			listener.callBackInitiated();
		}
	}
	
	@Override
	public void onFailure(Throwable caught)
	{
		boolean notify = true;
		
		// this hook is available to process exceptions in stan
		if (listener != null)
		{
			notify = listener.callBackEndedWithException( caught);
		}
		
//		else if ( caught instanceof AccessException)
//		{
//			AccessException ae = (AccessException) caught;
//			if ( ae.getURLRedirect() != null)
//			{
//				Window.Location.replace( ae.getURLRedirect());
//				return;
//			}
//			else if ( ae.isReloadRequired())
//			{
//				Window.Location.reload();
//				return;
//			}
//		}
		
		if (notify)
		{
			callback.onFailure(caught);
		}
	}

	@Override
	public void onSuccess(T result)
	{
		if (listener != null)
		{
			listener.callBackEnded();
		}
		
		callback.onSuccess(result);
	}

}