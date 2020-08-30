package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * +------------------------------------------------------------------+<br>
 * 	SedStatement					{label: SedLabel}					<br>
 * 	|--	SedAssignStatement												<br>
 * 	|--	SedGotoStatement												<br>
 * 	|--	SedIfStatement													<br>
 * 	|--	SedCallStatement												<br>
 * 	|--	SedWaitStatement												<br>
 * 	|--	SedTagStatement													<br>
 * 	+------------------------------------------------------------------+<br>
 * @author yukimula
 *
 */
public abstract class SedStatement extends SedNode {

	public SedStatement(CirNode cir_source) {
		super(cir_source);
	}
	
	public SedLabel get_source_label() { 
		return (SedLabel) this.get_child(0);
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_source_label().generate_code() + ": " + this.generate_content() + ";";
	}
	
	protected abstract String generate_content() throws Exception;

}
