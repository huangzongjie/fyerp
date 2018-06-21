package com.graly.mes.prd.designer.command;

import org.eclipse.gef.commands.Command;

import com.graly.mes.prd.designer.model.Timer;
import com.graly.mes.prd.designer.model.TimerContainer;

public class TimerDeleteCommand extends Command {
	
	private TimerContainer timerContainer;
	private Timer timer;
	
	public void execute() {
		timerContainer.removeTimer(timer);
	}
	
	public void undo() {
		timerContainer.addTimer(timer);
	}
	
	public void setTimerContainer(TimerContainer timerContainer) {
		this.timerContainer = timerContainer;
	}
	
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
}
