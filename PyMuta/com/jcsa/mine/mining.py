"""This file implements the mining algorithm for finding good patterns from mutation testing."""


import os
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest
import pydotplus
from sklearn import tree
from sklearn import metrics
from scipy import sparse
import com.jcsa.mine.feature as jcfeature


class RIPFPMiner:
	"""
	It implements the pattern mining using frequent pattern mining.
	"""

	def __init__(self):
		self.middle = None
		return

	def __mine__(self, parent: jcfeature.RIPPattern, words: list):
		"""
		:param parent:
		:param words:
		:return:
		"""
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		length, support, confidence = self.middle.estimate(parent)
		if length < inputs.get_max_length() and support >= inputs.get_min_support() and \
				confidence <= inputs.get_max_confidence():
			for k in range(0, len(words)):
				word = words[k].strip()
				child = self.middle.get_child(parent, word)
				if child != parent:
					self.__mine__(child, words[k + 1:])
		return

	def mine(self, inputs: jcfeature.RIPMineInputs):
		"""
		:param inputs:
		:return: RIPMineOutput
		"""
		# 1. collect all the words w.r.t. supporting class of sequences
		support_sequences = inputs.get_classifier().select(inputs.get_document().get_sequences(),
														   inputs.get_support_class())
		support_words = set()
		for support_sequence in support_sequences:
			support_sequence: jctest.SymSequence
			for support_word in support_sequence.get_words():
				support_word: str
				support_words.add(support_word.strip())
		words = list()
		for support_word in support_words:
			if len(support_word) > 0:
				words.append(support_word)

		# 2. ready to mine within global words library
		self.middle = jcfeature.RIPMineMiddle(inputs)
		print("\t\t*-- Frequent pattern mining over", len(words), "symbolic words.")
		for i in range(0, len(words)):
			word = words[i]
			root = self.middle.get_root(word)
			self.__mine__(root, words[i + 1:])

		# 3. generate good patterns for producing outputs
		good_patterns = self.middle.extract_good_patterns()
		return jcfeature.RIPMineOutput(inputs, good_patterns)


def do_frequent_mining(mine_inputs: jcfeature.RIPMineInputs, output_directory: str):
	"""
	:param mine_inputs:
	:param output_directory:
	:return: Perform frequent pattern mining algorithm
	"""
	output_directory.strip()
	miner = RIPFPMiner()
	return miner.mine(mine_inputs)


class RIPDTMiner:
	"""
	It implements the pattern mining via decision tree model
	"""

	def __init__(self):
		self.classifier = None
		self.middle = None
		self.X = None
		self.Y = list()
		self.W = list()
		return

	def __input_context__(self, inputs: jcfeature.RIPMineInputs):
		"""
		:param inputs:
		:return:	(1) update context information
					(2) update X, Y, W to train the decision tree
		"""
		self.middle = jcfeature.RIPMineMiddle(inputs)
		D = dict()
		self.Y.clear()
		self.W.clear()
		for sequence in inputs.get_document().get_sequences():
			sequence: jctest.SymSequence
			total, support, confidence = inputs.get_classifier().estimate([sequence], inputs.get_support_class())
			if support > 0:
				self.Y.append(1)
			else:
				self.Y.append(0)
			for word in sequence.get_words():
				if not (word in D):
					D[word] = len(self.W)
					self.W.append(word)
		rows, columns, dataset = list(), list(), list()
		line = 0
		for execution in inputs.get_document().get_sequences():
			execution: jctest.SymSequence
			execution_words = set()
			for instance in execution.get_executions():
				instance: jctest.SymExecution
				for word in instance.get_words():
					execution_words.add(str(word))
			for word in execution_words:
				column = D[word]
				rows.append(line)
				columns.append(column)
				dataset.append(1)
			line += 1
		self.X = sparse.coo_matrix((dataset, (rows, columns)),
								   shape=(len(inputs.get_document().get_sequences()), len(self.W)))
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
		self.middle: jcfeature.RIPMineMiddle
		WN = list()
		document = self.middle.inputs.get_document()
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
			WN.append(RIPDTMiner.__normalize__(norm_word))
		return WN

	def __fit_decisions__(self, tree_file: str):
		"""
		:param tree_file:
		:return: create a classifier and training it using the context data and return the predicted results
		"""
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		self.classifier = tree.DecisionTreeClassifier(min_samples_leaf=inputs.get_min_support())
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
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		self.classifier: tree.DecisionTreeClassifier
		leaf_path = dict()	# exec_id --> leaf_id, node_path
		X_array = self.X.toarray()
		node_indicators = self.classifier.decision_path(X_array)
		leave_ids = self.classifier.apply(X_array)
		for exec_id in range(0, len(inputs.get_document().get_sequences())):
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
		self.middle: jcfeature.RIPMineMiddle
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
			pattern = self.middle.get_root("")
			for word in words:
				pattern = self.middle.get_child(pattern, word)
				patterns.add(pattern)
			patterns.add(pattern)
		return patterns

	def mine(self, inputs: jcfeature.RIPMineInputs, tree_path: str):
		self.__input_context__(inputs)
		YP = self.__fit_decisions__(tree_path)
		leaf_path = self.__get_leaf_path__(YP)
		patterns = self.__path_patterns__(leaf_path)
		self.middle: jcfeature.RIPMineMiddle
		good_patterns = self.middle.extract_good_patterns()
		return jcfeature.RIPMineOutput(inputs, good_patterns)


def do_decision_mining(mine_inputs: jcfeature.RIPMineInputs, output_directory: str):
	"""
	:param mine_inputs:
	:param output_directory:
	:return: Perform decision tree based pattern mining algorithm
	"""
	miner = RIPDTMiner()
	filename = mine_inputs.get_document().get_program().name + ".pdf"
	return miner.mine(mine_inputs, os.path.join(output_directory, filename))


# main procedure methods


def new_model_name(model_name: str, sample_class, support_class):
	if sample_class == jcfeature.MU_SAMPLE_CLASS:
		sample_class = "mut"
	elif sample_class == jcfeature.SQ_SAMPLE_CLASS:
		sample_class = "seq"
	elif sample_class == jcfeature.EX_SAMPLE_CLASS:
		sample_class = "exe"
	else:
		sample_class = "xxx"
	if support_class == jcfeature.UK_SUPPORT_CLASS:
		support_class = "unk"
	elif support_class == jcfeature.WC_SUPPORT_CLASS:
		support_class = "wcc"
	elif support_class == jcfeature.SC_SUPPORT_CLASS:
		support_class = "scc"
	else:
		support_class = "yyy"
	return "{}_{}_{}".format(model_name, sample_class, support_class)


def do_mining(document: jctest.CDocument, output_directory: str, model_name: str,
			  sample_class, support_class, used_tests, max_length: int,
			  min_support: int, min_confidence: float, max_confidence: float,
			  do_model_mining):
	"""
	:param document:			It provides original and entire dataset for being mined.
	:param output_directory:	The directory where output-directory is created for.
	:param model_name:			The output files are preserved in output_directory/model_name
	:param sample_class:		MU_SAMPLE_CLASS, SQ_SAMPLE_CLASS, EX_SAMPLE_CLASS
	:param support_class:		UK_SUPPORT_CLASS, WC_SUPPORT_CLASS, SC_SUPPORT_CLASS
	:param used_tests:			The set of test cases assumed to be used during testing.
	:param max_length:			The maximal length of generated patterns for presentation.
	:param min_support:			The minimal number of supporting samples required for each pattern.
	:param min_confidence:		The minimal confidence achieved by the good patterns produced.
	:param max_confidence:		The maximal confidence achieved to terminate the searching on tree.
	:param do_model_mining:		do_frequent_mining, do_decision_mining, ...
	:return:
	"""
	model_name = new_model_name(model_name, sample_class, support_class)
	print("Do Mining on", document.get_program().name, "for model of", model_name)
	# 1. create inputs of mining algorithm
	inputs = jcfeature.RIPMineInputs(document, used_tests, sample_class, support_class,
									 max_length, min_support, min_confidence, max_confidence)
	print("\t\t1. Create data mining inputs for", inputs.get_document().get_program().name)
	print("\t\t*-- parameters: {", inputs.get_sample_class(), inputs.get_support_class(), inputs.get_max_length(),
		  inputs.get_min_support(), inputs.get_min_confidence(), inputs.get_max_confidence())

	# 2. perform data mining algorithms on
	if not os.path.exists(output_directory):
		os.mkdir(output_directory)
	output_directory = os.path.join(output_directory, model_name)
	if not os.path.exists(output_directory):
		os.mkdir(output_directory)
	output = do_model_mining(mine_inputs=inputs, output_directory=output_directory)
	output: jcfeature.RIPMineOutput
	print("\t\t2. Produce", len(output.get_patterns()), "good patterns from", inputs.get_document().get_program().name)

	# 3. Write the pattern outputs to specified directory
	writer = jcfeature.RIPMineWriter()
	writer.write_to(output, output_directory)
	print("\t\t3. Consume", len(output.get_subsuming_patterns(False)), "subsuming patterns to", output_directory)
	return


def do_mining_on_classes(document: jctest.CDocument, output_directory: str, model_name: str,
						 used_tests, max_length: int, min_support: int, min_confidence: float,
						 max_confidence: float, do_model_mining):
	"""
	:param document:
	:param output_directory:
	:param model_name:
	:param used_tests:
	:param max_length:
	:param min_support:
	:param min_confidence:
	:param max_confidence:
	:param do_model_mining:
	:return:
	"""
	do_mining(document, output_directory, model_name, jcfeature.MU_SAMPLE_CLASS, jcfeature.UK_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.MU_SAMPLE_CLASS, jcfeature.WC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.MU_SAMPLE_CLASS, jcfeature.SC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.SQ_SAMPLE_CLASS, jcfeature.UK_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.SQ_SAMPLE_CLASS, jcfeature.WC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.SQ_SAMPLE_CLASS, jcfeature.SC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	return


def do_testing(inputs_directory: str, output_directory: str, select_tests: bool):
	"""
	:param inputs_directory:
	:param output_directory:
	:param select_tests: True to use randomly selected test suite for classifier
	:return:
	"""
	for file_name in os.listdir(inputs_directory):
		c_document = jctest.CDocument(os.path.join(inputs_directory, file_name), file_name)
		print("Perform testing for", file_name, "with", len(c_document.get_mutants()), "mutants and",
			  len(c_document.get_project().test_space.get_test_cases()), "test cases annotated with",
			  len(c_document.get_conditions_lib().get_all_conditions()), "symbolic conditions used.")

		if select_tests:
			evaluator = jcmuta.MutationTestEvaluation(c_document.get_project())
			selected_mutants = evaluator.select_mutants_by_classes(["STRP", "BTRP"])
			mutation_tests = evaluator.select_tests_for_mutants(selected_mutants)
			random_number = int(len(c_document.get_project().test_space.get_test_cases()) * 0.01)
			random_tests = evaluator.select_tests_for_random(random_number)
			used_tests = random_tests | mutation_tests
			score = evaluator.measure_score(None, used_tests)
			print("\t==> Select", len(used_tests), "test cases with", int(score * 1000000) / 10000.0, "score.")
		else:
			used_tests = None
			print("\t==> Select all the possible test inputs from mutation test project...")

		# mining on all classes
		do_mining_on_classes(c_document, output_directory, "dtm", used_tests, 32, 2, 0.70, 0.95, do_decision_mining)
		do_mining_on_classes(c_document, output_directory, "fpm", used_tests, 1,  2, 0.70, 0.95, do_frequent_mining)
		print()
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Data/patterns"
	do_testing(prev_path, post_path, False)

