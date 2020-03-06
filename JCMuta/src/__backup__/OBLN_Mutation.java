package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBLN_Mutation extends SingleMutation {

	protected OBLN_Mutation(MutationMode mode, AstBitwiseBinaryExpression location) throws Exception {
		super(MutOperator.OBLN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseBinaryExpression expr = (AstBitwiseBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case BAN_LAN: case BAN_LOR:
			return operator == COperator.bit_and;
		case BOR_LAN: case BOR_LOR:
			return operator == COperator.bit_or;
		case BXR_LAN: case BXR_LOR:
			return operator == COperator.bit_xor;
		default: return false;
		}
	}

}
