package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;

/**
 * <code>
 * 	switch(expr) |==> switch( trap_on_case(expr, value) )
 * </code>
 * @author yukimula
 *
 */
public class SSWM_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SSWM_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		AstSwitchStatement stmt = 
				(AstSwitchStatement) mutation.get_location();
		return stmt.get_condition();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		/* getters */
		SSWM_Mutation mut = (SSWM_Mutation) mutation;
		AstSwitchStatement stmt = 
				(AstSwitchStatement) mut.get_location();
		AstExpression origin = stmt.get_condition();
		AstExpression replace = mut.get_case_expression();
		
		/* mutate code */
		buffer.append(MutaCode.Trap_Case).append("( ");
		buffer.append(origin.get_location().read());
		buffer.append(", ");
		buffer.append(replace.get_location().read());
		buffer.append(" )");
	}

}
