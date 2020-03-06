package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBAA_Mutation extends SingleMutation {

	protected OBAA_Mutation( MutationMode mode, AstBitwiseAssignExpression location) throws Exception {
		super(MutOperator.OBAA, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseAssignExpression expr = (AstBitwiseAssignExpression) location;
		COperator operator = expr.get_operator().get_operator();
		switch(mode) {
		case BAN_ADD_A: 
		case BAN_SUB_A:
		case BAN_MUL_A:
		case BAN_DIV_A:
		case BAN_MOD_A:
			return operator == COperator.bit_and_assign;
		case BOR_ADD_A: 
		case BOR_SUB_A:
		case BOR_MUL_A:
		case BOR_DIV_A:
		case BOR_MOD_A:
			return operator == COperator.bit_or_assign;
		case BXR_ADD_A: 
		case BXR_SUB_A:
		case BXR_MUL_A:
		case BXR_DIV_A:
		case BXR_MOD_A:
			return operator == COperator.bit_xor_assign;
		default: 
			return false;
		}
	}

}
