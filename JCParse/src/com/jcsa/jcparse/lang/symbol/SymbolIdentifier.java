package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.scope.CName;

/**
 * identifier |-- {name: String}	<br>
 * 	(1) name#scope		{AST|CIR}
 * 	(2) #ast.key		{AST}
 * 	(3)	return#cir.id	{CIR}
 * 	(4)	default#cir.id	{CIR}
 * 	(5) do#func#exe_id	{EXECUTION}
 * @author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/** the name of the identifier **/
	private String name;
	
	/**
	 * create an identifier node w.r.t. complete variable name
	 * @param data_type
	 * @param name
	 * @throws IllegalArgumentException
	 */
	private SymbolIdentifier(CType data_type, String name) throws IllegalArgumentException {
		super(data_type);
		if(name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("Invalid name: " + name);
		else 
			this.name = name.trim();
	}
	
	/**
	 * @return the name of the identifier
	 */
	public String get_name() { return this.name; }
	
	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolIdentifier(this.get_data_type(), this.get_name());
	}
	
	/* factory methods */
	/**
	 * @param data_type
	 * @param cname
	 * @return name#scope
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType data_type, CName cname) throws Exception {
		String name = cname.get_name() + "#" + cname.get_scope().hashCode();
		return new SymbolIdentifier(data_type, name);
	}
	/**
	 * @param data_type
	 * @param ast_reference
	 * @return #ast.key
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType data_type, AstNode ast_reference) throws Exception {
		String name = "#" + ast_reference.get_key();
		return new SymbolIdentifier(data_type, name);
	}
	/**
	 * @param data_type
	 * @param function
	 * @return return#function.id
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType data_type, CirFunctionDefinition function) throws Exception {
		String name = "return#" + function.get_node_id();
		return new SymbolIdentifier(data_type, name);
	}
	/**
	 * @param data_type
	 * @param default_value
	 * @return default#value.id
	 * @throws Excecption
	 */
	protected static SymbolIdentifier create(CType data_type, CirDefaultValue default_value) throws Exception {
		String name = "default#" + default_value.get_node_id();
		return new SymbolIdentifier(data_type, name);
	}
	/**
	 * @param execution
	 * @return do#execution
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CirExecution execution) throws Exception {
		String name = "do#" + execution.toString();
		return new SymbolIdentifier(CBasicTypeImpl.int_type, name);
	}
	/**
	 * @param data_type
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType data_type, String name) throws Exception {
		return new SymbolIdentifier(data_type, name);
	}
	
}
