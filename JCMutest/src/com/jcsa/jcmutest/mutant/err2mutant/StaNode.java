package com.jcsa.jcmutest.mutant.err2mutant;

/**
 * <code>
 * 	StaNode																	<br>
 * 	|--	StaExpression						{data_type: CType}				<br>
 * 	|--	|--	StaBasicExpression												<br>
 * 	|--	|--	|--	StaIdentifier				{name: String}					<br>
 * 	|--	|--	|--	StaConstant					{constant: CConstant}			<br>
 * 	|--	|--	|--	StaLiteral					{literal: String}				<br>
 * 	|--	|--	|--	StaReference				{reference: CirExpression}		<br>
 * 	|--	|--	StaFieldExpression												<br>
 * 	|--	|--	StaCallExpression												<br>
 * 	|--	|--	StaUnaryExpression				{+,-,~,!,*,&,cast}				<br>
 * 	|--	|--	StaBinaryExpression				{-,/,%,<<,>>,<=,<,>=,>,!=,==}	<br>
 * 	|--	|--	StaMultiExpression				{+,*,&,|,^,&&,||}				<br>
 * 	|--	|--	StaInitializerList												<br>
 * 	|--	StaOperator							{operator: COperator}			<br>
 * 	|--	StaField							{name: String}					<br>
 * 	|--	StaArgumentList														<br>
 * 	|--	StaStatement						{statement: CirStatement}		<br>
 * 	|--	StaAssertion						{type: StaAssertionType}		<br>
 * 	|--	|-- StaExecuteOnAssertion			{statement, int}				<br>
 * 	|--	|--	StaSetExpressionAssertion		{expression, expression}		<br>
 * 	|--	|--	StaAddExpressionAssertion		{expression, op, expression}	<br>
 * 	|--	|--	StaInsExpressionAssertion		{expression, op, expression?}	<br>
 * 	|--	|--	StaTrappingOnAssertion			{statement}						<br>
 * 	|--	StaFailure															<br>
 * </code>
 * @author yukimula
 *
 */
public interface StaNode {
	
	public StaNode get_parent();
	public boolean is_root();
	public Iterable<StaNode> get_children();
	public int number_of_children();
	public StaNode get_child(int k) throws IndexOutOfBoundsException;
	public void add_child(StaNode child) throws IllegalArgumentException;
	
}
