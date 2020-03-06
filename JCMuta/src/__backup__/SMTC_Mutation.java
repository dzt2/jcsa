package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

/**
 * <code> body |==> {trap_on_times(n), body} </code><br>
 * The body must be in the loop-structure
 * @author yukimula
 *
 */
public class SMTC_Mutation extends CustomizeMutation {

	protected SMTC_Mutation(AstStatement location, int times) throws Exception {
		super(MutOperator.SMTC, MutationMode.VTRAP_FOR_TIMES, location, Integer.valueOf(times));
	}
	
	/**
	 * get the times for loop
	 * @return
	 */
	public int get_loop_times() { return (Integer) argument; }

	@Override
	protected boolean validate(MutationMode mode, AstNode location, Object argument) {
		int times = (Integer) argument;
		if(times > 0) {
			switch(mode) {
			case VTRAP_FOR_TIMES:
				if(location instanceof AstWhileStatement
					|| location instanceof AstDoWhileStatement
					|| location instanceof AstForStatement)
					return true;
				else return false;
			default: return false;
			}
		}
		else return false;
	}
}
