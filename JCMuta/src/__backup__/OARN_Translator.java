package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;

/**
 * <code>{+, -, *, /, %} ==> {<, <=, ==, !=, >=, >}</code>
 * @author yukimula
 *
 */
public class OARN_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof OARN_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* getters */
		AstArithBinaryExpression expr = (AstArithBinaryExpression) mutation.get_location();
		AstExpression loperand = expr.get_loperand(), roperand = expr.get_roperand();
		
		/* mutate code */
		buffer.append("( (").append(loperand.get_location().read()).append(") ");
		switch(mutation.get_mode()) {
		case ADD_GRT: case SUB_GRT: case MUL_GRT: case DIV_GRT: case MOD_GRT: buffer.append(">");  break;
		case ADD_GRE: case SUB_GRE: case MUL_GRE: case DIV_GRE: case MOD_GRE: buffer.append(">="); break;
		case ADD_EQV: case SUB_EQV: case MUL_EQV: case DIV_EQV: case MOD_EQV: buffer.append("=="); break;
		case ADD_NEQ: case SUB_NEQ: case MUL_NEQ: case DIV_NEQ: case MOD_NEQ: buffer.append("!="); break;
		case ADD_SMT: case SUB_SMT: case MUL_SMT: case DIV_SMT: case MOD_SMT: buffer.append("<");  break;
		case ADD_SME: case SUB_SME: case MUL_SME: case DIV_SME: case MOD_SME: buffer.append("<="); break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append(" (").append(roperand.get_location().read()).append(") )");
	}

}
