import os
import random
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest
import com.jcsa.proc.feature as jcfeat


class RIPFPTMiner:
	"""
	It implements frequent pattern mining on RIP execution conditions (selected as True) features.
	"""

	def __init__(self):
		self.context = None
		self.solutions = set()
		return

	def __mine__(self, parent: jcfeat.RIPPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		self.context: jcfeat.RIPMineContext
		total, support, confidence = self.context.estimate(parent)
		length = len(parent.get_words())
		self.solutions.add(parent)
		if support >= self.context.get_min_support() and confidence <= self.context.get_max_confidence() and \
				length < self.context.get_max_length():
			for word in words:
				child = self.context.get_child(parent, word)
				if child != parent:
					self.__mine__(child, words)
		return

	def __solve__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return: good_patterns
		"""
		self.context: jcfeat.RIPMineContext
		words = execution.get_words()
		self.solutions.clear()
		for word in words:
			root = self.context.get_root(word)
			self.__mine__(root, words)
		return self.context.extract_good_patterns(self.solutions)

	def mine(self, context: jcfeat.RIPMineContext):
		"""
		:param context:
		:return:
		"""
		self.context = context
		root_executions = context.get_classifier().select(context.get_document().get_executions(), context.is_uk_or_cc())
		all_patterns = set()
		while len(root_executions) > 0:
			root_execution = jcbase.rand_select(root_executions)
			removed_executions = set()
			removed_executions.add(root_execution)
			good_patterns = self.__solve__(root_execution)
			for pattern in good_patterns:
				for execution in pattern.get_executions():
					removed_executions.add(execution)
			for execution in removed_executions:
				if execution in root_executions:
					root_executions.remove(execution)
			all_patterns = all_patterns | good_patterns
		return jcfeat.RIPPatternSpace(self.context.get_document(), self.context.get_classifier(), all_patterns)


def evaluate_results(space: jcfeat.RIPPatternSpace, output_directory: str, name: str, exe_or_mut: bool, uk_or_cc: bool):
	writer = jcfeat.RIPPatternWriter()
	writer.write_evaluate(space, os.path.join(output_directory, name + ".sum"))
	writer.write_matching(space, os.path.join(output_directory, name + ".bpt"), exe_or_mut, uk_or_cc)
	writer.write_patterns(space.get_subsuming_patterns(True), os.path.join(output_directory, name + ".mpt"))
	return


def get_rip_document(directory: str, file_name: str, output_directory: str):
	document = jctest.CDocument(directory, file_name)
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	return document


def do_frequent_mine(document: jctest.CDocument, tests, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
					 min_confidence: float, max_confidence: float, max_length: int, output_directory: str):
	miner = RIPFPTMiner()
	output_directory.strip()
	context = jcfeat.RIPMineContext(document, exe_or_mut, uk_or_cc, min_support,
									min_confidence, max_confidence, max_length, tests)
	return miner.mine(context)


def testing(inputs_directory: str, output_directory: str, model_name: str,
			exe_or_mut: bool, uk_or_cc: bool, min_support: int, min_confidence: float,
			max_confidence: float, max_length: int, select, do_mining):
	"""
	:param inputs_directory:
	:param output_directory:
	:param model_name:
	:param exe_or_mut:
	:param uk_or_cc:
	:param min_support:
	:param min_confidence:
	:param max_confidence:
	:param max_length:
	:param select: True to select tests, False to use all the tests
	:param do_mining:
	:return:
	"""
	output_directory = os.path.join(output_directory, model_name)
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	for file_name in os.listdir(inputs_directory):
		print("Testing on", file_name)
		# Step-I. Load features from data files
		document = get_rip_document(os.path.join(inputs_directory, file_name), file_name, output_directory)
		evaluation = jcmuta.MutationTestEvaluation(document.project)
		selected_mutants = evaluation.select_mutants_by_classes(["STRP", "BTRP"])
		selected_tests = evaluation.select_tests_for_mutants(selected_mutants)
		selected_tests = selected_tests | evaluation.select_tests_for_random(30)
		print("\t(1) Load", len(document.get_executions()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.conditions.get_words()), "words of symbolic conditions.")
		print("\t\t==>Select", len(selected_tests), "test cases with",
			  evaluation.measure_score(document.get_mutants(), selected_tests), "of mutation score.")
		# Step-II. Perform pattern mining algorithms
		if select:
			tests = selected_tests
		else:
			tests = None
		space = do_mining(document=document, exe_or_mut=exe_or_mut, uk_or_cc=uk_or_cc, min_support=min_support,
						  min_confidence=min_confidence, max_confidence=max_confidence, max_length=max_length,
						  output_directory=output_directory, tests=tests)
		space: jcfeat.RIPPatternSpace
		print("\t(2) Generate", len(space.get_patterns()), "patterns with",
			  len(space.get_subsuming_patterns(False)), "subsuming ones.")
		# Step-III. Evaluate the performance of mining results
		evaluate_results(space, output_directory, file_name, exe_or_mut, uk_or_cc)
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Data/"
	print("Testing start from here.")
	testing(prev_path, post_path, "frequent_mine_s1", True, True, 2, 0.70, 0.90, 1, True, 	do_frequent_mine)
	testing(prev_path, post_path, "frequent_mine_a1", True, True, 2, 0.70, 0.90, 1, False, 	do_frequent_mine)
	print("Testing end for all.")

