package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
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
import com.jcsa.jcparse.lang.program.types.AstCirNodeType;
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
			throw new IllegalArgumentException("Invalid statement: null");
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
					sym_constant(Boolean.TRUE), ContextMutations.trap_value);
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
		else if(expression.is_expression_node() || expression.get_node_type() == AstCirNodeType.retr_stmt) {
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
		condition = ContextMutations.evaluate(condition, null, null);
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
		else if(ContextMutations.has_trap_value(condition)) {
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
		SymbolExpression real_value = ContextMutations.evaluate(muta_value, null, null);
		if(ContextMutations.has_trap_value(real_value)) {
			targets.add(AstContextStates.trp_stmt(source.get_location()));
		}
		else {
			AstCirNode child = source.get_location();
			AstCirParChild child_type = child.get_child_type();
			switch(child_type) {
			case uoperand:		this.ext_set_uoperand(child, orig_value, muta_value, targets);	break;
			case condition:		this.ext_set_condition(child, orig_value, muta_value, targets); break;
			case loperand:		this.ext_set_loperand(child, orig_value, muta_value, targets); 	break;
			case roperand:		this.ext_set_roperand(child, orig_value, muta_value, targets); 	break;
			case ivalue:		this.ext_set_ivalue(child, orig_value, muta_value, targets); 	break;
			case lvalue:		this.ext_set_lvalue(child, orig_value, muta_value, targets); 	break;
			case rvalue:		this.ext_set_rvalue(child, orig_value, muta_value, targets); 	break;
			case address:		this.ext_set_address(child, orig_value, muta_value, targets); 	break;
			case index:			this.ext_set_index(child, orig_value, muta_value, targets); 	break;
			case element:		this.ext_set_element(child, orig_value, muta_value, targets); 	break;
			case argument:		this.ext_set_argument(child, orig_value, muta_value, targets); 	break;
			case evaluate:		this.ext_set_evaluate(child, orig_value, muta_value, targets); 	break;
			case fbody:			this.ext_set_fbody(child, orig_value, muta_value, targets); 	break;
			case callee:		this.ext_set_callee(child, orig_value, muta_value, targets); 	break;
			case n_condition:	this.ext_set_n_condition(child, orig_value, muta_value, targets); break;
			default:			break;
			}
		}
	}
	
	/* value propagation */
	private	void	ext_set_uoperand(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstExpression source = (AstExpression) child.get_parent().get_ast_source();
		if(source instanceof AstCastExpression) {
			CType cast_type = ((AstCastExpression) source).get_typename().get_type();
			orig_value = SymbolFactory.cast_expression(cast_type, orig_value);
			muta_value = SymbolFactory.cast_expression(cast_type, muta_value);
			targets.add(AstContextStates.set_expr(child.get_parent(), orig_value, muta_value));
		}
		else if(source instanceof AstCommaExpression) {
			targets.add(AstContextStates.set_expr(child.get_parent(), orig_value, muta_value));
		}
		else if(source instanceof AstUnaryExpression) {
			COperator operator = ((AstUnaryExpression) source).get_operator().get_operator();
			switch(operator) {
			case positive:		break;
			case negative:		
			{
				orig_value = SymbolFactory.arith_neg(orig_value); 
				muta_value = SymbolFactory.arith_neg(muta_value);
				break;
			}
			case bit_not:
			{
				orig_value = SymbolFactory.bitws_rsv(orig_value);
				muta_value = SymbolFactory.bitws_rsv(muta_value);
				break;
			}
			case address_of:
			{
				orig_value = SymbolFactory.address_of(orig_value);
				muta_value = SymbolFactory.address_of(muta_value);
				break;
			}
			default:	
			{
				throw new IllegalArgumentException("Unsupported: " + operator);
			}
			}
			targets.add(AstContextStates.set_expr(child.get_parent(), orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported source: " + source.generate_code());
		}
	}
	private	void	ext_set_condition(AstCirNode child, SymbolExpression orig_value,
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent(); AstNode source = parent.get_ast_source();
		if(source instanceof AstLogicBinaryExpression) {
			AstExpression operand;
			if(parent.get_child(0) == child) {
				operand = ((AstLogicBinaryExpression) source).get_roperand();
			}
			else {
				operand = ((AstLogicBinaryExpression) source).get_loperand();
			}
			
			COperator operator = ((AstLogicBinaryExpression) source).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				targets.add(AstContextStates.eva_cond(parent.statement_of(), operand, true));
				orig_value = SymbolFactory.logic_and(orig_value, operand);
				muta_value = SymbolFactory.logic_and(muta_value, operand);
			}
			else {
				targets.add(AstContextStates.eva_cond(parent.statement_of(), operand, false));
				orig_value = SymbolFactory.logic_ior(orig_value, operand);
				muta_value = SymbolFactory.logic_ior(muta_value, operand);
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstConditionalExpression) {
			CType type = ((AstConditionalExpression) source).get_value_type();
			AstExpression toperand = ((AstConditionalExpression) source).get_true_branch();
			AstExpression foperand = ((AstConditionalExpression) source).get_false_branch();
			orig_value = SymbolFactory.ifte_expression(type, orig_value, toperand, foperand);
			muta_value = SymbolFactory.ifte_expression(type, muta_value, toperand, foperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstIfStatement) {
			if(!SymbolFactory.is_bool(orig_value)) {
				orig_value = SymbolFactory.sym_condition(orig_value, true);
				muta_value = SymbolFactory.sym_condition(muta_value, true);
				targets.add(AstContextStates.set_expr(child, orig_value, muta_value));
			}
		}
		else if(source instanceof AstForStatement) {
			if(!SymbolFactory.is_bool(orig_value)) {
				orig_value = SymbolFactory.sym_condition(orig_value, true);
				muta_value = SymbolFactory.sym_condition(muta_value, true);
				targets.add(AstContextStates.set_expr(child, orig_value, muta_value));
			}
		}
		else if(source instanceof AstWhileStatement) {
			if(!SymbolFactory.is_bool(orig_value)) {
				orig_value = SymbolFactory.sym_condition(orig_value, true);
				muta_value = SymbolFactory.sym_condition(muta_value, true);
				targets.add(AstContextStates.set_expr(child, orig_value, muta_value));
			}
		}
		else if(source instanceof AstDoWhileStatement) {
			if(!SymbolFactory.is_bool(orig_value)) {
				orig_value = SymbolFactory.sym_condition(orig_value, true);
				muta_value = SymbolFactory.sym_condition(muta_value, true);
				targets.add(AstContextStates.set_expr(child, orig_value, muta_value));
			}
		}
		else if(source instanceof AstLogicUnaryExpression) {
			orig_value = SymbolFactory.sym_condition(orig_value, false);
			muta_value = SymbolFactory.sym_condition(muta_value, false);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source.getClass().getSimpleName());
		}
	}
	private	void	ext_set_loperand(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent(); 
		AstNode source = parent.get_ast_source();
		AstCirNode statement = parent.statement_of();
		
		if(source instanceof AstBinaryExpression) {
			CType type = ((AstBinaryExpression) source).get_value_type();
			AstExpression roperand = ((AstBinaryExpression) source).get_roperand();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			switch(operator) {
			case arith_add:
			{
				orig_value = SymbolFactory.arith_add(type, orig_value, roperand);
				muta_value = SymbolFactory.arith_add(type, muta_value, roperand);
				break;
			}
			case arith_sub:
			{
				orig_value = SymbolFactory.arith_sub(type, orig_value, roperand);
				muta_value = SymbolFactory.arith_sub(type, muta_value, roperand);
				break;
			}
			case arith_mul:
			{
				orig_value = SymbolFactory.arith_mul(type, orig_value, roperand);
				muta_value = SymbolFactory.arith_mul(type, muta_value, roperand);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.not_equals(roperand, Integer.valueOf(0)), false));
				break;
			}
			case arith_div:
			{
				orig_value = SymbolFactory.arith_div(type, orig_value, roperand);
				muta_value = SymbolFactory.arith_div(type, muta_value, roperand);
				break;
			}
			case arith_mod:
			{
				orig_value = SymbolFactory.arith_mod(type, orig_value, roperand);
				muta_value = SymbolFactory.arith_mod(type, muta_value, roperand);
				break;
			}
			case bit_and:
			{
				orig_value = SymbolFactory.bitws_and(type, orig_value, roperand);
				muta_value = SymbolFactory.bitws_and(type, muta_value, roperand);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.not_equals(roperand, Integer.valueOf(0)), false));
				break;
			}
			case bit_or:
			{
				orig_value = SymbolFactory.bitws_ior(type, orig_value, roperand);
				muta_value = SymbolFactory.bitws_ior(type, muta_value, roperand);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.not_equals(roperand, Integer.valueOf(~0)), false));
				break;
			}
			case bit_xor:
			{
				orig_value = SymbolFactory.bitws_xor(type, orig_value, roperand);
				muta_value = SymbolFactory.bitws_xor(type, muta_value, roperand);
				break;
			}
			case left_shift:
			{
				orig_value = SymbolFactory.bitws_lsh(type, orig_value, roperand);
				muta_value = SymbolFactory.bitws_lsh(type, muta_value, roperand);
				break;
			}
			case righ_shift:
			{
				orig_value = SymbolFactory.bitws_rsh(type, orig_value, roperand);
				muta_value = SymbolFactory.bitws_rsh(type, muta_value, roperand);
				break;
			}
			case greater_tn:
			{
				orig_value = SymbolFactory.greater_tn(orig_value, roperand);
				muta_value = SymbolFactory.greater_tn(muta_value, roperand);
				break;
			}
			case greater_eq:
			{
				orig_value = SymbolFactory.greater_eq(orig_value, roperand);
				muta_value = SymbolFactory.greater_eq(muta_value, roperand);
				break;
			}
			case smaller_tn:	
			{
				orig_value = SymbolFactory.smaller_tn(orig_value, roperand);
				muta_value = SymbolFactory.smaller_tn(muta_value, roperand);
				break;
			}
			case smaller_eq:	
			{
				orig_value = SymbolFactory.smaller_eq(orig_value, roperand);
				muta_value = SymbolFactory.smaller_eq(muta_value, roperand);
				break;
			}
			case equal_with:	
			{
				orig_value = SymbolFactory.equal_with(orig_value, roperand);
				muta_value = SymbolFactory.equal_with(muta_value, roperand);
				break;
			}
			case not_equals:	
			{
				orig_value = SymbolFactory.not_equals(orig_value, roperand);
				muta_value = SymbolFactory.not_equals(muta_value, roperand);
				break;
			}
			default:	throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstConditionalExpression) {
			CType type = ((AstConditionalExpression) source).get_value_type();
			AstExpression condition = ((AstConditionalExpression) source).get_condition();
			AstExpression foperand = ((AstConditionalExpression) source).get_false_branch();
			orig_value = SymbolFactory.ifte_expression(type, condition, orig_value, foperand);
			muta_value = SymbolFactory.ifte_expression(type, condition, muta_value, foperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	void	ext_set_roperand(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent(); 
		AstNode source = parent.get_ast_source();
		AstCirNode statement = parent.statement_of();
		
		if(source instanceof AstConditionalExpression) {
			CType type = ((AstConditionalExpression) source).get_value_type();
			AstExpression condition = ((AstConditionalExpression) source).get_condition();
			AstExpression foperand = ((AstConditionalExpression) source).get_false_branch();
			orig_value = SymbolFactory.ifte_expression(type, condition, orig_value, foperand);
			muta_value = SymbolFactory.ifte_expression(type, condition, muta_value, foperand);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstBinaryExpression) {
			CType type = ((AstBinaryExpression) source).get_value_type();
			AstExpression loperand = ((AstBinaryExpression) source).get_loperand();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			
			switch(operator) {
			case arith_add:
			{
				orig_value = SymbolFactory.arith_add(type, loperand, orig_value);
				muta_value = SymbolFactory.arith_add(type, loperand, muta_value);
				break;
			}
			case arith_sub:
			{
				orig_value = SymbolFactory.arith_sub(type, loperand, orig_value);
				muta_value = SymbolFactory.arith_sub(type, loperand, muta_value);
				break;
			}
			case arith_mul:
			{
				orig_value = SymbolFactory.arith_mul(type, loperand, orig_value);
				muta_value = SymbolFactory.arith_mul(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
								not_equals(loperand, Integer.valueOf(0)), false));
				break;
			}
			case arith_div:
			{
				orig_value = SymbolFactory.arith_div(type, loperand, orig_value);
				muta_value = SymbolFactory.arith_div(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
						not_equals(loperand, Integer.valueOf(0)), false));
				break;
			}
			case arith_mod:
			{
				orig_value = SymbolFactory.arith_mod(type, loperand, orig_value);
				muta_value = SymbolFactory.arith_mod(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
						not_equals(loperand, Integer.valueOf(0)), false));
				break;
			}
			case bit_and:
			{
				orig_value = SymbolFactory.bitws_and(type, loperand, orig_value);
				muta_value = SymbolFactory.bitws_and(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
						not_equals(loperand, Integer.valueOf(0)), false));
				break;
			}
			case bit_or:
			{
				orig_value = SymbolFactory.bitws_ior(type, loperand, orig_value);
				muta_value = SymbolFactory.bitws_ior(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
						not_equals(loperand, Integer.valueOf(~0)), false));
				break;
			}
			case bit_xor:
			{
				orig_value = SymbolFactory.bitws_xor(type, loperand, orig_value);
				muta_value = SymbolFactory.bitws_xor(type, loperand, muta_value);
				break;
			}
			case left_shift:
			{
				orig_value = SymbolFactory.bitws_lsh(type, loperand, orig_value);
				muta_value = SymbolFactory.bitws_lsh(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
						not_equals(loperand, Integer.valueOf(0)), false));
				break;
			}
			case righ_shift:
			{
				orig_value = SymbolFactory.bitws_rsh(type, loperand, orig_value);
				muta_value = SymbolFactory.bitws_rsh(type, loperand, muta_value);
				targets.add(AstContextStates.eva_cond(statement, SymbolFactory.
						not_equals(loperand, Integer.valueOf(0)), false));
				break;
			}
			case greater_tn:
			{
				orig_value = SymbolFactory.greater_tn(loperand, orig_value);
				muta_value = SymbolFactory.greater_tn(loperand, muta_value);
				break;
			}
			case greater_eq:
			{
				orig_value = SymbolFactory.greater_eq(loperand, orig_value);
				muta_value = SymbolFactory.greater_eq(loperand, muta_value);
				break;
			}
			case smaller_tn:
			{
				orig_value = SymbolFactory.smaller_tn(loperand, orig_value);
				muta_value = SymbolFactory.smaller_tn(loperand, muta_value);
				break;
			}
			case smaller_eq:
			{
				orig_value = SymbolFactory.smaller_eq(loperand, orig_value);
				muta_value = SymbolFactory.smaller_eq(loperand, muta_value);
				break;
			}
			case equal_with:
			{
				orig_value = SymbolFactory.equal_with(loperand, orig_value);
				muta_value = SymbolFactory.equal_with(loperand, muta_value);
				break;
			}
			case not_equals:
			{
				orig_value = SymbolFactory.not_equals(loperand, orig_value);
				muta_value = SymbolFactory.not_equals(loperand, muta_value);
				break;
			}
			default:	throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	void	ext_set_ivalue(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstExpression source = (AstExpression) parent.get_ast_source();
		CType type = source.get_value_type();
		
		if(source instanceof AstIncreUnaryExpression) {
			switch(((AstIncreUnaryExpression) source).get_operator().get_operator()) {
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
			default:	throw new IllegalArgumentException("Unsupport: " + source.generate_code());
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstIncrePostfixExpression) {
			switch(((AstIncrePostfixExpression) source).get_operator().get_operator()) {
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
			default:	throw new IllegalArgumentException("Unsupport: " + source.generate_code());
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + source);
		}
	}
	private	void	ext_set_lvalue(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstBinaryExpression) {
			AstExpression roperand = ((AstBinaryExpression) source).get_roperand();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			CType type = ((AstBinaryExpression) source).get_value_type();
			
			switch(operator) {
			case assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, roperand);
				muta_value = SymbolFactory.exp_assign(muta_value, roperand);
				break;
			}
			case arith_add_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_add(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_add(type, muta_value, roperand));
				break;
			}
			case arith_sub_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_sub(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_sub(type, muta_value, roperand));
				break;
			}
			case arith_mul_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_mul(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_mul(type, muta_value, roperand));
				break;
			}
			case arith_div_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_div(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_div(type, muta_value, roperand));
				break;
			}
			case arith_mod_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.arith_mod(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.arith_mod(type, muta_value, roperand));
				break;
			}
			case bit_and_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.bitws_and(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.bitws_and(type, muta_value, roperand));
				break;
			}
			case bit_or_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.bitws_ior(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.bitws_ior(type, muta_value, roperand));
				break;
			}
			case bit_xor_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.bitws_xor(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.bitws_xor(type, muta_value, roperand));
				break;
			}
			case left_shift_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.bitws_lsh(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.bitws_lsh(type, muta_value, roperand));
				break;
			}
			case righ_shift_assign:
			{
				orig_value = SymbolFactory.exp_assign(orig_value, SymbolFactory.bitws_rsh(type, orig_value, roperand));
				muta_value = SymbolFactory.exp_assign(muta_value, SymbolFactory.bitws_rsh(type, muta_value, roperand));
				break;
			}
			default:	
			{
				throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			}
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	void	ext_set_rvalue(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstNode source = parent.get_ast_source();
		if(source instanceof AstReturnStatement) {
			orig_value = SymbolFactory.imp_assign(((AstReturnStatement) source).get_return(), orig_value);
			muta_value = SymbolFactory.imp_assign(((AstReturnStatement) source).get_return(), muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstInitDeclarator) {
			orig_value = SymbolFactory.imp_assign(((AstInitDeclarator) source).get_declarator(), orig_value);
			muta_value = SymbolFactory.imp_assign(((AstInitDeclarator) source).get_declarator(), muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstBinaryExpression) {
			AstExpression loperand = ((AstBinaryExpression) source).get_loperand();
			CType type = ((AstBinaryExpression) source).get_value_type();
			COperator operator = ((AstBinaryExpression) source).get_operator().get_operator();
			
			switch(operator) {
			case assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, orig_value);
				muta_value = SymbolFactory.exp_assign(loperand, muta_value);
				break;
			}
			case arith_add_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_add(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_add(type, loperand, muta_value));
				break;
			}
			case arith_sub_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_sub(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_sub(type, loperand, muta_value));
				break;
			}
			case arith_mul_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mul(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mul(type, loperand, muta_value));
				break;
			}
			case arith_div_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_div(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_div(type, loperand, muta_value));
				break;
			}
			case arith_mod_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mod(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.arith_mod(type, loperand, muta_value));
				break;
			}
			case bit_and_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_and(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_and(type, loperand, muta_value));
				break;
			}
			case bit_or_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_ior(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_ior(type, loperand, muta_value));
				break;
			}
			case bit_xor_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_xor(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_xor(type, loperand, muta_value));
				break;
			}
			case left_shift_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_lsh(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_lsh(type, loperand, muta_value));
				break;
			}
			case righ_shift_assign:
			{
				orig_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_rsh(type, loperand, orig_value));
				muta_value = SymbolFactory.exp_assign(loperand, SymbolFactory.bitws_rsh(type, loperand, muta_value));
				break;
			}
			default:	throw new IllegalArgumentException("Invalid operator: null");
			}
			
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	void	ext_set_address(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent(); AstNode source = parent.get_ast_source();
		if(source instanceof AstArrayExpression) {
			AstExpression index = ((AstArrayExpression) source).get_dimension_expression();
			orig_value = SymbolFactory.arith_add(orig_value.get_data_type(), orig_value, index);
			muta_value = SymbolFactory.arith_add(orig_value.get_data_type(), muta_value, index);
			orig_value = SymbolFactory.dereference(orig_value);
			muta_value = SymbolFactory.dereference(muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else if(source instanceof AstPointUnaryExpression) {
			orig_value = SymbolFactory.dereference(orig_value);
			muta_value = SymbolFactory.dereference(muta_value);
			targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source.generate_code());
		}
	}
	private	void	ext_set_index(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent(); 
		AstArrayExpression source = (AstArrayExpression) parent.get_ast_source();
		AstExpression array = source.get_array_expression();
		orig_value = SymbolFactory.arith_add(array.get_value_type(), array, orig_value);
		muta_value = SymbolFactory.arith_add(array.get_value_type(), array, muta_value);
		orig_value = SymbolFactory.dereference(orig_value);
		muta_value = SymbolFactory.dereference(muta_value);
		targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
	}
	private	void	ext_set_element(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		List<Object> orig_elements = new ArrayList<Object>();
		List<Object> muta_elements = new ArrayList<Object>();
		AstCirNode parent = child.get_parent();
		
		for(int k = 0; k < parent.number_of_children(); k++) {
			if(child == parent.get_child(k)) {
				orig_elements.add(orig_value); 
				muta_elements.add(muta_value);
			}
			else {
				orig_elements.add(parent.get_child(k).get_ast_source());
				muta_elements.add(parent.get_child(k).get_ast_source());
			}
		}
		
		targets.add(AstContextStates.set_expr(parent, 
				SymbolFactory.initializer_list(orig_elements), 
				SymbolFactory.initializer_list(muta_elements)));
	}
	private	void	ext_set_argument(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception {
		AstCirNode parent = child.get_parent();
		AstCirNode callee = parent.get_child(0);
		List<Object> orig_arguments = new ArrayList<Object>();
		List<Object> muta_arguments = new ArrayList<Object>();
		
		for(int k = 1; k < parent.number_of_children(); k++) {
			if(child == parent.get_child(k)) {
				orig_arguments.add(orig_value);
				muta_arguments.add(muta_value);
			}
			else {
				orig_arguments.add(parent.get_child(k).get_ast_source());
				muta_arguments.add(parent.get_child(k).get_ast_source());
			}
		}
		
		orig_value = SymbolFactory.call_expression(callee.get_ast_source(), orig_arguments);
		muta_value = SymbolFactory.call_expression(callee.get_ast_source(), muta_arguments);
		targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
	}
	private	void	ext_set_fbody(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception { 
		AstCirNode parent = child.get_parent();
		AstFieldExpression source = (AstFieldExpression) parent.get_ast_source();
		orig_value = SymbolFactory.field_expression(orig_value, source.get_field().get_name());
		muta_value = SymbolFactory.field_expression(muta_value, source.get_field().get_name());
		targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
	}
	private	void	ext_set_callee(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception { 
		AstCirNode parent = child.get_parent();
		List<Object> arguments = new ArrayList<Object>();
		for(int k = 1; k < parent.number_of_children(); k++) {
			arguments.add(parent.get_child(k).get_ast_source());
		}
		orig_value = SymbolFactory.call_expression(orig_value, arguments);
		muta_value = SymbolFactory.call_expression(muta_value, arguments);
		targets.add(AstContextStates.set_expr(parent, orig_value, muta_value));
	}
	private	void	ext_set_evaluate(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception { }
	private	void	ext_set_n_condition(AstCirNode child, SymbolExpression orig_value, 
			SymbolExpression muta_value, Collection<AstContextState> targets) throws Exception { }
	
}
