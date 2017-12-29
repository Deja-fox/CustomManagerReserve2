package com.jald.reserve.bean.normal;

import java.io.Serializable;

public class KHighwayOpenSiteBean implements Serializable {
	private static final long serialVersionUID = -9017572676650878929L;

	public static final String EMPTY_SITE = "00000000";

	private String siteId;
	private String siteName;

	public KHighwayOpenSiteBean(String siteId, String siteName) {
		this.siteId = siteId;
		this.siteName = siteName;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

}
