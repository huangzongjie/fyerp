package com.graly.erp.pdm.bomhistory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class BomContrastDialog extends BomHisVersionDialog {
	protected Long version;
	protected Material material;
	protected Long newVersion;
	
	public BomContrastDialog(Shell parentShell, Material material,
			IManagedForm form,Boolean bool,Long version) {
		super(parentShell, material, form);
		this.version=version;
		this.material=material;
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSelectHistory(tBar);
		
		section.setTextClient(tBar);	
	}
	
	public void createToolItemSelectHistory(ToolBar tBar){
		itemHistory = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_PREVIEW);
		itemHistory.setText("¶Ô±È");
		itemHistory.setImage(SWTResourceCache.getImage("preview"));
		//itemHistory.setEnabled(false);
		itemHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				contrastAdapter();
			}
		});
	}
		
	@Override
	public void contrastAdapter(){ 
		if(selectBom!=null){
			newVersion=selectBom.getParentVersion();
			BomConTreeDialog btd = new BomConTreeDialog(UI.getActiveShell(), form, material, false,version,newVersion);
			if(btd.open() == Dialog.CANCEL) {
				
			}
		}
	}
	
	@Override
	protected List<Bom> getBomHisVersion(Object object){
		try {
			if(object instanceof Material){
				List<Bom> bomList=new ArrayList<Bom>();
				Material material = (Material)object;
				manager=Framework.getService(PDMManager.class);
				List<Object[]> objList=manager.getBomContrastVersion(material.getObjectRrn(), version);
				for(Object[] objs:objList){
					BigDecimal version= (BigDecimal)objs[0];
					Bom vBom=new Bom();
					vBom.setParentVersion(version.longValue());
					String string=objs[3]==null?"":(String)objs[3];
					vBom.setUserName(string);
					if(objs[1] != null){
						String updatedStr=objs[1].toString();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						Date udate=sdf.parse(updatedStr);
						vBom.setUpdated(udate);
					}
					bomList.add(vBom);
					
				}
				return input=bomList;
			}
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}


}