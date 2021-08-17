package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It denotes an abstract annotation to describe the abstract features of killing process.
 *
 * @author yukimula
 *
 */
public class CirAnnotation {
	
	/* attributes */
	/** abstract category of the annotation **/
	private CirAnnotationClass		category;
	/** refined class of the annotation **/
	private CirAnnotationType		operator;
	/** the CFG-node where the annotation is evaluated **/
	private CirExecution			execution;
	/** the C-intermediate location where annotation is defined **/
	private CirNode					location;
	/** symbolic expression as the parameter to refine the node **/
	private SymbolExpression		parameter;

	/* constructor */
	/**
	 * @param category
	 * @param operator
	 * @param execution
	 * @param location
	 * @param parameter
	 * @throws IllegalArgumentException
	 */
	private CirAnnotation(CirAnnotationClass category, CirAnnotationType operator, CirExecution
			execution, CirNode location, SymbolExpression parameter) throws IllegalArgumentException {
		if(category == null) {
			throw new IllegalArgumentException("Invalid category: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {
			this.category = category;
			this.operator = operator;
			this.execution = execution;
			this.location = location;
			this.parameter = parameter;
		}
	}

	/* getters */
	/**
	 * @return abstract category of the annotation
	 */
	public CirAnnotationClass	get_category() 	{ return this.category; }
	/**
	 * @return the refined class of the annotation
	 */
	public CirAnnotationType	get_operator() 	{ return this.operator; }
	/**
	 * @return the CFG-node where the annotation is evaluated
	 */
	public CirExecution			get_execution() { return this.execution; }
	/**
	 * @return the C-intermediate location where annotation is defined
	 */
	public CirNode				get_location() 	{ return this.location; }
	/**
	 * @return whether the parameter is not null
	 */
	public boolean				has_parameter() { return this.parameter != null; }
	/**
	 * @return symbolic expression as the parameter to refine the node
	 */
	public SymbolExpression		get_parameter() { return this.parameter; }

	/* universals */
	@Override
	public String toString() {
		return this.category + "$" + this.operator + "$" +
				this.execution + "$" + this.location + "$" + this.parameter;
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
	
	/* constraint class */
	/**
	 * @param execution
	 * @param times
	 * @return constraint:cov_stmt:execution:statement:integer
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
			return new CirAnnotation(CirAnnotationClass.constraint, 
					CirAnnotationType.cov_stmt, 
					execution, execution.get_statement(), 
					SymbolFactory.sym_expression(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @param value
	 * @return constraint:eva_expr:execution:statement:{condition as value}
	 * @throws Exception
	 */
	protected static CirAnnotation eva_expr(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.constraint,
					CirAnnotationType.eva_expr,
					execution, execution.get_statement(),
					SymbolFactory.sym_condition(condition, value));
		}
	}
	
	/* stmt_error class */
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return stmt_error:mut_flow:if_source:orig_target:muta_target
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
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_flow,
					orig_flow.get_source(),
					orig_flow.get_target().get_statement(),
					SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	/**
	 * @param execution
	 * @return stmt_error:mut_stmt:execution:statement:true
	 * @throws Exception
	 */
	protected static CirAnnotation add_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_stmt, 
					execution, execution.get_statement(),
					SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * @param execution
	 * @return stmt_error:mut_stmt:execution:statement:false
	 * @throws Exception
	 */
	protected static CirAnnotation del_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_stmt, 
					execution, execution.get_statement(),
					SymbolFactory.sym_constant(Boolean.FALSE));
		}
	}
	/**
	 * @param execution
	 * @return stmt_error:trp_stmt:execution:statement:null
	 * @throws Exception
	 */
	protected static CirAnnotation trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.trp_stmt,
					execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param expression
	 * @param mut_value
	 * @return stmt_error:mut_stat:execution:expression:mut_value
	 * @throws Exception
	 */
	protected static CirAnnotation mut_stat(CirExpression expression, SymbolExpression mut_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(mut_value == null) {
			throw new IllegalArgumentException("Invalid mut_value as null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.stmt_error,
					CirAnnotationType.mut_stat, 
					expression.execution_of(), expression, mut_value);
		}
	}
	
	/* expr_error class */
	/**
	 * @param expression
	 * @param mut_value
	 * @return expr_error:set_expr:execution:expression:mut_value
	 * @throws Exception
	 */
	protected static CirAnnotation set_expr(CirExpression expression, SymbolExpression mut_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(mut_value == null) {
			throw new IllegalArgumentException("Invalid mut_value as null");
		}
		else {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.set_expr, 
					expression.execution_of(), expression, mut_value);
		}
	}
	/**
	 * @param expression either numeric (int|real) or pointer
	 * @param sub_value
	 * @return expr_error:dif_expr:execution:expression:sub_value
	 * @throws Exception
	 */
	protected static CirAnnotation dif_expr(CirExpression expression, SymbolExpression sub_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(sub_value == null) {
			throw new IllegalArgumentException("Invalid sub_value as null");
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.dif_expr,
					expression.execution_of(), expression, sub_value);
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression should be numieric (int|real)
	 * @param ext_value
	 * @return expr_error:ext_expr:execution:expression:ext_value
	 * @throws Exception
	 */
	protected static CirAnnotation ext_expr(CirExpression expression, SymbolExpression ext_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(ext_value == null) {
			throw new IllegalArgumentException("Invalid ext_value as null");
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.ext_expr,
					expression.execution_of(), expression, ext_value);
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression must be integer
	 * @param xor_value
	 * @return expr_error:xor_expr:execution:expression:xor_value
	 * @throws Exception
	 */
	protected static CirAnnotation xor_expr(CirExpression expression, SymbolExpression xor_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(xor_value == null) {
			throw new IllegalArgumentException("Invalid xor_value as null");
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.expr_error,
					CirAnnotationType.xor_expr,
					expression.execution_of(), expression, xor_value);
		}
		else {
			throw new IllegalArgumentException("Invalid type: " + expression.get_data_type());
		}
	}
	
	/* difference build */
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value - orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression sub_value(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		return SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return fabs(muta_value) - fabs(orig_value)
	 * @throws Exception
	 */
	protected static SymbolExpression ext_value(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		CTypeFactory type_factory = new CTypeFactory();
		CType type = type_factory.get_variable_function_type(CBasicTypeImpl.double_type);
		SymbolExpression function = SymbolFactory.identifier(type, "fabs");
		List<Object> arguments = new ArrayList<Object>();
		
		arguments.clear(); 
		arguments.add(muta_value);
		muta_value = SymbolFactory.call_expression(function, arguments);
		
		arguments.clear();
		arguments.add(orig_value);
		orig_value = SymbolFactory.call_expression(function, arguments);
		
		return SymbolFactory.arith_sub(CBasicTypeImpl.double_type, muta_value, orig_value);
	}
	/**
	 * @param orig_value
	 * @param muta_value
	 * @return muta_value ^ orig_value
	 * @throws Exception
	 */
	protected static SymbolExpression xor_value(SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		return SymbolFactory.bitws_xor(orig_value.get_data_type(), muta_value, orig_value);
	}
	
	/* conc_error class */
	/**
	 * @param expression should be boolean
	 * @param value
	 * @return conc_error:set_bool:execution:expression:boolean
	 * @throws Exception
	 */
	protected static CirAnnotation set_bool(CirExpression expression, boolean value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_bool, expression.execution_of(),
					expression, SymbolFactory.sym_constant(value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return conc_error:set_numb:execution:expression:integer
	 * @throws Exception
	 */
	protected static CirAnnotation set_numb(CirExpression expression, long value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_numb, expression.execution_of(),
					expression, SymbolFactory.sym_constant(value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return conc_error:set_addr:execution:expression:integer
	 * @throws Exception
	 */
	protected static CirAnnotation set_addr(CirExpression expression, long value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_addr, expression.execution_of(),
					expression, SymbolFactory.sym_constant(value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param value
	 * @return conc_error:set_real:execution:expression:double
	 * @throws Exception
	 */
	protected static CirAnnotation set_real(CirExpression expression, double value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_real, expression.execution_of(),
					expression, SymbolFactory.sym_constant(value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param constant
	 * @return conc_error:set_auto:execution:expression:constant
	 * @throws Exception
	 */
	protected static CirAnnotation set_auto(CirExpression expression, CConstant constant) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_automic(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_auto, expression.execution_of(),
					expression, SymbolFactory.sym_constant(constant));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return conc_error:set_xxxx:execution:expression:null
	 * @throws Exception
	 */
	protected static CirAnnotation chg_expr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_bool,
					expression.execution_of(), expression, null);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_numb,
					expression.execution_of(), expression, null);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_real,
					expression.execution_of(), expression, null);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_addr,
					expression.execution_of(), expression, null);
		}
		else if(CirMutation.is_automic(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.set_auto,
					expression.execution_of(), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return conc_error:dif_numb:execution:expression:integer
	 * @throws Exception
	 */
	protected static CirAnnotation dif_numb(CirExpression expression, long difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.dif_numb, expression.execution_of(),
					expression, SymbolFactory.sym_constant(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return conc_error:dif_real:execution:expression:double
	 * @throws Exception
	 */
	protected static CirAnnotation dif_real(CirExpression expression, double difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.dif_real, expression.execution_of(),
					expression, SymbolFactory.sym_constant(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return conc_error:dif_addr:execution:expression:integer
	 * @throws Exception
	 */
	protected static CirAnnotation dif_addr(CirExpression expression, long difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.dif_addr, expression.execution_of(),
					expression, SymbolFactory.sym_constant(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return conc_error:ext_numb:execution:expression:integer
	 * @throws Exception
	 */
	protected static CirAnnotation ext_numb(CirExpression expression, long difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.ext_numb, expression.execution_of(),
					expression, SymbolFactory.sym_constant(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return conc_error:ext_real:execution:expression:double
	 * @throws Exception
	 */
	protected static CirAnnotation ext_real(CirExpression expression, double difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.ext_real, expression.execution_of(),
					expression, SymbolFactory.sym_constant(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return conc_error:xor_numb:execution:expression:integer
	 * @throws Exception
	 */
	protected static CirAnnotation xor_numb(CirExpression expression, long difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.conc_error,
					CirAnnotationType.xor_numb, expression.execution_of(),
					expression, SymbolFactory.sym_constant(difference));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	
	/* scop_error class */
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:TRUE_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_true_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_true_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:FALS_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_fals_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_fals_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:BOOL_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_bool_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_boolean(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_bool_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:POST_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_post_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_post_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:ZERO_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_zero_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_zero_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NEGT_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_negt_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_negt_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NPOS_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_npos_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_npos_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NZRO_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_nzro_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_nzro_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NNEG_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_nneg_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_nneg_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NUMB_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_numb_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_numb_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NULL_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_null_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_null_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NULL_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_invp_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_invp_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:set_scop:execution:expression:NULL_Scope
	 * @throws Exception
	 */
	protected static CirAnnotation set_addr_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.set_scop, expression.execution_of(), 
					expression, CirAnnotationScope.get_addr_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:dif_scop:execution:expression:POST_Domain
	 * @throws Exception
	 */
	protected static CirAnnotation inc_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.dif_scop, expression.execution_of(),
					expression, CirAnnotationScope.get_post_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:dif_scop:execution:expression:POST_Domain
	 * @throws Exception
	 */
	protected static CirAnnotation dec_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.dif_scop, expression.execution_of(),
					expression, CirAnnotationScope.get_negt_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:ext_scop:execution:expression:POST_Domain
	 * @throws Exception
	 */
	protected static CirAnnotation ext_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.ext_scop, expression.execution_of(),
					expression, CirAnnotationScope.get_post_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:ext_scop:execution:expression:NEGT_Domain
	 * @throws Exception
	 */
	protected static CirAnnotation shk_scope(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_numeric(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.ext_scop, expression.execution_of(),
					expression, CirAnnotationScope.get_negt_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:xor_scop:execution:expression:POST_Domain
	 * @throws Exception
	 */
	protected static CirAnnotation xor_post(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.xor_scop, expression.execution_of(),
					expression, CirAnnotationScope.get_post_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return scop_error:xor_scop:execution:expression:NEGT_Domain
	 * @throws Exception
	 */
	protected static CirAnnotation xor_negt(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutation.is_integer(expression)) {
			return new CirAnnotation(CirAnnotationClass.scop_error,
					CirAnnotationType.xor_scop, expression.execution_of(),
					expression, CirAnnotationScope.get_negt_scope());
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type());
		}
	}
	
}
