package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>continue; ==> break;</code>
 * @author yukimula
 */
public class SCRB_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof SCRB_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		buffer.append("break;");
	}

}
