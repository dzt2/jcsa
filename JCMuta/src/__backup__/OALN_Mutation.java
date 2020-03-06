package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OALN_Mutation extends SingleMutation {

	protected OALN_Mutation(MutationMode mode, AstArithBinaryExpression location) throws Exception {
		super(MutOperator.OALN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithBinaryExpression expr = (AstArithBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case ADD_LAN: case ADD_LOR:
			return operator == COperator.arith_add;
		case SUB_LAN: case SUB_LOR:
			return operator == COperator.arith_sub;
		case MUL_LAN: case MUL_LOR:
			return operator == COperator.arith_mul;
		case DIV_LAN: case DIV_LOR:
			return operator == COperator.arith_div;
		case MOD_LAN: case MOD_LOR:
			return operator == COperator.arith_mod;
		default: return false;
		}
	}

}
