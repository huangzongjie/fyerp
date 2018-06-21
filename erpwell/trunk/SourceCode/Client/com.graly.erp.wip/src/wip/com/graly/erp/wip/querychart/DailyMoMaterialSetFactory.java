package com.graly.erp.wip.querychart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.data.DataFactory;
import org.eclipse.birt.chart.model.data.impl.DataFactoryImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.util.CDateTime;

import com.graly.erp.base.model.Material;
import com.graly.erp.chart.model.data.util.DataSetFactoryImpl;
import com.graly.erp.wip.model.DailyMoMaterial;
import com.ibm.icu.util.Calendar;

public class DailyMoMaterialSetFactory extends DataSetFactoryImpl {
	private List<Object> xCatalogs = new LinkedList<Object>();
	private List<Date> xCatalogValues = new LinkedList<Date>();
	
	public DailyMoMaterialSetFactory(List<DailyMoMaterial> object) {
		super(object);
	}

	@Override
	public List<Object> getXCatalogs() {
		if(object instanceof List){
			for(Object obj : (List)object){
				if(obj instanceof DailyMoMaterial){
					CDateTime cdt = new CDateTime(((DailyMoMaterial)obj).getCurrDate());
					if(!xCatalogs.contains(cdt)){
						xCatalogs.add(cdt);
						xCatalogValues.add(((DailyMoMaterial)obj).getCurrDate());
					}
				}
			}
		}
		if(xCatalogs.size() == 0){
			xCatalogs.add(CDateTime.getInstance());
		}
		return xCatalogs;
	}

	@Override
	public List<Object> getYSeries() {
		List<Object> ySeries = new ArrayList<Object>();
		Map<String, List<DailyMoMaterial>> m = new HashMap<String, List<DailyMoMaterial>>();
		if(object instanceof List){
			List so = (List)object;
			List so2 = new ArrayList();
			so2.addAll(so);
			int maxSize = 0;
			for(Object obj : so){
				List<DailyMoMaterial> l = new LinkedList<DailyMoMaterial>();
				DailyMoMaterial dmm = (DailyMoMaterial)obj;
				String key = ("K_" + dmm.getMaterialRrn());
				for(Object o : so2){
					DailyMoMaterial dmm2 = (DailyMoMaterial)o;
					if(!m.keySet().contains(key)){
						if(dmm2.getMaterialRrn().compareTo(dmm.getMaterialRrn()) == 0
								/*&& dmm2.getMoLineRrn().compareTo(dmm.getMoLineRrn()) == 0*/){
							l.add(dmm2);
						}
					}else{
						break;
					}
					
				}
				if(l.size() != 0){
////					maxSize = (maxSize > l.size() ? maxSize : l.size());
//					maxSize = xCatalogCount;
//					int oldSize = l.size();
//					if(l.size() < maxSize){//填充数目,数量一致
//						for(int i = 0; i< maxSize - oldSize; i++){
//							DailyMoMaterial dmm3 = new DailyMoMaterial();
//							dmm3.setMaterialRrn(dmm.getMaterialRrn());
//							dmm3.setTotalTime(BigDecimal.ZERO);
//							dmm3.setCurrDayPower(BigDecimal.ONE);
//							l.add(dmm3);
//						}
//					}
					List<DailyMoMaterial> rList = new LinkedList<DailyMoMaterial>();
					for(int i=0; i<xCatalogValues.size();i++){
						Date date = xCatalogValues.get(i);
						DailyMoMaterial dmm3 = null;
						for(DailyMoMaterial dmm4 : l){
							Date curDate = dmm4.getCurrDate();
							if(curDate.compareTo(date) == 0){
								if(dmm3 == null){
									dmm3 = dmm4;
								}else{
									dmm3.setTotalTime(dmm3.getTotalTime().add(dmm4.getTotalTime()));
								}
//								break;
							}
						}
						if(dmm3 != null){
							rList.add(i, dmm3);
						}else{
							DailyMoMaterial dmm5 = new DailyMoMaterial();
							dmm5.setTotalTime(BigDecimal.ZERO);
							dmm5.setCurrDayPower(BigDecimal.ONE);
							rList.add(i, dmm5);
						}
					}
					m.put(key, rList);
				}
			}
			if(!m.isEmpty()) ySeries.add(m);
		}
		if(ySeries.size() == 0){
			DailyMoMaterial obj = new DailyMoMaterial();
			Material material = new Material();
			material.setObjectRrn(123456L);
			material.setName("物料名称");
			obj.setMaterial(material);
			obj.setMaterialRrn(material.getObjectRrn());
			obj.setTotalTime(BigDecimal.TEN);
			obj.setCurrDayPower(BigDecimal.ONE);
			List<DailyMoMaterial> l = new LinkedList<DailyMoMaterial>();
			l.add(obj);
			m.put("K_"+material.getObjectRrn(), l);
			ySeries.add(m);
		}
		return ySeries;
	}
	
	

}
