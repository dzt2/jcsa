package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelDataTypes;
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
	
	private CType ctype;
	private SelDataTypes dtype;
	protected SelDataType(CType ctype) throws Exception {
		this.ctype = ctype;
		if(ctype == null) {
			this.dtype = SelDataTypes.cvoid;
		}
		else {
			this.ctype = CTypeAnalyzer.get_value_type(ctype);
			if(this.ctype instanceof CBasicType) {
				switch(((CBasicType) this.ctype).get_tag()) {
				case c_void:	this.dtype = SelDataTypes.cvoid; break;
				case c_bool:	this.dtype = SelDataTypes.cbool; break;
				case c_char:	
				case c_uchar:	this.dtype = SelDataTypes.cchar; break;
				case c_short:
				case c_int:
				case c_long:
				case c_llong:	this.dtype = SelDataTypes.csign; break;
				case c_ushort:
				case c_uint:
				case c_ulong:
				case c_ullong:	this.dtype = SelDataTypes.usign; break;
				case c_float:
				case c_double:
				case c_ldouble:	this.dtype = SelDataTypes.creal; break;
				default: 		this.dtype = SelDataTypes.cbody; break;
				}
			}
			else if(this.ctype instanceof CArrayType
					|| this.ctype instanceof CPointerType
					|| this.ctype instanceof CFunctionType) {
				this.dtype = SelDataTypes.caddr;
			}
			else if(this.ctype instanceof CStructType
					|| this.ctype instanceof CUnionType) {
				this.dtype = SelDataTypes.cbody;
			}
			else if(this.ctype instanceof CEnumType) {
				this.dtype = SelDataTypes.csign;
			}
			else {
				throw new IllegalArgumentException(this.ctype.generate_code());
			}
		}
	}
	
	/**
	 * @return the data type in C language definition
	 */
	public CType get_ctype() { return this.ctype; }
	
	/**
	 * @return the data type in Symbolic Error Language
	 */
	public SelDataTypes get_dtype() { return this.dtype; }
	
	@Override
	public String generate_code() throws Exception {
		return this.dtype.toString();
	}

}
