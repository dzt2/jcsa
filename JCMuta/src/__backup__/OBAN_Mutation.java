package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBAN_Mutation extends SingleMutation {

	protected OBAN_Mutation(MutationMode mode, AstBitwiseBinaryExpression location) throws Exception {
		super(MutOperator.OBAN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseBinaryExpression expr = (AstBitwiseBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case BAN_ADD: case BAN_SUB: case BAN_MUL: case BAN_DIV: case BAN_MOD:
			return operator == COperator.bit_and;
		case BOR_ADD: case BOR_SUB: case BOR_MUL: case BOR_DIV: case BOR_MOD:
			return operator == COperator.bit_or;
		case BXR_ADD: case BXR_SUB: case BXR_MUL: case BXR_DIV: case BXR_MOD:
			return operator == COperator.bit_xor;
		default: return false;
		}
	}

}
