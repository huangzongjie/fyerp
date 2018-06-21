package com.graly.erp.inv.adjust.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.model.InvErrorLog;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutSection;
import com.graly.erp.inv.out.imp.ErrorLogDisplayDialog;
import com.graly.erp.inv.out.imp.OutLineImport;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AdjustOutSection extends OutSection {
	private static final Logger logger = Logger
			.getLogger(AdjustOutSection.class);
	private static final String TABLE_NAME = "INVMovementOutOtherLine";
	private ADTable adTable;
	protected ToolItem itemByLotOut;
	protected ToolItem itemImport;
	private INVManager invManager;
	private PDMManager pdmManager;

	public AdjustOutSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemByLotOut(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemImport(tBar);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	protected void newAdapter() {
		String where = " 1!=1 ";
		MovementOut out = new MovementOut();
		out.setOrgRrn(Env.getOrgRrn());
		AdjustOutLineBlockDialog olbd = new AdjustOutLineBlockDialog(UI
				.getActiveShell(), this.getTableManager().getADTable(), where,
				out, getADTableOfPOLine());
		if (olbd.open() == Dialog.CANCEL) {
			out = (MovementOut) olbd.getParentObject();
			if (out != null && out.getObjectRrn() != null) {
				selectedOut = out;
				refreshSection();
				refreshAdd(selectedOut);
			}
			// refreshSection();
		}
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog = new MaterialWCAndInvoiceQueryDialog(UI
					.getActiveShell(), tableManager, this,
					Documentation.DOCTYPE_ADOU);
			queryDialog.open();
		}
	}

	protected ADTable getADTableOfPOLine() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}

	protected void createToolItemByLotOut(ToolBar tBar) {
		itemByLotOut = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_ADOU_LOTOUT);
		itemByLotOut.setText(Message.getString("inv.by_lot_out"));
		itemByLotOut.setImage(SWTResourceCache.getImage("barcode"));
		itemByLotOut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				outByLotAdapter();
			}
		});
	}

	protected void outByLotAdapter() {
		ByLotOutDialog olbd = new ByLotOutDialog(UI.getActiveShell());
		if (olbd.open() == Dialog.CANCEL) {
			MovementOut out = ((ByLotOutSection) olbd.getLotMasterSection())
					.getMovementOut();
			if (out != null && out.getObjectRrn() != null) {
				this.selectedOut = out;
				if (selectedOut != null && selectedOut.getObjectRrn() != null)
					refreshAdd(selectedOut);
				editAdapter();
			}
		}
	}

	protected void editAdapter() {
		try {
			if (selectedOut != null && selectedOut.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedOut = (MovementOut) adManager.getEntity(selectedOut);
				String whereClause = (" movementRrn = '"
						+ selectedOut.getObjectRrn().toString() + "' ");
				AdjustOutLineBlockDialog cd = new AdjustOutLineBlockDialog(UI
						.getActiveShell(), this.getTableManager().getADTable(),
						whereClause, selectedOut, getADTableOfPOLine());
				if (cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedOut);
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OutSection : editAdapter() " + e);
		}
	}

	@Override
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_ADOU_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	@Override
	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_ADOU_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	@Override
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_ADOU_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void createToolItemImport(ToolBar tBar) {
		itemImport = new AuthorityToolItem(tBar, SWT.PUSH,
				Constants.KEY_ADOU_NEW);
		itemImport.setText(Message.getString("oou.import"));
		itemImport.setImage(SWTResourceCache.getImage("export"));
		itemImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter();
			}
		});
	}

	@Override
	protected void importAdapter() {
		try {
			FileDialog fileDialog = new FileDialog(UI.getActiveShell(),
					SWT.OPEN);
			// 设置初始路径
			fileDialog.setFilterPath("C:/");
			// 设置扩展名过滤
			String[] filterExt = { "*.xls;*.xlsx" };
			fileDialog.setFilterExtensions(filterExt);
			// 打开文件对话框，返回选择的文件
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				if (!selectedFile.contains(".xls")) {
					UI.showWarning(Message
							.getString("ppm.upload_file_type_not_support"));
					return;
				} else {

					File file = new File(selectedFile);

					HSSFWorkbook workbook = new HSSFWorkbook(
							new FileInputStream(file));
					// 获得第一张sheet
					HSSFSheet sheet = workbook.getSheetAt(0);
					// 获得sheet总行数
					int rowCount = sheet.getLastRowNum();
					logger.info("found excel rows count:" + rowCount);
					if (rowCount < 1) {
						return;
					}

					Map<String,List<MovementLine>> maps = new HashMap<String,List<MovementLine>>();
					for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {//从第二行开始遍历,第一行是标题
						// 获得行对象
						HSSFRow row = sheet.getRow(rowIndex);
						if (row != null) {
							String key = getCellString(row.getCell(2))+"_出库调整_"+getCellString(row.getCell(3));
							List<MovementLine> lines = null;
							if(maps.containsKey(key)){
								lines = maps.get(key);
							}else{
								lines = new ArrayList<MovementLine>();
							}
							MovementLine ml = new MovementLine();
							ml.setOrgRrn(Env.getOrgRrn());
							List<String> rowData = new ArrayList<String>();
							// 获得本行中单元格的个数
							int cellCount = row.getLastCellNum();
							//物料编号
							ml.setMaterialId(getCellString(row.getCell(0)));
							if(pdmManager == null){
								pdmManager = Framework.getService(PDMManager.class);
							}
							
							List<Material> mlst = pdmManager.getMaterialById(ml.getMaterialId(), Env.getOrgRrn());
							if(mlst != null && mlst.size() > 0){
								Material material = mlst.get(0);
								ml.setMaterialRrn(material.getObjectRrn());
							}
							//物料名称
							ml.setMaterialName(getCellString(row.getCell(1)));
							//仓库
							//相关单位
							//数量
							ml.setQtyMovement(new BigDecimal(getCellString(row.getCell(4))));
							lines.add(ml);
							ml.setLineNo(10L*(1+lines.indexOf(ml)));
							maps.put(key, lines);
						}
					}
					
					if(invManager == null){
						invManager = Framework.getService(INVManager.class);
					}
					
					for(String kee : maps.keySet()){
						String[] strs = kee.split("_",-1);
						String warehouseId = strs[0];//仓库
						Warehouse warehouse = invManager.getWarehouseById(warehouseId, Env.getOrgRrn());
						String outType = strs[1];//出库类型
						String kind = strs[2];//相关单位
						MovementOut out = new MovementOut();
						out.setOrgRrn(Env.getOrgRrn());
						out.setWarehouseRrn(warehouse.getObjectRrn());
						out.setOutType(outType);
						out.setKind(kind);
						
						List<MovementLine> lines = maps.get(kee);
						invManager.saveMovementOutLine(out, lines, MovementOut.OutType.ADOU, Env.getUserRrn());
					}
				}
				UI.showInfo("导入成功");
			}
		} catch (Exception e) {
			UI.showError("导入失败");
			logger.error("Error at AdjustOutSection : importAdapter() " + e);
		}
		
		refreshAdapter();
	}

	private String getCellString(HSSFCell cell) {
		String result = "";
		if (cell != null) {
			// 单元格类型：Numeric:0,String:1,Formula:2,Blank:3,Boolean:4,Error:5
			int cellType = cell.getCellType();
			switch (cellType) {
				case HSSFCell.CELL_TYPE_STRING:
					result = cell.getRichStringCellValue().getString();
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					result = String.valueOf(cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					result = String.valueOf(cell.getNumericCellValue());
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					result = String.valueOf(cell.getBooleanCellValue());
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					result = "";
					break;
				case HSSFCell.CELL_TYPE_ERROR:
					result = "";
					break;
				default:
					break;
			}
		}
		return result;
	}
}
