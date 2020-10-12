package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

/**
 * The level of detecting a cir-mutation
 * 
 * @author yukimula
 *
 */
public enum CirDetectionLevel {
	
	not_reached,					/* 					==> False 	*/
	
	not_satisfied,					/* [False, False]	==> False 	*/
	
	satisfiable_not_infected,		/* [?, False]		==> False 	*/
	satisfiable_infectable,			/* [?, ?] 			==> ? 		*/
	
	satisfied_not_infected,			/* [True, False]	==>	False 	*/
	satisfied_infectable,			/* [True, ?]		==> ? 		*/
	satisfied_infected,				/* [True, True] 	==> True 	*/
	
}
