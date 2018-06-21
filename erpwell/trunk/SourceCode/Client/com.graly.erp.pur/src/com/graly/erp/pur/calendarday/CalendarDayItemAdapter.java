package com.graly.erp.pur.calendarday;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.views.ListItemAdapter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CalendarDayItemAdapter<T extends Object> extends ListItemAdapter {
	public static List<CalendarDay> calendarDay;
	private static final String CHECKED_KEY = "CHECKED";
	private static final String UNCHECK_KEY = "UNCHECKED";

	public CalendarDayItemAdapter() {
		super();
		if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
			JFaceResources.getImageRegistry().put(UNCHECK_KEY, makeShot(UI.getActiveShell(), false));
			JFaceResources.getImageRegistry().put(CHECKED_KEY, makeShot(UI.getActiveShell(), true));
		}
	}

	public CalendarDayItemAdapter(List<T> initialElements) {
		super(initialElements);
	}

	@Override
	public Color getForeground(Object element, String id) {
		if (element instanceof CalendarDay) {
			CalendarDay calendarDay = (CalendarDay) element;
			Date day = calendarDay.getDay();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(day);
			int d = calendar.get(Calendar.DAY_OF_WEEK);
			if (d == 1 || d == 7) {
				return SWTResourceCache.getColor("Red");
			}
		}
		return null;
	}

	@Override
	public String getText(Object object, String id) {
		if ("isHoliday".equals(id)) {
			return "";
		}
		if (object instanceof String) {
			return (String) object;
		}
		if (object != null && id != null) {
			try {
				Object property = PropertyUtil.getPropertyForString(object, id);
				return (String) property;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		if (object != null && id != null) {
			try {
				Object property = PropertyUtil.getPropertyForIField(object, id);
				if (property instanceof Boolean) {
					if (Boolean.TRUE.equals(property)) {
						return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY);
					} else {
						return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Image makeShot(Control control, boolean type) {
		// Hopefully no platform uses exactly this color because we'll make
		// it transparent in the image.
		Color greenScreen = new Color(control.getDisplay(), 222, 223, 224);

		Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);

		// otherwise we have a default gray color
		shell.setBackground(greenScreen);

		Button button = new Button(shell, SWT.CHECK);
		button.setBackground(greenScreen);
		button.setSelection(type);

		// otherwise an image is located in a corner
		button.setLocation(1, 1);
		Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		// otherwise an image is stretched by width
		bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
		bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
		button.setSize(bsize);
		shell.setSize(bsize);

		shell.open();
		GC gc = new GC(shell);
		Image image = new Image(control.getDisplay(), bsize.x, bsize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		shell.close();

		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(greenScreen.getRGB());

		return new Image(control.getDisplay(), imageData);
	}

}
