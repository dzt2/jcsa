package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBBN_Mutation extends SingleMutation {

	protected OBBN_Mutation(MutationMode mode, AstBitwiseBinaryExpression location) throws Exception {
		super(MutOperator.OBBN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseBinaryExpression expr = (AstBitwiseBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case BAN_BOR: case BAN_BXR:
			return operator == COperator.bit_and;
		case BOR_BAN: case BOR_BXR:
			return operator == COperator.bit_or;
		case BXR_BAN: case BXR_BOR:
			return operator == COperator.bit_xor;
		default: return false;
		}
	}

}
