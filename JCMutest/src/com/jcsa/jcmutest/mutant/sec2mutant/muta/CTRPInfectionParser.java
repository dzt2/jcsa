package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

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
		CirExpression loperand = statement.get_rvalue();
		AstExpression roperand = case_statement.get_expression();
		SymExpression condition = SymFactory.equal_with(loperand, roperand);
		
		/* switch_condition == case_condition -> trap_statement(statement) */
		SecConstraint constraint = 
					SecFactory.assert_constraint(statement, condition, true);
		SecDescription init_error = SecFactory.trap_statement(statement);
		this.add_infection(constraint, init_error);
	}
	
}
