package com.graly.erp.pur.request.refmo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;

public class MoLineItemAdapter<T extends Object> extends AbstractItemAdapter {
	public static List<DocumentationLine> doLines;
	private final String StartTime = "timeStart";
	private final String EndTime = "timeEnd";
	private final String TimePattern = "HH:mm a";
	private final String DatePattern = "yyyy-MM-dd";
	
	public MoLineItemAdapter() {
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
		try{
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
		if(element instanceof RequisitionLine) {
			return SWTResourceCache.getColor("Function");
		}
		if(element instanceof ManufactureOrderLine) {
			ManufactureOrderLine moLine = (ManufactureOrderLine)element;
			if(moLine.getDateStart() == null || moLine.getDateEnd() == null) {
				return SWTResourceCache.getColor("Red");
			}
		}
		if(element instanceof ManufactureOrderBom) {
			ManufactureOrderBom moBom = (ManufactureOrderBom)element;
			if(moBom.getIsDateNeed()) {
				// 没有时间生产, 但仍能用最低库存进行上一级物料的生产(即isCanStart为true)
				if(moBom.getIsCanStart()) {
					return SWTResourceCache.getColor("Run");
				}
				// 否则显示红色
				else {
					return SWTResourceCache.getColor("Red");
				}
			} else if(moBom.getIsMaterialNeed()) {
				return SWTResourceCache.getColor("Function");
			}
		}
		return null;
	}
	
	public static void setMOLines(List<DocumentationLine> doLines) {
		MoLineItemAdapter.doLines = doLines;
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
		}
		else {
			return getPropertyText(moLine, id);
		}
	}
	
	private String getTextBy(RequisitionLine prLine, String id) {
		if(TextProvider.FieldName_MaterialId.equals(id)
				|| TextProvider.FieldName_MaterialName.equals(id)
				|| TextProvider.FieldName_LineStatus.equals(id)
				|| TextProvider.FieldName_NeedQty.equals(id)
				|| TextProvider.FieldName_ProductQty.equals(id)) {
			return getPropertyText(prLine, id);
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
		return "";
	}
	
	private String getTextBy(ManufactureOrderBom moBom, String id) {
		if(TextProvider.FieldName_MaterialId.equals(id)
				|| TextProvider.FieldName_MaterialName.equals(id)
				|| TextProvider.FieldName_LineStatus.equals(id)
				|| TextProvider.FieldName_ProductQty.equals(id)
				|| TextProvider.FieldName_NeedQty.equals(id)) {
			return getPropertyText(moBom, id);
		}
		else if(TextProvider.FieldName_DateStart.equals(id)) return getFormatDateBy(moBom.getDateStart(), DatePattern);
		else if(TextProvider.FieldName_DateEnd.equals(id)) return getFormatDateBy(moBom.getDateEnd(), DatePattern);
		return "";
	}
	
	private String getPropertyText(Object object, String id) {
		Object property = PropertyUtil.getPropertyForString(object, id);
		return (String)property;
	}
}
