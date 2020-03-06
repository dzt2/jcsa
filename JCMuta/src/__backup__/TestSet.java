package __backup__;

import java.util.ArrayList;
import java.util.List;

/**
 * Set of test cases in the space
 * @author yukimula
 */
public class TestSet {
	
	protected TestSpace space;
	protected List<TestCase> tests;
	protected TestSet(TestSpace space) {
		this.space = space;
		tests = new ArrayList<TestCase>(); 
	}
	
	/**
	 * number of all tests
	 * @return
	 */
	public int size() { 
		return tests.size(); 
	}
	/**
	 * get all the tests in set
	 * @return
	 */
	public List<TestCase> gets() { 
		return tests; 
	}
	/**
	 * get the space where the set is defined
	 * @return
	 */
	public TestSpace get_space() {
		return space;
	}
	
}
