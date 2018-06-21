package com.graly.erp.inv.alarm;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.AlarmTarget;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.ADUserGroup;

public class AlarmJob extends Job {

	private static final Logger logger = Logger.getLogger(AlarmJob.class);
	
	public static boolean alarmUser =false;
	public static long alarmTime =600000;
	public static AlarmTarget alarmTarget;
	public AlarmJob() {
		super("收货检验警报");
		init();
	}
	/**
	 * 1.初始化系统警报时间
	 * 2.判断用户是否需要警报
	 * */
	public void init(){
	//判断用户是否在警报处理当中
		try {
			ADManager adManager = Framework.getService(ADManager.class);
//			ADRefList  adRefList = new ADRefList();
//			adRefList.setObjectRrn(42717070L);
//			adRefList = (ADRefList) adManager.getEntity(adRefList);//获取警报触发间隔时间
//			alarmTime = Long.parseLong(adRefList.getValue());
			List<AlarmTarget> targets = adManager.getEntityList(Env.getOrgRrn(), AlarmTarget.class,Integer.MAX_VALUE,"1=1",null);
			if(targets!=null && targets.size()>0){
				for(AlarmTarget target : targets){
					if(target.getUserGroupRrn()!=null){
						ADUserGroup userGroup = new ADUserGroup();
						userGroup.setObjectRrn(target.getUserGroupRrn());
						userGroup = (ADUserGroup) adManager.getEntity(userGroup);
						List<ADUser> groupUsers= userGroup.getUsers();
						if(groupUsers!=null && groupUsers.size()>0){
							for(ADUser groupUser :groupUsers){
								if(groupUser.getObjectRrn().equals(Env.getUserRrn()) && target.getField1()!=null){
									alarmUser =true;
									alarmTarget = target;
									break;
								}
							}
						}
					}else if(target.getUserRrn()!=null && target.getField1()!=null){
						if(target.getUserRrn().equals(Env.getUserRrn())){
							alarmTarget = target;
							alarmUser =true;
							break;
						}
					}
					
				}
				
			}
			if(alarmTarget!=null){
				alarmTime = Long.parseLong(alarmTarget.getField1());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 1.查找属于当前用户的警报
	 * 2.根据不同的警报弹出不同的警报对话框
	 * */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			if(alarmUser){
				//如果是警报对象每1秒去数据库捞取数据
				while(true){
					Display.getDefault().asyncExec(new Runnable() {
			            public void run() {
			            	List<AlarmData> alarmDatas  = new ArrayList<AlarmData>();
			            	if(alarmTarget.getTargetType().equals(AlarmTarget.TARGET_TYPE_IQC)){
			            		try {
			            			ADManager adManager = Framework.getService(ADManager.class);
			            			alarmDatas = adManager.getEntityList(Env.getOrgRrn(), AlarmData.class,Integer.MAX_VALUE,
			            				" status ='"+AlarmData.STATUS_OPEN+"'"+" and alarmType ='"+AlarmTarget.TARGET_TYPE_IQC+"'","field1 desc");
			            		} catch (Exception e) {
			            			e.printStackTrace();
			            		}
			            		if(alarmDatas!=null && alarmDatas.size()>0){
					            	AlarmDialog alarmDialog = new AlarmDialog(Display.getCurrent().getActiveShell(),null,null,alarmDatas);
					            	alarmDialog.setBlockOnOpen(true);
					            	//----------------------
					        		try {
					        			ClassLoader  loader  =  AlarmJob.class.getClassLoader();
					        			URL url = loader.getResource("icons/toadload.wav");
						        		File f = new File("icons/toadload.wav");
//						        		URL url = null;
//					        			url = f .toURL();
						        		AudioClip music=Applet.newAudioClip(url);
						        	    music.play();//开始
						        	    Thread.sleep(1000);
						        	    music.stop();
					        		} catch (Exception e) {
					        			e.printStackTrace();
					        		}
					            	//-----------------------
					            	if(alarmDialog.open() == Dialog.OK){
					            	}
			            		}

			            	}else if(alarmTarget.getTargetType().equals(AlarmTarget.TARGET_TYPE_WAREHOUSE)){
			            		try {
			            			ADManager adManager = Framework.getService(ADManager.class);
			            			alarmDatas = adManager.getEntityList(Env.getOrgRrn(), AlarmData.class,Integer.MAX_VALUE,"  status ='"+AlarmData.STATUS_OPEN+"'" 
			            					+" and alarmType ='"+AlarmTarget.TARGET_TYPE_WAREHOUSE+"'","field1 desc");
			            		} catch (Exception e) {
			            			e.printStackTrace();
			            		}
			            		if(alarmDatas!=null && alarmDatas.size()>0){
				            		AlarmWareHouseDialog alarmWareHouseDialog = new AlarmWareHouseDialog(Display.getCurrent().getActiveShell(),null,null,alarmDatas);
				            		alarmWareHouseDialog.setBlockOnOpen(true);
					            	//----------------------
					        		try {
						        		File f = new File("icons/toadload.wav");
						        		URL url = null;
					        			url = f .toURL();
						        		AudioClip music=Applet.newAudioClip(url);
						        	    music.play();//开始
						        	    Thread.sleep(1000);
						        	    music.stop();
					        		} catch (Exception e) {
					        			e.printStackTrace();
					        		}
					            	//-----------------------				            	
				            		if(alarmWareHouseDialog.open() == Dialog.OK){
					            	}
			            		}
			            	}else if(alarmTarget.getTargetType().equals(AlarmTarget.TARGET_TYPE_SERVICE)){
			            		List<Material> materials =null;
			            		try {
			            			INVManager invManager = Framework.getService(INVManager.class);
			            			materials = invManager.getServiceMaterialAlarm(Env.getOrgRrn(),null);
			            		} catch (Exception e) {
			            			e.printStackTrace();
			            		}
			            		if(materials!=null && materials.size()>0){
				            		AlarmServiceDialog alarmServiceDialog = new AlarmServiceDialog(Display.getCurrent().getActiveShell(),null,null,materials);
				            		alarmServiceDialog.setBlockOnOpen(true);
					            	//-----------------------				            	
				            		if(alarmServiceDialog.open() == Dialog.OK){
					            	}
			            		}
			            	
			            	}
			            }
			            });
					Thread.sleep(alarmTime);
				}
			} 
			return Status.OK_STATUS;
		} catch(Exception e){
			logger.error("AlarmJmsJob run : " + e.getMessage(), e);		
		} finally {
		}
		return Status.CANCEL_STATUS;
	}
}
