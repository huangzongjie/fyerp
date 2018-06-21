package com.graly.erp.wip.storage;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.Storage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;

/**
 * ���������
 * */
public class StorageDifferenceProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(StorageDifferenceProperties.class);
 
	public StorageDifferenceProperties() {
		super();
	}

	public StorageDifferenceProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table);
	}
   

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	
	/*
	 *�ɹ����ϱ����вɹ�Ȩ�ޣ������޸Ĳ������� 
	 * */
	@Override
	protected void saveAdapter() {
		Storage storage = (Storage) getAdObject();
		Material material = storage.getMaterial();
		if(material.getIsPurchase()){
			boolean hasPurchase = false;//�Ƿ��вɹ�Ȩ��
			if (Env.getAuthority() != null) {
				if (Env.getAuthority().contains("INV.StorageDiff.Save.Purchase")) {
					hasPurchase =true;
				} else {
					hasPurchase = false;
				}
			} else {
				hasPurchase = false;
			}
			if(hasPurchase){
				super.saveAdapter();
			}else{
				UI.showError("�ɹ����ϱ�����Ҫ�ɹ�Ȩ��,����ϵ�ɹ�����");
			}
		}else{
			super.saveAdapter();
		}
	}
	 
}
