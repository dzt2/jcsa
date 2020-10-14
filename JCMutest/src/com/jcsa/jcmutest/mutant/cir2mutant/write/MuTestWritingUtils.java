package com.jcsa.jcmutest.mutant.cir2mutant.write;

import java.io.File;

import com.jcsa.jcparse.base.Complex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class MuTestWritingUtils {
	
	/**
	 * null			--> n@none
	 * Boolean 		--> b@true|false
	 * Character	-->	c@char_value
	 * Short		--> i@number
	 * Integer		--> i@number
	 * Long			--> i@number
	 * Float		--> f@number
	 * Double		-->	f@number
	 * Complex		--> x@number@number
	 * String		--> s@string(no_space)
	 * CKeyword		--> k@keyword.toString
	 * COperator	--> o@operator.toString
	 * CPunctuator	-->	p@punctuator.toString
	 * AstNode		--> a@ast_node.id
	 * CirNode		-->	r@cir_node.id
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	private static String encode_token(Object parameter) throws Exception {
		if(parameter == null) {
			return "n@none";
		}
		else if(parameter instanceof Boolean) {
			return "b@" + parameter.toString();
		}
		else if(parameter instanceof Character) {
			return "c@" + ((int) ((Character) parameter).charValue());
		}
		else if(parameter instanceof Short || 
				parameter instanceof Integer || 
				parameter instanceof Long) {
			return "i@" + parameter.toString();
		}
		else if(parameter instanceof Float || parameter instanceof Double) {
			return "f@" + parameter.toString();
		}
		else if(parameter instanceof Complex) {
			return "x@" + ((Complex) parameter).get_x() + 
					"@" + ((Complex) parameter).get_y();
		}
		else if(parameter instanceof CKeyword) {
			return "k@" + parameter.toString();
		}
		else if(parameter instanceof COperator) {
			return "o@" + parameter.toString();
		}
		else if(parameter instanceof CPunctuator) {
			return "p@" + parameter.toString();
		}
		else if(parameter instanceof AstNode) {
			return "a@" + ((AstNode) parameter).get_key();
		}
		else if(parameter instanceof CirNode) {
			return "r@" + ((CirNode) parameter).get_node_id();
		}
		else if(parameter instanceof String) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("s@"); 
			String text = parameter.toString();
			for(int k = 0; k < text.length(); k++) {
				char ch = text.charAt(k);
				if(!Character.isWhitespace(ch)) {
					buffer.append(ch);
				}
			}
			return buffer.toString();
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + parameter);
		}
	}
	
	/**
	 * @param constant
	 * @return
	 * @throws Exception
	 */
	private static String encode_constant(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:	return encode_token(constant.get_bool());
		case c_char:	return encode_token(constant.get_char());
		case c_short:	
		case c_ushort:	
		case c_int:		
		case c_uint:	return encode_token(constant.get_integer());
		case c_long:	
		case c_ulong:	
		case c_llong:	
		case c_ullong:	return encode_token(constant.get_long());
		case c_float:	return encode_token(constant.get_float());
		case c_double:	
		case c_ldouble:	return encode_token(constant.get_double());
		default: throw new IllegalArgumentException("Invalid constant");
		}
	}
	
	/**
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private static String encode_type(CType type) throws Exception {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:				return "void";
			case c_bool:				return "bool";
			case c_char:				return "char";
			case c_uchar:				return "unsigned char";
			case c_short:				return "short";
			case c_ushort:				return "unsigned short";
			case c_int:					return "int";
			case c_uint:				return "unsigned int";
			case c_long:				return "long";
			case c_ulong:				return "unsigned long";
			case c_llong:				return "long long";
			case c_ullong:				return "unsigned long long";
			case c_float:				return "float";
			case c_double:				return "double";
			case c_ldouble:				return "long double";
			case c_float_complex:		return "float _Complex";
			case c_double_complex:		return "double _Complex";
			case c_ldouble_complex:		return "long double _Complex";
			case c_float_imaginary:		return "floag _Imaginary";
			case c_double_imaginary:	return "double _Imaginary";
			case c_ldouble_imaginary:	return "long double _Imaginary";
			case gnu_va_list:			return "va_list";
			default: 					return "unknown";
			}
		}
		else if(type instanceof CArrayType) {
			int length = ((CArrayType) type).length();
			String child = encode_type(((CArrayType) type).get_element_type());
			if(length < 0) {
				return "(" + child + ")*";
			}
			else {
				return "(" + child + ")[" + length + "]";
			}
		}
		else if(type instanceof CPointerType) {
			return "(" + encode_type(((CPointerType) type).get_pointed_type()) + ")*";
		}
		else if(type instanceof CFunctionType) {
			return "(" + encode_type(((CFunctionType) type).get_return_type()) + ")#";
		}
		else if(type instanceof CStructType) {
			return ((CStructType) type).get_name();
		}
		else if(type instanceof CUnionType) {
			return ((CUnionType) type).get_name();
		}
		else if(type instanceof CEnumType) {
			return "int";
		}
		else if(type instanceof CQualifierType) {
			return encode_type(((CQualifierType) type).get_reference());
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	
	/**
	 * null			--> n@none
	 * Boolean 		--> b@true|false
	 * Character	-->	c@char_value
	 * Short		--> i@number
	 * Integer		--> i@number
	 * Long			--> i@number
	 * Float		--> f@number
	 * Double		-->	f@number
	 * Complex		--> x@number@number
	 * String		--> s@string(no_space)
	 * CKeyword		--> k@keyword.toString
	 * COperator	--> o@operator.toString
	 * CPunctuator	-->	p@punctuator.toString
	 * AstNode		--> a@ast_node.id
	 * CirNode		-->	r@cir_node.id
	 * CType		--> encode-algorithm
	 * CConstant	--> encode-children
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	public static String encode(Object parameter) throws Exception {
		if(parameter == null) {
			return "n@none";
		}
		else if(parameter instanceof CConstant) {
			return encode_constant((CConstant) parameter);
		}
		else if(parameter instanceof CType) {
			return encode_type((CType) parameter);
		}
		else {
			return encode_token(parameter);
		}
	}
	
	/**
	 * @param file
	 * @return file name without prefix and postfix
	 * @throws Exception
	 */
	public static String basename_without_postfix(File file) throws Exception {
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if(index >= 0)
			name = name.substring(0, index).strip();
		return name;
	}
	
}
