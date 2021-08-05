package com.jcsa.jcparse.lang.ctype.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CFieldBody;

public class CFieldBodyImpl implements CFieldBody {

	protected List<CField> field_list;
	protected Map<String, CField> field_map;

	protected CFieldBodyImpl() {
		field_list = new ArrayList<>();
		field_map = new HashMap<>();
	}

	@Override
	public int size() {
		return field_list.size();
	}

	@Override
	public CField get_field(int k) throws Exception {
		if (k < 0 || k >= field_list.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return this.field_list.get(k);
	}

	@Override
	public boolean has_field(String name) {
		return field_map.containsKey(name);
	}

	@Override
	public CField get_field(String name) throws Exception {
		if (field_map.containsKey(name))
			return field_map.get(name);
		else
			throw new IllegalArgumentException("Invalid field: " + name);
	}

	@Override
	public void add_field(CField field) throws Exception {
		if (field == null)
			throw new IllegalArgumentException("Invalid field: null");
		else if (field_map.containsKey(field.get_name()))
			throw new IllegalArgumentException("Duplicated: " + field.get_name());
		else {
			field_list.add(field);
			field_map.put(field.get_name(), field);
		}
	}

	protected StringBuilder buffer = new StringBuilder();

	@Override
	public String toString() {
		buffer.setLength(0);

		for (int i = 0; i < field_list.size(); i++) {
			CField field = field_list.get(i);
			buffer.append(field.get_name());
			if (i != field_list.size() - 1)
				buffer.append(", ");
		}

		return buffer.toString();
	}
}
