package test;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcparse.lang.base.BitSequence;

import __backup__.CodeMutationType;
import __backup__.DBInterface;
import __backup__.JCMT_Builder;
import __backup__.JCMT_Project;
import __backup__.MutScore;
import __backup__.Mutant;
import __backup__.MutantSpace;
import __backup__.TestOracleManager;

/**
 * Translate the old mutation score and save it
 * to the empty data base in project
 * @author yukimula
 *
 */
public class MutScoreTranslator {
	
	/* arguments */
	protected static final String prefix = "../../../MyData/CODE2/";
	protected static final String cfidir = prefix + "ifiles/";
	protected static final String eqvdir = prefix + "nequiv/";
	protected static final String prodir = prefix + "TestProjectsAll/";
	protected static final String cover_prefx = "/utility/cover/";
	protected static final String infect_prefx = "/utility/infect/";
	protected static final String strong_prefx = "/utility/strong/";
	
	/* Old-DB-Interface for MutScore */
	protected static class __MutScoreDBInterface extends DBInterface {
		/* SQL query */
		protected static final String TABLE_NAME = "scores";
		protected static final String READ_STMT_MUTANT = "select * from scoreset where mutant=?;";
		/* constructor */
		protected __MutScoreDBInterface() { super(); }
		/**
		 * read all the scores and set the information to the input scores
		 * in the collection.
		 * @param scores
		 * @return
		 * @throws Exception
		 */
		public int read_scores(Collection<MutScore> scores) throws Exception {
			if(scores == null)
				throw new IllegalArgumentException("No outputs specified");
			else {
				int count = 0; ResultSet rs;
				PreparedStatement stmt =
						connection.prepareStatement(READ_STMT_MUTANT);
				for(MutScore score : scores) {
					if(score != null) {
						stmt.setInt(1, score.get_mutant());
						rs = stmt.executeQuery(); 
						try {
							rs.next();
							this.parse(score, rs); 
							count++;
						}
						catch(Exception ex) {
							// undefined score
							ex.printStackTrace();
						}
					}
				}
				return count;
			}
		}
		private void parse(MutScore score, ResultSet rs) throws Exception {
			String txt = rs.getString("killset");
			BitSequence seq = score.get_score_set();
			
			seq.clear();
			for(int k = 0; k < txt.length(); k++) {
				char ch = txt.charAt(k);
				switch(ch) {
				case '1':	seq.set(k, BitSequence.BIT1);	break;
				case '0':	seq.set(k, BitSequence.BIT0);	break;
				default:	throw new IllegalArgumentException("invalid character: " + ch);
				}
			}
		}
	}
	public static void main(String[] args) throws Exception {
		do_experiment();
	}
	protected static void do_experiment() throws Exception {
		File[] files = new File(prodir).listFiles();
 		for(int k = 0; k < files.length; k++) {
 			System.out.println("Test project -- " + files[k].getName());
 			do_experiment(files[k].getName()); System.out.println("");
 		}
	}
	protected static void do_experiment(String name) throws Exception {
		File[] files = get_files_of(name); int counter;
		JCMT_Project project = get_project(files[0], files[1]);
		System.out.println("\t(1) Open project: " + project.
				get_code_manager().get_mutant_space().size() + " mutants.");
		counter = translate(project, files[1], CodeMutationType.coverage);
		System.out.println("\t(2) Translate " + counter + " scores into coverage");
		counter = translate(project, files[1], CodeMutationType.weakness);
		System.out.println("\t(3) Translate " + counter + " scores into weakness");
		counter = translate(project, files[1], CodeMutationType.stronger);
		System.out.println("\t(4) Translate " + counter + " scores into stronger");
	}
	
	/* project getters */
 	/**
	 * get the [project_root; code_file; nequiv_file; ]
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static File[] get_files_of(String name) throws Exception {
		File root = new File(prodir + name);
		File cfile = new File(cfidir + name + ".c");
		File efile = new File(eqvdir + name + ".txt");
		return new File[] { root, cfile, efile };
	}
	/**
	 * Open an existing test project from file
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static JCMT_Project get_project(File root, File cfile) throws Exception {
		JCMT_Project project = JCMT_Builder.open(root);
		JCMT_Builder.set_muta_cursor(project, cfile); return project;
	}
	/**
	 * translate the old data source to new score data base 
	 * @param project
	 * @param cfile
	 * @param option
	 * @return
	 * @throws Exception
	 */
	private static int translate(JCMT_Project project, File cfile, CodeMutationType option) throws Exception {
		if(project == null || !project.get_code_manager().is_cursor_openned())
			throw new IllegalArgumentException("no mutant and code are specified");
		else if(cfile == null)
			throw new IllegalArgumentException("invalid cfile: null");
		else if(option == null)
			throw new IllegalArgumentException("invalid cfile: null");
		else {
			/* declarations */
			MutantSpace mspace = project.get_code_manager().get_mutant_space();
			TestOracleManager oracle = project.get_oracle_manager(cfile, option);
			File[] data_source = get_old_score(project, option);
			if(data_source == null) 
				throw new IllegalArgumentException("undefined: " + 
						project.get_resource().get_root().getAbsolutePath());
			
			/* build up the files of mutation scores */
			List<MutScore> buffer = new ArrayList<MutScore>();
			Collection<Mutant> mutants = mspace.get_all();
			for(Mutant mutant : mutants) {
				buffer.add(oracle.produce_score(mutant)); 
			}
			
			/* read the old data items */
			int counter = 0;
			for(int k = 0; k < data_source.length; k++) {
				counter += read_old_scores(buffer, data_source[k]);
			}
			
			/* write the new data items */
			oracle.save_scores(buffer.iterator());
			
			
			/* return */	return counter;
		}
	}
	private static File[] get_old_score(JCMT_Project project, CodeMutationType option) throws Exception {
		File root = project.get_resource().get_root();
		String prefix;
		switch(option) {
		case coverage:	prefix = cover_prefx;	break;
		case weakness:	prefix = infect_prefx;	break;
		case stronger:	prefix = strong_prefx;	break;
		default:	throw new IllegalArgumentException("unknown option: " + option);
		}
		File dir = new File(root.getAbsolutePath() + File.separator + prefix);
		if(!dir.exists() || !dir.isDirectory())
			throw new IllegalArgumentException("Not found: " + dir.getAbsolutePath());
		else return dir.listFiles();
	}
	private static int read_old_scores(Collection<MutScore> scores, File source) throws Exception {
		String path = source.getName(); int n;
		if(path.endsWith(".db")) {
			__MutScoreDBInterface dbi = new __MutScoreDBInterface();
			dbi.open(source); n = dbi.read_scores(scores); dbi.close();
			return n;
		}
		else return 0;
	}

}
