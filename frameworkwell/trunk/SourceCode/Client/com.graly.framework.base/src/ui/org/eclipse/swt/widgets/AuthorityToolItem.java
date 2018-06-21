package org.eclipse.swt.widgets;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import com.graly.framework.base.ui.util.Env;

public class AuthorityToolItem extends ToolItem {
	
	private String authorityKey;
	
	public AuthorityToolItem(ToolBar parent, int style, String authorityKey) {
		super(parent, style);
		this.authorityKey = authorityKey;
		this.setEnabled(true);
	}
	
	@Override
	public void setEnabled (boolean enabled) {
		if (authorityKey != null && !"".equals(authorityKey.trim())) {
			if (Env.getAuthority() != null) {
				if (Env.getAuthority().contains(authorityKey)) {
					super.setEnabled(enabled);
				} else {
					super.setEnabled(false);
				}
			} else {
				super.setEnabled(false);
			}
		} else {
			super.setEnabled(enabled);
		}
	}
}
