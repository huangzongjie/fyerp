package com.graly.erp.pdm.material;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
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
import com.graly.erp.base.util.FormulaParser;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.product.client.CANAManager;
import com.graly.erp.product.model.CanaProduct;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

/**
 * 成本公式Dialog
 * @author Denny
 *
 */
public class CostFormulaDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(CostFormulaDialog.class);
			
	protected IManagedForm managedForm;
	protected Text formulaField, totalCostField;
	protected String costFormula;
	protected BigDecimal totalCost;
	protected Material material;
	private MaterialProperties materialProterpies;
	private String message = "";
	
	public CostFormulaDialog(Shell parentShell, MaterialProperties materialProterpies, IManagedForm managedForm, Material material) {
		super(parentShell);
		this.materialProterpies = materialProterpies;
		this.managedForm = managedForm;
		this.material = material;
		this.costFormula = material.getCostFormula();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
		setTitle("成本公式设置");
        Composite composite = (Composite) super.createDialogArea(parent);
        
        FormToolkit toolkit = managedForm.getToolkit();
        Composite client = toolkit.createComposite(composite, SWT.BORDER);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        GridData fieldGd = new GridData(GridData.FILL_HORIZONTAL);
        toolkit.createLabel(client, "成本公式");
        formulaField = toolkit.createText(client, "", SWT.BORDER);
        formulaField.setLayoutData(fieldGd);
        formulaField.setText((costFormula == null ? "A" : costFormula.toString()));
        formulaField.addKeyListener(getKeyListener());
        toolkit.createLabel(client, "材料成本合计(A):");
        totalCostField = toolkit.createText(client, "", SWT.BORDER);
        totalCostField.setLayoutData(fieldGd);
        totalCostField.setEnabled(false);
        totalCost = caculateCost();
        totalCostField.setText((totalCost == null ? "" : totalCost.toString()));
		return client;
	}

	/**
	 * @return
	 */
	private KeyListener getKeyListener() {
		return new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String formula = formulaField.getText().trim();
				
				if(formula != null && formula.length() > 0){
					if(formula.indexOf("A") != -1){
						String formula2 = formula.replaceAll("A", totalCost.toString());
						FormulaParser fp = new FormulaParser(formula2);
						totalCostField.setText(String.valueOf(fp.getResult()));
						message = fp.getMsg();
					}else{
						totalCostField.setText(totalCost.toString());
					}
				}else{
					formulaField.setText("A");
					totalCostField.setText(totalCost.toString());
				}
			}
        	
        };
	}
	
	private BigDecimal caculateCost(){
		try{
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			BigDecimal cost = null;
		if(material != null && material.getObjectRrn() != null){
			cost = pdmManager.caculateCostFormula(material);
		}
		return cost;
		}catch (Exception e){
			logger.error("CostFormulaDialog : caculateCost()", e);
		}
		return null;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		Message.getString("common.ok"), false);
        createButton(parent, IDialogConstants.CANCEL_ID,
        		Message.getString("common.cancel"), false);
    }
	
	@Override
	protected void okPressed() {
		try {
			setMessage("");
			if(message == null || message.trim().length() == 0){
				material.setCostFormula(formulaField.getText().trim());
				CANAManager canaManager = Framework.getService(CANAManager.class);
				CanaProduct canaProduct = canaManager.getCanaProduct(material.getMaterialId());
				canaProduct.setPrice2Low(new BigDecimal(totalCostField.getText()));
				canaManager.updateCanaProduct(canaProduct);
				materialProterpies.setAdObject(material);
				materialProterpies.saveAdapter();
				super.okPressed();
			}
			setMessage(message, IMessageProvider.ERROR);
		} catch (Exception e) {
			logger.error("CostFormulaDialog : okPressed()", e);
		}
	}
}
