package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * SymbolType					[get_type(): CType]
 * @author yukimula
 *
 */
public class SymbolType extends SymbolElement {
	
	/** the type that this node represents **/
	private CType type;
	
	/**
	 * @param type the type that this node represents
	 * @throws Exception
	 */
	private SymbolType(CType type) throws Exception {
		super(SymbolClass.type);
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else {
			this.type = type;
		}
	}
	
	/**
	 * @return the type that this node represents
	 */
	public CType get_type() { return this.type; }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolType(this.type);
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		return this.type.generate_code();
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type the type that this node represents
	 * @return
	 * @throws Exception
	 */
	protected static SymbolType create(CType type) throws Exception {
		return new SymbolType(type);
	}

}
