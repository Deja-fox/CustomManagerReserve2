package com.jald.reserve.bean.normal;

public class KBottomTabItem {

	private boolean selected;
	private int tabIcoResId;
	private String tabLabel;

	public KBottomTabItem() {
	}

	public KBottomTabItem(int resId, String label) {
		this.tabIcoResId = resId;
		this.tabLabel = label;
	}

	public int getTabIcoResId() {
		return tabIcoResId;
	}

	public void setTabIcoResId(int tabIcoResId) {
		this.tabIcoResId = tabIcoResId;
	}

	public String getTabLabel() {
		return tabLabel;
	}

	public void setTabLabel(String tabLabel) {
		this.tabLabel = tabLabel;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
