package com.graly.framework.base.uiX.viewmanager;

import java.util.Map;

import com.graly.framework.base.entitymanager.IRefresh;

public interface IXRefresh extends IRefresh {
	public String getViewerContents();
	public void setQueryKeys(Map<String,String> queryKeys);
}
