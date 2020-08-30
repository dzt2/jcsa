package com.jcsa.jcmutest.mutant.sed2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedLabel extends SedNode {
	
	private CirStatement cir_statement;
	private CirExecution cir_execution;
	public SedLabel(CirNode cir_source, CirStatement cir_statement) {
		super(cir_source);
		this.cir_statement = cir_statement;
		if(cir_statement != null) {
			try {
				this.cir_execution = this.cir_statement.get_tree().
						get_function_call_graph().get_function(cir_statement).
						get_flow_graph().get_execution(this.cir_statement);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * @return the statement that the label represents
	 */
	public CirStatement get_cir_statement() { return this.cir_statement; }
	/**
	 * @return whether the executional node of the statement is found
	 */
	public boolean has_cir_execution() { return this.cir_execution != null; }
	/**
	 * @return the executional node to which the label represents
	 */
	public CirExecution get_cir_execution() { return this.cir_execution; }

	@Override
	protected SedNode clone_self() {
		return new SedLabel(this.get_cir_source(), this.cir_statement);
	}

	@Override
	public String generate_code() throws Exception {
		if(this.cir_execution != null)
			return this.cir_execution.toString();
		else
			return "unknown_func[?]";
	}

}
