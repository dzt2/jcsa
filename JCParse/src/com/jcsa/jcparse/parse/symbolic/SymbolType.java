package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

/**
 * 	<code>SymbolType	[type: CType]</code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolType extends SymbolElement {
	
	/** the type represented by the cast node **/
	private	CType type;
	
	/**
	 * It creates a type-node in cast_expression
	 * @param type	the type of this type-node
	 * @throws Exception
	 */
	private SymbolType(CType type) throws Exception {
		super(SymbolClass.type_name);
		this.type = SymbolFactory.get_type(type);
	}
	
	/**
	 * @return the type represented by the cast node 
	 */
	public CType get_type() { return this.type; }

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolType(this.type); }
	
	/**
	 * @param type
	 * @return the type-name form of type code
	 * @throws Exception
	 */
	private	String generate_type_code(CType type) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:					return "void";
			case c_bool:					return "bool";
			case c_char:					return "char";
			case c_uchar:					return "uchar";
			case c_short:					return "short";
			case c_ushort:					return "ushort";
			case c_int:						return "int";
			case c_uint:					return "uint";
			case c_long:					return "long";
			case c_ulong:					return "ulong";
			case c_llong:					return "llong";
			case c_ullong:					return "ullong";
			case c_float:					return "float";
			case c_double:					return "double";
			case c_ldouble:					return "ldouble";
			case c_float_complex:			return "fcomplex";
			case c_double_complex:			return "dcomplex";
			case c_ldouble_complex:			return "lcomplex";
			case c_float_imaginary:			return "fimagine";
			case c_double_imaginary:		return "dimagine";
			case c_ldouble_imaginary:		return "limagine";
			default:						return "unknown";
			}
		}
		else if(type instanceof CArrayType) {
			int length = ((CArrayType) type).length();
			type = SymbolFactory.get_type(((CArrayType) type).get_element_type());
			String code = "(" + this.generate_type_code(type) + ")";
			
			if(length > 0) {
				return code + "[" + length + "]";
			}
			else {
				return code + "*";
			}
		}
		else if(type instanceof CPointerType) {
			type = SymbolFactory.get_type(((CPointerType) type).get_pointed_type());
			return "(" + this.generate_type_code(type) + ")*";
 		}
		else if(type instanceof CFunctionType) {
			type = SymbolFactory.get_type(((CFunctionType) type).get_return_type());
			return "(" + this.generate_type_code(type) + ")(...)";
		}
		else if(type instanceof CStructType) {
			String name = ((CStructType) type).get_name().strip();
			if(name.startsWith("struct ")) {
				name = name.substring("struct ".length()).strip();
			}
			if(name.isEmpty()) {
				name = "" + type.hashCode();
			}
			return "struct " + name;
		}
		else if(type instanceof CUnionType) {
			String name = ((CUnionType) type).get_name();
			if(name.startsWith("union ")) {
				name = name.substring("union ".length()).strip();
			}
			if(name.isEmpty()) {
				name = "" + type.hashCode();
			}
			return "union " + name;
		}
		else if(type instanceof CEnumType) {
			return "int";
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	
	@Override
	protected String generate_code(boolean simplified) throws Exception {
		return this.generate_type_code(this.type);
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type 	the type represented by the cast node 
	 * @return		It creates a type-node in cast_expression
	 * @throws Exception
	 */
	protected static SymbolType create(CType type) throws Exception {
		return new SymbolType(type);
	}
	
}
