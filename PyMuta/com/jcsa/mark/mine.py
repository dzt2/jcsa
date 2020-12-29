"""
This file implements the classification, prediction and pattern mining algorithms.
"""


import os
from typing import TextIO
import com.jcsa.libs.muta as cmuta


UC_CLASS, UI_CLASS, UP_CLASS, KI_CLASS = "UC", "UI", "UP", "KI"


class MutationClassifier:
	"""
	It is used to classify, counting and estimate symbolic execution state line with features being mined.
	"""
	def __init__(self, classifier_tests):
		"""
		:param classifier_tests:
			(1) set of test cases to count non-covered (UC), non-infected (UI), non-propagate (UP) and killed results
			(2) set as None if the execution state is counted over test result rather than sample itself.
		"""
		self.classifier_tests = classifier_tests
		self.solutions = dict()		# sample --> [uc, ui, up, ki]
		return

	def has_tests(self):
		return self.classifier_tests is not None

	def get_tests(self):
		return self.classifier_tests

	def __get_solution__(self, sample):
		"""
		:param sample: either Mutant or MutationFeaturesLine
		:return: uc, ui, up, ki from existing solution w.r.t. the sample
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
		:param sample: either Mutant or MutationFeaturesLine
		:return:
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		if isinstance(sample, cmuta.MutationFeaturesLine):
			sample: cmuta.MutationFeaturesLine
			s_result = sample.get_mutant().get_result()
			w_result = sample.get_mutant().get_weak_mutant().get_result()
			c_result = sample.get_mutant().get_coverage_mutant().get_result()
			if sample.has_test_case():							# dynamic line w.r.t. special test
				if s_result.is_killed_by(sample.get_test_case()):
					ki += 1
				elif w_result.is_killed_by(sample.get_test_case()):
					up += 1
				elif c_result.is_killed_by(sample.get_test_case()):
					ui += 1
				else:
					uc += 1
			else:												# resort to solution of mutant on classifier tests
				uc, ui, up, ki = self.__solve__(sample.get_mutant())
		else:
			sample: cmuta.Mutant
			s_result = sample.get_result()
			w_result = sample.get_weak_mutant().get_result()
			c_result = sample.get_coverage_mutant().get_result()
			if self.classifier_tests is None:					# count over the sample rather than each execution
				if s_result.is_killable():
					ki += 1
				elif w_result.is_killable():
					up += 1
				elif c_result.is_killable():
					ui += 1
				else:
					uc += 1
			else:												# count on execution against self.classifier_tests
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
		:param sample: either Mutant or MutationFeaturesLine
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
		:param sample: either Mutant or MutationFeaturesLine
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

	def counting(self, samples):
		"""
		:param samples: set of Mutant and MutationFeaturesLine
		:return:	uc (number of samples or results not covered)
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

	def classify(self, samples):
		"""
		:param samples:
		:return: mapping from UC | UI | UP | KI to set of samples classified
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
		:param samples: set of Mutant and MutationFeaturesLine
		:param uk_or_cc: true to select non-killed or false to select coincidental correctness results in samples
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
			confidence = support / total
		return total, support, confidence

	def select(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant and MutationFeaturesLine
		:param uk_or_cc: true to select non-killed or false to select coincidental correctness results in samples
		:return: set of non-killed (True) or coincidental-correct (False) samples selected from inputs set
		"""
		results = self.classify(samples)
		selected_samples = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			selected_samples = selected_samples | results[UC_CLASS]
		return selected_samples


class MutationPattern:
	"""
	The pattern of symbolic execution state w.r.t. a set of feature words
	"""
	def __init__(self, classifier: MutationClassifier):
		self.classifier = classifier
		self.words = list()		# sorted feature words
		self.mutants = set()	# mutants of lines matching with the pattern
		self.lines = set()		# execution state lines matching with pattern
		return

	def get_classifier(self):
		return self.classifier

	def get_words(self):
		"""
		:return: set of feature words in the pattern
		"""
		return self.words

	def __str__(self):
		return str(self.words)

	def __len__(self):
		"""
		:return: length of the words defined in the pattern
		"""
		return len(self.words)

	def get_features(self, document: cmuta.MutationFeaturesDocument):
		features = list()
		for word in self.words:
			features.append(document.get_feature(word))
		return features

	def get_lines(self):
		return self.lines

	def get_mutants(self):
		return self.mutants

	def __match__(self, line: cmuta.MutationFeaturesLine):
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
		:param lines: collection of symbolic execution state lines being matched
		:return: update self.lines and self.mutants
		"""
		self.lines.clear()
		self.mutants.clear()
		for line in lines:
			line: cmuta.MutationFeaturesLine
			if self.__match__(line):
				self.lines.add(line)
				self.mutants.add(line.get_mutant())
		return

	def get_samples(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to select execution state lines or false to select mutants matched in the pattern
				as the output samples for being evaluated or classified
		:return:
		"""
		if line_or_mutant:
			return self.lines
		else:
			return self.mutants

	def classify(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to select execution state lines or false to select mutants matched in the pattern
				as the output samples for being evaluated or classified
		:return: mapping from UC | UI | UP | KI to set of samples w.r.t. the specified class
		"""
		return self.classifier.classify(self.get_samples(line_or_mutant))

	def counting(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to select execution state lines or false to select mutants matched in the pattern
				as the output samples for being evaluated or classified
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
		:param line_or_mutant: true to select execution state lines or false to select mutants matched in the pattern
				as the output samples for being evaluated or classified
		:param uk_or_cc: true to select non-killed or false to select coincidental correctness results in samples
		:return: total, support, confidence
		"""
		return self.classifier.estimate(self.get_samples(line_or_mutant), uk_or_cc)

	def select(self, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param line_or_mutant: true to select execution state lines or false to select mutants matched in the pattern
				as the output samples for being evaluated or classified
		:param uk_or_cc: true to select non-killed or false to select coincidental correctness results in samples
		:return: set of execution state lines or mutants matching the specified class
		"""
		return self.classifier.select(self.get_samples(line_or_mutant), uk_or_cc)

	def get_child(self, feature_word: str):
		"""
		:param feature_word:
		:return: child pattern extended from the new input word or the parent itself when the word has been used
		"""
		feature_word = feature_word.strip()
		if len(feature_word) > 0 and not(feature_word in self.words):
			child = MutationPattern(self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(feature_word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: it subsumes the pattern when its lines matched contain all those in specified pattern
		"""
		pattern: MutationPattern
		for line in pattern.get_lines():
			if not(line in self.lines):
				return False
		return True


class MutationPatterns:
	"""
	It maintains the patterns generated from document
	"""
	def __init__(self, document: cmuta.MutationFeaturesDocument, classifier: MutationClassifier, patterns):
		"""
		:param document: provide original lines and mutants
		:param classifier: used to count, classify and estimate patterns
		:param patterns: output selected good patterns from document
		"""
		self.document = document
		self.doc_lines = set()
		self.doc_mutants = set()
		for line in document.get_lines():
			line: cmuta.MutationFeaturesLine
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
				line: cmuta.MutationFeaturesLine
				self.pat_lines.add(line)
				self.pat_mutants.add(line.get_mutant())
		min_patterns = self.__min_patterns__()
		self.min_patterns = set()
		for pattern in min_patterns:
			pattern: MutationPattern
			self.min_patterns.add(pattern)
		return

	def __min_patterns__(self):
		"""
		:return: minimal set of patterns for covering all the lines in self.patterns
		"""
		remain_patterns, removed_patterns, minimal_patterns = set(), set(), set()
		for pattern in self.patterns:
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			subsume_pattern = None
			removed_patterns.clear()
			for pattern in remain_patterns:
				if subsume_pattern is None:
					subsume_pattern = pattern
					removed_patterns.add(pattern)
				elif subsume_pattern.subsume(pattern):
					removed_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern):
					removed_patterns.add(pattern)
					subsume_pattern = pattern
			for pattern in removed_patterns:
				remain_patterns.remove(pattern)
			if subsume_pattern is not None:
				minimal_patterns.add(subsume_pattern)
		return minimal_patterns

	def get_document(self):
		return self.document

	def get_doc_lines(self):
		return self.doc_lines

	def get_doc_mutants(self):
		return self.doc_mutants

	def get_patterns(self):
		return self.patterns

	def get_min_patterns(self):
		return self.min_patterns

	def get_pat_lines(self):
		return self.pat_lines

	def get_pat_mutants(self):
		return self.pat_mutants

	def get_classifier(self):
		return self.classifier

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
				if not (sample in results):
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

	def select_best_patterns(self, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param line_or_mutant: true to take MutantExecutionLine or false to take Mutant as sample
		:param uk_or_cc: true to estimate on non-killed or coincidental correctness sample when set as false
		:return: mapping from samples to best patterns they matched with
		"""
		sample_patterns = MutationPatterns.samples_to_patterns(self.min_patterns, line_or_mutant)
		results = dict()
		for sample, patterns in sample_patterns.items():
			best_pattern = MutationPatterns.best_patterns_from(patterns, line_or_mutant, uk_or_cc)
			results[sample] = best_pattern
		return results


class MutationPatternGenerator:
	"""
	It generates the mutation patterns using recursive algorithm of frequent pattern mining
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

	''' basic method '''

	def __root__(self, document: cmuta.MutationFeaturesDocument, word: str):
		"""
		:param document:
		:param word:
		:return: root pattern with one single word that matches with all the lines in document
		"""
		root = MutationPattern(self.__classifier__)
		root = root.get_child(word)
		if not(str(root) in self.__patterns__):
			self.__patterns__[str(root)] = root
			root.set_lines(document.get_lines())
		root = self.__patterns__[str(root)]
		root: MutationPattern
		return root

	def __child__(self, parent: MutationPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: child pattern extended from parent by adding one word and updating its lines
		"""
		child = parent.get_child(word)
		if child != parent:
			if not(str(child) in self.__patterns__):
				self.__patterns__[str(child)] = child
				child.set_lines(parent.get_lines())
			child = self.__patterns__[str(child)]
			child: MutationPattern
			return child
		return parent

	def __generate__(self, parent: MutationPattern, words):
		"""
		:param parent:
		:param words:
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

	def __output__(self, document: cmuta.MutationFeaturesDocument):
		"""
		:param document:
		:return: generate good patterns being mined from document
		"""
		good_patterns = set()
		for pattern, solution in self.__solution__.items():
			pattern: MutationPattern
			support = solution[1]
			confidence = solution[2]
			if support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		return MutationPatterns(document, self.__classifier__, good_patterns)

	def generate(self, document: cmuta.MutationFeaturesDocument, classifier_tests):
		"""
		:param document: it provides all the lines and mutants for analysis
		:param classifier_tests: test cases to create mutation classifier
		:return: MutationPatterns
		"""
		self.__classifier__ = MutationClassifier(classifier_tests)
		self.__patterns__.clear()
		self.__solution__.clear()

		init_lines = self.__classifier__.select(document.get_lines(), self.uk_or_cc)
		for init_line in init_lines:
			init_line: cmuta.MutationFeaturesLine
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
		for annotation in pattern.get_features(self.patterns.document):
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
			for pattern in self.patterns.get_min_patterns():
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
		for pattern in self.patterns.get_min_patterns():
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

	''' best patterns writer '''

	def write_best_patterns(self, output_file: str, line_or_mutant: bool, uk_or_cc: bool):
		with open(output_file, 'w') as writer:
			self.writer = writer
			mutants_patterns = MutationPatterns.samples_to_patterns(self.patterns.get_min_patterns(), False)
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


def mining_patterns(document: cmuta.MutationFeaturesDocument, classifier_tests, line_or_mutant: bool,
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
	if len(document.get_lines()) > 0:
		print("Testing on", document.get_project().program.name)
		print("\t(1) Load", len(document.get_lines()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.get_corpus_words()), "words.")
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
		print("\t(2) Generate", len(patterns.get_patterns()), "patterns with", len(patterns.get_min_patterns()), "of minimal set from.")

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
		mining_patterns(docs, None, True, True, 2, 0.75, 1, os.path.join(none_path))
		mining_patterns(docs, selected_tests, True, True, 20, 0.80, 1, os.path.join(test_path))
		mining_patterns(docs, c_project.test_space.get_test_cases(), True, True, 100, 0.80, 1, os.path.join(over_path))

		# 2. dynamic document analysis
		# docs = c_project.load_dynamic_document(dir)
		# mining_patterns(docs, docs.get_test_cases(), True, True, 20, 0.80, 1, os.path.join(dyna_path))
	print("\nTesting end for all...")

