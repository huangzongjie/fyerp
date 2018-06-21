package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;
import com.graly.mes.prd.designer.common.model.SemanticElementFactory;
import com.graly.mes.prd.designer.model.Action;
import com.graly.mes.prd.designer.model.Timer;
import com.graly.mes.prd.designer.model.TimerContainer;

public class TimerCreateCommand extends Command {
	
	private TimerContainer timerContainer;
	private Timer timer;
	private SemanticElementFactory factory;
	
	public TimerCreateCommand(SemanticElementFactory factory) {
		this.factory = factory;
	}
	
	public void execute() {
		if (timer == null) {
			timer = (Timer)factory.createById("com.graly.mes.prd.designer.timer");
			timer.setAction((Action)factory.createById("com.graly.mes.prd.designer.action"));
		}
		timerContainer.addTimer(timer);
	}
	
	public void undo() {
		timerContainer.removeTimer(timer);
	}
	
	public void setTimerContainer(TimerContainer timerContainer) {
		this.timerContainer = timerContainer;
	}
	
}
