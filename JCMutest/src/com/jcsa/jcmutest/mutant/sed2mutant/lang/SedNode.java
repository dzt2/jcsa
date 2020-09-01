package com.jcsa.jcmutest.mutant.sed2mutant.lang;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * The symbolic execution description language is defined as following:<br>
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SedNode							{cir_source: CirNode}				<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SedToken															<br>
 * 	|--	SedOperator					{operator: COperator}				<br>
 * 	|--	SedField					{name: String}						<br>
 * 	|--	SedLabel					{location: CirStatement}			<br>
 * 	|--	SedArgumentList													<br>
 * 	+------------------------------------------------------------------+<br>
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
 * 	+------------------------------------------------------------------+<br>
 * 	SedStatement					{label: SedLabel}					<br>
 * 	|--	SedAssignStatement												<br>
 * 	|--	SedGotoStatement												<br>
 * 	|--	SedIfStatement													<br>
 * 	|--	SedCallStatement												<br>
 * 	|--	SedWaitStatement												<br>
 * 	|--	SedTagStatement													<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SedConstraint														<br>
 * 	|--	SedBasicConstraint				{location: SedLabel}			<br>
 * 	|--	|--	SedExecutionConstraint		execute(location, int)			<br>
 * 	|--	|--	SedConditionConstraint		assert(location, expr)			<br>
 * 	|--	SedCompositeConstraint			{constraints: SedConstraint+}	<br>
 * 	|--	|--	SedConjunctionConstraint									<br>
 * 	|--	|--	SedDisjunctionConstraint									<br>
 * 	+------------------------------------------------------------------+<br>
 * 	SedStateError						{location: SedLabel}			<br>
 * 	|--	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(statement)				<br>
 * 	|--	|--	SedDelStatementError		del_stmt(statement)				<br>
 * 	|--	|--	SedMutStatementError		mut_stmt(statement, statement)	<br>
 * 	|--	SedExpressionError				{orig_expression: SedExpression}<br>
 * 	|--	|--	SedInsExpressionError		ins_expr(oprt, expr)			<br>
 *	|--	|--	SedSetExpressionError		set_expr(expr, expr)			<br>
 * 	|--	|--	SedAddExpressionError		add_expr(expr, oprt, expr)		<br>
 * 	|--	SedAbstractError				{orig_expression: SedExpression}<br>
 * 	|--	|--	SedNotBooleanError			not_expr(expr)					<br>
 * 	|--	|--	SedRsvIntegerError			rsv_expr(expr)					<br>
 * 	|--	|--	SedNegNumericError			neg_expr(expr)					<br>
 * 	|--	|--	SedSetBooleanError			set_bool(expr, bool)			<br>
 * 	|--	|--	SedMutBooleanError			mut_bool(expr, expr)			<br>
 * 	|--	|--	SedSetCharacterError		set_char(expr, char)			<br>
 * 	|--	|--	SedMutCharacterError		mut_char(expr, expr)			<br>
 * 	|--	|--	SedChgCharacterError		chg_char(expr)					<br>
 * 	|--	|--	SedSetIntegerError			set_int(expr, long)				<br>
 * 	|--	|--	SedMutIntegerError			mut_int(expr, expr)				<br>
 * 	|--	|--	SedChgIntegerError			chg_int(expr)					<br>
 * 	|--	|--	SedSetDoubleError			set_real(expr, double)			<br>
 * 	|--	|--	SedMutDoubleError			mut_real(expr, expr)			<br>
 * 	|--	|--	SedChgDoubleError			chg_real(expr)					<br>
 * 	|--	|--	SedSetAddressError			set_addr(expr, long)			<br>
 * 	|--	|--	SedMutAddressError			mut_addr(expr, expr)			<br>
 * 	|--	|--	SedChgAddressError			chg_addr(expr)					<br>
 * 	|--	|--	SedMutStructError			mut_body(expr, expr)			<br>
 * 	|--	|--	SedChgStructError			chg_body(expr)					<br>
 * 	|--	|--	SedAddIntegerError			add_int(expr, long)				<br>
 * 	|--	|--	SedIncIntegerError			inc_int(expr)					<br>
 * 	|--	|--	SedDecIntegerError			dec_int(expr)					<br>
 * 	|--	|--	SedAddDoubleError			add_real(expr, double)			<br>
 * 	|--	|--	SedIncDoubleError|SedDecDoubleError							<br>
 * 	|--	|--	SedAddAddressError			add_addr(expr, long)			<br>
 * 	|--	|--	SedIncAddressError|SedDecAddressError						<br>
 * 	|--	|--	SedMulIntegerError			mul_int(expr, long)				<br>
 * 	|--	|--	SedGrowIntegerError			grw_int(expr)					<br>
 * 	|--	|--	SedShrinkIntegerError		shk_int(expr)					<br>
 * 	|--	|--	SedMulDoubleError			mul_real(expr, double)			<br>
 * 	|--	|--	SedGrowDoubleError			grw_real(expr)					<br>
 * 	|--	|--	SedShrinkDoubleError		shk_real(expr)					<br>
 * 	|--	|--	SedMaskIntegerError			mas_int(expr)					<br>
 * 	|--	|--	SedRMaskIntegerError		rmas_int(expr)					<br>
 * 	|--	|--	SedMaskBooleanError			mas_bool(expr)					<br>
 * 	|--	|--	SedRMaskBooleanError		rmas_bool(expr)					<br>
 * 	+------------------------------------------------------------------+<br>
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
