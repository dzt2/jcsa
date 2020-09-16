package com.jcsa.jcmutest.mutant.sec2mutant.util.apis.impl;

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

public abstract class SecExpressionPropagator extends SecErrorPropagator {

	@Override
	protected void propagate_add_statement(SecAddStatementError error) throws Exception {}

	@Override
	protected void propagate_del_statement(SecDelStatementError error) throws Exception {}

	@Override
	protected void propagate_set_statement(SecSetStatementError error) throws Exception {}

	@Override
	protected void propagate_trap_error(SecTrapError error) throws Exception {}

	@Override
	protected void propagate_none_error(SecNoneError error) throws Exception {}
	
	@Override
	protected void propagate_set_reference(SecSetReferenceError error) throws Exception {}

	@Override
	protected void propagate_add_reference(SecAddReferenceError error) throws Exception {}

	@Override
	protected void propagate_ins_reference(SecInsReferenceError error) throws Exception {}

	@Override
	protected void propagate_uny_reference(SecUnyReferenceError error) throws Exception {}

}
