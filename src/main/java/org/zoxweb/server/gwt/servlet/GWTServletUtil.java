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
package org.zoxweb.server.gwt.servlet;

import org.zoxweb.server.shiro.servlet.ShiroBaseGWTRPC;

/**
 * GWT servlet utility class.
 */
public class GWTServletUtil 
{
	/**
	 * The constructor is declared private to prevent instantiation.
	 */
	private GWTServletUtil()
	{
		
	}

	/**
	 * Looks up attribute value based on given name.
	 * @param gwtServlet
	 * @param attributeName
	 * @return
	 */
	public static Object lookupAttributeValue(ShiroBaseGWTRPC gwtServlet, String attributeName)
	{
		return gwtServlet.getServletRequest().getSession().getAttribute(attributeName);
	}

}