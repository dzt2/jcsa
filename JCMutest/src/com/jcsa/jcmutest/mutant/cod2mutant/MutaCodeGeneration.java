package com.jcsa.jcmutest.mutant.cod2mutant;

import java.io.File;
import java.io.FileWriter;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.Mutant;
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

	/**
	 * @param mutation
	 * @return summary is used to identify the mutation being seeded
	 * @throws Exception
	 */
	private static String get_summary(AstMutation mutation) throws Exception {
		return "// " + mutation.toString() + "\n";
	}

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

	/**
	 * generate the mutation code in the specified file
	 * @param mutant
	 * @param type coverage | weak | strong | original
	 * @param file where the mutation code is produced
	 * @return
	 * @throws Exception
	 */
	public static String generate(Mutant mutant,
			MutationTestType type, File file) throws Exception {
		AstMutation mutation;
		switch(type) {
		case coverage:	mutation = mutant.get_coverage_mutant().get_mutation();	break;
		case weak:		mutation = mutant.get_weak_mutant().get_mutation();		break;
		case strong:	mutation = mutant.get_strong_mutant().get_mutation(); 	break;
		default: 		mutation = mutant.get_mutation(); 						break;
		}
		TextMutation text_mutation = MutationTextParsers.parse(mutation);

		String summary = get_summary(mutation);
		String head = get_head(
				mutant.get_id(), type, mutation, text_mutation.get_location());

		CText text = text_mutation.get_location().get_tree().get_source_code();
		AstNode location = text_mutation.get_location();
		String prefix = text.substring(0, location.get_location().get_bias());

		String postfix = text.substring(
				location.get_location().get_bias() +
				location.get_location().get_length(),
				text.length());
		postfix = add_comment(text_mutation.get_muta_code() + postfix);

		String code = summary + head + prefix + postfix;
		FileWriter writer = new FileWriter(file);
		writer.write(code); writer.close();

		return code;
	}

	/**
	 * generate the mutation code in the specified file
	 * @param mutant
	 * @param file where the mutation code is produced
	 * @return
	 * @throws Exception
	 */
	public static String generate(Mutant mutant, File file) throws Exception {
		return generate(mutant, MutationTestType.original, file);
	}

}
