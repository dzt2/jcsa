package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

public class CRCR_Translator extends MutTranslator {

	@Override
	protected boolean validate(Mutation mutation) {
		return mutation instanceof CRCR_Mutation;
	}
	@Override
	protected AstNode derive(Mutation mutation) throws Exception {
		return mutation.get_location();
	}
	@Override
	protected void mutate(Mutation mutation) throws Exception {
		AstNode origin = mutation.get_location();
		String constant = origin.get_location().read();
		
		switch(mutation.get_mode()) {
		case CST_TOT_ZRO:	buffer.append("(0)"); 	break;
		case CST_POS_ONE:	buffer.append("(1)"); 	break;
		case CST_NEG_ONE:	buffer.append("(-1)"); 	break;
		case CST_NEG_CST:	buffer.append("(-").append(constant).append(")");	break;
		case CST_INC_ONE:	buffer.append("( ").append(constant).append(" + 1 )");	break;
		case CST_DEC_ONE:	buffer.append("( ").append(constant).append(" - 1 )");	break;
		default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
		}
	}

}
