package __backup__;

import java.io.File;

/**
 * <code>
 * 	|-- compile.txt	<br>
 * 	|-- output.0.db	<br>
 * 	|-- output.1.db	<br>
 * 	|-- ...... 		<br>
 * 	|-- output.n.db	<br>
 * 	|-- scores.db	<br>
 * 	|-- difference.db<br>
 * </code>
 * @author yukimula
 */
public class OutputDirectory {
	
	/* arguments for path access */
	protected static final String COMPILE_FILE 	= "compile.txt";
	protected static final String OUTPUT_PREFX 	= "output.";
	protected static final String SCORE_FILE	= "scores.db";
	protected static final String DIFFER_FILE	= "difference.db";
	
	/** Parent directory of "results/xxx.c/" **/
	protected CFileOutputDirectory parent;
	/** mutation type (or the name of this directory) **/
	protected CodeMutationType mutation_type;
	/** the directory **/
	protected File directory;
	/** the oracle of the directory **/
	protected TestOracleManager oracle;
	
	/**
	 * Construct an output directory for given code file and mutation type.
	 * <br>
	 * 
	 * A new directory will be created if results/xxx.c/mutation_type/ does not exist.
	 * <br>
	 * 
	 * @param parent
	 * @param type
	 * @throws Exception
	 */
	protected OutputDirectory(CFileOutputDirectory parent, CodeMutationType type) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("invalid parent: null");
		else if(type == null)
			throw new IllegalArgumentException("undefined type: null");
		else {
			this.parent = parent; this.mutation_type = type;
			this.directory = new File(parent.directory.getAbsolutePath() 
					+ File.separator + type.toString());
			this.directory = FileProcess.get_directory(directory);
			
			/* create compile.txt and scores.db */
			File compile = this.get_compile_log();
			if(!compile.exists()) 
				FileProcess.write("", compile);
			File scores = this.get_score_file();
			if(!scores.exists()) 
				FileProcess.copy(JCMConfig.JCM_DB_TEMPLATE, scores);
			File differences = this.get_difference_file();
			if(!differences.exists())
				FileProcess.copy(JCMConfig.JCM_DB_TEMPLATE, differences);
			this.get_output_file(0);
			
			/* obtan the oracle manager */
			this.oracle = new TestOracleManager(this);
		}
	}
	
	/* getters */
	/**
	 * <code>results/xxx.c/</code>
	 * @return
	 */
	public CFileOutputDirectory get_parent() { return parent; }
	/**
	 * get the name of this directory
	 * @return
	 */
	public CodeMutationType get_name() { return mutation_type; }
	/**
	 * <code>results/xxx.c/mutation_type/</code>
	 * @return
	 */
	public File get_root() { return directory; }
	/**
	 * <code>results/xxx.c/mutation_type/compile.txt</code>
	 * @return
	 */
	public File get_compile_log() {
		return this.get_file_of(COMPILE_FILE);
	}
	/**
	 * <code>results/xxx.c/mutation_type/output.k.db</code>
	 * @param k
	 * @return
	 */
	public File get_output_file(int k) throws Exception {
		if(k < 0)
			throw new IllegalArgumentException("invalid k: " + k);
		else {
			File target = this.get_file_of(OUTPUT_PREFX + k + ".db");
			if(!target.exists())
				FileProcess.copy(JCMConfig.JCM_DB_TEMPLATE, target);
			return target;
		}
	}
	/**
	 * get the number of output.x.db
	 * @return
	 */
	public int number_of_result_files() {
		File[] files = this.directory.listFiles();
		return files.length - 3;
	}
	/**
	 * <code>results/xxx.c/mutation_type/scores.db</code>
	 * @return
	 */
	public File get_score_file() {
		return this.get_file_of(SCORE_FILE);
	}
	/***
	 * <code>results/xxx.c/mutation_type/differences.db</code>
	 * @return
	 */
	public File get_difference_file() {
		return this.get_file_of(DIFFER_FILE);
	}
	/**
	 * get the file from the specified name in current directory
	 * @param name
	 * @return
	 */
	private File get_file_of(String name) {
		return new File(directory.getAbsolutePath() + File.separator + name);
	}
	/**
	 * get the oracle for the outputs directory
	 * @return
	 */
	public TestOracleManager get_oracle() { return oracle; }
	
}
