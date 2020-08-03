package com.jcsa.jcparse.lang.ir.stmt.transit;

import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

/**
 * goto label.
 * @author yukimula
 *
 */
public interface CirUnconditionStatement extends CirStatement {
	
	/**
	 * @return the label of the statement that this statement skips to
	 */
	public CirLabel get_goto_label();
	
}
