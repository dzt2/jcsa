"""This file implements killable prediction rule using encoded features from encode.py"""


import os
import com.jcsa.mine.encode as jcenco


class EncPattern:
	"""
	The pattern records a set of symbolic conditions (encoded) in the project
	"""

	# constructor

	def __init__(self, document: jcenco.EncDocument):
		self.document = document
		self.features = list()		# sequence of integers (sorted)
		self.executions = set()		# the set of executions matching with the pattern
		self.mutants = set()		# the set of mutants matching with this pattern
		return

	# data interfaces

	def get_document(self):
		return self.document

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		return self.mutants

	def __matching__(self, execution: jcenco.EncExecution):
		"""
		:param execution:
		:return: whether the execution matches with the pattern
		"""
		for feature in self.features:
			condition = self.document.conditions.get_condition(feature)
			if not (condition in execution.get_conditions()):
				return False
		return True

	def set_executions(self, parent):
		"""
		:param parent: parent pattern or None
		:return:
		"""
		if parent is None:
			executions = self.document.exec_space.get_executions()
		else:
			executions = parent.executions
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcenco.EncExecution
			if self.__matching__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	# feature interfaces

	def get_features(self):
		"""
		:return: sequence of integers encoding conditions
		"""
		return self.features

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def get_conditions(self):
		conditions = list()
		for feature in self.features:
			condition = self.document.conditions.get_condition(feature)
			conditions.append(condition)
		return conditions

	# relationship

	def new_child(self, feature: int):
		"""
		:param feature:
		:return: child pattern extended from this one or itself
		"""
		if feature >= 0 and not (feature in self.features):
			child = EncPattern(self.document)
			for old_feature in self.features:
				child.features.append(old_feature)
			child.features.append(feature)
			child.features.sort()
			return child
		else:
			return self

	def subsume(self, pattern, strict=False):
		"""
		:param pattern:
		:param strict:
		:return:
		"""
		pattern: EncPattern
		for execution in self.executions:
			if not (execution in pattern.executions):
				return False
		if strict:
			return len(self.executions) > len(pattern.executions)
		else:
			return True

	def estimate(self, used_tests):
		"""
		:param used_tests: the collection of test cases (ID) to kill a mutant
		:return: length, support, confidence
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			execution: jcenco.EncExecution
			if execution.get_mutant().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		total = killed + alive
		support = alive
		if total > 0:
			confidence = support / total
		else:
			confidence = 0.0
		return len(self.features), support, confidence


class EncPatterns:
	"""
	It provides unique interfaces to generate EncPattern.
	"""

	def __init__(self, document: jcenco.EncDocument):
		self.document = document
		self.patterns = dict()	# String --> EncPattern
		return

	def __unique_pattern__(self, pattern: EncPattern, parent):
		"""
		:param pattern:
		:param parent: whether to generate the pattern under a parent
		:return: unique instance of the pattern
		"""
		if not (str(pattern) in self.patterns):
			self.patterns[str(pattern)] = pattern
			pattern.set_executions(parent)
		pattern = self.patterns[str(pattern)]
		pattern: EncPattern
		return pattern

	def get_root(self):
		return self.__unique_pattern__(EncPattern(self.document), None)

	def get_child(self, parent: EncPattern, feature: int):
		return self.__unique_pattern__(parent.new_child(feature), self.__unique_pattern__(parent))

	def get_pattern_of(self, features):
		"""
		:param features:
		:return: construct a pattern using set of integer features directly
		"""
		pattern = self.get_root()
		for feature in features:
			feature: int
			pattern = self.get_child(pattern, feature)
		return pattern

	def get_patterns(self):
		return self.patterns.values()

	def estimate(self, pattern: EncPattern, used_tests):
		return self.__unique_pattern__(pattern, None).estimate(used_tests)

	def clear(self):
		"""
		clear the patterns generated in this space
		:return:
		"""
		self.patterns.clear()
		return

	def select_good_patterns(self, max_length: int, min_support: int, min_confidence: float, used_tests, patterns=None):
		"""
		:param max_length:
		:param min_support:
		:param min_confidence:
		:param used_tests:
		:param patterns: None if selecting in all the generated patterns
		:return:
		"""
		good_patterns = set()
		if patterns is None:
			patterns = self.patterns.values()
		for pattern in patterns:
			pattern: EncPattern
			length, support, confidence = self.estimate(pattern, used_tests)
			if length <= max_length and support >= min_support and confidence >= min_confidence:
				good_patterns.add(pattern)
		return good_patterns










