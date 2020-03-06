package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CHeaderToken;

public class CHeaderTokenImpl extends CTokenImpl implements CHeaderToken {

	private boolean is_sys;
	private String path;

	protected CHeaderTokenImpl(boolean is_sys, String path) {
		super();
		if (path == null || path.isEmpty())
			throw new IllegalArgumentException("Invalid path: null");
		else {
			this.is_sys = is_sys;
			this.path = path;
		}
	}

	@Override
	public boolean is_system_header() {
		return is_sys;
	}

	@Override
	public boolean is_user_header() {
		return !is_sys;
	}

	@Override
	public String get_path() {
		return path;
	}

	@Override
	public String toString() {
		if (is_sys)
			return "<HD>::<" + path + ">";
		else
			return "<HD>::\"" + path + "\"";
	}
}
