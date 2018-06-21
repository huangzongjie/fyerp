package com.graly.erp.pdm.material;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;

public class EditVolumeDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(EditVolumeDialog.class);
	
	protected IManagedForm managedForm;
	protected BigDecimal length,width,height,volume;
	protected String weight;
	protected Text lengthField,widthField,heightField,volumeField,weightField;
	protected Material material;
	private boolean isVolumeChanged = false;
	private MaterialProperties materialProterpies;
	private String message;
	
	public EditVolumeDialog(Shell parentShell, MaterialProperties materialProterpies, IManagedForm managedForm, Material material) {
		super(parentShell);
		this.materialProterpies = materialProterpies;
		this.managedForm = managedForm;
		this.material = material;
		this.length = material.getLength();
		this.width = material.getWidth();
		this.height = material.getHeight();
		this.volume = material.getVolume();
		this.weight = material.getWeight();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle("修改物料体积");
        Composite composite = (Composite) super.createDialogArea(parent);
        
        FormToolkit toolkit = managedForm.getToolkit();
        Composite client = toolkit.createComposite(composite, SWT.BORDER);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        GridData fieldGd = new GridData(GridData.FILL_HORIZONTAL);
        toolkit.createLabel(client, "  长");
        lengthField = toolkit.createText(client, "", SWT.BORDER);
        lengthField.setLayoutData(fieldGd);
        lengthField.setText((length == null ? "" : length.toString()));
        toolkit.createLabel(client, "* 宽");
        widthField = toolkit.createText(client, "", SWT.BORDER);
        widthField.setLayoutData(fieldGd);
        widthField.setText((width == null ? "" : width.toString()));
        toolkit.createLabel(client, "* 高");
        heightField = toolkit.createText(client, "", SWT.BORDER);
        heightField.setLayoutData(fieldGd);
        heightField.setText((height == null ? "" : height.toString()));
        toolkit.createLabel(client, "= 体积");
        volumeField = toolkit.createText(client, "", SWT.BORDER);
        volumeField.setText((volume == null ? "" : volume.toString()));
        volumeField.setEnabled(false);
        volumeField.setLayoutData(fieldGd);
        toolkit.createLabel(client, "重量");
        weightField = toolkit.createText(client, "", SWT.BORDER);
        weightField.setText(weight == null ? "" : weight);
        weightField.setLayoutData(fieldGd);
        
        addValueChangeListener();
		return client;
	}
	
	private void addValueChangeListener(){
		if (lengthField != null)
			lengthField.addKeyListener(getLWHChangeListener());
		if (widthField != null)
			widthField.addKeyListener(getLWHChangeListener());
		if (heightField != null)
			heightField.addKeyListener(getLWHChangeListener());
	}
	
	private KeyListener getLWHChangeListener(){
		return new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				refreshVolume();
			}
		};
	}
	
	private void refreshVolume() {
		BigDecimal newLength = null;
		BigDecimal newWidth = null;
		BigDecimal newHeight = null;
		BigDecimal newVolume = null;
		BigDecimal volumeChange = null;
//		
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			List<ADRefList> volumeChanges= adManager.getEntityList(Env.getOrgRrn(), ADRefList.class, Integer.MAX_VALUE, "referenceName = 'VolumeChange'", null);
			if(volumeChanges!=null && volumeChanges.size()>0){
				ADRefList adRefList = volumeChanges.get(0);
				volumeChange = new BigDecimal(adRefList.getValue());
			}else{
				volumeChange = BigDecimal.ZERO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(lengthField.getText() != null && lengthField.getText().trim().length() > 0){
			newLength = new BigDecimal(lengthField.getText());
		}
		if(widthField.getText() != null && ((String)widthField.getText()).trim().length() > 0){
			newWidth = new BigDecimal(widthField.getText());
		}
		if(heightField.getText() != null && ((String)heightField.getText()).trim().length() > 0){
			newHeight = new BigDecimal(heightField.getText());
		}

		if(newLength != null && newWidth != null && newHeight != null){
			newVolume = newLength.multiply(newWidth).multiply(newHeight).divide(volumeChange);
			volumeField.setText(newVolume.toString());
		}else{
			volumeField.setText("");
		}
		boolean f1 = false;
		boolean f2 = false;
		boolean f3 = false;
		if(length == null || length.compareTo(newLength) != 0){
			f1 = true;
		}else{
			f1 = false;
		}
		if(width == null || width.compareTo(newWidth) != 0){
			f2 = true;
		}else{
			f2 = false;
		}
		if(height == null || height.compareTo(newHeight) != 0){
			f3 = true;
		}else{
			f3 = false;
		}
		if(volume == null || volume.compareTo(newVolume) != 0){
			isVolumeChanged = true;
		}else{
			isVolumeChanged = (f1 || f2 || f3);
		}
		materialProterpies.setVolumeChanged(isVolumeChanged);
	}
	
	protected boolean validate(){
		try{
			if (lengthField != null){
				String l = lengthField.getText();
				try {
					BigDecimal bL = new BigDecimal(l);
				} catch (Exception e) {
					throw new ClientException("输入的长存在非法数字,请重新输入!", e);
				}
			}
			if (widthField != null){
				String w = widthField.getText();
				try {
					BigDecimal bW = new BigDecimal(w);
				} catch (Exception e) {
					throw new ClientException("输入的宽存在非法数字,请重新输入!", e);
				}
			}
			if (heightField != null){
				String h = heightField.getText();
				try {
					BigDecimal bH = new BigDecimal(h);
				} catch (Exception e) {
					throw new ClientException("输入的高存在非法数字,请重新输入!", e);
				}
			}
			
			if(weightField != null){
				String we = weightField.getText();
				try {
					BigDecimal bWe = new BigDecimal(we);
				} catch (Exception e) {
					throw new ClientException("输入的重量存在非法数字,请重新输入!", e);
				}
			}
		}catch(Exception e){
			logger.error("EditVolumeDialog : validate()",e);
			message = e.getMessage();
			return false;
		}
		return true;
	}
	
	@Override
	protected void okPressed() {
		setMessage("");
		if(validate()){
			material.setLength(new BigDecimal(lengthField.getText()));
			material.setWidth(new BigDecimal(widthField.getText()));
			material.setHeight(new BigDecimal(heightField.getText()));
			material.setVolume(new BigDecimal(volumeField.getText()));
			material.setWeight(weightField.getText());
			materialProterpies.setAdObject(material);
			if(isVolumeChanged){
				materialProterpies.saveVolume();
			}else{
				materialProterpies.refresh();
				materialProterpies.saveAdapter();
			}
			super.okPressed();
		}
		setMessage(message, IMessageProvider.ERROR);
	}
}
