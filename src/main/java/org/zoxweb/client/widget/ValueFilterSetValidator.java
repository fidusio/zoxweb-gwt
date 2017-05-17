/*
 * Copyright (c) 2012-Jul 24, 2014 ZoxWeb.com LLC.
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author mzebib
 * @param <V> 
 *
 */
public class ValueFilterSetValidator<V> 
{
	private List<ValueFilterHandler<V>> list = new ArrayList<ValueFilterHandler<V>>();
	
	
	public void add(ValueFilterHandler<V> vf)
	{
		list.add(vf);
	}
	
	public void remove(ValueFilterHandler<V> vf)
	{
		list.remove(vf);
	}
	
	public boolean contains(ValueFilterHandler<V> vf)
	{
		if (vf != null)
		{
			return list.contains(vf);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param checkAll
	 * @return true if all valid 
	 */
	public boolean areAllValid(boolean checkAll)
	{
		boolean ret = true;
		
		for (ValueFilterHandler<V> vf : list)
		{
			if (!vf.isValid())
			{
				ret = false;
			
				if (!checkAll)
				{
					break;
				}
			}
		}
		
		return ret;
	}
	
}
