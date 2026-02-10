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
 * This interface declares three methods used for Websocket which include:
 * opening the socket, listening for a message, and then closing the socket.
 */
public interface ZWWebSocketListener {

    /**
     *
     */
    void onClose();

    /**
     *
     * @param msg
     */
    void onMessage(String msg);

    /**
     *
     */
    void onOpen();

}