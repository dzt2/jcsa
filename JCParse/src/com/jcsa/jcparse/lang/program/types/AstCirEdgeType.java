package com.jcsa.jcparse.lang.program.types;

/**
 * 	The type of AstCirEdge that represents dependence relationships.
 * 	
 * 	@author yukimula
 *
 */
public enum AstCirEdgeType {
	
	/** break | continue | goto **/	skip_depend,
	/** return --> funct **/		retr_depend,
	/** funct --> stmt **/			func_depend,
	/** if | for | while | do **/	true_depend,
	/** if **/						fals_depend,
	/** switch **/					case_depend,
	
	
}
