package com.jcsa.jcmutest.mutant.sec2mutant.lang.desc;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecNode;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecKeyword;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymContexts;

/**
 * <code>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecNode																	<br>
 * 	SecDescription						{statement: SecStatement}			<br>
 * 	|--	SecConstraint					asserton(stmt, expr) === evaluator	<br>
 * 	|--	SecStateError														<br>
 * 	|--	SecDescriptions														<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecStateError															<br>
 * 	|--	SecStatementError				{orig_stmt: SecStatement}			<br>
 * 	|--	|--	SecAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SecDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SecSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * 	|--	SecExpressionError				{orig_expr: SecExpression}			<br>
 * 	|--	|--	SecSetExpressionError		set_expr(orig_expr, muta_expr)		<br>
 * 	|--	|--	SecAddExpressionError		add_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecInsExpressionError		ins_expr(orig_expr, oprt, muta_expr)<br>
 * 	|--	|--	SecUnyExpressionError		uny_expr(orig_expr, oprt)			<br>
 * 	+----------------------------------------------------------------------+<br>
 * 	SecDescriptions						{descriptions: SecDescription+}		<br>
 * 	|--	SecConjunctDescriptions												<br>
 * 	|--	SecDisjunctDescriptions												<br>
 * 	+----------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 */
public abstract class SecDescription extends SecNode {
	
	public SecDescription(CirStatement statement,
			SecKeywords keyword) throws Exception {
		this.add_child(new SecStatement(statement));
		this.add_child(new SecKeyword(keyword));
	}
	
	public SecStatement get_location() {
		return (SecStatement) this.get_child(0);
	}
	
	public SecKeyword get_keyword() { 
		return (SecKeyword) this.get_child(1);
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_location().generate_code() + ":" + 
				this.get_keyword().generate_code() + ":" +
				this.generate_content();
	}
	
	/**
	 * @return content following location:keyword:content
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;
	
	/**
	 * @return whether the descriptions are of all constraints
	 */
	public abstract boolean is_constraint();
	
	/**
	 * @return whether the descriptions are of all state errors
	 */
	public abstract boolean is_state_error();
	
	/**
	 * @return whether the descriptions are of either state errors or constraints.
	 */
	public boolean is_consistent() {
		return this.is_constraint() || this.is_state_error();
	}
	
	/**
	 * @param contexts
	 * @return the description optimized based on the given contexts.
	 * @throws Exception
	 */
	public abstract SecDescription optimize(SymContexts contexts) throws Exception;
	
	/**
	 * @return the description optimized without contextual information
	 *  	   which provides a purely static analysis approach.
	 * @throws Exception
	 */
	public SecDescription optimize() throws Exception {
		return this.optimize(null);
	}
	
}
