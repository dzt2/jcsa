package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;

public class ORAN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof ORAN_Mutation;
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
		case GRT_ADD: case GRE_ADD: case EQV_ADD: case NEQ_ADD: case SMT_ADD: case SME_ADD: buffer.append('+'); break;
		case GRT_SUB: case GRE_SUB: case EQV_SUB: case NEQ_SUB: case SMT_SUB: case SME_SUB: buffer.append('-'); break;
		case GRT_MUL: case GRE_MUL: case EQV_MUL: case NEQ_MUL: case SMT_MUL: case SME_MUL: buffer.append('*'); break;
		case GRT_DIV: case GRE_DIV: case EQV_DIV: case NEQ_DIV: case SMT_DIV: case SME_DIV: buffer.append('/'); break;
		case GRT_MOD: case GRE_MOD: case EQV_MOD: case NEQ_MOD: case SMT_MOD: case SME_MOD: buffer.append('%'); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
