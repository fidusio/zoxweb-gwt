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
package org.zoxweb.client.data;

import org.zoxweb.client.rpc.CallBackHandlerListener;
import org.zoxweb.shared.util.GetName;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HasHandlers;

/**
 * [Please state the purpose for this class or method because it will help the team for future maintenance ...].
 *
 */
public class ApplicationClientDAO
        implements CallBackHandlerListener, HasHandlers, GetName {

    public static final ApplicationClientDAO DEFAULT = new ApplicationClientDAO("zoxweb-core", null);

    protected HandlerManager handlerManager = null;
    protected String name;

    protected ApplicationClientDAO(String name, HandlerManager handlerManager) {
        this.name = name;

        if (handlerManager != null) {
            this.handlerManager = handlerManager;
        } else {
            this.handlerManager = new HandlerManager(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    public HandlerManager getHandlerManager() {
        return handlerManager;
    }

    @Override
    public void callBackInitiated() {
        // to be compliant
    }

    @Override
    public void callBackEnded() {
        // no impl
    }

    @Override
    public boolean callBackEndedWithException(Throwable caught) {
        return false;
    }

}