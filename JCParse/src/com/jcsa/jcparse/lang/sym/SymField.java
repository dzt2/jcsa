package com.jcsa.jcparse.lang.sym;

/**
 * field |-- {name: String}
 * @author yukimula
 *
 */
public class SymField extends SymNode {
	
	private String name;
	protected SymField(String name) {
		this.name = name;
	}
	
	/**
	 * @return name of the field
	 */
	public String get_name() { return this.name; }

	@Override
	protected SymNode new_self() {
		return new SymField(this.name);
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		return this.name;
	}

}
