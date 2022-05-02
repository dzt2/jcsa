package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstBlockErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstConstraintState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstCoverTimesState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstFlowsErrorState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstSeedMutantState;
import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstValueErrorState;
import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.program.AstCirTree;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;

final class ContextAnnotationUtils {
	
	/** singleton mode & constructor **/  private ContextAnnotationUtils() { }
	private static ContextAnnotationUtils utils = new ContextAnnotationUtils();
	
	/**
	 * It generates the set of annotations that represent the input source state
	 * @param program		the program from which the state is described with
	 * @param state			the state from which the annotations are generated
	 * @param annotations	the collection to preserve the created annotations
	 * @throws Exception
	 */
	private void gen(AstCirTree program, AstContextState state, 
			Collection<ContextAnnotation> annotations) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state as: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(state instanceof AstCoverTimesState) {
			this.gen_cov_time(program, (AstCoverTimesState) state, annotations);
		}
		else if(state instanceof AstConstraintState) {
			this.gen_eva_cond(program, (AstConstraintState) state, annotations);
		}
		else if(state instanceof AstSeedMutantState) {
			this.gen_sed_muta(program, (AstSeedMutantState) state, annotations);
		}
		else if(state instanceof AstBlockErrorState) {
			this.gen_set_stmt(program, (AstBlockErrorState) state, annotations);
		}
		else if(state instanceof AstFlowsErrorState) {
			this.gen_set_flow(program, (AstFlowsErrorState) state, annotations);
		}
		else if(state instanceof AstValueErrorState) {
			this.gen_set_expr(program, (AstValueErrorState) state, annotations);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + state.toString());
		}
	}
	
	/**
	 * It generates the annotations from the cov_time
	 * @param program
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void gen_cov_time(AstCirTree program, AstCoverTimesState state, 
			Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location();
		int minimal_times = state.get_minimal_times();
		annotations.add(ContextAnnotation.cov_time(statement, minimal_times));
	}
	
	/**
	 * @param program
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_eva_cond(AstCirTree program, AstConstraintState state,
			Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location(); 
		Object condition = state.get_condition();
		if(state.is_must()) {
			annotations.add(ContextAnnotation.mus_cond(statement, condition));
		}
		else {
			annotations.add(ContextAnnotation.eva_cond(statement, condition));
		}
	}
	
	/**
	 * @param program
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private	void gen_sed_muta(AstCirTree program, AstSeedMutantState state,
			Collection<ContextAnnotation> annotations) throws Exception { }
	
	/**
	 * @param program
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_set_stmt(AstCirTree program, AstBlockErrorState state,
			Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location();
		if(state.is_trapping_exception()) {
			annotations.add(ContextAnnotation.trp_stmt(statement));
		}
		else if(state.is_mutation_executed()) {
			annotations.add(ContextAnnotation.set_stmt(statement, true));
		}
		else {
			annotations.add(ContextAnnotation.set_stmt(statement, false));
		}
	}
	
	/**
	 * @param program
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_set_flow(AstCirTree program, AstFlowsErrorState state,
			Collection<ContextAnnotation> annotations) throws Exception {
		AstCirNode statement = state.get_location();
		AstCirNode next_node = program.get_tree_node(state.get_mutation_next_ID());
		annotations.add(ContextAnnotation.set_flow(statement, next_node));
	}
	
	/**
	 * @param program
	 * @param state
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_set_expr(AstCirTree program, AstValueErrorState state,
			Collection<ContextAnnotation> annotations) throws Exception {
		/* 1. symbolic evaluation and contextual preservation */
		AstCirNode expression = state.get_expression();
		SymbolContext orig_context = SymbolContext.new_context();
		SymbolContext muta_context = SymbolContext.new_context();
		SymbolExpression orig_value = state.get_original_value();
		SymbolExpression muta_value = state.get_mutation_value();
		orig_value = ContextMutations.evaluate(orig_value, null, orig_context);
		muta_value = ContextMutations.evaluate(muta_value, null, muta_context);
		
		/* 2. normal analysis */
		if(ContextMutations.has_trap_value(muta_value)) {
			annotations.add(ContextAnnotation.trp_stmt(expression)); 
			return;
		}
		
		
		
		
		
		
		// TODO implement more methods...
	}
	
	/**
	 * @param node
	 * @param annotations
	 * @throws Exception
	 */
	public static void generate(ContextMutationNode node, 
			Collection<ContextAnnotation> annotations) throws Exception {
		if(node == null) {
			throw new IllegalArgumentException("Invalid node: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations");
		}
		else {
			utils.gen(node.get_tree().get_program(), node.get_state(), annotations);
		}
	}
	
}
