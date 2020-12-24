"""
This file implements the classification, prediction and pattern mining algorithms.
"""


import os
from typing import TextIO
import com.jcsa.mark.muta as cmuta


UC_CLASS, UI_CLASS, UP_CLASS, KI_CLASS = "UC", "UI", "UP", "KI"


class MutationClassifier:
	"""
	It classifies the category of Mutant or MutantExecutionLine in document
	"""
	def __init__(self, classifier_tests):
		"""
		:param classifier_tests:
					(1) Set as None if the classifier counts on sample itself (either Mutant or MutantExecutionLine)
					(2) Set of TestCases if the classifier counts on test results of each sample.
		"""
		self.classifier_tests = classifier_tests
		self.solutions = dict()		# sample --> [uc, ui, up, ki]
		return

	def has_tests(self):
		"""
		:return: whether the tests of classifier is set
		"""
		return self.classifier_tests is not None

	def get_tests(self):
		"""
		:return: the set of test cases to set for the classifier or None if we count sample itself rather than
				its test results.
		"""
		return self.classifier_tests

	def __get_solution__(self, sample):
		"""
		:param sample:
		:return: uc, ui, up, ki from existing solution w.r.t. the sample
		"""
		counter = self.solutions[sample]
		uc = counter[0]
		ui = counter[1]
		up = counter[2]
		ki = counter[3]
		uc: int
		ui: int
		up: int
		ki: int
		return uc, ui, up, ki

	def __set_solution__(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: uc, ui, up, ki
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		if isinstance(sample, cmuta.MutationFeatureLine):	# sample as MutantExecutionLine
			sample: cmuta.MutationFeatureLine
			if sample.has_test_case():						# based on one test case in the execution line
				s_result = sample.get_mutant().get_result()
				w_result = sample.get_mutant().get_weak_mutant().get_result()
				c_result = sample.get_mutant().get_coverage_mutant().get_result()
				if s_result.is_killed_by(sample.get_test_case()):
					ki += 1
				elif w_result.is_killed_by(sample.get_test_case()):
					up += 1
				elif c_result.is_killed_by(sample.get_test_case()):
					ui += 1
				else:
					uc += 1
			else:											# restore to the mutation sample over test set
				uc, ui, up, ki = self.__solve__(sample.get_mutant())
		else:												# sample as Mutant
			sample: cmuta.Mutant
			s_result = sample.get_result()
			w_result = sample.get_weak_mutant().get_result()
			c_result = sample.get_coverage_mutant().get_result()
			if self.classifier_tests is None:				# counted on the sample itself rather than results
				if s_result.is_killable():
					ki += 1
				elif w_result.is_killable():
					up += 1
				elif c_result.is_killable():
					ui += 1
				else:
					uc += 1
			else:											# counted on sample's test results
				for test in self.classifier_tests:
					if s_result.is_killed_by(test):
						ki += 1
					elif w_result.is_killed_by(test):
						up += 1
					elif c_result.is_killed_by(test):
						ui += 1
					else:
						uc += 1
		self.solutions[sample] = (uc, ui, up, ki)
		return uc, ui, up, ki

	def __solve__(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: 	uc (number of results not covering),
					ui (number of results not infected),
					up (number of results not propagate),
					ki (number of results that kill it)
		"""
		if not(sample in self.solutions):
			self.__set_solution__(sample)
		return self.__get_solution__(sample)

	def __classify__(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: UC, UI, UP, KI as string type
		"""
		uc, ui, up, ki = self.__solve__(sample)
		if ki > 0:
			return KI_CLASS
		elif up > 0:
			return UP_CLASS
		elif ui > 0:
			return UI_CLASS
		else:
			return UP_CLASS

	def classify(self, samples):
		"""
		:param samples: set of Mutant or MutantExecutionLine
		:return: String{UC, UI, UP, KI} ==> set[Mutant|MutantExecutionLine]
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			key = self.__classify__(sample)
			if not(key in results):
				results[key] = set()
			results[key].add(sample)
		return results

	def counting(self, samples):
		"""
		:param samples: set of Mutant or MutantExecutionLine
		:return: 	uc (number of samples or results not covered)
					ui (number of samples or results not infected)
					up (number of samples or results not impacts)
					ki (number of samples or results being killed)
					uk (number of samples or results not killed)
					cc (number of samples or results not killed but covered)
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__solve__(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def estimate(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or MutantExecutionLine
		:param uk_or_cc: true to estimate on non-killed samples (or results) or false to estimate on
						coincidental correct samples (or results).
		:return: total, support, confidence
		"""
		uc, ui, up, ki, uk, cc = self.counting(samples)
		if uk_or_cc:
			support = uk
		else:
			support = cc
		total = support + ki
		confidence = 0.0
		if support > 0:
			confidence = support / (total + 0.0)
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutants or MutantExecutionLine
		:param uk_or_cc: true to select non-killed samples or false to select coincidental correctness samples
		:return: set[Mutant|MutantExecutionLine]
		"""
		results = self.classify(samples)
		selected_samples = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selected_samples  = selected_samples | results[UC_CLASS]
		return selected_samples


class MutationPattern:
	"""
	The mutation pattern is modeled as a set of feature words in the mutant execution line.
	"""

	''' constructor '''

	def __init__(self, classifier: MutationClassifier):
		"""
		:param classifier: used to estimate the lines or mutants in the pattern
		"""
		self.words = list()
		self.lines = set()
		self.mutants = set()
		self.classifier = classifier
		return

	def get_classifier(self):
		return self.classifier

	''' features getter '''

	def get_words(self):
		"""
		:return: the set of feature words that match the lines of mutants in the pattern
		"""
		return self.words

	def __str__(self):
		return str(self.words)

	def __len__(self):
		"""
		:return: length of pattern is length of its words
		"""
		return len(self.words)

	def get_features(self, project: cmuta.CProject):
		"""
		:param project:
		:return: annotations parsed from the words in the pattern based on the program provided
		"""
		features = list()
		for word in self.words:
			features.append(cmuta.MutationFeature.parse(project, word))
		return features

	''' sample getters '''

	def get_lines(self):
		"""
		:return: execution lines matched with this pattern
		"""
		return self.lines

	def get_mutants(self):
		"""
		:return: mutants of which lines matched with this pattern
		"""
		return self.mutants

	def get_samples(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to select execution lines or false to select mutants matching with this pattern
		:return: execution lines matched with the pattern (True) or mutants matched with the pattern (False)
		"""
		if line_or_mutant:
			return self.lines
		else:
			return self.mutants

	def __match__(self, line: cmuta.MutationFeatureLine):
		"""
		:param line:
		:return: whether the line matches with this pattern
		"""
		for word in self.words:
			if not(word in line.get_feature_words()):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: the collection of execution lines which are matched and update the pattern
		:return:
		"""
		self.lines.clear()
		self.mutants.clear()
		for line in lines:
			line: cmuta.MutationFeatureLine
			if self.__match__(line):
				self.lines.add(line)
				self.mutants.add(line.get_mutant())
		return

	''' estimation '''

	def classify(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to classify on lines or mutants
		:return: mapping from String to set of MutantExecutionLine (true) or Mutant (false)
		"""
		return self.classifier.classify(self.get_samples(line_or_mutant))

	def counting(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to counted on lines or mutants
		:return: 	uc (number of samples or results not covered)
					ui (number of samples or results not infected)
					up (number of samples or results not impacts)
					ki (number of samples or results being killed)
					uk (number of samples or results not killed)
					cc (number of samples or results not killed but covered)
		"""
		return self.classifier.counting(self.get_samples(line_or_mutant))

	def estimate(self, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param line_or_mutant: true to counted on lines or mutants
		:param uk_or_cc: true to estimate on non-killed or false to estimate on coincidental correct samples
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(line_or_mutant), uk_or_cc)

	''' relationships '''

	def get_child(self, word: str):
		"""
		:param word: additional word appended to extend the pattern for generating its child
		:return: the child pattern extended from this pattern by adding one word or itself if the word is None or
				belongs to the existing words in the pattern
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
		:return: true if the lines in this line include the lines of the pattern
		"""
		pattern: MutationPattern
		for line in pattern.get_lines():
			if not(line in self.lines):
				return False
		return True


class MutationPatterns:
	"""
	It represents the outputs of the generated patterns.
	"""
	def __init__(self, document: cmuta.MutationFeatureDocument, classifier: MutationClassifier, patterns):
		"""
		:param document: it provides all the lines and mutants provided from original program and project
		:param classifier: the classifier to classify and estimate the lines and mutants in the testing
		:param patterns: patterns generated and selected from the generator
		"""
		self.document = document
		self.doc_lines = set()
		self.doc_mutants = set()
		for line in document.get_feature_lines():
			line: cmuta.MutationFeatureLine
			self.doc_lines.add(line)
			self.doc_mutants.add(line.get_mutant())
		self.classifier = classifier
		self.patterns = set()
		self.pat_lines = set()
		self.pat_mutants = set()
		for pattern in patterns:
			pattern: MutationPattern
			self.patterns.add(pattern)
			for line in pattern.get_lines():
				line: cmuta.MutationFeatureLine
				self.pat_lines.add(line)
				self.pat_mutants.add(line.get_mutant())
		self.min_patterns = MutationPatterns.minimal_patterns_of(self.patterns)
		return

	''' document getters '''

	def get_document(self):
		return self.document

	def get_doc_lines(self):
		"""
		:return: the execution lines in the document
		"""
		return self.doc_lines

	def get_doc_mutants(self):
		"""
		:return: the collection of mutants in the document
		"""
		return self.doc_mutants

	''' patterns getter '''

	def get_patterns(self):
		return self.patterns

	def get_minimal_patterns(self):
		"""
		:return: the minimal set of patterns generated
		"""
		return self.min_patterns

	def get_pat_lines(self):
		"""
		:return: collection of lines matched by the output patterns
		"""
		return self.pat_lines

	def get_pat_mutants(self):
		"""
		:return: set of mutants matched by the output patterns
		"""
		return self.pat_mutants

	''' classifier getter '''

	def get_classifier(self):
		return self.classifier

	def get_classifier_tests(self):
		return self.classifier.get_tests()

	''' generator methods '''

	@staticmethod
	def minimal_patterns_of(patterns):
		"""
		:param patterns:
		:return: the minimal set of patterns that cover all the lines of the others in the inputs
		"""
		remain_patterns, removed_patterns, minimal_patterns = set(), set(), set()
		for pattern in patterns:
			pattern: MutationPattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			removed_patterns.clear()
			subsuming_pattern = None
			for pattern in remain_patterns:
				if subsuming_pattern is None:
					subsuming_pattern = pattern
					removed_patterns.add(pattern)
				elif pattern.subsume(subsuming_pattern):
					subsuming_pattern = pattern
					removed_patterns.add(pattern)
				elif subsuming_pattern.subsume(pattern):
					removed_patterns.add(pattern)
			if subsuming_pattern is None:
				break
			else:
				minimal_patterns.add(subsuming_pattern)
			for pattern in removed_patterns:
				remain_patterns.remove(pattern)
		return minimal_patterns

	@staticmethod
	def samples_to_patterns(patterns, line_or_mutant: bool):
		"""
		:param patterns:
		:param line_or_mutant: true to take MutantExecutionLine as key or Mutant as key
		:return: mapping from MutantExecutionLine or Mutant to set of MutationPattern(s)
		"""
		results = dict()
		for pattern in patterns:
			pattern: MutationPattern
			samples = pattern.get_samples(line_or_mutant)
			for sample in samples:
				if not(sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def best_patterns_from(patterns, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param patterns: set of patterns from which the best pattern is selected
		:param line_or_mutant: true to take MutantExecutionLine or false to take Mutant as sample
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
				word_length = len(pattern.get_words())
				if word_length >= worst_length:
					worst_pattern = pattern
					worst_length = word_length
			if worst_pattern is not None:
				remain_patterns.remove(worst_pattern)

		length = max(int(len(remain_patterns) * 0.50), 1)
		while len(remain_patterns) > length:
			worst_pattern, worst_confidence = None, 1.0
			for pattern in remain_patterns:
				total, support, confidence = pattern.estimate(line_or_mutant, uk_or_cc)
				if confidence <= worst_confidence:
					worst_confidence = confidence
					worst_pattern = pattern
			if worst_pattern is not None:
				remain_patterns.remove(worst_pattern)

		best_pattern, best_support = None, 0
		for pattern in remain_patterns:
			total, support, confidence = pattern.estimate(line_or_mutant, uk_or_cc)
			if support >= best_support:
				best_support = support
				best_pattern = pattern
		return best_pattern


class MutationPatternGenerator:
	"""
	It implements the generation of mutation patterns using frequent pattern mining.
	"""
	def __init__(self, line_or_mutant: bool, uk_or_cc: bool, min_support: int, max_confidence: float, max_length: int):
		"""
		:param line_or_mutant: true to take MutantExecutionLine as sample or false to take Mutant as well
		:param uk_or_cc: true to estimate on non-killed samples or false to take coincidental correct samples
		:param min_support: minimal number of samples that support the patterns
		:param max_confidence: maximal confidence once achieved the pattern generation will be terminated
		:param max_length: maximal length allowed for generating each pattern in the program
		"""
		self.line_or_mutant = line_or_mutant
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.__classifier__ = None		# Used to create MutationPattern and estimate them
		self.__patterns__ = dict()		# String ==> MutationPattern
		self.__solution__ = dict()		# MutationPattern ==> [total, support, confidence]
		return

	''' basic methods '''

	def __root__(self, document: cmuta.MutationFeatureDocument, word: str):
		"""
		:param document: it provides all the document lines for matching with the root pattern
		:param word: the unique word for creating the root pattern
		:return: the unique instance of the pattern
		"""
		root = MutationPattern(self.__classifier__)
		root = root.get_child(word)
		if not(str(root) in self.__patterns__):
			self.__patterns__[str(root)] = root
			root.set_lines(document.get_feature_lines())
		root = self.__patterns__[str(root)]
		root: MutationPattern
		return root

	def __child__(self, parent: MutationPattern, word: str):
		"""
		:param parent: pattern from which the child is generated
		:param word:
		:return: the unique child pattern extended from the parent by adding one word
		"""
		child = parent.get_child(word)
		if child != parent and not(str(child) in self.__patterns__):
			self.__patterns__[str(child)] = child
			child.set_lines(parent.get_lines())
		child = self.__patterns__[str(child)]
		child: MutationPattern
		return child

	def __generate__(self, parent: MutationPattern, words):
		"""
		:param parent: pattern from which the children will be solved
		:param words: words to create its children patterns
		:return:
		"""
		if not(parent in self.__solution__):
			total, support, confidence = parent.estimate(self.line_or_mutant, self.uk_or_cc)
			self.__solution__[parent] = (total, support, confidence)
		solution = self.__solution__[parent]
		support = solution[1]
		confidence = solution[2]
		if len(parent.words) < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__generate__(child, words)
		return

	''' generator methods '''

	def __output__(self, document: cmuta.MutationFeatureDocument):
		"""
		:return: MutationPatterns generated from the solution
		"""
		good_patterns = set()
		for pattern, solution in self.__solution__.items():
			pattern: MutationPattern
			support = solution[1]
			confidence = solution[2]
			if support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		output = MutationPatterns(document, self.__classifier__, good_patterns)
		return output

	def generate(self, document: cmuta.MutationFeatureDocument, classifier_tests=None):
		"""
		:param document: it provides all the lines and mutants for analysis
		:param classifier_tests: test cases to create mutation classifier
		:return: MutationPatterns
		"""
		self.__classifier__ = MutationClassifier(classifier_tests)
		self.__patterns__.clear()
		self.__solution__.clear()

		init_lines = self.__classifier__.select(document.get_feature_lines(), self.uk_or_cc)
		for init_line in init_lines:
			init_line: cmuta.MutationFeatureLine
			words = init_line.get_feature_words()
			for word in words:
				root = self.__root__(document, word)
				self.__generate__(root, words)

		patterns = self.__output__(document)
		self.__classifier__ = None
		self.__patterns__.clear()
		self.__solution__.clear()
		return patterns


class MutationPatternWriter:
	"""
	It writes the information of mutation patterns to output file.
	"""
	def __init__(self, patterns: MutationPatterns):
		"""
		:param patterns:
		"""
		self.patterns = patterns
		self.writer = None
		return

	''' basic methods '''

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

	''' pattern writers '''

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
						  format("Length", len(pattern.get_words()),
								 "Lines", len(pattern.get_lines()),
								 "Mutants", len(pattern.get_mutants())))

		''' counter: title UC UI UP KI UK CC '''
		self.writer.write("\t@Counting.\n")
		self.writer.write("\tSample\tUC\tUI\tUP\tKI\tUK\tCC\n")
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Line", uc, ui, up, ki, uk, cc))
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Line", uc, ui, up, ki, uk, cc))

		''' estimate: title total support confidence '''
		self.writer.write("\t@Estimate.\n")
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
			mutant: cmuta.Mutant
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
			for pattern in self.patterns.get_minimal_patterns():
				pattern_index += 1
				self.__write_pattern__(pattern, pattern_index)
				self.writer.write("\n")
		return

	''' testing results '''

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
		doc_lines = self.patterns.get_doc_lines()
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
		killed, over_score, valid_score = project.evaluation.evaluate_mutation_score(doc_mutants, classifier.get_tests())
		self.writer.write("\t{} := {}%\n".format("over_score", MutationPatternWriter.__percentage__(over_score)))
		self.writer.write("\t{} := {}%\n".format("valid_score", MutationPatternWriter.__percentage__(valid_score)))
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
		pat_lines, pat_mutants = self.patterns.get_pat_lines(), self.patterns.get_pat_mutants()
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
		for pattern in self.patterns.get_minimal_patterns():
			index += 1
			length = len(pattern.get_words())
			lines_number = len(pattern.get_lines())
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
		patterns_number = len(self.patterns.get_minimal_patterns())
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

	''' best patterns writer '''

	def write_best_patterns(self, output_file: str, line_or_mutant: bool, uk_or_cc: bool):
		with open(output_file, 'w') as writer:
			self.writer = writer
			mutants_patterns = MutationPatterns.samples_to_patterns(self.patterns.get_minimal_patterns(), False)
			for mutant, patterns in mutants_patterns.items():
				mutant: cmuta.Mutant
				pattern = MutationPatterns.best_patterns_from(patterns, line_or_mutant, uk_or_cc)
				mutant_id = mutant.get_mut_id()
				result = self.patterns.classifier.__classify__(mutant)
				mutation_class = mutant.get_mutation().get_mutation_class()
				mutation_operator = mutant.get_mutation().get_mutation_operator()
				line = mutant.get_mutation().get_location().line_of(False)
				code = mutant.get_mutation().get_location().get_code(True)
				parameter = mutant.get_mutation().get_parameter()
				self.writer.write("Mutant\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".format(
					mutant_id, result, mutation_class, mutation_operator, line, code, parameter))
				self.__write_pattern_words__(pattern)
				self.writer.write("\n")
		return


def mining_patterns(document: cmuta.MutationFeatureDocument, classifier_tests, line_or_mutant: bool,
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
	print("Testing on", document.get_project().program.name)
	print("\t(1) Load", len(document.get_feature_lines()), "lines of", len(document.get_mutants()),
		  "mutants with", len(document.get_corpus()), "words.")
	__killed__, over_score, valid_score = document.get_project().evaluation.\
		evaluate_mutation_score(document.get_project().mutant_space.get_mutants(), classifier_tests)
	test_number = None
	if classifier_tests is not None:
		test_number = len(classifier_tests)
	print("\t\tSelect", test_number, "test cases for killing", __killed__,
		  "mutants with {}%({}%).".format(MutationPatternWriter.__percentage__(over_score),
										  MutationPatternWriter.__percentage__(valid_score)))

	generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_confidence, max_length)
	patterns = generator.generate(document, classifier_tests)
	print("\t(2) Generate", len(patterns.get_patterns()), "patterns with", len(patterns.get_minimal_patterns()), "of minimal set from.")

	writer = MutationPatternWriter(patterns)
	writer.write_patterns(os.path.join(output_directory, document.get_project().program.name + ".mpt"))
	writer.write_results(os.path.join(output_directory, document.get_project().program.name + ".mrt"))
	writer.write_best_patterns(os.path.join(output_directory, document.get_project().program.name + ".bpt"), line_or_mutant, uk_or_cc)
	print("\t(3) Output the pattern, test results to output file finally...")
	print()
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	none_path = "/home/dzt2/Development/Data/patterns/none"
	test_path = "/home/dzt2/Development/Data/patterns/test"
	over_path = "/home/dzt2/Development/Data/patterns/over"
	dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for fname in os.listdir(prev_path):
		dir = os.path.join(prev_path, fname)
		c_project = cmuta.CProject(dir, fname)

		# 1. static document analysis
		docs = c_project.load_static_document(dir)
		selected_mutants = c_project.evaluation.select_mutants_by_classes(["STRP", "BTRP"])
		minimal_tests, __remained__ = c_project.evaluation.select_tests_for_mutants(selected_mutants)
		minimal_number = int(len(c_project.test_space.get_test_cases()) * 0.005)
		random_tests = c_project.evaluation.select_tests_for_random(minimal_number)
		selected_tests = minimal_tests | random_tests
		mining_patterns(docs, None, True, False, 2, 0.75, 1, os.path.join(none_path))
		mining_patterns(docs, selected_tests, True, False, 20, 0.80, 1, os.path.join(test_path))
		mining_patterns(docs, c_project.test_space.get_test_cases(), True, False, 100, 0.80, 1, os.path.join(over_path))

		# 2. dynamic document analysis
		docs = c_project.load_dynamic_document(dir)
		mining_patterns(docs, docs.get_test_cases(), True, False, 20, 0.80, 1, os.path.join(dyna_path))
	print("\nTesting end for all...")

