package com.graly.mes.wip.model;

import java.util.HashMap;
import java.util.Map;

import com.graly.framework.core.statemachine.AbstractStateMachine;
import com.graly.framework.core.statemachine.State;
import com.graly.framework.core.statemachine.Transition;

public class LotStateMachine extends AbstractStateMachine {
	
    public static String COMCLASS_SCHD = "SCHD";
    public static String COMCLASS_WIP = "WIP";
    public static String COMCLASS_FIN = "FIN";
    public static String COMCLASS_COM = "COM";
    public static String COMCLASS_TEMP = "TEMPSAVE";
    
    public static String STATE_SCHD = "SCHD";
    public static String STATE_WAIT = "WAIT";
    public static String STATE_RUN = "RUN";
    public static String STATE_TRAN = "TRAN";
    public static String STATE_HELD = "HELD";
    public static String STATE_FIN = "FIN";
    public static String STATE_COM = "COM";
    public static String STATE_TEMP = "TEMPSAVE";
    
    public static String SUBSTATE_SCHD = "SCHD";
    public static String SUBSTATE_WAIT = "WAIT";
    public static String SUBSTATE_RUN = "RUN";
    public static String SUBSTATE_TRAN = "TRAN";
    public static String SUBSTATE_HELD = "HELD";
    public static String SUBSTATE_QHLD = "QHLD";
    public static String SUBSTATE_RHLD = "RHLD";
    public static String SUBSTATE_THLD = "THLD";
    public static String SUBSTATE_LHLD = "LHLD";
    public static String SUBSTATE_FHLD = "FHLD";
    public static String SUBSTATE_FINISH = "FINISH";
    public static String SUBSTATE_COMPLT = "COMPLT";
    public static String SUBSTATE_TEMP = "TEMPSAVE";
    
    private static State cschd;
    private static State cwip;
    private static State cfin;
    private static State ccom;
    private static State sschd;
    private static State swait;
    private static State srun;
    private static State stran;
    private static State sheld;
    private static State sfin;
    private static State scom;
    private static State uschd;
    private static State uwait;
    private static State urun;
    private static State utran;
    private static State uheld;
    private static State uqhld;
    private static State urhld;
    private static State uthld;
    private static State ulhld;
    private static State ufhld;
    private static State ufinish;
    private static State ucomplt;
    
    public static String TRANS_TEMPSAVE = "TEMPSAVE";
	public static String TRANS_SCHEDLOT = "SCHEDLOT";
	public static String TRANS_SCHEDUPDALOT = "SCHEDUPDALOT";
	public static String TRANS_UNSCHEDLOT = "UNSCHEDLOT";
	public static String TRANS_NEWLOTSTART = "NEWLOTSTART";
	public static String TRANS_PRNRUNCARD = "PRNRUNCARD";
	public static String TRANS_TRACKIN = "TRACKIN";
	public static String TRANS_TRACKOUT = "TRACKOUT";
	public static String TRANS_LOTMOVE = "LOTMOVE";
	public static String TRANS_ABORTLOT = "ABORTLOT";
	public static String TRANS_HOLDLOT = "HOLDLOT";
	public static String TRANS_MULTIHOLDLOT = "MULTIHOLDLOT";
	public static String TRANS_RELEASELOT = "RELEASELOT";
	public static String TRANS_MULTIRELEASELOT = "MULTIRELEASELOT";
	public static String TRANS_SPLITLOT = "SPLITLOT";
	public static String TRANS_SPLITOUT = "SPLITOUT";
	public static String TRANS_MERGELOT = "MERGELOT";
	public static String TRANS_MERGEOUT = "MERGEOUT";
	public static String TRANS_COMBINELOT = "COMBINELOT";
	public static String TRANS_SCRAPLOT = "SCRAPLOT";
	public static String TRANS_UNSCRAPLOT = "UNSCRAPLOT";
	public static String TRANS_TERMLOT = "TERMLOT";
	public static String TRANS_UNTERMLOT = "UNTERMLOT";
	public static String TRANS_SHIPLOT = "SHIPLOT";
	public static String TRANS_UNSHIPLOT = "UNSHIPLOT";
	public static String TRANS_CHANGELOT = "CHANGELOT";
	public static String TRANS_ADJUSTLOTQTY = "ADJUSTLOTQTY";
	public static String TRANS_RTNLOTTOINV = "RTNLOTTOINV";
	public static String TRANS_NEWPART = "NEWPART";
	public static String TRANS_PROCCHANGE = "PROCCHANGE";
	public static String TRANS_MOVETOLOC = "MOVETOLOC";
	public static String TRANS_BACKUPLOT = "BACKUPLOT";
	public static String TRANS_SKIPLOT = "SKIPLOT";
	public static String TRANS_MATERIALCONSUM = "MATERIALCONSUM";
	public static String TRANS_FINISH = "FINISH";
	public static String TRANS_TRANSFER = "TRANSFER";
	public static String TRANS_DELETELOT = "DELETELOT";
	public static String TRANS_OUTPART = "OUTPART";
	
	public static String TRANS_QUEUEHOLD = "QUEUEHOLD";
	public static String TRANS_TRACKOUTHOLD = "TRACKOUTHOLD";
	
    public Map<String, State> stateMap = new HashMap<String, State>();

	public LotStateMachine() {
		try{
			cschd = new State(COMCLASS_SCHD);
			cwip = new State(COMCLASS_WIP);
			cfin = new State(COMCLASS_FIN);
			ccom = new State(COMCLASS_COM);
			
			sschd = new State(STATE_SCHD);
			swait = new State(STATE_WAIT);
			srun = new State(STATE_RUN);
			stran = new State(STATE_TRAN);
			sheld = new State(STATE_HELD);
			sfin = new State(STATE_FIN);
			scom = new State(STATE_COM);
			
			cschd.getSubStates().add(sschd);
			cschd.setInitialState(sschd);
			
			cwip.getSubStates().add(swait);
			cwip.getSubStates().add(srun);
			cwip.getSubStates().add(stran);
			cwip.getSubStates().add(sheld);
			cwip.setInitialState(swait);
			
			cfin.getSubStates().add(sfin);
			cfin.setInitialState(sfin);
			
			ccom.getSubStates().add(scom);
			ccom.setInitialState(scom);
			
			uschd = new State(SUBSTATE_SCHD);
			uwait = new State(SUBSTATE_WAIT);
			urun = new State(SUBSTATE_RUN);
			utran = new State(SUBSTATE_TRAN);
			uheld = new State(SUBSTATE_HELD);
			uqhld = new State(SUBSTATE_QHLD);
			urhld = new State(SUBSTATE_RHLD);
			uthld = new State(SUBSTATE_THLD);
			ulhld = new State(SUBSTATE_LHLD);
			ufhld = new State(SUBSTATE_FHLD);
			ufinish = new State(SUBSTATE_FINISH);
			ucomplt = new State(SUBSTATE_COMPLT);
			
			sschd.getSubStates().add(uschd);
			sschd.setInitialState(uschd);
			
			swait.getSubStates().add(uwait);
			swait.setInitialState(uwait);
			
			srun.getSubStates().add(urun);
			srun.setInitialState(urun);
			
			stran.getSubStates().add(utran);
			stran.setInitialState(utran);
			
			sheld.getSubStates().add(uheld);
			sheld.getSubStates().add(uqhld);
			sheld.getSubStates().add(urhld);
			sheld.getSubStates().add(uthld);
			sheld.getSubStates().add(ulhld);
			sheld.getSubStates().add(ufhld);
			sheld.setInitialState(uheld);
			
			sfin.getSubStates().add(ufinish);
			sfin.setInitialState(ufinish);
			
			scom.getSubStates().add(ucomplt);
			scom.setInitialState(ucomplt);
			
			stateMap.put(SUBSTATE_SCHD, uschd);
			stateMap.put(SUBSTATE_WAIT, uwait);
			stateMap.put(SUBSTATE_RUN, urun);
			stateMap.put(SUBSTATE_TRAN, utran);
			stateMap.put(SUBSTATE_HELD, uheld);
			stateMap.put(SUBSTATE_QHLD, uqhld);
			stateMap.put(SUBSTATE_RHLD, urhld);
			stateMap.put(SUBSTATE_THLD, uthld);
			stateMap.put(SUBSTATE_LHLD, ulhld);
			stateMap.put(SUBSTATE_FHLD, ufhld);
			stateMap.put(SUBSTATE_FINISH, ufinish);
			stateMap.put(SUBSTATE_COMPLT, ucomplt);
			
			Transition trans;
			trans = new Transition(uwait);
			uschd.getTransitions().add(TRANS_NEWLOTSTART, trans);
			trans = new Transition(ucomplt);
			uschd.getTransitions().add(TRANS_TERMLOT, trans);
			
			trans = new Transition(urun);
			uwait.getTransitions().add(TRANS_TRACKIN, trans);
			trans = new Transition(ufinish);
			uwait.getTransitions().add(TRANS_FINISH, trans);
			trans = new Transition(uheld);
			uwait.getTransitions().add(TRANS_HOLDLOT, trans);
			trans = new Transition(uqhld);
			uwait.getTransitions().add(TRANS_QUEUEHOLD, trans);
			trans = new Transition(uthld);
			uwait.getTransitions().add(TRANS_TRACKOUTHOLD, trans);
			trans = new Transition(ucomplt);
			uwait.getTransitions().add(TRANS_MERGELOT, trans);
			trans = new Transition(ucomplt);
			uwait.getTransitions().add(TRANS_TERMLOT, trans);
			trans = new Transition(ucomplt);
			uwait.getTransitions().add(TRANS_SCRAPLOT, trans);
			
			trans = new Transition(uwait);
			urun.getTransitions().add(TRANS_TRACKOUT, trans);
			trans = new Transition(uheld);
			urun.getTransitions().add(TRANS_HOLDLOT, trans);
			trans = new Transition(ucomplt);
			urun.getTransitions().add(TRANS_SCRAPLOT, trans);
			
			trans = new Transition(uheld);
			uheld.getTransitions().add(TRANS_HOLDLOT, trans);
			
			trans = new Transition(ucomplt);
			ufinish.getTransitions().add(TRANS_SHIPLOT, trans);
			
			trans = new Transition(uwait);
			ucomplt.getTransitions().add(TRANS_UNSCRAPLOT, trans);
		} catch (Exception e) {
			
		}
	}
	
	public void initialize(String initState) {
		try{
			initialize(stateMap.get(initState));
		} catch (Exception e) {
		}
	}
    
}
