package com.jcsa.jcparse.flwa.depend;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;

/**
 * The data-flow relationship is defined as:
 * 		[def, use, flows]
 * @author yukimula
 *
 */
public class CDependReference {
	
	private CirExpression def, use;
	private List<CirExecutionFlow> flows;
	
	protected CDependReference(CirExpression def, CirExpression use) throws Exception {
		if(def == null)
			throw new IllegalArgumentException("Invalid def: null");
		else if(use == null)
			throw new IllegalArgumentException("Invalid use: null");
		else {
			this.def = def; this.use = use;
			this.flows = new LinkedList<CirExecutionFlow>();
		}
	}
	
	public CirExpression get_def() { return this.def; }
	public CirExpression get_use() { return this.use; }
	public Iterable<CirExecutionFlow> get_flow_path() { return this.flows; }
	protected void set_flow_path(Iterable<CirExecutionFlow> flows) throws Exception {
		this.flows.clear();
		for(CirExecutionFlow flow : flows) {
			if(flow != null) this.flows.add(flow);
		}
	}
	protected void add_flow(CirExecutionFlow flow) throws Exception {
		this.flows.add(flow);
	}
	
	@Override
	public String toString() {
		try {
			return def.generate_code(true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
