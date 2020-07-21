package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.code.CodeGeneration;
import com.jcsa.jcparse.lang.ctype.CType;

public abstract class CTypeImpl implements CType {

	@Override
	public String generate_code() {
		try {
			return CodeGeneration.generate_code(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
