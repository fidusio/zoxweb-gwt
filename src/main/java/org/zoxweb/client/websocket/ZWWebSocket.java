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

import java.util.ArrayList;
import java.util.List;

import org.zoxweb.shared.http.HTTPMessageConfigInterface;
import org.zoxweb.shared.util.SUS;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedStringUtil;

import com.google.gwt.core.client.GWT;

/**
 * WebSocket client implementation for GWT.
 * Provides full-duplex communication channels over a single TCP connection.
 */
public class ZWWebSocket
{
    /**
     * WebSocket ready state constants.
     */
    public static final int CONNECTING = 0;
    public static final int OPEN = 1;
    public static final int CLOSING = 2;
    public static final int CLOSED = 3;

    private static long counter = 1;

    /**
     * Check if WebSocket is supported in the current browser.
     * @return true if WebSocket is supported
     */
    public static boolean isSupported()
    {
        return _isWebsocket();
    }

    private static native boolean _isWebsocket() /*-{
        return ("WebSocket" in $wnd);
    }-*/;

    private final List<ZWWebSocketListener> listeners = new ArrayList<>();
    private final String varName;
    private final String url;

    /**
     * Create a WebSocket from an HTTP message configuration.
     * @param hcc the HTTP message configuration
     * @throws NullPointerException if hcc is null
     */
    public ZWWebSocket(HTTPMessageConfigInterface hcc)
    {
        this(formatFullURL(hcc));
    }

    /**
     * Create a WebSocket with the specified URL.
     * @param url the WebSocket URL (must start with ws:// or wss://)
     * @throws IllegalArgumentException if URL is invalid
     */
    public ZWWebSocket(String url)
    {
        if (SUS.isEmpty(url))
        {
            throw new IllegalArgumentException("WebSocket URL cannot be null or empty");
        }

        String lowerUrl = url.toLowerCase();
        if (!lowerUrl.startsWith("ws://") && !lowerUrl.startsWith("wss://"))
        {
            throw new IllegalArgumentException("WebSocket URL must start with ws:// or wss://: " + url);
        }

        this.url = url;
        this.varName = "gwtws-" + counter++;
    }

    /**
     * Format a full WebSocket URL from HTTP configuration.
     * Converts http:// to ws:// and https:// to wss://
     */
    private static String formatFullURL(HTTPMessageConfigInterface hcc)
    {
        SUS.checkIfNulls("HTTPMessageConfigInterface cannot be null", hcc);

        if (SUS.isEmpty(hcc.getURL()))
        {
            String moduleUrl = GWT.getModuleBaseURL();

            if (moduleUrl.toLowerCase().startsWith("https"))
            {
                // https -> wss
                hcc.setURL("wss" + moduleUrl.substring("https".length()));
            }
            else if (moduleUrl.toLowerCase().startsWith("http"))
            {
                // http -> ws
                hcc.setURL("ws" + moduleUrl.substring("http".length()));
            }
        }

        return SharedStringUtil.concat(hcc.getURL(), hcc.getURI(), "/");
    }

    /**
     * Add a listener for WebSocket events.
     * @param listener the listener to add
     */
    public void addListener(ZWWebSocketListener listener)
    {
        if (listener != null && !listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener.
     * @param listener the listener to remove
     * @return true if the listener was removed
     */
    public boolean removeListener(ZWWebSocketListener listener)
    {
        return listeners.remove(listener);
    }

    /**
     * Get the number of registered listeners.
     * @return listener count
     */
    public int getListenerCount()
    {
        return listeners.size();
    }

    /**
     * Open the WebSocket connection.
     * @throws IllegalStateException if WebSocket is not supported
     */
    public void open()
    {
        if (!isSupported())
        {
            throw new IllegalStateException("WebSocket is not supported in this browser");
        }
        _open(this, varName, url);
    }

    /**
     * Close the WebSocket connection.
     */
    public void close()
    {
        if (getState() == OPEN || getState() == CONNECTING)
        {
            _close(varName);
        }
    }

    /**
     * Close a WebSocket safely, ignoring any errors.
     * @param socket the socket to close (may be null)
     */
    public static void close(ZWWebSocket socket)
    {
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (Exception e)
            {
                // Silently ignore close errors
                GWT.log("Error closing WebSocket: " + e.getMessage());
            }
        }
    }

    /**
     * Get the current WebSocket ready state.
     * @return one of CONNECTING, OPEN, CLOSING, or CLOSED
     */
    public int getState()
    {
        try
        {
            return _state(varName);
        }
        catch (Exception e)
        {
            return CLOSED;
        }
    }

    /**
     * Check if the WebSocket is currently open.
     * @return true if state is OPEN
     */
    public boolean isOpen()
    {
        return getState() == OPEN;
    }

    /**
     * Check if the WebSocket is currently connecting.
     * @return true if state is CONNECTING
     */
    public boolean isConnecting()
    {
        return getState() == CONNECTING;
    }

    /**
     * Send a text message.
     * @param msg the message to send
     * @throws IllegalStateException if WebSocket is not open
     */
    public void send(String msg)
    {
        if (getState() != OPEN)
        {
            throw new IllegalStateException("WebSocket is not open. Current state: " + getState());
        }
        _send(varName, msg);
    }

    /**
     * Send binary data.
     * @param bytes the byte array to send
     */
    public void send(byte[] bytes)
    {
        if (bytes != null)
        {
            send(bytes, 0, bytes.length);
        }
    }

    /**
     * Send a portion of binary data.
     * @param bytes the byte array
     * @param index starting index
     * @param length number of bytes to send
     */
    public void send(byte[] bytes, int index, int length)
    {
        if (bytes != null)
        {
            send(SharedStringUtil.toString(SharedBase64.encode(bytes, index, length)));
        }
    }

    /**
     * Get the WebSocket URL.
     * @return the URL
     */
    public String getUrl()
    {
        return url;
    }

    // Native JSNI methods

    private native void _open(ZWWebSocket ws, String s, String url) /*-{
        $wnd[s] = new WebSocket(url);
        $wnd[s].onopen = $entry(function() {
            ws.@org.zoxweb.client.websocket.ZWWebSocket::onOpen()();
        });
        $wnd[s].onclose = $entry(function(event) {
            ws.@org.zoxweb.client.websocket.ZWWebSocket::onClose(SLjava/lang/String;Z)(event.code, event.reason, event.wasClean);
        });
        $wnd[s].onerror = $entry(function(event) {
            ws.@org.zoxweb.client.websocket.ZWWebSocket::onError()();
        });
        $wnd[s].onmessage = $entry(function(msg) {
            ws.@org.zoxweb.client.websocket.ZWWebSocket::onMessage(Ljava/lang/String;)(msg.data);
        });
    }-*/;

    private native void _close(String s) /*-{
        if ($wnd[s]) {
            $wnd[s].close();
        }
    }-*/;

    private native void _send(String s, String msg) /*-{
        $wnd[s].send(msg);
    }-*/;

    private native int _state(String s) /*-{
        if ($wnd[s]) {
            return $wnd[s].readyState;
        }
        return 3; // CLOSED
    }-*/;

    // Event handlers called from JSNI

    protected void onOpen()
    {
        List<ZWWebSocketListener> copy = new ArrayList<>(listeners);
        for (ZWWebSocketListener listener : copy)
        {
            try
            {
                listener.onOpen();
            }
            catch (Exception e)
            {
                GWT.log("Error in onOpen listener: " + e.getMessage());
            }
        }
    }

    protected void onClose(short code, String reason, boolean wasClean)
    {
        ZWCloseEvent closeEvent = new ZWCloseEvent(code, reason, wasClean);
        List<ZWWebSocketListener> copy = new ArrayList<>(listeners);
        for (ZWWebSocketListener listener : copy)
        {
            try
            {
                if (listener instanceof ZWWebSocketListenerExt)
                {
                    ((ZWWebSocketListenerExt) listener).onClose(closeEvent);
                }
                else
                {
                    listener.onClose();
                }
            }
            catch (Exception e)
            {
                GWT.log("Error in onClose listener: " + e.getMessage());
            }
        }
    }

    protected void onError()
    {
        List<ZWWebSocketListener> copy = new ArrayList<>(listeners);
        for (ZWWebSocketListener listener : copy)
        {
            try
            {
                if (listener instanceof ZWWebSocketListenerExt)
                {
                    ((ZWWebSocketListenerExt) listener).onError();
                }
            }
            catch (Exception e)
            {
                GWT.log("Error in onError listener: " + e.getMessage());
            }
        }
    }

    protected void onMessage(String msg)
    {
        List<ZWWebSocketListener> copy = new ArrayList<>(listeners);
        for (ZWWebSocketListener listener : copy)
        {
            try
            {
                if (listener instanceof ZWBinaryWebSocketListener)
                {
                    byte[] bytes = SharedBase64.decode(msg != null ? msg.getBytes() : null);
                    ((ZWBinaryWebSocketListener) listener).onMessage(bytes);
                }
                else
                {
                    listener.onMessage(msg);
                }
            }
            catch (Exception e)
            {
                GWT.log("Error in onMessage listener: " + e.getMessage());
            }
        }
    }
}
