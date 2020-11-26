package com.jcsa.jcmutest.mutant.cir2mutant.path;

/**
 * The type of symbolic instance edge.
 * 
 * @author yukimula
 *
 */
public enum SymInstanceEdgeType {
	/** from predicate or call|wait point to reach the next target **/			exec_flow,
	/** from reaching point to the initial state error node as infection **/	infc_flow,
	/** from state error to next error in the inner statement propagates **/	inpa_flow,
	/** from state error to next error in the outside scope propagations **/	oupa_flow,
	/** from error (predicate) to next (flow) error in control methods  **/		cont_flow,
}
