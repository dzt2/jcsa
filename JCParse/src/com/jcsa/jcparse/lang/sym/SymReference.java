package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SymReference extends SymExpression {
	
	protected SymReference(AstExpression source) {
		super(source.get_value_type(), source);
	}
	
	protected SymReference(CirExpression source) {
		super(source.get_data_type(), source);
	}
	
	/**
	 * @return whether the source is AstExpression
	 */
	public boolean is_ast_source() { return this.get_token() instanceof AstExpression; }
	/** 
	 * @return whether the source is CirExpression
	 */
	public boolean is_cir_source() { return this.get_token() instanceof CirExpression; }
	/**
	 * @return source as ast-expression or null
	 */
	public AstExpression get_ast_source() {
		Object token = this.get_token();
		if(token instanceof AstExpression) {
			return (AstExpression) token;
		}
		else {
			return null;
		}
	}
	/**
	 * @return source as cir-expression or null
	 */
	public CirExpression get_cir_source() {
		Object token = this.get_token();
		if(token instanceof CirExpression) {
			return (CirExpression) token;
		}
		else {
			return null;
		}
	}

	
	@Override
	protected SymNode clone_self() {
		if(this.is_ast_source()) {
			return new SymReference(this.get_ast_source());
		}
		else {
			return new SymReference(this.get_cir_source());
		}
	}
	

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		if(ast_code) {
			if(this.is_ast_source()) {
				return this.get_ast_source().get_code();
			}
			else {
				CirExpression source = this.get_cir_source();
				if(source.get_ast_source() != null) {
					return source.get_ast_source().get_code();
				}
				else {
					return source.generate_code();
				}
			}
		}
		else {
			if(this.is_ast_source()) {
				return this.get_ast_source().get_code();
			}
			else {
				return this.get_cir_source().generate_code();
			}
		}
	}
	
}
