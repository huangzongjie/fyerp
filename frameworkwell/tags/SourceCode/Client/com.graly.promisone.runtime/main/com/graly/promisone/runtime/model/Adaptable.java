package com.graly.promisone.runtime.model;

public interface Adaptable {

    /**
     * Returns an object which is an instance of the given class
     * associated with this object. Returns <code>null</code> if
     * no such object can be found.
     *
     * @param adapter the adapter class to look up
     * @return a object castable to the given class,
     *    or <code>null</code> if this object does not
     *    have an adapter for the given class
     */
	<T> T getAdapter(Class<T> adapter);
	
}
