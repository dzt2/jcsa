package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CStorageClass;
import com.jcsa.jcparse.lang.scope.CInstance;

public class CInstanceImpl implements CInstance {
	protected CType type;
	protected CStorageClass storage;

	protected CInstanceImpl(CType vtype) throws Exception {
		if (vtype == null)
			throw new IllegalArgumentException("Invalid vtype: null");
		else if (!vtype.is_defined())
			throw new IllegalArgumentException("Undefined value-type: " + vtype);
		else {
			this.type = vtype;
			this.storage = CStorageClass.c_auto;
		}
	}

	protected CInstanceImpl(CType vtype, CStorageClass storage) throws Exception {
		if (vtype == null)
			throw new IllegalArgumentException("Invalid vtype: null");
		else if (!vtype.is_defined() & storage != CStorageClass.c_extern)
			throw new IllegalArgumentException("Undefined value-type: " + vtype);
		else if (storage == null)
			throw new IllegalArgumentException("Invalid storage-class: null");
		else {
			this.type = vtype;
			this.storage = storage;
		}
	}

	@Override
	public CType get_type() {
		return type;
	}

	@Override
	public CStorageClass get_storage_class() {
		return storage;
	}

}
