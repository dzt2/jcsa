package com.jcsa.jcmutest.backups;

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

public class SecType extends SecToken {
	
	private CType ctype;
	private SecValueTypes vtype;
	public SecType(CType type) throws Exception {
		if(type == null)
			type = CBasicTypeImpl.void_type;
		this.ctype = type;
		this.vtype = this.get_value_type(type);
	}
	private SecValueTypes get_value_type(CType type) throws Exception {
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_void:	return SecValueTypes.cvoid;
			case c_bool:	return SecValueTypes.cbool;
			case c_char:
			case c_uchar:	return SecValueTypes.cchar;
			case c_short:
			case c_int:
			case c_long:
			case c_llong:	return SecValueTypes.csign;
			case c_ushort:
			case c_uint:
			case c_ulong:
			case c_ullong:	return SecValueTypes.usign;
			case c_float:
			case c_double:
			case c_ldouble:	return SecValueTypes.creal;
			default: 		return SecValueTypes.cbody;
			}
		}
		else if(type instanceof CArrayType
				|| type instanceof CPointerType
				|| type instanceof CFunctionType) {
			return SecValueTypes.caddr;
		}
		else if(type instanceof CStructType
				|| type instanceof CUnionType) {
			return SecValueTypes.cbody;
		}
		else if(type instanceof CEnumType) {
			return SecValueTypes.csign;
		}
		else if(type instanceof CQualifierType) {
			return this.get_value_type(((CQualifierType) type).get_reference());
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
	}
	
	public CType get_ctype() { return this.ctype; }
	
	public SecValueTypes get_vtype() { return this.vtype; } 
	
	@Override
	public String generate_code() throws Exception {
		return this.vtype.toString();
	}

}
