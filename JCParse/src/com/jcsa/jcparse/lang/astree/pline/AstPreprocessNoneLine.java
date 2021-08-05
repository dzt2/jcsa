package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;

/**
 * <code><b>#</b> \n</code>
 *
 * @author yukimula
 *
 */
public interface AstPreprocessNoneLine extends AstExternalUnit {
	public AstPunctuator get_hash();
}
