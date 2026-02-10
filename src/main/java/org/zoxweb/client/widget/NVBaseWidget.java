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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.Widget;
import org.zoxweb.shared.util.*;

/**
 *
 * @author mzebib
 *
 * @param <V>
 */
@SuppressWarnings("serial")
public abstract class NVBaseWidget<V>
        extends Composite
        implements GetNVConfig, SetValue<V>,
        //CRUDOperations,
        HasName, SetMandatory {
    protected NVConfig nvConfig;
    protected NVConfigNameMap nvConfigNameMap;

    @SuppressWarnings("rawtypes")
    protected NVTextWidgetController textWidgetController;
    //protected CRUDManager crudManager;
    private String name;
    protected boolean readOnly = false;
    private boolean isManadatory = true;

    /**
     *
     * @param nvConfig
     * @throws NullPointerException
     */
    protected NVBaseWidget(NVConfig nvConfig)
            throws NullPointerException {
        this(nvConfig, false);
    }

    /**
     *
     * @param nvConfig
     * @param nullAllowed
     * @throws NullPointerException
     */
    protected NVBaseWidget(NVConfig nvConfig, boolean nullAllowed)
            throws NullPointerException {
        if (!nullAllowed) {
            SUS.checkIfNulls("NVConfig is null.", nvConfig);
        }

        this.nvConfig = nvConfig;
//		this.crudManager = crudManager;
    }

    /**
     *
     * @return nv config
     */
    @Override
    public NVConfig getNVConfig() {
        return nvConfig;
    }

    /**
     *
     * @param value
     */
    public void setValue(V value)
            throws NullPointerException, IllegalArgumentException, ExceptionCollection {
        setWidgetValue(value);
    }

    /**
     *
     * @return typed value
     */
    public V getValue()
            throws NullPointerException, IllegalArgumentException, ExceptionCollection {
        V v = getWidgetValue();

        return v;
    }

    /**
     *
     */
    public abstract Widget getWidget();

    /**
     *
     * @param value
     */
    public abstract void setWidgetValue(V value);

    /**
     *
     * @return typed value
     */
    public abstract V getWidgetValue();

    /**
     *
     * @param value
     */
    public abstract void setWidgetValue(String value);


    public boolean isMandatory() {
        return isManadatory;
    }

    public void setMandatory(boolean mandatory) {
        this.isManadatory = mandatory;
    }

    /**
     *
     * @return NVConfigNameMap
     */
    public NVConfigNameMap getNVConfigNameMap() {
        return nvConfigNameMap;
    }

    /**
     *
     * @param nvcnm
     */
    public void setNVConfigNameMap(NVConfigNameMap nvcnm) {
        nvConfigNameMap = nvcnm;
    }

    /**
     *
     * @return NVTextWidgetController
     */
    @SuppressWarnings("rawtypes")
    public NVTextWidgetController getTextWidgetController() {
        return textWidgetController;
    }

    /**
     *
     * @param textWidgetController
     */
    @SuppressWarnings("rawtypes")
    public void setTextWidgetController(NVTextWidgetController textWidgetController) {
        this.textWidgetController = textWidgetController;
    }

//	/**
//	 * @return the crudManager
//	 */
//	public CRUDManager getCRUDManager()
//	{
//		return crudManager;
//	}
//
//	/**
//	 * @param crudManager the crudManager to set
//	 */
//	public void setCRUDManager(CRUDManager crudManager)
//	{
//		this.crudManager = crudManager;
//	}
//
//	@Override
//	public boolean hasPermission(String resourceID, CRUD crud)
//	{
//		if (crudManager != null)
//		{
//			return crudManager.hasPermission(resourceID, crud);
//		}
//
//		return true;
//	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public abstract void setReadOnly(boolean readOnly);

    public abstract void clear();
}