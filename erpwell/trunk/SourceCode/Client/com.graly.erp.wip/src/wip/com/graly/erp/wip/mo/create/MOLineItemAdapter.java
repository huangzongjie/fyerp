package com.graly.erp.wip.mo.create;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.receive.TextProvider;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;

public class MOLineItemAdapter<T extends Object> extends AbstractItemAdapter {
	public static final String SUFFIX_MINUTE = "分钟";
	public static final String SUFFIX_DAY = "天";
	public static List<DocumentationLine> doLines;
	public static Map<RequisitionLine, ManufactureOrderBom> bomMap;
	public static Map<ManufactureOrderLine, ManufactureOrderBom> moLineBomMap;
	private final String StartTime = "timeStart";
	private final String EndTime = "timeEnd";
	private final String TimePattern = "HH:mm a";
	private final String DatePattern = "yyyy-MM-dd";
	public static final String FIELD_QTY = "qty";
	public static final String FIELD_QTY_NEED = "qtyNeed";
	public static final String FIELD_QTY_RECEIVE = "qtyReceive";
	public static final String SUFFIX_HOUR = "小时";
	public static final BigDecimal MINUTE_60 = new BigDecimal("60");
	
	public MOLineItemAdapter() {
		super();
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof List) {
			List<DocumentationLine> maters = new ArrayList<DocumentationLine>();
			List<DocumentationLine> list = (List<DocumentationLine>)inputElement;
			for(DocumentationLine dtLine : list) {
				if(getLevel(dtLine) == 0) {
					maters.add(dtLine);
				}
			}
			return maters.toArray();
		}
		return new Object[]{inputElement};
    }
	
	@Override
	public Object[] getChildren(Object object) {
		if(object instanceof RequisitionLine) return null;
		else if(object instanceof ManufactureOrderBom) {
			ManufactureOrderBom parentBom = (ManufactureOrderBom)object;
			long parentMaterialRrn = parentBom.getMaterialRrn();
			String parentPath = parentBom.getPath() == null ?
					parentBom.getMaterialRrn().toString() : parentBom.getPath() + parentBom.getMaterialRrn();
			List<DocumentationLine> list = getChildDoLine(parentMaterialRrn, parentPath + "/", -1L);
			return list.toArray();
		} else if(object instanceof ManufactureOrderLine) {
			ManufactureOrderLine moLine = (ManufactureOrderLine)object;
			long parentMoRrn = -1L;
			if(moLine.getObjectRrn() != null) {
				parentMoRrn = moLine.getObjectRrn();
			}
			String parentPath = moLine.getPath() == null ?
					moLine.getMaterialRrn().toString() : moLine.getPath() + moLine.getMaterialRrn();
			List<DocumentationLine> list = getChildDoLine(moLine.getMaterialRrn(), parentPath + "/", parentMoRrn);
			return list.toArray();
		}
		return null;
	}
	
	@Override
	public boolean hasChildren(Object object) {
		if(object instanceof RequisitionLine) return false;
		else if(object instanceof ManufactureOrderBom) {
			ManufactureOrderBom parentBom = (ManufactureOrderBom)object;
			long parentMaterialRrn = parentBom.getMaterialRrn();
			String parentPath = parentBom.getPath() == null ?
					parentBom.getMaterialRrn().toString() : parentBom.getPath() + parentBom.getMaterialRrn();
			List<DocumentationLine> list = getChildDoLine(parentMaterialRrn, parentPath + "/", -1L);
			if(list.size() > 0 ) return true;
		} else if(object instanceof ManufactureOrderLine) {
			ManufactureOrderLine moLine = (ManufactureOrderLine)object;
			long parentMoRrn = -1L;
			if(moLine.getObjectRrn() != null) {
				parentMoRrn = moLine.getObjectRrn();
			}
			String parentPath = moLine.getPath() == null ?
					moLine.getMaterialRrn().toString() : moLine.getPath() + moLine.getMaterialRrn();
			List<DocumentationLine> list = getChildDoLine(moLine.getMaterialRrn(), parentPath + "/", parentMoRrn);
			if(list.size() > 0 ) return true;
		}
		return false;
	}
	
	@Override
	public String getText(Object object, String id) {
		try {
			if (object != null && id != null){
				if(object instanceof ManufactureOrderLine) {
					return getTextBy((ManufactureOrderLine)object, id);
				} else if(object instanceof RequisitionLine) {					
					return getTextBy((RequisitionLine)object, id);					
				} else if(object instanceof ManufactureOrderBom) {
					return getTextBy((ManufactureOrderBom)object, id);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public Color getForeground(Object element, String id) {
		if(element instanceof ManufactureOrderLine) {
			ManufactureOrderLine moLine = (ManufactureOrderLine)element;
			if(moLine.getDateStart() == null || moLine.getDateEnd() == null) {
				return SWTResourceCache.getColor("Red");
			}
			if(moLineBomMap != null && moLineBomMap.get(element) != null) {
				ManufactureOrderBom moBom = moLineBomMap.get(moLine);
				if(moBom.getQtyOnHand() != null && moBom.getQtyAllocation() != null) {
					if(moBom.getQtyOnHand().compareTo(moBom.getQtyAllocation()) < 0) {
						return SWTResourceCache.getColor("Alarm");
					}
				}
			}
		}
		else {
			Object tempBom = element;
			if(element instanceof RequisitionLine) {
				RequisitionLine prLine = (RequisitionLine)element;
				if(bomMap != null && bomMap.get(prLine) != null) {
					tempBom = bomMap.get(prLine);
				}
			}
			if(tempBom instanceof ManufactureOrderBom) {
				ManufactureOrderBom moBom = (ManufactureOrderBom)tempBom;
				if(moBom.getIsDateNeed()) {
					// 没有时间生产, 但仍能用最低库存进行上一级物料的生产(即isCanStart为true)
					if(moBom.getIsCanStart()) {
						return SWTResourceCache.getColor("Run");
					}
					// 需要时间生产，且不能开始，且moBom的开始或结束日期为空，则显示红色
//					else if(moBom.getDateStart() == null || moBom.getDateEnd() == null) {
					else {
						//需要时间，且不能用最低库存开始，则仍显示红色(即使为采购申请有开始和结束日期，则仍显示红色)
						return SWTResourceCache.getColor("Red");
					}
				} else if(moBom.getIsMaterialNeed()) {
					return SWTResourceCache.getColor("Function");
				}
				if(moBom.getQtyOnHand() != null && moBom.getQtyAllocation() != null) {
					if(moBom.getQtyOnHand().compareTo(moBom.getQtyAllocation()) < 0) {
						return SWTResourceCache.getColor("Alarm");
					}
				}
			}
		}
		return null;
	}
	
	public static void setMOLines(List<DocumentationLine> doLines) {
		MOLineItemAdapter.doLines = doLines;
	}
	
	public static void setBomMap(Map<RequisitionLine, ManufactureOrderBom> bomMap) {
		MOLineItemAdapter.bomMap = bomMap;
	}
	
	public static void setMoLineBomMap(Map<ManufactureOrderLine, ManufactureOrderBom> moLineBomMap) {
		MOLineItemAdapter.moLineBomMap = moLineBomMap;
	}
	
	// parentMaterialRrn始终有值
	private List<DocumentationLine> getChildDoLine(long parentMaterialRrn, String parentPath, long parentMoRrn) {
		List<DocumentationLine> children = new ArrayList<DocumentationLine>();
		for (DocumentationLine line : doLines) {
			if(line instanceof ManufactureOrderBom) {
				ManufactureOrderBom childBom = (ManufactureOrderBom)line;
				if (childBom.getMaterialParentRrn() != null
						&& childBom.getMaterialParentRrn() == parentMaterialRrn
						&& parentPath.equals(childBom.getPath())) {
					children.add(childBom);
				}
			} else if(line instanceof ManufactureOrderLine) {
				/* parentMoRrn 不为-1表示不是新创建的,可以通过parentMoRrn和parentPath确定父子关系
				 * 否则只能通过parentPath确定父子关系,因为moLine无法确定父物料,即parentMaterialRrn
				 */
				ManufactureOrderLine moLine = (ManufactureOrderLine)line;
				if(parentMoRrn != -1 && moLine.getParentMoRrn() != null) {
					if(moLine.getParentMoRrn() == parentMoRrn && parentPath.equals(moLine.getPath())) {
						children.add(moLine);
					}
				} else {
					if(parentPath.equals(moLine.getPath())) {
						children.add(moLine);
					}
				}
			} else if(line instanceof RequisitionLine) {
				RequisitionLine prLine = (RequisitionLine)line;
				if(parentPath.equals(prLine.getPath())) {
					children.add(prLine);
				}
			}
		}
		return children;
	}
	
	private long getLevel(DocumentationLine line) {
		if(line instanceof ManufactureOrderLine) {
			if(((ManufactureOrderLine)line).getPathLevel() != null) {
				return ((ManufactureOrderLine)line).getPathLevel();				
			}
		}
		if(line instanceof RequisitionLine) {
			if(((RequisitionLine)line).getPathLevel() != null) {
				return ((RequisitionLine)line).getPathLevel();				
			}
		}
		if(line instanceof ManufactureOrderBom) {
			if(((ManufactureOrderBom)line).getPathLevel() != null) {
				return ((ManufactureOrderBom)line).getPathLevel();				
			}
		}
		return -1;
	}
	
	private String getFormatDateBy(Date date, String pattern) {
		if(date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return formatter.format(date);
		}
		return "";
	}
	
	private String getTextBy(ManufactureOrderLine moLine, String id) {
		if(StartTime.equals(id)) {
			return getFormatDateBy(moLine.getDateStart(), TimePattern);
		} else if(EndTime.equals(id)) {
			return getFormatDateBy(moLine.getDateEnd(), TimePattern);
		} else if(TextProvider.FieldName_DateStart.equals(id)) {
			return getFormatDateBy(moLine.getDateStart(), DatePattern);
		} else if(TextProvider.FieldName_DateEnd.equals(id)) {
			return getFormatDateBy(moLine.getDateEnd(), DatePattern);
		} else if(TextProvider.FieldName_StandTime.equals(id)) {
			if(moLine.getStandTime() != null)
				return moLine.getStandTime() + SUFFIX_MINUTE;
		} else if(FIELD_QTY.equals(id)) {
			return String.valueOf(moLine.getQty().doubleValue());
		} else if(FIELD_QTY_NEED.equals(id)) {
			return String.valueOf(moLine.getQtyNeed().doubleValue());
		} else if(FIELD_QTY_RECEIVE.equals(id)) {
			return String.valueOf(moLine.getQtyReceive().doubleValue());
		} else if(TextProvider.FieldName_TotalTime.equals(id)) {
			if(moLine.getStandTime() != null && moLine.getQty() != null)
				return String.valueOf(moLine.getStandTime()
						.multiply(moLine.getQty()).divide(MINUTE_60, 2, RoundingMode.UP).doubleValue()) + SUFFIX_HOUR;
			else return "0" + SUFFIX_HOUR;
		} 
		else {
			return getPropertyText(moLine, id);
		}
		return "";
	}
	
	private String getTextBy(RequisitionLine prLine, String id) {
		if(TextProvider.FieldName_MaterialId.equals(id)
				|| TextProvider.FieldName_MaterialName.equals(id)
				|| TextProvider.FieldName_LineStatus.equals(id)) {
			return getPropertyText(prLine, id);
		}
		else if(FIELD_QTY.equals(id)) {
			return String.valueOf(prLine.getQty().doubleValue());
		} else if(FIELD_QTY_NEED.equals(id)) {
			return String.valueOf(prLine.getQtyNeed().doubleValue());
		} else if(FIELD_QTY_RECEIVE.equals(id)) {
			if(prLine.getQtyInventoty() != null) {
				return String.valueOf(prLine.getQtyInventoty().doubleValue());				
			}
		}
		else if(TextProvider.FieldName_StandTime.equals(id)) {
			if(prLine.getLeadTime() != null) {
				return prLine.getLeadTime() + SUFFIX_DAY;				
			}
		}
		else if(TextProvider.FieldName_DateStart.equals(id)) 
			return getFormatDateBy(prLine.getDateStart(), DatePattern);
		else if(TextProvider.FieldName_DateEnd.equals(id))
			return getFormatDateBy(prLine.getDateEnd(), DatePattern);
		else if(TextProvider.FieldName_FinishedQty.equals(id)) {
			if(prLine.getQtyInventoty() != null) {
				return prLine.getQtyInventoty().toString();				
			}
		}
//		else if(TextProvider.FieldName_NeedQty.equals(id)) {
//			if(prLine.getQty() != null) {
//				return prLine.getQty().toString(); // 需求数量默认为生产数量
//			}
//		}
		// 是startTime, endTime, startDate, qty(即生产数量)返回""
		return "";
	}
	
	private String getTextBy(ManufactureOrderBom moBom, String id) {
		if(TextProvider.FieldName_MaterialId.equals(id)
				|| TextProvider.FieldName_MaterialName.equals(id)
				|| TextProvider.FieldName_LineStatus.equals(id)) {
			return getPropertyText(moBom, id);
		}
		else if(FIELD_QTY.equals(id)) {
			return String.valueOf(moBom.getQty().doubleValue());
		} else if(FIELD_QTY_NEED.equals(id)) {
			return String.valueOf(moBom.getQtyNeed().doubleValue());
		}
//		else if(FIELD_QTY_RECEIVE.equals(id)) {
//		}
		else if(TextProvider.FieldName_StandTime.equals(id)) {
			if(moBom.getStandTime() != null) {
				if(moBom.getIsProduct()) {
					return moBom.getStandTime() + SUFFIX_MINUTE;
				} else if(moBom.getIsMaterialNeed()){
					return moBom.getStandTime() + SUFFIX_DAY;
				}				
			}
		}
		else if(TextProvider.FieldName_DateStart.equals(id)) return getFormatDateBy(moBom.getDateStart(), DatePattern);
		else if(TextProvider.FieldName_DateEnd.equals(id)) return getFormatDateBy(moBom.getDateEnd(), DatePattern);
//		else if(TextProvider.FieldName_FinishedQty.equals(id)) {
//			return moBom.getQtyNeed().toString();
//		}
		// 是startTime, endTime, qty, receivedQty返回""
		return "";
	}
	
	private String getPropertyText(Object object, String id) {
		Object property = PropertyUtil.getPropertyForString(object, id);
		return (String)property;
	}

}
