package com.jcsa.jcmutest.mutant.sed2mutant.lang;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * The symbolic execution description language is defined as following:<br>
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedNode							{cir_source: CirNode}				<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedToken															<br>
 * 	|--	SedOperator					{operator: COperator}				<br>
 * 	|--	SedField					{name: String}						<br>
 * 	|--	SedLabel					{location: CirStatement}			<br>
 * 	|--	SedArgumentList													<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedExpression					{data_type: CType}					<br>
 * 	|--	SedBasicExpression												<br>
 * 	|--	|--	SedIdExpression			{name: String}						<br>
 * 	|--	|--	SedConstant				{bool|char|int|long|float|double}	<br>
 * 	|--	|--	SedLiteral				{literal: String}					<br>
 * 	|--	|--	SedDefaultValue												<br>
 * 	|--	SedUnaryExpression			{-, ~, !, &, *, cast}				<br>
 * 	|--	SedBinaryExpression			[+, -, *, /, ..., ==, !=]			<br>
 * 	|--	SedFieldExpression												<br>
 * 	|--	SedInitializerList												<br>
 * 	|--	SedCallExpression												<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedStatement					{label: SedLabel}					<br>
 * 	|--	SedAssignStatement												<br>
 * 	|--	SedGotoStatement												<br>
 * 	|--	SedIfStatement													<br>
 * 	|--	SedCallStatement												<br>
 * 	|--	SedWaitStatement												<br>
 * 	|--	SedTagStatement													<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedConstraint														<br>
 * 	|--	SedBasicConstraint				{location: SedLabel}			<br>
 * 	|--	|--	SedExecutionConstraint		execute(location, int)			<br>
 * 	|--	|--	SedConditionConstraint		assert(location, expr)			<br>
 * 	|--	SedCompositeConstraint			{constraints: SedConstraint+}	<br>
 * 	|--	|--	SedConjunctionConstraint									<br>
 * 	|--	|--	SedDisjunctionConstraint									<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SedStateError						{location: SedLabel}				<br>
 * 	|--	SedStatementError				{orig_statement: SedLabel}			<br>
 * 	|--	|--	SedAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * 	|--	SedExpressionError				{orig_expression: SedExpression}	<br>
 * 	|--	|--	SedAbstractExpressionError										<br>
 * 	|--	|--	|--	SedInsExpressionError	ins_expr(expr, oprt)				<br>
 * 	|--	|--	|--	SedSetExpressionError	set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	|--	SedAddExpressionError	add_expr(orig_expr, oprt, oprd)		<br>
 * 	|--	|--	SedConcretExpressionError										<br>
 * 	|--	|--	|--	SedBoolExpressionError	{orig_expr : boolean}				<br>
 * 	|--	|--	|--	|--	SedSetBoolExpressionError	set_bool(expr, bool|expr)	<br>
 * 	|--	|--	|--	|--	SedNotBoolExpressionError	not_bool(expr)				<br>
 * 	|--	|--	|-- SedCharExpressionError	{orig_expr : char|uchar}			<br>
 * 	|--	|--	|--	|--	SedSetCharExpressionError	set_char(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedAddCharExpressionError	add_char(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedMulCharExpressionError	mul_char(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedAndCharExpressionError	and_char(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedIorCharExpressionError	ior_char(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedXorCharExpressionError	xor_char(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedNegCharExpressionError	neg_char(expr)				<br>
 * 	|--	|--	|--	|--	SedRsvCharExpressionError	rsv_char(expr)				<br>
 * 	|--	|--	|--	|--	SedIncCharExpressionError	inc_char(expr)				<br>
 * 	|--	|--	|--	|--	SedDecCharExpressionError	dec_char(expr)				<br>
 * 	|--	|--	|--	|--	SedExtCharExpressionError	ext_char(expr)				<br>
 * 	|--	|--	|--	|--	SedShkCharExpressionError	shk_char(expr)				<br>
 * 	|--	|--	|--	|--	SedChgCharExpressionError	chg_char(expr)				<br>
 * 	|--	|--	|--	SedLongExpressionError	{orig_expr : (u)int|long|llong}		<br>
 * 	|--	|--	|--	|--	SedSetLongExpressionError	set_long(expr, long|expr)	<br>
 * 	|--	|--	|--	|--	SedAddLongExpressionError	add_long(expr, long|expr)	<br>
 * 	|--	|--	|--	|--	SedMulLongExpressionError	mul_long(expr, long|expr)	<br>
 * 	|--	|--	|--	|--	SedAndLongExpressionError	and_long(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedIorLongExpressionError	ior_long(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedXorLongExpressionError	xor_long(expr, char|expr)	<br>
 * 	|--	|--	|--	|--	SedNegLongExpressionError	neg_long(expr)				<br>
 * 	|--	|--	|--	|--	SedRsvLongExpressionError	rsv_long(expr)				<br>
 * 	|--	|--	|--	|--	SedIncLongExpressionError	inc_long(expr)				<br>
 * 	|--	|--	|--	|--	SedDecLongExpressionError	dec_long(expr)				<br>
 * 	|--	|--	|--	|--	SedExtLongExpressionError	ext_long(expr)				<br>
 * 	|--	|--	|--	|--	SedShkLongExpressionError	shk_long(expr)				<br>
 * 	|--	|--	|--	|--	SedChgLongExpressionError	chg_long(expr)				<br>
 * 	|--	|--	|--	SedRealExpressionError	{orig_expr : float|double}			<br>
 * 	|--	|--	|--	|--	SedSetRealExpressionError	set_real(expr, double|expr)	<br>
 * 	|--	|--	|--	|--	SedAddRealExpressionError	add_real(expr, double|expr)	<br>
 * 	|--	|--	|--	|--	SedMulRealExpressionError	mul_real(expr, double|expr)	<br>
 * 	|--	|--	|--	|--	SedNegRealExpressionError	neg_real(expr)				<br>
 * 	|--	|--	|--	|--	SedIncRealExpressionError	inc_real(expr)				<br>
 * 	|--	|--	|--	|--	SedDecRealExpressionError	dec_real(expr)				<br>
 * 	|--	|--	|--	|--	SedExtRealExpressionError	ext_real(expr)				<br>
 * 	|--	|--	|--	|--	SedShkRealExpressionError	shk_real(expr)				<br>
 * 	|--	|--	|--	|--	SedChgRealExpressionError	chg_real(expr)				<br>
 * 	|--	|--	|--	SedAddrExpressionError	{orig_expr : pointer|address}		<br>
 * 	|--	|--	|--	|--	SedSetAddrExpressionError	set_addr(expr, long|expr)	<br>
 * 	|--	|--	|--	|--	SedAddAddrExpressionError	add_addr(expr, long|expr)	<br>
 * 	|--	|--	|--	|--	SedIncAddrExpressionError	inc_addr(expr)				<br>
 * 	|--	|--	|--	|--	SedDecAddrExpressionError	dec_addr(expr)				<br>
 * 	|--	|--	|--	|--	SedChgAddrExpressionError	chg_addr(expr)				<br>
 * 	|--	|--	|--	SedByteExpressionError	{orig_expr : struct|union|void}		<br>
 * 	|--	|--	|--	|--	SedSetByteExpressionError	set_byte(expr, expr)		<br>
 * 	|--	|--	|--	|--	SedChgByteExpressionError	chg_byte(expr)				<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SedNode {
	
	/* attributes */
	/** the cir-node to which the SedNode represents **/
	private CirNode cir_source;
	/** the parent of this node or null if it is root **/
	private SedNode parent;
	/** the index of the node in its parent or -1 if it is root **/
	private int child_index;
	/** the children added under the SedNode **/
	private List<SedNode> children;
	/**
	 * create an isolated sed-node without any children under any parent.
	 * @param cir_source
	 */
	public SedNode(CirNode cir_source) {
		this.cir_source = cir_source;
		this.parent = null;
		this.child_index = -1;
		this.children = new LinkedList<SedNode>();
	}
	
	/* getters */
	/**
	 * @return whether there is cir-soruce to which the node represents
	 */
	public boolean has_cir_source() {
		return this.cir_source != null;
	}
	/**
	 * @return the cir-node to which the SedNode represents
	 */
	public CirNode get_cir_source() {
		return this.cir_source;
	}
	/**
	 * @return whether the node is a root without any parent node
	 */
	public boolean is_root() {
		return this.parent == null;
	}
	/**
	 * @return the parent node in which the SedNode is created
	 */
	public SedNode get_parent() {
		return this.parent;
	}
	/**
	 * @return the index of the child under its parent or -1
	 */
	public int get_child_index() {
		return this.child_index;
	}
	/**
	 * @return the children under the node as parent
	 */
	public Iterable<SedNode> get_children() {
		return this.children;
	}
	/**
	 * @return the number of children in this node
	 */
	public int number_of_children() {
		return this.children.size();
	}
	/**
	 * @param k
	 * @return the kth child under the parent
	 * @throws IndexOutOfBoundsException
	 */
	public SedNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return whether the node is a leaf without any children
	 */
	public boolean is_leaf() {
		return this.children.isEmpty();
	}
	/**
	 * add a child in the tail of the node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	public void add_child(SedNode child) throws IllegalArgumentException {
		if(child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			if(child.parent != null) child = child.clone();
			child.child_index = this.children.size();
			child.parent = this;
			this.children.add(child);
		}
	}
	public SedNode clone() {
		SedNode parent = this.clone_self();
		for(SedNode child : this.children) {
			parent.add_child(child.clone());
		}
		return parent;
	}
	@Override
	public String toString() {
		try {
			return this.generate_code();
		} catch (Exception e) {
			return null;
		}
	}
	
	/* setters */
	/**
	 * @return clone the node itself as isolated
	 */
	protected abstract SedNode clone_self();
	/**
	 * @return the code generated to describe this node
	 * @throws Exception
	 */
	public abstract String generate_code() throws Exception;
	
}
