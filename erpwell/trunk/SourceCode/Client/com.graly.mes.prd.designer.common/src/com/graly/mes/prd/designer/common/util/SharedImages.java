package com.graly.mes.prd.designer.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class SharedImages {
	
	public static final SharedImages INSTANCE = new SharedImages();

	private final Map imageMap = new HashMap();
	
	public Image getImage(ImageDescriptor imageDescriptor) {
		if (imageDescriptor == null) {
			return null;
		}
		Image image = (Image)imageMap.get(imageDescriptor);
		if (image == null) {
			image = imageDescriptor.createImage();
			imageMap.put(imageDescriptor, image);
		}
		return image;
	}
	
	public void dispose() {
		Iterator iter = imageMap.values().iterator();
		while (iter.hasNext()) {
			((Image)iter.next()).dispose();
		}
		imageMap.clear();
	}
	
}
