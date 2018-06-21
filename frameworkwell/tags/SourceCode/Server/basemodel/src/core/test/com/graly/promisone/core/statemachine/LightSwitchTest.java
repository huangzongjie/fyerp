package com.graly.promisone.core.statemachine;

import junit.framework.TestCase;

public class LightSwitchTest extends TestCase {
	
	public void testTrunOn(){
		try{
			LightSwitchStateMachine ls = new LightSwitchStateMachine();
			State state = ls.send(LightSwitchStateMachine.EVENT_ON);
			String stateId = state.getStateId();
		} catch (Exception e) {
			
		}
	}
	
	public void testTrunOff(){
		try{
			LightSwitchStateMachine ls = new LightSwitchStateMachine();
			State state = ls.send(LightSwitchStateMachine.EVENT_OFF);
			String stateId = state.getStateId();
		} catch (Exception e) {
			
		}
	}
}
