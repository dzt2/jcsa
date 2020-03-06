package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * To represent the mutation in source code text, usually first-order
 * @author yukimula
 *
 */
public class TextMutation {
	protected MutOperator operator;
	protected MutationMode mode;
	protected AstNode origin;
	protected String replace;
	
	/**
	 * construct a first-order mutation
	 * @param origin
	 * @param replace
	 * @throws Exception
	 */
	protected TextMutation(MutOperator operator, MutationMode mode,
			AstNode origin, String replace) throws Exception {
		if(origin == null)
			throw new IllegalArgumentException("Invalid ast-node: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(mode == null) 
			throw new IllegalArgumentException("Invalid mode: null");
		else if(replace == null)
			throw new IllegalArgumentException("Invalid replace: null");
		else {
			this.origin = origin;
			this.replace = replace; 
			this.operator = operator; 
			this.mode = mode;
		}
	}
	
	/**
	 * operator of the mutation
	 * @return
	 */
	public MutOperator get_operator() {return operator;}
	/**
	 * get the mode of the mutation
	 * @return
	 */
	public MutationMode get_mode() {return mode;}
	/**
	 * get the location where mutant is seeded
	 * @return
	 */
	public AstNode get_origin() {return origin;}
	/**
	 * get the text to replace original code segment
	 * @return
	 */
	public String get_replace() {return replace;}
	
	public static TextMutation produce(
			MutOperator operator, MutationMode mode,
			AstNode origin, String replace) throws Exception {
		return new TextMutation(operator, mode, origin, replace);
	}

}
