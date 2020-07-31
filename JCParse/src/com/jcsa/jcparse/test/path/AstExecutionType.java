package com.jcsa.jcparse.test.path;

public enum AstExecutionType {
	
	/** get into a node as statement **/	beg_stmt,
	
	/** get into a node as expression **/	beg_expr,
	
	/** get from a node as expression **/	end_expr,
	
	/** get from a node as statement **/	end_stmt,
	
}
