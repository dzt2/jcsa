package com.jcsa.jcparse.lang.astree.unit;

import com.jcsa.jcparse.lang.astree.AstScopeNode;

/**
 * <code>trans_unit --> (external_unit)*</code>
 *
 * @author yukimula
 */
public interface AstTranslationUnit extends AstScopeNode {
	public int number_of_units();

	public AstExternalUnit get_unit(int k);

	public void append_unit(AstExternalUnit unit) throws Exception;
}
