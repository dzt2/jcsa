package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * CirStatement [index]	|-- CirAssignStatement
 * 						|-- CirGotoStatement 
 * 						|-- CirCaseStatement 
 * 						|-- CirIfStatement 	
 * 						|-- CirCallStatement 
 * 						|-- CirTagStatement
 * @author yukimula
 *
 */
public interface CirStatement extends CirNode {
}
