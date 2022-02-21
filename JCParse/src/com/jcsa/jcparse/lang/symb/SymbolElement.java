package com.jcsa.jcparse.lang.symb;

/**
 * 	<code>
 * 	|--	SymbolElement					(<i>non-typed elements used in symbolic analysis</i>)			<br>
 * 	|--	|--	SymbolField					[get_name(): String]											<br>
 * 	|--	|--	SymbolOperator				[get_operator(): COperator]										<br>
 * 	|--	|--	SymbolType					[get_type(): CType]												<br>
 * 	|--	|--	SymbolArgumentList			{arg_list |--> (expression {, expression}*)}					<br>
 * 	</code>
 * 
 * @author yukimula
 *
 */
public abstract class SymbolElement extends SymbolNode {

	protected SymbolElement(SymbolClass _class) throws Exception {
		super(_class);
	}
	
}
