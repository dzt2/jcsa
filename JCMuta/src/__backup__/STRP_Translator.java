package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>stmt |--> trap_on_statement();</code>
 * @author yukimula
 *
 */
public class STRP_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof STRP_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		buffer.append(MutaCode.Trap_Statement);
		buffer.append("( );");
	}

}
