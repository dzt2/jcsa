package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymIdentifier extends SymBasicExpression {
	
	private String name;
	private SymIdentifier(CType data_type, String name) throws IllegalArgumentException {
		super(data_type);
		if(name == null || name.isBlank())
			throw new IllegalArgumentException("Invalid name: null");
		else
			this.name = name;
	}
	
	/**
	 * @return the name of the identifier
	 */
	public String get_name() { return this.name; }

	@Override
	protected SymNode construct() throws Exception {
		return new SymIdentifier(this.get_data_type(), this.name);
	}
	
	/**
	 * {name: type} as symbolic identifier
	 * @param data_type
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static SymIdentifier create(CType data_type, String name) throws Exception {
		return new SymIdentifier(data_type, name);
	}
	
}
