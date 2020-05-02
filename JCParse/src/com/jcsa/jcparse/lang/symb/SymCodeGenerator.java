package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CParameterTypeList;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * Used to generate code to describe symbolic expression.
 * 
 * @author yukimula
 *
 */
public class SymCodeGenerator {
	
	/** to control whether the name is printed in its simplified version **/
	private static boolean simple_print = false;
	/** to preserve the code in symbolic expression **/
	private static final StringBuilder buffer = new StringBuilder();
	
	/* implementation methods */
	/**
	 * generate code within the source of symbolic expression
	 * @param source
	 * @throws Exception
	 */
	private static void gen(SymNode source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(source instanceof SymAddress)
			gen_address((SymAddress) source);
		else if(source instanceof SymConstant)
			gen_constant((SymConstant) source);
		else if(source instanceof SymLiteral)
			gen_literal((SymLiteral) source);
		else if(source instanceof SymDefaultValue)
			gen_default_value((SymDefaultValue) source);
		else if(source instanceof SymUnaryExpression)
			gen_unary_expression((SymUnaryExpression) source);
		else if(source instanceof SymBinaryExpression)
			gen_binary_expression((SymBinaryExpression) source);
		else if(source instanceof SymMultiExpression)
			gen_multi_expression((SymMultiExpression) source);
		else if(source instanceof SymFieldExpression)
			gen_field_expression((SymFieldExpression) source);
		else if(source instanceof SymField)
			gen_field((SymField) source);
		else if(source instanceof SymInvocateExpression)
			gen_invocate_expression((SymInvocateExpression) source);
		else if(source instanceof SymArgumentList)
			gen_argument_list((SymArgumentList) source);
		else if(source instanceof SymSequenceExpression)
			gen_sequence((SymSequenceExpression) source);
		else 
			throw new IllegalArgumentException("Unsupport: " + source.getClass().getSimpleName());
	}
	
	/* data type generator */
	private static void gen_type(CType type) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(type instanceof CBasicType)
			gen_basic_type((CBasicType) type);
		else if(type instanceof CArrayType)
			gen_array_type((CArrayType) type);
		else if(type instanceof CPointerType)
			gen_point_type((CPointerType) type);
		else if(type instanceof CFunctionType) 
			gen_function_type((CFunctionType) type);
		else if(type instanceof CStructType)
			gen_struct_type((CStructType) type);
		else if(type instanceof CUnionType)
			gen_union_type((CUnionType) type);
		else if(type instanceof CEnumType)
			gen_enum_type((CEnumType) type);
		else if(type instanceof CQualifierType)
			gen_qualifier_type((CQualifierType) type);
		else 
			throw new IllegalArgumentException("Invalid as: " + type);
	}
	private static void gen_basic_type(CBasicType type) throws Exception {
		switch(type.get_tag()) {
		case c_void:	buffer.append("void"); break;
		case c_bool:	buffer.append("bool"); break;
		case c_char:	buffer.append("char"); break;
		case c_uchar:	buffer.append("unsigned char"); break;
		case c_short:	buffer.append("short"); break;
		case c_ushort:	buffer.append("unsigned short"); break;
		case c_int:		buffer.append("int");	break;
		case c_uint:	buffer.append("unsigned int"); break;	
		case c_long:	buffer.append("long"); break;
		case c_ulong:	buffer.append("unsigned long"); break;
		case c_llong:	buffer.append("long long"); break;
		case c_ullong:	buffer.append("unsigned long long"); break;
		case c_float:	buffer.append("float"); break;
		case c_double:	buffer.append("double"); break;
		case c_ldouble:	buffer.append("long double"); break;
		case gnu_va_list:	buffer.append("va_list"); break;
		default: throw new IllegalArgumentException("Invalid: " + type.get_tag());
		}
	}
	private static void gen_array_type(CArrayType type) throws Exception {
		gen_type(type.get_element_type());
		if(type.length() > 0)
			buffer.append("[").append(type.length()).append("]");
		else
			buffer.append("*");
	}
	private static void gen_point_type(CPointerType type) throws Exception {
		gen_type(type.get_pointed_type());
		buffer.append("*");
	}
	private static void gen_function_type(CFunctionType type) throws Exception {
		gen_type(type.get_return_type());
		buffer.append(" (");
		CParameterTypeList tlist = type.get_parameter_types();
		for(int k = 0; k < tlist.size(); k++) {
			gen_type(tlist.get_parameter_type(k));
			if(k < tlist.size() - 1) buffer.append(", ");
		}
		buffer.append(")");
	}
	private static void gen_struct_type(CStructType type) throws Exception {
		buffer.append("struct ");
		String name = type.get_name();
		if(name == null || name.isBlank())
			name = "$" + type.hashCode();
		buffer.append(name);
	}
	private static void gen_union_type(CUnionType type) throws Exception {
		buffer.append("union ");
		String name = type.get_name();
		if(name == null || name.isBlank())
			name = "$" + type.hashCode();
		buffer.append(name);
	}
	private static void gen_enum_type(CEnumType type) throws Exception {
		buffer.append("enum ");
		String name = type.get_name();
		if(name == null || name.isBlank())
			name = "$" + type.hashCode();
		buffer.append(name);
	}
	private static void gen_qualifier_type(CQualifierType type) throws Exception {
		gen_type(type.get_reference());
	}
	
	/* basic-expression nodes */
	private static void gen_address(SymAddress source) throws Exception {
		String name = source.get_address();
		if(simple_print) {
			int index = name.indexOf('#');
			if(index > 0) {
				name = name.substring(0, index).strip();
			}
		}
		buffer.append(name);
	}
	private static void gen_constant(SymConstant source) throws Exception {
		CConstant constant = source.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			buffer.append(constant.get_bool().booleanValue());
		}
		break;
		case c_char:
		case c_uchar:
		{
			buffer.append("\'").append(constant.get_char().charValue()).append("\'");
		}
		break;
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			buffer.append(constant.get_integer().intValue());
		}
		break;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			buffer.append(constant.get_long().longValue());
		}
		break;
		case c_float:
		{
			buffer.append(constant.get_float().floatValue());
		}
		break;
		case c_double:
		case c_ldouble:
		{
			buffer.append(constant.get_double().doubleValue());
		}
		break;
		default: throw new IllegalArgumentException("Invalid constant: " + constant);
		}
	}
	private static void gen_literal(SymLiteral source) throws Exception {
		buffer.append("\"").append(source.get_literal()).append("\"");
	}
	private static void gen_default_value(SymDefaultValue source) throws Exception {
		buffer.append("[?]");
	}
	
	/* composite expression */
	private static void gen_unary_expression(SymUnaryExpression source) throws Exception {
		switch(source.get_operator()) {
		case positive: 		break;
		case negative: 		buffer.append("-"); break;
		case bit_not:		buffer.append("~");	break;
		case logic_not:		buffer.append("!");	break;
		case address_of:	buffer.append("&"); break;
		case dereference:	buffer.append("*"); break;
		case assign:		
			buffer.append("("); 
			gen_type(source.get_data_type()); 
			buffer.append(") "); 
			break;
		default: throw new IllegalArgumentException("Invalid operator: " + source.get_operator());
		}
		buffer.append("(");
		gen(source.get_operand());
		buffer.append(")");
	}
	private static void gen_binary_expression(SymBinaryExpression source) throws Exception {
		gen(source.get_loperand());
		
		switch(source.get_operator()) {
		case arith_sub:		buffer.append(" - ");	break;
		case arith_div:		buffer.append(" / ");	break;
		case arith_mod:		buffer.append(" % ");	break;
		case left_shift:	buffer.append(" << ");	break;
		case righ_shift:	buffer.append(" >> ");	break;
		case greater_tn:	buffer.append(" > ");	break;
		case greater_eq:	buffer.append(" >= ");	break;
		case smaller_tn:	buffer.append(" < ");	break;
		case smaller_eq:	buffer.append(" <= ");	break;
		case not_equals:	buffer.append(" != ");	break;
		case equal_with:	buffer.append(" == ");	break;
		default: throw new IllegalArgumentException("Invalid operator: " + source.get_operator());
		}
		
		gen(source.get_roperand());
	}
	private static void gen_multi_expression(SymMultiExpression source) throws Exception {
		String operator;
		switch(source.get_operator()) {
		case arith_add:	operator = " + "; break;
		case arith_mul:	operator = " * "; break;
		case bit_and:	operator = " & "; break;
		case bit_or:	operator = " | "; break;
		case bit_xor:	operator = " ^ "; break;
		case logic_and:	operator = " && "; break;
		case logic_or:	operator = " || "; break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_operator());
		}
		
		for(int k = 0; k < source.number_of_operands(); k++) {
			gen(source.get_operand(k));
			
			if(k < source.number_of_operands() - 1)
				buffer.append(operator);
		}
	}
	
	/* special expressions */
	private static void gen_field(SymField source) throws Exception {
		buffer.append(source.get_name());
	}
	private static void gen_field_expression(SymFieldExpression source) throws Exception {
		gen(source.get_body());
		buffer.append(".");
		gen(source.get_field());
	}
	private static void gen_invocate_expression(SymInvocateExpression source) throws Exception {
		gen(source.get_function());
		gen(source.get_argument_list());
	}
	private static void gen_argument_list(SymArgumentList source) throws Exception {
		buffer.append("(");
		for(int k = 0; k < source.number_of_arguments(); k++) {
			gen(source.get_argument(k));
			if(k < source.number_of_arguments() - 1) {
				buffer.append(", ");
			}
		}
		buffer.append(")");
	}
	private static void gen_sequence(SymSequenceExpression source) throws Exception {
		buffer.append("{");
		for(int k = 0; k < source.number_of_elements(); k++) {
			gen(source.get_element(k));
			if(k < source.number_of_elements() - 1)
				buffer.append(", ");
		}
		buffer.append("}");
	}
	
	/**
	 * generate the code of symbolic expression
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static String generate(SymNode source) throws Exception {
		buffer.setLength(0);
		gen(source);
		return buffer.toString();
	}
	/**
	 * set the code printed contianing simple name of address
	 */
	public static void set_simple_print() { simple_print = true; }
	/**
	 * set the code printed containing complete name of address
	 */
	public static void set_complex_print() { simple_print = false; }
	
}
