package com.graly.erp.chart.model.data.util;

import java.util.List;

public abstract class DataSetFactoryImpl implements DataSetFactory {
	protected Object object;
	
	public DataSetFactoryImpl(Object object) {
		super();
		this.object = object;
	}

	@Override
	abstract public List<Object> getXCatalogs();

	@Override
	abstract public List<Object> getYSeries();

}
