package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * <code>
 * 	|--	SedStatementError		{statement: SedStatement}			<br>
 * 	|--	|--	SedAddStatement		(add_statement(statement))			<br>
 * 	|--	|--	SedDelStatement		(del_statement(statement))			<br>
 * 	|--	|--	SedSetStatement		(set_statement(source, target))		<br>
 * </code>
 * @author dzt2
 *
 */
public abstract class SedStatementError extends SedStateError {
	
	/**
	 * @return the label of the statement where the error expects to change
	 */
	public SedLabel get_orig_statement() {
		return (SedLabel) this.get_child(1);
	}
	
}
