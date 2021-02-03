"""
This file implemnets frequent pattern mining + decision tree algorithms to classify killable and unkilled testing.
"""


import os
import com.jcsa.libs.muta as jcmuta
import com.jcsa.mine.pattern as jcmpat


def select_root_patterns(factory: jcmpat.RIPPatternFactory, min_support: int, min_confidence: float):
	"""
	:param factory:
	:param min_support: minimal number of supports
	:param min_confidence: minimal confidence need
	:return: set of patterns (root) being selected with specified estimation metrics
	"""
	good_executions = factory.get_classifier().select(factory.get_document().get_executions(), factory.uk_or_cc)
	good_patterns = set()
	for good_execution in good_executions:
		good_execution: jcmuta.RIPExecution
		words = good_execution.get_words()
		for word in words:
			root_pattern, total, support, confidence = factory.get_pattern(None, word, True)
			if support >= min_support and confidence >= min_confidence:
				good_patterns.add(root_pattern)
	return jcmpat.RIPPatternSpace(factory.get_document(), factory.get_classifier(), good_patterns)


def __all_words_in__(pattern: jcmpat.RIPPattern):
	words = set()
	for execution in pattern.get_executions():
		execution: jcmuta.RIPExecution
		for word in execution.get_words():
			word: str
			words.add(word)
	return words


def __extend_on_pattern__(factory: jcmpat.RIPPatternFactory, parent: jcmpat.RIPPattern, words,
						  min_support: int, max_confidence: float, max_depth: int,
						  patterns: set, tree: set):
	"""
	:param factory:
	:param parent:
	:param words:
	:param min_support:
	:param max_confidence:
	:param max_depth:
	:param patterns:
	:return:
	"""
	if len(parent) < max_depth:
		for word in words:
			child, total, support, confidence = factory.get_pattern(parent, word, True)
			if child != parent and not(child in patterns):
				patterns.add(child)
				if support >= min_support and confidence <= max_confidence:
					tree.add(child)
					__extend_on_pattern__(factory, child, words, min_support, max_confidence, max_depth, patterns, tree)
	return


def extend_pattern_tree(root_pattern: jcmpat.RIPPattern, min_support: int, max_confidence: float,
						max_depth: int, factory: jcmpat.RIPPatternFactory):
	"""
	:param factory:
	:param root_pattern:
	:param min_support:
	:param max_confidence:
	:param max_depth:
	:return: set of patterns extended from the root
	"""
	tree = set()
	tree.add(root_pattern)
	words = __all_words_in__(root_pattern)
	__extend_on_pattern__(factory, root_pattern, words, min_support, max_confidence, max_depth, set(), tree)
	return tree


def mining_patterns(document: jcmuta.RIPDocument, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
					min_confidence: float, max_confidence: float, max_depth: int, output_directory: str):
	"""
	:param document: it provides lines and mutations in the program
	:param exe_or_mut: true to take line as sample or false to take mutant as sample
	:param uk_or_cc: true to estimate on non-killed samples or false on coincidental correctness samples
	:param min_support: minimal number of samples supporting the patterns
	:param min_confidence: minimal confidence to filter root patterns
	:param max_confidence: maximal confidence once achieved to stop the pattern generation
	:param max_depth: maximal length of the patterns allowed to generate
	:param output_directory: directory where the output files are preserved
	:return:
	"""
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	if len(document.get_executions()) > 0:
		print("Testing on", document.get_project().program.name)
		print("\t(1) Load", len(document.get_executions()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.get_corpus()), "words.")

		factory = jcmpat.RIPPatternFactory(document, jcmpat.RIPClassifier(), exe_or_mut, uk_or_cc)
		root_patterns_space = select_root_patterns(factory, min_support, min_confidence)
		root_writer = jcmpat.RIPPatternWriter()
		root_writer.write_patterns(root_patterns_space.get_subsuming_patterns(),
								   os.path.join(output_directory, document.get_project().program.name + ".mpt"))
		root_writer.write_matching(root_patterns_space,
								   os.path.join(output_directory, document.get_project().program.name + ".bpt"),
								   exe_or_mut, uk_or_cc)
		root_writer.write_evaluate(root_patterns_space,
								   os.path.join(output_directory, document.get_project().program.name + ".sum"))
		print("\t(2) Generate", len(root_patterns_space.get_subsuming_patterns()), "roots from program under test.")

		index = 0
		for root_pattern in root_patterns_space.get_subsuming_patterns():
			tree = extend_pattern_tree(root_pattern, min_support, max_confidence, max_depth, factory)
			index += 1
			print("\t\tGenerate", len(tree), "patterns for root_pattern#", index)
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def testing_project(directory: str, file_name: str,
					t_value, f_value, n_value,
					exe_or_mut: bool,
					uk_or_cc: bool,
					stat_directory: str,
					dyna_directory):
	c_project = jcmuta.CProject(directory, file_name)

	docs = c_project.load_static_document(directory, t_value, f_value, n_value)
	mining_patterns(docs, exe_or_mut, uk_or_cc, 2, 0.70, 0.90, 3, os.path.join(stat_directory))

	if not(dyna_directory is None):
		docs = c_project.load_dynamic_document(directory, t_value, f_value, n_value)
		mining_patterns(docs, exe_or_mut, uk_or_cc, 20, 0.80, 0.95, 3, os.path.join(dyna_directory))
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	stat_path = "/home/dzt2/Development/Data/patterns/stat"
	dyna_path = None
	# dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for filename in os.listdir(prev_path):
		direct = os.path.join(prev_path, filename)
		testing_project(directory=direct, file_name=filename, t_value=True, f_value=False, n_value=True,
						exe_or_mut=True, uk_or_cc=True, stat_directory=stat_path, dyna_directory=dyna_path)

