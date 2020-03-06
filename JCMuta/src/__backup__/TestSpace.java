package __backup__;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * space where test case is defined
 * @author yukimula
 */
public class TestSpace {
	
	/* constructor */
	protected List<TestCase> testlist;
	protected Map<String, Integer> index;
	public TestSpace() {
		testlist = new ArrayList<TestCase>();
		index = new HashMap<String, Integer>();
	}
	
	/* getters */
	/**
	 * number of test cases in the space
	 * @return
	 */
	public int size() { return testlist.size(); }
	/**
	 * whether there is test case to the specified id in this space
	 * @param id
	 * @return
	 */
	public boolean has(String key) { return index.containsKey(key); }
	/**
	 * get the test case of the specified id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TestCase get(String key) throws Exception {
		if(index.containsKey(key)) return testlist.get(index.get(key));
		else throw new IllegalArgumentException( "Undefined: " + key);
	}
	/**
	 * get the set of test cases in the space
	 * @return
	 */
	public Iterator<TestCase> gets() { return testlist.iterator(); }
	/**
	 * get the kth test case in space
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public TestCase get(int k) throws Exception {
		if(k < 0 || k >= testlist.size())
			throw new IllegalArgumentException("Undefined k: " + k);
		else return this.testlist.get(k);
	}
	/**
	 * Get the set of tests from given begining index and number
	 * @param beg : first test in space
	 * @param len : number of tests
	 * @return
	 * @throws Exception
	 */
	public TestSet set_of(Set<String> tags) throws Exception {
		TestSet tests = new TestSet(this);
		if(tags != null && !tags.isEmpty()) {
			int k, n = testlist.size();
			for(k = 0; k < n; k++) {
				TestCase test = testlist.get(k);
				if(test != null) {
					if(tags.contains(test.tag)) {
						tests.tests.add(test);
					}
				}
			}
			return tests;
		}
		else return tests;
	}
	
	/* setters */
	/**
	 * clear the space
	 */
	public void clear() { 
		for(TestCase testcase : testlist) 
			testcase.space = null;
		index.clear(); testlist.clear();
	}
	/**
	 * create a new test case in current space
	 * @param id
	 * @param inputs
	 * @param ouputs
	 * @return
	 * @throws Exception
	 */
	public TestCase new_test_case(String command, String tag) throws Exception {
		if(index.containsKey(command))
			throw new IllegalArgumentException("Duplicated: " + command);
		else if(command == null)
			throw new IllegalArgumentException("Invalid command: null");
		else {
			int id = testlist.size();
			TestCase test = new TestCase(this, id, command, tag);
			this.index.put(command, id); 
			this.testlist.add(test); return test;
		}
	}
	/**
	 * remove an existing test case from the space
	 * @param testcase
	 * @return
	 * @throws Exception
	 */
	public boolean del_test_case(TestCase testcase) throws Exception {
		if(testcase == null || testcase.space != this)
			throw new IllegalArgumentException("Invalid test: " + testcase);
		else if(!index.containsKey(testcase.command))
			throw new IllegalArgumentException("Internal error: " + testcase.id);
		else { 
			int k = index.get(testcase.command);
			this.index.remove(testcase.command); 
			testlist.remove(k); return true; 
		}
	}
	/**
	 * insert the test case to the specified index 
	 * (this can update the original test item with new information)
	 * @param tid
	 * @param command
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public TestCase set_test_case(int tid, String command, String tag) throws Exception {
		if(index.containsKey(command))
			throw new IllegalArgumentException("Duplicated: " + command);
		else if(command == null)
			throw new IllegalArgumentException("Invalid command: null");
		else if(tid < 0)
			throw new IllegalArgumentException("Invalid tid: " + tid);
		else {
			/* update the test-list size */
			while(testlist.size() <= tid) testlist.add(null);
			
			/* remove original item */
			if(testlist.get(tid) != null) {
				TestCase test = testlist.get(tid);
				index.remove(test.command); 
				test.space = null;
			}
			
			/* set the new test */
			TestCase test = new TestCase(this, tid, command, tag);
			testlist.set(tid, test); index.put(test.command, tid);
			
			/* return */	return test;
		}
	}
	
}
