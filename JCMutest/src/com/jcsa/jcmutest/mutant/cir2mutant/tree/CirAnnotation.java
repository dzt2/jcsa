package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It denotes an annotation defined in symbolic execution process for killing a
 * mutation during testing.
 * 
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* attributes */
	/** the execution where the annotation is specified in symbolic execution **/
	private CirExecution 		execution;
	/** the logical type (predicate) of the annotation to define its semantic **/
	private CirAnnotationClass	logic_type;
	/** the location in C-intermediate representative code for the annotation **/
	private CirNode				store_unit;
	/** the symbolic value to denote or refine the descriptions of annotation **/
	private SymbolExpression	symb_value;
	
	/* constructor */
	/**
	 * It creates an object of CirAnnotation with its entire list of parameters.
	 * @param execution
	 * @param logic_type
	 * @param store_unit
	 * @param symb_value
	 * @throws Exception
	 */
	private CirAnnotation(CirExecution execution, CirAnnotationClass logic_type,
			CirNode store_unit, SymbolExpression symb_value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else if(logic_type == null) {
			throw new IllegalArgumentException("Invalid logic_type: null");
		}
		else if(store_unit == null) {
			throw new IllegalArgumentException("Invalid store_unit: null");
		}
		else if(symb_value == null) {
			throw new IllegalArgumentException("Invalid symb_value: null");
		}
		else {
			this.execution  = execution;
			this.logic_type = logic_type;
			this.store_unit = store_unit;
			this.symb_value = CirAnnotationValue.safe_evaluate(symb_value, null);
		}
	}
	
	/* getters */
	/**
	 * @return the execution where the annotation is specified in symbolic execution
	 */
	public CirExecution 		get_execution() 	{ return this.execution; }
	/**
	 * @return the logical type (predicate) of the annotation to define its semantic
	 */
	public CirAnnotationClass 	get_logic_type() 	{ return this.logic_type; }
	/**
	 * @return the location in C-intermediate representative code for the annotation
	 */
	public CirNode				get_store_unit() 	{ return this.store_unit; }
	/**
	 * @return the symbolic value to denote or refine the descriptions of annotation
	 */
	public SymbolExpression		get_symb_value() 	{ return this.symb_value; }
	
	/* general */
	@Override
	public String toString() {
		String predicate = this.logic_type.toString();
		String execution = this.execution.toString();
		String store_unit = "" + this.store_unit.get_node_id();
		String symb_value = this.symb_value.toString();
		return predicate + "(" + execution + ", " + store_unit + ", " + symb_value + ")";
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirAnnotation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	/* factory */
	/**
	 * @param execution
	 * @param times
	 * @return cov_stmt(execution, statement, times)
	 * @throws Exception
	 */
	protected static CirAnnotation cov_stmt(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new CirAnnotation(execution, CirAnnotationClass.cov_stmt,
					execution.get_statement(), 
					SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * 
	 * @param execution
	 * @param condition
	 * @return eva_expr(execution, statement, condition as true)
	 * @throws Exception
	 */
	protected static CirAnnotation eva_expr(CirExecution execution, Object condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirAnnotation(execution, CirAnnotationClass.eva_expr,
					execution.get_statement(),
					SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param execution
	 * @return trp_stmt(execution.exit, exit_statement, expt_value)
	 * @throws Exception
	 */
	protected static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			execution = execution.get_graph().get_exit();
			return new CirAnnotation(execution, CirAnnotationClass.trp_stmt,
					execution.get_statement(), CirAnnotationValue.expt_value);
		}
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return mut_flow(source, orig_target, muta_target)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else {
			return new CirAnnotation(orig_flow.get_source(), 
					CirAnnotationClass.mut_flow,
					orig_flow.get_target().get_statement(),
					SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	/**
	 * @param execution
	 * @param exe_or_not
	 * @return mut_stmt(execution, statement, exe_or_not)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_stmt(CirExecution execution, SymbolExpression exe_or_not) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(exe_or_not == null) {
			throw new IllegalArgumentException("Invalid exe_or_not: null");
		}
		else {
			return new CirAnnotation(execution, CirAnnotationClass.mut_stmt,
					execution.get_statement(), exe_or_not);
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return mut_expr(execution, expression, value)
	 * @throws Exception
	 */
	protected static CirAnnotation mut_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(CirMutations.is_assigned(expression)) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else {
			SymbolExpression symb_value;
			if(CirMutations.is_boolean(expression)) {
				symb_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				symb_value = SymbolFactory.sym_expression(value);
			}
			return new CirAnnotation(expression.execution_of(),
					CirAnnotationClass.mut_expr, expression, symb_value);
		}
	}
	/**
	 * @param reference
	 * @param value
	 * @return mut_stat(execution, reference, value)
	 * @throws Exception 
	 */
	protected static CirAnnotation mut_stat(CirExpression reference, Object value) throws Exception {
		if(reference == null || reference.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + reference);
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value as null");
		}
		else if(!CirMutations.is_assigned(reference)) {
			throw new IllegalArgumentException("Invalid: " + reference);
		}
		else {
			SymbolExpression symb_value;
			if(CirMutations.is_boolean(reference)) {
				symb_value = SymbolFactory.sym_condition(value, true);
			}
			else {
				symb_value = SymbolFactory.sym_expression(value);
			}
			return new CirAnnotation(reference.execution_of(),
					CirAnnotationClass.mut_stat, reference, symb_value);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return dif_asub(execution, expression, difference)
	 * @throws Exception
	 */
	protected static CirAnnotation dif_asub(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference as null");
		}
		else if(CirMutations.is_numeric(expression)) {
			return new CirAnnotation(expression.execution_of(),
					CirAnnotationClass.dif_asub, expression, difference);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return dif_exor(execution, expression, difference)
	 * @throws Exception
	 */
	protected static CirAnnotation dif_exor(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference as null");
		}
		else if(CirMutations.is_integer(expression)) {
			return new CirAnnotation(expression.execution_of(),
					CirAnnotationClass.dif_exor, expression, difference);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return dif_rsub(execution, expression, difference)
	 * @throws Exception
	 */
	protected static CirAnnotation dif_rsub(CirExpression expression, SymbolExpression difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference as null");
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			return new CirAnnotation(expression.execution_of(),
					CirAnnotationClass.dif_rsub, expression, difference);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	
}
