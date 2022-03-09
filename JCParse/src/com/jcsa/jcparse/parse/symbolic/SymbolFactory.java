package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.COperator;


/**
 * It implements the construction and generation of symbolic expressions.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* definitions */
	/** the template used to support sizeof-operation **/
	private CRunTemplate 	template;
	/** true if to transform the default value based on their data types **/
	private boolean			optimize;
	/**
	 * private constructor for the symbol node generation and parsing
	 */
	private SymbolFactory() { this.template = null; this.optimize = false; }
	
	/* singleton mode */
	/** the factory is used to create data type in symbolic expression **/
	public static final CTypeFactory type_factory = new CTypeFactory();
	/** the factory instance for singleton mode **/
	private static final SymbolFactory symb_factory = new SymbolFactory();
	/**
	 * It sets the parameters used to parsing algorithm of this instance
	 * @param template	the template used to support sizeof-operation
	 * @param optimize	true if to transform the default value based on their data types
	 */
	private void configure(CRunTemplate template, boolean optimize) {
		this.template = template; this.optimize = optimize;
	}
	/**
	 * It sets the parameters used to parsing algorithm of this singleton instance
	 * @param template	the template used to support sizeof-operation
	 * @param optimize	true if to transform the default value based on their data types
	 */
	public static void set_config(CRunTemplate template, boolean optimize) {
		symb_factory.configure(template, optimize);
	}
	
	/* type classifier */
	/**
	 * @param type
	 * @return the value type without qualifiers
	 */
	public static CType   get_type(CType type) {
		if(type == null) {
			return CBasicTypeImpl.void_type;
		}
		else {
			while(type instanceof CQualifierType) {
				type = ((CQualifierType) type).get_reference();
			}
			return type;
		}
	}
	/**
	 * @param type
	 * @return void | null
	 */
	public static boolean is_void(CType type) {
		type = get_type(type);
		if(type == null) {
			return true;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return bool
	 */
	public static boolean is_bool(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return char | uchar
	 */
	public static boolean is_char(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_uchar:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return char|short|int|long|llong|enum
	 */
	public static boolean is_sign(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_char:
			case c_short:
			case c_int:
			case c_long:
			case c_llong:	return true;
			default:		return false;
			}
		}
		else if(type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return uchar|ushort|uint|ulong|ullong
	 */
	public static boolean is_usig(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return char|uchar|short|ushort|int|uint|long|ulong|llong|ullong
	 */
	public static boolean is_numb(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
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
		else if(type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return float | double | ldouble
	 */
	public static boolean is_real(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return array | pointer
	 */
	public static boolean is_addr(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CArrayType || type instanceof CPointerType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param type
	 * @return struct | union | function
	 */
	public static boolean is_auto(CType type) {
		type = get_type(type);
		if(type == null) {
			return false;
		}
		else if(type instanceof CStructType || 
				type instanceof CUnionType ||
				type instanceof CFunctionType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return void
	 */
	public static boolean is_void(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_void(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return void | logic | relational
	 */
	public static boolean is_bool(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_bool(expression.get_data_type());		
		}
	}
	/**
	 * @param expression
	 * @return char | uchar
	 */
	public static boolean is_char(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_char(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return char|short|int|long|llong
	 */
	public static boolean is_sign(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_sign(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return uchar|ushort|uint|ulong|ullong
	 */
	public static boolean is_usig(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usig(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return char|uchar|short|ushort|int|uint|long|ulong|llong|ullong
	 */
	public static boolean is_numb(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_numb(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return float | double | ldouble
	 */
	public static boolean is_real(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_real(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return array | pointer
	 */
	public static boolean is_addr(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_addr(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return function | struct | union
	 */
	public static boolean is_auto(SymbolExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_auto(expression.get_data_type());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
