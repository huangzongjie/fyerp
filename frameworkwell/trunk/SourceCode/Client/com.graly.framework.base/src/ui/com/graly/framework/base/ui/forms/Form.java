package com.graly.framework.base.ui.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.field.BooleanField;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.DateField;
import com.graly.framework.base.ui.forms.field.DateTimeField;
import com.graly.framework.base.ui.forms.field.DualListField;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.HiddenField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.ImageField;
import com.graly.framework.base.ui.forms.field.RadioField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.forms.field.TableItemFilterField;
import com.graly.framework.base.ui.forms.field.TableListField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.UrlField;

public abstract class Form extends Composite {
	
	private static final Logger logger = Logger.getLogger(Form.class);
	
	protected ScrolledForm form;
    protected FormToolkit toolkit;
    protected Object object;
    
    protected int mLeftPadding = 5;
    protected int mTopPadding = 0;
    protected int mRightPadding = 5;
    protected int mBottomPadding = 0;

    protected int mHorizSpacing = 5;
    protected int mVertSpacing = 5;
    
    private int gridY = 1;
    
    protected LinkedHashMap<String, IField> fields = new LinkedHashMap<String, IField>(10, (float)0.75, false);
    
    public Form(Composite parent, int style, Object object) {
    	super(parent, style);
    	this.object = object;
    }
    
    public abstract void addFields();
    
    public abstract boolean validate();
    
    public void createForm(){
        addFields();
        createContent();
        if (object != null) {
            loadFromObject();
        }
    }
    
    public boolean saveToObject() {
    	return false;
    }
    
    public void loadFromObject() {
    }
    
    protected void addField(String key, IField field) {
        fields.put(key, field);
    }

    public void set(String key, Object value) {
        IField field = fields.get(key);
        if (field != null) {
            field.setValue(value);
        }
    }

    public Object get(String key) {
        IField field = fields.get(key);
        if (field != null) {
            return field.getValue();
        }
        return null;
    }

    public void refresh() {
        for (IField field : fields.values()) {
            field.refresh();
        }
    }
    
    protected void createContent() {
        toolkit = new FormToolkit(getDisplay());
        setLayout(new FillLayout());
        form = toolkit.createScrolledForm(this);
        
        Composite body = getFormBody();
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = mVertSpacing;
        layout.horizontalSpacing = mHorizSpacing;
        layout.marginLeft = mLeftPadding;
        layout.marginRight = mRightPadding;
        layout.marginTop = mTopPadding;
        layout.marginBottom = mBottomPadding;
        body.setLayout(layout);
        
        // first: create the children controls and compute the
        // number of columns to be used by the grid layout
        int cols = 1;
        for (IField f : fields.values()){
            f.createContent(body, toolkit);
            
            Control[] ctrls = f.getControls();
            int c = f.getColumnsCount();
            c = c > ctrls.length ? c : ctrls.length;
            if (cols < c) {
                cols = c;
            }
        }
        layout.numColumns = cols * this.getGridY();
        
        // second: place the created controls inside the grid layout cells
        int i = 0;
        for (IField f : fields.values()) {
            Control[] ctrls = f.getControls();
            if (ctrls.length == 0) {
            	continue;
            }
            i++;
            if (i % getGridY() == 0 && getGridY() != 1){
	            GridData gd = (GridData)ctrls[0].getLayoutData();
	            if (gd == null) {
	                gd = new GridData();
	                ctrls[0].setLayoutData(gd);
	            }
	            gd.horizontalIndent = 10;
            }
            // get the last r controls that should be spanned horizontally
            // to fit into the grid
            int r = ctrls.length % cols;
            if (r > 0) {
                GridData gd = (GridData)ctrls[ctrls.length-1].getLayoutData();
                if (gd == null) {
                    gd = new GridData();
                    ctrls[ctrls.length-1].setLayoutData(gd);
                }
                gd.horizontalSpan = cols - r + 1;
            }
            if (f.getADField() != null){
            	ADField field = (ADField)f.getADField();
            	if (field.getIsSameline()){
            		int num = (i - 1) % this.getGridY();
            		if (num != 0){
            			Label l = toolkit.createLabel(ctrls[0].getParent(), "");
            			GridData gd = new GridData();
            			gd.horizontalSpan = cols * (this.getGridY() - num);
            			l.setLayoutData(gd);
            			l.moveAbove(ctrls[0]);
            		}
                	GridData gd = (GridData)ctrls[ctrls.length-1].getLayoutData();
                	if (gd == null) {
                        gd = new GridData();
                        ctrls[ctrls.length-1].setLayoutData(gd);
                    }
            		gd.horizontalSpan = cols * this.getGridY() - (r == 0 ? 1 : r - 1);
            		gd = (GridData)ctrls[0].getLayoutData();
    	            if (gd == null) {
    	                gd = new GridData();
    	                ctrls[0].setLayoutData(gd);
    	            }
    	            gd.horizontalIndent = 0;
            		i = 0;
            	}
            }
        }
    }

	/**
	 * @return
	 */
	public Composite getFormBody() {
		return form.getBody();
	}
    
    public LinkedHashMap<String, IField> getFields() {
        return fields;
    }
    
    public Object getObject() {
        return object;
    }
    
    public void setObject(Object object) {
        this.object = object;
    }
    
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public int getGridY() {
		return gridY;
	}
	
    @Override
    public void dispose() {
        if (toolkit != null) {
            toolkit.dispose();
            toolkit = null;
        }
        super.dispose();
    }
    
    public TextField createText(String id, String label, int limit){
    	return createText(id, label, "", limit);
    }
    
    public TextField createText(String id, String label, String value, int limit) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value != null ? value : ""); 
        fe.setLength(limit);
        return fe;
    }
    
    public TextField createUpperText(String id, String label, int limit){
    	return createUpperText(id, label, "", limit);
    }
    
    public TextField createUpperText(String id, String label, String value, int limit) {
    	TextField fe = new TextField(id, true);
        fe.setLabel(label);
        fe.setValue(value != null ? value : ""); 
        fe.setLength(limit);
        return fe;
    }
    
    public TextField createPassword(String id, String label, int limit) {
        return createPassword(id, label, "", limit); 
    }

    public TextField createPassword(String id, String label, String value, int limit) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.appendStyle(SWT.PASSWORD);
        fe.setLength(limit);
        return fe;
    }

    public TextField createReadOnlyText(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setReadOnly(true);
        return fe;
    }

    public TextField createTextArea(String id, String label) {
        return createTextArea(id, label, null);
    }

    public TextField createTextArea(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setMultiLine(true);
        return fe;
    }
    
    public TextField createReadOnlyTextArea(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setMultiLine(true);
        fe.setReadOnly(true);
        return fe;
    }
    
    public TableItemFilterField createTableItemFilterField(String id, String label,
    	TableViewer viewer, ADTable adTable, String whereClause) {
    	TableItemFilterField bt = new TableItemFilterField(id, viewer, adTable, SWT.CHECK, whereClause);
        bt.setLabel(label);
        bt.setValue(null);
        return bt;
    }
    
    public TableItemFilterField createTableItemFilterField(String id, String label,
        	TableViewer viewer, ADTable adTable) {
        	TableItemFilterField bt = new TableItemFilterField(id, viewer, adTable, SWT.CHECK, null);
            bt.setLabel(label);
            bt.setValue(null);
            return bt;
    }
    
    public TextField createReadOnlyMultiLineText(String id, String label, String value) {
    	TextField fe = new TextField(id);
        fe.setLabel(label);
        fe.setValue(value);
        fe.setReadOnly(true);
        fe.setMultiLine(true);
        return fe;
    }
    
    public SeparatorField createSeparatorField(String id, String label) {
    	SeparatorField fe = new SeparatorField(id);
        fe.setLabel(label);
        return fe;
    }
    
    public RefTextField createRefTextField(String id, String label) {
    	return createRefTextField(id, label, 32);
    }
    
    public RefTextField createRefTextField(String id, String label, int limit) {
    	RefTextField fe = new RefTextField(id);
        fe.setLabel(label);
        fe.setLength(limit);
        return fe;
    }
    
    public BooleanField createBooleanField(String id, String label) {
        return createBooleanField(id, label, false);
    }

    public BooleanField createBooleanField(String id, String label, boolean value) {
    	BooleanField fe = new BooleanField(id);
        fe.setLabel(label);
        fe.setValue(new Boolean(value));
        return fe;
    }
    
    public ComboField createComboField(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items);
        fe.setLabel(label);
        return fe;
    }

    public ComboField createComboField(String id, String label, LinkedHashMap<String, String> items, String value) {
    	ComboField fe = new ComboField(id, items);
        fe.setLabel(label);
        fe.setValue(value);
        return fe;
    }

    public ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        return fe;
    }

    public ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items, String value) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        fe.setValue(value);
        return fe;
    }
    
    public RadioField createRadioGroup(String id, String label, LinkedHashMap<String, String> items) {
    	RadioField fe = new RadioField(id, items);
        fe.setLabel(label);
        return fe;
    }

    public RadioField createRadioGroup(String id, String label, LinkedHashMap<String, String> items, int selection) {
    	RadioField fe = new RadioField(id, items);
        fe.setLabel(label);
        fe.setValue(new Integer(selection));
        return fe;
    }
    
    public DateField createDateField(String id, String label) {
    	DateField fe = new DateField(id);
        fe.setLabel(label);
        fe.setValue(null);
        return fe;
    }
    
    public DateField createShortDateField(String id, String label) {
    	DateField fe = new DateField(id, SWT.BORDER, SWT.SHORT);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
    
    public DateTimeField createDateTimeField(String id, String label) {
    	DateTimeField fe = new DateTimeField(id);
        fe.setLabel(label);
        fe.setValue(null);
        return fe;
    }
    
    public CalendarField createCalendarField(String id, String label) {
    	CalendarField fe = new CalendarField(id);
        fe.setLabel(label);
        fe.setValue(null);
        return fe;
    }
    
    public FromToCalendarField createFromToCalendarField(String id, String label) {
    	FromToCalendarField fe = new FromToCalendarField(id);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
    
    public FromToCalendarField createFromToCalendarField(String id, String label, Date fromDate, Date toDate) {
    	FromToCalendarField fe = new FromToCalendarField(id, fromDate, toDate);
    	fe.setLabel(label);
    	fe.setValue(null);
    	return fe;
    }
    
    public ImageField createImageField(String id, String label) {
    	ImageField imf = new ImageField(id);
    	imf.setLabel(label);
    	imf.setValue("");
    	return imf;
    }
    
    public ImageField createImageField(String id, String label, String imageUrl) {
    	ImageField imf = new ImageField(id, imageUrl);
    	imf.setLabel(label);
    	return imf;
    }
    
    public RefTableField createRefTableFieldList(String id, String label, TableViewer viewer, ADRefTable refTable) {
    	int mStyle = SWT.READ_ONLY;
    	RefTableField fe = new RefTableField(id, viewer, refTable, mStyle);
        fe.setLabel(label);
        return fe;
    }
    
    public RefTableField createRefTableFieldCombo(String id, String label, TableViewer viewer, ADRefTable refTable) {
    	RefTableField fe = new RefTableField(id, viewer, refTable);
        fe.setLabel(label);
        return fe;
    }
    
    public RefTableField createRefTableFieldComboReadOnly(String id, String label, TableViewer viewer, ADRefTable refTable) {
    	RefTableField fe = new RefTableField(id, viewer, refTable);
        fe.setLabel(label);
        fe.setReadOnly(true);
        return fe;
    }
    
    public TableListField createTableListField(String id, String label, TableViewer viewer) {
    	TableListField fe = new TableListField(id, viewer);
        fe.setLabel(label);
        return fe;
    }
    
    public DualListField createDualListField(String id, String label, ADTable adTable, List inputList) {
    	List<Object> list = new ArrayList<Object>();
    	list.addAll(inputList);
    	DualListField fe = new DualListField(id, adTable, list);
        fe.setLabel(label);
        return fe;
    }
    
    public SearchField createSearchField(String id, String label, ADTable adTable,
    		ADRefTable refTable, String whereClause, int style) {
    	SearchField fe = new SearchField(id, adTable, refTable, whereClause, style);
    	fe.setLabel(label);
    	return fe;
    }
    
    public UrlField createUrlField(String id, String label, int style) {
    	UrlField fe = new UrlField(id, style);
    	fe.setLabel(label);
    	return fe;
    }
    
    public HiddenField createHiddenField(String id) {
    	HiddenField fe = new HiddenField(id);
        return fe;
    }
}
