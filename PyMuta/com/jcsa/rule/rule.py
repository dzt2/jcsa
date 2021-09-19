""" This file defines the data model of state difference patterns. """


import os
from collections import deque
from typing import TextIO
import pydotplus
import sklearn.tree as sktree
from sklearn import metrics
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.test as jctest
import com.jcsa.rule.ende as jcende


## pattern


class DifferStateTree:
	"""
	It defines the hierarchical structure model of the state difference patterns.
	"""

	def __init__(self, document: jcende.MerDocument):
		self.document = document
		self.root = DifferStateNode(self, None, -1)
		return

	def get_document(self):
		"""
		:return: the document on which the tree is defined
		"""
		return self.document

	def get_root(self):
		return self.root

	def get_node(self, features):
		"""
		:param features: the set of integers encoding the unique pattern
		:return: the unique tree node w.r.t. the given features
		"""
		feature_list = self.document.anto_space.normal(features)
		node = self.root
		for feature in feature_list:
			node = node.new_child(feature)
		return node

	def get_nodes(self):
		"""
		:return: the set of all nodes under this tree
		"""
		return self.root.get_subtree_nodes()


class DifferStateNode:
	"""
	It denotes a node in state difference pattern tree, referring to a unique pattern (rule).
	"""

	def __init__(self, tree: DifferStateTree, parent, feature: int):
		"""
		:param tree: 	the tree where this node is uniquely defined
		:param parent: 	the parent of the node or None for root node
		:param feature: the integer feature annotated on input edges
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.feature = -1
		else:
			self.parent = parent
			self.feature = feature
		self.children = list()
		self.rule = DifferStateRule(self)
		return

	def get_tree(self):
		"""
		:return: the tree where this node is created
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether this node is a root without any parent
		"""
		if self.parent is None:
			return True
		return False

	def get_parent(self):
		"""
		:return: the parent of the node or None for root node
		"""
		if self.parent is None:
			return None
		self.parent: DifferStateNode
		return self.parent

	def get_feature(self):
		return self.feature

	def is_leaf(self):
		"""
		:return: whether the node is a leaf without any child
		"""
		return len(self.children) == 0

	def get_children(self):
		"""
		:return: the set of children created under this node
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children created under this node
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child created under this parent node
		"""
		child = self.children[k]
		child: DifferStateNode
		return child

	def new_child(self, feature: int):
		"""
		:param feature:
		:return: It creates a new child w.r.t. the new feature under this node.
		"""
		if feature <= self.feature:
			return self
		else:
			for child in self.children:
				child: DifferStateNode
				if child.feature == feature:
					return child
			child = DifferStateNode(self.tree, self, feature)
			self.children.append(child)
			return child

	def get_rule(self):
		"""
		:return: the state difference pattern (rule) it represents
		"""
		return self.rule

	def get_subtree_nodes(self):
		"""
		:return: the set of all the nodes created under this node as root
		"""
		queue, nodes = deque(), set()
		queue.append(self)
		while len(queue) > 0:
			node = queue.popleft()
			node: DifferStateNode
			nodes.add(node)
			for child in node.children:
				child: DifferStateNode
				queue.append(child)
		return nodes


class DifferStateRule:
	"""
	It defines the state difference pattern in the space
	"""

	def __init__(self, node: DifferStateNode):
		"""
		:param node:
		"""
		self.document = node.get_tree().get_document()
		self.__new_features__(node)
		self.__new_sampling__(node)
		return

	def __new_features__(self, node: DifferStateNode):
		"""
		:param node:
		:return: the set of integer features encoding the state difference pattern.
		"""
		features = set()
		while not node.is_root():
			features.add(node.get_feature())
			node = node.get_parent()
		self.features = self.document.anto_space.normal(features)
		return

	def __is_matched_with__(self, execution: jcende.MerExecution):
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __new_sampling__(self, node: DifferStateNode):
		"""
		:param node:
		:return: it generates the set of executions matching with this rule
		"""
		self.executions = set()
		if node.is_root():
			parent_executions = self.document.exec_space.get_executions()
		else:
			parent_executions = node.get_parent().get_rule().executions
		for execution in parent_executions:
			execution: jcende.MerExecution
			if self.__is_matched_with__(execution):
				self.executions.add(execution)
		return

	## getters

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def get_features(self):
		"""
		:return: the integer features encoding the annotations defining this pattern
		"""
		return self.features

	def get_annotations(self):
		"""
		:return: the set of annotations defining this pattern
		"""
		return self.document.anto_space.decode(self.features)

	def get_executions(self):
		"""
		:return: the set of executions matching with this pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which executions match with
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def has_sample(self, sample):
		"""
		:param sample: either MerMutant or MerExecution
		:return:
		"""
		for execution in self.executions:
			execution: jcende.MerExecution
			if (execution == sample) or (execution.get_mutant() == sample):
				return True
		return False

	def evaluate(self, used_tests):
		"""
		:param used_tests: the set of test cases or id to decide of which executions in pattern are killed or not
		:return: length, support (alive), confidence
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			if execution.get_mutant().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		support = alive
		if support > 0:
			confidence = support / (alive + killed)
		else:
			confidence = 0.0
		return len(self.features), support, confidence

	def predict(self, used_tests):
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


class DifferStateRuleInputs:
	"""
	It specifies the parameters for running the state difference patterns.
	"""

	def __init__(self, document: jcende.MerDocument, max_length: int,
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
		:return: it creates a middle module for cache of pattern mining.
		"""
		return DifferStateRuleMiddle(self)


class DifferStateRuleMiddle:
	"""
	It models the data cache to provide inputs of state difference pattern mining.
	"""

	def __init__(self, inputs: DifferStateRuleInputs):
		self.inputs = inputs
		self.r_tree = DifferStateTree(inputs.get_document())
		return

	## getters

	def get_inputs(self):
		"""
		:return: the inputs module that defines this cache
		"""
		return self.inputs

	def get_document(self):
		"""
		:return: the document basis
		"""
		return self.inputs.get_document()

	def get_tree(self):
		"""
		:return: state difference tree for creating patterns (rules)
		"""
		return self.r_tree

	def get_root(self):
		"""
		:return: the root node of the difference pattern tree
		"""
		return self.r_tree.get_root()

	def get_child(self, parent: DifferStateNode, feature: int):
		"""
		:param parent:
		:param feature:
		:return: the unique child of the parent node in the cache tree space
		"""
		features = set()
		features.add(feature)
		while not parent.is_root():
			features.add(parent.get_feature())
			parent = parent.get_parent()
		return self.r_tree.get_node(features)

	def get_node(self, features):
		"""
		:param features:
		:return: the unique node w.r.t. given feature vector
		"""
		return self.r_tree.get_node(features)

	## selectors

	def get_rules(self, key=None):
		"""
		:param key: None to select all, or MerExecution or MerMutant of the rules that match with
		:return: the set of state difference rules created in the tree that match with given keys
		"""
		rules = set()
		for node in self.r_tree.get_nodes():
			if key is None:
				rules.add(node.get_rule())
			elif node.get_rule().has_sample(key):
				rules.add(node.get_rule())
			else:
				continue
		return rules

	def evaluate(self, used_tests, rules=None):
		"""
		:param used_tests: the set of tests to count the number of executions being killed in data
		:param rules: None to evaluate on every generated rule or the set of rules being evaluated
		:return: mapping from DifferStateRule to [length, support, confidence]
		"""
		if rules is None:
			rules = self.get_rules(None)
		e_dict = dict()
		for rule in rules:
			rule: DifferStateRule
			length, support, confidence = rule.evaluate(used_tests)
			e_dict[rule] = (length, support, confidence)
		return e_dict

	def get_good_rules(self, used_tests, rules=None):
		"""
		:param used_tests:
		:param rules: None to select in every generated rules
		:return: mapping from good rules to [length, support, confidence] under the given test sutie
		"""
		eval_dict = self.evaluate(used_tests, rules)
		good_rules = set()
		for rule, evaluation in eval_dict.items():
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			if (length <= self.inputs.get_max_length()) and \
					(support >= self.inputs.get_min_support()) and \
					(confidence >= self.inputs.get_min_confidence()):
				good_rules.add(rule)
		return good_rules

	@staticmethod
	def __sort_rules_in__(rule_evaluation_dict: dict):
		"""
		:param rule_evaluation_dict: mapping from DifferStateRule to [length, support, confidence]
		:return: the sorted list of patterns selected from the state difference tree.
		"""
		# 1. sort the rules by confidence
		confidence_rule_dict, confidence_rule_list = dict(), list()
		for rule, evaluation in rule_evaluation_dict.items():
			confidence = int(evaluation[2] * 1000000)
			if not (confidence in confidence_rule_dict):
				confidence_rule_dict[confidence] = set()
				confidence_rule_list.append(confidence)
			confidence_rule_dict[confidence].add(rule)
		confidence_rule_list.sort(reverse=True)

		# 2. sort the rules by support
		sorted_rule_list = list()
		for confidence in confidence_rule_list:
			confidence_rules = confidence_rule_dict[confidence]
			support_rule_dict, support_rule_list = dict(), list()
			for rule in confidence_rules:
				rule: DifferStateRule
				evaluation = rule_evaluation_dict[rule]
				support = evaluation[1]
				if not (support in support_rule_dict):
					support_rule_dict[support] = set()
					support_rule_list.append(support)
				support_rule_dict[support].add(rule)
			support_rule_list.sort(reverse=True)
			for support in support_rule_list:
				support_rules = support_rule_dict[support]
				for rule in support_rules:
					rule: DifferStateRule
					sorted_rule_list.append(rule)
		return sorted_rule_list

	def sort_rules(self, used_tests, rules=None):
		"""
		:param used_tests:
		:param rules:
		:return: the sorted list of DifferStateRule in the given rules using used_tests as metrics
		"""
		eval_dict = self.evaluate(used_tests, rules)
		return DifferStateRuleMiddle.__sort_rules_in__(eval_dict)


## mining


class DifferStateRuleFPMiner:
	"""
	Frequent pattern mining based identification.
	"""

	def __init__(self, inputs: DifferStateRuleInputs):
		self.middle = inputs.get_middle_module()
		self.solutions = dict()	# DifferStateRule to [length, support, confidence] in one iteration
		return

	def __mine__(self, parent: DifferStateNode, used_tests, features: list):
		"""
		:param parent:
		:param used_tests:
		:param features:
		:return:
		"""
		inputs = self.middle.get_inputs()
		length, support, confidence = parent.get_rule().evaluate(used_tests)
		self.solutions[parent.get_rule()] = (length, support, confidence)
		if (length < inputs.get_max_length()) and (support >= inputs.get_min_support()) and (confidence < inputs.get_max_confidence()):
			for k in range(0, len(features)):
				child = self.middle.get_child(parent, features[k])
				child: DifferStateNode
				if child != parent:
					self.__mine__(child, used_tests, features[k + 1: ])
		return

	def mine_iter(self, features, used_tests):
		"""
		:param features: the features from which the patterns are generated
		:param used_tests: the test suite used to evaluate patterns being created
		:return:
		"""
		## 1. initialization
		feature_list = self.middle.get_document().anto_space.normal(features)
		if used_tests is None:
			number_of_tests = len(self.middle.get_document().test_space.get_test_cases())
		else:
			number_of_tests = len(used_tests)

		## 2. mining process
		print("\t\t--> Mine({}, {})".format(len(feature_list), number_of_tests), end='\t')
		self.solutions.clear()
		self.__mine__(self.middle.get_root(), used_tests, feature_list)
		good_rules = self.middle.get_good_rules(used_tests, self.solutions.keys())
		print("[{} rules; {} goods]".format(len(self.solutions), len(good_rules)))
		self.solutions.clear()

		## 3. return only the good rules as output
		return good_rules


class DifferStateRuleDTMiner:
	"""
	It implements decision tree based mining.
	"""

	def __init__(self, inputs: DifferStateRuleInputs):
		self.middle = inputs.get_middle_module()
		return

	def __new_decision_tree__(self, used_tests):
		"""
		:param used_tests:
		:return: it generates the decision tree for best classifying samples
		"""
		xmatrix = self.middle.get_document().exec_space.new_feature_matrix()
		ylabels = self.middle.get_document().exec_space.new_label_list(used_tests)
		dc_tree = sktree.DecisionTreeClassifier()
		dc_tree.fit(xmatrix, ylabels)
		plabels = dc_tree.predict(xmatrix)
		print(metrics.classification_report(ylabels, plabels))
		return dc_tree

	def __out_decision_tree__(self, dc_tree: sktree.DecisionTreeClassifier, tree_file_path: str):
		"""
		:param dc_tree:
		:param tree_file_path:
		:return:
		"""
		m_document = self.middle.get_document()
		names = list()
		for annotation in m_document.anto_space.get_annotations():
			annotation: jcende.MerAnnotation
			names.append(annotation.get_code())
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
		generated_rules = set()
		for eid in range(0, len(self.middle.get_document().exec_space.get_executions())):
			node_index = node_indicator.indices[node_indicator.indptr[eid]: node_indicator.indptr[eid + 1]]
			eid_features = set()
			for node_id in node_index:
				if leaf_id[eid] == node_id:
					continue
				elif xmatrix[eid, dc_feature[node_id]] > dc_threshold[node_id]:
					eid_features.add(dc_feature[node_id])
			rule = self.middle.get_node(eid_features).get_rule()
			generated_rules.add(rule)
		return generated_rules

	def mine(self, used_tests, tree_file_path=None):
		"""
		:param used_tests:
		:param tree_file_path: pdf file of decision tree
		:return:
		"""
		dc_tree = self.__new_decision_tree__(used_tests)
		if tree_file_path is not None:
			self.__out_decision_tree__(dc_tree, tree_file_path)
		rules = self.__min_decision_path__(dc_tree)
		return self.middle.get_good_rules(used_tests, rules)


## output layer


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


def min_prediction(rules):
	"""
	:param rules:
	:return: the minimal set of KillPredictionNode for covering all the mutants
	"""
	## 1. capture the nodes and mutants for coverage
	all_rules, all_mutants, cov_rules = set(), set(), set()
	for rule in rules:
		rule: DifferStateRule
		all_rules.add(rule)
		for mutant in rule.get_mutants():
			all_mutants.add(mutant)
	## 2. select the nodes until all the mutants removed
	while len(all_mutants) > 0 and len(all_rules) > 0:
		next_rule = jcbase.rand_select(all_rules)
		all_rules.remove(next_rule)
		next_rule: DifferStateRule
		next_mutants = next_rule.get_mutants()
		if len(next_mutants & all_mutants) > 0:
			cov_rules.add(next_rule)
			for mutant in next_mutants:
				if mutant in all_mutants:
					all_mutants.remove(mutant)
	return cov_rules


class DifferStateOutput:
	"""
	It defines how information to be printed on file output streams.
	"""

	def __init__(self, c_document: jctest.CDocument, inputs: DifferStateRuleInputs):
		self.c_document = c_document
		self.m_document = inputs.get_document()
		self.inputs = inputs
		self.writer = None
		return

	## basic methods

	def __output__(self, text: str):
		"""
		:param text:
		:return: write the text to output file
		"""
		self.writer: TextIO
		self.writer.write(text)
		self.writer.flush()
		return

	def __mut2str__(self, mutant: jcende.MerMutant):
		"""
		:param mutant:
		:return: id result class operator function line "code" [parameter]
		"""
		source_mutant = self.c_document.project.muta_space.get_mutant(mutant.get_mid())
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
		return "{}\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t[{}]".format(mid, result, m_class, m_operator,
															 func_name, line, code, parameter)

	def __rul2str__(self, rule: DifferStateRule, used_tests):
		"""
		:param rule: 		killable prediction rule
		:param used_tests:	used to evaluate pattern prediction metrics
		:return: 			id length executions mutants support confidence(%)
		"""
		rid = str(rule.get_features())
		executions = len(rule.get_executions())
		length, support, confidence = rule.evaluate(used_tests)
		confidence = int(confidence * 10000) / 100.0
		mutants = len(rule.get_mutants())
		return "{}\t{}\t{}\t{}\t{}\t{}%".format(rid, length, executions, mutants, support, confidence)

	def __cod2str__(self, annotation: jcende.MerAnnotation):
		"""
		:param condition:
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
		return "{}\t{}\t#{}\t\"{}\"\t\"{}\"\t[{}]".format(logic_type, execution, line, statement, store_unit.get_cir_code(), symb_value)

	# write methods

	def write_rules_to_mutants(self, middle: DifferStateRuleMiddle, rules, file_path: str, used_tests):
		"""
		:param rules:
		:param file_path:
		:param used_tests:
		:return:
		"""
		orig_mutants, pred_mutants = set(), set()
		for execution in self.m_document.exec_space.get_executions():
			execution: jcende.MerExecution
			if execution.get_mutant().is_killed_in(used_tests):
				continue
			else:
				orig_mutants.add(execution.get_mutant())
		minimal_rules = min_prediction(rules)

		with open(file_path, 'w') as writer:
			self.writer = writer

			## 3-A. node information print
			rules = middle.sort_rules(used_tests, rules)
			for rule in rules:
				self.__output__("[P]\t{}\n".format(self.__rul2str__(rule, used_tests)))
				condition_index = 0
				for condition in rule.get_annotations():
					condition_index += 1
					self.__output__("\t[C.{}]\t{}\n".format(condition_index, self.__cod2str__(condition)))
				mutant_index = 0
				for mutant in rule.get_mutants():
					mutant_index += 1
					self.__output__("\t[M.{}]\t{}\n".format(mutant_index, self.__mut2str__(mutant)))
					pred_mutants.add(mutant)
				self.__output__("\n")
			optimal_rate = len(minimal_rules) / (len(pred_mutants) + 0.0001)

			## 3-B. summarized of metrics
			precision, recall, f1_score = prf_evaluation(orig_mutants, pred_mutants)
			self.__output__("\nOrig = {}\tPred = {}\tRules = {}({}%)\n".format(len(orig_mutants),
																			   len(pred_mutants),
																			   len(minimal_rules),
																			   int(optimal_rate * 10000) / 100.0))
			self.__output__("Evaluate:\tP = {}%\tR = {}%\tScore = {}\n".format(int(precision * 10000) / 100.0,
																			   int(recall * 10000) / 100.0,
																			   int(f1_score * 10000) / 100.0))
			self.__output__("\n")

			## 3-C. summary of all nodes
			self.__output__("Rule\tLength\tExecutions\tMutants\tResult\tKilled\tAlive\tConfidence(%)\n")
			for rule in rules:
				rid = str(rule.get_features())
				length = len(rule.get_features())
				rule_exec = len(rule.get_executions())
				rule_muta = len(rule.get_mutants())
				result, killed, survive, confidence = rule.predict(used_tests)
				self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}%\n".format(rid, length, rule_exec, rule_muta,
																		   result, killed, survive,
																		   int(confidence * 10000) / 100.0))

		return

	def write_mutants_to_rules(self, middle: DifferStateRuleMiddle, rules, file_path: str, used_tests):
		"""
		:param rules:
		:param file_path:
		:param used_tests:
		:return:
		"""
		pred_mutants = set()
		for rule in rules:
			rule: DifferStateRule
			for mutant in rule.get_mutants():
				pred_mutants.add(mutant)
		with open(file_path, 'w') as writer:
			self.writer = writer  # set the file output stream
			for mutant in pred_mutants:
				mutant_rules = set()
				for rule in rules:
					if rule.has_sample(mutant):
						mutant_rules.add(rule)
				mutant_rules = middle.sort_rules(used_tests, mutant_rules)
				self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
				rule_index = 0
				for rule in mutant_rules:
					rule_index += 1
					self.__output__("\t[R.{}]\t{}\n".format(rule_index,
															self.__rul2str__(rule, used_tests)))
					condition_index = 0
					for condition in rule.get_annotations():
						condition_index += 1
						self.__output__(
							"\t\t[C.{}.{}]\t{}\n".format(rule_index, condition_index, self.__cod2str__(condition)))
				self.__output__("\n")
		return


## testing method


def do_mining(c_document: jctest.CDocument, m_document: jcende.MerDocument,
			  output_directory: str, file_name: str, max_length: int,
			  min_support: int, min_confidence: float, max_confidence: float,
			  used_tests):
	"""
	:param used_tests:
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
	print("Start killable prediction mining on Project #{}".format(file_name))
	o_directory = os.path.join(output_directory, file_name)
	if not os.path.exists(o_directory):
		os.mkdir(o_directory)
	print("\t(1) Load {} executions between {} mutants and {} tests.".format(
		len(m_document.exec_space.get_executions()),
		len(m_document.exec_space.get_mutants()),
		len(m_document.test_space.get_test_cases())))

	# II. construct the mining modules
	inputs = DifferStateRuleInputs(m_document, max_length, min_support, min_confidence, max_confidence)
	writer = DifferStateOutput(c_document, inputs)
	print("\t(2) Mining by: max_len = {}; min_supp = {}; min_conf = {}; max_conf = {}.".format(inputs.get_max_length(),
																							   inputs.get_min_support(),
																							   inputs.get_min_confidence(),
																							   inputs.get_max_confidence()))

	## III. generate patterns from frequent pattern mining
	unkilled_features = set()
	for execution in m_document.exec_space.get_executions():
		execution: jcende.MerExecution
		if execution.get_mutant().is_killed_in(used_tests):
			continue
		else:
			for feature in execution.get_features():
				unkilled_features.add(feature)
	fp_miner = DifferStateRuleFPMiner(inputs)
	fp_rules = fp_miner.mine_iter(unkilled_features, used_tests)
	writer.write_rules_to_mutants(fp_miner.middle, fp_rules, os.path.join(o_directory, file_name + ".f2r"), used_tests)
	writer.write_mutants_to_rules(fp_miner.middle, fp_rules, os.path.join(o_directory, file_name + ".f2m"), used_tests)

	## IV. decision tree based pattern mining
	inputs.max_length = 256
	dc_miner = DifferStateRuleDTMiner(inputs)
	dc_rules = dc_miner.mine(used_tests, os.path.join(o_directory, file_name + ".tree.pdf"))
	writer.write_rules_to_mutants(dc_miner.middle, dc_rules, os.path.join(o_directory, file_name + ".d2r"), used_tests)
	writer.write_mutants_to_rules(dc_miner.middle, dc_rules, os.path.join(o_directory, file_name + ".d2m"), used_tests)
	print()
	return


def main(project_directory: str, encoding_directory: str, output_directory: str, exec_postfix: str):
	"""
	:param project_directory:
	:param encoding_directory:
	:param output_directory:
	:param exec_postfix: .stn or .stp
	:return:
	"""
	## initialization
	max_length, min_support, min_confidence, max_confidence = 1, 2, 0.75, 0.99
	print_equivalent, print_individual = True, False

	## testing on every project in the project directory
	for file_name in os.listdir(project_directory):
		## load document and encoded features into memory
		c_document_directory = os.path.join(project_directory, file_name)
		m_document_directory = os.path.join(encoding_directory, file_name)
		c_document = jctest.CDocument(c_document_directory, file_name, exec_postfix)
		m_document = jcende.MerDocument(m_document_directory, file_name)

		## perform pattern mining and generation proceed
		do_mining(c_document, m_document, output_directory, file_name,
				  max_length, min_support, min_confidence, max_confidence, None)
	return


## execution script


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/features"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/patterns"
	main(proj_directory, enco_directory, outs_directory, ".stp")

