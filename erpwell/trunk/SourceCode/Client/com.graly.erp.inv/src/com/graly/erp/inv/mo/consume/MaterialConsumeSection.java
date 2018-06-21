package com.graly.erp.inv.mo.consume;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.BrowserDialog;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.MovementWriteOff;
import com.graly.erp.inv.mwriteoff.WriteOffDialog;
import com.graly.erp.inv.transfer.LotTrsDialog;
import com.graly.erp.inv.transfer.LotTrsSection;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.LotConsume;

public class MaterialConsumeSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MaterialConsumeSection.class);
	protected TableListManager listTableManager;
	protected MaterialCosumeDialog consumeDialog;
	private LotConsume selectLine;
	
	private List<LotConsume> input;
	private String moId;
	private String materialId;
	private Long moRrn = null;
	private Date startDate = null, endDate = null;

	protected ToolItem itemWriteOff;
	protected ToolItem itemBatWriteOff;
	protected ToolItem itemConsumeRecords;
	protected ToolItem itemWorkConsume;
	protected ToolItem itemBomDifference;
	
	public MaterialConsumeSection() {
		super();
	}

	public MaterialConsumeSection(TableListManager tableManager) {
		this();
		this.listTableManager = tableManager;
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemExport(tBar);
		createToolItemSearch(tBar);
//		createToolItemManualWriteOff(tBar);//按lot核销
		createToolItemBatMaterialWriteOff(tBar);
		createToolItemMaterialWriteOff(tBar);
		createItemConsumeRecords(tBar);
		createToolItemWorkConsume(tBar);
//		createItemBomDifference(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemBatMaterialWriteOff(ToolBar tBar) {
		itemBatWriteOff = new ToolItem(tBar, SWT.PUSH);
		itemBatWriteOff.setText("批量核销");
		itemBatWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemBatWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				batMaterialWriteOffAdapter();
			}
		});
	}

	protected void batMaterialWriteOffAdapter() {
		FileDialog fileDialog = new FileDialog(UI.getActiveShell(), SWT.OPEN);
		// 设置初始路径
		fileDialog.setFilterPath("C:/");
		// 设置扩展名过滤
		String[] filterExt = { "*.xls"};
		fileDialog.setFilterExtensions(filterExt);
		// 打开文件对话框，返回选择的文件
		String selectedFile = fileDialog.open();
		if (selectedFile != null) {
			if (!selectedFile.contains(".xls")) {
				UI.showWarning(Message.getString("ppm.upload_file_type_not_support"));
				return;
			}
			
			FileInputStream fis = null;
			boolean flag = true;
			StringBuffer errDetail = new StringBuffer();
			try {
				fis = new FileInputStream(selectedFile);
				POIFSFileSystem fs = new POIFSFileSystem(fis);
				HSSFWorkbook wb = new HSSFWorkbook(fs); // 读取excel工作簿
				HSSFSheet sheet = wb.getSheetAt(0); // 读取excel的sheet
				List<String> moIds = new ArrayList<String>();
				List<String> materialIds = new ArrayList<String>();
				List<Double> qtys = new ArrayList<Double>();
				for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
					String err = null;
					try {
						//从第二行开始，第一行是表头
							flag = true;
							String materialId = null;
							String moId = null;
							Double qty = 0D;
							HSSFRow row = sheet.getRow(i);
							
							if (row != null) {
								for (int j = 0; j < row.getLastCellNum(); j++) {
									try {
										HSSFCell cell = row.getCell((short) j);
										switch (cell.getCellType()) {
										case HSSFCell.CELL_TYPE_STRING:
											if (j == 0) {
												moId = cell.getStringCellValue();
												moIds.add(moId);
											}else if (j == 1) {
												materialId = cell.getStringCellValue();
												materialIds.add(materialId);
											}else if (j == 2) {
												
											}else if (j == 3) {
												qty = Double.valueOf(cell.getStringCellValue());
												qtys.add(qty);
											}
											break;
										case HSSFCell.CELL_TYPE_NUMERIC:
											if (j == 0) {
												double d = cell.getNumericCellValue();
												DecimalFormat parse = new DecimalFormat("0");
												moId = String.valueOf(parse.format(d));
												moIds.add(moId);
											}else if (j == 1) {
												double d = cell.getNumericCellValue();
												DecimalFormat parse = new DecimalFormat("0");
												materialId = String.valueOf(parse.format(d));
												materialIds.add(materialId);
											}else if (j == 2) {
												
											}else if (j == 3) {
												qty = cell.getNumericCellValue();
												qtys.add(qty);
											}
											break;
										case HSSFCell.CELL_TYPE_BOOLEAN:
											break;
										case HSSFCell.CELL_TYPE_BLANK:
											if(j==0 || j==1 || j==3){
												throw new Exception("第"+(i+1)+"行，第"+(j+1)+"列单元格为空");
											}
											break;
										default:
											if(j==0 || j==1 || j==3){
												throw new Exception("第"+(i+1)+"行，第"+(j+1)+"列无法识别");
											}
											break;
										}
									} catch (Exception e) {
										err = "第" + (j+1) +"列　出错啦!\n";
										throw new Exception(e);
									}
								}
							}
					} catch (Exception e) {
						errDetail.append("第" + (i+1) +"行," + err);
						logger.error(e);
					}
				}
				fis.close();
				if(errDetail != null && errDetail.length() > 0){
					UI.showInfo(errDetail.toString());
					return;
				}
				
				WipManager wipManager = Framework.getService(WipManager.class);
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				INVManager invManager = Framework.getService(INVManager.class);
				if(moIds.size() != materialIds.size()){
					UI.showError("工作令数与物料个数不符");
					return;
				}
				
				if(moIds.size() != qtys.size()){
					UI.showError("工作令数与冲销数量个数不符");
					return;
				}
				
				StringBuffer writeOffMsg = new StringBuffer();
				String msg = "";
				for(int i=0; i<moIds.size(); i++){
					try {
						String moId = moIds.get(i);
						String materialId = materialIds.get(i);
						double qty = qtys.get(i);
						ManufactureOrder mo = wipManager.getMoById(Env.getOrgRrn(), moId);
						if(mo == null){
							msg = "第"+ (i+2) + "行导入失败，工作令" + moId + "无法找到\n";
							writeOffMsg.append(msg);
							continue;
						}
						MovementWriteOff mw = new MovementWriteOff();
						mw.setOrgRrn(Env.getOrgRrn());
						mw.setMoRrn(mo.getObjectRrn());
						mw.setMoId(moId);
						
						MovementLine line = new MovementLine();
						line.setOrgRrn(Env.getOrgRrn());
						line.setLineStatus(Documentation.STATUS_DRAFTED);
						line.setLineNo(10L);
						line.setQtyMovement(new BigDecimal(qty));
						
						//通过物料获得库存单位
						List<Material> materialList = pdmManager.getMaterialById( materialId,Env.getOrgRrn());
						if(materialList.size() == 0) {
							msg = "第"+ (i+2) + "行导入失败，物料" + materialId + "无法找到\n";
							writeOffMsg.append(msg);
							continue;
						}
						Material mater = materialList.get(0);
						line.setMaterialRrn(mater.getObjectRrn());
						
						List<MovementLine> lines = new ArrayList<MovementLine>();
						lines.add(line);
						mw.setMovementLines(lines);
						
						invManager.manualWriteOff(mw, 1);
					} catch (Exception e) {
						msg = "第"+ (i+2) + "行导入失败\n";
						writeOffMsg.append(msg);
						logger.error(e);
					}
				}
				
				if(writeOffMsg != null && writeOffMsg.length() > 0){
					UI.showError(writeOffMsg.toString()+"其余行已成功导入");
				}else{
					UI.showInfo("导入成功");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void createItemBomDifference(ToolBar tBar) {
		itemBomDifference = new ToolItem(tBar, SWT.PUSH);
		itemBomDifference.setText("Bom差异表");
		itemBomDifference.setImage(SWTResourceCache.getImage("search"));
		itemBomDifference.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomDifferenceAdapter();
			}
		});
	}
	protected void bomDifferenceAdapter() {
		String url = Message.getString("url.bomdistinct");
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
		bd.open();
	}

	protected void createItemConsumeRecords(ToolBar tBar) {
		itemConsumeRecords = new ToolItem(tBar, SWT.PUSH);
		itemConsumeRecords.setText("核销记录");
		itemConsumeRecords.setImage(SWTResourceCache.getImage("search"));
		itemConsumeRecords.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				consumeRecordsAdapter();
			}
		});
	}
	protected void consumeRecordsAdapter() {
		String material_id = selectLine.getMaterialId();
		String urlfmt = Message.getString("url.workconsume");
		String url = String.format(urlfmt, material_id);
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
		bd.open();
	}

	protected void createToolItemWorkConsume(ToolBar tBar) {
		itemWorkConsume = new ToolItem(tBar, SWT.PUSH);
		itemWorkConsume.setText("手动核销日期");
		itemWorkConsume.setImage(SWTResourceCache.getImage("search"));
		itemWorkConsume.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				workConsumeAdapter();
			}
		});
	}
	
	protected void workConsumeAdapter() {
		    String material_id = selectLine.getMaterialId();
			String urlfmt = Message.getString("url.manualconsume");
			String url = String.format(urlfmt, material_id);
			BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
			bd.open();
		}
	
	protected void createToolItemMaterialWriteOff(ToolBar tBar) {
		itemWriteOff = new ToolItem(tBar, SWT.PUSH);
		itemWriteOff.setText(Message.getString("inv.manual_writeoff"));
		itemWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				materialWriteOffAdapter();
			}
		});
	}
	
	protected void materialWriteOffAdapter() {
		MaterialWriteOffDialog dialog = new MaterialWriteOffDialog(UI.getActiveShell());
		if(dialog.open() == Dialog.CANCEL) {
    		}
	}
	
	protected void createToolItemManualWriteOff(ToolBar tBar) {
		itemWriteOff = new ToolItem(tBar, SWT.PUSH);
		itemWriteOff.setText(Message.getString("inv.manual_writeoff"));
		itemWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				writeOffAdapter();
			}
		});
	}
	
	protected void writeOffAdapter() {
		WriteOffDialog dialog = new WriteOffDialog(UI.getActiveShell());
		if(dialog.open() == Dialog.CANCEL) {
			
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.consumeDialog != null && consumeDialog.getEntityQueryDialog() != null) {
			queryDialog = consumeDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			MaterialCosumeDialog vaDialog = new MaterialCosumeDialog(false);
			queryDialog = vaDialog.new MOQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}
	
	protected void createNewViewer(Composite client, final IManagedForm form){
		viewer = listTableManager.createViewer(client, form.getToolkit());
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
		
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				Object obj = ss.getFirstElement();
				if (obj instanceof LotConsume) {
					LotConsume inConsume = (LotConsume)obj;
					String dateConsume = inConsume.getConsumeDate();
					if(dateConsume != null && dateConsume.contains(",")){
						String[] dates = dateConsume.split(",");
						StringBuffer sb = new StringBuffer();
						for(int i = 0; i<dates.length; i++){
							sb.append(dates[i]+"\n");
						}
						UI.showInfo(sb.toString());
					}
					
				}
			}
			
		});
		redrawTableViewer();
	}
	
	private void setSelectionRequisition(Object obj) {
		if (obj instanceof LotConsume) {
			selectLine = (LotConsume) obj;
		} else {
			selectLine = null;
		}
	}
	
	@Override
	public void refresh(){
		try{
//			if(moRrn != null) {
				input = new ArrayList<LotConsume>();
				INVManager invManager = Framework.getService(INVManager.class);
				String toStartDate = null, toEndDate = null;
				if(startDate != null)
					toStartDate = I18nUtil.formatDate(startDate);
				if(endDate != null)
					toEndDate = I18nUtil.formatDate(endDate);
				input = invManager.getMaterialConsumeByMo(Env.getOrgRrn(),
						moRrn, materialId, toStartDate, toEndDate);
//				for(LotConsume lc : input) {
//					lc.setLotId(moId);
//				}
				if(input == null || input.size() == 0) {
					UI.showError(String.format(Message.getString("inv.mo_is_not_consume_material"), moId));
				}
				viewer.setInput(input);
				listTableManager.updateView(viewer);
				
				
				redrawTableViewer();
				
				createSectionDesc(section);
//			}
		} catch (Exception e){
			logger.error("EntityBlock : refresh ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	private void redrawTableViewer() {
		Table table = ((TableViewer)viewer).getTable();
		LotConsume totalConsume = new LotConsume();
		totalConsume.setLotId(Message.getString("inv.total"));
		BigDecimal totalQtyConsume = BigDecimal.ZERO;
//	BigDecimal totalPrice = BigDecimal.ZERO;
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof LotConsume) {
				LotConsume inConsume = (LotConsume)obj;
				if (inConsume.getQtyConsume() != null) {
					totalQtyConsume = totalQtyConsume.add(inConsume.getQtyConsume());
				}
				String dateConsume = inConsume.getConsumeDate();
				if(dateConsume != null && dateConsume.contains(",")){
					item.setBackground(5, Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
				}
			}
		}
		totalConsume.setQtyConsume(totalQtyConsume);
//				totalDetail.setLineTotal(totalPrice);
		TableViewer tv = (TableViewer)viewer;
		tv.insert(totalConsume, table.getItemCount());
		Color color = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
		table.getItems()[table.getItemCount()-1].setBackground(color);
		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
		table.getItems()[table.getItemCount()-1].setFont(font);
		table.redraw();
	}
	
	protected void createSectionDesc(Section section) {
		int count = 0;
		if(input != null) count = input.size();
		String text = Message.getString("common.totalshow");
		text = String.format(text, String.valueOf(count), String.valueOf(count));
		section.setDescription("  " + text);
	}
	
	protected ADTable getADTable() {
		return listTableManager.getADTable();
	}
	
	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof MaterialCosumeDialog) {
			this.consumeDialog = (MaterialCosumeDialog)dialog;
		} else {
			this.consumeDialog = null;
		}
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public Date getStart() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

}
