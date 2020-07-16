package __backup__;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.base.BitSequence;

import __backup__.ExecManager.TestThread;

/**
 * To execute the mutant against tests in given project
 * @author yukimula
 */
public class JCMT_Tester {
	
	/* arguments */
	/** maximum number of mutations buffer **/
	protected static int MAX_MUTANT_BUFF = 16;
	
	/* context information */
	/** project under test **/
	protected JCMT_Project project;
	/** include library paths **/
	protected List<File> include;
	/** library items commands **/
	protected List<String> library;
	/** size of output buffer **/
	protected int buff_size;
	/** time for executing one test-mutant **/
	protected long timeout;
	/** whether to ignore the results **/
	protected boolean ignore;
	
	/* execution buffer */
	/** the space for execution as _exec_ **/
	private List<ExecManager> exec_spaces;
	/** mappings from tests to execution outputs on original program **/
	private Map<TestCase, TestOutput> outputs;
	/** mappings from tests to execution outputs on mutated programs **/
	private Map<TestCase, TestOutput> moutputs;
	/** list of the results for writing oracle **/
	private List<TestResult> result_cache;
	/** mappings from mutants to their captured mutation score sets **/
	private Map<Mutant, MutScore[]> score_cache; 
	
	/* constructor */
	/****
	 * constructor 
	 * @param project
	 * @throws Exception
	 */
	public JCMT_Tester(JCMT_Project project) throws Exception {
		if(project == null)
			throw new IllegalArgumentException("Invalid project: null");
		else {
			this.project = project;
			this.include = new ArrayList<File>();
			this.library = new ArrayList<String>();
			this.buff_size = 0; this.timeout = 0L;
			this.exec_spaces = new ArrayList<ExecManager>();
			this.outputs = new HashMap<TestCase,TestOutput>();
			this.moutputs = new HashMap<TestCase,TestOutput>();
			this.result_cache = new ArrayList<TestResult>();
			this.score_cache = new HashMap<Mutant,MutScore[]>();
		}
	}
	
	/* open-close APIs */
	/**
	 * configure the arguments of compilation and testing
	 * @param include
	 * @param library
	 * @param buff_size
	 * @param timeout
	 * @throws Exception
	 */
	public void open(List<File> include, List<String> library, 
			int buff_size, long timeout, boolean ignore) throws Exception {
		if(include == null)
			throw new IllegalArgumentException("Invalid include: null");
		else if(library == null)
			throw new IllegalArgumentException("Invalid library: null");
		else {
			this.close();
			this.include.addAll(include); 
			this.library.addAll(library);
			this.buff_size = (buff_size < 0) ? 0 : buff_size;
			this.timeout = (timeout < 0) ? 0L : timeout;
			this.ignore = ignore;
		}
	}
	/**
	 * Close the tester machine
	 */
	public void close() {
		this.include.clear();
		this.library.clear();
		this.buff_size = 0;
		this.timeout = 0L;
		this.exec_spaces.clear();
		this.outputs.clear();
		this.moutputs.clear();
		this.result_cache.clear();
		this.score_cache.clear();
	}
	
	/* install method */
	/**
	 * Get N execution spaces from the project. This will clear all the original
	 * items in the old execution spaces and reset the execution list in project.
	 * This will update the execution spaces in exec_spaces.
	 * @param n
	 * @throws Exception
	 */
	private void get_execution_space(int n) throws Exception {
		if(n <= 0)
			throw new IllegalArgumentException("Invalid number: " + n);
		else {
			/* reset the cache size */
			project.get_code_manager().reset_cache(n);
			project.get_test_manager().reset_cache(n);
			
			/* declarations */
			CodeManager code = project.get_code_manager();
			TestManager test = project.get_test_manager();
			this.exec_spaces.clear();
			File origin = code.get_resource().get_code().get_root();
			
			/* initialize the execution spaces */
			for(int k = 0; k < n; k++) {
				/* add the next execution managers */
				File cdir = code.get_resource().get_compile_list().get_cache(k);
				ExecManager exec = test.get_exec_manager(k, cdir); 
				exec_spaces.add(exec);
				/* initialize its code files */
				FileProcess.copy(origin, cdir);
				/* configure its compiler */
				exec.set_compiler(include.iterator(), library.iterator());
			}	/* end for */
			
			/* return */	return;
		}
	}
	/**
	 * Get the testing thread from each of execution spaces.
	 * It requires that the code files have been correctly
	 * installed in the compilation directory.<br>
	 * Each thread contains an empty to receive the inputs
	 * from the outsider
	 * @return
	 * @throws Exception
	 */
	private List<TestThread> get_threads_of() throws Exception {
		if(exec_spaces.isEmpty())
			throw new IllegalArgumentException("Invalid access: no execution spaces");
		else {
			List<TestThread> threads = new ArrayList<TestThread>();
			for(ExecManager exec : exec_spaces) {
				TestThread thread = exec.get_thread(
						new ArrayList<TestCase>(), buff_size, timeout);
				if(thread.get_compile_status() == ExecStatus.ST_PROGRAM)
					threads.add(thread);	// eliminate incorrectly compiled program.
			}
			return threads;
		}
	}
	
	/* test case collector */
	/**
	 * Get the tests for R-step testing for mutant
	 * @param tests
	 * @return
	 * @throws Exception
	 */
	private int get_tests_for_coverage(Collection<TestCase> tests) throws Exception {
		if(tests == null)
			throw new IllegalArgumentException("invalid tests: null");
		else {
			Collection<TestCase> keys = outputs.keySet();
			tests.clear(); tests.addAll(keys); return tests.size();
		}
	}
	/**
	 * Get the tests for I-step or P-step testing
	 * @param tests
	 * @return
	 * @throws Exception
	 */
	private int get_tests_for_mutation(
			Collection<TestCase> tests, MutScore score) throws Exception {
		if(tests == null)
			throw new IllegalArgumentException("invalid tests: null");
		else if(score == null)
			throw new IllegalArgumentException("invalid score: null");
		else {
			TestSpace tspace = project.get_test_manager().get_test_space();
			BitSequence bits = score.get_score_set();
			int tid = 0, tnum = bits.length(); tests.clear();
			for(tid = 0; tid < tnum; tid++) {
				if(bits.get(tid)) tests.add(tspace.get(tid));
			}
			return tests.size();
		}
	}
	
	/* test the original and mutation program */
	/**
	 * Update the original outputs by execution the program p 
	 * against every test in the whole set T.
	 * @param T
	 * @return number of outputs extracted from test set
	 * @throws Exception
	 */
	private int exec_original_program(Collection<TestCase> T, TestOracleManager oracle) throws Exception {
		if(this.exec_spaces.isEmpty())
			throw new IllegalArgumentException("Invalid access: no execution spaces are defined");
		else if(T == null || T.isEmpty())
			throw new IllegalArgumentException("Invalid T: empty tests");
		else if(oracle == null)
			throw new IllegalArgumentException("Invalid oracle: null");
		else {
			/* set the compilation spaces as p */
			File code = project.get_code_manager().get_resource().get_code().get_root();
			for(ExecManager Ek : exec_spaces) {  Ek.set_program(code); }
			
			/* get the testing thread from each of spaces */
			List<TestThread> threads = this.get_threads_of();
			if(threads.isEmpty()) {
				oracle.add_record(false);
				throw new RuntimeException("No threads are avaibale!");
			}
			else oracle.add_record(true);	// record original program as correct
			
			/* put the tests to the requirement list of each space */
			int k = 0, n = threads.size(); 
			for(TestCase test : T) {
				if(test != null && test.get_space() != null) {
					threads.get(k).get_request().add(test);
					k = (k + 1) % n;
				}
			}
			
			/* execute the threads to obtain the outputs */
			for(TestThread thread : threads) thread.start();
			for(TestThread thread : threads) thread.join();
			
			/* obtain the testing results */
			this.outputs.clear();
			for(TestThread thread : threads) {
				List<TestCase> reqlist = thread.get_request();
				List<TestOutput> outputs = thread.get_outputs();
				for(int i = 0; i < reqlist.size(); i++) {
					TestCase x = reqlist.get(i);
					TestOutput y = outputs.get(i);
					if(y.get_status() == TestStatus.OBTAIN_OUTPUT)
						this.outputs.put(x, y);
				}
			}
			
			/* record the test outputs to result data file */
			this.save_results(outputs, oracle);
			
			/* return */	return outputs.size();
		}
	}
	/**
	 * Update the mutation results in score set by executing
	 * the mutant against every test in T based on existing
	 * original program outputs (which is the premise).
	 * @param mutant
	 * @param T
	 * @param result
	 * @return : number of tests to kill the mutant
	 * @throws Exception
	 */
	private int exec_mutation_program(Mutant mutant, 
			Collection<TestCase> T, TestOracleManager oracle, 
			MutScore score, CodeMutationType type) throws Exception {
		if(exec_spaces.isEmpty() || outputs.isEmpty())
			throw new IllegalArgumentException("Original program not tested!");
		else if(T == null || T.isEmpty()) return 0;
		else if(oracle == null) 
			throw new IllegalArgumentException("No oracle is specified");
		else if(score == null)
			throw new IllegalArgumentException("No score is specified.");
		else {
			/* set the mutant for every compilation space */
			AstCirFile cursor = project.get_code_manager().get_cursor();
			for(ExecManager exec : exec_spaces) {
				exec.set_mutant(mutant, cursor, type);
			}
			
			/* get the threads for each execution space */
			List<TestThread> threads = this.get_threads_of();
			
			/* determine the status of mutation compilation */
			if(threads.isEmpty()) {
				oracle.add_record(mutant, false);	return 0;
			}
			else { 
				oracle.add_record(mutant, true);
			}
			
			/* set the requirement list from T */
			int k = 0, n = threads.size();
			for(TestCase test : T) {
				threads.get(k).get_request().add(test);
				k = (k + 1) % n;
			}
			
			/* execute the threads to obtain the outputs */
			for(TestThread thread : threads) thread.start();
			for(TestThread thread : threads) thread.join();
			
			/* collect outputs and save the results */
			this.moutputs.clear();
			for(TestThread thread : threads) {
				List<TestCase> reqlist = thread.get_request();
				List<TestOutput> outputs = thread.get_outputs();
				for(int i = 0; i < reqlist.size(); i++) {
					TestCase x = reqlist.get(i);
					TestOutput y = outputs.get(i);
					if(y != null) moutputs.put(x, y);
				}
				reqlist.clear(); outputs.clear();
			}
			threads.clear();
			this.save_results(mutant, moutputs, oracle);
			
			/* compute the score based on outputs */
			int kills = 0; 
			Collection<TestCase> tests = moutputs.keySet();
			for(TestCase test : tests) {
				TestOutput output = moutputs.get(test);
				if(this.is_killed(test, output)) {
					score.get_score_set().set(
							test.get_test_id(), BitSequence.BIT1);
					kills = kills + 1;
				}
			}
			
			/* return */	this.moutputs.clear(); return kills;
		}
	}
	
	/* results manager methods */
	/**
	 * read the score set of coverage, weak, strong mutation
	 * from data based for the following mutants (based on
	 * the MAX_MUTANT_BUFF)
	 * @param mutants
	 * @return
	 * @throws Exception
	 */
	private int open_cache(Iterator<Mutant> mutants, TestOracleManager oracle) throws Exception {
		if(oracle == null)
			throw new IllegalArgumentException("No oracle specified");
		else if(mutants == null || !mutants.hasNext()) return 0;
		else {
			/* create newly scores for the mutant */
			this.score_cache.clear();
			while(score_cache.size() < 
					MAX_MUTANT_BUFF && mutants.hasNext()) {
				Mutant mutant = mutants.next();
				MutScore[] scores = new MutScore[3];
				scores[0] = oracle.produce_score(mutant);
				scores[1] = oracle.produce_score(mutant);
				scores[2] = oracle.produce_score(mutant);
				score_cache.put(mutant, scores);
			}
			
			/* return */	return score_cache.size();
		}
	}
	/**
	 * write the score set from memory to the database file
	 * @return
	 * @throws Exception
	 */
	private int close_cache(CFileOutputDirectory directory) throws Exception {
		if(!score_cache.isEmpty()) {
			TestOracleManager oracle;
			List<MutScore> buffer = new ArrayList<MutScore>();
			Collection<Mutant> keys = score_cache.keySet();
			
			/* write coverage mutation score sets */
			buffer.clear();
			for(Mutant mutant : keys) 
				buffer.add(score_cache.get(mutant)[0]);
			oracle = directory.get_output_directory(
					CodeMutationType.coverage).get_oracle();
			oracle.save_scores(buffer.iterator());
			
			/* write weak mutation score sets */
			buffer.clear();
			for(Mutant mutant : keys) 
				buffer.add(score_cache.get(mutant)[1]);
			oracle = directory.get_output_directory(
					CodeMutationType.weakness).get_oracle();
			oracle.save_scores(buffer.iterator());
			
			/* write strong mutation score sets */
			buffer.clear();
			for(Mutant mutant : keys) 
				buffer.add(score_cache.get(mutant)[2]);
			oracle = directory.get_output_directory(
					CodeMutationType.stronger).get_oracle();
			oracle.save_scores(buffer.iterator());
		}
		score_cache.clear(); return 0;
 	}
	/**
	 * Execute the mutant against every test until all of the
	 * coverage, weak mutation and strong mutation are done.
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	private int exec_mutant(Mutant mutant, CFileOutputDirectory directory) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else if(directory == null)
			throw new IllegalArgumentException("invalid directory: null");
		else if(!score_cache.containsKey(mutant))
			throw new IllegalArgumentException("not loaded in cache!");
		else {
			/* declarations */
			List<TestCase> test_buffer = new ArrayList<TestCase>();
			MutScore[] scores = score_cache.get(mutant);
			this.report("Testing Mutant #" + mutant.get_mutant_id());
			
			/* get oracles for outputs */
			TestOracleManager coverage_oracle = directory.get_coverage().get_oracle();
			TestOracleManager weakness_oracle = directory.get_weakness().get_oracle();
			TestOracleManager stronger_oracle = directory.get_stronger().get_oracle();
			
			/* coverage testing for mutation */
			this.get_tests_for_coverage(test_buffer); int kills;
			if(!test_buffer.isEmpty()) {
				kills = exec_mutation_program(mutant, test_buffer, 
						coverage_oracle, scores[0], CodeMutationType.coverage);
				this.report("\t--> Coverage Tests Complete: \t" + kills + "/" + test_buffer.size());
				
				/* weak mutation testing */
				this.get_tests_for_mutation(test_buffer, scores[0]);
				if(!test_buffer.isEmpty()) {
					kills = exec_mutation_program(mutant, test_buffer,
							weakness_oracle, scores[1], CodeMutationType.weakness);
					this.report("\t--> Weaken Tests Complete: \t" + kills + "/" + test_buffer.size());
					
					/* strong mutation testing */
					this.get_tests_for_mutation(test_buffer, scores[1]);
					if(!test_buffer.isEmpty()) {
						kills = exec_mutation_program(mutant, test_buffer,
								stronger_oracle, scores[2], CodeMutationType.stronger);
						this.report("\t--> Strong Tests Complete: \t" + kills + "/" + test_buffer.size());
					}
				}
			}
			
			/* return */	return 0;
		}
	}
	/**
	 * Execute the mutants in score cache against 
	 * every test in coverage, weak and strong
	 * mutation testing
	 * @throws Exception
	 */
	private void exec_mutants(CFileOutputDirectory directory) throws Exception {
		if(directory == null)
			throw new IllegalArgumentException("invalid directory: null");
		else {
			Collection<Mutant> keys = score_cache.keySet();
			for(Mutant mutant : keys) exec_mutant(mutant, directory);
		}
	}
	
	/* execute for all the mutants and program under test */
	/**
	 * Execute every mutant in M against every test in T.
	 * @param N
	 * @param M
	 * @param T
	 * @throws Exception
	 */
	public void exec(int N, Collection<Mutant> M, Collection<TestCase> T) throws Exception {
		if(N <= 0)
			throw new IllegalArgumentException("invalid N: " + N);
		else if(M == null || M.isEmpty()) return;
		else if(T == null || T.isEmpty()) return;
		else if(!project.code_manager.is_cursor_openned())
			throw new IllegalArgumentException("no code is available");
		else {
			/* declarations */
			File cfile = project.get_code_manager().get_cursor().get_source_file();
			CFileOutputDirectory directory = project.get_resource().
					get_result().get_code_output_directory(cfile);
			
			/* execute the original program and records its results to the stronger-database */
			report("Inputs: " + M.size() + " mutants, " + T.size() + " tests.");
			this.get_execution_space(N); report("Apply " + N + " execution spaces...");
			this.exec_original_program(T, directory.get_stronger().get_oracle());	
			report("Collect " + outputs.size() + " outputs...");
			
			Iterator<Mutant> iter = M.iterator();
			while(iter.hasNext()) {
				this.open_cache(iter, directory.get_stronger().get_oracle());	/* get the scores for testing */
				this.exec_mutants(directory);		/* execute all the mutants in cache against tests for coverage, weak and strong mutation criteria */
				this.close_cache(directory);		/* write the score set to database */
				this.report("Write score sets to database.");
			}
		}
	}
	
	/* basic method */
	/**
	 * Save the outputs to the data files in oracle directory for original program
	 * @param outputs
	 * @param oracle
	 * @return
	 * @throws Exception
	 */
	private int save_results(
			Map<TestCase, TestOutput> outputs,
			TestOracleManager oracle) throws Exception {
		if(ignore) return 0;
		else if(outputs == null || outputs.isEmpty()) return 0;
		else if(oracle == null)
			throw new IllegalArgumentException("No oracle specified");
		else {
			this.result_cache.clear();
			Collection<TestCase> tests = outputs.keySet();
			for(TestCase test : tests) {
				TestOutput output = outputs.get(test);
				if(output != null) {
					TestResult result = oracle.get_result_of(test, output);
					this.result_cache.add(result);
				}
			}
			return oracle.save_results(this.result_cache.iterator());
		}
	}
	/**
	 * Save the outputs to the data files in oracle directory for mutated programs
	 * @param mutant
	 * @param outputs
	 * @param oracle
	 * @return
	 * @throws Exception
	 */
	private int save_results(Mutant mutant,
			Map<TestCase, TestOutput> outputs,
			TestOracleManager oracle) throws Exception {
		if(ignore) return 0;
		else if(outputs == null || outputs.isEmpty()) return 0;
		else if(oracle == null)
			throw new IllegalArgumentException("No oracle specified");
		else {
			this.result_cache.clear();
			Collection<TestCase> tests = outputs.keySet();
			for(TestCase test : tests) {
				TestOutput output = outputs.get(test);
				if(output != null) {
					TestResult result = oracle.get_result_of(test, output);
					this.result_cache.add(result);
				}
			}
			return oracle.save_results(mutant, this.result_cache.iterator());
		}
	}
	/**
	 * Whether the output of x is correct
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception
	 */
	private boolean is_killed(TestCase x, TestOutput y) throws Exception {
		if(x == null)
			throw new IllegalArgumentException("invalid x: null");
		else if(y == null)
			throw new IllegalArgumentException("invalid y: null");
		else if(y.get_status() != TestStatus.OBTAIN_OUTPUT) return true;
		else return !this.outputs.get(x).equals(y);
	}
	/**
	 * Set the information to console
	 * @param text
	 */
	private void report(String text) {
		System.out.println("\t[MSG]: " + text);
	}
	
}
