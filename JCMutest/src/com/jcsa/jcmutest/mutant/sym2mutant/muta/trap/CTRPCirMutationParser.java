package com.jcsa.jcmutest.mutant.sym2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

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
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		AstSwitchStatement switch_statement = this.get_switch_statement(mutation.get_location());
		AstCaseStatement case_statement = this.get_case_statement((AstNode) mutation.get_parameter());
		CirAssignStatement statement1 = (CirAssignStatement) 
					this.get_cir_node(cir_tree, switch_statement, CirSaveAssignStatement.class);
		CirCaseStatement statement2 = (CirCaseStatement) 
							this.get_cir_node(cir_tree, case_statement, CirCaseStatement.class);
		
		SymbolExpression condition1 = SymbolFactory.sym_expression(statement2.get_condition());
		SymbolExpression condition2 = SymbolFactory.sym_expression(statement2);
		condition2 = SymbolFactory.greater_eq(statement2, Integer.valueOf(1));
		SymbolExpression condition = SymbolFactory.logic_and(condition1, condition2);
		SymConstraint constraint = SymInstanceUtils.expr_constraint(statement2, condition, true);
		
		infections.put(SymInstanceUtils.trap_error(statement1), constraint);
	}

}
