package com.jcsa.jcparse.lang.irlang.stmt;

/**
 * goto_statement --> goto label
 * @author yukimula
 *
 */
public interface CirGotoStatement extends CirStatement {
	public CirLabel get_label();
	public void set_label(CirLabel label) throws IllegalArgumentException;
}
