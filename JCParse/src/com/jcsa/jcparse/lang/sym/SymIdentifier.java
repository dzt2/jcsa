package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * identifier	|--	{name: String}
 * @author yukimula
 *
 */
public class SymIdentifier extends SymBasicExpression {
	
	private String name;
	
	protected SymIdentifier(CType data_type, String name) {
		super(data_type);
		this.name = name;
	}
	
	@Override
	protected SymNode new_self() {
		return new SymIdentifier(this.get_data_type(), this.name);
	}
	
	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		if(ast_style) {
			if(this.name.contains("@")) {
				int index = this.name.indexOf('@');
				return this.name.substring(0, index);
			}
			else {
				return this.name;
			}
		}
		else {
			return this.name;
		}
	}

}
