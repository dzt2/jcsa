package com.jcsa.jcmutest.mutant.sel2mutant.apis;

import java.util.List;

import com.jcsa.jcmutest.mutant.sel2mutant.lang.desc.SelDescription;

/**
 * It is used to extend the source description in SegNode
 * so to generate its extension set in a sound way.
 * 
 * @author yukimula
 *
 */
public interface SegLocalExtender {
	
	/**
	 * extend the set of descriptions from the source and preserve
	 * them in the output list extension_set
	 * @param source
	 * @param extension_set
	 * @throws Exception
	 */
	public void local_extend(SelDescription source_description,
			List<SelDescription> extension_set) throws Exception;
	
}
