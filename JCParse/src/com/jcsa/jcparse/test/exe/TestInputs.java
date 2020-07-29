package com.jcsa.jcparse.test.exe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The input space for testing the program.
 * 
 * @author yukimula
 *
 */
public class TestInputs {
	
	/* constructor & attribute */
	/** the set of test inputs in **/
	private List<TestInput> inputs;
	/**
	 * create the test inputs space as empty
	 */
	protected TestInputs() {
		this.inputs = new ArrayList<TestInput>();
	}
	
	/* getters */
	/** 
	 * @return the number of test inputs in the space
	 */
	public int number_of_inputs() { return this.inputs.size(); }
	/**
	 * @return the test inputs created in this space.
	 */
	public Iterable<TestInput> get_inputs() { return inputs; }
	
	/* read & write */
	/**
	 * @param input_file the file to which the test inputs are written
	 * @throws Exception 
	 */
	public void save(File input_file) throws Exception {
		FileWriter writer = new FileWriter(input_file);
		for(TestInput input : this.inputs) {
			String parameter = input.get_parameter();
			writer.write(parameter.strip() + "\n");
		}
		writer.close();
	}
	/**
	 * @param input_file the file from which test inputs are loaded
	 * @throws Exception
	 */
	private void load(File input_file) throws Exception {
		String parameter; this.inputs.clear();
		Set<String> parameter_set = new HashSet<String>();
		
		BufferedReader reader = new 
				BufferedReader(new FileReader(input_file));
		while((parameter = reader.readLine()) != null) {
			parameter = parameter.strip();
			if(!parameter_set.contains(parameter)) {
				parameter_set.add(parameter);
				this.inputs.add(new TestInput(this, 
						this.inputs.size(), parameter));
			}
		}
		reader.close();
	}
	/**
	 * @param input_files the files from which test inputs are loaded
	 * @throws Exception
	 */
	private void load(Iterable<File> input_files) throws Exception {
		String parameter; this.inputs.clear();
		Set<String> parameter_set = new HashSet<String>();
		
		for(File input_file : input_files) {
			BufferedReader reader = new 
					BufferedReader(new FileReader(input_file));
			while((parameter = reader.readLine()) != null) {
				parameter = parameter.strip();
				if(!parameter_set.contains(parameter)) {
					parameter_set.add(parameter);
					this.inputs.add(new TestInput(this, 
							this.inputs.size(), parameter));
				}
			}
			reader.close();
		}
	}
	
	/* parameter */
	/**
	 * @param input_file the file from which test inputs are loaded
	 * @return test inputs space
	 * @throws Exception
	 */
	public static TestInputs inputs(File input_file) throws Exception {
		TestInputs inputs = new TestInputs();
		inputs.load(input_file); return inputs;
	}
	/**
	 * @param input_files the files from which test inputs are loaded
	 * @return test inputs space
	 * @throws Exception
	 */
	public static TestInputs inputs(Iterable<File> input_files) throws Exception {
		TestInputs inputs = new TestInputs();
		inputs.load(input_files); return inputs;
	}
	
}
