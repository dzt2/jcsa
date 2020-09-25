package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OEXAMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.get_cir_nodes(
				tree, source.get_location(), CirBinAssignStatement.class).get(0);
		COperator operator, parameter = (COperator) source.get_parameter();
		switch(parameter) {
		case arith_add_assign:	operator = COperator.arith_add; break;
		case arith_sub_assign:	operator = COperator.arith_sub; break;
		case arith_mul_assign:	operator = COperator.arith_mul; break;
		case arith_div_assign:	operator = COperator.arith_div; break;
		case arith_mod_assign:	operator = COperator.arith_mod; break;
		case bit_and_assign:	operator = COperator.bit_and; break;
		case bit_or_assign:		operator = COperator.bit_or; break;
		case bit_xor_assign:	operator = COperator.bit_xor; 	break;
		case left_shift_assign:	operator = COperator.left_shift; break;
		case righ_shift_assign:	operator = COperator.righ_shift; break;
		default: throw new IllegalArgumentException("Invalid parameter: " + parameter);
		}
		targets.add(CirMutations.ins_expression(statement.get_rvalue(), operator, statement.get_lvalue()));
	}

}
