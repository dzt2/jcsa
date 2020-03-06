package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;

public class ORSN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof ORSN_Mutation;
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
		case GRT_LSH: case GRE_LSH: case EQV_LSH: case NEQ_LSH: case SMT_LSH: case SME_LSH: buffer.append("<<"); break;
		case GRT_RSH: case GRE_RSH: case EQV_RSH: case NEQ_RSH: case SMT_RSH: case SME_RSH: buffer.append(">>"); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
