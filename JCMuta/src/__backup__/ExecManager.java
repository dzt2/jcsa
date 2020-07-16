package __backup__;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jcsa.jcparse.lang.AstCirFile;

/**
 * To manage the generation, compilation, and testing
 * of a program and mutant and test case under the 
 * given directory space of xxx/project/test/_exec_K/
 * @author yukimula
 */
public class ExecManager {
	
	/* arguments */
	public static final int DEF_BUFF_SIZE = 1024 * 1024 * 16;
	public static final int DEF_EXEC_TIME = 5 * 1000;
	public static final String DEF_COMPILER = "clang";
	
	/* basic support for generation */
	/**
	 * structural compiler for C program
	 * @author yukimula
	 */
	private static class _Compiler_ {
		
		protected static final String DEFAULT_COMPILER = "gcc";
		
		protected File exec;
		protected List<File> cfiles;
		protected List<File> ifiles;
		protected List<String> libs;
		protected _Compiler_(File exec) {
			this.exec = exec;
			cfiles = new ArrayList<File>();
			ifiles = new ArrayList<File>();
			libs = new ArrayList<String>();
		}
		
		protected void clear_cfiles() { cfiles.clear(); }
		protected void clear_ifiles() { ifiles.clear(); }
		protected void clear_libs() { this.libs.clear(); }
		
		protected void add_include_directory(File dir) throws Exception {
			if(dir == null || !dir.isDirectory() || !dir.exists())
				throw new IllegalArgumentException("Invalid directory: null");
			else { this.ifiles.add(dir); }
		}
		protected void add_library_item(String lib) throws Exception {
			if(lib == null || lib.isEmpty())
				throw new IllegalArgumentException("Invalid lib: null");
			else this.libs.add(lib);
		}
		protected void add_code_file(File cfile) throws Exception {
			if(cfile == null || !cfile.exists() || cfile.isDirectory())
				throw new IllegalArgumentException("Invalid cfile: null");
			else this.cfiles.add(cfile);
		}
		
		/**
		 * get the command of compiler
		 * @param compiler
		 * @return
		 * @throws Exception
		 */
		public String[] command(String compiler) throws Exception {
			if(cfiles.isEmpty())
				throw new IllegalArgumentException("Invalid access: compilation not ready now");
			else {
				/* set the compiler command */
				if(compiler == null) compiler = DEFAULT_COMPILER;
				
				/* declarations */
				int n = 3 + cfiles.size() + 2 * ifiles.size() + libs.size();
				int k = 0; String[] command_list = new String[n];
				
				/* gcc -o exec [cfile]+ [-I ifile]+ [lib]+ */
				command_list[k++] = compiler;
				command_list[k++] = "-o";
				command_list[k++] = exec.getAbsolutePath();
				for(File cfile : cfiles)
					command_list[k++] = cfile.getAbsolutePath();
				for(File ifile : ifiles) {
					command_list[k++] = "-I";
					command_list[k++] = ifile.getAbsolutePath();
				}
				for(String lib : libs) command_list[k++] = lib;
				
				/* return */	return command_list;
			}
		}
		
	}
	/**
	 * The thread to execute the mutation testing while assumed
	 * that the program (or mutant) is compiled at first. The
	 * thread will receive a list of test cases as requirements
	 * and then output a list of test-output for corresponding
	 * test in the list. If the test generation fails, an empty
	 * test output will be returned with specified status <code>
	 * ST_PROGRAM</code>.<br>
	 * <br>
	 * Precondition: the program or mutant has been established.
	 * The thread will initialize the executor and then compile 
	 * to generate main.exe under the execution directory. If 
	 * fails, the thread returns the compiler status as output.
	 * @author yukimula
	 */
	public static class TestThread extends Thread {
		/* constructor */
		protected ExecStatus cm_status;
		protected ExecManager executor;
		protected List<TestCase> tests;
		protected List<TestOutput> ans;
		protected long timeout; 
		protected int buff_size;
		protected TestThread(ExecManager executor, 
				List<TestCase> tests, int buff_size,
				long timeout) throws Exception {
			if(executor == null)
				throw new IllegalArgumentException("Invalid executor: null");
			/*else if(tests == null)
				throw new IllegalArgumentException("Invalid test set: null");*/
			else {
				try {
					executor.init();
					executor.compile(DEF_COMPILER);
					cm_status = executor.get_status();
					
					this.executor = executor; this.tests = tests;
					this.ans = new ArrayList<TestOutput>();
					this.buff_size = buff_size; this.timeout = timeout;
				}
				catch(Exception ex) {
					ex.printStackTrace();
					cm_status = executor.get_status();
				}
			}
		}
		
		/**
		 * get the status for program compilation at beginning
		 * @return
		 */
		public ExecStatus get_compile_status() { return cm_status; }
		/**
		 * get the list of test outputs corresponding to the
		 * input test cases list for request.
		 * @return
		 */
		public List<TestOutput> get_outputs() { return ans; }
		/**
		 * get the requirement list
		 * @return
		 */
		public List<TestCase> get_request() { return tests; }
		/**
		 * set the input to the thread for execution before testing
		 * @param tests
		 */
		public void set_request(List<TestCase> tests) { this.tests = tests; }
		
		@Override
		public void run() {
			/* no inputs are set */
			if(tests == null) return;
			
			/* iterate to execute testing */
			for(TestCase test : tests) {
				if(test != null) {
					TestOutput output = null;
					try {
						executor.init();
						executor.set_testcase(test, timeout);
						executor.gen_shell();
						output = executor.testing(buff_size, timeout);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						if(output == null) {
							output = new TestOutput(TestStatus.COMMAND_FAILS);
						}
					}
					ans.add(output);
				}
				else ans.add(null);
			}	/* end for */
			
			// System.out.println("\tThread #" + this.getId() + " terminates.");
		}
		
	}
	/**
	 * The thread to execute the mutation testing while assumed
	 * that the test case (shell) is first generated. The thread
	 * will perform testing by iterating each mutation in a test.
	 * The thread requires a list of mutations and outputs a list
	 * of test outputs for corresponding requirements. If the test
	 * shell generation fails, the status for generation will be
	 * <code>ST_TESTSET</code>.<br>
	 * <br>
	 * Precondition: the test case has been established so to 
	 * generate the test shell file. The thread will initialize 
	 * the executor and first generate test shell, and then execute
	 * the mutation program every time.
	 * @author yukimula
	 */
	public static class MutaThread extends Thread {
		
		/* constructor */
		protected ExecManager executor;
		protected AstCirFile source;
		protected List<Mutant> mutants;
		protected List<TestOutput> ans;
		protected ExecStatus gn_status;
		protected long timeout;
		protected int buff_size;
		protected CodeMutationType mtype;
		protected MutaThread(ExecManager executor, 
				List<Mutant> mutants, AstCirFile source,
				int buff_size, long timeout, CodeMutationType mtype) throws Exception {
			if(executor == null)
				throw new IllegalArgumentException("Invalid executor: null");
			else if(mutants == null)
				throw new IllegalArgumentException("Invalid mutants: null");
			else if(source == null)
				throw new IllegalArgumentException("Invalid cursor: null");
			else {
				try {
					executor.init();
					executor.gen_shell();
					gn_status = executor.get_status();
					
					this.executor = executor; this.mutants = mutants;
					ans = new ArrayList<TestOutput>(); this.source = source;
					this.buff_size = buff_size; this.timeout = timeout;
					this.mtype = mtype;
				}
				catch(Exception ex) {
					ex.printStackTrace();
					gn_status = executor.get_status();
				}
			}
		}
		
		/**
		 * get the status for test shell generation
		 * @return
		 */
		public ExecStatus get_shell_status() { return gn_status; }
		/**
		 * get the list of test outputs corresponding to the
		 * input test cases list for request.
		 * @return
		 */
		public List<TestOutput> get_outputs() { return ans; }
		/**
		 * Get the mutants under test
		 * @return
		 */
		public List<Mutant> get_require() { return mutants; }
		
		@Override
		public void run() {
			/* iterate each mutant in requirement */
			for(Mutant mutant : mutants) {
				if(mutant != null) {
					TestOutput output = null;
					try {
						executor.init();
						executor.set_mutant(mutant, source, mtype);
						executor.compile(DEF_COMPILER);
						output = executor.testing(buff_size, timeout);
					}
					catch(Exception ex) {
						// ex.printStackTrace();
						if(output == null) {
							output = new TestOutput(TestStatus.COMMAND_FAILS);
						}
					}
					ans.add(output);
				}
				else ans.add(null);
			}
		}
	}
	
	/* constructor */
	protected ExecStatus status;
	protected ExecResource resource;
	protected _Compiler_ compiler;
	protected StringBuilder buffer;
	protected MutaCodeGenerator writer;
	public ExecManager(ExecResource resource) throws Exception {
		if(resource == null)
			throw new IllegalArgumentException("Invalid resource: null");
		else { 
			this.resource = resource; buffer = new StringBuilder();
			this.compiler = new _Compiler_(this.resource.get_main());
			/* set code file for compilation */
			File[] cfiles = resource.get_cdir().listFiles();
			for(int i = 0; i < cfiles.length; i++)
				this.compiler.add_code_file(cfiles[i]);
			this.writer = new MutaCodeGenerator();
		}
	}
	
	/* configuration */
	/**
	 * set the compiler arguments
	 * @param include
	 * @param library
	 * @throws Exception
	 */
	public void set_compiler(Iterator<File> include, 
			Iterator<String> library) throws Exception {
		compiler.clear_ifiles(); compiler.clear_libs();
		if(include != null) {
			while(include.hasNext())
				compiler.add_include_directory(include.next());
		}
		if(library != null) {
			while(library.hasNext())
				compiler.add_library_item(library.next());
		}
	}
	/**
	 * reset the original program code directory in execution space for compilation
	 * @param cdir
	 * @throws Exception
	 */
	public void set_program(File cdir) throws Exception {
		if(cdir == null || !cdir.exists() || !cdir.isDirectory())
			throw new IllegalArgumentException("Invalid code directory");
		else {
			FileProcess.copy(cdir, resource.get_cdir());
			this.compiler.clear_cfiles();
			File[] cfiles = resource.get_cdir().listFiles();
			for(int i = 0; i < cfiles.length; i++)
				this.compiler.add_code_file(cfiles[i]);
		}
	}
	/**
	 * mutate the mutation code file to cdir in resource 
	 * @param mutant
	 * @param cursor
	 * @param mtype : 0 (coverage), 1 (weak mutation), 2 (strong mutation)
	 * @throws Exception
	 */
	public void set_mutant(Mutant mutant, AstCirFile cursor, CodeMutationType mtype) throws Exception {
		File cdir = this.resource.get_cdir();
		String name = FileProcess.name_of(cursor.get_source_file());
		File target = FileProcess.file_of(cdir, name);
		writer.write(mutant, cursor, target, mtype);
	}
	/**
	 * generate the exec.sh shell code in buffer
	 * @param test
	 * @throws Exception
	 */
	public void set_testcase(TestCase test, long timeout) throws Exception {
		if(test == null)
			throw new IllegalArgumentException("Invalid test: null");
		else { 
			buffer.setLength(0); buffer.append("#!/bin/bash\n");
			if(timeout > 0L) 
				buffer.append("timeout ").append(timeout/1000).append(" ");
			buffer.append("./").append(ExecResource.MAIN_NAME);
			buffer.append(" ").append(test.get_command()).append('\n');
		}
	}
	
	/* state transition */
	/**
	 * verify whether the resource is available, especially when 
	 * the operators perform on file system.
	 * @throws Exception
	 */
	private void verify_available() throws Exception {
		if(!resource.available())
			throw new RuntimeException("invalid access: not available!");
	}
	/**
	 * initialize the execution space
	 * @throws Exception
	 */
	@Deprecated
	protected void clear() throws Exception {
		this.verify_available();
		resource.clear();
		status = ExecStatus.NG_COMPILE;
	}
	/**
	 * initialize the test status
	 */
	protected void init() {
		status = ExecStatus.NG_COMPILE;
	}
	/**
	 * compile and generate the main.exe file
	 * @param compile
	 * @throws Exception
	 */
	protected void compile(String compile) throws Exception {
		this.verify_available();
		if(status != ExecStatus.NG_COMPILE && status != ExecStatus.ST_TESTSET)
			throw new IllegalArgumentException("Invalid access: " + status);
		else {
			resource.clear_main();
			
			if(compile == null) compile = DEF_COMPILER;
			
			String[] commands = compiler.command(compile);
			CmdProcess proc = new CmdProcess(commands, null, DEF_BUFF_SIZE);
			Thread thread = new Thread(proc); thread.start(); thread.join();
			
			if(resource.get_main().exists()) status = ExecStatus.ST_PROGRAM;
			else throw new RuntimeException("Compilation fails at: " + commands[3]);
		}
	}
	/**
	 * generate the exec.sh
	 * @throws Exception
	 */
	protected void gen_shell() throws Exception {
		this.verify_available();
		if(status != ExecStatus.NG_COMPILE && status != ExecStatus.ST_PROGRAM)
			throw new IllegalArgumentException("Invalid access: " + status);
		else if(buffer.length() == 0)
			throw new IllegalArgumentException("Invalid access: no test input");
		else {
			resource.clear_exec();
			
			FileProcess.write(buffer.toString(), resource.get_exec());
			
			if(resource.get_exec().exists()) status = ExecStatus.ST_TESTSET;
			else throw new RuntimeException("Shell generation fails: " + resource);
		}
	}
	/**
	 * execute testing and produce outputs.
	 * @param buff_size : negative when not to capture outputs
	 * @param timeout : negative when wait forever.
	 * @return : test output values
	 * @throws Exception
	 */
	protected TestOutput testing(int buff_size, long timeout) throws Exception {
		this.verify_available();
		if(status != ExecStatus.ST_PROGRAM && status != ExecStatus.ST_TESTSET)
			throw new IllegalArgumentException("Invalid access: " + status);
		else if(!resource.get_main().exists() || !resource.get_exec().exists())
			throw new RuntimeException("Invalid access: testint not ready!");
		else {
			/* remove the original outputs */
			resource.clear_outputs();
			
			/* execute testing */
			String[] cmds = new String[] {"bash", resource.get_exec().getAbsolutePath()};
			CmdProcess proc = new CmdProcess(cmds, resource.get_root(), buff_size);
			Thread tr = new Thread(proc); tr.start(); tr.join((long)(timeout * 1.2)); 
			if(tr.isAlive()) {
				tr.interrupt(); tr.join();	// finally end
			}
			
			/* setting the status */
			TestOutput output;
			if(proc.is_out_of_memory())
				output = new TestOutput(TestStatus.OUT_OF_MEMORY);
			else if(proc.is_out_of_time())
				output = new TestOutput(TestStatus.OUT_OF_EXTIME);
			else output = new TestOutput(TestStatus.OBTAIN_OUTPUT);
			
			/* collect outputs */
			output.set_return_code(proc.get_exit_code());
			output.set_stdout(proc.get_stdout().toString());
			output.set_stderr(proc.get_stderr().toString());
			
			/* resource.update(); 
			Iterator<File> iter = resource.get_outputs();
			while(iter.hasNext()) {
				File file = iter.next();
				if(!file.isDirectory()) {
					String key = FileProcess.name_of(file);
					String value = FileProcess.read(file);
					output.put(key, value);
				}
			} */
			
			/* return */	return output;
		}
	}
	
	/* getters */
	/**
	 * get the status of mutation testing executor
	 * @return
	 */
	protected ExecStatus get_status() { return status; }
	/**
	 * get the resource of the executor
	 * @return
	 */
	public ExecResource get_resource() { return resource; }
	
	/* executor thread */
	/**
	 * get execution thread for mutation testing based on each mutant against a list of test cases.
	 * @param tests
	 * @param buff_size
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public TestThread get_thread(List<TestCase> tests, int buff_size, long timeout) throws Exception {
		this.verify_available(); 
		return new TestThread(this, tests, buff_size, timeout);
	}
	/**
	 * get execution thread for mutation testing based on each test case against a set of mutants.
	 * @param context
	 * @param mutants
	 * @param buff_size
	 * @param timeout
	 * @param mtype : 0 -- coverage; 1 -- weak mutation; 2 -- strong mutation.
	 * @return
	 * @throws Exception
	 */
	public MutaThread get_thread(AstCirFile context, List<Mutant> mutants, int buff_size, long timeout, CodeMutationType mtype) throws Exception {
		this.verify_available(); 
		return new MutaThread(this, mutants, context, buff_size, timeout, mtype);
	}
}
