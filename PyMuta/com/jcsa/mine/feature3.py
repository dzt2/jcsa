"""This file defines the structural model of inputs and output around the mining algorithms."""


import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


# the definition of label to categorize the mutation test result according to the step of detection process.


UR_CLASS = "UR"					# the used test suite failed to reach the target mutation
UI_CLASS = "UI"					# the used test suite reached but failed to infect mutant
UP_CLASS = "UP"					# the used test suite infect but fails to kill the mutant
KI_CLASS = "KI"					# the used test suite manages to detect the target mutant


# the definition of sample_class used in RIPPattern.get_samples


MU_SAMPLE_CLASS = None			# select Mutant as sample for being returned
SQ_SAMPLE_CLASS = True			# select SymSequence as sample for returning
EX_SAMPLE_CLASS = False			# select SymExecution as sample to be return


# the methods for determining whether a sample w.r.t. given label can be counted or supported {label --> bool, bool}


def unk_counted_support(label: str):
	"""
	:param label: UR, UI, UP, KI
	:return: counted, support
				1. counted: whether the sample w.r.t. given label is counted in total
				2. support: whether the sample w.r.t. given label is counted in support
	"""
	if label == KI_CLASS:
		return True, False
	elif label == UR_CLASS or label == UI_CLASS or label == UP_CLASS:
		return True, True
	else:
		return False, False


def wcc_counted_support(label: str):
	"""
	:param label: UR, UI, UP, KI
	:return: counted, support
				1. counted: whether the sample w.r.t. given label is counted in total
				2. support: whether the sample w.r.t. given label is counted in support
	"""
	if label == UR_CLASS:
		return False, False
	elif label == UI_CLASS or label == UP_CLASS:
		return True, True
	elif label == KI_CLASS:
		return True, False
	else:
		return False, False


def scc_counted_support(label: str):
	"""
	:param label: UR, UI, UP, KI
	:return: counted, support
				1. counted: whether the sample w.r.t. given label is counted in total
				2. support: whether the sample w.r.t. given label is counted in support
	"""
	if label == UR_CLASS or label == UI_CLASS:
		return False, False
	elif label == UP_CLASS:
		return True, True
	elif label == KI_CLASS:
		return True, False
	else:
		return False, False


def kid_counted_support(label: str):
	"""
	:param label: UR, UI, UP, KI
	:return: counted, support
				1. counted: whether the sample w.r.t. given label is counted in total
				2. support: whether the sample w.r.t. given label is counted in support
	"""
	if label == KI_CLASS:
		return True, True
	elif label == UR_CLASS or label == UI_CLASS or label == UP_CLASS:
		return True, False
	else:
		return False, False


# classifier to classify Mutant, SymSequence or SymExecution in form of reachability, infection, propagation (RIP).


class RIPClassifier:
	"""
	It is used to classify the result of (Mutant, SymSequence, SymExecution) against the used test suite
	according to where the detection achieved, in form of reachability, infection and propagation (RIP).
	"""

	def __init__(self, used_tests):
		"""
		:param used_tests:
				1. the collection of TestCase (or test_id) to decide the detection where it is achieved.
				2. None to represent the entire test suite being used during testing
		"""
		self.used_tests = used_tests
		self.solutions = dict()			# Mutant.muta_id ==> label
		return

	def __find__(self, sample):
		"""
		:param sample: Mutant, SymSequence, SymExecution
		:return: UR, UI, UP, KI to denote the execution stage of mutant against test suite
		"""
		# 1. obtain the mutant against test suite
		if isinstance(sample, jctest.SymExecution):
			mutant = sample.get_sequence().get_mutant()
		elif isinstance(sample, jctest.SymSequence):
			mutant = sample.get_mutant()
		else:
			sample: jcmuta.Mutant
			mutant = sample

		# 2. update the solution if mutant.muta_id does not exist
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

		# 3. obtain the class label w.r.t. mutant against tests
		label = self.solutions[mutant.get_muta_id()]
		label: str
		return label

	def classify(self, samples):
		"""
		:param samples: the collection of Mutant, SymSequence or SymExecution
		:return: Mapping from {UR, UI, UP, KI} to the set of samples w.r.t. the class
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
		:param samples: the collection of Mutant, SymSequence or SymExecution
		:return: 	ur, ui, up, ki, uk, cc
					1. ur: the number of samples that used tests fail to reach
					2. ui: the number of samples that used tests reach but fail to infect
					3. up: the number of samples that used tests infect but fail to kills
					4. ki: the number of samples that used tests managed to kill mutants
					5. uk: the number of samples that used tests fail to kill (ur + ui + up)
					6. cc: the number of samples that used tests fail to kill but reach (ui + up)
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
			elif label == UR_CLASS:
				ur += 1
		return ur, ui, up, ki, ur + ui + up, ui + up

	def estimate(self, samples, support_class):
		"""
		:param samples: the collection of Mutant, SymSequence or SymExecution
		:param support_class: (unk|wcc|scc|kid)_counted_support
		:return: total, support, confidence
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

	def select(self, samples, support_class):
		"""
		:param samples: the collection of Mutant, SymSequence or SymExecution
		:param support_class: (unk|wcc|scc|kid)_counted_support
		:return: the set of samples supporting the class
		"""
		selected_samples = set()
		for sample in samples:
			label = self.__find__(sample)
			counted, selected = support_class(label)
			if selected:
				selected_samples.add(sample)
		return selected_samples


# the data model of structural patterns that describe detect stage in form of reachability, infection and propagation.


class RIPPattern:
	"""
	It describes the pattern of mutants based on results according to the stage of detection levels.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		"""
		:param document:
		:param classifier:
		"""
		self.document = document		# It provides the original dataset for pattern mining.
		self.classifier = classifier	# It is used to evaluate the performance of this ones.
		self.executions = set()			# The set of SymExecution matching with this pattern.
		self.sequences = set()			# The set of SymSequences be matched to this pattern.
		self.mutants = set()			# The set of Mutant that are matched to this pattern.
		self.words = list()				# The sequence of words encoding conditions included.
		return

	# data samples

	def get_document(self):
		"""
		:return: It provides the original dataset for pattern mining.
		"""
		return self.document

	def get_executions(self):
		"""
		:return: The set of SymExecution matching with this pattern.
		"""
		return self.executions

	def get_sequences(self):
		"""
		:return: The set of SymSequences be matched to this pattern.
		"""
		return self.sequences

	def get_mutants(self):
		"""
		:return: The set of Mutant that are matched to this pattern.
		"""
		return self.mutants

	def get_samples(self, sample_class):
		"""
		:param sample_class:
				1. None --- to select Mutant as samples
				2. True --- to select SymSequence as samples
				3. False--- to select SymExecution as sample
		:return:
		"""
		if sample_class is None:
			return self.mutants
		elif sample_class:
			return self.sequences
		else:
			return self.executions

	def __match_exe__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return:
		"""
		for word in self.words:
			if word in execution.get_words():
				return True
		return False

	def __match_seq__(self, sequence: jctest.SymSequence):
		"""
		:param sequence:
		:return:
		"""
		for word in self.words:
			if not(word in sequence.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: parent pattern from which this one is directly extended or None under the root document
		:return:
		"""
		# 1. obtain the sequences for being matched
		if parent is None:
			sequences = self.document.get_sequences()
		else:
			parent: RIPPattern
			sequences = parent.get_sequences()

		# 2. clear the data samples in this pattern
		self.mutants.clear()
		self.sequences.clear()
		self.executions.clear()

		# 3. update the data samples by matching on
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

	# estimations

	def get_classifier(self):
		"""
		:return: It is used to evaluate the performance of this ones.
		"""
		return self.classifier

	def classify(self, sample_class):
		"""
		:param sample_class:
				1. None --- to select Mutant as samples
				2. True --- to select SymSequence as samples
				3. False--- to select SymExecution as sample
		:return: Mapping from {UR, UI, UP, KI} to the set of samples w.r.t. the class
		"""
		return self.classifier.classify(self.get_samples(sample_class))

	def counting(self, sample_class):
		"""
		:param sample_class:
				1. None --- to select Mutant as samples
				2. True --- to select SymSequence as samples
				3. False--- to select SymExecution as sample
		:return:	ur, ui, up, ki, uk, cc
					1. ur: the number of samples that used tests fail to reach
					2. ui: the number of samples that used tests reach but fail to infect
					3. up: the number of samples that used tests infect but fail to kills
					4. ki: the number of samples that used tests managed to kill mutants
					5. uk: the number of samples that used tests fail to kill (ur + ui + up)
					6. cc: the number of samples that used tests fail to kill but reach (ui + up)
		"""
		return self.classifier.counting(self.get_samples(sample_class))

	def estimate(self, sample_class, support_class):
		"""
		:param sample_class:
				1. None --- to select Mutant as samples
				2. True --- to select SymSequence as samples
				3. False--- to select SymExecution as sample
		:param support_class: (unk|wcc|scc|kid)_counted_support
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(sample_class), support_class)

	def select(self, sample_class, support_class):
		"""
		:param sample_class:
				1. None --- to select Mutant as samples
				2. True --- to select SymSequence as samples
				3. False--- to select SymExecution as sample
		:param support_class: (unk|wcc|scc|kid)_counted_support
		:return:
		"""
		return self.classifier.select(self.get_samples(sample_class), support_class)

	# feature model

	def get_words(self):
		"""
		:return: The sequence of words encoding conditions included.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: The sequence of words encoding conditions included.
		"""
		return self.document.get_conditions_lib().get_conditions(self.words)

	def __str__(self):
		return str(self.words)

	def __len__(self):
		return len(self.words)

	# relationships

	def extends(self, word: str):
		"""
		:param word: the word added to the direct child extended from this one
		:return: the child pattern extended from this one by adding a new word
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

	def subsume(self, pattern, sample_class, strict: bool):
		"""
		:param pattern:
		:param sample_class:
				1. None --- to select Mutant as samples
				2. True --- to select SymSequence as samples
				3. False--- to select SymExecution as sample
		:param strict:
				1. True --- return only for strict subsumption
				2. False--- return True for equivalence
		:return:
		"""
		# 1. obtain the samples of this pattern and target pattern
		pattern: RIPPattern
		source_samples = self.get_samples(sample_class)
		target_samples = pattern.get_samples(sample_class)

		# 2. subsumption determination
		if len(target_samples) > len(source_samples):
			return False
		else:
			for sample in target_samples:
				if not(sample in source_samples):
					return False

		# 3. strict determination or NOT
		if strict:
			return len(source_samples) > len(target_samples)
		else:
			return True


# the data model to represent the inputs data to the mining algorithm


class RIPMineInputs:
	"""
	It preserves the parameters used for pattern mining.
	"""

	def __init__(self, document: jctest.CDocument, used_tests, sample_class, support_class,
				 max_length: int, min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: 			It provides the original data for generating patterns
		:param used_tests: 			The collection of test cases used for deciding whether a mutant is killed or not
		:param sample_class: 		None --- take Mutant as samples for evaluation;
									True --- take SymSequences as samples for evaluation;
									False--- take SymExecution as samples for evaluation.
		:param support_class: 		function from class label (str) to {bool, bool} to decide how estimation is done
		:param max_length: 			The maximal length of generated patterns
		:param min_support: 		The minimal support required for good patterns
		:param min_confidence: 		The minimal confidence needed for good pattern
		:param max_confidence: 		The maximal confidence to stop searching on space
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


# the middle module for pattern mining to produce unique patterns and select good ones


class RIPMineMiddle:
	"""
	It preserves inputs parameters and unique patterns and estimation interface
	"""

	def __init__(self, inputs: RIPMineInputs):
		self.inputs = inputs		# The source inputs data samples and parameters used for mining.
		self.patterns = dict()		# Mapping from str to unique instance of RIPPattern
		self.solution = dict()		# Mapping from RIPPattern to [length, support, confidence]
		return

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_classifier(self):
		return self.inputs.get_classifier()

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: None to generate root pattern with one word or existing parent pattern
		:param word:
		:return: child pattern extended from parent by adding one new word
		"""
		# 1. create copy of child pattern
		if parent is None:
			child = RIPPattern(self.get_document(), self.get_classifier())
			child = child.extends(word)
		else:
			parent: RIPPattern
			child = parent.extends(word)

		# 2. update the pattern when it is first created into the module
		if not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_samples(parent)
			self.estimate(child)

		# 3. obtain the unique instance of child pattern from the module
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
		:return: the unique instance of the pattern in the space
		"""
		child = self.get_root("")
		for word in pattern.get_words():
			child = self.get_child(child, word)
		return child

	def estimate(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: length, support, confidence
		"""
		pattern = self.get_pattern(pattern)
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
		:return: the set of "good" patterns generated from the module
		"""
		good_patterns = set()
		for pattern, solution in self.solution.items():
			pattern: RIPPattern
			length = solution[0]
			support = solution[1]
			confidence = solution[2]
			if length <= self.inputs.get_max_length() and support >= self.inputs.get_min_support() and \
					confidence >= self.inputs.get_min_confidence():
				good_patterns.add(pattern)
		return good_patterns


# the output module for pattern mining to present the


class RIPMineOutput:
	"""
	It manages the generated patterns and corresponding samples for evaluation.
	"""

	def __init__(self, middle: RIPMineMiddle):
		"""
		:param middle:
		"""
		self.inputs = middle.inputs

		# 1. document dataset
		self.document = self.inputs.get_document()
		self.doc_executions = set()
		self.doc_sequences = set()
		self.doc_mutants = set()
		for sequence in self.document.get_sequences():
			sequence: jctest.SymSequence
			self.doc_sequences.add(sequence)
			self.doc_mutants.add(sequence.get_mutant())
			for execution in sequence.get_executions():
				execution: jctest.SymExecution
				self.doc_executions.add(execution)

		# 2. pattern dataset
		self.patterns = middle.extract_good_patterns()
		self.pat_executions = set()
		self.pat_sequences = set()
		self.pat_mutants = set()
		for pattern in self.patterns:
			for mutant in pattern.get_mutants():
				mutant: jcmuta.Mutant
				self.pat_mutants.add(mutant)
			for sequence in pattern.get_sequences():
				sequence: jctest.SymSequence
				self.pat_sequences.add(sequence)
			for execution in pattern.get_executions():
				execution: jctest.SymExecution
				self.pat_executions.add(execution)

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
		if sample_class is None:
			return self.doc_mutants
		elif sample_class:
			return self.doc_sequences
		else:
			return self.doc_executions

	def get_patterns(self):
		return self.patterns

	def get_pat_executions(self):
		return self.pat_executions

	def get_pat_sequences(self):
		return self.pat_sequences

	def get_pat_mutants(self):
		return self.pat_mutants

	def get_pat_samples(self, sample_class):
		if sample_class is None:
			return self.pat_mutants
		elif sample_class:
			return self.pat_sequences
		else:
			return self.pat_executions

	def get_classifier(self):
		return self.inputs.get_classifier()

	# selection methods

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
		return RIPMineOutput.select_subsuming_patterns(self.patterns, self.inputs.get_sample_class(), strict)

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

