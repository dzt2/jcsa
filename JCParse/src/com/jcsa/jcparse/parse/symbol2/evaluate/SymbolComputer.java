package com.jcsa.jcparse.parse.symbol2.evaluate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBasicExpression;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolNodeFactory;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;
import com.jcsa.jcparse.parse.symbol2.process.SymbolInvoker;

/**
 * It implements the basic computational interfaces to simplify or evaluate symbolic expression based on operands.
 * 
 * @author yukimula
 *
 */
class SymbolComputer {
	
	/* definitions */
	/** the evaluator machine it serves for **/
	private SymbolEvaluator evaluator;
	private SymbolNodeFactory factory;
	protected SymbolComputer(SymbolEvaluator evaluator) {
		this.evaluator = evaluator;
		this.factory = this.evaluator.get_symbol_factory();
	}
	
	/* basic expressions */
	/**
	 * @param expression
	 * @return do nothing and return the inputs
	 */
	protected SymbolExpression compute_basic(SymbolBasicExpression expression) {
		return expression;
	}
	
	/* unary expressions */
	/**
	 * @param operand
	 * @return compute based on rules as:
	 * 			(1) constant		==>	-(constant)
	 * 			(2)	-expression		==> expression
	 * 			(3)	~expression		==>	expression + 1
	 * 			(4)	x - y			==>	y - x
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_neg(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {											/* constant ==> -(constant) */
			CBasicType type = ((SymbolConstant) operand).get_constant().get_type();
			switch(type.get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				return this.factory.new_constant(-((SymbolConstant) operand).get_int());
			}
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				return this.factory.new_constant(-((SymbolConstant) operand).get_long());
			}
			case c_float:
			case c_double:
			case c_ldouble:
			{
				return this.factory.new_constant(-((SymbolConstant) operand).get_double());
			}
			default: throw new IllegalArgumentException("Unsupported: " + type.generate_code());
			}
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case negative:																			/* -expression ==> expression */
			{
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			case bit_not:																			/* ~expression ==> expression + 1 */
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.factory.obj2constant(Integer.valueOf(1));
				return this.factory.new_arith_add(operand.get_data_type(), loperand, roperand);
			}
			default:																				/* -operand */
			{
				return this.factory.new_arith_neg(operand);
			}
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case arith_sub:
			{
				SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_loperand();
				SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_roperand();
				return this.factory.new_arith_sub(operand.get_data_type(), loperand, roperand);
			}
			default:
			{
				return this.factory.new_arith_neg(operand);
			}
			}
		}
		else {
			return this.factory.new_arith_neg(operand);
		}
	}
	/**
	 * @param operand
	 * @return	It computes ~operand based on following:
	 * 			(1)	constant --> ~constant
	 * 			(2)	~expression --> expression
	 * 			(3) -expression --> expression - 1
	 * 			(4) otherwise --> ~operand
	 * @throws Exception
	 */
	protected SymbolExpression compute_bitws_rsv(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			CBasicType type = ((SymbolConstant) operand).get_constant().get_type();
			switch(type.get_tag()) {
			case c_bool:
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				return this.factory.new_constant(~(((SymbolConstant) operand).get_int()));
			}
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				return this.factory.new_constant(~(((SymbolConstant) operand).get_long()));
			}
			default: throw new IllegalArgumentException(type.generate_code());
			}
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case bit_not:
			{
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			case negative:
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.factory.new_constant(1);
				return this.factory.new_arith_sub(operand.get_data_type(), loperand, roperand);
			}
			default:
			{
				return this.factory.new_bitws_rsv(operand);
			}
			}
		}
		else {
			return this.factory.new_bitws_rsv(operand);
		}
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return x && y --> !x || !y
	 * @throws Exception
	 */
	private SymbolExpression negate_logic_and(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		loperand = this.compute_logic_not(loperand);
		roperand = this.compute_logic_not(roperand);
		return this.factory.new_logic_ior(loperand, roperand);
	}
	/**
	 * @param loperand
	 * @param roperand
	 * @return x || y --> !x && !y
	 * @throws Exception
	 */
	private SymbolExpression negate_logic_ior(SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		loperand = this.compute_logic_not(loperand);
		roperand = this.compute_logic_not(roperand);
		return this.factory.new_logic_and(loperand, roperand);
	}
	/**
	 * @param operand
	 * @return	compute !operand based on:
	 * 			1. constant		--> !constant
	 * 			2. !expression	-->	expression
	 * 			3. -expression	-->	expression == 0
	 * 			4. ~expression	--> expression == -1
	 * 			5. x < y		--> x >= y
	 * 			....
	 * 			6. otherwise	--> {operand as false}
	 * @throws Exception
	 */
	protected SymbolExpression compute_logic_not(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			return this.factory.new_constant(!(((SymbolConstant) operand).get_bool()));
		}
		else if(operand instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) operand).get_operator().get_operator();
			switch(operator) {
			case logic_not:
			{
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			case negative:
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.factory.new_constant(0);
				return this.factory.new_equal_with(loperand, roperand);
			}
			case bit_not:
			{
				SymbolExpression loperand = ((SymbolUnaryExpression) operand).get_operand();
				SymbolExpression roperand = this.factory.new_constant(-1);
				return this.factory.new_equal_with(loperand, roperand);
			}
			default:
			{
				return this.factory.obj2condition(operand, false);
			}
			}
		}
		else if(operand instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) operand).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) operand).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) operand).get_roperand();
			switch(operator) {
			case logic_and:
			{
				return this.negate_logic_and(loperand, roperand);
			}
			case logic_or:
			{
				return this.negate_logic_ior(loperand, roperand);
			}
			case smaller_tn:
			{
				return this.factory.new_greater_eq(loperand, roperand);
			}
			case smaller_eq:
			{
				return this.factory.new_greater_tn(loperand, roperand);
			}
			case greater_tn:
			{
				return this.factory.new_smaller_eq(loperand, roperand);
			}
			case greater_eq:
			{
				return this.factory.new_smaller_tn(loperand, roperand);
			}
			case equal_with:
			{
				return this.factory.new_not_equals(loperand, roperand);
			}
			case not_equals:
			{
				return this.factory.new_equal_with(loperand, roperand);
			}
			default:
			{
				return this.factory.obj2condition(operand, false);
			}
			}
		}
		else {
			return this.factory.obj2condition(operand, false);
		}
	}
	/**
	 * @param operand
	 * @return compute &operand based on:
	 * 			1. *expression 	--> expression
	 * 			2. otherwise	-->	&operand
	 * @throws Exception
	 */
	protected SymbolExpression compite_address_of(SymbolExpression operand) throws Exception {
		if(operand.is_reference()) {
			if(operand instanceof SymbolUnaryExpression) {
				if(((SymbolUnaryExpression) operand).get_operator().get_operator() == COperator.dereference) {
					return ((SymbolUnaryExpression) operand).get_operand();
				}
				else {
					return this.factory.new_address_of(operand);
				}
			}
			else {
				return this.factory.new_address_of(operand); 
			}
		}
		else {
			throw new IllegalArgumentException("Not a reference: " + operand);
		}
	}
	/**
	 * @param operand
	 * @return	compute *operand as:
	 * 			1. &expression 	--> expression
	 * 			2. otherwise	--> *operand
	 * @throws Exception
	 */
	protected SymbolExpression compute_dereference(SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolUnaryExpression) {
			if(((SymbolUnaryExpression) operand).get_operator().get_operator() == COperator.address_of) {
				return ((SymbolUnaryExpression) operand).get_operand();
			}
			else {
				return this.factory.new_dereference(operand);
			}
		}
		else {
			return this.factory.new_dereference(operand);
		}
	}
	/**
	 * @param type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_type_cast(CType type, SymbolExpression operand) throws Exception {
		if(operand instanceof SymbolConstant) {
			type = CTypeAnalyzer.get_value_type(type);
			if(type instanceof CBasicType) {
				switch(((CBasicType) type).get_tag()) {
				case c_bool:	return this.factory.new_constant(((SymbolConstant) operand).get_bool());
				case c_char:
				case c_uchar:	return this.factory.new_constant(((SymbolConstant) operand).get_char());
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:	return this.factory.new_constant(((SymbolConstant) operand).get_int());
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:	return this.factory.new_constant(((SymbolConstant) operand).get_long());
				case c_float:	return this.factory.new_constant(((SymbolConstant) operand).get_float());
				case c_double:
				case c_ldouble:	return this.factory.new_constant(((SymbolConstant) operand).get_double());
				default: 		return this.factory.new_dereference(operand);
				}
			}
			else if(type instanceof CEnumType) {
				return this.factory.new_constant(((SymbolConstant) operand).get_int());
			}
			else {
				return this.factory.new_dereference(operand);
			}
		}
		else {
			return this.factory.new_type_casting(type, operand);
		}
	}
	
	/* special expression */
	/**
	 * @param elements
	 * @return 
	 * @throws Exception
	 */
	protected SymbolExpression compute_initializer_list(Iterable<SymbolExpression> elements) throws Exception {
		ArrayList<Object> objects = new ArrayList<Object>();
		for(SymbolExpression element : elements) {
			objects.add(element);
		}
		return this.factory.new_initializer_list(objects);
	}
	/**
	 * @param body
	 * @param field
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_field_expression(SymbolExpression body, String field) throws Exception {
		return this.factory.new_field_expression(body, field);
	}
	/**
	 * @param function
	 * @param arguments
	 * @return 
	 * @throws Exception
	 */
	protected SymbolExpression compute_call_expression(SymbolExpression function, Iterable<SymbolExpression> arguments) throws Exception {
		ArrayList<Object> argument_objects = new ArrayList<Object>();
		for(SymbolExpression argument : arguments) argument_objects.add(argument);
		SymbolCallExpression call_expression = this.factory.new_call_expression(function, argument_objects);
		if(this.evaluator.get_symbol_process() != null) {
			for(SymbolInvoker invoker : this.evaluator.get_symbol_process().get_invokers()) {
				SymbolExpression result = invoker.invoke(call_expression);
				if(result != null) return result;
			}
			return call_expression;
		}
		else {
			return call_expression;
		}
	}
	
	/* arithmetic addition and subtract {+,-} */
	/**
	 * @param constant
	 * @return whether the constant is zero
	 * @throws Exception
	 */
	private boolean is_zero_constant(SymbolConstant constant) throws Exception {
		return !constant.get_bool();
	}
	/**
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant compute_constant_add(SymbolConstant x, SymbolConstant y) throws Exception {
		Object lnumber = x.get_number(), rnumber = y.get_number();
		if(lnumber instanceof Long) {
			long xvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue + yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue + yvalue);
			}
		}
		else {
			double xvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue + yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue + yvalue);
			}
		}
	}
	/**
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	private SymbolConstant compute_constant_sub(SymbolConstant x, SymbolConstant y) throws Exception {
		Object lnumber = x.get_number(), rnumber = y.get_number();
		if(lnumber instanceof Long) {
			long xvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue - yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue - yvalue);
			}
		}
		else {
			double xvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue - yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue - yvalue);
			}
		}
	}
	/**
	 * @param expression
	 * @param positive_operands
	 * @param negative_operands
	 * @throws Exception
	 */
	private void extend_operands_in_add_or_sub(SymbolExpression expression, 
			Collection<SymbolExpression> positive_operands, 
			Collection<SymbolExpression> negative_operands) throws Exception {
		if(expression instanceof SymbolUnaryExpression) {
			COperator operator = ((SymbolUnaryExpression) expression).get_operator().get_operator();
			SymbolExpression operand = ((SymbolUnaryExpression) expression).get_operand();
			if(operator == COperator.negative) {
				this.extend_operands_in_add_or_sub(operand, negative_operands, positive_operands);
			}
			else {
				positive_operands.add(expression);
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			
			if(operator == COperator.arith_add) {
				this.extend_operands_in_add_or_sub(loperand, positive_operands, negative_operands);
				this.extend_operands_in_add_or_sub(roperand, positive_operands, negative_operands);
			}
			else if(operator == COperator.arith_sub) {
				this.extend_operands_in_add_or_sub(loperand, positive_operands, negative_operands);
				this.extend_operands_in_add_or_sub(roperand, negative_operands, positive_operands);
			}
			else {
				positive_operands.add(expression);
			}
		}
		else {
			positive_operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param new_operands
	 * @return update variables into new_operands and add the constant in last one of new_operands
	 * @throws Exception
	 */
	private void update_operands_in_add(Iterable<SymbolExpression> operands, List<SymbolExpression> new_operands) throws Exception {
		SymbolConstant constant = this.factory.new_constant(0);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_constant_add(constant, (SymbolConstant) operand);
			}
			else {
				new_operands.add(operand);
			}
		}
		new_operands.add(constant);
	}
	/**
	 * @param expression
	 * @return { pos_operands, neg_operands }
	 * @throws Exception
	 */
	private List<List<SymbolExpression>> divide_operands_in_add_or_sub(SymbolExpression expression) throws Exception {
		List<SymbolExpression> pos_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> neg_operands = new ArrayList<SymbolExpression>();
		this.extend_operands_in_add_or_sub(expression, pos_operands, neg_operands);
		
		List<SymbolExpression> new_pos_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> new_neg_operands = new ArrayList<SymbolExpression>();
		this.update_operands_in_add(pos_operands, new_pos_operands);
		this.update_operands_in_add(neg_operands, new_neg_operands);
		
		SymbolConstant pos_constant = (SymbolConstant) new_pos_operands.remove(new_pos_operands.size() - 1);
		SymbolConstant neg_constant = (SymbolConstant) new_neg_operands.remove(new_neg_operands.size() - 1);
		SymbolConstant constant = this.compute_constant_sub(pos_constant, neg_constant);
		if(!this.is_zero_constant(constant)) { new_pos_operands.add(constant); }
		
		List<List<SymbolExpression>> results = new ArrayList<List<SymbolExpression>>();
		results.add(new_pos_operands); results.add(new_neg_operands); return results;
	}
	/**
	 * simplify the operands in positive and negative parts of the expression
	 * @param pos_operands
	 * @param neg_operands
	 * @throws Exception
	 */
	private void simplify_pos_neg_operands(List<SymbolExpression> pos_operands, List<SymbolExpression> neg_operands) throws Exception {
		SymbolExpression pos_remove, neg_remove;
		do {
			pos_remove = null; neg_remove = null;
			
			for(SymbolExpression pos_operand : pos_operands) {
				for(SymbolExpression neg_operand : neg_operands) {
					if(pos_operand.equals(neg_operand)) {
						pos_remove = pos_operand;
						neg_remove = neg_operand;
					}
				}
				if(pos_remove != null) {
					break;
				}
			}
			
			pos_operands.remove(pos_remove);
			neg_operands.remove(neg_remove);
		} while(pos_remove != null && neg_remove != null);
	}
	/**
	 * @param operands 		[ x1 + x2 + ... + xn ]
	 * @throws Exception
	 */
	private SymbolExpression accumulate_by_add(CType type, List<SymbolExpression> operands) throws Exception {
		if(operands.isEmpty()) {
			return this.factory.new_constant(0);
		}
		else if(operands.size() == 1) {
			return operands.get(0);
		}
		else {
			SymbolExpression expression = null;
			for(SymbolExpression operand : operands) {
				if(expression == null) {
					expression = operand;
				}
				else {
					expression = this.factory.new_arith_add(type, expression, operand);
				}
			}
			return expression;
		}
	}
	/**
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private SymbolExpression compute_arith_add_or_sub(SymbolExpression expression) throws Exception {
		List<List<SymbolExpression>> pos_neg_operands = 
							this.divide_operands_in_add_or_sub(expression);
		List<SymbolExpression> positive_operands = pos_neg_operands.get(0);
		List<SymbolExpression> negative_operands = pos_neg_operands.get(1);
		this.simplify_pos_neg_operands(positive_operands, negative_operands);
		if(positive_operands.isEmpty()) {
			if(negative_operands.isEmpty()) {
				return this.factory.new_constant(0);
			}
			else {
				SymbolExpression roperand = this.accumulate_by_add(
						expression.get_data_type(), negative_operands);
				return this.factory.new_arith_neg(roperand);
			}
		}
		else {
			if(negative_operands.isEmpty()) {
				SymbolExpression loperand = this.accumulate_by_add(
						expression.get_data_type(), positive_operands);
				return loperand;
			}
			else {
				SymbolExpression loperand = this.accumulate_by_add(
						expression.get_data_type(), positive_operands);
				SymbolExpression roperand = this.accumulate_by_add(
						expression.get_data_type(), negative_operands);
				return this.factory.new_arith_sub(expression.get_data_type(), loperand, roperand);
			}
		}
		
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_add(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_add_or_sub(this.factory.new_arith_add(type, loperand, roperand));
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_sub(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_add_or_sub(this.factory.new_arith_sub(type, loperand, roperand));
	}
	
	/* arithmetic multiply and division {*, /} */
	/**
	 * @param constant
	 * @return constant == 1
	 * @throws Exception
	 */
	private boolean is_pone_constant(SymbolConstant constant) throws Exception {
		Object number = constant.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == 1;
		}
		else {
			return ((Double) number).doubleValue() == 1.0;
		}
	}
	/**
	 * @param constant
	 * @return constant == -1
	 * @throws Exception
	 */
	private boolean is_none_constant(SymbolConstant constant) throws Exception {
		Object number = constant.get_number();
		if(number instanceof Long) {
			return ((Long) number).longValue() == -1;
		}
		else {
			return ((Double) number).doubleValue() == -1.0;
		}
	}
	/**
	 * @param x
	 * @param y
	 * @return x * y
	 * @throws Exception
	 */
	private SymbolConstant compute_constant_mul(SymbolConstant x, SymbolConstant y) throws Exception {
		Object lnumber = x.get_number(), rnumber = y.get_number();
		if(lnumber instanceof Long) {
			long xvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue * yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue * yvalue);
			}
		}
		else {
			double xvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue * yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue * yvalue);
			}
		}
	}
	/**
	 * @param x
	 * @param y
	 * @return x / y
	 * @throws Exception
	 */
	private SymbolConstant compute_constant_div(SymbolConstant x, SymbolConstant y) throws Exception {
		Object lnumber = x.get_number(), rnumber = y.get_number();
		if(lnumber instanceof Long) {
			long xvalue = ((Long) lnumber).longValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue / yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue / yvalue);
			}
		}
		else {
			double xvalue = ((Double) lnumber).doubleValue();
			if(rnumber instanceof Long) {
				long yvalue = ((Long) rnumber).longValue();
				return this.factory.new_constant(xvalue / yvalue);
			}
			else {
				double yvalue = ((Double) rnumber).doubleValue();
				return this.factory.new_constant(xvalue / yvalue);
			}
		}
	}
	/**
	 * @param expression
	 * @param divised_operands
	 * @param divisor_operands
	 * @throws Exception
	 */
	private void extend_operands_in_mul_or_div(SymbolExpression expression, 
			List<SymbolExpression> divised_operands, 
			List<SymbolExpression> divisor_operands) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.arith_mul) {
				this.extend_operands_in_mul_or_div(loperand, divised_operands, divisor_operands);
				this.extend_operands_in_mul_or_div(roperand, divised_operands, divisor_operands);
			}
			else if(operator == COperator.arith_div) {
				this.extend_operands_in_mul_or_div(loperand, divised_operands, divisor_operands);
				this.extend_operands_in_mul_or_div(roperand, divisor_operands, divised_operands);
			}
			else {
				divised_operands.add(expression);
			}
		}
		else {
			divised_operands.add(expression);
		}
	}
	/**
	 * @param operands
	 * @param new_operands constant at last index
	 * @throws Exception
	 */
	private SymbolConstant update_operands_in_mul(Iterable<SymbolExpression> operands, List<SymbolExpression> new_operands) throws Exception {
		SymbolConstant constant = this.factory.new_constant(1);
		for(SymbolExpression operand : operands) {
			if(operand instanceof SymbolConstant) {
				constant = this.compute_constant_mul(constant, (SymbolConstant) operand);
			}
			else {
				new_operands.add(operand);
			}
		}
		return constant;
	}
	/**
	 * @param type
	 * @param operands 
	 * @return [x1 * x2 * ... * xn] * constant
	 * @throws Exception
	 */
	private SymbolExpression accumulate_by_mul(CType type, Iterable<SymbolExpression> operands, SymbolConstant constant) throws Exception {
		if(this.is_zero_constant(constant)) {
			return constant;
		}
		else {
			SymbolExpression expression = null;
			for(SymbolExpression operand : operands) {
				if(expression == null) {
					expression = operand;
				}
				else {
					expression = this.factory.new_arith_mul(type, expression, operand);
				}
			}
			
			if(expression == null) {
				return constant;
			}
			else if(this.is_pone_constant(constant)) {
				return expression;
			}
			else if(this.is_none_constant(constant)) {
				return this.factory.new_arith_neg(expression);
			}
			else {
				expression = this.factory.new_arith_mul(type, expression, constant);
				return expression;
			}
		}
	}
	/**
	 * @param expression
	 * @return { x / y }
	 * @throws Exception
	 */
	private SymbolExpression compute_arith_mul_or_div(SymbolExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		List<SymbolExpression> divised_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> divisor_operands = new ArrayList<SymbolExpression>();
		this.extend_operands_in_mul_or_div(expression, divised_operands, divisor_operands);
		
		List<SymbolExpression> new_divised_operands = new ArrayList<SymbolExpression>();
		List<SymbolExpression> new_divisor_operands = new ArrayList<SymbolExpression>();
		SymbolConstant lconstant = this.update_operands_in_mul(divised_operands, new_divised_operands);
		SymbolConstant rconstant = this.update_operands_in_mul(divisor_operands, new_divisor_operands);
		
		if(this.is_zero_constant(lconstant)) {
			return this.factory.new_constant(0);
		}
		else if(this.is_zero_constant(rconstant)) {
			throw new ArithmeticException("Divided by zero: " + expression);
		}
		else {
			this.simplify_pos_neg_operands(new_divised_operands, new_divisor_operands);
			if(CTypeAnalyzer.is_real(type)) {
				lconstant = this.compute_constant_div(lconstant, rconstant);
				rconstant = this.factory.new_constant(1);
			}
		}
		
		SymbolExpression loperand = this.accumulate_by_mul(expression.get_data_type(), new_divised_operands, lconstant);
		SymbolExpression roperand = this.accumulate_by_mul(expression.get_data_type(), new_divisor_operands, rconstant);
		
		if(loperand instanceof SymbolConstant) {
			if(this.is_zero_constant((SymbolConstant) loperand)) {
				return loperand;
			}
		}
		if(roperand instanceof SymbolConstant) {
			if(this.is_zero_constant((SymbolConstant) roperand)) {
				throw new ArithmeticException("Divided by zero: " + expression);
			}
			else if(this.is_pone_constant((SymbolConstant) roperand)) {
				return loperand;
			}
			else if(this.is_none_constant((SymbolConstant) roperand)) {
				return this.factory.new_arith_neg(loperand);
			}
		}
		
		return this.factory.new_arith_div(expression.get_data_type(), loperand, roperand);
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_mul(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_mul_or_div(this.factory.new_arith_mul(type, loperand, roperand));
	}
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_div(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		return this.compute_arith_mul_or_div(this.factory.new_arith_div(type, loperand, roperand));
	}
	
	/* arithmetic remainder (%) */
	/**
	 * @param type
	 * @param loperand
	 * @param roperand
	 * @return x % y
	 * @throws Exception
	 */
	protected SymbolExpression compute_arith_mod(CType type, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(roperand instanceof SymbolConstant) {
			long rvalue = ((SymbolConstant) roperand).get_long();
			if(rvalue == 0) {
				throw new ArithmeticException("Invalid mod: " + loperand + " % " + roperand);
			}
			else if(rvalue == 1 || rvalue == -1) {
				return this.factory.new_constant(0);
			}
		}
		
		if(loperand instanceof SymbolConstant) {
			long lvalue = ((SymbolConstant) loperand).get_long();
			if(lvalue == 0) {
				return this.factory.new_constant(0);
			}
			else if(lvalue == 1) {
				return this.factory.new_constant(1);
			}
		}
		
		if(loperand instanceof SymbolConstant) {
			if(roperand instanceof SymbolConstant) {
				long x = ((SymbolConstant) loperand).get_long();
				long y = ((SymbolConstant) roperand).get_long();
				return this.factory.new_constant(x % y);
			}
		}
		
		return this.factory.new_arith_mod(type, loperand, roperand);
	}
	
	/* bitwise and (&) */
	
	
	
}
