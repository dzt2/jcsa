package com.jcsa.jcparse.lang.symbol;

/**
 * <code>
 * 	SymbolUnit						[<i>usually taken as leaf or connector</i>]											<br>
 * 	|--	SymbolArgumentList			[<i>SymbolCallExpression.children[1]</i>]											<br>
 * 	|--	SymbolField					{name: String}	[<i>SymbolFieldExpression.children[1]</i>]							<br>
 * 	|--	SymbolOperator				{operator: COperator}																<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SymbolUnit extends SymbolNode {
}
