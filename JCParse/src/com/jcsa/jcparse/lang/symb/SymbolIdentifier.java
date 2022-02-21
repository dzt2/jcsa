package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.scope.CName;

/**
 * SymbolIdentifier		[get_name(): String]
 * 
 * @author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/** the name of the identifier expression **/
	private String name;
	
	/**
	 * @param type	the data type of the expression
	 * @param name	the name of the identifier
	 * @throws Exception
	 */
	protected SymbolIdentifier(CType type, String name) throws Exception {
		super(SymbolClass.identifier, type);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else {
			this.name = name.strip();
		}
	}
	
	/**
	 * @return the name of the identifier expression
	 */
	public String get_name() { return this.name; }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolIdentifier(this.get_data_type(), this.name);
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		if(simplified) {
			int index = this.name.indexOf('#');
			if(index >= 0) {
				String title = name.substring(0, index).strip();
				if(title.isEmpty() || title.equals("do")) {
					return this.name;
				}
				else {
					return title;
				}
			}
			else {
				return this.name;
			}
		}
		else {
			return this.name;
		}
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type
	 * @param name
	 * @return (name: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, String name) throws Exception {
		return new SymbolIdentifier(type, name);
	}
	/**
	 * @param type
	 * @param name
	 * @return (name#scope: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, CName cname) throws Exception {
		String name = cname.get_name() + "#" + cname.get_scope().hashCode();
		return create(type, name);
	}
	/**
	 * @param type
	 * @param reference
	 * @return (#ast_key: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, AstNode reference) throws Exception {
		String name = "#" + reference.get_key();
		return create(type, name);
	}
	/**
	 * @param type
	 * @param function
	 * @return (return#cir_id: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, CirFunctionDefinition function) throws Exception {
		String name = "return#" + function.get_node_id();
		return create(type, name);
	}
	/**
	 * @param type
	 * @param default_value
	 * @return (default#value.id: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, CirDefaultValue default_value) throws Exception {
		String name = "default#" + default_value.get_node_id();
		return create(type, name);
	}
	/**
	 * @param execution
	 * @return (do#stmt_id: int)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CirExecution execution) throws Exception {
		String name = "do#" + execution.toString();
		return new SymbolIdentifier(CBasicTypeImpl.int_type, name);
	}
	/**
	 * @param type
	 * @return (#_: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type) throws Exception {
		return create(type, "#_");
	}
	
}
