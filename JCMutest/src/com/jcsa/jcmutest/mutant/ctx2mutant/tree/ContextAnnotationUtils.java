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
import com.jcsa.jcparse.lang.program.AstCirTree;
import com.jcsa.jcparse.lang.program.types.AstCirParChild;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
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
		int maximal_times = state.get_maximal_times();
		annotations.add(ContextAnnotation.cov_time(statement, minimal_times, maximal_times));
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
		Object condition = state.get_condition(); boolean must_need = state.is_must();
		annotations.add(ContextAnnotation.eva_cond(statement, condition, must_need));
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
		AstCirNode muta_next = program.get_tree_node(state.get_mutation_next_ID());
		AstCirNode orig_next = program.get_tree_node(state.get_original_next_ID());
		annotations.add(ContextAnnotation.set_flow(statement, orig_next, muta_next));
	}
	
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_set_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<ContextAnnotation> annotations) throws Exception {
		annotations.add(ContextAnnotation.set_expr(expression, orig_value, muta_value));
		if(SymbolFactory.is_bool(orig_value)) {
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_bool().booleanValue()) {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.true_value));
				}
				else {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.fals_value));
				}
			}
			annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.bool_value));
		}
		else if(SymbolFactory.is_usig(orig_value)) {
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_long() != 0) {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.post_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nzro_value));
				}
				else {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.zero_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.npos_value));
				}
			}
			annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nneg_value));
			annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.numb_value));
		}
		else if(SymbolFactory.is_numb(orig_value)) {
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_long() > 0) {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.post_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nneg_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nzro_value));
				}
				else if(((SymbolConstant) muta_value).get_long() < 0) {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.negt_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.npos_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nzro_value));
				}
				else {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.zero_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.npos_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nneg_value));
				}
			}
			annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.numb_value));
		}
		else if(SymbolFactory.is_real(orig_value)) {
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_double() > 0) {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.post_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nneg_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nzro_value));
				}
				else if(((SymbolConstant) muta_value).get_double() < 0) {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.negt_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.npos_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nzro_value));
				}
				else {
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.zero_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.npos_value));
					annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.nneg_value));
				}
			}
			annotations.add(ContextAnnotation.set_expr(expression, orig_value, ContextMutation.numb_value));
		}
		else { /* none of the annotation is created */ }
	}
	
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_inc_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<ContextAnnotation> annotations) throws Exception {
		if(SymbolFactory.is_numb(orig_value) || SymbolFactory.is_real(orig_value)) {
			SymbolExpression difference = SymbolFactory.arith_sub(orig_value.get_data_type(), muta_value, orig_value);
			difference = ContextMutation.evaluate(difference, null, null);
			if(difference instanceof SymbolConstant) {
				annotations.add(ContextAnnotation.inc_expr(expression, orig_value, difference));
				Object number = ((SymbolConstant) difference).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value > 0) {
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.post_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.nneg_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.nzro_value));
					}
					else {
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.negt_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.npos_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.nzro_value));
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value > 0) {
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.post_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.nneg_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.nzro_value));
					}
					else {
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.negt_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.npos_value));
						annotations.add(ContextAnnotation.inc_expr(expression, orig_value, ContextMutation.nzro_value));
					}
				}
			}
		}
	}
	
	/**
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void gen_xor_expr(AstCirNode expression, 
			SymbolExpression orig_value, SymbolExpression muta_value, 
			Collection<ContextAnnotation> annotations) throws Exception {
		if(SymbolFactory.is_numb(orig_value) || SymbolFactory.is_real(orig_value)) {
			SymbolExpression difference = SymbolFactory.bitws_xor(orig_value.get_data_type(), muta_value, orig_value);
			difference = ContextMutation.evaluate(difference, null, null);
			if(difference instanceof SymbolConstant) {
				annotations.add(ContextAnnotation.xor_expr(expression, orig_value, difference));
				long value = ((SymbolConstant) difference).get_long().longValue();
				if(value > 0) {
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, ContextMutation.post_value));
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, ContextMutation.nneg_value));
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, ContextMutation.nzro_value));
				}
				else {
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, ContextMutation.negt_value));
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, ContextMutation.npos_value));
					annotations.add(ContextAnnotation.xor_expr(expression, orig_value, ContextMutation.nzro_value));
				}
			}
		}
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
		orig_value = ContextMutation.evaluate(orig_value, null, orig_context);
		muta_value = ContextMutation.evaluate(muta_value, null, muta_context);
		
		/* 2. normal analysis */
		if(ContextMutation.has_trap_value(muta_value)) {
			annotations.add(ContextAnnotation.trp_stmt(expression)); 
			return;
		}
		else if(expression.get_child_type() == AstCirParChild.evaluate ||
				expression.get_child_type() == AstCirParChild.execute ||
				expression.get_child_type() == AstCirParChild.tbranch ||
				expression.get_child_type() == AstCirParChild.fbranch) {
			/* ignore the evaluation result because it is never used... */
		}
		else {
			if(!orig_value.equals(muta_value)) {
				this.gen_set_expr(expression, orig_value, muta_value, annotations);
				this.gen_inc_expr(expression, orig_value, muta_value, annotations);
				this.gen_xor_expr(expression, orig_value, muta_value, annotations);
			}
		}
		
		/* 3. state mutation saved */
		AstCirNode statement = expression.statement_of();
		for(SymbolExpression identifier : muta_context.get_keys()) {
			if(orig_context.has_value(identifier)) {
				SymbolExpression orig_expr = orig_context.get_value(identifier);
				SymbolExpression muta_expr = muta_context.get_value(identifier);
				if(!orig_expr.equals(muta_expr)) {
					this.gen_set_expr(statement, identifier, muta_value, annotations);
				}
			}
		}
		
		/* 4. for equivalence checking */
		if(annotations.isEmpty()) {
			while(!expression.get_parent().is_module_node()) {
				expression = expression.get_parent();
			}
			annotations.add(ContextAnnotation.eva_cond(expression, Boolean.FALSE, false));
		}
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
