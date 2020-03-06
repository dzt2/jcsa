package __backup__;

import java.util.Iterator;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.AstFile;

/**
 * parse the <code>Mutation</code> to <code>ContextMutation</code>
 * @author yukimula
 *
 */
public class Mutation2ContextParser {
	
	/* constructor */
	protected AstFile source;
	protected Mutation2TextParser parser;
	public Mutation2ContextParser(AstFile source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			this.source = source;
			this.parser = new Mutation2TextParser();
		}
	}
	
	/* parse method */
	/**
	 * generate context-mutation from given mutation and put them into the result set
	 * @param mutation
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public int parse(Mutation mutation, Set<ContextMutation> results) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(results == null)
			throw new IllegalArgumentException("Invalid results: null");
		else {
			/* create the first-order mutation */
			TextMutation tmutant = parser.parse(mutation);
			
			/* get the function definition of the mutant */
			AstNode origin = tmutant.get_origin();
			AstFunctionDefinition def = source.function_of(origin);
			
			/* get the function caller in AST level */
			String name = this.name_of(def.get_declarator());
			CirFunctionCallGraph graph = source.get_cir_tree().get_function_call_graph();
			CirFunction caller = graph.get_function(name);
			Iterator<CirFunctionCall> calls = caller.get_in_calls().iterator();
			
			/* generate context mutation */
			int counter = 0;
			while(calls.hasNext()) {
				CirFunctionCall call = calls.next();
				AstFunCallExpression expr = (AstFunCallExpression) 
						call.get_call_statement().get_ast_source();
				String mutaname = MutaCode.Muta_Prefix + name;
				ContextMutation cmutant = ContextMutation.produce(
						tmutant.get_operator(), tmutant.get_mode(), 
						origin, tmutant.get_replace(), expr, mutaname);
				results.add(cmutant); counter++;
			}
			
			return counter;
		}
	}
	/**
	 * parse the context-independent mutation to context-dependent mutation(s)
	 * @param mutation
	 * @param results
	 * @return : number of generated mutants
	 * @throws Exception
	 */
	public int parse(TextMutation mutation, Set<ContextMutation> results) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(results == null)
			throw new IllegalArgumentException("Invalid results: null");
		else {
			TextMutation tmutant = mutation;
			/* get the function definition of the mutant */
			AstNode origin = tmutant.get_origin();
			AstFunctionDefinition def = source.function_of(origin);
			
			/* get the function caller in AST level */
			String name = this.name_of(def.get_declarator());
			CirFunctionCallGraph graph = source.get_cir_tree().get_function_call_graph();
			CirFunction caller = graph.get_function(name);
			Iterator<CirFunctionCall> calls = caller.get_in_calls().iterator();
			
			/* generate context mutation */
			int counter = 0;
			while(calls.hasNext()) {
				CirFunctionCall call = calls.next();
				AstFunCallExpression expr = (AstFunCallExpression) 
						call.get_call_statement().get_ast_source();
				String mutaname = MutaCode.Muta_Prefix + name;
				ContextMutation cmutant = ContextMutation.produce(
						tmutant.get_operator(), tmutant.get_mode(), 
						origin, tmutant.get_replace(), expr, mutaname);
				results.add(cmutant);
			}
			return counter;
		}
	}
	
	private String name_of(AstDeclarator declarator) throws Exception {
		while(declarator.get_production() != DeclaratorProduction.identifier)
			declarator = declarator.get_declarator();
		return declarator.get_identifier().get_cname().get_name();
	}
	
}
