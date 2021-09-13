package com.jcsa.jcmutest.mutant.cir2mutant.backup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It maintains the evaluation state of an attribute
 * @author yukimula
 *
 */
public class CirAttributeState {
	
	/* attributes */
	private CirAttribute attribute;
	private List<CirAnnotationUnit> units;
	private List<Boolean> evaluation_results;
	protected CirAttributeState(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.attribute = attribute;
			this.units = new ArrayList<CirAnnotationUnit>();
			this.evaluation_results = new ArrayList<Boolean>();
			
			Set<CirAnnotation> annotations = new HashSet<CirAnnotation>();
			CirAnnotationUtil.generate_annotations(attribute, annotations);
			for(CirAnnotation annotation : annotations) {
				this.units.add(new CirAnnotationUnit(annotation));
			}
		}
	}
	
	/* getters */
	/**
	 * @return the attribute to be evaluated in the state
	 */
	public CirAttribute get_attribute() { return this.attribute; }
	/**
	 * @return the evaluation results of the attribute
	 */
	public Iterable<Boolean> get_evaluation_results() { return this.evaluation_results; }
	/**
	 * @return the units of the annotations generated from the attribute
	 */
	public Iterable<CirAnnotationUnit> get_units() { return this.units; }
	
	/* counters */
	/**
	 * @return the number of attribute being executed in dynamic analysis
	 */
	public int number_of_executions() { return this.evaluation_results.size(); }
	/**
	 * @return the number of attribute being accepted in dynamic analysis
	 */
	public int number_of_acceptions() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null) {
				if(result.booleanValue()) {
					counter++;
				}
			}
		}
		return counter;
	}
	/**
	 * @return the number of attribute being rejected in dynamic analysis
	 */
	public int number_of_rejections() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null) {
				if(!result.booleanValue()) {
					counter++;
				}
			}
		}
		return counter;
	}
	/**
	 * @return the number of attribute being accepted or likely be acceptable
	 */
	public int number_of_acceptable() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result == null || result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return whether the attribute in the node has been evaluated before
	 */
	public boolean is_executed() { return !this.evaluation_results.isEmpty(); }
	/**
	 * @return whether the attribute in the node has been accepted before.
	 */
	public boolean is_accepted() {
		for(Boolean result: this.evaluation_results) {
			if(result != null && result) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return whether the attribute in the node has always been rejected.
	 */
	public boolean is_rejected() {
		for(Boolean result: this.evaluation_results) {
			if(result == null) {
				return false;
			}
			else if(result) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @return whether the attribute in the node can be accepted before.
	 */
	public boolean is_acceptable() {
		for(Boolean result: this.evaluation_results) {
			if(result == null || result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	
	/* setters */
	/**
	 * clear the evaluation results and generated annotations
	 */
	protected void clc() { 
		this.evaluation_results.clear();
		for(CirAnnotationUnit unit : this.units) {
			unit.clc();
		}
	}
	/**
	 * evaluate the CirAttribute on context and record the result
	 * @param context
	 * @return evaluate the attribute and update annotations
	 * @throws Exception
	 */
	protected Boolean add(SymbolProcess context) throws Exception {
		Boolean result = this.attribute.evaluate(context);
		this.evaluation_results.add(result);
		for(CirAnnotationUnit unit : this.units) {
			unit.add(context);
		}
		return result;
	}
	/**
	 * summarize the abstract annotations from symbolic annotations of the attribute
	 * @throws Exception
	 */
	protected void sum() throws Exception {
		for(CirAnnotationUnit unit : this.units) {
			unit.sum();
		}
	}
	
}
