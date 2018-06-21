package com.graly.erp.xz.pur.request;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.Storage;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class XZRequisitionSection extends MasterSection {
	
	private static final Logger logger = Logger.getLogger(XZRequisitionSection.class);
	protected ToolItem itemMerge;
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected Requisition selectedReq;
	private static final String TABLE_NAME = "XZPURRequisitionLine";
	private ADTable adTable;
	protected ToolItem itemGenPurcharOrder;//���õ�
	protected ToolItem itemPreviewKN;//���ܹ�˾
	protected ToolItem itemPreviewBL;//��¯��˾
	protected ToolItem itemPreviewFW;//����˾
	protected ToolItem itemPreviewXS;//���۹�˾
	protected PrintQueryDialog printQueryDialog;
	protected ADTable printADTable;
	
	private static final String KEY_XZ_GEN_REVOKE = "XZ.REQ.PO";
	public XZRequisitionSection(EntityTableManager tableManager){
		super(tableManager);
		initPrintTable();
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionRequisition(ss.getFirstElement());
	    		editAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
		    		setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemKNPreview(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBLPreview(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemFWPreview(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemXSPreview(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemGen(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		
		setEnabledItem();
		section.setTextClient(tBar);
	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_NEW);
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
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_EDIT);
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PR_DELETE);
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
	
	protected void newAdapter() {
		int i=1;
		String where = " 1!=1 ";
		XZRequisitionLineBlockDialog cd = new XZRequisitionLineBlockDialog(UI.getActiveShell(),
				getADTableOfRequisition(), where, null,i);
		if(cd.open() == Dialog.CANCEL) {
			refreshSection();
		}
	}
	
	protected void editAdapter() {
		try {
			if(selectedReq != null && selectedReq.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedReq = (Requisition)adManager.getEntity(selectedReq);
				String whereClause = ( " requisitionRrn = '" + selectedReq.getObjectRrn().toString() + "' ");
				XZRequisitionLineBlockDialog cd = new XZRequisitionLineBlockDialog(UI.getActiveShell(),
						getADTableOfRequisition(), whereClause, selectedReq);
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at RequisitionSection : editAdapter() " + e);
		}
	}
	
	@Override
	protected void queryAdapter() {		
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPR);
			queryDialog.open();
		}
	}

	protected void deleteAdapter() {
		if(selectedReq != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message
						.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedReq.getObjectRrn() != null) {
						if(!(Env.getUserRrn() ==selectedReq.getCreatedBy().longValue())){
							UI.showError("ֻ��ɾ���Լ������ļƻ�");
							return;
						}
						PURManager purManager = Framework.getService(PURManager.class);
						purManager.deletePR(selectedReq, Env.getUserRrn());
						this.selectedReq = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	
	protected void refreshSection() {
		try {
			refresh();
			if(selectedReq != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedReq = (Requisition)adManager.getEntity(selectedReq);
				this.setStatusChanged(selectedReq.getDocStatus());
			}
		} catch(Exception e) {
			logger.error("Error at RequisitionSection : refreshSection() " + e);
		}
	}

	protected ADTable getADTableOfRequisition() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected void setSelectionRequisition(Object obj) {
		if(obj instanceof Requisition) {
			selectedReq = (Requisition)obj;
			setStatusChanged(selectedReq.getDocStatus());
		} else {
			selectedReq = null;
			setStatusChanged("");
		}
	}
	
	protected void setStatusChanged(String status) {
		if(Requisition.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if(Requisition.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
	
	/**
	 * �����Ѿ�ͳ�ƹ��¶Ȳɹ��������������½����µ��������üƻ�
	 * */
	protected void setEnabledItem() {
		try{
			ADManager adManager = Framework.getService(ADManager.class);
			StringBuffer sb = new StringBuffer();
			Date sysDate = Env.getSysDate();
			sb.append(" mpsId = 'Y' ");
			sb.append(" AND to_char(created,'YYYY-MM') = to_char(TO_DATE('" +
					I18nUtil.formatDate(sysDate) 
					+"', " +"'YYYY-MM-DD'),'YYYY-MM') ");
			List<Requisition> requisitions = adManager.getEntityList(Env.getOrgRrn(),Requisition.class,Integer.MAX_VALUE,
					sb.toString(),"");
			if(requisitions!=null && requisitions.size() >0){
				itemNew.setEnabled(false);
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	protected void createToolItemGen(ToolBar tBar) {
		itemGenPurcharOrder = new AuthorityToolItem(tBar, SWT.PUSH, KEY_XZ_GEN_REVOKE);
		itemGenPurcharOrder.setText("ͳ�������¶Ȳɹ�����");
		itemGenPurcharOrder.setImage(SWTResourceCache.getImage("new"));
		itemGenPurcharOrder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				genPurcharOrderAdapter();
			}
		});
	}
	
	
	/**
	 * �������üƻ�(Ҳ�вɹ�����)����Ӧ�̡���˾�ֿ�ͳ��
	 * */
	protected void genPurcharOrderAdapter() {
		try{
			String whereClause = "docStatus ='APPROVED' AND (mpsId <> 'Y' or mpsId is null ) ";
			PURManager purManager = Framework.getService(PURManager.class);
			purManager.generatePOXZ(Env.getOrgRrn(),Env.getUserRrn(),whereClause);
		}catch(Exception e){
			e.printStackTrace();
		}
		UI.showInfo("�����ɹ�");
		refresh();
		
//		try {
// 
//			ADManager adManager = Framework.getService(ADManager.class);
//			INVManager invManager = Framework.getService(INVManager.class);
//			List<Requisition>  requisitions = adManager.getEntityList(Env.getOrgRrn(), 
//					Requisition.class,Integer.MAX_VALUE,
//					" docStatus ='APPROVED' AND (mpsId <> 'Y' or mpsId is null )","created");
//			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();//ͳ��������������<�ֿ���,�ֿ���>
//			LinkedHashMap<Long, ArrayList<RequisitionLine>> verndorMap = new LinkedHashMap<Long, ArrayList<RequisitionLine>>();//���й�Ӧ��
//			for(Requisition requisition : requisitions){
//				requisition = (Requisition) adManager.getEntity(requisition);
//				for(RequisitionLine requisitionLine : requisition.getPrLines()){
//					Storage storage = invManager.getMaterialStorage(Env.getOrgRrn(), requisitionLine.getMaterialRrn(), 43005950L, Env.getUserRrn());
//					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
//						storage = storageMap.get(storage.getObjectRrn());
//					}else{
//						storageMap.put(storage.getObjectRrn(), storage);
//					}
//					BigDecimal qtyOnhand = storage.getQtyOnhand();
//					if(qtyOnhand.compareTo(BigDecimal.ZERO)<=0){
//						//��治��//���С��������
//						storage.setQtyOnhand(BigDecimal.ZERO);
//						RequisitionLine poline = (RequisitionLine) requisitionLine.clone();
//						poline.setObjectRrn(null);
//						poline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
//						//��Ӧ����HashMap�в��������½���
//						if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
//							List<RequisitionLine> poLines = verndorMap.get(requisitionLine.getVendorRrn());
//							poLines.add(poline);
//						}else{
//							ArrayList<RequisitionLine> polines = new ArrayList<RequisitionLine>();
//							polines.add(poline);
//							verndorMap.put(requisitionLine.getVendorRrn(), polines);
//						}
//					}else if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
//						if(qtyOnhand.subtract(requisitionLine.getQty()).compareTo(BigDecimal.ZERO) >=0){
//							//�����ڵ���������
//							storage.setQtyOnhand(qtyOnhand.subtract(requisitionLine.getQty()));
//						}else{
//							//���С��������
//							storage.setQtyOnhand(BigDecimal.ZERO);
//							RequisitionLine poline = (RequisitionLine) requisitionLine.clone();
//							poline.setObjectRrn(null);
//							poline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
//							//��Ӧ����HashMap�в��������½���
//							if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
//								List<RequisitionLine> poLines = verndorMap.get(requisitionLine.getVendorRrn());
//								poLines.add(poline);
//							}else{
//								ArrayList<RequisitionLine> polines = new ArrayList<RequisitionLine>();
//								polines.add(poline);
//								verndorMap.put(requisitionLine.getVendorRrn(), polines);
//							}
//						}
//					}
//					
//				}
//				requisition.setMpsId("Y"); //�Ѿ�ͳ�Ʋɹ�����ͳ��
//				adManager.saveEntity(requisition, Env.getOrgRrn());
//				
//			}
//			Iterator iteVer = verndorMap.keySet().iterator();//������Ӧ��
//			while (iteVer.hasNext()) {
//				Long vendorRrn = (Long) iteVer.next();
//				ArrayList<RequisitionLine> reqLines = verndorMap.get(vendorRrn);
//				if(reqLines!=null && reqLines.size() > 0 ){
//					//����˾���ɹ���
//					LinkedHashMap<String, ArrayList<RequisitionLine>> companyMap = new LinkedHashMap<String, ArrayList<RequisitionLine>>();
//					for(RequisitionLine reqLine : reqLines){
//						if(companyMap.get(reqLine.getXzCompany())!=null){
//							List<RequisitionLine> comReqLines = companyMap.get(reqLine.getXzCompany());
//							comReqLines.add(reqLine);
//						}else{
//							ArrayList<RequisitionLine> comReqLines = new ArrayList<RequisitionLine>();
//							comReqLines.add(reqLine);
//							companyMap.put(reqLine.getXzCompany(), comReqLines);
//						}
//					}
//					Iterator iteCompany = companyMap.keySet().iterator();//������Ӧ������Ĺ�˾
//					while (iteCompany.hasNext()) {
//						String company = (String) iteCompany.next();
//						ArrayList<RequisitionLine> comReqLines = companyMap.get(company);
//						PurchaseOrder po = new PurchaseOrder();
//						po.setOrgRrn(Env.getOrgRrn());
//						po.setIsActive(true);
//						po.setCreated(new Date());
//						po.setVendorRrn(vendorRrn);
//						PURManager purmanager = Framework.getService(PURManager.class);
//						purmanager.createPOFromPR(po, comReqLines, Env.getUserRrn());
//					}
//				}
//			}
//			
//			refresh();		
//		} catch (Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			return;
//		}
	}
	
//	/**
//	 * �������üƻ�(Ҳ�вɹ�����)����Ӧ��ͳ��
//	 * */
//	protected void consumeAdapter() {
//		try {
// 
//			ADManager adManager = Framework.getService(ADManager.class);
//			INVManager invManager = Framework.getService(INVManager.class);
//			List<Requisition>  requisitions = adManager.getEntityList(Env.getOrgRrn(), 
//					Requisition.class,Integer.MAX_VALUE,
//					" docStatus ='APPROVED' AND (mpsId <> 'Y' or mpsId is null )","created");
//			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();//ͳ��������������
//			LinkedHashMap<Long, ArrayList<RequisitionLine>> verndorMap = new LinkedHashMap<Long, ArrayList<RequisitionLine>>();//���й�Ӧ��
//			for(Requisition requisition : requisitions){
//				requisition = (Requisition) adManager.getEntity(requisition);
//				for(RequisitionLine requisitionLine : requisition.getPrLines()){
//					Storage storage = invManager.getMaterialStorage(Env.getOrgRrn(), requisitionLine.getMaterialRrn(), 43005950L, Env.getUserRrn());
//					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
//						storage = storageMap.get(storage.getObjectRrn());
//					}else{
//						storageMap.put(storage.getObjectRrn(), storage);
//					}
//					BigDecimal qtyOnhand = storage.getQtyOnhand();
//					if(qtyOnhand.compareTo(BigDecimal.ZERO)<=0){
//						//��治��//���С��������
//						storage.setQtyOnhand(BigDecimal.ZERO);
//						RequisitionLine poline = (RequisitionLine) requisitionLine.clone();
//						poline.setObjectRrn(null);
//						poline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
//						//��Ӧ����HashMap�в��������½���
//						if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
//							List<RequisitionLine> poLines = verndorMap.get(requisitionLine.getVendorRrn());
//							poLines.add(poline);
//						}else{
//							ArrayList<RequisitionLine> polines = new ArrayList<RequisitionLine>();
//							polines.add(poline);
//							verndorMap.put(requisitionLine.getVendorRrn(), polines);
//						}
//					}else if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
//						if(qtyOnhand.subtract(requisitionLine.getQty()).compareTo(BigDecimal.ZERO) >=0){
//							//�����ڵ���������
//							storage.setQtyOnhand(qtyOnhand.subtract(requisitionLine.getQty()));
//						}else{
//							//���С��������
//							storage.setQtyOnhand(BigDecimal.ZERO);
//							RequisitionLine poline = (RequisitionLine) requisitionLine.clone();
//							poline.setObjectRrn(null);
//							poline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
//							//��Ӧ����HashMap�в��������½���
//							if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
//								List<RequisitionLine> poLines = verndorMap.get(requisitionLine.getVendorRrn());
//								poLines.add(poline);
//							}else{
//								ArrayList<RequisitionLine> polines = new ArrayList<RequisitionLine>();
//								polines.add(poline);
//								verndorMap.put(requisitionLine.getVendorRrn(), polines);
//							}
//						}
//					}
//					
//				}
//				requisition.setMpsId("Y"); //�Ѿ�ͳ�Ʋɹ�����ͳ��
//				adManager.saveEntity(requisition, Env.getOrgRrn());
//				
//			}
//			Iterator iteVer = verndorMap.keySet().iterator();
//			while (iteVer.hasNext()) {
//				Long vendorRrn = (Long) iteVer.next();
//				ArrayList<RequisitionLine> polines = verndorMap.get(vendorRrn);
//				if(polines!=null && polines.size() > 0 ){
//					PurchaseOrder po = new PurchaseOrder();
//					po.setOrgRrn(Env.getOrgRrn());
//					po.setIsActive(true);
//					po.setCreated(new Date());
//					po.setVendorRrn(vendorRrn);
//					PURManager purmanager = Framework.getService(PURManager.class);
//					purmanager.createPOFromPR(po, polines, Env.getUserRrn());
//				}
//			}
//			
//			refresh();		
//		} catch (Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			return;
//		}
//	}
	
	/**
	 * �������üƻ�,���ƻ��͹�Ӧ��ͳ��*/
//	protected void consumeAdapter() {
//		try {
// 
//			ADManager adManager = Framework.getService(ADManager.class);
//			INVManager invManager = Framework.getService(INVManager.class);
//			List<Requisition>  requisitions = adManager.getEntityList(Env.getOrgRrn(), 
//					Requisition.class,Integer.MAX_VALUE,
//					" docStatus ='APPROVED' AND (mpsId <> 'Y' or mpsId is null )","created");
//			HashMap<Long,Storage> storageMap = new LinkedHashMap<Long,Storage>();//ͳ��������������
//			for(Requisition requisition : requisitions){
//				
//				LinkedHashMap<Long, ArrayList<RequisitionLine>> verndorMap = new LinkedHashMap<Long, ArrayList<RequisitionLine>>();
//				requisition = (Requisition) adManager.getEntity(requisition);
//				for(RequisitionLine requisitionLine : requisition.getPrLines()){
//					Storage storage = invManager.getMaterialStorage(Env.getOrgRrn(), requisitionLine.getMaterialRrn(), 43005950L, Env.getUserRrn());
//					if(storage!=null && storageMap.get(storage.getObjectRrn())!=null){
//						storage = storageMap.get(storage.getObjectRrn());
//					}else{
//						storageMap.put(storage.getObjectRrn(), storage);
//					}
//					BigDecimal qtyOnhand = storage.getQtyOnhand();
//					if(qtyOnhand.compareTo(BigDecimal.ZERO)<=0){
//						//��治��//���С��������
//						storage.setQtyOnhand(BigDecimal.ZERO);
//						RequisitionLine poline = (RequisitionLine) requisitionLine.clone();
//						poline.setObjectRrn(null);
//						poline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
//						//��Ӧ����HashMap�в��������½���
//						if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
//							List<RequisitionLine> poLines = verndorMap.get(requisitionLine.getVendorRrn());
//							poLines.add(poline);
//						}else{
//							ArrayList<RequisitionLine> polines = new ArrayList<RequisitionLine>();
//							polines.add(poline);
//							verndorMap.put(requisitionLine.getVendorRrn(), polines);
//						}
//					}else if(qtyOnhand.compareTo(BigDecimal.ZERO)==1){
//						if(qtyOnhand.subtract(requisitionLine.getQty()).compareTo(BigDecimal.ZERO) >=0){
//							//�����ڵ���������
//							storage.setQtyOnhand(qtyOnhand.subtract(requisitionLine.getQty()));
//						}else{
//							//���С��������
//							storage.setQtyOnhand(BigDecimal.ZERO);
//							RequisitionLine poline = (RequisitionLine) requisitionLine.clone();
//							poline.setObjectRrn(null);
//							poline.setQty(requisitionLine.getQty().subtract(qtyOnhand));
//							//��Ӧ����HashMap�в��������½���
//							if(verndorMap.get(requisitionLine.getVendorRrn())!=null){
//								List<RequisitionLine> poLines = verndorMap.get(requisitionLine.getVendorRrn());
//								poLines.add(poline);
//							}else{
//								ArrayList<RequisitionLine> polines = new ArrayList<RequisitionLine>();
//								polines.add(poline);
//								verndorMap.put(requisitionLine.getVendorRrn(), polines);
//							}
//						}
//					}
//					
//				}
//				requisition.setMpsId("Y"); //�Ѿ�ͳ�Ʋɹ�����ͳ��
//				adManager.saveEntity(requisition, Env.getOrgRrn());
//				Iterator iteVer = verndorMap.keySet().iterator();
//				while (iteVer.hasNext()) {
//					Long vendorRrn = (Long) iteVer.next();
//					ArrayList<RequisitionLine> polines = verndorMap.get(vendorRrn);
//					if(polines!=null && polines.size() > 0 ){
//						PurchaseOrder po = new PurchaseOrder();
//						po.setOrgRrn(Env.getOrgRrn());
//						po.setIsActive(true);
//						po.setCreated(new Date());
//						po.setVendorRrn(vendorRrn);
//						PURManager purmanager = Framework.getService(PURManager.class);
//						purmanager.createPOFromPR(po, polines, Env.getUserRrn());
//					}
//				}
//			}
//			refresh();
//		} catch (Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			return;
//		}
//	}
	protected void createToolItemKNPreview(ToolBar tBar) {
		itemGenPurcharOrder = new ToolItem(tBar, SWT.PUSH);
		itemGenPurcharOrder.setText("���ܻ�����ӡ");
		itemGenPurcharOrder.setImage(SWTResourceCache.getImage("print"));
		itemGenPurcharOrder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewKNAdapter();
			}
		});
	}
	
	protected void createToolItemBLPreview(ToolBar tBar) {
		itemGenPurcharOrder = new ToolItem(tBar, SWT.PUSH);
		itemGenPurcharOrder.setText("��¯��˾��ӡ");
		itemGenPurcharOrder.setImage(SWTResourceCache.getImage("print"));
		itemGenPurcharOrder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewBLAdapter();
			}
		});
	}
	
	protected void createToolItemFWPreview(ToolBar tBar) {
		itemGenPurcharOrder = new ToolItem(tBar, SWT.PUSH);
		itemGenPurcharOrder.setText("����˾��ӡ");
		itemGenPurcharOrder.setImage(SWTResourceCache.getImage("print"));
		itemGenPurcharOrder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewFWAdapter();
			}
		});
	}
	
	protected void createToolItemXSPreview(ToolBar tBar) {
		itemGenPurcharOrder = new ToolItem(tBar, SWT.PUSH);
		itemGenPurcharOrder.setText("���۹�˾��ӡ");
		itemGenPurcharOrder.setImage(SWTResourceCache.getImage("print"));
		itemGenPurcharOrder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewXSAdapter();
			}
		});
	}
	
	
	protected void previewKNAdapter() {
		String report = "xz_requestion_kn_report.rptdesign";
		if (printQueryDialog != null) {
			printQueryDialog.setReportName(report);
			printQueryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(printADTable);
			printQueryDialog =  new PrintQueryDialog(UI.getActiveShell(), tableManager, this);
			printQueryDialog.setReportName(report);
			printQueryDialog.open();
		}
	}
	
	protected void previewBLAdapter() {
		String report = "xz_requestion_bl_report.rptdesign";
		if (printQueryDialog != null) {
			printQueryDialog.setReportName(report);
			printQueryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(printADTable);
			printQueryDialog =  new PrintQueryDialog(UI.getActiveShell(), tableManager, this);
			printQueryDialog.setReportName(report);
			printQueryDialog.open();
		}
	}
	
	protected void previewFWAdapter() {
		String report = "xz_requestion_fw_report.rptdesign";
		if (printQueryDialog != null) {
			printQueryDialog.setReportName(report);
			printQueryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(printADTable);
			printQueryDialog =  new PrintQueryDialog(UI.getActiveShell(), tableManager, this);
			printQueryDialog.setReportName(report);
			printQueryDialog.open();
		}
	}
	
	protected void previewXSAdapter() {
		String report = "xz_requestion_xs_report.rptdesign";
		if (printQueryDialog != null) {
			printQueryDialog.setReportName(report);
			printQueryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(printADTable);
			printQueryDialog =  new PrintQueryDialog(UI.getActiveShell(), tableManager, this);
			printQueryDialog.setReportName(report);
			printQueryDialog.open();
		}
	}
	private ADTable initPrintTable(){
		try{
			String TABLE_NAME = "XZPURRequisitionLineQuery";
			ADManager adManager = Framework.getService(ADManager.class);
			printADTable = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
		}catch(Exception e){
			
		}
		return printADTable;
	}
	
	
	class PrintQueryDialog extends EntityQueryDialog{
		private String reportName =null;
		public PrintQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		@Override
		protected void okPressed() {
			if(!validateQueryKey()) return;
			fillQueryKeys();
			createWhereClause();
			setReturnCode(OK);
	        this.setVisible(false);
	        try {
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	    		params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
	    		
	    		HashMap<String, String> userParams = new HashMap<String, String>();
	    		Map<String, Object> keys = printQueryDialog.getQueryKeys();
	    		for(String name : keys.keySet()){
	    			if("created".equals(name)){
	    				Object val = keys.get(name);
	    				if(val == null || String.valueOf(val).trim().length() == 0){
	    					userParams.put("REPORT_MONTH", null);
	    				}else{
	    					userParams.put("REPORT_MONTH", (String) val);
	    				}
	    			}					
	    		}
	    		String parms = convertParams(userParams);
	    		StringBuffer sf = new StringBuffer();
	    		sf.append("http://192.168.0.193:8080/ERPreport/frameset?__report=");
	    		sf.append(printQueryDialog.getReportName());
	    		sf.append("&__format=html&__resourceFolder=");
	    		sf.append(parms);
	    		Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+sf.toString() );
	    	    
//	    		PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), reportName, params, userParams);
//	    		dialog.open();
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}

		public String getReportName() {
			return reportName;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}
		
		public void createWhereClause() {
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			String modelName = tableManager.getADTable().getModelName() + ".";
			sb = new StringBuffer("");
			
			sb.append(" 1=1 ");
					
	        for(IField f : fields.values()) {
				Object t = f.getValue();
				if (t instanceof Date) {
					Date cc = (Date)t;
					Class<?> clazz = null;
					Field objProperty = null;
					try {
						clazz = Class.forName(printADTable.getModelClass());
						if(clazz != null){
							objProperty = clazz.getDeclaredField(f.getId());
							Field[] fs = clazz.getDeclaredFields();
							assert fs.length != 0;
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					if(objProperty != null){
						if(objProperty.getType().equals(String.class)){
							//��������������String���͵�
							if(cc != null) {
								sb.append(" AND ");
								sb.append(modelName);
								sb.append(f.getId());
								if(FieldType.SHORTDATE.equals(f.getFieldType())){
									sb.append(" = '");
									sb.append(I18nUtil.formatShortDate(cc));
								}else{
									sb.append(" = '");
									sb.append(I18nUtil.formatDate(cc));
								}
								sb.append("'");
							}
						}else if(objProperty.getType().equals(Date.class)){
							//��������������Date���͵�
							if(cc != null) {
								sb.append(" AND ");
								sb.append("TO_CHAR(");
								sb.append(modelName);
								sb.append(f.getId());
								if(FieldType.SHORTDATE.equals(f.getFieldType())){
									sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
									sb.append(I18nUtil.formatShortDate(cc));
								}else{
									sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
									sb.append(I18nUtil.formatDate(cc));
								}
								sb.append("'");
							}
						}
					}else{
						if(cc != null) {
							sb.append(" AND ");
							sb.append("TO_CHAR(");
							sb.append(modelName);
							sb.append(f.getId());
							if(FieldType.SHORTDATE.equals(f.getFieldType())){
								sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
								sb.append(I18nUtil.formatShortDate(cc));
							}else{
								sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
								sb.append(I18nUtil.formatDate(cc));
							}
							sb.append("'");
						}
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
				} else if(t instanceof Map){//ֻ������FromToCalendarField
					Map m = (Map)t;
					Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
					Date to = (Date) m.get(FromToCalendarField.DATE_TO);
					if(from != null) {
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					
					if(to != null){
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
				}
	        }
	        if (getTray() != null) {
	        	AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
	        	String advance = tray.getAdvaceWhereClause();
	        	sb.append(advance);
	        }
		}
		
	}
	
	private static String convertParams(Map<String, String> params) {
		if (params != null && !params.isEmpty()) {
			StringBuffer sb = new StringBuffer();

			for (Entry<String, String> entry : params.entrySet()) {
				sb.append("&").append(entry.getKey()); 

				if (entry.getValue() != null) {
					sb.append("=").append(entry.getValue()); 
				}
			}

			return sb.toString();
		}
		return ""; 
	}
}
