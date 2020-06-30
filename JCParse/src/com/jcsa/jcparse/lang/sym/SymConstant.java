package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant	|-- {bool|char|int|long|float|double}
 * @author yukimula
 *
 */
public class SymConstant extends SymBasicExpression {

	protected SymConstant(CConstant constant) {
		super(constant.get_type(), constant);
	}
	
	/**
	 * @return the constant that the node describes
	 */
	public CConstant get_constant() { return (CConstant) this.get_token(); }
	
	/**
	 * @return bool | char | int | long | float | double | null
	 */
	public Object get_constant_value() {
		CConstant constant = this.get_constant();
		switch(constant.get_type().get_tag()) {
		case c_bool: 		return constant.get_bool();
		case c_char:
		case c_uchar:		return constant.get_char();
		// TODO implement the symbolic modeling
		default: return null;
		}
	}

	@Override
	protected SymNode clone_self() {
		return new SymConstant(this.get_constant());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
