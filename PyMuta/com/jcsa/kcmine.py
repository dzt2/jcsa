""" This file implements frequent pattern mining on killing conditions for detecting equivalent mutants. """


import os
from typing import TextIO

import com.jcsa.libs.muta as jcmuta


UC_CLASS = "UC"			# execution that is not covered during testing
UI_CLASS = "UI"			# execution that is not infected but covered
UP_CLASS = "UP"			# execution that is not killed but infected
KI_CLASS = "KI"			# execution that is killed


class KillClassifier:
	"""
	It is used to classify, count and estimate the test results of mutation testing
	"""

	def __init__(self, tests):
		"""
		:param tests: test cases used as the context to determine whether a mutant is killed.
				(1) for dynamic execution (with test case), this parameter is ignored and it
					only counts the test result of mutant on specified test case.
				(2) for static execution, tests specify the set of test cases being used and
					it counts the number of tests in self.tests killing the target mutant.
				(3) for static execution, None tests mean all the test and it only count the
					number of mutants or static executions being killed.
		"""
		self.tests = tests
		self.solutions = dict()
		return

	def __solving__(self, mutant: jcmuta.Mutant, test_case):
		"""
		:param mutant: mutation being executed
		:param test_case: used by dynamic execution or None to use self.tests to count
		:return: 	uc, ui, up, ki
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		s_result = mutant.get_result()
		w_result = mutant.get_weak_mutant().get_result()
		c_result = mutant.get_coverage_mutant().get_result()
		if test_case is None:
			if self.tests is None:
				if s_result.is_killable():
					ki += 1
				elif w_result.is_killable():
					up += 1
				elif c_result.is_killable():
					ui += 1
				else:
					uc += 1
			else:
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
			if s_result.is_killed_by(test_case):
				ki += 1
			elif w_result.is_killed_by(test_case):
				up += 1
			elif c_result.is_killed_by(test_case):
				ui += 1
			else:
				uc += 1
		return uc, ui, up, ki

	def __get_solution__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: 	uc, ui, up, ki
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
		"""
		solution = self.solutions[sample]
		uc = solution[0]
		ui = solution[1]
		up = solution[2]
		ki = solution[3]
		uc: int
		ui: int
		up: int
		ki: int
		return uc, ui, up, ki

	def __set_solution__(self, sample):
		"""
		:param sample: either mutant or SymbolicExecution
		:return:
		"""
		if isinstance(sample, jcmuta.Mutant):
			sample: jcmuta.Mutant
			solution = self.__solving__(sample, None)
		else:
			sample: jcmuta.SymbolicExecution
			solution = self.__solving__(sample.get_mutant(), sample.get_test_case())
		self.solutions[sample] = solution
		return

	def __counting__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: 	uc, ui, up, ki
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
		"""
		if not(sample in self.solutions):
			self.__set_solution__(sample)
		return self.__get_solution__(sample)

	def __classify__(self, sample):
		"""
		:param sample: either Mutant or SymbolicExecution
		:return: 	UC: sample is not covered by self.tests
					UI: sample is not infected but covered by any test in self.tests
					UP: sample is not killed but infected by self.tests
					KI: sample is killed by self.tests
		"""
		uc, ui, up, ki = self.__counting__(sample)
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
		:param samples: set of Mutant or SymbolicExecution
		:return: 	uc, ui, up, ki, uk, cc
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
					(5) uk: number of test cases that cannot kill the sample (uc + ui + up)
					(6) cc: number of test cases that cannot kill but cover sample (ui + up)
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__counting__(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def classify(self, samples):
		"""
		:param samples: set of Mutant or SymbolicExecution
		:return: {UC, UI, UP, KI} ==> set of samples
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			results[self.__classify__(sample)].add(sample)
		return results

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or SymbolicExecution
		:param uk_or_cc: true to take non-killed samples as support or coincidental correct samples
		:return: total, support, confidence
		"""
		uc, ui, up, ki, uk, cc = self.counting(samples)
		if uk_or_cc:
			support = uk
		else:
			support = cc
		total = support + ki
		if support > 0:
			confidence = support / (total + 0.0)
		else:
			confidence = 0.0
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or SymbolicExecution
		:param uk_or_cc: true to select non-killed samples or coincidental correct samples
		:return: set of samples w.r.t. the given type
		"""
		results = self.classify(samples)
		selected_samples = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selected_samples = selected_samples | results[UC_CLASS]
		return selected_samples

	def has_tests(self):
		return not(self.tests is None)

	def get_tests(self):
		"""
		:return: test cases used as the context to determine whether a mutant is killed.
				(1) for dynamic execution (with test case), this parameter is ignored and it
					only counts the test result of mutant on specified test case.
				(2) for static execution, tests specify the set of test cases being used and
					it counts the number of tests in self.tests killing the target mutant.
				(3) for static execution, None tests mean all the test and it only count the
					number of mutants or static executions being killed.
		"""
		return self.tests


class KCPattern:
	"""
	It defines the pattern of kill-condition with a set of words that encode them in mutation analysis
	"""

	def __init__(self, document: jcmuta.SymbolicDocument, classifier: KillClassifier):
		"""
		:param document: it provides all the executions and mutants for being matched
		:param classifier: classifier used to estimate the performance of the pattern
		"""
		self.document = document		# it provides all the executions and mutants for being matched
		self.classifier = classifier	# classifier used to estimate the performance of the pattern
		self.words = list()				# set of words that encode killing conditions in this pattern
		self.executions = set()			# set of symbolic executions that match with the pattern
		self.mutants = set()			# set of mutations of which executions match with the pattern
		return

	def get_words(self):
		"""
		:return: set of words that encode killing conditions in this pattern
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: killing conditions in this pattern
		"""
		conditions = list()
		for word in self.words:
			conditions.append(self.document.get_condition(word))
		return conditions

	def __str__(self):
		return str(self.words)

	def __len__(self):
		"""
		:return: number of killing conditions in the pattern
		"""
		return len(self.words)

	def get_document(self):
		"""
		:return: it provides all the executions and mutants for being matched
		"""
		return self.document

	def get_executions(self):
		"""
		:return: set of symbolic executions that match with the pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: set of mutations of which executions match with the pattern
		"""
		return self.mutants

	def get_samples(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: true to return self.executions or self.mutants
		:return:
		"""
		if exe_or_mut:
			return self.executions
		else:
			return self.mutants

	def is_matched(self, execution: jcmuta.SymbolicExecution):
		"""
		:param execution:
		:return: whether the symbolic execution matches with the pattern
		"""
		for word in self.words:
			if not(word in execution.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: parent pattern to update the samples or None to update on all the executions in document
		:return:
		"""
		if parent is None:
			executions = self.document.get_executions()
		else:
			parent: KCPattern
			executions = parent.get_executions()
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcmuta.SymbolicExecution
			if self.is_matched(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	def get_classifier(self):
		return self.classifier

	def counting(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: true to counting on executions or mutants
		:return: 	uc, ui, up, ki, uk, cc
					(1) uc: number of test cases that cannot cover the target mutant
					(2) ui: number of test cases that cannot infect but cover the mutant
					(3) up: number of test cases that cannot kill but infect the mutant
					(4) ki: number of test cases that kill the target mutant
					(5) uk: number of test cases that cannot kill the sample (uc + ui + up)
					(6) cc: number of test cases that cannot kill but cover sample (ui + up)
		"""
		return self.classifier.counting(self.get_samples(exe_or_mut))

	def classify(self, exe_or_mut: bool):
		"""
		:param exe_or_mut: true to classify on executions or mutants
		:return: {UC, UI, UP, KI} ==> set of samples
		"""
		return self.classifier.classify(self.get_samples(exe_or_mut))

	def estimate(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: true to estimate on executions or mutants
		:param uk_or_cc: true to take non-killed as support or coincidental correct one
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exe_or_mut), uk_or_cc)

	def select(self, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param exe_or_mut: true to estimate on executions or mutants
		:param uk_or_cc: true to select non-killed or coincidental correct one
		:return:
		"""
		return self.classifier.select(self.get_samples(exe_or_mut), uk_or_cc)

	def get_child(self, word: str):
		"""
		:param word: additional word appended to this pattern to extend the child pattern
		:return: child pattern extended from parent pattern by adding one additional word
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = KCPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: whether the samples matching with this pattern include those matched with pattern
		"""
		pattern: KCPattern
		for execution in pattern.executions:
			if not(execution in self.executions):
				return False
		return True


class KCPatternSpace:
	"""
	Space to maintain the killing condition patterns generated from pattern miner.
	"""

	def __init__(self, document: jcmuta.SymbolicDocument, classifier: KillClassifier, good_patterns):
		"""
		:param document: it provides all the executions and mutants from feature files
		:param classifier: used to classify and estimate the performance of generated patterns
		:param good_patterns: set of patterns generated and selected from dataset
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
		for pattern in good_patterns:
			pattern: KCPattern
			self.all_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jcmuta.SymbolicExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
		self.min_patterns = self.__minimal_kc_patterns__()
		return

	def __minimal_kc_patterns__(self):
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
				elif subsume_pattern.subsume(pattern):
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern):
					subsume_pattern = pattern
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
			if not(subsume_pattern is None):
				minimal_pattern = subsume_pattern
				minimal_pattern: KCPattern
				minimal_patterns.add(minimal_pattern)
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
	def __select_best_pattern__(patterns, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param patterns: set of patterns from which the best pattern is selected
		:param exe_or_mut: true to take SymbolicExecution or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: the pattern best matching the requirement with largest support, confidence and minimal length
		"""
		remain_patterns = set()
		for pattern in patterns:
			pattern: KCPattern
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
				total, support, confidence = pattern.estimate(exe_or_mut, uk_or_cc)
				if confidence <= worst_confidence:
					worst_confidence = confidence
					worst_pattern = pattern
			if worst_pattern is not None:
				remain_patterns.remove(worst_pattern)

		best_pattern, best_support = None, 0
		for pattern in remain_patterns:
			total, support, confidence = pattern.estimate(exe_or_mut, uk_or_cc)
			if support >= best_support:
				best_support = support
				best_pattern = pattern
		return best_pattern

	def get_best_patterns(self, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param exec_or_mutant: true to take SymbolicExecution or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: Mutant ==> SymExecutionPattern that best matches with it...
		"""
		mutant_patterns = dict()
		for pattern in self.all_patterns:
			for mutant in pattern.get_mutants():
				mutant: jcmuta.Mutant
				if not (mutant in mutant_patterns):
					mutant_patterns[mutant] = set()
				mutant_patterns[mutant].add(pattern)
		best_patterns = dict()
		for mutant, patterns in mutant_patterns.items():
			best_pattern = KCPatternSpace.__select_best_pattern__(patterns, exec_or_mutant, uk_or_cc)
			if not(best_pattern is None):
				best_pattern: KCPattern
				best_patterns[mutant] = best_pattern
		return best_patterns


class KCPatternMiner:
	"""
	It implements pattern mining algorithm to identify patterns of killing conditions
	"""

	def __init__(self, exe_or_mut: bool, uk_or_cc: bool, min_support: int, max_confidence: float, max_length: int):
		"""
		:param exe_or_mut: true to take SymbolicExecution as sample or false to take Mutant as well
		:param uk_or_cc: true to estimate on non-killed samples or false to take coincidental correct samples
		:param min_support: minimal number of samples that support the patterns
		:param max_confidence: maximal confidence once achieved the pattern generation will be terminate
		:param max_length: maximal length of pattern being generated
		"""
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.document = None			# Used as inputs to generate good patterns of killing conditions
		self.classifier = None			# Used to create SymExecutionPattern and estimate them
		self.patterns = dict()			# String ==> SymExecutionPattern
		self.solutions = dict()			# SymExecutionPattern ==> [total, support, confidence]
		return

	def __root__(self, word: str):
		"""
		:param word:
		:return: unique pattern with one word
		"""
		root = KCPattern(self.document, self.classifier)
		root = root.get_child(word.strip())
		if not(str(root) in self.patterns):
			self.patterns[str(root)] = root
			root.set_samples(None)
		root = self.patterns[str(root)]
		root: KCPattern
		return root

	def __child__(self, parent: KCPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: child pattern extended from parent or itself
		"""
		child = parent.get_child(word)
		if child == parent:
			return parent
		else:
			if not(str(child) in self.patterns):
				self.patterns[str(child)] = child
				child.set_samples(parent)
			child = self.patterns[str(child)]
			child: KCPattern
			return child

	def __mine__(self, parent: KCPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		if not(parent in self.solutions):
			self.solutions[parent] = parent.estimate(self.exe_or_mut, self.uk_or_cc)
		solution = self.solutions[parent]
		support = solution[1]
		confidence = solution[2]
		if len(parent) < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__mine__(child, words)
		return

	def __output__(self):
		good_patterns = set()
		for pattern, solution in self.solutions.items():
			pattern: KCPattern
			support = solution[1]
			confidence = solution[2]
			if support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		return KCPatternSpace(self.document, self.classifier, good_patterns)

	def mine(self, document: jcmuta.SymbolicDocument, classifier_tests):
		"""
		:param document:
		:param classifier_tests: set of test cases for determining test results of mutation analysis
		:return: KCPatternSpace
		"""
		self.document = document
		self.classifier = KillClassifier(classifier_tests)
		self.patterns.clear()
		self.solutions.clear()

		executions = self.classifier.select(self.document.get_executions(), self.uk_or_cc)
		for execution in executions:
			execution: jcmuta.SymbolicExecution
			words = execution.get_words()
			for word in words:
				root = self.__root__(word)
				self.__mine__(root, words)

		space = self.__output__()
		self.document = None
		self.classifier = None
		self.patterns.clear()
		self.solutions.clear()

		return space


class KCPatternWriter:
	"""
	It writes the evaluation results of killing condition patterns
	"""

	def __init__(self, space: KCPatternSpace):
		"""
		:param space:
		"""
		self.space = space
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
		return KCPatternWriter.__percentage__(ratio)

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

	def __write_pattern_count__(self, pattern: KCPattern):
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
													  KCPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(True, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line", total, support,
													  KCPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant", total, support,
													  KCPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant", total, support,
													  KCPatternWriter.__percentage__(confidence)))
		return

	def __write_pattern_lines__(self, pattern: KCPattern):
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

	def __write_pattern_words__(self, pattern: KCPattern):
		"""
		:param pattern:
		:return: index type execution line statement location parameter
		"""
		self.writer: TextIO
		self.writer.write("\t@Words\n")
		self.writer.write("\tIndex\tType\tValue\tExecution\tLine\tStatement\tLocation\tParameter\n")
		index = 0
		for annotation in pattern.get_conditions():
			index += 1
			annotation_type = annotation.get_feature()
			execution = annotation.get_execution()
			statement = execution.get_statement()
			ast_line = None
			if statement.has_ast_source():
				ast_line = statement.get_ast_source().line_of(False)
			location = annotation.get_location()
			parameter = annotation.get_parameter()
			self.writer.write("\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t\"{}\"\t{}\n".format(index,
																				  annotation_type,
																				  annotation.get_value(),
																				  execution,
																				  ast_line,
																				  statement.get_cir_code(),
																				  location.get_cir_code(),
																				  parameter))
		return

	def __write_pattern__(self, pattern: KCPattern, pattern_index: int):
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
			for pattern in self.space.get_min_patterns():
				pattern_index += 1
				self.__write_pattern__(pattern, pattern_index)
				self.writer.write("\n")
		return

	''' best patterns '''

	def write_best_patterns(self, output_file: str, exe_or_mut: bool, uk_or_cc: bool):
		"""
		:param output_file:
		:param exe_or_mut:
		:param uk_or_cc:
		:return:
		"""
		with open(output_file, 'w') as writer:
			self.writer = writer
			mutant_best_patterns = self.space.get_best_patterns(exe_or_mut, uk_or_cc)
			for mutant, best_pattern in mutant_best_patterns.items():
				mutant_id = mutant.get_mut_id()
				result = self.space.classifier.__classify__(mutant)
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
		document = self.space.get_document()
		project = document.get_project()
		doc_lines = self.space.get_doc_executions()
		doc_mutants = self.space.get_doc_mutants()
		classifier = self.space.classifier

		''' summary tests mutants over_score valid_score '''
		self.writer.write("@Summary\n")
		test_number = None
		if classifier.has_tests():
			test_number = len(self.space.classifier.get_tests())
		self.writer.write("\t{} := {}\n".format("Tests", test_number))
		self.writer.write("\t{} := {}\n".format("Lines", len(doc_lines)))
		self.writer.write("\t{} := {}\n".format("Mutants", len(doc_mutants)))
		killed, over_score, valid_score = project.evaluation.evaluate_mutation_score(doc_mutants,
																					 classifier.get_tests())
		self.writer.write(
			"\t{} := {}%\n".format("over_score", KCPatternWriter.__percentage__(over_score)))
		self.writer.write(
			"\t{} := {}%\n".format("valid_score", KCPatternWriter.__percentage__(valid_score)))
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
													  KCPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_lines, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line", total, support,
													  KCPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_mutants, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant", total, support,
													  KCPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_mutants, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant", total, support,
													  KCPatternWriter.__percentage__(confidence)))
		self.writer.write("\n")

		''' matching precision recall f1_score '''
		pat_lines, pat_mutants = self.space.get_pat_executions(), self.space.get_pat_mutants()
		doc_uk_lines = classifier.select(doc_lines, True)
		doc_cc_lines = classifier.select(doc_lines, False)
		doc_uk_mutants = classifier.select(doc_mutants, True)
		doc_cc_mutants = classifier.select(doc_mutants, False)
		self.writer.write("@Matching\n")
		self.writer.write("\tTitle\tprecision(%)\trecall(%)\tf1_score\n")
		precision, recall, f1_score = KCPatternWriter.__prf_evaluation__(doc_uk_lines, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Line",
													  KCPatternWriter.__percentage__(precision),
													  KCPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = KCPatternWriter.__prf_evaluation__(doc_cc_lines, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line",
													  KCPatternWriter.__percentage__(precision),
													  KCPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = KCPatternWriter.__prf_evaluation__(doc_uk_mutants, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant",
													  KCPatternWriter.__percentage__(precision),
													  KCPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = KCPatternWriter.__prf_evaluation__(doc_cc_mutants, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant",
													  KCPatternWriter.__percentage__(precision),
													  KCPatternWriter.__percentage__(recall),
													  f1_score))
		self.writer.write("\n")

		''' patterns length lines mutants uk_line(%) cc_line(%) uk_mutant(%) cc_mutant(%) '''
		self.writer.write("@Patterns\n")
		self.writer.write("\tIndex\tLength\tLines\tMutants\tUK_Lines(%)\tCC_Lines(%)\tUK_Mutants(%)\tCC_Mutants(%)\n")
		index = 0
		for pattern in self.space.get_min_patterns():
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
									 KCPatternWriter.__percentage__(uk_line_confidence),
									 KCPatternWriter.__percentage__(cc_line_confidence),
									 KCPatternWriter.__percentage__(uk_mutant_confidence),
									 KCPatternWriter.__percentage__(cc_mutant_confidence)))
		self.writer.write("\n")

		''' optimization patterns uk_line_optimization cc_line_optimization ... '''
		self.writer.write("@Optimizer\n")
		patterns_number = len(self.space.get_min_patterns())
		self.writer.write("\t{} := {}%\n".format("UK_LINE_OPTIMIZE",
												 KCPatternWriter.__proportion__(patterns_number,
																						  len(doc_uk_lines))))
		self.writer.write("\t{} := {}%\n".format("CC_LINE_OPTIMIZE",
												 KCPatternWriter.__proportion__(patterns_number,
																						  len(doc_cc_lines))))
		self.writer.write("\t{} := {}%\n".format("UK_MUTA_OPTIMIZE",
												 KCPatternWriter.__proportion__(patterns_number,
																						  len(doc_uk_mutants))))
		self.writer.write("\t{} := {}%\n".format("CC_MUTA_OPTIMIZE",
												 KCPatternWriter.__proportion__(patterns_number,
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


def mining_patterns(document: jcmuta.SymbolicDocument, classifier_tests, exe_or_mut: bool, uk_or_cc: bool,
					min_support: int, max_confidence: float, max_length: int, output_directory: str):
	"""
	:param document: it provides lines and mutations in the program
	:param classifier_tests: used to generate classifier of mutation
	:param exe_or_mut: true to take line as sample or false to take mutant as sample
	:param uk_or_cc: true to estimate on non-killed samples or false on coincidental correctness samples
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
			  "mutants with {}%({}%).".format(KCPatternWriter.__percentage__(over_score),
											  KCPatternWriter.__percentage__(valid_score)))

		generator = KCPatternMiner(exe_or_mut, uk_or_cc, min_support, max_confidence, max_length)
		patterns = generator.mine(document, classifier_tests)
		print("\t(2) Generate", len(patterns.get_all_patterns()), "patterns with", len(patterns.get_min_patterns()), "of minimal set from.")

		writer = KCPatternWriter(patterns)
		writer.write_patterns(os.path.join(output_directory, document.get_project().program.name + ".mpt"))
		writer.write_results(os.path.join(output_directory, document.get_project().program.name + ".mrt"))
		writer.write_best_patterns(os.path.join(output_directory, document.get_project().program.name + ".bpt"), exe_or_mut, uk_or_cc)
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def testing_project(directory: str, file_name: str,
					exe_or_mut: bool,
					uk_or_cc: bool,
					print_value: bool,
					none_directory: str,
					over_directory: str,
					test_directory: str,
					dyna_directory: str,
					dynamic_evaluation: bool):
	c_project = jcmuta.CProject(directory, file_name)

	docs = c_project.load_static_document(directory, print_value)
	selected_mutants = c_project.evaluation.select_mutants_by_classes(["STRP", "BTRP"])
	minimal_tests, __remained__ = c_project.evaluation.select_tests_for_mutants(selected_mutants)
	minimal_number = int(len(c_project.test_space.get_test_cases()) * 0.004)
	random_tests = c_project.evaluation.select_tests_for_random(minimal_number)
	selected_tests = minimal_tests | random_tests

	mining_patterns(docs, None, exe_or_mut, uk_or_cc, 2, 0.80, 1, os.path.join(none_directory))
	mining_patterns(docs, selected_tests, exe_or_mut, uk_or_cc, 20, 0.80, 1, os.path.join(test_directory))
	mining_patterns(docs, c_project.test_space.get_test_cases(), exe_or_mut, uk_or_cc, 100, 0.80, 1, os.path.join(over_directory))

	if dynamic_evaluation:
		docs = c_project.load_dynamic_document(directory, print_value)
		mining_patterns(docs, docs.get_test_cases(), exe_or_mut, uk_or_cc, 20, 0.80, 1, os.path.join(dyna_directory))
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	none_path = "/home/dzt2/Development/Data/patterns/none"
	test_path = "/home/dzt2/Development/Data/patterns/test"
	over_path = "/home/dzt2/Development/Data/patterns/over"
	dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for filename in os.listdir(prev_path):
		direct = os.path.join(prev_path, filename)
		testing_project(directory=direct, file_name=filename, exe_or_mut=True, uk_or_cc=True, print_value=False,
						none_directory=none_path, over_directory=over_path, test_directory=test_path,
						dyna_directory=dyna_path, dynamic_evaluation=False)
	print("\nTesting end for all...")

