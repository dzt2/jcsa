""" It defines the killable prediction rule and algorithms to extract them from source code. """


import os
from collections import deque
from typing import TextIO
import com.jcsa.libs.base 	as jcbase
import com.jcsa.libs.test 	as jctest
import com.jcsa.mine.encode as jcenco


## killable prediction rule model


class KillPredictionTree:
	"""
	It manages the generation of unique killable prediction rules based on tree structure
	"""

	def __init__(self, m_document: jcenco.MerDocument):
		"""
		:param m_document: the memory-reduced document to provide encoded features
		"""
		self.m_document = m_document
		self.root = KillPredictionNode(self, None, -1)
		return

	def get_m_document(self):
		"""
		:return: the memory-reduced document to provide encoded features
		"""
		return self.m_document

	@staticmethod
	def get_unique_features(features):
		"""
		:param features: the collection of integers encoding symbolic conditions in mutation testing
		:return: the unique (sorted) sequence representing the conditions included with the features
		"""
		feature_list = list()
		for feature in features:
			feature: int
			if feature < 0:
				continue
			elif feature in feature_list:
				continue
			else:
				feature_list.append(feature)
		feature_list.sort()
		return feature_list

	def decode(self, features):
		"""
		:param features: the set of integers encoding the symbolic conditions in the mutant execution
		:return: the set of symbolic conditions encoded by the features within the specified document
		"""
		conditions = set()
		features = KillPredictionTree.get_unique_features(features)
		for feature in features:
			condition = self.m_document.cond_space.get_condition(feature)
			conditions.add(condition)
		return conditions

	def get_root(self):
		"""
		:return: the root node w.r.t. rule without any premises
		"""
		return self.root

	def get_node(self, features):
		"""
		:param features: the set of integer features w.r.t. the tree node in prediction space
		:return: unique tree node w.r.t. killable prediction rule encoded by it
		"""
		features = KillPredictionTree.get_unique_features(features)
		tree_node = self.root
		for feature in features:
			tree_node = tree_node.extend_child(feature)
		return tree_node


class KillPredictionNode:
	"""
	It denotes a node in KillPredictionTree w.r.t. a unique killable prediction rule in the space
	"""

	def __init__(self, tree: KillPredictionTree, parent, edge_feature: int):
		"""
		:param tree: 			the killable prediction rule tree where this node is uniquely defined
		:param parent: 			the parent node of this node, or None to represent the node is a root
		:param edge_feature: 	the integer feature annotated on the edge from its parent to the node
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
		:return: the killable prediction rule tree where this node is uniquely defined
		"""
		return self.tree

	def is_root(self):
		"""
		:return: whether this tree node is a root without any parent
		"""
		if self.parent is None:
			return True
		else:
			return False

	def get_parent(self):
		"""
		:return: the parent node of this node, or None to represent the node is a root
		"""
		if self.parent is None:
			return None
		else:
			self.parent: KillPredictionNode
			return self.parent

	def is_leaf(self):
		"""
		:return: whether this node is a leaf without any more child
		"""
		return len(self.children) == 0

	def get_children(self):
		"""
		:return: the set of child nodes created under this node
		"""
		return self.children

	def get_rule(self):
		"""
		:return: the killable prediction rule that the node represents in the tree uniquely
		"""
		return self.rule

	def extend_child(self, edge_feature: int):
		"""
		:param edge_feature: used to create the child w.r.t.
		:return: It generates the new child using the following rules:
					1) 	when edge_feature <= self.edge_feature, it returns the node itself;
					2)	when edge_feature > self.edge_feature and there exists some child under the node
						and child's edge_feature equals with the input, then returns the existing child;
					3)	otherwise, create a new child under the node and update its children set anyway.
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
	The unique defined killable prediction rule in the tree structural model.
	"""

	def __init__(self, node: KillPredictionNode):
		"""
		:param node: the tree node where the rule is created
		"""
		self.node = node
		self.features = self.__generate_features__()
		self.executions = self.__generate_executions__()
		return

	def __generate_features__(self):
		"""
		:return: the sorted list of integer features encoding conditions in the premises
		"""
		tree_node = self.node
		features = set()
		while not tree_node.is_root():
			features.add(tree_node.edge_feature)
			tree_node = tree_node.get_parent()
		return KillPredictionTree.get_unique_features(features)

	def __match_with_execution__(self, execution: jcenco.MerExecution):
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
		:return: the set of executions match with this rule
		"""
		if self.node.is_root():
			parent_executions = self.node.get_tree().get_m_document().exec_space.get_executions()
		else:
			parent_executions = self.node.get_parent().get_rule().executions
		executions = set()
		for execution in parent_executions:
			execution: jcenco.MerExecution
			if self.__match_with_execution__(execution):
				executions.add(execution)
		return executions

	def get_node(self):
		"""
		:return: the tree node where the prediction rule is uniquely defined
		"""
		return self.node

	def get_features(self):
		"""
		:return: the unique list of integer features encoding conditions in the rule
		"""
		return self.features

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions incorporated in the prediction rule
		"""
		return self.node.get_tree().decode(self.features)

	def __str__(self):
		return str(self.features)

	def __len__(self):
		return len(self.features)

	def get_executions(self):
		"""
		:return: the set of symbolic executions matching with this pattern in tree
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutants of which execution(s) match with this rule
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants


## killable prediction rule mining











