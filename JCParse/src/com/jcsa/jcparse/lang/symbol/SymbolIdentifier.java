package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>id_expr --> name#scope</code>
 * @author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/** the simple name of the identifier **/
	private String name;
	
	/** the scope where the name is defined **/
	private Object scope;
	
	/**
	 * It creates an isolated identifier expression with given name in scope
	 * @param name
	 * @param scope
	 * @throws IllegalArgumentException
	 */
	private SymbolIdentifier(CType data_type, String name, Object scope) throws IllegalArgumentException {
		super(SymbolClass.identifier, data_type);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else { this.name = name.strip(); this.scope = scope; }
	}
	
	/**
	 * It creates an isolated identifier expression with given name in scope
	 * @param name
	 * @param scope
	 * @throws IllegalArgumentException
	 */
	protected static SymbolIdentifier create(CType data_type, String name, Object scope) throws IllegalArgumentException {
		return new SymbolIdentifier(data_type, name, scope);
	}
	
	/**
	 * @return the simple name of the identifier
	 */
	public String get_name() { return this.name; }
	
	/**
	 * @return the scope where the name is defined
	 */
	public Object get_scope() { return this.scope; }
	
	/**
	 * @return the unique identifier of this expression
	 */
	public String get_identifier() { return this.name + "#" + this.scope; }
	
	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolIdentifier(this.get_data_type(), 
					this.get_name(), this.get_scope());
	}
	
	@Override
	protected String generate_code(boolean simplified) throws Exception {
		if(simplified) {
			return this.get_name();
		}
		else {
			return this.get_identifier();
		}
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
