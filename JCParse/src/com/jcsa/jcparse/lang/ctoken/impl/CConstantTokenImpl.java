package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CConstantToken;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class CConstantTokenImpl extends CTokenImpl implements CConstantToken {

	private CConstant constant;

	protected CConstantTokenImpl() {
		super();
		constant = new CConstant();
	}

	@Override
	public CConstant get_constant() {
		return constant;
	}

	@Override
	public String toString() {
		return "<CT>::{" + constant.toString() + "}";
	}
}
