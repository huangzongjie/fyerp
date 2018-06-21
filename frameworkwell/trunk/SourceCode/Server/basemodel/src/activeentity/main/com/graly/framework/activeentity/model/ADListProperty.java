package com.graly.framework.activeentity.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("L")
public class ADListProperty extends ADProperty {
	private static final long serialVersionUID = 1L;

	@Transient
	public List<String> getValues(){
		List<String> values = new ArrayList<String>();
		if(value != null){
			String[] vals = value.split(";");
			for(int i=0; i<vals.length; i++){
				values.add(vals[i]);
			}
		}
		return values;
	}
}
