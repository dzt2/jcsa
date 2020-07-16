package com.jcsa.jcparse.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

/**
 * The template defines the information to build up the running environment
 * for executing programs written in C.
 * 
 * @author yukimula
 *
 */
public class CRunTemplate {
	
	/* parameters to build up environment */
	/** true: {1 --> 01 00 00 00} **/
	protected boolean little_endian;
	/** number of bytes occupied by one storage unit **/
	protected int word_size;
	/** size of void type **/
	private int void_size;
	/** size of boolean size **/
	private int bool_size;
	/** size of char value **/
	private int char_size;
	/** size of short type **/
	private int short_size;
	/** size of int type **/
	private int int_size;
	/** size of long type **/
	private int long_size;
	/** size of long long type **/
	private int long_long_size;
	/** size of float type **/
	private int float_size;
	/** size of double type **/
	private int double_size;
	/** size of long double type **/
	private int long_double_size;
	/** size of float complex type **/
	private int float_complex_size;
	/** size of double complex type **/
	private int double_complex_size;
	/** size of long double complex type **/
	private int long_double_complex_size;
	/** size of float imaginary type **/
	private int float_imaginary_size;
	/** size of double imaginary type **/
	private int double_imaginary_size;
	/** size of long double imaginary type **/
	private int long_double_imaginary_size;
	/** size of va_list variable **/
	private int va_list_size;
	/** size of pointer value **/
	private int pointer_size;
	
	/* constructor */
	/**
	 * read the key-value pairs from template-file
	 * @param template_file
	 * @return
	 * @throws Exception
	 */
	private Map<String, Integer> read(File template_file) throws Exception {
		Map<String, Integer> results = new HashMap<String, Integer>();
		
		BufferedReader reader = new BufferedReader(new FileReader(template_file));
		String line;
		while((line = reader.readLine()) != null) {
			line = line.strip();
			if(!line.isEmpty()) {
				String[] items = line.split("\t");
				String title = items[0].strip();
				int value = Integer.parseInt(items[1].strip());
				results.put(title, value);
			}
		}
		reader.close();
		
		return results;
	}
	/**
	 * create a running environment template for executing C programs
	 * @param template_file
	 * @throws Exception
	 */
	public CRunTemplate(File template_file) throws Exception {
		if(template_file == null || !template_file.exists())
			throw new IllegalArgumentException("Invalid file");
		else {
			Map<String, Integer> results = this.read(template_file);
			this.little_endian = (results.get("little_endian") != 0);
			this.word_size = results.get("word_size");
			
			this.void_size = results.get("void_size");
			this.char_size = results.get("char_size");
			this.short_size = results.get("short_size");
			this.int_size = results.get("int_size");
			this.long_size = results.get("long_size");
			this.long_long_size = results.get("long_long_size");
			this.float_size = results.get("float_size");
			this.double_size = results.get("double_size");
			this.long_double_size = results.get("long_double_size");
			this.float_complex_size = results.get("float_complex_size");
			this.double_complex_size = results.get("double_complex_size");
			this.long_double_complex_size = results.get("long_double_complex_size");
			this.float_imaginary_size = results.get("float_imaginary_size");
			this.double_imaginary_size = results.get("double_imaginary_size");
			this.long_double_imaginary_size = results.get("long_double_imaginary_size");
			this.pointer_size = results.get("pointer_size");
			this.va_list_size = results.get("va_list_size");
		}
	}
	
	/* utility methods */
	/**
	 * compute the number of bytes occupied by the variable of specified type.
	 * @param data_type
	 * @return
	 * @throws Exception
	 */
	public int sizeof(CType data_type) throws Exception {
		if(data_type == null)
			throw new IllegalArgumentException("No data type specified");
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:					return this.void_size;
			case c_bool:					return this.bool_size;
			case c_char: 					return this.char_size;
			case c_uchar:					return this.char_size;
			case c_short:					return this.short_size;
			case c_ushort:					return this.short_size;
			case c_int:						return this.int_size;
			case c_uint:					return this.int_size;
			case c_long:					return this.long_size;
			case c_ulong:					return this.long_size;
			case c_llong:					return this.long_long_size;
			case c_ullong:					return this.long_long_size;
			case c_float:					return this.float_size;
			case c_double:					return this.double_size;
			case c_ldouble:					return this.long_double_size;
			case c_float_complex:			return this.float_complex_size;
			case c_double_complex:			return this.double_complex_size;
			case c_ldouble_complex:			return this.long_double_complex_size;
			case c_float_imaginary:			return this.float_imaginary_size;
			case c_double_imaginary:		return this.double_imaginary_size;
			case c_ldouble_imaginary:		return this.long_double_imaginary_size;
			case gnu_va_list:				return this.va_list_size;
			default: throw new IllegalArgumentException("Unsupport: " + data_type);
			}
		}
		else if(data_type instanceof CArrayType) {
			int length = ((CArrayType) data_type).length();
			if(length > 0) {
				return this.sizeof(((CArrayType) data_type).get_element_type()) * length;
			}
			else {
				return this.pointer_size;
			}
		}
		else if(data_type instanceof CPointerType) {
			return this.pointer_size;
		}
		else if(data_type instanceof CFunctionType) {
			return this.pointer_size;
		}
		else if(data_type instanceof CStructType) {
			CFieldBody body = ((CStructType) data_type).get_fields();
			int size = 0;
			for(int k = 0; k < body.size(); k++) {
				CField field = body.get_field(k);
				size = size + this.sizeof(field.get_type());
			}
			return size;
		}
		else if(data_type instanceof CUnionType) {
			CFieldBody body = ((CUnionType) data_type).get_fields();
			int size = 0;
			for(int k = 0; k < body.size(); k++) {
				CField field = body.get_field(k);
				int field_size = this.sizeof(field.get_type());
				if(field_size > size) size = field_size;
			}
			return size;
		}
		else if(data_type instanceof CEnumType) {
			return this.int_size;
		}
		else if(data_type instanceof CQualifierType) {
			return this.sizeof(((CQualifierType) data_type).get_reference());
		}
		else 
			throw new IllegalArgumentException("Not support for: " + data_type);
	}
	/**
	 * get the number of bytes occupied by the value hold by the expression.
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public int sizeof(AstExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(expression instanceof AstInitializer) {
			if(((AstInitializer) expression).is_body())
				return this.sizeof(((AstInitializer) expression).get_body());
			else
				return this.sizeof(((AstInitializer) expression).get_expression());
		}
		else if(expression instanceof AstInitializerBody) {
			int size = 0;
			AstInitializerList list = 
					((AstInitializerBody) expression).get_initializer_list();
			for(int k = 0; k < list.number_of_initializer(); k++) {
				AstFieldInitializer element = list.get_initializer(k);
				AstInitializer initializer = element.get_initializer();
				if(initializer.is_body())
					size = size + this.sizeof(initializer.get_body());
				else
					size = size + this.sizeof(initializer.get_expression());
			}
			return size;
		}
		else if(expression.get_value_type() != null) {
			return this.sizeof(expression.get_value_type());
		}
		else
			throw new IllegalArgumentException("Unsupport: " + expression);
	}
	
}
