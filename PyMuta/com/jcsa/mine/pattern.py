""" This file defines the pattern of mutant execution state and evaluation framework """


import os
from collections import deque
from typing import TextIO
import com.jcsa.libs.base 	as jcbase
import com.jcsa.libs.test 	as jctest
import com.jcsa.mine.encode as jcenco


## Basic Methods to Support


def get_sorted_features(features):
	"""
	:param features: the collection of features (int) to represent conditions in symbolic execution
	:return: the sorted list of unique integer features captured from the inputs sequence.
	"""
	sorted_features = list()
	for feature in features:
		feature: int
		if not (feature in sorted_features):
			sorted_features.append(feature)
	sorted_features.sort()
	return sorted_features


def __sort_rules_by_keys__(input_rules, rule_evaluation_dict: dict, key_index: int, reverse: bool):
	"""
	:param input_rules: the input rules from which the rules are sorted
	:param rule_evaluation_dict: the mapping from each rule to {length, support, confidence}
	:param key_index: the index to select of which evaluation metric is used to sort the rule
	:param reverse: whether to reverse the keys in reversed sequence
	:return: the sorted sequence of killable prediction rules from input_rules
	"""
	key_dict, key_list, sort_list = dict(), list(), list()
	for input_rule in input_rules:
		input_rule: SymExecutionPatternRule
		evaluation = rule_evaluation_dict[input_rule]
		key = evaluation[key_index]
		if isinstance(key, float):
			key = int(key * 1000000)
		else:
			key: int
		if not (key in key_dict):
			key_dict[key] = set()
			key_list.append(key)
		key_dict[key].add(input_rule)
	key_list.sort(reverse=reverse)
	for key in key_list:
		for rule in key_dict[key]:
			rule: SymExecutionPatternRule
			sort_list.append(rule)
	return sort_list


def sort_kill_prediction_rules(input_rules, rule_evaluation_dict: dict):
	"""
	:param input_rules:
	:param rule_evaluation_dict:
	:return: sort by confidence, and sort by support
	"""
	confidence_rule_dict, confidence_list = dict(), list()
	for rule in input_rules:
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
		key_rules = __sort_rules_by_keys__(confidence_rule_dict[key], rule_evaluation_dict, 1, True)
		for rule in key_rules:
			sort_rule_list.append(rule)
	return sort_rule_list


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


## Pattern Model Definition


class SymExecutionPatternTree:
	"""
	It organizes the generated patterns in hierarchical way for uniqueness.
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the memory-reduced (encoded) data source
		"""
		self.m_document = m_document
		self.root = SymExecutionPatternNode(self, None, -1)
		return

	def get_m_document(self):
		return self.m_document

	def get_root(self):
		return self.root

	def get_node(self, features):
		"""
		:param features:
		:return: the unique node w.r.t. the input features
		"""
		features = get_sorted_features(features)
		node = self.root
		for feature in features:
			node = node.extend_child(feature)
		return node

	def get_nodes(self):
		"""
		:return: the set of all the nodes under the tree
		"""
		queue = deque()
		queue.append(self.root)
		while len(queue) > 0:
			node = queue.popleft()
			node: SymExecutionPatternNode
			for child in node.get_children():
				queue.append(child)
			yield node


class SymExecutionPatternNode:
	"""
	A node in symbolic execution pattern generation tree with respect to one unique pattern in the space
	"""
	def __init__(self, tree: SymExecutionPatternTree, parent, local_feature: int):
		"""
		:param tree: 			the symbolic execution pattern's tree where the node is defined and built
		:param parent: 			the parent node where this child is created or None if the node is a root
		:param local_feature: 	the feature (int) annotated on the edge from parent to it or -1 when root
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.feature = -1
		else:
			parent: SymExecutionPatternNode
			self.parent = parent
			self.feature = local_feature
		self.children = list()
		self.rule = SymExecutionPatternRule(self)
		return

	def get_tree(self):
		"""
		:return: the symbolic execution pattern's tree where the node is defined and built
		"""
		return self.tree

	def get_parent(self):
		"""
		:return: the parent node where this child is created or None if the node is a root
		"""
		if self.parent is None:
			return None
		else:
			self.parent: SymExecutionPatternNode
			return self.parent

	def is_root(self):
		"""
		:return: whether the node is a root without any parent node in the tree space
		"""
		if self.parent is None:
			return True
		else:
			return False

	def get_feature(self):
		"""
		:return: the feature (int) annotated on the edge from parent to it or -1 when root
		"""
		return self.feature

	def get_rule(self):
		"""
		:return: the dataset correlated with the pattern specified by this node in the tree
		"""
		return self.rule

	def get_children(self):
		"""
		:return: the children extended from this parent node in mining algorithm
		"""
		return self.children

	def extend_child(self, new_feature: int):
		"""
		:param new_feature:
		:return: the child is created or returned based on following rules:
					(1) When new_feature is smaller than or equal with local feature, return the node itself;
					(2) When new_feature is greater than local feature and a child exists w.r.t. new_feature;
					(3) Otherwise, create a new child using the new_feature under the pattern and appends it;
		"""
		if new_feature <= self.feature:
			return self
		else:
			for child in self.children:
				child: SymExecutionPatternNode
				if child.get_feature() == new_feature:
					return child
			new_child = SymExecutionPatternNode(self.tree, self, new_feature)
			self.children.append(new_child)
			return new_child


class SymExecutionPatternRule:
	"""
	It manages the data-source correlated with each pattern in the symbolic execution of mutation analysis.
	"""

	## constructor

	def __init__(self, node: SymExecutionPatternNode):
		"""
		:param node: the symbolic execution pattern tree node referring to this unique pattern in the space
		"""
		self.node = node
		self.features = list()		# the sorted sequence of symbolic conditions (int-features)
		self.executions = set()		# the collection of symbolic executions correlated with it
		self.__generate_features__()
		self.__update_executions__()
		return

	def __generate_features__(self):
		"""
		:return: update the features in the data block
		"""
		self.features.clear()
		node = self.node
		while not node.is_root():
			self.features.append(node.get_feature())
			node = node.get_parent()
		self.features.sort()
		return

	def __is_matching_with_(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return: whether the pattern of this node matches with the execution
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __update_executions__(self):
		"""
		:return: update the symbolic executions correlated with this pattern based on its parent pattern
		"""
		if self.node.is_root():
			parent_executions = self.node.get_tree().get_m_document().exec_space.get_executions()
		else:
			parent_executions = self.node.get_parent().get_rule().get_executions()
		self.executions.clear()
		for execution in parent_executions:
			execution: jcenco.MerExecution
			if self.__is_matching_with_(execution):
				self.executions.add(execution)
		return

	## node-features

	def get_node(self):
		"""
		:return: the symbolic execution pattern tree node referring to this unique pattern in the space
		"""
		return self.node

	def get_features(self):
		"""
		:return: the sorted sequence of symbolic conditions (int-features)
		"""
		return self.features

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions annotated in the symbolic execution of target mutant
		"""
		conditions = list()
		document = self.node.get_tree().get_m_document()
		for feature in self.features:
			condition = document.cond_space.get_condition(feature)
			conditions.append(condition)
		return conditions

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	## data-evaluation

	def get_executions(self):
		"""
		:return: the collection of symbolic executions correlated with it
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutations of which symbolic executions match with this pattern
		"""
		mutants = set()
		for execution in self.executions:
			execution: jcenco.MerExecution
			mutants.add(execution.get_mutant())
		return mutants

	def predicts(self, used_tests):
		"""
		:param used_tests: the set of test cases to decide whether the rule will lead to killed or not
		:return: result(bool), killed, survive, confidence(%)
		"""
		killed, survive, total = 0, 0, 0
		for execution in self.executions:
			execution: jcenco.MerExecution
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				killed += 1
			else:
				survive += 1
			total += 1
		if killed < survive:
			result = True
		elif killed > survive:
			result = False
		else:
			result = None
		if total > 0:
			confidence = max(killed, survive) / total
		else:
			confidence = 0.0
		return result, killed, survive, confidence

	def evaluate(self, used_tests):
		"""
		:param used_tests:
		:return: length, support, confidence(%), result(bool)
		"""
		length = len(self.features)
		result, killed, survive, confidence = self.predicts(used_tests)
		support = survive
		if support > 0:
			confidence = survive / (killed + survive)
		else:
			confidence = 0.0
		return length, support, confidence, result


## Pattern Mining Modules


class SymExecutionMiningInputs:
	"""
	The inputs module defines the parameters used for pattern mining.
	"""

	def __init__(self, m_document: jcenco.MerDocument,
				 max_length: int, min_support: int,
				 min_confidence: float, max_confidence: float,
				 min_output_number: int, max_output_number: int):
		"""
		:param m_document:			the memory-reduced document of data source
		:param max_length:			the maximal length of rule being generated
		:param min_support:			the minimal support required for valid rule
		:param min_confidence:		the minimal confidence required for valid rule
		:param max_confidence:		the maximal confidence required to stop mining
		:param min_output_number:	the minimal number of output prediction rules
		:param max_output_number:	the maximal number of output prediction rules
		"""
		self.document = m_document
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.min_output_number = min_output_number
		self.max_output_number = max_output_number
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

	def get_max_output_number(self):
		return self.max_output_number


class SymExecutionMiningMiddle:
	"""
	The middle layer of mining algorithm manages a tree model for constructing killable prediction rules.
	"""

	def __init__(self, inputs: SymExecutionMiningInputs):
		"""
		:param inputs: the inputs layer on which the middle layer is constructed
		"""
		self.inputs = inputs
		self.tree = SymExecutionPatternTree(self.inputs.get_document())
		return

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_tree(self):
		return self.tree

	def get_root(self):
		return self.tree.get_root()

	def get_child(self, parent: SymExecutionPatternNode, feature: int):
		return self.tree.get_node(parent.get_rule().get_features()).extend_child(feature)

	def select_good_rules(self, input_rules, used_tests):
		"""
		:param input_rules: None to select good rules within all the tree nodes in the module
		:param used_tests:	Used to evaluate generated pattern in predictive rules
		:return:
		"""
		## collect the rules from which goods are selected
		all_rules = set()
		if input_rules is None:
			for node in self.tree.get_nodes():
				all_rules.add(node.get_rule())
		else:
			for rule in input_rules:
				rule: SymExecutionPatternRule
				all_rules.add(rule)
		## select good rules based on input parameters
		good_rules = dict()
		for rule in all_rules:
			length, support, confidence, result = rule.evaluate(used_tests)
			if length <= self.inputs.get_max_length() and \
					support >= self.inputs.get_min_support() and \
					confidence >= self.inputs.get_min_confidence():
				good_rules[rule] = (length, support, confidence)
		return sort_kill_prediction_rules(good_rules.keys(), good_rules)


class SymExecutionPatternMiner:
	"""
	It implements association rule mining to extract symbolic execution patterns
	"""
	def __init__(self, inputs: SymExecutionMiningInputs):
		self.middle = SymExecutionMiningMiddle(inputs)
		self.solutions = dict()
		return

	def __pass__(self, tree_node: SymExecutionPatternNode):
		"""
		:param tree_node:
		:return: whether the traversal can pass through the tree node
		"""
		if tree_node.is_root():
			return True
		else:
			solution = self.solutions[tree_node.get_rule()]
			length = solution[0]
			support = solution[1]
			confidence = solution[2]
			return length < self.middle.get_inputs().get_max_length() and \
				   support >= self.middle.get_inputs().get_min_support() and \
				   confidence <= self.middle.get_inputs().get_max_confidence()

	def __mine__(self, tree_node: SymExecutionPatternNode, features: list, used_tests):
		"""
		:param tree_node: 	the tree node where the mining is performed
		:param features: 	the sequence of integer features being used to extend its children
		:param used_tests: 	the set of test cases to evaluate the input rule of the tree nodes
		:return:
		"""
		if not (tree_node.get_rule() in self.solutions):
			length, support, confidence, result = tree_node.get_rule().evaluate(used_tests)
			self.solutions[tree_node.get_rule()] = (length, support, confidence)
		if self.__pass__(tree_node):
			for k in range(0, len(features)):
				child = self.middle.get_child(tree_node, features[k])
				if not (child is None) and (child != tree_node):
					self.__mine__(child, features[k + 1: ], used_tests)
		return

	def __outs__(self, used_tests):
		"""
		:return: the sorted killable prediction rules generated from the mining
		"""
		## generate the best prediction rules from solution space
		rule_list = self.middle.select_good_rules(self.solutions.keys(), used_tests)
		min_output_length = self.middle.inputs.get_min_output_number()
		max_output_length = self.middle.inputs.get_max_output_number()
		if len(rule_list) == 0:
			rule_list = sort_kill_prediction_rules(self.solutions.keys(), self.solutions)
			if len(rule_list) > min_output_length:
				rule_list = rule_list[0: min_output_length]
		else:
			if len(rule_list) > max_output_length:
				rule_list = rule_list[0: max_output_length]
		## generate output mappings from given rules
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
		features = get_sorted_features(features)
		root_node = self.middle.get_root()
		self.solutions.clear()
		if used_tests is None:
			used_tests_number = len(self.middle.inputs.get_document().test_space.get_test_cases())
		else:
			used_tests_number = len(used_tests)

		print("\t\t\t\tMine({}, {})".format(len(features), used_tests_number), end="")
		self.__mine__(root_node, features, used_tests)
		rule_evaluation_dict = self.__outs__(used_tests)
		print("\t==> [{} rules & {} goods]".format(len(self.solutions), len(rule_evaluation_dict)))

		self.solutions.clear()
		return rule_evaluation_dict


class SymExecutionMiningOutput:
	"""
	It is used to evaluate and output evaluation results for empirical study
	"""

	def __init__(self, c_document: jctest.CDocument, m_document: jcenco.MerDocument):
		self.c_document = c_document
		self.m_document = m_document
		self.writer = None
		self.miner = None
		return

	## basic printing methods

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

	def __rul2str__(self, rule: SymExecutionPatternRule, used_tests):
		"""
		:param rule: 		killable prediction rule
		:param used_tests:	used to evaluate pattern prediction metrics
		:return: 			id length executions mutants support confidence(%)
		"""
		rid = str(rule.get_features())
		executions = len(rule.get_executions())
		length, support, confidence, result = rule.evaluate(used_tests)
		confidence = int(confidence * 10000) / 100.0
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

	## output interfaces

	def __mine_one_mutant__(self, mutant: jcenco.MerMutant, used_tests):
		"""
		:param mutant:
		:param used_tests:
		:return: the mapping from killable prediction rules
		"""
		## I. capture the features from executions of mutant in data source
		features = set()
		for execution in self.m_document.exec_space.get_executions_of(mutant):
			execution: jcenco.MerExecution
			for feature in execution.get_features():
				features.add(feature)
		## II. perform the pattern mining algorithm to extract the patterns
		self.miner: SymExecutionPatternMiner
		solutions = self.miner.mine(features, used_tests)
		sorted_rules = sort_kill_prediction_rules(solutions.keys(), solutions)
		if len(sorted_rules) > self.miner.middle.inputs.max_output_number:
			sorted_rules = sorted_rules[0: self.miner.middle.inputs.max_output_number]
		results = dict()
		for rule in sorted_rules:
			evaluation = solutions[rule]
			results[rule] = evaluation
		return results

	def write_mutant_rules_file(self, inputs: SymExecutionMiningInputs, mut_rule_path: str,
								rule_mut_path: str, max_used_tests_number: int):
		"""
		:param max_used_tests_number:
		:param inputs:
		:param mut_rule_path:	mutant --> rule(s)
		:param rule_mut_path:	rule --> mutant(s)
		:return:
		"""
		self.miner = SymExecutionPatternMiner(inputs)
		rule_mutants_dict = dict()	# best rule to the mutants for prediction
		with open(mut_rule_path, 'w') as writer:
			self.writer = writer
			proceed_counter, proceed_summary = 0, len(self.m_document.exec_space.get_mutants())
			for mutant in self.m_document.exec_space.get_mutants():
				proceed_counter += 1
				print("\t\t\tProceeding at {}[{}/{}]".format(self.m_document.name, proceed_counter, proceed_summary))
				self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
				rule_index = 0
				used_tests = mutant.get_result().get_tests_of(False)
				if len(used_tests) > max_used_tests_number:
					selected_tests = set()
					while len(selected_tests) < max_used_tests_number:
						selected_test = jcbase.rand_select(used_tests)
						selected_tests.add(selected_test)
					used_tests = selected_tests
				rule_evaluation_dict = self.__mine_one_mutant__(mutant, used_tests)
				for rule, evaluation in rule_evaluation_dict.items():
					rule_index += 1
					self.__output__("\t[R.{}]\t{}\n".format(rule_index, self.__rul2str__(rule, used_tests)))
					condition_index = 0
					for condition in rule.get_conditions():
						condition_index += 1
						self.__output__("\t\t[C.{}.{}]\t{}\n".format(rule_index, condition_index, self.__cod2str__(condition)))
				self.__output__("\n")
				if len(rule_evaluation_dict) > 0:
					best_rules = sort_kill_prediction_rules(rule_evaluation_dict.keys(), rule_evaluation_dict)
					best_rule = best_rules[0]
					if not (best_rule in rule_mutants_dict):
						rule_mutants_dict[best_rule] = set()
					rule_mutants_dict[best_rule].add(mutant)
		with open(rule_mut_path, 'w') as writer:
			self.writer = writer
			orig_mutants, pred_mutants = set(), set()
			for rule, rule_mutants in rule_mutants_dict.items():
				self.__output__("[P]\t{}\n".format(self.__rul2str__(rule, used_tests)))
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

	def write_prediction_rules(self, inputs: SymExecutionMiningInputs, file_path: str, used_tests):
		self.miner = SymExecutionPatternMiner(inputs)
		features = set()
		orig_mutants, pred_mutants = set(), set()
		for execution in self.m_document.exec_space.get_executions():
			execution: jcenco.MerExecution
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				pass
			else:
				for feature in execution.get_features():
					features.add(feature)
				orig_mutants.add(execution.get_mutant())
		orig_output_number = self.miner.middle.get_inputs().max_output_number
		self.miner.middle.get_inputs().max_output_number = len(features)
		rule_evaluation_dict = self.miner.mine(features, used_tests)
		self.miner.middle.get_inputs().max_output_number = orig_output_number

		with open(file_path, 'w') as writer:
			self.writer = writer
			for rule, evaluation in rule_evaluation_dict.items():
				self.__output__("[P]\t{}\n".format(self.__rul2str__(rule, used_tests)))
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
			self.__output__("\nSummary: Orig = {}\tPred = {}\tP = {}%\tR = {}%\tScore = {}\n".format(
				len(orig_mutants), len(pred_mutants), int(precision * 10000) / 100.0,
				int(recall * 10000) / 100.0, int(f1_score * 10000) / 100.0))
			self.__output__("\n")

			self.__output__("Rule\tLength\tExecutions\tMutants\tResult\tKilled\tAlive\tConfidence(%)\n")
			for rule in rule_evaluation_dict.keys():
				rid = str(rule.get_features())
				length = len(rule.get_features())
				rule_exec = len(rule.get_executions())
				rule_muta = len(rule.get_mutants())
				result, killed, survive, confidence = rule.predicts(used_tests)
				self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}%\n".format(rid, length, rule_exec, rule_muta,
																		   result, killed, survive,
																		   int(confidence * 10000) / 100.0))
			self.__output__("\n")
		return


## MAIN TEST METHOD


def do_mining(c_document: jctest.CDocument, m_document: jcenco.MerDocument,
			  output_directory: str, file_name: str, max_length: int,
			  min_support: int, min_confidence: float, max_confidence: float):
	"""
	:param c_document: original document
	:param m_document: encoded document
	:param output_directory: the output directory where files are printed
	:param file_name: the project name
	:param max_length: the maximal length of generated patterns
	:param min_support: minimal support for mining
	:param min_confidence: minimal confidence for mining
	:param max_confidence: maximal confidence for mining
	:return:
	"""
	# I. create output directory for pattern generation
	o_directory = os.path.join(output_directory, file_name)
	if not os.path.exists(o_directory):
		os.mkdir(o_directory)
	print("\t(1) Load {} executions and {} mutants in {} test cases.".format(
		len(m_document.exec_space.get_executions()),
		len(m_document.exec_space.get_mutants()),
		len(m_document.test_space.get_test_cases())))

	# II. construct the mining modules
	inputs = SymExecutionMiningInputs(m_document, max_length, min_support, min_confidence, max_confidence, 4, 8)
	writer = SymExecutionMiningOutput(c_document, m_document)
	writer.write_prediction_rules(inputs, os.path.join(o_directory, file_name + ".e2r"),
								  m_document.exec_space.get_test_cases())
	writer.write_mutant_rules_file(inputs, os.path.join(o_directory, file_name + ".m2r"),
								   os.path.join(o_directory, file_name + ".r2m"), 128)
	print("\t(2) Write the symbolic execution patterns to {}.".format(o_directory))
	return


def main(project_directory: str, encoding_directory: str, output_directory: str):
	"""
	:param project_directory:
	:param encoding_directory:
	:param output_directory:
	:return:
	"""
	max_length, min_support, min_confidence, max_confidence = 1, 2, 0.75, 0.99
	for file_name in os.listdir(project_directory):
		c_document_directory = os.path.join(project_directory, file_name)
		m_document_directory = os.path.join(encoding_directory, file_name)
		c_document = jctest.CDocument(c_document_directory, file_name)
		m_document = jcenco.MerDocument(m_document_directory, file_name)
		print("Start to test for {} project.".format(file_name))
		do_mining(c_document, m_document, output_directory, file_name,
				  max_length, min_support, min_confidence, max_confidence)
		print()
	return


## MAIN TEST SCRIPT


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/deatures"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/patterns"
	main(proj_directory, enco_directory, outs_directory)

