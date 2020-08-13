package com.jcsa.jcmutest.project;

public class MuTestProjectTest {
	
	/* definition */
	private MuTestProject project;
	protected MuTestProjectTest(MuTestProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project");
		else {
			this.project = project;
		}
	}
	
	/* getters */
	/**
	 * @return the mutation test project that defines test space
	 */
	public MuTestProject get_project() { return this.project; }
	
}
