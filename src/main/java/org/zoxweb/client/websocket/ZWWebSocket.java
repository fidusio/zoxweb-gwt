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
package org.zoxweb.client.websocket;

import java.util.HashSet;
import java.util.Set;

import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.core.client.GWT;

/**
 * WebSocket is a protocol providing full-duplex communications channels over a single TCP connection.
 * It is used for client and server communication.
 */
public class ZWWebSocket
{

    private static long counter = 1;

    private static native boolean _isWebsocket() /*-{
                                    return ("WebSocket" in window);
									}-*/;

    public static boolean isSupported()
    {
        return _isWebsocket();
    }

    private final Set<ZWWebSocketListener> listeners = new HashSet<>();

    private final String varName;
    private final String url;

    public ZWWebSocket(HTTPMessageConfigInterface hcc) {
    	this(formatFullURL(hcc));
    }

    private static String formatFullURL(HTTPMessageConfigInterface hcc)
    {
    	if (SharedStringUtil.isEmpty(hcc.getURL()))
    	{
    		String url = GWT.getModuleBaseURL().toLowerCase();

    		if (url.startsWith("http"))
    		{
    			// replace http or https with ws or ws
    			url = "ws" + GWT.getModuleBaseURL().substring("http".length());
    			hcc.setURL(url);
    		}
    	}

    	return  SharedStringUtil.concat(hcc.getURL(), hcc.getURI(), "/");
    	
    }
    
    public ZWWebSocket(String url)
    {
        this.url = url;
        this.varName = "gwtws-" + counter++;
    }

    private native void _close(String s) 
    /*-{
			$wnd[s].close();
	}-*/;

    private native void _open(ZWWebSocket ws, String s, String url) 
    /*-{
         $wnd[s] = new WebSocket(url);
		 $wnd[s].onopen = function() { ws.@org.zoxweb.client.websocket.ZWWebSocket::onOpen()(); };
		 $wnd[s].onclose = function() { ws.@org.zoxweb.client.websocket.ZWWebSocket::onClose()(); };
		 $wnd[s].onerror = function() { ws.@org.zoxweb.client.websocket.ZWWebSocket::onError()(); };
		 $wnd[s].onmessage = function(msg) { ws.@org.zoxweb.client.websocket.ZWWebSocket::onMessage(Ljava/lang/String;)(msg.data); }
	 }-*/;

    private native void _send(String s, String msg)
    /*-{
    	$wnd[s].send(msg);
	}-*/;

    private native int _state(String s)
    /*-{
         return $wnd[s].readyState;
	}-*/;

    /**
     *
     * @param listener
     */
    public void addListener(ZWWebSocketListener listener)
    {
        listeners.add(listener);
    }

    /**
     *
     */
    public void close()
    {
        _close(varName);
    }

    /**
     *
     * @return
     */
    public int getState()
    {
        return _state(varName);
    }

    /**
     *
     */
    protected void onClose()
    {
        for (ZWWebSocketListener listener : listeners)
        {
            listener.onClose();
        }
    }

    /**
     *
     */
    protected void onError()
    {
        for (ZWWebSocketListener listener : listeners)
        {
        	if (listener instanceof ZWWebSocketListenerExt)
        	{
        		((ZWWebSocketListenerExt)listener).onError();
        	}
        }
    }

    /**
     *
     * @param msg
     */
    protected void onMessage(String msg)
    {
        for (ZWWebSocketListener listener : listeners)
        {
            if (listener instanceof ZWBinaryWebSocketListener)
            {
                byte[] bytes =  SharedBase64.decode( msg!= null ? msg.getBytes() : null);//Base64Utils.fromBase64(msg);
                ((ZWBinaryWebSocketListener) listener).onMessage(bytes);
            }
            else
                {
            	listener.onMessage(msg);
            }
        }
    }

    /**
     *
     */
    protected void onOpen()
    {
        for (ZWWebSocketListener listener : listeners)
        {
            listener.onOpen();
        }
    }

    /**
     *
     */
    public void open()
    {
        _open(this, varName, url);
    }

    /**
     *
     * @param msg
     */
    public void send(String msg)
    {
        _send(varName, msg);
    }

    /**
     *
     * @param bytes
     */
    public void send(byte[] bytes)
    {
    	send(bytes, 0, bytes.length);
    }

    /**
     *
     * @param bytes
     * @param index
     * @param length
     */
    public void send(byte[] bytes, int index, int length)
    {
        send(SharedStringUtil.toString(SharedBase64.encode(bytes, index, length)));
    }

    /**
     *
     * @param socket
     */
    public static void close (ZWWebSocket socket)
    {
    	if (socket != null)
    	{
    		try
            {
    			socket.close();
    		}
    		catch (Exception e )
            {
    			e.printStackTrace();
    		}
    	}
    }

}