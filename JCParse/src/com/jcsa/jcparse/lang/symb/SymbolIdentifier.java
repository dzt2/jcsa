package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * SymbolIdentifier		[get_name(): String]
 * 
 * @author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/** the name of the identifier expression **/
	private String name;
	
	/**
	 * @param type	the data type of the expression
	 * @param name	the name of the identifier
	 * @throws Exception
	 */
	protected SymbolIdentifier(CType type, String name) throws Exception {
		super(SymbolClass.identifier, type);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else {
			this.name = name.strip();
		}
	}
	
	/**
	 * @return the name of the identifier expression
	 */
	public String get_name() { return this.name; }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolIdentifier(this.get_data_type(), this.name);
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		if(simplified) {
			int index = this.name.indexOf('#');
			if(index >= 0) {
				String title = name.substring(0, index).strip();
				if(title.isEmpty() || title.equals("do")) {
					return this.name;
				}
				else {
					return title;
				}
			}
			else {
				return this.name;
			}
		}
		else {
			return this.name;
		}
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type
	 * @param name
	 * @return (name: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, String name) throws Exception {
		return new SymbolIdentifier(type, name);
	}
	
}
