package org.eclipse.swt.widgets;

import org.eclipse.swt.events.SelectionListener;

import com.graly.framework.base.ui.util.Env;

public class AuthorityMenuItem{
	protected String authorityKey;
	protected MenuItem mItem;
	
	public AuthorityMenuItem(Menu parent, int style, String authorityKey) {
		this.authorityKey = authorityKey;
		if (authorityKey != null && !"".equals(authorityKey.trim())) {
			if (Env.getAuthority() != null) {
				if (Env.getAuthority().contains(authorityKey)) {
					mItem = new MenuItem(parent, style);
				}
			}
		}
	}
	
	public void addSelectionListener(SelectionListener selectionListener) {
		if(mItem != null){
			mItem.addSelectionListener(selectionListener);
		}
	}
	
	public void setText (String string) {
		if(mItem != null){
			mItem.setText(string);
		}
	}
	
	public String getAuthorityKey() {
		return authorityKey;
	}
	
	public void setAuthorityKey(String authorityKey) {
		this.authorityKey = authorityKey;
	}
	
	public boolean isCreated(){
		return mItem != null;
	}

	public MenuItem getmItem() {
		return mItem;
	}

	public void setmItem(MenuItem mItem) {
		this.mItem = mItem;
	}

}
