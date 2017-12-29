package com.jald.reserve.bean.normal;

import java.io.Serializable;

public class KCarTypeBean implements Serializable {
	private static final long serialVersionUID = -9017572676650878929L;

	public static final String EMPTY_TYPE = "00000000";

	private String typeId;
	private String typeName;

	public KCarTypeBean(String typeId, String typeName) {
		this.typeId = typeId;
		this.typeName = typeName;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
