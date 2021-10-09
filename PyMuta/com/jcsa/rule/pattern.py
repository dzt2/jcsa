""" This file defines data model and interfaces for creating, evaluating and visualizing state difference pattern. """


import os
from collections import deque
from typing import TextIO
import pydotplus
import graphviz
from sklearn import metrics
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.test as jctest
import com.jcsa.rule.encode as jecode
import sklearn.tree as sktree


## pattern model


class StateDifferenceTree:
	"""
	It denotes the hierarchical structure to uniquely define the state difference patterns in the program.
	"""

	def __init__(self, document: jecode.MerDocument):
		"""
		:param document: the document of dataset to encode mutation test project dataset.
		"""
		self.document = document
		self.root = StateDifferenceTreeNode(self, None, -1)
		return

	def get_document(self):
		"""
		:return: the document of dataset to encode mutation test project dataset.
		"""
		return self.document

	def get_root(self):
		return self.root

	def get_child(self, parent, feature: int):
		"""
		:param parent: 	the unique parent node in the tree
		:param feature:
		:return: the child of the parent using input feature
		"""
		if parent is None:
			parent = self.root
		else:
			parent: StateDifferenceTreeNode
			parent = self.get_node(parent.get_pattern().get_features())
		return parent.new_child(feature)

	def get_node(self, features):
		"""
		:param features: the set of integer features to encode the node of pattern being generated
		:return: the unique tree node of which pattern is specified by the input features sequence.
		"""
		feature_list = self.document.anto_space.normal(features)
		node = self.root
		for feature in feature_list:
			node = node.new_child(feature)
		return node

	def reset_state(self):
		"""
		:return: it re-initializes the tree by rebuilding its root and removing the previous existing ones.
		"""
		self.root = StateDifferenceTreeNode(self, None, -1)
		return


class StateDifferenceTreeNode:
	"""
	It denotes a node created in StateDifferenceTree to uniquely specify a state difference pattern in context.
	"""

	def __init__(self, tree: StateDifferenceTree, parent, feature: int):
		"""
		:param tree: 	the state difference pattern tree to define this node in the program uniquely
		:param parent: 	the parent node of this node or None if the node is a root without any parent
		:param feature: the integer feature of CirAnnotation being annotated on the edge to this node
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.feature = -1
		else:
			self.parent = parent
			self.feature = feature
		self.children = list()
		self.pattern = StateDifferencePattern(self)
		return

	def get_tree(self):
		"""
		:return: the state difference pattern tree to define this node in the program uniquely
		"""
		return self.tree

	def get_feature(self):
		"""
		:return: the integer feature of CirAnnotation being annotated on the edge to this node
		"""
		return self.feature

	def is_root(self):
		"""
		:return: whether the node is a root without any parent in the tree
		"""
		if self.parent is None:
			return True
		return False

	def get_parent(self):
		"""
		:return: the parent node of this node or None if the node is a root without any parent
		"""
		if self.parent is None:
			return None
		else:
			self.parent: StateDifferenceTreeNode
			return self.parent

	def is_leaf(self):
		"""
		:return: whether the node is a leaf without any child
		"""
		return len(self.children) == 0

	def get_children(self):
		"""
		:return: the set of child nodes that are extended from this node to represent refined patterns.
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children created under this node or 0 if the node is a leaf
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k: [0, n - 1] where n = self.number_of_children()
		:return: the kth child created under this node
		"""
		child = self.children[k]
		child: StateDifferenceTreeNode
		return child

	def get_pattern(self):
		"""
		:return: the state difference pattern that is uniquely specified by this tree node.
		"""
		return self.pattern

	def new_child(self, feature: int):
		"""
		:param feature: the integer encoding CirAnnotation that is denoted on edge from this node to its new child.
		:return: It will create or return an existing child from the current node w.r.t. the given feature on edge.
					(1) if feature <= self.feature, then return the current node itself;
					(2)	if some child's feature matches with the input, return the existing one;
					(3) otherwise, create a new child using the given feature as edge annotation.
		"""
		if feature <= self.feature:
			return self
		else:
			for child in self.children:
				child: StateDifferenceTreeNode
				if child.feature == feature:
					return child
			child = StateDifferenceTreeNode(self.tree, self, feature)
			self.children.append(child)
			return child


class StateDifferencePattern:
	"""
	It defines the state difference pattern using a feature vector of CirAnnotation.
	"""

	def __init__(self, node: StateDifferenceTreeNode):
		"""
		:param node: the state difference pattern tree node to uniquely define this pattern
		"""
		self.document = node.get_tree().get_document()
		self.features = self.__new_features__(node)
		self.executions = self.__new_data_set__(node)
		return

	def __new_features__(self, node: StateDifferenceTreeNode):
		"""
		:param node:
		:return: it generates the unique sorted list of features to define the pattern.
		"""
		features = set()
		while not node.is_root():
			features.add(node.get_feature())
			node = node.get_parent()
		return self.document.anto_space.normal(features)

	def __matched_with__(self, execution: jecode.MerExecution):
		"""
		:param execution:
		:return: whether the execution matches with this state difference pattern
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __new_data_set__(self, node: StateDifferenceTreeNode):
		"""
		:param node:
		:return: it generates the set of executions matching with this pattern under the node
		"""
		if node.is_root():
			parent_executions = self.document.exec_space.get_executions()
		else:
			parent_executions = node.get_parent().get_pattern().executions
		executions = set()
		for execution in parent_executions:
			execution: jecode.MerExecution
			if self.__matched_with__(execution):
				executions.add(execution)
		return executions

	## getters

	def get_document(self):
		"""
		:return: the document where the state difference pattern is defined on
		"""
		return self.document

	def get_features(self):
		"""
		:return: the feature vector that uniquely defines this state patterns
		"""
		return self.features

	def get_annotations(self):
		"""
		:return: the set of annotations encoded by this pattern's feature vector.
		"""
		return self.document.anto_space.decode(self.features)

	def get_executions(self):
		"""
		:return: the set of executions that match with the state difference pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which executions match with this pattern.
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def has_samples(self, key):
		"""
		:param key: either MerMutant or MerExecution
		:return:
		"""
		for execution in self.executions:
			if execution == key:
				return True
			elif execution.get_mutant() == key:
				return True
			else:
				continue
		return False

	## evaluate

	def __len__(self):
		"""
		:return: the length of feature vector
		"""
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def predict(self, used_tests):
		"""
		:param used_tests: the set of test cases to decide of which executions are killed in the pattern context.
		:return: result (killed or not), killed, alive, confidence (of prediction made)
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			if execution.get_mutant().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		if killed >= alive:
			result = True
		else:
			result = False
		total = killed + alive
		if total == 0:
			confidence = 0.0
		else:
			confidence = max(killed, alive) / total
		return result, killed, alive, confidence

	def evaluate(self, used_tests):
		"""
		:param used_tests: the set of test cases to decide of which executions are killed in the pattern context.
		:return: length, support (number of undetected mutants), confidence (support / len(executions))
		"""
		result, killed, alive, _ = self.predict(used_tests)
		length, support, confidence = len(self), alive, 0.0
		if support > 0:
			confidence = support / (killed + alive)
		return length, support, confidence


## inputs modules


class StateDifferenceMineInputs:
	"""
	The module of inputs to pattern mining of state difference.
	"""

	def __init__(self, document: jecode.MerDocument, max_length: int,
				 min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: 		the document to encode the data source
		:param max_length: 		the maximal length of patterns allowed
		:param min_support: 	the minimal support for patterns used
		:param min_confidence: 	the minimal confidence of rule allowed
		:param max_confidence: 	the maximal confidence to stop generation
		"""
		self.document = document
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		return

	def get_document(self):
		return self.document

	def get_max_length(self):
		return self.max_length

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_middle_module(self):
		"""
		:return: it creates a middle module used for mining directly
		"""
		return StateDifferenceMineMiddle(self)


class StateDifferenceMineMiddle:
	"""
	This module manages the construction and selection of state difference patterns from a cache tree.
	"""

	def __init__(self, inputs: StateDifferenceMineInputs):
		self.inputs = inputs
		self.p_tree = StateDifferenceTree(self.inputs.get_document())
		return

	## tree getters

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_pattern_tree(self):
		return self.p_tree

	def get_root(self):
		return self.p_tree.get_root()

	def get_child(self, parent: StateDifferenceTreeNode, feature: int):
		return self.p_tree.get_child(parent, feature)

	def get_node(self, features):
		return self.p_tree.get_node(features)

	## filter method

	def __inputs__(self, patterns):
		"""
		:param patterns: the set of patterns from which are selected or None to select within the current tree
		:return:
		"""
		input_patterns = set()
		if patterns is None:
			queue = deque()
			queue.append(self.p_tree.get_root())
			while len(queue) > 0:
				tree_node = queue.popleft()
				tree_node: StateDifferenceTreeNode
				for child in tree_node.get_children():
					queue.append(child)
				input_patterns.add(tree_node.get_pattern())
		else:
			for pattern in patterns:
				pattern: StateDifferencePattern
				input_patterns.add(pattern)
		return input_patterns

	def select_patterns(self, patterns, key):
		"""
		:param patterns: the set of patterns from which are selected or None to select within the current tree
		:param key: either MerMutant, MerExecution or None to select all of the patterns from the input source
		:return: the set of patterns selected from input collection using the key as the selector.
		"""
		input_patterns = self.__inputs__(patterns)
		output_patterns = set()
		for pattern in input_patterns:
			if key is None:
				output_patterns.add(pattern)
			elif pattern.has_samples(key):
				output_patterns.add(pattern)
		return output_patterns

	def evaluate_patterns(self, patterns, used_tests):
		"""
		:param patterns: the set of patterns to be evaluated or None to evaluate all the patterns in the tree
		:param used_tests: the set of test cases used for evaluating the input patterns from first parameters
		:return: the mapping from input pattern to [length, support, confidence] as evaluation metrics output
		"""
		input_patterns = self.__inputs__(patterns)
		evaluation_map = dict()
		for pattern in input_patterns:
			length, support, confidence = pattern.evaluate(used_tests)
			evaluation_map[pattern] = (length, support, confidence)
		return evaluation_map

	def sort_patterns(self, patterns, used_tests):
		"""
		:param patterns: the set of patterns to be sorted by support and confidence metrics
		:param used_tests: the set of test cases used to evaluate and sort the patterns via utility
		:return: the sorted list of patterns
		"""
		evaluation_map = self.evaluate_patterns(patterns, used_tests)

		## 1. sort by confidence at first
		confidence_dict, confidence_list = dict(), list()
		for pattern, evaluation in evaluation_map.items():
			confidence = evaluation[2]
			key = int(confidence * 10000)
			if not (key in confidence_dict):
				confidence_list.append(key)
				confidence_dict[key] = set()
			confidence_dict[key].add(pattern)
		confidence_list.sort(reverse=True)

		## 2. sort by support at thereby
		sorted_patterns = list()
		for confidence in confidence_list:
			support_dict, support_list = dict(), list()
			for pattern in confidence_dict[confidence]:
				evaluation = evaluation_map[pattern]
				support = evaluation[1]
				if not (support in support_dict):
					support_list.append(support)
					support_dict[support] = set()
				support_dict[support].add(pattern)
			support_list.sort(reverse=True)

			for support in support_list:
				for pattern in support_dict[support]:
					pattern: StateDifferencePattern
					sorted_patterns.append(pattern)

		## 3. return the sorted sequence of patterns
		return sorted_patterns

	def filter_patterns(self, patterns, used_tests):
		"""
		:param patterns: the set of patterns from which are filtered or none to filter all those in tree
		:param used_tests: the set of test cases used to evaluate and filter good patterns in the context
		:return: the set of available patterns that match the input parameters for mining
		"""
		evaluation_map = self.evaluate_patterns(patterns, used_tests)
		good_patterns = set()
		for pattern, evaluation in evaluation_map.items():
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			if (length <= self.inputs.get_max_length()) and \
					(support >= self.inputs.get_min_support()) and \
					(confidence >= self.inputs.get_min_confidence()):
				good_patterns.add(pattern)
		return good_patterns


## mining algorithm


class StateDifferenceFPMiner:
	"""
	It implements the frequent pattern mining.
	"""

	def __init__(self, inputs: StateDifferenceMineInputs):
		self.middle = StateDifferenceMineMiddle(inputs)
		self.caches = dict()	## StateDifferencePattern --> {length, support, confidence}
		return

	def __mine__(self, parent: StateDifferenceTreeNode, features: list, used_tests):
		"""
		:param parent: 		the parent node from which the recursive children are created
		:param features: 	the set of features used to extend and created children from
		:param used_tests: 	the set of test cases used for evaluating generated patterns
		:return: it recursively mines the patterns from state difference in the tree.
		"""
		## 1. evaluate the pattern of the input parent node
		length, support, confidence = parent.get_pattern().evaluate(used_tests)
		self.caches[parent.get_pattern()] = (length, support, confidence)
		inputs = self.middle.get_inputs()

		## 2. validate whether the recursive children needs be traversed
		if (length < inputs.get_max_length()) and (support >= inputs.get_min_support()) and (confidence < inputs.get_max_confidence()):
			for k in range(0, len(features)):
				child = self.middle.get_child(parent, features[k])
				if child != parent:
					self.__mine__(child, features[k + 1:], used_tests)
		return

	def __outs__(self, o_directory: str, file_name: str, c_document: jctest.CDocument, good_patterns, used_tests):
		"""
		:param o_directory:
		:param file_name:
		:param c_document:
		:param used_tests
		:return:
		"""
		## 1. collect the c_annotations for printing
		c_annotations = set()
		for pattern in good_patterns:
			pattern: StateDifferencePattern
			for annotation in pattern.get_annotations():
				c_annotation = annotation.find_source(c_document)
				c_annotations.add(c_annotation)
				for child in c_annotation.get_all_children():
					child: jctest.CirAnnotation
					c_annotations.add(child)

		## 2. create nodes and edges in DiGraph
		graph = graphviz.Digraph(comment="Frequent Pattern Tree for {}".format(file_name))
		for c_annotation in  c_annotations:
			key = str(c_annotation)
			m_annotation = self.middle.get_document().anto_space.find_annotation(str(c_annotation))
			m_pattern = self.middle.get_node([m_annotation.aid]).get_pattern()
			length, support, confidence = m_pattern.evaluate(used_tests)
			confidence = int(confidence * 10000) / 100.0
			text = "C: {}\nE: {}\nS: {}\nU: {}\nV: {}\t[{}, {}%]".format(c_annotation.get_logic_type(),
																		 c_annotation.get_execution(),
																		 c_annotation.get_execution().get_statement().code,
																		 c_annotation.get_store_unit().code,
																		 c_annotation.get_symb_value().code,
																		 support, confidence)
			graph.node(key, text)
		for c_annotation in c_annotations:
			for child in c_annotation.get_children():
				graph.edge(str(c_annotation), str(child))

		## 3. output the pdf file anyway
		graph.render(filename=file_name + ".fpm", directory=o_directory, format="pdf")
		file_path = os.path.join(o_directory, file_name + ".fpm")
		os.remove(file_path)
		return

	def mine(self, features, used_tests, is_reported, c_document, o_directory):
		"""
		:param features:		the set of features from which the patterns will be generated.
		:param used_tests:		the set of test cases used to evaluate the metrics of each pattern
		:param is_reported:		whether to report the debugging information in pattern mining process.
		:param c_document:		the data source document for visualization of frequent pattern mining.
		:param o_directory: 	the directory where the pattern tree is printed
		:return:
		"""
		## 1. initialize the feature vector and used test number for reporting
		feature_list = self.middle.get_document().anto_space.normal(features)
		if used_tests is None:
			number_of_tests = len(self.middle.get_document().test_space.get_test_cases())
		else:
			number_of_tests = len(used_tests)

		## 2. perform frequent pattern mining algorithm for association mining
		if is_reported:
			print("\t\t--> Mine({}, {})".format(len(feature_list), number_of_tests), end='\t')
		self.caches.clear()
		self.__mine__(self.middle.get_root(), feature_list, used_tests)
		good_patterns = self.middle.filter_patterns(self.caches.keys(), used_tests)
		if is_reported:
			print("[{} rules; {} goods]".format(len(self.caches), len(good_patterns)))

		## 3. output the annotation tree to specified file if it is specified
		if not (c_document is None):
			file_name = c_document.get_program().name
			self.__outs__(o_directory, file_name, c_document, good_patterns, used_tests)
		self.caches.clear()
		return good_patterns


class StateDifferenceDTMiner:
	"""
	Decision tree based pattern mining.
	"""

	def __init__(self, inputs: StateDifferenceMineInputs):
		self.middle = inputs.get_middle_module()
		return

	def __new_decision_tree__(self, used_tests, is_reported: bool):
		"""
		:param used_tests:
		:return: it generates the decision tree for best classifying samples
		"""
		xmatrix = self.middle.get_document().exec_space.new_feature_matrix()
		ylabels = self.middle.get_document().exec_space.new_label_list(used_tests)
		dc_tree = sktree.DecisionTreeClassifier()
		dc_tree.fit(xmatrix, ylabels)
		if is_reported:
			plabels = dc_tree.predict(xmatrix)
			print(metrics.classification_report(ylabels, plabels), end='')
		return dc_tree

	@staticmethod
	def __normalize_annotation__(annotation: jecode.MerAnnotation, c_document: jctest.CDocument):
		"""
		:param annotation:
		:param c_document:
		:return: execution[logic_type](location.code, parameter)
		"""
		c_annotation = annotation.find_source(c_document)
		execution = c_annotation.get_execution()
		logic_type = c_annotation.logic_type
		code = c_annotation.store_unit.get_cir_code()
		if c_annotation.symb_value is None:
			parameter = None
		else:
			parameter = c_annotation.symb_value.get_code()
		text = "{}::{}({}::{})".format(logic_type, execution, code, parameter)
		text = text.replace('\"', '\'\'')
		return text

	def __out_decision_tree__(self, dc_tree: sktree.DecisionTreeClassifier, tree_file_path: str, c_document: jctest.CDocument):
		"""
		:param dc_tree:
		:param tree_file_path:
		:return:
		"""
		m_document = self.middle.get_document()
		names = list()
		for annotation in m_document.anto_space.get_annotations():
			annotation: jecode.MerAnnotation
			names.append(StateDifferenceDTMiner.__normalize_annotation__(annotation, c_document))
		dot_data = sktree.export_graphviz(dc_tree, out_file=None,
										  feature_names=names,
										  class_names=["Alive", "Killed"],
										  filled=True)
		graph = pydotplus.graph_from_dot_data(dot_data)
		graph.write_pdf(tree_file_path)
		return

	def __min_decision_path__(self, dc_tree: sktree.DecisionTreeClassifier):
		xmatrix = self.middle.get_document().exec_space.new_feature_matrix()
		node_indicator = dc_tree.decision_path(xmatrix)
		leaf_id = dc_tree.apply(xmatrix)
		dc_feature = dc_tree.tree_.feature
		dc_threshold = dc_tree.tree_.threshold
		patterns = set()
		for eid in range(0, len(self.middle.get_document().exec_space.get_executions())):
			node_index = node_indicator.indices[node_indicator.indptr[eid]: node_indicator.indptr[eid + 1]]
			eid_features = set()
			for node_id in node_index:
				if leaf_id[eid] == node_id:
					continue
				elif xmatrix[eid, dc_feature[node_id]] > dc_threshold[node_id]:
					eid_features.add(dc_feature[node_id])
			pattern = self.middle.get_node(eid_features).get_pattern()
			patterns.add(pattern)
		return patterns

	def mine(self, used_tests, c_document: jctest.CDocument, tree_file_path: str, is_reported: bool):
		"""
		:param used_tests:
		:param c_document:
		:param tree_file_path: pdf file of decision tree
		:param is_reported: whether to report the classification metrics
		:return:
		"""
		dc_tree = self.__new_decision_tree__(used_tests, is_reported)
		if tree_file_path is not None:
			self.__out_decision_tree__(dc_tree, tree_file_path, c_document)
		patterns = self.__min_decision_path__(dc_tree)
		return self.middle.filter_patterns(patterns, used_tests)


## output layer

class StateDifferencePatternWriter:
	"""
	It implements the visualization of state difference patterns.
	"""

	def __init__(self, c_document: jctest.CDocument, inputs: StateDifferenceMineInputs):
		"""
		:param c_document: the original document of dataset in mutation test project
		:param inputs: the module used for driving the pattern mining in encoded document
		"""
		self.c_document = c_document
		self.m_document = inputs.get_document()
		self.inp_module = inputs
		self.writer = None
		return

	## basic approach

	def __do_canceled__(self):
		return self

	def __open_writer__(self, writer: TextIO, beg_line: str):
		"""
		:param writer:
		:param beg_line:
		:return:
		"""
		self.writer = writer
		if beg_line:
			self.writer.write(beg_line.strip() + "\n\n")
		return

	def __output_text__(self, text: str):
		"""
		:param text: the string to be written on output stream
		:return:
		"""
		self.writer: TextIO
		self.writer.write(text)
		return

	def __close_writer__(self):
		"""
		:return:
		"""
		self.__output_text__("\nEnd_Of_File")
		self.writer = None
		return

	def __prf_measure__(self, used_tests, patterns):
		"""
		:param used_tests: the set of tests to collect undetected mutants in project
		:param patterns: the set of patterns for covering mutants in the project
		:return: undetected_number, predicted_number, matched_number, precision, recall, f1_score
					1. undetected_number: the number of undetected mutants by the used_tests
					2. predicted_number: the number of mutants predicted by input patterns
					3. matched_number: the number of undetected mutants matched by input patterns
					4. precision = matched_number / predicted_number
					5. recall = matched_number / undetected_number
					6. f1_score = 2 * precision * recall / (precision + recall)
		"""
		## 1. mutations classification and intersection operation
		undetected_mutants = set()
		for mutant in self.m_document.exec_space.get_mutants():
			mutant: jecode.MerMutant
			if not mutant.is_killed_in(used_tests):
				undetected_mutants.add(mutant)
		predicted_mutants = set()
		for pattern in patterns:
			pattern: StateDifferencePattern
			for execution in pattern.get_executions():
				predicted_mutants.add(execution.get_mutant())
		matched_mutants = undetected_mutants & predicted_mutants

		## 2. mutation sample counting and metrics evaluation
		return self.__prf_evaluate__(undetected_mutants, predicted_mutants)

	def __prf_evaluate__(self, orig_samples, pred_samples):
		"""
		:param orig_samples:
		:param pred_samples:
		:return: undetected_number, predicted_number, matched_number, precision, recall, f1_score
					1. undetected_number: the number of undetected mutants by the used_tests
					2. predicted_number: the number of mutants predicted by input patterns
					3. matched_number: the number of undetected mutants matched by input patterns
					4. precision = matched_number / predicted_number
					5. recall = matched_number / undetected_number
					6. f1_score = 2 * precision * recall / (precision + recall)
		"""
		self.__do_canceled__()
		undetected_number = len(orig_samples)
		predicted_number = len(pred_samples)
		matched_samples = orig_samples & pred_samples
		matched_number = len(matched_samples)
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if matched_number > 0:
			precision = matched_number / predicted_number
			recall = matched_number / undetected_number
			f1_score = 2 * precision * recall / (precision + recall)
		precision = int(precision * 1000000) / 10000.0
		recall = int(recall * 1000000) / 10000.0
		f1_score = int(f1_score * 1000000) / 1000000.0
		return undetected_number, predicted_number, matched_number, precision, recall, f1_score

	def __mini_select__(self, patterns):
		"""
		:param patterns: the set of patterns from which the minimal coverage set are generated
		:return: the set of minimal set of patterns that cover all the mutants predicted by the input patterns
		"""
		## 1. collect all the patterns along with their executions
		all_patterns, all_executions = set(), set()
		for pattern in patterns:
			pattern: StateDifferencePattern
			all_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jecode.MerExecution
				all_executions.add(execution)
		self.__do_canceled__()

		## 2. randomly select the minimal set from all_patterns for covering all_executions
		min_patterns = set()
		while len(all_executions) > 0:
			## 2-1. select the next pattern from set
			pattern = jcbase.rand_select(all_patterns)
			pattern: StateDifferencePattern
			all_patterns.remove(pattern)
			## 2-2. in case that all the patterns are extracted, stop further traversal.
			if len(all_patterns) == 0:
				min_patterns.add(pattern)
				break
			## 2-3. otherwise, update the executions set and continue the loop forwards.
			is_updated = False
			for execution in pattern.get_executions():
				if execution in all_executions:
					all_executions.remove(execution)
					is_updated = True
			## 2-4. in case that some executions match and removed, update the minimal set
			if is_updated:
				min_patterns.add(pattern)

		## 3. return the minimal set of patterns that cover all the executions of input set
		return min_patterns

	## format approach

	def __mut2str__(self, mutant: jecode.MerMutant):
		"""
		:param mutant: it transforms the mutant to text information for printed
		:return: id result class operator function line "code" [parameter]
		"""
		## 1. obtain the source mutation from c_document
		source_mutant = mutant.find_source(self.c_document)

		## 2. extract feature text from the source mutant
		mid = source_mutant.get_muta_id()
		if mutant.is_killed_in(None):
			result = "killed"
		else:
			result = "survive"
		m_class = source_mutant.get_mutation().get_mutation_class()
		m_operator = source_mutant.get_mutation().get_mutation_operator()
		m_location = source_mutant.get_mutation().get_location()
		m_function = m_location.function_definition_of()
		func_name = m_function.get_code(True)
		index = func_name.index('(')
		func_name = func_name[0: index].strip()
		line = m_location.line_of(tail=False)
		code = m_location.get_code(True)
		parameter = source_mutant.get_mutation().get_parameter()

		## 3. format the output string of the mutant information
		return "{}\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t[{}]".format(mid, result, m_class, m_operator,
															 func_name, line, code, parameter)

	def __pat2str__(self, pattern: StateDifferencePattern, used_tests=None):
		"""
		:param pattern: it transforms the state difference pattern to string for printing
		:param used_tests: the set of tests used to evaluate the input pattern for prints
		:return: id length executions mutants support confidence(%)
		"""
		self.__do_canceled__()
		pid = str(pattern.get_features())
		executions = len(pattern.get_executions())
		length, support, confidence = pattern.evaluate(used_tests)
		confidence = int(confidence * 1000000) / 10000.0
		mutants = len(pattern.get_mutants())
		return "{}\t{}\t{}\t{}\t{}\t{}%".format(pid, length, executions, mutants, support, confidence)

	def __ant2str__(self, annotation: jecode.MerAnnotation):
		"""
		:param annotation:
		:return: class operator execution line statement location [parameter]
		"""
		source_annotation = annotation.find_source(self.c_document)
		logic_type = source_annotation.get_logic_type()
		execution = source_annotation.get_execution()
		store_unit = source_annotation.get_store_unit()
		symb_value = source_annotation.get_symb_value()
		statement = execution.get_statement()
		if store_unit.has_ast_source():
			line = store_unit.get_ast_source().line_of(tail=False)
		else:
			line = None
		return "{}\t{}\t#{}\t\"{}\"\t\"{}\"\t[{}]".format(logic_type, execution, line, statement,
														  store_unit.get_cir_code(), symb_value)

	## writing methods

	def write_pattern_objects(self, middle: StateDifferenceMineMiddle, patterns, file_path: str, used_tests, beg_line: str):
		"""
		:param middle:		the middle module used to sort the output patterns for being printed to out files
		:param patterns: 	the set of state difference patterns to be printed on file with single visualized
		:param file_path: 	the path of output file (xxx.x.pat) to preserve single features of input patterns
		:param used_tests: 	the set of test used to evaluate the effectiveness of patterns outputted to files
		:param beg_line:	the first line to be printed onto the file
		:return:
		"""
		with open(file_path, 'w') as writer:
			## 1. initialize the output stream writer
			self.__open_writer__(writer, beg_line)

			## 2. sort the patterns to be printed for
			output_patterns = middle.sort_patterns(patterns, used_tests)

			## 3. single pattern line being printed
			for pattern in output_patterns:
				## 3-0. start of the pattern XML block
				self.__output_text__("BEG_PATTERN\n")

				# 3-1. [PID] length executions mutations support confidence(%)
				self.__output_text__("\t[PID]\tlength\texecutions\tmutations\tsupport\tconfidence(%)\n")
				self.__output_text__("\t{}\n".format(self.__pat2str__(pattern, used_tests)))
				self.__output_text__("\n")

				## 3-2. [AID] class execution line statement store_unit symb_value
				self.__output_text__("\t[AID]\tclass\texecution\tline\tstatement\tstore_unit\tsymb_value\n")
				annotation_index = 0
				for annotation in pattern.get_annotations():
					annotation_index += 1
					self.__output_text__("\t{}\t{}\n".format(annotation_index, self.__ant2str__(annotation)))
				self.__output_text__("\n")

				## 3-3. [MID] result class operator function line "code" [parameter]
				self.__output_text__("\t[MID]\tresult\tclass\toperator\tfunction\tline\tcode\tparameter\n")
				for mutant in pattern.get_mutants():
					self.__output_text__("\t{}\n".format(self.__mut2str__(mutant)))
				self.__output_text__("\n")

				## end of the pattern XML block
				self.__output_text__("END_PATTERN\n\n")

			## 4. close the output file and print EOF
			self.__close_writer__()
		return

	def write_pattern_metrics(self, middle: StateDifferenceMineMiddle, patterns, file_path: str, used_tests, beg_line: str):
		"""
		:param middle:		the middle module used for evaluating state difference patterns being printed
		:param patterns:	the set of patterns to be evaluated and printed their scores to the out files
		:param file_path:	the path of the output file to preserve the metrics of the output patterns in
		:param used_tests:	the set of test cases used for evaluating the effectiveness of output pattern
		:param beg_line:	the first line to be printed onto the file
		:return:
		"""
		with open(file_path, 'w') as writer:
			## 1. initialize the writer and start output
			self.__open_writer__(writer, beg_line)

			## 2. print the summary scores at the very beginning
			min_patterns = self.__mini_select__(patterns)
			all_number, pre_number, mat_number, precision, recall, f1_score = self.__prf_measure__(used_tests, patterns)
			optimized_ratio = len(min_patterns) / (all_number + 0.00000001)
			optimized_ratio = int(optimized_ratio * 1000000) / 10000.0
			self.__output_text__("Pattern Mining Evaluation Metrics\n")
			self.__output_text__("\tUndetected\t{}\tPredicted\t{}\tMatched\t{}\n".format(all_number, pre_number, mat_number))
			self.__output_text__("\tOutput\t{}\tMinimal\t{}\tRatio\t{}%\n".format(len(patterns), len(min_patterns), optimized_ratio))
			self.__output_text__("\tPrecision\t{}%\tRecall\t{}%\tF1_Score\t{}\n".format(precision, recall, f1_score))
			self.__output_text__("\n")

			## 3. evaluation metrics for every input patterns by sort
			output_patterns = middle.sort_patterns(patterns, used_tests)
			self.__output_text__("Summary Table of Each Pattern\n")
			self.__output_text__("Pid\tLength\tExecutions\tMutants\tResult\tKilled\tAlive\tConfidence(%)\n")
			for pattern in output_patterns:
				pid = str(pattern.get_features())
				length = len(pattern.get_features())
				executions = len(pattern.get_executions())
				mutants = len(pattern.get_mutants())
				result, killed, survive, confidence = pattern.predict(used_tests)
				confidence = int(confidence * 1000000) / 10000.0
				self.__output_text__("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}%\n".format(pid, length, executions, mutants,
																				result, killed, survive, confidence))
			self.__output_text__("\n")

			## 4. close the output file and print EOF
			self.__close_writer__()
		return

	def write_mutant_patterns(self, middle: StateDifferenceMineMiddle, patterns, file_path: str, used_tests, beg_line: str):
		"""
		:param middle:
		:param patterns:
		:param file_path:
		:param used_tests:
		:param beg_line:	the first line to be printed onto the file
		:return:
		"""
		## 1. collect the mutants predicted by the input patterns
		output_patterns, mutants = set(), set()
		for pattern in patterns:
			pattern: StateDifferencePattern
			output_patterns.add(pattern)
			for mutant in pattern.get_mutants():
				mutants.add(mutant)

		## 2. write the patterns matching with each mutant from
		with open(file_path, 'w') as writer:
			self.__open_writer__(writer, beg_line)
			for mutant in mutants:
				## 2-0. start flag of the mutation and patterns
				self.__output_text__("BEG_MUTATION\n")

				## 2-1. write the mutation information as head
				self.__output_text__("\t[MID]\tresult\tclass\toperator\tfunction\tline\tcode\tparameter\n")
				self.__output_text__("\t{}\n".format(self.__mut2str__(mutant)))
				self.__output_text__("\n")

				## 2-2. collect the patterns matching with the mutant
				mutant_patterns = set()
				for pattern in output_patterns:
					if pattern.has_samples(mutant):
						mutant_patterns.add(pattern)
				pattern_list = middle.sort_patterns(mutant_patterns, used_tests)

				## 2-3. write each pattern w.r.t. the mutant on to file
				for pattern in pattern_list:
					self.__output_text__("\t[PID]\tlength\texecutions\tmutants\tsupport\tconfidence(%)\n")
					self.__output_text__("\t{}\n".format(self.__pat2str__(pattern, used_tests)))
					self.__output_text__("\tclass\texecution\tline\tstatement\tstore_unit\tsymb_value\n")
					for annotation in pattern.get_annotations():
						self.__output_text__("\t{}\n".format(self.__ant2str__(annotation)))
					self.__output_text__("\n")

				## 2-4. end flag of the mutation and patterns
				self.__output_text__("END_MUTATION\n\n")
			self.__close_writer__()
		return

	def write_failed_mutation(self, middle: StateDifferenceMineMiddle, patterns, file_path: str, used_tests, beg_line: str):
		"""
		:param middle:
		:param patterns:
		:param file_path:
		:param used_tests:
		:param beg_line:	the first line to be printed onto the file
		:return: the set of mutants and their annotations that are not covered by the patterns
		"""
		## 1. collect all the undetected mutants by the used tests in current program
		undetected_mutants, predicted_mutants = set(), set()
		for mutant in self.m_document.exec_space.get_mutants():
			mutant: jecode.MerMutant
			if not mutant.is_killed_in(used_tests):
				undetected_mutants.add(mutant)
		for pattern in patterns:
			pattern: StateDifferencePattern
			for mutant in pattern.get_mutants():
				predicted_mutants.add(mutant)
		uncovered_mutants = undetected_mutants - predicted_mutants

		## 2. output the uncovered mutants to file for further debugging
		with open(file_path, 'w') as writer:
			## 2-1. initialize the output stream writer
			self.__open_writer__(writer, beg_line)

			## 2-2. write the mutant and its annotation patterns for debugging
			for mutant in uncovered_mutants:
				## A. start flag of each uncovered mutant
				self.__output_text__("BEG_UNCOVERED\n")

				## B. mutation information as the header
				self.__output_text__("\t[MID]\tresult\tclass\toperator\tfunction\tline\tcode\tparameter\n")
				self.__output_text__("\t{}\n".format(self.__mut2str__(mutant)))
				self.__output_text__("\n")

				## C. collect the annotations and produce corresponding single patterns.
				features = set()
				for execution in self.m_document.exec_space.get_executions_of(mutant):
					execution: jecode.MerExecution
					for feature in execution.get_features():
						features.add(feature)
				mutant_patterns = set()
				for feature in features:
					pattern = middle.get_node([feature]).get_pattern()
					mutant_patterns.add(pattern)
				pattern_list = middle.sort_patterns(mutant_patterns, used_tests)
				if len(pattern_list) > 12:
					pattern_list = pattern_list[0: 12]

				## D. output the annotation-based pattern set to further debugging
				for pattern in pattern_list:
					self.__output_text__("\t[PID]\tlength\texecutions\tmutants\tsupport\tconfidence(%)\n")
					self.__output_text__("\t{}\n".format(self.__pat2str__(pattern, used_tests)))
					self.__output_text__("\tclass\texecution\tline\tstatement\tstore_unit\tsymb_value\n")
					for annotation in pattern.get_annotations():
						self.__output_text__("\t{}\n".format(self.__ant2str__(annotation)))
					self.__output_text__("\n")

				## E. end flag of the uncovered mutation
				self.__output_text__("END_UNCOVERED\n\n")

			## 2-3. close the output stream and set None
			self.__close_writer__()
		return

	def __mine_best_pattern__(self, document: jecode.MerDocument, mutant: jecode.MerMutant, miner: StateDifferenceFPMiner, max_tests_number: int):
		"""
		:param document:
		:param mutant:
		:param miner:
		:return: the best pattern to represent the mutation or None if the mutant cannot be matched
		"""
		self.__do_canceled__()

		## 1. capture the features incorporated in the mutation
		features = set()
		for execution in document.exec_space.get_executions_of(mutant):
			execution: jecode.MerExecution
			for feature in execution.get_features():
				features.add(feature)

		## 2. it collects the tests that cannot kill the mutant
		test_list = set()
		for tid in range(0, len(mutant.get_result())):
			if mutant.is_killed_by(tid):
				continue
			else:
				test_list.add(tid)
		used_tests = set()
		while len(test_list) > 0:
			rand_test = jcbase.rand_select(test_list)
			rand_test: int
			used_tests.add(rand_test)
			test_list.remove(rand_test)
			if max_tests_number > 0 and len(used_tests) >= max_tests_number:
				break

		## 3. it generates the good patterns for best matching
		patterns = miner.mine(features, used_tests, False, None, None)
		pattern_list = miner.middle.sort_patterns(patterns, used_tests)
		if len(pattern_list) > 0:
			best_pattern = pattern_list[0]
		else:
			best_pattern = None
		return best_pattern, used_tests

	def write_mutant_clusters(self, inputs: StateDifferenceMineInputs, file_path: str, beg_line: str, is_reported: bool, max_tests_number: int):
		"""
		:param inputs:
		:param file_path:
		:param beg_line:
		:return:
		"""
		## 1. perform frequent pattern mining on every mutant to fetch representative pattern
		pattern_mutants, pattern_tests, miner = dict(), dict(), StateDifferenceFPMiner(inputs)
		all_mutants, uncovered_mutants, covered_mutants = set(), set(), set()
		counter, total_number = 0, len(inputs.get_document().exec_space.get_mutants())
		for mutant in inputs.get_document().exec_space.get_mutants():
			mutant: jecode.MerMutant
			all_mutants.add(mutant)
			best_pattern, used_tests = self.__mine_best_pattern__(inputs.get_document(), mutant, miner, max_tests_number)
			counter += 1
			if is_reported and counter % 20 == 0:
				file_name = self.c_document.get_program().name
				print("\t\t{}[{}/{}] ==> {} pattern and {} tests".format(file_name, counter, total_number,
																		 (best_pattern is not None), len(used_tests)))
			if best_pattern is None:
				uncovered_mutants.add(mutant)
			else:
				best_pattern: StateDifferencePattern
				if not (best_pattern in pattern_mutants):
					pattern_mutants[best_pattern] = set()
					pattern_tests[best_pattern] = used_tests
				pattern_mutants[best_pattern].add(mutant)
				covered_mutants.add(mutant)

		## 2. it writes the mutation-clustering patterns for each mutants being covered
		with open(file_path, 'w') as writer:
			## 2-1. initialize the output stream writer
			self.__open_writer__(writer, beg_line)

			## 2-2. write the coverage metrics from the mutants
			orig_number, pred_number, match_number, precision, recall, f1_score = self.__prf_evaluate__(all_mutants, covered_mutants)
			self.__output_text__("BEG-COVERAGE\n")
			self.__output_text__("\tOrig_Mutants = {}\tPred_Mutants = {}\tMatch_Mutants = {}\n".format(orig_number, pred_number, match_number))
			self.__output_text__("\tPrecision = {}%\tRecall = {}%\tF1_Score = {}\n".format(precision, recall, f1_score))
			optimized_ratio = len(pattern_mutants) / (len(all_mutants) + 0.0000000001)
			optimized_ratio = int(optimized_ratio * 1000000) / 10000.0
			self.__output_text__("\tClusters = {}\tOptimized_Ratio = {}%\n".format(len(pattern_mutants), optimized_ratio))
			self.__output_text__("END_COVERAGE\n\n")

			## 2-3. covered mutation and the corresponding patterns
			self.__output_text__("BEG_CLUSTERS\n")
			for pattern, mutants in pattern_mutants.items():
				pattern: StateDifferencePattern
				used_tests = pattern_tests[pattern]
				self.__output_text__("\tP.{}\n".format(self.__pat2str__(pattern, used_tests)))
				for annotation in pattern.get_annotations():
					self.__output_text__("\tC.{}\n".format(self.__ant2str__(annotation)))
				for mutant in mutants:
					self.__output_text__("\t\t{}\n".format(self.__mut2str__(mutant)))
				self.__output_text__("\n")
			self.__output_text__("END_CLUSTERS\n\n")

			## 2-4. uncovered mutation being printed
			self.__output_text__("BEG_UNCOVERED\n")
			for uncovered_mutant in uncovered_mutants:
				self.__output_text__("\t{}\n".format(self.__mut2str__(uncovered_mutant)))
			self.__output_text__("END_UNCOVERED\n\n")

			## 2-5. close the writer and end of the file
			self.__close_writer__()
		return


## testing method


def do_fpm_mining(c_document: jctest.CDocument, inputs: StateDifferenceMineInputs,
				  o_directory: str, file_name: str, used_tests, is_reported: bool):
	"""
	:param c_document: 		the document of mutation testing project and its data source
	:param inputs: 			the input module to drive the pattern mining procedures
	:param o_directory:		the directory where the pattern files will be generated
	:param file_name:		the name of the project file as the prefix of output files
	:param used_tests:		the set of test cases used to evaluate patterns or None for all
	:param is_reported:		whether to report the mining algorithm debugging details
	:return:
	"""
	## 1. collect the features from undetected mutants within the project
	features = set()
	for execution in inputs.get_document().exec_space.get_executions():
		execution: jecode.MerExecution
		if not execution.get_mutant().is_killed_in(used_tests):
			for feature in execution.get_features():
				features.add(feature)

	## 2. construct the frequent pattern mining and its middle module
	fp_miner = StateDifferenceFPMiner(inputs)
	fp_middle = fp_miner.middle
	ou_patterns = fp_miner.mine(features, used_tests, is_reported, c_document, o_directory)

	## 3. write the output patterns and their scores to specified directory
	writer = StateDifferencePatternWriter(c_document, inputs)
	mi_patterns = writer.__mini_select__(ou_patterns)
	writer.write_pattern_objects(fp_middle, ou_patterns, os.path.join(o_directory, file_name + ".fpm.p2o"), used_tests,
								 "Instances Table for Good Patterns and their Corresponding Mutants & Annotations")
	writer.write_pattern_objects(fp_middle, mi_patterns, os.path.join(o_directory, file_name + ".fpm.p2m"), used_tests,
								 "Instances Table for Mini Patterns and their Corresponding Mutants & Annotations")
	writer.write_mutant_patterns(fp_middle, ou_patterns, os.path.join(o_directory, file_name + ".fpm.m2p"), used_tests,
								 "Mutation Table for Covered Mutants and their Correspoding Matched Patterns")
	writer.write_failed_mutation(fp_middle, ou_patterns, os.path.join(o_directory, file_name + ".fpm.m2u"), used_tests,
								 "Mutation Table for Uncovered Mutants and Corresponding Annotations Defined")
	writer.write_pattern_metrics(fp_middle, ou_patterns, os.path.join(o_directory, file_name + ".fpm.e2s"), used_tests,
								 "Evaluation Metrics Table for Generated Good State Difference based Patterns")
	return


def do_dtm_mining(c_document: jctest.CDocument, inputs: StateDifferenceMineInputs,
				  o_directory: str, file_name: str, used_tests, is_reported: bool):
	"""
	:param c_document: 		the document of mutation testing project and its data source
	:param inputs: 			the input module to drive the pattern mining procedures
	:param o_directory:		the directory where the pattern files will be generated
	:param file_name:		the name of the project file as the prefix of output files
	:param used_tests:		the set of test cases used to evaluate patterns or None for all
	:param is_reported:		whether to report the mining algorithm debugging details
	:return:
	"""
	## 1. construct the decision tree based mining and its middle module
	dt_miner = StateDifferenceDTMiner(inputs)
	dt_middle = dt_miner.middle
	ou_patterns = dt_miner.mine(used_tests, c_document, os.path.join(o_directory, file_name + ".dtm.pdf"), is_reported)

	## 2. write the output patterns and their scores to specified directory
	writer = StateDifferencePatternWriter(c_document, inputs)
	mi_patterns = writer.__mini_select__(ou_patterns)
	writer.write_pattern_objects(dt_middle, ou_patterns, os.path.join(o_directory, file_name + ".dtm.p2o"), used_tests,
								 "Instances Table for Good Patterns and their Corresponding Mutants & Annotations")
	writer.write_pattern_objects(dt_middle, mi_patterns, os.path.join(o_directory, file_name + ".dtm.p2m"), used_tests,
								 "Instances Table for Mini Patterns and their Corresponding Mutants & Annotations")
	writer.write_mutant_patterns(dt_middle, ou_patterns, os.path.join(o_directory, file_name + ".dtm.m2p"), used_tests,
								 "Mutation Table for Covered Mutants and their Correspoding Matched Patterns")
	writer.write_failed_mutation(dt_middle, ou_patterns, os.path.join(o_directory, file_name + ".dtm.m2u"), used_tests,
								 "Mutation Table for Uncovered Mutants and Corresponding Annotations Defined")
	writer.write_pattern_metrics(dt_middle, ou_patterns, os.path.join(o_directory, file_name + ".dtm.e2s"), used_tests,
								 "Evaluation Metrics Table for Generated Good State Difference based Patterns")
	return


def do_fp_cluster(c_document: jctest.CDocument, inputs: StateDifferenceMineInputs,
				  o_directory: str, file_name: str, is_reported: bool, max_tests_number: int):
	"""
	:param c_document:
	:param inputs:
	:param o_directory:
	:param file_name:
	:param is_reported:
	:param max_tests_number:
	:return:
	"""
	writer = StateDifferencePatternWriter(c_document, inputs)
	writer.write_mutant_clusters(inputs, os.path.join(o_directory, file_name + ".fpc"),
								 "Frequent Pattern based Clustering", is_reported, max_tests_number)
	return


def do_mining(c_document: jctest.CDocument, m_document: jecode.MerDocument,
			  output_directory: str, file_name: str, used_tests, is_reported: bool,
			  max_length: int, min_support: int, min_confidence: float, max_confidence: float):
	"""
	:param used_tests:
	:param c_document: original document
	:param m_document: encoded document
	:param output_directory: the output directory where files are printed
	:param file_name: the project name
	:param is_reported: whether to report the pattern mining details
	:param max_length: the maximal length of generated patterns
	:param min_support: minimal support for mining
	:param min_confidence: minimal confidence for mining
	:param max_confidence: maximal confidence for mining
	:return:
	"""
	# I. create output directory for pattern generation
	print("BEG-Project #{}".format(file_name))
	o_directory = os.path.join(output_directory, file_name)
	if not os.path.exists(o_directory):
		os.mkdir(o_directory)
	print("\tI. Load {} executions between {} mutants and {} tests.".format(
		len(m_document.exec_space.get_executions()),
		len(m_document.exec_space.get_mutants()),
		len(m_document.test_space.get_test_cases())))

	# II. construct the input module for driving pattern mining procedures
	inputs = StateDifferenceMineInputs(m_document, max_length, min_support, min_confidence, max_confidence)
	print("\tII. Inputs: max_len = {}; min_supp = {}; min_conf = {}; max_conf = {}.".format(inputs.get_max_length(),
																							inputs.get_min_support(),
																							inputs.get_min_confidence(),
																							inputs.get_max_confidence()))

	## III. perform frequent pattern mining and evaluate it
	print("\tIII. Perform Frequent Pattern Mining and Evaluate for Output.")
	do_fpm_mining(c_document, inputs, o_directory, file_name, used_tests, is_reported)

	## IV. perform decision tree based mining and evaluated
	print("\tIV. Perform Decision Tree Mining and Evaluate it for Output.")
	old_max_length = inputs.get_max_length()
	inputs.max_length = 256
	do_dtm_mining(c_document, inputs, o_directory, file_name, used_tests, is_reported)
	inputs.max_length = old_max_length

	## V. perform frequent pattern based clustering and output
	print("\tV. Perform Frequent Pattern based Clustering and Output them.")
	inputs.min_support = 1
	# do_fp_cluster(c_document, inputs, o_directory, file_name, True, 96)
	inputs.min_support = min_support

	## VI. end of all of the mutation testing project
	print("END-Project #{}".format(file_name))
	return


def main(project_directory: str, encoding_directory: str, output_directory: str):
	"""
	:param project_directory:
	:param encoding_directory:
	:param output_directory:
	:return:
	"""
	## establish the pattern mining and output parameters
	max_length, min_support, min_confidence, max_confidence, used_tests, is_reported = 1, 2, 0.75, 0.99, None, True

	## testing on every project in the project directory
	for file_name in os.listdir(project_directory):
		## load document and encoded features into memory
		c_document_directory = os.path.join(project_directory, file_name)
		m_document_directory = os.path.join(encoding_directory, file_name)
		c_document = jctest.CDocument(c_document_directory, file_name)
		m_document = jecode.MerDocument(m_document_directory, file_name)

		## perform pattern mining and evaluation proceed
		do_mining(c_document, m_document,
				  output_directory, file_name, used_tests, is_reported,
				  max_length, min_support, min_confidence, max_confidence)
		print()
	return


## execution script


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/features"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/patterns"
	main(proj_directory, enco_directory, outs_directory)
	exit(0)

