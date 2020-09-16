package com.jcsa.jcmutest.mutant.sec2mutant.util.prog;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecAddReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecInsReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecSetReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refer.SecUnyReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecAddStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecDelStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecSetStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecNoneError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.uniq.SecTrapError;
import com.jcsa.jcmutest.mutant.sec2mutant.util.apis.SecErrorPropagator;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SecArithNegPropagator extends SecErrorPropagator {

	@Override
	protected void add_statement_error(SecAddStatementError error) throws Exception {
		this.report_unsupported_operations();
	}
	
	@Override
	protected void del_statement_error(SecDelStatementError error) throws Exception {
		this.report_unsupported_operations();
	}

	@Override
	protected void set_statement_error(SecSetStatementError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void trap_error(SecTrapError error) throws Exception {
		this.report_unsupported_operations();
	}

	@Override
	protected void none_error(SecNoneError error) throws Exception {
		this.report_unsupported_operations();
	}

	@Override
	protected void set_expression_error(SecSetExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void add_expression_error(SecAddExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void ins_expression_error(SecInsExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void uny_expression_error(SecUnyExpressionError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_reference_error(SecSetReferenceError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void add_reference_error(SecAddReferenceError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void ins_reference_error(SecInsReferenceError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void uny_reference_error(SecUnyReferenceError error) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean test_target_location(CirNode location) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
