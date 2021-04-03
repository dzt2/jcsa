"""This file defines the structural model of inputs and output data used in pattern mining based on RIP framework"""


import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


# the label to describe the categorization of test result between mutant and a test suite in form of RIP framework.


UR_CLASS = 'R'						# the test suite failed to reach the mutated statement.
UI_CLASS = 'I'						# the test suite failed to infect but reach the mutant.
UP_CLASS = 'P'						# the test suite failed to kill but infect that mutant.
KI_CLASS = 'K'						# the test suite successfully detect the target mutant.


# the definition of sample category being counted as data samples for evaluation patterns being generated


MUT_SAMPLE_CLASS = 'M'				# take the Mutant(s) as the data samples for being classified.
SEQ_SAMPLE_CLASS = 'S'				# take the SymSequence(s) as data sample for being classified.
EXE_SAMPLE_CLASS = 'E'				# take the SymExecutions as data samples for being classified.


# the supporting method to decide whether a sample can be counted and supported in form of (str --> (bool, bool))


def __unk_count_support__(label: str):
	"""
	:param label: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS}
	:return: 	counted, supported
				1. counted: whether the sample w.r.t. the label is counted in estimation;
				2. supported: whether the sample w.r.t. label is supported in estimation.
	"""
	if label == UR_CLASS or label == UI_CLASS or label == UP_CLASS:
		return True, True
	elif label == KI_CLASS:
		return True, False
	else:
		return False, False


def __wcc_count_support__(label: str):
	"""
	:param label: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS}
	:return: 	counted, supported
				1. counted: whether the sample w.r.t. the label is counted in estimation;
				2. supported: whether the sample w.r.t. label is supported in estimation.
	"""
	if label == UI_CLASS or label == UP_CLASS:
		return True, True
	elif label == KI_CLASS:
		return True, False
	else:
		return False, False


def __scc_count_support__(label: str):
	"""
	:param label: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS}
	:return: 	counted, supported
				1. counted: whether the sample w.r.t. the label is counted in estimation;
				2. supported: whether the sample w.r.t. label is supported in estimation.
	"""
	if label == UP_CLASS:
		return True, True
	elif label == KI_CLASS:
		return True, False
	else:
		return False, False


def __kid_count_support__(label: str):
	"""
	:param label: {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS}
	:return: 	counted, supported
				1. counted: whether the sample w.r.t. the label is counted in estimation;
				2. supported: whether the sample w.r.t. label is supported in estimation.
	"""
	if label == KI_CLASS:
		return True, True
	else:
		return True, False


UNK_SUPPORT_CLASS = __unk_count_support__			# take {UR, UI, UP} as support and {KI} as negative
WCC_SUPPORT_CLASS = __wcc_count_support__			# take {UI, UP} as support and {KI} as negative
SCC_SUPPORT_CLASS = __scc_count_support__			# take {UP} as support and {KI} as negative
KID_SUPPORT_CLASS = __kid_count_support__			# take {UR, UI, UP} as negative and {KI} as support


# classifier module used to evaluate the performance of any generated patterns from mutation analysis.


class RIPClassifier:
	"""
	The module is used to classify each sample according to its results against a given test suite.
	"""

	def __init__(self, used_tests):
		"""
		:param used_tests:
				1. The collection of TestCase (or its test_id) that the sample (Mutant) being executed against.
				2. None to represent the entire collection of test cases in project are executed on the sample.
		"""
		self.used_tests = used_tests
		self.__solutions__ = dict()		# Mutant.muta_id --> class_label(str)
		return

	def __find__(self, sample):
		"""
		:param sample: 	Mutant | SymSequence | SymExecution
		:return: 		UR_CLASS	--- The used_tests failed to reach the mutated statement of the sample.
						UI_CLASS	---	The used_tests reached but fail to infect the mutant of the sample.
						UP_CLASS 	---	The used_tests infected but fails to kill the mutant of the sample.
						KI_CLASS	---	The used_tests successfully kill the target mutation of the sample.
		"""
		# 1. obtain the mutant being evaluated
		if isinstance(sample, jctest.SymExecution):
			mutant = sample.get_sequence().get_mutant()
		elif isinstance(sample, jctest.SymSequence):
			mutant = sample.get_mutant()
		else:
			sample: jcmuta.Mutant
			mutant = sample

		# 2. when the category of the mutant is not solved before
		if not(mutant in self.__solutions__):
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
			self.__solutions__[mutant] = label

		# 3. extract the category label of the mutant from space
		label = self.__solutions__[mutant]
		label: str
		return label

	def classify(self, samples):
		"""
		:param samples: the collection of (Mutant, SymSequence, SymExecution) to be evaluated
		:return: Mapping from {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS} to the set of samples w.r.t. the category
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
		:param samples: the collection of (Mutant, SymSequence, SymExecution) to be evaluated
		:return:	ur, ui, up, ki, uk, cc
					1. ur: the number of samples that fail to be reached by used_tests.
					2. ui: the number of samples that are reached but fail to be infected by used_tests.
					3. up: the number of samples that are infected but fail to be killed by used_tests.
					4. ki: the number of samples that are successfully killed by the used_tests.
					5. uk: the number of samples that fail to be killed by the used_tests (ur + ui + up).
					6. cc: the number of samples that fail to be killed even when reached (ui + up).
		"""
		ur, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			label = self.__find__(sample)
			if label == UR_CLASS:
				ur += 1
			elif label == UI_CLASS:
				ui += 1
			elif label == UP_CLASS:
				up += 1
			elif label == KI_CLASS:
				ki += 1
		return ur, ui, up, ki, ur + ui + up, ui + up

	def estimate(self, samples, support_class):
		"""
		:param samples: the collection of (Mutant, SymSequence, SymExecution) to be evaluated
		:param support_class: one of {UNK_SUPPORT_CLASS, WCC_SUPPORT_CLASS, SCC_SUPPORT_CLASS, KID_SUPPORT_CLASS}
		:return: 	total, support, confidence
					1. total: the number of samples being counted for supporting classification.
					2. support: the number of samples be supported in supporting classification.
					3. confidence: support / total
		"""
		total, support, confidence = 0, 0, 0.0
		for sample in samples:
			label = self.__find__(sample)
			counted, supported = support_class(label)
			if counted:
				total += 1
				if supported:
					support += 1
		if support > 0:
			confidence = support / total
		return total, support, confidence

	def partition(self, samples, support_class):
		"""
		:param samples: the collection of (Mutant, SymSequence, SymExecution) to be evaluated
		:param support_class: one of {UNK_SUPPORT_CLASS, WCC_SUPPORT_CLASS, SCC_SUPPORT_CLASS, KID_SUPPORT_CLASS}
		:return: 	positive_samples, negative_samples
					1. positive_samples: the collection of samples being supported according to support_class.
					2. negative_samples: the collection of samples counted but not supported by support_class.
		"""
		positive_samples, negative_samples = set(), set()
		for sample in samples:
			counted, supported = support_class(self.__find__(sample))
			if counted:
				if supported:
					positive_samples.add(sample)
				else:
					negative_samples.add(sample)
		return positive_samples, negative_samples


# structural description of the patterns in term of symbolic conditions required for killing any mutant in testing.


class RIPPattern:
	"""
	It describes the structural definition of patterns for mutant in term of symbolic conditions required in testing.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		self.document = document					# It provides the entire dataset with samples for matched.
		self.classifier = classifier				# It is used to evaluate the performance for this pattern.
		self.samples = dict()						# SAMPLE_CLASS --> set
		self.samples[MUT_SAMPLE_CLASS] = set()		# The set of Mutant(s) being matched for this pattern.
		self.samples[SEQ_SAMPLE_CLASS] = set()		# The set of SymSequence(s) be matched by the pattern.
		self.samples[EXE_SAMPLE_CLASS] = set()		# The set of SymExecutions be matched by this pattern.
		self.words = list()							# The set of words encoding symbolic conditions included.
		return

	# data samples

	def get_document(self):
		"""
		:return: It provides the entire dataset with samples for matched.
		"""
		return self.document

	def get_samples(self, sample_class: str):
		"""
		:param sample_class:
				1. MUT_SAMPLE_CLASS: The set of Mutant(s) being matched for this pattern.
				2. SEQ_SAMPLE_CLASS: The set of SymSequence(s) be matched by the pattern.
				3. EXE_SAMPLE_CLASS: The set of SymExecutions be matched by this pattern.
		:return:
		"""
		return self.samples[sample_class]

	def __match_exe__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return: whether the execution matches with this pattern
		"""
		for word in self.words:
			if word in execution.get_words():
				return True
		return False

	def __match_seq__(self, sequence: jctest.SymSequence):
		"""
		:param sequence:
		:return: whether the sequence matches with this pattern
		"""
		for word in self.words:
			if not(word in sequence.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent:
				1. RIPPattern: the pattern from which this one is directly extended.
				2. None: the pattern is considered being extended via root document.
		:return:
		"""
		# 1. obtain the sequences for being updated
		if parent is None:
			sequences = self.document.get_sequences()
		else:
			parent: RIPPattern
			sequences = parent.get_samples(SEQ_SAMPLE_CLASS)

		# 2. clear the data samples from the pattern
		self.get_samples(MUT_SAMPLE_CLASS).clear()
		self.get_samples(SEQ_SAMPLE_CLASS).clear()
		self.get_samples(EXE_SAMPLE_CLASS).clear()

		# 3. updating the data samples being matched from sequences
		for sequence in sequences:
			sequence: jctest.SymSequence
			if self.__match_seq__(sequence):
				self.get_samples(SEQ_SAMPLE_CLASS).add(sequence)
				self.get_samples(MUT_SAMPLE_CLASS).add(sequence.get_mutant())
				for execution in sequence.get_executions():
					execution: jctest.SymExecution
					self.get_samples(EXE_SAMPLE_CLASS).add(execution)
		return

	def get_executions(self):
		return self.samples.get(EXE_SAMPLE_CLASS)

	def get_sequences(self):
		return self.samples.get(SEQ_SAMPLE_CLASS)

	def get_mutants(self):
		return self.samples.get(MUT_SAMPLE_CLASS)

	# estimation

	def get_classifier(self):
		"""
		:return: It is used to evaluate the performance for this pattern.
		"""
		return self.classifier

	def classify(self, sample_class: str):
		"""
		:param sample_class:
				1. MUT_SAMPLE_CLASS: The set of Mutant(s) being matched for this pattern.
				2. SEQ_SAMPLE_CLASS: The set of SymSequence(s) be matched by the pattern.
				3. EXE_SAMPLE_CLASS: The set of SymExecutions be matched by this pattern.
		:return: Mapping from {UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS} to the set of samples w.r.t. the category
		"""
		return self.classifier.classify(self.get_samples(sample_class))

	def counting(self, sample_class: str):
		"""
		:param sample_class:
				1. MUT_SAMPLE_CLASS: The set of Mutant(s) being matched for this pattern.
				2. SEQ_SAMPLE_CLASS: The set of SymSequence(s) be matched by the pattern.
				3. EXE_SAMPLE_CLASS: The set of SymExecutions be matched by this pattern.
		:return: 	ur, ui, up, ki, uk, cc
					1. ur: the number of samples that fail to be reached by used_tests.
					2. ui: the number of samples that are reached but fail to be infected by used_tests.
					3. up: the number of samples that are infected but fail to be killed by used_tests.
					4. ki: the number of samples that are successfully killed by the used_tests.
					5. uk: the number of samples that fail to be killed by the used_tests (ur + ui + up).
					6. cc: the number of samples that fail to be killed even when reached (ui + up).
		"""
		return self.classifier.counting(self.get_samples(sample_class))

	def estimate(self, sample_class: str, support_class):
		"""
		:param sample_class:
				1. MUT_SAMPLE_CLASS: The set of Mutant(s) being matched for this pattern.
				2. SEQ_SAMPLE_CLASS: The set of SymSequence(s) be matched by the pattern.
				3. EXE_SAMPLE_CLASS: The set of SymExecutions be matched by this pattern.
		:param support_class: one of {UNK_SUPPORT_CLASS, WCC_SUPPORT_CLASS, SCC_SUPPORT_CLASS, KID_SUPPORT_CLASS}
		:return: 	total, support, confidence
					1. total: the number of samples being counted for supporting classification.
					2. support: the number of samples be supported in supporting classification.
					3. confidence: support / total
		"""
		return self.classifier.estimate(self.get_samples(sample_class), support_class)

	def partition(self, sample_class: str, support_class):
		"""
		:param sample_class:
				1. MUT_SAMPLE_CLASS: The set of Mutant(s) being matched for this pattern.
				2. SEQ_SAMPLE_CLASS: The set of SymSequence(s) be matched by the pattern.
				3. EXE_SAMPLE_CLASS: The set of SymExecutions be matched by this pattern.
		:param support_class: one of {UNK_SUPPORT_CLASS, WCC_SUPPORT_CLASS, SCC_SUPPORT_CLASS, KID_SUPPORT_CLASS}
		:return: 	positive_samples, negative_samples
					1. positive_samples: the collection of samples being supported according to support_class.
					2. negative_samples: the collection of samples counted but not supported by support_class.
		"""
		return self.classifier.partition(self.get_samples(sample_class), support_class)

	# feature model

	def get_words(self):
		"""
		:return: The set of words encoding symbolic conditions included.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: The set of symbolic conditions included.
		"""
		return self.document.get_conditions_lib().get_conditions(self.words)

	def __str__(self):
		return str(self.words)

	def __len__(self):
		return len(self.words)

	# relationships

	def extends(self, word: str):
		"""
		:param word:
		:return: The child pattern directly extended from this one by adding one new word
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

	def subsume(self, pattern, sample_class: str, strict: bool):
		"""
		:param pattern: the RIPPattern being subsumed by this one
		:param sample_class:
				1. MUT_SAMPLE_CLASS: The set of Mutant(s) being matched for this pattern.
				2. SEQ_SAMPLE_CLASS: The set of SymSequence(s) be matched by the pattern.
				3. EXE_SAMPLE_CLASS: The set of SymExecutions be matched by this pattern.
		:param strict:
				1. True  --- To compute strict subsumption in which equivalence is not considered.
				2. False --- To compute subsumption in which the equivalence is taken in accounts.
		:return:
		"""
		# 1. obtain the samples for computing subsumption
		pattern: RIPPattern
		source_samples = self.get_samples(sample_class)
		target_samples = pattern.get_samples(sample_class)

		# 2. compute the subsumption relationships
		if len(source_samples) < len(target_samples):
			return False
		else:
			for sample in target_samples:
				if not(sample in source_samples):
					return False

		# 3. strict or non-strict subsumption
		if strict:
			return len(source_samples) > len(target_samples)
		else:
			return True


# the inputs module


class RIPMineInputs:
	"""
	It preserves the inputs parameters for pattern mining.
	"""

	def __init__(self, document: jctest.CDocument, used_tests, sample_class: str, support_class,
				 max_length: int, min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document:		It provides the entire dataset from which patterns are produced.
		:param used_tests:		It is used to evaluate the performance of generated patterns in.
		:param sample_class:	One of the {MUT_SAMPLE_CLASS, SEQ_SAMPLE_CLASS, EXE_SAMPLE_CLASS}
		:param support_class:	One of the {UNK_SUPPORT_CLASS, WCC_SUPPORT_CLASS, SCC_SUPPORT_CLASS, KID_SUPPORT_CLASS}
		:param max_length:		The maximal length allowed for generated patterns for presentations.
		:param min_support:		The minimal support required for generated patterns in presentation.
		:param min_confidence:	The minimal confidence needed in generated patterns in presentation.
		:param max_confidence:	The maximal confidence once achieve to terminate the mining process.
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

	def get_max_length(self):
		return self.max_length

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence


# the middle module


class RIPMineMiddle:
	"""
	The middle module is used to produce unique patterns and obtain their estimation fast.
	"""

	def __init__(self, inputs: RIPMineInputs):
		self.inputs = inputs
		self.patterns = dict()	# String ==> RIPPattern
		self.solution = dict()	# RIPPattern ==> (length, support, confidence)
		return

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_classifier(self):
		return self.inputs.get_classifier()

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: RIPPattern if extended to child or None to create root pattern
		:param word:
		:return:
		"""
		# 1. create a simple copy oof the pattern from {parent::word}
		if parent is None:
			child = RIPPattern(self.get_document(), self.get_classifier())
			child = child.extends(word)
		else:
			parent: RIPPattern
			child = parent.extends(word)

		# 2. if the pattern has not be created, update its samples and estimation
		if not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_samples(parent)
			self.estimate(child)

		child = self.patterns[str(child)]
		child: RIPPattern
		return child

	def get_root(self, word: str):
		return self.__new_pattern__(None, word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)

	def get_pattern(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: the unique instance of the pattern
		"""
		unique_pattern = self.__new_pattern__(None, '')
		for word in pattern.get_words():
			unique_pattern = self.__new_pattern__(unique_pattern, word)
		return unique_pattern

	def estimate(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: length, support, confidence
		"""
		# 1. obtain the unique instance of the pattern with samples updated
		pattern = self.get_pattern(pattern)

		# 2. update the solution when pattern has not be updated
		if not(pattern in self.solution):
			length = len(pattern)
			total, support, confidence = pattern.estimate(self.inputs.get_sample_class(),
														  self.inputs.get_support_class())
			self.solution[pattern] = (length, support, confidence)

		# 3. obtain the solution w.r.t. the input pattern
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
		:return: The collection of 'good' patterns generated from the mining module
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


# the output module


class RIPMineOutput:
	"""
	The output module after producing patterns from mining algorithm.
	"""

	def __init__(self, middle: RIPMineMiddle):
		"""
		:param middle:
		"""
		self.inputs = middle.get_inputs()
		self.classifier = middle.get_classifier()

		self.document = middle.get_document()
		self.doc_mutants = set()
		self.doc_sequences = set()
		self.doc_executions = set()
		for sequence in self.document.get_sequences():
			sequence: jctest.SymSequence
			self.doc_sequences.add(sequence)
			self.doc_mutants.add(sequence.get_mutant())
			for execution in sequence.get_executions():
				execution: jctest.SymExecution
				self.doc_executions.add(execution)

		self.patterns = middle.extract_good_patterns()
		self.pat_mutants = set()
		self.pat_sequences = set()
		self.pat_executions = set()
		for pattern in self.patterns:
			for sample in pattern.get_samples(MUT_SAMPLE_CLASS):
				sample: jcmuta.Mutant
				self.pat_mutants.add(sample)
			for sample in pattern.get_samples(SEQ_SAMPLE_CLASS):
				sample: jctest.SymSequence
				self.pat_sequences.add(sample)
			for sample in pattern.get_samples(EXE_SAMPLE_CLASS):
				sample: jctest.SymExecution
				self.pat_executions.add(sample)

		return

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.document

	def get_doc_executions(self):
		return self.doc_executions

	def get_doc_sequences(self):
		return self.doc_sequences

	def get_doc_mutants(self):
		return self.doc_mutants

	def get_doc_samples(self, sample_class):
		if sample_class == MUT_SAMPLE_CLASS:
			return self.doc_mutants
		elif sample_class == SEQ_SAMPLE_CLASS:
			return self.doc_sequences
		elif sample_class == EXE_SAMPLE_CLASS:
			return self.doc_executions
		else:
			return set()

	def get_patterns(self):
		return self.patterns

	def get_pat_executions(self):
		return self.pat_executions

	def get_pat_sequences(self):
		return self.pat_sequences

	def get_pat_mutants(self):
		return self.pat_mutants

	def get_pat_samples(self, sample_class):
		if sample_class == MUT_SAMPLE_CLASS:
			return self.pat_mutants
		elif sample_class == SEQ_SAMPLE_CLASS:
			return self.pat_sequences
		elif sample_class == EXE_SAMPLE_CLASS:
			return self.pat_sequences
		else:
			return set()

	def get_classifier(self):
		return self.classifier

	@staticmethod
	def select_subsuming_patterns(patterns, sample_class, strict: bool):
		"""
		:param patterns: the set of patterns from which the subsuming ones are obtained
		:param sample_class: to decide of which samples are take to compute subsumption
		:param strict: whether to generate minimal set of strict subsuming all others
		:return:
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
	def remap_keys_patterns(patterns, sample_class: str):
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
	def select_best_pattern(patterns, sample_class: str, support_class):
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
	def select_minimal_patterns(patterns, sample_class: str):
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
		return RIPMineOutput.select_subsuming_patterns(self.patterns, self.inputs.get_sample_class(), strict)

	def get_minimal_patterns(self, sample_class: str):
		"""
		:param sample_class:
		:return: The minimal set of RIPPattern(s) that cover all the samples as specified
		"""
		return RIPMineOutput.select_minimal_patterns(self.patterns, sample_class)

	def get_best_patterns(self, sample_class: str, support_class):
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
		mutants_patterns = RIPMineOutput.remap_keys_patterns(self.patterns, MUT_SAMPLE_CLASS)
		best_patterns = dict()
		for mutant, patterns in mutants_patterns.items():
			mutant: jcmuta.Mutant
			best_pattern = RIPMineOutput.select_best_pattern(patterns, sample_class, support_class)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[mutant] = best_pattern
		return best_patterns

