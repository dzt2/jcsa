package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * <code>trap_on_times(n); stmt</code>
 * @author yukimula
 */
public class SMTC_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SMTC_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		AstNode stmt = mutation.get_location();
		
		AstStatement body;
		if(stmt instanceof AstWhileStatement) {
			body = ((AstWhileStatement) stmt).get_body();
		}
		else if(stmt instanceof AstDoWhileStatement) {
			body = ((AstDoWhileStatement) stmt).get_body();
		}
		else if(stmt instanceof AstForStatement) {
			body = ((AstForStatement) stmt).get_body();
		}
		else throw new IllegalArgumentException(
				"Invalid statement:\n\t\"" + stmt.get_location().read() + "\"");
		
		return body;
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		SMTC_Mutation mut = (SMTC_Mutation) mutation;
		AstNode stmt = mut.get_location(); AstStatement body;
		if(stmt instanceof AstWhileStatement) 
			body = ((AstWhileStatement) stmt).get_body();
		else if(stmt instanceof AstDoWhileStatement) 
			body = ((AstDoWhileStatement) stmt).get_body();
		else if(stmt instanceof AstForStatement) 
			body = ((AstForStatement) stmt).get_body();
		else throw new IllegalArgumentException(
				"Invalid statement:\n\t\"" + stmt.get_location().read() + "\"");
		
		/* mutate code */
		buffer.append("{\n\t");
		buffer.append(MutaCode.Trap_Times);
		buffer.append("( ");
		buffer.append(mut.get_loop_times());
		buffer.append(" );");
		buffer.append(body.get_location().read());
		buffer.append("}");
	}

}
