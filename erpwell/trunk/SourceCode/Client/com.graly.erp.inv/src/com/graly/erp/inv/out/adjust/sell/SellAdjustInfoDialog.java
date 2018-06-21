package com.graly.erp.inv.out.adjust.sell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.MovementOut.OutType;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.query.SingleQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

public class SellAdjustInfoDialog extends SingleQueryDialog {
	Logger logger = Logger.getLogger(SellAdjustInfoDialog.class);
	private static final String WHERE_CLAUSE_SUFFIX = " AND 1=1 ";
	protected String where;
	private MovementOut movementOut;
	private MovementOut out;
	private static final String FieldName_docId = "objectRrn";
	private IField f; // 销售出库订单编号
	private Object boject;
	private List<MovementLineLot> lineLots=new ArrayList<MovementLineLot>();
	

	public SellAdjustInfoDialog(TableListManager listTableManager, IManagedForm managedForm, String whereClause, int style) {
		super(listTableManager, managedForm, whereClause, style);
	}

	public SellAdjustInfoDialog(TableListManager listTableManager, IManagedForm managedForm, String whereClause, int style, Object boject) {
		super(listTableManager, managedForm, whereClause, style);
		this.boject = boject;
	}

	public SellAdjustInfoDialog(StructuredViewer viewer, Object object) {
		super();
		this.viewer = (CheckboxTableViewer) viewer;
		this.object = object;
	}

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 950;
		p.y = 550;
		return p;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
		setTitle(Message.getString("inv.copyFromPO"));

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		composite.setLayout(gl);

		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		Composite queryComp = toolkit.createComposite(composite, SWT.BORDER);
		queryComp.setLayout(new GridLayout(2, false));
		queryComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite resultComp = new Composite(composite, SWT.NONE);
		resultComp.setLayout(new GridLayout());
		resultComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			createSearchContent(queryComp, toolkit);
			createSearchTableViewer(resultComp);
			getInitSearchResult();
			return composite;
		}
		

	protected void createSearchContent(Composite parent, FormToolkit toolkit) {
		try{
			ADField moField = null;
			for(ADField adField : listTableManager.getADTable().getFields()) {
				if(FieldName_docId.equals(adField.getName())) {
					moField = adField;
					break;
				}
			}
			if(moField != null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(moField.getReftableRrn());
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null){
					return;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				String whereClause = "";
				if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
					whereClause = refTable.getWhereClause();
				}
				f = new SOmovementOutSearchField(moField.getName(), adTable, refTable, whereClause, SWT.BORDER);
		    	f.setLabel(I18nUtil.getI18nMessage(moField, "label"));
		    	f.addValueChangeListener(getPOChangedListener());
		    	f.createContent(parent, toolkit);
			}
		} catch (Exception e){
			logger.error("EntityForm : Init tablelist", e);
		}
	}
	
	private IValueChangeListener getPOChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				createWhereClause();
				refresh(true);
			}
		};
	};

	@Override
	protected void createWhereClause() {
		StringBuffer temp = new StringBuffer(whereClause);
		temp.append(WHERE_CLAUSE_SUFFIX);
		if (f instanceof SearchField && ((SearchField)f).getKey() != null) {
			temp.append(" AND objectRrn = " +((SearchField)f).getKey());
		}
		sb = temp;
	}
	
	/**
	 * 勾选销售出库单后点击确定按钮
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		List<MovementOut> moList = new ArrayList<MovementOut>();
		if (buttonId == IDialogConstants.OK_ID) {
			Object[] os = viewer.getCheckedElements();
			Map<Long, MovementOut> moLineMap = new HashMap<Long, MovementOut>();
			for (Object object : os) {
				if (object instanceof MovementOut) {
					movementOut = (MovementOut) object;
					if (!moLineMap.containsKey(movementOut.getWarehouseRrn())) {
						moLineMap.put(movementOut.getWarehouseRrn(), movementOut);
					}
					moList.add(movementOut);
				} else {
					movementOut = null;
				}
			}
			if(movementOut!=null){
				sellAllMo(movementOut);
				UI.showInfo("红冲完成！");
					cancelPressed();
			}
			if(moLineMap.size()<1){
				UI.showError("请选择销售出库单！");
				return;
			}
		}else{
			cancelPressed();
		}

	}
	
	private void sellAllMo(MovementOut movementOut){
		try {
			out=createMovementOut();
			List<MovementLine> linesList=new ArrayList<MovementLine>();
			linesList=getMoLineList(movementOut.getObjectRrn());
			
			for(MovementLine line:linesList){
			MovementLineLot movementLineLot = null;
			Long materialRrn=line.getMaterialRrn();
			List<MovementLineLot> moLineLots=getMoLineListLot(line);
			if(moLineLots.size()==0){
				Lot lot=getMoLineListLot(line.getMaterialId());
				movementLineLot=pareseMovementLineLot(line, line.getQtyMovement(), lot);
				 lineLots.add(movementLineLot);
			}else{
				for(MovementLineLot lineLot:moLineLots){
					Lot lot=getMoLineListLot(lineLot.getLotId());
					movementLineLot=pareseMovementLineLot(line, line.getQtyMovement(), lot);
					 lineLots.add(movementLineLot);
					}
			}
			
			}
			List<MovementLine> lines = createMovementLines();
				
				INVManager invManager = Framework.getService(INVManager.class);
				movementOut = invManager.saveMovementOutLine(out, lines, getOutType(), Env.getUserRrn());
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	protected MovementOut createMovementOut() {
		MovementOut out = new MovementOut();
		out.setOrgRrn(Env.getOrgRrn());
		out.setWarehouseRrn(movementOut.getWarehouseRrn());
		out.setOutType(movementOut.getOutType());				
		return out;
	}
	
	protected List<MovementLine> createMovementLines() throws Exception {
		List<MovementLine> lines = new ArrayList<MovementLine>();
		
		List<Long> materialRrns = new ArrayList<Long>();
		List<MovementLineLot> lineLots = null;
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for(MovementLineLot lineLot : this.getLineLots()) {//遍历所有Lot将相同物料的批合并成一个MovementLine
			if(materialRrns.contains(lineLot.getMaterialRrn()))
				continue;
			
			lineLots = new ArrayList<MovementLineLot>();
			total = BigDecimal.ZERO;
			for(MovementLineLot tempLineLot : getLineLots()) {
				if(tempLineLot.getMaterialRrn().equals(lineLot.getMaterialRrn())) {
					lineLots.add(tempLineLot);
					total = total.add(tempLineLot.getQtyMovement());
				}
			}
			materialRrns.add(lineLot.getMaterialRrn());
			if(lineLots.size() > 0) {
				lines.add(generateLine(lineLots, total, i * 10));
				i++;
			}
		}
		return lines;
	}
	
	// 单价和行总价没有，movementRrn, movementId, movementLineRrn在后台设置
	protected MovementLine generateLine(List<MovementLineLot> lineLots,
			BigDecimal qtyOut, int lineNo) throws Exception {
		MovementLine line = null;
		// 如果为再次保存则，根据物料找到已保存的出库单行，重新赋给lineLots, qtyOut
		if(out != null && out.getObjectRrn() != null) {
			MovementLineLot lineLot = lineLots.get(0);
			for(MovementLine movementLine : getMoLineList(out.getObjectRrn())) {
				if(lineLot.getMaterialRrn().equals(movementLine.getMaterialRrn())
						&& movementLine.getObjectRrn() != null) {
					movementLine.setMovementLots(lineLots);
					movementLine.setQtyMovement(qtyOut);
					return movementLine;
				}
			}
		}
		// 否则重新建个出库单行
		line = new MovementLine();;
		line.setOrgRrn(Env.getOrgRrn());
		line.setLineStatus(MovementTransfer.STATUS_DRAFTED);
		line.setLineNo(new Long(lineNo));
		line.setQtyMovement(qtyOut);
		
		line.setMovementLots(lineLots);
		MovementLineLot lineLot = lineLots.get(0);
		line.setMaterialId(lineLot.getMaterialId());
		line.setMaterialName(lineLot.getMaterialName());
		line.setMaterialRrn(lineLot.getMaterialRrn());
		ADManager adManager = Framework.getService(ADManager.class);
		Material material = new Material();
		material.setObjectRrn(lineLot.getMaterialRrn());
		material = (Material)adManager.getEntity(material);
		line.setUomId(material.getInventoryUom());
		return line;
	}
	

	/**
	 * 
	 * @param 获得所有的批次
	 * @return
	 */
	
	private List<MovementLineLot> getMoLineListLot(MovementLine movementLine){
	try {
		INVManager manager=Framework.getService(INVManager.class);
		List<MovementLineLot> lineLots=manager.getLineLots(movementLine.getObjectRrn());
		return lineLots;
	} catch (Exception e) {
		e.printStackTrace();
		
	}
		return null;
}
	
	private Lot getMoLineListLot(String lotId){
		try {
			INVManager manager=Framework.getService(INVManager.class);
			Lot lineLots=manager.getLotByLotId(Env.getOrgRrn(), lotId);
			return lineLots;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
			return null;
	}
	
	protected List<MovementLineLot> getLineLots() {
		if(lineLots == null) {
			lineLots = new ArrayList<MovementLineLot>();
			return lineLots;
		}
		return lineLots;
	}
	
	protected MovementLineLot pareseMovementLineLot(MovementLine line, BigDecimal outQty, Lot lot) {
		BigDecimal zero=BigDecimal.ZERO;
		MovementLineLot outLineLot = new MovementLineLot();
		outLineLot.setOrgRrn(Env.getOrgRrn());
		outLineLot.setIsActive(true);
		outLineLot.setCreated(new Date());
		outLineLot.setCreatedBy(Env.getUserRrn());
		outLineLot.setUpdated(new Date());
		outLineLot.setUpdatedBy(Env.getUserRrn());
		
		if(movementOut != null) {
			outLineLot.setMovementRrn(movementOut.getObjectRrn());
			outLineLot.setMovementId(movementOut.getDocId());
		}
		outLineLot.setMovementLineRrn(line.getObjectRrn());
		outLineLot.setLotRrn(lot.getObjectRrn());
		outLineLot.setLotId(lot.getLotId());
		outLineLot.setMaterialRrn(lot.getMaterialRrn());
		outLineLot.setMaterialId(lot.getMaterialId());
		outLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		outLineLot.setQtyMovement(outQty.negate());
		return outLineLot;
	}
	
	private List<MovementLineLot> getLineLotsByMaterial(Long materialRrn) {
		List<MovementLineLot> lLots = new ArrayList<MovementLineLot>();
		for(MovementLineLot lineLot : getLineLots()) {
			if(lineLot.getMaterialRrn().equals(materialRrn))
				lLots.add(lineLot);
		}
		return lLots;
	}
	
	
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.AOU;//出库类型改为财务调整
	}
	

	private List<MovementLine> getMoLineList(Long movementRrn){
		try {
			INVManager manager=Framework.getService(INVManager.class);
				List<MovementLine> list=manager.getMovementLines(movementRrn);
					return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}
	
	/**
	 * @author Aaron
	 * 查询销售出库单
	 */
	class SOmovementOutSearchField extends SearchField {
		
		public SOmovementOutSearchField(String id, ADTable adTable,
				ADRefTable refTable, String whereClause, int style) {
			super(id, adTable, refTable, whereClause, style);
		}
		
		protected SelectionListener getSelectionListener() {
	    	return new SelectionAdapter() {
	    		public void widgetSelected(SelectionEvent e) {
	    			listTableManager = new TableListManager(adTable);
	    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
	    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	    			
	    			SingleEntityQueryDialog singleDialog = new SoMoQueryDialog(
	    					listTableManager, null, whereClause, style);
	    			singleDialog.setTempSearchCondition(createLikeWhereClause());
	    			if(singleDialog.open() == IDialogConstants.OK_ID) {
	    				ADBase adBase = singleDialog.getSelectionEntity();
	    				if(adBase != null && adBase.getObjectRrn() != null) {
	    					setKey(adBase.getObjectRrn().toString(), adBase);
	    				}
	    				refresh();
	    				setFocus();
	    			}
	    		}
	    	};    		
	    }
	}
	
	class SoMoQueryDialog extends SingleEntityQueryDialog {
		public SoMoQueryDialog(TableListManager listTableManager,
				IManagedForm managedForm, String whereClause, int style){
			super(listTableManager, managedForm, whereClause, style);
		}
		
		@Override
	protected void refresh(boolean clearFlag) {
       String wc = getKeys();;
		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager.getADTable().getObjectRrn();
            l = manager.getEntityList(Env.getOrgRrn(), objectId, 
            		Env.getMaxResult(), getKeys(), "");
        } catch (Exception e) {
        	logger.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
        }
		tableViewer.setInput(l);			
		listTableManager.updateView(tableViewer);
		}
		
		protected void  createWhereClause() {
			String modelName = listTableManager.getADTable().getModelName() + ".";
			sb = new StringBuffer(" 1=1 ");
			if (queryForm!=null){
				LinkedHashMap<String, IField> fields = queryForm.getFields();
		        for(IField f : fields.values()) {
		        	if(f.getLabel().endsWith("*")){
		        		if(f.getValue() == null)
		        			sb.append(" AND 1<>1 ");
		        	}
		        	Object t = f.getValue();	        	
					if (t instanceof Date) {
						Date cc = (Date)t;
						if(cc != null) {
							sb.append(" AND ");
							sb.append("TO_CHAR(");
							sb.append(modelName);
							sb.append(f.getId());
							sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
							sb.append(I18nUtil.formatDate(cc));
							sb.append("'");
						}
					} else if(t instanceof String) {
						String txt = (String)t;
						if(!txt.trim().equals("") && txt.length() != 0) {
							sb.append(" AND ");
							sb.append(modelName);
							sb.append(f.getId());
							sb.append(" LIKE '");
							sb.append(txt);
							sb.append("'");
						}
					} else if(t instanceof Boolean) {
						 Boolean bl = (Boolean)t;
						 sb.append(" AND ");
						 sb.append(modelName);
						 sb.append(f.getId());
						 sb.append(" = '");
						 if(bl) {
							sb.append("Y");
						 } else if(!bl) {
							sb.append("N");
						 }
						 sb.append("'");
					} else if(t instanceof Long) {
						long l = (Long)t;
						sb.append(" AND ");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(" = " + l + " ");
					}
		        }
			}			
		}
	}
}
