package com.jald.reserve.bean.normal;

public class KETongBaoMenuItem {

	private int iconResId;
	private String title;

	public KETongBaoMenuItem() {
	}

	public KETongBaoMenuItem(int icoResId, String title) {
		this.iconResId = icoResId;
		this.title = title;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
