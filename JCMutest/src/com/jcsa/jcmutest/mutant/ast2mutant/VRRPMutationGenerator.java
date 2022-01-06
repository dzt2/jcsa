package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VRRPMutationGenerator extends MutationGenerator {

	private Map<String, CType> identifiers = new HashMap<>();

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {
		this.identifiers.clear();
		for(AstNode location : locations) {
			if(location instanceof AstIdExpression) {
				AstIdExpression expression = (AstIdExpression) location;
				CType data_type = CTypeAnalyzer.
							get_value_type(expression.get_value_type());
				if(this.is_numeric_expression(expression)) {
					this.identifiers.put(expression.get_name(), data_type);
				}
			}
		}
	}

	@Override
	protected boolean available(AstNode location) throws Exception {
		if(location instanceof AstIdExpression) {
			return this.is_numeric_expression(location)
					&& !this.is_left_reference(location);
		}
		else {
			return false;
		}
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstIdExpression expression = (AstIdExpression) location;
		CType data_type = CTypeAnalyzer.
					get_value_type(expression.get_value_type());

		for(String name : this.identifiers.keySet()) {
			CType ttype = this.identifiers.get(name);
			if(!name.equals(expression.get_name())) {
				if(CTypeAnalyzer.is_character(data_type)
					&& CTypeAnalyzer.is_character(ttype)) {
					mutations.add(AstMutations.VRRP(expression, name));
				}
				else if(CTypeAnalyzer.is_integer(data_type)
						&& CTypeAnalyzer.is_integer(ttype)) {
					mutations.add(AstMutations.VRRP(expression, name));
				}
				else if(CTypeAnalyzer.is_real(data_type)
						&& CTypeAnalyzer.is_real(ttype)) {
					mutations.add(AstMutations.VRRP(expression, name));
				}
			}
		}

	}

}
