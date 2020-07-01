package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * fun_call_expr	|--	expression argument_list
 * @author yukimula
 *
 */
public class SymFunCallExpression extends SymExpression {

	protected SymFunCallExpression(CType data_type) {
		super(data_type, null);
	}
	
	/**
	 * @return expression describing the callee function
	 */
	public SymExpression get_callee_expression() { return (SymExpression) this.get_child(0); }
	/**
	 * @return the list of arguments in calling expression
	 */
	public SymArgumentList get_argument_list() { return (SymArgumentList) this.get_child(1); }
	
	@Override
	protected SymNode clone_self() {
		return new SymFunCallExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		return this.get_callee_expression().generate_code(ast_code) + this.get_argument_list().generate_code(ast_code);
	}

}
