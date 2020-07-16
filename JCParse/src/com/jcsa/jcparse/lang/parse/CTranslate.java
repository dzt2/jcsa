package com.jcsa.jcparse.lang.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.impl.AstNodeDFSIterator;
import com.jcsa.jcparse.lang.astree.impl.AstNodeIterator;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLib;
import com.jcsa.jcparse.lang.lexical.keywords.CLangKeywordLoaderFactory;
import com.jcsa.jcparse.lang.parse.parser.C89_Parser;
import com.jcsa.jcparse.lang.parse.parser.C99_Parser;
import com.jcsa.jcparse.lang.parse.parser.CConstantEvaluator;
import com.jcsa.jcparse.lang.parse.parser.CExpressionBuilder;
import com.jcsa.jcparse.lang.parse.parser.CParser;
import com.jcsa.jcparse.lang.parse.parser.CTypeBuilder;
import com.jcsa.jcparse.lang.parse.parser2.CirParser;
import com.jcsa.jcparse.lang.parse.tokenizer.CTokenStream;
import com.jcsa.jcparse.lang.parse.tokenizer.CTokenizer;
import com.jcsa.jcparse.lang.parse.tokenizer.CTokenizerImpl;
import com.jcsa.jcparse.lang.text.CText;

/**
 * Used to parse source code to AST and derive the information
 * of its declarations and expression-types
 * @author yukimula
 */
public class CTranslate {
	
	// source text method
	/**
	 * get the standard text of source file
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static CText get_source_text(File file) throws Exception {
		/* read the standard text from source file characters */
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		StringBuilder buffer = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
			buffer.append(CText.LINE_SEPARATOR);
		}
		reader.close();
		buffer.append(CText.LINE_SEPARATOR);

		/* construct for CText object */
		CText text = new CText();
		text.append(buffer.toString());

		/* return text */ return text;
	}
	// keyword methods
	protected static void load_c89_keyword(CLangKeywordLib lib) throws Exception {
		lib.clear();
		CLangKeywordLoaderFactory.get_c89_keyword_loader().load(lib);
	}
	protected static void load_c99_keyword(CLangKeywordLib lib) throws Exception {
		lib.clear();
		CLangKeywordLoaderFactory.get_c89_keyword_loader().load(lib);
		CLangKeywordLoaderFactory.get_c99_keyword_loader().load(lib);
	}
	protected static void load_gnu_keyword(CLangKeywordLib lib) throws Exception {
		lib.clear();
		CLangKeywordLoaderFactory.get_c89_keyword_loader().load(lib);
		CLangKeywordLoaderFactory.get_c99_keyword_loader().load(lib);
		CLangKeywordLoaderFactory.get_gnu_keyword_loader().load(lib);
	}
	
	// AST parser
	/**
	 * parse the source code and generate AST
	 * @param file
	 * @param std
	 * @return
	 * @throws Exception
	 */
	public static AstTranslationUnit get_ast_root(File file, ClangStandard std) throws Exception {
		if(file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else if(!file.exists())
			throw new IllegalArgumentException("Undefined file: " + file.getAbsolutePath());
		else {
			/* get parser and keyword-library */
			CParser parser; CLangKeywordLib lib;
			lib = new CLangKeywordLib();
			switch(std) {
			case gnu_c89:	load_gnu_keyword(lib);	parser = new C89_Parser(); break;
			case gnu_c99:	load_gnu_keyword(lib);	parser = new C99_Parser(); break;
			default:
				throw new IllegalArgumentException("Invalid C-standard: " + std);
			}
			
			/* get standard text of source code in file */
			CText text = get_source_text(file);
			
			/* get scanner from source code */
			CTokenizer tokenizer = new CTokenizerImpl(lib);
			tokenizer.open(text);
			CTokenStream stream = tokenizer.get_stream();
			
			/* parse to get AST root */
			AstTranslationUnit root;
			if ((root = parser.parse(stream)) == null)
				throw new RuntimeException("Invalid parse for " + file.getAbsolutePath());
			
			/* return AST */ return root;
		}
	}
	/**
	 * derive the types for declarations and expression in AST nodes
	 * @param root
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public static boolean derive_types_for(AstTranslationUnit root, CRunTemplate template) throws Exception {
		/* prepare for the computer */
		CConstantEvaluator evaluator = new CConstantEvaluator(template);
		CTypeBuilder builder = new CTypeBuilder(evaluator);
		CExpressionBuilder expr_builder = new CExpressionBuilder();
		
		/* set the iterator to access nodes in AST */
		AstNodeIterator iterator = new AstNodeDFSIterator(root);
		
		/* derive types from declarations */
		while(iterator.has_next()) {
			AstNode node = iterator.get_next();
			try {
				if (node instanceof AstDeclaration)
					builder.build_up((AstDeclaration) node);
				else if (node instanceof AstTypeName)
					builder.build_up((AstTypeName) node);
				else if (node instanceof AstFunctionDefinition)
					builder.build_up((AstFunctionDefinition) node);
			}
			catch(Exception ex) {
				System.out.println(node.get_location().trim_code());
				throw ex;
			}
			
		}
		
		/* build up types for each expression */
		iterator = new AstNodeDFSIterator(root);
		while(iterator.has_next()) {
			AstNode node = iterator.get_next();
			if (node instanceof AstExpression)
				expr_builder.build_up((AstExpression) node);
		}
		
		/* return success */ return true;
	}
	
	/**
	 * generate the abstract syntax tree from source code text
	 * @param source_file
	 * @param standard
	 * @param sizeof_base
	 * @return
	 * @throws Exception
	 */
	public static AstTree parse(File source_file, ClangStandard standard, CRunTemplate sizeof_base) throws Exception {
		CText source_code = get_source_text(source_file);
		AstTranslationUnit ast_root = get_ast_root(source_file, standard);
		derive_types_for(ast_root, sizeof_base);
		
		return new AstTree(source_file, source_code, ast_root);
	}
	/**
	 * generate the C-like intermediate representation by parsing the abstract syntax tree.
	 * @param ast_tree
	 * @return
	 * @throws Exception
	 */
	public static CirTree parse(AstTree ast_tree, CRunTemplate sizeof_base) throws Exception {
		return CirParser.parse_all(ast_tree.get_ast_root(), sizeof_base);
	}
	
}
