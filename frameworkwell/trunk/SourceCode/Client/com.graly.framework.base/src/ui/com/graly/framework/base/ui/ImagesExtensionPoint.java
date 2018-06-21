package com.graly.framework.base.ui;

import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

import com.graly.framework.base.application.Activator;

public class ImagesExtensionPoint {
	
	private final static Logger logger = Logger.getLogger(ImagesExtensionPoint.class);

    public final static String X_POINT = "com.graly.framework.base.images";
    public final static String E_IMAGE = "image";
    public final static String A_ID = "id";
    public final static String A_SRC = "src";
    public final static String A_THUMBNAIL = "thumbnail";

    public void initialize() {
        loadAdapterExtensionPoints();
    }

    public void loadAdapterExtensionPoints() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(X_POINT);
        if (point == null) {
            return;
        }
        IExtension[] extensions = point.getExtensions();
        for (IExtension element : extensions) {
            loadAdapterExtension(element);
        }
    }

    private void loadAdapterExtension(IExtension extension) {
        IConfigurationElement[] elements = extension.getConfigurationElements();
        for (IConfigurationElement elem : elements) {
            if (elem.getName().equals(E_IMAGE)) {
                loadImageElement(elem);
            } else {
            	logger.warn("ImagesExtensionPoint: Unsupport elements " + elem.getName());
            }
        }
    }

    private void loadImageElement(IConfigurationElement element) {
        String id = element.getAttribute(A_ID);
        String src = element.getAttribute(A_SRC);
        String thumbnail = element.getAttribute(A_THUMBNAIL);
        String ns = element.getNamespaceIdentifier();
        Bundle bundle = Platform.getBundle(ns);
        URL url = bundle.getEntry(src);
        Activator.putImage(id, ImageDescriptor.createFromURL(url));
        if (thumbnail != null && thumbnail.trim().length() > 0) {
            URL thumbnailUrl = bundle.getEntry(thumbnail);
            Activator.putImage(id+".thumbnail", ImageDescriptor.createFromURL(thumbnailUrl));
        }
    }
}
