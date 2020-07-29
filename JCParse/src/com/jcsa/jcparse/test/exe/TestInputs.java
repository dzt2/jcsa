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
	public TestInputs() {
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
	 * clear all the test inputs in the space
	 */
	public void clear() { this.inputs.clear(); }
	/**
	 * @param suite_file the file to which the test inputs are written
	 * @throws Exception 
	 */
	public void save(File suite_file) throws Exception {
		FileWriter writer = new FileWriter(suite_file);
		for(TestInput input : this.inputs) {
			String parameter = input.get_parameter();
			writer.write(parameter.strip() + "\n");
		}
		writer.close();
	}
	/**
	 * @param suite_file the file in which the test inputs are saved
	 * @throws Exception
	 */
	public void load(File suite_file) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(suite_file));
		Set<String> parameters = new HashSet<String>(); 
		String line; this.inputs.clear(); TestInput input;
		while((line = reader.readLine()) != null) {
			line = line.strip();
			if(!parameters.contains(line)) {
				parameters.add(line);
				input = new TestInput(this, this.inputs.size(), line);
				this.inputs.add(input);
			}
		}
		reader.close();
	}
	/**
	 * @param suite_file the test suite file where test inputs are appended
	 * @throws Exception
	 */
	public void append(File suite_file) throws Exception {
		Set<String> parameters = new HashSet<String>();
		for(TestInput input : this.inputs) {
			parameters.add(input.get_parameter());
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(suite_file));
		String line; TestInput input;
		while((line = reader.readLine()) != null) {
			line = line.strip();
			if(!parameters.contains(line)) {
				parameters.add(line);
				input = new TestInput(this, this.inputs.size(), line);
				this.inputs.add(input);
			}
		}
		reader.close();
	}
	/**
	 * @param suite_files the test suite files where test inputs are provided
	 * @throws Exception
	 */
	public void append(Iterable<File> suite_files) throws Exception {
		Set<String> parameters = new HashSet<String>();
		for(TestInput input : this.inputs) {
			parameters.add(input.get_parameter());
		}
		
		for(File suite_file : suite_files) {
			BufferedReader reader = new BufferedReader(new FileReader(suite_file));
			String line; TestInput input;
			while((line = reader.readLine()) != null) {
				line = line.strip();
				if(!parameters.contains(line)) {
					parameters.add(line);
					input = new TestInput(this, this.inputs.size(), line);
					this.inputs.add(input);
				}
			}
			reader.close();
		}
	}
	
}
