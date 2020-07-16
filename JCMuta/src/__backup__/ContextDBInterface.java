package __backup__;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import com.jcsa.jcparse.lang.AstCirFile;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

/**
 * Data base interfaces for context-sensitive mutations
 * @author yukimula
 */
public class ContextDBInterface extends DBInterface {
	
	/** name of table in SQL-DB **/
	public static final String TABLE = "cmutants";
	
	/** number of mutations in cache **/
	public static final int SIZE_CACHE = 1024 * 96; 
	/** statement template for SQL insert */
	public static final String WRITE_ITEM_STMT = "insert into cmutants (id, operator, mode, origin, replace, callee, funcname) values (?, ?, ?, ?, ?, ?, ?);";
	public static final String READ_SIZE_ITEMS = "select count(*) from ctmutants;";
	public static final String READ_ALL_ITEMS = "select * from cmutants;";
	public static final String READ_ITEM_BY_OP = "select * from cmutants where operator=?;";
	public static final String READ_ITEM_BY_MODE = "select * from cmutants where mode=?;";
	public static final String READ_ITEM_BY_PONT = "select * from cmutants where origin=?;";
	public static final String READ_ITEM_BY_APPS = "select * from cmutants where callee=?;";
	public static final String READ_ITEM_BY_FUNC = "select * from cmutants where funcname=?;";
	public static final String READ_ITEM_BY_ID 	= "select * from cmutants where id=?;";
	
	protected AstCirFile context;
	public ContextDBInterface(AstCirFile context) throws Exception {
		super();
		if(context == null)
			throw new IllegalArgumentException("No context!");
		else this.context = context;
	}
	
	/* data-IOs */
	/**
	 * number of mutations in data file
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
				ContextMutation mutation = 
						(ContextMutation) mutant.get_mutation();
				stmt.setInt(1, mutant.get_mutant_id());
				stmt.setString(2, mutation.get_operator().toString());
				stmt.setString(3, mutation.get_mode().toString());
				stmt.setInt(4, mutation.get_origin().get_key());
				stmt.setString(5, mutation.get_replace());
				stmt.setInt(6, mutation.get_callee().get_key());
				stmt.setString(7, mutation.get_muta_function());
				
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
					parse(space, rs);
					count = count + 1;
				}
				else report("Ignoring mutant " + rs.getInt("id"));
			}
			return count;
		}
	}
	/**
	 * read the mutants for give application point
	 * @param app
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int mread(AstNode callee, MutantSpace space) throws Exception {
		if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else if(connection == null)
			throw new RuntimeException("Invalid access: not openned");
		else if(callee == null)
			throw new IllegalArgumentException("Invalid callee: null");
		else {
			PreparedStatement stmt = connection.prepareStatement(READ_ITEM_BY_APPS);
			stmt.setInt(1, callee.get_key()); 
			
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
	 * read the mutants seeded in given function
	 * @param fun
	 * @param space
	 * @return
	 * @throws Exception
	 */
	public int mread(AstFunctionDefinition fun, MutantSpace space) throws Exception {
		PreparedStatement stmt = connection.prepareStatement(READ_ITEM_BY_FUNC);
		stmt.setString(1, MutaCode.Muta_Prefix + get_name(fun.get_declarator()));
		
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
	private String get_name(AstDeclarator decl) throws Exception {
		while(decl.get_production() != DeclaratorProduction.identifier) {
			decl = decl.get_declarator();
		}
		return decl.get_identifier().get_name();
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
		int origin_key = rs.getInt("origin"); 
		AstNode origin = context.get_ast_tree().get_node(origin_key);
		String replace = rs.getString("replace");
		int callee_key = rs.getInt("callee");
		AstNode callee = context.get_ast_tree().get_node(callee_key);
		String funcname = rs.getString("funcname");
		
		if(!(callee instanceof AstFunCallExpression))
			throw new RuntimeException("Invalid callee: \"" + 
					callee.get_location().read() + "\"");
		else {
			ContextMutation mutation = ContextMutation.
					produce(operator, mut_mode, origin, replace, 
							(AstFunCallExpression) callee, funcname);
			return space.new_mutant(mid, mutation);
		}
	}
	/**
	 * report information to system
	 * @param msg
	 */
	protected void report(String msg) {
		System.err.println(msg);
	}
	
}
