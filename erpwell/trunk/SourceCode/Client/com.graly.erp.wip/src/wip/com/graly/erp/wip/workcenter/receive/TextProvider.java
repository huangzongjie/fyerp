package com.graly.erp.wip.workcenter.receive;

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
	public static String FieldName_CurrentQty = "qtyCurrent";
	public static String FieldName_DelayReason = "delayReason";//延误原因
	public static String FieldName_DelayReasonDetail = "delayReasonDetail";//延误原因内容
	public static String FieldName_Mps_Line_Delivery_Rrn = "mpsLineDeliveryRrn";//主计划通知
	public static String FieldName_DelayDept = "delayDept";//延误部门
	
	
	public static String FieldName_StorageQty = "qtyOnHand";
	public static String FieldName_TransactionQty = "qtyTransit";
	public static String FieldName_LineWipQty = "qtyMoLineWip";
	public static String FieldName_AllocationQty = "qtyAllocation";
	// 创建工作令各个DocLine列表栏位(右部为Chart)
	public static String FieldName_ProductQty = "qty";
	public static String FieldName_FinishedQty = "qtyReceive";
	public static String FieldName_NeedQty = "qtyNeed";
	public static String FieldName_DateStart = "dateStart";
	public static String FieldName_DateEnd = "dateEnd";
	public static String FieldName_LineStatus = "lineStatus";
	public static String FieldName_StandTime = "standTime";
	public static String FieldName_TotalTime = "totalTime";		//共需工时
	
	public static String FieldName_UserQc = "userQc";
	public static String FieldName_EquipmentRrn = "equipmentRrn";
	public static String FieldName_EquipmentId = "equipmentId";
	public static String FieldName_MoldRrn = "moldRrn";
	public static String FieldName_MoldId = "moldId";
	public static String FieldName_Comments = "lotComment";
	
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
	
	public static String StorageQty = Message.getString("inv.qty_storage");
	public static String TransationQty = Message.getString("inv.qty_transaction");
	public static String LineWipQty = Message.getString("inv.qty_line_wip");
	public static String AllocationQty = Message.getString("inv.qty_allocation");
}
