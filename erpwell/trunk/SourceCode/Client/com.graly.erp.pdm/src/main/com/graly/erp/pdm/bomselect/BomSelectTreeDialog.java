package com.graly.erp.pdm.bomselect;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomTreeDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomSelectTreeDialog extends BomTreeDialog {
	private static Logger logger = Logger.getLogger(BomSelectTreeDialog.class);
	protected ADManager adManager;

	public BomSelectTreeDialog(Shell parent, IManagedForm form, Material material) {
		super(parent, form, material);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemVerify(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExpendAll(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemView(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemReferenceDoc(tBar);
		section.setTextClient(tBar);
		// 初始化按钮状态是否可用
		setStatusChanged();
	}

	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editorwAdapter(event);
			}
		});
	}
	
	protected void createToolItemVerify(ToolBar tBar) {
		itemVerify = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_VERIFY);
		itemVerify.setText(Message.getString("pdm.bom_verify"));
		itemVerify.setImage(SWTResourceCache.getImage("approve"));
		itemVerify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				verifyAdapter(event);
			}
		});
	}
	
	protected void createToolItemExpendAll(ToolBar tBar) {
		itemExpend = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_EXPAND);
		itemExpend.setText(Message.getString("pdm.bom_expend_all"));
		itemExpend.setImage(SWTResourceCache.getImage("report"));
		itemExpend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				expendAllAdapter(event);
			}
		});
	}
	
	protected void createToolItemView(ToolBar tBar) {
		itemView = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_PREVIEW);
		itemView.setText(Message.getString("common.print"));
		itemView.setImage(SWTResourceCache.getImage("preview"));
		itemView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewAdapter();
			}
		});
	}
	
	protected void createToolItemReferenceDoc(ToolBar tBar) {
		itemReferenceDoc = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM_REFERENCEDOC);
		itemReferenceDoc.setText(Message.getString("bas.refence_doc"));
		itemReferenceDoc.setImage(SWTResourceCache.getImage("search"));
		itemReferenceDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				refreenceDocAdapter();
			}
		});
	}
	
	protected void editorwAdapter(SelectionEvent event) {
		try {
			if(material != null) {
				BomSelectEditTreeDialog editDialog = new BomSelectEditTreeDialog(UI.getActiveShell(), form, material);
				if(editDialog.open() == Dialog.CANCEL) {
					if(adManager == null)
						adManager = Framework.getService(ADManager.class);
					material = (Material)adManager.getEntity(material);
					bomTreeForm.refresh(material);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at: ", e);
		}
	}
	
	protected void setStatusChanged() {		
		Long bomRrn = material.getBomRrn();
		if(bomRrn != null && bomRrn.longValue() != 0) {
			isAlternate = false;
			isEnable = false;
		} else {
			isEnable = true;
			isAlternate = true;
		}
		setEnable(false);
		setIsAlternate(false);
	}
	
	public void setEnable(boolean isEnableFromExternal) {}
}
