package com.graly.framework.activeentity.model;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("S")
public class ADSingleProperty extends ADProperty {
	private static final long serialVersionUID = 1L;
	
}
