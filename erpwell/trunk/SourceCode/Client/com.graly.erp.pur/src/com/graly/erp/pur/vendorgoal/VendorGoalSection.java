package com.graly.erp.pur.vendorgoal;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;

import com.graly.erp.pur.client.PURManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.uiX.viewmanager.XTableViewerManager;
import com.graly.framework.base.uiX.viewmanager.forms.XMasterSection;
import com.graly.framework.runtime.Framework;

public class VendorGoalSection extends XMasterSection {
	private static final Logger logger = Logger.getLogger(VendorGoalSection.class);
	
	private static final String TABLE_PO_NAME = "PURVPoLine";
	protected ADTable adTablePo;
	
	public VendorGoalSection() {
		super();
	}

	public VendorGoalSection(XTableViewerManager tableManager) {
		super(tableManager);
	}
	
	@Override
	public String getViewerContents() {
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			if(!queryKeys.containsKey("MONTH")){
				Calendar cal = Calendar.getInstance();
				queryKeys.put("MONTH", (cal.get(Calendar.MONTH)+1)+"");
			}
			String s = purManager.queryVendorPurGoal(queryKeys, Env.getOrgRrn(), null);
			return (s!=null&&s.trim().length()>0)?s:null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	protected ADTable getADTableOfPO() {
		try {
			if (adTablePo == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTablePo = entityManager.getADTable(0L, TABLE_PO_NAME);
				adTablePo = entityManager.getADTableDeep(adTablePo.getObjectRrn());
			}
			return adTablePo;
		} catch (Exception e) {
			logger.error("PoQuerySection : getADTableOfPO", e);
		}
		return null;
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				String itemData = (String) selection.getFirstElement();
				String whereClause = null;
				if(itemData.contains("VENDOR_RRN=")){
					String data = itemData;
					int start = data.indexOf("VENDOR_RRN=");
					start+=("VENDOR_RRN=".length());
					data = data.substring(start);
					if(data.contains(",")){
						int end = data.indexOf(",");
						data = data.substring(0, end);
					}
					whereClause = "vendorRrn="+data;
				}
				
				if(itemData.contains("YEAR=")){
					String data = itemData;
					int start = data.indexOf("YEAR=");
					start+=("YEAR=".length());
					data = data.substring(start);
					if(data.contains(",")){
						int end = data.indexOf(",");
						data = data.substring(0, end);
					}
					if(whereClause == null){
						whereClause = " to_char(dateApproved,'yyyy')='"+data+"' ";
					}else{
						whereClause += " AND to_char(dateApproved,'yyyy')='"+data+"' ";
					}
				}
				
				if(itemData.contains("PURCHASER=")){
					String data = itemData;
					StringBuffer subWhereClause = new StringBuffer();
					int start = data.indexOf("PURCHASER=");
					start+=("PURCHASER=".length());
					data = data.substring(start);
					if(data.contains(",")){
						int end = data.indexOf(",");
						data = data.substring(0, end);
					}
					
					if(data != null && data.trim().length() > 0){
						if(data.contains("+")){
							String[] purchasers = data.split("\\+");
							int i=0;
							for(String purchaser : purchasers){
								if((i++)>0){
									subWhereClause.append(" OR ");
								}
								subWhereClause.append(" purchaser='"+purchaser+"' ");
							}
						}else{
							subWhereClause.append(" purchaser='"+data+"' ");
						}
					}
					if(whereClause == null){
						whereClause = " ("+subWhereClause.toString()+") ";
					}else{
						whereClause += " AND ("+subWhereClause.toString()+") ";
					}
				}
				
				PoQuerySectionDialog pqd = new PoQuerySectionDialog(UI.getActiveShell(),getADTableOfPO(), whereClause==null?" 1<>1 ":whereClause);
				pqd.open();
			}});
	}
}
