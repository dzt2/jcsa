package com.jcsa.jcparse.lang.scope.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.scope.CEnumTypeName;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CFieldName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CLabelName;
import com.jcsa.jcparse.lang.scope.CMacroName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CNameTable;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.scope.CStructTypeName;
import com.jcsa.jcparse.lang.scope.CTypedefName;
import com.jcsa.jcparse.lang.scope.CUnionTypeName;

public class CNameTableImpl implements CNameTable {

	protected CScope scope;
	protected Map<String, CName> name_map;

	protected CNameTableImpl(CScope scope) throws Exception {
		if (scope == null)
			throw new IllegalArgumentException("Invalid scope: null");
		name_map = new HashMap<>();
		this.scope = scope;
	}

	@Override
	public CScope get_scope() {
		return scope;
	}

	@Override
	public boolean has_name(String name) {
		if (name == null)
			return false;
		else
			return name_map.containsKey(name);
	}

	@Override
	public Iterator<String> get_names() {
		return name_map.keySet().iterator();
	}

	@Override
	public CName get_name(String name) throws Exception {
		if (name == null || !name_map.containsKey(name))
			throw new IllegalArgumentException("Undefined name: " + name);
		else
			return name_map.get(name);
	}

	/**
	 * create a name for struct declaration or definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CStructTypeName new_struct_name(AstName name) throws Exception {
		CStructTypeName cname = new CStructTypeNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for union declaration or definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CUnionTypeName new_union_name(AstName name) throws Exception {
		CUnionTypeName cname = new CUnionTypeNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for enum type declaration | definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CEnumTypeName new_enum_name(AstName name) throws Exception {
		CEnumTypeName cname = new CEnumTypeNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for label definition at label :
	 *
	 * @param label
	 * @return
	 * @throws Exception
	 */
	@Override
	public CLabelName new_label_name(AstLabel label) throws Exception {
		CLabelName cname = new CLabelNameImpl(scope, label);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for macro declaration, i.e. #define
	 *
	 * @param macro
	 * @return
	 * @throws Exception
	 */
	@Override
	public CMacroName new_macro_name(AstMacro macro) throws Exception {
		CMacroName cname = new CMacroNameImpl(scope, macro);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for enumerator in enum-body
	 *
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@Override
	public CEnumeratorName new_enumerator_name(AstEnumerator e) throws Exception {
		CEnumeratorName cname = new CEnumeratorNameImpl(scope, e.get_name());
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for typedef specifier
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CTypedefName new_typedef_name(AstName name) throws Exception {
		CTypedefName cname = new CTypedefNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for field definition in struct|union body
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CFieldName new_field_name(AstName name) throws Exception {
		CFieldName cname = new CFieldNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for variable declaration | definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CInstanceName new_instance_name(AstName name) throws Exception {
		CInstanceName cname = new CInstanceNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	/**
	 * create a name for parameter in parameter list
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public CParameterName new_parameter_name(AstName name) throws Exception {
		CParameterName cname = new CParameterNameImpl(scope, name);
		this.put_name(cname);
		return cname;
	}

	@Override
	public void del_name(CName name) throws Exception {
		if (name_map.containsKey(name.get_name())) {
			CName cname = name_map.get(name.get_name());
			if (cname != name)
				throw new IllegalArgumentException("Not our name: " + name);
			name_map.remove(name.get_name());
		}
	}

	@Override
	public void clear() {
		name_map.clear();
	}

	@Override
	public int size() {
		return name_map.size();
	}

	protected void put_name(CName cname) throws Exception {
		if (!this.name_map.containsKey(cname.get_name())) {
			this.name_map.put(cname.get_name(), cname);
		} else if (cname instanceof CInstanceName) {
			CName head = this.name_map.get(cname.get_name());
			if (head instanceof CInstanceName) {
				CInstanceName iname = (CInstanceName) head;
				while (iname.get_next_name() != null)
					iname = iname.get_next_name();
				iname.set_next_name((CInstanceName) cname);
			} else
				throw new IllegalArgumentException("Invalid instance-name insertion \"" + cname.get_name() + "\"");
		}
	}
}
