package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBSN_Mutation extends SingleMutation {

	protected OBSN_Mutation(MutationMode mode, AstBitwiseBinaryExpression location) throws Exception {
		super(MutOperator.OBSN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseBinaryExpression expr = (AstBitwiseBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case BAN_LSH: case BAN_RSH:
			return operator == COperator.bit_and;
		case BOR_LSH: case BOR_RSH:
			return operator == COperator.bit_or;
		case BXR_LSH: case BXR_RSH:
			return operator == COperator.bit_xor;
		default: return false;
		}
	}

}
