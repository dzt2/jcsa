package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymFactory;

public abstract class SecReferenceError extends SecStateError {

	public SecReferenceError(CirStatement statement, SecKeywords 
			keyword, CirExpression orig_reference) throws Exception {
		super(statement, keyword);
		if(!(orig_reference instanceof CirReferExpression))
			throw new IllegalArgumentException(orig_reference.generate_code(true));
		this.add_child(new SecExpression(SymFactory.parse(orig_reference)));
	}
	
	/**
	 * @return the reference of which value will be mutated in this point
	 */
	public SecExpression get_orig_reference() { 
		return (SecExpression) this.get_child(2); 
	}

	@Override
	public CirNode get_cir_location() {
		return this.get_orig_reference().get_expression().get_cir_source();
	}

}
