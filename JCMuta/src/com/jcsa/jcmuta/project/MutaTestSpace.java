package com.jcsa.jcmuta.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.MutationUtil;

public class MutaTestSpace {
	
	/** name of the test data file to preserve test cases **/
	private static final String test_data_file_name = "test.tst";
	/** name of the inputs directory for executing tests **/
	private static final String inputs_directory_name = "inputs";
	/** name of the directory to preserve normal outputs **/
	private static final String output_directory_name = "output";
	/** name of the directory to preserve mutant outputs **/
	private static final String output2_directory_name="output2";
	
	/* properties */
	private MutaProject project;
	private List<TestCase> test_cases;
	private File test_data_file;
	private File inputs_directory;
	private File output_directory;
	private File output2_directory;
	
	/* constructor */
	/**
	 * create a test space in the project for testing
	 * @param project
	 * @throws Exception
	 */
	protected MutaTestSpace(MutaProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.test_cases = new ArrayList<TestCase>();
			
			File directory = this.project.project_directory;
			
			test_data_file = new File(directory.getAbsolutePath() 
					+ File.separator + test_data_file_name);
			this.read_tests();
			
			this.inputs_directory = new File(directory.getAbsolutePath()
					+ File.separator + inputs_directory_name);
			if(!this.inputs_directory.exists()) inputs_directory.mkdir();
			
			this.output_directory = new File(directory.getAbsolutePath()
					+ File.separator + output_directory_name);
			if(!this.output_directory.exists()) output_directory.mkdir();
			
			this.output2_directory = new File(directory.getAbsolutePath()
					+ File.separator + output2_directory_name);
			if(!this.output2_directory.exists()) output2_directory.mkdir();
		}
	}
	
	/* read and load */
	/**
	 * read the test cases from the data file
	 * @throws Exception
	 */
	private void read_tests() throws Exception {
		if(this.test_data_file.exists()) {
			Set<String> test_lines = new HashSet<String>(); String line;
			BufferedReader reader = new 
					BufferedReader(new FileReader(this.test_data_file));
			
			while((line = reader.readLine()) != null) {
				line = line.strip();
				if(!test_lines.contains(line)) {
					test_lines.add(line);
					this.test_cases.add(new TestCase(this, test_cases.size(), line));
				}
			}
			
			reader.close();
		}
	}
	/**
	 * save the test cases into the data file
	 * @throws Exception
	 */
	private void save_tests() throws Exception {
		FileWriter writer = new FileWriter(this.test_data_file);
		Set<String> test_lines = new HashSet<String>();
		
		for(TestCase test_case : this.test_cases) {
			String line = test_case.get_argument().strip();
			if(!test_lines.contains(line)) {
				test_lines.add(line);
				writer.write(line);
				writer.write("\n");
			}
		}
		
		writer.close();
	}
	
	/* getters */
	/**
	 * get the project of the test space
	 * @return
	 */
	public MutaProject get_project() { return this.project; }
	/**
	 * get the test cases in the space
	 * @return
	 */
	public Iterable<TestCase> get_test_cases() { return this.test_cases; }
	/**
	 * get the number of test cases in the space
	 * @return
	 */
	public int number_of_test_cases() { return this.test_cases.size(); }
	/**
	 * get the test case with respect to the ID
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public TestCase get_test_case(int id) throws Exception { return test_cases.get(id); }
	/**
	 * get the inputs directory
	 * @return
	 */
	public File get_inputs_directory() { return this.inputs_directory; }
	/**
	 * output/ directory
	 * @return
	 */
	public File get_output_directory() { return this.output_directory; }
	/**
	 * output2/ directory
	 * @return
	 */
	public File get_output2_directory() { return this.output2_directory; }
	/**
	 * get the output file when the test case is executed against original program
	 * @param test_case
	 * @return
	 */
	public File get_orig_output_file(TestCase test_case) {
		return new File(this.output_directory.getAbsolutePath() + File.separator + test_case.get_id());
	}
	/**
	 * get the output file when the test case is executed against mutated program
	 * @param test_case
	 * @return
	 */
	public File get_muta_output_file(TestCase test_case) {
		return new File(this.output2_directory.getAbsolutePath() + File.separator + test_case.get_id());
	}
	
	/* setters */
	/**
	 * add the inputs data files to the project/inputs directory
	 * @param inputs_directory
	 * @throws Exception
	 */
	public void set_test_inputs(File inputs_directory) throws Exception {
		MutationUtil.copy_directory(inputs_directory, this.inputs_directory);
	}
	/**
	 * set the test cases by reading from all the data files as provided
	 * @param test_data_files
	 * @throws Exception
	 */
	public void set_test_cases(Iterable<File> test_data_files) throws Exception {
		Set<String> test_lines = new HashSet<String>();
		this.test_cases.clear(); String line;
		
		for(File test_data_file : test_data_files) {
			BufferedReader reader = new 
					BufferedReader(new FileReader(test_data_file));
			while((line = reader.readLine()) != null) {
				line = line.strip();
				if(!test_lines.contains(line)) {
					test_lines.add(line);
					test_cases.add(new TestCase(this, test_cases.size(), line));
				}
			}
			reader.close();
		}
		
		this.save_tests();
	}
	/**
	 * set the test cases by reading from all the data files and its original test cases.
	 * @param test_data_files
	 * @throws Exception
	 */
	public void add_test_cases(Iterable<File> test_data_files) throws Exception {
		List<File> test_files = new ArrayList<File>();
		test_files.add(this.test_data_file);
		for(File test_data_file : test_data_files) 
			test_files.add(test_data_file);
		this.set_test_cases(test_files);
	}
	
}
