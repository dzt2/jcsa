package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.ptoken.PHeaderToken;

public class PHeaderTokenImpl extends PTokenImpl implements PHeaderToken {

	private boolean is_sys;
	private String path;

	protected PHeaderTokenImpl(boolean is_sys, String path) {
		super();
		if (path == null || path.isEmpty())
			throw new IllegalArgumentException("Invalid path: null");
		else {
			this.path = path;
			this.is_sys = is_sys;
		}
	}

	@Override
	public boolean is_system() {
		return is_sys;
	}

	@Override
	public boolean is_usedef() {
		return !is_sys;
	}

	@Override
	public String get_path() {
		return path;
	}

	@Override
	public String toString() {
		return "[header]{ system = " + is_sys + "; " + "path = \"" + path + "\"; }";
	}
}
