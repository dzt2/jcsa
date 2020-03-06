package com.jcsa.jcmuta.project;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.MutationUtil;
import com.jcsa.jcmuta.mutant.code2mutation.MutationCodeType;
import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * The binaries generated for mutation testing include:
 * 	1) program.exe
 * 	2) mutant.exe
 * 	3) exec_tests.k.sh
 * 	4) muta_tests.k.sh
 * @author yukimula
 *
 */
public class MutaBinaries {
	
	/* arguments */
	/** the name of the binaries directory for executing tests **/
	private static final String binary_directory_name = "binaries";
	/** the name of the executional file compiled from original code **/
	private static final String original_program_name = "program.exe";
	/** the name of the executional file compiled from mutation code **/
	private static final String mutation_program_name = "mutant.exe";
	/** the prefix of the script files name for testing original program **/
	private static final String original_testing_name = "exec_tests.";
	/** the prefix of the script files name for testing mutated programs **/
	private static final String mutation_testing_name = "muta_tests.";
	
	/* attributes */
	private MutaProject project;
	private File bin_directory;
	private File orig_program;
	private File muta_program;
	private List<File> orig_test_scripts;
	private List<File> muta_test_scripts;
	
	/* constructor */
	/**
	 * create a binary space for compilation and execution of mutation testing
	 * @param project
	 * @throws Exception
	 */
	protected MutaBinaries(MutaProject project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			File directory = this.project.project_directory;
			
			this.bin_directory = new File(
					directory.getAbsolutePath() + 
					File.separator + binary_directory_name);
			if(!bin_directory.exists()) bin_directory.mkdir();
			
			this.orig_program = new File(bin_directory.getAbsolutePath() 
					+ File.separator + original_program_name);
			this.muta_program = new File(bin_directory.getAbsolutePath()
					+ File.separator + mutation_program_name);
			
			this.orig_test_scripts = new ArrayList<File>();
			this.muta_test_scripts = new ArrayList<File>();
			this.update_test_scripts();
		}
	}
	/**
	 * update the testing scripts files for original and mutated program.
	 * @throws Exception
	 */
	private void update_test_scripts() throws Exception {
		File[] files = this.bin_directory.listFiles();
		this.orig_test_scripts.clear();
		this.muta_test_scripts.clear();
		
		if(files != null) {
			for(File file : files) {
				if(file.getName().startsWith(original_testing_name)) 
					this.orig_test_scripts.add(file);
				if(file.getName().startsWith(mutation_testing_name)) 
					this.muta_test_scripts.add(file);
			}
		}
	}
	
	/* getters */
	/**
	 * get the project where the binaries are created
	 * @return
	 */
	public MutaProject get_project() { return this.project; }
	/**
	 * get the directory of the binaries file
	 * @return
	 */
	public File get_bin_directory() { return this.bin_directory; }
	/**
	 * get the original program file being compiled and executed
	 * @return
	 */
	public File get_orig_program() { return this.orig_program; }
	/**
	 * get the mutation program file being compiled and executed
	 * @return
	 */
	public File get_muta_program() { return this.muta_program; }
	/**
	 * get the script files for running original program against test cases
	 * @return
	 */
	public Iterable<File> get_orig_test_scripts() { return this.orig_test_scripts; }
	/**
	 * get the script files for running mutation program against test cases
	 * @return
	 */
	public Iterable<File> get_muta_test_scripts() { return this.muta_test_scripts; }
	
	/* setters */
	/**
	 * generate the command to compile the original program in current project
	 * @return
	 * @throws Exception
	 */
	private String generate_orig_compile_command() throws Exception {
		List<File> source_files = this.project.source_files.get_orig_source_files();
		return project.config.compile_program_command(source_files, this.orig_program);
	}
	/**
	 * generate the command to compile the mutation program (this will generate
	 * the mutation code file at the same time!)
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	private String generate_muta_compile_command(Mutant mutant, MutationCodeType type) throws Exception {
		List<File> source_files = project.source_files.get_muta_source_files(mutant, type);
		return this.project.config.compile_mutant_command(source_files, this.muta_program);
	}
	/**
	 * generate the scripts for running original and mutation program against all the test cases
	 * @param timeout
	 * @param threads
	 * @throws Exception
	 */
	public void generate_test_scripts(int timeout, int threads) throws Exception {
		this.generate_test_scripts(new HashSet<Integer>(), timeout, threads);
	}
	/**
	 * generate the script files for testing the original and mutation program files against
	 * all the test cases in project with respect to the given timeout argument and threads.
	 * @param filter the test cases not being executed
	 * @param timeout seconds wait for program to exit
	 * @param threads number of scripts being executed
	 * @throws Exception
	 */
	private void generate_test_scripts(Set<Integer> filter, int timeout, int threads) throws Exception {
		if(timeout <= 0)
			throw new IllegalArgumentException("Invalid timeout: " + timeout);
		else if(threads <= 0)
			throw new IllegalArgumentException("Invalid threads: " + threads);
		else {
			for(File script : this.orig_test_scripts) script.delete();
			for(File script : this.muta_test_scripts) script.delete();
			this.orig_test_scripts.clear(); this.muta_test_scripts.clear();
			
			List<List<TestCase>> splitted = split_test_cases(filter, threads);
			
			for(List<TestCase> test_cases : splitted) {
				File orig_test_script = new File(bin_directory + File.separator + 
						original_testing_name + this.orig_test_scripts.size() + ".sh");
				File muta_test_script = new File(bin_directory + File.separator +
						mutation_testing_name + this.muta_test_scripts.size() + ".sh");
				
				this.generate_test_scripts(true, timeout, test_cases, orig_test_script);
				this.generate_test_scripts(false,timeout, test_cases, muta_test_script);
				
				this.orig_test_scripts.add(orig_test_script);
				this.muta_test_scripts.add(muta_test_script);
			}
		}
	}
	/**
	 * 
	 * @param filter the set of test cases that will not be executed
	 * @param threads
	 * @return
	 * @throws Exception
	 */
	private List<List<TestCase>> split_test_cases(Set<Integer> filter, int threads) throws Exception {
		List<List<TestCase>> test_cases = new ArrayList<List<TestCase>>();
		for(int k = 0; k < threads; k++) 
			test_cases.add(new ArrayList<TestCase>());
		
		MutaTestSpace test_space = this.project.test_space;
		for(int tid = 0; tid < test_space.number_of_test_cases(); tid++) {
			if(!filter.contains(tid)) {
				TestCase test_case = test_space.get_test_case(tid);
				test_cases.get(tid % threads).add(test_case);
			}
		}
		
		return test_cases;
	}
	private void generate_test_scripts(boolean normal, int timeout, 
			List<TestCase> test_cases, File script_file) throws Exception {
		FileWriter writer = new FileWriter(script_file);
		writer.write(String.format("cd %s\n", this.bin_directory.getAbsolutePath()));
		for(TestCase test_case : test_cases) {
			MutaTestSpace test_space = test_case.get_space();
			File output_file, program_file; String command;
			
			if(normal) {
				program_file = this.orig_program;
				output_file = test_space.get_orig_output_file(test_case);
			}
			else {
				program_file = this.muta_program;
				output_file = test_space.get_muta_output_file(test_case);
			}
			
			command = this.project.config.execute_testing_command(timeout, 
					program_file, test_case.get_argument(), output_file);
			
			writer.write(command); writer.write("\n");
		}
		writer.close();
	}
	
	/* executions */
	/**
	 * generate the command to execute the script file in SHELL environment.
	 * @param script_file
	 * @return
	 * @throws Exception
	 */
	private String get_execute_script_command(File script_file) throws Exception {
		return "bash " + script_file.getAbsolutePath();
	}
	/**
	 * compile and generate the original program
	 * @throws Exception
	 */
	private void compile_orig_program() throws Exception {
		if(this.orig_program.exists()) this.orig_program.delete();
		
		String command = this.generate_orig_compile_command();
		CommandProcess process = new CommandProcess(command, bin_directory);
		Thread process_thread = new Thread(process);
		process_thread.start(); process_thread.join();
		
		if(!this.orig_program.exists())
			throw new RuntimeException("Compilation fails at "  + command);
	}
	/**
	 * compile and generate the mutation program
	 * @param mutant
	 * @param type
	 * @throws Exception
	 */
	private void compile_muta_program(Mutant mutant, MutationCodeType type) throws Exception {
		if(this.muta_program.exists()) this.muta_program.delete();
		
		String command = this.generate_muta_compile_command(mutant, type);
		CommandProcess process = new CommandProcess(command, bin_directory);
		Thread process_thread = new Thread(process);
		process_thread.start(); process_thread.join();
		
		if(!this.muta_program.exists())
			throw new RuntimeException("Compilation fails at " + command);
	}
	/**
	 * delete all the old files in output and execute the shell scripts concurrently
	 * @throws Exception
	 */
	private void execute_orig_program() throws Exception {
		if(!this.orig_program.exists())
			throw new IllegalArgumentException("Original program is not compiled.");
		else {
			/** delete all the files in output directory **/
			MutationUtil.delete_files(project.test_space.get_output_directory());
			
			/** start to execute the original program against every test cases in script **/
			List<Thread> execution_threads = new ArrayList<Thread>(); Thread thread;
			for(File script_file : this.orig_test_scripts) {
				String command = this.get_execute_script_command(script_file);
				CommandProcess process = new CommandProcess(command, bin_directory);
				thread = new Thread(process); thread.start(); execution_threads.add(thread);
			}
			
			/** wait until all the threads completed **/
			for(Thread execution_thread : execution_threads) {
				try {
					execution_thread.join();
				}
				catch(Exception ex) {
					ex.printStackTrace(); continue;
				}
			}
		}
	}
	/**
	 * delete all the old files in output2 and execute the shell scripts concurrently
	 * @throws Exception
	 */
	private void execute_muta_program() throws Exception {
		if(!this.muta_program.exists())
			throw new IllegalArgumentException("The mutant program is not compiled");
		else {
			/** delete all the files in output directory **/
			MutationUtil.delete_files(project.test_space.get_output_directory());
			
			/** start to execute the original program against every test cases in script **/
			List<Thread> execution_threads = new ArrayList<Thread>();
			for(File script_file : this.muta_test_scripts) {
				CommandProcess process = new CommandProcess(script_file.getName(), bin_directory);
				Thread thread = new Thread(process); thread.start(); execution_threads.add(thread);
			}
			
			/** wait until all the threads completed **/
			for(Thread execution_thread : execution_threads) {
				try {
					execution_thread.join();
				}
				catch(Exception ex) {
					ex.printStackTrace(); continue;
				}
			}
		}
	}
	
	/* executors */
	/**
	 * compile and execute the original program against all the test cases
	 * @throws Exception
	 */
	public void execute_orig(int timeout, int threads) throws Exception {
		this.generate_test_scripts(new HashSet<Integer>(), timeout, threads);
		this.compile_orig_program();
		this.execute_orig_program();
	}
	private MutaTestResult execute_muta(Mutant mutant, MutationCodeType type) throws Exception {
		File output = this.project.test_space.get_output_directory();
		File output2= this.project.test_space.get_output2_directory();
		
		try {
			this.compile_muta_program(mutant, type);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return new MutaTestResult(mutant, false, type, output, output2);
		}
		
		this.execute_muta_program();
		return new MutaTestResult(mutant, true, type, output, output2);
	}
	/**
	 * execute the mutation test on the mutant with specified time-out seconds and 
	 * using concurrent testing approach from coverage, weakness until stronger.
	 * @param mutant
	 * @param timeout
	 * @param threads
	 * @throws Exception
	 */
	public void execute_muta(Mutant mutant, int timeout, int threads) throws Exception {
		Set<Integer> filter = new HashSet<Integer>(); MutaTestResult result;
		File result_file = this.project.results.get_result_file(mutant);
		StringBuilder buffer = new StringBuilder();
		
		this.generate_test_scripts(filter, timeout, threads);
		result = this.execute_muta(mutant, MutationCodeType.Coverage);
		if(result.is_compile_passed()) append_filter(result, filter);
		buffer.append(result.toString()).append("\n");
		
		this.generate_test_scripts(filter, timeout, threads);
		result = this.execute_muta(mutant, MutationCodeType.Weakness);
		if(result.is_compile_passed()) append_filter(result, filter);
		buffer.append(result.toString()).append("\n");
		
		this.generate_test_scripts(filter, timeout, threads);
		result = this.execute_muta(mutant, MutationCodeType.Stronger);
		if(result.is_compile_passed()) append_filter(result, filter);
		buffer.append(result.toString()).append("\n");
		
		FileWriter writer = new FileWriter(result_file);
		writer.write(buffer.toString()); writer.close();
	}
	private void append_filter(MutaTestResult result, Set<Integer> filter) throws Exception {
		BitSequence bits = result.get_test_result();
		for(int k = 0; k < bits.length(); k++) {
			if(bits.get(k)) filter.add(k);
		}
	}
	
}
