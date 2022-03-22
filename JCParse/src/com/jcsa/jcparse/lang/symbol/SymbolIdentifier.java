package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>identifier --> name#scope</code>
 * 
 * @author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/** the simple name of the identifier **/
	private String name;
	
	/** the scope where the name is defined **/
	private Object scope;
	
	/**
	 * It creates an identifier as expression used in symbolic expression.
	 * @param type		the type of the identifier
	 * @param name		the simple name of identifier
	 * @param scope		the scope where the name is defined
	 * @throws Exception
	 */
	private SymbolIdentifier(CType type, String name, Object scope) throws Exception {
		super(SymbolClass.identifier, type);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else { this.name = name.strip(); this.scope = scope; }
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
	 * @return name#scope
	 */
	public String get_identifier() { return this.name + "#" + this.scope; }

	@Override
	protected SymbolNode new_one() throws Exception { 
		return new SymbolIdentifier(this.get_data_type(), this.name, this.scope); 
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
	
	/**
	 * It creates an identifier as expression used in symbolic expression.
	 * @param type		the type of the identifier
	 * @param name		the simple name of identifier
	 * @param scope		the scope where the name is defined
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, String name, Object scope) throws Exception {
		return new SymbolIdentifier(type, name, scope);
	}
	
	@Override
	protected SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		if(name_value_map.containsKey(this)) {
			return name_value_map.get(this);
		}
		else {
			return this;
		}
	}
	
}
