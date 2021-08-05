package com.jcsa.jcparse.lang.scope.impl;

import java.util.List;

import com.jcsa.jcparse.lang.ctoken.CToken;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CStorageClass;
import com.jcsa.jcparse.lang.scope.CInstance;
import com.jcsa.jcparse.lang.scope.CLabel;
import com.jcsa.jcparse.lang.scope.CMacro;

/**
 * To produce instance for semantic analysis
 *
 * @author yukimula
 */
public class CEntityFactory {
	protected long label_id = 0;
	public CEntityFactory() {}
	public CInstance get_instance_of(CType vtype) throws Exception {
		return new CInstanceImpl(vtype);
	}
	public CInstance get_instance_of(CStorageClass storage, CType vtype) throws Exception {
		return new CInstanceImpl(vtype, storage);
	}
	public CLabel new_label() {
		return new CLabelImpl(label_id++);
	}
	public CMacro get_macro(String name, List<CToken> tokens) throws Exception {
		return new CMacroImpl(name, tokens);
	}
}
