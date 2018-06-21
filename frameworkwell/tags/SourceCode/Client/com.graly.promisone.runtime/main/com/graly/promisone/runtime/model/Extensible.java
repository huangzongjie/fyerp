package com.graly.promisone.runtime.model;

public interface Extensible {
	
	void registerExtension(Extension extension) throws Exception;
	void unregisterExtension(Extension extension) throws Exception;
}
