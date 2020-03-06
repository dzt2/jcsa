package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CParameterTypeList;
import com.jcsa.jcparse.lang.ctype.CType;

public class CFunctionTypeImpl implements CFunctionType {

	protected CType rtype;
	protected CParameterTypeList plist;

	protected CFunctionTypeImpl(CType rtype, boolean variable) throws Exception {
		if (rtype == null)
			throw new IllegalArgumentException("invalid return-type: null");
		else {
			this.rtype = rtype;
			this.plist = new CParameterTypeListImpl(variable);
		}
	}

	@Override
	public CType get_return_type() {
		return rtype;
	}

	@Override
	public CParameterTypeList get_parameter_types() {
		return plist;
	}

	@Override
	public boolean is_defined() {
		return true;
	}

	@Override
	public String toString() {
		return rtype.toString() + " ( " + plist.toString() + ")";
	}

	@Override
	public boolean equals(Object val) {
		if (val instanceof CFunctionType) {
			CType rtype = ((CFunctionType) val).get_return_type();
			CParameterTypeList plist = ((CFunctionType) val).get_parameter_types();
			if (this.rtype.equals(rtype)) {
				if (plist.is_ellipsis() == this.plist.is_ellipsis() && plist.size() == this.plist.size()) {
					int n = plist.size();
					for (int i = 0; i < n; i++) {
						CType atype1, atype2;
						try {
							atype1 = plist.get_parameter_type(i);
							atype2 = this.plist.get_parameter_type(i);
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						if (!atype1.equals(atype2))
							return false;
					}
					return true;
				} else if (plist.size() == 0 || this.plist.size() == 0)
					return true;
				else
					return false;
			} else
				return false;
		} else
			return false;
	}
}
