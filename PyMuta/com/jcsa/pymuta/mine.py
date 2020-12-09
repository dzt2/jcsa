"""
This implements the integrated framework for mining mutation patterns.
"""

import os
from typing import TextIO
import random
import com.jcsa.pymuta.code as ccode
import com.jcsa.pymuta.muta as cmuta


UC_CLASS, UI_CLASS, UP_CLASS, KI_CLASS = "UC", "UI", "UP", "KI"
CV_MUTANT, BC_MUTANT, CD_MUTANT, OT_MUTANT = "CV", "BC", "CD", "OT"


class MutationTestSelector:
	"""
	It implements the selection on mutants and test cases.
	"""
	@staticmethod
	def classify_mutant(mutant: cmuta.Mutant):
		"""
		:param mutant:
		:return: CV|BC|CD|OT
		"""
		if (mutant.get_coverage_mutant() == mutant) or (mutant.get_mutation().get_mutation_class() in ["STRP", "ETRP"]):
			return CV_MUTANT
		elif mutant.get_mutation().get_mutation_class() == "BTRP":
			location = mutant.get_mutation().get_location().get_parent()
			if location.get_class_name().endswith("Statement"):
				return BC_MUTANT
			else:
				return CD_MUTANT
		else:
			return OT_MUTANT

	@staticmethod
	def classify_mutants(mutants):
		"""
		:param mutants:
		:return:
		"""
		results = dict()
		results[CV_MUTANT] = set()
		results[BC_MUTANT] = set()
		results[CD_MUTANT] = set()
		results[OT_MUTANT] = set()
		for mutant in mutants:
			mutant: cmuta.Mutant
			key = MutationTestSelector.classify_mutant(mutant)
			results[key].add(mutant)
		return results

	@staticmethod
	def __rand_select__(samples):
		n = len(samples)
		k = random.randint(0, n - 1)
		selected_sample = None
		for sample in samples:
			selected_sample = sample
			if k <= 0:
				break
			else:
				k = k - 1
		return selected_sample

	@staticmethod
	def __rand_select_test__(mutant: cmuta.Mutant):
		"""
		:param mutant:
		:return: a random test case that kill the mutant
		"""
		killing_tests = mutant.get_result().get_killing_tests()
		test = MutationTestSelector.__rand_select__(killing_tests)
		test: cmuta.TestCase
		return test

	@staticmethod
	def select_test_cases(mutants):
		"""
		:param mutants:
		:return: test cases that kill all the mutants except non-killable
		"""
		remain_mutants, removed_mutants = set(), set()
		for mutant in mutants:
			mutant: cmuta.Mutant
			if mutant.get_result().is_killed():
				remain_mutants.add(mutant)
		test_cases = set()
		while len(remain_mutants) > 0:
			removed_mutants.clear()
			next_mutant = MutationTestSelector.__rand_select__(remain_mutants)
			next_mutant: cmuta.Mutant
			next_test_case = MutationTestSelector.__rand_select_test__(next_mutant)
			removed_mutants.add(next_mutant)
			for mutant in remain_mutants:
				if mutant.get_result().is_killed_by(next_test_case):
					removed_mutants.add(mutant)
			for mutant in removed_mutants:
				remain_mutants.remove(mutant)
			test_cases.add(next_test_case)
		return test_cases

	@staticmethod
	def select_coverage_mutants(mutants):
		results = MutationTestSelector.classify_mutants(mutants)
		return results[CV_MUTANT]

	@staticmethod
	def select_branch_mutants(mutants):
		results = MutationTestSelector.classify_mutants(mutants)
		return results[CV_MUTANT] | results[BC_MUTANT]

	@staticmethod
	def select_condition_mutants(mutants):
		results = MutationTestSelector.classify_mutants(mutants)
		return results[CV_MUTANT] | results[BC_MUTANT] | results[CD_MUTANT]

	@staticmethod
	def select_killed_mutants(mutants, test_cases):
		"""
		:param mutants:
		:param test_cases:
		:return: killed_mutants, score (%)
		"""
		killed_mutants = set()
		for mutant in mutants:
			mutant: cmuta.Mutant
			if mutant.get_result().is_killed_in(test_cases):
				killed_mutants.add(mutant)
		score = 0.0
		if len(mutants) > 0:
			score = len(killed_mutants) / (len(mutants) + 0.0)
		return killed_mutants, int(score * 100000000) / 1000000.0


class MutationClassifier:
	"""
	It implements the classification on test-cases and execution counters
	"""
	def __init__(self, test_cases):
		"""
		:param test_cases: provide None if it assumes to estimate on the entire test space
		"""
		self.cache = dict()
		self.tests = test_cases
		return

	def evaluate_one(self, sample):
		"""
		:param sample: MutantExecutionLine or Mutant
		:return: uc, ui, up, ki
		"""
		if not (sample in self.cache):
			uc, ui, up, ki = 0, 0, 0, 0
			if isinstance(sample, cmuta.Mutant):
				sample: cmuta.Mutant
				s_result = sample.get_result()
				w_result = sample.get_weak_mutant().get_result()
				c_result = sample.get_coverage_mutant().get_result()
				if self.tests is None:
					# evaluate on all the test cases as known
					if s_result.is_killed():
						ki += 1
					elif w_result.is_killed():
						up += 1
					elif c_result.is_killed():
						ui += 1
					else:
						uc += 1
				else:
					# evaluate on constraint-based test cases
					for test_case in self.tests:
						if s_result.is_killed_by(test_case):
							ki += 1
						elif w_result.is_killed_by(test_case):
							up += 1
						elif c_result.is_killed_by(test_case):
							ui += 1
						else:
							uc += 1
			else:
				sample: cmuta.MutantExecutionLine
				if sample.has_test_case():
					if sample.is_killed():
						ki += 1
					elif sample.is_infected():
						up += 1
					elif sample.is_covered():
						ui += 1
					else:
						uc += 1
				else:
					mutant = sample.get_mutant()
					uc, ui, up, ki = self.evaluate_one(mutant)
			self.cache[sample] = (uc, ui, up, ki)
		solution = self.cache[sample]
		uc, ui, up, ki = solution[0], solution[1], solution[2], solution[3]
		uc: int
		ui: int
		up: int
		ki: int
		return uc, ui, up, ki

	def evaluate_all(self, samples):
		"""
		:param samples: collection of Mutant or MutantExecutionLine
		:return: mapping from sample to (uc, ui, up, ki)
		"""
		results = dict()
		for sample in samples:
			uc, ui, up, ki = self.evaluate_one(sample)
			results[sample] = (uc, ui, up, ki)
		return results

	def counting_all(self, samples):
		"""
		:param samples:
		:return: uc, ui, up, ki, uk, cc for all the samples
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.evaluate_one(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def estimate_all(self, samples, uk_or_cc: bool):
		"""
		:param samples: set of Mutant or MutantExecutionLine
		:param uk_or_cc: true to estimate on non-killed, and false to estimate on CC
		:return: total, support, precision
		"""
		uc, ui, up, ki, uk, cc = self.counting_all(samples)
		total = uc + ui + up + ki
		if uk_or_cc:
			support = uk
		else:
			support = cc
		precision = 0.0
		if support > 0:
			precision = support / (total + 0.0)
		return total, support, precision

	def classify_one(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: UC|UI|UP|KI
		"""
		uc, ui, up, ki = self.evaluate_one(sample)
		if ki > 0:
			return KI_CLASS
		elif up > 0:
			return UP_CLASS
		elif ui > 0:
			return UI_CLASS
		else:
			return UP_CLASS

	def classify_all(self, samples):
		"""
		:param samples:
		:return:
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			key = self.classify_one(sample)
			results[key].add(sample)
		return results

	def select_goods(self, samples, uk_or_cc: bool):
		"""
		:param samples: collection of Mutant ot MutantExecutionLine
		:param uk_or_cc: true to select UI+UP+UC or false to select UI+UP
		:return: collection of Mutant or MutantExecutionLine selected
		"""
		results = self.classify_all(samples)
		good_samples = results[UI_CLASS] | results[UP_CLASS]
		if uk_or_cc:
			good_samples = good_samples | results[UP_CLASS]
		return good_samples


class MutationPattern:
	"""
	The pattern of Mutant Execution Line
	"""
	''' constructor '''
	def __init__(self, classifier: MutationClassifier):
		self.words = list()
		self.lines = list()
		self.classifier = classifier
		return

	''' feature words '''
	def get_words(self):
		return self.words

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_annotations(self, program: ccode.CProgram):
		"""
		:param program:
		:return: annotations generated from word in the pattern
		"""
		annotations = list()
		for word in self.words:
			word: str
			annotation = cmuta.CirAnnotation.parse(word.strip(), program)
			annotations.append(annotation)
		return annotations

	''' data samples '''
	def get_lines(self):
		return self.lines

	def get_mutants(self):
		mutants = set()
		for line in self.lines:
			line: cmuta.MutantExecutionLine
			mutants.add(line.get_mutant())
		return mutants

	def __match__(self, line: cmuta.MutantExecutionLine):
		"""
		:param line:
		:return: true if the line matches with the pattern
		"""
		for word in self.words:
			if not(word in line.get_words()):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: the set of lines being matched with this pattern
		:return: update self.lines by matching with the inputs lines
		"""
		self.lines.clear()
		for line in lines:
			line: cmuta.MutantExecutionLine
			if self.__match__(line):
				self.lines.append(line)
		return

	''' classification '''
	def classify(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to classify the lines or false to classify the mutants
		:return: [UC, UI, UP, KI] ==> set of MutantExecutionLine (True) or Mutant (False)
		"""
		if line_or_mutant:
			samples = self.get_lines()
		else:
			samples = self.get_mutants()
		return self.classifier.classify_all(samples)

	def counting(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to classify the lines or false to classify the mutants
		:return: uc, ui, up, ki, uk, cc
		"""
		if line_or_mutant:
			samples = self.get_lines()
		else:
			samples = self.get_mutants()
		return self.classifier.counting_all(samples)

	def estimate(self, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param line_or_mutant: true to classify the lines or false to classify the mutants
		:param uk_or_cc: true to estimate on non-killed or false to estimate on CC samples
		:return: total, support, precision
		"""
		if line_or_mutant:
			samples = self.get_lines()
		else:
			samples = self.get_mutants()
		return self.classifier.estimate_all(samples, uk_or_cc)

	''' relationship '''
	def get_child(self, word: str):
		"""
		:param word:
		:return: extend to create a child from the pattern or itself if the word is not additional
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
		:return: true if the lines of this pattern include all those lines in given pattern
		"""
		pattern: MutationPattern
		for line in pattern.get_lines():
			if not(line in self.lines):
				return False
		return True


class MutationPatternGenerator:
	"""
	It generate patterns using association rules mining
	"""
	def __init__(self, line_or_mutant: bool, uk_or_cc: bool, min_support: int, max_precision: float, max_length: int, test_cases):
		"""
		:param line_or_mutant: true to classify the lines or false to classify the mutants
		:param uk_or_cc: true to estimate on non-killed or false to estimate on CC samples
		:param min_support: minimal number of good samples in each pattern
		:param max_precision: maximal precision once achieved the algorithm will stop
		:param max_length: the maximal length of words in each pattern
		:param test_cases: used as context to classify the mutations under analysis
		"""
		self.line_or_mutant = line_or_mutant
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_precision = max_precision
		self.max_length = max_length
		self.patterns = dict()		# String --> MutationPattern
		self.solution = dict()		# Pattern --> [total, support, precision]
		self.test_cases = test_cases
		self.classifier = MutationClassifier(test_cases)
		return

	def __root__(self, document: cmuta.MutantExecutionDocument, word: str):
		"""
		:param document:
		:param word:
		:return: root pattern with one single word that matches with the entire lines in document
		"""
		root = MutationPattern(self.classifier)
		root = root.get_child(word)
		if not(str(root) in self.patterns):
			self.patterns[str(root)] = root
			root.set_lines(document.get_lines())
		root = self.patterns[str(root)]
		root: MutationPattern
		return root

	def __child__(self, parent: MutationPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: the child pattern (unique) generated from the parent by adding one word
		"""
		child = parent.get_child(word)
		if child != parent and not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_lines(parent.get_lines())
		child = self.patterns[str(child)]
		child: MutationPattern
		return child

	def __filter__(self):
		"""
		:return: good patterns selected from the generated solutions
		"""
		good_patterns = set()
		for pattern, solution in self.solution.items():
			pattern: MutationPattern
			support = solution[1]
			precision = solution[2]
			if support >= self.min_support and precision >= self.max_precision and len(pattern.words) <= self.max_length:
				good_patterns.add(pattern)
		return good_patterns

	def __generate__(self, parent: MutationPattern, words):
		"""
		:param parent:
		:param words:
		:return:
		"""
		if not(parent in self.solution):
			total, support, precision = parent.estimate(self.line_or_mutant, self.uk_or_cc)
			self.solution[parent] = [total, support, precision]
		solution = self.solution[parent]
		support = solution[1]
		precision = solution[2]
		if support >= self.min_support and precision <= self.max_precision and len(parent.words) < self.max_length:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__generate__(child, words)
		return

	def generate(self, document: cmuta.MutantExecutionDocument):
		"""
		:param document:
		:return: good patterns, classifier
		"""
		self.patterns.clear()
		self.solution.clear()
		self.classifier = MutationClassifier(self.test_cases)
		init_lines = self.classifier.select_goods(document.get_lines(), self.uk_or_cc)
		for init_line in init_lines:
			init_line: cmuta.MutantExecutionLine
			words = init_line.get_words()
			for word in words:
				root_pattern = self.__root__(document, word)
				self.__generate__(root_pattern, words)
		good_patterns = self.__filter__()
		self.patterns.clear()
		self.solution.clear()
		classifier = self.classifier
		return good_patterns, classifier


class MutationPatternSelector:
	"""
	It select subset of mutation patterns from good one
	"""
	@staticmethod
	def select_samples_in_patterns(patterns, line_or_mutant: bool):
		"""
		:param patterns:
		:param line_or_mutant: true to select MutantExecutionLine or false to select Mutant
		:return:
		"""
		samples = set()
		for pattern in patterns:
			pattern: MutationPattern
			for line in pattern.get_lines():
				line: cmuta.MutantExecutionLine
				if line_or_mutant:
					samples.add(line)
				else:
					samples.add(line.get_mutant())
		return samples

	@staticmethod
	def select_samples_in_document(document: cmuta.MutantExecutionDocument, line_or_mutant: bool):
		"""
		:param document:
		:param line_or_mutant:
		:return: collection of lines (True) or mutants (False) in the document
		"""
		samples = set()
		for line in document.get_lines():
			line: cmuta.MutantExecutionLine
			if line_or_mutant:
				samples.add(line)
			else:
				samples.add(line.get_mutant())
		return samples

	@staticmethod
	def select_minimal_patterns(patterns):
		"""
		:param patterns:
		:return: the minimal set of patterns selected from inputs that can covering all the lines of the others
		"""
		remain_patterns, removed_patterns, minimal_patterns = set(), set(), set()
		for pattern in patterns:
			pattern: MutationPattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			removed_patterns.clear()
			subsume_pattern = None
			for pattern in remain_patterns:
				if subsume_pattern is None:
					subsume_pattern = pattern
					removed_patterns.add(pattern)
				elif subsume_pattern.subsume(pattern):
					removed_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern):
					subsume_pattern = pattern
					removed_patterns.add(pattern)
			for pattern in removed_patterns:
				remain_patterns.remove(pattern)
			if subsume_pattern is None:
				break
			else:
				minimal_patterns.add(subsume_pattern)
		return minimal_patterns

	@staticmethod
	def map_samples_to_patterns(patterns, line_or_mutant: bool):
		"""
		:param patterns:
		:param line_or_mutant:
		:return: map from line (True) or mutant (False) to the set of patterns matching with it
		"""
		results = dict()
		for pattern in patterns:
			pattern: MutationPattern
			for line in pattern.get_lines():
				line: cmuta.MutantExecutionLine
				if line_or_mutant:
					sample = line
				else:
					sample = line.get_mutant()
				if not(sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def select_best_pattern(patterns, line_or_mutant: bool, uk_or_cc: bool, precision_buffer_size=5):
		"""
		:param uk_or_cc:
		:param line_or_mutant:
		:param patterns:
		:param precision_buffer_size: the size of buffer to preserve patterns with maximal precision of top-N
		:return:
		"""
		new_patterns = set()
		for pattern in patterns:
			pattern: MutationPattern
			new_patterns.add(pattern)

		precision_buffer = list()
		while len(precision_buffer) < precision_buffer_size and len(new_patterns) > 0:
			best_precision, best_pattern = 0.0, None
			for pattern in new_patterns:
				total, support, precision = pattern.estimate(line_or_mutant, uk_or_cc)
				if precision >= best_precision:
					best_pattern = pattern
					best_precision = precision
			if best_pattern is None:
				break
			else:
				precision_buffer.append(best_pattern)

		best_support, best_pattern = 0, None
		for pattern in precision_buffer:
			total, support, precision = pattern.estimate(line_or_mutant, uk_or_cc)
			if support >= best_support:
				best_pattern = pattern
				best_support = support
		return best_pattern


class MutationPatternWriter:
	@staticmethod
	def __proportion__(x: int, y: int):
		if x == 0:
			return 0.0
		else:
			ratio = x / (y + 0.0)
			return int(ratio * 100000000) / 1000000.0

	@staticmethod
	def __write_pattern_count__(writer: TextIO, pattern: MutationPattern):
		"""
		:param writer:
		:param pattern:
		:return:
		"""
		line1 = "\tSummary\tWords\t{}\tLines\t{}\tMutants\t{}\n"
		writer.write(line1.format(len(pattern.words), len(pattern.get_lines()), len(pattern.get_mutants())))
		line2 = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		writer.write(line2.format("Metrics", "UC", "UI", "UP", "KI", "UK(%)", "CC(%)"))
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		total = uc + ui + up + ki
		writer.write(line2.format("Line", uc, ui, up, ki,
								  MutationPatternWriter.__proportion__(uk, total),
								  MutationPatternWriter.__proportion__(cc, total)))
		uc, ui, up, ki, uk, cc = pattern.counting(False)
		total = uc + ui + up + ki
		writer.write(line2.format("Mutant", uc, ui, up, ki,
								  MutationPatternWriter.__proportion__(uk, total),
								  MutationPatternWriter.__proportion__(cc, total)))
		writer.write("\n")
		return

	@staticmethod
	def __write_pattern_words__(writer: TextIO, pattern: MutationPattern, program: ccode.CProgram):
		"""
		:param writer:
		:param pattern:
		:param program:
		:return:
		"""
		writer.write("\tIndex\tType\tExecution\tStatement\tLocation\tParameter\n")
		annotations = pattern.get_annotations(program)
		index = 0
		for annotation in annotations:
			writer.write("\tWord[{}]\t{}\t{}\t\"{}\"\t\"{}\"\t\"{}\"\n".
						 format(index, annotation.get_type(), annotation.get_execution(),
								annotation.get_execution().get_statement().get_cir_id(),
								annotation.get_location().get_cir_code(), annotation.get_parameter()))
			index += 1
		writer.write("\n")
		return

	@staticmethod
	def __write_pattern_lines__(writer: TextIO, pattern: MutationPattern):
		"""
		:param writer:
		:param pattern:
		:return:
		"""
		mutants = pattern.get_mutants()
		writer.write("\tID\tRES\tCLASS\tOPERATOR\tLINE\tCODE\tPARAMETER\n")
		line = "\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t\"{}\"\n"
		for mutant in mutants:
			mutation = mutant.get_mutation()
			result = pattern.classifier.classify_one(mutant)
			writer.write(line.format(mutant.get_mut_id(), result,
									 mutation.mutation_class,
									 mutation.mutation_operator,
									 mutation.get_location().line_of(),
									 mutation.location.get_code(True),
									 mutation.parameter))
		return

	@staticmethod
	def __prf_evaluation__(doc_samples: set, pat_samples: set):
		int_samples = doc_samples & pat_samples
		doc_size, pat_size, int_size = len(doc_samples), len(pat_samples), len(int_samples)
		if int_size > 0:
			precision = int_size / (pat_size + 0.0)
			recall = int_size / (doc_size + 0.0)
			f1_score = 2 * precision * recall / (precision + recall)
			return int(precision * 10000) / 100.0, int(10000 * recall) / 100.0, int(f1_score * 10000) / 10000.0
		return 0.0, 0.0, 0.0

	@staticmethod
	def __write_patterns_summary__(writer: TextIO, document: cmuta.MutantExecutionDocument, patterns, classifier: MutationClassifier):
		"""
		:param writer:
		:param document:
		:param patterns:
		:return:
		"""
		doc_lines = MutationPatternSelector.select_samples_in_document(document, True)
		doc_mutants = MutationPatternSelector.select_samples_in_document(document, False)
		writer.write("Evaluation\n")
		writer.write("\tPatterns := {}\tLines := {}({}%)\tMutants := {}({}%)\n".
					 format(len(patterns),
							len(doc_lines),
							MutationPatternWriter.__proportion__(len(patterns), len(doc_lines)),
							len(doc_mutants),
							MutationPatternWriter.__proportion__(len(patterns), len(doc_mutants))))
		total, uk_support, precision = classifier.estimate_all(doc_lines, True)
		total, cc_support, precision = classifier.estimate_all(doc_lines, False)
		writer.write("\tPatterns := {}\tUK-Execs := {}({}%)\tUK-Execs := {}({}%)\n".
					 format(len(patterns),
							uk_support,
							MutationPatternWriter.__proportion__(len(patterns), uk_support),
							cc_support,
							MutationPatternWriter.__proportion__(len(patterns), cc_support)))
		writer.write("\tTitle\tPrecision(%)\tRecall(%)\tF1-Score\n")
		pat_lines = MutationPatternSelector.select_samples_in_patterns(patterns, True)
		pat_mutants = MutationPatternSelector.select_samples_in_patterns(patterns, False)
		uk_lines = classifier.select_goods(doc_lines, True)
		cc_lines = classifier.select_goods(doc_lines, False)
		uk_mutants = classifier.select_goods(doc_mutants, True)
		cc_mutants = classifier.select_goods(doc_mutants, False)
		template = "\t{}\t{}\t{}\t{}\t{}\n"
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(uk_lines, pat_lines)
		writer.write(template.format("UK-LINE", len(uk_lines), precision, recall, f1_score))
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(cc_lines, pat_lines)
		writer.write(template.format("CC-LINE", len(cc_lines), precision, recall, f1_score))
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(uk_mutants, pat_mutants)
		writer.write(template.format("UK-MUTA", len(uk_mutants), precision, recall, f1_score))
		precision, recall, f1_score = MutationPatternWriter.__prf_evaluation__(cc_mutants, pat_mutants)
		writer.write(template.format("CC-MUTA", len(cc_mutants), precision, recall, f1_score))
		return

	@staticmethod
	def write_patterns(document: cmuta.MutantExecutionDocument, patterns, classifier: MutationClassifier, output_file: str):
		with open(output_file, 'w') as writer:
			MutationPatternWriter.__write_patterns_summary__(writer, document, patterns, classifier)
			pid = 0
			for pattern in patterns:
				pattern: MutationPattern
				writer.write("\nBEG\t" + str(pid) + "\n")
				MutationPatternWriter.__write_pattern_count__(writer, pattern)
				MutationPatternWriter.__write_pattern_words__(writer, pattern, document.get_project().program)
				MutationPatternWriter.__write_pattern_lines__(writer, pattern)
				writer.write("END\t" + str(pid) + "\n")
				pid += 1
				writer.write("\n")
		return

	@staticmethod
	def write_best_patterns(patterns, classifier: MutationClassifier, output_file: str, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param uk_or_cc:
		:param line_or_mutant:
		:param classifier:
		:param patterns:
		:param output_file:
		:return:
		"""
		mutant_patterns = MutationPatternSelector.map_samples_to_patterns(patterns, False)
		with open(output_file, 'w') as writer:
			for mutant, patterns in mutant_patterns.items():
				mutant: cmuta.Mutant
				line = "\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t\"{}\"\n"
				mutation = mutant.get_mutation()
				result = classifier.classify_one(mutant)
				writer.write(line.format(mutant.get_mut_id(), result,
										 mutation.mutation_class,
										 mutation.mutation_operator,
										 mutation.get_location().line_of(),
										 mutation.location.get_code(True),
										 mutation.parameter))
				best_pattern = MutationPatternSelector.select_best_pattern(patterns, line_or_mutant, uk_or_cc)
				if best_pattern is not None:
					MutationPatternWriter.__write_pattern_words__(writer, best_pattern, mutant.get_space().get_project().program)
		return


def mining_patterns_on_none(root_path: str, post_path: str):
	line_or_mutant, uk_or_cc, min_support, max_precision, max_length = True, True, 2, 0.80, 1
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		print("Load", docs.get_length(), "execution lines with", len(docs.corpus), "words from", file_name)
		generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_precision, max_length, None)
		god_patterns, classifier = generator.generate(docs)
		min_patterns = MutationPatternSelector.select_minimal_patterns(god_patterns)
		print("\t==> Generate", len(god_patterns), "mutation patterns from the document with", len(min_patterns),
			  "of minimal set.")
		MutationPatternWriter.write_patterns(docs, god_patterns, classifier,
											 os.path.join(post_path, file_name + ".gpt"))
		print("\t==> Write", len(god_patterns), "patterns of good set to output file")
		MutationPatternWriter.write_patterns(docs, min_patterns, classifier,
											 os.path.join(post_path, file_name + ".mpt"))
		print("\t==> Write", len(min_patterns), "patterns of minimal to output file.")
		MutationPatternWriter.write_best_patterns(min_patterns, classifier, os.path.join(post_path, file_name + ".bpt"),
												  line_or_mutant, uk_or_cc)
		print("\t==> Write best patterns on output file for", file_name)
		print()
	return


def mining_patterns_on_tests(root_path: str, post_path: str):
	line_or_mutant, uk_or_cc, min_support, max_precision, max_length = True, True, 2, 0.80, 1
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		print("Load", docs.get_length(), "execution lines with", len(docs.corpus), "words from", file_name)
		selected_mutants = MutationTestSelector.select_coverage_mutants(c_project.mutant_space.get_mutants())
		selected_tests = MutationTestSelector.select_test_cases(selected_mutants)
		killed_mutants, score = MutationTestSelector.select_killed_mutants(c_project.mutant_space.get_mutants(),
																		   selected_tests)
		print("\t (1) Select", len(selected_tests), "for killing", len(killed_mutants), "mutants ({}%).".format(score))
		generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_precision, max_length,
											 selected_tests)
		god_patterns, classifier = generator.generate(docs)
		min_patterns = MutationPatternSelector.select_minimal_patterns(god_patterns)
		print("\t (2) Generate", len(god_patterns), "mutation patterns from the document with", len(min_patterns),
			  "of minimal set.")
		MutationPatternWriter.write_patterns(docs, god_patterns, classifier,
											 os.path.join(post_path, file_name + ".gpt"))
		print("\t (3) Write", len(god_patterns), "patterns of good set to output file")
		MutationPatternWriter.write_patterns(docs, min_patterns, classifier,
											 os.path.join(post_path, file_name + ".mpt"))
		print("\t (4) Write", len(min_patterns), "patterns of minimal to output file.")
		MutationPatternWriter.write_best_patterns(min_patterns, classifier, os.path.join(post_path, file_name + ".bpt"),
												  line_or_mutant, uk_or_cc)
		print("\t (5) Write best patterns on output file for", file_name)
		print()


def mining_patterns_on_all(root_path: str, post_path: str):
	line_or_mutant, uk_or_cc, min_support, max_precision, max_length = True, True, 2, 0.80, 1
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		print("Load", docs.get_length(), "execution lines with", len(docs.corpus), "words from", file_name)
		generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_precision, max_length, c_project.test_space.get_test_cases())
		god_patterns, classifier = generator.generate(docs)
		min_patterns = MutationPatternSelector.select_minimal_patterns(god_patterns)
		print("\t==> Generate", len(god_patterns), "mutation patterns from the document with", len(min_patterns),
			  "of minimal set.")
		MutationPatternWriter.write_patterns(docs, god_patterns, classifier,
											 os.path.join(post_path, file_name + ".gpt"))
		print("\t==> Write", len(god_patterns), "patterns of good set to output file")
		MutationPatternWriter.write_patterns(docs, min_patterns, classifier,
											 os.path.join(post_path, file_name + ".mpt"))
		print("\t==> Write", len(min_patterns), "patterns of minimal to output file.")
		MutationPatternWriter.write_best_patterns(min_patterns, classifier, os.path.join(post_path, file_name + ".bpt"),
												  line_or_mutant, uk_or_cc)
		print("\t==> Write best patterns on output file for", file_name)
		print()
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/patterns"
	mining_patterns_on_all(root_path, post_path)

