package com.jcsa.jcparse.lang.scope;

import java.util.Iterator;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;

/**
 * name table maps the name to its corresponding CName object (unique)
 *
 * @author yukimula
 */
public interface CNameTable {
	/**
	 * the scope where the local name-table is defined
	 *
	 * @return
	 */
	public CScope get_scope();

	/**
	 * whether the name is defined in the table
	 *
	 * @param name
	 * @return
	 */
	public boolean has_name(String name);

	/**
	 * get the list of names in this table
	 *
	 * @return
	 */
	public Iterator<String> get_names();

	/**
	 * get the name entity by its string literal
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CName get_name(String name) throws Exception;

	/**
	 * create a name for struct declaration or definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CStructTypeName new_struct_name(AstName name) throws Exception;

	/**
	 * create a name for union declaration or definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CUnionTypeName new_union_name(AstName name) throws Exception;

	/**
	 * create a name for enum type declaration | definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CEnumTypeName new_enum_name(AstName name) throws Exception;

	/**
	 * create a name for label definition at label :
	 *
	 * @param label
	 * @return
	 * @throws Exception
	 */
	public CLabelName new_label_name(AstLabel label) throws Exception;

	/**
	 * create a name for macro declaration, i.e. #define
	 *
	 * @param macro
	 * @return
	 * @throws Exception
	 */
	public CMacroName new_macro_name(AstMacro macro) throws Exception;

	/**
	 * create a name for enumerator in enum-body
	 *
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public CEnumeratorName new_enumerator_name(AstEnumerator e) throws Exception;

	/**
	 * create a name for typedef specifier
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CTypedefName new_typedef_name(AstName name) throws Exception;

	/**
	 * create a name for field definition in struct|union body
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CFieldName new_field_name(AstName name) throws Exception;

	/**
	 * create a name for variable declaration | definition
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CInstanceName new_instance_name(AstName name) throws Exception;

	/**
	 * create a name for parameter in parameter list
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public CParameterName new_parameter_name(AstName name) throws Exception;

	/**
	 * delete an existing name from the table
	 *
	 * @param name
	 * @throws Exception
	 */
	public void del_name(CName name) throws Exception;

	/**
	 * clear all the names in table
	 */
	public void clear();

	/**
	 * get the number of names in table
	 *
	 * @return
	 */
	public int size();

}
