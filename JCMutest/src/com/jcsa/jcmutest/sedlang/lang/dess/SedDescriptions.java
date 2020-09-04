package com.jcsa.jcmutest.sedlang.lang.dess;

import com.jcsa.jcmutest.sedlang.lang.SedNode;

public abstract class SedDescriptions extends SedNode {
	
	public int number_of_descriptions() {
		return this.number_of_children();
	}
	
	public SedDescription get_description(int k) 
			throws IndexOutOfBoundsException {
		return (SedDescription) this.get_child(k);
	}
	
}
