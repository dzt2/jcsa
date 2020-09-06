package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymIdentifier extends SymBasicExpression {
	
	public static final String AnyBoolean = "@BOOL";
	public static final String AnyCharacter = "@CHAR";
	public static final String AnyInteger = "@INTG";
	public static final String AnyPosInteger = "@PINT";
	public static final String AnyNegInteger = "@NINT";
	public static final String AnyReal = "@REAL";
	public static final String AnyPosReal = "@PREAL";
	public static final String AnyNegReal = "@NREAL";
	public static final String AnyAddress = "@ADDR";
	public static final String AnySequence = "@BSEQ";
	
	private String name;
	protected SymIdentifier(CType data_type, String name) throws IllegalArgumentException {
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

	@Override
	public String generate_code() throws Exception {
		return this.name;
	}
	
}
