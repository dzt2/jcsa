package com.jcsa.jcmutest.mutant.sec2mutant.util.prog.unay;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.util.prog.SecExpressionPropagator;

public class SecAddressOfPropagator extends SecExpressionPropagator {

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception { }

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception { }

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception { }

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception { }

}
