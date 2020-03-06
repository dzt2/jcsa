package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBBA_Mutation extends SingleMutation {

	protected OBBA_Mutation(MutationMode mode, 
			AstBitwiseAssignExpression location) throws Exception {
		super(MutOperator.OBBA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseAssignExpression expr = (AstBitwiseAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case BAN_BOR_A:
		case BAN_BXR_A:
			return operator == COperator.bit_and_assign;
		case BOR_BAN_A:
		case BOR_BXR_A:
			return operator == COperator.bit_or_assign;
		case BXR_BAN_A:
		case BXR_BOR_A:
			return operator == COperator.bit_xor_assign;
		default:
			return false;
		}
	}

}
