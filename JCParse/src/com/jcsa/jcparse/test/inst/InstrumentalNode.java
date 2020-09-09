package com.jcsa.jcparse.test.inst;

import java.util.ArrayList;
import java.util.List;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * Each node in instrumental path contains one or multiple units that describe
 * the values of its expressions being evaluated and the beg and end tag of the
 * statement.
 * 
 * @author yukimula
 *
 */
public class InstrumentalNode {
	
	/* definitions */
	/** the instrumental path from dynamic testing **/
	private InstrumentalPath path;
	/** the index of the node in the execution path **/
	private int index;
	/** the executional node that the node corresponds to **/
	private CirExecution execution;
	/** the sequence of units describing the instrumental event **/
	private List<InstrumentalUnit> units;
	
	/* constructor */
	/**
	 * create an isolated node in the path w.r.t. the executional node with units
	 * @param path
	 * @param execution
	 * @param units
	 * @throws Exception
	 */
	protected InstrumentalNode(InstrumentalPath path, int index, CirExecution 
			execution, Iterable<InstrumentalUnit> units) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution");
		else {
			this.path = path;
			this.index = index;
			this.execution = execution;
			this.units = new ArrayList<InstrumentalUnit>();
			/* ignore the beg-stmt and end-stmt of the execution */
			if(units != null) {
				for(InstrumentalUnit unit : units) {
					if(unit.get_type() == InstrumentalType.evaluate) {
						this.units.add(unit);
					}
				}
			}
		}
	}
	
	/* getters */
	/**
	 * @return the executional path in which the node is created
	 */
	public InstrumentalPath get_path() { return this.path; }
	/**
	 * @return the index of the node in the path or -1 if it is not linked within
	 */
	public int get_index() { return this.index; }
	/**
	 * @return the executional node of the statement to which the node corresponds
	 */
	public CirExecution get_execution() { return execution; }
	/**
	 * @return the statement to which the node corresponds
	 */
	public CirStatement get_statement() { return execution.get_statement(); }
	/**
	 * @return the units describe the events occur within the execution of the node
	 */
	public Iterable<InstrumentalUnit> get_units() { return units; }
	
}
