package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLBN_Mutation extends SingleMutation {

	protected OLBN_Mutation(MutationMode mode, AstLogicBinaryExpression location) throws Exception {
		super(MutOperator.OLBN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstLogicBinaryExpression expr = (AstLogicBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LAN_BAN: case LAN_BOR: case LAN_BXR:
			return operator == COperator.logic_and;
		case LOR_BAN: case LOR_BOR: case LOR_BXR:
			return operator == COperator.logic_or;
		default: return false;
		}
	}

}
