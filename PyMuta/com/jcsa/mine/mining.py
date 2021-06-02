"""This file defines the structural model of killable prediction rules and implement algorithm for mining them."""


import os
from typing import TextIO
import com.jcsa.libs.base 	as jcbase
import com.jcsa.libs.test	as jctest
import com.jcsa.mine.encode	as jcenco


## UTILITY METHODS


def sort_feature_vector(features):
	"""
	:param features: the collection of integers being sorted
	:return:
	"""
	feature_list = list()
	for feature in features:
		feature: int
		if not (feature in feature_list):
			feature_list.append(feature)
	feature_list.sort()
	return feature_list


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
		input_rule: KillPredictionRule
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
			rule: KillPredictionRule
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


## MODEL DEFINITION


class KillPredictionTree:
	"""
	The structural tree manages the generation of unique prediction rules in program space.
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the memory-reduced version of data source document
		"""
		self.m_document = m_document
		self.root = KillPredictionNode(self, None, -1)
		return

	def get_document(self):
		"""
		:return: the memory-reduced version of data source document
		"""
		return self.m_document

	def get_root(self):
		return self.root

	def get_node(self, features):
		"""
		:param features:
		:return:
		"""
		feature_list = sort_feature_vector(features)
		node = self.root
		for feature in feature_list:
			node = node.__extend__(feature)
		return node

	def get_child(self, parent, feature: int):
		"""
		:param parent:
		:param feature:
		:return:
		"""
		parent: KillPredictionNode
		if parent.get_tree() == self:
			return parent.__extend__(feature)
		else:
			features = set()
			features.add(feature)
			while not (parent.is_root()):
				features.add(parent.feature)
				parent = parent.get_parent()
			return self.get_node(features)

	def __len__(self):
		return self.root.__counts__(None)

	def get_nodes(self):
		nodes = set()
		self.root.__counts__(nodes)
		return nodes


class KillPredictionNode:
	"""
	It denotes a node in tree-based model of constructing killable prediction rules.
	"""

	def __init__(self, tree: KillPredictionTree, parent, feature: int):
		"""
		:param tree: 	the tree where the node is uniquely created
		:param parent: 	the parent of this node or None if it is the root
		:param feature:	the integer feature annotated on edge from parent to this node or meaningless for root node
		"""
		self.tree = tree
		self.parent = parent
		self.feature = feature
		self.children = list()
		self.rule = KillPredictionRule(self)
		return

	def get_tree(self):
		"""
		:return: the tree where the node is created uniquely
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the tree node is a root without any parent
		"""
		return self.parent is None

	def is_leaf(self):
		"""
		:return: whether the tree node is a leaf without any children
		"""
		return len(self.children) == 0

	def get_parent(self):
		"""
		:return: the parent of this node or None if the node is root
		"""
		if self.parent is None:
			return None
		else:
			self.parent: KillPredictionNode
			return self.parent

	def get_children(self):
		"""
		:return: the children nodes created under this node as extended rules
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children created under the tree node
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k: k >= 0 and k < self.number_of_children()
		:return:  the kth child extended from this tree node
		"""
		child = self.children[k]
		child: KillPredictionNode
		return child

	def get_rule(self):
		"""
		:return: the unique prediction rule represented by this tree node
		"""
		return self.rule

	def __extend__(self, feature: int):
		"""
		:param feature: the integer feature encoding on the edge from this node to its child
		:return: 	The method will return or create a child node under this one based on the following rules.
					(1)	If the feature is in the path from root until this node, return the node itself;
					(2) else if the feature is smaller than features on the path from root to this node, it is
						an invalid feature and the method will return None as output;
					(3) else if the feature is in the edge from this node to one of the existing children, it
						just returns the existing child as output;
					(4) otherwise, it creates a new child and adds the child to the node.children sequence.
		"""
		## (1)	If the feature is in the path from root until this node, return the node itself;
		node = self
		while not (node.is_root()):
			if node.feature == feature:
				return self
			else:
				node = node.get_parent()
		## (2) 	else if the feature is smaller than features on the path from root to this node, it is
	 	##		an invalid feature and the method will return None as output;
		if feature < self.feature:
			return None
		## (3) 	else if the feature is in the edge from this node to one of the existing children, it
		## 		just returns the existing child as output;
		for child in self.children:
			child: KillPredictionNode
			if child.feature == feature:
				return child
		## (4) otherwise, it creates a new child and adds the child to the node.children sequence.
		child = KillPredictionNode(self.tree, self, feature)
		self.children.append(child)
		return child

	def __counts__(self, nodes):
		"""
		:param nodes: the collection of nodes to preserve tree nodes under this root's subtree.
		:return: the number of tree nodes under the subtree rooted in this node
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
	The killable prediction rule takes a series of symbolic conditions (memory-reduced features) as features to
	predict whether a mutant is killable or not, in which the premises are taken as representative of the mutant
	being predicted.
	"""

	def __init__(self, tree_node: KillPredictionNode):
		"""
		:param tree_node: the tree node from which the rule is defined
		"""
		# declarations
		self.document = tree_node.get_tree().get_document()
		self.features = list()
		self.executions = set()

		## update the feature vector
		node = tree_node
		while not (node.is_root()):
			self.features.append(node.feature)
			node = node.get_parent()
		self.features.reverse()

		## update the data executions
		if tree_node.is_root():
			parent_executions = self.document.exec_space.get_executions()
		else:
			parent_executions = tree_node.get_parent().get_rule().executions
		for execution in parent_executions:
			execution: jcenco.MerExecution
			if self.__matched__(execution):
				self.executions.add(execution)
		return

	def __matched__(self, execution: jcenco.MerExecution):
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def get_features(self):
		"""
		:return: the sorted sequence of integers encoding the symbolic conditions incorporated
		"""
		return self.features

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions used as premises of the prediction rules
		"""
		conditions = set()
		for feature in self.features:
			condition = self.document.cond_space.get_condition(feature)
			conditions.add(condition)
		return conditions

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def get_executions(self):
		"""
		:return: the set of executions matching with the rule's premises
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which executions match with the rule
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def predict(self, used_tests):
		"""
		:param used_tests: the set of TestCase (or unique integer) to kill the mutant or None to represent all tests
		:return: 	result, killed, alive, confidence
					(1)	result: whether the rule predicts mutant using the premise of the rule is killed by used_tests
					(2) killed: the number of executions mathing with this rule and be killed by used_tests
					(3) alive:  the number of executions matching with this rule and survive from used_tests
					(4) confidence: the likelihood that the prediction results are correct from the rule in the set
		"""
		killed, alive = 0, 0
		for execution in self.executions:
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
		:param used_tests:
		:return: 	length, support, confidence
		"""
		result, killed, alive, confidence = self.predict(used_tests)
		length = len(self)
		support = alive
		if support > 0:
			confidence = support / (killed + alive)
		else:
			confidence = 0.0
		return length, support, confidence


## MINING MODULE


class KillPredictionInputs:
	"""
	The inputs layer defines the parameters for mining killable prediction rules.
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


class KillPredictionMiddle:
	"""
	The middle layer of mining algorithm manages a tree model for constructing killable prediction rules.
	"""

	def __init__(self, inputs: KillPredictionInputs):
		"""
		:param inputs: the inputs layer on which the middle layer is constructed
		"""
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

	def select_good_rules(self, input_rules, rule_evaluation_dict: dict):
		"""
		:param input_rules: the set of killable prediction rules from which the outputs are produced
		:param rule_evaluation_dict: the mapping from KillPredictionRule to {length, support, confidence}
		:return:
		"""
		good_rules = list()
		for rule in input_rules:
			rule: KillPredictionRule
			evaluation = rule_evaluation_dict[rule]
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			if length <= self.inputs.get_max_length() and \
					support >= self.inputs.get_min_support() and \
					confidence >= self.inputs.get_min_confidence():
				good_rules.append(rule)

		if len(good_rules) < self.inputs.get_min_output_number():
			sort_rule_list = sort_kill_prediction_rules(input_rules, rule_evaluation_dict)
			for rule in sort_rule_list:
				good_rules.append(rule)
				if len(good_rules) >= self.inputs.get_min_output_number():
					break
		if len(good_rules) > self.inputs.get_max_output_number():
			good_rules = good_rules[0: self.inputs.get_max_output_number()]

		# 3. sort the output good rules
		return sort_kill_prediction_rules(good_rules, rule_evaluation_dict)


class KillPredictionMiner:
	"""
	The module implements mining algorithm
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.middle = KillPredictionMiddle(inputs)
		self.solutions = dict()
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
			return length < self.middle.get_inputs().get_max_length() and \
				   support >= self.middle.get_inputs().get_min_support() and \
				   confidence <= self.middle.get_inputs().get_max_confidence()

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
				child = self.middle.get_child(tree_node, features[k])
				if not (child is None) and (child != tree_node):
					self.__mine__(child, features[k + 1: ], used_tests)
		return

	def __outs__(self):
		"""
		:return: the sorted killable prediction rules generated from the mining
		"""
		rule_list = self.middle.select_good_rules(self.solutions.keys(), self.solutions)
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
		features = sort_feature_vector(features)
		root_node = self.middle.get_root()
		self.solutions.clear()
		if used_tests is None:
			used_tests_number = None
		else:
			used_tests_number = len(used_tests)

		print("\t\t\t\tMine({}, {})".format(len(features), used_tests_number), end="")
		self.__mine__(root_node, features, used_tests)
		rule_evaluation_dict = self.__outs__()
		print("\t==> [{} rules & {} goods]".format(len(self.solutions), len(rule_evaluation_dict)))

		self.solutions.clear()
		return rule_evaluation_dict


## EVALUATE LAYER


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

	def __mine_one_mutant__(self, mutant: jcenco.MerMutant, max_used_tests: int):
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
		sorted_rules = sort_kill_prediction_rules(solutions.keys(), solutions)
		results = dict()
		for rule in sorted_rules:
			evaluation = solutions[rule]
			results[rule] = evaluation
		return results

	def write_mutant_rules_file(self, inputs: KillPredictionInputs, mut_rule_path: str,
								rule_mut_path: str, max_used_tests: int):
		"""
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
				print("\t\t\tProceeding at {}[{}/{}]".format(self.m_document.name, proceed_counter, proceed_summary))
				self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
				rule_index = 0
				rule_evaluation_dict = self.__mine_one_mutant__(mutant, max_used_tests)
				for rule, evaluation in rule_evaluation_dict.items():
					rule_index += 1
					self.__output__("\t[R.{}]\t{}\n".format(rule_index, self.__rul2str__(rule, evaluation)))
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
		self.miner.middle.get_inputs().max_output_number = len(features)
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
	max_length, min_support, min_confidence, max_confidence, min_output_number, max_output_number = 1, 2, 0.70, 0.99, 4, 8
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

		inputs = KillPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence, min_output_number, max_output_number)
		outputter = KillPredictionOutput(c_document, m_document)
		outputter.write_mutant_rules_file(inputs, os.path.join(o_directory, file_name + ".m2r"),
										  os.path.join(o_directory, file_name + ".r2m"), 128)
		outputter.write_prediction_rules(inputs, os.path.join(o_directory, file_name + ".e2r"))
		print("\t(2) Write the killable prediction rules to {}.".format(o_directory))
		print()
	return


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/features"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/rules"
	main(proj_directory, enco_directory, outs_directory)

