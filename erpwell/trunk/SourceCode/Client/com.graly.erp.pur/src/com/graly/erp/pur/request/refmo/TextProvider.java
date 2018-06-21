package com.graly.erp.pur.request.refmo;

import com.graly.framework.base.ui.util.Message;

public class TextProvider {
	// 接收子工作令列表栏位
	public static String FieldName_MaterialId = "materialId";
	public static String FieldName_MaterialName = "materialName";
	public static String FieldName_LotId = "lotId";
	public static String FieldName_BatchQty = "batchQty";
	public static String FieldName_LotType = "lotType";
	public static String FieldName_UnitQty = "unitQty";
	public static String FieldName_UsedQty = "qtyCurrent";
	public static String FieldName_ConsumeQty = "qtyTransaction";
//	public static String FieldName_CurrentQty = "qtyCurrent";
	// 创建工作令各个DocLine列表栏位(右部为Chart)
	public static String FieldName_ProductQty = "qty";
	public static String FieldName_FinishedQty = "qtyReceive";
	public static String FieldName_NeedQty = "qtyNeed";
	public static String FieldName_DateStart = "dateStart";
	public static String FieldName_DateEnd = "dateEnd";
	public static String FieldName_LineStatus = "lineStatus";
	
	public static String MaterialId = Message.getString("pdm.material_id");
	public static String MaterialName = Message.getString("pdm.material_name");
	public static String LotType = Message.getString("wip.lot_type");
	public static String TotalQty = Message.getString("wip.lot_qty");
	public static String LotId = Message.getString("inv.lotid");
	public static String CurrentQty = Message.getString("inv.lot_qty");
	public static String UsedQty = Message.getString("inv.lot_used_qty");
	
	public static String ProductQty = Message.getString("wip.product_qty");
	public static String FinishedQty = Message.getString("wip.finished_qty");
	public static String NeedQty = Message.getString("wip.need_qty");
	public static String DateStart = Message.getString("wip.start_date");
	public static String DateEnd = Message.getString("wip.end_date");
}
