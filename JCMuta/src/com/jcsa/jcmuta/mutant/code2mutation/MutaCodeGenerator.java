package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.text.CText;

/**
 * Used to generate the source code of mutation program.
 * 
 * @author yukimula
 *
 */
public abstract class MutaCodeGenerator {
	
	/* code constants */
	private static final int line_increment = 9;
	protected static final int maximal_code_length = 32;
	protected static final String mutant_line_comment = " /* mutated line */ ";
	protected static final String include_jcmulib = "\n#include \"jcmulib.h\"\n\n";
	protected static final String mutation_comment = 
			"/*\n"
			+ " * \tclass: \t%s\n"
			+ " * \toperator: \t%s\n"
			+ " * \tlocation: \tat line %d \"%s\"\n"
			+ " * \tparameter: %s\n"
			+ " */\n";
	
	/* property */
	/** buffer to preserve the generated code **/
	protected StringBuilder buffer;
	
	/* constructor */
	/**
	 * create a generator for generating mutation code
	 */
	protected MutaCodeGenerator() {
		this.buffer = new StringBuilder();
	}
	
	/* generation */
	/**
	 * generate the source code for mutation program
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public String generate(AstMutation mutation, MutationCodeType type) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else {
			this.buffer.setLength(0);
			this.generate_muta_comment(mutation);
			this.generate_head(mutation);
			switch(type) {
			case Coverage:	this.generate_coverage_code(mutation); break;
			case Weakness:	this.generate_weakness_code(mutation); break;
			case Stronger:	this.generate_stronger_code(mutation); break;
			default: throw new IllegalArgumentException("Invalid type: null");
			}
			return this.buffer.toString();
		}
		
	}
	
	/* generation methods */
	/**
	 * get the parameter of the mutation
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	private String get_parameter_string(Object parameter) throws Exception {
		if(parameter == null) {
			return "";
		}
		else if(parameter instanceof AstNode) {
			AstNode location = (AstNode) parameter;
			int line = location.get_location().line_of();
			String location_code = location.get_location().trim_code(maximal_code_length);
			if(location_code.length() > maximal_code_length) {
				location_code = location_code + "......";
			}
			return "at line " + line + " \"" + location_code + "\"";
		}
		else if(parameter instanceof CName) {
			return ((CName) parameter).get_name();
		}
		else if(parameter instanceof CConstant) {
			CBasicType type = ((CConstant) parameter).get_type();
			
			Object value;
			switch(type.get_tag()) {
			case c_bool:
				value = ((CConstant) parameter).get_bool(); break;
			case c_char: case c_uchar:
				value = (int) (((CConstant) parameter).get_char()); break;
			case c_short: case c_ushort:
			case c_int: case c_uint:
				value = ((CConstant) parameter).get_integer(); break;
			case c_long: case c_ulong:
			case c_llong: case c_ullong:
				value = ((CConstant) parameter).get_long(); break;
			case c_float:
				value = ((CConstant) parameter).get_float(); break;
			case c_double: case c_ldouble:
				value = ((CConstant) parameter).get_double(); break;
			default: throw new IllegalArgumentException("Invalid type: " + type.get_tag());
			}
			
			return value.toString();
		}
		else return parameter.toString();
	}
	/**
	 * generate the comment of mutation information
	 * @param mutation
	 * @throws Exception
	 */
	private void generate_muta_comment(AstMutation mutation) throws Exception {
		String class_name = mutation.get_mutation_class().toString();
		String operator_name = mutation.get_mutation_operator().toString();
		AstNode location = mutation.get_location();
		int line = location.get_location().line_of() + line_increment;
		String location_code = location.get_location().trim_code(maximal_code_length);
		if(location_code.length() > maximal_code_length) {
			location_code = location_code + "......";
		}
		String parameter_str = this.get_parameter_string(mutation.get_parameter());
		
		String code = String.format(mutation_comment, 
				class_name, operator_name, line, location_code, parameter_str);
		this.buffer.append(code);
	}
	/**
	 * generate the #include head_file
	 * @param mutation
	 * @throws Exception
	 */
	private void generate_head(AstMutation mutation) throws Exception {
		this.buffer.append(include_jcmulib);
	}
	
	/* implementation methods */
	/**
	 * generate mutation code for coverage testing
	 * @param mutation
	 * @throws Exception
	 */
	protected abstract void generate_coverage_code(AstMutation mutation) throws Exception;
	/**
	 * generate mutation code for weak mutation test
	 * @param mutation
	 * @throws Exception
	 */
	protected abstract void generate_weakness_code(AstMutation mutation) throws Exception;
	/**
	 * generate mutation code for strong mutation
	 * @param mutation
	 * @throws Exception
	 */
	protected abstract void generate_stronger_code(AstMutation mutation) throws Exception;
	
	/* utility methods */
	/**
	 * get the character of data type [i|u|f]
	 * @param data_type
	 * @return
	 * @throws Exception
	 */
	protected char get_data_type_tag(CType data_type) throws Exception {
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:
			case c_char: 
			case c_short:
			case c_int:
			case c_long:
			case c_llong:		return 'i';
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:		return 'u';
			case c_float:
			case c_double:
			case c_ldouble:		return 'f';
			default: throw new IllegalArgumentException("Invalid: " + data_type);
			} 
		}
		else if(data_type instanceof CArrayType) {
			return 'i';
		}
		else if(data_type instanceof CPointerType) {
			return 'i';
		}
		else if(data_type instanceof CEnumType) {
			return 'i';
		}
		else {
			throw new IllegalArgumentException("Unsupport type: " + data_type);
		}
	}
	/**
	 * get the maximal tag of data type
	 * @param type1
	 * @param type2
	 * @return
	 * @throws Exception
	 */
	protected char get_data_type_tag(CType type1, CType type2) throws Exception {
		char c1 = this.get_data_type_tag(type1);
		char c2 = this.get_data_type_tag(type1);
		
		if(c1 == 'i') {
			if(c2 == 'i') 
				return 'i';
			else if(c2 == 'u')
				return 'u';
			else
				return 'f';
		}
		else if(c1 == 'u') {
			if(c2 == 'i') 
				return 'u';
			else if(c2 == 'u')
				return 'u';
			else
				return 'f';
		}
		else {
			return 'f';
		}
	}
	/**
	 * replace the location in code as specified replaced text
	 * @param ast_tree
	 * @param location
	 * @param replace
	 * @throws Exception
	 */
	protected void replace_muta_code(AstNode location, String replace) throws Exception {
		AstTree ast_tree = location.get_tree();
		CText text = ast_tree.get_source_code();
		int beg = location.get_location().get_bias();
		int end = beg + location.get_location().get_length();
		boolean first = true;
		
		/** previous **/
		for(int k = 0; k < beg; k++) {
			this.buffer.append(text.get_char(k));
		}
		
		/** replacement **/
		for(int k = 0; k < replace.length(); k++) {
			if(first && replace.charAt(k) == '\n') {
				first = false;
				this.buffer.append(mutant_line_comment);
			}
			this.buffer.append(replace.charAt(k));
		}
		
		/** postfix **/
		for(int k = end; k < text.length(); k++) {
			if(first && text.get_char(k) == '\n') {
				first = false; 
				this.buffer.append(mutant_line_comment);
			}
			this.buffer.append(text.get_char(k));
		}
		
		/** EOF **/
		if(first) {
			this.buffer.append(mutant_line_comment);
		}
	}
	/**
	 * generate the code representing the data type to be casted
	 * @param data_type
	 * @return
	 * @throws Exception
	 */
	protected String get_data_type_code(CType data_type) throws Exception {
		data_type = CTypeAnalyzer.get_value_type(data_type);
		
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:	return "void";
			case c_bool:	return "int";
			case c_char:	return "char";
			case c_uchar:	return "unsigned char";
			case c_short:	return "short";
			case c_ushort:	return "unsigned short";
			case c_int:		return "int";
			case c_uint:	return "unsigned int";
			case c_long:	return "long";
			case c_ulong:	return "unsigned long";
			case c_llong:	return "long long";
			case c_ullong:	return "unsigned long long";
			case c_float:	return "float";
			case c_double:	return "double";
			case c_ldouble:	return "long double";
			default: throw new IllegalArgumentException("Invalid data_type");
			}
		}
		else if(data_type instanceof CArrayType) {
			String code = this.get_data_type_code(((CArrayType) data_type).get_element_type());
			return code + " *";
		}
		else if(data_type instanceof CPointerType) {
			String code = this.get_data_type_code(((CPointerType) data_type).get_pointed_type());
			return code + " *";
		}
		else if(data_type instanceof CStructType) {
			return "struct " + ((CStructType) data_type).get_name();
		}
		else if(data_type instanceof CUnionType) {
			return "union " + ((CStructType) data_type).get_name();
		}
		else if(data_type instanceof CEnumType) {
			return "int";
		}
		else throw new IllegalArgumentException("Unknown: " + data_type);
	}
	/**
	 * get the standard name of operator
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	protected String get_operator_name(COperator operator) throws Exception {
		switch(operator) {
		case arith_add:				return "arith_add";
		case arith_sub:				return "arith_sub";
		case arith_mul:				return "arith_mul";
		case arith_div:				return "arith_div";
		case arith_mod:				return "arith_mod";
		case bit_and:				return "bitws_and";
		case bit_or:				return "bitws_ior";
		case bit_xor:				return "bitws_xor";
		case left_shift:			return "bitws_lsh";
		case righ_shift:			return "bitws_rsh";
		case logic_and:				return "logic_and";
		case logic_or:				return "logic_ior";
		case greater_tn:			return "greater_tn";
		case greater_eq:			return "greater_eq";
		case smaller_tn:			return "smaller_tn";
		case smaller_eq:			return "smaller_eq";
		case equal_with:			return "equal_with";
		case not_equals:			return "not_equals";
		case assign:				return "assign";
		case arith_add_assign:		return "arith_add_assign";
		case arith_sub_assign:		return "arith_sub_assign";
		case arith_mul_assign:		return "arith_mul_assign";
		case arith_div_assign:		return "arith_div_assign";
		case arith_mod_assign:		return "arith_mod_assign";
		case bit_and_assign:		return "bitws_and_assign";
		case bit_or_assign:			return "bitws_ior_assign";
		case bit_xor_assign:		return "bitws_xor_assign";
		case left_shift_assign:		return "bitws_lsh_assign";
		case righ_shift_assign:		return "bitws_rsh_assign";
		default: throw new IllegalArgumentException("Unsupport: " + operator);
		}
	}
	protected String get_operator_type(COperator operator) throws Exception {
		switch(operator) {
		case arith_add:				return "arith";
		case arith_sub:				return "arith";
		case arith_mul:				return "arith";
		case arith_div:				return "arith";
		case arith_mod:				return "arith";
		case bit_and:				return "bitws";
		case bit_or:				return "bitws";
		case bit_xor:				return "bitws";
		case left_shift:			return "bitws";
		case righ_shift:			return "bitws";
		case logic_and:				return "logic";
		case logic_or:				return "logic";
		case greater_tn:			return "relation";
		case greater_eq:			return "relation";
		case smaller_tn:			return "relation";
		case smaller_eq:			return "relation";
		case equal_with:			return "relation";
		case not_equals:			return "relation";
		case assign:				return "assign";
		case arith_add_assign:		return "arith";
		case arith_sub_assign:		return "arith";
		case arith_mul_assign:		return "arith";
		case arith_div_assign:		return "arith";
		case arith_mod_assign:		return "arith";
		case bit_and_assign:		return "bitws";
		case bit_or_assign:			return "bitws";
		case bit_xor_assign:		return "bitws";
		case left_shift_assign:		return "bitws";
		case righ_shift_assign:		return "bitws";
		default: throw new IllegalArgumentException("Unsupport: " + operator);
		}
	}
	protected String get_operator_code(String operator_name) throws Exception {
		if(operator_name.equals("arith_add"))
			return "+";
		else if(operator_name.equals("arith_sub"))
			return "-";
		else if(operator_name.equals("arith_mul"))
			return "*";
		else if(operator_name.equals("arith_div"))
			return "/";
		else if(operator_name.equals("arith_mod"))
			return "%";
		else if(operator_name.equals("bitws_and"))
			return "&";
		else if(operator_name.equals("bitws_ior"))
			return "|";
		else if(operator_name.equals("bitws_xor"))
			return "^";
		else if(operator_name.equals("bitws_lsh"))
			return "<<";
		else if(operator_name.equals("bitws_rsh"))
			return ">>";
		else if(operator_name.equals("logic_and"))
			return "&&";
		else if(operator_name.equals("logic_ior"))
			return "||";
		else if(operator_name.equals("greater_tn"))
			return ">";
		else if(operator_name.equals("greater_eq"))
			return ">=";
		else if(operator_name.equals("smaller_tn"))
			return "<";
		else if(operator_name.equals("smaller_eq"))
			return "<=";
		else if(operator_name.equals("equal_with"))
			return "==";
		else if(operator_name.equals("not_equals"))
			return "!=";
		else if(operator_name.equals("assign"))
			return "=";
		else if(operator_name.equals("arith_add_assign"))
			return "+=";
		else if(operator_name.equals("arith_sub_assign"))
			return "-=";
		else if(operator_name.equals("arith_mul_assign"))
			return "*=";
		else if(operator_name.equals("arith_div_assign"))
			return "/=";
		else if(operator_name.equals("arith_mod_assign"))
			return "%=";
		else if(operator_name.equals("bitws_and_assign"))
			return "&=";
		else if(operator_name.equals("bitws_ior_assign"))
			return "|=";
		else if(operator_name.equals("bitws_xor_assign"))
			return "^=";
		else if(operator_name.equals("bitws_lsh_assign"))
			return "<<=";
		else if(operator_name.equals("bitws_rsh_assign"))
			return ">>=";
		else throw new IllegalArgumentException("Unknown " + operator_name);
	}
	
}
