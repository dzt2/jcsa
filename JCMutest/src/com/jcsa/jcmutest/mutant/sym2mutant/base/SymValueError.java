package com.jcsa.jcmutest.mutant.sym2mutant.base;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;


public abstract class SymValueError extends SymStateError {
	
	/** the original value hold by the expression before mutated **/
	private SymbolExpression orig_value;
	/** the mutation valud that mutates the original values hold **/
	private SymbolExpression muta_value;
	
	/**
	 * @param type
	 * @param execution
	 * @param orig_expression
	 * @param muta_expression
	 * @throws IllegalArgumentException
	 */
	protected SymValueError(SymInstanceType type, CirExecution execution, 
			CirExpression expression, SymbolExpression orig_expression,
			SymbolExpression muta_expression) throws Exception {
		super(type, execution, expression);
		if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression: null");
		else {
			this.orig_value = orig_expression;
			this.muta_value = muta_expression;
		}
	}
	
	private boolean is_boolean(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null) {
			return false;
		}
		else {
			type = CTypeAnalyzer.get_value_type(type);
			if(CTypeAnalyzer.is_boolean(type)) {
				return true;
			}
			else {
				CirNode parent = expression.get_parent();
				if(parent instanceof CirIfStatement) {
					return ((CirIfStatement) parent).get_condition() == expression;
				}
				else if(parent instanceof CirCaseStatement) {
					return ((CirCaseStatement) parent).get_condition() == expression;
				}
				else {
					return false;
				}
			}
		}
	}
	private boolean is_integer(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_integer(CTypeAnalyzer.get_value_type(type));
	}
	private boolean is_numeric(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_number(CTypeAnalyzer.get_value_type(type));
	}
	
	/**
	 * @return the expression where the state error is injected
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_location(); }
	/**
	 * @return the original value hold by the expression before mutated
	 */
	public SymbolExpression get_original_value() { return this.orig_value; }
	/**
	 * @return the mutation valud that mutates the original values hold
	 */
	public SymbolExpression get_mutation_value() { return this.muta_value; }

	@Override
	protected String generate_code() throws Exception {
		return this.get_type() + ":" + this.get_location().get_node_id() + "(" + 
				this.orig_value.generate_code(true) + ", " + this.muta_value.generate_code(true) + ")";
	}
	@Override
	public Boolean validate(SymbolProcess contexts) throws Exception {
		SymbolExpression orig_value = SymbolEvaluator.evaluate_on(this.orig_value, contexts);
		SymbolExpression muta_value = SymbolEvaluator.evaluate_on(this.muta_value, contexts);
		if(orig_value.generate_code(true).equals(muta_value.generate_code(true))) {
			return Boolean.FALSE;
		}
		else if(orig_value instanceof SymbolConstant) {
			if(muta_value instanceof SymbolConstant) {
				SymbolConstant lconstant = (SymbolConstant) orig_value;
				SymbolConstant rconstant = (SymbolConstant) muta_value;
				if(this.is_boolean(this.get_expression())) {
					return Boolean.valueOf(lconstant.get_bool() != rconstant.get_bool());
				}
				else if(this.is_integer(this.get_expression())) {
					return Boolean.valueOf(lconstant.get_long() != rconstant.get_long());
				}
				else if(this.is_numeric(this.get_expression())) {
					return Boolean.valueOf(lconstant.get_double() != rconstant.get_double());
				}
				else {
					return Boolean.FALSE;
				}
			}
			else {
				return Boolean.TRUE;
			}
		}
		else if(muta_value instanceof SymbolConstant) {
			return Boolean.TRUE;
		}
		else {
			return null;	/* undecidable */
		}
	}
	
}
