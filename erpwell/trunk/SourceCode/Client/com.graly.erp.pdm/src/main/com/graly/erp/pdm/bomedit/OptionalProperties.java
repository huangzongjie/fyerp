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
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OptionalProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(OptionalProperties.class);
	public static final String OPTIONAL_MATERIAL = "(可选择料)";
	private static Long childRrn;
	private static Bom optionalBom;
	private ADManager adManager;

	public static void setChildRrn(Long materialRrn) {
		OptionalProperties.childRrn = materialRrn;
	}
	
	@Override
	protected void createToolItemNew(ToolBar bar) {
		itemNew = new AuthorityToolItem(bar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_OPTIONAL_NEW);	
		
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
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_OPTIONAL_SAVE);	
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
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_OPTIONAL_DELETE);	
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
		MaterialOptional optional = (MaterialOptional)getAdObject().getClass().newInstance();
		Material material = (Material)this.getParentObject();
		optional.setOrgRrn(material.getOrgRrn());
		optional.setChildRrn(childRrn);
		return optional;
	}
	
	public static void setEnableOptionalBom(Bom bom) {
		OptionalProperties.optionalBom = bom;
	}
	
	@Override
	protected void deleteAdapter(){
		ADBase oldBase = getAdObject();
		boolean deleteFlag = delete();
		if (deleteFlag) {
			getMasterParent().refreshDelete(oldBase);
			try {
				if(adManager == null) {
					adManager = Framework.getService(ADManager.class);
				}
				long count = adManager.getEntityCount(Env.getOrgRrn(), getTable().getObjectRrn(),
						createWhereClause(((Material)parentObject).getObjectRrn()));
				if(count == 0) {
					// 如果没有可选料并且备注以PREFIX开头，则将备注中PREFIX信息去掉
					if(optionalBom.getDescription() != null
							&& optionalBom.getDescription().indexOf(OPTIONAL_MATERIAL) != -1) {
						optionalBom.setDescription(optionalBom.getDescription().substring(OPTIONAL_MATERIAL.length()));
					}
					
					adManager.saveEntity(optionalBom, Env.getUserRrn());
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				logger.error("Error at OptionalProperties.delete() ", e1);
			}
		}
	}

	@Override
	public boolean save() {
		boolean save = super.save();
		try {
			if(save) {
				// 如果保存成功并且optionalBom备注不含有PREFIX信息，则在备注中添加PREFIX信息
				if(optionalBom.getDescription() == null
						|| optionalBom.getDescription().indexOf(OPTIONAL_MATERIAL) == -1) {
					String des = optionalBom.getDescription() == null ? OPTIONAL_MATERIAL : OPTIONAL_MATERIAL + optionalBom.getDescription();
					optionalBom.setDescription(des);
					
					if(adManager == null) {
						adManager = Framework.getService(ADManager.class);
					}
					adManager.saveEntity(optionalBom, Env.getUserRrn());
					form.getMessageManager().removeAllMessages();
				}
			}
		} catch(Exception e) {
			logger.error("Error at OptionalProperties.save() ", e);
		}
		return save;
	}
	
	private String createWhereClause(long parentRrn) {
		return " materialRrn = " + parentRrn + " AND childRrn = " + childRrn;
	}
}
