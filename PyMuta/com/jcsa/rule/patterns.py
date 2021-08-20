""" This file defines the data model of killable-prediction rule and interfaces for evaluation them. """


import os
import com.jcsa.rule.encode as jcenco


## data model definition


class KillPredictionTree:
	"""
	It manages the generation of (unique) prediction rules in a tree structure.
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the document provides (encoded) features as data source
		"""
		self.m_document = m_document
		return

	def __done__(self):
		return self

	def get_m_document(self):
		"""
		:return: the document that provides the (encoded) features as data sources
		"""
		return self.m_document

	def get_u_features(self, features):
		"""
		:param features: the set of integer features encoding the conditions in the mutant execution
		:return: the unique sorted sequence of valid integer features captured from input features
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
		:param conditions: the set of MerCondition incorporated and encoded
		:return: the unique sorted of integer features encoding the inputs
		"""
		features = set()
		for condition in conditions:
			condition: jcenco.MerCondition
			features.add(condition.get_cid())
		return self.get_u_features(features)

	def decode(self, features):
		"""
		:param features: the set of integer features encoding the conditions in mutant executions
		:return: the set of MerCondition(s) encoded by the input features
		"""
		conditions = list()
		features = self.get_u_features(features)
		cond_space = self.m_document.cond_space
		for feature in features:
			conditions.append(cond_space.get_condition(feature))
		return conditions


class KillPredictionNode:
	"""
	It models a node in KillPredictionTree, which refers to a unique prediction rule
	"""

	def __init__(self, tree: KillPredictionTree, parent, edge_feature: int):
		"""
		:param tree: 	the KillPredictionTree where the tree node is created
		:param parent: 	the parent of this node or None if the node is a root
		:param edge_feature: the feature annotated on edge from parent to the node
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.feature = -1
		else:
			self.parent = parent
			self.feature = edge_feature
		self.children = list()
		self.rule = KillPredictionRule(self)
		return

	## tree getters

	def get_tree(self):
		"""
		:return: the tree where the node of killable prediction rule is defined uniquely.
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether the node is a root without any parent
		"""
		if self.parent is None:
			return True
		else:
			return False

	def is_leaf(self):
		"""
		:return: whether the node is a leaf without any child
		"""
		return len(self.children) == 0

	def get_parent(self):
		"""
		:return: the parent of this node or None if the node is a root
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
		:return: the number of children created under this node
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

	## data getters

	def get_feature(self):
		"""
		:return: the feature annotated on the edge from its parent to this node
		"""
		return self.feature

	def get_rule(self):
		"""
		:return: the killable prediction rule defined by this node
		"""
		return self.rule


class KillPredictionRule:
	"""
	The killable prediction rule uniquely defined in a tree model.
	"""

	## constructor

	def __init__(self, node: KillPredictionNode):
		"""
		:param node: the tree node where the rule is uniquely defined
		"""
		self.node = node
		self.features = self.__init_features__()
		self.executions = self.__init_executions__()
		return

	def __init_features__(self):
		"""
		:return: the unique features encoded by this rule
		"""
		features = set()
		node = self.node
		while not node.is_root():
			features.add(node.get_feature())
			node = node.get_parent()
		return self.node.get_tree().get_u_features(features)

	def __is_matched_with__(self, execution: jcenco.MerExecution):
		"""
		:param execution:
		:return: whether the execution matches with this rule's premises
		"""
		for feature in execution.get_features():
			if not (feature in self.features):
				return False
		return True

	def __init_executions__(self):
		"""
		:return: the set of symbolic executions matching with this rule
		"""
		if self.node.is_root():
			parent_executions = self.node.get_tree().get_m_document().exec_space.get_executions()
		else:
			parent_executions = self.node.get_parent().get_rule().get_executions()
		child_executions = set()
		for execution in parent_executions:
			execution: jcenco.MerExecution
			if self.__is_matched_with__(execution):
				child_executions.add(execution)
		return child_executions

	## feature getters

	def get_features(self):
		return self.features

	def get_conditions(self):
		return self.node.get_tree().decode(self.features)

	def __str__(self):
		return str(self.features)

	def __len__(self):
		return len(self.features)

	## data getters

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def predicts(self, used_tests):
		"""
		:param used_tests: the set of (int|TestCase) test cases to determine the rule's prediction
		:return: result, killed, alive, confidence
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			if execution.get_mutant().get_result().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		total = killed + alive
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
		:param used_tests:
		:return: length, support, confidence, result
		"""
		result, killed, alive, confidence = self.predicts(used_tests)
		support = alive
		if support > 0:
			confidence = support / (killed + alive)
		else:
			confidence = 0.0
		return len(self), support, confidence, result















