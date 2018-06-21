package com.graly.erp.base.materiallocate;

public interface MaterialLocateManager {

	boolean locateMaterial(String materialId);
	
	void locateNext(String materialId, int index);
	
	void locateLast(String materialId, int index);
}
