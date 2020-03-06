package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * Parse the <code>TextMutation</code> to coverage mutation. That is:
 * the mutation is killed once the seeding point is covered by tests.
 * @author yukimula
 */
public class Mutation2CoverageParser {
	
	public Mutation2CoverageParser() {}
	
	/**
	 * Parse a strong mutation to coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public TextMutation parse(TextMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			/* generate new mutated code */
			AstNode origin; String replace;
			switch(mutation.get_operator()) {
			/* trap for statement */
			case STRP:		
			case SBRC: 
			case SCRB:
			case SWDD:
			case SDWD:
			case SMTC:
			case SSDL:		
				origin = mutation.get_origin();
				replace = coverage_of_statement((AstStatement) origin);
				break;
			case VSFR:		
				origin = mutation.get_origin().get_parent();
				if(JC_Classifier.is_left_operand((AstExpression) origin))
					replace = coverage_of_lexpression((AstExpression) origin);
				else replace = coverage_of_rexpression((AstExpression) origin);
				break;
			default:		
				origin = mutation.get_origin();
				if(JC_Classifier.is_left_operand((AstExpression) origin))
					replace = coverage_of_lexpression((AstExpression) origin);
				else replace = coverage_of_rexpression((AstExpression) origin);
				break;
			}
			/* generate */ return gen_mutation(mutation, origin, replace);
		}
	}
	
	/**
	 * Generate the location and replace code text for coverage mutation
	 * of the statement mutation operators, of which seeding point is a 
	 * statement (usually).
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private String coverage_of_statement(AstStatement stmt) throws Exception {
		return MutaCode.Trap_Statement + "(); ";
	}
	/**
	 * Generate the location and mutated code for coverage mutation
	 * which is seeded in a expression as the right-value.
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	private String coverage_of_rexpression(AstExpression expr) throws Exception {
		String orig_code = expr.get_location().read();
		return "( " + MutaCode.Trap_Statement + "(), ( " + orig_code + " ) )";
	}
	/**
	 * Generate the location and mutated code for coverage mutation
	 * which is seeded in an expression as the left-value.
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	private String coverage_of_lexpression(AstExpression expr) throws Exception {
		String orig_code = expr.get_location().read();
		return " *( " + MutaCode.Trap_Statement + "(), &( " + orig_code + ") )";
	}
	/**
	 * Generate the coverage mutation according to the given
	 * seeding point and the replace code segment.
	 * @param mutation
	 * @param origin
	 * @param replace
	 * @return
	 * @throws Exception
	 */
	private TextMutation gen_mutation(TextMutation mutation, 
			AstNode origin, String replace) throws Exception {
		if(mutation instanceof ContextMutation) {
			return ContextMutation.produce(
					mutation.get_operator(), mutation.get_mode(), 
					origin, replace, 
					((ContextMutation) mutation).get_callee(), 
					((ContextMutation) mutation).get_muta_function());
		}
		else {
			return TextMutation.produce(
					mutation.get_operator(), mutation.get_mode(), 
					origin, replace);
		}
	}
	
}
