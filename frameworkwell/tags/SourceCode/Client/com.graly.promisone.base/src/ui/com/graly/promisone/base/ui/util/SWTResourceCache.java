package com.graly.promisone.base.ui.util;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jface.resource.ImageRegistry;
import com.graly.promisone.base.application.Activator;

public class SWTResourceCache {
	
	/**
     * The cache of images that have been dispensed by this provider. Maps
     * ImageDescriptor->Image. Caches are all static to avoid creating extra
     * system resources for very common images, font and colors.
     */
    private static Map<Object, Image> imageTable = new Hashtable<Object, Image>(40);

    /**
     * The cache of colors that have been dispensed by this provider. Maps
     * RGB->Color.
     */
    private static Map<Object, Color> colorTable = new Hashtable<Object, Color>(7);
    static {
    	Color color =new Color(Display.getCurrent(), 234, 126, 10);
		colorTable.put("Folder", color);
		color =new Color(Display.getCurrent(), 55, 114, 221);
		colorTable.put("Function", color);
		color =new Color(Display.getCurrent(), 0, 0, 0);
		colorTable.put("Black", color);
		color =new Color(Display.getCurrent(), 255, 0, 0);
		colorTable.put("Red", color);
		color =new Color(Display.getCurrent(), 253, 72, 34);
		colorTable.put("Held", color);
		color =new Color(Display.getCurrent(), 57, 244, 43);
		colorTable.put("Run", color);
    }
    /**
     * The cache of fonts that have been dispensed by this provider. Maps
     * FontData->Font.
     */
    private static Map<Object, Font> fontTable = new Hashtable<Object, Font>(7);
    static {
		FontData fontData = new FontData("Verdana", 10, SWT.BOLD);
		Font font = new Font(Display.getCurrent(), fontData);
		font.getFontData()[0].setStyle(2);
		fontTable.put("Verdana", font);
		fontData = new FontData("Verdana", 13, SWT.NORMAL);
		font = new Font(Display.getCurrent(), fontData);
		font.getFontData()[0].setStyle(SWT.NORMAL);
		fontTable.put("Verdana2", font);
    }
    /**
     * Disposes of all allocated images, colors and fonts when shutting down the
     * plug-in.
     */
    public static final void shutdown() {
        if (imageTable != null) {
            for (Object element : imageTable.values()) {
                ((Image) element).dispose();
            }
            imageTable = new Hashtable<Object, Image>(40);
        }
        if (colorTable != null) {
            for (Object element : colorTable.values()) {
                ((Color) element).dispose();
            }
            colorTable = new Hashtable<Object, Color>(7);
        }
        if (fontTable != null) {
            for (Object element : fontTable.values()) {
                ((Font) element).dispose();
            }
            fontTable = new Hashtable<Object, Font>(7);
        }
    }

    /**
     * Get the Map of RGBs to Colors.
     * @return Returns the colorTable.
     */
    public final static Map getColorTable() {
        return colorTable;
    }

    /**
     * Return the map of FontDatas to Fonts.
     * @return Returns the fontTable.
     */
    public final static Map getFontTable() {
        return fontTable;
    }

    /**
     * Return the map of ImageDescriptors to Images.
     * @return Returns the imageTable.
     */
    public final static Map getImageTable() {
        return imageTable;
    }

    public static Image getImage(Object key) {
    	if (imageTable.get(key) == null){
    		Image image = Activator.getImage((String)key);
    		if (image != null) {
    			putImage(key, image);
    			return image;
    		}
    	}
        return imageTable.get(key);
    }

    public static void putImage(Object key, Image image) {
        imageTable.put(key, image);
    }

    public static Font getFont(Object key) {
        return fontTable.get(key);
    }

    public static void putFont(Object key, Font font) {
        fontTable.put(key, font);
    }

    public static Color getColor(Object key) {
        return colorTable.get(key);
    }

    public static void putColor(Object key, Color color) {
        colorTable.put(key, color);
    }
    
}
