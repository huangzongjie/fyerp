package com.graly.erp.pur.po.query;

import java.util.Date;

import org.eclipse.swt.graphics.Color;

import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.VPoLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class PoQueryTableManager extends EntityTableManager {

	public PoQueryTableManager(ADTable adTable) {
		super(adTable);
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(Object.class, new PoDetailItemAdapter());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
	}

	public class PoDetailItemAdapter extends EntityItemAdapter {
		Date now = null;
		
		public PoDetailItemAdapter(){
			now = Env.getSysDate();
		}
		
		@Override
		public Color getBackground(Object element, String id) {
			return null;
		}
		
		@Override
		public Color getForeground(Object element, String id) {
			if(element instanceof VPoLine) {
				VPoLine vLine = (VPoLine)element;
				// �����������С�ڵ�ǰ����ʱ��������ˣ����ջ���Ϊ������ջ���С�ڶ��������Ժ�ɫ���
				if(vLine.getDateEnd() != null && vLine.getDateEnd().compareTo(now) < 0 
						&& (PurchaseOrder.STATUS_APPROVED.equals(vLine.getLineStatus())
								|| PurchaseOrder.STATUS_DRAFTED.equals(vLine.getLineStatus()))) {
					if(vLine.getQtyDelivered() == null || vLine.getQtyDelivered().longValue() == 0)
						return SWTResourceCache.getColor("Red");
					else if(vLine.getQty() != null && vLine.getQty().compareTo(vLine.getQtyDelivered()) > 0)
						return SWTResourceCache.getColor("Red");
				}
			}
			return null;
		}
	}
}
