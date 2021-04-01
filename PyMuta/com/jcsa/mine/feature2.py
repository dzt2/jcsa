"""This file defines the data model of inputs information to mining algorithms."""


import os
from typing import TextIO

import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


# the definition of class label w.r.t. each mutation based on detection process in RIP framework

UR_CLASS = "UR"					# the test suite failed to execute mutation.
UI_CLASS = "UI"					# the test suite reached but fail to infect.
UP_CLASS = "UP"					# the test suite infects but failed to kill.
KI_CLASS = "KI"					# the test suite killed the target mutation.


# the definition of sample categories for choosing right samples for evaluating produced patterns

MU_SAMPLE_CLASS = None			# select Mutant as the data samples for evaluation
SQ_SAMPLE_CLASS = False			# select SymSequence as data samples in evaluation
EX_SAMPLE_CLASS = True			# select SymExecution as the samples in evaluation


# the collection of strategy methods for determining whether a sample of particular class is counted and supported.

def unk_support_strategy(label: str):
	"""
	Determine the including and supporting counter on samples w.r.t. particular class
		(1) UR or UI or UP: include = True and support = True
		(2) KI:				include = True and support = False

	:param label: UR or UI or UP or KI
	:return: include, support
				1. include: whether the sample of given label is counted as total
				2. support: whether the sample of given label is counted as support
	"""
	if label == UR_CLASS or label == UI_CLASS or label == UP_CLASS:
		return True, True
	else:
		return True, False


def wcc_support_strategy(label: str):
	"""
	Determine the include and support counter on samples w.r.t. particular class
		(1) UR:			include = False and support = False
		(2) UI or UP: 	include = True and support = True
		(3) KI: 		include = True and support = False

	:param label: UR or UI or UP or KI
	:return: include, support
				1. include: whether the sample of given label is counted as total
				2. support: whether the sample of given label is counted as support
	"""
	if label == UR_CLASS:
		return False, False
	elif label == UI_CLASS or label == UP_CLASS:
		return True, True
	else:
		return True, False


def scc_support_strategy(label: str):
	"""
	Determine the include and support counter on samples w.r.t. particular class
		(1) UR or UI:	include = False and support = False
		(2) UP: 		include = True and support = True
		(3) KI: 		include = True and support = False

	:param label: UR or UI or UP or KI
	:return: include, support
				1. include: whether the sample of given label is counted as total
				2. support: whether the sample of given label is counted as support
	"""
	if label == UR_CLASS or label == UI_CLASS:
		return False, False
	elif label == UP_CLASS:
		return True, True
	else:
		return True, False


def kid_support_strategy(label: str):
	"""
	Determine the include and support counter on samples w.r.t. particular class
		(1) UR or UI:	include = True and support = False
		(2) UP: 		include = True and support = False
		(3) KI: 		include = True and support = True

	:param label: UR or UI or UP or KI
	:return: include, support
				1. include: whether the sample of given label is counted as total
				2. support: whether the sample of given label is counted as support
	"""
	if label == KI_CLASS:
		return True, True
	else:
		return True, False


# classification core module


class RIPClassifier:
	"""
	It implements the T-oriented mutant classifier for evaluating performance of generated patterns.
	"""

	def __init__(self, used_tests):
		"""
		:param used_tests:
				(1)	The collection of TestCase (or unique integer ID) being used to decide whether a mutant
					is reached, infected or detected.
				(2) None to represent the entire test cases defined in test space of project is applied for
		"""
		self.used_tests = used_tests
		self.solutions = dict()			# Mapping from Mutant.muta_id to its class label of {UR|UI|UP|KI}
		return

	def __find__(self, sample):
		"""
		:param sample: Mutant or SymSequence or SymExecution
		:return: class label of the mutant by {UR, UI, UP, KI}
		"""
		# 1. find the mutant w.r.t. the given sample
		if isinstance(sample, jctest.SymExecution):
			mutant = sample.get_sequence().get_mutant()
		elif isinstance(sample, jctest.SymSequence):
			mutant = sample.get_mutant()
		else:
			sample: jcmuta.Mutant
			mutant = sample

		# 2. solve the class label if first meeting
		if not(mutant.get_muta_id() in self.solutions):
			s_result = mutant.get_result()
			w_result = mutant.get_w_mutant().get_result()
			c_result = mutant.get_c_mutant().get_result()
			if s_result.is_killed_in(self.used_tests):
				label = KI_CLASS
			elif w_result.is_killed_in(self.used_tests):
				label = UP_CLASS
			elif c_result.is_killed_in(self.used_tests):
				label = UI_CLASS
			else:
				label = UR_CLASS
			self.solutions[mutant.get_muta_id()] = label

		# 3. return the class label from solution space
		label = self.solutions[mutant.get_muta_id()]
		label: str
		return label

	def classify(self, samples):
		"""
		:param samples: collection of Mutant, SymSequence or SymExecution
		:return: Mapping from labels of {UR, UI, UP, KI} to corresponding samples
		"""
		class_dict = dict()
		class_dict[UR_CLASS] = set()
		class_dict[UI_CLASS] = set()
		class_dict[UP_CLASS] = set()
		class_dict[KI_CLASS] = set()
		for sample in samples:
			label = self.__find__(sample)
			class_dict[label].add(sample)
		return class_dict

	def counting(self, samples):
		"""
		:param samples: collection of Mutant, SymSequence or SymExecution
		:return: 	ur, ui, up, ki, uk, cc
					1. ur: the number of samples that failed to be reached by used_tests.
					2. ui: the number of samples that are reached but failed to infected by used_tests.
					3. up: the number of samples that are infected but fail to be killed by used_tests.
					4. ki: the number of samples that are detected by used_tests
					5. uk: the number of samples that failed to be killed by used_tests.
					6. cc: the number of samples that failed to be killed but reached by used_tests.
		"""
		ur, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			label = self.__find__(sample)
			if label == KI_CLASS:
				ki += 1
			elif label == UP_CLASS:
				up += 1
			elif label == UI_CLASS:
				ui += 1
			else:
				ur += 1
		return ur, ui, up, ki, ur + ui + up, ui + up

	def estimate(self, samples, strategy):
		"""
		:param samples: collection of Mutant, SymSequence or SymExecution
		:param strategy: strategy function {UR|UI|UP|KI} ==> {include, support}
		:return: total, support, confidence
		"""
		total, support, confidence = 0, 0, 0.0
		for sample in samples:
			label = self.__find__(sample)
			include_tag, support_tag = strategy(label)
			if include_tag:
				total += 1
				if support_tag:
					support += 1
		if support > 0:
			confidence = support / total
		return total, support, confidence

	def select(self, samples, strategy):
		"""
		:param samples: collection of Mutant, SymSequence or SymExecution
		:param strategy: strategy function {UR|UI|UP|KI} ==> {include, support}
		:return: the set of samples of which class label returns support = True
		"""
		selected_samples = set()
		for sample in samples:
			label = self.__find__(sample)
			include, support = strategy(label)
			if support:
				selected_samples.add(sample)
		return selected_samples


# data model to describe mutation patterns


class RIPPattern:
	"""
	It describes the structural presentation of mutation pattern in forms of symbolic conditions included.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		"""
		:param document:
		:param classifier:
		"""
		self.document = document		# It provides the entire dataset of original samples for being mined.
		self.classifier = classifier	# It is used to estimate the performance of the given mutant pattern.
		self.executions = set()			# The collection of SymExecution being matched by this pattern.
		self.sequences = set()			# The collection of SymSequence being matched by this pattern.
		self.mutants = set()			# The collection of Mutant of which sequences are matched by this.
		self.words = list()				# The sequence of words encoding conditions included by the pattern.
		return

	# data samples

	def get_document(self):
		"""
		:return: It provides the entire dataset of original samples for being mined.
		"""
		return self.document

	def get_executions(self):
		"""
		:return: The collection of SymExecution being matched by this pattern.
		"""
		return self.executions

	def get_sequences(self):
		"""
		:return: The collection of SymSequence being matched by this pattern.
		"""
		return self.sequences

	def get_mutants(self):
		"""
		:return: The collection of Mutant of which sequences are matched by this.
		"""
		return self.mutants

	def get_samples(self, sample_class):
		"""
		:param sample_class:
				1. None	--- Take self.mutants as samples
				2. True	---	Take self.executions as samples
				3. False---	Take self.sequences as samples
		:return:
		"""
		if sample_class is None:
			return self.mutants
		elif sample_class:
			return self.executions
		else:
			return self.sequences

	def __match_seq__(self, sequence: jctest.SymSequence):
		"""
		:param sequence:
		:return:
		"""
		for word in self.words:
			if not(word in sequence.get_words()):
				return False
		return True

	def __match_exe__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return:
		"""
		for word in self.words:
			if word in execution.get_words():
				return True
		return False

	def set_samples(self, parent):
		"""
		:param parent:
			1. the parent pattern from which this one is directly extended
			2. None to consider this pattern as the root from document
		:return:
		"""
		# 1. obtain the parent sequences for updating
		if parent is None:
			sequences = self.document.get_sequences()
		else:
			parent: RIPPattern
			sequences = parent.get_sequences()

		# 2. clear the original samples for matching
		self.mutants.clear()
		self.sequences.clear()
		self.executions.clear()

		# 3. matching process take times here...
		for sequence in sequences:
			sequence: jctest.SymSequence
			if self.__match_seq__(sequence):
				self.sequences.add(sequence)
				self.mutants.add(sequence.get_mutant())
				for execution in sequence.get_executions():
					execution: jctest.SymExecution
					if self.__match_exe__(execution):
						self.executions.add(execution)
		return

	# evaluations

	def get_classifier(self):
		"""
		:return: the classifier is used to estimate performance of this pattern
		"""
		return self.classifier

	def classify(self, sample_class):
		"""
		:param sample_class:
				1. None	--- Take self.mutants as samples
				2. True	---	Take self.executions as samples
				3. False---	Take self.sequences as samples
		:return: Mapping from labels of {UR, UI, UP, KI} to corresponding samples
		"""
		return self.classifier.classify(self.get_samples(sample_class))

	def counting(self, sample_class):
		"""
		:param sample_class:
				1. None	--- Take self.mutants as samples
				2. True	---	Take self.executions as samples
				3. False---	Take self.sequences as samples
		:return: 	ur, ui, up, ki, uk, cc
					1. ur: the number of samples that failed to be reached by used_tests.
					2. ui: the number of samples that are reached but failed to infected by used_tests.
					3. up: the number of samples that are infected but fail to be killed by used_tests.
					4. ki: the number of samples that are detected by used_tests
					5. uk: the number of samples that failed to be killed by used_tests.
					6. cc: the number of samples that failed to be killed but reached by used_tests.
		"""
		return self.classifier.counting(self.get_samples(sample_class))

	def estimate(self, sample_class, support_strategy):
		"""
		:param sample_class:
				1. None	--- Take self.mutants as samples
				2. True	---	Take self.executions as samples
				3. False---	Take self.sequences as samples
		:param support_strategy: str ==> {bool, bool}
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(sample_class), support_strategy)

	def select(self, sample_class, support_strategy):
		"""
		:param sample_class:
				1. None	--- Take self.mutants as samples
				2. True	---	Take self.executions as samples
				3. False---	Take self.sequences as samples
		:param support_strategy: str ==> {bool, bool}
		:return: the set of samples w.r.t. supporting class
		"""
		return self.classifier.select(self.get_samples(sample_class), support_strategy)

	# feature model

	def get_words(self):
		"""
		:return: The sequence of words encoding conditions included by the pattern.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: The sequence of conditions included by the pattern.
		"""
		return self.document.get_conditions_lib().get_conditions(self.words)

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	# relationships

	def extends(self, word: str):
		"""
		:param word: the word added to this pattern for generating a child pattern
		:return: child pattern extended from this one by adding one new word in
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = RIPPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		else:
			return self

	def subsume(self, pattern, sample_class, strict):
		"""
		:param pattern:
		:param sample_class:
				1. None	--- Take self.mutants as samples
				2. True	---	Take self.executions as samples
				3. False---	Take self.sequences as samples
		:param strict: whether to return True for strict subsumption
		:return:
		"""
		pattern: RIPPattern
		source_samples = self.get_samples(sample_class)
		target_samples = pattern.get_samples(sample_class)
		for sample in target_samples:
			if not(sample in source_samples):
				return False
		if strict:
			return len(source_samples) > len(target_samples)
		else:
			return True


# data model for managing inputs dataset


class RIPMineInputs:
	"""
	It preserves the parameters used for performing pattern mining.
	"""

	def __init__(self, document: jctest.CDocument, used_tests, sample_class, support_strategy,
				 max_length: int, min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: 			It provides the original data for generating patterns
		:param used_tests: 			The collection of test cases used for deciding whether a mutant is killed or not
		:param sample_class: 		None --- take Mutant as samples for evaluation;
									True --- take SymExecution as samples for evaluation;
									False--- take SymSequence as samples for evaluation.
		:param support_strategy: 	function from class label (str) to {bool, bool} to decide how estimation is done
		:param max_length: 			The maximal length of generated patterns
		:param min_support: 		The minimal support required for good patterns
		:param min_confidence: 		The minimal confidence needed for good pattern
		:param max_confidence: 		The maximal confidence to stop searching on space
		"""
		self.document = document
		self.classifier = RIPClassifier(used_tests)
		self.sample_class = sample_class
		self.support_strategy = support_strategy
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		return

	def get_document(self):
		return self.document

	def get_classifier(self):
		return self.classifier

	def get_sample_class(self):
		return self.sample_class

	def get_support_strategy(self):
		return self.support_strategy

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_max_length(self):
		return self.max_length


# middle module for managing produced patterns in mining procedure


class RIPMineMiddle:
	"""
	It preserve the middle results produced by mining algorithms
	"""

	def __init__(self, inputs: RIPMineInputs):
		self.inputs = inputs
		self.patterns = dict()  # String ==> RIPPattern
		self.solution = dict()  # RIPPattern ==> (length, support, confidence)
		return

	def get_document(self):
		return self.inputs.get_document()

	def get_classifier(self):
		return self.inputs.get_classifier()

	def get_inputs(self):
		return self.inputs

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: None to create root pattern
		:param word: newly added word for extension
		:return: child pattern extended from parent with one new word specified
		"""
		# 1. create a new child pattern from parent by adding one new word
		if parent is None:
			child = RIPPattern(self.get_document(), self.get_classifier())
			child = child.extends(word)
		else:
			parent: RIPPattern
			child = parent.extends(word)
		# 2. generate unique instance if not existence before
		if not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_samples(parent)
			self.estimate(child)
		# 3. obtain the child pattern from parent by adding one new word uniquely
		child = self.patterns[str(child)]
		child: RIPPattern
		return child

	def get_root(self, word: str):
		return self.__new_pattern__(None, word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)

	def estimate(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: length, support, confidence
		"""
		if not(pattern in self.solution):
			length = len(pattern)
			total, support, confidence = pattern.estimate(self.inputs.get_sample_class(),
														  self.inputs.get_support_strategy())
			self.solution[pattern] = (length, support, confidence)
		solution = self.solution[pattern]
		length = solution[0]
		support = solution[1]
		confidence = solution[2]
		length: int
		support: int
		confidence: float
		return length, support, confidence

	def extract_good_patterns(self):
		"""
		:return: The collection of "good" patterns generated in the middle and satisfy the corresponding metrics
		"""
		good_patterns = set()
		for pattern, solution in self.solution.items():
			pattern: RIPPattern
			length = solution[0]
			support = solution[1]
			confidence = solution[2]
			if length <= self.inputs.get_max_length() and \
					support >= self.inputs.get_min_support() and \
					confidence >= self.inputs.get_min_confidence():
				good_patterns.add(pattern)
		return good_patterns


# output module used to presentation


class RIPMineOutput:
	"""
	The output module of pattern mining.
	"""

	def __init__(self, middle: RIPMineMiddle):
		# root attributes
		self.inputs = middle.get_inputs()
		self.patterns = middle.extract_good_patterns()

		# document samples
		self.ori_executions = set()
		self.ori_sequences = set()
		self.ori_mutants = set()
		for sequence in self.inputs.get_document().get_sequences():
			sequence: jctest.SymSequence
			self.ori_sequences.add(sequence)
			self.ori_mutants.add(sequence.get_mutant())
			for execution in sequence.get_executions():
				execution: jctest.SymExecution
				self.ori_executions.add(execution)

		# pattern samples
		self.pat_executions = set()
		self.pat_sequences = set()
		self.pat_mutants = set()
		for pattern in self.patterns:
			for execution in pattern.get_executions():
				execution: jctest.SymExecution
				self.pat_executions.add(execution)
			for sequence in pattern.get_sequences():
				sequence: jctest.SymSequence
				self.pat_sequences.add(sequence)
			for mutant in pattern.get_mutants():
				mutant: jcmuta.Mutant
				self.pat_mutants.add(mutant)

		return

	def get_document(self):
		return self.inputs.get_document()

	def get_orig_executions(self):
		return self.ori_executions

	def get_orig_sequences(self):
		return self.ori_sequences

	def get_orig_mutants(self):
		return self.ori_mutants

	def get_orig_samples(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return:
		"""
		if sample_class is None:
			return self.ori_mutants
		elif sample_class:
			return self.ori_executions
		else:
			return self.ori_sequences

	def get_patterns(self):
		return self.patterns

	def get_patt_executions(self):
		return self.pat_executions

	def get_patt_sequences(self):
		return self.pat_sequences

	def get_patt_mutants(self):
		return self.pat_mutants

	def get_patt_samples(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return:
		"""
		if sample_class is None:
			return self.pat_mutants
		elif sample_class:
			return self.pat_executions
		else:
			return self.pat_sequences

	def get_classifier(self):
		return self.inputs.get_classifier()

	@staticmethod
	def select_subsuming_patterns(patterns, sample_class, strict: bool):
		"""
		:param sample_class: to take the samples for computing subsumption
		:param strict: whether to generate strictly subsuming set
		:param patterns: set of RIP-testability patterns
		:return: minimal set of patterns that subsume the others
		"""
		remain_patterns, remove_patterns, minimal_patterns = set(), set(), set()
		patterns = jcbase.rand_resort(patterns)
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			subsume_pattern = None
			remove_patterns.clear()
			for pattern in remain_patterns:
				if subsume_pattern is None:
					subsume_pattern = pattern
					remove_patterns.add(pattern)
				elif subsume_pattern.subsume(pattern, sample_class, strict):
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern, sample_class, strict):
					subsume_pattern = pattern
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
			if not (subsume_pattern is None):
				minimal_pattern = subsume_pattern
				minimal_pattern: RIPPattern
				minimal_patterns.add(minimal_pattern)
		return minimal_patterns

	@staticmethod
	def remap_keys_patterns(patterns, sample_class):
		"""
		:param patterns:
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return: Sample ==> set of RIPPattern
		"""
		results = dict()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(sample_class)
			for sample in samples:
				if not (sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def select_best_pattern(patterns, sample_class, support_class):
		"""
		:param patterns:
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:param support_class:
				1. True to select strong coincidental correct samples as support (UP)
				2. False to select weak coincidental correct samples for support (UI + UP)
				3. None to select all undetected samples for support (UR + UI + UP)
		:return:
		"""
		remain_patterns, solutions = set(), dict()
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
			total, support, confidence = pattern.estimate(sample_class, support_class)
			length = len(pattern)
			solutions[pattern] = (length, support, confidence)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_confidence, worst_pattern = 1.0, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				confidence = solution[2]
				if worst_pattern is None or confidence <= worst_confidence:
					worst_confidence = confidence
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_support, worst_pattern = 99999, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				support = solution[1]
				if worst_pattern is None or support <= worst_support:
					worst_support = support
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_length, worst_pattern = 0, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				length = solution[0]
				if worst_pattern is None or length >= worst_length:
					worst_length = length
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		for pattern in remain_patterns:
			return pattern
		return None

	@staticmethod
	def select_minimal_patterns(patterns, sample_class):
		"""
		:param patterns:
		:param sample_class: True to cover RIPExecution or Mutant
		:return: minimal set of patterns covering all the executions in the set
		"""
		keys_patterns = RIPMineOutput.remap_keys_patterns(patterns, sample_class)
		minimal_patterns, removed_keys = set(), set()
		while len(keys_patterns) > 0:
			removed_keys.clear()
			for sample, patterns in keys_patterns.items():
				selected_pattern = jcbase.rand_select(patterns)
				if not (selected_pattern is None):
					pattern = selected_pattern
					pattern: RIPPattern
					for pat_sample in pattern.get_samples(sample_class):
						removed_keys.add(pat_sample)
					minimal_patterns.add(pattern)
					break
			for sample in removed_keys:
				if sample in keys_patterns:
					keys_patterns.pop(sample)
		return minimal_patterns

	def get_minimal_patterns(self, sample_class):
		"""
		:param sample_class:
		:return: The minimal set of RIPPattern(s) that cover all the samples as specified
		"""
		return RIPMineOutput.select_minimal_patterns(self.patterns, sample_class)

	def get_best_patterns(self, sample_class, support_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:param support_class:
				1. True to select strong coincidental correct samples as support (UP)
				2. False to select weak coincidental correct samples for support (UI + UP)
				3. None to select all undetected samples for support (UR + UI + UP)
		:return: Mapping from Mutant to the RIPPattern it best matches with
		"""
		mutants_patterns = RIPMineOutput.remap_keys_patterns(self.patterns, MU_SAMPLE_CLASS)
		best_patterns = dict()
		for mutant, patterns in mutants_patterns.items():
			mutant: jcmuta.Mutant
			best_pattern = RIPMineOutput.select_best_pattern(patterns, sample_class, support_class)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[mutant] = best_pattern
		return best_patterns

	def get_subsuming_patterns(self, strict: bool):
		"""
		:param strict: whether to generate strictly subsuming set
		:return: minimal set of patterns that subsume the others
		"""
		return RIPMineOutput.select_subsuming_patterns(self.patterns, self.inputs.get_sample_class(), strict)


# used to print the information to outside

class RIPMineWriter:
	"""
	It implements the information output to text file for each generated RIPPattern
	"""

	def __init__(self):
		self.writer = None
		return

	# basic methods

	def __output__(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		return

	@staticmethod
	def __percentage__(ratio: float):
		return int(ratio * 1000000) / 10000.0

	@staticmethod
	def __proportion__(x: int, y: int):
		if x == 0:
			ratio = 0.0
		else:
			ratio = x / y
		return RIPMineWriter.__percentage__(ratio)

	@staticmethod
	def __prf_metric__(orig_samples: set, patt_samples: set):
		"""
		:param orig_samples: the collection of data samples from original document
		:param patt_samples: the collection of data samples from RIPPattern(s)
		:return: precision, recall, f1_score
		"""
		como_samples = orig_samples & patt_samples
		common = len(como_samples)
		if common == 0:
			precision = 0.0
			recall = 0.0
			f1_score = 0.0
		else:
			precision = common / len(patt_samples)
			recall = common / len(orig_samples)
			f1_score = 2 * precision * recall / (precision + recall)
		return precision, recall, f1_score

	# xxx.mpt

	def __write_pattern_head__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return:
				Summary 	Length Executions Mutations
				Counting	title UR UI UP KI UK CC
				Estimate	title total support confidence
		"""
		# Summary Length Executions Sequences Mutations
		self.__output__("\t{}\n".format("@SUMMARY"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\n".format("attribute", "length", "exe_num", "seq_num", "mut_num"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\n".format("value", len(pattern), len(pattern.get_executions()),
														  len(pattern.get_sequences()), len(pattern.get_mutants())))
		# Counting	title UR UI UP KI UK CC
		self.__output__("\t{}\n".format("@COUNTING"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("sample_class", "UR", "UI", "UP", "KI", "UK", "CC"))
		ur, ui, up, ki, uk, cc = pattern.counting(EX_SAMPLE_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(SQ_SAMPLE_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(MU_SAMPLE_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", ur, ui, up, ki, uk, cc))
		# Estimate sample_class support_class total support negative confidence (%)
		self.__output__("\t{}\n".format("@ESTIMATE"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("sample_class", "support_class", "total", "support",
																  "negative", "confidence", "confidence(%)"))
		# Estimate EXE {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(EX_SAMPLE_CLASS, unk_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(EX_SAMPLE_CLASS, wcc_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(EX_SAMPLE_CLASS, scc_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		# Estimate SEQ {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(SQ_SAMPLE_CLASS, unk_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(SQ_SAMPLE_CLASS, wcc_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(SQ_SAMPLE_CLASS, scc_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		# Estimate MUT {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(MU_SAMPLE_CLASS, unk_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(MU_SAMPLE_CLASS, wcc_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(MU_SAMPLE_CLASS, scc_support_strategy)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		return

	def __write_pattern_body__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return:
			condition category operator execution statement location parameter
			Mutant Result Class Operator Function Line Location Parameter
		"""
		# condition category operator validate execution statement location parameter
		self.__output__("\t@CONDITION\n")
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("index", "category", "operator",
																  "execution", "statement", "location", "parameter"))
		index = 0
		for condition in pattern.get_conditions():
			index += 1
			category = condition.get_category()
			operator = condition.get_operator()
			execution= condition.get_execution()
			statement= "\"" + execution.get_statement().get_cir_code() + "\""
			location = "\"" + condition.get_location().get_cir_code() + "\""
			parameter = "{" + str(condition.get_parameter()) + "}"
			self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, category, operator, execution,
																	  statement, location, parameter))
		# mutant Result Class Operator Function Line Location Parameter
		self.__output__("\t@MUTATION\n")
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("index", "result", "class", "operator",
																  "line", "location", "parameter"))
		for mutant in pattern.get_mutants():
			mutant: jcmuta.Mutant
			index = mutant.get_muta_id()
			result = pattern.get_classifier().__find__(mutant)
			mutation_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.mutation.get_mutation_operator()
			location = mutant.mutation.get_location()
			parameter = mutant.mutation.get_parameter()
			line = location.line_of(False)
			code = "\"" + location.get_code(True) + "\""
			self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, result, mutation_class,
																	  operator, line, code, parameter))
		return

	def __write_pattern__(self, pattern: RIPPattern):
		self.__output__("#BEG\n")
		self.__write_pattern_head__(pattern)
		self.__write_pattern_body__(pattern)
		self.__output__("#END\n")

	def __write_patterns__(self, patterns, file_path: str):
		"""
		:param patterns:
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			for pattern in patterns:
				pattern: RIPPattern
				self.__write_pattern__(pattern)
				self.__output__("\n")
		return

	# xxx.bpt

	def __write_best_pattern__(self, mutant: jcmuta.Mutant, pattern: RIPPattern):
		"""
		:param mutant:
		:param pattern:
		:return: 	Mutant 		Result 		Class 		Operator 	Line 		Location 	Parameter
					Condition 	category 	operator 	execution 	statement	location	parameter	+
		"""
		# Mutant 		Result 		Class 		Operator 	Line 		Location 	Parameter
		self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("ID", "result", "class", "operator",
															  "line", "location", "parameter"))
		mutant_id = mutant.get_muta_id()
		result = pattern.get_classifier().__find__(mutant)
		mutation_class = mutant.get_mutation().get_mutation_class()
		operator = mutant.mutation.get_mutation_operator()
		location = mutant.mutation.get_location()
		parameter = mutant.mutation.get_parameter()
		line = location.line_of(False)
		code = "\"" + location.get_code(True) + "\""
		self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(mutant_id, result, mutation_class,
															  operator, line, code, parameter))
		# Condition 	category 	operator 	execution 	statement	location	parameter	+
		self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("condition", "category", "operator", "execution",
															  "statement", "location", "parameter"))
		index = 0
		for condition in pattern.get_conditions():
			index += 1
			category = condition.get_category()
			operator = condition.get_operator()
			execution = condition.get_execution()
			statement = "\"" + execution.get_statement().get_cir_code() + "\""
			location = "\"" + condition.get_location().get_cir_code() + "\""
			parameter = "{" + str(condition.get_parameter()) + "}"
			self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, category, operator, execution,
																  statement, location, parameter))
		return

	def __write_best_patterns__(self, mutant_pattern_dict: dict, file_path: str):
		"""
		:param mutant_pattern_dict: Mapping from Mutant to RIPPattern it best matches with
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant, pattern in mutant_pattern_dict.items():
				mutant: jcmuta.Mutant
				pattern: RIPPattern
				self.__write_best_pattern__(mutant, pattern)
				self.__output__("\n")
		return

	# xxx.sum

	@staticmethod
	def __evaluate__(output: RIPMineOutput, patterns, sample_class, support_class):
		"""
		:param output:
		:param patterns:
		:return: doc_samples pat_samples reduce precision recall f1_score
		"""
		number = len(patterns)
		orig_samples = output.get_orig_samples(sample_class)
		patt_samples = output.get_patt_samples(sample_class)
		orig_samples = output.get_classifier().select(orig_samples, support_class)
		reduce = number / len(orig_samples)
		precision, recall, f1_score = RIPMineWriter.__prf_metric__(orig_samples, patt_samples)
		reduce = RIPMineWriter.__percentage__(reduce)
		precision = RIPMineWriter.__percentage__(precision)
		recall = RIPMineWriter.__percentage__(recall)
		return len(orig_samples), len(patt_samples), reduce, precision, recall, f1_score

	def __write_evaluation_all__(self, output: RIPMineOutput, patterns):
		"""
		:param output:
		:return:  doc_samples pat_samples reduce precision recall f1_score
		"""
		document = output.get_document()
		classifier = output.get_classifier()
		self.__output__("@Cost-Effective\n")
		# sample_class support_class doc_samples pat_samples reduce precision recall f1_score
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("sample_class", "support_class", "orig_samples",
																	"patt_samples", "reduce(%)", "precision(%)",
																	"recall(%)", "f1_score"))
		# EX_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter.\
			__evaluate__(output, patterns, EX_SAMPLE_CLASS, unk_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, EX_SAMPLE_CLASS, wcc_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, EX_SAMPLE_CLASS, scc_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		# SQ_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, SQ_SAMPLE_CLASS, unk_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, SQ_SAMPLE_CLASS, wcc_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, SQ_SAMPLE_CLASS, scc_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		# MU_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, MU_SAMPLE_CLASS, unk_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, MU_SAMPLE_CLASS, wcc_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, MU_SAMPLE_CLASS, scc_support_strategy)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		return

	def __write_evaluation_one__(self, patterns):
		"""
		:param patterns:
		:return: 	index length executions sequences mutants
		"""
		index = 0
		self.__output__("@Counting\n")
		self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format("index", "length", "exe_numb", "seq_numb", "mut_numb"))
		for pattern in patterns:
			index += 1
			pattern: RIPPattern
			self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format(index, len(pattern), len(pattern.get_executions()),
															len(pattern.get_sequences()), len(pattern.get_mutants())))
		return

	def __write_evaluation_two__(self, output: RIPMineOutput, patterns, sample_class, support_class):
		"""
		:param patterns:
		:return: 	title uk_exe_supp uk_exe_conf
					title wc_exe_supp wc_exe_conf
					title sc_exe_supp sc_exe_conf
					title uk_seq_supp uk_seq_conf
					title wc_seq_supp wc_seq_conf
					title sc_seq_supp sc_seq_conf
					title uk_mut_supp uk_mut_conf
					title wc_mut_supp wc_mut_conf
					title sc_mut_supp sc_mut_conf
		"""
		self.__output__("@Measure_{}_{}\n".format(sample_class, support_class))
		self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format("index", "support", "confidence(%)", "recall(%)", "f1_score"))
		index = 0
		orig_samples = output.get_orig_samples(sample_class)
		for pattern in patterns:
			pattern: RIPPattern
			index += 1
			total, support, confidence = pattern.estimate(sample_class, support_class)
			patt_samples = pattern.get_samples(sample_class)
			precision, recall, f1_score = self.__prf_metric__(orig_samples, patt_samples)
			self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format(index, support, RIPMineWriter.__percentage__(confidence),
														  	RIPMineWriter.__percentage__(recall), f1_score))
		return

	def __write_evaluation_sum__(self, output: RIPMineOutput, file_path: str):
		"""
		:param output:
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			patterns = output.get_subsuming_patterns(False)
			self.__write_evaluation_all__(output, patterns)
			self.__output__("\n")
			self.__write_evaluation_one__(patterns)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, output.inputs.get_sample_class(),
										  output.inputs.get_support_strategy())
			self.__output__("\n")
		return

	def write_to(self, output: RIPMineOutput, directory: str):
		"""
		:param output:
		:param directory:
		:return: xxx.mpt xxx.bpt xxx.sum
		"""
		file_name = output.get_document().get_program().name
		self.__write_patterns__(output.get_subsuming_patterns(True), os.path.join(directory, file_name + ".mpt"))
		self.__write_best_patterns__(output.get_best_patterns(SQ_SAMPLE_CLASS, unk_support_strategy),
									 os.path.join(directory, file_name + ".bpt"))
		self.__write_evaluation_sum__(output, os.path.join(directory, file_name + ".sum"))
		return

