package com.jcsa.jcmutest.mutant.cir2mutant.__backup__;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It maintains the evaluation status of a CirInfectionNode
 * 
 * @author yukimula
 *
 */
public class CirInfectionNodeData {
	
	/* attributes */
	/** the symbolic attribute that the node maintains state **/
	private CirAttribute 				symbolic_attribute;
	/** the sequence of symbolic evaluation of the attribute **/
	private List<Boolean>				evaluation_results;
	/** the set of symbolic annotations to represent attribute it describes **/
	private Collection<CirAnnotation>	symbolic_annotations;
	/** the set of concrete annotations generate during symbolic evaluation **/
	private Collection<CirAnnotation>	concrete_annotations;
	/** the set of abstract annotations summarized from the concrete ones **/
	private Collection<CirAnnotation>	abstract_annotations;
	
	/* constructor */
	/**
	 * create an empty data state to maintain the evaluation of CirAttribute
	 * @param attribute
	 * @throws Exception
	 */
	protected CirInfectionNodeData(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute as null");
		}
		else {
			this.symbolic_attribute = attribute;
			this.evaluation_results = new ArrayList<Boolean>();
			this.symbolic_annotations = new HashSet<CirAnnotation>();
			this.concrete_annotations = new HashSet<CirAnnotation>();
			this.abstract_annotations = new HashSet<CirAnnotation>();
			CirAnnotationUtil.generate_annotations(
					this.symbolic_attribute, this.symbolic_annotations);
		}
	}
	
	/* getters */
	/**
	 * @return	the symbolic attribute that the node maintains state
	 */
	public CirAttribute get_attribute() { return this.symbolic_attribute; }
	/**
	 * @return the sequence of symbolic evaluation of the attribute
	 */
	public Iterable<Boolean> get_evaluation_results() { return this.evaluation_results; }
	/**
	 * @return the set of symbolic annotations to represent attribute it describes
	 */
	public Iterable<CirAnnotation> get_symbolic_annotations() { return this.symbolic_annotations; }
	/**
	 * @return the set of concrete annotations generate during symbolic evaluation
	 */
	public Iterable<CirAnnotation> get_concrete_annotations() { return this.concrete_annotations; }
	/**
	 * @return the set of abstract annotations summarized from the concrete ones
	 */
	public Iterable<CirAnnotation> get_abstract_annotations() { return this.abstract_annotations; }
	
	/* setters */
	/**
	 * clear the accumulated evaluation results in the data state
	 */
	protected void clc() {
		this.evaluation_results.clear();
		this.concrete_annotations.clear();
		this.abstract_annotations.clear();
	}
	/**
	 * evaluate the CirAttribute on context and record the result
	 * @param context
	 * @return true (satisfied); false (not); null (unknown)
	 * @throws Exception
	 */
	protected Boolean add(SymbolProcess context) throws Exception {
		Boolean result = this.symbolic_attribute.evaluate(context);
		this.evaluation_results.add(result);
		CirAnnotationUtil.concretize_annotations(
				this.symbolic_annotations, 
				context, this.concrete_annotations);
		return result;
	}
	/**
	 * generate the abstract annotations from concrete
	 * @throws Exception
	 */
	protected void sum() throws Exception {
		this.abstract_annotations.clear();
		CirAnnotationUtil.summarized_annotations(
				this.concrete_annotations, this.abstract_annotations);
	}
	
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
	
}
