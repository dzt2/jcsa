package com.jcsa.jcmuta.mutant.error2mutation;

/**
 * Propagation method:<br>
 * 	(1) expression --> expression as parent:<br>
 * 		{defer, address, field, cast, compute, init_body, wait}
 * 	(2) expression as condition --> statement in true branch<br>
 * 	(3) expression as condition --> statement in false branch<br>
 * 	(4) expression as right-val --> left-value in assignment.<br>
 * 	(5) expression as definition --> usage point(s) in other statements.<br>
 * 	(6) expression as argument --> expression as parameter in calling.<br>
 * 	(7) expression as return point --> expression as waiting expression.<br>
 * 
 * TODO implement this class!
 * @author yukimula
 *
 */
public class StatePropagation {

}
