package com.jcsa.jcmutest.mutant.sta2mutant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;

/**
 * 	It provides the basic methods for supporting state based mutation analysis.	<br>
 * 	
 * 	@author yukimula
 *
 */
public final class StateMutations {
	
	/* definitions */
	/** {true, false} **/
	public static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	/** integer or double **/
	public static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	/** { x | x > 0 } **/
	public static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	/** { x | x < 0 } **/
	public static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	/** { x | x <= 0 } **/
	public static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	/** { x | x >= 0 } **/
	public static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	/** { x | x != 0 } **/
	public static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	/** { x | x == 0 } **/
	public static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	/** address value in pointer **/
	public static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	/** the automatic type value **/
	public static final SymbolExpression auto_value = SymbolFactory.variable(CBasicTypeImpl.void_type, "@AutoValue");
	/** value for the exceptions **/
	public static final SymbolExpression trap_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");	
	
	/* symbolic */
	/**
	 * @param source
	 * @return whether the source is trap_value equivalent
	 */
	private static boolean is_trap_value(SymbolNode source) {
		if(source instanceof SymbolIdentifier) {
			return source.equals(trap_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression contains trap_value
	 */
	public static boolean has_trap_value(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_trap_value(parent)) {
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param source
	 * @return whether the source is of any abstract domain value
	 */
	private static boolean is_abst_value(SymbolNode source) {
		if(source instanceof SymbolIdentifier) {
			return source.equals(bool_value) || source.equals(numb_value) || 
					source.equals(post_value) || source.equals(npos_value) || 
					source.equals(negt_value) || source.equals(nneg_value) || 
					source.equals(nzro_value) || source.equals(addr_value);
		}
		else {
			return false;
		}
	}
	/**
	 * @param source
	 * @return whether the expression contains absolute domain value in.
	 */
	public static boolean has_abst_value(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				if(is_abst_value(parent)) {
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param expression	the symbolic expression being evaluated based on IO contexts
	 * @param in_context	the context that provide the input states for the evaluation
	 * @param ou_context	the context that provide the output state for the evaluation
	 * @return				the symbolic result of evaluating expression; and trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression, 
			SymbolContext in_context, SymbolContext ou_context) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) { return trap_value; }
		else if(has_abst_value(expression)) { return expression; }
		else {
			try {
				return expression.evaluate(in_context, ou_context);
			}
			catch(ArithmeticException ex) {
				return trap_value;
			}
		}
	}
	/**
	 * @param expression
	 * @return the abstract value that best matches with the expression
	 * @throws Exception
	 */
	public static SymbolExpression get_domain(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) { return trap_value; }
		else if(is_abst_value(expression)) { return expression; }
		else if(SymbolFactory.is_bool(expression)) { return bool_value; }
		else if(expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) expression).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					return post_value;
				}
				else if(value < 0) {
					return negt_value;
				}
				else {
					return zero_value;
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					return post_value;
				}
				else if(value < 0) {
					return negt_value;
				}
				else {
					return zero_value;
				}
			}
		}
		else if(SymbolFactory.is_char(expression)) { return nneg_value; }
		else if(SymbolFactory.is_usig(expression)) { return nneg_value; }
		else if(SymbolFactory.is_sign(expression)) { return numb_value; }
		else if(SymbolFactory.is_real(expression)) { return numb_value; }
		else if(SymbolFactory.is_addr(expression)) { return addr_value; }
		else { return auto_value; }
	}
	/**
	 * @param expression
	 * @return the set of abstract values as the domains directly subsumed by the expression
	 * @throws Exception
	 */
	public static Collection<SymbolExpression> next_domains(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			List<SymbolExpression> domains = new ArrayList<SymbolExpression>();
			if(expression.equals(trap_value)) { /* no more domain */ }
			else if(has_trap_value(expression)) { domains.add(trap_value); }
			else if(expression.equals(bool_value)) { /* no more domain */ }
			else if(expression.equals(post_value)) { domains.add(nneg_value); domains.add(nzro_value); }
			else if(expression.equals(negt_value)) { domains.add(npos_value); domains.add(nzro_value); }
			else if(expression.equals(zero_value)) { domains.add(npos_value); domains.add(nneg_value); }
			else if(expression.equals(npos_value)) { domains.add(numb_value); }
			else if(expression.equals(nneg_value)) { domains.add(numb_value); }
			else if(expression.equals(nzro_value)) { domains.add(numb_value); }
			else if(expression.equals(numb_value)) { /* no more domain */ }
			else if(expression.equals(addr_value)) { /* no more domain */ }
			else if(expression.equals(auto_value)) { /* no more domain */ }
			else { domains.add(get_domain(expression)); }
			return domains;
		}
	}
	/**
	 * @param expression
	 * @return all the abstract value domains available for the input expression
	 * @throws Exception
	 */
	public static Collection<SymbolExpression> all_domains(SymbolExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			Set<SymbolExpression> domains = new HashSet<SymbolExpression>();
			Queue<SymbolExpression> queue = new LinkedList<SymbolExpression>();
			queue.add(expression);
			while(!queue.isEmpty()) {
				SymbolExpression value = queue.poll();
				if(is_abst_value(value) || is_trap_value(value)) {
					domains.add(value);
				}
				for(SymbolExpression next_domain : next_domains(value)) {
					queue.add(next_domain);
				}
			}
			return domains;
		}
	}
	
	/* data type classification */
	/**
	 * @param data_type
	 * @return null if the original data type is invalid
	 */ 
	private static CType get_normalized_type(CType data_type) {
		return SymbolFactory.get_type(data_type);
	}
	/**
	 * @param data_type
	 * @return whether the data type is void
	 */
	public static boolean is_void(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is boolean
	 */
	public static boolean is_boolean(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return true iff. {uchar|ushort|uint|ulong}
	 */
	public static boolean is_usigned(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public static boolean is_integer(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else if(data_type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public static boolean is_doubles(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble: return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is integer or real
	 */
	public static boolean is_numeric(CType data_type) {
		return is_integer(data_type) || is_doubles(data_type);
	}
	/**
	 * @param data_type
	 * @return whether the data type is a address pointer
	 */
	public static boolean is_address(CType data_type) {
		data_type = get_normalized_type(data_type);
		return data_type instanceof CArrayType || 
				data_type instanceof CPointerType;
	}
	
	/* location classifier */
	/**
	 * @param expression
	 * @return
	 */
	public static boolean is_void(CirExpression expression) {
		return is_void(expression.get_data_type());
	}
	/**
	 * @param location
	 * @return whether the expression is a boolean
	 */
	public static boolean is_boolean(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(is_boolean(expression.get_data_type())) {
			return true;
		}
		else if(expression.get_parent() instanceof CirStatement) {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement || statement instanceof CirCaseStatement;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a unsigned integer
	 */
	public static boolean is_usigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usigned(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is an integer
	 */
	public static boolean is_integer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real
	 */
	public static boolean is_doubles(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_doubles(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real or integer
	 */
	public static boolean is_numeric(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression) || is_doubles(expression);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a pointer
	 */
	public static boolean is_address(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_address(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a reference defined in left-side of assignment
	 */
	public static boolean is_assigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			return statement.get_lvalue() == expression;
		}
		else {
			return false;
		}
	}
	
}
