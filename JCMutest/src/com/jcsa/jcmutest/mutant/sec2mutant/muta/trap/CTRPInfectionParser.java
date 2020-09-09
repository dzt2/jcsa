package com.jcsa.jcmutest.mutant.sec2mutant.muta.trap;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CTRPInfectionParser extends SecInfectionParser {
	
	@Override
	protected CirStatement get_statement() throws Exception {
		AstNode location = this.location;
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (CirStatement) this.get_cir_nodes(
						location, CirSaveAssignStatement.class).get(0);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException(this.mutation.toString());
	}
	
	@Override
	protected void generate_infections() throws Exception {
		/* get the location where the case needs to be covered */
		AstNode parameter = (AstNode) this.mutation.get_parameter();
		CirCaseStatement case_statement = null;
		while(parameter != null) {
			if(parameter instanceof AstCaseStatement) {
				case_statement = (CirCaseStatement) get_cir_nodes(
						parameter, CirCaseStatement.class).get(0);
				break;
			}
			else {
				parameter = parameter.get_parent();
			}
		}
		if(case_statement == null) {
			throw new IllegalArgumentException(mutation.toString());
		}
		
		/* execute(case-statement) && case-condition == true */
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		constraints.add(SecFactory.execute_constraint(case_statement, 1));
		constraints.add(SecFactory.assert_constraint(
					case_statement, case_statement.get_condition(), true));
		
		/* ==> trap_statement(case_statement) */
		this.add_infection(
				SecFactory.conjunct(case_statement, constraints), 
				SecFactory.trap_statement(case_statement));
	}
	
}
