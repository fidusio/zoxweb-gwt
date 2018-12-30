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

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Date;

import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zoxweb.shared.data.NVEntityFactory;
import org.zoxweb.shared.db.QueryMarker;
import org.zoxweb.shared.db.QueryMatch;
import org.zoxweb.shared.db.QueryRequest;
import org.zoxweb.shared.filters.FilterType;
import org.zoxweb.shared.filters.ValueFilter;
import org.zoxweb.shared.util.ArrayValues;
import org.zoxweb.shared.util.Const.GNVType;
import org.zoxweb.shared.util.Const.LogicalOperator;
import org.zoxweb.shared.util.DynamicEnumMap;
import org.zoxweb.shared.util.DynamicEnumMapManager;
import org.zoxweb.shared.util.GetNameValue;
import org.zoxweb.shared.util.MetaToken;
import org.zoxweb.shared.util.NVBase;
import org.zoxweb.shared.util.NVBigDecimal;
import org.zoxweb.shared.util.NVBigDecimalList;
import org.zoxweb.shared.util.NVConfig;
import org.zoxweb.shared.util.NVConfigEntity;
import org.zoxweb.shared.util.NVDouble;
import org.zoxweb.shared.util.NVDoubleList;
import org.zoxweb.shared.util.NVEntity;
import org.zoxweb.shared.util.NVEntityGetNameMap;

import org.zoxweb.shared.util.NVEntityReferenceIDMap;
import org.zoxweb.shared.util.NVEntityReferenceList;
import org.zoxweb.shared.util.NVEnum;
import org.zoxweb.shared.util.NVEnumList;
import org.zoxweb.shared.util.NVFloat;
import org.zoxweb.shared.util.NVFloatList;
import org.zoxweb.shared.util.NVGenericMap;
import org.zoxweb.shared.util.NVGenericMapList;
import org.zoxweb.shared.util.NVGetNameValueList;
import org.zoxweb.shared.util.NVInt;
import org.zoxweb.shared.util.NVIntList;
import org.zoxweb.shared.util.NVLong;
import org.zoxweb.shared.util.NVLongList;
import org.zoxweb.shared.util.NVPair;
import org.zoxweb.shared.util.NVPairGetNameMap;
import org.zoxweb.shared.util.NVPairList;
import org.zoxweb.shared.util.NVStringList;
import org.zoxweb.shared.util.NVBlob;
import org.zoxweb.shared.util.NVBoolean;
import org.zoxweb.shared.util.SharedBase64;
import org.zoxweb.shared.util.SharedBase64.Base64Type;
import org.zoxweb.shared.util.SharedStringUtil;
import org.zoxweb.shared.util.SharedUtil;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * JSON client-side utility methods.
 */
public class JSONClientUtil
{

	private static final String FIELD_NAMES = "field_names";
	private static final String BATCH_SIZE = "batch_size";
	private static final String QUERY = "query";
	
	
	public static Base64Type DEFAULT_BASE64 = Base64Type.DEFAULT;

	/**
	 * The constructor is declared private to prevent instantiation.
	 */
	private JSONClientUtil()
	{

	}

	/**
	 * Converts json to NVEntity.
	 * @param nve
	 * @param json
	 * @param nveFactory
	 * @param <V>
	 * @return
	 */
	public static <V extends NVEntity> V fromJSON(V nve, String json, NVEntityFactory nveFactory)
	{
		@SuppressWarnings("deprecation")
		JSONObject value = (JSONObject) JSONParser.parseLenient(json);
		
		return fromJSON(nve, value, nveFactory);
	}

	/**
	 *
	 * @param value
	 * @param nveFactory
	 * @param <V>
	 * @return
	 */
	private static <V extends NVEntity> V classTypeToNVE(JSONObject value, NVEntityFactory nveFactory)
	{
		JSONValue className = value.get(MetaToken.CLASS_TYPE.getName());
		
		if (className != null && className instanceof JSONString)
		{
			return nveFactory.createNVEntity(((JSONString)className).stringValue());
		}
		
		return null;
	}

	/**
	 * Converts json to List of NVEntity.
	 * @param json
	 * @param nveFactory
	 * @return
	 */
	public static List<NVEntity> fromJSONValues(String json, NVEntityFactory nveFactory)
	{
		@SuppressWarnings("deprecation")
		JSONObject jsonObject = (JSONObject) JSONParser.parseLenient(json);
		JSONArray values = (JSONArray) jsonObject.get(MetaToken.VALUES.getName());
		List<NVEntity> ret = new ArrayList<NVEntity>();
		
		for (int i = 0; i < values.size(); i++)
		{
			ret.add(fromJSON(null, (JSONObject) values.get(i), nveFactory));
		}
		
		return ret;
	}
	
	
	public static List<NVEntity> fromJSONArray(String json, NVEntityFactory nveFactory)
	{
		@SuppressWarnings("deprecation")
		JSONArray values = (JSONArray) JSONParser.parseLenient(json);
//		JSONArray values = (JSONArray) jsonObject.get(MetaToken.VALUES.getName());
		List<NVEntity> ret = new ArrayList<NVEntity>();
		
		for (int i = 0; i < values.size(); i++)
		{
			ret.add(fromJSON(null, (JSONObject) values.get(i), nveFactory));
		}
		
		return ret;
	}

	/**
	 *
	 * @param nve
	 * @param value
	 * @param nveFactory
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V extends NVEntity> V fromJSON(V nve, JSONObject value, NVEntityFactory nveFactory)
	{
		// parse the class type
		if (nve == null)
		{
			nve = classTypeToNVE(value, nveFactory);
		}
		
		if (nve != null)
		{
			NVConfigEntity nvce = (NVConfigEntity) nve.getNVConfig();
		
			for (NVConfig nvc : nvce.getAttributes())
			{
				JSONValue jsonValue = value.get(nvc.getName());
				
				if (jsonValue == null || jsonValue instanceof JSONNull)
				{
					continue;
				}
				
				if (nvc instanceof NVConfigEntity)
				{
					if (nvc.isArray())
					{
						// array NVEntity
						JSONArray jsonArray = (JSONArray) jsonValue;
						ArrayValues<NVEntity> avNVE = (ArrayValues<NVEntity>) nve.lookup(nvc.getName());

						for (int i = 0; i < jsonArray.size(); i++)
						{
							avNVE.add(fromJSON(null, (JSONObject) jsonArray.get(i), nveFactory));
						}
					}
					else
					{
						nve.setValue(nvc.getName(), fromJSON(null, (JSONObject)jsonValue, nveFactory));
					}
				}
				else
				{
					if (!nvc.isArray())
					{
						if (nvc.getMetaTypeBase().equals(String.class))
						{
							String val = ((JSONString) value.get(nvc.getName())).stringValue();
							nve.setValue(nvc.getName(), val);
						}
						else if (nvc.getMetaTypeBase().equals(Long.class))
						{
							long val = (long) ((JSONNumber) value.get(nvc.getName())).doubleValue();
							nve.setValue(nvc.getName(), val);
						}
						else if (nvc.getMetaType().equals(Date.class))
						{
							JSONValue jValue = value.get(nvc.getName());
							if (jValue.isString() != null)
							{
								nve.setValue(nvc.getName(), DateTimeUtil.SINGLETON.validate(((JSONString)jValue).stringValue()));
							}
							else
							{
								long val = (long) ((JSONNumber) value.get(nvc.getName())).doubleValue();
								nve.setValue(nvc.getName(), val);
							}
						}
						else if (nvc.getMetaTypeBase().equals(Integer.class))
						{
							int val = (int) ((JSONNumber) value.get(nvc.getName())).doubleValue();
							nve.setValue(nvc.getName(), val);
						}
						else if (nvc.getMetaTypeBase().equals(Float.class))
						{
							float val = (float) ((JSONNumber) value.get(nvc.getName())).doubleValue();
							nve.setValue(nvc.getName(), val);
						}
						else if (nvc.getMetaTypeBase().equals(Double.class))
						{
							nve.setValue(nvc.getName(), ((JSONNumber) value.get(nvc.getName())).doubleValue());
						}
						else if (nvc.getMetaTypeBase().equals(Boolean.class))
						{
							nve.setValue(nvc.getName(), ((JSONBoolean) value.get(nvc.getName())).booleanValue());
						}
						else if (nvc.getMetaType().equals(NVGenericMap.class))
	                    {
	                        JSONObject jsonObject = (JSONObject) value.get(nvc.getName());
	                        NVGenericMap nvb = (NVGenericMap)nve.lookup(nvc);
	                        fromJSONGenericMap(jsonObject, nvb, nveFactory);
	                    }
						else if (nvc.getMetaType().equals(NVGenericMapList.class))
	                    {
	                        JSONArray jsonArray = (JSONArray) value.get(nvc.getName());
	                        NVGenericMapList nvb = (NVGenericMapList)nve.lookup(nvc);
	                        fromJSON(jsonArray, nvb, nveFactory);
	                    }
						else if (nvc.getMetaType().equals(BigDecimal.class))
						{
							double val = ((JSONNumber) value.get(nvc.getName())).doubleValue();
							BigDecimal bd = new BigDecimal(val);
							bd = bd.setScale(3, RoundingMode.HALF_EVEN);
							nve.setValue(nvc.getName(), bd);
						}
						else if (nvc.getMetaType().isEnum())
						{
							Enum<?> e = SharedUtil.lookupEnum(((JSONString)jsonValue).stringValue(), (Enum<?>[]) nvc.getMetaType().getEnumConstants());
							
							if (e!= null)
							{
								nve.setValue(nvc.getName(), e);
							}
						}
						else if (nvc.getMetaType().equals(NVStringList.class))
	                    {
	                        JSONArray jsonArray = (JSONArray) value.get(nvc.getName());
	                        NVStringList nvb = (NVStringList)nve.lookup(nvc);
	                        fromJSON(jsonArray, nvb);
	                    }
					}
					else if (nvc.getMetaType().equals(byte[].class))
					{
						nve.setValue(nvc.getName(), SharedBase64.decode(DEFAULT_BASE64, ((JSONString) value.get(nvc.getName())).stringValue().getBytes()));
					}
					else
					{
						Class<?> metaBase = nvc.getMetaTypeBase();
						JSONArray jsonArray = (JSONArray) value.get(nvc.getName());
						NVBase<?> nvb = nve.lookup(nvc);
						
						if (jsonArray == null)
						{
							continue;
						}
						
						if (metaBase == String.class)
						{
							ArrayValues<NVPair> list = (ArrayValues<NVPair>) nvb;
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								list.add(toNVPair((JSONObject) jsonArray.get(i)));
							}
						}
						else if (metaBase == Long.class)
						{
							List<Long> list = new ArrayList<Long>();
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								list.add((long) ((JSONNumber) jsonArray.get(i)).doubleValue());
							}
							
							nve.setValue(nvc.getName(), list);
						}
						else if (metaBase == Integer.class)
						{
							List<Integer> list = new ArrayList<Integer>();
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								list.add((int) ((JSONNumber) jsonArray.get(i)).doubleValue());
							}
							
							nve.setValue(nvc.getName(), list);
						}
						else if (metaBase == Float.class)
						{
							List<Float> list = new ArrayList<Float>();
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								list.add((float) ((JSONNumber) jsonArray.get(i)).doubleValue());
							}
							
							nve.setValue(nvc.getName(), list);
						}
						else if (metaBase == Double.class)
						{
							List<Double> list = new ArrayList<Double>();
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								list.add((double) ((JSONNumber) jsonArray.get(i)).doubleValue());
							}
							
							nve.setValue(nvc.getName(), list);
						}
						else if (metaBase == BigDecimal.class)
						{
							List<BigDecimal> list = new ArrayList<BigDecimal>();
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								double val = ((JSONNumber) jsonArray.get(i)).doubleValue();
								BigDecimal bd = new BigDecimal(val);
								bd = bd.setScale(3, RoundingMode.HALF_EVEN);
								list.add(bd);
							}
							
							nve.setValue(nvc.getName(), list);
						}
						else if (metaBase.isEnum())
						{
							List<Enum<?>> list = new ArrayList<Enum<?>>();
							
							for (int i = 0; i < jsonArray.size(); i++)
							{
								Enum<?> e = SharedUtil.lookupEnum(((JSONString) jsonArray.get(i)).stringValue(), (Enum<?>[]) metaBase.getEnumConstants());
								
								if (e != null)
								{
									list.add(e);
								}
							}
							
							nve.setValue(nvc.getName(), list);
						}
					}	
				}
			}
		}
		
		return nve;
	}

	/**
	 * Converts JSONObject to NVPair.
	 * @param jsonObject
	 * @return
	 */
	private static NVPair toNVPair(JSONObject jsonObject)
	{
		NVPair nvp = new NVPair();
	
		Set<String> keySet = jsonObject.keySet();
	
		if (keySet.contains(MetaToken.NAME.getName()) && keySet.contains(MetaToken.VALUE.getName()))
		{
			// Set up with name and value prefix
			// e.g. {"name":"last_name", "value":"Smith"}
			
			JSONValue jsonValue = jsonObject.get(MetaToken.VALUE.getName());
			
			if (jsonValue != null && jsonValue instanceof JSONString)
			{
				nvp.setValue(((JSONString) jsonValue).stringValue());
			}
			
			JSONValue jsonName = jsonObject.get(MetaToken.NAME.getName());
			
			if (jsonName != null && jsonName instanceof JSONString)
			{
				nvp.setName(((JSONString) jsonName).stringValue());
			}
		}
		else
		{
			// Set up without name and value prefix
			// e.g. {"last_name":"Smith"}
			
			String name = null;
			
			for (String str : keySet)
			{
				if (!str.equals(MetaToken.NAME.getName()) && !str.equals(MetaToken.VALUE.getName()) && !str.equals(MetaToken.VALUE_FILTER.getName()))
				{
					name = str;
					break;
				}
			}
			
			if (name != null)
			{
				nvp.setName(name);
				JSONValue jsonValue = jsonObject.get(name);
				
				if (jsonValue != null && jsonValue instanceof JSONString)
				{
					nvp.setValue(((JSONString) jsonValue).stringValue());
				}
			}
		}
		
		JSONString jsonVF = (JSONString) jsonObject.get(MetaToken.VALUE_FILTER.getName());
		
		if (jsonVF != null)
		{
			ValueFilter<String, String> vf = (FilterType) SharedUtil.lookupEnum(jsonVF.stringValue(), FilterType.values());
			
			if (vf == null)
			{
				vf = DynamicEnumMapManager.SINGLETON.lookup(jsonVF.stringValue());
			}

			if (vf != null)
			{
				nvp.setValueFilter(vf);
			}
		}
		
//		JSONValue jsonVF = jsonObject.get(MetaToken.VALUE_FILTER.getName());
//		
//		if (jsonVF != null)
//		{
//			if (jsonVF instanceof JSONString)
//			{
//				FilterType ft = (FilterType) SharedUtil.lookupEnum(FilterType.values(), ((JSONString) jsonVF).stringValue());
//				
//				if (ft != null)
//				{
//					nvp.setValueFilter(ft);
//				}
//			}
//			else if (jsonVF instanceof JSONObject)
//			{
//				DynamicEnumMap dem = fromJSONDynamicEnumMap(jsonVF.toString());
//				
//				if (dem != null)
//				{
//					nvp.setValueFilter(dem);
//				}
//			}
//		}
		
		return nvp;
	}
	
	
	
	public static JSONObject toJSON(NVEntity nve)
	{
		return toJSON(nve, true);
	}

	/**
	 * Converts NVEntity to JSONObject.
	 * @param nve
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject toJSON(NVEntity nve, boolean printClass)
	{
		SharedUtil.checkIfNulls("Null NVEntity", nve);
		JSONObject jsonObject = new JSONObject();
		
		NVConfigEntity nvce = (NVConfigEntity) nve.getNVConfig();
		if(printClass)
			jsonObject.put(MetaToken.CLASS_TYPE.getName(), new JSONString(nve.getClass().getName()));
		
		for (NVConfig nvc : nvce.getAttributes())
		{
//			if (nvc instanceof NVConfigEntity)
//			{
//				if (!nvc.isArray() && nve.lookupValue(nvc) != null)
//				{
//					jsonObject.put(nvc.getName(), toJSON((NVEntity) nve.lookupValue(nvc)));
//				}
//			}
//			else if (nvc instanceof NVConfig)
			{
				if (!nvc.isArray())
				{
					JSONValue jsonValue = null;
					Object value = nve.lookupValue(nvc);
					
					if (value != null)
					{
						if (value instanceof NVEntity)
						{
							jsonObject.put(nvc.getName(), toJSON((NVEntity) value, printClass));
						}
						else if (nvc.getMetaTypeBase().equals(String.class))
						{
							jsonValue = new JSONString((String) value);
						}
						else if (
								 nvc.getMetaTypeBase().equals(Long.class) ||
								 nvc.getMetaTypeBase().equals(Integer.class) ||
								 nvc.getMetaTypeBase().equals(Float.class) ||
								 nvc.getMetaTypeBase().equals(Double.class) ||
								 nvc.getMetaTypeBase().equals(BigDecimal.class)
								)
						{
							Number num = (Number)value;
							if (num.doubleValue() != 0 || num.longValue() != 0 || 
								num.intValue() != 0 || num.floatValue() != 0)
							jsonValue = new JSONNumber(((Number) value).doubleValue());
						}
						else if (nvc.getMetaTypeBase().equals(Date.class))
						{
							//jsonValue = new JSONNumber(((Number) value).doubleValue());
							if ((Long)value != 0)
								jsonValue = new JSONString(DateTimeUtil.DEFAULT_GMT_MILLIS.format(new Date((Long) value)));
						}
						else if (nvc.getMetaTypeBase().equals(Boolean.class))
						{
							if ((Boolean)value)
							{
								jsonValue = JSONBoolean.getInstance((Boolean) value);
							}
						}
						else if (value instanceof Enum)
						{
							jsonValue = new JSONString(((Enum<?>)value).name());
						}
						else if (nvc.getMetaTypeBase().equals(NVGenericMap.class))
						{
							jsonValue = toJSONGenericMap((NVGenericMap)nve.lookup(nvc), printClass);
						}
						else if (nvc.getMetaTypeBase().equals(NVGenericMapList.class))
						{
							jsonValue = toJSONGenericMapList((NVGenericMapList)nve.lookup(nvc), printClass);
						}
						else if (nvc.getMetaTypeBase().equals(NVStringList.class))
						{
							jsonValue = toJSONStringList((NVStringList)nve.lookup(nvc));
						}
						
						if (jsonValue != null)
						{
							jsonObject.put(nvc.getName(), jsonValue);
						}
					}
				}
				else
				{
					JSONArray jsonArray = new JSONArray();
					int counter = 0;
					NVBase<?> nvb = nve.lookup(nvc);
					Class<?> metaBase = nvc.getMetaTypeBase();
					
					if (metaBase == String.class)
					{
						ArrayValues<NVPair> values = (ArrayValues<NVPair>) nvb;

						for (NVPair nvp : values.values())
						{
							JSONObject nvpJSON = toJSON(nvp);
							
							if (nvpJSON != null)
							{
								jsonArray.set(counter++, nvpJSON);
							}
						}
					}
					else if (nvb instanceof NVEntityReferenceList
							|| nvb instanceof NVEntityReferenceIDMap
							|| nvb instanceof NVEntityGetNameMap)
					{
						ArrayValues<NVEntity> values = (ArrayValues<NVEntity>) nvb;

						for (NVEntity nveTemp : values.values())
						{
							if (nveTemp != null)
							{
								jsonArray.set(counter++, toJSON(nveTemp, printClass));
							}
						}
					}
					else if (metaBase == Long.class)
					{
						NVLongList values = (NVLongList) nvb;

						for (Long val : values.getValue())
						{
							if (val != null)
							{
								jsonArray.set(counter++, new JSONNumber((double)val));
							}
						}
					}
					else if (metaBase == Integer.class)
					{
						NVIntList values = (NVIntList) nvb;

						for (Integer val : values.getValue())
						{
							if (val != null)
							{
								jsonArray.set(counter++, new JSONNumber((double)val));
							}
						}
					}
					else if (metaBase == Float.class)
					{
						NVFloatList values = (NVFloatList) nvb;

						for (Float val : values.getValue())
						{
							if (val != null)
							{
								jsonArray.set(counter++, new JSONNumber((double)val));
							}
						}
					}
					else if (metaBase == Double.class)
					{
						NVDoubleList values = (NVDoubleList) nvb;

						for (Double val : values.getValue())
						{
							if (val != null)
							{
								jsonArray.set(counter++, new JSONNumber((double)val));
							}
						}
					}
					else if (metaBase == BigDecimal.class)
					{
						NVBigDecimalList values = (NVBigDecimalList) nvb;

						for (BigDecimal val : values.getValue())
						{
							if (val != null)
							{
								jsonArray.set(counter++, new JSONNumber(val.doubleValue()));
							}
						}
					}
					else if (metaBase.isEnum())
					{
						NVEnumList values = (NVEnumList) nvb;

						for (Enum<?> e : values.getValue())
						{
							if (e != null)
							{
								jsonArray.set(counter++, new JSONString(e.name()));
							}
						}
					}
					else if (nvc.getMetaType() == byte[].class)
					{
						if (nvb.getValue() != null)
						{
							jsonObject.put(nvc.getName(), new JSONString(SharedBase64.encodeAsString(DEFAULT_BASE64,((NVBlob)nvb).getValue())));	
						}
						// so we don't add the array
						continue;
					}
					
					jsonObject.put(nvc.getName(), jsonArray);
					// we have a primitive array
				}
			}
		}
		
		return jsonObject;
	}

	/**
	 * Converts NVPair to JSONObject.
	 * @param nvp
	 * @return
	 */
	public static JSONObject toJSON(NVPair nvp)
	{
		JSONObject ret = null;

		if (nvp != null)
		{
			if (nvp.getName() != null)
			{
				ret = new JSONObject();
				
				if (nvp.getValueFilter() == null || nvp.getValueFilter() == FilterType.CLEAR)
				{
					ret.put(nvp.getName(), nvp.getValue() != null ? new JSONString(nvp.getValue()) : JSONNull.getInstance());
				}
				else
				{
					ret.put(MetaToken.NAME.getName(), new JSONString(nvp.getName()));
					ret.put(MetaToken.VALUE.getName(), nvp.getValue() != null ? new JSONString(nvp.getValue()) : JSONNull.getInstance());
					
//					if (nvp.getValueFilter() instanceof DynamicEnumMap)
//					{
//						ret.put(MetaToken.VALUE_FILTER.getName(), toJSONDynamicEnumMap((DynamicEnumMap) nvp.getValueFilter()));
//					}
//					else
					{
						ret.put(MetaToken.VALUE_FILTER.getName(), new JSONString(nvp.getValueFilter().toCanonicalID()));
					}
				}
			}
			else
			{
				// name == null
			}
		}
		
		return ret;
	}
	
	public static JSONObject toJSONGenericMap(NVGenericMap nvgm, boolean printClass)
	{
		JSONObject ret = null;
		if (nvgm != null)
		{
			ret = new JSONObject();
			for (GetNameValue<?> gnv : nvgm.values())
			{
				JSONValue jsonValue = null;
				if (gnv.getValue() != null)
				{
					if (gnv instanceof NVBoolean)
					{
						if (((NVBoolean)gnv).getValue())
						{
							jsonValue = JSONBoolean.getInstance((Boolean)gnv.getValue());
						}
					}
					else if (gnv instanceof NVLong || gnv instanceof NVInt || gnv instanceof NVFloat || gnv instanceof NVDouble || gnv instanceof NVBigDecimal)
					{
						Number num = (Number)gnv.getValue();
						if (num.doubleValue() != 0 || num.longValue() != 0 || 
							num.intValue() != 0 || num.floatValue() != 0)
						{
							jsonValue = new JSONNumber(((Number) num).doubleValue());
						}
					}
					else if (gnv.getValue() instanceof NVEntity)
					{
						jsonValue = toJSON((NVEntity)gnv.getValue());
					}
					else if (gnv.getValue() instanceof String)
					{
						jsonValue = new JSONString((String) gnv.getValue());
					}
					else if (gnv instanceof NVEnum)
					{
						jsonValue = new JSONString(((Enum<?>)gnv.getValue()).name());
					}
					else if (gnv instanceof NVBlob)
					{
						jsonValue = new JSONString(SharedBase64.encodeWrappedAsString((byte[]) gnv.getValue()));
					}
					else if (gnv instanceof NVGenericMap)
					{
						jsonValue = toJSONGenericMap((NVGenericMap)gnv, printClass);
					}
					else if (gnv instanceof NVGenericMapList)
					{
						jsonValue = toJSONGenericMapList((NVGenericMapList)gnv, printClass);
					}
					else if(gnv instanceof NVStringList)
					{
						jsonValue = toJSONStringList((NVStringList) gnv);
					}
					else if (gnv instanceof NVBase)
					{
						jsonValue = nvbToJSONArray((NVBase<?>) gnv);
					}
					
					//missing cases 
					//arrays
					
					
						
					if (jsonValue != null)
					{
						ret.put(gnv.getName(), jsonValue);
					}
				}	
			}
		}
		
		return ret;
	}

	/**
	 *
	 * @param className
	 * @param batchSize
	 * @param markers
	 * @return
	 */
	public static JSONObject toJSONQuery(String className, int batchSize, List<QueryMarker> markers)
	{
		return toJSONQuery(className, batchSize, markers != null ? markers.toArray(new QueryMarker[0]) : null);
	}

	/**
	 *
	 * @param className
	 * @param batchSize
	 * @param markers
	 * @return
	 */
	public static JSONObject toJSONQuery(String className, int batchSize, QueryMarker... markers)
	{
		QueryRequest qr = new QueryRequest();
		qr.setCanonicalID(className);
		qr.setBatchSize(batchSize);
		qr.setQuery(markers);
		
		return toJSONQuery(qr);
	}
	
	
	public static JSONArray toJSONArray(List<NVEntity> list)
	{
		JSONArray ret = new JSONArray();
		
		int index = 0;
		for (NVEntity nve: list)
		{
			if (nve != null)
				ret.set(index++, toJSON(nve, true));
		}	
		return ret;
	}
	
	public static JSONArray toJSONArray(NVEntity nves[])
	{
		JSONArray ret = new JSONArray();
		
		int index = 0;
		for (NVEntity nve: nves)
		{
			if (nve != null)
				ret.set(index++, toJSON(nve, true));
		}	
		return ret;
	}

	/**
	 *
	 * @param qr
	 * @return
	 */
	public static JSONObject toJSONQuery(QueryRequest qr)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MetaToken.CANONICAL_ID.getName(), new JSONString(qr.getCanonicalID()));
				
		jsonObject.put(BATCH_SIZE, new JSONNumber(qr.getBatchSize()));
		
		if (qr.getFieldNames() != null)
		{
			JSONArray jsonArray = new JSONArray();
			int i = 0;
			
			for (String fn : qr.getFieldNames())
			{
				if (!SharedStringUtil.isEmpty(fn))
				{
					jsonArray.set(i++, new JSONString(fn));
				}
			}
			
			jsonObject.put(FIELD_NAMES, jsonArray);
		}
		
		if (qr.getQuery() != null)
		{
			JSONArray jsonArray = new JSONArray();
			int i = 0;

			for (QueryMarker qm : qr.getQuery())
			{
				if (qm != null)
				{
					JSONObject qmJSON = new JSONObject();

					if (qm instanceof GetNameValue)
					{
						if (qm instanceof QueryMatch)
						{
							QueryMatch<?> qMatch = (QueryMatch<?>) qm;
							Object value = qMatch.getValue();
							
							if (value instanceof Number)
							{
								qmJSON.put(qMatch.getName(), new JSONNumber(((Number)value).doubleValue()));
							}
							else if (value instanceof String)
							{
								qmJSON.put(qMatch.getName(), new JSONString((String) value));
							}
							else if (value instanceof Enum)
							{
								qmJSON.put(qMatch.getName(), new JSONString(((Enum<?>) value).name()));
							}
							
							if (qMatch.getOperator() != null)
							{
								qmJSON.put(MetaToken.RELATIONAL_OPERATOR.getName(), new JSONString(qMatch.getOperator().name()));
							}
						}
					}
					else if (qm instanceof LogicalOperator)
					{
						qmJSON.put(MetaToken.LOGICAL_OPERATOR.getName(), new JSONString(((LogicalOperator)qm).name()));
					}

					jsonArray.set(i++, qmJSON);
				}
			}
			
			jsonObject.put(QUERY, jsonArray);
		}

		return jsonObject;
	}

	/**
	 * Converts NVEntity to JSONObject.
	 * @param nve
	 * @return
	 */
	public static JSONObject toJSONWrapper(NVEntity nve)
	{
		JSONObject value = toJSON(nve, true);		
		JSONObject ret = new JSONObject();
		
		ret.put(MetaToken.JSON_CONTENT.getName(), new JSONString("" + value));
		
		return ret;		
	}

	
	public static JSONObject toJSONMap(Map<String, ?> map)
	{
		return toJSONMap(map, true);
	}
	
	/**
	 *
	 * @param map
	 * @return
	 */
	public static JSONObject toJSONMap(Map<String, ?> map, boolean printClass)
	{
		JSONObject ret = new JSONObject();
		
		if (map != null)
		{
			for (Entry<String, ?> entry :  map.entrySet())
			{
				if (entry.getValue() instanceof List)
				{
					List<?> list = (List<?>) entry.getValue();
					JSONArray jsonArray = new JSONArray();
					
					for (int i = 0; i < list.size(); i++)
					{
						if (list.get(i) instanceof String)
						{
							jsonArray.set(i, new JSONString((String) list.get(i)));
						}
						else if (list.get(i) instanceof NVEntity)
						{
							jsonArray.set(i, toJSON((NVEntity) list.get(i), printClass));
						}
						else if (list.get(i) instanceof Boolean)
						{
							jsonArray.set(i, JSONBoolean.getInstance((Boolean) entry.getValue()));
						}
					}
					
					ret.put(entry.getKey(), jsonArray);
				}
				else
				{
					if (entry.getValue() instanceof String)
					{
						ret.put(entry.getKey(), new JSONString((String) entry.getValue()));
					}
					else if (entry.getValue() instanceof NVEntity)
					{
						ret.put(entry.getKey(), toJSON((NVEntity) entry.getValue()));
					}
					else if (entry.getValue() instanceof Boolean)
					{
						ret.put(entry.getKey(), JSONBoolean.getInstance((Boolean) entry.getValue()));
					}
				}
			}
		}
		
		return ret;
	}
	
	
	
	
	public static NVGenericMap fromJSONGenericMap(String json, NVEntityFactory nvef)
	{
		@SuppressWarnings("deprecation")
		JSONObject jsonObject = (JSONObject) JSONParser.parseLenient(json);
		
		return fromJSONGenericMap(jsonObject, null, nvef);
	}
	
	
	public static NVGenericMap fromJSONGenericMap(JSONObject json, NVEntityFactory nvef)
	{
		return fromJSONGenericMap(json, null, nvef);
	}
	
	public static NVGenericMap fromJSONGenericMap(JSONObject json, NVGenericMap ret, NVEntityFactory nvef)
	{
		if (ret == null)
			ret = new NVGenericMap();
		
		for (String key: json.keySet())
		{
			if (key.equals(MetaToken.CLASS_TYPE.getName()))
				continue;
			
			JSONValue  jne = json.get(key);
			if (jne.isArray() != null)
			{
				JSONArray ja = jne.isArray();
				NVBase<?> nvb = guessNVBaseArray(ja);
				if (nvb != null)
				{
					nvb.setName(key);
					ret.add(nvb);
					for (int i = 0; i < ja.size(); i++)
					{
						if (ja.get(i).isNull() != JSONNull.getInstance())
						{
							if (nvb instanceof NVPairList)
							{
								((NVPairList)nvb).add(toNVPair((JSONObject) ja.get(i)));
							}
							else if (nvb instanceof NVIntList)
							{
								((NVIntList)nvb).getValue().add((Integer)SharedUtil.parsePrimitiveValue(GNVType.NVINT, ja.get(i).isNumber().doubleValue()));
							}
							else if (nvb instanceof NVLongList)
							{
								((NVLongList)nvb).getValue().add((Long)SharedUtil.parsePrimitiveValue(GNVType.NVLONG, ja.get(i).isNumber().doubleValue()));
							}
							else if (nvb instanceof NVFloatList)
							{
								((NVFloatList)nvb).getValue().add((Float)SharedUtil.parsePrimitiveValue(GNVType.NVFLOAT, ja.get(i).isNumber().doubleValue()));
							}
							else if (nvb instanceof NVDoubleList)
							{
								((NVDoubleList)nvb).getValue().add((Double)SharedUtil.parsePrimitiveValue(GNVType.NVDOUBLE, ja.get(i).isNumber().doubleValue()));
							}
							else if (nvb instanceof NVStringList)
							{
								((NVStringList)nvb).getValue().add(ja.get(i).isString().stringValue());
							}
							else if (nvb instanceof NVGenericMapList)
							{
								((NVGenericMapList)nvb).add(fromJSONGenericMap((JSONObject)ja.get(i), null, nvef));
							}
						}
					}
				}
				
				
			}
			else if (jne.isObject() == null )
			{
				NVBase<?> nvb = guessPrimitive(key, jne);
				if (nvb != null)
					ret.add(nvb);
			}
			else// if (jne.isObject() != null)
			{
				NVEntity nve = fromJSON(null, jne.isObject(), nvef);
				if (nve != null)
					ret.add(key, nve);
				else
				{
					NVGenericMap toAdd = fromJSONGenericMap(jne.isObject(), null, nvef);
					toAdd.setName(key);
					ret.add(toAdd);
				}
			}
		}
		
		return ret;
	}

	/**
	 *
	 * @param json
	 * @param nveFactory
	 * @return
	 */
	public static Map<String, ?> fromJSONMap(String json, NVEntityFactory nveFactory)
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		@SuppressWarnings("deprecation")
		JSONObject jsonObject = (JSONObject) JSONParser.parseLenient(json);
		
		if (jsonObject != null)
		{
			for (String key : jsonObject.keySet())
			{
				JSONValue jsonValue = jsonObject.get(key);
				
				if (jsonValue instanceof JSONArray)
				{
					JSONArray jsonArray = (JSONArray) jsonValue;
					
					List<Object> list = new ArrayList<Object>();
										
					for (int i = 0; i < jsonArray.size(); i++)
					{
						if (jsonArray.get(i) instanceof JSONObject)
						{
							NVEntity nve = fromJSON(null, (JSONObject) jsonArray.get(i), nveFactory);
							list.add(nve);
						}
						else if (jsonArray.get(i) instanceof JSONString)
						{
							list.add(((JSONString) jsonArray.get(i)).stringValue());
						}
						else if (jsonArray.get(i) instanceof JSONBoolean)
						{
							list.add(((JSONBoolean) jsonArray.get(i)).booleanValue());
						}
					}
					
					ret.put(key, list);
				}
				else if (jsonValue instanceof JSONObject)
				{
					NVEntity val = fromJSON(null, (JSONObject) jsonValue, nveFactory);
					ret.put(key, val);
				}
				else if (jsonValue instanceof JSONString)
				{
					String val = ((JSONString) jsonValue).stringValue();
					ret.put(key, val);
				}
				else if (jsonValue instanceof JSONBoolean)
				{
					boolean val = ((JSONBoolean) jsonValue).booleanValue();
					ret.put(key, val);
				}
			}
		}
		
		return ret;
	}

	/**
	 * Converts DynamicEnumMap to JSONObject.
	 * @param dem
	 * @return
	 */
	public static JSONObject toJSONDynamicEnumMap(DynamicEnumMap dem)
	{
		JSONObject ret = new JSONObject();
		
		if (dem != null)
		{
			ret.put(MetaToken.REFERENCE_ID.getName(), new JSONString((String) dem.getReferenceID()));
			ret.put(MetaToken.USER_ID.getName(), new JSONString((String) dem.getUserID()));
			ret.put(MetaToken.ACCOUNT_ID.getName(), new JSONString((String) dem.getAccountID()));
			ret.put(MetaToken.NAME.getName(), new JSONString((String) dem.getName()));
			ret.put(MetaToken.DESCRIPTION.getName(), new JSONString((String) dem.getDescription()));
			ret.put(MetaToken.IS_FIXED.getName(), JSONBoolean.getInstance((Boolean) dem.isFixed()));
			ret.put(MetaToken.STATIC.getName(), JSONBoolean.getInstance((Boolean) dem.isStatic()));
			ret.put(MetaToken.IGNORE_CASE.getName(), JSONBoolean.getInstance((Boolean) dem.isIgnoreCase()));
			
			JSONArray jsonArray = new JSONArray();
			
			for (int i = 0; i < dem.size(); i++)
			{
				jsonArray.set(i, toJSON(dem.getValue().get(i)));
			}
			
			ret.put(MetaToken.VALUE.getName(), jsonArray);
		}
		
		return ret;
	}

	/**
	 * Converts json to DynamicEnumMap.
	 * @param json
	 * @return
	 */
	public static DynamicEnumMap fromJSONDynamicEnumMap(String json)
	{
		DynamicEnumMap dem = new DynamicEnumMap();
		@SuppressWarnings("deprecation")
		JSONObject jsonObject = (JSONObject) JSONParser.parseLenient(json);
		
		if (jsonObject != null)
		{
			if (jsonObject.get(MetaToken.REFERENCE_ID.getName()) != null
					&& jsonObject.get(MetaToken.REFERENCE_ID.getName()) instanceof JSONString)
			{
				dem.setReferenceID(((JSONString) jsonObject.get(MetaToken.REFERENCE_ID.getName())).stringValue());
			}
			
			if (jsonObject.get(MetaToken.USER_ID.getName()) != null
					&& jsonObject.get(MetaToken.USER_ID.getName()) instanceof JSONString)
			{
				dem.setUserID(((JSONString) jsonObject.get(MetaToken.USER_ID.getName())).stringValue());
			}
			
			if (jsonObject.get(MetaToken.ACCOUNT_ID.getName()) != null
					&& jsonObject.get(MetaToken.ACCOUNT_ID.getName()) instanceof JSONString)
			{
				dem.setAccountID(((JSONString) jsonObject.get(MetaToken.ACCOUNT_ID.getName())).stringValue());
			}
			
			if (jsonObject.get(MetaToken.NAME.getName()) != null
					&& jsonObject.get(MetaToken.NAME.getName()) instanceof JSONString)
			{
				dem.setName(((JSONString) jsonObject.get(MetaToken.NAME.getName())).stringValue());
			}
			
			if (jsonObject.get(MetaToken.DESCRIPTION.getName()) != null
					&& jsonObject.get(MetaToken.DESCRIPTION.getName()) instanceof JSONString)
			{
				dem.setDescription(((JSONString) jsonObject.get(MetaToken.DESCRIPTION.getName())).stringValue());
			}
			
			if (jsonObject.get(MetaToken.IS_FIXED.getName()) != null
					&& jsonObject.get(MetaToken.IS_FIXED.getName()) instanceof JSONBoolean)
			{
				dem.setFixed(((JSONBoolean) jsonObject.get(MetaToken.IS_FIXED.getName())).booleanValue());
			}
			
			if (jsonObject.get(MetaToken.STATIC.getName()) != null
					&& jsonObject.get(MetaToken.STATIC.getName()) instanceof JSONBoolean)
			{
				dem.setStatic(((JSONBoolean) jsonObject.get(MetaToken.STATIC.getName())).booleanValue());
			}
			
			if (jsonObject.get(MetaToken.IGNORE_CASE.getName()) != null
					&& jsonObject.get(MetaToken.IGNORE_CASE.getName()) instanceof JSONBoolean)
			{
				dem.setIgnoreCase(((JSONBoolean) jsonObject.get(MetaToken.IGNORE_CASE.getName())).booleanValue());
			}
			
			if (jsonObject.get(MetaToken.VALUE.getName()) != null
					&& jsonObject.get(MetaToken.VALUE.getName()) instanceof JSONArray)
			{
				JSONArray jsonArray = (JSONArray) jsonObject.get(MetaToken.VALUE.getName());
													
				for (int i = 0; i < jsonArray.size(); i++)
				{
					dem.addEnumValue(toNVPair((JSONObject) jsonArray.get(i)));
				}
			}
		}
		
		return dem;
	}

	/**
	 * Converts List of DynamicEnumMap to JSONObject.
	 * @param list
	 * @return
	 */
	public static JSONObject toJSONDynamicEnumMapList(List<DynamicEnumMap> list)
	{
		JSONObject ret = new JSONObject();
		
		if (list != null && list.size() > 0)
		{
			JSONArray jsonArray = new JSONArray();
			
			for (int i = 0; i < list.size(); i++)
			{
				JSONObject jsonObject = toJSONDynamicEnumMap(list.get(i));
				
				if (jsonObject != null)
				{
					jsonArray.set(i, jsonObject);
				}
			}
			
			ret.put(MetaToken.VALUES.getName(), jsonArray);
		}
		
		return ret;
	}

	/**
	 * Converts json to List of DynamicEnumMap.
	 * @param json
	 * @return
	 */
	public static List<DynamicEnumMap> fromJSONDynamicEnumMapList(String json)
	{
		@SuppressWarnings("deprecation")
		JSONObject jsonObject = (JSONObject) JSONParser.parseLenient(json);
		JSONArray values = (JSONArray) jsonObject.get(MetaToken.VALUES.getName());

		List<DynamicEnumMap> ret = new ArrayList<DynamicEnumMap>();
		
		for (int i = 0; i < values.size(); i++)
		{
			ret.add(fromJSONDynamicEnumMap(values.get(i).toString()));
		}
		
		return ret;
	}
	
	public static String toString (JSONObject jobj)
	{
		 StringBuilder sb = new StringBuilder("{");
		    boolean first = true;
		    String[] keys = jobj.keySet().toArray(new String[0]);
		    for (String key : keys) {
		      if (first) {
		        first = false;
		      } else {
		        sb.append(",");
		      }
		      sb.append(JsonUtils.escapeValue(key));
		      sb.append(":");
		      sb.append(jobj.get(key));
		    }
		    sb.append("}");
		    return sb.toString();
	}
	
	
	public static String toString (NVEntity nve)
	{
		return toString(toJSON(nve));
	}

	public static String toString(Map<String, ?> map) {
	    return toString(toJSONMap(map));
    }
	
	public static JSONArray toJSONGenericMapList(NVGenericMapList nvgml, boolean printClass)
	{
		JSONArray jsonArray = new JSONArray();
		if (nvgml != null)
		{
			int counter = 0;
			for (NVGenericMap nvp : nvgml.getValue())
			{
				if (nvp != null)
				{
					jsonArray.set(counter++, toJSONGenericMap(nvp, printClass));
				}
			}
		}
		
		return jsonArray;
	}
	public static JSONArray toJSONStringList(NVStringList nvsl)
	{
		JSONArray jsonArray = new JSONArray();
		
		int counter = 0;
		for (String nvp : nvsl.getValue())
		{
			if (nvp != null)
			{
				jsonArray.set(counter++, new JSONString(nvp));
			}
		}
		
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONValue nvbToJSONArray(NVBase<?> nvb)
	{
		JSONArray jsonArray = new JSONArray();
		int counter = 0;
		//Class<?> metaBase = nvc.getMetaTypeBase();
		
		if (nvb instanceof NVPairList || nvb instanceof  NVPairGetNameMap || nvb instanceof NVGetNameValueList)
		{
			ArrayValues<NVPair> values = (ArrayValues<NVPair>) nvb;

			for (NVPair nvp : values.values())
			{
				JSONObject nvpJSON = toJSON(nvp);
				
				if (nvpJSON != null)
				{
					jsonArray.set(counter++, nvpJSON);
				}
			}
		}
		else if (nvb instanceof NVEntityReferenceList
				|| nvb instanceof NVEntityReferenceIDMap
				|| nvb instanceof NVEntityGetNameMap)
		{
			ArrayValues<NVEntity> values = (ArrayValues<NVEntity>) nvb;

			for (NVEntity nveTemp : values.values())
			{
				if (nveTemp != null)
				{
					jsonArray.set(counter++, toJSON(nveTemp, true));
				}
			}
		}
		else if (nvb instanceof NVLongList)
		{
			NVLongList values = (NVLongList) nvb;

			for (Long val : values.getValue())
			{
				if (val != null)
				{
					jsonArray.set(counter++, new JSONNumber((double)val));
				}
			}
		}
		else if (nvb instanceof NVIntList)
		{
			NVIntList values = (NVIntList) nvb;

			for (Integer val : values.getValue())
			{
				if (val != null)
				{
					jsonArray.set(counter++, new JSONNumber((double)val));
				}
			}
		}
		else if (nvb instanceof NVFloatList)
		{
			NVFloatList values = (NVFloatList) nvb;

			for (Float val : values.getValue())
			{
				if (val != null)
				{
					jsonArray.set(counter++, new JSONNumber((double)val));
				}
			}
		}
		else if (nvb instanceof NVDoubleList)
		{
			NVDoubleList values = (NVDoubleList) nvb;

			for (Double val : values.getValue())
			{
				if (val != null)
				{
					jsonArray.set(counter++, new JSONNumber((double)val));
				}
			}
		}
		else if (nvb instanceof NVBigDecimalList)
		{
			NVBigDecimalList values = (NVBigDecimalList) nvb;

			for (BigDecimal val : values.getValue())
			{
				if (val != null)
				{
					jsonArray.set(counter++, new JSONNumber(val.doubleValue()));
				}
			}
		}
		else if (nvb instanceof NVEnumList)
		{
			NVEnumList values = (NVEnumList) nvb;

			for (Enum<?> e : values.getValue())
			{
				if (e != null)
				{
					jsonArray.set(counter++, new JSONString(e.name()));
				}
			}
		}
		else if (nvb.getValue() != null && nvb.getValue() instanceof byte[])
		{
			return new JSONString(SharedBase64.encodeAsString(Base64Type.URL,((NVBlob)nvb).getValue()));		
		}
		
		if (jsonArray.size() > 0)
			return jsonArray;
		
		return null;
		
	}
	
	
	private static void fromJSON(JSONArray jsonArray, NVStringList nvsl)
	{
		if(jsonArray != null)
		{
			for (int i = 0; i < jsonArray.size(); i++)
			{
				nvsl.getValue().add(((JSONString)jsonArray.get(i)).stringValue());
			}
		}
	}
	private static void fromJSON(JSONArray jsonArray, NVGenericMapList nvsl, NVEntityFactory nveFactory)
	{
		if(jsonArray != null)
		{
			for (int i = 0; i < jsonArray.size(); i++)
			{
				nvsl.getValue().add(fromJSONGenericMap((JSONObject)jsonArray.get(i), nveFactory));
			}
		}
	}
	
	
	private static NVBase<?> guessNVBaseArray(JSONArray ja)
	{
		NVBase<?> ret = null;
		
		GNVType guess = null;
		for (int i=0; i < ja.size(); i++)
		{	
			JSONValue je = ja.get(i);
			
			if (je.isObject() != null)
			{
				// could an NVEntity or NVPairList or NVGnericMap
				// nvpair
				JSONObject jo  = je.isObject();
				if (jo.size() == 1)
				{
					
					if (ret == null)
					{
						return  new NVPairList(null, new ArrayList<NVPair>());
					}
				}
				
				if (jo.size()>1)
				{
					return new NVGenericMapList();
					
				}
			}
			else if (je.isString() != null)
			{
				return new NVStringList();
			}
			else if (je.isNumber()!=null)
			{
				
				
				GNVType gnv = GNVType.toGNVType(je.isNumber().doubleValue());
				if (gnv != null)
				{
					if (guess == null)
					{
						guess = gnv;
					}
					else
					{
						switch(gnv)
						{
						
						case NVDOUBLE:
							if (guess == GNVType.NVINT || guess == GNVType.NVLONG || guess == GNVType.NVFLOAT)
							{
								guess = gnv;
							}
							break;
						case NVFLOAT:
							if (guess == GNVType.NVINT || guess == GNVType.NVLONG)
							{
								guess = gnv;
							}
							break;
						case NVINT:
							break;
						case NVLONG:
							if (guess == GNVType.NVINT)
							{
								guess = gnv;
							}
							break;
						default:
							break;
						
						}
					}
				}
			}
		}
		
		if (ret == null && guess != null)
		{
			switch(guess)
			{
			
			case NVDOUBLE:
				ret = new NVDoubleList(null, new ArrayList<Double>());
				break;
			case NVFLOAT:
				ret = new NVFloatList(null, new ArrayList<Float>());
				break;
			case NVINT:
				ret = new NVIntList(null, new ArrayList<Integer>());
				break;
			case NVLONG:
				ret = new NVLongList(null, new ArrayList<Long>());
				break;
			default:
				break;
			
			}
		}
		
		
		return ret;
	}
	
	
	public static NVBase<?> guessPrimitive(String name, JSONValue jp)
	{
				
		if (jp.isBoolean() != null)
		{
			return new NVBoolean(name, jp.isBoolean().booleanValue());
		}
		else if (jp.isNumber() != null)
		{
			String isNumber = ""+jp.isNumber().doubleValue();
			// if there is no dots it should be a 
			if (isNumber.indexOf(".") == -1)
			{
				try
				{
					return new NVLong(name, Long.parseLong(isNumber));
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					return new NVDouble(name, jp.isNumber().doubleValue());
				}
				catch(NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if (jp.isString() != null)
		{
			
			
//			Causing conversion errors
			try
			{
				byte value[] = SharedBase64.decodeWrappedAsString(jp.isString().stringValue());
				return new NVBlob(name, value);
			}
			catch(Exception e)
			{
				
			}
			
			String value = jp.isString().stringValue();
			if (DateTimeUtil.SINGLETON.isValid(value))
			{
				return new NVLong(name, DateTimeUtil.SINGLETON.validate(value));
			}
			
			return new NVPair(name, value);
		}
		
		return null;
		
	}
}