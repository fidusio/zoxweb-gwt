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
package org.zoxweb.client.data.events;

import com.google.gwt.event.shared.GwtEvent;
import org.zoxweb.shared.util.CRUDNVBase;

public class CRUDNVBaseEvent
        extends GwtEvent<CRUDNVBaseHandler> {

    public final static Type<CRUDNVBaseHandler> TYPE = new Type<CRUDNVBaseHandler>();

    private final CRUDNVBase crudNVBase;

    public CRUDNVBaseEvent(CRUDNVBase crudNVBase) {
        this.crudNVBase = crudNVBase;
    }

    /**
     * Returns the associated type.
     *
     * @return
     */
    @Override
    public Type<CRUDNVBaseHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Dispatches given handler.
     *
     * @param handler
     */
    @Override
    protected void dispatch(CRUDNVBaseHandler handler) {
        handler.applyCRUD(crudNVBase.getCRUD(), crudNVBase.getNVBase());
    }

}