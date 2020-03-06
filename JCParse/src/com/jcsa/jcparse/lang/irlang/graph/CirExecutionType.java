package com.jcsa.jcparse.lang.irlang.graph;

/**
 * <code>
 * 	assign_stmt
 * 	==>	bin_assign	[base]
 * 	==> inc_assign	[base]
 * 	==>	ini_assign	[base]
 * 	==> ret_assign	[base]
 * 	==> sav_assign	[base]
 * 	==> wat_assign	[wait]
 * 	goto_stmt		[none]
 * 	if_stmt			[brch]
 * 	case_stmt		[brch]
 * 	tag_stmt		[none]
 * 	call_stmt		[call]
 * </code>
 * @author yukimula
 *
 */
public enum CirExecutionType {
	/** CirAssignStatement except CirWaitAssignStatement **/	base, 
	/** CirIfStatement and CirCaseStatement **/					brch, 
	/** CirCallStatement **/									call, 
	/** CirWaitStatement **/									wait, 
	/** CirGotoStatement and CirTagStatement **/				none,
}
