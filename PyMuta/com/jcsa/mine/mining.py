"""This file defines the data model of killable prediction rule and mining algorithm for generating them."""


import os

from typing.io import TextIO

import com.jcsa.libs.base as jcbase
import com.jcsa.libs.test as jctest
import com.jcsa.mine.encode as jcenco


## DATA MODEL


class MerPredictRuleTree:
	"""
	It manages the killable prediction rules in tree structure based memory-reduced data samples.
	"""

	def __init__(self, document: jcenco.MerDocument):
		self.document = document
		self.root = MerPredictRuleTreeNode(self, None, -1)
		return

	# getters

	def get_document(self):
		"""
		:return: document as the data source of the tree
		"""
		return self.document

	def get_root(self):
		"""
		:return: the root node in the tree as empty rule without any features
		"""
		return self.root

	def get_node(self, features):
		"""
		:param features: the collection of features (int) encoding the conditions included in rule
		:return:
		"""
		feature_list = list()
		for feature in features:
			feature: int
			if not (feature in feature_list):
				feature_list.append(feature)
		feature_list.sort()
		tree_node = self.root
		for feature in feature_list:
			tree_node = tree_node.__extend__(feature)
		tree_node: MerPredictRuleTreeNode
		return tree_node

	def get_child(self, parent, feature: int):
		"""
		:param parent: the parent node from which the child is defined
		:param feature: the feature to define the child
		:return: the unique child node under the parent in the tree space
		"""
		parent: MerPredictRuleTreeNode
		if parent.get_tree() == self:
			return parent.__extend__(feature)
		else:
			features = parent.get_features()
			features.sort()
			return self.get_node(features)

	# collection

	@staticmethod
	def __get_nodes_in__(node, nodes: set):
		"""
		:param node: tree node under which the nodes are collected
		:param nodes: the set preserves the node being collected under the tree node
		:return:
		"""
		nodes.add(node)
		node: MerPredictRuleTreeNode
		for child in node.get_children():
			MerPredictRuleTree.__get_nodes_in__(child, nodes)
		return

	@staticmethod
	def __len_nodes_in__(node):
		"""
		:param node:
		:return: compute the number of nodes in the sub-tree of which root is the input node
		"""
		node: MerPredictRuleTreeNode
		counter = 1
		for child in node.get_children():
			counter += MerPredictRuleTree.__len_nodes_in__(child)
		return counter

	def get_nodes(self):
		"""
		:return: the set of all the tree nodes created under the tree
		"""
		nodes = set()
		MerPredictRuleTree.__get_nodes_in__(self.root, nodes)
		return nodes

	def __len__(self):
		return MerPredictRuleTree.__len_nodes_in__(self.root)


class MerPredictRuleTreeNode:
	"""
	The tree node in prediction rule tree refers to a unique killable prediction rule using the features encoded
	over the path from root until the target node.
	"""

	def __init__(self, tree: MerPredictRuleTree, parent, feature: int):
		"""
		:param tree: the tree where this node is created
		:param parent: the parent from which this node is extended
		:param feature: the integer extends this node from its parent
		"""
		self.tree = tree			# the hierarchical model to create this node
		self.parent = parent		# the parent of this tree node or None if it is root
		if parent is None:
			self.feature = -1		# invalid integer will not be used to encode symbolic conditions
		else:
			self.feature = feature	# the integer encode symbolic condition from parent to this node
		self.children = list()		# the sequence of children extended from this node using feature
		self.executions = set()		# the set of symbolic executions that are matched with this node
		self.__updates__()			# NOTE: the data samples are updated during initialization process
		return

	def __extend__(self, feature: int):
		"""
		:param feature: the integer used to extend this node to generate a child rule under the tree node
		:return: 	There are four possible cases during generating the child node.
					1)	The feature is smaller than the features on root-node path, then no child is created
						and the method will return None;
					2) 	the feature is in the root-node path (sorted), it will not insert any child and just
						return the tree node itself as a result;
					3)	the feature is greater than the features on root-node path, however, there have been
						children of which feature equals with the input, just return the existing child;
					4)	otherwise, create a new child node, insert in the children list and return finally.
		"""
		## 1. if the feature is in root-node path, return the node itself
		node = self
		while not (node.is_root()):
			if node.feature == feature:
				return self
		## 2. if the feature is smaller than root-node path's features, return None
		if feature < self.feature:
			return None
		## 3. if there exists child using the input feature, return the existing one
		for child in self.children:
			child: MerPredictRuleTreeNode
			if child.feature == feature:
				return child
		## 4. otherwise, create a new child and add it in the tail of the children
		child = MerPredictRuleTreeNode(self.tree, self, feature)
		self.children.append(child)
		return child

	# tree getters

	def get_tree(self):
		"""
		:return: the hierarchical tree where the node is created
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the node is root without parent.
		"""
		return self.parent is None

	def get_parent(self):
		"""
		:return: the parent of this node or None if the node is root
		"""
		if self.parent is None:
			return None
		else:
			self.parent: MerPredictRuleTreeNode
			return self.parent

	def is_leaf(self):
		"""
		:return: whether the node is leaf without any child
		"""
		return len(self.children) == 0

	def number_of_children(self):
		"""
		:return: the number of child node created under this node
		"""
		return len(self.children)

	def get_children(self):
		"""
		:return: the child nodes created under this node
		"""
		return self.children

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child under this node's children list
		"""
		if (k < 0) or (k >= len(self.children)):
			return None
		else:
			child = self.children[k]
			child: MerPredictRuleTreeNode
			return child

	# data getters

	def get_executions(self):
		"""
		:return: the set of executions that are matched with this node
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which execution(s) match with this node
		"""
		mutants = set()
		for execution in self.executions:
			execution: jcenco.MerExecution
			mutants.add(execution.get_mutant())
		return mutants

	def __matched__(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return: whether the features in the execution match with this node
		"""
		node = self
		while not (node.is_root()):
			if node.feature in execution.get_features():
				node = node.get_parent()
			else:
				return False
		return True

	def __updates__(self):
		"""
		:return: update the executions matched with this node using data from its parent or the entire document
		"""
		if self.parent is None:
			parent_executions = self.tree.get_document().exec_space.get_executions()
		else:
			parent_executions = self.parent.executions
		self.executions.clear()
		for execution in parent_executions:
			execution: jcenco.MerExecution
			if self.__matched__(execution):
				self.executions.add(execution)
		return

	# feature getters

	def get_features(self):
		"""
		:return: the sequence of integer features encoding the symbolic conditions included in the rule
		"""
		features = list()
		node = self
		while not (node.is_root()):
			features.append(node.feature)
			node = node.get_parent()
		features.reverse()
		return features

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions included in the rule of the node
		"""
		conditions = set()
		document = self.tree.get_document()
		node = self
		while not (node.is_root()):
			condition = document.cond_space.get_condition(node.feature)
			conditions.add(condition)
			node = node.get_parent()
		return conditions

	def __len__(self):
		"""
		:return: the depth of the node in the tree or 0 if it is the root
		"""
		length = 0
		node = self
		while not (node.is_root()):
			node = node.get_parent()
			length += 1
		return length

	def __str__(self):
		return str(self.get_features())

	# prediction made

	def predict(self, used_tests):
		"""
		:param used_tests: the collection of tests (or int) to decide whether a mutant under the rule is killed or not
		:return: 	result, killed, alive, confidence
					1) result:	true if the rule predicts the mutant w.r.t. the conditions are killable or not;
					2) killed:	the number of executions of which mutants are killed by used_tests;
					3) alive:	the number of executions of which mutants survived from used_tests;
					4) confidence:	the probability to believe the prediction made by this rule is correct on tree.
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			execution: jcenco.MerExecution
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		if killed > alive:
			result = True
		elif killed < alive:
			result = False
		else:
			result = None
		total = killed + alive
		if total > 0:
			confidence = max(killed, alive) / total
		else:
			confidence = 0.0
		return result, killed, alive, confidence

	def evaluate(self, used_tests):
		"""
		:param used_tests: the collection of tests (or int) to decide whether a mutant under the rule is killed or not
		:return: 	length, support, confidence
					1) length:	the length of the tree node (depth)
					2) support:	the number of executions matching with the tree node
					3) confidence: the probability to believe the rule as valid to predict alive
		"""
		length = len(self)
		killed, alive = 0, 0
		for execution in self.executions:
			execution: jcenco.MerExecution
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		total = killed + alive
		support = alive
		if support > 0:
			confidence = support / total
		else:
			confidence = 0.0
		return length, support, confidence


## INPUT LAYER


class MerPredictionInputs:
	"""
	It manages the document source and parameters used for mining algorithm as inputs.
	"""

	def __init__(self, document: jcenco.MerDocument, max_length: int, min_support: int,
				 min_confidence: float, max_confidence: float, min_output_rules: int):
		"""
		:param document: 			the document provides memory-reduced data samples for building prediction rules
		:param max_length: 			the maximal length (or depth) allowed for constructing tree nodes in prediction
		:param min_support: 		the minimal support of matching executions within the good prediction tree node
		:param min_confidence: 		the minimal confidence required for the good prediction tree node create within
		:param max_confidence: 		the maximal confidence is used to terminate the searching on the tree
		:param min_output_rules: 	the minimal number of rules being selected as good from the output solutions
		"""
		self.document = document
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.min_output_rules = min_output_rules
		return

	# parameters

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

	def get_min_output_rules(self):
		return self.min_output_rules


## MIDDLE LAYER


class MerPredictionMiddle:
	"""
	The middle layer of the mining algorithm preserves the tree model to describe killable prediction rules.
	"""

	def __init__(self, inputs: MerPredictionInputs):
		self.inputs = inputs
		self.tree = MerPredictRuleTree(self.inputs.get_document())
		return

	# getters

	def get_document(self):
		return self.inputs.get_document()

	def get_inputs(self):
		return self.inputs

	def get_tree(self):
		return self.tree

	def get_root(self):
		return self.tree.get_root()

	def get_child(self, parent: MerPredictRuleTreeNode, feature: int):
		return self.tree.get_child(parent, feature)

	def extract_good_rules(self, used_tests):
		"""
		:param used_tests:
		:return:
		"""
		node_evaluation_dict = dict()
		for node in self.tree.get_nodes():
			node: MerPredictRuleTreeNode
			length, support, confidence = node.evaluate(used_tests)
			if length <= self.inputs.get_max_length() and support >= self.inputs.get_min_support() and confidence >= self.inputs.get_min_confidence():
				node_evaluation_dict[node] = (length, support, confidence)
		return node_evaluation_dict


## UTILITY METHOD


def new_feature_list(features):
	"""
	:param features: the collection of integers encoding the symbolic conditions
	:return:
	"""
	feature_list = list()
	for feature in features:
		feature: int
		if not (feature in feature_list):
			feature_list.append(feature)
	feature_list.sort()
	return feature_list


def extract_mutant_features(document: jcenco.MerDocument, mutants):
	"""
	:param document: data source
	:param mutants: the collection of Mutant of which execution features are collected
	:return:
	"""
	features = set()
	for mutant in mutants:
		for execution in document.exec_space.get_executions_of(mutant):
			execution: jcenco.MerExecution
			for feature in execution.get_features():
				features.add(feature)
	return new_feature_list(features)


def extract_used_tests_for(mutant: jcenco.MerMutant, max_length: int):
	"""
	:param mutant:
	:param max_length: maximal number of tests being selected randomly
	:return: the set of tests that fail to kill the target mutant
	"""
	tests = mutant.get_result().get_tests_of(False)
	if len(tests) > max_length:
		random_tests = set()
		while len(random_tests) < max_length:
			test = jcbase.rand_select(tests)
			test: int
			random_tests.add(test)
		tests.clear()
		for test in random_tests:
			tests.append(test)
		return tests
	else:
		return tests


def sort_rules_by_length(node_evaluation_dict: dict):
	"""
	:param node_evaluation_dict: mapping from MerPredictRuleTreeNode to [length, support, confidence]
	:return: the sorted sequence of tree nodes using the evaluation results as given
	"""
	key_dict, key_list = dict(), list()
	for node, evaluation in node_evaluation_dict.items():
		node: MerPredictRuleTreeNode
		length = evaluation[0]
		length: int
		if not (length in key_dict):
			key_dict[length] = set()
			key_list.append(length)
		key_dict[length].add(node)
	key_list.sort()
	sort_list = list()
	for key in key_list:
		for node in key_dict[key]:
			node: MerPredictRuleTreeNode
			sort_list.append(node)
	return sort_list


def sort_rules_by_support(node_evaluation_dict: dict):
	"""
	:param node_evaluation_dict: mapping from MerPredictRuleTreeNode to [length, support, confidence]
	:return:
	"""
	key_dict, key_list = dict(), list()
	for node, evaluation in node_evaluation_dict.items():
		node: MerPredictRuleTreeNode
		support = evaluation[1]
		support: int
		if not (support in key_dict):
			key_dict[support] = set()
			key_list.append(support)
		key_dict[support].add(node)
	key_list.sort(reverse=True)
	sort_list = list()
	for key in key_list:
		for node in key_dict[key]:
			node: MerPredictRuleTreeNode
			sort_list.append(node)
	return sort_list


def sort_rules_by_confidence(node_evaluation_dict: dict):
	"""
	:param node_evaluation_dict: mapping from MerPredictRuleTreeNode to [length, support, confidence]
	:return:
	"""
	key_dict, key_list = dict(), list()
	for node, evaluation in node_evaluation_dict.items():
		node: MerPredictRuleTreeNode
		confidence = evaluation[2]
		confidence: float
		key = int(confidence * 10000)
		if not (key in key_dict):
			key_dict[key] = set()
			key_list.append(key)
		key_dict[key].add(node)
	key_list.sort(reverse=True)
	sort_list = list()
	for key in key_list:
		for node in key_dict[key]:
			node: MerPredictRuleTreeNode
			sort_list.append(node)
	return sort_list


def precision_recall_evaluate(orig_samples: set, pred_samples: set):
	"""
	:param orig_samples:
	:param pred_samples:
	:return: precision, recall, f1_score
	"""
	como_samples = orig_samples & pred_samples
	if len(como_samples) > 0:
		precision = len(como_samples) / len(pred_samples)
		recall = len(como_samples) / len(orig_samples)
		f1_score = 2 * precision * recall / (precision + recall)
		return precision, recall, f1_score
	else:
		return 0.0, 0.0, 0.0


## MINING ALGORITHM


class MerPredictionMiner:
	"""
	It implements the association rule mining algorithm for generating good rules for prediction.
	"""

	def __init__(self, inputs: MerPredictionInputs):
		"""
		:param inputs:
		"""
		self.middle = MerPredictionMiddle(inputs)
		self.solutions = dict()		# MerPredictRuleTreeNode --> {length, support, confidence}
		return

	def __mine__(self, parent: MerPredictRuleTreeNode, features: list, used_tests):
		"""
		:param parent:
		:param features:
		:param used_tests:
		:return:
		"""
		if not (parent in self.solutions):
			length, support, confidence = parent.evaluate(used_tests)
			self.solutions[parent] = (length, support, confidence)
		solution = self.solutions[parent]
		length = solution[0]
		support = solution[1]
		confidence = solution[2]
		if length < self.middle.get_inputs().get_max_length() and support >= self.middle.get_inputs().get_min_support() and confidence < self.middle.get_inputs().get_max_confidence():
			for k in range(0, len(features)):
				child = self.middle.get_child(parent, features[k])
				if not (child is None) and (child != parent):
					self.__mine__(child, features[k + 1: ], used_tests)
		return

	def __outs__(self):
		"""
		:return:
		"""
		node_evaluation_dict = dict()
		for node, evaluation in self.solutions.items():
			node: MerPredictRuleTreeNode
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			length: int
			support: int
			confidence: float
			if length <= self.middle.get_inputs().get_max_length() and \
					support >= self.middle.get_inputs().get_min_support() and \
					confidence >= self.middle.get_inputs().get_min_confidence():
				node_evaluation_dict[node] = (length, support, confidence)
		if len(node_evaluation_dict) < self.middle.get_inputs().get_min_output_rules():
			sort_nodes = sort_rules_by_confidence(self.solutions)
			for node in sort_nodes:
				evaluation = self.solutions[node]
				length = evaluation[0]
				support = evaluation[1]
				confidence = evaluation[2]
				length: int
				support: int
				confidence: float
				node_evaluation_dict[node] = (length, support, confidence)
				if len(node_evaluation_dict) >= self.middle.get_inputs().get_min_output_rules():
					break
		return node_evaluation_dict

	def mine(self, features, used_tests):
		"""
		:param features:
		:param used_tests:
		:return:
		"""
		# 1. initialization
		feature_list = new_feature_list(features)
		self.solutions.clear()
		root_node = self.middle.get_root()

		# 2. start recursive mining
		if used_tests is None:
			used_tests_size = None
		else:
			used_tests_size = len(used_tests)
		print("\t\t\tMining: IN[{}; {}]".format(len(feature_list), used_tests_size), end="")
		self.__mine__(root_node, feature_list, used_tests)
		node_evaluation_dict = self.__outs__()
		print(" ==> OUT[{}; {}/{}]".format(len(node_evaluation_dict), len(self.solutions), len(self.middle.get_tree())))

		# 3. output results
		self.solutions.clear()
		return node_evaluation_dict

	def mine_for(self, mutants, used_tests):
		"""
		:param mutants: the collection of mutants being mined
		:param used_tests:
		:return:
		"""
		features = new_feature_list(extract_mutant_features(self.middle.get_document(), mutants))
		return self.mine(features, used_tests)


class MerPredictionOutput:
	"""
	It manages the evaluation of rule outputs
	"""

	def __init__(self, c_document: jctest.CDocument, m_document: jcenco.MerDocument):
		self.c_document = c_document
		self.m_document = m_document
		self.writer = None
		return

	def __output__(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		self.writer.flush()
		return

	def __mut2str__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: id result class operator function line code parameter
		"""
		orig_mutant = self.c_document.project.muta_space.get_mutant(mutant.get_mid())
		mid = orig_mutant.get_muta_id()
		result = orig_mutant.get_result().is_killed_in(None)
		if result:
			result = "Killed"
		else:
			result = "Survive"
		m_class = orig_mutant.get_mutation().get_mutation_class()
		operator = orig_mutant.get_mutation().get_mutation_operator()
		location = orig_mutant.get_mutation().get_location()
		line = location.line_of(tail=False) + 1
		definition = location.function_definition_of()
		code = "\"{}\"".format(location.get_code(True))
		def_code = definition.get_code(True)
		index = def_code.index('(')
		fun_code = def_code[0: index].strip()
		parameter = orig_mutant.get_mutation().get_parameter()
		return "{}\t{}\t{}\t{}\t{}\t{}\t{}\t[{}]".format(mid, result, m_class, operator, fun_code, line, code,
														 parameter)

	def __nod2str__(self, node: MerPredictRuleTreeNode, evaluation):
		"""
		:param node:
		:param evaluation: length, support, confidence
		:return: features length support confidence(%) executions mutants
		"""
		self.c_document = self.c_document
		features = node.get_features()
		length = evaluation[0]
		support = evaluation[1]
		confidence = int(evaluation[2] * 10000) / 100.0
		executions = len(node.get_executions())
		mutants = len(node.get_mutants())
		return "{}\t{}\t{}\t{}%\t{}\t{}".format(str(features), length, support, confidence, executions, mutants)

	def __cod2str__(self, condition: jcenco.MerCondition):
		"""
		:param condition:
		:return: category operator execution statement location parameter
		"""
		sym_condition = self.c_document.conditions.get_condition(condition.get_code())
		return "{}\t{}\t{}\t\"{}\"\t\"{}\"\t[{}]".format(sym_condition.get_category(), sym_condition.get_operator(),
														 sym_condition.get_execution(),
														 sym_condition.get_execution().get_statement().get_cir_code(),
														 sym_condition.get_location().get_cir_code(),
														 sym_condition.get_parameter())

	## write mutant-rules pair

	def __write_mutant_rules__(self, miner: MerPredictionMiner, mutant: jcenco.MerMutant,
							   min_used_tests: int, max_print_size: int):
		"""
		:param miner:
		:param mutant:
		:param min_used_tests: the minimal number of tests used for mining
		:param max_print_size: the maximal number of rules being printed
		:return:
		"""
		node_evaluation_dict = miner.mine_for([mutant], extract_used_tests_for(mutant, min_used_tests))
		good_rules = sort_rules_by_support(node_evaluation_dict)
		if (max_print_size > 0) and (len(good_rules) > max_print_size):
			good_rules = good_rules[0: max_print_size]
		self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
		for rule in good_rules:
			self.__output__("\t[R]\t{}\n".format(self.__nod2str__(rule, node_evaluation_dict[rule])))
			index = 0
			for condition in rule.get_conditions():
				self.__output__("\t\t[C.{}]\t{}\n".format(index, self.__cod2str__(condition)))
				index += 1
		self.__output__("\n")
		return

	def write_mutants_rules(self, file_path: str, inputs: MerPredictionInputs, mutants,
							min_used_tests: int, max_print_size: int):
		"""
		:param max_print_size:
		:param min_used_tests:
		:param file_path:
		:param inputs:
		:param mutants: the set of mutants being used to generate prediction rules
		:return:
		"""
		miner = MerPredictionMiner(inputs)
		with open(file_path, 'w') as writer:
			self.writer = writer
			index = 0
			for mutant in mutants:
				index += 1
				print("\t\t-->\tMining on progress: [{}/{}]".format(index, len(mutants)))
				self.__write_mutant_rules__(miner, mutant, min_used_tests, max_print_size)
		return miner

	def write_predict_rules(self, file_path: str, inputs: MerPredictionInputs, mutants):
		"""
		:param file_path:
		:param inputs:
		:param mutants:
		:return: the absolute prediction rules for undetected mutants
		"""
		miner = MerPredictionMiner(inputs)
		node_evaluation_dict = miner.mine_for(mutants, None)
		with open(file_path, 'w') as writer:
			self.writer = writer
			for node, evaluation in node_evaluation_dict.items():
				self.__output__("BEG_RULE\n")
				self.__output__("\t[R]\t{}\n".format(self.__nod2str__(node, evaluation)))
				index = 0
				for condition in node.get_conditions():
					index += 1
					self.__output__("\t\t[C.{}]\t{}\n".format(index, self.__cod2str__(condition)))
				node_mutants = node.get_mutants()
				self.__output__("\t[S]\t{} mutants\n".format(len(node_mutants)))
				index = 0
				for mutant in node_mutants:
					index += 1
					self.__output__("\t\t[M.{}]\t{}\n".format(index, self.__mut2str__(mutant)))
				self.__output__("END_RULE\n")
				self.__output__("\n")
		return miner

	def write_predict_trees(self, file_path: str, middle: MerPredictionMiddle):
		"""
		:param file_path:
		:param middle:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			orig_samples, pred_samples = set(), set()
			for mutant in middle.inputs.get_document().muta_space.get_mutants():
				mutant: jcenco.MerMutant
				if not (mutant.get_result().is_killed_in(None)):
					orig_samples.add(mutant)
			tree_nodes = middle.extract_good_rules(None)
			for tree_node in tree_nodes.keys():
				for mutant in tree_node.get_mutants():
					pred_samples.add(mutant)
			precision, recall, f1_score = precision_recall_evaluate(orig_samples, pred_samples)
			self.__output__("BEG_SUM\n")
			self.__output__("\tPREDICT_RULE:\t{}\t({}%)\n".format(len(tree_nodes),
																  int(len(tree_nodes) / len(orig_samples) * 10000) / 100.0))
			self.__output__("\tORIG_MUTANTS:\t{}\n".format(len(orig_samples)))
			self.__output__("\tPRED_MUTANTS:\t{}\n".format(len(pred_samples)))
			self.__output__("\tPRECISION(%):\t{}%\n".format(int(precision * 10000) / 100.0))
			self.__output__("\tRECALLING(%):\t{}%\n".format(int(recall * 10000) / 100.0))
			self.__output__("\tF1_SCORES(%):\t{}\n".format(f1_score))
			self.__output__("END_SUM\n")
			self.__output__("\n")
			self.__output__("Rule\tLength\tSupport\tConfidence(%)\tExec_Number\tMuta_Number\n")
			for tree_node in tree_nodes:
				tree_node: MerPredictRuleTreeNode
				length, support, confidence = tree_node.evaluate(None)
				exec_number = len(tree_node.get_executions())
				muta_number = len(tree_node.get_mutants())
				self.__output__("{}\t{}\t{}\t{}%\t{}\t{}\n".format(str(tree_node),
																   length, support,
																   int(confidence * 10000) / 100.0,
																   exec_number, muta_number))
			self.__output__("\n")
		return


def main(features_directory: str, encoding_directory: str, postfix: str, output_directory: str, select_alive: bool):
	"""
	:param features_directory:
	:param encoding_directory:
	:param postfix:
	:param output_directory:
	:param select_alive:
	:return:
	"""
	max_length, min_support, min_confidence, max_confidence, min_good_rules, max_print_size = 1, 1, 0.70, 0.90, 3, 8
	for file_name in os.listdir(features_directory):
		## 1. load documents
		inputs_directory = os.path.join(features_directory, file_name)
		encode_directory = os.path.join(encoding_directory, file_name)
		c_document = jctest.CDocument(inputs_directory, file_name, postfix)
		m_document = jcenco.MerDocument(encode_directory, file_name)
		print("Testing on", file_name)
		print("\tSummary:{} mutants\t{} executions\t{} conditions.".format(len(m_document.muta_space.get_mutants()),
																		   len(m_document.exec_space.get_executions()),
																		   len(m_document.cond_space.get_conditions())))
		## 2. construct mining machine
		inputs = MerPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence, min_good_rules)
		mutants = set()
		for mutant in m_document.exec_space.get_mutants():
			mutant: jcenco.MerMutant
			if select_alive:
				if not (mutant.get_result().is_killed_in(None)):
					mutants.add(mutant)
			else:
				mutants.add(mutant)
		output = MerPredictionOutput(c_document, m_document)

		## 3. output information to directory
		output.write_mutants_rules(os.path.join(output_directory, file_name + ".mur"), inputs, mutants, 100, max_print_size)
		miner = output.write_predict_rules(os.path.join(output_directory, file_name + ".pur"), inputs, mutants)
		output.write_predict_trees(os.path.join(output_directory, file_name + ".tur"), miner.middle)
		print("\tOutput all prediction rules to directory...")
		print()
	return


if __name__ == "__main__":
	features_dir = "/home/dzt2/Development/Data/zexp/features"
	encoding_dir = "/home/dzt2/Development/Data/zexp/encoding"
	output_dir = "/home/dzt2/Development/Data/zexp/rules"
	main(features_dir, encoding_dir, ".sip", output_dir, False)

