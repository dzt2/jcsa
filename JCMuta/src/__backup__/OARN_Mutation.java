package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>{+, -, *m /, %} |==> {<, <=, >, >=, ==, !=}</code>
 * @author yukimula
 *
 */
public class OARN_Mutation extends SingleMutation {

	protected OARN_Mutation(MutationMode mode, AstArithBinaryExpression location) throws Exception {
		super(MutOperator.OARN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithBinaryExpression expr = (AstArithBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case ADD_GRT: case ADD_GRE: case ADD_EQV: case ADD_NEQ: case ADD_SMT: case ADD_SME:
			return operator == COperator.arith_add;
		case SUB_GRT: case SUB_GRE: case SUB_EQV: case SUB_NEQ: case SUB_SMT: case SUB_SME:
			return operator == COperator.arith_sub;
		case MUL_GRT: case MUL_GRE: case MUL_EQV: case MUL_NEQ: case MUL_SMT: case MUL_SME:
			return operator == COperator.arith_mul;
		case DIV_GRT: case DIV_GRE: case DIV_EQV: case DIV_NEQ: case DIV_SMT: case DIV_SME:
			return operator == COperator.arith_div;
		case MOD_GRT: case MOD_GRE: case MOD_EQV: case MOD_NEQ: case MOD_SMT: case MOD_SME:
			return operator == COperator.arith_mod;
		default: return false;
		}
	}

}
