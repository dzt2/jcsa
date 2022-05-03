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
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
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
		AstCirNode expression = state.get_expression();
		SymbolExpression orig_value = state.get_original_value();
		SymbolExpression muta_value = state.get_mutation_value();
		SymbolContext orig_context = SymbolContext.new_context();
		SymbolContext muta_context = SymbolContext.new_context();
		orig_value = ContextMutation.evaluate(orig_value, null, orig_context);
		muta_value = ContextMutation.evaluate(muta_value, null, muta_context);
		
		
		
		
		
		
		
	}
	
}
