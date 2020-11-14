package com.jcsa.jcparse.lang.sym;

/**
 * field |-- {name: String}
 * @author yukimula
 *
 */
public class SymField extends SymUnit {
	
	/* definitions */
	private String name;
	private SymField(String name) throws IllegalArgumentException {
		if(name == null || name.isBlank()) {
			throw new IllegalArgumentException("invalid name");
		}
		else {
			this.name = name;
		}
	}
	
	/**
	 * @return the name of the field
	 */
	public String get_name() { return this.name; }
	
	@Override
	protected SymNode construct() throws Exception {
		return new SymField(this.name);
	}
	
	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
	/**
	 * @param name
	 * @return field := name
	 * @throws Exception
	 */
	protected static SymField create(String name) throws Exception {
		return new SymField(name);
	}
	
}
