package com.jcsa.jcmutest.mutant.sec2mutant.muta.trap;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPInfectionParser extends SecInfectionParser {
	
	@Override
	protected CirStatement get_statement() throws Exception {
		return (CirStatement) this.get_cir_nodes(
				this.location, CirIfStatement.class).get(0);
	}
	
	@Override
	protected void generate_infections() throws Exception {
		CirIfStatement if_statement = (CirIfStatement) this.
				get_cir_nodes(this.location, CirIfStatement.class).get(0);
		int execute_times = ((Integer) mutation.get_parameter()).intValue() + 1;
		
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		constraints.add(SecFactory.assert_constraint(
						if_statement, if_statement.get_condition(), true));
		constraints.add(SecFactory.execute_constraint(if_statement, execute_times));
		
		this.add_infection(
				SecFactory.conjunct(if_statement, constraints), 
				SecFactory.trap_statement(if_statement));
	}
	
}
