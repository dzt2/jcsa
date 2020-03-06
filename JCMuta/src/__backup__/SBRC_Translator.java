package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>break; ==> continue;</code>
 * @author yukimula
 *
 */
public class SBRC_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SBRC_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		buffer.append("continue;");
	}

}
