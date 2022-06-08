package com.jcsa.jcparse.parse.code;

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

/**
 * It generates the code that describes the type in C programming language format.
 *
 * @author yukimula
 *
 */
public class TypeCodeGenerator {

	/* attributes */
	/** to preserve the string of code being generated. **/
	private StringBuilder buffer;

	/* constructor & singleton */
	/**
	 * private constructor for singleton mode
	 */
	private TypeCodeGenerator() {
		this.buffer = new StringBuilder();
	}
	/** the singleton of the type code generator **/
	private static final TypeCodeGenerator generator = new TypeCodeGenerator();

	/* generation method */
	/**
	 * generate the code of data type in buffer.
	 * @param data_type
	 * @throws Exception
	 */
	private void gen(CType data_type) throws Exception {
		if(data_type == null) {
			throw new IllegalArgumentException("Invalid data_type: null");
		}
		else if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:				buffer.append("void"); 					break;
			case c_bool:				buffer.append("bool");					break;
			case c_char:				buffer.append("char");					break;
			case c_uchar:				buffer.append("char");					break;
			case c_short:				buffer.append("short");					break;
			case c_ushort:				buffer.append("unsigned short");		break;
			case c_int:					buffer.append("int");					break;
			case c_uint:				buffer.append("unsigned int");			break;
			case c_long:				buffer.append("long");					break;
			case c_ulong:				buffer.append("unsigned long");			break;
			case c_llong:				buffer.append("long long");				break;
			case c_ullong:				buffer.append("unsigned long long");	break;
			case c_float:				buffer.append("float");					break;
			case c_double:				buffer.append("double");				break;
			case c_ldouble:				buffer.append("long double");			break;
			case c_float_complex:		buffer.append("float _Complex");		break;
			case c_double_complex:		buffer.append("double _Complex");		break;
			case c_ldouble_complex:		buffer.append("long double _Complex");	break;
			case c_float_imaginary:		buffer.append("float _Imaginary");		break;
			case c_double_imaginary:	buffer.append("double _Imaginary");		break;
			case c_ldouble_imaginary:	buffer.append("long double _Imaginary");break;
			case gnu_va_list:			buffer.append("va_list");				break;
			default:
				throw new IllegalArgumentException("Unsupport basic-type: " +
											((CBasicType) data_type).get_tag());
			}
		}
		else if(data_type instanceof CArrayType) {
			buffer.append("(");
			this.gen(((CArrayType) data_type).get_element_type());
			buffer.append(")");

			int length = ((CArrayType) data_type).length();
			if(length > 0) {
				buffer.append("[" + length + "]");
			}
			else {
				buffer.append("*");
			}
		}
		else if(data_type instanceof CPointerType) {
			buffer.append("(");
			this.gen(((CPointerType) data_type).get_pointed_type());
			buffer.append(")");
			buffer.append("*");
		}
		else if(data_type instanceof CFunctionType) {
			buffer.append("(");
			this.gen(((CFunctionType) data_type).get_return_type());
			buffer.append(")");
			buffer.append("#");
			
			CParameterTypeList plist = ((CFunctionType) data_type).get_parameter_types();
			buffer.append("(");
			for(int k = 0; k < plist.size(); k++) {
				this.gen(plist.get_parameter_type(k));
				if(k < plist.size() - 1) {
					buffer.append(", ");
				}
			}
			buffer.append(")#");
		}
		else if(data_type instanceof CQualifierType) {
			this.gen(((CQualifierType) data_type).get_reference());
		}
		else if(data_type instanceof CEnumType) {
			buffer.append("int");
		}
		else if(data_type instanceof CStructType) {
			String name = ((CStructType) data_type).get_name();
			if(name != null && !name.trim().isEmpty()) {
				buffer.append("struct " + name);
			}
			else {
				buffer.append("struct #" + data_type.hashCode());
			}
		}
		else if(data_type instanceof CUnionType) {
			String name = ((CUnionType) data_type).get_name();
			if(name != null && !name.trim().isEmpty()) {
				buffer.append("union " + name);
			}
			else {
				buffer.append("union #" + data_type.hashCode());
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + data_type);
		}
	}

	/* generation method for public usage */
	/**
	 * @param data_type
	 * @return the code that describes the data type in C programming language.
	 * @throws Exception
	 */
	protected static String generate(CType data_type) throws Exception {
		generator.buffer.setLength(0);
		generator.gen(data_type);
		return generator.buffer.toString();
	}

}
