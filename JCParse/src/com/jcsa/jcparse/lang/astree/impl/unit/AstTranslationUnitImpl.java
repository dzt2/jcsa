package com.jcsa.jcparse.lang.astree.impl.unit;

import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.scope.CScope;

public class AstTranslationUnitImpl extends AstVariableNode implements AstTranslationUnit {

	public AstTranslationUnitImpl() throws Exception {
		super();
	}

	@Override
	public int number_of_units() {
		return children.size();
	}

	@Override
	public AstExternalUnit get_unit(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return (AstExternalUnit) children.get(k);
	}

	@Override
	public void append_unit(AstExternalUnit unit) throws Exception {
		this.append_child(unit);
	}

	protected CScope scope;

	@Override
	public CScope get_scope() {
		return scope;
	}

	@Override
	public void set_scope(CScope scope) {
		this.scope = scope;
	}

}
