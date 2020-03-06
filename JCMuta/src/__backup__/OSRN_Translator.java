package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;

public class OSRN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OSRN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstShiftBinaryExpression expr = 
				(AstShiftBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand(), roperand = expr.get_roperand();
		
		/* mutate code */
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case LSH_GRT: case RSH_GRT: buffer.append("> "); break;
		case LSH_GRE: case RSH_GRE: buffer.append(">="); break;
		case LSH_EQV: case RSH_EQV: buffer.append("=="); break;
		case LSH_NEQ: case RSH_NEQ: buffer.append("!="); break;
		case LSH_SMT: case RSH_SMT: buffer.append("< "); break;
		case LSH_SME: case RSH_SME: buffer.append("<="); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
