package __backup__;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.text.CLocation;
import com.jcsa.jcparse.lang.text.CText;
import com.jcsa.jcparse.lang.AstFile;

/**
 * To generate code for mutation
 * @author yukimula
 */
public class MutaCodeGenerator {
	
	protected StringBuilder buff;
	public MutaCodeGenerator() {
		buff = new StringBuilder();
	}
	
	/**
	 * generate the mutated code for strong mutation
	 * @param mutation
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public void write(Mutant mutant, AstFile source, File target, CodeMutationType mtype) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(mtype == null)
			throw new IllegalArgumentException("invalid mtype: null");
		else {
			/* FIX: avoid incorrect weak mutation 
			 * If no file generated, compilation fails */
			try {
				buff.setLength(0);
				gen_jcm_code(mutant, source, mtype);
			}
			catch(Exception ex) {
				ex.printStackTrace(); 
				FileProcess.remove(target);
				return;
			}
			
			FileWriter writer = new FileWriter(target);
			writer.write(buff.toString()); writer.close();
		}
	}
	
	/**
	 * generate code for mutant
	 * @param mutant
	 * @param source
	 * @throws Exception
	 */
	protected void gen_jcm_code(Mutant mutant, AstFile source, CodeMutationType mtype) throws Exception {
		/* initialization */
		buff.setLength(0); TextMutation mutation = mutant.get_mutation();
		 
		/* header outputs */
		buff.append(String.format(MutaCode.Muta_Inform, 
				mutant.get_mutant_id(), mutation.get_operator().toString(), 
				mutation.get_mode().toString()));
		buff.append("#include \"").append(MutaCode.Muta_Header).append("\"\n\n");
		
		/* translate mutation */
		switch(mtype) {
		case coverage:	mutation = JCMutationUtil.mutation2coverage(mutation);	break;
		case weakness:	mutation = JCMutationUtil.mutation2weak(mutation);		break;
		default:		break; }
		 
		/* generate the mutation code in buffer */
		if(mutation instanceof ContextMutation)
			gen_ctmutant((ContextMutation) mutation, source);
		else gen_fomutant(mutation, source);
	}
	/**
	 * generate code for first-order mutation
	 * @param mutation
	 * @param source
	 * @throws Exception
	 */
	protected void gen_fomutant(TextMutation mutation, AstFile source) throws Exception {
		/* declaration */
		AstNode origin = mutation.get_origin();
		CLocation loc = origin.get_location();
		int beg = loc.get_bias(), k = 0;
		int end = beg + loc.get_length();
		CText text = source.get_code();
		
		/* prefix outputs */
		for(k = 0; k < beg; k++) 
			buff.append(text.get_char(k));
		 
		/* mutate the code line */
		buff.append(mutation.get_replace());
		 
		/* postfix file */
		int n = text.length(); boolean first = true;
		for(k = end; k < n; k++) {
			char ch = text.get_char(k);
			if(first && ch == '\n') {
				first = false; buff.append("\t/* mutated line */");
			}
			buff.append(ch); 
		}
	}
	/**
	 * generate the mutation code for context-related mutant 
	 * @param mutation
	 * @param Source
	 * @throws Exception
	 */
	protected void gen_ctmutant(ContextMutation mutation, AstFile source) throws Exception {
		this.gen_mut_declar(mutation, source);
		this.gen_mut_callee(mutation, source);
		this.gen_mut_define(mutation, source);
	}
	
	/* supporting methods */
	/***
	 * generate the mutated function declaration
	 * @param mutation
	 * @param source
	 * @throws Exception
	 */
	private void gen_mut_declar(ContextMutation mutation, AstFile source) throws Exception {
		/* get the function where mutant is seeded */
		AstFunctionDefinition def = source.function_of(mutation.get_origin());
		if(def == null) throw new IllegalArgumentException("Not in function");
		
		/* get the index for deriving text */
		CText text = source.get_code();
		int fbeg = def.get_location().get_bias();
		AstName fname = this.find_name(def.get_declarator()); 
		int nbeg = fname.get_location().get_bias();
		
		/* prefix */ while(fbeg < nbeg) buff.append(text.get_char(fbeg++));
		/* function name */ buff.append(' ').append(mutation.get_muta_function());
		/* argument type list */ buff.append("( ); \t// declare mutated function\n");
	}
	/**
	 * generate the mutation function
	 * @param mutation
	 * @param source
	 * @throws Exception
	 */
	private void gen_mut_define(ContextMutation mutation, AstFile source) throws Exception {
		/* get the function where mutant is seeded */
		AstFunctionDefinition def = source.function_of(mutation.get_origin());
		if(def == null) throw new IllegalArgumentException("Not in function");
		
		/* find the mutated point */
		CText text = source.get_code(); int i; char ch;
		int fbeg = def.get_location().get_bias();
		int fend = fbeg + def.get_location().get_length();
		AstName fname = this.find_name(def.get_declarator());
		int nbeg = fname.get_location().get_bias();
		int nend = nbeg + fname.get_location().get_length();
		AstNode seed_point = mutation.get_origin();
		int sbeg = seed_point.get_location().get_bias();
		int send = seed_point.get_location().get_length() + sbeg;
		
		/* comment before mutated function */
		buff.append("\n// mutated function start\n");
		
		/* before the mutated name */
		i = fbeg; while(i < nbeg) buff.append(text.get_char(i++));
		
		/* seed the mutated name */
		buff.append(mutation.get_muta_function());
		
		/* before the seed point */
		i = nend; while(i < sbeg) buff.append(text.get_char(i++));
		
		/* seed the mutated code */
		buff.append(mutation.get_replace());
		
		/* seed comment until new_line */
		i = send; 
		do { 
			ch = text.get_char(i++); 
			if(ch == '\n') break;
			else buff.append(ch); 
		} while(i < fend);
		
		/* seed mutated comment */
		buff.append(" \t /* mutated line */\n");
		
		/* final code generation */
		while(i < fend) { buff.append(text.get_char(i++)); }
		
		/* comment after mutated function */
		buff.append("\n// mutated function end\n\n");
	}
	private AstName find_name(AstDeclarator decl) throws Exception {
		while(decl.get_production() != 
				DeclaratorProduction.identifier)
			decl = decl.get_declarator();
		return decl.get_identifier();
	}
	/**
	 * generate the mutated callee point in following program
	 * @param mutation
	 * @param source
	 * @throws Exception
	 */
	private void gen_mut_callee(ContextMutation mutation, AstFile source) throws Exception {
		/* declarations */
		AstFunCallExpression call_point = mutation.get_callee();
		AstExpression func_expr = call_point.get_function();
		
		/* get the seeding point */
		CText text = source.get_code(); int end = text.length();
		int cbeg = func_expr.get_location().get_bias(); int i; char ch;
		int cend = func_expr.get_location().get_length() + cbeg;
		
		/* before call point */
		i = 0; while(i < cbeg) buff.append(text.get_char(i++));
		
		/* mutated call point */
		buff.append(mutation.get_muta_function());
		
		/* until the next new_line */
		i = cend; 
		while(i < end) {
			ch = text.get_char(i++);
			if(ch == '\n') break;
			else buff.append(ch);
		}
		
		/* seed comment for callee */
		buff.append(" \t/* mutated call point */\n");
		
		/* put the following code */
		while(i < end) buff.append(text.get_char(i++));
	}
	
}
