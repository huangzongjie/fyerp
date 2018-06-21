package com.graly.framework.core.statemachine;

public class LightSwitchStateMachine extends AbstractStateMachine {
	
    private static State on;
    private static State off;
    
    public static String STATE_ON = "ON";
    public static String STATE_OFF = "OFF";
    
    public static String EVENT_ON = "TURN_ON";
    public static String EVENT_OFF = "TURN_OFF";
    
	static {
		try{
			on = new State(STATE_ON);
			off = new State(STATE_OFF);
			
	        Transition trans;
	        trans = new Transition(on);
	        off.getTransitions().add(EVENT_ON, trans);
	
	        trans = new Transition(off);
	        on.getTransitions().add(EVENT_OFF, trans);
		} catch (Exception e) {
			
		}
	}
	
	public LightSwitchStateMachine() {
		try{
			initialize(off);
		} catch (Exception e) {
			
		}
	}
	
}
