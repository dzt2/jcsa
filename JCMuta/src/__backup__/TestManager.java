package __backup__;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

/**
 * to manage the inputs, outputs and access to
 * test in the directory xxx/project/test/
 * @author yukimula
 */
public class TestManager {
	
	/* constructor */
	protected TestResource resource;
	protected TestSpace tspace;
	public TestManager(TestResource resource) throws Exception {
		if(resource == null)
			throw new IllegalArgumentException("Invalid resource: null");
		else { 
			this.resource = resource; 
			this.tspace = new TestSpace();
			resource.get_exec_list().clear();
			this.load();
		}
	}
	
	/* getters */
	/**
	 * get the resource for xxx/project/test/
	 * @return
	 */
	public TestResource get_resource() { return resource; }
	/**
	 * get the space for test cases of program under test
	 * @return
	 */
	public TestSpace get_test_space() { return tspace; }
	
	/* DB synchronization */
	/**
	 * load the test case in data file to test space.
	 * This will clear the original test case in space
	 * @throws Exception
	 */
	private void load() throws Exception {
		TestDBInterface dbi = new TestDBInterface();
		dbi.open(resource.get_testDB());
		dbi.read(tspace); dbi.close();
	}
	/**
	 * save the test cases in space to the data file
	 * @throws Exception
	 */
	private void save() throws Exception {
		TestDBInterface dbi = new TestDBInterface();
		dbi.open(resource.get_testDB(), JCMConfig.JCM_DB_TEMPLATE);
		dbi.write(tspace.gets()); dbi.close();
	}
	
	/* input methods */
	/**
	 * reset the test cases in the test space.
	 * This will clear the original space and database file
	 * @param suites
	 * @throws Exception
	 */
	public void reset_testDB(Iterator<File> suites) throws Exception {
		tspace.clear();		/* clear original tests */
		
		if(suites != null && suites.hasNext()) {
			while(suites.hasNext()) {
				/* get the next suite file */
				File suite = suites.next();
				
				/* read the test key lines to the test space */
				if(suite.exists() && !suite.isDirectory()) {
					FileReader reader = new FileReader(suite);
					BufferedReader rd = new BufferedReader(reader);
					
					String line, tag = suite.getName();
					while((line = rd.readLine()) != null) {
						line = line.trim();
						if(!tspace.has(line)) {
							tspace.new_test_case(line, tag);
						}
					}
					rd.close();
				}
			}	// end while
		}
		
		/* synchronize */ this.save();
	}
	/**
	 * add new test cases from suite files to the test space.
	 * The new tests will be appended to the test database file.
	 * @param suites
	 * @throws Exception
	 */
	public void append_testDB(Iterator<File> suites) throws Exception {
		if(suites != null && suites.hasNext()) {
			while(suites.hasNext()) {
				/* get the next suite file */
				File suite = suites.next();
				
				/* read the test key lines to the test space */
				if(suite.exists() && !suite.isDirectory()) {
					FileReader reader = new FileReader(suite);
					BufferedReader rd = new BufferedReader(reader);
					
					String line, tag = suite.getName();
					while((line = rd.readLine()) != null) {
						line = line.trim();
						if(!tspace.has(line)) {
							tspace.new_test_case(line, tag);
						}
					}
					rd.close();
				}
			}	// end while
			
			/* synchronize */ // this.save();
		}
		/* synchronize */ this.save();
	}
	/**
	 * reset the files in xxx/project/test/inputs/.
	 * This will clear the original inputs/*
	 * @param inputs : null to clear the xxx/test/inputs/ directory
	 * @throws Exception
	 */
	public void reset_inputs(File inputs) throws Exception {
		ImageDirectory dir = resource.get_inputs(); 
		
		dir.clear();
		if(inputs != null && inputs.exists() && inputs.isDirectory()) {
			File[] files = inputs.listFiles();
			for(int i = 0; i < files.length; i++) {
				dir.put_of(files[i]);
			}
		}
	}
	/**
	 * Reset the execution cache directories. This will clear
	 * the original cache directories list.
	 * @param n
	 * @throws Exception
	 */
	public void reset_cache(int n) throws Exception {
		FileCacheList comps = resource.get_exec_list();
		comps.clear(); if(n > 0) comps.reset(n); 
	}
	
	/* factory method */
	/**
	 * get the kth cache directory's execution manager
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public ExecManager get_exec_manager(int k, File cdir) throws Exception {
		FileCacheList comps = resource.get_exec_list();
		File dir = comps.get_cache(k);
		ExecResource res = new ExecResource(dir, cdir);
		return new ExecManager(res);
	}
}
