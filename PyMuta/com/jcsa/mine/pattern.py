""" This file defines the structural model to describe killable prediction rule (pattern) and implement mining. """


import os
from typing import TextIO
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.test as jctest
import com.jcsa.rule.encode as jcenco


## definition


class KillPredictionTree:
	"""
	It maintains the tree-structured space to construct killable prediction rules uniquely.
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the encoded data source for pattern mining
		"""
		self.m_document = m_document
		self.root = KillPredictionNode(self, None, -1)
		return

	def __done__(self):
		"""
		:return: to remove the staticmethod warning
		"""
		return self

	def get_m_document(self):
		"""
		:return: the encoded data source for pattern mining
		"""
		return self.m_document

	def get_u_features(self, features):
		"""
		:param features: the set of integer features that encode symbolic conditions in mutant executions
		:return: the unique sorted sequence of integer features used to encode symbolic mutant executions
		"""
		self.__done__()
		feature_list = list()
		for feature in features:
			feature: int
			if feature >= 0 and not (feature in feature_list):
				feature_list.append(feature)
		feature_list.sort()
		return feature_list

	def encode(self, conditions):
		"""
		:param conditions: the collection of (encoded) symbolic conditions to be encoded by the document of data source
		:return: the unique sequence of integer features that encode the given symbolic conditions in input collections
		"""
		features = set()
		for condition in conditions:
			condition: jcenco.MerCondition
			features.add(condition.get_cid())
		return self.get_u_features(features)

	def decode(self, features):
		"""
		:param features: the set of integer features encoding the symbolic conditions incorporated in rules
		:return: the set of symbolic conditions included in the killable prediction rule specified by input
		"""
		features = self.get_u_features(features)
		conditions = set()
		condition_space = self.get_m_document().cond_space
		for feature in features:
			condition = condition_space.get_condition(feature)
			conditions.add(condition)
		return conditions

	def get_root(self):
		return self.root

	def get_node(self, features):
		"""
		:param features:
		:return: the unique node of which rule referring to the given features
		"""
		features = self.get_u_features(features)
		node = self.root
		for feature in features:
			node = node.new_child(feature)
		return node


class KillPredictionNode:
	"""
	It denotes a node in KillPredictionTree, referring to one unique rule in the tree space.
	"""

	def __init__(self, tree: KillPredictionTree, parent, edge_feature: int):
		"""
		:param tree: 	the tree space where this node is created
		:param parent: 	the parent of the node or None if it is a root
		:param edge_feature: the feature annotated on edge from parent to this node or -1 if it is a root
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.edge_feature = -1
		else:
			parent: KillPredictionNode
			self.parent = parent
			self.edge_feature = edge_feature
		self.children = list()
		self.rule = KillPredictionRule(self)
		return

	def get_tree(self):
		"""
		:return: the tree space where this node is created
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the tree node is a root without any valid parent in the tree space
		"""
		if self.parent is None:
			return True
		else:
			return False

	def is_leaf(self):
		"""
		:return: whether the tree node is a leaf without any child
		"""
		return len(self.children) == 0

	def get_parent(self):
		"""
		:return: the parent of the node or None if it is a root
		"""
		if self.parent is None:
			return None
		else:
			self.parent: KillPredictionNode
			return self.parent

	def get_children(self):
		"""
		:return: the children created under this node
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children created under the tree
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child created under this node
		"""
		child = self.children[k]
		child: KillPredictionNode
		return child

	def get_rule(self):
		"""
		:return: the unique killable prediction rule that the node represents
		"""
		return self.rule

	def new_child(self, edge_feature: int):
		"""
		It will create a new child under the parent based on following rules.
		(1)	if edge_feature is smaller than (equal with) the local feature, return the node itself;
		(2) if there exists some child w.r.t. the edge_feature, return that child without creating;
		(3) otherwise, create a new child using the edge_feature under the node and return it back.
		:param edge_feature:
		:return: either new child or existing one uniquely defined using the edge_feature
		"""
		if edge_feature <= self.edge_feature:
			return self
		else:
			for child in self.children:
				child: KillPredictionNode
				if child.edge_feature == edge_feature:
					return child
			new_child = KillPredictionNode(self.tree, self, edge_feature)
			self.children.append(new_child)
			new_child.parent = self
			return new_child


class KillPredictionRule:
	"""
	It maintains the definition of killable prediction rule and data samples match with it
	"""

	## generation

	def __init__(self, node: KillPredictionNode):
		"""
		:param node: the tree node to which the rule uniquely corresponds
		"""
		self.node = node
		self.features = self.__generate_features__()
		self.executions = self.__produce_execution__()
		return

	def __generate_features__(self):
		"""
		:return: the unique sequence of integer features encoding the symbolic conditions included in premises
		"""
		features = set()
		node = self.node
		while not node.is_root():
			features.add(node.edge_feature)
			node = node.get_parent()
		return self.node.get_tree().get_u_features(features)

	def __match_with_sample__(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return: whether the pattern matches with the execution
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __produce_execution__(self):
		"""
		:return: the set of symbolic executions matching with this rule
		"""
		if self.node.is_root():
			parent_executions = self.node.get_tree().get_m_document().exec_space.get_executions()
		else:
			parent_executions = self.node.get_parent().get_rule().executions
		executions = set()
		for execution in parent_executions:
			execution: jcenco.MerExecution
			if self.__match_with_sample__(execution):
				executions.add(execution)
		return executions

	## definition

	def get_tree_node(self):
		"""
		:return: the tree node where the rule is uniquely defined in the space
		"""
		return self.node

	def get_features(self):
		"""
		:return: the unique features encoding the conditions in the premises
		"""
		return self.features

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions incorporated in the premises
		"""
		return self.node.get_tree().decode(self.features)

	def __len__(self):
		"""
		:return: the size of the pattern
		"""
		return len(self.features)

	def __str__(self):
		return str(self.features)

	## evaluation

	def get_executions(self):
		"""
		:return: the set of mutant's executions match with this pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which executions match with this pattern
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def predict(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase or integers that used to decide the prediction
		:return: result, killed, alive, confidence
		"""
		killed, alive, total = 0, 0, 0
		for execution in self.executions:
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
			total += 1
		if killed >= alive:
			result = True
		else:
			result = False
		if total > 0:
			confidence = max(killed, alive) / total
		else:
			confidence = 0.0
		return result, killed, alive, confidence

	def evaluate(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase or integers that used to decide the prediction
		:return: length, support, confidence, result
		"""
		result, killed, alive, confidence = self.predict(used_tests)
		length = len(self)
		support = alive
		if support > 0:
			confidence = support / (killed + alive)
		else:
			confidence = 0.0
		return length, support, confidence, result


## algorithm


class KillPredictionInputs:
	"""
	It specifies the inputs parameters for running mining algorithm.
	"""

	def __init__(self, m_document: jcenco.MerDocument,
				 max_length: int, min_support: int,
				 min_confidence: float, max_confidence: float,
				 min_output_number: int, max_output_number: int):
		"""
		:param m_document: the document plays as the data source for evaluation
		:param max_length: the maximal length allowed in each generated pattern
		:param min_support: the minimal support required for "good" patterns
		:param min_confidence: the minimal confidence need be achieved for pattern
		:param max_confidence: the maximal confidence to stop the traversal of tree
		:param min_output_number: the minimal number of output patterns in one single mining
		:param max_output_number: the maximal number of output patterns in one single mining
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
	The middle layer maintains the data and generation of patterns in mining process
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.inputs = inputs
		self.tree = KillPredictionTree(self.inputs.get_document())
		return

	## data getters

	def __done__(self):
		return self

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	## tree getters

	def get_tree(self):
		return self.tree

	def get_root(self):
		return self.tree.get_root()

	def get_child(self, parent: KillPredictionNode, feature: int):
		self.__done__()
		return parent.new_child(feature)


class KillPredictionMiners:
	"""
	It implements the pattern mining algorithm.
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.middle = KillPredictionMiddle(inputs)
		self.solutions = dict()	# KillPredictionRule --> (length, support, confidence)
		return

	def __eval__(self, node: KillPredictionNode, used_tests):
		"""
		:param node:
		:param used_tests:
		:return: length, support, confidence
		"""
		if not (node in self.solutions):
			length, support, confidence, result = node.get_rule().evaluate(used_tests)
			self.solutions[node] = (length, support, confidence)
		solution = self.solutions[node]
		length = solution[0]
		support = solution[1]
		confidence = solution[2]
		length: int
		support: int
		confidence: float
		## print("\t\t\tRule\t{}\t{}\t{}\t{}".format(node.get_rule(), length, support, confidence))
		return length, support, confidence

	def __pass__(self, node: KillPredictionNode, used_tests):
		"""
		:param node:
		:param used_tests:
		:return:
		"""
		if node.is_root():
			return True
		else:
			length, support, confidence = self.__eval__(node, used_tests)
			inputs = self.middle.get_inputs()
			if length < inputs.get_max_length() and support >= inputs.get_min_support() and confidence <= inputs.get_max_confidence():
				return True
			else:
				return False

	def __mine__(self, parent: KillPredictionNode, features: list, used_tests):
		"""
		:param parent:
		:param features:
		:param used_tests:
		:return:
		"""
		if self.__pass__(parent, used_tests):
			for k in range(0, len(features)):
				child = self.middle.get_child(parent, features[k])
				if child != parent:
					self.__mine__(child, features[k + 1:], used_tests)
		return

	def __sort__(self, used_tests):
		"""
		:return: create the sorted list of killable prediction rules by confidence and support
		"""
		## 1. sort the nodes based on confidence
		confidence_list, confidence_dict = list(), dict()
		for node in self.solutions.keys():
			length, support, confidence = self.__eval__(node, used_tests)
			confidence = int(confidence * 1000000)
			if not (confidence in confidence_dict):
				confidence_dict[confidence] = set()
				confidence_list.append(confidence)
			confidence_dict[confidence].add(node)
		confidence_list.sort(reverse=True)

		## 2. sort the nodes based on support finally
		sort_nodes, support_list, support_dict = list(), list(), dict()
		for confidence in confidence_list:
			confidence_nodes = confidence_dict[confidence]
			support_dict.clear()
			support_list.clear()
			for node in confidence_nodes:
				length, support, confidence = self.__eval__(node, used_tests)
				if not (support in support_dict):
					support_dict[support] = set()
					support_list.append(support)
				support_dict[support].add(node)
			support_list.sort(reverse=True)

			for support in support_list:
				for node in support_dict[support]:
					node: KillPredictionNode
					sort_nodes.append(node)
		return sort_nodes

	def __outs__(self, used_tests):
		"""
		:param used_tests:
		:return: good_nodes, node_evaluation_dict
		"""
		## 1. select the sorted list of nodes
		sort_nodes = self.__sort__(used_tests)
		good_nodes = list()

		## 2. select the good nodes by metrics
		inputs = self.middle.get_inputs()
		for node in sort_nodes:
			length, support, confidence = self.__eval__(node, used_tests)
			if length <= inputs.get_max_length() and support >= inputs.get_min_support() and confidence >= inputs.get_min_confidence():
				good_nodes.append(node)

		## 3. select more available nodes in
		if len(good_nodes) < inputs.get_min_output_number():
			for node in sort_nodes:
				if not (node in good_nodes):
					good_nodes.append(node)
				if len(good_nodes) > inputs.get_min_output_number():
					break

		## 4. remove unneeded nodes in the tree
		if len(good_nodes) > inputs.get_max_output_number():
			good_nodes = good_nodes[0: inputs.get_max_output_number()]

		## 5. generate evaluation dict
		node_evaluation_dict = dict()
		for node in good_nodes:
			length, support, confidence = self.__eval__(node, used_tests)
			node_evaluation_dict[node] = (length, support, confidence)
		return good_nodes, node_evaluation_dict

	def mine(self, features, used_tests):
		"""
		:param features:
		:param used_tests:
		:return: good_nodes, node_evaluation_dict
		"""
		## 1. initialization
		features = self.middle.get_tree().get_u_features(features)
		self.solutions.clear()
		root_node = self.middle.get_root()
		if used_tests is None:
			used_tests_number = len(self.middle.inputs.get_document().test_space.get_test_cases())
		else:
			used_tests_number = len(used_tests)

		## 2. pattern mining
		print("\t\t\t\tMine({}, {})".format(len(features), used_tests_number), end="")
		self.__mine__(root_node, features, used_tests)
		good_nodes, node_evaluation_dict = self.__outs__(used_tests)
		print("\t==> [{} rules & {} goods]".format(len(self.solutions), len(good_nodes)))

		## 3. output final outputs
		self.solutions.clear()
		return good_nodes, node_evaluation_dict


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


def min_prediction(nodes):
	"""
	:param nodes:
	:return: the minimal set of KillPredictionNode for covering all the mutants
	"""
	## 1. capture the nodes and mutants for coverage
	all_nodes, all_mutants, cov_nodes = set(), set(), set()
	for node in nodes:
		node: KillPredictionNode
		all_nodes.add(node)
		for mutant in node.get_rule().get_mutants():
			all_mutants.add(mutant)
	## 2. select the nodes until all the mutants removed
	while len(all_mutants) > 0 and len(all_nodes) > 0:
		next_node = jcbase.rand_select(all_nodes)
		all_nodes.remove(next_node)
		next_node: KillPredictionNode
		next_mutants = next_node.get_rule().get_mutants()
		if len(next_mutants & all_mutants) > 0:
			cov_nodes.add(next_node)
			for mutant in next_mutants:
				if mutant in all_mutants:
					all_mutants.remove(mutant)
	return cov_nodes


class KillPredictionOutput:
	"""
	It defines how information to be printed on file output streams.
	"""

	def __init__(self, c_document: jctest.CDocument, inputs: KillPredictionInputs):
		self.c_document = c_document
		self.m_document = inputs.get_document()
		self.miner = KillPredictionMiners(inputs)
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

	def __rul2str__(self, rule: KillPredictionRule, used_tests):
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
		source_condition = self.c_document.get_condition_space().decode(condition.get_code())
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

	# write methods

	def write_unkilled_rules(self, file_path: str, used_tests):
		"""
		:param file_path: xxx.e2r
		:param used_tests: to classify mutations as killed or not
		:return:
		"""
		## 1. collect features and samples for mining
		features, orig_mutants, pred_mutants = set(), set(), set()
		for execution in self.miner.middle.get_inputs().get_document().exec_space.get_executions():
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				pass
			else:
				for feature in execution.get_features():
					features.add(feature)
				orig_mutants.add(execution.get_mutant())

		## 2. perform pattern mining on given inputs
		old_output_number = self.miner.middle.inputs.max_output_number
		self.miner.middle.inputs.max_output_number = len(features)
		good_nodes, node_evaluation_dict = self.miner.mine(features, used_tests)
		minimal_nodes = min_prediction(good_nodes)
		self.miner.middle.inputs.max_output_number = old_output_number

		## 3. output equivalent rules to file path
		with open(file_path, 'w') as writer:
			self.writer = writer

			## 3-A. node information print
			for node in good_nodes:
				rule = node.get_rule()
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
			optimal_rate = len(minimal_nodes) / (len(pred_mutants) + 0.0001)

			## 3-B. summarized of metrics
			precision, recall, f1_score = prf_evaluation(orig_mutants, pred_mutants)
			self.__output__("\nOrig = {}\tPred = {}\tRules = {}({}%)\n".format(len(orig_mutants),
																			   len(pred_mutants),
																			   len(minimal_nodes),
																			   int(optimal_rate * 10000) / 100.0))
			self.__output__("Evaluate:\tP = {}%\tR = {}%\tScore = {}\n".format(int(precision * 10000) / 100.0,
																			   int(recall * 10000) / 100.0,
																			   int(f1_score * 10000) / 100.0))
			self.__output__("\n")

			## 3-C. summary of all nodes
			self.__output__("Rule\tLength\tExecutions\tMutants\tResult\tKilled\tAlive\tConfidence(%)\n")
			for node in good_nodes:
				rule = node.get_rule()
				rid = str(rule.get_features())
				length = len(rule.get_features())
				rule_exec = len(rule.get_executions())
				rule_muta = len(rule.get_mutants())
				result, killed, survive, confidence = rule.predict(used_tests)
				self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}%\n".format(rid, length, rule_exec, rule_muta,
																		   result, killed, survive,
																		   int(confidence * 10000) / 100.0))
		return

	def __mine_in_mutant__(self, mutant: jcenco.MerMutant, used_tests):
		"""
		:param mutant:
		:param used_tests:
		:return:
		"""
		features = set()
		for execution in self.m_document.exec_space.get_executions_of(mutant):
			execution: jcenco.MerExecution
			for feature in execution.get_features():
				features.add(feature)
		good_nodes, node_evaluation_dict = self.miner.mine(features, used_tests)
		return good_nodes, node_evaluation_dict

	def __mine_mutant__(self, mutant: jcenco.MerMutant, max_tests: int):
		"""
		:param mutant:
		:param max_tests:
		:return:
		"""
		## 1. capture the used-tests to model its semantics
		used_tests = mutant.get_result().get_tests_of(False)
		if len(used_tests) > max_tests:
			selected_tests = set()
			while len(selected_tests) < max_tests:
				selected_test = jcbase.rand_select(used_tests)
				selected_tests.add(selected_test)
			used_tests = selected_tests

		## 2. capture the correlated features
		features = set()
		for execution in self.m_document.exec_space.get_executions_of(mutant):
			execution: jcenco.MerExecution
			for feature in execution.get_features():
				features.add(feature)

		## 3. perform pattern mining on one mutant
		good_nodes, node_evaluation_dict = self.miner.mine(features, used_tests)
		return good_nodes, node_evaluation_dict, used_tests

	def write_unkilled_mutants(self, file_path: str, used_tests):
		"""
		:param file_path:
		:param used_tests:
		:return:
		"""
		mutant_rule_dict = dict()
		with open(file_path, 'w') as writer:
			self.writer = writer		# set the file output stream
			proceed_counter, proceed_summary = 0, len(self.m_document.exec_space.get_mutants())
			for mutant in self.m_document.exec_space.get_mutants():
				## A. mine the good nodes for predicting mutation equivalence
				proceed_counter += 1
				if mutant.get_result().is_killed_in(used_tests):
					continue
				else:
					## A. mining the best matched prediction rule for the specific mutant
					good_nodes, node_evaluation_dict = self.__mine_in_mutant__(mutant, used_tests)
					if len(good_nodes) > 0:
						good_node = good_nodes[0]
						length, support, confidence = node_evaluation_dict[good_node]
						mutant_rule_dict[mutant] = (good_node, length, support, confidence)

					## B. print the mutant and its corresponding prediction rules
					self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
					rule_index = 0
					for good_node in good_nodes:
						rule_index += 1
						self.__output__("\t[R.{}]\t{}\n".format(rule_index,
																self.__rul2str__(good_node.get_rule(), used_tests)))
						condition_index = 0
						for condition in good_node.get_rule().get_conditions():
							condition_index += 1
							self.__output__(
								"\t\t[C.{}.{}]\t{}\n".format(rule_index, condition_index, self.__cod2str__(condition)))
					self.__output__("\n")
		return mutant_rule_dict

	def write_mutation_rules(self, file_path: str, max_tests: int):
		"""
		:param file_path: xxx.m2r
		:param max_tests: the maximal number of tests used to select tests for each mutant
		:return: [Mutant: KillPredictionNode, length, support, confidence]
		"""
		mutant_rule_dict = dict()
		with open(file_path, 'w') as writer:
			self.writer = writer		# set the file output stream
			proceed_counter, proceed_summary = 0, len(self.m_document.exec_space.get_mutants())
			for mutant in self.m_document.exec_space.get_mutants():
				## A. mine the good nodes for predicting mutation equivalence
				proceed_counter += 1
				print("\t\t\tProceeding at {}[{}/{}]".format(self.m_document.name, proceed_counter, proceed_summary))
				good_nodes, node_evaluation_dict, used_tests = self.__mine_mutant__(mutant, max_tests)
				if len(good_nodes) > 0:
					good_node = good_nodes[0]
					length, support, confidence = node_evaluation_dict[good_node]
					mutant_rule_dict[mutant] = (good_node, length, support, confidence)

				## B. print the mutant and its corresponding prediction rules
				self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
				rule_index = 0
				for good_node in good_nodes:
					rule_index += 1
					self.__output__("\t[R.{}]\t{}\n".format(rule_index,
															self.__rul2str__(good_node.get_rule(), used_tests)))
					condition_index = 0
					for condition in good_node.get_rule().get_conditions():
						condition_index += 1
						self.__output__(
							"\t\t[C.{}.{}]\t{}\n".format(rule_index, condition_index, self.__cod2str__(condition)))
				self.__output__("\n")
		return mutant_rule_dict

	def write_available_nodes(self, mutant_best_nodes: dict, file_path: str):
		"""
		:param mutant_best_nodes: MerMutant --> {KillPredictionNode, length, support, confidence}
		:param file_path:
		:return:
		"""
		## 1. collect the good data inputs
		orig_mutants, pred_mutants, node_mutants = set(), set(), dict()
		for mutant in self.m_document.exec_space.get_mutants():
			orig_mutants.add(mutant)
		for mutant in mutant_best_nodes.keys():
			mutant: jcenco.MerMutant
			node_evaluation = mutant_best_nodes[mutant]
			node = node_evaluation[0]
			node: KillPredictionNode
			if not (node in node_mutants):
				node_mutants[node] = set()
			node_mutants[node].add(mutant)
			pred_mutants.add(mutant)
		minimal_nodes = min_prediction(node_mutants.keys())
		optimize_rate = len(minimal_nodes) / len(pred_mutants)

		with open(file_path, 'w') as writer:
			self.writer = writer	# establish the file output stream
			## II. print the nodes and corresponding mutants
			for node, mutants in node_mutants.items():
				self.__output__("[P]\t{}\n".format(self.__rul2str__(node.get_rule(), None)))
				condition_index = 0
				for condition in node.get_rule().get_conditions():
					condition_index += 1
					self.__output__("\t[C.{}]\t{}\n".format(condition_index, self.__cod2str__(condition)))
				mutant_index = 0
				for mutant in mutants:
					mutant_index += 1
					self.__output__("\t[M.{}]\t{}\n".format(mutant_index, self.__mut2str__(mutant)))
				self.__output__("\n")

			## III. summarization of the good nodes
			precision, recall, f1_score = prf_evaluation(orig_mutants, pred_mutants)
			self.__output__("\nOrig = {}\tPred = {}\tRules = {}({}%)\n".format(len(orig_mutants),
																			   len(pred_mutants),
																			   len(minimal_nodes),
																			   int(optimize_rate * 10000) / 100.0))
			self.__output__("Evaluate:\tP = {}%\tR = {}%\tScore = {}\n".format(int(precision * 10000) / 100.0,
																			   int(recall * 10000) / 100.0,
																			   int(f1_score * 10000) / 100.0))
			self.__output__("\n")
		return


## testing methods


def do_mining(c_document: jctest.CDocument, m_document: jcenco.MerDocument,
			  output_directory: str, file_name: str, max_length: int,
			  min_support: int, min_confidence: float, max_confidence: float,
			  print_equivalence: bool, print_individual: bool):
	"""
	:param c_document: original document
	:param m_document: encoded document
	:param output_directory: the output directory where files are printed
	:param file_name: the project name
	:param max_length: the maximal length of generated patterns
	:param min_support: minimal support for mining
	:param min_confidence: minimal confidence for mining
	:param max_confidence: maximal confidence for mining
	:param print_equivalence: whether to print patterns of equivalent and unkilled mutations
	:param print_individual: whether to print patterns of each individual mutant and their counters
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
	inputs = KillPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence, 4, 8)
	writer = KillPredictionOutput(c_document, inputs)
	print("\t(2) Mining by: max_len = {}; min_supp = {}; min_conf = {}; max_conf = {}.".format(inputs.get_max_length(),
																							   inputs.get_min_support(),
																							   inputs.get_min_confidence(),
																							   inputs.get_max_confidence()))

	## III. perform pattern mining and output from equivalent (undetected) mutations
	if print_equivalence:
		writer.write_unkilled_rules(os.path.join(o_directory, file_name + ".e2r"), None)
		writer.write_unkilled_rules(os.path.join(o_directory, file_name + ".u2r"), m_document.test_space.rand_test_cases(256))
		writer.write_unkilled_mutants(os.path.join(o_directory, file_name + ".r2e"), None)
		writer.write_unkilled_mutants(os.path.join(o_directory, file_name + ".r2u"), m_document.test_space.rand_test_cases(256))
		print("\t(3.E) Generate patterns from equivalent & undetected mutations for", file_name)

	## IV. perform pattern mining and output from
	if print_individual:
		mutant_nodes = writer.write_mutation_rules(os.path.join(o_directory, file_name + ".m2r"), 128)
		writer.write_available_nodes(mutant_nodes, os.path.join(o_directory, file_name + ".r2m"))
		print("\t(4) Generate patterns from every individual mutation and count the coverage metrics.")
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
		m_document = jcenco.MerDocument(m_document_directory, file_name)

		## perform pattern mining and generation proceed
		do_mining(c_document, m_document, output_directory, file_name,
				  max_length, min_support, min_confidence, max_confidence,
				  print_equivalent, print_individual)
	return


## execution script


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/features"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/patterns"
	main(proj_directory, enco_directory, outs_directory, ".stp")

