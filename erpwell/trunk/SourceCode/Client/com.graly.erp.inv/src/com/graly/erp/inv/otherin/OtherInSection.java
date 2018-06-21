package com.graly.erp.inv.otherin;

import java.io.File;
import java.io.FileInputStream;
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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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
import com.graly.erp.inv.DelInvMovementAuthorityManager;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OtherInSection extends MasterSection {

	private static final Logger logger = Logger.getLogger(OtherInSection.class);
	public static final String TABLE_NAME_MOVEMENTLINE = "INVOINMovementLine";
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected ToolItem itemByLotIn;
	protected TableListManager listTableManager;
	protected ADTable adTable;
	protected MovementIn selectedIn;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	ADManager adManager;
	protected ToolItem itemImport;
	public OtherInSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				editAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemByLotIn(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		createToolItemImport(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemByLotIn(ToolBar tBar) {
		itemByLotIn = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_LOTIN);
		itemByLotIn.setText(Message.getString("inv.by_lot_in"));
		itemByLotIn.setImage(SWTResourceCache.getImage("barcode"));
		itemByLotIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				inByLotAdapter();
			}
		});
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_NEW );
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void createToolItemImport(ToolBar tBar) {
		itemImport = new ToolItem(tBar, SWT.PUSH);
		itemImport.setText(Message.getString("oou.import"));
		itemImport.setImage(SWTResourceCache.getImage("export"));
		itemImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter();
			}
		});
	}
	
	protected void inByLotAdapter() {
		ByLotInDialog olbd = new ByLotInDialog(UI.getActiveShell(), getInTypeForByLotIn());
		if(olbd.open() == Dialog.CANCEL) {
			MovementIn in = ((OtherInLotSection)olbd.getLotMasterSection()).getMovementIn();
			if(in != null && in.getObjectRrn() != null) {
				this.selectedIn = in;
				if(selectedIn != null && selectedIn.getObjectRrn() != null)
					refreshAdd(selectedIn);
				editAdapter();
			}
		}
	}

	protected void newAdapter() {
		try {
			adTable = getADTableOfRequisition(TABLE_NAME_MOVEMENTLINE);
			listTableManager = new TableListManager(adTable);
			MovementIn mi = new MovementIn();
			mi.setOrgRrn(Env.getOrgRrn());
			OtherInLineDialog newInDialog = new OtherInLineDialog(UI.getActiveShell(), this.getTableManager().getADTable(), " 1<>1 ",
					mi, adTable, false);
			if (newInDialog.open() == Dialog.CANCEL) {
				mi = (MovementIn)newInDialog.getParentObject();
				if (mi != null && mi.getObjectRrn() != null) {
					selectedIn = mi;
					refreshSection();
					refreshAdd(selectedIn);
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
		
	}

	protected void deleteAdapter() {
		if (selectedIn != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedIn.getObjectRrn() != null) {
						if(DelInvMovementAuthorityManager.hasDeleteAuthority(Env.getUserRrn(),
								selectedIn.getWarehouseRrn(), selectedIn.getWarehouseId())) {
							INVManager invManager = Framework.getService(INVManager.class);
							invManager.deleteMovementIn(selectedIn, MovementIn.InType.OIN, Env.getUserRrn());
							this.refreshDelete(selectedIn);
							this.selectedIn = null;
							refreshSection();							
						}
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	protected void editAdapter() {
		try {
			if(selectedIn != null && selectedIn.getObjectRrn() != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedIn = (MovementIn)adManager.getEntity(selectedIn);
				adTable = getADTableOfRequisition(TABLE_NAME_MOVEMENTLINE);
				String whereClause = " movementRrn = " + selectedIn.getObjectRrn() + " ";
				OtherInLineDialog inLineDialog = new OtherInLineDialog(UI.getActiveShell(), this.getTableManager().getADTable(), whereClause,
						selectedIn, adTable, false);
				if (inLineDialog.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedIn);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OtherInSection : editAdapter() " + e);
		}
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_OIN);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		try {
			if (selectedIn != null && selectedIn.getObjectRrn() != null) {
				if(adManager == null)
					adManager = Framework.getService(ADManager.class);
				selectedIn = (MovementIn) adManager.getEntity(selectedIn);
				setStatusChanged(selectedIn.getDocStatus());
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedIn = null;
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			if(adTable == null) {
				adTable = entityManager.getADTable(0L, tableName);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());				
			}
		} catch (Exception e) {
			logger.error("InSection : getADTableOfRequisition()", e);
		}
		return adTable;
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof MovementIn) {
			selectedIn = (MovementIn) obj;
			setStatusChanged(selectedIn.getDocStatus());
		} else {
			selectedIn = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (Requisition.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if (Requisition.STATUS_CLOSED.equals(status)) {
			itemNew.setEnabled(false);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
	
	protected MovementIn.InType getInTypeForByLotIn() {
		return MovementIn.InType.OIN;
	}
	
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
							String key = getCellString(row.getCell(2))+"_调整入库_"+getCellString(row.getCell(3));
							List<MovementLine> lines = null;
							if(maps.containsKey(key)){
								lines = maps.get(key);
							}else{
								lines = new ArrayList<MovementLine>();
							}
							MovementLine ml = new MovementLine();
							ml.setOrgRrn(Env.getOrgRrn());
							//物料编号
							ml.setMaterialId(getCellString(row.getCell(0)));
							PDMManager pdmManager = Framework.getService(PDMManager.class);
							
							List<Material> mlst = pdmManager.getMaterialById(ml.getMaterialId(), Env.getOrgRrn());
							if(mlst != null && mlst.size() > 0){
								Material material = mlst.get(0);
								ml.setMaterialRrn(material.getObjectRrn());
								ml.setMaterialName(material.getName());
							}else{
								System.out.println(ml.getMaterialId());
							}
							//物料名称
							//仓库
							//相关单位
							//数量
							ml.setQtyMovement(new BigDecimal(getCellString(row.getCell(4))));
							ml.setUnitPrice(new BigDecimal(getCellString(row.getCell(5))));
							lines.add(ml);
							ml.setLineNo(10L*(1+lines.indexOf(ml)));
							maps.put(key, lines);
						}
					}
					
					INVManager invManager = Framework.getService(INVManager.class);

					for(String kee : maps.keySet()){
						String[] strs = kee.split("_",-1);
						String warehouseId = strs[0];//仓库
						Warehouse warehouse = invManager.getWarehouseById(warehouseId, Env.getOrgRrn());
						String outType = strs[1];//出库类型
						String kind = strs[2];//相关单位
						MovementIn in = new MovementIn();
						in.setOrgRrn(Env.getOrgRrn());
						in.setWarehouseRrn(warehouse.getObjectRrn());
						in.setInType(outType);
						in.setKind(kind);
						
						List<MovementLine> lines = maps.get(kee);
						invManager.saveMovementInLine(in, lines, MovementIn.InType.OIN, Env.getUserRrn());
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
