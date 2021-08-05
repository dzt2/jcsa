package com.jcsa.jcparse.lang.ctype.impl;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CParameterTypeList;
import com.jcsa.jcparse.lang.ctype.CType;

public class CParameterTypeListImpl implements CParameterTypeList {

	protected boolean is_variable;
	protected List<CType> type_list;

	protected CParameterTypeListImpl(boolean variable) {
		this.is_variable = variable;
		this.type_list = new ArrayList<>();
		this.buffer = new StringBuilder();
	}

	@Override
	public int size() {
		return type_list.size();
	}

	@Override
	public CType get_parameter_type(int i) throws Exception {
		if (i < 0 || i >= type_list.size())
			throw new IllegalArgumentException("invalid index: " + i);
		else
			return type_list.get(i);
	}

	@Override
	public void add_parameter_type(CType type) throws Exception {
		if (type == null)
			throw new IllegalArgumentException("Invalid type: null");
		type_list.add(type);
	}

	@Override
	public boolean is_ellipsis() {
		return is_variable;
	}

	protected StringBuilder buffer;

	@Override
	public String toString() {
		buffer.setLength(0);

		for (int i = 0; i < type_list.size(); i++) {
			buffer.append(type_list.get(i));
			if (i != type_list.size() - 1)
				buffer.append(", ");
		}

		if (is_variable)
			buffer.append(", ...");

		return buffer.toString();
	}
}
