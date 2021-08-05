package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CStructType;

public class CStructTypeImpl extends CTypeImpl implements CStructType {

	protected String name;
	protected CFieldBody body;

	protected CStructTypeImpl(String name) {
		if(name == null) this.name = "";
		else this.name = name;
		this.body = new CFieldBodyImpl();
	}

	@Override
	public CFieldBody get_fields() {
		return body;
	}

	@Override
	public boolean is_defined() {
		return body.size() > 0;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("struct[");
		for(int k = 0; k < this.body.size(); k++) {
			try {
				CField field = this.body.get_field(k);
				buffer.append(field.get_name() + ", ");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	public boolean equals(Object val) {
		return this == val;
	}

	@Override
	public String get_name() {
		return name;
	}

}
