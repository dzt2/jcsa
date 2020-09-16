package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.simpl;

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

public abstract class SecComputationPropagator extends SecErrorPropagator {
	
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
		this.report_unsupported_operations();
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
	protected void set_reference_error(SecSetReferenceError error) throws Exception { /* no propagation */ }

	@Override
	protected void add_reference_error(SecAddReferenceError error) throws Exception { /* no propagation */ }

	@Override
	protected void ins_reference_error(SecInsReferenceError error) throws Exception { /* no propagation */ }

	@Override
	protected void uny_reference_error(SecUnyReferenceError error) throws Exception { /* no propagation */ }
	
}
