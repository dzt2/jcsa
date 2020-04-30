package com.jcsa.jcparse.lang.symb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SymMultiExpression extends SymExpression {
	
	/* attribute */
	/** operator to interpret **/
	private COperator operator;
	
	/* constructor */
	/**
	 * create an expression with multiple operands (more than 1)
	 * @param data_type
	 * @param operator
	 * @throws IllegalArgumentException
	 */
	protected SymMultiExpression(CType data_type, COperator operator) throws IllegalArgumentException {
		super(data_type);
		switch(operator) {
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_not:	this.operator = operator; break;
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	
	/* getters */
	/**
	 * get the operator to interpret the expression
	 * @return
	 */
	public COperator get_operator() { return this.operator; }
	/**
	 * get the number of operands in the expression
	 * @return
	 */
	public int number_of_operands() { return this.number_of_children(); }
	/**
	 * get the kth operand in the expression
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}
	
	/* setters */
	/**
	 * add an operand in the expression
	 * @param operand
	 * @throws IllegalArgumentException
	 */
	public void add_operand(SymExpression operand) throws IllegalArgumentException {
		this.add_child(operand);
	}
	
	private String operator_name() {
		switch(operator) {
		case arith_add:		return "+";
		case arith_mul:		return "*";
		case bit_and:		return "&";
		case bit_or:		return "|";
		case bit_xor:		return "^";
		case logic_and:		return "&&";
		case logic_or:		return "||";
		default: return null;
		}
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("(");
		for(int k = 0; k < this.number_of_children(); k++) {
			SymExpression operand = this.get_operand(k);
			buffer.append(operand.toString());
			if(k < this.number_of_children() - 1) {
				buffer.append(" ");
				buffer.append(this.operator_name());
				buffer.append(" ");
			}
		}
		buffer.append(")");
		
		return buffer.toString();
	}
	
	/* compress */
	/**
	 * divide the operands into constants [0] and variables [1]
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, List<SymExpression>> divide() throws Exception {
		/** 1. declarations **/
		Map<Integer, List<SymExpression>> results = 
				new HashMap<Integer, List<SymExpression>>();
		results.put(0, new ArrayList<SymExpression>());
		results.put(1, new ArrayList<SymExpression>());
		
		/** 2. divide into constants and variables **/
		for(int k = 0; k < this.number_of_operands(); k++) {
			SymExpression operand = this.get_operand(k);
			if(operand instanceof SymConstant) {
				results.get(0).add(operand);
			}
			else {
				results.get(1).add(operand);
			}
		}
		
		/** 3. disconnect from the parent **/
		this.clear_children(); return results;
	}
	private SymConstant arith_add(Iterable<SymExpression> operands) throws Exception {
		double summary = 0.0; boolean is_floating = false;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary + value;
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary + value;
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary + value;
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary + value;
			}
			break;
			case c_float:
			{
				double value = constant.get_float().doubleValue();
				summary = summary + value;
				is_floating = true;
			}
			break;
			case c_double:
			case c_ldouble:
			{
				double value = constant.get_double().doubleValue();
				summary = summary + value;
				is_floating = true;
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		if(!is_floating)
			return SymProcess.new_constant((long) summary);
		else return SymProcess.new_constant(summary);
	}
	private SymConstant arith_mul(Iterable<SymExpression> operands) throws Exception {
		double summary = 1.0; boolean is_floating = false;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary * value;
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary * value;
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary * value;
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary * value;
			}
			break;
			case c_float:
			{
				double value = constant.get_float().doubleValue();
				summary = summary * value;
				is_floating = true;
			}
			break;
			case c_double:
			case c_ldouble:
			{
				double value = constant.get_double().doubleValue();
				summary = summary * value;
				is_floating = true;
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		if(!is_floating)
			return SymProcess.new_constant((long) summary);
		else return SymProcess.new_constant(summary);
	}
	private SymConstant bitws_and(Iterable<SymExpression> operands) throws Exception {
		long summary = ~0L;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary & value;
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary & value;
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary & value;
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary & value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		return SymProcess.new_constant(summary);
	}
	private SymConstant bitws_ior(Iterable<SymExpression> operands) throws Exception {
		long summary = 0L;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary | value;
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary | value;
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary | value;
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary | value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		return SymProcess.new_constant(summary);
	}
	private SymConstant bitws_xor(Iterable<SymExpression> operands) throws Exception {
		long summary = 0L;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				long value = 0;
				if(constant.get_bool().booleanValue()) value++;
				summary = summary ^ value;
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary ^ value;
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary ^ value;
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary ^ value;
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		return SymProcess.new_constant(summary);
	}
	private SymConstant logic_and(Iterable<SymExpression> operands) throws Exception {
		boolean summary = true;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				summary = summary && constant.get_bool().booleanValue();
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary && (value != 0L);
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary && (value != 0L);
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary && (value != 0L);
			}
			break;
			case c_float:
			{
				double value = constant.get_float().doubleValue();
				summary = summary && (value != 0.0);
			}
			break;
			case c_double:
			case c_ldouble:
			{
				double value = constant.get_double().doubleValue();
				summary = summary && (value != 0.0);
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		return SymProcess.new_constant(summary);
	}
	private SymConstant logic_ior(Iterable<SymExpression> operands) throws Exception {
		boolean summary = false;
		
		for(SymExpression operand : operands) {
			CConstant constant = ((SymConstant) operand).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_bool:
			{
				summary = summary || constant.get_bool().booleanValue();
			}
			break;
			case c_char:
			case c_uchar:
			{
				long value = constant.get_char().charValue();
				summary = summary || (value != 0L);
			}
			break;
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			{
				long value = constant.get_integer().longValue();
				summary = summary || (value != 0L);
			}
			break;
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:
			{
				long value = constant.get_long().longValue();
				summary = summary || (value != 0L);
			}
			break;
			case c_float:
			{
				double value = constant.get_float().doubleValue();
				summary = summary || (value != 0.0);
			}
			break;
			case c_double:
			case c_ldouble:
			{
				double value = constant.get_double().doubleValue();
				summary = summary || (value != 0.0);
			}
			break;
			default: throw new IllegalArgumentException("Invalid operand");
			}
		}
		
		return SymProcess.new_constant(summary);
	}
	/**
	 * calculate the summary of the constants based on operator
	 * @param constants
	 * @return
	 * @throws Exception
	 */
	private SymConstant calculate(Iterable<SymExpression> constants) throws Exception {
		switch(this.operator) {
		case arith_add:	return this.arith_add(constants);
		case arith_mul:	return this.arith_mul(constants);
		case bit_and:	return this.bitws_and(constants);
		case bit_or:	return this.bitws_ior(constants);
		case bit_xor:	return this.bitws_xor(constants);
		case logic_and:	return this.logic_and(constants);
		case logic_or:	return this.logic_ior(constants);
		default: throw new IllegalArgumentException("Invalid operator: " + this.operator);
		}
	}
	private boolean match_with(SymConstant expression, long match) throws Exception {
		CConstant constant = expression.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			long value = 0;
			if(constant.get_bool().booleanValue())
				value++;
			return value == match;
		}
		case c_char:
		case c_uchar:
		{
			long value = constant.get_char().charValue();
			return value == match;
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			long value = constant.get_integer().longValue();
			return value == match;
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			long value = constant.get_long().longValue();
			return value == match;
		}
		case c_float:
		{
			double value = constant.get_float().doubleValue();
			return value == match;
		}
		case c_double:
		case c_ldouble:
		{
			double value = constant.get_double().doubleValue();
			return value == match;
		}
		default: throw new IllegalArgumentException("Invalid constant");
		}
	}
	/**
	 * whether the value can be decided from the constant
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private boolean decidable(SymConstant constant) throws Exception {
		switch(this.operator) {
		case arith_add:
		{
			return false;
		}
		case arith_mul:
		{
			return this.match_with(constant, 0L);
		}
		case bit_and:
		{
			return this.match_with(constant, 0L);
		}
		case bit_or:
		{
			return this.match_with(constant, ~0L);
		}
		case bit_xor:
		{
			return false;
		}
		case logic_and:
		{
			return !constant.get_constant().get_bool().booleanValue();
		}
		case logic_or:
		{
			return constant.get_constant().get_bool().booleanValue();
		}
		default: throw new IllegalArgumentException("Invalid " + this.operator);
		}
	}
	/**
	 * whether the constant can be ignored?
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private boolean ignorable(SymConstant constant) throws Exception {
		switch(this.operator) {
		case arith_add:
		{
			return this.match_with(constant, 0L);
		}
		case arith_mul:
		{
			return this.match_with(constant, 1L);
		}
		case bit_and:
		{
			return this.match_with(constant, ~0L);
		}
		case bit_or:
		{
			return this.match_with(constant, 0L);
		}
		case bit_xor:
		{
			return this.match_with(constant, 0L);
		}
		case logic_and:
		{
			return constant.get_constant().get_bool().booleanValue();
		}
		case logic_or:
		{
			return !constant.get_constant().get_bool().booleanValue();
		}
		default: throw new IllegalArgumentException("Invalid " + this.operator);
		}
	}
	/**
	 * compress this expression into simplified model
	 * @return
	 * @throws Exception
	 */
	protected SymExpression compress() throws Exception {
		/* 1. divide into variables and constants */
		Map<Integer, List<SymExpression>> divide_set = this.divide();
		
		/* 2. compress constants into one value */
		List<SymExpression> constants = divide_set.get(0);
		List<SymExpression> variables = divide_set.get(1);
		SymConstant constant = this.calculate(constants);
		
		/* 3. return decidable constant */
		if(variables.isEmpty()) this.add_operand(constant);
		else if(decidable(constant)) this.add_operand(constant);
		/* 4. combine with the variable operands */
		else {
			if(!this.ignorable(constant))
				this.add_operand(constant);
			for(SymExpression variable : variables)
				this.add_operand(variable);
		}
		
		/* 5. rebuild the expression or its unique operand */
		if(this.number_of_children() == 1) {
			SymExpression operand = this.get_operand(0);
			this.clear_children(); return operand;
		}
		else {
			return this;
		}
	}
	
}
