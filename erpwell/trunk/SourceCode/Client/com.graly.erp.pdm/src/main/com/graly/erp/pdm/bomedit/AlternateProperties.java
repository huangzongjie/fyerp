package com.graly.erp.pdm.bomedit;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.MaterialAlternate;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AlternateProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(AlternateProperties.class);
	private static final String PREFIX = "(可替代料)";
	private static Long childRrn;
	private static String path;
	private static Bom alterBom;
	private ADManager adManager;
	
	public static void setChildRrn(Long materialRrn, String path) {
		AlternateProperties.childRrn = materialRrn;
		AlternateProperties.path = path;
	}
	
	@Override
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_ALTERNATE_NEW);	
		
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_ALTERNATE_SAVE);	
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_ALTERNATE_DELETE);	
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
	
	@Override
	public ADBase createAdObject() throws Exception {
		MaterialAlternate alternate = (MaterialAlternate)getAdObject().getClass().newInstance();
		Material material = (Material)this.getParentObject();
		alternate.setOrgRrn(material.getOrgRrn());
		alternate.setChildRrn(childRrn);
		alternate.setPath(path);
		return alternate;
	}
	
	public static void setEnableAlterBom(Bom bom) {
		AlternateProperties.alterBom = bom;
	}
	
	@Override
	protected void deleteAdapter(){
		ADBase oldBase = getAdObject();
		boolean deleteFlag = super.delete();
		if (deleteFlag) {
			getMasterParent().refreshDelete(oldBase);
			try {
				if(adManager == null) {
					adManager = Framework.getService(ADManager.class);
				}
				long count = adManager.getEntityCount(Env.getOrgRrn(), getTable().getObjectRrn(),
						createWhereClause(((Material)parentObject).getObjectRrn()));
				if(count == 0) {
					// 如果没有可替代并且备注中含有PREFIX信息，则将备注中PREFIX信息去掉
					if(alterBom.getDescription() != null
							&& alterBom.getDescription().indexOf(PREFIX) != -1) {
						alterBom.setDescription(alterBom.getDescription().substring(PREFIX.length()));
					}
					
					adManager.saveEntity(alterBom, Env.getUserRrn());
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				logger.error("Error at AlternateProperties.delete() ", e1);
			}
		}
	}

	@Override
	public boolean save() {
		boolean save = super.save();
		try {
			if(save) {
				// 如果保存成功并且alterBom备注没有PREFIX信息，则在备注中添加PREFIX信息
				if(alterBom.getDescription() == null
						|| alterBom.getDescription().indexOf(PREFIX) == -1) {
					String des = alterBom.getDescription() == null ? PREFIX : PREFIX + alterBom.getDescription();
					alterBom.setDescription(des);
					
					if(adManager == null) {
						adManager = Framework.getService(ADManager.class);
					}
					adManager.saveEntity(alterBom, Env.getUserRrn());
					form.getMessageManager().removeAllMessages();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at AlternateProperties.save() ", e);
		}
		return save;
	}
	
	private String createWhereClause(long rootRrn) {
		return " materialRrn = " + rootRrn + " AND path = '" + path + "' AND childRrn = " + childRrn;
	}
}
