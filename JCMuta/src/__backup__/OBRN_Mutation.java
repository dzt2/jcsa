package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBRN_Mutation extends SingleMutation {

	protected OBRN_Mutation(MutationMode mode, AstBitwiseBinaryExpression location) throws Exception {
		super(MutOperator.OBRN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstBitwiseBinaryExpression expr = (AstBitwiseBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case BAN_GRT: case BAN_GRE: case BAN_EQV: case BAN_NEQ: case BAN_SMT: case BAN_SME:
			return operator == COperator.bit_and;
		case BOR_GRT: case BOR_GRE: case BOR_EQV: case BOR_NEQ: case BOR_SMT: case BOR_SME:
			return operator == COperator.bit_or;
		case BXR_GRT: case BXR_GRE: case BXR_EQV: case BXR_NEQ: case BXR_SMT: case BXR_SME:
			return operator == COperator.bit_xor;
		default: return false;
		}
	}

}
