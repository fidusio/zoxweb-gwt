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

/**
 * Extended WebSocket listener with error handling and detailed close events.
 */
public interface ZWWebSocketListenerExt
    extends ZWWebSocketListener
{
    /**
     * Called when an error occurs on the WebSocket connection.
     */
    void onError();

    /**
     * Called when the WebSocket connection is closed with detailed information.
     * This method is called instead of onClose() for listeners implementing this interface.
     *
     * @param event the close event containing code, reason, and wasClean flag
     */
    void onClose(ZWCloseEvent event);
}
