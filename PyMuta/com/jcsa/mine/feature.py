"""
This file defines the data model of feature used as inputs of data mining algorithm.
"""


import os
from typing import TextIO
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


UR_CLASS = "UR"					# The testing failed to reach the mutated statement.
UI_CLASS = "UI"					# The testing reaches the mutant but fail to infect.
UP_CLASS = "UP"					# The testing infected the state but failed to kill.
KI_CLASS = "KI"					# The testing successfully kills the target mutants.


UK_SUPPORT_CLASS = None			# Used as parameter of RIPClassifier.estimate in which undetected samples are selected.
WC_SUPPORT_CLASS = False		# Used as parameter of RIPClassifier.estimate in which weak coincidental correct ones
SC_SUPPORT_CLASS = True			# Used as parameter of RIPClassifier.estimate in which strong coincidental correctness


MU_SAMPLE_CLASS = None			# Take the Mutant as samples for being evaluated in RIPPattern.get_samples
SQ_SAMPLE_CLASS = False			# Take the SymSequence as samples for being evaluated in RIPPattern.get_samples
EX_SAMPLE_CLASS = True			# Take the SymExecution as samples for being evaluated in RIPPattern.get_samples


class RIPClassifier:
	"""
	It is used to classify and evaluate the performance of patterns being created in terms of
	reachability, infection and propagation framework.
	"""

	def __init__(self, used_tests):
		"""
		:param used_tests:
				1. The collection of test cases (or their unique integer ID) that are assumed to be used in testing.
				2. None if all the test cases in the space are of consideration.
		"""
		self.used_tests = used_tests
		self.solutions = dict()			# Mapping from Mutant.muta_id to {UR|UI|UP|KI} class label.
		return

	def __classify__(self, sample):
		"""
		:param sample: either Mutant or SymExecution or SymSequence
		:return: 	UR	---	The used_tests fail to reach the mutant in sample
					UI	---	The used_tests fail to infect but reach the mutant
					UP	---	The used_tests fail to kill but infect program state
					KI	---	The used_tests successfully kill the target mutant.
		"""
		# 1. obtain the target mutant for being classified
		if isinstance(sample, jctest.SymExecution):
			mutant = sample.get_sequence().get_mutant()
		elif isinstance(sample, jctest.SymSequence):
			mutant = sample.get_mutant()
		else:
			sample: jcmuta.Mutant
			mutant = sample
		# 2. solve the class-tag when it is not evaluated yet
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
		# 3. obtain the existing class label to the mutant
		label = self.solutions[mutant.get_muta_id()]
		label: str
		return label

	def classify(self, samples):
		"""
		:param samples: The collection of {Mutant, SymExecution, SymSequence} for being analyzed.
		:return:		Mapping from {UR|UI|UP|KI} ==> The set of samples w.r.t. the given class.
		"""
		class_map = dict()
		class_map[UR_CLASS] = set()
		class_map[UI_CLASS] = set()
		class_map[UP_CLASS] = set()
		class_map[KI_CLASS] = set()
		for sample in samples:
			label = self.__classify__(sample)
			class_map[label].add(sample)
		return class_map

	def counting(self, samples):
		"""
		:param samples: The collection of {Mutant, SymExecution, SymSequence} for being analyzed.
		:return: 		ur, ui, up, ki, uk, cc
						1. ur: the number of samples that fail to be reached by used tests.
						2. ui: the number of samples that fail to be infected but reached by tests.
						3. up: the number of samples that fail to be detected but infected by tests.
						4. ki: the number of samples that be successfully detected by used tests.
		"""
		ur, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			label = self.__classify__(sample)
			if label == KI_CLASS:
				ki += 1
			elif label == UP_CLASS:
				up += 1
			elif label == UI_CLASS:
				ui += 1
			else:
				ur += 1
		return ur, ui, up, ki, ur + ui + up, ui + up

	def estimate(self, samples, support_class):
		"""
		:param samples: The collection of {Mutant, SymExecution, SymSequence} for being analyzed.
		:param support_class:
						1. True to select strong coincidental correct samples as support (UP)
						2. False to select weak coincidental correct samples for support (UI + UP)
						3. None to select all undetected samples for support (UR + UI + UP)
		:return: total, support, confidence
		"""
		ur, ui, up, ki, uk, cc = self.counting(samples)
		if support_class is None:
			support = uk
		elif support_class:
			support = up
		else:
			support = cc
		total = support + ki
		if support == 0:
			confidence = 0.0
		else:
			confidence = support / total
		return total, support, confidence

	def select(self, samples, support_class):
		"""
		:param samples:	The collection of {Mutant, SymExecution, SymSequence} for being analyzed.
		:param support_class:
						1. True to select strong coincidental correct samples as selected (UP)
						2. False to select weak coincidental correct samples for selected (UI + UP)
						3. None to select all undetected samples for selected (UR + UI + UP)
		:return: The collection of samples being selected from.
		"""
		class_map = self.classify(samples)
		if support_class is None:
			return class_map[UR_CLASS] | class_map[UI_CLASS] | class_map[UP_CLASS]
		elif support_class:
			return class_map[UP_CLASS]
		else:
			return class_map[UI_CLASS] | class_map[UP_CLASS]


class RIPPattern:
	"""
	It describes the pattern of mutation in terms of reachability-infection-propagation framework.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		"""
		:param document:
		:param classifier:
		"""
		self.document = document
		self.classifier = classifier
		self.executions = set()
		self.sequences = set()
		self.mutants = set()
		self.words = list()
		return

	# Data Model

	def get_document(self):
		"""
		:return: It provides the original data samples taken from the program
		"""
		return self.document

	def get_executions(self):
		"""
		:return: The set of SymExecution of which words match with the pattern.
		"""
		return self.executions

	def get_sequences(self):
		"""
		:return: The set of SymSequence of which words match with the pattern.
		"""
		return self.sequences

	def get_mutants(self):
		"""
		:return: The set of Mutant of which words match with the pattern.
		"""
		return self.mutants

	def get_samples(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return: The set of samples matched with this pattern.
		"""
		if sample_class is None:
			return self.mutants
		elif sample_class:
			return self.executions
		else:
			return self.sequences

	def __match_exe__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return: True if any word included by the execution
		"""
		for word in self.words:
			if word in execution.get_words():
				return True
		return len(self.words) == 0

	def __match_seq__(self, sequence: jctest.SymSequence):
		"""
		:param sequence:
		:return: True if all the words in pattern are included by the pattern
		"""
		for word in self.words:
			if not(word in sequence.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent:
				1. The RIPPattern from which this pattern is directly extended as its child.
				2. None if the samples matched in this pattern updated for entire databases.
		:return:
		"""
		# 1. obtain the symbolic sequences being matched by parent
		if parent is None:
			sequences = self.document.get_sequences()
		else:
			parent: RIPPattern
			sequences = parent.get_sequences()
		# 2. perform the data matching operations on this pattern
		for sequence in sequences:
			sequence: jctest.SymSequence
			if self.__match_seq__(sequence):
				self.sequences.add(sequence)
				self.mutants.add(sequence.get_mutant())
				for execution in sequence.get_executions():
					if self.__match_exe__(execution):
						self.executions.add(execution)
		return

	# Estimation

	def get_classifier(self):
		"""
		:return: The RIPClassifier used to estimate this pattern.
		"""
		return self.classifier

	def classify(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return: Mapping from {UR|UI|UP|KI} to the set of corresponding samples
		"""
		return self.classifier.classify(self.get_samples(sample_class))

	def counting(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return: ur, ui, up, ki, uk, cc
				1. ur: the number of samples that fail to be reached by used tests.
				2. ui: the number of samples that fail to be infected but reached by tests.
				3. up: the number of samples that fail to be detected but infected by tests.
				4. ki: the number of samples that be successfully detected by used tests.
		"""
		return self.classifier.counting(self.get_samples(sample_class))

	def estimate(self, sample_class, support_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:param support_class:
				1. True to select strong coincidental correct samples as support (UP)
				2. False to select weak coincidental correct samples for support (UI + UP)
				3. None to select all undetected samples for support (UR + UI + UP)
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(sample_class), support_class)

	def select(self, sample_class, support_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:param support_class:
				1. True to select strong coincidental correct samples as support (UP)
				2. False to select weak coincidental correct samples for support (UI + UP)
				3. None to select all undetected samples for support (UR + UI + UP)
		:return: The collection of samples matched by this pattern with correspond to supporting class
		"""
		return self.classifier.select(self.get_samples(sample_class), support_class)

	# Feature Model

	def get_words(self):
		"""
		:return: the collection of words encoding the symbolic conditions required in testing
		"""
		return self.words

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_conditions(self):
		"""
		:return: the collection of the symbolic conditions required in testing
		"""
		return self.document.get_conditions_lib().get_conditions(self.words)

	# Relationships

	def extends(self, word: str):
		"""
		:param word:
		:return: the child pattern directly extended from this one by adding one new word
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

	def subsume(self, pattern, strict: bool):
		"""
		:param pattern: The pattern to be subsumed by this one for testing.
		:param strict:
				1. True if the computation is based on strict subsumption
				2. False if the computation incorporates equality operation.
		:return:
		"""
		pattern: RIPPattern
		for execution in pattern.get_executions():
			if not(execution in self.executions):
				return False
		if strict:
			return len(self.executions) > len(pattern.get_executions())
		else:
			return True


class RIPMineInputs:
	"""
	The inputs model describes the inputs data directly used for mining algorithm.
	"""

	def __init__(self, document: jctest.CDocument, used_tests, sample_class, support_class,
				 max_length: int, min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: 		It provides the original samples in database.
		:param used_tests: 		The set of test cases used in execution of mutation.
		:param sample_class: 	The class of samples used for pattern evaluation.
								1. True to take SymExecution
								2. False to take SymSequence
								3. None to take the Mutant
		:param support_class: 	The class of samples taken as supporting class.
								1. True to take {UP}
								2. False to take {UI + UP}
								3. None to take {UR + UI + UP}
		:param max_length: 		The maximal length of the patterns being generated.
		:param min_support: 	The minimal number of samples required for supporting.
		:param min_confidence: 	The minimal confidence required for mining patterns.
		:param max_confidence: 	The maximal confidence to terminate the mining procedure.
		"""
		self.document = document
		self.classifier = RIPClassifier(used_tests)
		self.sample_class = sample_class
		self.support_class = support_class
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

	def get_support_class(self):
		return self.support_class

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_max_length(self):
		return self.max_length


class RIPMineMiddle:
	"""
	It provides the factory methods for creating RIPPattern (unique instance).
	"""

	def __init__(self, inputs: RIPMineInputs):
		self.inputs = inputs
		self.patterns = dict()	# String ==> RIPPattern
		self.solution = dict()	# RIPPattern ==> (length, support, confidence)
		return

	def get_document(self):
		return self.inputs.get_document()

	def get_classifier(self):
		return self.inputs.get_classifier()

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
														  self.inputs.get_support_class())
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


class RIPMineOutput:
	"""
	It preserves the good patterns generated from the middle module and mining algorithms.
	"""

	def __init__(self, inputs: RIPMineInputs, good_patterns: set):
		"""
		:param inputs: It provides the entire inputs data for mining algorithm.
		:param good_patterns: The set of good patterns being generated by mining.
		"""
		# 1. original data building
		self.document = inputs.get_document()
		self.orig_executions = set()
		self.orig_sequences = set()
		self.orig_mutants = set()
		for sequence in self.document.get_sequences():
			sequence: jctest.SymSequence
			for execution in sequence.get_executions():
				execution: jctest.SymExecution
				self.orig_executions.add(execution)
			self.orig_sequences.add(sequence)
			self.orig_mutants.add(sequence.get_mutant())
		# 2. matching data building
		self.patterns = set()
		self.patt_executions = set()
		self.patt_sequences = set()
		self.patt_mutants = set()
		for pattern in good_patterns:
			pattern: RIPPattern
			self.patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jctest.SymExecution
				self.patt_executions.add(execution)
				self.patt_sequences.add(execution.get_sequence())
				self.patt_mutants.add(execution.get_sequence().get_mutant())
		# 3. structural descriptions
		self.inputs = inputs
		self.classifier = inputs.get_classifier()
		return

	# getters

	def get_document(self):
		return self.document

	def get_orig_executions(self):
		return self.orig_executions

	def get_orig_sequences(self):
		return self.orig_sequences

	def get_orig_mutants(self):
		return self.orig_mutants

	def get_orig_samples(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return:
		"""
		if sample_class is None:
			return self.orig_mutants
		elif sample_class:
			return self.orig_executions
		else:
			return self.orig_sequences

	def get_patterns(self):
		return self.patterns

	def get_patt_executions(self):
		return self.patt_executions

	def get_patt_sequences(self):
		return self.patt_sequences

	def get_patt_mutants(self):
		return self.patt_mutants

	def get_patt_samples(self, sample_class):
		"""
		:param sample_class:
				1. True to select SymExecution being matched as samples
				2. False to select SymSequence being matched as samples
				3. None to select Mutant of SymSequence as samples for
		:return:
		"""
		if sample_class is None:
			return self.patt_mutants
		elif sample_class:
			return self.patt_executions
		else:
			return self.patt_sequences

	def get_classifier(self):
		return self.classifier

	@staticmethod
	def select_subsuming_patterns(patterns, strict: bool):
		"""
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
				elif subsume_pattern.subsume(pattern, strict):
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern, strict):
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

	def get_subsuming_patterns(self, strict: bool):
		"""
		:param strict: whether to generate strictly subsuming set
		:return: minimal set of patterns that subsume the others
		"""
		return RIPMineOutput.select_subsuming_patterns(self.patterns, strict)

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

		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("length",  len(pattern),
																	  "exe_num", len(pattern.get_executions()),
																	  "seq_num", len(pattern.get_sequences()),
																	  "mut_num", len(pattern.get_mutants())))
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
		total, support, confidence = pattern.estimate(EX_SAMPLE_CLASS, UK_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(EX_SAMPLE_CLASS, WC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(EX_SAMPLE_CLASS, SC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		# Estimate SEQ {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(SQ_SAMPLE_CLASS, UK_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(SQ_SAMPLE_CLASS, WC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(SQ_SAMPLE_CLASS, SC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		# Estimate MUT {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(MU_SAMPLE_CLASS, UK_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(MU_SAMPLE_CLASS, WC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(MU_SAMPLE_CLASS, SC_SUPPORT_CLASS)
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
			result = pattern.get_classifier().__classify__(mutant)
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
		result = pattern.get_classifier().__classify__(mutant)
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
			__evaluate__(output, patterns, EX_SAMPLE_CLASS, UK_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, EX_SAMPLE_CLASS, WC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, EX_SAMPLE_CLASS, SC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		# SQ_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, SQ_SAMPLE_CLASS, UK_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, SQ_SAMPLE_CLASS, WC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, SQ_SAMPLE_CLASS, SC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		# MU_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, MU_SAMPLE_CLASS, UK_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, MU_SAMPLE_CLASS, WC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, MU_SAMPLE_CLASS, SC_SUPPORT_CLASS)
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

			self.__write_evaluation_two__(output, patterns, EX_SAMPLE_CLASS, UK_SUPPORT_CLASS)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, EX_SAMPLE_CLASS, WC_SUPPORT_CLASS)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, EX_SAMPLE_CLASS, SC_SUPPORT_CLASS)
			self.__output__("\n")

			self.__write_evaluation_two__(output, patterns, SQ_SAMPLE_CLASS, UK_SUPPORT_CLASS)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, SQ_SAMPLE_CLASS, WC_SUPPORT_CLASS)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, SQ_SAMPLE_CLASS, SC_SUPPORT_CLASS)
			self.__output__("\n")

			self.__write_evaluation_two__(output, patterns, MU_SAMPLE_CLASS, UK_SUPPORT_CLASS)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, MU_SAMPLE_CLASS, WC_SUPPORT_CLASS)
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, MU_SAMPLE_CLASS, SC_SUPPORT_CLASS)
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
		self.__write_best_patterns__(output.get_best_patterns(SQ_SAMPLE_CLASS, UK_SUPPORT_CLASS),
									 os.path.join(directory, file_name + ".bpt"))
		self.__write_evaluation_sum__(output, os.path.join(directory, file_name + ".sum"))
		return

