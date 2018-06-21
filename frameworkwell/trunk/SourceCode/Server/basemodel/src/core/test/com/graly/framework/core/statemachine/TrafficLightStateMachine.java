package com.graly.framework.core.statemachine;

import java.util.HashMap;
import java.util.Map;

public class TrafficLightStateMachine extends AbstractStateMachine {
    
	private static State on;
    private static State off;
    private static State red;
    private static State yellow;
    private static State green;
    
    
    public static String STATE_ON = "ON";
    public static String STATE_OFF = "OFF";
    public static String STATE_RED = "RED";
    public static String STATE_YELLOW = "YELLOW";
    public static String STATE_GREEN = "GREEN";
    
    public static String EVENT_ON = "TURN_ON";
    public static String EVENT_OFF = "TURN_OFF";
    public static String EVENT_TIMEELAPSED = "TIME_ELAPSED";
    
    public static Map<String, State> stateMap = new HashMap<String, State>();
    
    static {
		try{
			on = new State(STATE_ON);
			off = new State(STATE_OFF);
			red = new State(STATE_RED);
			yellow = new State(STATE_YELLOW);
			green = new State(STATE_GREEN);
			
            on.getSubStates().add(red);
            on.getSubStates().add(yellow);
            on.getSubStates().add(green);
            on.setInitialState(red);
            
            stateMap.put(STATE_ON, on);
            stateMap.put(STATE_OFF, off);
            stateMap.put(STATE_RED, red);
            stateMap.put(STATE_YELLOW, yellow);
            stateMap.put(STATE_GREEN, green);
            
	        Transition trans;
	        
	        trans = new Transition(on);
	        off.getTransitions().add(EVENT_ON, trans);
	
	        trans = new Transition(off);
	        on.getTransitions().add(EVENT_OFF, trans);
	        
            trans = new Transition(green);
            red.getTransitions().add(EVENT_TIMEELAPSED, trans);
            
            trans = new Transition(yellow);
            green.getTransitions().add(EVENT_TIMEELAPSED, trans);
            
            trans = new Transition(red);
            yellow.getTransitions().add(EVENT_TIMEELAPSED, trans);
            
		} catch (Exception e) {
			
		}
	}
    
	public TrafficLightStateMachine(String initState) {
		try{
			initialize(stateMap.get(initState));
		} catch (Exception e) {
			
		}
	}
}
