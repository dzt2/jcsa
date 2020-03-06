package com.jcsa.jcmuta.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcparse.lang.base.BitSequence;

public class MutaTestResults {
	
	/* arguments */
	/** the name of directory to preserve the test results **/
	private static final String result_directory_name = "results";
	/** the number of test results in each directory and cache **/
	private static final int cache_size = 256;
	/** the number of directories from results to result files **/
	private static final int layer_size = 3;
	
	/* attributes */
	/** mutation test project for results to describe **/
	private MutaProject project;
	/** project/result **/
	private File result_directory;
	
	/* constructor */
	/**
	 * create mutation testing results space
	 * @param project
	 * @throws Exception
	 */
	protected MutaTestResults(MutaProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			
			this.result_directory = new File(
					project.project_directory.getAbsolutePath() + 
					File.separator + result_directory_name);
			if(!result_directory.exists()) result_directory.mkdir();
		}
	}
	
	/* getters */
	/**
	 * get the project that the results present
	 * @return
	 */
	public MutaProject get_project() { return this.project; }
	/**
	 * get the file where the test result of given mutant is preserved.
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public File get_result_file(Mutant mutant) throws Exception {
		int[] identifiers = this.get_cache_identifier(mutant.get_id());
		
		File directory = this.result_directory;
		for(int k = identifiers.length - 1; k >= 0; k--) {
			if(!directory.exists()) directory.mkdir();
			directory = new File(directory.getAbsolutePath() + 
						File.separator + identifiers[k]);
		}
		
		return directory;
	}
	private int[] get_cache_identifier(int mid) throws Exception {
		int[] identifiers = new int[layer_size];
		for(int k = 0; k < layer_size; k++) {
			int identifier = mid % cache_size;
			mid = mid / cache_size;
			identifiers[k] = identifier;
		}
		return identifiers;
	}
	/**
	 * read the test results (coverage, weakness, stronger) of the mutant
	 * or null if it has not been tested before.
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public Map<MutationCodeType, MutaTestResult> read_test_results(Mutant mutant) throws Exception {
		File result_file = this.get_result_file(mutant);
		if(!result_file.exists()) return null;
		else {
			BufferedReader reader = new BufferedReader(new FileReader(result_file));
			Map<MutationCodeType, MutaTestResult> results = new HashMap<MutationCodeType, MutaTestResult>(); 
			String line; boolean compiled = false; MutationCodeType type = null; BitSequence bits = null;
			
			while((line = reader.readLine()) != null) {
				if(!line.isBlank()) {
					int index = line.indexOf(':');
					String name = line.substring(0, index).strip();
					String value = line.substring(index + 1).strip();
					
					if(name.equals("#mutant")) {
						int id = Integer.parseInt(value);
						if(id != mutant.get_id()) {
							reader.close();
							throw new RuntimeException("Reading fails at mutant#" + mutant.get_id() + " as mutant#" + id);
						}
					}
					else if(name.equals("#type")) {
						type = MutationCodeType.valueOf(value);
					}
					else if(name.equals("#compile")) {
						compiled = Boolean.parseBoolean(value);
					}
					else if(name.equals("#output")) {
						bits = new BitSequence(value.length());
						for(int k = 0; k < value.length(); k++) {
							if(value.charAt(k) == '1') {
								bits.set(k, true);
							}
							else if(value.charAt(k) == '0') {
								bits.set(k, false);
							}
							else {
								reader.close();
								throw new IllegalArgumentException("Invalid char: " + value.charAt(k));
							}
						}
					}
				}
				else {
					results.put(type, new MutaTestResult(mutant, compiled, type, bits));
				}
			}
			
			reader.close();	
			return results;
		}
	}
	/**
	 * add the test result in the results 
	 * @param result
	 * @throws Exception
	 */
	public void add_test_result(Mutant mutant, MutaTestResult[] results) throws Exception {
		File file = this.get_result_file(mutant);
		FileWriter writer = new FileWriter(file);
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(results[0].toString()).append("\n");
		buffer.append(results[1].toString()).append("\n");
		buffer.append(results[2].toString()).append("\n");
		
		writer.write(buffer.toString()); writer.close();
	}
	
}
