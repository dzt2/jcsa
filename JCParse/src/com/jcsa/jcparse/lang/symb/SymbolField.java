package com.jcsa.jcparse.lang.symb;

/**
 * SymbolField					[get_name(): String]
 * @author yukimula
 *
 */
public class SymbolField extends SymbolElement {
	
	/** name of the field node **/
	private String name;
	
	/**
	 * @param name the name of the field node
	 * @throws Exception
	 */
	private SymbolField(String name) throws Exception {
		super(SymbolClass.field);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: " + name);
		}
		else {
			this.name = name.strip();
		}
	}
	
	/**
	 * @return name of the field node
	 */
	public String get_name() { return this.name; }

	@Override
	protected SymbolNode construct_copy() throws Exception { return new SymbolField(this.name); }

	@Override
	protected String get_code(boolean simplified) throws Exception { return this.name; }

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param name the name of the field node
	 * @return
	 * @throws Exception
	 */
	protected static SymbolField create(String name) throws Exception {
		return new SymbolField(name);
	}
	
}
