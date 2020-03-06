package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;

public class OLRN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OLRN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstLogicBinaryExpression expr = 
				(AstLogicBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand();
		AstExpression roperand = expr.get_roperand();
		
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case LAN_GRT: case LOR_GRT: buffer.append("> "); break;
		case LAN_GRE: case LOR_GRE: buffer.append(">="); break;
		case LAN_EQV: case LOR_EQV: buffer.append("=="); break;
		case LAN_NEQ: case LOR_NEQ: buffer.append("!="); break;
		case LAN_SMT: case LOR_SMT: buffer.append("< "); break;
		case LAN_SME: case LOR_SME: buffer.append("<="); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
