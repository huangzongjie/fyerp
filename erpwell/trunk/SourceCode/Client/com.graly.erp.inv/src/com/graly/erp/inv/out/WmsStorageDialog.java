package com.graly.erp.inv.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WmsStorageDialog extends EntityDialog {


	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected static String DIALOG_TABLE_NAME="WmsMaterial";
	protected WmsStorageDialog wmsStorageDialog;
	protected String whereClause;
	private ADTable dialogADTable;
	private List<Material> materials;
	
	
	public WmsStorageDialog(Shell parent, ADTable table, ADBase adObject,List<Material> materials) {
		super(parent, table, adObject);
		this.materials = materials;
	}
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("-------------立体库出库处理界面-------------");
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		ADManager entityManager;
		try {
			FormToolkit formToolkit  = new FormToolkit(Display.getCurrent());
			entityManager = Framework.getService(ADManager.class);
			dialogADTable = entityManager.getADTable(0L, DIALOG_TABLE_NAME);
			tableManager = new EntityTableManager(dialogADTable);
			viewer = (TableViewer) getTableManager().createViewer(body, formToolkit);
//			viewer.setInput(null);
			initTableViewer();
		    final TableViewer tv = (TableViewer) viewer;
		    CellEditor[] editors = new CellEditor[tv.getTable().getColumnCount()];
		    for(int i=0;i<editors.length;i++){
		    	editors[i]=new WmsMaterialPropertyEditor(tv.getTable());
		    	editors[i].setValidator(new ICellEditorValidator(){

					@Override
					public String isValid(Object value) {
						try {
							if(value instanceof String){
								new BigDecimal((String)value);
							}
						} catch (RuntimeException e) {
							return "输入的是无效数值";
						}
						return null;
					}});
		    }
		    tv.setCellEditors(editors);
			viewer.setCellModifier(new ICellModifier(){

					@Override
					public boolean canModify(Object element, String property) {
						if("qtyTransit".equals(property) || "qtyOut".equals(property)){
							return true;
						}
						return false;
					}

					@Override
					public Object getValue(Object element, String property) {
						if(element instanceof Material){
							Material mat = (Material) element;
							if("qtyTransit".equals(property)){
								return mat.getQtyTransit();
							}else if("qtyOut".equals(property)){
								return mat.getQtyOut();
							}
						}
						return "";
					}

					@Override
					public void modify(Object element, String property,
							Object value) {
						if(element instanceof TableItem){
							if(value instanceof String){
								try {
									TableItem ti = (TableItem) element;
									Material msl = (Material)ti.getData();
									if("qtyTransit".equals(property)){
										msl.setQtyTransit(new BigDecimal((String)value));
									}else if("qtyOut".equals(property)){
										msl.setQtyOut(new BigDecimal((String)value));
									}
									viewer.refresh();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public TableViewerManager getTableManager() {
		return tableManager;
	}

	public void setTableManager(TableViewerManager tableManager) {
		this.tableManager = tableManager;
	}
	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	public void initTableViewer(){
		try {
			tableManager.setInput(materials);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
//		Button btnOk = createButton(parent, IDialogConstants.OK_ID,
//				Message.getString("common.ok"), false);
//		if (DIALOGTYPE_VIEW.equals(dialogType)) {
//			btnOk.setEnabled(false);
//		}
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
	
	class WmsMaterialPropertyEditor extends DialogCellEditor{

		public WmsMaterialPropertyEditor() {
			super();
		}

		public WmsMaterialPropertyEditor(Composite parent, int style) {
			super(parent, style);
		}

		public WmsMaterialPropertyEditor(Composite parent) {
			super(parent);
		}

		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			InputDialog d = new InputDialog(cellEditorWindow.getShell(),"输入框","在此输入新值",this.getValue().toString(),new IInputValidator(){

				@Override
				public String isValid(String newText) {
					try {
						new BigDecimal(newText);
					} catch (RuntimeException e) {
						//setErrorMessage("输入的是无效数值");
						return "输入的是无效数值";
					}
					return null;
				}});
			d.open();
			return d.getValue();
		}
	}
	@Override
	protected void okPressed() {
		if(validateQty()) {//立体库库存大于0拆分销货单
			List<Material> materials = (List<Material>) viewer.getInput();
			List<Material> wmsMaterials = new ArrayList<Material>(); 
			int i =0;
			for(Material material : materials){
				if(material.getQtyOut().compareTo(BigDecimal.ZERO)>0){
					wmsMaterials.add(material);
				}
			}
			MovementOut out =null;
			if(wmsMaterials!=null && wmsMaterials.size() >0 ){
				out = createWmsMovementOut(wmsMaterials);
			}
			UI.showInfo("立体库单据编号："+out.getDocId());
			super.okPressed();
		} else {
			return;
		}
	}	
	
	public boolean validateQty(){
		List<Material> materials = (List<Material>) viewer.getInput();
		for(Material material : materials){
			if(material.getQtyTransit().compareTo(material.getQtyInitial())>0){
				UI.showError("环保良品出库数量不能大于环保库存");
				return false;
			}
			if(material.getQtyOut().compareTo(material.getQtyOnHand())>0){
				UI.showError("立体库出库数量不能大于立体库库存");
				return false;
			}
			if(material.getQtyOut().add(material.getQtyTransit()).compareTo(material.getQtyIn())!=0){
				UI.showError("2个仓库出库总数不等于销售出库数量");
				return false;
			}
		}
		return true;
	}
	
	private MovementOut createWmsMovementOut(List<Material> materials){
		MovementOut out = new MovementOut();
		try {
		MovementOut parentOut = (MovementOut) this.adObject;
		out.setOrgRrn(Env.getOrgRrn());
		out.setIsActive(true);
		out.setSoId(parentOut.getSoId());
		out.setWarehouseRrn(parentOut.getWarehouseRrn());
		out.setWarehouseId(parentOut.getWarehouseId());
		out.setDescription(parentOut.getDescription());
		out.setCustomerName(parentOut.getCustomerName());
		out.setSeller(parentOut.getSeller());
		out.setKind(parentOut.getKind());
		out.setDeliverAddress(parentOut.getDeliverAddress());
		out.setLinkMan(parentOut.getLinkMan());
		out.setWmsWarehouse("自动化A库");
		List<MovementLine> lines = new ArrayList<MovementLine>();
		int i = 0;
		for(Material material : materials){
			MovementLine line = new MovementLine();
			line.setOrgRrn(Env.getOrgRrn());
			line.setLineNo(new Long((i + 1)*10));
			line.setMaterialRrn(material.getObjectRrn());
			line.setMaterialId(material.getMaterialId());
			line.setMaterialName(material.getName());
			line.setLotType(material.getLotType());
			line.setUomId(material.getInventoryUom());

			line.setMovementId(material.getMaterialId());
			line.setQtyMovement(material.getQtyOut());
			lines.add(line);
			i++;
		}
		out.setMovementLines(lines);
	
		INVManager invManager = Framework.getService(INVManager.class);
		ADManager adManager = Framework.getService(ADManager.class);
		out = invManager.saveMovementOutLine(out, out.getMovementLines(), MovementOut.OutType.SOU, Env.getUserRrn());
		 //将总仓的数量置为总仓数
		 for(MovementLine ml : parentOut.getMovementLines()){
				for(Material material : materials){
					if(ml.getMaterialId().equals(material.getMaterialId())){
						ml.setQtyMovement(material.getQtyTransit());
						adManager.saveEntity(ml, Env.getOrgRrn());
					}
				}
		 }
//		 ADManager adManager = Framework.getService(ADManager.class);
//		 adManager.saveEntity(parentOut, Env.getOrgRrn());
//		 invManager.saveMovementOutLine(parentOut, parentOut.getMovementLines(), MovementOut.OutType.SOU, Env.getUserRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return out;
		}
		return out;
	}
}
