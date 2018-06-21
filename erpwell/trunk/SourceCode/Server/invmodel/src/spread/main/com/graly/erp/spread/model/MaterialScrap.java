package com.graly.erp.spread.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value="SCRAP")
public class MaterialScrap extends MaterialSpread {
	private static final long	serialVersionUID	= 1L;
	
	@Column(name="SCRAP_DATE")
	private Date scrapDate;

	public Date getScrapDate() {
		return scrapDate;
	}

	public void setScrapDate(Date scrapDate) {
		this.scrapDate = scrapDate;
	}
}
