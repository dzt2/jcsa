package com.jcsa.jcmutest.mutant.sel2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.SelValueType;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;

public class SelDataType extends SelToken {
	
	private CType data_type;
	private SelValueType value_type;
	public SelDataType(CType data_type) throws Exception {
		if(data_type == null)
			throw new IllegalArgumentException("Invalid data_type: null");
		else {
			data_type = CTypeAnalyzer.get_value_type(data_type);
			this.data_type = data_type;
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_void:	this.value_type = SelValueType.cvoid; break;
				case c_bool:	this.value_type = SelValueType.cbool; break;
				case c_char:
				case c_uchar:	this.value_type = SelValueType.cchar; break;
				case c_short:
				case c_int:
				case c_long:
				case c_llong:	this.value_type = SelValueType.csign; break;
				case c_ushort:
				case c_uint:
				case c_ulong:
				case c_ullong:	this.value_type = SelValueType.usign; break;
				case c_float:	
				case c_double:
				case c_ldouble:	this.value_type = SelValueType.creal; break;
				default: 		this.value_type = SelValueType.cbody; break;
				}
			}
			else if(data_type instanceof CArrayType
					|| data_type instanceof CPointerType
					|| data_type instanceof CFunctionType) {
				this.value_type = SelValueType.caddr;
			}
			else if(data_type instanceof CStructType
					|| data_type instanceof CUnionType) {
				this.value_type = SelValueType.cbody;
			}
			else if(data_type instanceof CEnumType) {
				this.value_type = SelValueType.csign;
			}
			else {
				throw new IllegalArgumentException(data_type.generate_code());
			}
		}
	}
	
	/* getters */
	/**
	 * @return data type of the value in C definition
	 */
	public CType get_data_type() { return this.data_type; }
	/**
	 * @return abstract category of value type in expression
	 */
	public SelValueType get_value_type() { return this.value_type; }

	@Override
	public String generate_code() throws Exception {
		return this.value_type.toString();
	}
	
}
