package com.jcsa.jcparse.lang.scope.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CNameTable;
import com.jcsa.jcparse.lang.scope.CScope;

public class CScopeImpl implements CScope {

	protected AstScopeNode origin;
	protected CScope parent;
	protected Set<CScope> children;
	protected CNameTable table;

	public CScopeImpl() throws Exception {
		this.parent = null;
		this.origin = null;
		this.children = new HashSet<CScope>();
		this.table = new CNameTableImpl(this);
	}

	protected CScopeImpl(CScope parent) throws Exception {
		this.parent = parent;
		this.origin = null;
		this.children = new HashSet<CScope>();
		this.table = new CNameTableImpl(this);
	}

	@Override
	public AstScopeNode get_origin() {
		return origin;
	}

	@Override
	public void set_origin(AstScopeNode origin) throws Exception {
		this.origin = origin;
	}

	@Override
	public CScope get_parent() {
		return parent;
	}

	@Override
	public Iterator<CScope> get_children() {
		return children.iterator();
	}

	@Override
	public CScope new_child() throws Exception {
		CScope child = new CScopeImpl(this);
		children.add(child);
		return child;
	}

	@Override
	public boolean has_child(CScope child) {
		if (child == null)
			return false;
		else
			return children.contains(child);
	}

	@Override
	public boolean del_child(CScope child) throws Exception {
		if (children.contains(child)) {
			children.remove(child);
			return true;
		} else
			return false;
	}

	@Override
	public CNameTable get_name_table() {
		return table;
	}

	@Override
	public boolean has_name(String name) {
		CScope scope = this;
		while (scope != null) {
			if (scope.get_name_table().has_name(name))
				return true;
			else
				scope = scope.get_parent();
		}
		return false;
	}

	@Override
	public CName get_name(String name) throws Exception {
		CScope scope = this;
		while (scope != null) {
			if (scope.get_name_table().has_name(name))
				return scope.get_name_table().get_name(name);
			else
				scope = scope.get_parent();
		}
		return null;
	}

}
