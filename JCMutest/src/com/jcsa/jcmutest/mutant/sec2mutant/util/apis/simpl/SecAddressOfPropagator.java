package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;

public class SecAddressOfPropagator extends SecComputationPropagator {

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		/* no influence */
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		/* no influence */
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		/* no influence */
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		/* no influence */
	}

	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		if(location instanceof CirAddressExpression) {
			return true;
		}
		else {
			return false;
		}
	}

}
