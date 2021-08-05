package com.jcsa.jcparse.lang.ctype.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.ctype.CEnumeratorList;

public class CEnumeratorListImpl implements CEnumeratorList {

	protected List<CEnumerator> enum_list;
	protected Map<String, CEnumerator> enum_map;

	protected CEnumeratorListImpl() {
		enum_list = new ArrayList<>();
		enum_map = new HashMap<>();
	}

	@Override
	public int size() {
		return enum_list.size();
	}

	@Override
	public CEnumerator get_enumerator(int k) throws Exception {
		if (k < 0 || k >= enum_list.size())
			throw new IllegalArgumentException("Invalid index: " + k);
		else
			return enum_list.get(k);
	}

	@Override
	public boolean has_enumerator(String name) {
		return enum_map.containsKey(name);
	}

	@Override
	public CEnumerator get_enumerator(String name) throws Exception {
		if (!enum_map.containsKey(name))
			throw new IllegalArgumentException("invalid name: " + name);
		else
			return enum_map.get(name);
	}

	@Override
	public void add_enumerator(CEnumerator enumerator) throws Exception {
		if (enumerator == null)
			throw new IllegalArgumentException("Invalid enumerator: null");
		else if (enum_map.containsKey(enumerator.get_literal()))
			throw new IllegalArgumentException("invalid enumerator: " + enumerator.get_literal());
		else {
			enum_list.add(enumerator);
			enum_map.put(enumerator.get_literal(), enumerator);
		}
	}

	protected StringBuilder buffer = new StringBuilder();

	@Override
	public String toString() {
		buffer.setLength(0);

		for (int i = 0; i < enum_list.size(); i++) {
			CEnumerator enumerator = enum_list.get(i);
			buffer.append(enumerator.toString());
			if (i != enum_list.size() - 1)
				buffer.append(", ");
		}

		return buffer.toString();
	}
}
