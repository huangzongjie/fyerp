package com.graly.erp.wip.mo.material_standtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.graly.erp.pdm.model.Bom;
import com.graly.framework.base.ui.util.Message;

public class BomConstant {
	public static final String MATERIAL_ID = Message.getString("pdm.material_id");
	public static final String NAME = Message.getString("pdm.material_name");
	public static final String MATERIAL_UOM = Message.getString("pdm.material_uom");
	public static final String UNIT_QTY = Message.getString("pdm.material_qtyunit");
	public static final String COMMENTS = Message.getString("pdm.material_comments");
	public static final String VOLUME = Message.getString("pdm.material_volume");
	public static final String STAND_TIME_BOM = "标准工时";//外购物料预处理
	public static final String PROCESS_NAME = "工艺";//外购物料预处理
	
	public static final String[] ColumnHeaders = new String[]{
		MATERIAL_ID, 
		NAME, 
		MATERIAL_UOM, 
		UNIT_QTY,
		VOLUME,
		COMMENTS,
		STAND_TIME_BOM,
		PROCESS_NAME};
	
	public static final String BOM_NEW = "New";
	public static final String BOM_EDIT = "Edit";
	
	public static final String MT_MATERIALID = "materialId";
	public static final String MT_NAME = "name";
	public static final String TABLE_NAME_BOM = "PDMBom";
	public static final String TABLE_NAME_OPTIONAL = "PDMOptional";
	public static final String TABLE_NAME_ALTERNAE = "PDMAlternate";
	
	public static List<Bom> firstLevelBoms;
	public static HashMap<String, String> bomPath;
	
	public static void setFirstLevelBom(Bom bom) {
		if(bom != null) {
			firstLevelBoms.add(bom);
		}
	}
	
	public static boolean isContains(Bom bom) {
		if(firstLevelBoms.contains(bom)) {
			return true;
		}
		return false;
	}

	public static void addAllFristLevelBoms(List<Bom> list) {
		firstLevelBoms  = new ArrayList<Bom>();
		firstLevelBoms.addAll(list);
	}
	
	public static void initBomPath() {
		bomPath = new LinkedHashMap<String, String>();
	}
	
	public static void createPath(String parentManterilRrn, List<Bom> childrenBoms) {
		String parentPath = null;
		if(bomPath.containsKey(parentManterilRrn)) {
			parentPath = bomPath.get(parentManterilRrn);
		} else {
			if(parentManterilRrn != null && !"".equals(parentManterilRrn.trim())) {
				parentPath = parentManterilRrn;
			}
		}
		if(parentPath != null) {
			StringBuffer sb = null;
			for(Bom bom : childrenBoms) {
				sb = new StringBuffer(parentPath);
				String childMaterialRrn = bom.getChildRrn().toString();
				sb.append("/" + childMaterialRrn);
				bomPath.put(childMaterialRrn, sb.toString());
			}
		}
	}
	
	public static String getBomPath(String materialId) {
		return bomPath.get(materialId);
	}
}
