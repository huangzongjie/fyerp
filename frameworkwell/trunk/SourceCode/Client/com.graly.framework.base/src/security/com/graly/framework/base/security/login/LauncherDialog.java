package com.graly.framework.base.security.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADOrg;
import com.graly.framework.security.model.ADUser;

public class LauncherDialog extends InClosableTitleAreaDialog {
	private final static Logger logger = Logger.getLogger(LauncherDialog.class);
	private CCombo defOrgCombo;
	protected Button isShowLauncher;
	private ADUser user;
	private static String ORGS = "orgs";
	private static String IS_SHOWLAUNCHER = "isShowLauncher";
	LinkedHashMap<String, String> map;
	
	public LauncherDialog(Shell parent) {
		super(parent);
	}
	
	public LauncherDialog(Shell parent, ADUser user) {
		super(parent);
		this.user = user;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
        setTitleImage(SWTResourceCache.getImage("search-dialog"));
        setTitle(Message.getString("common.title_areaSelection"));
        setMessage(Message.getString("common.info_areaSelection"));
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite content = new Composite(composite, SWT.BORDER);
        GridLayout gl = new GridLayout(1, true);
        gl.verticalSpacing = 40;
        content.setLayout(gl);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Composite comp = new Composite(content, SWT.NULL);
        GridLayout compGl = new GridLayout(2, false);
        compGl.horizontalSpacing = 10;
        comp.setLayout(compGl);
        comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Label label = new Label(comp, SWT.NULL);
        label.setText(Message.getString("common.select_area"));
        defOrgCombo = new CCombo(comp, SWT.BORDER | SWT.READ_ONLY);
        defOrgCombo.setLayoutData(new GridData(GridData.FILL_BOTH));
        toolkit.adapt(defOrgCombo);
        toolkit.paintBordersFor(defOrgCombo);
        
        Composite orgInfCom = new Composite(content, SWT.NONE);
        GridLayout orgInfComGL = new GridLayout(3, false);
        orgInfComGL.horizontalSpacing = 10;
        orgInfCom.setLayout(orgInfComGL);
        orgInfCom.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        if(user != null){
			Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
			List<ADOrg> list = (List<ADOrg>)obj;
			try{
				Collections.sort(list,new ADOrgComparator());
			}catch(Exception collectionException){
				//如排序不正常，不做处理
			}
			
			for(ADOrg userOrg : list){
				ToolBar toolBar = new ToolBar(orgInfCom,SWT.NONE);
				ToolItem toolItem = new ToolItem(toolBar, SWT.BORDER);
				if(userOrg.getObjectRrn().equals(139420L)){
					//开能
					toolItem.setImage(SWTResourceCache.getImage("kaineng"));
				}else if(userOrg.getObjectRrn().equals(139421L)){
					//壁炉
					toolItem.setImage(SWTResourceCache.getImage("bilu"));
				}else if(userOrg.getObjectRrn().equals(12644730L)){
					//12644730奔泰
					toolItem.setImage(SWTResourceCache.getImage("bentai"));
				}else if(userOrg.getObjectRrn().equals(41673024L)){
					//41673024滤芯
					toolItem.setImage(SWTResourceCache.getImage("lvxin"));
				}else if(userOrg.getObjectRrn().equals(42939913L)){
					//42939913备件ERP
					toolItem.setImage(SWTResourceCache.getImage("beijian"));
				}else if(userOrg.getObjectRrn().equals(49204677L)){
					//49204677廊坊
					toolItem.setImage(SWTResourceCache.getImage("langfang"));
				}else if(userOrg.getObjectRrn().equals(43005921L)){
					//43005921行政ERP
					toolItem.setImage(SWTResourceCache.getImage("xzerp"));
				}else if(userOrg.getObjectRrn().equals(63506125L)){
					//饮水机行政ERP
					toolItem.setImage(SWTResourceCache.getImage("ysj"));
				}else if(userOrg.getObjectRrn().equals(70000000L)){
					//饮水机行政ERP
					toolItem.setImage(SWTResourceCache.getImage("huayu"));
				}else if(userOrg.getObjectRrn().equals(68088906L)){
					//饮水机行政ERP
					toolItem.setImage(SWTResourceCache.getImage("yn"));
				}else if(userOrg.getObjectRrn().equals(69573429L)){
					//饮水机行政ERP
					toolItem.setImage(SWTResourceCache.getImage("ynzz"));
				}else{
					//其他区域
					toolItem.setImage(SWTResourceCache.getImage("qita"));
				} 
				
				toolItem.setData(userOrg);
				toolItem.addSelectionListener(new SelectionAdapter(){
				    public void widgetSelected(SelectionEvent event) {
				    ToolItem selectItem = (ToolItem) event.widget;
			    	ADOrg userOrg = (ADOrg) selectItem.getData();
			    	if(userOrg!=null){
			    		modifyLauncher();
			    		Env.setOrgRrn(userOrg.getObjectRrn());
			    		okPressed();
			    	}
				}
				});
//		        Button button = new Button(orgInfCom, SWT.NULL);
//		        button.setData(userOrg);
//		        button.setText("    "+userOrg.getDescription()+"@"+userOrg.getName()+"    ");
//		        button.setImage(SWTResourceCache.getImage("print"));
//				button.addSelectionListener(new SelectionAdapter() {
//				    public void widgetSelected(SelectionEvent event) {
//				    	Button button = (Button) event.widget;
//				    	ADOrg userOrg = (ADOrg) button.getData();
//				    	if(userOrg!=null){
//				    		Env.setOrgRrn(userOrg.getObjectRrn());
//				    		okPressed();
//				    	}
//					}
//					});
//				Label spaceLabel = new Label(orgInfCom, SWT.NULL);
//				spaceLabel.setText("    ");
			}
			
//        	List<ADOrg> list =  new ArrayList<ADOrg>();
//        	try {
//				ADManager adManager = Framework.getService(ADManager.class);
//				list = adManager.getEntityList(Env.getOrgRrn(), ADOrg.class,Integer.MAX_VALUE," code is not null ",null);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
////        	Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
////        	List<ADOrg> list = (List<ADOrg>)obj;
//        	for(ADOrg adOrg :list){
//        		Label orgLabel = new Label(orgInfCom,SWT.NONE);
//        		StringBuffer sf= new StringBuffer();
//        		sf.append(adOrg.getName());
//        		sf.append(" : ");
//        		sf.append(adOrg.getDescription());
//        		sf.append("		");
//        		orgLabel.setText(sf.toString());
//        		orgLabel.setForeground(SWTResourceCache.getColor("Red"));
//        	}
        }
        orgInfCom.pack();
        isShowLauncher = new Button(content, SWT.CHECK);
        isShowLauncher.setText(Message.getString("common.showLauncher_next"));
        isShowLauncher.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        initContent();
        
        return composite;
    }
	
	protected void initContent() {
		if(user != null) {
			Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
			List<ADOrg> list = (List<ADOrg>)obj;
			List<String> orgNames = new ArrayList<String>();
			Long defOrgId = user.getDefaultOrgRrn();
			map = new LinkedHashMap<String, String>();
			int i = 0, index = 0;
			for(ADOrg org : list) {
				map.put(org.getName(), org.getObjectRrn().toString());
				orgNames.add(org.getName());
				if(defOrgId != null) {
					if(defOrgId.equals(org.getObjectRrn())) {
						index = i;
					}
				}
				i++;
			}
			defOrgCombo.setItems(orgNames.toArray(new String[]{}));
			defOrgCombo.setText(orgNames.get(index));
			obj = PropertyUtil.getPropertyForIField(user, IS_SHOWLAUNCHER);
			if(obj != null) {
				isShowLauncher.setSelection((Boolean)obj);
			}
		}
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.OK_ID) {
			if(!"".equals(defOrgCombo.getText().trim())) {
				if(user != null) {
					if(!user.getIsShowLauncher().equals(isShowLauncher.getSelection())) {
						user.setIsShowLauncher(isShowLauncher.getSelection());
						try {
							ADManager manager = Framework.getService(ADManager.class);
							ADTable table = manager.getADTable(Env.getOrgRrn(), "ADUser");
							user = (ADUser)manager.getEntity(user);
							manager.saveEntity(table.getObjectRrn(), user, Env.getUserRrn());
						} catch(Exception e) {
							logger.error("svae User Error at Launcher : buttonPressed(): " + e);
						}
					}
				}
				if(map != null) {
					String selectOrgId = map.get(defOrgCombo.getText().trim());
					Env.setOrgRrn(Long.parseLong(selectOrgId));
				}
			} else {
				UI.showError("You Must Select a Area !");
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
	
	@Override
    protected void okPressed() {
        super.okPressed();
    }
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		IDialogConstants.OK_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
    protected void modifyLauncher() {
		if(user != null) {
			if(!user.getIsShowLauncher().equals(isShowLauncher.getSelection())) {
				user.setIsShowLauncher(isShowLauncher.getSelection());
				try {
					ADManager manager = Framework.getService(ADManager.class);
					ADTable table = manager.getADTable(Env.getOrgRrn(), "ADUser");
					user = (ADUser)manager.getEntity(user);
					manager.saveEntity(table.getObjectRrn(), user, Env.getUserRrn());
				} catch(Exception e) {
					logger.error("svae User Error at Launcher : buttonPressed(): " + e);
				}
			}
		}
    }
    class ADOrgComparator implements Comparator<ADOrg>{

		@Override
		public int compare(ADOrg o1, ADOrg o2) {
			if(o1 != null && o1 != null) {
				if(o1.getCreated() != null && o2.getCreated() != null);
				return o1.getCreated().compareTo(o2.getCreated());
			}
			return 0;
		}
    	
    }
    
}
