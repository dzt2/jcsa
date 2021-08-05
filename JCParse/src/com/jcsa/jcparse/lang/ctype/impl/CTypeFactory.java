package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CEnumeratorList;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.lexical.CTypeQualifier;

/***
 * To create the CType objects for AstNode
 * @author yukimula
 */
public class CTypeFactory {
	public CTypeFactory() {}
	public CPointerType get_pointer_type(CType type) throws Exception {
		return new CPointerTypeImpl(type);
	}
	public CArrayType get_array_type(CType type, int length) throws Exception {
		return new CArrayTypeImpl(length, type);
	}
	public CFunctionType get_fixed_function_type(CType ret_type) throws Exception {
		return new CFunctionTypeImpl(ret_type, false);
	}
	public CFunctionType get_variable_function_type(CType ret_type) throws Exception {
		return new CFunctionTypeImpl(ret_type, true);
	}
	public CStructType get_struct_type(String name) {
		return new CStructTypeImpl(name);
	}
	public CUnionType get_union_type(String name) {
		return new CUnionTypeImpl(name);
	}
	public CEnumType get_enum_type(String name) {
		return new CEnumTypeImpl(name);
	}
	public CQualifierType get_qualifier_type(CTypeQualifier qualifier, CType type) throws Exception {
		return new CQualifierTypeImpl(qualifier, type);
	}
	public boolean new_field(CStructType type, String name, CType vtype) throws Exception {
		CFieldImpl field = new CFieldImpl(type, name, vtype);
		type.get_fields().add_field(field);
		return true;
	}
	public boolean new_field(CStructType type, String name, CType vtype, int bitsize) throws Exception {
		CFieldImpl field = new CFieldImpl(type, name, vtype, bitsize);
		type.get_fields().add_field(field);
		return true;
	}
	public boolean new_field(CUnionType type, String name, CType vtype) throws Exception {
		CFieldImpl field = new CFieldImpl(type, name, vtype);
		type.get_fields().add_field(field);
		return true;
	}
	public boolean new_field(CUnionType type, String name, CType vtype, int bitsize) throws Exception {
		CFieldImpl field = new CFieldImpl(type, name, vtype, bitsize);
		type.get_fields().add_field(field);
		return true;
	}
	public boolean new_enumerator(CEnumType type, String name, int value) throws Exception {
		CEnumerator enumerator = new CEnumeratorImpl(type, name, value);
		type.get_enumerator_list().add_enumerator(enumerator);
		return true;
	}
	public boolean new_enumerator(CEnumType type, String name) throws Exception {
		int value = 0;
		CEnumeratorList elist = type.get_enumerator_list();
		if (elist.size() != 0) {
			CEnumerator tail = elist.get_enumerator(elist.size() - 1);
			value = tail.get_value() + 1;
		}

		CEnumerator enumerator = new CEnumeratorImpl(type, name, value);
		elist.add_enumerator(enumerator);
		return true;
	}

	/**
	 * get the type for value of target
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static CType get_value_type_of(CType type) throws Exception {
		while (type instanceof CQualifierType)
			type = ((CQualifierType) type).get_reference();
		return type;
	}
}
