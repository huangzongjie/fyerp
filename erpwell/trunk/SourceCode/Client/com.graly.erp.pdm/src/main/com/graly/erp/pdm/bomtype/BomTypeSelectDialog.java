package com.graly.erp.pdm.bomtype;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class BomTypeSelectDialog extends SingleEntityQueryDialog {

	public BomTypeSelectDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		super(listTableManager, managedForm, whereClause, style);
	}
	
	protected void setTitleImage() {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
	}
	
	protected void setTitleInfo() {
		try{
			String editorTitle = String.format(Message.getString("common.editor"),
					I18nUtil.getI18nMessage(listTableManager.getADTable(), "label"));
			setTitle(editorTitle);
		} catch (Exception e){
		}
	}
}
