package com.graly.promisone.core.statemachine;

public class TransitionResult {

	private boolean hasFired;
	private State newState;
	private Exception error = null;

	public TransitionResult(boolean hasFired, State newState, Exception error) {
		this.setHasFired(hasFired);
		this.setNewState(newState);
		this.setError(error);
	}

	public void setHasFired(boolean hasFired) {
		this.hasFired = hasFired;
	}

	public boolean isHasFired() {
		return hasFired;
	}

	public void setNewState(State newState) {
		this.newState = newState;
	}

	public State getNewState() {
		return newState;
	}

	public void setError(Exception error) {
		this.error = error;
	}

	public Exception getError() {
		return error;
	}

}
