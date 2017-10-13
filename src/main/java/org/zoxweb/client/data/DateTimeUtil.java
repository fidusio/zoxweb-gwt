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

import org.zoxweb.shared.filters.ValueFilter;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;

/**
 * Date and time utilities.
 */
@SuppressWarnings("serial")
public final class DateTimeUtil
implements ValueFilter<String, Long>
{

	public static final DateTimeUtil SINGLETON = new DateTimeUtil();
	/**
	 * The constructor is declared final to prevent instantiation.
	 */
	private DateTimeUtil()
	{
		
	}
	
	public static final  TimeZone GMT_TZ= TimeZone.createTimeZone(0);
	public static final  DateTimeFormat DEFAULT_GMT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss:SSS'Z'");
	@Override
	public String toCanonicalID() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Long validate(String in) throws NullPointerException, IllegalArgumentException 
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isValid(String in)
	{
		// TODO Auto-generated method stub
		return false;
	}

}