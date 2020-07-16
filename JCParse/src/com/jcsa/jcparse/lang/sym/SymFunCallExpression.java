package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymFunCallExpression extends SymExpression {

	protected SymFunCallExpression(CType data_type) {
		super(data_type);
	}

	@Override
	protected SymNode new_self() {
		return new SymFunCallExpression(this.get_data_type());
	}
	
	/**
	 * @return the function to be called
	 */
	public SymExpression get_function() {
		return (SymExpression) this.get_child(0);
	}
	/**
	 * @return the argument list to be called
	 */
	public SymArgumentList get_argument_list() {
		return (SymArgumentList) this.get_child(1);
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		return this.get_function().generate_code(ast_style) + 
				this.get_argument_list().generate_code(ast_style);
	}

}
