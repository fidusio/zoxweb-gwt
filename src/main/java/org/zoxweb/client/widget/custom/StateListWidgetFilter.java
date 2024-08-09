package org.zoxweb.client.widget.custom;

import org.zoxweb.client.widget.NVBaseWidget;
import org.zoxweb.client.widget.NVDynamicEnumWidget;
import org.zoxweb.client.widget.NVStringWidget;
import org.zoxweb.shared.data.AddressDAO;
import org.zoxweb.shared.data.DataConst;
import org.zoxweb.shared.filters.ValueFilter;
import org.zoxweb.shared.util.Const;
import org.zoxweb.shared.util.NVPair;

/**
 * The state list widget filter validates country with associated states/provinces.
 * Example: For country U.S.A. returns a widget containing a list of U.S. states.
 * @author mzebib
 */
@SuppressWarnings("serial")
public class StateListWidgetFilter 
	implements ValueFilter<String, NVBaseWidget<?>>
{

	public static final StateListWidgetFilter SINGLETON = new StateListWidgetFilter();

	public static final String USA_NAME = "USA";
    public static final String CANADA_NAME = "CAN";

	private StateListWidgetFilter()
	{
		
	}
	
	@Override
	public String toCanonicalID() 
	{
		return null;
	}

	@Override
	public NVBaseWidget<?> validate(String in)
			throws NullPointerException, IllegalArgumentException
	{
		
		NVBaseWidget<?> ret = null;
		NVPair country = DataConst.COUNTRIES.lookup(in);
		
		if (country != null && USA_NAME.equals(country.getName()))
		{
			NVDynamicEnumWidget nvdew =  new NVDynamicEnumWidget(DataConst.US_STATES.getName(), DataConst.US_STATES, Const.NVDisplayProp.NAME_VALUE, false);
			nvdew.setEditVisible(false);
			nvdew.setWidgetSize(15, 2);
			ret = nvdew;
			ret.setSize("100%", "2EM");
		}
		else if (country != null && CANADA_NAME.equals(country.getName()))
		{
			NVDynamicEnumWidget nvdew = new NVDynamicEnumWidget(DataConst.CANADIAN_PROVINCES.getName(), DataConst.CANADIAN_PROVINCES, Const.NVDisplayProp.NAME_VALUE, false);
			nvdew.setEditVisible(false);
			nvdew.setWidgetSize(15, 2);
			ret = nvdew;
			ret.setSize("100%", "2EM");
		}
		else
		{
			ret = new NVStringWidget(AddressDAO.Param.STATE_PROVINCE.getNVConfig());
			ret.setSize("100%", "1.5EM");
		}
		
		return ret;
	}


	@Override
	public boolean isValid(String in) 
	{
		try
		{
			validate(in);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

}