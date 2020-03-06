package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OLSN_Mutation extends SingleMutation {

	protected OLSN_Mutation(MutationMode mode, AstLogicBinaryExpression location) throws Exception {
		super(MutOperator.OLSN, mode, location);
	}

	@Override
	protected boolean validate(MutationMode mode, AstNode location) {
		AstLogicBinaryExpression expr = (AstLogicBinaryExpression) location;
		COperator operator = expr.get_operator().get_operator();
		
		switch(mode) {
		case LAN_LSH: case LAN_RSH:
			return operator == COperator.logic_and;
		case LOR_LSH: case LOR_RSH:
			return operator == COperator.logic_or;
		default: return false;
		}
	}

}
