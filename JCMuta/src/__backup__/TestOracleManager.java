package __backup__;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The manager provides access to data in output directory as
 * project/results/xxx.c/[mutation_type]/
 * @author yukimula
 */
public class TestOracleManager {
	
	/** maximal number of items in a output.x.db file **/
	public static final int MAX_OUTPUT_ITEMS = 72 * 1024 * 1024 / 96;
	
	/* property and constructor */
	private OutputDirectory directory;
	/** map from mutant to its corresponding result file key **/
	private Map<Integer, CompileRecord> records;
	/* DB interface for test results */
	private TestResultDBInterface result_dbi;
	/** cursor to the final test result data file **/
	private int result_findex, result_fsize;
	/** Mutation score DB interface **/
	private MutScoreDBInterface score_dbi;
	/** Mutation difference DB interface **/
	private MutDifferenceDBInterface diff_dbi;
	/** test space for obtain the number of tests **/
	private TestSpace test_space;
	/** to compare the mutants' results **/
	private MutComparator comparator;
	
	/**
	 * Manager for test oracle
	 * @param directory
	 * @throws Exception
	 */
	protected TestOracleManager(OutputDirectory directory) throws Exception {
		if(directory == null)
			throw new IllegalArgumentException("invalid directory: null");
		else {
			/* getters */
			this.directory = directory; 
			this.result_dbi = new TestResultDBInterface(); 
			this.score_dbi = new MutScoreDBInterface();
			this.diff_dbi = new MutDifferenceDBInterface();
			test_space = directory.get_parent().get_parent().
					get_project().get_test_manager().get_test_space();
			this.records = new HashMap<Integer, CompileRecord>();
			this.comparator = new MutComparator(this);
			
			/* update the data from data files */
			this.load_records(); 
			this.update_result_file_cursor();
			this.save_records(); 
		}
	}
	
	/* getters */
	/**
	 * get the output directory
	 * @return
	 */
	public OutputDirectory get_directory() { return directory; }
	
	/* compile log access */
	/**
	 * whether there is record to this mutant
	 * @param mutant
	 * @return
	 */
	public boolean has_record_of(Mutant mutant) {
		if(mutant == null) return false;
		else return records.containsKey(mutant.get_mutant_id());
	}
	/**
	 * get the compile record for the specified mutant
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public CompileRecord get_record(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else if(records.containsKey(mutant.get_mutant_id())) 
			return records.get(mutant.get_mutant_id());
		else throw new IllegalArgumentException("undefined: " + mutant.get_mutant_id());
	}
	/**
	 * Read the cursor for reading the compile.txt  
	 * @throws Exception
	 */
	private void load_records() throws Exception {
		File log = directory.get_compile_log();
		if(log.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(log));
			String line; CompileRecord record = null;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					record = CompileRecord.parse(line);
					records.put(record.mutant, record);
				}
			}
			reader.close();
		}
	}
	/**
	 * Save the cursor for each compiled mutants to compile.txt
	 * @throws Exception
	 */
	public void save_records() throws Exception {
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(directory.get_compile_log()));
		Collection<CompileRecord> records = this.records.values();
		for(CompileRecord record : records) {
			if(record != null) {
				writer.write(record.toString() + "\n");
			}
		}
		writer.close();
	}
	/**
	 * record the compilation result for original program.
	 * @param compiled
	 * @throws Exception
	 */
	public void add_record(boolean compiled) throws Exception {
		this.add_record_for(TestResult.PROGRAM_ID, compiled);
	}
	/**
	 * record the mutant as correctly (or incorrectly) compiled in the file
	 * @param mutant 
	 * @param compiled
	 * @throws Exception
	 */
	public void add_record(Mutant mutant, boolean compiled) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else this.add_record_for(mutant.get_mutant_id(), compiled);
	}
	/**
	 * Add a new record to the records set
	 * @param id : program or mutant ID
	 * @param compiled : whether it is correctly compiled
	 * @throws Exception
	 */
	private void add_record_for(int id, boolean compiled) throws Exception {
		/* update the cache */
		CompileRecord record = new CompileRecord(
				id, compiled, get_result_file_index());
		records.put(id, record);
		
		/* update the compile.txt file */
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(
								directory.get_compile_log(), true)));
		writer.write(record.toString() + "\n"); writer.close();
	}
	
	/* test result access */
	/**
	 * Update the index to the test result file and number of items in it.
	 * @throws Exception
	 */
	private void update_result_file_cursor() throws Exception {
		this.result_findex = this.directory.number_of_result_files() - 1;
		File result_file = this.directory.get_output_file(result_findex);
		
		this.result_dbi.open(result_file);
		this.result_fsize = this.result_dbi.size();
		this.result_dbi.close();
		
		if(this.result_fsize >= MAX_OUTPUT_ITEMS) {
			this.result_findex++; this.result_fsize = 0;
		}
	}
	/**
	 * get the index to the final test result file
	 * @throws Exception
	 */
	private int get_result_file_index() throws Exception {
		if(this.result_fsize >= MAX_OUTPUT_ITEMS) 
			this.update_result_file_cursor();
		return this.result_findex;
	}
	/**
	 * Obtain the test results of original program
	 * @param results : to store the captured results.
	 * @return : number of captured test results
	 * @throws Exception
	 */
	public int load_results(Collection<TestResult> results) throws Exception {
		if(results == null)
			throw new IllegalArgumentException("invalid results: null");
		else return this.load_results_of(TestResult.PROGRAM_ID, results);
	}
	/**
	 * Obtain the test results of mutant 
	 * @param mutant : specified mutant
	 * @param results : to store the captured results
	 * @return : number of captured test results
	 * @throws Exception
	 */
	public int load_results(Mutant mutant, Collection<TestResult> results) throws Exception {
		if(results == null)
			throw new IllegalArgumentException("invalid results: null");
		else if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else return this.load_results_of(mutant.get_mutant_id(), results);
	}
	/**
	 * Get the test results from specified program (with ID)
	 * @param id : TestResult.PROGRAM_ID for original program
	 * @param results
	 * @return
	 * @throws Exception
	 */
	private int load_results_of(int id, Collection<TestResult> results) throws Exception {
		if(!records.containsKey(id))
			throw new IllegalArgumentException("no test results generated for P#" + id);
		else {
			/* compilation validate */
			CompileRecord record = records.get(id);
			if(record.get_tag()) {
				int origin_size = results.size();
				File db = this.directory.get_output_file(record.cursor);
				this.result_dbi.open(db);
				this.result_dbi.read_mutant(id, results);
				this.result_dbi.close();
				return results.size() - origin_size;
			}
			else return 0;
		}
	}
	/**
	 * save the results of original program to the current result file
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int save_results(Iterator<TestResult> results) throws Exception {
		if(results == null) 
			throw new IllegalArgumentException("no results to be stored");
		else return this.save_results_of(TestResult.PROGRAM_ID, results);
	}
	/**
	 * save the results of mutant to the current result file
	 * @param mutant
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int save_results(Mutant mutant, Iterator<TestResult> results) throws Exception {
		if(results == null) 
			throw new IllegalArgumentException("no results to be stored");
		else if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else return this.save_results_of(mutant.get_mutant_id(), results);
	}
	/**
	 * Save the test results for specified program (with ID).
	 * The program must have been compiled and recorded before! 
	 * @param id : program to be recorded with its results
	 * @param results : set of its test results
	 * @return : number of results written in the file
	 * @throws Exception
	 */
	private int save_results_of(int id, Iterator<TestResult> results) throws Exception {
		if(!records.containsKey(id))
			throw new IllegalArgumentException("P#" + id + " has not been compiled!");
		else {
			CompileRecord record = records.get(id);
			if(record.get_tag()) {
				File db = this.directory.get_output_file(record.get_cursor());
				this.result_dbi.open(db);
				int number = this.result_dbi.write(results);
				this.result_dbi.close( );
				this.result_fsize += number; return number;
			}
			else {
				throw new RuntimeException("Incorrect program cannot produce results.");
			}
		}
	}
	/**
	 * translate the output to the vector-encoded result for original program
	 * @param test
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public TestResult get_result_of(TestCase test, TestOutput output) throws Exception {
		return new TestResult(null, test, output);
	}
	/**
	 * translate the output to the vector-encoded result for given mutant.
	 * @param mutant 
	 * @param test
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public TestResult get_result_of(
			Mutant mutant, TestCase test,
			TestOutput output) throws Exception {
		return new TestResult(mutant, test, output);
	}
	
	/* mutation score access */
	/**
	 * Get the new (empty) scores for given mutant
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public MutScore produce_score(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		// else return this.get_empty_score_of(mutant.get_mutant_id());
		else return new MutScore(mutant.get_mutant_id(), test_space.size());
	}
	/**
	 * Write the scores to the data file score.db
	 * @param scores
	 * @return
	 * @throws Exception
	 */
	public int save_scores(Iterator<MutScore> scores) throws Exception {
		this.score_dbi.open(this.directory.get_score_file());
		int number = this.score_dbi.write_scores(scores);
		this.score_dbi.close(); return number;
	}
	/**
	 * Load all the scores to the collection (existing objects and no produced)
	 * @param scores
	 * @return
	 * @throws Exception
	 */
	public int load_scores(Collection<MutScore> scores) throws Exception {
		this.score_dbi.open(this.directory.get_score_file());
		int number = this.score_dbi.read_scores(scores);
		this.score_dbi.close(); return number;
	}
	
	/* program-comparator */
	/**
	 * generate the differences between mutants and programs
	 * @param mutants
	 * @return
	 * @throws Exception
	 */
	protected boolean generate_differences(Collection<Mutant> mutants) throws Exception {
		if(mutants == null || mutants.isEmpty()) return false;
		else {
			/* declarations */
			File dbfile = directory.get_difference_file();
			MutDifference difference; int n = mutants.size();
			Collection<MutDifference> buffer = new ArrayList<MutDifference>();
			
			/* clear the original file and the data interface */
			FileProcess.remove(dbfile);		/* remove the original data */
			this.diff_dbi.open(dbfile, JCMConfig.JCM_DB_TEMPLATE);
			
			List<Mutant> mutants_list = new ArrayList<Mutant>();
			mutants_list.addAll(mutants);
			for(int i = 0; i < n; i++) {
				Mutant source = mutants_list.get(i);
				difference = comparator.compare(source);
				buffer.clear(); buffer.add(difference);
				
				for(int j = i + 1; j < n; j++) {
					Mutant target = mutants_list.get(j);
					difference = comparator.compare(source, target);
					buffer.add(difference);
				}
				
				/* write the differences to output */
				this.diff_dbi.write_differences(buffer.iterator());
				
			}
			
			this.diff_dbi.close();
			return true;
		}
	}
	/**
	 * load the differences related with the mutant (might be source or target)
	 * @param mutant
	 * @param differences
	 * @return
	 * @throws Exception
	 */
	protected int load_differences(Mutant mutant, Collection<MutDifference> differences) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("invalid mutant: null");
		else if(differences == null)
			throw new IllegalArgumentException("no outputs specified");
		else {
			int counter;
			this.diff_dbi.open(this.directory.get_difference_file());
			counter = this.diff_dbi.read_differences(mutant.get_mutant_id(), differences);
			this.diff_dbi.close(); return counter;
		}
	}
	/**
	 * compare the mutant with the original program (generate not load from datafile)
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public MutDifference compare(Mutant mutant) throws Exception {
		return this.comparator.compare(mutant);
	}
	/**
	 * compare the source with the target (generate not load from datafile)
	 * @param mutant
	 * @return
	 * @throws Exception
	 */
	public MutDifference compare(Mutant source, Mutant target) throws Exception {
		return comparator.compare(source, target);
	}
	
}
