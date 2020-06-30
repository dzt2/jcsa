package com.jcsa.jcparse.lang.sym;

/**
 * field 	{name}
 * @author yukimula
 *
 */
public class SymField extends SymNode {
	
	protected SymField(String name) {
		super(name);
	}
	
	/**
	 * @return the name of the field
	 */
	public String get_name() { return (String) this.get_token(); }
	
	@Override
	protected SymNode clone_self() {
		return new SymField(this.get_name());
	}
	
	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		return this.get_name();
	}

}
