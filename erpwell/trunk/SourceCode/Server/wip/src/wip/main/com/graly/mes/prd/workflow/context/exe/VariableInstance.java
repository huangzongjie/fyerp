package com.graly.mes.prd.workflow.context.exe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.context.exe.converter.BooleanToStringConverter;
import com.graly.mes.prd.workflow.context.exe.converter.ByteToLongConverter;
import com.graly.mes.prd.workflow.context.exe.converter.BytesToByteArrayConverter;
import com.graly.mes.prd.workflow.context.exe.converter.CharacterToStringConverter;
import com.graly.mes.prd.workflow.context.exe.converter.DateToLongConverter;
import com.graly.mes.prd.workflow.context.exe.converter.DoubleToStringConverter;
import com.graly.mes.prd.workflow.context.exe.converter.FloatToDoubleConverter;
import com.graly.mes.prd.workflow.context.exe.converter.FloatToStringConverter;
import com.graly.mes.prd.workflow.context.exe.converter.IntegerToLongConverter;
import com.graly.mes.prd.workflow.context.exe.converter.SerializableToByteArrayConverter;
import com.graly.mes.prd.workflow.context.exe.converter.ShortToLongConverter;
import com.graly.mes.prd.workflow.context.exe.variableinstance.NullInstance;
import com.graly.mes.prd.workflow.context.exe.variableinstance.UnpersistableInstance;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.prd.workflow.graph.exe.Token;

/**
 * is a jbpm-internal class that serves as a base class for classes that store
 * variable values in the database.
 */
@Entity
@Table(name="WF_PARAMETER_INSTANCE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue("V")
public abstract class VariableInstance extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	protected String name = null;
	
	@ManyToOne
	@JoinColumn(name = "TOKEN_RRN", referencedColumnName = "OBJECT_RRN")
	protected Token token = null;
	
	@ManyToOne
	@JoinColumn(name = "TOKEN_VARIABLE_MAP_RRN", referencedColumnName = "OBJECT_RRN")
	protected TokenVariableMap tokenVariableMap = null;
	
	@ManyToOne
	@JoinColumn(name = "PROCESS_INSTANCE_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessInstance processInstance = null;
	
	@Column(name="CONVERTER")
	private String converter = null;
	
	@Transient
	protected Object valueCache = null;
	
	@Transient
	protected boolean isValueCached = false;

	static Map<String, Converter> converterMap = new HashMap<String, Converter>();
	static {
		converterMap.put("B", new BooleanToStringConverter());
		converterMap.put("Y", new BytesToByteArrayConverter());
		converterMap.put("E", new ByteToLongConverter());
		converterMap.put("C", new CharacterToStringConverter());
		converterMap.put("A", new DateToLongConverter());
		converterMap.put("D", new DoubleToStringConverter());
		converterMap.put("F", new FloatToStringConverter());
		converterMap.put("G", new FloatToDoubleConverter());
		converterMap.put("I", new IntegerToLongConverter());
		converterMap.put("R", new SerializableToByteArrayConverter());
		converterMap.put("H", new ShortToLongConverter());
	}
	// constructors
	// /////////////////////////////////////////////////////////////

	public VariableInstance() {
	}

	public static VariableInstance create(Token token, String name, Object value) {

		VariableInstance variableInstance = null;
		if (value == null) {
			variableInstance = new NullInstance();
		} else {
			variableInstance = createVariableInstance(value);
		}

		variableInstance.token = token;
		variableInstance.name = name;
		variableInstance.processInstance = (token != null ? token.getProcessInstance() : null);
		variableInstance.setValue(value);
		return variableInstance;
	}

	public static VariableInstance createVariableInstance(Object value) {
		VariableInstance variableInstance = null;

		Iterator<JbpmType> iter = JbpmType.getJbpmTypes().iterator();
		while ((iter.hasNext()) && (variableInstance == null)) {
			JbpmType jbpmType = (JbpmType) iter.next();

			if (jbpmType.matches(value)) {
				variableInstance = jbpmType.newVariableInstance();
			}
		}

		if (variableInstance == null) {
			variableInstance = new UnpersistableInstance();
		}

		return variableInstance;
	}

	// abstract methods
	// /////////////////////////////////////////////////////////

	/**
	 * is true if this variable-instance supports the given value, false
	 * otherwise.
	 */
	public abstract boolean isStorable(Object value);

	/**
	 * is the value, stored by this variable instance.
	 */
	protected abstract Object getObject();

	/**
	 * stores the value in this variable instance.
	 */
	protected abstract void setObject(Object value);

	// variable management
	// //////////////////////////////////////////////////////

	public boolean supports(Object value) {
		if (getConverter() != null) {
			return getConverter().supports(value);
		}
		return isStorable(value);
	}

	public void setValue(Object value) {
		valueCache = value;
		isValueCached = true;

		if (getConverter() != null) {
			if (!getConverter().supports(value)) {
				throw new JbpmException(
						"the converter '"
								+ getConverter().getClass().getName()
								+ "' in variable instance '"
								+ this.getClass().getName()
								+ "' does not support values of type '"
								+ value.getClass().getName()
								+ "'.  to change the type of a variable, you have to delete it first");
			}
			value = getConverter().convert(value);
		}
		if ((value != null) && (!this.isStorable(value))) {
			throw new JbpmException(
					"variable instance '"
							+ this.getClass().getName()
							+ "' does not support values of type '"
							+ value.getClass().getName()
							+ "'.  to change the type of a variable, you have to delete it first");
		}
		setObject(value);
	}

	public Object getValue() {
		if (isValueCached) {
			return valueCache;
		}
		Object value = getObject();
		if ((value != null) && (getConverter() != null)) {
			if (getConverter() instanceof SerializableToByteArrayConverter
					&& processInstance != null) {
				SerializableToByteArrayConverter s2bConverter = (SerializableToByteArrayConverter) getConverter();
				value = s2bConverter.revert(value, processInstance.getProcessDefinition());
			} else {
				value = getConverter().revert(value);
			}
			valueCache = value;
			isValueCached = true;
		}
		return value;
	}

	public void removeReferences() {
		tokenVariableMap = null;
		token = null;
		processInstance = null;
	}

	// utility methods /////////////////////////////////////////////////////////

	public String toString() {
		return "${" + name + "}";
	}

	// getters and setters
	// //////////////////////////////////////////////////////

	public String getName() {
		return name;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public Token getToken() {
		return token;
	}

	public void setTokenVariableMap(TokenVariableMap tokenVariableMap) {
		this.tokenVariableMap = tokenVariableMap;
	}

	public void setConverter(Converter converter) {
		if (converter instanceof BooleanToStringConverter) {
			this.converter = "B";
		} else if (converter instanceof BytesToByteArrayConverter) {
			this.converter = "Y";
		} else if (converter instanceof ByteToLongConverter) {
			this.converter = "E";
		} else if (converter instanceof CharacterToStringConverter) {
			this.converter = "C";
		} else if (converter instanceof DateToLongConverter) {
			this.converter = "A";
		} else if (converter instanceof DoubleToStringConverter) {
			this.converter = "D";
		} else if (converter instanceof FloatToStringConverter) {
			this.converter = "D";
		} else if (converter instanceof FloatToDoubleConverter) {
			this.converter = "G";
		} else if (converter instanceof IntegerToLongConverter) {
			this.converter = "I";
		} else if (converter instanceof SerializableToByteArrayConverter) {
			this.converter = "R";
		} else if (converter instanceof ShortToLongConverter) {
			this.converter = "H";
		} 
	}

	public Converter getConverter() {
		return converterMap.get(converter);
	}

	// private static Log log = LogFactory.getLog(VariableInstance.class);
}
