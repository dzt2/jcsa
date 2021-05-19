"""This file defines the model of killable prediction rules and the algorithms for mining them."""


import os
from typing import TextIO
import com.jcsa.libs.base	as jcbase
import com.jcsa.libs.test	as jctest
import com.jcsa.mine.encode	as jcenco


## MODEL DEFINITIONS


def new_feature_sequence(features):
	"""
	:param features: the set of integer features encoding the prediction rule
	:return: the sorted sequence of integer features for defining a rule unique
	"""
	feature_list = list()
	for feature in features:
		feature: int
		if not (feature in feature_list):
			feature_list.append(feature)
	feature_list.sort()
	return feature_list


class KillPredictionTree:
	"""
	The hierarchical structural model uses tree to describe the killable prediction rules.
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the (memory-reduced) document provides the data source for being mined
		"""
		self.document = m_document
		self.root = KillPredictionNode(self, None, -1)
		return

	def get_document(self):
		"""
		:return: the (memory-reduced) document provides the data source for being mined
		"""
		return self.document

	def get_root(self):
		"""
		:return: the root node of the tree
		"""
		return self.root

	def get_node(self, features):
		"""
		:param features: the set of integer features defining a unique prediction rule
		:return:
		"""
		features = new_feature_sequence(features)
		node = self.root
		for feature in features:
			node = node.__extend__(feature)
		return node

	def get_child(self, parent, feature: int):
		"""
		:param parent: 	the parent under which the child is created or None to denote the root
		:param feature:
		:return: the child extended from the parent using input feature
		"""
		if parent is None:
			parent = self.root
		else:
			parent: KillPredictionNode
		return parent.__extend__(feature)

	def __len__(self):
		"""
		:return: the number of nodes created under the tree
		"""
		return self.root.__counts__(None)

	def get_nodes(self):
		"""
		:return: the collection of nodes created under the tree
		"""
		nodes = set()
		self.root.__counts__(nodes)
		return nodes


class KillPredictionNode:
	"""
	The node in tree represents a unique killable prediction rule used for predicting mutation fallibility.
	"""

	def __init__(self, tree: KillPredictionTree, parent, feature: int):
		"""
		:param tree: 	the tree where this node is created
		:param parent: 	the parent of this tree node or None if the node is root
		:param feature: the integer annotated on the edge from parent to this node or meaningless if the node is root.
		"""
		self.tree = tree		# the tree where this tree node is created
		self.parent = parent	# the parent of this node or None if it is root
		self.feature = feature	# the integer annotated on the edge from parent to this node
		self.children = list()	# the children extended from this node
		self.rule = KillPredictionRule(self)	# the killable prediction rule this node represents
		return

	# tree getters

	def get_tree(self):
		"""
		:return: the tree where this tree node is created
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the node is a root without any parent
		"""
		return self.parent is None

	def is_leaf(self):
		"""
		:return: whether the node is a leaf without any child
		"""
		return len(self.children) == 0

	def get_parent(self):
		"""
		:return: the parent of this node
		"""
		if self.parent is None:
			return None
		else:
			self.parent: KillPredictionNode
			return self.parent

	def get_children(self):
		"""
		:return: the children extended from this node
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children extended from this node
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child extended from this node
		"""
		child = self.children[k]
		child: KillPredictionNode
		return child

	def get_rule(self):
		"""
		:return: the killable prediction rule this node represents
		"""
		return self.rule

	def __extend__(self, feature: int):
		"""
		:param feature: the integer used to create an edge from this node to the child annotated
		:return: 	Create a child from this node using the edge annotated with input integer:
					(1)	if the feature is in the edges from root to this node, return the node itself;
					(2) else if the feature is smaller than input edge's feature, then return None;
					(3) else if the feature is in the edge to some child, return the existing child;
					(4) else create a new child using the edge annotated with the integer feature.
		"""
		# (1) if the feature is in the edges from root to this node, return the node itself
		node = self
		while not (node.is_root()):
			if node.feature == feature:
				return self
			else:
				node = node.get_parent()
		# (2) else if the feature is smaller than input edge's feature, then return None;
		if feature < self.feature:
			return None
		# (3) else if the feature is in the edge to some child, return the existing child;
		for child in self.children:
			child: KillPredictionNode
			if child.feature == feature:
				return child
		# (4) else create a new child using the edge annotated with the integer feature.
		child = KillPredictionNode(self.tree, self, feature)
		self.children.append(child)
		return child

	def __counts__(self, nodes):
		"""
		:param nodes: the set to preserve the nodes in the tree rooted on this node
		:return: the number of nodes under the node's tree
		"""
		if not (nodes is None):
			if isinstance(nodes, set):
				nodes.add(self)
			else:
				nodes: list
				nodes.append(self)
		counter = 1
		for child in self.children:
			child: KillPredictionNode
			counter += child.__counts__(nodes)
		return counter


class KillPredictionRule:
	"""
	The killable prediction rule maintains the data matched with the tree node in the KillPredictionTree.
	"""

	def __init__(self, tree_node: KillPredictionNode):
		"""
		:param tree_node: the tree node that defines the prediction rule
		"""
		self.tree_node = tree_node
		self.features = self.__generate_features__()
		self.executions = self.__generate_executions__()
		return

	# data generator

	def __generate_features__(self):
		"""
		:return: the sequence of features annotated on the path from root until this node
		"""
		features = list()
		node = self.tree_node
		while not (node.is_root()):
			features.append(node.feature)
			node = node.get_parent()
		features.reverse()
		return features

	def __matched__(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return:
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __generate_executions__(self):
		"""
		:return: the collection of executions matching with the rule
		"""
		if self.tree_node.is_root():
			parent_executions = self.tree_node.get_tree().get_document().exec_space.get_executions()
		else:
			parent_executions = self.tree_node.get_parent().get_rule().executions
		executions = set()
		for parent_execution in parent_executions:
			parent_execution: jcenco.MerExecution
			if self.__matched__(parent_execution):
				executions.add(parent_execution)
		return executions

	# getters

	def get_tree_node(self):
		"""
		:return: the tree node that defines this prediction rule uniquely
		"""
		return self.tree_node

	def get_document(self):
		"""
		:return: the memory-reduced document provides original data source
		"""
		return self.tree_node.get_tree().get_document()

	def get_features(self):
		"""
		:return: the sequence of integer features encoding the conditions included in the rule
		"""
		return self.features

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions annotated in the killable prediction rules
		"""
		conditions = set()
		document = self.tree_node.get_tree().get_document()
		for feature in self.features:
			condition = document.cond_space.get_condition(feature)
			conditions.add(condition)
		return conditions

	def get_executions(self):
		"""
		:return: the collection of memory-reduced executions matching with this rule
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which test results are predicted using the rule
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def __len__(self):
		"""
		:return: the length of the rule is the number of conditions included
		"""
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def predict(self, used_tests):
		"""
		:param used_tests: the set of test case (or their ID) used to predict the mutation test result using the rule
		:return: 	result, killed, alive, confidence
					(1) result: True to denote killed, False to denote survive, or None to denote unknown;
					(2) killed:	the number of executions matching with this rule and killed by used_tests;
					(3) alive: the number of executions matching with this rule and survive by used_tests;
					(4) confidence: the probability estimated that the prediction made are correct.
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			mutant = execution.get_mutant()
			if mutant.get_result().is_killed_in(used_tests):
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
		:param used_tests: the set of test case (or their ID) used to predict the mutation test result using the rule
		:return:	length, support, confidence
					(1) length: the length of the rule is the number of symbolic conditions incorporated in this rule;
					(2) support: the number of executions matching with the rule that fail to be killed by used_tests;
					(3) confidence: the estimated probability for any mutant matching with this rule to survive from the used_tests
		"""
		result, killed, alive, confidence = self.predict(used_tests)
		support = alive
		if support > 0:
			confidence = support / (killed + alive)
		else:
			confidence = 0.0
		return len(self.features), support, confidence


## MINING PREPROCESS


class KillPredictionInputs:
	"""
	The module maintains the parameters used to drive mining algorithms.
	"""

	def __init__(self, document: jcenco.MerDocument, max_length: int, min_support: int,
				 min_confidence: float, max_confidence: float, min_output_number: int):
		"""
		:param document: 			the memory-reduced version of data source document;
		:param max_length: 			the maximal length of prediction rules to stop the mining traversal;
		:param min_support: 		the minimal support evaluated for prediction rules by the algorithm;
		:param min_confidence: 		the minimal confidence required for prediction rules for the mining;
		:param max_confidence: 		the maximal confidence of prediction rules to stop mining traversal;
		:param min_output_number: 	the minimal number of killable prediction rules being output from.
		"""
		self.document = document
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.min_output_number = min_output_number
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

	def get_min_output_number(self):
		return self.min_output_number


class KillPredictionMemory:
	"""
	It maintains the memory of killable prediction trees used for mining algorithm
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.inputs = inputs
		self.tree = KillPredictionTree(self.inputs.get_document())
		return

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_tree(self):
		return self.tree

	def get_root(self):
		return self.tree.get_root()

	def get_child(self, parent: KillPredictionNode, feature: int):
		return self.tree.get_child(parent, feature)

	# selection methods

	@staticmethod
	def __sort_rules_in_keys__(rules, rule_evaluation_dict: dict, key_index: int, reverse: bool):
		"""
		:param rules: the collection of KillPredictionRule(s) from which the sorted rules are produced
		:param rule_evaluation_dict: the maps from KillPredictionRule to {length, support, confidence}
		:param key_index: the index in [0, 1, 2] to select the key for sorting the KillPredictionRule
		:param reverse: whether to sort the rules by keys in reversed sequence
		:return: the sorted sequence of KillPredictionRule(s) selected from input rules using dict as evidence
		"""
		key_rule_dict, key_list = dict(), list()
		for rule in rules:
			rule: KillPredictionRule
			evaluation = rule_evaluation_dict[rule]
			key = evaluation[key_index]
			if isinstance(key, float):
				key = int(key * 1000000)
			else:
				key: int
			if not (key in key_rule_dict):
				key_rule_dict[key] = set()
				key_list.append(key)
			key_rule_dict[key].add(rule)
		key_list.sort(reverse=reverse)
		sorted_rule_list = list()
		for key in key_list:
			rules = key_rule_dict[key]
			for rule in rules:
				rule: KillPredictionRule
				sorted_rule_list.append(rule)
		return sorted_rule_list

	@staticmethod
	def sort_kill_prediction_rules(rules, rule_evaluation_dict: dict):
		"""
		:param rules:
		:param rule_evaluation_dict:
		:return: sort by confidence, and sort by support
		"""
		confidence_rule_dict, confidence_list = dict(), list()
		for rule in rules:
			evaluation = rule_evaluation_dict[rule]
			confidence = evaluation[2]
			confidence: float
			key = int(confidence * 1000000)
			if not (key in confidence_rule_dict):
				confidence_rule_dict[key] = set()
				confidence_list.append(key)
			confidence_rule_dict[key].add(rule)
		confidence_list.sort(reverse=True)
		sort_rule_list = list()
		for key in confidence_list:
			key_rules = KillPredictionMemory.__sort_rules_in_keys__(confidence_rule_dict[key], rule_evaluation_dict, 1, True)
			for rule in key_rules:
				sort_rule_list.append(rule)
		return sort_rule_list

	def select_good_rules(self, rules, rule_evaluation_dict: dict):
		"""
		:param rules: the set of killable prediction rules from which the outputs are produced
		:param rule_evaluation_dict: the mapping from KillPredictionRule to {length, support, confidence}
		:return:
		"""
		# 1. select the good KillPredictionRule(s) based on input parameters
		good_rules = set()
		for rule in rules:
			rule: KillPredictionRule
			evaluation = rule_evaluation_dict[rule]
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			if length <= self.inputs.get_max_length() and \
					support >= self.inputs.get_min_support() and \
					confidence >= self.inputs.get_min_confidence():
				good_rules.add(rule)

		# 2. when number is less than required, appending the sorted list
		if len(good_rules) < self.inputs.get_min_output_number():
			sort_rule_list = KillPredictionMemory.sort_kill_prediction_rules(rules, rule_evaluation_dict)
			for rule in sort_rule_list:
				good_rules.add(rule)
				if len(good_rules) >= self.inputs.get_min_output_number():
					break

		# 3. sort the output good rules
		return good_rules


## MINING ALGORITHM


class KillPredictionMiner:
	"""
	It implements the recursive algorithm for mining killable prediction rules.
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.memory = KillPredictionMemory(inputs)
		self.solutions = dict()	# KillPredictionRule --> {length, support, confidence}
		return

	def __pass__(self, tree_node: KillPredictionNode):
		"""
		:param tree_node:
		:return: whether the traversal can pass through the tree node
		"""
		solution = self.solutions[tree_node.get_rule()]
		length = solution[0]
		support = solution[1]
		confidence = solution[2]
		if tree_node.is_root():
			return True
		else:
			return length < self.memory.get_inputs().get_max_length() and \
				   support >= self.memory.get_inputs().get_min_support() and \
				   confidence <= self.memory.get_inputs().get_max_confidence()

	def __mine__(self, tree_node: KillPredictionNode, features: list, used_tests):
		"""
		:param tree_node: 	the tree node where the mining is performed
		:param features: 	the sequence of integer features being used to extend its children
		:param used_tests: 	the set of test cases to evaluate the input rule of the tree nodes
		:return:
		"""
		if not (tree_node.get_rule() in self.solutions):
			length, support, confidence = tree_node.get_rule().evaluate(used_tests)
			self.solutions[tree_node.get_rule()] = (length, support, confidence)
		if self.__pass__(tree_node):
			for k in range(0, len(features)):
				child = self.memory.get_child(tree_node, features[k])
				if not (child is None) and (child != tree_node):
					self.__mine__(child, features[k + 1: ], used_tests)
		return

	def __outs__(self):
		"""
		:return: the sorted killable prediction rules generated from the mining
		"""
		rule_list = self.memory.select_good_rules(self.solutions.keys(), self.solutions)
		rule_dict = dict()
		for rule in rule_list:
			evaluation = self.solutions[rule]
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			length: int
			support: int
			confidence: float
			rule_dict[rule] = (length, support, confidence)
		return rule_dict

	def mine(self, features, used_tests):
		"""
		:param features:
		:param used_tests:
		:return:
		"""
		features = new_feature_sequence(features)
		root_node = self.memory.get_root()
		self.solutions.clear()
		if used_tests is None:
			used_tests_number = None
		else:
			used_tests_number = len(used_tests)

		print("\t\t\tMine({}, {})".format(len(features), used_tests_number), end="")
		self.__mine__(root_node, features, used_tests)
		rule_evaluation_dict = self.__outs__()
		print("\t==> [{} rules & {} goods]".format(len(self.solutions), len(rule_evaluation_dict)))

		self.solutions.clear()
		return rule_evaluation_dict


def prf_evaluation(orig_samples: set, pred_samples: set):
	como_samples = orig_samples & pred_samples
	if len(como_samples) > 0:
		precision = len(como_samples) / len(pred_samples)
		recall = len(como_samples) / len(orig_samples)
		f1_score = 2 * precision * recall / (precision + recall)
	else:
		precision = 0.0
		recall = 0.0
		f1_score = 0.0
	return precision, recall, f1_score


## EVALUATION LAYER


class KillPredictionOutput:
	"""
	It implements the evaluation of mining algorithm and generated rules from.
	"""

	def __init__(self, c_document: jctest.CDocument, m_document: jcenco.MerDocument):
		self.c_document = c_document
		self.m_document = m_document
		self.writer = None
		self.miner = None
		return

	# output interface

	def __output__(self, text: str):
		"""
		:param text:
		:return: write the text to output file
		"""
		self.writer: TextIO
		self.writer.write(text)
		self.writer.flush()
		return

	def __mut2str__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: id result class operator function line "code" [parameter]
		"""
		source_mutant = self.c_document.project.muta_space.get_mutant(mutant.get_mid())
		mid = source_mutant.get_muta_id()
		if mutant.get_result().is_killed_in(None):
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
		line = m_location.line_of(tail=False) + 1
		code = m_location.get_code(True)
		parameter = source_mutant.get_mutation().get_parameter()
		return "{}\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t[{}]".format(mid, result, m_class, m_operator,
															 func_name, line, code, parameter)

	def __rul2str__(self, rule: KillPredictionRule, evaluation):
		"""
		:param rule: 		killable prediction rule
		:param evaluation: 	(length, support, confidence)
		:return: 	id length executions mutants support confidence(%)
		"""
		self.c_document.get_executions()
		rid = str(rule.get_features())
		length = evaluation[0]
		support = evaluation[1]
		confidence = evaluation[2]
		confidence = int(confidence * 10000) / 100.0
		executions = len(rule.get_executions())
		mutants = len(rule.get_mutants())
		return "{}\t{}\t{}\t{}\t{}\t{}%".format(rid, length, executions, mutants, support, confidence)

	def __cod2str__(self, condition: jcenco.MerCondition):
		"""
		:param condition:
		:return: class operator execution line statement location [parameter]
		"""
		source_condition = self.c_document.get_conditions_lib().get_condition(condition.get_code())
		category = source_condition.get_category()
		operator = source_condition.get_operator()
		execution = source_condition.get_execution()
		statement = execution.get_statement().get_cir_code()
		location = source_condition.get_location()
		if location.has_ast_source():
			line = location.get_ast_source().line_of(tail=False) + 1
		else:
			line = None
		parameter = source_condition.get_parameter()
		return "{}\t{}\t{}\t#{}\t\"{}\"\t\"{}\"\t[{}]".format(category, operator,
															  execution, line, statement,
															  location.get_cir_code(), parameter)

	# mutant-rules information

	def __mine_one_mutant__(self, mutant: jcenco.MerMutant, max_used_tests: int, max_print_size: int):
		"""
		:param mutant:
		:param max_used_tests: maximal number of tests used
		:return: the mapping from killable prediction rules
		"""
		features = set()
		for execution in self.m_document.exec_space.get_executions_of(mutant):
			execution: jcenco.MerExecution
			for feature in execution.get_features():
				features.add(feature)
		used_tests = mutant.get_result().get_tests_of(False)
		if len(used_tests) > max_used_tests:
			selected_tests = set()
			while len(selected_tests) < max_used_tests:
				selected_test = jcbase.rand_select(used_tests)
				selected_tests.add(selected_test)
			used_tests = selected_tests
		self.miner: KillPredictionMiner
		solutions = self.miner.mine(features, used_tests)
		sorted_rules = KillPredictionMemory.sort_kill_prediction_rules(solutions.keys(), solutions)
		if len(sorted_rules) > max_print_size:
			sorted_rules = sorted_rules[0: max_print_size]
		results = dict()
		for rule in sorted_rules:
			evaluation = solutions[rule]
			results[rule] = evaluation
		return results

	def write_mutant_rules_file(self, inputs: KillPredictionInputs, mut_rule_path: str, rule_mut_path: str,
								max_used_tests: int, max_print_size: int):
		"""
		:param max_print_size:
		:param max_used_tests:
		:param inputs:
		:param mut_rule_path:	mutant --> rule(s)
		:param rule_mut_path:	rule --> mutant(s)
		:return:
		"""
		self.miner = KillPredictionMiner(inputs)
		rule_mutants_dict = dict()	# best rule to the mutants for prediction
		with open(mut_rule_path, 'w') as writer:
			self.writer = writer
			proceed_counter, proceed_summary = 0, len(self.m_document.exec_space.get_mutants())
			for mutant in self.m_document.exec_space.get_mutants():
				proceed_counter += 1
				print("\t\t\tProceeding at {}/{}".format(proceed_counter, proceed_summary))
				self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
				rule_index = 0
				rule_evaluation_dict = self.__mine_one_mutant__(mutant, max_used_tests, max_print_size)
				for rule, evaluation in rule_evaluation_dict.items():
					rule_index += 1
					self.__output__("\t[R.{}]\t{}\n".format(rule_index, self.__rul2str__(rule, evaluation)))
					condition_index = 0
					for condition in rule.get_conditions():
						condition_index += 1
						self.__output__("\t\t[C.{}.{}]\t{}\n".format(rule_index, condition_index, self.__cod2str__(condition)))
				self.__output__("\n")
				if len(rule_evaluation_dict) > 0:
					best_rules = KillPredictionMemory.sort_kill_prediction_rules(rule_evaluation_dict.keys(),
																				 rule_evaluation_dict)
					best_rule = best_rules[0]
					if not (best_rule in rule_mutants_dict):
						rule_mutants_dict[best_rule] = set()
					rule_mutants_dict[best_rule].add(mutant)
		with open(rule_mut_path, 'w') as writer:
			self.writer = writer
			orig_mutants, pred_mutants = set(), set()
			for rule, rule_mutants in rule_mutants_dict.items():
				self.__output__("[P]\t{}\n".format(self.__rul2str__(rule, rule.evaluate(None))))
				condition_index = 0
				for condition in rule.get_conditions():
					condition_index += 1
					self.__output__("\t[C.{}]\t{}\n".format(condition_index, self.__cod2str__(condition)))
				mutant_index = 0
				for mutant in rule_mutants:
					mutant_index += 1
					self.__output__("\t[M.{}]\t{}\n".format(mutant_index, self.__mut2str__(mutant)))
					pred_mutants.add(mutant)
				self.__output__("\n")
			for mutant in self.m_document.exec_space.get_mutants():
				orig_mutants.add(mutant)
			precision, recall, f1_score = prf_evaluation(orig_mutants, pred_mutants)
			self.__output__("\nSummary:\n")
			self.__output__("\tNumber: {}({}%)\n".format(len(rule_mutants_dict),
														 int(len(rule_mutants_dict) * 10000 / len(pred_mutants)) / 100.0))
			self.__output__("\tPrecision: {}%\n".format(int(precision * 10000 / 100.0)))
			self.__output__("\tRecalling: {}%\n".format(int(recall * 10000 / 100.0)))
			self.__output__("\tF1_Scores: {}\n".format(int(f1_score * 10000 / 100.0)))
			self.__output__("\n")
		return

	def write_prediction_rules(self, inputs: KillPredictionInputs, file_path: str):
		self.miner = KillPredictionMiner(inputs)
		features = set()
		orig_mutants, pred_mutants = set(), set()
		for execution in self.m_document.exec_space.get_executions():
			execution: jcenco.MerExecution
			if execution.get_mutant().get_result().is_killed_in(None):
				pass
			else:
				for feature in execution.get_features():
					features.add(feature)
				orig_mutants.add(execution.get_mutant())
		rule_evaluation_dict = self.miner.mine(features, None)

		with open(file_path, 'w') as writer:
			self.writer = writer
			for rule, evaluation in rule_evaluation_dict.items():
				self.__output__("[P]\t{}\n".format(self.__rul2str__(rule, evaluation)))
				condition_index = 0
				for condition in rule.get_conditions():
					condition_index += 1
					self.__output__("\t[C.{}]\t{}\n".format(condition_index, self.__cod2str__(condition)))
				mutant_index = 0
				for mutant in rule.get_mutants():
					mutant_index += 1
					self.__output__("\t[M.{}]\t{}\n".format(mutant_index, self.__mut2str__(mutant)))
					pred_mutants.add(mutant)
				self.__output__("\n")

			precision, recall, f1_score = prf_evaluation(orig_mutants, pred_mutants)
			self.__output__("\nSummary: P = {}%\tR = {}%\tScore = {}\n".format(
				int(precision * 10000) / 100.0,
				int(recall * 10000) / 100.0, int(f1_score * 10000) / 100.0))
			self.__output__("\n")

			self.__output__("Rule\tLength\tExecutions\tMutants\tResult\tKilled\tAlive\tConfidence(%)\n")
			for rule in rule_evaluation_dict.keys():
				rid = str(rule.get_features())
				length = len(rule.get_features())
				rule_exec = len(rule.get_executions())
				rule_muta = len(rule.get_mutants())
				result, killed, alive, confidence = rule.predict(None)
				self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}%\n".format(rid, length, rule_exec, rule_muta,
																		   result, killed, alive,
																		   int(confidence * 10000) / 100.0))
			self.__output__("\n")
		return


## MAIN TEST METHOD


def main(project_directory: str, encoding_directory: str, output_directory: str):
	"""
	:param project_directory:
	:param encoding_directory:
	:param output_directory:
	:return:
	"""
	max_length, min_support, min_confidence, max_confidence, min_output_number = 1, 2, 0.70, 0.99, 4
	for file_name in os.listdir(project_directory):
		c_document_directory = os.path.join(project_directory, file_name)
		m_document_directory = os.path.join(encoding_directory, file_name)
		o_directory = os.path.join(output_directory, file_name)
		if not os.path.exists(o_directory):
			os.mkdir(o_directory)
		print("Testing for {} project.".format(file_name))

		c_document = jctest.CDocument(c_document_directory, file_name, ".sip")
		m_document = jcenco.MerDocument(m_document_directory, file_name)
		print("\t(1) Load {} executions and {} mutants in {} test cases.".format(len(m_document.exec_space.get_executions()),
																				 len(m_document.exec_space.get_mutants()),
																				 len(m_document.test_space.get_test_cases())))

		inputs = KillPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence, min_output_number)
		outputter = KillPredictionOutput(c_document, m_document)
		outputter.write_mutant_rules_file(inputs, os.path.join(o_directory, file_name + ".m2r"),
										  os.path.join(o_directory, file_name + ".r2m"), 128, 8)
		outputter.write_prediction_rules(inputs, os.path.join(o_directory, file_name + ".e2r"))
		print("\t(2) Write the killable prediction rules to {}.".format(o_directory))
		print()
	return


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/features"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/rules"
	main(proj_directory, enco_directory, outs_directory)

