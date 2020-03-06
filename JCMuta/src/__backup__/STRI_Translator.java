package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class STRI_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof STRI_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		AstNode stmt = mutation.get_location();
		
		AstExpression expr;
		if(stmt instanceof AstIfStatement){
			expr = ((AstIfStatement) stmt).get_condition();
		}
		else if(stmt instanceof AstForStatement) {
			expr = ((AstForStatement) stmt).get_condition().get_expression();
		}
		else if(stmt instanceof AstWhileStatement) {
			expr = ((AstWhileStatement) stmt).get_condition();
		}
		else if(stmt instanceof AstDoWhileStatement) {
			expr = ((AstDoWhileStatement) stmt).get_condition();
		}
		else throw new IllegalArgumentException(
				"Invalid statement:\n\"" + stmt.get_location().read() + "\"");

		return expr;
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* get condition of the statement */
		AstNode stmt = mutation.get_location(); AstExpression expr;
		if(stmt instanceof AstIfStatement){
			expr = ((AstIfStatement) stmt).get_condition();
		}
		else if(stmt instanceof AstForStatement) {
			expr = ((AstForStatement) stmt).get_condition().get_expression();
		}
		else if(stmt instanceof AstWhileStatement) {
			expr = ((AstWhileStatement) stmt).get_condition();
		}
		else if(stmt instanceof AstDoWhileStatement) {
			expr = ((AstDoWhileStatement) stmt).get_condition();
		}
		else throw new IllegalArgumentException(
				"Invalid statement:\n\"" + stmt.get_location().read() + "\"");
		
		/* mutate code based on expression */
		switch(mutation.get_mode()) {
		case TRAP_ON_TRUE:		buffer.append(MutaCode.Trap_True);	break;
		case TRAP_ON_FALSE:		buffer.append(MutaCode.Trap_False); break;
		default: throw new IllegalArgumentException("Undefined mode: " + mutation.get_mode());
		}
		buffer.append("( ").append(expr.get_location().read()).append(" )");
	}

}
