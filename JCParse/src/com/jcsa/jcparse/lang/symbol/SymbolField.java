package com.jcsa.jcparse.lang.symbol;

/**
 * 	<code>
 * 	SymbolField						[field_name: String]
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolField extends SymbolElement {
	
	/** the name of the field **/
	private	String field_name;
	
	/**
	 * It creates a field node in field_expression
	 * @param field_name
	 * @throws Exception
	 */
	private SymbolField(String field_name) throws Exception {
		super(SymbolClass.field_name);
		if(field_name == null || field_name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name");
		}
		else {
			this.field_name = field_name.strip();
		}
	}

	/**
	 * @return the name of the field
	 */
	public String get_name() { return this.field_name; }

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolField(this.field_name); }

	@Override
	protected String generate_code(boolean simplified) throws Exception { return this.field_name; }

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * 
	 * @param field_name	the name of the field
	 * @return				It creates a field node in field_expression
	 * @throws Exception
	 */
	protected static SymbolField create(String field_name) throws Exception {
		return new SymbolField(field_name);
	}
	
}
