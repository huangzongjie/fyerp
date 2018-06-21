package com.graly.alm.util;

import java.util.List;

import com.graly.alm.model.Action;
import com.graly.alm.model.AlarmHis;

public abstract class AlarmAction {
	
	protected Long orgRrn;
	protected Long hisRrn;
	protected String param1;
	protected String param2;
	protected String param3;
	protected String param4;
	protected String param5;

	public static void excuteAction(List<Action> actionList, AlarmHis his) {
		for (int i = 0; i < actionList.size(); i++) {
			try {
				Action action = (Action) actionList.get(i);	
				String programId = action.getActionType().getProgramId();
				Object obj = Class.forName(programId).newInstance();
				if (obj instanceof AlarmAction) {
					AlarmAction alarmAction = (AlarmAction)obj;
					alarmAction.orgRrn = his.getOrgRrn();
					alarmAction.hisRrn = his.getObjectRrn();
					alarmAction.param1 = action.getParam1();
					alarmAction.param2 = action.getParam2();
					alarmAction.param3 = action.getParam3();
					alarmAction.param4 = action.getParam4();
					alarmAction.param5 = action.getParam5();
					alarmAction.excute();
				}
				
			} catch (Exception e) {
			}
		}
	}
	
	public abstract void excute();
}
