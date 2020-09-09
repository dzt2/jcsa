package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CTRPInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		AstNode location = this.mutation.get_location();
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.cir_tree.get_localizer().
						get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
				
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException(this.mutation.toString());
	}

	@Override
	protected void generate_infections() throws Exception {
		/* find the case-statement and its roperand */
		AstNode parameter = (AstNode) this.mutation.get_parameter();
		AstCaseStatement case_statement = null;
		while(parameter != null) {
			if(parameter instanceof AstCaseStatement) {
				case_statement = (AstCaseStatement) parameter;
				break;
			}
			else {
				parameter = parameter.get_parent();
			}
		}
		if(case_statement == null)
			throw new IllegalArgumentException(this.mutation.toString());
		
		/* cir-code getters */
		CirAssignStatement statement = (CirAssignStatement) this.statement;
		CirExpression orig_expression = statement.get_rvalue();
		
	}

}
