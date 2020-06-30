package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * identifier	|--	{name}
 * @author yukimula
 *
 */
public class SymIdentifier extends SymBasicExpression {

	protected SymIdentifier(CType data_type, String name) {
		super(data_type, name);
	}
	
	/**
	 * @return the name of the identifier expression
	 */
	public String get_name() { return (String) this.get_token(); }
	
	@Override
	protected SymNode clone_self() {
		return new SymIdentifier(this.get_data_type(), this.get_name());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		String name = (String) this.get_token();
		/** in CIR, do not cut-off the name */
		if(!ast_code) {
			return name;
		}
		/** in AST, any name with @ is either refined or implicated name **/
		else if(name.contains("@")) {
			int index = name.indexOf('@');
			String prefix = name.substring(0, index).strip();
			String postfix = name.substring(index+1).strip();
			/** case-1. implicator name **/
			if(prefix.isEmpty()) {
				return "implicator_" + postfix;
			}
			/** case-2. return expression **/
			else if(prefix.equals("return")) {
				return "return_" + postfix;
			}
			/** case-3. either declarator or identifier **/
			else {
				return prefix;
			}
		}
		/** in AST, any name without @ is the original name **/
		else {
			return name;
		}
	}

}
