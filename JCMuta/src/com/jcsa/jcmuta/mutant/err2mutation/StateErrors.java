package com.jcsa.jcmuta.mutant.err2mutation;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * Set of state errors with unique ID and factory method.
 * 
 * @author yukimula
 *
 */
public class StateErrors {
	
	/* constructor and singleton */
	/** string ID to each state error in set **/
	private Map<String, StateError> errors;
	private StateErrors() {
		this.errors = new HashMap<String, StateError>();
	}
	/**
	 * create an empty set of state errors 
	 * @return
	 */
	public static StateErrors new_set() { return new StateErrors(); }
	
	/* getters */
	/**
	 * preserve the error in the map
	 * @param error
	 * @return
	 */
	private StateError preserve(StateError error) {
		String id = error.toString();
		if(!this.errors.containsKey(id))
			this.errors.put(id, error);
		return this.errors.get(id);
	}
	/**
	 * execute(stmt)
	 * @param statement
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError execute(CirStatement statement) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.execute);
		error.operands.add(statement); return this.preserve(error);
	}
	/**
	 * not_execute(stmt)
	 * @param statement
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError not_execute(CirStatement statement) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.not_execute);
		error.operands.add(statement); return this.preserve(error);
	}
	/**
	 * set_bool(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError set_bool(CirExpression expression, boolean value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.set_bool);
		error.operands.add(expression); 
		error.operands.add(Boolean.valueOf(value));
		return this.preserve(error);
	}
	/**
	 * chg_bool(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError chg_bool(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.chg_bool);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * set_numb(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError set_numb(CirExpression expression, long value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.set_numb);
		error.operands.add(expression); 
		error.operands.add(Long.valueOf(value));
		return this.preserve(error);
	} 
	/**
	 * set_numb(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError set_numb(CirExpression expression, double value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.set_numb);
		error.operands.add(expression); 
		error.operands.add(Double.valueOf(value));
		return this.preserve(error);
	} 
	/**
	 * neg_numb(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError neg_numb(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.neg_numb);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * set_numb(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError xor_numb(CirExpression expression, long value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.xor_numb);
		error.operands.add(expression); 
		error.operands.add(Long.valueOf(value));
		return this.preserve(error);
	}
	/**
	 * neg_numb(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError rsv_numb(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.rsv_numb);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * dif_numb(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError dif_numb(CirExpression expression, long value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.dif_numb);
		error.operands.add(expression); 
		error.operands.add(Long.valueOf(value));
		return this.preserve(error);
	}
	/**
	 * dif_numb(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError dif_numb(CirExpression expression, double value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.dif_numb);
		error.operands.add(expression); 
		error.operands.add(Double.valueOf(value));
		return this.preserve(error);
	}
	/**
	 * inc_numb(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError inc_numb(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.inc_numb);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * dec_numb(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError dec_numb(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.dec_numb);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * chg_numb(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError chg_numb(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.chg_numb);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * dif_addr(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError dif_addr(CirExpression expression, long value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.dif_addr);
		error.operands.add(expression); 
		error.operands.add(Double.valueOf(value));
		return this.preserve(error);
	}
	/**
	 * set_addr(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError set_addr(CirExpression expression, String value) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.set_addr);
		error.operands.add(expression); 
		error.operands.add(value);
		return this.preserve(error);
	}
	/**
	 * chg_addr(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError chg_addr(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.chg_addr);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * mut_expr(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError mut_expr(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.mut_expr);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * mut_refer(expr)
	 * @param expression
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError mut_refer(CirExpression expression) throws IllegalArgumentException {
		StateError error = new StateError(ErrorType.mut_refer);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	
}
