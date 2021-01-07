"""
It implements the pattern mining algorithm to obtain patterns of equivalent mutation execution
"""


import os
from typing import TextIO
import com.jcsa.libs.muta as jcmuta


UC_CLASS = "UC"
UI_CLASS = "UI"
UP_CLASS = "UP"
KI_CLASS = "KI"


class MutationClassifier:
	"""
	It is used to classify the symbolic execution or mutation for pattern mining.
	"""

	def __init__(self, tests):
		"""
		:param tests:
				(1) for dynamic symbolic execution, the parameter is not used
				(2) for static symbolic execution, the parameter is used to determine mutation test results
				(3) for static symbolic execution and tests is None, the classifier is used to determine the
					result of mutation based on whether it is killed or not.
		"""
		self.tests = tests
		self.solutions = dict()		# SymbolicExecution | Mutant ==> [uc, ui, up, ki]
		return

	def has_tests(self):
		return not(self.tests is None)

	def get_tests(self):
		"""
		:return:
				(1) for dynamic symbolic execution, the parameter is not used
				(2) for static symbolic execution, the parameter is used to determine mutation test results
				(3) for static symbolic execution and tests is None, the classifier is used to determine the
					result of mutation based on whether it is killed or not.
		"""
		return self.tests

	def __solving__(self, mutant: jcmuta.Mutant, test_case):
		"""
		:param mutant: mutation that is executed and evaluate according to its result
		:param test_case: test case is provided by dynamic symbolic execution or None
		:return: uc, ui, up, ki
					(1) uc: number of executions that are not covered
					(2) ui: number of executions that are covered but not infected
					(3) up: number of executions that are not killed but infected
					(4) ki: number of executions that are killed during testing
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		if test_case is not None:
			if s_result.is_killed_by(test_case):
				ki += 1
			elif w_result.is_killed_by(test_case):
				up += 1
			elif c_result.is_killed_by(test_case):
				ui += 1
			else:
				uc += 1
		elif self.tests is not None:
			for test in self.tests:
				if s_result.is_killed_by(test):
					ki += 1
				elif w_result.is_killed_by(test):
					up += 1
				elif c_result.is_killed_by(test):
					ui += 1
				else:
					uc += 1
		else:
			if s_result.is_killable():
				ki += 1
			elif w_result.is_killable():
				up += 1
			elif c_result.is_killable():
				ui += 1
			else:
				uc += 1
		return uc, ui, up, ki

	def __get_solution__(self, sample):
		"""
		:param sample: either SymbolicExecution or Mutant
		:return: uc, ui, up, ki
					(1) uc: number of executions that are not covered
					(2) ui: number of executions that are covered but not infected
					(3) up: number of executions that are not killed but infected
					(4) ki: number of executions that are killed during testing
		"""
		solution = self.solutions[sample]
		uc = solution[0]		# number of executions that are not covered
		ui = solution[1]		# number of executions that are covered but not infected
		up = solution[2]		# number of executions that are not killed but infected
		ki = solution[3]		# number of executions that are killed during testing
		uc: int
		ui: int
		up: int
		ki: int
		return uc, ui, up, ki

	def __set_solution__(self, sample):
		"""
		:param sample: either SymbolicExecution or Mutant
		:return: update [uc, ui, up, ki] ==> self.solutions[sample]
		"""
		if isinstance(sample, jcmuta.SymbolicExecution):
			sample: jcmuta.SymbolicExecution
			solution = self.__solving__(sample.get_mutant(), sample.get_test_case())
		else:
			sample: jcmuta.Mutant
			solution = self.__solving__(sample, None)
		self.solutions[sample] = solution
		return

	def __solution__(self, sample):
		"""
		:param sample: SymbolicExecution or Mutant being solved
		:return: uc, ui, up, ki
					(1) uc: number of executions that are not covered
					(2) ui: number of executions that are covered but not infected
					(3) up: number of executions that are not killed but infected
					(4) ki: number of executions that are killed during testing
		"""
		if not(sample in self.solutions):
			self.__set_solution__(sample)
		return self.__get_solution__(sample)

	def __classify__(self, sample):
		"""
		:param sample: SymbolicExecution or Mutant being solved
		:return: UC | UI | UP | KI
		"""
		uc, ui, up, ki = self.__solution__(sample)
		if ki > 0:
			return KI_CLASS
		elif up > 0:
			return UP_CLASS
		elif ui > 0:
			return UI_CLASS
		else:
			return UC_CLASS

	def counting(self, samples):
		"""
		:param samples: set of Mutant or SymbolicExecution being counted
		:return: uc, ui, up, ki, uk, cc
					(1) uc: number of executions that are not covered
					(2) ui: number of executions that are covered but not infected
					(3) up: number of executions that are not killed but infected
					(4) ki: number of executions that are killed during testing
					(5) uk: number of executions that are not killed
					(6) cc: number of executions that are not killed but covered
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__solution__(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def classify(self, samples):
		"""
		:param samples: set of Mutant or SymbolicExecution being classified
		:return: [UC|UI|UP|KI] ==> set of Mutant or SymbolicExecution
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			key = self.__classify__(sample)
			results[key].add(sample)
		return results

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or SymbolicExecution being estimated
		:param uk_or_cc: true to take uk as support or cc as support
		:return: total, support, confidence
		"""
		uc, ui, up, ki, uk, cc = self.counting(samples)
		support = cc
		if uk_or_cc:
			support = uk
		total = support + ki
		confidence = 0.0
		if support > 0:
			confidence = support / total
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or SymbolicExecution being estimated
		:param uk_or_cc: true to select non-killed samples or coincidental correct samples
		:return:
		"""
		results = self.classify(samples)
		selected_results = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selected_results = selected_results | results[UC_CLASS]
		return selected_results


class MutationPattern:
	"""
	It contains the words of symbolic conditions in mutant execution and execution & mutant being matched
	"""

	def __init__(self, classifier: MutationClassifier):
		"""
		:param classifier: used to estimate the patterns
		"""
		self.classifier = classifier	# used to estimate the pattern
		self.executions = set()			# set of executions matched with the pattern
		self.mutants = set()			# set of mutants of which executions matched
		self.words = list()				# set of words to encode symbolic conditions in mutant execution
		return

	def get_words(self):
		"""
		:return: set of words to encode symbolic conditions in mutant execution
		"""
		return self.words

	def __str__(self):
		return str(self.words)

	def __len__(self):
		"""
		:return: number of symbolic conditions being matched in execution
		"""
		return len(self.words)

	def get_conditions(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: symbolic conditions in mutant execution as the pattern
		"""
		conditions = list()
		for word in self.words:
			condition = jcmuta.SymbolicCondition.parse(project, word)
			condition: jcmuta.SymbolicCondition
			conditions.append(condition)
		return conditions

	def get_executions(self):
		"""
		:return: set of executions matched with the pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: set of mutants of which executions matched
		"""
		return self.mutants

	def get_samples(self, exec_or_mutant: bool):
		"""
		:param exec_or_mutant: true to select SymbolicExecution or Mutant being matched
		:return:
		"""
		if exec_or_mutant:
			return self.executions
		else:
			return self.mutants

	def __match__(self, execution: jcmuta.SymbolicExecution, print_value: bool):
		"""
		:param execution:
		:param print_value: true to select value-condition or non-value-condition as features being matched
		:return: whether the symbolic execution matches with the pattern
		"""
		words = execution.get_words(print_value)
		for word in self.words:
			if not(word in words):
				return False
		return True

	def set_executions(self, executions, print_value: bool):
		"""
		:param executions: set of symbolic executions being matched
		:param print_value: true to select value-condition or non-value-condition as features being matched
		:return: update self.mutants and self.executions
		"""
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcmuta.SymbolicExecution
			if self.__match__(execution, print_value):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	def get_classifier(self):
		"""
		:return: used to estimate the performance of the pattern
		"""
		return self.classifier

	def counting(self, exec_or_mutant: bool):
		"""
		:param exec_or_mutant: true to select SymbolicExecution or Mutant being matched
		:return: 	uc, ui, up, ki, uk, cc
					(1) uc: number of executions that are not covered
					(2) ui: number of executions that are covered but not infected
					(3) up: number of executions that are not killed but infected
					(4) ki: number of executions that are killed during testing
					(5) uk: number of executions that are not killed
					(6) cc: number of executions that are not killed but covered
		"""
		return self.classifier.counting(self.get_samples(exec_or_mutant))

	def classify(self, exec_or_mutant: bool):
		"""
		:param exec_or_mutant: true to select SymbolicExecution or Mutant being matched
		:return: [UC|UI|UP|KI] ==> set of Mutant or SymbolicExecution
		"""
		return self.classifier.classify(self.get_samples(exec_or_mutant))

	def estimate(self, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param exec_or_mutant: true to select SymbolicExecution or Mutant being matched
		:param uk_or_cc: true to take uk as support or cc as support
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exec_or_mutant), uk_or_cc)

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or SymbolicExecution being estimated
		:param uk_or_cc: true to select non-killed samples or coincidental correct samples
		:return:
		"""
		results = self.classify(samples)
		selected_results = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selected_results = selected_results | results[UC_CLASS]
		return selected_results

	def get_child(self, word: str):
		"""
		:param word:
		:return: child pattern extended from this one by adding one word or itself
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = MutationPattern(self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: it subsumes the pattern if the samples contain all those in the pattern
		"""
		pattern: MutationPattern
		for execution in self.executions:
			if not(execution in pattern.get_executions()):
				return False
		return True


class MutationPatterns:
	"""
	It preserves the document and patterns generated from it
	"""

	def __init__(self, document: jcmuta.SymbolicDocument, classifier: MutationClassifier, patterns):
		"""
		:param document: it preserves all the executions and mutants in the testing
		:param classifier: used to estimate the patterns and performance
		:param patterns: the set of patterns generated from the document
		"""
		self.document = document
		self.doc_executions = set()
		self.doc_mutants = set()
		for execution in document.get_executions():
			execution: jcmuta.SymbolicExecution
			self.doc_executions.add(execution)
			self.doc_mutants.add(execution.get_mutant())
		self.classifier = classifier
		self.all_patterns = set()
		self.pat_executions = set()
		self.pat_mutants = set()
		for pattern in patterns:
			pattern: MutationPattern
			self.all_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jcmuta.SymbolicExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
		self.min_patterns = self.__get_min_patterns__()
		return

	def __get_min_patterns__(self):
		"""
		:return: set of patterns that subsume all the others
		"""
		remain_patterns, remove_patterns, minimal_patterns = set(), set(), set()
		for pattern in self.all_patterns:
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			subsume_pattern = None
			remove_patterns.clear()
			for pattern in remain_patterns:
				if subsume_pattern is None:
					subsume_pattern = pattern
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern):
					subsume_pattern = pattern
					remove_patterns.add(pattern)
				elif subsume_pattern.subsume(pattern):
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
			if subsume_pattern is not None:
				subsume_pattern: MutationPattern
				minimal_patterns.add(subsume_pattern)
		return minimal_patterns

	def get_document(self):
		"""
		:return: it provides all the symbolic executions being classified
		"""
		return self.document

	def get_doc_executions(self):
		"""
		:return: the set of symbolic executions loaded from mutation testing
		"""
		return self.doc_executions

	def get_doc_mutants(self):
		"""
		:return: the set of mutants being executed during testing
		"""
		return self.doc_mutants

	def get_classifier(self):
		"""
		:return: used to classify and evaluate mutation patterns
		"""
		return self.classifier

	def get_all_patterns(self):
		"""
		:return: all the patterns generated from the document
		"""
		return self.all_patterns

	def get_min_patterns(self):
		"""
		:return: minimal set of patterns subsuming all the others
		"""
		return self.min_patterns

	def get_pat_executions(self):
		return self.pat_executions

	def get_pat_mutants(self):
		return self.pat_mutants

	@staticmethod
	def __select_best_pattern__(patterns, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param patterns: set of patterns from which the best pattern is selected
		:param exec_or_mutant: true to take SymbolicExecution or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: the pattern best matching the requirement with largest support, confidence and minimal length
		"""
		remain_patterns = set()
		for pattern in patterns:
			pattern: MutationPattern
			remain_patterns.add(pattern)

		length = max(int(len(remain_patterns) * 0.50), 1)
		while len(remain_patterns) > length:
			worst_pattern, worst_length = None, 0
			for pattern in remain_patterns:
				word_length = len(pattern.words)
				if word_length >= worst_length:
					worst_pattern = pattern
					worst_length = word_length
			if worst_pattern is not None:
				remain_patterns.remove(worst_pattern)

		length = max(int(len(remain_patterns) * 0.50), 1)
		while len(remain_patterns) > length:
			worst_pattern, worst_confidence = None, 1.0
			for pattern in remain_patterns:
				total, support, confidence = pattern.estimate(exec_or_mutant, uk_or_cc)
				if confidence <= worst_confidence:
					worst_confidence = confidence
					worst_pattern = pattern
			if worst_pattern is not None:
				remain_patterns.remove(worst_pattern)

		best_pattern, best_support = None, 0
		for pattern in remain_patterns:
			total, support, confidence = pattern.estimate(exec_or_mutant, uk_or_cc)
			if support >= best_support:
				best_support = support
				best_pattern = pattern
		return best_pattern

	def select_best_patterns(self, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param exec_or_mutant: true to take SymbolicExecution or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: Mutant ==> SymExecutionPattern that best matches with it...
		"""
		mutant_patterns = dict()
		for pattern in self.all_patterns:
			for mutant in pattern.get_mutants():
				mutant: jcmuta.Mutant
				if not(mutant in mutant_patterns):
					mutant_patterns[mutant] = set()
				mutant_patterns[mutant].add(pattern)
		best_patterns = dict()
		for mutant, patterns in mutant_patterns.items():
			best_pattern = MutationPatterns.__select_best_pattern__(patterns, exec_or_mutant, uk_or_cc)
			if best_pattern is not None:
				best_pattern: MutationPattern
				best_patterns[mutant] = best_pattern
		return best_patterns


class MutationPatternMiner:
	"""
	It implements the frequent pattern mining to get mutation patterns
	"""

	def __init__(self, exec_or_mutant: bool, uk_or_cc: bool, print_value: bool,
				 min_support: int, max_confidence: float, max_length: int):
		"""
		:param exec_or_mutant: true to take SymbolicExecution as sample or false to take Mutant as well
		:param uk_or_cc: true to estimate on non-killed samples or false to take coincidental correct samples
		:param print_value: true to take value of symbolic condition into feature or no-value feature
		:param min_support: minimal number of samples that support the patterns
		:param max_confidence: maximal confidence once achieved the pattern generation will be terminate
		:param max_length: maximal length of pattern being generated
		"""
		self.exec_or_mutant = exec_or_mutant
		self.uk_or_cc = uk_or_cc
		self.print_value = print_value
		self.min_support = min_support
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.__classifier__ = None  # Used to create SymExecutionPattern and estimate them
		self.__patterns__ = dict()  # String ==> SymExecutionPattern
		self.__solution__ = dict()  # SymExecutionPattern ==> [total, support, confidence]
		return

	def __root__(self, document: jcmuta.SymbolicDocument, word: str):
		"""
		:param document:
		:param word:
		:return: unique root pattern with one word that matches with all the executions
		"""
		root = MutationPattern(self.__classifier__)
		root = root.get_child(word)
		if not(str(root) in self.__patterns__):
			self.__patterns__[str(root)] = root
			root.set_executions(document.get_executions(), self.print_value)
		root = self.__patterns__[str(root)]
		root: MutationPattern
		return root

	def __child__(self, parent: MutationPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: child pattern extended from the parent by adding one word or parent
		"""
		child = parent.get_child(word)
		if child != parent:
			if not(str(child) in self.__patterns__):
				self.__patterns__[str(child)] = child
				child.set_executions(parent.get_executions(), self.print_value)
			child = self.__patterns__[str(child)]
			child: MutationPattern
			return child
		return parent

	def __mine__(self, parent: MutationPattern, words):
		"""
		:param parent:
		:param words:
		:return: recursively mine the patterns from its children
		"""
		if not(parent in self.__solution__):
			total, support, confidence = parent.estimate(self.exec_or_mutant, self.uk_or_cc)
			self.__solution__[parent] = (total, support, confidence)
		solution = self.__solution__[parent]
		support = solution[1]
		confidence = solution[2]
		if len(parent.words) < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__mine__(child, words)
		return

	def __output__(self, document: jcmuta.SymbolicDocument):
		"""
		:param document:
		:return:
		"""
		good_patterns = set()
		for pattern, solution in self.__solution__.items():
			pattern: MutationPattern
			support = solution[1]
			confidence = solution[2]
			if support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		return MutationPatterns(document, self.__classifier__, good_patterns)

	def mine(self, document: jcmuta.SymbolicDocument, classifier_tests):
		"""
		:param document:
		:param classifier_tests:
		:return:
		"""
		self.__classifier__ = MutationClassifier(classifier_tests)
		self.__patterns__.clear()
		self.__solution__.clear()

		init_executions = self.__classifier__.select(document.get_executions(), self.uk_or_cc)
		for init_execution in init_executions:
			init_execution: jcmuta.SymbolicExecution
			words = init_execution.get_words(self.print_value)
			for word in words:
				root = self.__root__(document, word)
				self.__mine__(root, words)

		patterns = self.__output__(document)
		self.__patterns__.clear()
		self.__solution__.clear()
		self.__classifier__ = None

		return patterns


class MutationPatternWriter:
	"""
		It writes the evaluation results on Symbolic Execution Patterns
		"""

	def __init__(self, patterns: MutationPatterns, print_value: bool):
		"""
		:param patterns:
		"""
		self.patterns = patterns
		self.print_value = print_value
		self.writer = None
		return

	@staticmethod
	def __percentage__(ratio: float):
		return int(ratio * 100000000) / 1000000.0

	@staticmethod
	def __proportion__(x: int, y: int):
		ratio = 0.0
		if x != 0:
			ratio = x / (y + 0.0)
		return MutationPatternWriter.__percentage__(ratio)

	@staticmethod
	def __prf_evaluation__(doc_samples: set, pat_samples: set):
		"""
		:param doc_samples: the samples from document that selected as target class
		:param pat_samples: the samples matched from the patterns as generated
		:return: precision, recall, f1_score
		"""
		int_samples = doc_samples & pat_samples
		int_length = len(int_samples)
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if int_length > 0:
			precision = int_length / (len(pat_samples) + 0.0)
			recall = int_length / (len(doc_samples) + 0.0)
			f1_score = 2 * precision * recall / (precision + recall)
		return precision, recall, f1_score

	''' pattern writer '''

	def __write_pattern_count__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return:
				(1) Summary: length, lines, mutants
				(2) Counter: title, UC, UI, UP, KI, UK CC
				(3) Estimate: title, total, support, confidence
		"""
		self.writer: TextIO

		''' summary: length, lines, mutants '''
		self.writer.write("\t@Summary.\n")
		self.writer.write("\t{} := {}\t{} := {}\t{} := {}\n".
						  format("Length", len(pattern.words),
								 "Lines", len(pattern.get_executions()),
								 "Mutants", len(pattern.get_mutants())))

		''' counter: title UC UI UP KI UK CC '''
		self.writer.write("\n\t@Counting.\n")
		self.writer.write("\tSample\tUC\tUI\tUP\tKI\tUK\tCC\n")
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Line", uc, ui, up, ki, uk, cc))
		uc, ui, up, ki, uk, cc = pattern.counting(False)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Mutant", uc, ui, up, ki, uk, cc))

		''' estimate: title total support confidence '''
		self.writer.write("\n\t@Estimate.\n")
		self.writer.write("\tTitle\ttotal\tsupport\tconfidence(%)\n")
		total, support, confidence = pattern.estimate(True, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Line", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(True, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		return

	def __write_pattern_lines__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return:
				ID RES CLASS OPERATOR LINE CODE PARAMETER
		"""
		self.writer: TextIO
		self.writer.write("\t@Samples\n")
		self.writer.write("\tID\tRES\tCLASS\tOPERATOR\tLINE\tCODE\tPARAMETER\n")
		for mutant in pattern.get_mutants():
			mutant: jcmuta.Mutant
			result = pattern.classifier.__classify__(mutant)
			mutant_id = mutant.get_mut_id()
			mutation_class = mutant.get_mutation().get_mutation_class()
			mutation_operator = mutant.get_mutation().get_mutation_operator()
			location = mutant.get_mutation().get_location()
			line = location.line_of(False)
			code = location.get_code(True)
			parameter = mutant.get_mutation().get_parameter()
			self.writer.write("\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".format(mutant_id,
																		  result,
																		  mutation_class,
																		  mutation_operator,
																		  line,
																		  code,
																		  parameter))
		return

	def __write_pattern_words__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return: index type execution line statement location parameter
		"""
		self.writer: TextIO
		self.writer.write("\t@Words\n")
		self.writer.write("\tIndex\tType\tExecution\tLine\tStatement\tLocation\tParameter\n")
		index = 0
		for annotation in pattern.get_conditions(self.patterns.document.get_project()):
			index += 1
			annotation_type = annotation.get_feature()
			execution = annotation.get_execution()
			statement = execution.get_statement()
			ast_line = None
			if statement.has_ast_source():
				ast_line = statement.get_ast_source().line_of(False)
			location = annotation.get_location()
			parameter = annotation.get_parameter()
			self.writer.write("\t{}\t{}\t{}\t{}\t\"{}\"\t\"{}\"\t{}\n".format(index,
																			  annotation_type,
																			  execution,
																			  ast_line,
																			  statement.get_cir_code(),
																			  location.get_cir_code(),
																			  parameter))
		return

	def __write_pattern__(self, pattern: MutationPattern, pattern_index: int):
		self.writer: TextIO
		self.writer.write("#BEG\t{}\n".format(pattern_index))
		self.__write_pattern_count__(pattern)
		self.writer.write("\n")
		self.__write_pattern_lines__(pattern)
		self.writer.write("\n")
		self.__write_pattern_words__(pattern)
		self.writer.write("#END\t{}\n".format(pattern_index))

	def write_patterns(self, output_file: str):
		"""
		:param output_file:
		:return:
		"""
		with open(output_file, 'w') as writer:
			self.writer = writer
			pattern_index = 0
			for pattern in self.patterns.get_min_patterns():
				pattern_index += 1
				self.__write_pattern__(pattern, pattern_index)
				self.writer.write("\n")
		return

	''' best patterns '''

	def write_best_patterns(self, output_file: str, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param output_file:
		:param line_or_mutant:
		:param uk_or_cc:
		:return:
		"""
		with open(output_file, 'w') as writer:
			self.writer = writer
			mutant_best_patterns = self.patterns.select_best_patterns(line_or_mutant, uk_or_cc)
			for mutant, best_pattern in mutant_best_patterns.items():
				mutant_id = mutant.get_mut_id()
				result = self.patterns.classifier.__classify__(mutant)
				mutation_class = mutant.get_mutation().get_mutation_class()
				mutation_operator = mutant.get_mutation().get_mutation_operator()
				line = mutant.get_mutation().get_location().line_of(False)
				code = mutant.get_mutation().get_location().get_code(True)
				parameter = mutant.get_mutation().get_parameter()
				self.writer.write("Mutant\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".format(
					mutant_id, result, mutation_class, mutation_operator, line, code, parameter))
				if best_pattern is not None:
					self.__write_pattern_words__(best_pattern)
				self.writer.write("\n")
		return

	''' summarize on mutation testing results '''

	def __write_document_results__(self):
		"""
		:return:
				(1) summary tests mutants over_score valid_score
				(2) counting UC UI UP KI UK CC
				(3) estimate total support confidence
				(4) matching precision recall f1_score
		"""
		self.writer: TextIO
		document = self.patterns.get_document()
		project = document.get_project()
		doc_lines = self.patterns.get_doc_executions()
		doc_mutants = self.patterns.get_doc_mutants()
		classifier = self.patterns.classifier

		''' summary tests mutants over_score valid_score '''
		self.writer.write("@Summary\n")
		test_number = None
		if classifier.has_tests():
			test_number = len(self.patterns.classifier.get_tests())
		self.writer.write("\t{} := {}\n".format("Tests", test_number))
		self.writer.write("\t{} := {}\n".format("Lines", len(doc_lines)))
		self.writer.write("\t{} := {}\n".format("Mutants", len(doc_mutants)))
		killed, over_score, valid_score = project.evaluation.evaluate_mutation_score(doc_mutants,
																					 classifier.get_tests())
		self.writer.write(
			"\t{} := {}%\n".format("over_score", MutationPatternWriter.__percentage__(over_score)))
		self.writer.write(
			"\t{} := {}%\n".format("valid_score", MutationPatternWriter.__percentage__(valid_score)))
		self.writer.write("\n")

		''' counting UC UI UP KI UK CC '''
		self.writer.write("@Counter\n")
		self.writer.write("\tSample\tUC\tUI\tUP\tKI\tUK\tCC\n")
		uc, ui, up, ki, uk, cc = classifier.counting(doc_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Lines", uc, ui, up, ki, uk, cc))
		uc, ui, up, ki, uk, cc = classifier.counting(doc_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Mutants", uc, ui, up, ki, uk, cc))
		self.writer.write("\n")

		''' estimate total support confidence '''
		self.writer.write("@Estimate\n")
		self.writer.write("\tTitle\ttotal\tsupport\tconfidence(%)\n")
		total, support, confidence = classifier.estimate(doc_lines, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Line", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_lines, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_mutants, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_mutants, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant", total, support,
													  MutationPatternWriter.__percentage__(confidence)))
		self.writer.write("\n")

		''' matching precision recall f1_score '''
		pat_lines, pat_mutants = self.patterns.get_pat_executions(), self.patterns.get_pat_mutants()
		doc_uk_lines = classifier.select(doc_lines, True)
		doc_cc_lines = classifier.select(doc_lines, False)
		doc_uk_mutants = classifier.select(doc_mutants, True)
		doc_cc_mutants = classifier.select(doc_mutants, False)
		self.writer.write("@Matching\n")
		self.writer.write("\tTitle\tprecision(%)\trecall(%)\tf1_score\n")
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(doc_uk_lines, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Line",
													  MutationPatternWriter.__percentage__(precision),
													  MutationPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(doc_cc_lines, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line",
													  MutationPatternWriter.__percentage__(precision),
													  MutationPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(doc_uk_mutants, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant",
													  MutationPatternWriter.__percentage__(precision),
													  MutationPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(doc_cc_mutants, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant",
													  MutationPatternWriter.__percentage__(precision),
													  MutationPatternWriter.__percentage__(recall),
													  f1_score))
		self.writer.write("\n")

		''' patterns length lines mutants uk_line(%) cc_line(%) uk_mutant(%) cc_mutant(%) '''
		self.writer.write("@Patterns\n")
		self.writer.write("\tIndex\tLength\tLines\tMutants\tUK_Lines(%)\tCC_Lines(%)\tUK_Mutants(%)\tCC_Mutants(%)\n")
		index = 0
		for pattern in self.patterns.get_min_patterns():
			index += 1
			length = len(pattern.words)
			lines_number = len(pattern.get_executions())
			mutants_number = len(pattern.get_mutants())
			total, uk_line_support, uk_line_confidence = pattern.estimate(True, True)
			total, cc_line_support, cc_line_confidence = pattern.estimate(True, False)
			total, uk_mutant_support, uk_mutant_confidence = pattern.estimate(False, True)
			total, cc_mutant_support, cc_mutant_confidence = pattern.estimate(False, False)
			self.writer.write("\t{}\t{}\t{}\t{}\t{}%\t{}%\t{}%\t{}%\n".
							  format(index, length, lines_number, mutants_number,
									 MutationPatternWriter.__percentage__(uk_line_confidence),
									 MutationPatternWriter.__percentage__(cc_line_confidence),
									 MutationPatternWriter.__percentage__(uk_mutant_confidence),
									 MutationPatternWriter.__percentage__(cc_mutant_confidence)))
		self.writer.write("\n")

		''' optimization patterns uk_line_optimization cc_line_optimization ... '''
		self.writer.write("@Optimizer\n")
		patterns_number = len(self.patterns.get_min_patterns())
		self.writer.write("\t{} := {}%\n".format("UK_LINE_OPTIMIZE",
												 MutationPatternWriter.__proportion__(patterns_number,
																						  len(doc_uk_lines))))
		self.writer.write("\t{} := {}%\n".format("CC_LINE_OPTIMIZE",
												 MutationPatternWriter.__proportion__(patterns_number,
																						  len(doc_cc_lines))))
		self.writer.write("\t{} := {}%\n".format("UK_MUTA_OPTIMIZE",
												 MutationPatternWriter.__proportion__(patterns_number,
																						  len(doc_uk_mutants))))
		self.writer.write("\t{} := {}%\n".format("CC_MUTA_OPTIMIZE",
												 MutationPatternWriter.__proportion__(patterns_number,
																						  len(doc_cc_mutants))))
		self.writer.write("\n")
		self.writer.flush()
		return

	def write_results(self, output_file: str):
		"""
		:param output_file:
		:return:
		"""
		with open(output_file, 'w') as writer:
			self.writer = writer
			self.__write_document_results__()
		return


def mining_patterns(document: jcmuta.SymbolicDocument, classifier_tests, line_or_mutant: bool, uk_or_cc: bool,
					print_value: bool, min_support: int, max_confidence: float, max_length: int, output_directory: str):
	"""
	:param document: it provides lines and mutations in the program
	:param classifier_tests: used to generate classifier of mutation
	:param line_or_mutant: true to take line as sample or false to take mutant as sample
	:param uk_or_cc: true to estimate on non-killed samples or false on coincidental correctness samples
	:param print_value: true to use value-condition or ignore value of condition for mining
	:param min_support: minimal number of samples supporting the patterns
	:param max_confidence: maximal confidence once achieved to stop the pattern generation
	:param max_length: maximal length of the patterns allowed to generate
	:param output_directory: directory where the output files are preserved
	:return:
	"""
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	if len(document.get_executions()) > 0:
		print("Testing on", document.get_project().program.name)
		print("\t(1) Load", len(document.get_executions()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.get_corpus()), "words.")
		__killed__, over_score, valid_score = document.get_project().evaluation.\
			evaluate_mutation_score(document.get_project().mutant_space.get_mutants(), classifier_tests)
		test_number = None
		if classifier_tests is not None:
			test_number = len(classifier_tests)
		print("\t\tSelect", test_number, "test cases for killing", __killed__,
			  "mutants with {}%({}%).".format(MutationPatternWriter.__percentage__(over_score),
											  MutationPatternWriter.__percentage__(valid_score)))

		generator = MutationPatternMiner(line_or_mutant, uk_or_cc, print_value, min_support, max_confidence, max_length)
		patterns = generator.mine(document, classifier_tests)
		print("\t(2) Generate", len(patterns.get_all_patterns()), "patterns with", len(patterns.get_min_patterns()), "of minimal set from.")

		writer = MutationPatternWriter(patterns, print_value)
		writer.write_patterns(os.path.join(output_directory, document.get_project().program.name + ".mpt"))
		writer.write_results(os.path.join(output_directory, document.get_project().program.name + ".mrt"))
		writer.write_best_patterns(os.path.join(output_directory, document.get_project().program.name + ".bpt"), line_or_mutant, uk_or_cc)
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def testing_project(directory: str, file_name: str,
					exec_or_mutant: bool,
					uk_or_cc: bool,
					print_value: bool,
					none_directory: str,
					over_directory: str,
					test_directory: str,
					dyna_directory: str,
					dynamic_evaluation: bool):
	c_project = jcmuta.CProject(directory, file_name)

	docs = c_project.load_static_document(directory)
	selected_mutants = c_project.evaluation.select_mutants_by_classes(["STRP", "BTRP"])
	minimal_tests, __remained__ = c_project.evaluation.select_tests_for_mutants(selected_mutants)
	minimal_number = int(len(c_project.test_space.get_test_cases()) * 0.004)
	random_tests = c_project.evaluation.select_tests_for_random(minimal_number)
	selected_tests = minimal_tests | random_tests

	mining_patterns(docs, None, exec_or_mutant, uk_or_cc, print_value, 2, 0.80, 1, os.path.join(none_directory))
	mining_patterns(docs, selected_tests, exec_or_mutant, uk_or_cc, print_value, 20, 0.80, 1, os.path.join(test_directory))
	mining_patterns(docs, c_project.test_space.get_test_cases(), exec_or_mutant, uk_or_cc, print_value, 100, 0.80, 1, os.path.join(over_directory))

	if dynamic_evaluation:
		docs = c_project.load_dynamic_document(directory)
		mining_patterns(docs, docs.get_test_cases(), exec_or_mutant, uk_or_cc, print_value, 20, 0.80, 1, os.path.join(dyna_directory))
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	none_path = "/home/dzt2/Development/Data/patterns/none"
	test_path = "/home/dzt2/Development/Data/patterns/test"
	over_path = "/home/dzt2/Development/Data/patterns/over"
	dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for filename in os.listdir(prev_path):
		direct = os.path.join(prev_path, filename)
		testing_project(direct,
						filename,
						True,
						True,
						False,
						none_path,
						over_path,
						test_path,
						dyna_path,
						False)
	print("\nTesting end for all...")

