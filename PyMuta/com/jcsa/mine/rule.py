"""This file defines the model of killable prediction rule and the input-output modules for driving mining algorithm."""


from collections import deque
import com.jcsa.libs.base as jcbase
import com.jcsa.mine.code as jecode


class KillPredictionRule:
	"""
	It defines the killable prediction rule using a sequence of integer features encoding the CirAnnotation(s) that are
	incorporated in the prediction rule's logical argument premise.
	"""

	## constructor

	def __init__(self, document: jecode.MerDocument, features, parent_executions):
		"""
		:param document: 			the document of encoded features on which the prediction rule is defined
		:param features: 			the sequence of integer features to encode the annotations for this rule
		:param parent_executions: 	the set of annotations in which the rule will match with or None for all
		"""
		self.document = document
		self.features = document.anot_space.normal(features)
		self.executions = set()
		if parent_executions is None:
			parent_executions = document.exec_space.get_executions()
		for execution in parent_executions:
			execution: jecode.MerExecution
			if self.__matched_with__(execution):
				self.executions.add(execution)
		return

	def __matched_with__(self, execution: jecode.MerExecution):
		"""
		:param execution: a symbolic execution annotated with requirement for killing a mutation
		:return: 	True if the rule matches with the execution, in which all the annotations denoted by this rule are
					incorporated by the symbolic execution as input.
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	## features

	def has_features(self, features):
		"""
		:param features: None to return True
		:return: whether any feature in inputs are used in the rule
		"""
		if features is None:
			return True
		for feature in features:
			if feature in self.features:
				return True
		return False

	def get_features(self):
		"""
		:return: the set of integer features encoding the annotations incorporated in this rule
		"""
		return self.features

	def get_annotations(self):
		"""
		:return: the set of annotations incorporated in this rule
		"""
		return self.document.anot_space.decode(self.features)

	def __len__(self):
		"""
		:return: the length of the rule is the number of its features
		"""
		return len(self.features)

	def __str__(self):
		return str(self.features)

	## samples

	def get_executions(self):
		"""
		:return: the set of symbolic executions matching with this rule
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which symbolic executions matching with this rule
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def has_sample(self, key):
		"""
		:param key: either MerMutant or MerExecution, or None to return true
		:return: True if the execution (or mutant) is matched with this rule
		"""
		if key is None:
			return True
		elif isinstance(key, jecode.MerExecution):
			return key in self.executions
		else:
			key: jecode.MerMutant
			for execution in self.document.exec_space.get_executions_of(key):
				if execution in self.executions:
					return True
			return False

	def count_class(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase (or tid) to decide of which executions in rule are killed.
		:return: killed_number, alived_number
		"""
		killed, alived = 0, 0
		for execution in self.executions:
			if execution.get_mutant().is_killed_in(used_tests):
				killed += 1
			else:
				alived += 1
		return killed, alived

	def is_consistent(self, test):
		"""
		:param test: MerTestCase or integer ID
		:return: true if all the executions in the rule produce identical results
		"""
		old_result, new_result = None, None
		for execution in self.executions:
			new_result = execution.get_mutant().is_killed_by(test)
			if old_result is None:
				old_result = new_result
			elif old_result != new_result:
				return False
			else:
				pass
		return True

	## measurement

	def rsc_measure(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase (or tid) to decide of which executions in rule are killed.
		:return: 	prd_result, prd_support, prd_confidence
					(1) prd_result: 	True if the majority of the executions matched, are killed by input tests;
					(2) prd_support:	the number of executions matched with matching with the prediction result;
					(3) prd_confidence:	the probability that a random execution in rule match with the prediction.
		"""
		killed, alived = self.count_class(used_tests)
		prd_result, prd_support, prd_confidence = False, 0, 0.0
		if killed >= alived:
			prd_result = True
		prd_support = max(killed, alived)
		if prd_support > 0:
			prd_confidence = prd_support / (killed + alived)
		return prd_result, prd_support, prd_confidence

	def lsc_measure(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase (or tid) to decide of which executions in rule are killed.
		:return: 	length, support, confidence
					(1) length: the length of the killable prediction rule i.e. the number of its features;
					(2) support: the number of executions in the rule that remain undetected by used tests;
					(3) confidence: the likelihood of the executions in rule that cannot be killed by test;
		"""
		killed, alived = self.count_class(used_tests)
		length, support, confidence = len(self), alived, 0.0
		if support > 0:
			confidence = support / (killed + alived)
		return length, support, confidence

	def cir_measure(self, used_tests):
		"""
		:param used_tests: the set of MerTestCase (or tid) to decide of which executions in rule are killed.
		:return: 	consistent, inconsistent, ratio
					(1) consistent: the number of executions that produce consistent result in the used tests;
					(2) inconsistent: the number of executions producing inconsistent results for input tests;
					(3) ratio:			the probability of the tests that produce identical results in executions;
		"""
		consistent, inconsistent, ratio = 0, 0, 0.0
		if used_tests is None:
			used_tests = self.document.test_space.get_test_cases()
		for test in used_tests:
			if self.is_consistent(test):
				consistent += 1
			else:
				inconsistent += 1
		if consistent == 0:
			ratio = consistent / (consistent + inconsistent)
		return consistent, inconsistent, ratio


class KillPredictionTree:
	"""
	It denotes the hierarchical structure to create unique killable prediction rules based on feature vector.
	"""

	def __init__(self, document: jecode.MerDocument):
		"""
		:param document: the document of encoding features for mining
		"""
		self.document = document
		self.root = KillPredictionNode(self, None, -1)
		return

	def get_document(self):
		"""
		:return: the document of encoding features for mining
		"""
		return self.document

	def get_root(self):
		return self.root

	def get_node(self, features):
		"""
		:param features:
		:return:
		"""
		features = self.document.anot_space.normal(features)
		node = self.root
		for feature in features:
			node = node.new_child(feature)
		return node

	def get_nodes(self):
		"""
		:return: the set of all the nodes created under the tree
		"""
		queue, nodes = deque(), set()
		queue.append(self.root)
		while len(queue) > 0:
			node = queue.popleft()
			node: KillPredictionNode
			nodes.add(node)
			for child in node.get_children():
				queue.append(child)
		return nodes


class KillPredictionNode:
	"""
	It presents a node in KillPredictionTree w.r.t. a unique rule in definition.
	"""

	def __init__(self, tree: KillPredictionTree, parent, feature: int):
		"""
		:param tree: 	the tree where this node is created
		:param parent: 	the parent of this node or None when it is a root
		:param feature: the integer feature annotated on edge from parent
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.feature = -1
		else:
			self.parent = parent
			self.feature = feature
		self.children = list()
		self.rule = self.__rule__()
		return

	def __rule__(self):
		"""
		:return: it constructs a new prediction rule specified by this node uniquely
		"""
		features = set()
		node = self
		while not node.is_root():
			features.add(node.get_feature())
			node = node.get_parent()
		if node.is_root():
			executions = self.get_tree().get_document().exec_space.get_executions()
		else:
			executions = node.get_parent().get_rule().get_executions()
		return KillPredictionRule(self.get_tree().get_document(), features, executions)

	def get_tree(self):
		"""
		:return: the tree where this node is created
		"""
		return self.tree

	def get_parent(self):
		"""
		:return: the parent of this node or None when it is a root
		"""
		if self.parent is None:
			return None
		self.parent: KillPredictionNode
		return self.parent

	def get_children(self):
		"""
		:return: the child nodes created under this node
		"""
		return self.children

	def is_root(self):
		"""
		:return: whether the node is a root without parent
		"""
		if self.parent is None:
			return True
		return False

	def is_leaf(self):
		"""
		:return: whether the node is a leaf without children
		"""
		return len(self.children) == 0

	def get_feature(self):
		"""
		:return: the integer feature annotated on edge from parent
		"""
		return self.feature

	def get_rule(self):
		"""
		:return: the prediction rule that this node represents
		"""
		return self.rule

	def new_child(self, feature: int):
		"""
		:param feature:
		:return: it creates a new child w.r.t the input feature on edge from this node to its child:
					(1) if feature <= self.feature: return this node itself;
					(2) otherwise, return the child w.r.t the input feature.
		"""
		if feature <= self.feature:
			return self
		else:
			for child in self.children:
				child: KillPredictionNode
				if child.get_feature() == feature:
					return child
			child = KillPredictionNode(self.tree, self, feature)
			self.children.append(child)
			return child


class KillPredictionInputs:
	"""
	The inputs module denotes the parameters and document used for driving mining algorithms.
	"""

	def __init__(self, document: jecode.MerDocument, max_length: int,
				 min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: 		the document of encoded features from which the rules are mined and defined
		:param max_length: 		the maximal length of the generated rules
		:param min_support: 	the minimal support that the generated rules should achieve
		:param min_confidence: 	the minimal confidence that the generated rules should achieve
		:param max_confidence: 	the maximal confidence to terminate the procedure of the mining
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
		return KillPredictionMiddle(self)


class KillPredictionMiddle:
	"""
	The middle module maintains a prediction tree for generating and evaluating the prediction rules.
	"""

	def __init__(self, inputs: KillPredictionInputs):
		self.inputs = inputs
		self.p_tree = KillPredictionTree(self.inputs.get_document())
		return

	## getters

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_root(self):
		return self.p_tree.get_root()

	def get_child(self, parent: KillPredictionNode, feature: int):
		features = set()
		features.add(feature)
		while not parent.is_root():
			features.add(parent.get_feature())
			parent = parent.get_parent()
		return self.p_tree.get_node(features)

	def get_node(self, features):
		return self.p_tree.get_node(features)

	def get_nodes(self):
		return self.p_tree.get_nodes()

	def get_rules(self, rules):
		"""
		:param rules: 	the set of KillPredictionRule or KillPredictionNode or None to select all
		:return: 		the set of KillPredictionRule extracted from the input rules
		"""
		output_rules = set()
		if rules is None:
			for node in self.get_nodes():
				output_rules.add(node.get_rule())
		else:
			for rule in rules:
				if isinstance(rule, KillPredictionNode):
					output_rules.add(rule.get_rule())
				else:
					rule: KillPredictionRule
					output_rules.add(rule)
		return output_rules

	## evaluate

	def rsc_evaluate(self, rules, used_tests):
		"""
		:param rules: 		the set of killable prediction rules to be evaluated
		:param used_tests: 	the set of MerTestCase or tid or None to evaluate on
		:return: 			mapping from inputs to [result, support, confidence]
		"""
		evaluations = dict()
		input_rules = self.get_rules(rules)
		for rule in input_rules:
			result, support, confidence = rule.rsc_measure(used_tests)
			evaluations[rule] = (result, support, confidence)
		return evaluations

	def lsc_evaluate(self, rules, used_tests):
		"""
		:param rules: 		the set of killable prediction rules to be evaluated
		:param used_tests: 	the set of MerTestCase or tid or None to evaluate on
		:return: 			mapping from inputs to [length, support, confidence]
		"""
		evaluations = dict()
		input_rules = self.get_rules(rules)
		for rule in input_rules:
			length, support, confidence = rule.lsc_measure(used_tests)
			evaluations[rule] = (length, support, confidence)
		return evaluations

	def cir_evaluate(self, rules, used_tests):
		"""
		:param rules: 		the set of killable prediction rules to be evaluated
		:param used_tests: 	the set of MerTestCase or tid or None to evaluate on
		:return: 			mapping from inputs to [consist, inconsistent, ratio]
		"""
		evaluations = dict()
		input_rules = self.get_rules(rules)
		for rule in input_rules:
			consistent, inconsistent, ratio = rule.cir_measure(used_tests)
			evaluations[rule] = (consistent, inconsistent, ratio)
		return evaluations

	def prf_evaluate(self, rules, used_tests):
		"""
		:param rules:
		:param used_tests:
		:return:	orig_number, pred_number, matc_number, precision, recall, f1_score
					(1) orig_number: 	the number of executions that fail to be killed by used test;
					(2) pred_number: 	the number of executions that are matched by the input rules;
					(3) matc_number: 	the number of executions that match with input rules and not killed;
					(4) precision:		matc_number / pred_number * 100%
					(5) recall:			matc_number / orig_number * 100%
					(6) f1_score:		2 * precision * recall / (precision + recall)
		"""
		## 1. collect undetected executions and matched ones
		orig_executions, pred_executions = set(), set()
		for execution in self.get_document().exec_space.get_executions():
			if not execution.get_mutant().is_killed_in(used_tests):
				orig_executions.add(execution)
		input_rules = self.get_rules(rules)
		for rule in input_rules:
			for execution in rule.get_executions():
				pred_executions.add(execution)
		matc_executions = orig_executions & pred_executions

		## 2. counting and perform evaluation
		orig_number = len(orig_executions)
		pred_number = len(pred_executions)
		matc_number = len(matc_executions)
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if matc_number > 0:
			precision = matc_number / pred_number
			recall = matc_number / orig_number
			f1_score = 2 * precision * recall / (precision + recall)
		return orig_number, pred_number, matc_number, precision, recall, f1_score

	## selection

	def selects_rules(self, rules, key):
		"""
		:param rules: 	the set of Killable Prediction Rules from which the rules are selected or None for all
		:param key: 	the MerMutant or MerExecution or None to select all the input rules
		:return: 		the set of killable prediction rules selected from the input rules to contain the keys
		"""
		input_rules = self.get_rules(rules)
		output_rules = set()
		for rule in input_rules:
			if rule.has_sample(key):
				output_rules.add(rule)
		return output_rules

	def filters_rules(self, rules, used_tests):
		"""
		:param rules: 		the set of Killable Prediction Rules from which the rules are selected or None for all
		:param used_tests: 	the set of MerTestCase (or integer IDs), or None to evaluate killable prediction rules
		:return: 			the set of killable prediction rules filtered from input based on the input parameters
		"""
		evaluation_dict = self.lsc_evaluate(rules, used_tests)
		output_rules, inputs = set(), self.inputs
		for rule, evaluation in evaluation_dict.items():
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			if (length <= inputs.get_max_length()) \
					and (support >= inputs.get_min_support()) \
					and (confidence >= inputs.get_min_confidence()):
				output_rules.add(rule)
		return output_rules

	def sorting_rules(self, rules, used_tests):
		"""
		:param rules: 		the set of Killable Prediction Rules from which the rules are selected or None for all
		:param used_tests: 	the set of MerTestCase (or integer IDs), or None to evaluate killable prediction rules
		:return:			the sorted list of killable prediction rules selected from the input rules using tests
		"""
		## 1. evaluate the input rules
		evaluation_dict = self.lsc_evaluate(rules, used_tests)

		## 2. sort the rules by confidence
		confidence_list, confidence_dict = list(), dict()
		for rule, evaluation in evaluation_dict.items():
			confidence = evaluation[2]
			key = int(confidence * 1000000)
			if not (key in confidence_dict):
				confidence_dict[key] = set()
				confidence_list.append(key)
			confidence_dict[key].add(rule)
		confidence_list.sort(reverse=True)

		## 3. sort the rules by support and generate
		output_rules = list()
		for confidence in confidence_list:
			confidence_rules = confidence_dict[confidence]
			support_dict, support_list = dict(), list()
			for rule in confidence_rules:
				evaluation = evaluation_dict[rule]
				support = evaluation[1]
				if not (support in support_dict):
					support_dict[support] = set()
					support_list.append(support)
				support_dict[support].add(rule)
			support_list.sort(reverse=True)
			for support in support_list:
				for rule in support_dict[support]:
					rule: KillPredictionRule
					output_rules.append(rule)
		return output_rules

	def minimal_rules(self, rules, keys):
		"""
		:param rules: 	the set of KillPredictionRule or KillPredictionNode for minimization
		:param keys: 	the set of target samples (MerMutant or MerExecution) that needs to be covered
		:return: 		minimal_rules, uncovered_keys
						(1) the minimal set of killable prediction rules selected from input to cover
						(2) the set of key in the input target_keys that fail to be covered by output
		"""
		## 1. initialize the rules and covered_keys
		input_rules = self.get_rules(rules)
		covered_keys = set()
		if keys is None:
			for rule in input_rules:
				for execution in rule.get_executions():
					covered_keys.add(execution)
		else:
			for key in keys:
				if isinstance(key, jecode.MerExecution):
					key: jecode.MerExecution
					covered_keys.add(key)
				else:
					key: jecode.MerMutant
					for execution in self.get_document().exec_space.get_executions_of(key):
						execution: jecode.MerExecution
						covered_keys.add(execution)

		## 2. compute the minimal rules and uncovered
		minimal_rules = set()
		while (len(input_rules) > 0) and (len(covered_keys) > 0):
			## 2-1. randomly select a killable prediction rule
			random_rule = jcbase.rand_select(input_rules)
			input_rules.remove(random_rule)
			random_rule: KillPredictionRule

			## 2-2. when selected rule matches with any keys
			matched_keys = covered_keys & random_rule.get_executions()
			if len(matched_keys):
				for key in matched_keys:
					covered_keys.remove(key)
				minimal_rules.add(random_rule)

		## 3. return minimal rules and uncovered keys
		return minimal_rules, covered_keys

