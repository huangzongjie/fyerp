package com.graly.erp.ppm.mpsline;

import java.util.ArrayList;
import java.util.List;


import com.graly.erp.ppm.model.MpsLineBom;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.views.AbstractItemAdapter;

public class BomTreeItemAdapter extends AbstractItemAdapter {
	private static List<MpsLineBom> mpsLineBom;
	private List<MpsLineBom> list;

	@Override
	public Object[] getChildren(Object object) {
		if(list != null){
			return list.toArray();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object object) {
		if (object instanceof List) {
			List<MpsLineBom> mpsLineBoms = (List<MpsLineBom>) object;
			List<MpsLineBom> masterBoms = new ArrayList<MpsLineBom>();
			for (MpsLineBom bom : mpsLineBoms) {
				if (bom.getMaterialParentRrn() == null) {
					masterBoms.add(bom);
				}
			}
			return masterBoms.toArray();
		}
		return new Object[] { object };
	}

	@Override
	public boolean hasChildren(Object object) {
		if (object instanceof MpsLineBom) {
			list = getChildMpsLineBom((MpsLineBom) object);
			if (list != null && list.size() >= 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getText(Object object, String id) {
		if (object != null && id != null) {
			try {
				Object property = PropertyUtil.getPropertyForString(object, id);
				return (String) property;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static void setMoBoms(List<MpsLineBom> mpsLineBom) {
		BomTreeItemAdapter.mpsLineBom = mpsLineBom;
	}

	public static List<MpsLineBom> getChildMpsLineBom(MpsLineBom parentBom) {
		if (mpsLineBom != null) {
			List<MpsLineBom> childBoms = new ArrayList<MpsLineBom>();
			long parentMaterialRrn = parentBom.getMaterialRrn();
			long childLevel = parentBom.getPathLevel() + 1;

			for (MpsLineBom childBom : mpsLineBom) {
				if (childBom.getMaterialParentRrn() != null && childBom.getMaterialParentRrn() == parentMaterialRrn
						&& childBom.getPathLevel() == childLevel) {
					childBoms.add(childBom);
				}
			}
			return childBoms;
		}
		return null;
	}
}
