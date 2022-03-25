package com.jcsa.jcparse.lang.symbol;

/**
 * 	The non-typed token used in symbolic expressions.
 * 	<br>
 * 	<code>
 * 	SymbolNode								[_class, source, parent, children]	<br>
 * 	|--	SymbolElement						(non-typed symbolic node as token)	<br>
 * 	|--	|--	SymbolType						[type_name: CType]					<br>
 * 	|--	|--	SymbolField						[field_name: String]				<br>
 * 	|--	|--	SymbolOperator					[operator: COperator]				<br>
 * 	|--	|--	SymbolArgumentList				(args_list --> (expr {, expr}+))	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class SymbolElement extends SymbolNode {

	protected SymbolElement(SymbolClass _class) throws IllegalArgumentException {
		super(_class);
	}

}
