package com.graly.framework.base.ui.validator;

import java.util.HashMap;
import com.graly.framework.base.ui.util.I18nUtil;

public class ValidatorFactory {
	
	public static boolean isValid(String dataType, String value){
		if (DataType.INTEGER.equalsIgnoreCase(dataType)){
			return GenericValidator.isInt(value);
		} else if (DataType.DOUBLE.equalsIgnoreCase(dataType)){
			return GenericValidator.isDouble(value);
		} else if (DataType.DATE.equalsIgnoreCase(dataType)){
			return GenericValidator.isDate(value, I18nUtil.getDefaultDatePattern(), true);
		} else if (DataType.TIME.equalsIgnoreCase(dataType)){
			return GenericValidator.isDate(value, I18nUtil.getDefaultTimePattern(), true);
		} else if (DataType.EMAIL.equalsIgnoreCase(dataType)){
			return GenericValidator.isEmail(value);
		} else if (DataType.URL.equalsIgnoreCase(dataType)){
			return GenericValidator.isUrl(value);
		}
		return true;
	}
	
	public static boolean isInRange(String dataType, String value, String sMin, String sMax){
		if ("integer".equalsIgnoreCase(dataType)){
			if (!GenericValidator.isInt(value)){
				return false;
			} else {
				try{
				int min = Integer.MIN_VALUE;
				int max = Integer.MAX_VALUE;
				if (sMin != null && !"".equalsIgnoreCase(sMin.trim())){
					min = Integer.valueOf(sMin);
				}
				if (sMax != null && !"".equalsIgnoreCase(sMax.trim())){
					max = Integer.valueOf(sMax);
				}
				return GenericValidator.isInRange(Integer.valueOf(value), min , max);
				} catch (Exception e){
					return false;
				}
			}
		} else if ("double".equalsIgnoreCase(dataType)){
			if (!GenericValidator.isDouble(value)){
				return false;
			} else {
				try{
				double min = -10000000000D;
				double max = Double.MAX_VALUE;
				if (sMin != null && !"".equalsIgnoreCase(sMin.trim())){
					min = Double.valueOf(sMin);
				}
				if (sMax != null && !"".equalsIgnoreCase(sMax.trim())){
					max = Double.valueOf(sMax);
				}
				return GenericValidator.isInRange(Double.valueOf(value), min , max);
				} catch (Exception e){
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isBlankOrNull(String value) {
		return GenericValidator.isBlankOrNull(value);
	}
}
