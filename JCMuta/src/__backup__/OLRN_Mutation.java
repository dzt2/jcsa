package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLRN_Mutation extends SingleMutation {

	protected OLRN_Mutation(MutationMode mode, 
			AstLogicBinaryExpression location) throws Exception {
		super(MutOperator.OLRN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstLogicBinaryExpression expr = (AstLogicBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LAN_GRT: case LAN_GRE: case LAN_EQV: case LAN_NEQ: case LAN_SMT: case LAN_SME:
			return operator == COperator.logic_and;
		case LOR_GRT: case LOR_GRE: case LOR_EQV: case LOR_NEQ: case LOR_SMT: case LOR_SME:
			return operator == COperator.logic_or;
		default: return false;
		}
	}

}
