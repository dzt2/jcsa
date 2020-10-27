package com.jcsa.jcmutest.mutant.cir2mutant.graph;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.CirStateErrorWord;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirTrapError;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class CirMutationResult {
	
	/* definitions */
	/** the mutation in C-intermediate code being tested **/
	private CirMutationTreeNode tree_node;
	/** the times that the faulty statement was executed **/
	private int execution_times;
	/** the times that the constraints are not satisfied **/
	private int rejections_of_constraints;
	/** the times that the constraints are satisfied **/
	private int acceptions_of_constraints;
	/** the times that the state errors are not infected **/
	private int rejections_of_state_errors;
	/** the times that the state errors are infected **/
	private int acceptions_of_state_errors;
	/** the set of words to describe the features hold by concrete state errors infected during testing **/
	private List<CirStateErrorWord> error_words;
	
	/* constructor */
	/**
	 * create an empty result to record the execution records during testing.
	 * @param cir_mutation
	 * @throws IllegalArgumentException
	 */
	protected CirMutationResult(CirMutationTreeNode tree_node) throws IllegalArgumentException {
		if(tree_node == null)
			throw new IllegalArgumentException("Invalid tree_node as null");
		else {
			this.tree_node = tree_node;
			this.execution_times = 0;
			this.rejections_of_constraints = 0;
			this.acceptions_of_constraints = 0;
			this.rejections_of_state_errors = 0;
			this.acceptions_of_state_errors = 0;
			this.error_words = new ArrayList<CirStateErrorWord>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation in C-intermediate code being tested
	 */
	public CirMutationTreeNode get_tree_node() { return this.tree_node; }
	/**
	 * @return whether the faulty statement is executed during testing
	 */
	public boolean is_executed() { return this.execution_times > 0; }
	/**
	 * @return the times that the faulty statement was executed in testing
	 */
	public int get_execution_times() { return this.execution_times; }
	/**
	 * @return whether the constraint has ever been satisfied during testing
	 */
	public boolean is_constraint_satisfied() { return this.acceptions_of_constraints > 0; }
	/**
	 * @return the times that the constraints are satisfied during testing
	 */
	public int get_constraint_acceptions() { return this.acceptions_of_constraints; }
	/**
	 * @return the times that the constraints are NOT satisfied during testing
	 */
	public int get_constraint_rejections() { return this.rejections_of_constraints; }
	/**
	 * @return how many times that the constraint is NOT evaluated during testing.
	 */
	public int get_constraint_ignorances() { return this.execution_times - this.acceptions_of_constraints - this.rejections_of_constraints; }
	/**
	 * @return whether the state error has ever been infected during testing.
	 */
	public boolean is_state_error_infected() { return this.acceptions_of_state_errors > 0; }
	/**
	 * @return the times that the state errors are infected during testing
	 */
	public int get_state_error_acceptions() { return this.acceptions_of_state_errors; }
	/**
	 * @return the times that the state errors are NOT infected during testing
	 */
	public int get_state_error_rejections() { return this.rejections_of_state_errors; }
	/**
	 * @return how many times that the state error is NOT evaluated during testing.
	 */
	public int get_state_error_ignorances() { return this.execution_times - this.acceptions_of_state_errors - this.rejections_of_state_errors; }
	/**
	 * @return the set of words to describe the features hold by concrete state errors infected during testing
	 */
	public Iterable<CirStateErrorWord> get_error_words() { return this.error_words; } 
	
	/* setters */
	private static boolean is_boolean_error(CirExpression location) throws Exception {
		CType type = location.get_data_type();
		if(type != null)
			type = CTypeAnalyzer.get_value_type(type);
		else
			return false;
		if(CTypeAnalyzer.is_boolean(type)) {
			return true;
		}
		else {
			CirNode parent = location.get_parent();
			if(parent instanceof CirIfStatement) {
				return location == ((CirIfStatement) parent).get_condition();
			}
			else if(parent instanceof CirCaseStatement) {
				return ((CirCaseStatement) parent).get_condition() == location;
			}
			else {
				return false;
			}
		}
	}
	private static boolean is_numeric_error(CirExpression location) throws Exception {
		CType type = location.get_data_type();
		if(type != null)
			type = CTypeAnalyzer.get_value_type(type);
		else
			return false;
		return CTypeAnalyzer.is_number(type);
	}
	private static boolean is_address_error(CirExpression location) throws Exception {
		CType type = location.get_data_type();
		if(type != null)
			type = CTypeAnalyzer.get_value_type(type);
		else
			return false;
		return CTypeAnalyzer.is_pointer(type);
	}
	private void append_error_word(CirStateErrorWord word) throws IllegalArgumentException {
		if(word == null)
			throw new IllegalArgumentException("Invalid word: null");
		else if(!this.error_words.contains(word))
			this.error_words.add(word);
	}
	
	/* mutation generators */
	/**
	 * append the words that describe the expression | reference | state_value error as given
	 * @param location
	 * @param orig_value
	 * @param muta_value
	 * @throws Exception
	 */
	private void append_error_words(CirExpression location, 
			SymExpression orig_value, SymExpression muta_value) throws Exception {
		if(is_boolean_error(location)) {
			this.append_error_word(CirStateErrorWord.not_bool);
			if(muta_value instanceof SymConstant) {
				if(((SymConstant) muta_value).get_bool()) {
					this.append_error_word(CirStateErrorWord.set_true);
				}
				else {
					this.append_error_word(CirStateErrorWord.set_false);
				}
			}
		}
		else if(is_numeric_error(location)) {
			this.append_error_word(CirStateErrorWord.chg_numb);
			if(muta_value instanceof SymConstant) {
				Object number = ((SymConstant) muta_value).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value > 0) {
						this.append_error_word(CirStateErrorWord.set_pos);
					}
					else if(value < 0) {
						this.append_error_word(CirStateErrorWord.set_neg);
					}
					else {
						this.append_error_word(CirStateErrorWord.set_zro);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value > 0) {
						this.append_error_word(CirStateErrorWord.set_pos);
					}
					else if(value < 0) {
						this.append_error_word(CirStateErrorWord.set_neg);
					}
					else {
						this.append_error_word(CirStateErrorWord.set_zro);
					}
				}
			}
		}
		else if(is_address_error(location)) {
			this.append_error_word(CirStateErrorWord.chg_addr);
			if(muta_value instanceof SymConstant) {
				long number = ((SymConstant) muta_value).get_long();
				if(number != 0) {
					this.append_error_word(CirStateErrorWord.set_invalid);
				}
				else {
					this.append_error_word(CirStateErrorWord.set_nullptr);
				}
			}
		}
		else {
			this.append_error_word(CirStateErrorWord.chg_bytes);
		}
		
		if(orig_value instanceof SymConstant && muta_value instanceof SymConstant) {
			Object lnumber = ((SymConstant) orig_value).get_number();
			Object rnumber = ((SymConstant) muta_value).get_number();
			
			if(lnumber instanceof Long) {
				long x = ((Long) lnumber).longValue();
				if(rnumber instanceof Long) {
					long y = ((Long) rnumber).longValue();
					/* add words */
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
				else {
					double y = ((Double) rnumber).doubleValue();
					/* add words */
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
			}
			else {
				double x = ((Double) lnumber).doubleValue();
				if(rnumber instanceof Long) {
					long y = ((Long) rnumber).longValue();
					/* add words */
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
				else {
					double y = ((Double) rnumber).doubleValue();
					/* add words */
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
			}
		}
		
		if(is_numeric_error(location) || is_address_error(location)) {
			SymExpression difference = SymFactory.
					arith_sub(location.get_data_type(), muta_value, orig_value);
			difference = SymEvaluator.evaluate_on(difference, null);
			if(difference instanceof SymConstant) {
				Object number = ((SymConstant) difference).get_number();
				if(number instanceof Long) {
					long x = ((Long) number).longValue();
					if(x > 0)
						this.append_error_word(CirStateErrorWord.inc_value);
					else if(x < 0)
						this.append_error_word(CirStateErrorWord.dec_value);
				}
				else {
					double x = ((Double) number).doubleValue();
					if(x > 0)
						this.append_error_word(CirStateErrorWord.inc_value);
					else if(x < 0)
						this.append_error_word(CirStateErrorWord.dec_value);
				}
			}
		}
	}
	/**
	 * append the words to describe the features hold by state error
	 * which is concrete and evaluated during testing.
	 * @param state_error
	 * @throws Exception
	 */
	private void append_error_words(CirStateError state_error) throws Exception {
		Boolean validate = state_error.validate(null);
		if(validate == null || validate.booleanValue()) {
			if(state_error instanceof CirFlowError) {
				this.append_error_word(CirStateErrorWord.chg_flow);
			}
			else if(state_error instanceof CirTrapError) {
				this.append_error_word(CirStateErrorWord.trapping);
			}
			else if(state_error instanceof CirExpressionError) {
				this.append_error_words(
						((CirExpressionError) state_error).get_expression(), 
						((CirExpressionError) state_error).get_original_value(), 
						((CirExpressionError) state_error).get_mutation_value());
			}
			else if(state_error instanceof CirReferenceError) {
				this.append_error_words(
						((CirReferenceError) state_error).get_reference(), 
						((CirReferenceError) state_error).get_original_value(), 
						((CirReferenceError) state_error).get_mutation_value());
			}
			else if(state_error instanceof CirStateValueError) {
				this.append_error_words(
						((CirStateValueError) state_error).get_reference(), 
						((CirStateValueError) state_error).get_original_value(), 
						((CirStateValueError) state_error).get_mutation_value());
			}
			else {
				throw new IllegalArgumentException(state_error.toString());
			}
		}
	}
	/**
	 * append the concrete mutation evaluated during testing.
	 * @param conc_mutation
	 * @throws Exception
	 */
	protected void append_concrete_mutation(CirMutation conc_mutation) throws Exception {
		/* 1. increase the execution times */ this.execution_times++;
		
		/* 2. determine the satisfiable and infection counters */
		Boolean satisfiable = conc_mutation.get_constraint().validate(null);
		Boolean infectable = conc_mutation.get_state_error().validate(null);
		if(satisfiable == null) {
			if(infectable == null) { 
				/* (Null, Null) */ 
			}
			else if(infectable.booleanValue()) {
				/* (Null, True) --> (Null, Null) */
			}
			else {
				/* (Null, False) */
				this.rejections_of_state_errors++;
			}
		}
		else if(satisfiable.booleanValue()) {
			if(infectable == null) {
				/* (True, Null) */
				this.acceptions_of_constraints++;
			}
			else if(infectable.booleanValue()) {
				/* (True, True) */
				this.acceptions_of_constraints++;
				this.acceptions_of_state_errors++;
			}
			else {
				/* (True, False) */
				this.acceptions_of_constraints++;
				this.rejections_of_state_errors++;
			}
		}
		else {
			/* (False, Any?) --> (False, False) */
			this.rejections_of_constraints++;
			this.rejections_of_state_errors++;
		}
		
		/* 3. record the feature words for concrete error */
		this.append_error_words(conc_mutation.get_state_error());
	}
	/**
	 * accumulate the counters in status to this one
	 * @param status
	 * @throws Exception
	 */
	public void accumulate_mutation_status(CirMutationResult status) throws Exception {
		if(status.tree_node != this.tree_node)
			throw new IllegalArgumentException("Tree-Node conflicted.");
		else {
			this.execution_times += status.execution_times;
			this.acceptions_of_constraints += status.acceptions_of_constraints;
			this.acceptions_of_state_errors += status.acceptions_of_state_errors;
			this.rejections_of_constraints += status.rejections_of_state_errors;
			this.rejections_of_state_errors += status.rejections_of_state_errors;
			for(CirStateErrorWord word : status.error_words) {
				this.append_error_word(word);
			}
		}
	}
	
	
}
