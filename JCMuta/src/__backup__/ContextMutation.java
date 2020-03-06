package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;

/**
 * To represent mutation in source code by function application
 * @author yukimula
 */
public class ContextMutation extends TextMutation {
	
	protected AstFunCallExpression callee;
	protected String muta_func;

	/* constructor */
	/**
	 * construct a context-sensitive mutation
	 * @param operator
	 * @param mode
	 * @param origin
	 * @param replace
	 * @param callee
	 * @param mfunc
	 * @throws Exception
	 */
	protected ContextMutation(
			MutOperator operator, MutationMode mode, 
			AstNode origin, String replace,
			AstFunCallExpression callee, String mfunc)
			throws Exception {
		super(operator, mode, origin, replace);
		
		if(callee == null)
			throw new IllegalArgumentException("Invalid callee: null");
		else if(mfunc == null || mfunc.isEmpty())
			throw new IllegalArgumentException("Invalid mfunc: null");
		else { 
			this.callee = callee; 
			this.muta_func = mfunc; 
		}
	}

	/* getters */
	/**
	 * get the identifier of function in call point
	 * @return
	 */
	public AstFunCallExpression get_callee() { return callee; }
	/**
	 * get the name for mutated function
	 * @return
	 */
	public String get_muta_function() { return muta_func; }
	
	/* factory method */
	/**
	 * create a context-sensitive mutation
	 * @param operator
	 * @param mode
	 * @param origin
	 * @param replace
	 * @param callee
	 * @param mfunc
	 * @return
	 * @throws Exception
	 */
	public static ContextMutation produce(
			MutOperator operator, MutationMode mode, 
			AstNode origin, String replace,
			AstFunCallExpression callee, String mfunc) 
			throws Exception {
		return new ContextMutation(operator, 
				mode, origin, replace, callee, mfunc);
	}
	
}
