package __backup__;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.AstFile;

/**
 * to manage the inputs and outputs of the files
 * from | into the xxx/project/source/
 * @author yukimula
 */
public class CodeManager {
	
	/* constructor */
	protected CodeResource resource;
	protected AstFile cursor;
	protected MutantSpace mspace;
	protected MutaCodeGenerator mut_generator;
	public CodeManager(CodeResource resource) throws Exception {
		if(resource == null)
			throw new IllegalArgumentException("Invalid resource: null");
		else { 
			this.resource = resource; 
			mspace = new MutantSpace(this); 
			cursor = null; 
			mut_generator = new MutaCodeGenerator();
			resource.get_compile_list().clear();	/* clear original cache */
		}
	}
	
	/* getters */
	/**
	 * get the directory resource of xxx/project/source
	 * @return
	 */
	public CodeResource get_resource() { return resource; }
	/**
	 * get the AST and C file as cursor refers to
	 * @return
	 */
	public AstFile get_cursor() { return cursor; }
	/**
	 * get the mutant space to the mutations of cursor file
	 * @return
	 */
	public MutantSpace get_mutant_space() { return mspace; }
	
	/* cursor methods */
	/**
	 * whether the cursor has been set in some C file
	 * in the xxx/project/code/
	 * @return
	 */
	public boolean is_cursor_openned() { return cursor != null; }
	/**
	 * set to the specified cursor
	 * @param cfile
	 * @throws Exception
	 */
	public void open_cursor(File cfile) throws Exception {
		this.close_cursor();
		cfile = resource.get_code().get_of(cfile);
		cursor = JCMutationUtil.ast_file_of(cfile);
	}
	/**
	 * close the cursor to C file
	 * @throws Exception
	 */
	public void close_cursor() throws Exception {
		mspace.clear(); cursor = null;
	}
	
	/* input methods */
	/**
	 * set the code files under xxx/project/source/code/.
	 * This will clear the database files under source/muta/; 
	 * and then clear the _compile_K/ cache list.
	 * That's because reset the files in code will make
	 * the original files become out-of-date.
	 * To avoid invalid access, all the original data will
	 * have to be deleted.<br>
	 * <br>
	 * So <b>CALL THIS METHOD CAREFULLY</b>!<br>
	 * The database file in xxx/project/source/muta will be
	 * recreated as empty database template.
	 * @param cfiles
	 * @throws Exception
	 */
	public void reset_code(Iterator<File> cfiles) throws Exception {
		/* clear the out-of-date data */
		this.close_cursor();
		resource.get_code().clear();
		resource.get_muta().clear();
		resource.get_compile_list().clear();
		
		/* input the code files */
		while(cfiles.hasNext()) {
			File cfile = cfiles.next();
			if(!cfile.isDirectory()) {
				if(FileProcess.name_of(cfile).endsWith(".c")) {
					resource.get_code().put_of(cfile);
					resource.get_muta().put_of(cfile, JCMConfig.JCM_DB_TEMPLATE);
					resource.get_mutac().put_of(cfile, JCMConfig.JCM_DB_TEMPLATE);
				}
			}
		}
	}
	/**
	 * generate the mutants and reset the database files under
	 * xxx/source/muta/ according to the code files in the 
	 * xxx/source/code/*.c
	 * <br><br>
	 * This method can be extremely <b>TEDIOUS</b> and will
	 * clear the database files under xxx/source/muta and
	 * close the cursor to the mutant space.
	 * @throws Exception
	 */
	public void reset_muta() throws Exception {
		this.close_cursor();	/* close the cursor */
		
		File[] cfiles = resource.get_code().get_files();
		for(int i = 0; i < cfiles.length; i++) {
			/* generate mutants */
			AstFile astfile = JCMutationUtil.ast_file_of(cfiles[i]);
			Set<Mutation> mutations = JCMutationUtil.gen_mutation(astfile.get_ast_root());
			JCMutationUtil.mutation2text(mutations, mspace); mutations.clear();
			
			/* write mutants to data file */
			File dbfile = resource.get_muta().get_of(cfiles[i]);
			MutantDBInterface dbi = new MutantDBInterface(astfile);
			dbi.open(dbfile, JCMConfig.JCM_DB_TEMPLATE); 
			dbi.write(mspace.gets()); dbi.close(); mspace.clear();
		}
	}
	/**
	 * This only generate the mutants for the project without saving them into the database
	 * @throws Exception
	 */
	public void gen_muta() throws Exception { 
		this.close_cursor();	/* close the cursor */
		File[] cfiles = resource.get_code().get_files();
		for(int i = 0; i < cfiles.length; i++) {
			/* generate mutants */
			AstFile astfile = JCMutationUtil.ast_file_of(cfiles[i]);
			Set<Mutation> mutations = JCMutationUtil.gen_mutation(astfile.get_ast_root());
			JCMutationUtil.mutation2text(mutations, mspace); mutations.clear();
		}
	}
	/**
	 * generate the context-mutations and reset the database files under source/mutac/xxx.db
	 * @throws Exception
	 */
	public void reset_mutac() throws Exception {
		this.close_cursor();	/* close the cursor */
		
		File[] cfiles = resource.get_code().get_files();
		for(int i = 0; i < cfiles.length; i++) {
			/* generate mutants */
			AstFile astfile = JCMutationUtil.ast_file_of(cfiles[i]);
			Set<Mutation> mutations = JCMutationUtil.gen_mutation(astfile.get_ast_root());
			JCMutationUtil.mutation2context(mutations, mspace, astfile); mutations.clear();
			
			/* write mutants to data file */
			File dbfile = resource.get_mutac().get_of(cfiles[i]);
			ContextDBInterface dbi = new ContextDBInterface(astfile);
			dbi.open(dbfile, JCMConfig.JCM_DB_TEMPLATE); 
			dbi.write(mspace.gets()); dbi.close(); mspace.clear();
		}
	}
	/**
	 * this will clear the original cache directory list and then
	 * create n cache directories.
	 * @param n : negative or zero when clear the cache
	 * @throws Exception
	 */
	public void reset_cache(int n) throws Exception {
		FileCacheList comps = resource.get_compile_list();
		
		comps.clear();
		if(n > 0) {
			comps.reset(n); 
			File source = resource.get_code().get_root();
			
			Iterator<File> cache = comps.get_cache_list();
			while(cache.hasNext()) {
				File target = cache.next();
				FileProcess.copy(source, target);	/* initialize the cache directory */
			}
		}
	}
	/**
	 * clear the mutant space
	 */
	public void reset_muta_space() { mspace.clear(); }
	
	/* mutant loaders */
	/**
	 * Load all the mutants from database for current cursor
	 * @return
	 * @throws Exception
	 */
	public int load_muta() throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else {
			/* clear the original mutants in space */
			mspace.clear();
			
			/* getters */
			File source = cursor.get_source();
			File dbfile = resource.get_muta().get_of(source);
			MutantDBInterface dbi = new MutantDBInterface(cursor);
			
			/* read from DB */
			dbi.open(dbfile); dbi.read(mspace); dbi.close();
			
			/* return */	return mspace.size();
		}
	}
	/**
	 * Load the mutants of specified operators to mutant space
	 * for the current cursor source file. This will <b>NOT</b> clear the 
	 * mutant space but <b>APPEND</b> new mutants to it.
	 * @param operators
	 * @return : number of mutants read from data file
	 * @throws Exception
	 */
	public int load_muta(MutOperator operator) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else {
			/* getters */
			File source = cursor.get_source();
			File dbfile = resource.get_muta().get_of(source);
			MutantDBInterface dbi = new MutantDBInterface(cursor);
			
			/* database read process */
			dbi.open(dbfile); 
			int num = dbi.read(operator, mspace); 
			dbi.close(); return num;
		}
	}
	/**
	 * Load the mutants of specified operator modes to mutant space
	 * for the current cursor source file. This will <b>NOT</b> clear the 
	 * mutant space but <b>APPEND</b> new mutants to it.
	 * @param modes
	 * @return
	 * @throws Exception
	 */
	public int load_muta(MutationMode mode) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else if(mode == null)
			throw new IllegalArgumentException("Invalid mode: null");
		else {
			/* getters */
			File source = cursor.get_source();
			File dbfile = resource.get_muta().get_of(source);
			MutantDBInterface dbi = new MutantDBInterface(cursor);
			
			/* database read process */
			dbi.open(dbfile); 
			int num = dbi.read(mode, mspace);
			dbi.close(); return num;
		}
	}
	/**
	 * Load the mutants seeded in the specified function.
	 * This will <b>NOT</b> clear the space but <b>APPEND</b> the 
	 * mutants to the space.
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public int load_muta(AstFunctionDefinition function) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else if(function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else {
			/* getters */
			File source = cursor.get_source(); int n;
			File dbfile = resource.get_muta().get_of(source);
			MutantDBInterface dbi = new MutantDBInterface(cursor);
			Queue<AstNode> queue = new LinkedList<AstNode>();
			
			/* database read process */
			dbi.open(dbfile); int num = 0; 
			queue.add(function.get_body());
			while(!queue.isEmpty()) {
				/* read mutants among the node */
				AstNode loc = queue.poll();
				num += dbi.read(loc, mspace);
				
				/* append the children nodes */
				n = loc.number_of_children();
				for(int i = 0; i < n; i++) {
					AstNode child = loc.get_child(i);
					if(child != null) queue.add(child);
				}
			}
			dbi.close(); return num;
		}
	}
	
	/* context mutant loaders */
	/**
	 * Load all the context-mutations to the space. This will clear the mutant space.
	 * @return
	 * @throws Exception
	 */
	public int load_mutac() throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else {
			/* clear the original mutants in space */
			mspace.clear();
			
			/* getters */
			File source = cursor.get_source();
			File dbfile = resource.get_mutac().get_of(source);
			ContextDBInterface dbi = new ContextDBInterface(cursor);
			
			/* read from DB */
			dbi.open(dbfile); dbi.read(mspace); dbi.close();
			
			/* return */	return mspace.size();
		}
	}
	/**
	 * Load the context-mutations to the space, APPEND rather than CLEAR!
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public int load_mutac(MutOperator operator) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else {
			/* getters */
			File source = cursor.get_source();
			File dbfile = resource.get_mutac().get_of(source);
			ContextDBInterface dbi = new ContextDBInterface(cursor);
			
			/* database read process */
			dbi.open(dbfile); 
			int num = dbi.read(operator, mspace); 
			dbi.close(); return num;
		}
	}
	/**
	 * Load the context mutations of specified mode, APPEND rather than CLEAR.
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public int load_mutac(MutationMode mode) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else if(mode == null)
			throw new IllegalArgumentException("Invalid mode: null");
		else {
			/* getters */
			File source = cursor.get_source();
			File dbfile = resource.get_mutac().get_of(source);
			ContextDBInterface dbi = new ContextDBInterface(cursor);
			
			/* database read process */
			dbi.open(dbfile); 
			int num = dbi.read(mode, mspace);
			dbi.close(); return num;
		}
	}
	/**
	 * Load the context mutations in specified function. APPEND rather than CLEAR!
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public int load_mutac(AstFunctionDefinition function) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: no cursor is set");
		else if(function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else {
			/* getters */
			File source = cursor.get_source(); 
			File dbfile = resource.get_mutac().get_of(source);
			ContextDBInterface dbi = new ContextDBInterface(cursor);
			
			/* database read process */
			dbi.open(dbfile); int num = 0; 
			dbi.mread(function, mspace);
			dbi.close(); return num;
		}
	}
	
	/* compile directory methods */
	/**
	 * generate the mutation code file for mutant in space to the <code>index</code> compiler-directory file.
	 * @param mutant : mutation file to be generated
	 * @param index : index to the compiler directory list
	 * @param mtype : 0 (coverage), 1 (weak mutation), 2 (strong mutation)
	 * @return
	 * @throws Exception
	 */
	public void write_muta_file(int mutant, int index, CodeMutationType mtype) throws Exception {
		if(cursor == null)
			throw new IllegalArgumentException("Invalid access: cursor is not openned");
		else if(!mspace.has(mutant))
			throw new IllegalArgumentException("Invalid access: mutant[" 
					+ mutant + "] is not loaded to current space");
		else if(index < 0 || index >= resource.get_compile_list().size())
			throw new IllegalArgumentException("Invalid access: _compile_[" 
					+ index + "] is not dispatched in " + resource.get_root().getAbsolutePath());
		else {
			File cdir = resource.get_compile_list().get_cache(index);
			String name = FileProcess.name_of(cursor.get_source());
			File target = FileProcess.file_of(cdir, name);
			mut_generator.write(mspace.get(mutant), cursor, target, mtype);
		}
	}
	
}
