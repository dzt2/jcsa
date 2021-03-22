"""
This file implements the data model of patterns and mining algorithms used.
"""


import os
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest
import pydotplus
from sklearn import tree
from sklearn import metrics
from scipy import sparse
from com.jcsa.proc.feature import RIPPattern
from com.jcsa.proc.feature import RIPMineContext
from com.jcsa.proc.feature import RIPPatternWriter
from com.jcsa.proc.feature import RIPPatternSpace


class RIPFPTMiner:
	"""
	It implements frequent pattern mining on RIP execution conditions (selected as True) features.
	"""

	def __init__(self):
		self.context = None
		return

	def __mine__(self, parent: RIPPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		self.context: RIPMineContext
		total, support, confidence = self.context.estimate(parent)
		length = len(parent.get_words())
		if support >= self.context.get_min_support() and confidence <= self.context.get_max_confidence() and length < self.context.get_max_length():
			for word in words:
				child = self.context.get_child(parent, word)
				if child != parent:
					self.__mine__(child, words)
		return

	def mine(self, context: RIPMineContext):
		"""
		:param context:
		:return:
		"""
		self.context = context
		root_executions = context.get_classifier().select(context.get_document().get_processes(), context.is_uk_or_cc())
		for root_execution in root_executions:
			root_execution: jctest.SymExecution
			for instance in root_execution.get_instances():
				instance: jctest.SymInstance
				words = instance.get_words()
				for word in words:
					root = context.get_root(word)
					self.__mine__(root, words)
		good_patterns = self.context.extract_good_patterns()
		return RIPPatternSpace(self.context.get_document(), self.context.get_classifier(), good_patterns)


class RIPDTTMiner:
	"""
	It implements the pattern mining using decision tree model
	"""

	def __init__(self):
		self.classifier = None
		self.context = None
		self.X = None
		self.Y = list()
		self.W = list()
		return

	def __input_context__(self, context: RIPMineContext):
		"""
		:param context:
		:return:	(1) update context information
					(2) update X, Y, W to train the decision tree
		"""
		self.context = context
		D = dict()
		self.Y.clear()
		self.W.clear()
		for execution in self.context.get_document().get_processes():
			execution: jctest.SymExecution
			if self.context.is_exe_or_mut():
				sample = execution
			else:
				sample = execution.get_mutant()
			total, support, confidence = self.context.get_classifier().estimate([sample], self.context.is_uk_or_cc())
			if support > 0:
				self.Y.append(1)
			else:
				self.Y.append(0)
			for instance in execution.get_instances():
				instance: jctest.SymInstance
				for word in instance.get_words():
					if not (word in D):
						D[word] = len(self.W)
						self.W.append(word)
		rows, columns, dataset = list(), list(), list()
		line = 0
		for execution in self.context.get_document().get_processes():
			execution_words = set()
			for instance in execution.get_instances():
				instance: jctest.SymInstance
				for word in instance.get_words():
					execution_words.add(str(word))
			for word in execution_words:
				column = D[word]
				rows.append(line)
				columns.append(column)
				dataset.append(1)
			line += 1
		self.X = sparse.coo_matrix((dataset, (rows, columns)),
								   shape=(len(self.context.get_document().get_processes()), len(self.W)))
		return

	@staticmethod
	def __normalize__(text: str):
		new_text = ""
		for k in range(0, len(text)):
			char = text[k]
			if char in ['{', '}', '\"']:
				char = ' '
			new_text += char
		return new_text

	def __gen_normal_WN__(self):
		"""
		:return: sequence of normalized words to describe the RIP conditions.
		"""
		self.context: RIPMineContext
		WN = list()
		document = self.context.get_document()
		for word in self.W:
			condition = document.conditions.get_condition(word)
			category = condition.get_category()
			operator = condition.get_operator()
			execution = condition.get_execution()
			location = condition.get_location().get_cir_code()
			if condition.get_parameter() is None:
				parameter = "null"
			else:
				parameter = condition.get_parameter().get_code()
			norm_word = "[{}, {}, {}, \"{}\", {}]".format(category, operator, execution, location, parameter)
			WN.append(RIPDTTMiner.__normalize__(norm_word))
		return WN

	def __fit_decisions__(self, tree_file: str):
		"""
		:param tree_file:
		:return: create a classifier and training it using the context data and return the predicted results
		"""
		self.context: RIPMineContext
		self.classifier = tree.DecisionTreeClassifier(min_samples_leaf=self.context.get_min_support())
		self.classifier.fit(self.X, self.Y)
		YP = self.classifier.predict(self.X)
		print(metrics.classification_report(self.Y, YP, target_names=["Killable", "Equivalent"]))
		if not(tree_file is None):
			W = self.__gen_normal_WN__()
			dot_data = tree.export_graphviz(self.classifier, out_file=None, feature_names=W,
											class_names=["Killable", "Equivalent"])
			graph = pydotplus.graph_from_dot_data(dot_data)
			graph.write_pdf(tree_file)
		return YP

	def __get_leaf_path__(self, YP):
		"""
		:return: selecting leaf that decides type as equivalent and their corresponding path in the program
		"""
		self.context: RIPMineContext
		self.classifier: tree.DecisionTreeClassifier
		leaf_path = dict()	# exec_id --> leaf_id, node_path
		X_array = self.X.toarray()
		node_indicators = self.classifier.decision_path(X_array)
		leave_ids = self.classifier.apply(X_array)
		for exec_id in range(0, len(self.context.get_document().get_processes())):
			if YP[exec_id] == 1:
				leaf_node_id = leave_ids[exec_id]
				node_index = node_indicators.indices[
							 node_indicators.indptr[exec_id]:
							 node_indicators.indptr[exec_id + 1]]
				leaf_path[exec_id] = (leaf_node_id, node_index)
		return leaf_path

	def __path_patterns__(self, leaf_path: dict):
		"""
		:param leaf_path:
		:return: leaf_node_id, node_id_list
		"""
		self.context: RIPMineContext
		X = self.X.toarray()
		patterns = set()
		features = self.classifier.tree_.feature
		thresholds = self.classifier.tree_.threshold
		for exec_id, value in leaf_path.items():
			words = list()
			leaf_id = value[0]
			node_path = value[1]
			for node_id in node_path:
				if node_id != leaf_id:
					word = self.W[features[node_id]]
					word: str
					if X[exec_id, features[node_id]] > thresholds[node_id]:
						words.append(word)		# select True-branch words
			pattern = self.context.get_root("")
			for word in words:
				pattern = self.context.get_child(pattern, word)
				patterns.add(pattern)
			patterns.add(pattern)
		return patterns

	def mine(self, context: RIPMineContext, tree_path: str):
		self.__input_context__(context)
		YP = self.__fit_decisions__(tree_path)
		leaf_path = self.__get_leaf_path__(YP)
		patterns = self.__path_patterns__(leaf_path)
		good_patterns = context.extract_good_patterns(patterns)
		return RIPPatternSpace(self.context.get_document(), self.context.get_classifier(), good_patterns)


def evaluate_results(space: RIPPatternSpace, output_directory: str, name: str, exe_or_mut: bool, uk_or_cc: bool):
	writer = RIPPatternWriter()
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
	context = RIPMineContext(document, exe_or_mut, uk_or_cc, min_support,
							 min_confidence, max_confidence, max_length, tests)
	return miner.mine(context)


def do_decision_mine(document: jctest.CDocument, tests, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
					 min_confidence: float, max_confidence: float, max_length: int, output_directory: str):
	miner = RIPDTTMiner()
	context = RIPMineContext(document, exe_or_mut, uk_or_cc, min_support,
							 min_confidence, max_confidence, max_length, tests)
	name = document.project.program.name
	return miner.mine(context, os.path.join(output_directory, name + ".pdf"))


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
		print("\t(1) Load", len(document.get_processes()), "lines of", len(document.get_mutants()),
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
		space: RIPPatternSpace
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
	testing(prev_path, post_path, "decision_tree_sn", True, True, 2, 0.70, 0.95, 8, True, 	do_decision_mine)
	testing(prev_path, post_path, "decision_tree_an", True, True, 2, 0.70, 0.95, 8, False, 	do_decision_mine)
	testing(prev_path, post_path, "frequent_mine_sn", True, True, 2, 0.70, 0.90, 1, True, 	do_frequent_mine)
	testing(prev_path, post_path, "frequent_mine_an", True, True, 2, 0.70, 0.90, 1, False, 	do_frequent_mine)
	print("Testing end for all.")

