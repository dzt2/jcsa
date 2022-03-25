package com.jcsa.jcparse.lang.symbol;

/**
 * <code>SymbolField [field_name: String]</code>
 * @author yukimula
 *
 */
public class SymbolField extends SymbolElement {
	
	/** the field name **/
	private String name;
	
	/**
	 * It creates an isolated field-node with specified name
	 * @param name
	 * @throws IllegalArgumentException
	 */
	private SymbolField(String name) throws IllegalArgumentException {
		super(SymbolClass.field_name);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else {
			this.name = name.strip();
		}
	}
	
	/**
	 * @return the name of this field
	 */
	public String get_name() { return this.name; }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolField(this.name);
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		return this.name;
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param name	the field name (not-empty)
	 * @return	It creates an isolated field-node with specified name
	 * @throws IllegalArgumentException
	 */
	protected static SymbolField create(String name) throws IllegalArgumentException {
		return new SymbolField(name);
	}

}
