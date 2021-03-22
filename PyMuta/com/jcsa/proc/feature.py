from typing import TextIO
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


NR_CLASS = "NR"			# testing that fails to reach the mutation
NI_CLASS = "NI"			# testing that fails to infect but reaches
NP_CLASS = "NP"			# testing that fails to kill but infect it
KI_CLASS = "KI"			# testing that manages to kill the mutants


class RIPClassifier:
	"""
	It is used to classify and evaluate performance of patterns in form of RIP framework.
	"""

	def __init__(self, tests):
		"""
		:param tests: the collection of test cases used in execution
		"""
		self.tests = tests
		self.solutions = dict()		# mid --> {int, int, int, int}
		return

	def __counting__(self, sample):
		"""
		:param sample: either SymExecution or Mutant
		:return: 	nr, ni, np, ki
					nr: the number of testing that fail to reach the mutation
					ni: the number of testing that reach but fail to infect
					np: the number of testing that infect but fail to kill it
					ki: the number of testing that kill the target mutant
		"""
		if isinstance(sample, jcmuta.Mutant):
			sample: jcmuta.Mutant
			mutant = sample
		else:
			sample: jctest.SymExecution
			mutant = sample.get_mutant()
		if not(mutant.get_muta_id() in self.solutions):
			nr, ni, np, ki = 0, 0, 0, 0
			s_result = mutant.get_result()
			w_result = mutant.get_w_mutant().get_result()
			c_result = mutant.get_c_mutant().get_result()
			if s_result.is_killed_in(self.tests):
				ki += 1
			elif w_result.is_killed_in(self.tests):
				np += 1
			elif c_result.is_killed_in(self.tests):
				ni += 1
			else:
				nr += 1
			self.solutions[mutant.get_muta_id()] = (nr, ni, np, ki)
		solution = self.solutions[mutant.get_muta_id()]
		nr = solution[0]
		ni = solution[1]
		np = solution[2]
		ki = solution[3]
		nr: int
		ni: int
		np: int
		ki: int
		return nr, ni, np, ki

	def __classify__(self, sample):
		"""
		:param sample: SymExecution or Mutant
		:return:	NR -- the testing fail to reach the mutant
					NI -- the testing reach but fail to infect
					NP -- the testing infect but fail to kill
					KI -- the testing managed to kill the mutant
		"""
		nr, ni, np, ki = self.__counting__(sample)
		if ki > 0:
			return KI_CLASS
		elif np > 0:
			return NP_CLASS
		elif ni > 0:
			return NI_CLASS
		else:
			return NR_CLASS

	def counting(self, samples):
		"""
		:param samples: the collection of mutants or executions
		:return: nr, ni, np, ki, uk, cc
					(1) nr: the number of testing that fail to reach the mutant
					(2) ni: the number of testing that reach but fail to infect
					(3) np: the number of testing that infect but fail to kill
					(4) ki: the number of testing to kill
					(5) uk: the number of testing that fail to kill
					(6) cc: the number of testing that reach but fail to kill
		"""
		nr, ni, np, ki = 0, 0, 0, 0
		for sample in samples:
			lnr, lni, lnp, lki = self.__counting__(sample)
			nr += lnr
			ni += lni
			np += lnp
			ki += lki
		return nr, ni, np, ki, nr + ni + np, ni + np

	def classify(self, samples):
		"""
		:param samples: the collection of mutants or executions
		:return: Mapping from class name to the samples it match with
		"""
		results = dict()
		results[NR_CLASS] = set()
		results[NI_CLASS] = set()
		results[NP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			results[self.__classify__(sample)].add(sample)
		return results

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples: the collection of mutants or executions
		:param uk_or_cc: True to take non-killed or coincidental correct as support
		:return: total, support, confidence (0.0 -- 1.0)
		"""
		nr, ni, np, ki, uk, cc = self.counting(samples)
		if uk_or_cc:
			support = uk
		else:
			support = cc
		total = support + ki
		if support == 0:
			confidence = 0.0
		else:
			confidence = support / (total + 0.0)
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples: the collection of mutants or executions
		:param uk_or_cc: True to select non-killed or coincidental correct ones
		:return:
		"""
		results = self.classify(samples)
		selects = results[NI_CLASS] | results[NP_CLASS]
		if uk_or_cc:
			selects = selects | results[NR_CLASS]
		return selects


class RIPPattern:
	"""
	It defines a structural description of pattern of testing process in forms of RIP framework.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		"""
		:param document: it provides original dataset for analysis and mining
		:param classifier: it is used to estimate the performance of the pattern
		"""
		self.document = document
		self.classifier = classifier
		self.words = list()
		self.executions = set()
		self.mutants = set()
		return

	# data samples

	def get_document(self):
		return self.document

	def get_executions(self):
		return self.executions

	def get_mutants(self):
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	@staticmethod
	def __include__(execution: jctest.SymExecution, word: str):
		"""
		:param execution:
		:param word:
		:return: whether the word is included by any instance in the process
		"""
		for instance in execution.get_instances():
			instance: jctest.SymInstance
			if word in instance.get_words():
				return True
		return False

	def __matching__(self, execution: jctest.SymExecution):
		"""
		:param execution:
		:return: True if the execution matches with the pattern
		"""
		for word in self.words:
			if RIPPattern.__include__(execution, word):
				pass
			else:
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: either RIPPattern or None for entire document
		:return: update the data samples matched by this pattern under the parent context
		"""
		if parent is None:
			executions = self.document.get_processes()
		else:
			parent: RIPPattern
			executions = parent.executions
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

	def counting(self, exe_or_mut: bool):
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)

	# feature model

	def get_words(self):
		return self.words

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_conditions(self):
		conditions = list()
		for word in self.words:
			conditions.append(self.document.conditions.get_condition(word))
		return conditions

	# relationships between

	def extends(self, word: str):
		"""
		:param word: new word being added to the pattern
		:return: an empty child pattern extended from this one by adding one word (samples are not updated)
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = RIPPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern, strict: bool):
		"""
		:param strict:
		:param pattern:
		:return: subsume, strict_subsume
		"""
		pattern: RIPPattern
		for execution in pattern.executions:
			if not(execution in self.executions):
				return False
		if strict:
			return len(self.executions) > len(pattern.executions)
		return True


class RIPPatterns:
	"""
	It provides factory interfaces to create patterns in RIP procedure
	"""

	def __init__(self, document: jctest.CDocument, tests):
		"""
		:param document:
		:param tests:
		"""
		self.document = document
		self.classifier = RIPClassifier(tests)
		self.patterns = dict()
		return

	def get_document(self):
		return self.document

	def get_classifier(self):
		return self.classifier

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: None for root pattern or child pattern from parent with one new word
		:param word:
		:return: unique pattern extended from parent with adding one new word
		"""
		if parent is None:
			pattern = RIPPattern(self.document, self.classifier)
			pattern = pattern.extends(word)
		else:
			parent: RIPPattern
			pattern = parent.extends(word)
		if not(str(pattern) in self.patterns):
			pattern.set_samples(parent)
			self.patterns[str(pattern)] = pattern
		pattern = self.patterns[str(pattern)]
		pattern: RIPPattern
		return pattern

	def get_root(self, word: str):
		return self.__new_pattern__(None, word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)

	def get_patterns(self):
		return self.patterns.values()


class RIPMineContext:
	"""
	It provides the contextual information used for pattern mining and interfaces to produce unique instance of
	RIP-patterns in project under test.
	"""

	def __init__(self, document: jctest.CDocument, exe_or_mut: bool, uk_or_cc: bool,
				 min_support: int, min_confidence: float, max_confidence: float,
				 max_length: int, tests):
		"""
		:param document: it provides the basic information for evaluating pattern mining
		:param exe_or_mut: True to take executions as samples or mutants for estimations
		:param uk_or_cc: True to take non-killed samples as support or coincidental correct
		:param min_support: minimal number of supports needed to generate patterns
		:param min_confidence: minimal confidence to select the better patterns generated
		:param max_confidence: maximal confidence to terminate the pattern generations
		:param max_length: maximal length of features in generated patterns
		:param tests: used to count supports in executions or mutants by classifier
		"""
		self.patterns = RIPPatterns(document, tests)
		self.solution = dict()
		# parameters
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.max_length = max_length
		return

	# getters

	def get_document(self):
		return self.patterns.get_document()

	def get_classifier(self):
		return self.patterns.get_classifier()

	def is_exe_or_mut(self):
		return self.exe_or_mut

	def is_uk_or_cc(self):
		return self.uk_or_cc

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_max_length(self):
		return self.max_length

	# factory

	def get_patterns(self):
		return self.patterns

	def get_root(self, word: str):
		return self.patterns.get_root(word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.patterns.get_child(parent, word)

	def estimate(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: total, support, confidence
		"""
		if not(pattern in self.solution):
			total, support, confidence = pattern.estimate(self.exe_or_mut, self.uk_or_cc)
			self.solution[pattern] = (total, support, confidence)
		solution = self.solution[pattern]
		total = solution[0]
		support = solution[1]
		confidence = solution[2]
		total: int
		support: int
		confidence: float
		return total, support, confidence

	def extract_good_patterns(self, patterns=None):
		"""
		:return: the set of patterns available with the parameters in the context
		"""
		good_patterns = set()
		if patterns is None:
			for pattern in self.patterns.get_patterns():
				pattern: RIPPattern
				total, support, confidence = self.estimate(pattern)
				length = len(pattern)
				if length <= self.max_length and support >= self.min_support and confidence >= self.min_confidence:
					good_patterns.add(pattern)
		else:
			for pattern in patterns:
				pattern: RIPPattern
				total, support, confidence = self.estimate(pattern)
				length = len(pattern)
				if length <= self.max_length and support >= self.min_support and confidence >= self.min_confidence:
					good_patterns.add(pattern)
		return good_patterns


class RIPPatternSpace:
	"""
	The space preserves the patterns generated and selected from mining algorithms.
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier, good_patterns):
		"""
		:param document: it provides original data samples for being classified and mined
		:param classifier: used to estimate the performance of generated RIP-patterns
		:param good_patterns: set of RIP-testability pattern being generated from program
		"""
		self.document = document
		self.doc_executions = set()
		self.doc_mutants = set()
		for execution in document.get_processes():
			execution: jctest.SymExecution
			self.doc_executions.add(execution)
			self.doc_mutants.add(execution.get_mutant())
		self.classifier = classifier
		self.all_patterns = set()
		self.pat_executions = set()
		self.pat_mutants = set()
		for pattern in good_patterns:
			pattern: RIPPattern
			self.all_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jctest.SymExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
		return

	# data getters

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

	# selecting methods

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
	def select_best_pattern(patterns, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param patterns:
		:param exe_or_mut:
		:param uk_or_cc:
		:return:
		"""
		remain_patterns, solutions = set(), dict()
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
			total, support, confidence = pattern.estimate(exe_or_mut, uk_or_cc)
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
	def select_minimal_patterns(patterns, exe_or_mut: bool):
		"""
		:param patterns:
		:param exe_or_mut: True to cover RIPExecution or Mutant
		:return: minimal set of patterns covering all the executions in the set
		"""
		keys_patterns = RIPPatternSpace.remap_keys_patterns(patterns, exe_or_mut)
		minimal_patterns, removed_keys = set(), set()
		while len(keys_patterns) > 0:
			removed_keys.clear()
			for sample, patterns in keys_patterns.items():
				selected_pattern = jcbase.rand_select(patterns)
				if not (selected_pattern is None):
					pattern = selected_pattern
					pattern: RIPPattern
					for pat_sample in pattern.get_samples(exe_or_mut):
						removed_keys.add(pat_sample)
					minimal_patterns.add(pattern)
					break
			for sample in removed_keys:
				if sample in keys_patterns:
					keys_patterns.pop(sample)
		return minimal_patterns

	# data inference

	def get_subsuming_patterns(self, strict: bool):
		"""
		:return:
		"""
		return RIPPatternSpace.select_subsuming_patterns(self.all_patterns, strict)

	def get_minimal_patterns(self, exe_or_mut: bool):
		"""
		:param exe_or_mut:
		:return: the minimal set of RIP patterns covering all the samples in the space
		"""
		return RIPPatternSpace.select_minimal_patterns(self.all_patterns, exe_or_mut)

	def get_best_patterns(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: used to estimate
		:param uk_or_cc: used to estimate
		:return: mapping from mutant to the pattern that best matches with the mutant
		"""
		mutants_patterns = RIPPatternSpace.remap_keys_patterns(self.all_patterns, False)
		best_patterns = dict()
		for mutant, patterns in mutants_patterns.items():
			mutant: jcmuta.Mutant
			best_pattern = RIPPatternSpace.select_best_pattern(patterns, exe_or_mut, uk_or_cc)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[mutant] = best_pattern
		return best_patterns


class RIPPatternWriter:
	"""
	It implements the writing of RIP conditions patterns.
	"""

	def __init__(self):
		self.writer = None
		return

	def output(self, text: str):
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
			ratio = x / (y + 0.0)
		return RIPPatternWriter.__percentage__(ratio)

	@staticmethod
	def __f1_measure__(doc_samples: set, pat_samples: set):
		"""
		:param doc_samples: samples from document
		:param pat_samples: samples matched with pattern
		:return: precision, recall, f1_score
		"""
		int_samples = doc_samples & pat_samples
		common = len(int_samples)
		if common > 0:
			precision = common / len(pat_samples)
			recall = common / len(doc_samples)
			f1_score = 2 * precision * recall / (precision + recall)
		else:
			precision = 0.0
			recall = 0.0
			f1_score = 0.0
		return precision, recall, f1_score

	# pattern writing

	def __write_pattern_summary__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return:
				Summary 	Length Executions Mutations
				Counting	title UR UI UP KI UK CC
				Estimate	title total support confidence
		"""
		# Summary Length Executions Mutants
		length = len(pattern)
		executions = len(pattern.get_executions())
		mutants = len(pattern.get_mutants())
		self.output("\t{}\t{}: {}\t{}: {}\t{}: {}\n".format("Summary",
															"Length", length,
															"Executions", executions,
															"Mutants", mutants))
		self.output("\n")

		# Counting title UR UI UP KI UK CC
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Counting", "Title", "UR", "UI", "UP", "KI", "UK", "CC"))
		ur, ui, up, ki, uk, cc = pattern.counting(True)
		self.output(template.format("", "Executions", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(False)
		self.output(template.format("", "Mutants", ur, ui, up, ki, uk, cc))
		self.output("\n")

		# Estimate title total support confidence(%)
		template = "\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Estimate", "Title", "Total", "Support", "Confidence (%)"))
		total, support, confidence = pattern.estimate(True, True)
		self.output(template.format("", "UK_Executions", total, support, confidence))
		total, support, confidence = pattern.estimate(True, False)
		self.output(template.format("", "CC_Executions", total, support, confidence))
		total, support, confidence = pattern.estimate(False, True)
		self.output(template.format("", "UK_Mutants", total, support, confidence))
		total, support, confidence = pattern.estimate(False, False)
		self.output(template.format("", "CC_Mutants", total, support, confidence))
		self.output("\n")

		return

	def __write_pattern_feature__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: condition category operator validate execution statement location parameter
		"""
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("Condition", "Category", "Operator", "Validate",
									"Execution", "Statement", "Location", "Parameter"))
		index = 0
		for feature in pattern.get_conditions():
			index += 1
			category = feature.get_category()
			operator = feature.get_operator()
			execution = feature.get_execution()
			statement = feature.get_execution().get_statement().get_cir_code()
			location = feature.get_location().get_cir_code()
			if feature.get_parameter() is None:
				parameter = ""
			else:
				parameter = feature.get_parameter().get_code()
			self.output(template.format(index, category, operator, True,
										execution, statement, location, parameter))
		self.output("\n")

	def __write_pattern_mutants__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: Mutant Result Class Operator Line Location Parameter
		"""
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("ID", "Result", "Class", "Operator", "Line", "Location", "Parameter"))
		for mutant in pattern.get_mutants():
			mutant: jcmuta.Mutant
			mutant_id = mutant.get_muta_id()
			result = pattern.get_classifier().__classify__(mutant)
			mutation_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.mutation.get_mutation_operator()
			location = mutant.mutation.get_location()
			parameter = mutant.mutation.get_parameter()
			line = location.line_of(False)
			code = location.get_code(True)
			self.output(template.format(mutant_id, result, mutation_class, operator, line, code, parameter))
		self.output("\n")

	def __write_pattern__(self, pattern: RIPPattern):
		self.output("#BEG\n")
		self.__write_pattern_summary__(pattern)
		self.__write_pattern_feature__(pattern)
		self.__write_pattern_mutants__(pattern)
		self.output("#END\n")

	def write_patterns(self, patterns, file_path: str):
		with open(file_path, 'w') as writer:
			self.writer = writer
			for pattern in patterns:
				self.__write_pattern__(pattern)
				self.writer.write("\n")
		return

	def write_matching(self, space: RIPPatternSpace, file_path: str, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param space:
		:param uk_or_cc:
		:param exe_or_mut:
		:param file_path:
		:return: 	Mutant 	ID RESULT CLASS OPERATOR LINE LOCATION PARMETER
					Pattern
					Category Operator Validate Execution Statement Location Parameter*
		"""
		mutants_patterns = space.get_best_patterns(exe_or_mut, uk_or_cc)
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant, pattern in mutants_patterns.items():
				mutant_id = mutant.get_muta_id()
				result = pattern.get_classifier().__classify__(mutant)
				mutation_class = mutant.get_mutation().get_mutation_class()
				operator = mutant.get_mutation().get_mutation_operator()
				location = mutant.get_mutation().get_location()
				parameter = mutant.get_mutation().get_parameter()
				line = location.line_of(False)
				code = location.get_code(True)
				self.output("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Mutant", mutant_id, result,
																	  mutation_class, operator, line, code, parameter))
				self.__write_pattern_feature__(pattern)
				self.output("\n")
		return

	@staticmethod
	def __evaluate__(document: jctest.CDocument, patterns, exe_or_mut: bool, uk_or_cc: bool,
					 classifier: RIPClassifier):
		"""
		:param document:
		:param patterns:
		:return: length doc_samples pat_samples reduce precision recall f1_score
		"""
		length = len(patterns)
		if exe_or_mut:
			doc_samples = classifier.select(document.get_processes(), uk_or_cc)
		else:
			doc_samples = classifier.select(document.get_mutants(), uk_or_cc)
		pat_samples = set()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(exe_or_mut)
			for sample in samples:
				pat_samples.add(sample)
		reduce = length / (len(doc_samples) + 0.0)
		precision, recall, f1_score = RIPPatternWriter.__f1_measure__(doc_samples, pat_samples)
		return length, len(doc_samples), len(pat_samples), reduce, precision, recall, f1_score

	def __write_evaluate_all__(self, space: RIPPatternSpace):
		document = space.get_document()
		patterns = space.get_subsuming_patterns(False)
		classifier = space.get_classifier()

		self.output("# Cost-Effective Analysis #\n")
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("title", "LEN", "DOC", "PAT", "REDUCE(%)", "PRECISION(%)", "RECALL(%)", "F1_SCORE"))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, True, True, classifier)
		self.output(template.format("UK_EXE", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, True, False, classifier)
		self.output(template.format("CC_EXE", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, False, True, classifier)
		self.output(template.format("UK_MUT", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPPatternWriter.__evaluate__(document, patterns, False, False, classifier)
		self.output(template.format("CC_MUT", length, doc_number, pat_number,
									RIPPatternWriter.__percentage__(reduce_rate),
									RIPPatternWriter.__percentage__(precision),
									RIPPatternWriter.__percentage__(recall),
									f1_score))

		self.output("\n")
		return

	def __write_evaluate_one__(self, index: int, pattern: RIPPattern):
		"""
		:param pattern:
		:return: index length executions mutants uk_exe_supp uk_exe_conf cc_exe_supp cc_exe_conf uk_mut_supp
				uk_mut_conf cc_mut_supp cc_mut_conf
		"""
		executions = len(pattern.get_executions())
		mutants = len(pattern.get_mutants())
		_, uk_exe_supp, uk_exe_conf = pattern.estimate(True, True)
		_, cc_exe_supp, cc_exe_conf = pattern.estimate(True, False)
		_, uk_mut_supp, uk_mut_conf = pattern.estimate(False, True)
		_, cc_mut_supp, cc_mut_conf = pattern.estimate(False, False)
		self.output("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, executions, mutants,
																			uk_exe_supp,
																			RIPPatternWriter.__percentage__(uk_exe_conf),
																			cc_exe_supp,
																			RIPPatternWriter.__percentage__(cc_exe_conf),
																			uk_mut_supp,
																			RIPPatternWriter.__percentage__(uk_mut_conf),
																			cc_mut_supp,
																			RIPPatternWriter.__percentage__(cc_mut_conf)
																			)
					)
		return

	def write_evaluate(self, space: RIPPatternSpace, file_path: str):
		"""
		:param space:
		:param file_path:
		:return:
			# Cost-Effective Analysis
			title	LEN DOC PAT REDUCE(%) PRECISION(%) RECALL(%) F1_SCORE
			UK_EXE
			CC_EXE
			UK_MUT
			CC_MUT
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			self.__write_evaluate_all__(space)
			self.output("# Pattern Evaluate #\n")
			self.output("\tindex\tlength\texecutions\tmutants\tuk_exe_supp\tuk_exe_conf(%)\tcc_exe_supp\tcc_exe_conf(%)"
						"\tuk_mut_supp\tuk_mut_conf(%)\tcc_mut_supp\tcc_mut_conf(%)\n")
			index = 0
			for pattern in space.get_subsuming_patterns(False):
				index += 1
				self.__write_evaluate_one__(index, pattern)
		return

