package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.program.AstCirEdge;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.types.AstCirParChild;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It implements the local subsumption extension based on AstContextState.
 * 	
 * 	@author yukimula
 *
 */
public final class AstContextStates {
	
	/* factory methods */
	/**
	 * @param location	the location in which the mutation is directly seeded
	 * @param mutant
	 * @return			sed_muta(location;	mutant_ID,	operators)
	 * @throws Exception
	 */
	public static AstSeedMutantState	sed_muta(AstCirNode location, Mutant mutant) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as null");
		}
		else if(location.is_expression_node() || location.is_statement_node()) { 	
			return new AstSeedMutantState(location, mutant); 
		}
		else {
			throw new IllegalArgumentException("Invalid as: " + location);
		}
	}
	/**
	 * @param statement	the statement to be executed at the coverage condition
	 * @param min_times	the minimal times for executing the target statement
	 * @param max_times	the maximal times for executing the target statement
	 * @return			cov_time(statement, min_times, max_times)
	 * @throws Exception
	 */
	public static AstCoverTimesState	cov_time(AstCirNode statement, int min_times, int max_times) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: " + statement);
		}
		else if(min_times > max_times || max_times < 0) {
			throw new IllegalArgumentException(min_times + " --> " + max_times);
		}
		else { return new AstCoverTimesState(statement, min_times, max_times); }
	}
	/**
	 * @param statement	the statement in which the condition is evaluated
	 * @param condition	the condition to be satisfied at the given points
	 * @param must_need	True (always met) False (satisfied at least once)
	 * @return			eva_cond(statement, condition, must_need)
	 * @throws Exception
	 */
	public static AstConstraintState	eva_cond(AstCirNode statement, Object condition, boolean must_need) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else { return new AstConstraintState(statement, SymbolFactory.sym_condition(condition, true), must_need); }
	}
	/**
	 * @param statement
	 * @param muta_exec
	 * @return mut_stmt(statement, !muta_exec, muta_exec)
	 * @throws Exception
	 */
	public static AstBlockErrorState	set_stmt(AstCirNode statement, boolean muta_exec) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(statement.is_expression_node() || statement.is_statement_node()) {
			return new AstBlockErrorState(statement, 
					SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)), 
					SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + statement.get_node_type());
		}
	}
	/**
	 * @param statement
	 * @param orig_next
	 * @param muta_next
	 * @return	mut_flow(statement; orig_next, muta_next)
	 * @throws Exception
	 */
	public static AstFlowsErrorState	set_flow(AstCirNode statement, AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		if(statement == null || !statement.is_statement_node()) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(orig_next == null) {
			throw new IllegalArgumentException("Invalid orig_next: null");
		}
		else if(muta_next == null) {
			throw new IllegalArgumentException("Invalid muta_next: null");
		}
		else { return new AstFlowsErrorState(statement, orig_next, muta_next); }
	}
	/**
	 * @param location
	 * @return set_stmt(func_body, TRUE, TRAP_VALUE)
	 * @throws Exception
	 */
	public static AstBlockErrorState	trp_stmt(AstCirNode location) throws Exception {
		if(location == null) {
			throw new IllegalArgumentException("Invalid location: null");
		}
		else {  
			while(!location.get_parent().is_module_node()) {
				location = location.get_parent();
			}
			return new AstBlockErrorState(location, SymbolFactory.
					sym_constant(Boolean.TRUE), ContextMutation.trap_value);
		}
	}
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @return
	 * @throws Exception
	 */
	public static AstValueErrorState	set_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(orig_value == null) {
			throw new IllegalArgumentException("Invalid orig_value: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(expression.is_expression_node()) {
			return new AstValueErrorState(expression, orig_value, muta_value);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + expression.get_node_type());
		}
	}
	
	/* singleon mode */
	private static final AstContextStates util = new AstContextStates();
	private AstContextStates() {}
	
	/* local extension */
	/**
	 * @param source
	 * @return it extends this current state to its directly subsumed states
	 * @throws Exception
	 */
	public static Collection<AstContextState> 	extend(AstContextState source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			Set<AstContextState> targets = new HashSet<AstContextState>();
			util.ext(source, targets);
			return targets;
		}
	}
	/**
	 * It extends the source state in local context and preserves the output to the targets
	 * @param source	the source state from which the target states are extended
	 * @param targets	to preserve the directly extended states from the source
	 * @throws Exception
	 */
	private	void	ext(AstContextState source, Collection<AstContextState> targets) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(targets == null) {
			throw new IllegalArgumentException("Invalid targets: null");
		}
		else {
			if(source instanceof AstSeedMutantState) {
				this.ext_sed_muta((AstSeedMutantState) source, targets);
			}
			else if(source instanceof AstConstraintState) {
				this.ext_eva_cond((AstConstraintState) source, targets);
			}
			else if(source instanceof AstCoverTimesState) {
				this.ext_cov_time((AstCoverTimesState) source, targets);
			}
			else if(source instanceof AstBlockErrorState) {
				this.ext_set_stmt((AstBlockErrorState) source, targets);
			}
			else if(source instanceof AstFlowsErrorState) {
				this.ext_set_flow((AstFlowsErrorState) source, targets);
			}
			else if(source instanceof AstValueErrorState) {
				this.ext_set_expr((AstValueErrorState) source, targets);
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + source);
			}
		}
	}
	/**
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_sed_muta(AstSeedMutantState source, Collection<AstContextState> targets) throws Exception {
		AstCirNode statement = source.get_location().statement_of();
		targets.add(AstContextStates.eva_cond(statement, Boolean.TRUE, false));
	}
	/**
	 * It collects the set of sub-conditions under the expression to given output collection set
	 * @param condition
	 * @param sub_conditions
	 * @throws Exception
	 */
	private	void	div_sym_conjunction(SymbolExpression condition, Collection<SymbolExpression> sub_conditions) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(sub_conditions == null) {
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) condition).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(operator == COperator.logic_and) {
				this.div_sym_conjunction(loperand, sub_conditions);
				this.div_sym_conjunction(roperand, sub_conditions);
			}
			else {
				sub_conditions.add(condition);
			}
		}
		else {
			sub_conditions.add(condition);
		}
	}
	/**
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_eva_cond(AstConstraintState source, Collection<AstContextState> targets) throws Exception {
		AstCirNode statement = source.get_location();
		SymbolExpression condition = source.get_condition();
		condition = ContextMutation.evaluate(condition, null, null);
		if(source.is_must()) {
			targets.add(AstContextStates.eva_cond(statement, condition, false));
		}
		else if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool().booleanValue()) {
				targets.add(AstContextStates.cov_time(statement, 1, Integer.MAX_VALUE));
			}
			else {
				targets.add(AstContextStates.eva_cond(statement, Boolean.TRUE, false));
				while(!statement.get_parent().is_module_node()) {
					statement = statement.get_parent();
				}
				targets.add(AstContextStates.eva_cond(statement, Boolean.FALSE, false));
			}
		}
		else if(ContextMutation.has_trap_value(condition)) {
			targets.add(AstContextStates.eva_cond(statement, Boolean.TRUE, false));
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) condition).get_coperator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(operator == COperator.logic_and) {
				Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
				this.div_sym_conjunction(condition, sub_conditions);
				for(SymbolExpression sub_condition : sub_conditions) {
					targets.add(AstContextStates.eva_cond(statement, sub_condition, false));
				}
			}
			else if(operator == COperator.greater_tn) {
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.greater_eq(loperand, roperand), false));
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.not_equals(loperand, roperand), false));
			}
			else if(operator == COperator.smaller_tn) {
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.smaller_eq(loperand, roperand), false));
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.not_equals(loperand, roperand), false));
			}
			else if(operator == COperator.equal_with) {
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.greater_eq(loperand, roperand), false));
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.smaller_eq(loperand, roperand), false));
			}
			else {
				targets.add(AstContextStates.eva_cond(statement, Boolean.TRUE, false));
			}
		}
		else {
			targets.add(AstContextStates.eva_cond(statement, Boolean.TRUE, false));
		}
	}
	/**
	 * @param value
	 * @return
	 */
	private	int		find_minimal_times(int value) {
		int times = 1;
		while(times < value) { times = times * 2; }
		return times / 2;
	}
	/**
	 * It extends the coverage-times state from the source
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_cov_time(AstCoverTimesState source, Collection<AstContextState> targets) throws Exception {
		AstCirNode statement = source.get_location();
		int times = this.find_minimal_times(source.get_minimal_times());
		if(times > 0) {
			targets.add(AstContextStates.cov_time(statement, times, Integer.MAX_VALUE));
		}
		else {
			if(statement.get_parent().is_module_node()) { /* reach the top-level */ }
			else {
				for(AstCirEdge in_edge : statement.get_in_edges()) {
					AstCirNode prev_statement = in_edge.get_source();
					AstNode location = prev_statement.get_ast_source();
					
					switch(in_edge.get_type()) {
					case skip_depend:
					case case_depend:
					{
						targets.add(AstContextStates.cov_time(prev_statement, 1, Integer.MAX_VALUE));	
						break;
					}
					case true_depend:
					{
						SymbolExpression condition;
						if(location instanceof AstIfStatement) {
							condition = SymbolFactory.sym_condition(((AstIfStatement) location).get_condition(), true);
						}
						else if(location instanceof AstForStatement) {
							AstExpressionStatement for_cond = ((AstForStatement) location).get_condition();
							if(for_cond.has_expression()) {
								condition = SymbolFactory.sym_condition(for_cond.get_expression(), true);
							}
							else {
								condition = SymbolFactory.sym_constant(Boolean.TRUE);
							}
						}
						else if(location instanceof AstWhileStatement) {
							condition = SymbolFactory.sym_condition(((AstWhileStatement) location).get_condition(), true);
						}
						else if(location instanceof AstDoWhileStatement) {
							condition = SymbolFactory.sym_condition(((AstDoWhileStatement) location).get_condition(), true);
						}
						else {
							throw new IllegalArgumentException("Invalid: " + location);
						}
						targets.add(AstContextStates.eva_cond(prev_statement, condition, false));
						break;
					}
					case fals_depend:
					{
						SymbolExpression condition;
						if(location instanceof AstIfStatement) {
							condition = SymbolFactory.sym_condition(((AstIfStatement) location).get_condition(), false);
						}
						else if(location instanceof AstForStatement) {
							AstExpressionStatement for_cond = ((AstForStatement) location).get_condition();
							if(for_cond.has_expression()) {
								condition = SymbolFactory.sym_condition(for_cond.get_expression(), false);
							}
							else {
								condition = SymbolFactory.sym_constant(Boolean.FALSE);
							}
						}
						else if(location instanceof AstWhileStatement) {
							condition = SymbolFactory.sym_condition(((AstWhileStatement) location).get_condition(), false);
						}
						else if(location instanceof AstDoWhileStatement) {
							condition = SymbolFactory.sym_condition(((AstDoWhileStatement) location).get_condition(), false);
						}
						else {
							throw new IllegalArgumentException("Invalid: " + location);
						}
						targets.add(AstContextStates.eva_cond(prev_statement, condition, false));
						break;
					}
					default:	
					{
						break;
					}
					}
				}
			}
		}
	}
	/**
	 * It extends the statement-error state to the directly subsumed ones
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_stmt(AstBlockErrorState source, Collection<AstContextState> targets) throws Exception {
		AstCirNode statement = source.get_location();
		if(source.is_trapping_exception()) { /* do nothing to propagate */ }
		else {
			for(AstCirNode child : statement.get_children()) {
				switch(child.get_child_type()) {
				case evaluate:
				case execute:
				case tbranch:
				case fbranch:	targets.add(AstContextStates.set_stmt(child, source.is_mutation_executed()));
				default:		break;
				}
			}
		}
	}
	/**
	 * It extends the flow-error states from the source
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_flow(AstFlowsErrorState source, Collection<AstContextState> targets) throws Exception { }
	/**
	 * It extends the local value state errors from the source
	 * @param source
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_expr(AstValueErrorState source, Collection<AstContextState> targets) throws Exception {
		SymbolExpression orig_value = source.get_original_value();
		SymbolExpression muta_value = source.get_mutation_value();
		SymbolExpression real_value = ContextMutation.evaluate(muta_value, null, null);
		if(ContextMutation.has_trap_value(real_value)) {
			targets.add(AstContextStates.trp_stmt(source.get_location()));
		}
		else {
			AstCirNode child = source.get_location();
			AstCirParChild child_type = child.get_child_type();
			switch(child_type) {
			case uoperand:		this.ext_set_uop(child, orig_value, muta_value, targets); 	break;
			case condition:		this.ext_set_con(child, orig_value, muta_value, targets); 	break;
			case loperand:		this.ext_set_lop(child, orig_value, muta_value, targets); 	break;
			case roperand:		this.ext_set_rop(child, orig_value, muta_value, targets); 	break;
			case ivalue:		this.ext_set_iva(child, orig_value, muta_value, targets); 	break;
			case lvalue:		this.ext_set_lva(child, orig_value, muta_value, targets); 	break;
			case rvalue:		this.ext_set_rva(child, orig_value, muta_value, targets); 	break;
			case address:		this.ext_set_adr(child, orig_value, muta_value, targets); 	break;
			case index:			this.ext_set_idx(child, orig_value, muta_value, targets); 	break;
			case element:		this.ext_set_ele(child, orig_value, muta_value, targets); 	break;
			case fbody:			this.ext_set_fbd(child, orig_value, muta_value, targets); 	break;
			case callee:		this.ext_set_cal(child, orig_value, muta_value, targets); 	break;
			case argument:		this.ext_set_arg(child, orig_value, muta_value, targets); 	break;
			/* reach the top-level expression of the child */
			case evaluate:		
			case n_condition:	
			case tbranch:
			case fbranch:
			case execute:		break;
			default:			throw new IllegalArgumentException("Invalid: " + child_type + "\n\t\t--> " + 
																	child.get_ast_source().generate_code() + "\n\t\t--> " + 
																	child.get_parent().get_ast_source().generate_code());
			}
		}
	}
	
	/* data-error extension */
	/**
	 * cast_expr|coma_expr|unary_expr(+, -, ~, &) --> operand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param outputs
	 * @throws Exception
	 */
	private void	ext_set_uop(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstCastExpression) {
			CType type = ((AstCastExpression) source).get_typename().get_type();
			orig_value = SymbolFactory.cast_expression(type, orig_value);
			muta_value = SymbolFactory.cast_expression(type, muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstCommaExpression) {
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstUnaryExpression) {
			COperator operator = ((AstUnaryExpression) source).get_operator().get_operator();
			if(operator == COperator.positive) { }
			else if(operator == COperator.negative) {
				orig_value = SymbolFactory.arith_neg(orig_value);
				muta_value = SymbolFactory.arith_neg(muta_value);
			}
			else if(operator == COperator.bit_not) {
				orig_value = SymbolFactory.bitws_rsv(orig_value);
				muta_value = SymbolFactory.bitws_rsv(muta_value);
			}
			else if(operator == COperator.address_of) {
				orig_value = SymbolFactory.address_of(orig_value);
				muta_value = SymbolFactory.address_of(muta_value);
			}
			else {
				throw new IllegalArgumentException(operator.toString());
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * logic_expr|cond_expr|loop_stmt|ifte_stmt|for_stmt --> condition
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private void	ext_set_con(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstLogicUnaryExpression) {
			orig_value = SymbolFactory.sym_condition(orig_value, false);
			muta_value = SymbolFactory.sym_condition(muta_value, false);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstLogicBinaryExpression) {
			COperator operator = ((AstLogicBinaryExpression) source).get_operator().get_operator();
			AstExpression operand;
			if(parent.get_child(0) == child) {
				operand = ((AstLogicBinaryExpression) source).get_roperand();
			}
			else {
				operand = ((AstLogicBinaryExpression) source).get_loperand();
			}
			if(operator == COperator.logic_and) {
				orig_value = SymbolFactory.logic_and(orig_value, operand);
				muta_value = SymbolFactory.logic_and(muta_value, operand);
			}
			else {
				orig_value = SymbolFactory.logic_ior(orig_value, operand);
				muta_value = SymbolFactory.logic_ior(muta_value, operand);
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstConditionalExpression) {
			CType type = ((AstConditionalExpression) source).get_value_type();
			AstExpression loperand = ((AstConditionalExpression) source).get_true_branch();
			AstExpression roperand = ((AstConditionalExpression) source).get_false_branch();
			orig_value = SymbolFactory.ifte_expression(type, orig_value, loperand, roperand);
			muta_value = SymbolFactory.ifte_expression(type, muta_value, loperand, roperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstIfStatement || 
				source instanceof AstWhileStatement || 
				source instanceof AstDoWhileStatement ||
				source instanceof AstForStatement) {
			if(!SymbolFactory.is_bool(orig_value) || !SymbolFactory.is_bool(muta_value)) {
				orig_value = SymbolFactory.sym_condition(orig_value, true);
				muta_value = SymbolFactory.sym_condition(muta_value, true);
				targets.add(AstContextStates.set_expr(child, orig_value, muta_value));
			}
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * @param type
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression sym_expression(CType type, COperator operator, Object loperand, Object roperand) throws Exception {
		switch(operator) {
		case arith_add:			return SymbolFactory.arith_add(type, loperand, roperand);
		case arith_sub:			return SymbolFactory.arith_sub(type, loperand, roperand);
		case arith_mul:			return SymbolFactory.arith_mul(type, loperand, roperand);
		case arith_div:			return SymbolFactory.arith_div(type, loperand, roperand);
		case arith_mod:			return SymbolFactory.arith_mod(type, loperand, roperand);
		case bit_and:			return SymbolFactory.bitws_and(type, loperand, roperand);
		case bit_or:			return SymbolFactory.bitws_ior(type, loperand, roperand);
		case bit_xor:			return SymbolFactory.bitws_xor(type, loperand, roperand);
		case left_shift:		return SymbolFactory.bitws_lsh(type, loperand, roperand);
		case righ_shift:		return SymbolFactory.bitws_rsh(type, loperand, roperand);
		case logic_and:			return SymbolFactory.logic_and(loperand, roperand);
		case logic_or:			return SymbolFactory.logic_ior(loperand, roperand);
		case greater_tn:		return SymbolFactory.greater_tn(loperand, roperand);
		case greater_eq:		return SymbolFactory.greater_eq(loperand, roperand);
		case smaller_tn:		return SymbolFactory.smaller_tn(loperand, roperand);
		case smaller_eq:		return SymbolFactory.smaller_eq(loperand, roperand);
		case equal_with:		return SymbolFactory.equal_with(loperand, roperand);
		case not_equals:		return SymbolFactory.not_equals(loperand, roperand);
		case assign:			return SymbolFactory.exp_assign(loperand, roperand);
		case arith_add_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_add(type, loperand, roperand));
		case arith_sub_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_sub(type, loperand, roperand));
		case arith_mul_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mul(type, loperand, roperand));
		case arith_div_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_div(type, loperand, roperand));
		case arith_mod_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mod(type, loperand, roperand));
		case bit_and_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_and(type, loperand, roperand));
		case bit_or_assign:		return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_ior(type, loperand, roperand));
		case bit_xor_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_xor(type, loperand, roperand));
		case left_shift_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_lsh(type, loperand, roperand));
		case righ_shift_assign:	return SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_rsh(type, loperand, roperand));
		default:				throw new IllegalArgumentException("Invalid operator: " + operator.toString());
		}
	}
	/**
	 * biny_expr|cond_expr --> loperand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_lop(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstConditionalExpression) {
			CType type = ((AstConditionalExpression) source).get_value_type();
			AstExpression condition = ((AstConditionalExpression) source).get_condition();
			AstExpression roperand = ((AstConditionalExpression) source).get_false_branch();
			orig_value = SymbolFactory.ifte_expression(type, condition, orig_value, roperand);
			muta_value = SymbolFactory.ifte_expression(type, condition, muta_value, roperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstBinaryExpression) {
			CType type = ((AstBinaryExpression) source).get_value_type();
			AstExpression roperand = ((AstBinaryExpression) source).get_roperand();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			orig_value = this.sym_expression(type, operator, orig_value, roperand);
			muta_value = this.sym_expression(type, operator, muta_value, roperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * biny_expr|cond_expr --> roperand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_rop(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstConditionalExpression) {
			CType type = ((AstConditionalExpression) source).get_value_type();
			AstExpression condition = ((AstConditionalExpression) source).get_condition();
			AstExpression loperand = ((AstConditionalExpression) source).get_true_branch();
			orig_value = SymbolFactory.ifte_expression(type, condition, loperand, orig_value);
			muta_value = SymbolFactory.ifte_expression(type, condition, loperand, muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstBinaryExpression) {
			CType type = ((AstBinaryExpression) source).get_value_type();
			AstExpression loperand = ((AstBinaryExpression) source).get_loperand();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			orig_value = this.sym_expression(type, operator, loperand, orig_value);
			muta_value = this.sym_expression(type, operator, loperand, muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * incr_expr --> operand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_iva(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstIncreUnaryExpression) {
			CType type = ((AstIncreUnaryExpression) source).get_value_type();
			COperator operator = ((AstIncreUnaryExpression) source).get_operator().get_operator();
			switch(operator) {
			case increment:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, Integer.valueOf(1)));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_add(type, muta_value, Integer.valueOf(1)));
				break;
			}
			case decrement:	
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, Integer.valueOf(1)));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_sub(type, muta_value, Integer.valueOf(1)));
				break;
			}
			default:	throw new IllegalArgumentException("Invalid operator: " + operator.toString());
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstIncrePostfixExpression) {
			CType type = ((AstIncrePostfixExpression) source).get_value_type();
			COperator operator = ((AstIncrePostfixExpression) source).get_operator().get_operator();
			switch(operator) {
			case increment:
			{
				orig_value = SymbolFactory.imp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, Integer.valueOf(1)));
				muta_value = SymbolFactory.imp_assign(muta_value, SymbolFactory.arith_add(type, muta_value, Integer.valueOf(1)));
				break;
			}
			case decrement:	
			{
				orig_value = SymbolFactory.imp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, Integer.valueOf(1)));
				muta_value = SymbolFactory.imp_assign(muta_value, SymbolFactory.arith_sub(type, muta_value, Integer.valueOf(1)));
				break;
			}
			default:	throw new IllegalArgumentException("Invalid operator: " + operator.toString());
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * biny_expr|retr_stmt|init_decl --> loperand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_lva(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstBinaryExpression) {
			CType type = ((AstBinaryExpression) source).get_value_type();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			AstExpression roperand = ((AstBinaryExpression) source).get_roperand();
			orig_value = this.sym_expression(type, operator, orig_value, roperand);
			muta_value = this.sym_expression(type, operator, muta_value, roperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstInitDeclarator) { }
		else if(source instanceof AstReturnStatement) { }
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	/**
	 * biny_expr|retr_stmt|init_decl --> roperand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_rva(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstBinaryExpression) {
			CType type = ((AstBinaryExpression) source).get_value_type();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			AstExpression loperand = ((AstBinaryExpression) source).get_loperand();
			orig_value = this.sym_expression(type, operator, loperand, orig_value);
			muta_value = this.sym_expression(type, operator, loperand, muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstInitDeclarator) { }
		else if(source instanceof AstReturnStatement) { }
		else {
			throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	/**
	 * addr_expr | unry_expr (*) --> operand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_adr(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstArrayExpression) {
			AstExpression address = ((AstArrayExpression) source).get_array_expression();
			AstExpression index = ((AstArrayExpression) source).get_dimension_expression();
			orig_value = SymbolFactory.arith_add(address.get_value_type(), orig_value, index);
			muta_value = SymbolFactory.arith_add(address.get_value_type(), muta_value, index);
			orig_value = SymbolFactory.dereference(orig_value);
			muta_value = SymbolFactory.dereference(muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstUnaryExpression) {
			orig_value = SymbolFactory.dereference(orig_value);
			muta_value = SymbolFactory.dereference(muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * addr_expr | unry_expr (*) --> operand
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_idx(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstArrayExpression) {
			AstExpression address = ((AstArrayExpression) source).get_array_expression();
			orig_value = SymbolFactory.arith_add(address.get_value_type(), address, orig_value);
			muta_value = SymbolFactory.arith_add(address.get_value_type(), address, muta_value);
			orig_value = SymbolFactory.dereference(orig_value);
			muta_value = SymbolFactory.dereference(muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * init_body --> expression
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_ele(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		List<Object> orig_elements = new ArrayList<Object>();
		List<Object> muta_elements = new ArrayList<Object>();
		AstCirNode parent = child.get_parent();
		
		for(int k = 0; k < parent.number_of_children(); k++) {
			if(parent.get_child(k) == child) {
				orig_elements.add(orig_value);
				muta_elements.add(muta_value);
			}
			else {
				orig_elements.add(parent.get_child(k).get_ast_source());
				muta_elements.add(parent.get_child(k).get_ast_source());
			}
		}
		
		orig_value = SymbolFactory.initializer_list(orig_elements);
		muta_value = SymbolFactory.initializer_list(muta_elements);
		targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
	}
	/**
	 * field_expr --> expression
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_fbd(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstFieldExpression) {
			String field = ((AstFieldExpression) source).get_field().get_name();
			orig_value = SymbolFactory.field_expression(orig_value, field);
			muta_value = SymbolFactory.field_expression(muta_value, field);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * call_expr --> function
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_cal(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstFunCallExpression) {
			List<Object> arguments = new ArrayList<Object>();
			if(((AstFunCallExpression) source).has_argument_list()) {
				AstArgumentList list = ((AstFunCallExpression) source).get_argument_list();
				for(int k = 0; k < list.number_of_arguments(); k++) {
					arguments.add(list.get_argument(k));
				}
			}
			orig_value = SymbolFactory.call_expression(orig_value, arguments);
			muta_value = SymbolFactory.call_expression(muta_value, arguments);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	/**
	 * call_expr --> argument[k]
	 * @param child
	 * @param orig_value
	 * @param muta_value
	 * @param targets
	 * @throws Exception
	 */
	private	void	ext_set_arg(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstFunCallExpression) {
			List<Object> orig_arguments = new ArrayList<Object>();
			List<Object> muta_arguments = new ArrayList<Object>();
			AstExpression function = ((AstFunCallExpression) source).get_function();
			for(int k = 1; k < parent.number_of_children(); k++) {
				if(parent.get_child(k) == child) {
					orig_arguments.add(orig_value);
					muta_arguments.add(muta_value);
				}
				else {
					orig_arguments.add(parent.get_child(k).get_ast_source());
					muta_arguments.add(parent.get_child(k).get_ast_source());
				}
			}
			orig_value = SymbolFactory.call_expression(function, orig_arguments);
			muta_value = SymbolFactory.call_expression(function, muta_arguments);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(source.generate_code());
		}
	}
	
}
