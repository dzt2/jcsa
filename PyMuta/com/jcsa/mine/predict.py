"""This file defines the model of killable prediction rules and implements algorithm for mining them."""


import os

from typing.io import TextIO

import com.jcsa.libs.base 	as jcbase
import com.jcsa.libs.test 	as jctest
import com.jcsa.mine.encode as jcenco


## RULE MODEL


def new_feature_list(features):
	"""
	:param features: the set of integer features encoding the root-node path
	:return: the sorted sequence of integer features created from the inputs
	"""
	feature_list = list()
	for feature in features:
		feature: int
		if not (feature in feature_list):
			feature_list.append(feature)
	return feature_list


class KillPredictRuleTree:
	"""
	It describes the killable prediction rules being generated using a hierarchical structure.
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the memory-reduced document provides direct data source
		"""
		self.document = m_document
		self.root = KillPredictRuleNode(self, None, -1)
		return

	# getters

	def get_document(self):
		"""
		:return: the memory-reduced document provides direct data source
		"""
		return self.document

	def get_root(self):
		"""
		:return: the root node of this tree
		"""
		return self.root

	def get_node(self, features):
		"""
		:param features: the set of integer features encoding the root-node path
		:return: the tree node extended from the root until using input features
		"""
		feature_list = new_feature_list(features)
		node = self.get_root()
		for feature in feature_list:
			node = node.__extends__(feature)
		return node

	def get_child(self, parent, feature: int):
		"""
		:param parent:
		:param feature:
		:return:
		"""
		parent: KillPredictRuleNode
		if parent.get_tree() == self:
			return parent.__extends__(feature)
		else:
			features = parent.get_features()
			features.append(feature)
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
		node: KillPredictRuleNode
		for child in node.get_children():
			KillPredictRuleTree.__get_nodes_in__(child, nodes)
		return

	@staticmethod
	def __len_nodes_in__(node):
		"""
		:param node:
		:return: compute the number of nodes in the sub-tree of which root is the input node
		"""
		node: KillPredictRuleNode
		counter = 1
		for child in node.get_children():
			counter += KillPredictRuleTree.__len_nodes_in__(child)
		return counter

	def __len__(self):
		"""
		:return: the number of tree nodes created under this tree
		"""
		return KillPredictRuleTree.__len_nodes_in__(self.get_root())

	def get_nodes(self):
		"""
		:return: the set of all the tree nodes created under the tree
		"""
		nodes = set()
		KillPredictRuleTree.__get_nodes_in__(self.root, nodes)
		return nodes


class KillPredictRuleNode:
	"""
	It represents the node in the tree for constructing killable prediction rules.
	"""

	def __init__(self, tree: KillPredictRuleTree, parent, feature: int):
		"""
		:param tree: 	the hierarchical structural tree for constructing killable prediction rules
		:param parent: 	the parent node under which this node is created or None if it is the root
		:param feature: the integer feature encoding symbolic condition links from parent to this
		"""
		self.tree = tree			# the hierarchical structural tree for constructing killable prediction rules
		self.parent = parent		# the parent node under which this node is created or None if it is the root
		self.feature = feature		# the integer feature encoding symbolic condition links from parent to this
									# node or meaningless if the node is a parent, which is established as -1.
		self.children = list()		# the collection of children nodes extended from this node in the tree uniquely.
		self.executions = set()		# the set of memory-reduced symbolic executions matching with rule of the node.
		self.__updates__()			# update the data samples from its parent incrementally
		return

	# node setters

	def __matched__(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return: True if the features in the root-node path are incorporated in the execution
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
		:return: update the executions matching with the rule of this node using its parent
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

	def __extends__(self, feature: int):
		"""
		:param feature: the integer feature inserted to the node-child path
		:return: 	there are four cases for creating a new child extended from this one
					(1)	if the feature is in the root-node path, return the node itself;
					(2) if the feature is smaller than its parent-self feature, return None;
					(3)	if the feature is in any existing self-child path, return child;
					(4)	otherwise, create a new child under this node using the feature.
		"""
		## 1. return the node itself if the feature is in the root-node path
		node = self
		while not node.is_root():
			if node.feature == feature:
				return self
			else:
				node = node.get_parent()
		## 2. return None if the feature is invalid and smaller than parent
		if feature < self.feature:
			return None
		## 3. return existing child node if the feature refers to any child
		for child in self.children:
			child: KillPredictRuleNode
			if child.feature == feature:
				return child
		## 4. create a new node as the child of this node inserting feature
		child = KillPredictRuleNode(self.tree, self, feature)
		self.children.append(child)
		return child

	# tree getters

	def get_tree(self):
		"""
		:return: the hierarchical structural tree for constructing killable prediction rules
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the node is a root without any parent and features in the path
		"""
		if self.parent is None:
			return True
		else:
			return False

	def get_parent(self):
		"""
		:return: the parent node under which this node is created or None if it is the root
		"""
		if self.parent is None:
			return None
		else:
			self.parent: KillPredictRuleNode
			return self.parent

	def number_of_children(self):
		"""
		:return: the number of children extended from this tree node
		"""
		return len(self.children)

	def get_children(self):
		"""
		:return: the collection of children nodes extended from this node in the tree uniquely.
		"""
		return self.children

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child node extended from this node
		"""
		if (k < 0) or (k >= len(self.children)):
			return None
		else:
			child = self.children[k]
			child: KillPredictRuleNode
			return child

	# data getters

	def get_executions(self):
		"""
		:return: the set of memory-reduced symbolic executions of which results are predicted by this rule
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which test results will be predicted using this rule.
		"""
		mutants = set()
		for execution in self.executions:
			execution: jcenco.MerExecution
			mutants.add(execution.get_mutant())
		return mutants

	def predict(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase (or its unique integer tid) to kill the target mutant
		:return: 	result, killed, alive, confidence
					1.	result:	whether the rule predicts the mutant as killed or not
					2.	killed:	the number of executions of which test results are killed
					3.	alive:	the number of executions of which test results are alive
					4.	confidence:	the probability to consider the prediction as correct
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
		support = max(killed, alive)
		if support > 0:
			confidence = support / (killed + alive)
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
		result, killed, alive, p_confidence = self.predict(used_tests)
		support, total = alive, killed + alive
		if support > 0:
			confidence = support / total
		else:
			confidence = 0.0
		return length, support, confidence

	# feature model

	def get_features(self):
		"""
		:return: the sequence of integer features encoding conditions in the root-node path for presenting the rule
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
		:return: the set of symbolic conditions incorporated in the root-node path of the prediction rule
		"""
		conditions = set()
		node = self
		document = self.tree.get_document()
		while not (node.is_root()):
			condition = document.cond_space.get_condition(node.feature)
			conditions.add(condition)
			node = node.get_parent()
		return conditions

	def __len__(self):
		"""
		:return: the depth of the prediction rule
		"""
		node = self
		length = 0
		while not (node.is_root()):
			node = node.get_parent()
			length += 1
		return length

	def __str__(self):
		return str(self.get_features())


## INPUT LAYER


class KillPredictionInputs:
	"""
	The inputs manage the parameters and document data source used for mining killable prediction rules.
	"""

	def __init__(self, m_document: jcenco.MerDocument, max_length: int,
				 min_support: int, min_confidence: float, max_confidence: float,
				 min_output_rules: int):
		"""
		:param m_document: 			the document provides memory-reduced data samples for building prediction rules
		:param max_length: 			the maximal length (or depth) allowed for constructing tree nodes in prediction
		:param min_support: 		the minimal support of matching executions within the good prediction tree node
		:param min_confidence: 		the minimal confidence required for the good prediction tree node create within
		:param max_confidence: 		the maximal confidence is used to terminate the searching on the tree
		:param min_output_rules:	the minimal number of killable prediction rules being generated for each mutant
		"""
		self.document = m_document
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.min_output_rules = min_output_rules
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

	def get_min_output_rules(self):
		return self.min_output_rules


## MIDDLE LAYER

class KillPredictionMiddle:
	"""
	The middle layer of the mining algorithm preserves the tree model to describe killable prediction rules.
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.inputs = inputs
		self.tree = KillPredictRuleTree(self.inputs.get_document())
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

	def get_child(self, parent: KillPredictRuleNode, feature: int):
		return self.tree.get_child(parent, feature)

	def extract_good_rules(self, used_tests):
		"""
		:param used_tests:
		:return:
		"""
		node_evaluation_dict = dict()
		for node in self.tree.get_nodes():
			node: KillPredictRuleNode
			length, support, confidence = node.evaluate(used_tests)
			if length <= self.inputs.get_max_length() and support >= self.inputs.get_min_support() and confidence >= self.inputs.get_min_confidence():
				node_evaluation_dict[node] = (length, support, confidence)
		return node_evaluation_dict


## SELECTION METHOD


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


def extract_used_tests_from(mutant: jcenco.MerMutant, max_length: int):
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


def __sort_rules_by_key__(nodes, node_evaluation_dict: dict, key_index: int, reverse: bool):
	"""
	:param nodes: the set of nodes being sorted
	:param node_evaluation_dict: provide evaluation results of nodes
	:param key_index: the index of evaluation key to sort node
	:param reverse: whether to sort nodes in reversed sequence
	:return: sorted list of nodes
	"""
	key_list, key_dict = list(), dict()
	for node in nodes:
		node: KillPredictRuleNode
		evaluation = node_evaluation_dict[node]
		key = evaluation[key_index]
		if isinstance(key, float):
			key = int(key * 10000)
		else:
			key: int
		if not (key in key_dict):
			key_dict[key] = set()
			key_list.append(key)
		key_dict[key].add(node)
	key_list.sort(reverse=reverse)
	sort_list = list()
	for key in key_list:
		for node in key_dict[key]:
			node: KillPredictRuleNode
			sort_list.append(node)
	return sort_list


def sort_prediction_rules(nodes, node_evaluation_dict: dict):
	"""
	:param nodes:
	:param node_evaluation_dict:
	:return:
	"""
	c_sorted_nodes = __sort_rules_by_key__(nodes, node_evaluation_dict, 2, True)
	t_sorted_nodes, s_sorted_nodes, cur_confidence = list(), list(), None
	for node in c_sorted_nodes:
		evaluation = node_evaluation_dict[node]
		confidence = evaluation[2]
		confidence: float
		if cur_confidence is None:
			cur_confidence = confidence
		if cur_confidence == confidence:
			s_sorted_nodes.append(node)
		else:
			s_sorted_nodes = __sort_rules_by_key__(s_sorted_nodes, node_evaluation_dict, 1, True)
			for s_node in s_sorted_nodes:
				t_sorted_nodes.append(s_node)
			s_sorted_nodes = list()
	s_sorted_nodes = __sort_rules_by_key__(s_sorted_nodes, node_evaluation_dict, 1, True)
	for s_node in s_sorted_nodes:
		t_sorted_nodes.append(s_node)
	return t_sorted_nodes


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


class KillPredictionMiner:
	"""
	It implements the mining algorithm to discover killable prediction rules.
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.middle = KillPredictionMiddle(inputs)
		self.solutions = dict()	# it preserves the cached evaluation result for generated rule nodes.
		return

	def __pass__(self, node: KillPredictRuleNode):
		"""
		:param node:
		:return: whether the node is pass-able
		"""
		if node.is_root():
			return True
		else:
			solution = self.solutions[node]
			length = solution[0]
			support = solution[1]
			confidence = solution[2]
			return length < self.middle.get_inputs().get_max_length() and \
				   support >= self.middle.get_inputs().get_min_support() and \
				   confidence <= self.middle.get_inputs().get_max_confidence()

	def __mine__(self, parent: KillPredictRuleNode, features: list, used_tests):
		"""
		:param parent:		the parent tree node being mined recursively
		:param features:	the set of features being iterated in traversal
		:param used_tests:	the set of tests used for evaluating tree nodes
		:return:			recursively mining the killable prediction rule
		"""
		if not (parent in self.solutions):
			length, support, confidence = parent.evaluate(used_tests)
			self.solutions[parent] = (length, support, confidence)
		if self.__pass__(parent):
			for k in range(0, len(features)):
				child = self.middle.get_child(parent, features[k])
				if not (child is None) and (child != parent):
					self.__mine__(child, features[k + 1: ], used_tests)
		return

	def __outs__(self):
		"""
		:return: mapping from prediction rule node to the evaluation results computed in mining.
		"""
		node_evaluation_dict = dict()
		for node, solution in self.solutions.items():
			node: KillPredictRuleNode
			length = solution[0]
			support = solution[1]
			confidence = solution[2]
			length: int
			support: int
			confidence: float
			if length <= self.middle.get_inputs().get_max_length() and \
					support >= self.middle.get_inputs().get_min_support() and \
					confidence >= self.middle.get_inputs().get_min_confidence():
				node_evaluation_dict[node] = (length, support, confidence)
		if len(node_evaluation_dict) < self.middle.get_inputs().get_min_output_rules():
			sort_nodes = sort_prediction_rules(self.solutions.keys(), self.solutions)
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

		# 2. start recursive mining
		if used_tests is None:
			used_tests_size = None
		else:
			used_tests_size = len(used_tests)
		print("\t\t\tMining: IN[{}; {}]".format(len(feature_list), used_tests_size), end="")
		root_node = self.middle.get_root()
		self.__mine__(root_node, feature_list, used_tests)
		node_evaluation_dict = self.__outs__()
		print(" ==> OUT[{}; {}/{}]".format(len(node_evaluation_dict), len(self.solutions), len(self.middle.get_tree())))

		# 3. output results
		self.solutions.clear()
		return node_evaluation_dict


## OUTPUT LAYER


class KillPredictionOutput:
	"""
	It generates output of the killable prediction rules.
	"""

	def __init__(self, c_document: jctest.CDocument, m_document: jcenco.MerDocument):
		self.c_document = c_document
		self.m_document = m_document
		self.writer = None
		return

	# basic methods

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

	def __nod2str__(self, node: KillPredictRuleNode, evaluation):
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

	# mining-on-fly output

	def __do_mining_mutant_rules__(self, miner: KillPredictionMiner, mutant: jcenco.MerMutant,
							   max_used_tests_number: int, max_print_size: int):
		"""
		:param miner:
		:param mutant:
		:param max_print_size:
		:return: node_evaluation_dict, sorted_tree_nodes
		"""
		self.c_document = self.c_document
		used_tests = extract_used_tests_from(mutant, max_used_tests_number)
		features = extract_mutant_features(miner.middle.get_document(), [mutant])
		node_evaluation_dict = miner.mine(features, used_tests)
		sorted_tree_nodes = sort_prediction_rules(node_evaluation_dict.keys(), node_evaluation_dict)
		if (max_print_size > 0) and (len(sorted_tree_nodes) > max_print_size):
			sorted_tree_nodes = sorted_tree_nodes[0: max_print_size + 1]
		return node_evaluation_dict, sorted_tree_nodes

	# mutant-rules output

	def write_mutant_rule_maps(self, inputs: KillPredictionInputs, mutants,
							   mutant_rule_file: str, rule_mutant_file: str,
							   max_used_tests_number: int, max_print_size: int):
		"""
		:param inputs:
		:param mutants:
		:param mutant_rule_file:
		:param rule_mutant_file:
		:param max_used_tests_number:
		:param max_print_size:
		:return:
		"""
		miner = KillPredictionMiner(inputs)
		counter, rule_mutant_dict = 0, dict()
		with open(mutant_rule_file, 'w') as writer:
			self.writer = writer
			for mutant in mutants:
				mutant: jcenco.MerMutant
				counter += 1
				print("\t\tProcess on {}[{}/{}]".format(self.m_document.name, counter, len(mutants)))
				node_evaluation_dict, sorted_nodes = self.__do_mining_mutant_rules__(miner, mutant, max_used_tests_number, max_print_size)
				self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
				index = 0
				for tree_node in sorted_nodes:
					evaluation = node_evaluation_dict[tree_node]
					self.__output__("\t[R.{}]\t{}\n".format(index, self.__nod2str__(tree_node, evaluation)))
					index += 1
					index2 = 0
					for condition in tree_node.get_conditions():
						self.__output__("\t\t[C.{}.{}]\t{}\n".format(index, index2, self.__cod2str__(condition)))
						index2 += 1
				if len(sorted_nodes) > 0:
					rule = sorted_nodes[0]
					if not (rule in rule_mutant_dict):
						rule_mutant_dict[rule] = set()
					rule_mutant_dict[rule].add(mutant)
				self.__output__("\n")
		predicted_mutants_number = 0
		with open(rule_mutant_file, 'w') as writer:
			self.writer = writer
			for rule, rule_mutants in rule_mutant_dict.items():
				self.__output__("[R]: {} predicts for {} mutants.\n".format(str(rule), len(rule_mutants)))
				index = 0
				for condition in rule.get_conditions():
					self.__output__("\t[C.{}]\t{}\n".format(index, self.__cod2str__(condition)))
					index += 1
				index = 0
				for mutant in rule_mutants:
					self.__output__("\t\t[M.{}]\t{}\n".format(index, self.__mut2str__(mutant)))
					index += 1
				predicted_mutants_number += len(rule_mutants)
				self.__output__("\n")
			self.__output__("\nUse {} rules to predict for {} mutants ({}%).\n".
							format(len(rule_mutant_dict), predicted_mutants_number,
								   int(len(rule_mutant_dict) * 10000 / (predicted_mutants_number + 0.0001)) / 100.0))
		return

	def write_predict_rules(self, file_path: str, inputs: KillPredictionInputs, mutants):
		"""
		:param file_path:
		:param inputs:
		:param mutants:
		:return: the absolute prediction rules for undetected mutants
		"""
		miner = KillPredictionMiner(inputs)
		node_evaluation_dict = miner.mine(extract_mutant_features(self.m_document, mutants), None)
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

	def write_predict_trees(self, file_path: str, middle: KillPredictionMiddle):
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
				tree_node: KillPredictRuleNode
				length, support, confidence = tree_node.evaluate(None)
				exec_number = len(tree_node.get_executions())
				muta_number = len(tree_node.get_mutants())
				self.__output__("{}\t{}\t{}\t{}%\t{}\t{}\n".format(str(tree_node),
																   length, support,
																   int(confidence * 10000) / 100.0,
																   exec_number, muta_number))
			self.__output__("\n")
		return


## TESTING MAIN


def main(features_directory: str, encoding_directory: str, postfix: str, output_directory: str, select_alive: bool):
	"""
	:param features_directory:
	:param encoding_directory:
	:param postfix:
	:param output_directory:
	:param select_alive:
	:return:
	"""
	max_length, min_support, min_confidence, max_confidence, min_good_rules, max_print_size, min_used_tests = 1, 1, 0.70, 0.90, 3, 8, 128
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
		inputs = KillPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence, min_good_rules)
		mutants = set()
		for mutant in m_document.exec_space.get_mutants():
			mutant: jcenco.MerMutant
			if select_alive:
				if not (mutant.get_result().is_killed_in(None)):
					mutants.add(mutant)
			else:
				mutants.add(mutant)
		output = KillPredictionOutput(c_document, m_document)

		## 3. output information to directory
		rule_directory = os.path.join(output_directory, file_name)
		if not os.path.exists(rule_directory):
			os.mkdir(rule_directory)
		output.write_mutant_rule_maps(inputs, mutants, os.path.join(rule_directory, file_name + ".mur"),
									  os.path.join(rule_directory, file_name + ".bur"), 128, max_print_size)
		miner = output.write_predict_rules(os.path.join(rule_directory, file_name + ".pur"), inputs, mutants)
		output.write_predict_trees(os.path.join(rule_directory, file_name + ".tur"), miner.middle)
		print("\tOutput all prediction rules to directory...")
		print()
	return


## EXECUTE PART


if __name__ == "__main__":
	features_dir = "/home/dzt2/Development/Data/zexp/features"
	encoding_dir = "/home/dzt2/Development/Data/zexp/encoding"
	output_dir = "/home/dzt2/Development/Data/zexp/rules"
	main(features_dir, encoding_dir, ".sip", output_dir, False)

