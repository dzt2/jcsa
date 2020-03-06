package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label --> {integer}
 * @author yukimula
 *
 */
public interface CirLabel extends CirNode {
	public int get_target_node_id();
	public void set_target_node_id(int id);
}
