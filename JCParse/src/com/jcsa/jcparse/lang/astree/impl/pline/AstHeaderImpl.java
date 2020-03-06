package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstHeader;

public class AstHeaderImpl extends AstFixedNode implements AstHeader {

	private boolean is_sys;
	private String path;

	public AstHeaderImpl(boolean is_sys, String path) throws Exception {
		super(0);

		if (path == null || path.isEmpty())
			throw new IllegalArgumentException("Invalid path: null");
		else {
			this.is_sys = is_sys;
			this.path = path;
		}
	}

	@Override
	public boolean is_system() {
		return is_sys;
	}

	@Override
	public boolean is_user_define() {
		return !is_sys;
	}

	@Override
	public String get_path() {
		return path;
	}

}
