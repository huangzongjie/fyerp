package com.graly.erp.inv.mo.consume;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementWriteOff;
import com.graly.erp.inv.mwriteoff.WriteOffSection;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MaterialWriteSection  extends WriteOffSection{
	protected Text materialIDText; //物料ID
	protected Text qtyText; //出库数量
	private List<MovementLine> movementLineList ;
	private static final Logger logger = Logger.getLogger(MaterialWriteSection.class);
	public MaterialWriteSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}

	@Override
	public void createContents(IManagedForm form, Composite parent,
			int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, sectionStyle);
		setSectionTitle();
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
		setItemInitStatus();
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);
	    createParentContent(client, toolkit);
	    createLotInfoComposite(client, toolkit);
	    createTableContent(client, toolkit);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(4, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = toolkit.createLabel(comp, "物料ID");
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setFont(SWTResourceCache.getFont("Verdana"));
		materialIDText = toolkit.createText(comp, "", SWT.BORDER);
		materialIDText.setTextLimit(48);
		
		Label qtyLabel = toolkit.createLabel(comp, "出库数量");
		qtyLabel.setForeground(SWTResourceCache.getColor("Folder"));
		qtyLabel.setFont(SWTResourceCache.getFont("Verdana"));
		qtyText = toolkit.createText(comp, "", SWT.BORDER);
		qtyText.setTextLimit(48);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);//GridData.FILL_HORIZONTAL
		gd.heightHint = 13;
		gd.widthHint = 340;
		materialIDText.setLayoutData(gd);
		qtyText.setLayoutData(gd);
		materialIDText.addKeyListener(getKeyListener());
		materialIDText.setFocus();
		qtyText.addKeyListener(getKeyListener());
	}
	
	protected MovementWriteOff createMovementWriteOff() {
		MovementWriteOff mw = new MovementWriteOff();
		mw.setOrgRrn(Env.getOrgRrn());
		mw.setMoId(String.valueOf(this.moField.getValue()).trim());
		return mw;
	}
	
	@Override
	protected void saveAdapter() {
		try {
			if(validate()){
				MovementWriteOff mw = createMovementWriteOff();
				WipManager wipManager = Framework.getService(WipManager.class);
				ManufactureOrder mo = wipManager.getMoById(Env.getOrgRrn(), mw.getMoId());
				if (mo == null) {
					UI.showError(Message.getString("inv.mo_is_not_exist"));
					return;
				}
				mw.setMoRrn(mo.getObjectRrn());
				if (UI.showConfirm(Message.getString("common.confirm_save"))) {
					mw.setMovementLines(movementLineList);
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.manualWriteOff(mw,Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					setIsSaved(true);
					setEnabled(false);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MaterialWriteSection : saveAdapter() ", e);
		}
	}
	
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<MovementLine> movementLines = new ArrayList<MovementLine>();
            for(ADBase ab : list) {
            	movementLines.add((MovementLine)ab);
            }
            setMovementLineList(movementLines);
            refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected void addMovementLine() {
		String materialId = materialIDText.getText();
		String qtyMovement = qtyText.getText();
		List<Material> materialList = null;
		
		try {
			if(materialId != null && !"".equals(materialId.trim())) {			
				
				PDMManager pdmManager = Framework.getService(PDMManager.class);
				materialList = pdmManager.getMaterialById( materialId,Env.getOrgRrn());
				
				if(materialList.size() == 0) {
					materialIDText.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError("查找不到所需的物料");
					materialIDText.selectAll();
					return;
				}

				if(qtyMovement == null || "".equals(qtyMovement.trim())) {
					Object value3 = this.qtyText.getText();
					if(value3 == null || value3.toString().trim().length() == 0){
						setErrorMessage("出库数量不能为空");
						return ;
					}
				}
				Material material =materialList.get(0);
				MovementLine movementLine =  new MovementLine();
				movementLine.setMaterialId(material.getMaterialId());
				movementLine.setMaterialRrn(material.getObjectRrn());
				movementLine.setQtyMovement(new BigDecimal(qtyMovement));
				movementLine.setMaterialName(material.getName());
				movementLine.setLotType(material.getLotType());
				
				if (validMovementLine(movementLine)) {
					materialIDText.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError("对不起，已经含有这种物料了");
					materialIDText.selectAll();
					return;
				}else{
					getMovementLineList().add(movementLine);
				}
				refresh();
			} else {
				UI.showError("物料ID不能为空");
				return;
			}
		} catch(Exception e) {
			materialIDText.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at MaterialWriteSection ：addMovementLine() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			materialIDText.selectAll();
		}
	}
	
	 
	protected boolean validMovementLine(MovementLine movementLine) {
		for(MovementLine mLine :getMovementLineList()){
			if(mLine.getMaterialRrn().equals(movementLine.getMaterialRrn())) return true;
		}
			return false;
	}
	
	protected String getOrderByClause() {
		return null;
	}

	protected List<?> getInput() {
		return getMovementLineList();
	}
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getInput());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	public List<MovementLine> getMovementLineList() {
		if(movementLineList == null) {
			movementLineList = new ArrayList<MovementLine>();
			return movementLineList;
		}
		return movementLineList;
	}

	public void setMovementLineList(List<MovementLine> movementLineList) {
		this.movementLineList = movementLineList;
	}



	@Override
	protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					materialIDText.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						setErrorMessage(null);
						addMovementLine();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at LotMasterSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj instanceof MovementLine) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				delete((MovementLine)obj);
        			}
        		}
        	}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	protected void delete(MovementLine movementLine) {
		getMovementLineList().remove(movementLine);
		refresh();
	}
	
	// 验证物料是否输入; 
	protected boolean validate() {
		setErrorMessage(null);
		
		Object value = this.moField.getValue();
		if (value == null || String.valueOf(value).trim().length() == 0) {
			setErrorMessage(String.format(Message.getString("common.ismandatory"),
					Message.getString("pur.relation_mo")));
			return false;
		}
		
		Object value2 = this.wirteoffTypeField.getValue();
		if(value2 == null || String.valueOf(value2).trim().length() == 0){
			setErrorMessage(String.format(Message.getString("common.ismandatory"),
					Message.getString("inv.writeoff_type")));
			return false;
		}
		if(this.getMovementLineList().size() == 0) {
			return false;
		}
		return true;
	}
	
}
