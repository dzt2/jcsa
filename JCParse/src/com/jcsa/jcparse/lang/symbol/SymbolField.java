package com.jcsa.jcparse.lang.symbol;

/**
 * field |-- {name: String}
 * @author yukimula
 *
 */
public class SymbolField extends SymbolUnit {
	
	/** the name of the field **/
	private String name;
	
	/**
	 * field |-- {name: String}
	 * @param name
	 * @throws IllegalArgumentException
	 */
	private SymbolField(String name) throws IllegalArgumentException {
		if(name == null || name.isBlank())
			throw new IllegalArgumentException("Invalid: " + name);
		else {
			this.name = name.strip();
		}
	}
	
	/**
	 * @return the name of the field 
	 */
	public String get_name() { return this.name; }
	
	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolField(this.name);
	}
	
	/**
	 * field |-- {name: String}
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static SymbolField create(String name) throws Exception {
		return new SymbolField(name);
	}
	
}
