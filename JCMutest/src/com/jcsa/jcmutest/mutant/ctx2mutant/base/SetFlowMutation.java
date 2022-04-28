package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class SetFlowMutation extends ContextMutation {
	
	protected SetFlowMutation(AstCirNode location, 
			AstCirNode orig_next, AstCirNode muta_next) throws Exception {
		super(ContextMutaClass.set_flow, location, 
				SymbolFactory.sym_constant(orig_next.get_node_id()), 
				SymbolFactory.sym_constant(muta_next.get_node_id()));
	}
	
	/**
	 * @return	the node ID of the next AstCirNode for being executed in original program
	 */
	public int get_original_next_ID() { return ((SymbolConstant) this.get_loperand()).get_int(); }
	
	/**
	 * @return	the node ID of the next AstCirNode for being executed in mutation program
	 */
	public int get_mutation_next_ID() { return ((SymbolConstant) this.get_roperand()).get_int(); }
	
}
