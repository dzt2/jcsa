package com.jcsa.jcparse.lang.parse.parser2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.jcsa.jcparse.lang.ClangStandard;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.parse.CTranslate;

public class ACParserTest {
	
	protected static final String prefix = "D:\\SourceCode\\MyData\\CODE2\\ifiles";
	protected static final String postfx = "result/cod/";

	public static void main(String[] args) throws Exception {
		File[] files = new File(prefix).listFiles();
		for(File file : files) {
			testing(file);
		}
	}
	
	private static void testing(File file) throws Exception {
		System.out.println("Testing " + file.getName());
		AstTree ast_tree = parse(file);
		System.out.println("\t1. Parsing source code into AST.");
		CirTree cir_tree = parse(ast_tree);
		System.out.println("\t2. Parsing into C-like intermediate code");
		File output = new File(postfx + file.getName() + ".xml");
		output1(cir_tree, output);
		System.out.println("\t3. Output the C-IR program to xml file");
		output2(cir_tree, new File(postfx + file.getName() + ".ir"));
		System.out.println("\t4. Generate IR code for " + file.getName());
		output3(cir_tree, new File(postfx + file.getName() + ".fg"));
		System.out.println("\t5. Build up the execution flow graph for.");
		System.out.println();
	}
	private static AstTree parse(File file) throws Exception {
		return CTranslate.parse(file, ClangStandard.gnu_c89);
	}
	private static CirTree parse(AstTree ast_tree) throws Exception {
		return CTranslate.parse(ast_tree);
	}
	private static Element get_element(CirNode node) throws Exception {
		String typename = node.getClass().getSimpleName();
		typename = typename.substring(3, typename.length() - 4);
		Element element = new Element(typename);
		
		String code = "";
		if(node.get_ast_source() != null) {
			code = node.get_ast_source().get_location().trim_code(32);
		}
		element.setAttribute("code", code);
		
		Iterable<CirNode> children = node.get_children();
		for(CirNode child : children) {
			Element child_element = get_element(child);
			element.addContent(child_element);
		}
		
		return element;
	}
	private static void output1(CirTree cir_tree, File output) throws Exception {
		Element eroot = get_element(cir_tree.get_root());
		Document document = new Document(eroot);
		Format format = Format.getCompactFormat();
		format.setEncoding("utf-8"); format.setIndent("  ");
		XMLOutputter writer = new XMLOutputter(format);
		writer.output(document, new FileOutputStream(output));
	}
	private static void output2(CirTree cir_tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		writer.write(cir_tree.get_root().generate_code());
		writer.close();
	}
	private static void output3(CirTree cir_tree, File output) throws Exception {
		FileWriter writer = new FileWriter(output);
		
		CirFunctionCallGraph fun_graph = cir_tree.get_function_call_graph();
		Iterable<CirFunction> functions = fun_graph.get_functions();
		for(CirFunction function : functions) {
			writer.write("function " + function.get_name() + ":\n");
			
			CirExecutionFlowGraph flow_graph = function.get_flow_graph();
			for(int k = 1; k <= flow_graph.size(); k++) {
				CirExecution execution = flow_graph.get_execution(k%flow_graph.size());
				writer.write("[" + execution.get_id() + "]::");
				writer.write("<" + execution.get_type().toString() + ">\t");
				writer.write(execution.is_reachable() + "\t");
				writer.write(execution.get_statement().generate_code());
				writer.write("\n");
				
				Iterable<CirExecutionFlow> flows = execution.get_ou_flows();
				for(CirExecutionFlow flow : flows) {
					writer.write("\t-->[" + flow.get_type() + "::" + flow.is_reachable() + "]-->\t");
					CirExecution target = flow.get_target();
					writer.write(target.get_graph().get_function().get_name());
					writer.write("::[" + target.get_id() + "]");
					writer.write("\n");
				}
			}
			
			writer.write("\n");
		}
		
		writer.close();
	}
	
}
