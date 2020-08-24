package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SadToken															<br>
 * 	|--	SadLabel						{pointer: CirExecution}			<br>
 * 	|-- SadField						{name: String}					<br>
 * 	|--	SadArgumentList													<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SadToken extends SadNode {
	
	protected SadToken(CirNode source) {
		super(source);
	}
	
}
