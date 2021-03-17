package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirAddressOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArgumentListPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithAddPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithDivPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithModPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithMulPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithNegPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithSubPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirAssignPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsLshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsRshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsRsvPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsXorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirDereferencePropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirEqualWithPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirErrorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirFieldOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirGreaterEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirGreaterTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirInitializerPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicNotPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirNotEqualsPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirSmallerEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirSmallerTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirTypeCastPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirWaitValuePropagator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;



/**
 * It implements the interface for optimizing or proceeding symbolic instance evaluated during testing.
 * 
 * @author yukimula
 *
 */
public class SymInstanceUtils {
	
	/* singleton mode */
	/** mapping from expression operator to the propagator for generating state error **/
	private Map<COperator, CirErrorPropagator> propagators;
	/** private constructor for producing singleton of utilities **/
	private SymInstanceUtils() { 
		propagators = new HashMap<COperator, CirErrorPropagator>();
		
		propagators.put(COperator.arith_add, new CirArithAddPropagator());
		propagators.put(COperator.arith_sub, new CirArithSubPropagator());
		propagators.put(COperator.arith_mul, new CirArithMulPropagator());
		propagators.put(COperator.arith_div, new CirArithDivPropagator());
		propagators.put(COperator.arith_mod, new CirArithModPropagator());
		propagators.put(COperator.negative, new CirArithNegPropagator());
		
		propagators.put(COperator.bit_not, new CirBitwsRsvPropagator());
		propagators.put(COperator.bit_and, new CirBitwsAndPropagator());
		propagators.put(COperator.bit_or, new CirBitwsIorPropagator());
		propagators.put(COperator.bit_xor, new CirBitwsXorPropagator());
		propagators.put(COperator.left_shift, new CirBitwsLshPropagator());
		propagators.put(COperator.righ_shift, new CirBitwsRshPropagator());
		
		propagators.put(COperator.assign, new CirAssignPropagator());
		propagators.put(COperator.address_of, new CirAddressOfPropagator());
		propagators.put(COperator.dereference, new CirDereferencePropagator());
		
		propagators.put(COperator.greater_eq, new CirGreaterEqPropagator());
		propagators.put(COperator.greater_tn, new CirGreaterTnPropagator());
		propagators.put(COperator.smaller_eq, new CirSmallerEqPropagator());
		propagators.put(COperator.smaller_tn, new CirSmallerTnPropagator());
		propagators.put(COperator.equal_with, new CirEqualWithPropagator());
		propagators.put(COperator.not_equals, new CirNotEqualsPropagator());
		
		propagators.put(COperator.logic_and, new CirLogicAndPropagator());
		propagators.put(COperator.logic_or, new CirLogicIorPropagator());
		propagators.put(COperator.logic_not, new CirLogicNotPropagator());
		
		propagators.put(COperator.arith_add_assign, new CirFieldOfPropagator());
		propagators.put(COperator.arith_sub_assign, new CirTypeCastPropagator());
		propagators.put(COperator.arith_mul_assign, new CirInitializerPropagator());
		propagators.put(COperator.arith_div_assign, new CirArgumentListPropagator());
		propagators.put(COperator.arith_mod_assign, new CirWaitValuePropagator());
	}
	/** the singleton of symbolic instance utility for algorithms **/
	private static final SymInstanceUtils utils = new SymInstanceUtils();
	
	/* annotation methods as supporting */
	/**
	 * @param expression
	 * @return true if the expression is logical or used as condition of IF-statement
	 * @throws Exception
	 */
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
	/**
	 * @param expression
	 * @return true if the expression is integer or double, and used as operand in arithmetic, bitwise expressions
	 * @throws Exception
	 */
	private boolean is_numeric(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_number(CTypeAnalyzer.get_value_type(type));
	}
	/**
	 * @param expression
	 * @return true if the expression is a pointer and used in arithmetic or parameter expressions
	 * @throws Exception
	 */
	private boolean is_address(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_pointer(CTypeAnalyzer.get_value_type(type));
	}
	/**
	 * generate the annotation for an expression in boolean context
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_boolean_expression(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.chg_bool, expression, null));
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool())
				annotations.add(new CirAnnotation(CirAnnotateType.set_true, expression, null));
			else
				annotations.add(new CirAnnotation(CirAnnotateType.set_false, expression, null));
		}
		annotations.add(new CirAnnotation(CirAnnotateType.set_bool, expression, muta_value));
	}
	/**
	 * generate the annotation for an expression in numeric context
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_numeric_expression(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.chg_numb, expression, null));
		annotations.add(new CirAnnotation(CirAnnotateType.set_numb, expression, muta_value));
		
		/* value domain property */
		if(muta_value instanceof SymbolConstant) {
			Object number = ((SymbolConstant) muta_value).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_post, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_negt, expression, null));
				}
				else {
					annotations.add(new CirAnnotation(CirAnnotateType.set_zero, expression, null));
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_post, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_negt, expression, null));
				}
				else {
					annotations.add(new CirAnnotation(CirAnnotateType.set_zero, expression, null));
				}
			}
		}
		
		/* difference property */
		SymbolExpression difference = SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = SymbolEvaluator.evaluate_on(difference, null);
		if(difference instanceof SymbolConstant) {
			Object number = ((SymbolConstant) difference).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.inc_value, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.dec_value, expression, null));
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.inc_value, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.dec_value, expression, null));
				}
			}
		}
		
		/* value range property */
		orig_value = SymbolEvaluator.evaluate_on(orig_value, null);
		muta_value = SymbolEvaluator.evaluate_on(muta_value, null);
		if(orig_value instanceof SymbolConstant) {
			Object lnumber = ((SymbolConstant) orig_value).get_number();
			if(muta_value instanceof SymbolConstant) {
				Object rnumber = ((SymbolConstant) muta_value).get_number();
				if(lnumber instanceof Long) {
					long x = ((Long) lnumber).longValue();
					if(rnumber instanceof Long) {
						long y = ((Long) rnumber).longValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
					else {
						double y = ((Double) rnumber).doubleValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
				}
				else {
					double x = ((Double) lnumber).doubleValue();
					if(rnumber instanceof Long) {
						long y = ((Long) rnumber).longValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
					else {
						double y = ((Double) rnumber).doubleValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
				}
			}
		}
	}
	/**
	 * generate the annotation for an expression in address context
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_address_expression(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.chg_addr, expression, null));
		annotations.add(new CirAnnotation(CirAnnotateType.set_addr, expression, muta_value));
		
		/* value domain property */
		if(muta_value instanceof SymbolConstant) {
			long value = ((SymbolConstant) muta_value).get_long();
			if(value == 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.set_null, expression, null));
			}
			else {
				annotations.add(new CirAnnotation(CirAnnotateType.set_invp, expression, null));
			}
		}
		
		/* difference property */
		SymbolExpression difference = SymbolFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = SymbolEvaluator.evaluate_on(difference, null);
		if(difference instanceof SymbolConstant) {
			Object number = ((SymbolConstant) difference).get_number();
			long value = ((Long) number).longValue();
			if(value > 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.inc_value, expression, null));
			}
			else if(value < 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.dec_value, expression, null));
			}
		}
	}
	/**
	 * generate annotations for expression value error
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_expression(CirExpression expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		if(orig_value.equals(muta_value)) {
			annotations.clear();
			return;
		}
		else if(this.is_boolean(expression)) {
			this.generate_annotations_in_boolean_expression(expression, orig_value, muta_value, annotations);
		}
		else if(this.is_numeric(expression)) {
			this.generate_annotations_in_numeric_expression(expression, orig_value, muta_value, annotations);
		}
		else if(this.is_address(expression)) {
			this.generate_annotations_in_address_expression(expression, orig_value, muta_value, annotations);
		}
		else {
			annotations.add(new CirAnnotation(CirAnnotateType.chg_auto, expression, null));
			annotations.add(new CirAnnotation(CirAnnotateType.set_auto, expression, muta_value));
		}
	}
	
	/* symbolic annotation generator */
	/**
	 * @param constraint
	 * @return	Translation rules are:
	 * 			|-- stmt_id >= times 	--> covr_stmt(statement, times)
	 * 			|--	(execution, TRUE)	-->	covr_stmt(statement, 1)
	 * 			|--	(execution, other)	--> eval_stmt(statement, expression)
	 * @throws Exception
	 */
	private void annotate_constraint(SymConstraint constraint, 
			Collection<CirAnnotation> annotations) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			CirExecution execution = constraint.get_execution();
			SymbolExpression condition = constraint.get_condition();
			
			/* 	*********************************************************
			 * 	|-- stmt_id >= times 	--> covr_stmt(statement, times)	|
			 * 	|							in {1, 2, 4, ... ==> times}	|
			 * 	|--	(execution, TRUE)	-->	covr_stmt(statement, 1) 	|
			 * 	|--	(execution, FASLE)	--> eval_stmt(statement, false)	|
			 * 	|--	(execution, other)	--> eval_stmt(statement, other)	|
			 * 	*********************************************************/
			if(condition instanceof SymbolConstant) {
				if(((SymbolConstant) condition).get_bool()) {
					annotations.add(new CirAnnotation(CirAnnotateType.covr_stmt, 
							execution.get_statement(), 
							SymbolFactory.sym_expression(Integer.valueOf(1))));
				}
				else {
					annotations.add(new CirAnnotation(CirAnnotateType.eval_stmt,
							execution.get_statement(),
							SymbolFactory.sym_expression(Boolean.FALSE)));
				}
			}
			else {
				Object[] exec_time_oprt = this.decode_statement_expression(condition);
				if(exec_time_oprt != null) {
					execution = (CirExecution) exec_time_oprt[0];
					int times = ((Integer) exec_time_oprt[1]).intValue();
					for(int k = 1; k <= times; k = k * 2) {
						annotations.add(new CirAnnotation(CirAnnotateType.covr_stmt,
								execution.get_statement(),
								SymbolFactory.sym_expression(Integer.valueOf(k))));
					}
					annotations.add(new CirAnnotation(CirAnnotateType.covr_stmt, 
							execution.get_statement(), 
							SymbolFactory.sym_expression(Integer.valueOf(times))));
				}
				else {
					annotations.add(new CirAnnotation(CirAnnotateType.eval_stmt,
							execution.get_statement(), condition));
				}
			}
		}
	}
	/**
	 * 	trap_error	|--	trap_stmt(statement, null)
	 * 	@param state_error
	 * 	@param annotations
	 * 	@throws Exception
	 */
	private void annotate_trap_error(SymTrapError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.trap_stmt, state_error.get_statement(), null));
	}
	/**
	 * flow_error |-- add_stmt(statement, null) + del_stmt(statement, null)
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_flow_error(SymFlowError state_error, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. get statements that should be executed in original program */
		Set<CirStatement> del_statements = new HashSet<CirStatement>();
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(state_error.get_original_flow().get_target());
		for(CirExecutionEdge edge : orig_path.get_edges()) { del_statements.add(edge.get_source().get_statement()); }
		del_statements.add(orig_path.get_target().get_statement());
		
		/* 2. get statements that should be executed in mutated program */
		Set<CirStatement> add_statements = new HashSet<CirStatement>();
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(state_error.get_mutation_flow().get_target());
		for(CirExecutionEdge edge : muta_path.get_edges()) { add_statements.add(edge.get_source().get_statement()); }
		add_statements.add(muta_path.get_target().get_statement());
		
		/* 3. remove the common part between original and mutation path */
		Set<CirStatement> common_statements = new HashSet<CirStatement>();
		for(CirStatement statement : del_statements) {
			if(add_statements.contains(statement)) {
				common_statements.add(statement);
			}
		}
		add_statements.removeAll(common_statements);
		del_statements.removeAll(common_statements);
		
		/* 4. append annotations for flow error */
		for(CirStatement statement : add_statements) 
			annotations.add(new CirAnnotation(CirAnnotateType.add_stmt, statement, null));
		for(CirStatement statement : del_statements) 
			annotations.add(new CirAnnotation(CirAnnotateType.del_stmt, statement, null));
	}
	/**
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_expr_error(SymExpressionError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.mut_value, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), annotations);
	}
	/**
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_refr_error(SymReferenceError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.mut_refer, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), annotations);
	}
	/**
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_stat_error(SymStateValueError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.mut_state, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), annotations);
	}
	/**
	 * @param instance
	 * @return the set of annotations to describe the symbolic instance
	 * @throws Exception
	 */
	public static Collection<CirAnnotation> annotations(SymInstance instance) throws Exception {
		Set<CirAnnotation> annotations = new HashSet<CirAnnotation>();
		if(instance instanceof SymConstraint)
			utils.annotate_constraint((SymConstraint) instance, annotations);
		else if(instance instanceof SymTrapError)
			utils.annotate_trap_error((SymTrapError) instance, annotations);
		else if(instance instanceof SymFlowError)
			utils.annotate_flow_error((SymFlowError) instance, annotations);
		else if(instance instanceof SymExpressionError)
			utils.annotate_expr_error((SymExpressionError) instance, annotations);
		else if(instance instanceof SymReferenceError)
			utils.annotate_refr_error((SymReferenceError) instance, annotations);
		else if(instance instanceof SymStateValueError)
			utils.annotate_stat_error((SymStateValueError) instance, annotations);
		else
			throw new IllegalArgumentException("Invalid instance as: " + instance);
		return annotations;
	}
	
	/* state error propagations */
	/**
	 * generate the error-constraint pair in local propagation from source error and append
	 * them in the propagations table.
	 * @param cir_mutations
	 * @param source_error
	 * @param propagations
	 * @throws Exception
	 */
	private void propagate_on(CirMutations cir_mutations, SymStateError source_error, 
				Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* get the next location for error of propagation */
		CirExpression location;
		if(source_error instanceof SymExpressionError) {
			location = ((SymExpressionError) source_error).get_expression();
		}
		else if(source_error instanceof SymReferenceError) {
			location = ((SymReferenceError) source_error).get_expression();
		}
		else {
			location = null;
		}
		
		/* syntax-directed error propagation algorithms */
		if(location != null) {
			CirNode parent = location.get_parent();
			
			if(parent instanceof CirDeferExpression) {
				this.propagators.get(COperator.dereference).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirFieldExpression) {
				this.propagators.get(COperator.arith_add_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirAddressExpression) {
				this.propagators.get(COperator.address_of).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirCastExpression) {
				this.propagators.get(COperator.arith_sub_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirInitializerBody) {
				this.propagators.get(COperator.arith_mul_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirWaitExpression) {
				this.propagators.get(COperator.arith_mod_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirComputeExpression) {
				this.propagators.get(((CirComputeExpression) parent).get_operator()).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirArgumentList) {
				this.propagators.get(COperator.arith_div_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirIfStatement
					|| parent instanceof CirCaseStatement) {
				CirStatement statement = (CirStatement) parent;
				CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
				CirExecutionFlow true_flow = execution.get_ou_flow(0);
				CirExecutionFlow fals_flow = execution.get_ou_flow(1);
				
				CirExpression condition;
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				
				propagations.put(cir_mutations.flow_error(true_flow, fals_flow), 
						cir_mutations.expression_constraint(statement, condition, true));
				propagations.put(cir_mutations.flow_error(fals_flow, true_flow), 
						cir_mutations.expression_constraint(statement, condition, false));
			}
			else if(parent instanceof CirAssignStatement) {
				propagators.get(COperator.assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
		}
	}
	/**
	 * @param cir_mutations
	 * @param source_error
	 * @return the set of CirMutation generated from source error as its next propagation gender
	 * @throws Exception
	 */
	public static Collection<CirMutation> propagate(CirMutations cir_mutations, SymStateError source_error) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(source_error == null)
			throw new IllegalArgumentException("Invalid source_error: null");
		else {
			List<CirMutation> next_mutations = new ArrayList<CirMutation>();
			Map<SymStateError, SymConstraint> propagations = new HashMap<SymStateError, SymConstraint>();
			utils.propagate_on(cir_mutations, source_error, propagations);
			for(SymStateError next_error : propagations.keySet()) {
				SymConstraint constraint = propagations.get(next_error);
				next_mutations.add(cir_mutations.new_mutation(constraint, next_error));
			}
			return next_mutations;
		}
	}
	
	/* symbolic constraint improvement */
	/* Stage-I. Local Improvement on Constraint itself */
	/**
	 * @param expression
	 * @return whether the expression is logical AND
	 */
	private boolean is_conjunction(SymbolExpression expression) {
		if(expression instanceof SymbolBinaryExpression) {
			return ((SymbolBinaryExpression) expression).get_operator().get_operator() == COperator.logic_and;
		}
		else {
			return false;
		}
	}
	/**
	 * 	It collect useful expression that are conjected to construct the source expression as given.
	 * 	(1)	If the expression is True constant, it is ignored to be added to conjunctions and return true.
	 * 	(2) If the expression is False constant, it will clear the conjunctions and return false.
	 * 	(3) If the expression is Logical-And Binary, its children will be collected recursively.
	 * @param expression
	 * @param conjunctions
	 * @throws Exception
	 */
	private boolean collect_in_conjunctions(SymbolExpression expression, Collection<SymbolExpression> conjunctions) throws Exception {
		if(expression instanceof SymbolConstant) {
			/* (1) True constant is ignored and does not influence on others */
			if(((SymbolConstant) expression).get_bool()) {
				return true;		
			}
			/* (2) False constant returns false to inform the clean of outputs */
			else {
				return false;		
			}
		}
		/* (3) Logical-And expression is recursively collected in its children */
		else if(this.is_conjunction(expression)) {	
			SymbolBinaryExpression bin_expression = (SymbolBinaryExpression) expression;
			if(this.collect_in_conjunctions(bin_expression.get_loperand(), conjunctions)) {
				if(this.collect_in_conjunctions(bin_expression.get_roperand(), conjunctions)) {
					return true;	/* Return true if all the children are correctly inserted */
				}
			}
			return false;
		}
		/* (4) Otherwise, the boolean representation of input is inserted */
		else {
			conjunctions.add(SymbolFactory.sym_condition(expression, true));
			return true;			/* Return true to inform the insertion successful */
		}
	}
	/**
	 * 	Stage-I. Condition Improvement
	 * 	In this stage, the expression in constraint is translated into a collection of new expressions as the basic
	 * 	items in the conjunction of X1 && X2 && X3 ... && Xn --> {X1, X2, ..., Xn}, such that the outputs include:
	 * 		(1)	coverage constraint on statement.
	 * 		(2) constraints improved from local conditions collected from conjunction form.
	 * 
	 * @param constraint
	 * @param cir_mutations
	 * @return
	 * @throws Exception
	 */
	private Collection<SymConstraint> improve_I(SymConstraint constraint, CirMutations cir_mutations) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			/* 1. collect the conditions in the conjunction of expression */
			Set<SymbolExpression> conjunctions = new HashSet<SymbolExpression>();
			boolean pass = this.collect_in_conjunctions(constraint.get_condition(), conjunctions);
			Collection<SymConstraint> local_constraints = new HashSet<SymConstraint>();
			CirStatement statement = constraint.get_execution().get_statement();
			
			/* 2. when available conditions are collected in the conjunctions */
			if(pass) {
				for(SymbolExpression conjunction : conjunctions) {
					local_constraints.add(cir_mutations.expression_constraint(statement, conjunction, true));
				}
				local_constraints.add(cir_mutations.statement_constraint(statement, 1));
			}
			/* 3. when false constant is included, return only false constant */
			else {
				local_constraints.add(cir_mutations.expression_constraint(statement, Boolean.FALSE, true));
			}
			
			/* 3. return the local constraints improved from source */
			return local_constraints;
		}
	}
	/* Stage-II. Path Improvement on Decidable Prefix */
	/**
	 * @param condition
	 * @param execution
	 * @return whether the symbolic expression in condition is defined in the statement
	 * @throws Exception
	 */
	private boolean is_defined_in(SymbolExpression condition, CirExecution execution) throws Exception {
		CirStatement statement = execution.get_statement();
		if(statement instanceof CirAssignStatement) {
			SymbolExpression definition = SymbolFactory.sym_expression(
					((CirAssignStatement) statement).get_lvalue());
			
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(condition); SymbolNode sym_node;
			while(!queue.isEmpty()) {
				sym_node = queue.poll();
				for(SymbolNode child : sym_node.get_children()) {
					queue.add(child);
				}
				if(sym_node.equals(definition)) {
					return true;
				}
			}
			
			return false;
		}
		else {
			return false;
		}
	}
	/**
	 * divide the statement-expression in form of "execution >= times" or "execution < times" as 
	 * tuple of [execution, times, operator]
	 * @param expression
	 * @return	[CirExecution, Integer, COperator] to represent "execution operator integer" or 
	 * 			null if the expression is not statement-oriented
	 * @throws Exception
	 */
	private Object[] decode_statement_expression(SymbolExpression expression) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			
			if(loperand instanceof SymbolIdentifier && loperand.get_source() instanceof CirExecution) {
				CirExecution execution = (CirExecution) loperand.get_source();
				int times = ((SymbolConstant) roperand).get_int().intValue();
				switch(operator) {
				case greater_tn:
				case greater_eq:
				case smaller_tn:
				case smaller_eq:
				case equal_with:
				case not_equals: 	break;
				default: throw new IllegalArgumentException("Unsupport: " + operator);
				}
				return new Object[] { execution, Integer.valueOf(times), operator };
			}
			else if(roperand instanceof SymbolIdentifier && roperand.get_source() instanceof CirExecution) {
				CirExecution execution = (CirExecution) roperand.get_source();
				int times = ((SymbolConstant) loperand).get_int().intValue();
				switch(operator) {
				case greater_tn:	operator = COperator.smaller_tn;	break;
				case greater_eq:	operator = COperator.smaller_eq;	break;
				case smaller_tn:	operator = COperator.greater_tn;	break;
				case smaller_eq:	operator = COperator.greater_eq;	break;
				case equal_with:	operator = COperator.equal_with;	break;
				case not_equals: 	operator = COperator.not_equals;	break;
				default: throw new IllegalArgumentException("Unsupport: " + operator);
				}
				return new Object[] { execution, Integer.valueOf(times), operator };
			}
		}
		return null;	/* when the expression is not statement-oriented */
	}
	/**
	 * 	In Stage-II, the constraint is improved over the prefix decidable path reaching to the target execution.
	 * 	
	 * @param constraint
	 * @param cir_mutations
	 * @return 
	 * @throws Exception
	 */
	private void improve_on_path(SymConstraint constraint, CirMutations cir_mutations, 
			Collection<SymConstraint> constraints) throws Exception {
		/* declarations */
		CirExecution execution = constraint.get_execution(), source;
		SymbolExpression condition = constraint.get_condition();
		
		/* for constant constraint, improve it to program or function entry */
		if(condition instanceof SymbolConstant) {
			CirFunction function = execution.get_graph().get_function().get_graph().get_main_function();
			if(function == null) { function = execution.get_graph().get_function(); }
			constraints.add(cir_mutations.expression_constraint(
					function.get_flow_graph().get_entry().get_statement(), condition, true));
		}
		else {
			Object[] exec_time_oprt = this.decode_statement_expression(condition);
			/* for statement coverage constraint, fix the constraint on the given point */ 
			if(exec_time_oprt != null) {
				execution = (CirExecution) exec_time_oprt[0];
				constraints.add(cir_mutations.expression_constraint(
						execution.get_statement(), condition, true));
			}
			/* otherwise, improve the condition to an available point in prefix paths */
			else {
				CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = path.get_reverse_edges();
				source = execution;
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					CirExecution prev_node = edge.get_source();
					if(this.is_defined_in(condition, prev_node)) {
						source = edge.get_target(); break;	// find definition point
					}
					else {
						source = prev_node;
					}
				}
				constraints.add(cir_mutations.expression_constraint(
							source.get_statement(), condition, true));
			}
		}
	}
	/**
	 * In Stage-II, the constraint is improved over the prefix decidable path reaching to the target execution.
	 * @param source_constraints
	 * @param cir_mutations
	 * @return set of constraints improved from the stage II by improving on path
	 * @throws Exception
	 */
	private Collection<SymConstraint> improve_II(Iterable<SymConstraint> source_constraints, CirMutations cir_mutations) throws Exception {
		Collection<SymConstraint> target_constraints = new HashSet<SymConstraint>();
		for(SymConstraint source_constraint : source_constraints) {
			this.improve_on_path(source_constraint, cir_mutations, target_constraints);
		}
		return target_constraints;
	}
	/* Stage-III. Subsumption-based Constraint Extension */
	/**
	 * generate the set of conditions subsuming the expression
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void extend_subsumption_set(SymbolExpression expression, Collection<SymbolExpression> conditions) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			SymbolBinaryExpression bin_expression = (SymbolBinaryExpression) expression;
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			switch(operator) {
			case smaller_tn:
			{
				conditions.add(bin_expression);
				conditions.add(SymbolFactory.smaller_eq(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymbolFactory.not_equals(bin_expression.get_loperand(), bin_expression.get_roperand()));
				break;
			}
			case smaller_eq:
			{
				conditions.add(bin_expression);
				break;
			}
			case greater_tn:
			{
				conditions.add(SymbolFactory.smaller_tn(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymbolFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymbolFactory.not_equals(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case greater_eq:
			{
				conditions.add(SymbolFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case equal_with:
			{
				conditions.add(bin_expression);
				conditions.add(SymbolFactory.smaller_eq(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymbolFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case not_equals:
			{
				conditions.add(bin_expression);
				conditions.add(SymbolFactory.not_equals(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			default:
			{
				conditions.add(expression);
				break;
			}
			}
		}
		else {
			conditions.add(expression);
		}
		conditions.add(expression);
	}
	/**
	 * Extend the constraint to generate its subsumption set
	 * @param constraint
	 * @param cir_mutations
	 * @param subsumptions
	 * @throws Exception
	 */
	private void extend_subsumption_constraints(SymConstraint constraint, 
			CirMutations cir_mutations, Collection<SymConstraint> subsumptions) throws Exception {
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.extend_subsumption_set(constraint.get_condition(), conditions);
		for(SymbolExpression condition : conditions) {
			subsumptions.add(cir_mutations.expression_constraint(constraint.get_statement(), condition, true));
		}
	}
	/**
	 * @param source_constraints
	 * @param cir_mutations
	 * @return the set of constraints as subsuming the source constraints
	 * @throws Exception
	 */
	private Collection<SymConstraint> improve_III(Iterable<SymConstraint> source_constraints, CirMutations cir_mutations) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(source_constraints == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			Collection<SymConstraint> target_constraints = new HashSet<SymConstraint>();
			for(SymConstraint source_constraint : source_constraints) {
				this.extend_subsumption_constraints(source_constraint, cir_mutations, target_constraints);
			}
			return target_constraints;
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the set of symbolic constraints improved from the given one
	 * @throws Exception
	 */
	public static Collection<SymConstraint> improve_constraints(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		constraint = cir_mutations.optimize(constraint, null);
		Collection<SymConstraint> local_constraints = utils.improve_I(constraint, cir_mutations);
		Collection<SymConstraint> paths_constraints = utils.improve_II(local_constraints, cir_mutations);
		Collection<SymConstraint> exted_constraints = utils.improve_III(paths_constraints, cir_mutations);
		return exted_constraints;
	}
	
}
