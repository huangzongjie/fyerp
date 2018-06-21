package com.graly.promisone.core.statemachine;


public class Transition {
	
	private State source = null;
	private State target = null;
	private Exception exceptionResult = null;
	private static TransitionResult notFiredResult = new TransitionResult(false, null, null); 

	public Transition() {
    }
	
	 public Transition(State target) {
         this.target = target;
     }
	
	 public TransitionResult fire(State origin) {
         TransitionResult result;            
         // If the transition should fire.
         if(shouldFire()) {
             State newState = origin;
             // If this is not an internal transition.
             if(target != null) {                
                 State o = origin;
                 // Unwind up from the state that originally received the event
                 // to the source state.
                 while(o != source) {
                     o.exit();
                     o = o.getSuperState();
                 }
                 fire(source, target);

                 newState = target.enterByHistory();
             } else {                 
             }
             result = new TransitionResult(true, newState, exceptionResult);
         }
         // Else the transition should not fire.
         else {
             result = notFiredResult;
         }
         return result;
     }
	
	 /*
      * There are several state transition traversal cases:
      * 
      * 1. The source and target are the same (self-transition).
      * 2. The target is a substate of the source.
      * 3. The source is a substate of the target.
      * 4. The source and target share the same superstate.
      * 5. All other cases.
      *     a. The source and target reside at the save level in the 
      *        hiearchy (but do not share the same superstate).
      *     b. The source is lower in the hiearchy than the target.
      *     c. The target is lower in the hierarchy than the source.
      * 
      * Case 1: Immediately performs the transition.
      * 
      * Case 2: Traverses the hierarchy from the source to the target, 
      *         entering each state along the way. No states are exited.
      * 
      * Case 3: Traverses the hierarchy from the source to the target, 
      *         exiting each state along the way. The target is then 
      *         entered.
      * 
      * Case 4: The source is exited and the target entered.
      * 
      * Case 5: Traverses the hiearchy until a common superstate is met.
      * 
      * The action is performed between the last state exit and first state
      * entry.
      */
	 private void fire(State s, State t) {
         // Handles case 1.
         // Handles case 3 after traversing from the source to the target.
		 if(s == target) {
             s.exit();
             target.entry();
         }
         // Handles case 2 after traversing from the target to the source.
         else if(s == t) {
             return;
         }
         // Handles case 4.
         // Handles case 5a after traversing the hierarchy until a common 
         // ancestor if found.
         else if(s.getSuperState() == t.getSuperState()) {
             s.exit();
             t.entry();
         } else {
             /*
              * The following traverses the hierarchy until one of the above
              * conditions is met.
              */
             // Handles case 3.
             // Handles case 5b.
             if(s.getLevel() > t.getLevel()) {
                 s.exit();
                 fire(s.getSuperState(), t);
             }
             // Handles case 2.
             // Handles case 5c.
             else if(s.getLevel() < t.getLevel()) {
            	 fire(s, t.getSuperState());
                 t.entry();
             }
             // Handles case 5a.
             else {
                 s.exit();
                 fire(s.getSuperState(), t.getSuperState());
                 t.entry();
             }
         }
	 }
	 
	 private boolean shouldFire() {
		 boolean result = true;
         return result;
     }

	public void setSource(State source) {
		this.source = source;
	}
	public State getSource() {
		return source;
	}
	public void setTarget(State target) {
		this.target = target;
	}
	public State getTarget() {
		return target;
	}
}
