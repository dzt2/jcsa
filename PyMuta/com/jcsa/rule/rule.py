"""This file defines the killable prediction rule and the mining algorithms to implement."""



from collections import deque
from typing import TextIO
from sklearn import metrics
import sklearn.tree as sktree
import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.test as jctest
import com.jcsa.rule.merd as jecode
import pydotplus
import graphviz


class KillPredictionTree:
	"""
	The hierarchical model to uniquely define the killable prediction rule.
	"""

	def __init__(self, document: jecode.MerDocument):
		"""
		:param document: the encoding document where the prediction rule is defined on
		"""
		self.document = document
		self.root = KillPredictionNode(self, None, -1)
		return

	def get_document(self):
		"""
		:return: the document of dataset to encode mutation test project dataset.
		"""
		return self.document

	def get_root(self):
		return self.root

	def get_node(self, features):
		feature_list = self.document.anot_space.normal(features)
		node = self.root
		for feature in feature_list:
			node = node.new_child(feature)
		return node

	def get_child(self, parent, feature: int):
		"""
		:param parent:
		:param feature:
		:return:
		"""
		if parent is None:
			parent = self.root
		features = set()
		while not parent.is_root():
			features.add(parent.get_feature())
			parent = parent.get_parent()
		features.add(feature)
		return self.get_node(self.document.anot_space.normal(features))


class KillPredictionNode:
	"""
	It denotes a node in KillPredictionTree with respect to a unique KillPredictionRule.
	"""

	def __init__(self, tree: KillPredictionTree, parent, feature: int):
		"""
		:param tree: 	the tree where the node is created
		:param parent: 	the parent of this node or None for root
		:param feature: the feature annotated on the edge from parent to its child
		"""
		self.tree = tree
		if parent is None:
			self.parent = None
			self.feature = -1
		else:
			parent: KillPredictionNode
			self.parent = parent
			self.feature = feature
		self.children = list()
		self.rule = KillPredictionRule(self)
		return

	def get_tree(self):
		"""
		:return: the tree where the node is created
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether this node is a root
		"""
		return self.parent is None

	def get_parent(self):
		"""
		:return: the parent node of this one or None when it is a root
		"""
		if self.parent is None:
			return None
		self.parent: KillPredictionNode
		return self.parent

	def get_feature(self):
		"""
		:return: the feature annotated on the edge from parent to its child
		"""
		return self.feature

	def is_leaf(self):
		"""
		:return: whether the node is a leaf without any child
		"""
		return len(self.children) == 0

	def get_children(self):
		"""
		:return: the child nodes created under this node
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

	def get_rule(self):
		"""
		:return: the killable prediction rule that the node specifies
		"""
		return self.rule

	def new_child(self, feature: int):
		"""
		:param feature:
		:return: It creates a new child under this node using the edge feature specified.
					(1) if feature <= self.feature: return the node itself;
					(2) otherwise, create the existing child under this node.
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


class KillPredictionRule:
	"""
	The killable prediction rule
	"""

	def __init__(self, node: KillPredictionNode):
		"""
		:param node: the tree node uniquely defining this rule
		"""
		self.document = node.get_tree().get_document()
		self.__new_features__(node)
		self.__new_executions__(node)
		return

	def __new_features__(self, node: KillPredictionNode):
		"""
		:param node:
		:return: list of integer features encoding the rule
		"""
		features = set()
		while not node.is_root():
			features.add(node.get_feature())
			node = node.get_parent()
		self.features = self.document.anot_space.normal(features)
		return

	def __matched_with__(self, execution: jecode.MerExecution):
		"""
		:param execution:
		:return: whether the execution matches with this rule
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __new_executions__(self, node: KillPredictionNode):
		"""
		:param node:
		:return:
		"""
		if node.is_root():
			parent_executions = self.document.exec_space.get_executions()
		else:
			parent_executions = node.get_parent().get_rule().executions
		executions = set()
		for execution in parent_executions:
			execution: jecode.MerExecution
			if self.__matched_with__(execution):
				executions.add(execution)
		self.executions = executions
		return

	## features

	def get_features(self):
		"""
		:return: the list of integer features encoding the rule
		"""
		return self.features

	def get_annotations(self):
		"""
		:return: the set of annotations encoded by this rule
		"""
		return self.document.anot_space.decode(self.features)

	def __len__(self):
		"""
		:return: the number of features in the node
		"""
		return len(self.features)

	def __str__(self):
		return str(self.features)

	## sampling

	def get_executions(self):
		return self.executions

	def get_mutants(self):
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












