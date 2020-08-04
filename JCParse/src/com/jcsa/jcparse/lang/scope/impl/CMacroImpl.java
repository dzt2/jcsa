package com.jcsa.jcparse.lang.scope.impl;

import java.util.List;

import com.jcsa.jcparse.lang.ctoken.CToken;
import com.jcsa.jcparse.lang.scope.CMacro;

public class CMacroImpl implements CMacro {

	protected String name;
	protected CToken[] tokens;

	protected CMacroImpl(String name, List<CToken> tokens) throws Exception {
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else if (tokens == null)
			throw new IllegalArgumentException("Invalid tokens: null");
		else {
			this.name = name;
			this.tokens = new CToken[tokens.size()];
			for (int i = 0; i < this.tokens.length; i++)
				this.tokens[i] = tokens.get(i);
		}
	}

	@Override
	public String get_macro_name() {
		return name;
	}

	@Override
	public CToken[] get_token_list() {
		return tokens;
	}

}
