"""
This file implements pattern mining to extract symbolic execution patterns for detecting, clustering and interpreting
mutation equivalence during testing.
"""


import os
import random
from typing import TextIO
import com.jcsa.libs.muta as jcmuta


UC_CLASS = "UC"		# not-covered symbolic execution
UI_CLASS = "UI"		# not-infected symbolic execution but covered
UP_CLASS = "UP"		# covered, infected but not propagate execution
KI_CLASS = "KI"		# killed execution


def get_rand_sample(samples):
	"""
	:param samples:
	:return: a sample that is randomly selected from the set
	"""
	length = len(samples)
	counter = random.randint(0, length)
	selected_sample = None
	for sample in samples:
		selected_sample = sample
		counter = counter - 1
		if counter < 0:
			break
	return selected_sample


class SymbolicExecutionClassifier:
	"""
	It is used to classify the symbolic execution into equivalent (not-killed by test cases)
	or killable (killed by test cases)
	"""
	def __init__(self, classifier_tests):
		"""
		:param classifier_tests:
				(1) set of test cases being considered to classify mutants into killed or not
				(2) None if all test cases in space are considered
		"""
		self.classifier_tests = classifier_tests
		self.solutions = dict()			# sample --> [uc, ui, up, ki]
		return

	def has_classifier_tests(self):
		"""
		:return: True if test cases being considered to classify mutants into killed or not
		"""
		return self.classifier_tests is not None

	def get_classifier_tests(self):
		"""
		:return:
				(1) set of test cases being considered to classify mutants into killed or not
				(2) None if all test cases in space are considered
		"""
		return self.classifier_tests

	def __get_solution__(self, sample):
		"""
		:param sample: Mutant or SymbolicExecution
		:return:	(1)	UC: number of executions not covered
					(2) UI: number of executions being covered but not infected
					(3) UP: number of executions being infected but not propagate
					(4) KI: number of executions being killed
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
		:param sample: Mutant or SymbolicExecution
		:return: update [uc, ui, up, ki] in self.solutions
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		if isinstance(sample, jcmuta.SymbolicExecution):
			sample: jcmuta.SymbolicExecution
			s_result = sample.get_mutant().get_result()
			w_result = sample.get_mutant().get_weak_mutant().get_result()
			c_result = sample.get_mutant().get_coverage_mutant().get_result()
			if sample.has_test_case():
				if s_result.is_killed_by(sample.get_test_case()):
					ki += 1
				elif w_result.is_killed_by(sample.get_test_case()):
					up += 1
				elif c_result.is_killed_by(sample.get_test_case()):
					ui += 1
				else:
					uc += 1
			else:
				uc, ui, up, ki = self.__solve__(sample.get_mutant())
		else:
			sample: jcmuta.Mutant
			s_result = sample.get_result()
			w_result = sample.get_weak_mutant().get_result()
			c_result = sample.get_coverage_mutant().get_result()
			if self.classifier_tests is None:
				if s_result.is_killable():
					ki += 1
				elif w_result.is_killable():
					up += 1
				elif c_result.is_killable():
					ui += 1
				else:
					uc += 1
			else:
				for test_case in self.classifier_tests:
					if s_result.is_killed_by(test_case):
						ki += 1
					elif w_result.is_killed_by(test_case):
						up += 1
					elif c_result.is_killed_by(test_case):
						ui += 1
					else:
						uc += 1
		self.solutions[sample] = (uc, ui, up, ki)
		return

	def __solve__(self, sample):
		"""
		:param sample: Mutant or SymbolicExecution
		:return:	(1)	UC: number of executions not covered
					(2) UI: number of executions being covered but not infected
					(3) UP: number of executions being infected but not propagate
					(4) KI: number of executions being killed
		"""
		if not(sample in self.solutions):
			self.__set_solution__(sample)
		return self.__get_solution__(sample)

	def __classify__(self, sample):
		"""
		:param sample: Mutant or SymbolicExecution
		:return: UC | UI | UP | KI
		"""
		uc, ui, up, ki = self.__solve__(sample)
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
		:return: uc, ui, up, ki, uk, cc
				(1) uc: number of samples not covered
				(2) ui: number of samples not infected but covered
				(3) up: number of samples not propagate but infected
				(4) ki: number of samples being killed
				(5) uk: number of samples not killed (uc + ui + up)
				(6) cc: number of samples not killed but reached (ui + up)
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__solve__(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def classify(self, samples):
		"""
		:param samples: set of Mutant or SymbolicExecution
		:return: UC|UI|UP|KI ==> set of samples w.r.t. type
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
		:param samples: set of Mutant or SymbolicExecution
		:param uk_or_cc: true to take non-killed samples as support or false to count non-killed (reached) samples
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
		:param samples: set of Mutant or SymbolicExecution
		:param uk_or_cc: true to select non-killed or false to select coincidental correct samples
		:return: set of samples being selected from inputs set
		"""
		results = self.classify(samples)
		selected_samples = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selected_samples = selected_samples | results[UC_CLASS]
		return selected_samples


class SymbolicExecutionPattern:
	"""
	The pattern of symbolic execution defines a set of features.
	"""
	def __init__(self, classifier: SymbolicExecutionClassifier):
		"""
		:param classifier: used to estimate the pattern
		"""
		self.classifier = classifier
		self.executions = set()
		self.mutants = set()
		self.feature_words = list()
		return

	def get_classifier(self):
		"""
		:return: used to estimate the samples in pattern
		"""
		return self.classifier

	def get_feature_words(self):
		"""
		:return: words of features to define this pattern
		"""
		return self.feature_words

	def __str__(self):
		return str(self.feature_words)

	def __len__(self):
		"""
		:return: length of the features in the pattern
		"""
		return len(self.feature_words)

	def get_features(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: structural features matched in symbolic execution
		"""
		features = list()
		for feature_word in self.feature_words:
			features.append(jcmuta.SymbolicExecutionFeature.parse(project, feature_word))
		return features

	def get_executions(self):
		"""
		:return: the symbolic executions matched with the pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the mutants of symbolic executions being matched
		"""
		return self.mutants

	def __match__(self, execution: jcmuta.SymbolicExecution):
		"""
		:param execution:
		:return:
		"""
		for feature_word in self.feature_words:
			if not(feature_word in execution.get_feature_words()):
				return False
		return True

	def set_executions(self, executions):
		"""
		:param executions: set of SymbolicExecution(s)
		:return:
		"""
		self.executions.clear()
		self.mutants.clear()
		for execution in executions:
			execution: jcmuta.SymbolicExecution
			if self.__match__(execution):
				self.executions.add(execution)
				self.mutants.add(execution.get_mutant())
		return

	def get_samples(self, exec_or_mutant: bool):
		"""
		:param exec_or_mutant: true to select self.executions or false to select self.mutants
		:return:
		"""
		if exec_or_mutant:
			return self.executions
		else:
			return self.mutants

	def counting(self, exec_or_mutant: bool):
		"""
		:param exec_or_mutant: true to select self.executions or false to select self.mutants
		:return: uc, ui, up, ki, uk, cc
				(1) uc: number of samples not covered
				(2) ui: number of samples not infected but covered
				(3) up: number of samples not propagate but infected
				(4) ki: number of samples being killed
				(5) uk: number of samples not killed (uc + ui + up)
				(6) cc: number of samples not killed but reached (ui + up)
		"""
		return self.classifier.counting(self.get_samples(exec_or_mutant))

	def classify(self, exec_or_mutant: bool):
		"""
		:param exec_or_mutant: true to select self.executions or false to select self.mutants
		:return: UC|UI|UP|KI ==> set of samples w.r.t. type
		"""
		return self.classifier.classify(self.get_samples(exec_or_mutant))

	def estimate(self, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param exec_or_mutant: true to select self.executions or false to select self.mutants
		:param uk_or_cc: true to take non-killed as support or false to select CC samples
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(exec_or_mutant), uk_or_cc)

	def get_child(self, feature_word: str):
		"""
		:param feature_word:
		:return: child pattern extended from this pattern by adding one external word or
					the parent pattern itself if the feature word exists in current one
		"""
		feature_word = feature_word.strip()
		if len(feature_word) > 0 and not(feature_word in self.feature_words):
			child = SymbolicExecutionPattern(self.classifier)
			for old_word in self.feature_words:
				child.feature_words.append(old_word)
			child.feature_words.append(feature_word)
			child.feature_words.sort()
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: true if the executions matched by this pattern include all those matched in input pattern
		"""
		pattern: SymbolicExecutionPattern
		for execution in pattern.get_executions():
			if not(execution in self.executions):
				return False
		return True


class SymbolicExecutionPatterns:
	"""
	It preserves the patterns generated from symbolic execution document
	"""
	def __init__(self, document: jcmuta.SymbolicExecutionDocument,
				 classifier: SymbolicExecutionClassifier,
				 generated_patterns):
		"""
		:param document: document to provide symbolic executions in mutation testing
		:param classifier: used to count, classify and estimate the patterns
		:param generated_patterns: set of patterns generated from SymbolicExecutionPatternGenerator
		"""
		''' 1. generate the data structure of document part '''
		self.document = document
		self.doc_executions = set()
		self.doc_mutants = set()
		for execution in document.get_executions():
			execution: jcmuta.SymbolicExecution
			self.doc_executions.add(execution)
			self.doc_mutants.add(execution.get_mutant())

		''' 2. generate the symbolic patterns '''
		self.all_patterns = set()
		self.pat_executions = set()
		self.pat_mutants = set()
		for pattern in generated_patterns:
			pattern: SymbolicExecutionPattern
			self.all_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jcmuta.SymbolicExecution
				self.pat_executions.add(execution)
				self.pat_mutants.add(execution.get_mutant())
		self.subsume_patterns = self.__extract_subsume_patterns__()
		self.minimal_patterns = self.__extract_minimal_patterns__()

		self.classifier = classifier
		return

	def __extract_subsume_patterns__(self):
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
			if subsume_pattern is not None:
				subsume_pattern: SymbolicExecutionPattern
				minimal_patterns.add(subsume_pattern)
		return minimal_patterns

	def __extract_minimal_patterns__(self):
		"""
		:return: the minimal set of patterns to cover all the executions
		"""
		remain_patterns, remove_patterns, minimal_patterns = set(), set(), set()
		remain_executions = set()
		for pattern in self.all_patterns:
			pattern: SymbolicExecutionPattern
			remain_patterns.add(pattern)
			for execution in pattern.get_executions():
				execution: jcmuta.SymbolicExecution
				remain_executions.add(execution)
		while len(remain_executions) > 0 and len(remain_patterns) > 0:
			selected_pattern = get_rand_sample(remain_patterns)
			selected_pattern: SymbolicExecutionPattern
			for execution in selected_pattern.get_executions():
				if execution in remain_executions:
					remain_executions.remove(execution)
					minimal_patterns.add(selected_pattern)
			remove_patterns.clear()
			for pattern in remain_patterns:
				common_part = pattern.get_executions().intersection(remain_executions)
				if len(common_part) == 0:
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
		return minimal_patterns

	@staticmethod
	def __extract_best_pattern_in__(patterns, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param patterns: set of patterns from which the best pattern is selected
		:param exec_or_mutant: true to take SymbolicExecution or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: the pattern best matching the requirement with largest support, confidence and minimal length
		"""
		remain_patterns = set()
		for pattern in patterns:
			pattern: SymbolicExecutionPattern
			remain_patterns.add(pattern)

		length = max(int(len(remain_patterns) * 0.50), 1)
		while len(remain_patterns) > length:
			worst_pattern, worst_length = None, 0
			for pattern in remain_patterns:
				word_length = len(pattern.get_feature_words())
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

	def get_document(self):
		return self.document

	def get_doc_executions(self):
		return self.doc_executions

	def get_doc_mutants(self):
		return self.doc_mutants

	def get_classifier(self):
		return self.classifier

	def get_all_patterns(self):
		return self.all_patterns

	def get_subsume_patterns(self):
		return self.subsume_patterns

	def get_minimal_patterns(self):
		return self.minimal_patterns

	def get_best_patterns(self, exec_or_mutant: bool, uk_or_cc: bool):
		"""
		:param exec_or_mutant: true to take SymbolicExecution or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: mutant to pattern
		"""
		mutant_patterns = dict()
		for pattern in self.all_patterns:
			for mutant in pattern.get_mutants():
				mutant: jcmuta.Mutant
				if not(mutant in mutant_patterns):
					mutant_patterns[mutant] = set()
				mutant_patterns[mutant].add(pattern)
		results = dict()
		for mutant, patterns in mutant_patterns.items():
			best_pattern = SymbolicExecutionPatterns.__extract_best_pattern_in__(patterns, exec_or_mutant, uk_or_cc)
			results[mutant] = best_pattern
		return results

	def get_pat_executions(self):
		return self.pat_executions

	def get_pat_mutants(self):
		return self.pat_mutants


class SymbolicExecutionPatternGenerator:
	"""
	It implements pattern mining algorithm to generate symbolic execution patterns from document
	"""
	def __init__(self, exec_or_mutant: bool, uk_or_cc: bool, min_support: int, max_confidence: float, max_length: int):
		"""
		:param exec_or_mutant: true to take SymbolicExecution as sample or false to take Mutant as well
		:param uk_or_cc: true to estimate on non-killed samples or false to take coincidental correct samples
		:param min_support: minimal number of samples that support the patterns
		:param max_confidence: maximal confidence once achieved the pattern generation will be ter
		"""
		self.exec_or_mutant = exec_or_mutant
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.__classifier__ = None  # Used to create MutationPattern and estimate them
		self.__patterns__ = dict()  # String ==> SymbolicExecutionPattern
		self.__solution__ = dict()  # SymbolicExecutionPattern ==> [total, support, confidence]
		return

	def __root__(self, document: jcmuta.SymbolicExecutionDocument, word: str):
		"""
		:param document:
		:param word:
		:return:
		"""
		root = SymbolicExecutionPattern(self.__classifier__)
		root = root.get_child(word)
		if not(str(root) in self.__patterns__):
			self.__patterns__[str(root)] = root
			root.set_executions(document.get_executions())
		root = self.__patterns__[str(root)]
		root: SymbolicExecutionPattern
		return root

	def __child__(self, parent: SymbolicExecutionPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: unique child pattern extended from the parent
		"""
		child = parent.get_child(word)
		if child != parent:
			if not(str(child) in self.__patterns__):
				self.__patterns__[str(child)] = child
				child.set_executions(parent.get_executions())
			child = self.__patterns__[str(child)]
			child: SymbolicExecutionPattern
			return child
		else:
			return parent

	def __generate__(self, parent: SymbolicExecutionPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		if not(parent in self.__solution__):
			total, support, confidence = parent.estimate(self.exec_or_mutant, self.uk_or_cc)
			self.__solution__[parent] = (total, support, confidence)
		solution = self.__solution__[parent]
		support = solution[1]
		confidence = solution[2]
		if len(parent.get_feature_words()) < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__generate__(child, words)
		return

	def __output__(self, document: jcmuta.SymbolicExecutionDocument):
		"""
		:param document:
		:return: SymbolicExecutionPatterns
		"""
		good_patterns = set()
		for pattern, solution in self.__solution__.items():
			pattern: SymbolicExecutionPattern
			support = solution[1]
			confidence = solution[2]
			if support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		return SymbolicExecutionPatterns(document, self.__classifier__, good_patterns)

	def generate(self, document: jcmuta.SymbolicExecutionDocument, classifier_tests):
		"""
		:param document: it provides all the lines and mutants for analysis
		:param classifier_tests: test cases to create mutation classifier
		:return: SymbolicExecutionPatterns
		"""
		self.__patterns__.clear()
		self.__solution__.clear()
		self.__classifier__ = SymbolicExecutionClassifier(classifier_tests)

		init_lines = self.__classifier__.select(document.get_executions(), self.uk_or_cc)
		for init_line in init_lines:
			init_line: jcmuta.SymbolicExecution
			words = init_line.get_feature_words()
			for word in words:
				root = self.__root__(document, word)
				self.__generate__(root, words)

		patterns = self.__output__(document)
		self.__patterns__.clear()
		self.__solution__.clear()
		self.__classifier__ = None

		return patterns


class SymbolicExecutionPatternWriter:
	"""
	It writes information of SymbolicExecutionPattern to output file
	"""
	def __init__(self, patterns: SymbolicExecutionPatterns):
		"""
		:param patterns:
		"""
		self.patterns = patterns
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
		return SymbolicExecutionPatternWriter.__percentage__(ratio)

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

	''' patterns writer '''

	def __write_pattern_count__(self, pattern: SymbolicExecutionPattern):
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
						  format("Length", len(pattern.get_feature_words()),
								 "Lines", len(pattern.get_executions()),
								 "Mutants", len(pattern.get_mutants())))

		''' counter: title UC UI UP KI UK CC '''
		self.writer.write("\n\t@Counting.\n")
		self.writer.write("\tSample\tUC\tUI\tUP\tKI\tUK\tCC\n")
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Line", uc, ui, up, ki, uk, cc))
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Line", uc, ui, up, ki, uk, cc))

		''' estimate: title total support confidence '''
		self.writer.write("\n\t@Estimate.\n")
		self.writer.write("\tTitle\ttotal\tsupport\tconfidence(%)\n")
		total, support, confidence = pattern.estimate(True, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Line", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(True, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		return

	def __write_pattern_lines__(self, pattern: SymbolicExecutionPattern):
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

	def __write_pattern_words__(self, pattern: SymbolicExecutionPattern):
		"""
		:param pattern:
		:return: index type execution line statement location parameter
		"""
		self.writer: TextIO
		self.writer.write("\t@Words\n")
		self.writer.write("\tIndex\tType\tExecution\tLine\tStatement\tLocation\tParameter\n")
		index = 0
		for annotation in pattern.get_features(self.patterns.document.get_project()):
			index += 1
			annotation_type = annotation.get_feature_type()
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

	def __write_pattern__(self, pattern: SymbolicExecutionPattern, pattern_index: int):
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
			for pattern in self.patterns.get_subsume_patterns():
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
			mutant_best_patterns = self.patterns.get_best_patterns(line_or_mutant, uk_or_cc)
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
		if classifier.has_classifier_tests():
			test_number = len(self.patterns.classifier.get_classifier_tests())
		self.writer.write("\t{} := {}\n".format("Tests", test_number))
		self.writer.write("\t{} := {}\n".format("Lines", len(doc_lines)))
		self.writer.write("\t{} := {}\n".format("Mutants", len(doc_mutants)))
		killed, over_score, valid_score = project.evaluation.evaluate_mutation_score(doc_mutants, classifier.get_classifier_tests())
		self.writer.write("\t{} := {}%\n".format("over_score", SymbolicExecutionPatternWriter.__percentage__(over_score)))
		self.writer.write("\t{} := {}%\n".format("valid_score", SymbolicExecutionPatternWriter.__percentage__(valid_score)))
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
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_lines, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_mutants, True)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		total, support, confidence = classifier.estimate(doc_mutants, False)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant", total, support,
													  SymbolicExecutionPatternWriter.__percentage__(confidence)))
		self.writer.write("\n")

		''' matching precision recall f1_score '''
		pat_lines, pat_mutants = self.patterns.get_pat_executions(), self.patterns.get_pat_mutants()
		doc_uk_lines = classifier.select(doc_lines, True)
		doc_cc_lines = classifier.select(doc_lines, False)
		doc_uk_mutants = classifier.select(doc_mutants, True)
		doc_cc_mutants = classifier.select(doc_mutants, False)
		self.writer.write("@Matching\n")
		self.writer.write("\tTitle\tprecision(%)\trecall(%)\tf1_score\n")
		precision, recall, f1_score = SymbolicExecutionPatternWriter.__prf_evaluation__(doc_uk_lines, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Line",
													  SymbolicExecutionPatternWriter.__percentage__(precision),
													  SymbolicExecutionPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = SymbolicExecutionPatternWriter.__prf_evaluation__(doc_cc_lines, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Line",
													  SymbolicExecutionPatternWriter.__percentage__(precision),
													  SymbolicExecutionPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = SymbolicExecutionPatternWriter.__prf_evaluation__(doc_uk_mutants, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("UK-Mutant",
													  SymbolicExecutionPatternWriter.__percentage__(precision),
													  SymbolicExecutionPatternWriter.__percentage__(recall),
													  f1_score))
		precision, recall, f1_score = SymbolicExecutionPatternWriter.__prf_evaluation__(doc_cc_mutants, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\n".format("CC-Mutant",
													  SymbolicExecutionPatternWriter.__percentage__(precision),
													  SymbolicExecutionPatternWriter.__percentage__(recall),
													  f1_score))
		self.writer.write("\n")

		''' patterns length lines mutants uk_line(%) cc_line(%) uk_mutant(%) cc_mutant(%) '''
		self.writer.write("@Patterns\n")
		self.writer.write("\tIndex\tLength\tLines\tMutants\tUK_Lines(%)\tCC_Lines(%)\tUK_Mutants(%)\tCC_Mutants(%)\n")
		index = 0
		for pattern in self.patterns.get_minimal_patterns():
			index += 1
			length = len(pattern.get_feature_words())
			lines_number = len(pattern.get_executions())
			mutants_number = len(pattern.get_mutants())
			total, uk_line_support, uk_line_confidence = pattern.estimate(True, True)
			total, cc_line_support, cc_line_confidence = pattern.estimate(True, False)
			total, uk_mutant_support, uk_mutant_confidence = pattern.estimate(False, True)
			total, cc_mutant_support, cc_mutant_confidence = pattern.estimate(False, False)
			self.writer.write("\t{}\t{}\t{}\t{}\t{}%\t{}%\t{}%\t{}%\n".
							  format(index, length, lines_number, mutants_number,
									 SymbolicExecutionPatternWriter.__percentage__(uk_line_confidence),
									 SymbolicExecutionPatternWriter.__percentage__(cc_line_confidence),
									 SymbolicExecutionPatternWriter.__percentage__(uk_mutant_confidence),
									 SymbolicExecutionPatternWriter.__percentage__(cc_mutant_confidence)))
		self.writer.write("\n")

		''' optimization patterns uk_line_optimization cc_line_optimization ... '''
		self.writer.write("@Optimizer\n")
		patterns_number = len(self.patterns.get_minimal_patterns())
		self.writer.write("\t{} := {}%\n".format("UK_LINE_OPTIMIZE",
												 SymbolicExecutionPatternWriter.__proportion__(patterns_number,
																					  len(doc_uk_lines))))
		self.writer.write("\t{} := {}%\n".format("CC_LINE_OPTIMIZE",
												 SymbolicExecutionPatternWriter.__proportion__(patterns_number,
																					  len(doc_cc_lines))))
		self.writer.write("\t{} := {}%\n".format("UK_MUTA_OPTIMIZE",
												 SymbolicExecutionPatternWriter.__proportion__(patterns_number,
																					  len(doc_uk_mutants))))
		self.writer.write("\t{} := {}%\n".format("CC_MUTA_OPTIMIZE",
												 SymbolicExecutionPatternWriter.__proportion__(patterns_number,
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


def mining_patterns(document: jcmuta.SymbolicExecutionDocument, classifier_tests, line_or_mutant: bool,
					uk_or_cc: bool, min_support: int, max_confidence: float, max_length: int, output_directory: str):
	"""
	:param document: it provides lines and mutations in the program
	:param classifier_tests: used to generate classifier of mutation
	:param line_or_mutant: true to take line as sample or false to take mutant as sample
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
			  "mutants with {}%({}%).".format(SymbolicExecutionPatternWriter.__percentage__(over_score),
											  SymbolicExecutionPatternWriter.__percentage__(valid_score)))

		generator = SymbolicExecutionPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_confidence, max_length)
		patterns = generator.generate(document, classifier_tests)
		print("\t(2) Generate", len(patterns.get_all_patterns()), "patterns with", len(patterns.get_minimal_patterns()), "of minimal set from.")

		writer = SymbolicExecutionPatternWriter(patterns)
		writer.write_patterns(os.path.join(output_directory, document.get_project().program.name + ".mpt"))
		writer.write_results(os.path.join(output_directory, document.get_project().program.name + ".mrt"))
		writer.write_best_patterns(os.path.join(output_directory, document.get_project().program.name + ".bpt"), line_or_mutant, uk_or_cc)
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def testing_project(directory: str, file_name: str, none_directory: str, over_directory: str,
					test_directory: str, dyna_directory: str, dynamic_evaluation: bool):
	c_project = jcmuta.CProject(directory, file_name)

	docs = c_project.load_static_document(directory)
	selected_mutants = c_project.evaluation.select_mutants_by_classes(["STRP", "BTRP"])
	minimal_tests, __remained__ = c_project.evaluation.select_tests_for_mutants(selected_mutants)
	minimal_number = int(len(c_project.test_space.get_test_cases()) * 0.004)
	random_tests = c_project.evaluation.select_tests_for_random(minimal_number)
	selected_tests = minimal_tests | random_tests

	mining_patterns(docs, None, True, True, 2, 0.80, 1, os.path.join(none_directory))
	mining_patterns(docs, selected_tests, True, True, 20, 0.80, 1, os.path.join(test_directory))
	mining_patterns(docs, c_project.test_space.get_test_cases(), True, True, 100, 0.80, 1, os.path.join(over_directory))

	if dynamic_evaluation:
		docs = c_project.load_dynamic_document(directory)
		mining_patterns(docs, docs.get_test_cases(), True, True, 20, 0.80, 1, os.path.join(dyna_directory))

	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	none_path = "/home/dzt2/Development/Data/patterns/none"
	test_path = "/home/dzt2/Development/Data/patterns/test"
	over_path = "/home/dzt2/Development/Data/patterns/over"
	dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for fname in os.listdir(prev_path):
		dir = os.path.join(prev_path, fname)
		testing_project(dir, fname, none_path, over_path, test_path, dyna_path, False)
	print("\nTesting end for all...")

