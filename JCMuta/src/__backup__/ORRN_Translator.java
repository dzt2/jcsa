package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;

public class ORRN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof ORRN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstRelationExpression expr = 
				(AstRelationExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand();
		AstExpression roperand = expr.get_roperand();
		
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case GRE_GRT: case EQV_GRT: case NEQ_GRT: case SMT_GRT: case SME_GRT: buffer.append("> "); break;
		case GRT_GRE: case EQV_GRE: case NEQ_GRE: case SMT_GRE: case SME_GRE: buffer.append(">="); break;
		case GRT_EQV: case GRE_EQV: case NEQ_EQV: case SMT_EQV: case SME_EQV: buffer.append("=="); break;
		case GRT_NEQ: case GRE_NEQ: case EQV_NEQ: case SMT_NEQ: case SME_NEQ: buffer.append("!="); break;
		case GRT_SMT: case GRE_SMT: case EQV_SMT: case NEQ_SMT: case SME_SMT: buffer.append("< "); break;
		case GRT_SME: case GRE_SME: case EQV_SME: case NEQ_SME: case SMT_SME: buffer.append("<="); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
