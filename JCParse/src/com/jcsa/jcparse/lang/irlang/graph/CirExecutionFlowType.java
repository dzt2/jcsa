package com.jcsa.jcparse.lang.irlang.graph;

/**
 * <code>
 * 	1. base --> *			[next_flow]
 * 	2. call	--> {beg_stmt}	[call_flow]	{user_defined}
 * 	3. {end_stmt} --> wait	[retr_flow]	{user_defined}
 * 	4. call	--> wait		[skip_flow]	{pointer + external}
 * 	5. brch --> *			[true_flow|fals_flow]
 * 	6. none --> *			[next_flow]
 * </code>
 * @author yukimula
 *
 */
public enum CirExecutionFlowType {
	next_flow,
	true_flow,
	fals_flow,
	call_flow,
	retr_flow,
	skip_flow,
	virt_flow,
}
