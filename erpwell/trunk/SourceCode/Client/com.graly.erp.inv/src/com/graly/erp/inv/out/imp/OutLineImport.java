package com.graly.erp.inv.out.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.InvErrorLog;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.erp.sal.client.SALManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class OutLineImport {
	private static final Logger logger = Logger.getLogger(OutLineImport.class);
	private List<Map<String, List<TxtMode>>> listMap = new ArrayList<Map<String, List<TxtMode>>>();
	private List<InvErrorLog> errorLogs = new ArrayList<InvErrorLog>();
	private List<MovementOut> moutList = new ArrayList<MovementOut>();
	private INVManager invManager;
	private ADManager adManager;
	private Lot lot = null;

	public OutLineImport(String url) {
		getMovtmentList(url);
	}

	protected void getMovtmentList(String fileURL) {
		Date now = Env.getSysDate();
		listMap = readTxt(fileURL);
		if(listMap == null || listMap.size() == 0){
			return;
		}
		for (Map<String, List<TxtMode>> map : listMap) {
			String salesOrderId = null;
			if(map.size()==0){
				InvErrorLog errorLog = new InvErrorLog();
				errorLog.setErrorDate(now);
				errorLog.setErrorMessage(Message.getString("inv_imp_file_not"));
				errorLogs.add(errorLog);
				return;
			}
			try {
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					salesOrderId = entry.getKey().toString();
					MovementOut out = getMovtmentById(salesOrderId);
					if (out == null) {
						break;
					}

					List<MovementLine> outLineList = out.getMovementLines(); // 获得出库单行
					List<TxtMode> txtModeList = (List<TxtMode>) entry.getValue();
					List<Lot> lotList = new ArrayList();
					for (TxtMode mode : txtModeList) {
						lotList.add(getLotListById(mode.getLotId(), mode.getLotQty()));
					}
					if (outLineList.size() != 0) {
						for (MovementLine movementLine : outLineList) {
							BigDecimal qtyOut = movementLine.getQtyMovement();
							BigDecimal qtyLineLot = BigDecimal.ZERO;
							List<Lot> currentLineLots = new ArrayList<Lot>();
							for (Lot lot : lotList) {
								if (lot.getMaterialRrn().equals(movementLine.getMaterialRrn())) {
									currentLineLots.add(lot);
									qtyLineLot = qtyLineLot.add(lot.getQtyTransaction());
								}
							}

							if (qtyOut.compareTo(qtyLineLot) != 0) {
								InvErrorLog errorLog = new InvErrorLog();
								errorLog.setErrorDate(now);
								errorLog.setErrorMessage(Message.getString("inv_imp_file_enough"));
								errorLog.setLotId(lot.getLotId());
								errorLog.setSalesOrderId(salesOrderId);
								errorLogs.add(errorLog);
								continue;
							}

							List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
							for (Lot lot : currentLineLots) {
								Warehouse wh = getOutWarehouse(out);
								if (wh == null) {
									InvErrorLog errorLog = new InvErrorLog();
									errorLog.setErrorDate(now);
									errorLog
											.setErrorMessage(Message
													.getString("inv.batch_must_be_select_warehouse_first"));
									errorLog.setLotId(lot.getLotId());
									errorLog.setSalesOrderId(salesOrderId);
									errorLogs.add(errorLog);
									continue;
								}
								if (validateLotOnHand(lot, wh)) {
									MovementLineLot lineLot = pareseMovementLineLot(out, movementLine, lot);
									lineLots.add(lineLot);
								} else {
									InvErrorLog errorLog = new InvErrorLog();
									errorLog.setErrorDate(now);
									errorLog.setErrorMessage(Message.getString("inv.inventory_shortage"));
									errorLog.setLotId(lot.getLotId());
									errorLog.setSalesOrderId(salesOrderId);
									errorLogs.add(errorLog);
									continue;
								}
							}
							movementLine.setMovementLots(lineLots);
						}
					} else {
						InvErrorLog errorLog = new InvErrorLog();
						errorLog.setErrorDate(now);
						errorLog.setErrorMessage(Message.getString("inv.movementLine_not"));
						errorLog.setLotId(lot.getLotId());
						errorLog.setSalesOrderId(salesOrderId);
						errorLogs.add(errorLog);
						continue;
					}
					out.setMovementLines(outLineList);
					invManager.saveMovementOutLine(out, outLineList,getOutType(), Env.getUserRrn());
					moutList.add(out);
				}
			} catch (Exception e) {
				InvErrorLog errorLog = new InvErrorLog();
				errorLog.setErrorDate(now);
				errorLog.setErrorMessage(e.getMessage());
				if(lot==null){
					errorLog.setLotId(" ");
				}
				
				errorLog.setSalesOrderId(salesOrderId);
				errorLogs.add(errorLog);
			}
		} 
		
	}

	protected MovementLineLot pareseMovementLineLot(MovementOut out, MovementLine line, Lot lot) {
		Date now = Env.getSysDate();
		MovementLineLot outLineLot = new MovementLineLot();
		outLineLot.setOrgRrn(Env.getOrgRrn());
		outLineLot.setIsActive(true);
		outLineLot.setCreated(now);
		outLineLot.setCreatedBy(Env.getUserRrn());
		outLineLot.setUpdated(now);
		outLineLot.setUpdatedBy(Env.getUserRrn());

		if (out != null) {
			outLineLot.setMovementRrn(out.getObjectRrn());
			outLineLot.setMovementId(out.getDocId());
		}
		outLineLot.setMovementLineRrn(line.getObjectRrn());
		outLineLot.setLotRrn(lot.getObjectRrn());
		outLineLot.setLotId(lot.getLotId());
		outLineLot.setMaterialRrn(lot.getMaterialRrn());
		outLineLot.setMaterialId(lot.getMaterialId());
		outLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		outLineLot.setQtyMovement(lot.getQtyTransaction());
		return outLineLot;
	}

	protected Lot getLotListById(String lotId, BigDecimal qtyTransition) throws Exception {
		try {
			if (lotId != null && !"".equals(lotId)) {
				if (invManager == null)
					invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if (lot == null || lot.getMaterialRrn() == null) {
					UI.showError(Message.getString("inv.lotnotexist"));
					return null;
				}

				lot.setQtyTransaction(qtyTransition);

				if (lot.getIsUsed()
						|| Lot.POSITION_OUT.equals(lot.getPosition())) {
					UI.showError(String.format(Message
							.getString("wip.lot_is_used"), lot.getLotId()));
					return null;
				}
				if (!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
					if (!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						UI.showError(String.format(Message
								.getString("inv.lot_not_in"), lot.getLotId()));
						return null;
					}
				}
				// // 如果l不为null，表示lot所对应的物料在lines中或与outLine对应的物料一致
				// MovementLine l = this.isContainsLot(lot);
				// if(l == null) {
				// return lot;
				// }
			}
			return lot;
		} catch (Exception e) {
			throw e;
		}
	}

	private boolean validateLotOnHand(Lot lot, Warehouse wh) {
		try {
			BigDecimal qtyOnhand = BigDecimal.ZERO;
			BigDecimal qtyTransition = lot.getQtyTransaction();
			if (wh != null || lot != null) {
				if (invManager == null)
					invManager = Framework.getService(INVManager.class);
				qtyOnhand = invManager
						.getLotStorage(Env.getOrgRrn(), lot.getObjectRrn(),
								wh.getObjectRrn(), Env.getUserRrn())
						.getQtyOnhand();
			}
			if (BigDecimal.ZERO.compareTo(qtyTransition) == 0) {
				return false;
			}
			if (qtyOnhand != null
					&& (qtyOnhand.add(qtyTransition.negate()).compareTo(
							BigDecimal.ZERO) >= 0 || qtyOnhand.add(
							qtyTransition.negate()).compareTo(qtyOnhand) >= 0)) {// 或者是出库后库存增加(核销时)
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected Warehouse getOutWarehouse(MovementOut out) {
		try {
			if (out == null || out.getWarehouseRrn() == null)
				return null;
			Warehouse wh = new Warehouse();
			wh.setObjectRrn(out.getWarehouseRrn());
			if (adManager == null)
				adManager = Framework.getService(ADManager.class);
			wh = (Warehouse) adManager.getEntity(wh);
			return wh;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected MovementOut getMovtmentById(String serialNumber) {
		MovementOut out = null;
		Date now = Env.getSysDate();
		try {
			SALManager salManager = Framework.getService(SALManager.class);
			out = salManager.createMovementOutFromSo(Env.getOrgRrn(),
					serialNumber, Env.getUserRrn());
			INVManager invManager = Framework.getService(INVManager.class);
			out = invManager.saveMovementOutLine(out, out.getMovementLines(),
					MovementOut.OutType.SOU, Env.getUserRrn());
		} catch (Exception e) {
			logger.error("SaleOrderQueryDialog : getMovementOutBySaleOrder()",
					e);
			InvErrorLog errorLog = new InvErrorLog();
			errorLog.setErrorDate(now);
			errorLog.setErrorMessage(Message.getString("sal.so_not_found"));
			errorLog.setLotId("");
			errorLog.setSalesOrderId(serialNumber);
			errorLogs.add(errorLog);
		}

		return out;
	}

	protected List<Map<String, List<TxtMode>>> readTxt(String url) {
		try {
			File file = new File(url);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String temp = "";
			List<String[]> list = new ArrayList<String[]>();
			String line = null;
			List<Map<String, List<TxtMode>>> list3 = new ArrayList<Map<String, List<TxtMode>>>();
			while ((line = br.readLine()) != null) {
				temp += line;
			}
			String[] str1 = temp.split("-");
			for (int i = 0; i <= str1.length - 1; i++) {
				String[] str2 = str1[i].split(",");
				list.add(str2);
			}
			for (String[] str3 : list) {
				List<TxtMode> list2 = new ArrayList<TxtMode>();
				Map<String, List<TxtMode>> map = new HashMap<String, List<TxtMode>>();
				for (int i = 0; i <= str3.length - 1; i++) {
					if (i != 0) {
						String[] str4 = str3[i].split("#");
						TxtMode mode = new TxtMode();
						mode.setLotId(str4[0]);
						BigDecimal bgid = new BigDecimal(str4[1]);
						mode.setLotQty(bgid);
						list2.add(mode);
						map.put(str3[0], list2);
					}
				}
				list3.add(map);
			}
			br.close();
			fr.close();
			return listMap = list3;
		} catch (FileNotFoundException e) {
			ExceptionHandlerManager.asyncHandleException(e);
		} catch (IOException e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	public List<MovementOut> getMoutList() {
		return moutList;
	}

	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.SOU;
	}

	public List<InvErrorLog> getErrorLogs() {
		return errorLogs;
	}

	public void setErrorLogs(List<InvErrorLog> errorLogs) {
		this.errorLogs = errorLogs;
	}

}
