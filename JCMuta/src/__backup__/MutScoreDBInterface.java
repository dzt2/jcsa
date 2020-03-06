package __backup__;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import com.jcsa.jcparse.lang.base.BitSequence;

/**
 * Database interface for <code>MutScore</code>
 * @author yukimula
 */
public class MutScoreDBInterface extends DBInterface {
	
	/** number of mutations in cache **/
	public static final int BUFF_SIZE = 1024 * 96; 
	
	/* SQL query */
	public static final String TABLE_NAME = "scores";
	public static final String WRITE_STMT = "insert into scores (mutant, score) values (?, ?);";
	public static final String READ_STMT_ALL = "select * from scores;";
	public static final String READ_STMT_COUNT = "select count(*) from scores;";
	public static final String READ_STMT_MUTANT = "select * from scores where mutant=?;";
	
	/* constructor */
	private StringBuilder string_buffer;
	/**
	 * construct a DB-API for access mutation scores 
	 */
	public MutScoreDBInterface() { 
		super(); 
		string_buffer = new StringBuilder();
	}
	
	/* access */
	/**
	 * get the number of results in the data file
	 * @return
	 * @throws Exception
	 */
	public int size() throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_STMT_COUNT);
			int size = rs.getInt(1); stmt.close(); return size;
		}
	}
	/**
	 * write all the scores to the 
	 * @param scores
	 * @return
	 * @throws Exception
	 */
	public int write_scores(Iterator<MutScore> scores) throws Exception {
		if(scores == null)
			throw new IllegalArgumentException("invalid scores: null");
		else if(scores.hasNext()) {
			/* set the connection not to commit for each SQL query */
			this.connection.setAutoCommit(false); int counter = 0, num = 0;
			PreparedStatement stmt = connection.prepareStatement(WRITE_STMT);
			
			/* write each mut-score to the data file */
			while(scores.hasNext()) {
				/* get next mutation score */
				MutScore score = scores.next();
				if(score == null) continue;
				
				/* add item to buffer */
				stmt.setInt(1, score.get_mutant());
				stmt.setString(2, this.string_of_score(score));
				
				/* add the query to the cache */
				stmt.addBatch(); counter++; num++;
				
				/* commit the queries when cache is full */
				if(counter >= BUFF_SIZE) {
					stmt.executeBatch();
					connection.commit(); 
					counter = 0;
				}
			}
			
			/* commit the remainders of query */
			if(counter > 0) {
				stmt.executeBatch();
				connection.commit();
			}
			
			/* recover the connection settings */
			connection.setAutoCommit(true); 
			stmt.close(); return num;
		}
		else return 0;
	}
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
					}
					
				}
			}
			return count;
		}
	}
	/**
	 * read the score of the mutant
	 * @param mutant
	 * @param scores
	 * @return
	 * @throws Exception
	 */
	public void read_score(MutScore score) throws Exception {
		if(score == null)
			throw new IllegalArgumentException("invalid score: null");
		else {
			PreparedStatement stmt =
					connection.prepareStatement(READ_STMT_MUTANT);
			stmt.setInt(1, score.get_mutant());
			ResultSet rs = stmt.executeQuery(); 
			
			try {
				rs.next();
				this.parse(score, rs); 
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/* basic methods */
	private String string_of_score(MutScore score) throws Exception {
		BitSequence seq = score.get_score_set();
		int k, n = seq.length();
		
		string_buffer.setLength(0);
		for(k = 0; k < n; k++) {
			if(seq.get(k))
				string_buffer.append('1');
			else string_buffer.append('0');
		}
		
		return string_buffer.toString();
	}
	private void parse(MutScore score, ResultSet rs) throws Exception {
		String txt = rs.getString("score");
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
