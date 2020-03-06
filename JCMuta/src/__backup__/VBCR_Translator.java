package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public class VBCR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof VBCR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		switch(mutation.get_mode()) {
		case MUT_TRUE:	buffer.append(MutaCode.True_Value); 	break;
		case MUT_FALSE:	buffer.append(MutaCode.False_Value); 	break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
	}

}
