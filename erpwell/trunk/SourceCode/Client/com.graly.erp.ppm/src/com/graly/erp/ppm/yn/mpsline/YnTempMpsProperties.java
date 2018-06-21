package com.graly.erp.ppm.yn.mpsline;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.pur.client.PURManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

/*2. 临时采购计划
当  (库存+在途）< 安全库存，  则 采购量=安全库存
每晚系统自动运算
1.  如果   需求<=安全库存， 则  采购量=2*安全库存-（库存+在途）
2.  如果   需求>安全库存，  则  采购量=安全库存+需求-（库存+在途）
*
*/
public class YnTempMpsProperties extends EntityProperties {
	
	protected ToolItem itemGenRequisition;
	protected ToolItem itemYnTempMps;
	
	public YnTempMpsProperties() {
		super();
	}

	public YnTempMpsProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemGenRequisition(tBar);
		createToolItemSetYnTempMps(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	 
	}

	protected void createToolItemGenRequisition(ToolBar tBar) {
		itemGenRequisition = new ToolItem(tBar, SWT.PUSH);
		itemGenRequisition.setText("临时计划当天运算");
		itemGenRequisition.setImage(SWTResourceCache.getImage("save"));
		itemGenRequisition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				genRequisition();
			}
		});
	}
	
	protected void createToolItemSetYnTempMps(ToolBar tBar) {
		itemYnTempMps = new ToolItem(tBar, SWT.PUSH);
		itemYnTempMps.setText("初始数据");
		itemYnTempMps.setImage(SWTResourceCache.getImage("save"));
		itemYnTempMps.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setYnTempMps();
			}
		});
	}
	
	protected void genRequisition() {
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			purManager.generateYnTempMps(Env.getOrgRrn(),68088940L,Env.getUserRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void setYnTempMps() {
		try {
			PURManager purManager = Framework.getService(PURManager.class);
			purManager.generateYnTempMps();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
}
