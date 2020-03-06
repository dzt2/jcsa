package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>stmt |==> ; </code>
 * @author yukimula
 *
 */
public class SSDL_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SSDL_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		buffer.append(" ;");
	}

}
