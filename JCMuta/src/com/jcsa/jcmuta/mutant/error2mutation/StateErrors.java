package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymEvaluator;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.impl.StandardSymEvaluator;

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
	/** create the empty set of state errors **/
	protected StateErrors() {
		this.errors = new HashMap<String, StateError>();
	}
	
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
		StateError error = new StateError(this, ErrorType.execute);
		error.operands.add(statement); return this.preserve(error);
	}
	/**
	 * not_execute(stmt)
	 * @param statement
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError not_execute(CirStatement statement) throws IllegalArgumentException {
		StateError error = new StateError(this, ErrorType.not_execute);
		error.operands.add(statement); return this.preserve(error);
	}
	/**
	 * execute_for(stmt, time)
	 * @param statement
	 * @param loop_times
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError execute_for(CirStatement statement, long loop_times) throws IllegalArgumentException {
		StateError error = new StateError(this, ErrorType.not_execute);
		error.operands.add(statement); 
		error.operands.add(Long.valueOf(loop_times));
		return this.preserve(error);
	}
	/**
	 * set_bool(expr, value)
	 * @param expression
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 */
	public StateError set_bool(CirExpression expression, boolean value) throws IllegalArgumentException {
		StateError error = new StateError(this, ErrorType.set_bool);
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
		StateError error = new StateError(this, ErrorType.chg_bool);
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
		StateError error = new StateError(this, ErrorType.set_numb);
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
		StateError error = new StateError(this, ErrorType.set_numb);
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
		StateError error = new StateError(this, ErrorType.neg_numb);
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
		StateError error = new StateError(this, ErrorType.xor_numb);
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
		StateError error = new StateError(this, ErrorType.rsv_numb);
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
		StateError error = new StateError(this, ErrorType.dif_numb);
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
		StateError error = new StateError(this, ErrorType.dif_numb);
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
		StateError error = new StateError(this, ErrorType.inc_numb);
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
		StateError error = new StateError(this, ErrorType.dec_numb);
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
		StateError error = new StateError(this, ErrorType.chg_numb);
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
		StateError error = new StateError(this, ErrorType.dif_addr);
		error.operands.add(expression); 
		error.operands.add(Long.valueOf(value));
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
		StateError error = new StateError(this, ErrorType.set_addr);
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
		StateError error = new StateError(this, ErrorType.chg_addr);
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
		StateError error = new StateError(this, ErrorType.mut_expr);
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
		StateError error = new StateError(this, ErrorType.mut_refer);
		error.operands.add(expression); 
		return this.preserve(error);
	}
	/**
	 * failure();
	 * @return
	 * @throws Exception
	 */
	public StateError failure() throws Exception {
		StateError error = new StateError(this, ErrorType.failure);
		return this.preserve(error);
	}
	/**
	 * syntax_error();
	 * @return
	 * @throws Exception
	 */
	public StateError syntax_error() throws Exception {
		StateError error = new StateError(this, ErrorType.syntax_error);
		return this.preserve(error);
	}
	
	/* basic method */
	/** used to preserve set of state errors to be extended from seed **/
	private final Set<StateError> extend_set = new HashSet<StateError>();
	/** standard symbolic evaluator **/
	private static final SymEvaluator evaluator = StandardSymEvaluator.new_evaluator();
	/**
	 * get the solution of expression
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymExpression get_solution(CirExpression expression) throws Exception {
		SymExpression expr = SymFactory.parse(expression);
		return evaluator.evaluate(expr);
	}
	/**
	 * whether the expression is used as a boolean condition
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private boolean is_boolean_condition(CirExpression expression) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type))
			return true;
		else {
			CirNode parent = expression.get_parent();
			return (parent instanceof CirIfStatement) || (parent instanceof CirCaseStatement);
		}
	}
	/**
	 * get the value of the constant
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private Object get_value_of(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:
			if(constant.get_bool().booleanValue())
				return Long.valueOf(1L);
			else return Long.valueOf(0L);
		case c_long:
			return constant.get_long().longValue();
		case c_double:
			return constant.get_double().doubleValue();
		default: throw new IllegalArgumentException("Invalid constant");
		}
	}
	
	/* extension methods */
	/**
	 * get the implication set from the error of source
	 * @param error
	 * @return
	 * @throws Exception
	 */
	public List<StateError> extend(StateError error) throws Exception {
		this.extend_set.clear();
		this.extend_at(error);
		List<StateError> errors = new ArrayList<StateError>();
		errors.addAll(this.extend_set);
		this.extend_set.clear();
		return errors;
	}
	/**
	 * extend from the error as given
	 * @param error
	 * @throws Exception
	 */
	private void extend_at(StateError error) throws Exception {
		if(error == null)
			throw new IllegalArgumentException("Invalid error: null");
		else if(this.extend_set.contains(error)) return; /* solved */
		else {
			switch(error.get_type()) {
			case execute:		this.extend_execute(error);		break;
			case not_execute:	this.extend_not_execute(error); break;
			case execute_for: 	this.extend_execute_for(error);	break;
			case set_bool:		this.extend_set_bool(error); 	break;
			case chg_bool:		this.extend_chg_bool(error); 	break;
			case set_numb:		this.extend_set_numb(error); 	break;
			case neg_numb:		this.extend_neg_numb(error); 	break;
			case xor_numb:		this.extend_xor_numb(error); 	break;
			case rsv_numb:		this.extend_rsv_numb(error); 	break;
			case dif_numb:		this.extend_dif_numb(error); 	break;
			case inc_numb:		this.extend_inc_numb(error); 	break;
			case dec_numb:		this.extend_dec_numb(error); 	break;
			case chg_numb:		this.extend_chg_numb(error); 	break;
			case dif_addr:		this.extend_dif_addr(error); 	break;
			case set_addr:		this.extend_set_addr(error); 	break;
			case chg_addr:		this.extend_chg_addr(error); 	break;
			case mut_expr:		this.extend_mut_expr(error); 	break;
			case mut_refer:		this.extend_mut_refer(error);	break;
			case failure:		this.extend_failure(error); 	break;
			case syntax_error:	this.extend_syntax_error(error);break;
			default: throw new IllegalArgumentException("Unsupport " + error);
			}
		}
	}
	/**
	 * set_bool ==> chg_bool {boolean}
	 * set_bool ==> set_numb {int|real}
	 * set_bool ==> set_addr {pointer}
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_bool(StateError error) throws Exception {
		/* 1. data getters */
		CirExpression expression = (CirExpression) error.get_operand(0);
		Boolean value = (Boolean) error.get_operand(1); CType data_type;
		data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. set_bool ==> chg_bool {boolean} */
		if(this.is_boolean_condition(expression)) {
			this.extend_set.add(error);
			this.extend_at(this.chg_bool(expression));
		}
		/* 3. set_bool ==> set_numb {int|real} */
		else if(CTypeAnalyzer.is_integer(data_type)) {
			long long_value = 0;
			if(value.booleanValue()) long_value++;
			this.extend_at(this.set_numb(expression, long_value));
		}
		/* 4. set_bool ==> set_numb {int|real} */
		else if(CTypeAnalyzer.is_real(data_type)) {
			double double_value = 0.0;
			if(value.booleanValue()) double_value++;
			this.extend_at(this.set_numb(expression, double_value));
		}
		/* 5. set_bool ==> set_addr {pointer} */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			String address;
			if(value.booleanValue())
				address = StateError.InvalidAddr;
			else
				address = StateError.NullPointer;
			this.extend_at(this.set_addr(expression, address));
		}
		/* 6. invalid case */
		else {
			throw new IllegalArgumentException("Invalid data type: " + data_type);
		}
	}
	/**
	 * chg_bool ==> mut_expr {boolean}
	 * chg_bool ==> chg_numb {int|real}
	 * chg_bool ==> chg_addr {pointer}
	 * @param error
	 * @throws Exception
	 */
	private void extend_chg_bool(StateError error) throws Exception {
		/* 1. declarations */
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. set_bool ==> chg_bool {boolean} */
		if(this.is_boolean_condition(expression)) {
			this.extend_set.add(error);
			this.extend_at(this.mut_expr(expression));
		}
		/* 3. set_bool ==> set_numb {int|real} */
		else if(CTypeAnalyzer.is_integer(data_type)) {
			this.extend_at(this.chg_numb(expression));
		}
		/* 4. set_bool ==> set_numb {int|real} */
		else if(CTypeAnalyzer.is_real(data_type)) {
			this.extend_at(this.chg_numb(expression));
		}
		/* 5. set_bool ==> set_addr {pointer} */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		/* 6. invalid case */
		else {
			throw new IllegalArgumentException("Invalid data type: " + data_type);
		}
	}
	/**
	 * set_numb ==> set_bool {boolean}
	 * set_numb ==> dif_numb {int|real <constant>}
	 * set_numb ==> xor_numb {int|real <constant>}
	 * set_numb ==> neg_numb {int|real <constant, condition>}
	 * set_numb ==> rsv_numb {int|real <constant, condition>}
	 * set_numb ==> set_addr {pointer}
	 * set_numb ==> chg_numb {int|real <otherwise>}
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_numb(StateError error) throws Exception {
		/* 1. data getters */
		CirExpression expression = (CirExpression) error.get_operand(0);
		Object target = error.get_operand(1); CType data_type;
		data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. set_numb ==> set_bool {boolean} */
		if(this.is_boolean_condition(expression)) {
			boolean bool_value;
			if(target instanceof Long)
				bool_value = (((Long) target).longValue() != 0L);
			else
				bool_value = (((Double) target).doubleValue() != 0.0);
			this.extend_at(this.set_bool(expression, bool_value));
		}
		/* 3. set_numb ==> set_addr */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			long addr = ((Long) target).longValue();
			String address;
			if(addr == 0) 
				address = StateError.NullPointer;
			else
				address = StateError.InvalidAddr;
			this.extend_at(this.set_addr(expression, address));
		}
		/*
		 *	set_numb ==> dif_numb {int|real <constant>}
		 * 	set_numb ==> xor_numb {int|real <constant>}
		 * 	set_numb ==> neg_numb {int|real <constant, condition>}
		 * 	set_numb ==> rsv_numb {int|real <constant, condition>} 
		 * 	set_numb ==> chg_numb {int|real <otherwise>}
		 */
		else if(CTypeAnalyzer.is_number(data_type)) {
			SymExpression result = this.get_solution(expression);
			this.extend_set.add(error);
			
			if(result instanceof SymConstant) {
				CConstant constant = ((SymConstant) result).get_constant();
				Object source = this.get_value_of(constant);
				
				if(source instanceof Long) {
					long x = ((Long) source).longValue();
					if(target instanceof Long) {
						long y = ((Long) target).longValue();
						this.extend_at(this.dif_numb(expression, y - x));
						if(x == -y)
							this.extend_at(this.neg_numb(expression));
						
						this.extend_at(this.xor_numb(expression, y ^ x));
						if(x == ~y)
							this.extend_at(this.rsv_numb(expression));
					}
					else {
						double y = ((Double) target).doubleValue();
						this.extend_at(this.dif_numb(expression, y - x));
						if(x == -y)
							this.extend_at(this.neg_numb(expression));
					}
				}
				else {
					double x = ((Double) source).doubleValue();
					if(target instanceof Long) {
						long y = ((Long) target).longValue();
						this.extend_at(this.dif_numb(expression, y - x));
						if(x == -y)
							this.extend_at(this.neg_numb(expression));
					}
					else {
						double y = ((Double) target).doubleValue();
						this.extend_at(this.dif_numb(expression, y - x));
						if(x == -y)
							this.extend_at(this.neg_numb(expression));
					}
				}
			}
			else {
				this.extend_at(this.chg_numb(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + data_type);
		}
	}
	/**
	 * neg_numb ==> set_bool {boolean}
	 * neg_numb ==> set_numb {int|real} <constant>
	 * neg_numb ==> chg_numb {int|real} <otherwise>
	 * neg_numb ==> set_addr {pointer}
	 * @param error
	 * @throws Exception
	 */
	private void extend_neg_numb(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 1. neg_numb ==> set_bool(expr, true) */
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.set_bool(expression, true));
		}
		/* 2. neg_numb ==> set_addr(expr, invalid) */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.set_addr(expression, StateError.InvalidAddr));
		}
		/* 3. neg_numb ==> set_value(expr, -expr) <constant> */
		else if(CTypeAnalyzer.is_number(data_type)) {
			SymExpression result = this.get_solution(expression);
			this.extend_set.add(error);
			
			if(result instanceof SymConstant) {
				Object value = this.get_value_of(((SymConstant) result).get_constant());
				if(value instanceof Long) {
					this.extend_at(this.set_numb(expression, -((Long) value).longValue()));
				}
				else {
					this.extend_at(this.set_numb(expression, -((Double) value).doubleValue()));
				}
			}
			
			this.extend_at(this.chg_numb(expression));
		}
		/* 4. otherwise */
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * xor_numb ==> chg_bool {boolean}
	 * xor_numb ==> chg_addr {pointer}
	 * xor_numb ==> set_numb {int|real}<constant>
	 * xor_numb ==> chg_numb {int|real}<otherwise>
	 * @param error
	 * @throws Exception
	 */
	private void extend_xor_numb(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		long difference = ((Long) error.get_operand(1)).longValue();
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 1. xor_numb ==> chg_bool {boolean} */
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		/* 2. xor_numb ==> chg_addr {pointer} */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		/* 3. xor_numb ==> set_numb {int|real}<constant> */
		else if(CTypeAnalyzer.is_number(data_type)) {
			SymExpression result = this.get_solution(expression);
			this.extend_set.add(error);
			
			if(result instanceof SymConstant) {
				Object value = this.get_value_of(((SymConstant) result).get_constant());
				long source = ((Long) value).longValue();
				long target = source ^ difference;
				this.extend_at(this.set_numb(expression, target));
			}
			
			this.extend_at(this.chg_numb(expression));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * rsv_numb ==> chg_bool {boolean}
	 * rsv_numb ==> chg_addr {pointer}
	 * rsv_numb ==> set_numb {int|real} <constant>
	 * rsv_numb ==> chg_numb
	 * @param error
	 * @throws Exception
	 */
	private void extend_rsv_numb(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			SymExpression result = this.get_solution(expression);
			this.extend_set.add(error);
			
			if(result instanceof SymConstant) {
				Object value = this.get_value_of(((SymConstant) result).get_constant());
				long source = ((Long) value).longValue();
				long target = ~source;
				this.extend_at(this.set_numb(expression, target));
			}
			
			this.extend_at(this.chg_numb(expression));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * dif_numb ==> chg_bool {boolean}
	 * dif_numb ==> dif_addr {pointer}
	 * dif_numb ==> set_value{int|real} <constant>
	 * dif_numb ==> inc_numb | dec_numb
	 * 
	 * @param error
	 * @throws Exception
	 */
	private void extend_dif_numb(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		Object value = error.get_operand(1);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			long difference;
			if(value instanceof Long)
				difference = ((Long) value).longValue();
			else 
				difference = ((Double) value).longValue();
			this.extend_at(this.dif_addr(expression, difference));
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			this.extend_set.add(error);
			SymExpression result = this.get_solution(expression);
			
			if(value instanceof Long) {
				long difference = ((Long) value).longValue();
				if(difference > 0) {
					this.extend_at(this.inc_numb(expression));
				}
				else {
					this.extend_at(this.dec_numb(expression));
				}
				
				if(result instanceof SymConstant) {
					Object source = this.get_value_of(((SymConstant) result).get_constant());
					if(source instanceof Long) {
						long source_value = ((Long) source).longValue();
						this.extend_at(this.set_numb(expression, source_value + difference));
					}
					else {
						double source_value = ((Double) source).doubleValue();
						this.extend_at(this.set_numb(expression, source_value + difference));
					}
				}
			}
			else {
				double difference = ((Double) value).doubleValue();
				if(difference > 0) {
					this.extend_at(this.inc_numb(expression));
				}
				else {
					this.extend_at(this.dec_numb(expression));
				}
				
				if(result instanceof SymConstant) {
					Object source = this.get_value_of(((SymConstant) result).get_constant());
					if(source instanceof Long) {
						long source_value = ((Long) source).longValue();
						this.extend_at(this.set_numb(expression, source_value + difference));
					}
					else {
						double source_value = ((Double) source).doubleValue();
						this.extend_at(this.set_numb(expression, source_value + difference));
					}
				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid data type.");
		}
	}
	/**
	 * inc_numb ==> chg_bool
	 * inc_numb ==> chg_addr
	 * inc_numb ==> chg_numb
	 * @param error
	 * @throws Exception
	 */
	private void extend_inc_numb(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			this.extend_at(this.chg_numb(expression));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * dec_numb ==> chg_bool
	 * dec_numb ==> chg_addr
	 * dec_numb ==> chg_numb
	 * @param error
	 * @throws Exception
	 */
	private void extend_dec_numb(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			this.extend_at(this.chg_numb(expression));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * chg_numb ==> chg_bool
	 * chg_numb ==> chg_addr
	 * chg_numb ==> mut_expr
	 * @param error
	 * @throws Exception
	 */
	private void extend_chg_numb(StateError error) throws Exception {
		/* 1. declarations */
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. chg_numb ==> chg_bool {boolean} */
		if(this.is_boolean_condition(expression)) {
			this.extend_set.add(error);
			this.extend_at(this.chg_bool(expression));
		}
		/* 3. chg_numb ==> mut_expr {int|real} */
		else if(CTypeAnalyzer.is_integer(data_type)) {
			this.extend_at(this.mut_expr(expression));
		}
		/* 4. chg_numb ==> mut_expr {int|real} */
		else if(CTypeAnalyzer.is_real(data_type)) {
			this.extend_at(this.mut_expr(expression));
		}
		/* 5. chg_numb ==> chg_addr {pointer} */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		/* 6. invalid case */
		else {
			this.extend_at(this.mut_expr(expression));
			//throw new IllegalArgumentException("Invalid data type: " + data_type);
		}
	}
	/**
	 * dif_addr ==> set_bool {boolean}
	 * dif_addr ==> chg_addr {pointer}
	 * dif_addr ==> dif_numb {int|real}
	 * @param error
	 * @throws Exception
	 */
	private void extend_dif_addr(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		long difference = ((Long) error.get_operand(1)).longValue();
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 1. dif_addr ==> set_bool */
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.set_bool(expression, true));
		}
		/* 2. dif_addr ==> chg_addr */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_set.add(error);
			this.extend_at(this.chg_addr(expression));
		}
		/* 3. dif_addr ==> dif_numb */
		else if(CTypeAnalyzer.is_number(data_type)) {
			this.extend_at(this.dif_numb(expression, difference));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * set_addr ==> set_bool {boolean}
	 * set_addr ==> chg_addr {pointer}
	 * set_addr ==> set_value {number} <null>
	 * set_addr ==> chg_value {number} <invalid>
	 * @param error
	 * @throws Exception
	 */
	private void extend_set_addr(StateError error) throws Exception {
		/* 1. data getter */
		CirExpression expression = (CirExpression) error.get_operand(0);
		String value = (String) error.get_operand(1);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. set_addr ==> set_bool */
		if(this.is_boolean_condition(expression)) {
			if(value.equals(StateError.NullPointer)) {
				this.extend_at(this.set_bool(expression, false));
			}
			else {
				this.extend_at(this.set_bool(expression, true));
			}
		}
		/* 3. set_addr ==> chg_addr */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_set.add(error);
			this.extend_at(this.chg_addr(expression));
		}
		/* 4. set_addr ==> set_numb | chg_numb */
		else if(CTypeAnalyzer.is_number(data_type)) {
			if(value.equals(StateError.NullPointer)) {
				this.extend_at(this.set_numb(expression, 0L));
			}
			else {
				this.extend_at(this.chg_numb(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * chg_addr ==> chg_bool {boolean}
	 * chg_addr ==> mut_expr {pointer}
	 * chg_addr ==> chg_numb {number}
	 * @param error
	 * @throws Exception
	 */
	private void extend_chg_addr(StateError error) throws Exception {
		/* 1. data getter */
		CirExpression expression = (CirExpression) error.get_operand(0);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. chg_addr ==> chg_bool */
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		/* 3. set_addr ==> chg_addr */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_set.add(error);
			this.extend_at(this.mut_expr(expression));
		}
		/* 4. set_addr ==> set_numb | chg_numb */
		else if(CTypeAnalyzer.is_number(data_type)) {
			this.extend_at(this.chg_numb(expression));
		}
		else {
			throw new IllegalArgumentException("Invalid data type");
		}
	}
	/**
	 * whether the expression is a left-value in assignment
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	/*
	private boolean is_left_reference(CirExpression expr) throws Exception {
		CirNode parent = expr.get_parent();
		if(parent instanceof CirAssignStatement) {
			return ((CirAssignStatement) parent).get_lvalue() == expr;
		}
		else {
			return false;
		}
	}
	*/
	/**
	 * mut_expr <-- --> mut_refer
	 * @param error
	 * @throws Exception
	 */
	private void extend_mut_expr(StateError error) throws Exception {
		this.extend_set.add(error);
		
		CirExpression expression = (CirExpression) error.get_operand(0);
		if(expression.get_data_type() == null) return;	/* initializer */
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		/* 2. chg_addr ==> chg_bool */
		if(this.is_boolean_condition(expression)) {
			this.extend_at(this.chg_bool(expression));
		}
		/* 3. set_addr ==> chg_addr */
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			this.extend_at(this.chg_addr(expression));
		}
		/* 4. set_addr ==> set_numb | chg_numb */
		else if(CTypeAnalyzer.is_number(data_type)) {
			this.extend_at(this.chg_numb(expression));
		}
	}
	/**
	 * 
	 * @param error
	 * @throws Exception
	 */
	private void extend_mut_refer(StateError error) throws Exception {
		CirExpression expression = (CirExpression) error.get_operand(0);
		if(expression instanceof CirDeferExpression)
			this.extend_set.add(error);
		this.extend_at(this.mut_expr(expression));
	}
	/**
	 * execute ==> mut_expr {assignment}
	 * @param error
	 * @throws Exception
	 */
	private void extend_execute(StateError error) throws Exception {
		this.extend_set.add(error);
		/*
		CirStatement statement = (CirStatement) error.get_operand(0);
		if(statement instanceof CirAssignStatement) {
			this.extend_at(this.mut_expr(((CirAssignStatement) statement).get_rvalue()));
		}
		*/
	}
	/**
	 * 
	 * @param error
	 * @throws Exception
	 */
	private void extend_not_execute(StateError error) throws Exception {
		this.extend_set.add(error);
		/*
		CirStatement statement = (CirStatement) error.get_operand(0);
		if(statement instanceof CirAssignStatement) {
			this.extend_at(this.mut_expr(((CirAssignStatement) statement).get_rvalue()));
		}
		*/
	}
	private void extend_failure(StateError error) throws Exception {
		this.extend_set.add(error);
	}
	private void extend_syntax_error(StateError error) throws Exception {
		this.extend_set.add(error);
	}
	private void extend_execute_for(StateError error) throws Exception {
		this.extend_set.add(error);
		CirStatement statement = (CirStatement) error.get_operand(0);
		this.extend_at(error.get_errors().execute(statement));
	}
	
}
