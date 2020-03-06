package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * <code>
 * 	do body while ( expr ); <br>
 * 	|==> <br>
 * 	while ( expr ) body <br>
 * </code>
 * @author yukimula
 */
public class SDWD_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SDWD_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* getters */
		AstDoWhileStatement stmt = 
				(AstDoWhileStatement) mutation.get_location();
		AstExpression expr = stmt.get_condition();
		AstStatement body = stmt.get_body();
		
		/* mutate code */
		buffer.append("while( ");
		buffer.append(expr.get_location().read());
		buffer.append(" ) ");
		buffer.append(body.get_location().read());
	}

}
