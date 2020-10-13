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
 * 	It records the status of detecting mutation for three elements 
 * 	in <code>CirMutation</code>:<br>
 * 	
 * 	(1)	true|false|null --> CirMutation.statement 	[reachable analysis]<br>
 * 	(2) true|false|null --> CirMutation.constraint	[satisfiable analysis]<br>
 * 	(3) true|false|null	-->	CirMutation.state_error	[influence analysis]<br>
 * 	(4) word+			-->	CirMutation.state_error	[feature description]<br>
 * 
 * @author yukimula
 *
 */
public class CirDetectionStatus {
	
	/* definitions */
	/** the mutation being described **/
	private CirMutation cir_mutation;
	/** whether the faulty statement was executed or not, set as
	 *  null if none of tests is used for reachability analysis. **/
	private Boolean reachable;
	/** whether the constraint for infecting the state error has
	 *  been satisfied or null if the result is undecidable. **/
	private Boolean satisfiable;
	/** whether the state error can influence on the final state
	 *  or null if the result is undecidable. **/
	private Boolean influencable;
	/** words used to describe the state error under tested **/
	private List<CirStateErrorWord> error_words;
	
	/* constructor */
	/**
	 * create an empty status for describing the mutation under test
	 * @param cir_mutation
	 * @throws IllegalArgumentException
	 */
	protected CirDetectionStatus(CirMutation cir_mutation) throws IllegalArgumentException {
		if(cir_mutation == null)
			throw new IllegalArgumentException("Invalid cir_mutation: null");
		else {
			this.cir_mutation = cir_mutation;
			this.reachable = null;
			this.satisfiable = null;
			this.influencable = null;
			this.error_words = new ArrayList<CirStateErrorWord>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutation being described for its status
	 */
	public CirMutation get_cir_mutation() { return this.cir_mutation; }
	/**
	 * @return whether the faulty statement was executed or not, set as
	 *  	   null if none of tests is used for reachability analysis.
	 */
	public Boolean get_reachable() { return this.reachable; }
	/**
	 * @return whether the constraint for infecting the state error has
	 * 		   been satisfied or null if the result is undecidable.
	 */
	public Boolean get_satisfiable() { return this.satisfiable; }
	/**
	 * @return whether the state error can influence on the final state
	 *  	   or null if the result is undecidable.
	 */
	public Boolean get_influencable() { return this.influencable; }
	/**
	 * @return words used to describe the state error under tested
	 */
	public Iterable<CirStateErrorWord> get_error_words() { return this.error_words; }
	
	/* implication */
	/**
	 * @return the status is only analyzed using static method without any
	 * 		   execution path or dynamic analysis.
	 */
	public boolean unknown_reached() { return this.reachable == null; }
	/**
	 * @return the statement was reached during testing
	 */
	public boolean is_reached() { 
		return this.reachable != null && this.reachable.booleanValue();
	}
	/**
	 * @return the statement was not reached during testing
	 */
	public boolean not_reached() {
		return this.reachable != null && !this.reachable.booleanValue();
	}
	/**
	 * @return the constraint was satisfied during testing
	 */
	public boolean is_satisfied() {
		return this.satisfiable != null && this.satisfiable.booleanValue();
	}
	/**
	 * @return the constraint was not satisfied during testing
	 */
	public boolean not_satisfied() {
		return this.satisfiable != null && !this.satisfiable.booleanValue();
	}
	/**
	 * @return the satisfiability of the constraint is unknown
	 * 		   (either not tested or undecidable)
	 */
	public boolean unknown_satisfied() {
		return this.satisfiable == null;
	}
	/**
	 * @return the state error influences on program state in testing
	 */
	public boolean is_infected() {
		return this.influencable != null && this.influencable.booleanValue();
	}
	/**
	 * @return the state error does not influence on state in testing
	 */
	public boolean not_infected() {
		return this.influencable != null && this.influencable.booleanValue();
	}
	/**
	 * @return the influencability of the error is unknown, either not
	 * 		   tested or undecidable.
	 */
	public boolean unknown_infected() {
		return this.influencable == null;
	}
	
	/* setters */
	protected void set(Boolean reachable, Boolean 
			satisfiable, Boolean influencable) {
		this.reachable = reachable;
		this.satisfiable = satisfiable;
		this.influencable = influencable;
	}
	
	/* feature generations */
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
	private void generate_error_words(CirExpression location, 
			SymExpression orig_value, SymExpression muta_value) throws Exception {
		this.error_words.clear();
		
		if(this.is_boolean_error(location)) {
			this.error_words.add(CirStateErrorWord.not_bool);
			if(muta_value instanceof SymConstant) {
				if(((SymConstant) muta_value).get_bool()) {
					this.error_words.add(CirStateErrorWord.set_true);
				}
				else {
					this.error_words.add(CirStateErrorWord.set_false);
				}
			}
		}
		else if(this.is_numeric_error(location)) {
			this.error_words.add(CirStateErrorWord.chg_numb);
			if(muta_value instanceof SymConstant) {
				Object number = ((SymConstant) muta_value).get_number();
				if(number instanceof Long) {
					long value = ((Long) number).longValue();
					if(value > 0) {
						this.error_words.add(CirStateErrorWord.set_pos);
					}
					else if(value < 0) {
						this.error_words.add(CirStateErrorWord.set_neg);
					}
					else {
						this.error_words.add(CirStateErrorWord.set_zro);
					}
				}
				else {
					double value = ((Double) number).doubleValue();
					if(value > 0) {
						this.error_words.add(CirStateErrorWord.set_pos);
					}
					else if(value < 0) {
						this.error_words.add(CirStateErrorWord.set_neg);
					}
					else {
						this.error_words.add(CirStateErrorWord.set_zro);
					}
				}
			}
		}
		else if(this.is_address_error(location)) {
			this.error_words.add(CirStateErrorWord.chg_addr);
			if(muta_value instanceof SymConstant) {
				long number = ((SymConstant) muta_value).get_long();
				if(number != 0) {
					this.error_words.add(CirStateErrorWord.set_invalid);
				}
				else {
					this.error_words.add(CirStateErrorWord.set_nullptr);
				}
			}
		}
		else {
			this.error_words.add(CirStateErrorWord.chg_bytes);
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
						this.error_words.add(CirStateErrorWord.inc_value);
					else if(x < y)
						this.error_words.add(CirStateErrorWord.dec_value);
					if(Math.abs(x) > Math.abs(y))
						this.error_words.add(CirStateErrorWord.ext_value);
					else if(Math.abs(x) < Math.abs(y))
						this.error_words.add(CirStateErrorWord.shk_value);
				}
				else {
					double y = ((Double) rnumber).doubleValue();
					/* add words */
					if(x > y) 
						this.error_words.add(CirStateErrorWord.inc_value);
					else if(x < y)
						this.error_words.add(CirStateErrorWord.dec_value);
					if(Math.abs(x) > Math.abs(y))
						this.error_words.add(CirStateErrorWord.ext_value);
					else if(Math.abs(x) < Math.abs(y))
						this.error_words.add(CirStateErrorWord.shk_value);
				}
			}
			else {
				double x = ((Double) rnumber).doubleValue();
				if(rnumber instanceof Long) {
					long y = ((Long) rnumber).longValue();
					/* add words */
					if(x > y) 
						this.error_words.add(CirStateErrorWord.inc_value);
					else if(x < y)
						this.error_words.add(CirStateErrorWord.dec_value);
					if(Math.abs(x) > Math.abs(y))
						this.error_words.add(CirStateErrorWord.ext_value);
					else if(Math.abs(x) < Math.abs(y))
						this.error_words.add(CirStateErrorWord.shk_value);
				}
				else {
					double y = ((Double) rnumber).doubleValue();
					/* add words */
					if(x > y) 
						this.error_words.add(CirStateErrorWord.inc_value);
					else if(x < y)
						this.error_words.add(CirStateErrorWord.dec_value);
					if(Math.abs(x) > Math.abs(y))
						this.error_words.add(CirStateErrorWord.ext_value);
					else if(Math.abs(x) < Math.abs(y))
						this.error_words.add(CirStateErrorWord.shk_value);
				}
			}
		}
	}
	protected void generate_concrete_words(CirMutation conc_mutation) throws Exception {
		this.error_words.clear();
		
		/* update feature words only when error is valid */
		CirStateError conc_error = conc_mutation.get_state_error();
		Boolean validate = conc_error.validate(null);
		if(validate == null || validate.booleanValue()) {
			if(conc_error instanceof CirFlowError) {
				this.error_words.add(CirStateErrorWord.chg_flow);
			}
			else if(conc_error instanceof CirTrapError) {
				this.error_words.add(CirStateErrorWord.trapping);
			}
			else if(conc_error instanceof CirExpressionError) {
				this.generate_error_words(
						((CirExpressionError) conc_error).get_expression(), 
						((CirExpressionError) conc_error).get_original_value(), 
						((CirExpressionError) conc_error).get_mutation_value());
			}
			else if(conc_error instanceof CirReferenceError) {
				this.generate_error_words(
						((CirReferenceError) conc_error).get_reference(), 
						((CirReferenceError) conc_error).get_original_value(), 
						((CirReferenceError) conc_error).get_mutation_value());
			}
			else if(conc_error instanceof CirStateValueError) {
				this.generate_error_words(
						((CirStateValueError) conc_error).get_reference(), 
						((CirStateValueError) conc_error).get_original_value(), 
						((CirStateValueError) conc_error).get_mutation_value());
			}
			else {
				throw new IllegalArgumentException(conc_error.toString());
			}
		}
		
		return;
	}
	protected void append_concrete_words(CirDetectionStatus status) {
		for(CirStateErrorWord word : status.error_words) {
			if(!this.error_words.contains(word))
				this.error_words.add(word);
		}
	}
	
}
