package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OASN_Mutation extends SingleMutation {

	protected OASN_Mutation(MutationMode mode, AstArithBinaryExpression location) throws Exception {
		super(MutOperator.OASN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstArithBinaryExpression expr = (AstArithBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case ADD_LSH: case ADD_RSH:
			return operator == COperator.arith_add;
		case SUB_LSH: case SUB_RSH:
			return operator == COperator.arith_sub;
		case MUL_LSH: case MUL_RSH:
			return operator == COperator.arith_mul;
		case DIV_LSH: case DIV_RSH:
			return operator == COperator.arith_div;
		case MOD_LSH: case MOD_RSH:
			return operator == COperator.arith_mod;
		default: return false;
		}
	}

}
