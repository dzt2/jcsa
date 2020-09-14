package com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecStatement;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SecStatementError				{orig_stmt: SecStatement}			<br>
 * 	|--	|--	SecAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SecDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SecSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SecStatementError extends SecStateError {

	public SecStatementError(CirStatement statement, SecKeywords 
			keyword, CirStatement orig_statement) throws Exception {
		super(statement, keyword);
		this.add_child(new SecStatement(orig_statement));
	}
	
	/**
	 * @return statement where fault is seeded
	 */
	public SecStatement get_orig_statement() {
		return (SecStatement) this.get_child(2);
	}

	@Override
	public CirNode get_cir_location() {
		return this.get_orig_statement().get_statement();
	}
	
}
