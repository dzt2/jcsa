package com.jcsa.jcparse.lang.symbol;

/**
 * 	<code>
 * 	|--	SymbolElement						(Non-Expression as Elemental Node)					<br>
 * 	|--	|--	SymbolArgumentList				{arg_list --> (expr {, expr}*)}						<br>		
 * 	|--	|--	SymbolField						[field_name: String]								<br>
 * 	|--	|--	SymbolType						[type: CType]										<br>
 * 	|--	|--	SymbolOperator					[operator: COperator]								<br>
 * 	</code>
 * 
 * 	@author yukimula
 *
 */
public abstract class SymbolElement extends SymbolNode {

	protected SymbolElement(SymbolClass _class) throws Exception {
		super(_class);
	}

}
