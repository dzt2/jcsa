package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirTrapError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * It records the information about the execution of each CirMutation during
 * its mutant is executed against each test input.
 * 
 * @author yukimula
 *
 */
public class CirMutationStatus {
	
	/* definitions */
	/** the mutation in C-intermediate code being tested **/
	private CirMutation cir_mutation;
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
	protected CirMutationStatus(CirMutation cir_mutation) throws IllegalArgumentException {
		if(cir_mutation == null)
			throw new IllegalArgumentException("Invalid cir_mutation as null");
		else {
			this.cir_mutation = cir_mutation;
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
	public CirMutation get_cir_mutation() { return this.cir_mutation; }
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
	private boolean is_boolean_error(CirExpression location) throws Exception {
		CType type = location.get_data_type();
		type = CTypeAnalyzer.get_value_type(type);
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
	private boolean is_numeric_error(CirExpression location) throws Exception {
		CType type = location.get_data_type();
		type = CTypeAnalyzer.get_value_type(type);
		return CTypeAnalyzer.is_number(type);
	}
	private boolean is_address_error(CirExpression location) throws Exception {
		CType type = location.get_data_type();
		type = CTypeAnalyzer.get_value_type(type);
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
		if(this.is_boolean_error(location)) {
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
		else if(this.is_numeric_error(location)) {
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
		else if(this.is_address_error(location)) {
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
					if(x > y) 
						this.append_error_word(CirStateErrorWord.dec_value);
					else if(x < y)
						this.append_error_word(CirStateErrorWord.inc_value);
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
				else {
					double y = ((Double) rnumber).doubleValue();
					/* add words */
					if(x > y) 
						this.append_error_word(CirStateErrorWord.dec_value);
					else if(x < y)
						this.append_error_word(CirStateErrorWord.inc_value);
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
			}
			else {
				double x = ((Double) rnumber).doubleValue();
				if(rnumber instanceof Long) {
					long y = ((Long) rnumber).longValue();
					/* add words */
					if(x > y) 
						this.append_error_word(CirStateErrorWord.dec_value);
					else if(x < y)
						this.append_error_word(CirStateErrorWord.inc_value);
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
				}
				else {
					double y = ((Double) rnumber).doubleValue();
					/* add words */
					if(x > y) 
						this.append_error_word(CirStateErrorWord.dec_value);
					else if(x < y)
						this.append_error_word(CirStateErrorWord.inc_value);
					if(Math.abs(x) > Math.abs(y))
						this.append_error_word(CirStateErrorWord.shk_value);
					else if(Math.abs(x) < Math.abs(y))
						this.append_error_word(CirStateErrorWord.ext_value);
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
	
}
