package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * [switch.condition == case.expression] --> trap(switch)
 * @author yukimula
 *
 */
public class CTRPCirMutationParser extends CirMutationParser {
	
	/**
	 * @param location
	 * @return the switch-statement where the location is seeded
	 * @throws Exception
	 */
	private AstSwitchStatement get_switch_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstSwitchStatement) {
				return (AstSwitchStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in switch-statement");
	}
	
	/**
	 * @param location
	 * @return the case-statement where the location is seeded
	 * @throws Exception
	 */
	private AstCaseStatement get_case_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstCaseStatement) {
				return (AstCaseStatement) location;
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("Not in case-statement");
	}
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(cir_tree, 
				this.get_switch_statement(mutation.get_location()), 
				CirSaveAssignStatement.class);
	}
	
	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		AstSwitchStatement switch_statement = this.get_switch_statement(mutation.get_location());
		AstCaseStatement case_statement = this.get_case_statement((AstNode) mutation.get_parameter());
		CirAssignStatement statement1 = (CirAssignStatement) 
					this.get_cir_node(cir_tree, switch_statement, CirSaveAssignStatement.class);
		CirCaseStatement statement2 = (CirCaseStatement) 
							this.get_cir_node(cir_tree, case_statement, CirCaseStatement.class);
		
		SymExpression condition1 = SymFactory.parse(statement2.get_condition());
		SymExpression condition2 = SymFactory.sym_statement(statement2);
		condition2 = SymFactory.greater_eq(statement2, Integer.valueOf(1));
		SymExpression condition = SymFactory.logic_and(condition1, condition2);
		CirConstraint constraint = mutations.expression_constraint(statement2, condition, true);
		
		infections.put(mutations.trap_error(statement1), constraint);
	}

}
