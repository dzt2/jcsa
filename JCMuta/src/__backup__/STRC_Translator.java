package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class STRC_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof STRC_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		AstExpression expr = (AstExpression) mutation.get_location();
		return expr;
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* get condition of the statement */
		AstNode expr = mutation.get_location();
		
		/* mutate code based on expression */
		switch(mutation.get_mode()) {
		case TRAP_ON_TRUE:		buffer.append(MutaCode.Trap_True);	break;
		case TRAP_ON_FALSE:		buffer.append(MutaCode.Trap_False); break;
		default: throw new IllegalArgumentException("Undefined mode: " + mutation.get_mode());
		}
		buffer.append("( ").append(expr.get_location().read()).append(" )");
	}

}
