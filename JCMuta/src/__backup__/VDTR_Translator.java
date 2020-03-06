package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class VDTR_Translator extends MutTranslator {
	
	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VDTR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstExpression expr = 
				(AstExpression) mutation.get_location();
		switch(mutation.get_mode()) {
		case TRAP_ON_POS:	buffer.append(MutaCode.Trap_Positive); 	break;
		case TRAP_ON_ZRO:	buffer.append(MutaCode.Trap_Zero);		break;
		case TRAP_ON_NEG:	buffer.append(MutaCode.Trap_Negative);	break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
		buffer.append("( ").append(expr.get_location().read()).append(" )");
	}
	
}
