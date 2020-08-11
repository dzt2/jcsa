package com.jcsa.jcmutest.mutant.ast2mutant.generate.refs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VRRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		if(location instanceof AstIdExpression) {
			return !this.is_left_reference(location) && this.is_numeric_expression(location);
		}
		else if(location instanceof AstFunctionDefinition) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/** the local names of id-expressions used in a function scope **/
	private Map<String, CType> local_names = new HashMap<String, CType>();
	
	/**
	 * update the local-names
	 * @param function
	 * @throws Exception
	 */
	private void update_local_names(AstFunctionDefinition function) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body());
		this.local_names.clear();
		
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstIdExpression) {
				AstIdExpression expression = (AstIdExpression) node;
				if(this.is_numeric_expression(expression)) {
					CType data_type = CTypeAnalyzer.
							get_value_type(expression.get_value_type());
					String name = expression.get_name();
					if(!this.local_names.containsKey(name)) {
						this.local_names.put(name, data_type);
					}
				}
			}
			else {
				for(int k = 0; k < node.number_of_children(); k++) {
					queue.add(node.get_child(k));
				}
			}
		}
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		if(location instanceof AstFunctionDefinition) {
			this.update_local_names((AstFunctionDefinition) location);
		}
		else {
			AstIdExpression source = (AstIdExpression) location;
			String sname = source.get_name();
			CType stype = CTypeAnalyzer.get_value_type(source.get_value_type());
			for(String tname : this.local_names.keySet()) {
				if(!sname.equals(tname)) {
					CType ttype = this.local_names.get(tname);
					if(CTypeAnalyzer.is_character(stype) && CTypeAnalyzer.is_character(ttype)) {
						mutations.add(AstMutations.VRRP(source, tname));
					}
					else if(CTypeAnalyzer.is_integer(stype) && CTypeAnalyzer.is_integer(ttype)) {
						mutations.add(AstMutations.VRRP(source, tname));
					}
					else if(CTypeAnalyzer.is_real(stype) && CTypeAnalyzer.is_real(ttype)) {
						mutations.add(AstMutations.VRRP(source, tname));
					}
				}
			}
		}
	}

}
