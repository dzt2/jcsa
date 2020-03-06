package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBSA_Mutation extends SingleMutation {

	protected OBSA_Mutation(MutationMode mode, 
			AstBitwiseAssignExpression location) throws Exception {
		super(MutOperator.OBSA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseAssignExpression expr = (AstBitwiseAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case BAN_LSH_A:
		case BAN_RSH_A:
			return operator == COperator.bit_and_assign;
		case BOR_LSH_A:
		case BOR_RSH_A:
			return operator == COperator.bit_or_assign;
		case BXR_LSH_A:
		case BXR_RSH_A:
			return operator == COperator.bit_xor_assign;
		default: return false;
		}
	}

}
