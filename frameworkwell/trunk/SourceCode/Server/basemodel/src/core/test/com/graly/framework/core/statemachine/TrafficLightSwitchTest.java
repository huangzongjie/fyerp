package com.graly.framework.core.statemachine;

import junit.framework.TestCase;

public class TrafficLightSwitchTest extends TestCase {
	
	public void testTrunOn(){
		try{
			TrafficLightStateMachine ls = new TrafficLightStateMachine(TrafficLightStateMachine.STATE_OFF);
			State state = ls.send(TrafficLightStateMachine.EVENT_ON);
			String stateId = state.getStateId();
		} catch (Exception e) {
				
		}
	}
	
	public void testTimeElapsed(){
		try{
			TrafficLightStateMachine ls = new TrafficLightStateMachine(TrafficLightStateMachine.STATE_ON);
			State state = ls.send(TrafficLightStateMachine.EVENT_TIMEELAPSED);
			String stateId = state.getStateId();
		} catch (Exception e) {
			
		}
	}
	
	public void testTrunOff(){
		try{
			TrafficLightStateMachine ls = new TrafficLightStateMachine(TrafficLightStateMachine.STATE_ON);
			State state = ls.send(TrafficLightStateMachine.EVENT_OFF);
			String stateId = state.getStateId();
		} catch (Exception e) {
			
		}
	}
}
