package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;

public class ORBN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof ORBN_Mutation;
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
		case GRT_BAN: case GRE_BAN: case EQV_BAN: case NEQ_BAN: case SMT_BAN: case SME_BAN: buffer.append('&'); break;
		case GRT_BOR: case GRE_BOR: case EQV_BOR: case NEQ_BOR: case SMT_BOR: case SME_BOR: buffer.append('|'); break;
		case GRT_BXR: case GRE_BXR: case EQV_BXR: case NEQ_BXR: case SMT_BXR: case SME_BXR: buffer.append('^'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
