package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLAN_Mutation extends SingleMutation {

	protected OLAN_Mutation(MutationMode mode, AstLogicBinaryExpression location) throws Exception {
		super(MutOperator.OLAN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstLogicBinaryExpression expr = (AstLogicBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LAN_ADD: case LAN_SUB: case LAN_MUL: case LAN_DIV: case LAN_MOD:
			return operator == COperator.logic_and;
		case LOR_ADD: case LOR_SUB: case LOR_MUL: case LOR_DIV: case LOR_MOD:
			return operator == COperator.logic_or;
		default: return false;
		}
	}

}
