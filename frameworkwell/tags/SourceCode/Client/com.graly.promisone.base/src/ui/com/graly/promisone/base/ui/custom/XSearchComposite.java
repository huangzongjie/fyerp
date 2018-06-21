package com.graly.promisone.base.ui.custom;


import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.internal.win32.SIZE;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.swt.internal.win32.TEXTMETRIC;
import org.eclipse.swt.internal.win32.TEXTMETRICA;
import org.eclipse.swt.internal.win32.TEXTMETRICW;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.accessibility.*;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.runtime.Framework;


public final class XSearchComposite extends Composite {
	
	Text text;
	Button arrow;
	boolean hasFocus;
	Listener listener, filter;
	Color foreground, background;
	Font font;
	
	private String valueColumn;
	private ADTable adTable;
	
public XSearchComposite (Composite parent, ADTable adTable, String valueColumn, int style) {
	super (parent, style = checkStyle (style));
	
	int textStyle = SWT.SINGLE;
	if ((style & SWT.READ_ONLY) != 0) textStyle |= SWT.READ_ONLY;
	if ((style & SWT.FLAT) != 0) textStyle |= SWT.FLAT;
	text = new Text (this, textStyle);
	int arrowStyle = SWT.FLAT;
	if ((style & SWT.FLAT) != 0) arrowStyle |= SWT.FLAT;
	arrow = new QueryButton (this, arrowStyle);
	arrow.setImage(SWTResourceCache.getImage("search-button"));
	
	listener = new Listener () {
		public void handleEvent (Event event) {
			if (text == event.widget) {
				textEvent (event);
				return;
			}
			if (arrow == event.widget) {
				arrowEvent (event);
				return;
			}
			if (XSearchComposite.this == event.widget) {
				comboEvent (event);
				return;
			}
			if (getShell () == event.widget) {
				handleFocus (SWT.FocusOut);
			}
		}
	};
	filter = new Listener() {
		public void handleEvent(Event event) {
			Shell shell = ((Control)event.widget).getShell ();
			if (shell == XSearchComposite.this.getShell ()) {
				handleFocus (SWT.FocusOut);
			}
		}
	};
	
	int [] comboEvents = {SWT.Dispose, SWT.Move, SWT.Resize};
	for (int i=0; i<comboEvents.length; i++) this.addListener (comboEvents [i], listener);
	
	int [] textEvents = {SWT.KeyDown, SWT.KeyUp, SWT.MenuDetect, SWT.Modify, SWT.MouseDown, SWT.MouseUp, SWT.Traverse, SWT.FocusIn, SWT.Verify};
	for (int i=0; i<textEvents.length; i++) text.addListener (textEvents [i], listener);
	
	int [] arrowEvents = {SWT.Selection, SWT.FocusIn};
	for (int i=0; i<arrowEvents.length; i++) arrow.addListener (arrowEvents [i], listener);
	
	this.adTable = adTable;
	this.valueColumn = valueColumn;
	
	initAccessible();
}
static int checkStyle (int style) {
	int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
	return style & mask;
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's text is modified, by sending
 * it one of the messages defined in the <code>ModifyListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ModifyListener
 * @see #removeModifyListener
 */
public void addModifyListener (ModifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Modify, typedListener);
}
/**
 * Adds the listener to the collection of listeners who will
 * be notified when the user changes the receiver's selection, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * <code>widgetSelected</code> is called when the combo's list selection changes.
 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
 * </p>
 *
 * @param listener the listener which should be notified when the user changes the receiver's selection
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #removeSelectionListener
 * @see SelectionEvent
 */
public void addSelectionListener(SelectionListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}

public void addArrowSelectionListener(SelectionListener listener) {
	arrow.addSelectionListener(listener);
}
/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's text is verified, by sending
 * it one of the messages defined in the <code>VerifyListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see VerifyListener
 * @see #removeVerifyListener
 * 
 * @since 3.3
 */
public void addVerifyListener (VerifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Verify,typedListener);
}
void arrowEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			handleFocus (SWT.FocusIn);
			break;
		}
		case SWT.Selection: {
			dropDown (true);
			break;
		}
	}
}
/**
 * Sets the selection in the receiver's text field to an empty
 * selection starting just before the first character. If the
 * text field is editable, this has the effect of placing the
 * i-beam at the start of the text.
 * <p>
 * Note: To clear the selected items in the receiver's list, 
 * use <code>deselectAll()</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #deselectAll
 */
public void clearSelection () {
	checkWidget ();
	text.clearSelection ();
}
void comboEvent (Event event) {
	switch (event.type) {
		case SWT.Dispose:
			Shell shell = getShell ();
			shell.removeListener (SWT.Deactivate, listener);
			Display display = getDisplay ();
			display.removeFilter (SWT.FocusIn, filter);
			text = null;    
			arrow = null;
			break;
		case SWT.Move:
			dropDown (false);
			break;
		case SWT.Resize:
			internalLayout (false);
			break;
	}
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int width = 0, height = 0;
	GC gc = new GC (text);
	int spacer = gc.stringExtent (" ").x; //$NON-NLS-1$
	int textWidth = gc.stringExtent (text.getText ()).x;
	gc.dispose ();
	Point textSize = text.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
	Point arrowSize = arrow.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
	int borderWidth = getBorderWidth ();
	height = Math.max (textSize.y, arrowSize.y);
	width = textWidth + 2*spacer + arrowSize.x + 2*borderWidth;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
	return new Point (width + 2*borderWidth, height + 2*borderWidth);
}
/**
 * Copies the selected text.
 * <p>
 * The current selection is copied to the clipboard.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.3
 */
public void copy () {
	checkWidget ();
	text.copy ();
}

/**
 * Cuts the selected text.
 * <p>
 * The current selection is first copied to the
 * clipboard and then deleted from the widget.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.3
 */
public void cut () {
	checkWidget ();
	text.cut ();
}
/**
 * Deselects the item at the given zero-relative index in the receiver's 
 * list.  If the item at the index was already deselected, it remains
 * deselected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to deselect
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int index) {
	checkWidget ();
}
/**
 * Deselects all selected items in the receiver's list.
 * <p>
 * Note: To clear the selection in the receiver's text field,
 * use <code>clearSelection()</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #clearSelection
 */
public void deselectAll () {
	checkWidget ();
}
void dropDown (boolean drop) {

}
/*
 * Return the lowercase of the first non-'&' character following
 * an '&' character in the given string. If there are no '&'
 * characters in the given string, return '\0'.
 */
char _findMnemonic (String string) {
	if (string == null) return '\0';
	int index = 0;
	int length = string.length ();
	do {
		while (index < length && string.charAt (index) != '&') index++;
		if (++index >= length) return '\0';
		if (string.charAt (index) != '&') return Character.toLowerCase (string.charAt (index));
		index++;
	} while (index < length);
 	return '\0';
}
/* 
 * Return the Label immediately preceding the receiver in the z-order, 
 * or null if none. 
 */
Label getAssociatedLabel () {
	Control[] siblings = getParent ().getChildren ();
	for (int i = 0; i < siblings.length; i++) {
		if (siblings [i] == this) {
			if (i > 0 && siblings [i-1] instanceof Label) {
				return (Label) siblings [i-1];
			}
		}
	}
	return null;
}
public Control [] getChildren () {
	checkWidget();
	return new Control [0];
}
/**
 * Gets the editable state.
 *
 * @return whether or not the receiver is editable
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.0
 */
public boolean getEditable () {
	checkWidget ();
	return text.getEditable();
}

public Menu getMenu() {
	return text.getMenu();
}
/**
 * Returns a <code>Point</code> whose x coordinate is the start
 * of the selection in the receiver's text field, and whose y
 * coordinate is the end of the selection. The returned values
 * are zero-relative. An "empty" selection as indicated by
 * the the x and y coordinates having the same value.
 *
 * @return a point representing the selection start and end
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getSelection () {
	checkWidget ();
	return text.getSelection ();
}

public int getStyle () {
	int style = super.getStyle ();
	style &= ~SWT.READ_ONLY;
	if (!text.getEditable()) style |= SWT.READ_ONLY; 
	return style;
}
/**
 * Returns a string containing a copy of the contents of the
 * receiver's text field.
 *
 * @return the receiver's text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText () {
	checkWidget ();
	return text.getText ();
}
/**
 * Returns the height of the receivers's text field.
 *
 * @return the text height
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTextHeight () {
	checkWidget ();
	return text.getLineHeight ();
}
/**
 * Returns the maximum number of characters that the receiver's
 * text field is capable of holding. If this has not been changed
 * by <code>setTextLimit()</code>, it will be the constant
 * <code>Combo.LIMIT</code>.
 * 
 * @return the text limit
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTextLimit () {
	checkWidget ();
	return text.getTextLimit ();
}

void handleFocus (int type) {
	if (isDisposed ()) return;
	switch (type) {
		case SWT.FocusIn: {
			if (hasFocus) return;
			if (getEditable ()) text.selectAll ();
			hasFocus = true;
			Shell shell = getShell ();
			shell.removeListener (SWT.Deactivate, listener);
			shell.addListener (SWT.Deactivate, listener);
			Display display = getDisplay ();
			display.removeFilter (SWT.FocusIn, filter);
			display.addFilter (SWT.FocusIn, filter);
			Event e = new Event ();
			notifyListeners (SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			if (!hasFocus) return;
			Control focusControl = getDisplay ().getFocusControl ();
			if (focusControl == arrow || focusControl == text) return;
			hasFocus = false;
			Shell shell = getShell ();
			shell.removeListener(SWT.Deactivate, listener);
			Display display = getDisplay ();
			display.removeFilter (SWT.FocusIn, filter);
			Event e = new Event ();
			notifyListeners (SWT.FocusOut, e);
			break;
		}
	}
}

void initAccessible() {
	AccessibleAdapter accessibleAdapter = new AccessibleAdapter () {
		public void getName (AccessibleEvent e) {
			String name = null;
			Label label = getAssociatedLabel ();
			if (label != null) {
				name = stripMnemonic (label.getText());
			}
			e.result = name;
		}
		public void getKeyboardShortcut(AccessibleEvent e) {
			String shortcut = null;
			Label label = getAssociatedLabel ();
			if (label != null) {
				String text = label.getText ();
				if (text != null) {
					char mnemonic = _findMnemonic (text);
					if (mnemonic != '\0') {
						shortcut = "Alt+"+mnemonic; //$NON-NLS-1$
					}
				}
			}
			e.result = shortcut;
		}
		public void getHelp (AccessibleEvent e) {
			e.result = getToolTipText ();
		}
	};
	getAccessible ().addAccessibleListener (accessibleAdapter);
	text.getAccessible ().addAccessibleListener (accessibleAdapter);
	
	arrow.getAccessible ().addAccessibleListener (new AccessibleAdapter() {
		public void getName (AccessibleEvent e) {
			e.result = SWT.getMessage ("SWT_Close") ; //$NON-NLS-1$ //$NON-NLS-2$
		}
		public void getKeyboardShortcut (AccessibleEvent e) {
			e.result = "Alt+Down Arrow"; //$NON-NLS-1$
		}
		public void getHelp (AccessibleEvent e) {
			e.result = getToolTipText ();
		}
	});

	getAccessible().addAccessibleTextListener (new AccessibleTextAdapter() {
		public void getCaretOffset (AccessibleTextEvent e) {
			e.offset = text.getCaretPosition ();
		}
		public void getSelectionRange(AccessibleTextEvent e) {
			Point sel = text.getSelection();
			e.offset = sel.x;
			e.length = sel.y - sel.x;
		}
	});
	
	getAccessible().addAccessibleControlListener (new AccessibleControlAdapter() {
		public void getChildAtPoint (AccessibleControlEvent e) {
			Point testPoint = toControl (e.x, e.y);
			if (getBounds ().contains (testPoint)) {
				e.childID = ACC.CHILDID_SELF;
			}
		}
		
		public void getLocation (AccessibleControlEvent e) {
			Rectangle location = getBounds ();
			Point pt = toDisplay (location.x, location.y);
			e.x = pt.x;
			e.y = pt.y;
			e.width = location.width;
			e.height = location.height;
		}
		
		public void getChildCount (AccessibleControlEvent e) {
			e.detail = 0;
		}
		
		public void getRole (AccessibleControlEvent e) {
			e.detail = ACC.ROLE_COMBOBOX;
		}
		
		public void getState (AccessibleControlEvent e) {
			e.detail = ACC.STATE_NORMAL;
		}

		public void getValue (AccessibleControlEvent e) {
			e.result = getText ();
		}
	});

	text.getAccessible ().addAccessibleControlListener (new AccessibleControlAdapter () {
		public void getRole (AccessibleControlEvent e) {
			e.detail = text.getEditable () ? ACC.ROLE_TEXT : ACC.ROLE_LABEL;
		}
	});

	arrow.getAccessible ().addAccessibleControlListener (new AccessibleControlAdapter() {
		public void getDefaultAction (AccessibleControlEvent e) {
			e.result = SWT.getMessage ("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	});
}
public boolean isFocusControl () {
	checkWidget();
	if (text.isFocusControl () || arrow.isFocusControl () ) {
		return true;
	} 
	return super.isFocusControl ();
}
void internalLayout (boolean changed) {
	Rectangle rect = getClientArea ();
	int width = rect.width;
	int height = rect.height;
	Point arrowSize = arrow.computeSize (SWT.DEFAULT, height, changed);
	text.setBounds (0, 0, width - arrowSize.x, height);
	arrow.setBounds (width - arrowSize.x, 0, arrowSize.x, 18);
}

/**
 * Pastes text from clipboard.
 * <p>
 * The selected text is deleted from the widget
 * and new text inserted from the clipboard.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.3
 */
public void paste () {
	checkWidget ();
	text.paste ();
}

public void redraw () {
	super.redraw();
	text.redraw();
	arrow.redraw();
}
public void redraw (int x, int y, int width, int height, boolean all) {
	super.redraw(x, y, width, height, true);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the receiver's text is modified.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ModifyListener
 * @see #addModifyListener
 */
public void removeModifyListener (ModifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Modify, listener);	
}
/**
 * Removes the listener from the collection of listeners who will
 * be notified when the user changes the receiver's selection.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #addSelectionListener
 */
public void removeSelectionListener (SelectionListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Selection, listener);
	removeListener(SWT.DefaultSelection,listener);	
}
/**
 * Removes the listener from the collection of listeners who will
 * be notified when the control is verified.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see VerifyListener
 * @see #addVerifyListener
 * 
 * @since 3.3
 */
public void removeVerifyListener (VerifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Verify, listener);
}

public void setBackground (Color color) {
	super.setBackground(color);
	background = color;
	if (text != null) text.setBackground(color);
	if (arrow != null) arrow.setBackground(color);
}
/**
 * Sets the editable state.
 *
 * @param editable the new editable state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * 
 * @since 3.0
 */
public void setEditable (boolean editable) {
	checkWidget ();
	text.setEditable(editable);
}
public void setEnabled (boolean enabled) {
	super.setEnabled(enabled);
	if (text != null) text.setEnabled(enabled);
	if (arrow != null) arrow.setEnabled(enabled);
}
public boolean setFocus () {
	checkWidget();
	if (isFocusControl ()) return true;
	return text.setFocus ();
}
public void setFont (Font font) {
	super.setFont (font);
	this.font = font;
	text.setFont (font);
	internalLayout (true);
}
public void setForeground (Color color) {
	super.setForeground(color);
	foreground = color;
	if (text != null) text.setForeground(color);
	if (arrow != null) arrow.setForeground(color);
}

/**
 * Sets the layout which is associated with the receiver to be
 * the argument which may be null.
 * <p>
 * Note: No Layout can be set on this Control because it already
 * manages the size and position of its children.
 * </p>
 *
 * @param layout the receiver's new layout or null
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLayout (Layout layout) {
	checkWidget ();
	return;
}
public void setMenu(Menu menu) {
	text.setMenu(menu);
}
/**
 * Sets the selection in the receiver's text field to the
 * range specified by the argument whose x coordinate is the
 * start of the selection and whose y coordinate is the end
 * of the selection. 
 *
 * @param selection a point representing the new selection start and end
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (Point selection) {
	checkWidget();
	if (selection == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	text.setSelection (selection.x, selection.y);
}

/**
 * Sets the contents of the receiver's text field to the
 * given string.
 * <p>
 * Note: The text field in a <code>Combo</code> is typically
 * only capable of displaying a single line of text. Thus,
 * setting the text to a string containing line breaks or
 * other special characters will probably cause it to 
 * display incorrectly.
 * </p>
 *
 * @param string the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	text.setText (string);
	text.selectAll ();
}
/**
 * Sets the maximum number of characters that the receiver's
 * text field is capable of holding to be the argument.
 *
 * @param limit new text limit
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTextLimit (int limit) {
	checkWidget();
	text.setTextLimit (limit);
}

public void setToolTipText (String string) {
	checkWidget();
	super.setToolTipText(string);
	arrow.setToolTipText (string);
	text.setToolTipText (string);		
}

public void setVisible (boolean visible) {
	super.setVisible(visible);
	/* 
	 * At this point the widget may have been disposed in a FocusOut event.
	 * If so then do not continue.
	 */
	if (isDisposed ()) return;
}

String stripMnemonic (String string) {
	int index = 0;
	int length = string.length ();
	do {
		while ((index < length) && (string.charAt (index) != '&')) index++;
		if (++index >= length) return string;
		if (string.charAt (index) != '&') {
			return string.substring(0, index-1) + string.substring(index, length);
		}
		index++;
	} while (index < length);
 	return string;
}
void textEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			handleFocus (SWT.FocusIn);
			break;
		}
		case SWT.KeyDown: {
			Event keyEvent = new Event ();
			keyEvent.time = event.time;
			keyEvent.character = event.character;
			keyEvent.keyCode = event.keyCode;
			keyEvent.stateMask = event.stateMask;
			notifyListeners (SWT.KeyDown, keyEvent);
			if (isDisposed ()) break;
			event.doit = keyEvent.doit;
			if (!event.doit) break;
			
			if (event.character == SWT.CR) {
				dropDown (false);
				Event selectionEvent = new Event ();
				selectionEvent.time = event.time;
				selectionEvent.stateMask = event.stateMask;
				notifyListeners (SWT.DefaultSelection, selectionEvent);
				if (isDisposed ()) break;
			}
			
			// Further work : Need to add support for incremental search in 
			// pop up list as characters typed in text widget
			break;
		}
		case SWT.KeyUp: {
			Event e = new Event ();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners (SWT.KeyUp, e);
			event.doit = e.doit;
			break;
		}
		case SWT.MenuDetect: {
			Event e = new Event ();
			e.time = event.time;
			notifyListeners (SWT.MenuDetect, e);
			break;
		}
		case SWT.Modify: {
			Event e = new Event ();
			e.time = event.time;
			notifyListeners (SWT.Modify, e);
			break;
		}
		case SWT.MouseDown: {
			if (event.button != 1) return;
			if (text.getEditable ()) return;
			text.selectAll ();
			dropDown (true);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) return;
			if (text.getEditable ()) return;
			text.selectAll ();
			break;
		}
		case SWT.Traverse: {		
			switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_ARROW_PREVIOUS:
				case SWT.TRAVERSE_ARROW_NEXT:
					// The enter causes default selection and
					// the arrow keys are used to manipulate the list contents so
					// do not use them for traversal.
					event.doit = false;
					break;
			}
			
			Event e = new Event ();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.character = event.character;
			e.keyCode = event.keyCode;
			notifyListeners (SWT.Traverse, e);
			event.doit = e.doit;
			event.detail = e.detail;
			break;
		}
		case SWT.Verify: {
			Event e = new Event ();
			e.text = event.text;
			e.start = event.start;
			e.end = event.end;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners (SWT.Verify, e);
			event.doit = e.doit;
			break;
		}
	}
}

public String getKey () {
	checkWidget ();
	return (String)text.getData();
}

public void setKey (Long key) {
	checkWidget();
	
	if (key == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	try{
		ADManager entityManager = Framework.getService(ADManager.class);
		String className = adTable.getModelClass();
		ADBase base = (ADBase)Class.forName(className).newInstance();
		base.setObjectId(key);
		Object obj = entityManager.getEntity(base);
		if (obj == null) {
			text.setText ("");
			text.setData(null);
		}
		text.setData(key);
		String valueStr = (String)PropertyUtil.getPropertyForString(obj, valueColumn);
		text.setText (valueStr);
	} catch (Exception e){
		text.setText("");
		e.printStackTrace();
	}
	internalLayout(false);
}
}
