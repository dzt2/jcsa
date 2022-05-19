package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstBlockErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConstraintState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstCoverTimesState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstFlowsErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstSeedMutantState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstValueErrorState;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.types.AstCirNodeType;
import com.jcsa.jcparse.lang.program.types.AstCirParChild;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;

final class ContextAnnotationUtils {
	
	/** private constructe singleton **/ private ContextAnnotationUtils() { }
	static final ContextAnnotationUtils utils = new ContextAnnotationUtils();
	
	/**
	 * It appends the annotations representing the state to the outputs
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	protected static void extend(AstContextState state, Collection<ContextAnnotation> annotations) throws Exception {
		utils.ext(state, annotations);
	}
	
	/**
	 * It appends the annotations representing the state to the outputs
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext(AstContextState state, Collection<ContextAnnotation> annotations) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations");
		}
		else if(state instanceof AstConstraintState) {
			this.ext_eva_cond((AstConstraintState) state, annotations);
		}
		else if(state instanceof AstCoverTimesState) {
			this.ext_cov_time((AstCoverTimesState) state, annotations);
		}
		else if(state instanceof AstSeedMutantState) {
			this.ext_sed_muta((AstSeedMutantState) state, annotations);
		}
		else if(state instanceof AstBlockErrorState) {
			this.ext_set_stmt((AstBlockErrorState) state, annotations);
		}
		else if(state instanceof AstFlowsErrorState) {
			this.ext_set_flow((AstFlowsErrorState) state, annotations);
		}
		else if(state instanceof AstValueErrorState) {
			this.ext_set_expr((AstValueErrorState) state, annotations);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state);
		}
	}
	
	/**
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext_cov_time(AstCoverTimesState state, Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location();
		int max_times = state.get_minimal_times();
		for(int times = 1; times < max_times; times = times * 2) {
			annotations.add(ContextAnnotation.cov_time(statement, times, Integer.MAX_VALUE));
		}
		annotations.add(ContextAnnotation.cov_time(statement, max_times, Integer.MAX_VALUE));
	}
	
	/**
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext_eva_cond(AstConstraintState state, Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location();
		SymbolExpression condition = state.get_condition();
		boolean must_need = state.is_must();
		condition = ContextMutation.evaluate(condition, null, null);
		
		if(ContextMutation.has_trap_value(condition)) {
			annotations.add(ContextAnnotation.eva_cond(statement, Boolean.TRUE, must_need));
		}
		else if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				annotations.add(ContextAnnotation.eva_cond(statement, Boolean.TRUE, must_need));
			}
			else {
				while(!statement.get_parent().is_module_node()) {
					statement = statement.get_parent();
				}
				annotations.add(ContextAnnotation.eva_cond(statement, Boolean.FALSE, must_need));
			}
		}
		else {
			annotations.add(ContextAnnotation.eva_cond(statement, condition, must_need));
		}
	}
	
	/**
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext_sed_muta(AstSeedMutantState state, Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode location = state.get_location();
		annotations.add(ContextAnnotation.sed_muta(location, state.get_mutant_ID(), state.get_operator()));
		annotations.add(ContextAnnotation.cov_time(location.statement_of(), 1, Integer.MAX_VALUE));
	}
	
	/**
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext_set_stmt(AstBlockErrorState state, Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location();
		if(state.is_trapping_exception()) {
			annotations.add(ContextAnnotation.trp_stmt(statement));
		}
		else {
			boolean muta_exec = state.is_mutation_executed();
			annotations.add(ContextAnnotation.set_stmt(statement, muta_exec));
			for(AstCirNode child : statement.get_children()) {
				switch(child.get_child_type()) {
				case tbranch:
				case fbranch:
				case execute:	annotations.add(ContextAnnotation.set_stmt(child, muta_exec));
				default:		break;
				}
			}
		}
	}
	
	/**
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext_set_flow(AstFlowsErrorState state, Collection<ContextAnnotation> annotations) throws Exception { }
	
	/**
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void ext_set_expr(AstValueErrorState state, Collection<ContextAnnotation> annotations) throws Exception {
		/* 1. value evaluation */
		AstCirNode expression = state.get_expression();
		SymbolExpression orig_value = state.get_original_value();
		SymbolExpression muta_value = state.get_mutation_value();
		SymbolContext orig_context = SymbolContext.new_context();
		SymbolContext muta_context = SymbolContext.new_context();
		orig_value = ContextMutation.evaluate(orig_value, null, orig_context);
		muta_value = ContextMutation.evaluate(muta_value, null, muta_context);
		boolean is_top_level = false;
		
		/* 2. trap exception */
		if(ContextMutation.has_trap_value(muta_value)) {
			annotations.add(ContextAnnotation.trp_stmt(expression)); 
		}
		/* 3. state compared */
		else if(expression.get_child_type() == AstCirParChild.execute
				|| expression.get_child_type() == AstCirParChild.evaluate) {
			this.ext_set_states(expression.statement_of(), orig_context, muta_context, annotations);
			is_top_level = true;
		}
		/* 4. state + value compared */
		else if(expression.get_child_type() == AstCirParChild.condition
				|| expression.get_child_type() == AstCirParChild.n_condition) {
			this.ext_set_result(expression, orig_value, muta_value, annotations);
			this.ext_set_states(expression.statement_of(), orig_context, muta_context, annotations);
			is_top_level = true;
		}
		else if(expression.get_child_type() == AstCirParChild.rvalue || 
				expression.get_child_type() == AstCirParChild.lvalue) {
			if(expression.get_parent().get_node_type() == AstCirNodeType.retr_stmt) {
				this.ext_set_result(expression, orig_value, muta_value, annotations);
			}
			else {
				this.ext_set_result(expression, orig_value, muta_value, annotations);
				this.ext_set_states(expression.statement_of(), orig_context, muta_context, annotations);
			}
			is_top_level = true;
		}
		/* 5. value compared */
		else {
			this.ext_set_result(expression, orig_value, muta_value, annotations);
		}
		
		/* 6. equivalence checked */
		if(annotations.isEmpty() && is_top_level) {
			AstCirNode statement = expression.statement_of();
			while(!statement.get_parent().is_module_node()) {
				statement = statement.get_parent();
			}
			annotations.add(ContextAnnotation.eva_cond(statement, Boolean.FALSE, false));
		}
	}
	
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @throws Exception
	 */
	private void ext_set_result(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<ContextAnnotation> annotations) throws Exception {
		CType type = ((AstExpression) expression.get_ast_source()).get_value_type();
		if(!orig_value.equals(muta_value)) {
			annotations.add(ContextAnnotation.set_expr(expression, orig_value, muta_value));
			for(SymbolExpression domain : ContextMutation.get_domains_of(type, muta_value)) {
				annotations.add(ContextAnnotation.set_expr(expression, orig_value, domain));
			}
			
			if(SymbolFactory.is_numb(type) || SymbolFactory.is_real(type)) {
				SymbolExpression difference = SymbolFactory.arith_sub(type, muta_value, orig_value);
				difference = ContextMutation.evaluate(difference, null, null);
				if(difference instanceof SymbolConstant) {
					annotations.add(ContextAnnotation.inc_expr(expression, orig_value, difference));
					for(SymbolExpression domain : ContextMutation.get_domains_of(type, difference)) {
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, domain));
					}
				}
			}
			
			if(SymbolFactory.is_numb(type)) {
				SymbolExpression difference = SymbolFactory.bitws_xor(type, muta_value, orig_value);
				difference = ContextMutation.evaluate(difference, null, null);
				if(difference instanceof SymbolConstant) {
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, difference));
					for(SymbolExpression domain : ContextMutation.get_domains_of(type, difference)) {
						annotations.add(ContextAnnotation.xor_expr(expression, orig_value, domain));
					}
				}
			}
		}
	}
	
	/**
	 * @param statement
	 * @param identifier
	 * @param error_value
	 * @param annotations
	 * @throws Exception
	 */
	private void ext_set_state(AstCirNode statement, SymbolExpression identifier, 
			SymbolExpression orig_value, SymbolExpression muta_value,
			Collection<ContextAnnotation> annotations) throws Exception {
		CType type = identifier.get_data_type();
		if(!orig_value.equals(muta_value)) {
			annotations.add(ContextAnnotation.set_refr(statement, identifier, muta_value));
			for(SymbolExpression domain : ContextMutation.get_domains_of(type, muta_value)) {
				annotations.add(ContextAnnotation.set_refr(statement, identifier, domain));
			}
			
			if(SymbolFactory.is_numb(type) || SymbolFactory.is_real(type)) {
				SymbolExpression difference = SymbolFactory.arith_sub(type, muta_value, orig_value);
				difference = ContextMutation.evaluate(difference, null, null);
				if(difference instanceof SymbolConstant) {
					annotations.add(ContextAnnotation.inc_refr(statement, identifier, difference));
					for(SymbolExpression domain : ContextMutation.get_domains_of(type, difference)) {
						annotations.add(ContextAnnotation.inc_refr(statement, identifier, domain));
					}
				}
			}
			
			if(SymbolFactory.is_numb(type)) {
				SymbolExpression difference = SymbolFactory.bitws_xor(type, muta_value, orig_value);
				difference = ContextMutation.evaluate(difference, null, null);
				if(difference instanceof SymbolConstant) {
					annotations.add(ContextAnnotation.xor_refr(statement, identifier, difference));
					for(SymbolExpression domain : ContextMutation.get_domains_of(type, difference)) {
						annotations.add(ContextAnnotation.xor_refr(statement, identifier, domain));
					}
				}
			}
		}
	}
	
	/**
	 * @param statement
	 * @param orig_context
	 * @param ou_context
	 * @param annotations
	 * @throws Exception
	 */
	private void ext_set_states(AstCirNode statement, SymbolContext orig_context, 
			SymbolContext muta_context, Collection<ContextAnnotation> annotations) throws Exception {
		for(SymbolExpression identifier : muta_context.get_keys()) {
			SymbolExpression muta_state = muta_context.get_value(identifier);
			SymbolExpression orig_state;
			if(orig_context.has_value(identifier)) {
				orig_state = orig_context.get_value(identifier);
			}
			else {
				orig_state = identifier;
			}
			this.ext_set_state(statement, identifier, orig_state, muta_state, annotations);
		}
		/* 
		for(SymbolExpression identifier : orig_context.get_keys()) {
			SymbolExpression orig_state = orig_context.get_value(identifier);
			if(!muta_context.has_value(identifier)) {
				SymbolExpression muta_state = identifier;
				this.ext_set_state(statement, identifier, orig_state, muta_state, annotations);
			}
		}
		*/
	}
	
}
