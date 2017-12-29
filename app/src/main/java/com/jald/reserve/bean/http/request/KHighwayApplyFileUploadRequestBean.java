package com.jald.reserve.bean.http.request;

import java.io.Serializable;

public class KHighwayApplyFileUploadRequestBean implements Serializable {
	private static final long serialVersionUID = 8414594275872236130L;

	private String file;
	private String file_type;
	private String file_format;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public String getFile_format() {
		return file_format;
	}

	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}

}
