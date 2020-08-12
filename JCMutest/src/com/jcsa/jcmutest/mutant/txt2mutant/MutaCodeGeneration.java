package com.jcsa.jcmutest.mutant.txt2mutant;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.text.CText;
import com.jcsa.jcparse.parse.code.CodeGeneration;

public class MutaCodeGeneration extends CodeGeneration {
	
	/** (id, mutation_test_type, mutation.toString(), location.line) **/
	private static final String header = "#include \"jcmutest.h\"\n\n"
			+ "/**\n"
			+ " * Mutant: %d\n"
			+ " * Type: %s\n"
			+ " * Mutation: %s\n"
			+ " * Line: %d\n"
			+ "**/\n\n";
	private static final String comment = "\t/**--- mutation --**/";
	
	private static String get_head(int mid, MutationTestType type, 
			AstMutation mutation, AstNode location) throws Exception {
		return String.format(header, mid, type.toString(), mutation.toString(), 
				location.get_location().line_of() + 10);
	}
	
	private static String add_comment(String code) throws Exception {
		StringBuilder buffer = new StringBuilder();
		boolean ins_comment = false;
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(!ins_comment && ch == '\n') {
				ins_comment = true;
				buffer.append(comment);
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}
	
	public static String generate(AstMutation mutation) throws Exception {
		TextMutation text_mutation = MutationTextParsers.parse(mutation);
		String head = get_head(-1, MutationTestType.strong, 
					mutation, text_mutation.get_location());
		
		CText text = text_mutation.get_location().get_tree().get_source_code();
		AstNode location = text_mutation.get_location();
		String prefix = text.substring(0, location.get_location().get_bias());
		
		String postfix = text.substring(
				location.get_location().get_bias() + 
				location.get_location().get_length(),
				text.length());
		postfix = add_comment(text_mutation.get_muta_code() + postfix);
		
		return head + prefix + postfix;
	}
	
	public static String generate(Mutant mutant, MutationTestType type,
			File file) throws Exception {
		AstMutation mutation;
		switch(type) {
		case coverage:	mutation = mutant.get_coverage_mutation();	break;
		case weak:		mutation = mutant.get_weak_mutation();		break;
		case strong:	mutation = mutant.get_strong_mutation(); 	break;
		default: 		mutation = mutant.get_mutation(); 			break;
		}
		TextMutation text_mutation = MutationTextParsers.parse(mutation);
		
		String head = get_head(mutant.
				get_id(), type, mutation, text_mutation.get_location());
		
		CText text = text_mutation.get_location().get_tree().get_source_code();
		AstNode location = text_mutation.get_location();
		String prefix = text.substring(0, location.get_location().get_bias());
		
		String postfix = text.substring(
				location.get_location().get_bias() + 
				location.get_location().get_length(),
				text.length());
		postfix = add_comment(text_mutation.get_muta_code() + postfix);
		
		String code = head + prefix + postfix;
		FileWriter writer = new FileWriter(file);
		writer.write(code); writer.close();
		
		return code;
	}
	
}
