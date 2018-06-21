package com.graly.erp.inv.adjust.out;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.erp.inv.out.OutLineLotSection;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class AdjustOutLineLotSection extends OutLineLotSection {
	private static final Logger logger = Logger.getLogger(AdjustOutLineLotSection.class);
	
	public AdjustOutLineLotSection(ADTable adTable, MovementOut out,
			MovementLine outLine, List<MovementLine> lines,
			OutLineLotDialog olld, boolean isView){
		super(adTable, out,	outLine, lines, olld, isView);
	}
	
	protected void addLot(String lotId){
		try {			
			if(lotId != null && !"".equals(lotId)) {
				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				
				//调整的不判断批次的位置
//				if (lot.getIsUsed() || Lot.POSITION_OUT.equals(lot.getPosition())) {
//					UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
//					return;
//				}
				
//				if(!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
//					if(!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//						UI.showError(String.format(Message.getString("inv.lot_not_in"), lot.getLotId()));
//						return;						
//					}
//				}
				// 如果l不为null，表示lot所对应的物料在lines中或与outLine对应的物料一致
				MovementLine l = this.isContainsLot(lot);
				if(l == null) {
					return;
				}
				
				MovementLineLot lineLot = null;
				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
						|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					Warehouse wh = getOutWarehouse();
					if(wh == null) {
						UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
						return;
					}
					OutQtySetupDialog outQtyDialog = new AdjustOutQtySetupDialog(UI.getActiveShell(), outLine, lot, wh);
					int openId = outQtyDialog.open();
					if(openId == Dialog.OK) {
						lineLot = pareseMovementLineLot(l, outQtyDialog.getInputQty(), lot);
					} else if(openId == Dialog.CANCEL) 
						return;
				} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
					lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot);
				}
				if(contains(lineLot)) {
					UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
					return;
				}
				getLineLots().add(lineLot);						
				refresh();
				setDoOprationsTrue();
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at AdjustOutLineLotSection ：addLot(String lotId) ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}else{
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} finally {
			txtLotId.selectAll();
		}
	}
	
	@Override
	protected void saveAdapter() {
		try {
			if(validateAll()) {
				INVManager invManager = Framework.getService(INVManager.class);
				invManager.saveMovementOutLine(out, lines, getOutType(), Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				this.setIsSaved(true);
				((OutLineLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
			}
		} catch (Exception e) {
			logger.error("Error at AdjustOutLineLotSection : saveAdapter() ", e);
		}	
	}
	
	//  重载getOutType(), 使出库的参数类型为OutType.OOU
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.ADOU;
	}
}
