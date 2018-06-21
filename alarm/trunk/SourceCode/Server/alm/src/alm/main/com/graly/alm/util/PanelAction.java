package com.graly.alm.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;

import com.graly.alm.model.AlarmPanelMessage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.ADUserGroup;
import com.graly.alm.helper.AlarmHelper;

public class PanelAction extends AlarmAction {

	private static final Logger logger = Logger.getLogger(PanelAction.class);

	public static final String SUBJECT_PANEL = "GLORY/ALARM/PANEL";
	
	@EJB
	private ADManager adManager;
	
	public PanelAction() {
	}

	public PanelAction(String param1, String param2, String param3,
			String param4, String param5) {
		this.param1 = param1;	// to(usergroup/user)
		this.param2 = param2;	// 
		this.param3 = param3;	// 
		this.param4 = param4;	// 
		this.param5 = param5;	// 
	} 

	@Override
	public void excute() {
		try {
			List<Long> userRrnList = new ArrayList<Long>();
			String[] tos = this.param1.split(";");
			for (String to : tos) {
				String whereClause = " name = '" + to + "'";
				List<ADUserGroup> usergroups = adManager.getEntityList(this.orgRrn, ADUserGroup.class, Integer.MAX_VALUE, whereClause, "");
				if (usergroups.size() > 0) {
					ADUserGroup usergroup = usergroups.get(0);
					usergroup = (ADUserGroup)adManager.getEntity(usergroup);
					List<ADUser> users = usergroup.getUsers();
					for (ADUser user : users) {
						userRrnList.add(user.getObjectRrn());
					}
				} else {
					whereClause = " userName = '" + to + "'";
					List<ADUser> users = adManager.getEntityList(this.orgRrn, ADUser.class, Integer.MAX_VALUE, whereClause, "");
					for (ADUser user : users) {
						userRrnList.add(user.getObjectRrn());
					}
				}
			}
			
			for (Long userRrn : userRrnList) {
				AlarmPanelMessage panel = new AlarmPanelMessage();
				panel.setOrgRrn(this.orgRrn);
				panel.setIsActive(true);
				panel.setCreated(new Date());
				panel.setUserRrn(userRrn);
				panel.setAlarmHisRrn(this.hisRrn);
				panel.setState(AlarmPanelMessage.STATE_OPEN);
				adManager.saveEntity(panel, 0);
				AlarmHelper.sendObjectMessage(SUBJECT_PANEL, panel, userRrn.toString());
			}
			
		} catch (Exception e) {
			logger.error("Sending alarm eMail failed.", e);
		} 

	}

}
