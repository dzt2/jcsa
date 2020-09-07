package com.jcsa.jcmutest.mutant.sel2mutant.lang.value.binary;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.token.SelExpression;
import com.jcsa.jcmutest.mutant.sel2mutant.lang.value.SelTypedValueError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

public abstract class SelBinaryValueError extends SelTypedValueError {

	public SelBinaryValueError(CirStatement statement, SelKeywords 
			keyword, CirExpression orig_expression,
			SymExpression muta_expression) throws Exception {
		super(statement, keyword, orig_expression);
		this.add_child(new SelExpression(muta_expression));
	}
	
	/**
	 * @return the expression to replace the original ones
	 */
	public SelExpression get_muta_expression() {
		return (SelExpression) this.get_child(4);
	}

	@Override
	protected String generate_content() throws Exception {
		return "[" + this.get_value_type().generate_code() + "]("
				+ this.get_orig_expression().generate_code() + ", "
				+ this.get_muta_expression().generate_code() + ")";
	}
	
}
