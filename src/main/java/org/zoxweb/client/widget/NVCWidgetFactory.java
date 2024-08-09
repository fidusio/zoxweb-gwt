/*
 * Copyright (c) 2012-Dec 15, 2015 ZoxWeb.com LLC.
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

import java.math.BigDecimal;
import java.util.Date;

import org.zoxweb.client.widget.custom.EmailWidget;
import org.zoxweb.shared.filters.FilterType;
import org.zoxweb.shared.filters.PartialFilter;
import org.zoxweb.shared.filters.ValueFilter;
import org.zoxweb.shared.util.Const;
import org.zoxweb.shared.util.DynamicEnumMap;
import org.zoxweb.shared.util.GetName;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVConfigNameMap;
import org.zoxweb.shared.util.NVEntity;

/**
 * @author mzebib
 *
 */
public abstract class NVCWidgetFactory
	implements GetName
{
	
	public static NVCWidgetFactory DEFAULT = new NVCWidgetFactory("ZWNVEntityWidgetFactory")
			{

				@Override
				protected NVEntityWidget createNVEntityWidgetImpl(NVEntity nve, boolean showShortHand) 
				{
					return new NVEntityDefaultWidget(nve, showShortHand);
				}

				@Override
				protected NVBaseWidget<?> createWidgetFromArrayBaseImpl(NVConfig nvc, boolean showShortHand) 
				{
					Class<?> clazz = nvc.getMetaType();	
					
					if (nvc.isArray())
					{
						if (nvc instanceof NVConfigEntity)
						{
							return new NVEntityDefaultWidget((NVConfigEntity) nvc, showShortHand);
						}
						
						if (nvc.isEnum())
						{
							return new NVEnumWidget(nvc);
						}
						
						if (clazz == String[].class)
						{
							if (nvc.getValueFilter() == FilterType.EMAIL)
							{
								return new EmailWidget();
							}
							
							return new NVPairWidget(nvc);
						}
						
						if (clazz == int[].class || clazz == Integer[].class)
						{
							return new NVIntegerWidget(nvc);
						}
						
						if (clazz == long[].class || clazz == Long[].class)
						{
							return new NVLongWidget(nvc);
						}
						
						if (clazz == boolean[].class || clazz == Boolean[].class)
						{
							return new NVBooleanWidget(nvc);
						}
						
						if (clazz == double[].class || clazz == Double[].class)
						{
							return new NVDoubleWidget(nvc);
						}
						
						if (clazz == float[].class || clazz == Float[].class)
						{
							return new NVFloatWidget(nvc);
						}
						
						if (clazz == BigDecimal[].class)
						{
							return new NVBigDecimalWidget(nvc);
						}
					}
					
					return null;
				}
				
				@Override
				protected NVBaseWidget<?> createWidgetImpl(NVConfig nvc, NVConfigNameMap map, boolean enableScrolling)
				{
					NVBaseWidget<?> ret = null;
					
					Class<?> clazz = nvc.getMetaType();
					
					if (nvc instanceof NVConfigEntity && !nvc.isArray())
					{
//						if (clazz == PhoneDAO.class)
//						{
//							ret = new PhoneDAOWidget((NVConfigEntity) nvc);
//						}
//						else if (clazz == AddressDAO.class)
//						{
//							ret = new AddressDAOWidget((NVConfigEntity) nvc);
//						}
//						else if (clazz == CreditCardDAO.class)
//						{
//							ret = new CreditCardDAOWidget((NVConfigEntity) nvc);
//						}
//						else if (clazz == DataContentDAO.class)
//						{
//							ret = new DataContentDAOWidget((NVConfigEntity) nvc);
//						}
//						else if (clazz == MessageTemplateDAO.class)
//						{
//							ret = new MessageTemplateDAOWidget((NVConfigEntity) nvc);
//						}
//						else if (clazz == SimpleDocumentDAO.class)
//						{
//							ret = new SimpleDocumentDAOWidget((NVConfigEntity) nvc);
//						}
//						else if (clazz == NVEntityContainerDAO.class)
//						{
//							ret = new NVEntityContainerDAOWidget((NVConfigEntity) nvc);
//						}
//						else
						{
							ret = new NVEntityDefaultWidget((NVConfigEntity) nvc);
						}
					}
					
					if (nvc.isArray() && ret == null)
					{
						if (nvc instanceof NVConfigEntity)
						{
							ret = new NVEntityArrayWidget((NVConfigEntity) nvc, enableScrolling);
						}
						else if (nvc.isEnum())
						{
							ret = new NVEnumArrayWidget(nvc);
						}
						else if (clazz == String[].class)
						{
//							if (nvc.isUnique())
//							{
//								ret = new NVStringMapWidget(nvc, enableScrolling);
//							}
//							else
							{
								ret = new NVStringArrayValuesWidget(nvc, enableScrolling);
							}
						}
						else if (clazz == int[].class || clazz == Integer[].class)
						{
							ret = new NVIntegerArrayWidget(nvc);
						}
						else if (clazz == long[].class || clazz == Long[].class)
						{
							ret = new NVLongArrayWidget(nvc);
						}
						else if (clazz == double[].class || clazz == Double[].class)
						{
							ret = new NVDoubleArrayWidget(nvc);
						}
						else if (clazz == float[].class || clazz == Float[].class)
						{
							ret = new NVFloatArrayWidget(nvc);
						}
						else if (clazz == boolean[].class || clazz == Boolean[].class)
						{
							ret = new NVBooleanArrayWidget(nvc);
						}
						else if (clazz == BigDecimal[].class)
						{
							ret = new NVBigDecimalArrayWidget(nvc);
						}
						else
						{
							throw new IllegalArgumentException("Array type not supported: " + nvc);
						}
					}
					else if (ret == null)
					{
						if (nvc.isEnum())
						{
							if (DynamicEnumMap.class.equals(nvc.getMetaType()))
							{
								ret = new NVDynamicEnumWidget(nvc, Const.NVDisplayProp.DEFAULT);
							}
							else
							{
								ret = new NVEnumWidget(nvc);
							}
						}
						else if (clazz == String.class)
						{
							if (nvc.getValueFilter() != null &&  nvc.getValueFilter().getClass().equals(DynamicEnumMap.class))
							{
								ret = new NVDynamicEnumWidget(nvc, Const.NVDisplayProp.DEFAULT);
							}
							else if (nvc.getValueFilter() != null && nvc.getValueFilter() == FilterType.EMAIL)
							{
								ret = new NVStringEmailWidget(nvc);
							}
							else
							{
								ret = new NVStringWidget(nvc);
							}
						}
						else if (clazz == int.class || clazz == Integer.class)
						{
							ret = new NVIntegerWidget(nvc);
						}
						else if (clazz == long.class || clazz == Long.class)
						{
							ret = new NVLongWidget(nvc);
						}
						else if (clazz == double.class || clazz == Double.class)
						{
							ret = new NVDoubleWidget(nvc);
						}
						else if (clazz == float.class || clazz == Float.class)
						{
							ret = new NVFloatWidget(nvc);
						}
						else if (clazz == boolean.class || clazz == Boolean.class)
						{
							ret = new NVBooleanWidget(nvc);
						}
						else if (clazz == BigDecimal.class)
						{
							ret = new NVBigDecimalWidget(nvc);
						}
						else if (clazz == Date.class)
						{	
							ret = new NVDateWidget(nvc);
						}
					}
					
					if (ret != null)
					{
						ret.setNVConfigNameMap(map);
						return ret;
					}
					
					return null;
				}
		
			};
	private String name;
	private NVCWidgetFactory[] factories;
	
	protected NVCWidgetFactory(String name, NVCWidgetFactory ... factories)
	{
		this.name = name;
		
		int size = 1;
		
		if (factories != null && factories.length > 0)
		{
			size += factories.length;
		}
		
		this.factories = new NVCWidgetFactory[size];
		
		this.factories[0] = this;
		
		for (int i = 1; i < size; i++)
		{
			this.factories[i] = factories[i - 1];
		}
	}
	
	@Override
	public String getName() 
	{
		return name;
	}
	
	public NVEntityWidget createNVEntityWidget(NVEntity nve)
	{
		return createNVEntityWidget(nve, false);
	}
	
	public NVEntityWidget createNVEntityWidget(NVEntity nve, boolean showShortHand)
	{
////		if (nve instanceof AddressDAO)
////		{
////			return new AddressDAOWidget((AddressDAO) nve, showShortHand);
////		}
////		else  if (nve instanceof PhoneDAO)
////		{
////			return new PhoneDAOWidget((PhoneDAO) nve, showShortHand);
////		}
////		else if (nve instanceof CreditCardDAO)
////		{
////			return new CreditCardDAOWidget((CreditCardDAO) nve, true, showShortHand);
////		}
////		else if (nve instanceof DataContentDAO)
////		{
////			return new DataContentDAOWidget((DataContentDAO) nve);
////		}
////		else if (nve instanceof MessageTemplateDAO)
////		{
////			return new MessageTemplateDAOWidget((MessageTemplateDAO) nve);
////		}
////		else if (nve instanceof APIConfigInfoDAO)
////		{
////			return new APIConfigInfoWidget((APIConfigInfoDAO) nve);
////		}
////		else if (nve instanceof SimpleDocumentDAO)
////		{
////			return new SimpleDocumentDAOWidget((SimpleDocumentDAO) nve);
////		}
////		else if (nve instanceof NVEntityContainerDAO)
////		{
////			return new NVEntityContainerDAOWidget((NVEntityContainerDAO) nve);
////		}
////		else if (nve instanceof FileInfoDAO)
////		{
////			return new FileViewerWidget((FileInfoDAO) nve);
////		}
////		else
////		{
////			return new NVEntityDefaultWidget(nve, showShortHand);
////		}
//		
//	
		for (NVCWidgetFactory factory : factories)
		{
			NVEntityWidget nveWidget = factory.createNVEntityWidgetImpl(nve, showShortHand);
			
			if (nveWidget != null)
			{
				return nveWidget;
			}
		}
		
		return null;
	}
	

	protected abstract NVEntityWidget createNVEntityWidgetImpl(NVEntity nve, boolean showShortHand);
	
	public NVBaseWidget<?> mapFilterTypeToWidget(ValueFilter<String, String> vf, NVConfig nvc)
	{
		return mapFilterTypeToWidget(vf, nvc, false);
	}
	
	@SuppressWarnings("unchecked")
	public NVBaseWidget<?> mapFilterTypeToWidget(ValueFilter<String, String> vf, NVConfig nvc, boolean nullAllowed)
	{
		if (vf != null)
		{
			if (vf == FilterType.BIG_DECIMAL)
			{
				return new NVBigDecimalWidget(nvc);
			}
			
			if (vf == FilterType.BOOLEAN)
			{
				return new NVBooleanWidget(nvc);
			}
				
			if (vf == FilterType.DOUBLE)
			{
				return new NVDoubleWidget(nvc);
			}
				
			if (vf == FilterType.FLOAT)
			{
				return new NVFloatWidget(nvc);
			}	
				
			if (vf == FilterType.INTEGER)
			{
				return new NVIntegerWidget(nvc);
			}
			
			if (vf == FilterType.LONG)
			{
				return new NVLongWidget(nvc);
			}
			
			if (vf == FilterType.EMAIL || vf == FilterType.URL)
			{
				NVStringWidget nvsw = new NVStringWidget(nvc);
				nvsw.getTextWidgetController().setValueFilter(new PartialFilter(vf));
				return nvsw;
			}
			
			if (vf == FilterType.PASSWORD || vf == FilterType.ENCRYPT_MASK)
			{
				NVPasswordWidget nvpw = new NVPasswordWidget(nvc);
				nvpw.getTextWidgetController().setValueFilter(new PartialFilter(vf));
				return nvpw;
			}
			
			if (vf instanceof DynamicEnumMap)
			{
				DynamicEnumMap dem =  (DynamicEnumMap) vf;
				
				return new NVDynamicEnumWidget(dem.getName(), dem, Const.NVDisplayProp.DEFAULT);
			}
		}
		
		return new NVStringWidget(nvc);
	}
	
	public NVBaseWidget<?> createWidgetFromArrayBase(NVConfig nvc)
	{
		return createWidgetFromArrayBase(nvc, false);
	}
	
	public NVBaseWidget<?> createWidgetFromArrayBase(NVConfig nvc, boolean showShortHand)
	{
		for (NVCWidgetFactory factory : factories)
		{
			NVBaseWidget<?> nvbw = factory.createWidgetFromArrayBaseImpl(nvc, showShortHand);
			
			if (nvbw != null)
			{
				return nvbw;
			}
		}
		
		throw new IllegalArgumentException("Unsupported type: " + nvc);
	}
	
	protected abstract NVBaseWidget<?> createWidgetFromArrayBaseImpl(NVConfig nvc, boolean showShortHand);
	
	protected abstract NVBaseWidget<?> createWidgetImpl(NVConfig nvc, NVConfigNameMap map, boolean enableScrolling);
	
	public NVBaseWidget<?> createWidget(NVConfig nvc, NVConfigNameMap map, boolean enableScrolling)
	{
		for (NVCWidgetFactory factory : factories)
		{
			NVBaseWidget<?> nvbw = factory.createWidgetImpl(nvc, map, enableScrolling);
			
			if (nvbw != null)
			{
				return nvbw;
			}
		}
		
		throw new IllegalArgumentException("Unsupported type: " + nvc);
	}
}