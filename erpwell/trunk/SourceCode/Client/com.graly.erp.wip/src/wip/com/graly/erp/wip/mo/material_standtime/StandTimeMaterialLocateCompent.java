package com.graly.erp.wip.mo.material_standtime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.materiallocate.MaterialLocateManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class StandTimeMaterialLocateCompent {

	private MaterialLocateManager locateManager;
	
	public StandTimeMaterialLocateCompent(MaterialLocateManager locateManager) {
		this.locateManager = locateManager;
	}
	
	public void createMaterialLocateComposite(Composite parent, FormToolkit toolkit) {
//		Composite client = toolkit.createComposite(parent, SWT.NULL);
//		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		GridLayout gl = new GridLayout(2, false);
//		gl.marginWidth = 5;
//		gl.marginHeight = 0;
//		client.setLayout(gl);
		
//		toolkit.createLabel(client, Message.getString("pdm.material_id"), SWT.NULL);
//		Text txtMId = toolkit.createText(client, "", SWT.BORDER);
//		txtMId.setTextLimit(32);
//		GridData gd = new GridData();
//		gd.widthHint = 300;
//		txtMId.setLayoutData(gd);

//		txtMId.addKeyListener(new KeyAdapter() {
//			boolean isLocate = false;
//			String materialId = null;
//			int index;
//			
//			@Override
//			public void keyPressed(KeyEvent event) {
//				Text tLotId = ((Text) event.widget);
//				tLotId.setForeground(SWTResourceCache.getColor("Black"));
//				switch (event.keyCode) {
//				case SWT.CR:
//					materialId = tLotId.getText();
//					index = 0;
//					isLocate = locateManager.locateMaterial(materialId);
//					if (!isLocate) {
//						tLotId.setForeground(SWTResourceCache.getColor("Red"));
//					} else {
//						locateManager.locateNext(materialId, index);
//						tLotId.selectAll();
//					}
//					break;
////				case SWT.ARROW_UP:
////					if(isLocate) {
////						index--;
////						locateManager.locateLast(materialId, index);
////					}
////					break;
////				case SWT.ARROW_DOWN:
////					if(isLocate) {
////						index++;
////						locateManager.locateNext(materialId, index);
////					}
////					break;
//				}
//			}
//		});

//		txtMId.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				Text tLotId = ((Text) e.widget);
//				tLotId.setText(tLotId.getText());
//				tLotId.selectAll();
//			}
//		});
	}
}
