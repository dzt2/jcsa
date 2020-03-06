package __backup__;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.AstFile;

public class MutantDBInterface extends DBInterface {
	
	/** name of table in SQL-DB **/
	public static final String TABLE = "mutants";
	
	/** number of mutations in cache **/
	public static final int SIZE_CACHE = 1024 * 96; 
	/** statement template for SQL insert */
	public static final String WRITE_ITEM_STMT = "insert into mutants (id, operator, mode, origin, replace) values (?, ?, ?, ?, ?);";
	public static final String READ_SIZE_ITEMS = "select count(*) from mutants;";
	public static final String READ_ALL_ITEMS = "select * from mutants;";
	public static final String READ_ITEM_BY_OP = "select * from mutants where operator=?;";
	public static final String READ_ITEM_BY_MODE = "select * from mutants where mode=?;";
	public static final String READ_ITEM_BY_PONT = "select * from mutants where origin=?;";
	public static final String READ_ITEM_BY_ID 	= "select * from mutants where id=?;";
	
	protected AstFile context;
	public MutantDBInterface(AstFile context) throws Exception {
		super();
		if(context == null)
			throw new IllegalArgumentException("No context!");
		else this.context = context;
	}
	
	/**
	 * read mutant by its id
	 * @param mid
	 * @return : null when no
	 * @throws Exception
	 */
	public Mutant read(MutantSpace space, int mid) throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else if(space.has(mid))
			throw new IllegalArgumentException("Duplicated mutant: " + mid);
		else {
			PreparedStatement stmt = connection.prepareStatement(READ_ITEM_BY_ID);
			stmt.setInt(1, mid); 
			
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return this.parse(space, rs);
			}
			else throw new RuntimeException("No mutant-" + mid + " is found");
		}
	}
	
	/**
	 * get the number of mutations in data file 
	 * @return
	 * @throws Exception
	 */
	public int size() throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_SIZE_ITEMS);
			int size = rs.getInt(1); stmt.close(); return size;
		}
	}
	/**
	 * write a set of mutants into the data file
	 * @param mutants
	 * @return : number of mutants written into the data base
	 * @throws Exception
	 */
	public int write(Iterator<Mutant> mutants) throws Exception {
		if(mutants != null && mutants.hasNext()) {
			/* set the connection not to commit for each SQL query */
			this.connection.setAutoCommit(false); int counter = 0, num = 0;
			PreparedStatement stmt = connection.prepareStatement(WRITE_ITEM_STMT);
			
			while(mutants.hasNext()) {
				/* get next mutant */
				Mutant mutant = mutants.next();
				if(mutant == null) continue;
				
				/* write the information to the data file */
				TextMutation mutation = mutant.get_mutation();
				stmt.setInt(1, mutant.get_mutant_id());
				stmt.setString(2, mutation.get_operator().toString());
				stmt.setString(3, mutation.get_mode().toString());
				stmt.setInt(4, mutation.get_origin().get_key());
				stmt.setString(5, mutation.get_replace());
				
				/* add the query to the cache */
				stmt.addBatch(); counter++; num++;
				
				/* commit the queries when cache is full */
				if(counter >= SIZE_CACHE) {
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
	 * read all the mutants in data file to the space (append)
	 * @param base
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int read(MutantSpace space) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else {
			int count = 0; 
			Statement stmt = this.connection.createStatement();
			ResultSet rs = stmt.executeQuery(READ_ALL_ITEMS);
			while(rs.next()) {
				if(!space.has(rs.getInt("id"))) {
					parse(space, rs);
					count = count + 1; 
				}
				else report("Ignoring mutant " + rs.getInt("id"));
			}
			return count;
		}
	}
	/**
	 * read mutants by their operator
	 * @param base
	 * @param operator
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int read(MutOperator operator, MutantSpace space) throws Exception {
		if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else {
			PreparedStatement stmt = connection.prepareStatement(READ_ITEM_BY_OP);
			stmt.setString(1, operator.toString()); 
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				if(!space.has(rs.getInt("id"))) {
					parse(space, rs);
					count = count + 1; 
				}
				else report("Ignoring mutant " + rs.getInt("id"));
			}
			return count;
		}
	}
	/**
	 * read mutants by mutation mode
	 * @param base
	 * @param mode
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int read(MutationMode mode, MutantSpace space) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid mutations: null");
		else if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else if(mode == null)
			throw new IllegalArgumentException("Invalid mode: null");
		else {
			PreparedStatement stmt = connection.prepareStatement(READ_ITEM_BY_MODE);
			stmt.setString(1, mode.toString()); 
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				if(!space.has(rs.getInt("id"))) {
					parse(space, rs);
					count = count + 1; 
				}
				else report("Ignoring mutant " + rs.getInt("id"));
			}
			return count;
		}
	}
	/**
	 * read the mutants seeded in given range
	 * @param base
	 * @param range
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int read(AstNode origin, MutantSpace space) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else if(origin == null)
			throw new IllegalArgumentException("Invalid point: null");
		else {
			PreparedStatement stmt = connection.prepareStatement(READ_ITEM_BY_PONT);
			stmt.setInt(1, origin.get_key()); 
			
			ResultSet rs = stmt.executeQuery(); int count = 0;
			while(rs.next()) {
				if(!space.has(rs.getInt("id"))) {
					this.parse(space, rs);
					count = count + 1; 
				}
				else report("Ignoring mutant " + rs.getInt("id"));
			}
			stmt.close(); return count;
		}
	}
	
	/* basic methods */
	/**
	 * parse the data item to corresponding mutant object (unlinked)
	 * @param rs
	 * @param base
	 * @return
	 * @throws Exception
	 */
	protected Mutant parse(MutantSpace space, ResultSet rs) throws Exception {
		int mid = rs.getInt("id");
		MutOperator operator = Mutant.get_operator_by(rs.getString("operator"));
		MutationMode mut_mode = Mutant.get_mode_from(operator, rs.getString("mode"));
		int astkey = rs.getInt("origin");
		AstNode origin = context.get_ast_node(astkey);
		String replace = rs.getString("replace");
		
		TextMutation mutation = TextMutation.produce(
				operator, mut_mode, origin, replace);
		return space.new_mutant(mid, mutation);
	}
	/**
	 * report information to system
	 * @param msg
	 */
	protected void report(String msg) {
		System.err.println(msg);
	}

}
