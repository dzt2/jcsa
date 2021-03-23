"""
This file defines the data model for describing patterns of symbolic executions being mined.
"""


import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


UR_CLASS = "UR"		# testing that failed to reach mutation
UI_CLASS = "UI"		# testing that reach but fail to infect
UP_CLASS = "UP"		# testing that infect but fail to kills
KI_CLASS = "KI"		# testing that fail to kill the mutants


class RIPClassifier:
	"""
	It implements the estimation, classification and evaluation on selected samples (either SymExecution or Mutant)
	in form of reachability, infection and propagation during testing and fault detection procedure.
	"""

	def __init__(self, used_tests, support_classes):
		"""
		:param used_tests:
					(1) collection of test cases or their unique integer IDs, in which we assume that the
						mutants are executed against the given set of test cases.
					(2) if established as None, we consider the mutants are executed against the entire
						test inputs defined in the space.
		:param support_classes:
					The collection of classes of samples taken as supporting of patterns
		"""
		self.__tests__ = used_tests
		self.__all_classes__ = { UR_CLASS, UI_CLASS, UP_CLASS, KI_CLASS }
		self.__sup_classes__ = set()
		for class_name in support_classes:
			class_name: str
			if class_name in self.__all_classes__:
				self.__sup_classes__.add(class_name)
		self.__solutions__ = dict()
		self.__solutions__[-1] = KI_CLASS
		return

	def __solving__(self, mutant: jcmuta.Mutant):
		"""
		:param mutant: the mutant being classified
		:return:	UR_CLASS: if the mutant cannot be reached by any test in self.__tests__
					UI_CLASS: if the mutant is reachable but can never be infected by test in self.__tests__
					UP_CLASS: if the mutant is infected but cannot be killed by any test in self.__tests__
					KI_CLASS: if the mutant is killed by any test in self.__tests__
		"""
		key = mutant.get_muta_id()
		if not(key in self.__solutions__):
			c_result = mutant.get_c_mutant().get_result()
			w_result = mutant.get_w_mutant().get_result()
			s_result = mutant.get_result()
			if s_result.is_killed_in(self.__tests__):
				label = KI_CLASS			# mutant is killed by the tests
			elif w_result.is_killed_in(self.__tests__):
				label = UP_CLASS			# mutant is not killed but infected by the tests
			elif c_result.is_killed_in(self.__tests__):
				label = UI_CLASS			# mutant is not infected but reached by tests
			else:
				label = UR_CLASS			# mutant is not reached by the tests
			self.__solutions__[key] = label
		label = self.__solutions__[key]
		label: str
		return label

	def __classify__(self, sample):
		"""
		:param sample: either Mutant or SymExecution
		:return: 	UR_CLASS: if the mutant cannot be reached by any test in self.__tests__
					UI_CLASS: if the mutant is reachable but can never be infected by test in self.__tests__
					UP_CLASS: if the mutant is infected but cannot be killed by any test in self.__tests__
					KI_CLASS: if the mutant is killed by any test in self.__tests__
		"""
		if isinstance(sample, jctest.SymExecution):
			sample: jctest.SymExecution
			return self.__solving__(sample.get_mutant())
		else:
			sample: jcmuta.Mutant
			return self.__solving__(sample)

	def classify(self, samples):
		"""
		:param samples: the collection of Mutant or SymExecution to be classified according to RIP framework
		:return: the mapping from UR_CLASS|UI_CLASS|UP_CLASS|KI_CLASS ==> set{sample} it corresponding to
		"""
		solutions = dict()
		for class_name in self.__all_classes__:
			solutions[class_name] = set()
		for sample in samples:
			label = self.__classify__(sample)
			solutions[label].add(sample)
		return solutions

	def counting(self, samples):
		"""
		:param samples: the collection of Mutant or SymExecution to be counted according to RIP framework
		:return: 	ur, ui, up, ki
					ur --- the number of samples that cannot be reached by the tests
					ui --- the number of samples that cannot be infected but reached by the tests
					up --- the number of samples that cannot be killed but infected by the tests
					ki --- the number of samples that can be killed by the tests
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
		return ur, ui, up, ki

	def estimate(self, samples):
		"""
		:param samples: samples: the collection of Mutant or SymExecution to be estimated according to RIP framework
						as well as the support-confidence framework of data mining.
		:return: 	total, support, confidence
					total	--- the total number of samples under analysis
					support	---	the number of samples of which class is in supporting
					confidence --- support / total * 100%
		"""
		total, support = 0, 0
		for sample in samples:
			total += 1
			label = self.__classify__(sample)
			if label in self.__sup_classes__:
				support += 1
		confidence = 0.0
		if support > 0:
			confidence = support / total
		return total, support, confidence

	def division(self, samples):
		"""
		:param samples: the collection of Mutant or SymExecution to be selected according to RIP framework
		:return: supports, negatives
		"""
		supports, negatives = set(), set()
		for sample in samples:
			label = self.__classify__(sample)
			if label in self.__sup_classes__:
				supports.add(sample)
			else:
				negatives.add(sample)
		return supports, negatives


class RIPPattern:
	"""
	The pattern of mutation kill-ability in terms of symbolic conditions required in process and
	estimated against the reachability-infection-propagation framework.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		"""
		:param document: it provides original data samples for being matched
		:param classifier: it is used to estimate the performance of this one
		"""
		self.document = document		# it provides original data samples for being matched
		self.classifier = classifier	# it is used to estimate the performance of this one
		self.executions = set()			# the set of SymExecution(s) matching with this one
		self.mutants = set()			# the set of Mutant(s) of which execution(s) match with this one
		self.words = list()				# the sorted sequence of words encoding the symbolic conditions included
		return

	# data samples

	def get_document(self):
		"""
		:return: it provides original data samples for being matched
		"""
		return self.document

	def get_executions(self):
		"""
		:return: it is used to estimate the performance of this one
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of Mutant(s) of which execution(s) match with this one
		"""
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: True to select executions or False for mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def __matching__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return: True if all the conditions among the pattern are required by execution
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: RIPPattern as direct pattern or None to update on entire document
		:return:
		"""
		if parent is None:
			executions = self.document.get_executions()
		else:
			parent: RIPPattern
			executions = parent.get_executions()
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jctest.SymExecution
			if self.__matching__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	# estimations

	def get_classifier(self):
		return self.classifier

	def classify(self, exe_or_mut: bool):
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def counting(self, exe_or_mut: bool):
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def division(self, exe_or_mut: bool):
		return self.classifier.division(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool):
		return self.classifier.estimate(self.get_samples(exe_or_mut))

	# feature model

	def get_words(self):
		return self.words

	def get_conditions(self):
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_conditions_lib().get_condition(word))
		return conditions

	def __str__(self):
		return str(self.words)

	def __len__(self):
		return len(self.words)

	# comparison

	def extends(self, word: str):
		"""
		:param word:
		:return: child pattern extended from this one by adding one new word
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
		:param pattern:
		:param strict: True to compute strict subsumption
		:return: True if all the executions of target pattern are included by this one
		"""
		pattern: RIPPattern
		for execution in pattern.executions:
			if not(execution in self.executions):
				return False
		if strict:
			return len(self.executions) > len(pattern.executions)
		else:
			return True


class RIPMineInputs:
	"""
	It defines metrics for generating good patterns and interfaces to ensure the uniqueness
	"""

	def __init__(self, document: jctest.CDocument, used_tests, support_classes,
				 exe_or_mut: bool, min_support: int, min_confidence: float,
				 max_confidence: float, max_length: int):
		"""
		:param document: it provides original dataset for data mining
		:param used_tests: the set of test cases assumed being used
		:param support_classes: set of names of supporting classes
		:param exe_or_mut: True to take executions or mutants as samples
		:param min_support: minimal number of samples to support good patterns
		:param min_confidence: minimal confidence to confide the good patterns
		:param max_confidence: maximal confidence to terminate the data mining
		:param max_length: the maximal length of words allowed to be included
		"""
		self.document = document
		self.classifier = RIPClassifier(used_tests, support_classes)
		self.exe_or_mut = exe_or_mut
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.unique_patterns = dict()	# String ==> RIPPattern
		return

	def get_document(self):
		return self.document

	def get_classifier(self):
		return self.classifier

	def is_exe_or_mut(self):
		return self.exe_or_mut

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_max_length(self):
		return self.max_length

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: pattern from which the new one is extended or None for root
		:param word:
		:return:
		"""
		if parent is None:
			parent = RIPPattern(self.document, self.classifier)
		else:
			parent: RIPPattern
		child = parent.extends(word)
		if not(str(child) in self.unique_patterns):
			self.unique_patterns[str(child)] = child
			child.set_samples(parent)
		child = self.unique_patterns[str(child)]
		child: RIPPattern
		return child

	def get_root(self, word: str):
		return self.__new_pattern__(None, word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)

	def get_all_patterns(self):
		return self.unique_patterns.values()


class RIPPatternSelection:
	"""
	It implements selection on RIPPattern(s)
	"""

	@staticmethod
	def select_good_patterns(patterns, exe_or_mut: bool, max_length: int, min_support: int, min_confidence: float):
		"""
		:param patterns: the set of patterns from which the goods are found
		:param exe_or_mut: True to take executions or mutants as samples to estimate
		:param max_length: the maximal length of words allowed be included
		:param min_support: the minimal support of samples that support good patterns
		:param min_confidence: minimal confidence required for good patterns
		:return: the collection of 'good' patterns selected from input set
		"""
		good_patterns = set()
		for pattern in patterns:
			pattern: RIPPattern
			length = len(pattern)
			total, support, confidence = pattern.estimate(exe_or_mut)
			if length <= max_length and support >= min_support and confidence >= min_confidence:
				good_patterns.add(pattern)
		return good_patterns

	@staticmethod
	def select_subsuming_patterns(patterns, strict: bool):
		"""
		:param patterns:
		:param strict: True to select minimal set of patterns that strictly subsume the other removed
		:return: the minimal set of patterns that subsume all the others that are removed from.
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
	def select_coverage_patterns(patterns, exe_or_mut: bool):
		"""
		:param patterns:
		:param exe_or_mut: True to take executions or mutants as coverage samples
		:return: the minimal subset of patterns randomly selected from inputs until it covers all the samples
		"""
		# initialization
		remain_samples, remain_pattern = set(), set()
		for pattern in patterns:
			pattern: RIPPattern
			remain_pattern.add(pattern)
			for sample in pattern.get_samples(exe_or_mut):
				remain_samples.add(sample)
		cover_patterns, remove_patterns = set(), set()

		# selection-parts
		while len(remain_samples) > 0:
			# 1. select a random next pattern from remaining
			next_pattern = jcbase.rand_select(remain_pattern)
			next_pattern: RIPPattern
			cover_patterns.add(next_pattern)

			# 2. remove all the samples matched by selected
			for sample in next_pattern.get_samples(exe_or_mut):
				if sample in remain_samples:
					remain_samples.remove(sample)

			# 3. remove useless patterns from remaining set
			remove_patterns.clear()
			for pattern in remain_pattern:
				has_samples = len(pattern.get_samples(exe_or_mut) & remain_samples) > 0
				if not has_samples:
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_pattern.remove(pattern)

		return cover_patterns

	@staticmethod
	def remap_keys_patterns(patterns, exe_or_mut: bool):
		"""
		:param patterns:
		:param exe_or_mut: True to use RIPExecution or Mutant as key
		:return: Sample ==> set of RIPPattern
		"""
		results = dict()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(exe_or_mut)
			for sample in samples:
				if not (sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def select_best_pattern(patterns, exe_or_mut: bool):
		"""
		:param patterns:
		:param exe_or_mut:
		:return: the best pattern with best performance in given set.
		"""
		remain_patterns, solutions = set(), dict()
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
			total, support, confidence = pattern.estimate(exe_or_mut)
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


class RIPMineOutput:
	"""
	It preserves the set of generated 'good' patterns and provide their subset based on
	subsumption, coverage, best-matching ways.
	"""

	def __init__(self, inputs: RIPMineInputs):
		self.document = inputs.get_document()
		self.classifier = inputs.get_classifier()
		self.doc_executions = set()
		self.doc_mutants = set()
		for execution in self.document.get_executions():
			execution: jctest.SymExecution
			self.doc_executions.add(execution)
			self.doc_mutants.add(execution.get_mutant())
		self.all_patterns = RIPPatternSelection.select_good_patterns(inputs.get_all_patterns(),
																	 inputs.is_exe_or_mut(),
																	 inputs.get_max_length(),
																	 inputs.get_min_support(),
																	 inputs.get_min_confidence())
		self.pat_executions = set()
		self.pat_mutants = set()
		for pattern in self.all_patterns:
			for execution in pattern.get_executions():
				execution: jctest.SymExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
		return

	def get_document(self):
		return self.document

	def get_doc_executions(self):
		return self.doc_executions

	def get_doc_mutants(self):
		return self.doc_mutants

	def get_classifier(self):
		return self.classifier

	def get_patterns(self):
		return self.all_patterns

	def get_pat_executions(self):
		return self.pat_executions

	def get_pat_mutants(self):
		return self.pat_mutants

	def get_subsuming_patterns(self, strict: bool):
		return RIPPatternSelection.select_subsuming_patterns(self.all_patterns, strict)

	def get_patterns_on_executions(self):
		"""
		:return: the set of good patterns that cover all the executions included.
		"""
		return RIPPatternSelection.select_coverage_patterns(self.all_patterns, True)

	def get_patterns_on_mutants(self):
		"""
		:return: the set of good patterns that cover all the mutants included.
		"""
		return RIPPatternSelection.select_coverage_patterns(self.all_patterns, False)

	def get_executions_and_best_patterns(self):
		"""
		:return: mapping from execution to the pattern that best matches with the mutant
		"""
		exec_patterns = RIPPatternSelection.remap_keys_patterns(self.all_patterns, False)
		best_patterns = dict()
		for execution, patterns in exec_patterns.items():
			execution: jctest.SymExecution
			best_pattern = RIPPatternSelection.select_best_pattern(patterns, True)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[execution] = best_pattern
		return best_patterns

	def get_mutants_and_best_patterns(self):
		"""
		:return: mapping from mutant to the pattern that best matches with the mutant
		"""
		mutants_patterns = RIPPatternSelection.remap_keys_patterns(self.all_patterns, False)
		best_patterns = dict()
		for mutant, patterns in mutants_patterns.items():
			mutant: jcmuta.Mutant
			best_pattern = RIPPatternSelection.select_best_pattern(patterns, True)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[mutant] = best_pattern
		return best_patterns

