package com.graly.erp.pdm.bomselect;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.EnableExpendAll;
import com.graly.erp.pdm.bomedit.MaterialTreeManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.base.ui.views.ItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class BomSelectTreeManager extends MaterialTreeManager implements UnSelectBomManager {
	private static final Logger logger = Logger.getLogger(BomSelectTreeManager.class);
	private List<Bom> unSelectBoms;
	private ItemAdapterFactory factory;

	public BomSelectTreeManager(int style, EnableExpendAll eeall) {
		super(style, eeall);
		unSelectBoms = new ArrayList<Bom>();
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		factory = new ItemAdapterFactory();
		try {
			ItemAdapter materialAdapter = new BomSelectMaterialItemAdapter();
			BomSelectItemAdapter bomAdapter = new BomSelectItemAdapter(eeall);
			factory.registerAdapter(List.class, materialAdapter);
			factory.registerAdapter(Material.class, materialAdapter);
			factory.registerAdapter(Bom.class, bomAdapter);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return factory;
	}

	public void clearUnSelectBomContent() {
		unSelectBoms = new ArrayList<Bom>();
		if(factory != null) {
			if(factory.getAdapter(Material.class) != null) {
				BomSelectMaterialItemAdapter materialAdapter
					= (BomSelectMaterialItemAdapter)factory.getAdapter(Material.class);
				materialAdapter.setUnSelectBoms(new ArrayList<Bom>());
			}
			if(factory.getAdapter(Bom.class) != null) {
				BomSelectItemAdapter bomAdapter
					= (BomSelectItemAdapter)factory.getAdapter(Bom.class);
				bomAdapter.setUnSelectBoms(new ArrayList<Bom>());
			}
		}
	}

	@Override
	public List<Bom> getUnSelectBoms() {
		if(factory != null) {
			if(factory.getAdapter(Material.class) != null) {
				BomSelectMaterialItemAdapter materialAdapter
					= (BomSelectMaterialItemAdapter)factory.getAdapter(Material.class);
				unSelectBoms.addAll(materialAdapter.getUnSelectBoms());				
			}
			if(factory.getAdapter(Bom.class) != null) {
				BomSelectItemAdapter bomAdapter
					= (BomSelectItemAdapter)factory.getAdapter(Bom.class);
				unSelectBoms.addAll(bomAdapter.getUnSelectBoms());
			}
		}
		return unSelectBoms;
	}

	public void setSelectedBomType(Material selectMaterial) {
		if(factory != null) {
			if(factory.getAdapter(Material.class) != null) {
				BomSelectMaterialItemAdapter materialAdapter
					= (BomSelectMaterialItemAdapter)factory.getAdapter(Material.class);
				materialAdapter.setBomTypeMaterial(selectMaterial);
			}
		}
	}
	
	public Material getSelectedBomType() {
		if(factory != null) {
			if(factory.getAdapter(Material.class) != null) {
				BomSelectMaterialItemAdapter materialAdapter
					= (BomSelectMaterialItemAdapter)factory.getAdapter(Material.class);
				return materialAdapter.getBomTypeMaterial();
			}
		}
		return null;
	}
}
