package com.jcsa.jcmutest.mutant.cir2mutant.model;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorWord;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * It describes the error for state error in a location
 * @author dzt2
 *
 */
public class CirErrorSentence {
	
	/** words that describe the error **/
	private List<CirErrorWord> words;
	/** location where the error occurs **/
	private CirNode location;
	/**
	 * create a sentence to describe the error at specified location
	 * @param location
	 * @throws Exception
	 */
	private CirErrorSentence(CirNode location) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location");
		else {
			this.words = new ArrayList<CirErrorWord>();
			this.location = location;
		}
	}
	
	/* getters */
	/**
	 * @return words that describe the error
	 */
	public Iterable<CirErrorWord> get_words() { return this.words; }
	/**
	 * @return location where the error occurs
	 */
	public CirNode get_location() { return this.location; }
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("cir#" + this.location.get_node_id());
		for(CirErrorWord word : this.words) {
			buffer.append(" ");
			buffer.append(word.toString());
		}
		return buffer.toString();
	}
	
	/* type classifier */
	private static boolean is_void(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_void(type);
	}
	private static boolean is_boolean(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
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
	private static boolean is_integer(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_integer(type);
	}
	private static boolean is_double(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_real(type);
	}
	private static boolean is_pointer(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_pointer(type);
	}
	
	/* expression, sym_expression translator */
	private static CirErrorSentence parse_expr_error(
			CirExpressionError error, CStateContexts contexts) throws Exception {
		CirExpression expression = error.get_expression();
		SymExpression orig_value = error.get_original_value();
		SymExpression muta_value = error.get_mutation_value();
		
		orig_value = SymEvaluator.evaluate_on(orig_value, contexts);
		muta_value = SymEvaluator.evaluate_on(muta_value, contexts);
		if(orig_value.equals(muta_value)) return null;	/* no error */
		
		List<CirErrorWord> words = new ArrayList<CirErrorWord>();
		if(is_void(expression)) {
			return null;
		}
		else if(is_boolean(expression)) {
			if(muta_value instanceof SymConstant) {
				if(((SymConstant) muta_value).get_bool()) {
					words.add(CirErrorWord.set_true);
				}
				else {
					words.add(CirErrorWord.set_false);
				}
			}
			words.add(CirErrorWord.chg_bool);
		}
		else if(is_integer(expression)) {
			if(muta_value instanceof SymConstant) {
				long y = ((SymConstant) muta_value).get_long();
				if(orig_value instanceof SymConstant) {
					long x = ((SymConstant) orig_value).get_long();
					if(y > x) {
						words.add(CirErrorWord.inc_value);
					}
					else if(y < x) {
						words.add(CirErrorWord.dec_value);
					}
					if(Math.abs(y) > Math.abs(x)) {
						words.add(CirErrorWord.ext_value);
					}
					else if(Math.abs(y) < Math.abs(x)) {
						words.add(CirErrorWord.shk_value);
					}
					if(x == -y) {
						words.add(CirErrorWord.neg_numb);
					}
					else if(x == ~y) {
						words.add(CirErrorWord.rsv_numb);
					}
				}
				if(y > 0) {
					words.add(CirErrorWord.set_pos);
				}
				else if(y < 0) {
					words.add(CirErrorWord.set_neg);
				}
				else {
					words.add(CirErrorWord.set_zro);
				}
			}
			words.add(CirErrorWord.chg_numb);
		}
		else if(is_double(expression)) {
			if(muta_value instanceof SymConstant) {
				double y = ((SymConstant) muta_value).get_double();
				if(orig_value instanceof SymConstant) {
					double x = ((SymConstant) orig_value).get_double();
					if(y > x) {
						words.add(CirErrorWord.inc_value);
					}
					else if(y < x) {
						words.add(CirErrorWord.dec_value);
					}
					if(Math.abs(y) > Math.abs(x)) {
						words.add(CirErrorWord.ext_value);
					}
					else if(Math.abs(y) < Math.abs(x)) {
						words.add(CirErrorWord.shk_value);
					}
					if(x == -y) {
						words.add(CirErrorWord.neg_numb);
					}
				}
				if(y > 0) {
					words.add(CirErrorWord.set_pos);
				}
				else if(y < 0) {
					words.add(CirErrorWord.set_neg);
				}
				else {
					words.add(CirErrorWord.set_zro);
				}
			}
			words.add(CirErrorWord.chg_numb);
		}
		else if(is_pointer(expression)) {
			if(muta_value instanceof SymConstant) {
				long y = ((SymConstant) muta_value).get_long();
				if(orig_value instanceof SymConstant) {
					long x = ((SymConstant) orig_value).get_long();
					if(y > x) {
						words.add(CirErrorWord.inc_value);
					}
					else if(y < x) {
						words.add(CirErrorWord.dec_value);
					}
					if(Math.abs(y) > Math.abs(x)) {
						words.add(CirErrorWord.ext_value);
					}
					else if(Math.abs(y) < Math.abs(x)) {
						words.add(CirErrorWord.shk_value);
					}
					if(x == -y) {
						words.add(CirErrorWord.neg_numb);
					}
					else if(x == ~y) {
						words.add(CirErrorWord.rsv_numb);
					}
				}
				if(y > 0) {
					words.add(CirErrorWord.set_pos);
				}
				else if(y < 0) {
					words.add(CirErrorWord.set_neg);
				}
				else {
					words.add(CirErrorWord.set_zro);
				}
			}
			words.add(CirErrorWord.chg_addr);
		}
		else {
			words.add(CirErrorWord.chg_body);
		}
		
		CirErrorSentence sentence = new CirErrorSentence(expression);
		sentence.words.addAll(words);
		return sentence;
	}
	private static CirErrorSentence parse_refr_error(
			CirReferenceError error, CStateContexts contexts) throws Exception {
		CirExpression expression = error.get_reference();
		SymExpression orig_value = error.get_original_value();
		SymExpression muta_value = error.get_mutation_value();
		
		orig_value = SymEvaluator.evaluate_on(orig_value, contexts);
		muta_value = SymEvaluator.evaluate_on(muta_value, contexts);
		if(orig_value.equals(muta_value)) return null;	/* no error */
		
		List<CirErrorWord> words = new ArrayList<CirErrorWord>();
		if(is_void(expression)) {
			return null;
		}
		else if(is_boolean(expression)) {
			if(muta_value instanceof SymConstant) {
				if(((SymConstant) muta_value).get_bool()) {
					words.add(CirErrorWord.set_true);
				}
				else {
					words.add(CirErrorWord.set_false);
				}
			}
			words.add(CirErrorWord.chg_bool);
		}
		else if(is_integer(expression)) {
			if(muta_value instanceof SymConstant) {
				long y = ((SymConstant) muta_value).get_long();
				if(orig_value instanceof SymConstant) {
					long x = ((SymConstant) orig_value).get_long();
					if(y > x) {
						words.add(CirErrorWord.inc_value);
					}
					else if(y < x) {
						words.add(CirErrorWord.dec_value);
					}
					if(Math.abs(y) > Math.abs(x)) {
						words.add(CirErrorWord.ext_value);
					}
					else if(Math.abs(y) < Math.abs(x)) {
						words.add(CirErrorWord.shk_value);
					}
					if(x == -y) {
						words.add(CirErrorWord.neg_numb);
					}
					else if(x == ~y) {
						words.add(CirErrorWord.rsv_numb);
					}
				}
				if(y > 0) {
					words.add(CirErrorWord.set_pos);
				}
				else if(y < 0) {
					words.add(CirErrorWord.set_neg);
				}
				else {
					words.add(CirErrorWord.set_zro);
				}
			}
			words.add(CirErrorWord.chg_numb);
		}
		else if(is_double(expression)) {
			if(muta_value instanceof SymConstant) {
				double y = ((SymConstant) muta_value).get_double();
				if(orig_value instanceof SymConstant) {
					double x = ((SymConstant) orig_value).get_double();
					if(y > x) {
						words.add(CirErrorWord.inc_value);
					}
					else if(y < x) {
						words.add(CirErrorWord.dec_value);
					}
					if(Math.abs(y) > Math.abs(x)) {
						words.add(CirErrorWord.ext_value);
					}
					else if(Math.abs(y) < Math.abs(x)) {
						words.add(CirErrorWord.shk_value);
					}
					if(x == -y) {
						words.add(CirErrorWord.neg_numb);
					}
				}
				if(y > 0) {
					words.add(CirErrorWord.set_pos);
				}
				else if(y < 0) {
					words.add(CirErrorWord.set_neg);
				}
				else {
					words.add(CirErrorWord.set_zro);
				}
			}
			words.add(CirErrorWord.chg_numb);
		}
		else if(is_pointer(expression)) {
			if(muta_value instanceof SymConstant) {
				long y = ((SymConstant) muta_value).get_long();
				if(orig_value instanceof SymConstant) {
					long x = ((SymConstant) orig_value).get_long();
					if(y > x) {
						words.add(CirErrorWord.inc_value);
					}
					else if(y < x) {
						words.add(CirErrorWord.dec_value);
					}
					if(Math.abs(y) > Math.abs(x)) {
						words.add(CirErrorWord.ext_value);
					}
					else if(Math.abs(y) < Math.abs(x)) {
						words.add(CirErrorWord.shk_value);
					}
					if(x == -y) {
						words.add(CirErrorWord.neg_numb);
					}
					else if(x == ~y) {
						words.add(CirErrorWord.rsv_numb);
					}
				}
				if(y > 0) {
					words.add(CirErrorWord.set_pos);
				}
				else if(y < 0) {
					words.add(CirErrorWord.set_neg);
				}
				else {
					words.add(CirErrorWord.set_zro);
				}
			}
			words.add(CirErrorWord.chg_addr);
		}
		else {
			words.add(CirErrorWord.chg_body);
		}
		
		CirErrorSentence sentence = new CirErrorSentence(expression);
		sentence.words.addAll(words);
		return sentence;
	}
	private static CirErrorSentence parse_stat_error(
			CirStateValueError error, CStateContexts contexts) throws Exception {
		CirErrorSentence sentence = new CirErrorSentence(error.get_reference());
		sentence.words.add(CirErrorWord.stat_err);
		return sentence;
	}
	private static CirErrorSentence parse_flow_error(
			CirFlowError error, CStateContexts contexts) throws Exception {
		CirErrorSentence sentence = new CirErrorSentence(error.get_statement());
		sentence.words.add(CirErrorWord.stmt_err);
		return sentence;
	}
	private static CirErrorSentence parse_trap_error(
			CirTrapError error, CStateContexts contexts) throws Exception {
		CirErrorSentence sentence = new CirErrorSentence(error.get_statement());
		sentence.words.add(CirErrorWord.trap_err);
		return sentence;
	}
	/**
	 * @param error
	 * @param contexts
	 * @return sentence to describe the error under contexts or null if it is invalid
	 * @throws Exception
	 */
	public static CirErrorSentence parse(CirStateError error, CStateContexts contexts) throws Exception {
		if(error == null)
			return null;
		else if(error instanceof CirExpressionError)
			return parse_expr_error((CirExpressionError) error, contexts);
		else if(error instanceof CirReferenceError)
			return parse_refr_error((CirReferenceError) error, contexts);
		else if(error instanceof CirStateValueError)
			return parse_stat_error((CirStateValueError) error, contexts);
		else if(error instanceof CirFlowError)
			return parse_flow_error((CirFlowError) error, contexts);
		else if(error instanceof CirTrapError)
			return parse_trap_error((CirTrapError) error, contexts);
		else
			throw new IllegalArgumentException(error.toString());
	}
	
}
