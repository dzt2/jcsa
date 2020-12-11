"""
This implements the extensible algorithm for pattern mining on either mutation or execution-line in the context of
either static or dynamic (with test cases)
"""

import os
from typing import TextIO
import com.jcsa.pymuta.code as ccode
import com.jcsa.pymuta.muta as cmuta


UC_CLASS = "UC"				# non-covered mutation
UI_CLASS = "UI"				# non-infected mutation
UP_CLASS = "UP"				# non-propagate mutation
KI_CLASS = "KI"				# mutation is killed


class MutationClassifier:
	"""
	It implements the classification on Mutants or MutantExecutionLine.
	"""
	def __init__(self, tests):
		"""
		:param tests: it provides test cases in which the UC, UI, UP, KI are counted
						or None to count on samples itself.
		"""
		self.all_tests = tests
		self.solutions = dict()		# Mutant|MutantExecutionLine --> (uc, ui, up, ki)
		return

	def __get_solution__(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: 	uc (number of non-covered tests on sample)
					ui (number of non-infect tests on sample)
					up (number of non-propagate tests on sample)
					ki (number of killing tests on sample)
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

	def __evaluate_on__(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: 	uc (number of non-covered tests on sample)
					ui (number of non-infect tests on sample)
					up (number of non-propagate tests on sample)
					ki (number of killing tests on sample)
		"""
		if not(sample in self.solutions):
			uc, ui, up, ki = 0, 0, 0, 0
			if isinstance(sample, cmuta.MutantExecutionLine):		# line for test or on mutant when test not specified
				sample: cmuta.MutantExecutionLine
				if sample.has_test_case():							# one line on one test case
					if sample.is_killed():
						ki += 1
					elif sample.is_infected():
						up += 1
					elif sample.is_covered():
						ui += 1
					else:
						uc += 1
				else:												# one line on given tests, --> solve by mutant
					mutant = sample.get_mutant()
					uc, ui, up, ki = self.__evaluate_on__(mutant)
			else:													# mutant for test or none for counting on itself
				sample: cmuta.Mutant
				s_result = sample.get_result()
				w_result = sample.get_weak_mutant().get_result()
				c_result = sample.get_coverage_mutant().get_result()
				if self.all_tests is None:							# none tests ==> counting on sample itself
					if s_result.is_killable():
						ki += 1
					elif w_result.is_killable():
						up += 1
					elif c_result.is_killable():
						ui += 1
					else:
						uc += 1
				else:												# tests set ==> counting on test cases
					for test in self.all_tests:
						if s_result.is_killed_by(test):
							ki += 1
						elif w_result.is_killed_by(test):
							up += 1
						elif c_result.is_killed_by(test):
							ui += 1
						else:
							uc += 1
			self.solutions[sample] = (uc, ui, up, ki)
		return self.__get_solution__(sample)

	def classify_one(self, sample):
		"""
		:param sample: Mutant or MutantExecutionLine
		:return: UC or UI or UP or KI
		"""
		uc, ui, up, ki = self.__evaluate_on__(sample)
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
		:param samples: collection of Mutant or MutantExecutionLine
		:return: mapping from UC|UI|UP|KI to set of samples matching with
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

	def counting_all(self, samples):
		"""
		:param samples: collection of Mutant or MutantExecutionLine
		:return: uc, ui, up, ki, uk, cc
		"""
		uc, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			luc, lui, lup, lki = self.__evaluate_on__(sample)
			uc += luc
			ui += lui
			up += lup
			ki += lki
		return uc, ui, up, ki, uc + ui + up, ui + up

	def estimate_all(self, samples, uk_or_cc: bool):
		"""
		:param uk_or_cc: true to estimate on non-killed sample-tests, while false
							to estimate on coincidental correctness sample-tests.
		:param samples: collection of Mutant or MutantExecutionLine
		:return: total, support, confidence
		"""
		uc, ui, up, ki, uk, cc = self.counting_all(samples)
		total = uc + ui + up + ki
		if uk_or_cc:
			support = uk
		else:
			support = cc
		confidence = 0.0
		if support > 0:
			confidence = support / (support + ki + 0.0)
		return total, support, confidence

	def select_samples(self, samples, uk_or_cc: bool):
		"""
		:param samples: Collection of Mutant or MutantExecutionLine
		:param uk_or_cc: true to select non-killed samples in the context of given test cases
						or false to select coincidental correctness samples
		:return: Set[Mutant|MutantExecutionLine]
		"""
		class_dict = self.classify_all(samples)
		selected_samples = class_dict[UI_CLASS] | class_dict[UP_CLASS]
		if uk_or_cc:
			selected_samples = selected_samples | class_dict[UP_CLASS]
		return selected_samples


class MutationPattern:
	"""
	It describes the pattern of MutantExecutionLine by a set of words in the line
	"""
	def __init__(self, classifier: MutationClassifier):
		"""
		:param classifier: used to estimate this pattern
		"""
		self.words = list()
		self.lines = list()
		self.classifier = classifier
		return

	''' words getters '''

	def get_words(self):
		"""
		:return: feature words that define this pattern
		"""
		return self.words

	def __len__(self):
		"""
		:return: the length of the pattern is the number of its words
		"""
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_annotations(self, program: ccode.CProgram):
		"""
		:param program:
		:return: parse the words into CirAnnotation(s)
		"""
		annotations = list()
		for word in self.words:
			annotation = cmuta.CirAnnotation.parse(word, program)
			annotations.append(annotation)
		return annotations

	''' data operations '''

	def get_lines(self):
		"""
		:return: execution lines matched by this pattern
		"""
		return self.lines

	def get_mutants(self):
		"""
		:return: the mutants of the lines in the pattern
		"""
		mutants = set()
		for line in self.lines:
			line: cmuta.MutantExecutionLine
			mutants.add(line.get_mutant())
		return mutants

	def __match__(self, line: cmuta.MutantExecutionLine):
		"""
		:param line:
		:return: whether the pattern matches with the line
		"""
		for word in self.words:
			if not(word in line.get_words()):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: collection of MutantExecutionLine
		:return: update self.lines by matching with the inputs
		"""
		self.lines.clear()
		for line in lines:
			if self.__match__(line):
				self.lines.append(line)
		return

	''' estimation method '''

	def __samples__(self, line_or_mutant: bool):
		"""
		:param line_or_mutant:
		:return: true to return self.lines or false to return self.get_mutants()
		"""
		if line_or_mutant:
			return self.lines
		else:
			return self.get_mutants()

	def classify(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to classify on lines or false to classify on lines
		:return: mapping from UC|UI|UP|KI to set of Lines (True) or Mutants (False)
		"""
		samples = self.__samples__(line_or_mutant)
		return self.classifier.classify_all(samples)

	def counting(self, line_or_mutant: bool):
		"""
		:param line_or_mutant: true to count on lines or false to classify on lines
		:return: uc, ui, up, ki, uk, cc
		"""
		samples = self.__samples__(line_or_mutant)
		return self.classifier.counting_all(samples)

	def estimate(self, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param line_or_mutant: true to count on lines or false to count on mutants
		:param uk_or_cc: true to estimate on non-killed while false to estimate on coincidental correct samples
		:return: total, support, confidence
		"""
		samples = self.__samples__(line_or_mutant)
		return self.classifier.estimate_all(samples, uk_or_cc)

	''' relationship '''

	def get_child(self, word: str):
		"""
		:param word: additional word added to extend the parent pattern to generate its child
		:return: child pattern extended from this pattern by adding one word or itself
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
		:return: this subsumes the pattern if the lines in pattern is subset of this one
		"""
		pattern: MutationPattern
		for line in pattern.get_lines():
			if not(line in self.lines):
				return False
		return True


class MutationPatterns:
	"""
	It maintains the input and output data for generated patterns
	"""
	def __init__(self, document: cmuta.MutantExecutionDocument, classifier_tests):
		"""
		:param document: it provides all the lines and mutants for mining patterns
		:param classifier_tests: used to create mutation classifier or None to count on sample rather than its tests
		"""
		self.document = document
		self.doc_lines = set()
		self.doc_mutants = set()
		for line in document.get_lines():
			line: cmuta.MutantExecutionLine
			self.doc_lines.add(line)
			self.doc_mutants.add(line.get_mutant())
		self.classifier = MutationClassifier(classifier_tests)
		self.patterns = set()
		self.pat_lines = set()
		self.pat_mutants = set()
		return

	def get_doc_lines(self):
		"""
		:return: lines in the document
		"""
		return self.doc_lines

	def get_doc_mutants(self):
		"""
		:return: mutants in the document
		"""
		return self.doc_mutants

	def get_pat_lines(self):
		"""
		:return: the lines in the patterns
		"""
		return self.pat_lines

	def get_pat_mutants(self):
		"""
		:return: mutants in the pattern
		"""
		return self.pat_mutants

	def get_patterns(self):
		"""
		:return: patterns generated in the data context
		"""
		return self.patterns

	def set_patterns(self, patterns):
		"""
		:param patterns:
		:return: update the patterns and lines in the pattern
		"""
		self.patterns.clear()
		self.pat_mutants.clear()
		self.pat_lines.clear()
		for pattern in patterns:
			pattern: MutationPattern
			self.patterns.add(pattern)
			for line in pattern.get_lines():
				line: cmuta.MutantExecutionLine
				self.pat_lines.add(line)
				self.pat_mutants.add(line.get_mutant())
		return

	''' generator methods '''

	@staticmethod
	def get_minimal_patterns(patterns):
		"""
		:param patterns:
		:return: minimal patterns that subsume all the others in the set
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
		:return: mapping from line(True) or Mutant(False) to patterns
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
				if not (sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def get_best_pattern_in(patterns, line_or_mutant: bool, uk_or_cc: bool, buffer_size=8):
		"""
		:param patterns:
		:param line_or_mutant: true to take line as sample or false to take mutant as sample
		:param uk_or_cc: true to estimate on non-killed but false to estimate on coincidental correctness
		:param buffer_size: size of buffer to preserve top-N confidence
		:return:
		"""
		new_patterns = set()
		for pattern in patterns:
			pattern: MutationPattern
			new_patterns.add(pattern)

		precision_buffer = list()
		while len(precision_buffer) < buffer_size and len(new_patterns) > 0:
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


class MutationPatternGenerator:
	"""
	It generates patterns using frequent pattern mining.
	"""
	def __init__(self, line_or_mutant: bool, uk_or_cc: bool, min_support: int, max_confidence: float, max_length: int):
		"""
		:param line_or_mutant: true to take sample as MutantExecutionLine or false to take as Mutant
		:param uk_or_cc: true to estimate on non-killed samples while false to estimate on CC ones
		:param min_support: minimal support required for generated patterns
		:param max_confidence: maximal confidence to stop pattern mining
		:param max_length: maximal length for words in the pattern
		"""
		self.line_or_mutant = line_or_mutant
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_confidence = max_confidence
		self.max_length = max_length
		self.patterns = dict()		# string ==> MutationPattern
		self.solutions = dict()		# MutationPattern ==> [total, support, confidence]
		return

	def __root__(self, patterns: MutationPatterns, word: str):
		"""
		:param patterns:
		:param word:
		:return: unique root pattern with one single word
		"""
		root = MutationPattern(patterns.classifier)
		root = root.get_child(word.strip())
		if not(str(root) in self.patterns):
			self.patterns[str(root)] = root
			root.set_lines(patterns.get_doc_lines())
		root = self.patterns[str(root)]
		root: MutationPattern
		return root

	def __child__(self, parent: MutationPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: child pattern extended from parent with adding one word
		"""
		child = parent.get_child(word.strip())
		if not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_lines(parent.get_lines())
		child = self.patterns[str(child)]
		child: MutationPattern
		return child

	def __inputs__(self, patterns: MutationPatterns):
		"""
		:param patterns:
		:return: the collection of lines as either non-killed or coincidental correctness
		"""
		classifier = patterns.classifier
		return classifier.select_samples(patterns.get_doc_lines(), self.uk_or_cc)

	def __output__(self, patterns: MutationPatterns):
		"""
		:param patterns:
		:return: update the good patterns generated from solutions
		"""
		good_patterns = set()
		for pattern, solution in self.solutions.items():
			pattern: MutationPattern
			support = solution[1]
			confidence = solution[2]
			length = len(pattern.words)
			if length <= self.max_length and support >= self.min_support and confidence >= self.max_confidence:
				good_patterns.add(pattern)
		patterns.set_patterns(good_patterns)
		return

	def __generate__(self, parent: MutationPattern, words):
		if not(parent in self.solutions):
			total, support, confidence = parent.estimate(self.line_or_mutant, self.uk_or_cc)
			self.solutions[parent] = (total, support, confidence)
		solution = self.solutions[parent]
		support = solution[1]
		confidence = solution[2]
		length = len(parent.words)
		if length < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__generate__(child, words)
		return

	def generate(self, patterns: MutationPatterns):
		"""
		:param patterns:
		:return:
		"""
		init_lines = self.__inputs__(patterns)
		for init_line in init_lines:
			init_line: cmuta.MutantExecutionLine
			words = init_line.get_words()
			for word in words:
				root = self.__root__(patterns, word)
				self.__generate__(root, words)
		self.__output__(patterns)
		return


class MutationPatternsWriter:
	"""
	It implements writing the patterns information.
	"""
	def __init__(self, data: MutationPatterns):
		self.data = data
		self.writer = None
		return

	@staticmethod
	def __proportion__(x: int, y: int):
		"""
		:param x:
		:param y:
		:return:
		"""
		if x == 0:
			proportion = 0.0
		else:
			proportion = x / (y + 0.0)
		return MutationPatternsWriter.__percent__(proportion)

	@staticmethod
	def __percent__(proportion: float):
		return int(100000000 * proportion) / 1000000.0

	@staticmethod
	def __precision_recall__(doc_samples: set, pat_samples: set):
		int_samples = doc_samples & pat_samples
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if len(int_samples) > 0:
			precision = len(int_samples) / (len(pat_samples) + 0.0)
			recall = len(int_samples) / (len(doc_samples) + 0.0)
			f1_score = 2 * precision * recall / (precision + recall)
		return MutationPatternsWriter.__percent__(precision), MutationPatternsWriter.__percent__(recall), f1_score

	''' pattern writers '''

	def __write_pattern_count__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return:
		"""
		''' summary word lines mutants '''
		self.writer: TextIO
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Summary", "Words", len(pattern.get_words()),
																  "Lines", len(pattern.get_lines()),
																  "Mutants", len(pattern.get_mutants())))
		''' metrics UC UI UP KI UK(%) CC(%) '''
		line2 = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.writer.write(line2.format("Metrics", "UC", "UI", "UP", "KI", "UK(%)", "CC(%)"))
		uc, ui, up, ki, uk, cc = pattern.counting(True)
		self.writer.write(line2.format("Lines", uc, ui, up, ki,
									   MutationPatternsWriter.__proportion__(uk, uk + ki),
									   MutationPatternsWriter.__proportion__(cc, cc + ki)))
		uc, ui, up, ki, uk, cc = pattern.counting(False)
		self.writer.write(line2.format("Mutants", uc, ui, up, ki,
									   MutationPatternsWriter.__proportion__(uk, uk + ki),
									   MutationPatternsWriter.__proportion__(cc, cc + ki)))
		return

	def __write_pattern_words__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return:
		"""
		self.writer: TextIO
		self.writer.write("\tIndex\tType\tExecution\tStatement\tLocation\tParameter\n")
		annotations = pattern.get_annotations(self.data.document.get_project().program)
		index = 0
		for annotation in annotations:
			self.writer.write("\t{}\t{}\t{}\t\"{}\"\t\"{}\"\t\"{}\"\n".
							  format(index, annotation.get_type(), annotation.get_execution(),
									 annotation.get_execution().get_statement().get_cir_code(),
									 annotation.get_location().get_cir_code(),
									 annotation.get_parameter()))
			index += 1
		return

	def __write_pattern_lines__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return:
		"""
		mutants = pattern.get_mutants()
		self.writer: TextIO
		self.writer.write("\tID\tRES\tCLASS\tOPERATOR\tLINE\tCODE\tPARAMETER\n")
		for mutant in mutants:
			mutation = mutant.get_mutation()
			result = self.data.classifier.classify_one(mutant)
			location = mutation.get_location()
			self.writer.write("\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".
							  format(mutant.get_mut_id(), result, mutation.get_mutation_class(),
									 mutation.get_mutation_operator(), location.line_of(),
									 location.get_code(True), mutation.get_parameter()))
		return

	def __write_pattern__(self, pattern: MutationPattern, pattern_index: int):
		"""
		:param pattern:
		:param pattern_index:
		:return:
		"""
		self.writer: TextIO
		self.writer.write("#BEG\t" + str(pattern_index) + "\n")
		self.__write_pattern_count__(pattern)
		self.writer.write("\n")
		self.__write_pattern_words__(pattern)
		self.writer.write("\n")
		self.__write_pattern_lines__(pattern)
		self.writer.write("#END\t" + str(pattern_index) + "\n")
		return

	def write_patterns(self, patterns, output_file: str):
		with open(output_file, 'w') as writer:
			self.writer = writer
			index = 0
			for pattern in patterns:
				index += 1
				self.__write_pattern__(pattern, index)
				self.writer.write("\n")
		return

	''' testing results '''

	def __write_results__(self, patterns):
		"""
		:param patterns:
		:return:
		"""
		self.writer: TextIO

		''' project: doc_mutants, doc_lines, killed_mutants, over_score(valid_score) '''
		doc_lines = self.data.get_doc_lines()
		doc_mutants = self.data.get_doc_mutants()
		test_cases = self.data.classifier.all_tests
		if test_cases is None:
			test_cases = self.data.document.get_project().test_space.get_test_cases()
		killed, over_score, valid_score = self.data.document.get_project().evaluation.evaluate_mutation_score(doc_mutants, test_cases)
		self.writer.write("Project\tMutants := {}\tLines := {}\tKilled := {}\tScore = {}%({}%)\n".
						  format(len(doc_mutants), len(doc_lines), killed, over_score, valid_score))

		''' mining: patterns, pat_lines(patterns/pat_lines), pat_mutants(patterns/pat_mutants) '''
		pat_lines = self.data.get_pat_lines()
		pat_mutants = self.data.get_pat_mutants()
		self.writer.write("Mining\tPatterns := {}\tLines := {}({}%)\tMutants := {}({}%)\n".
						  format(len(patterns),
								 len(pat_lines),
								 MutationPatternsWriter.__proportion__(len(patterns), len(pat_lines)),
								 len(pat_mutants),
								 MutationPatternsWriter.__proportion__(len(patterns), len(pat_mutants))))
		self.writer.write("\n")

		''' UK-Line: doc_uk_support pat_uk_support pat_uk_precision pat_uk_recall '''
		self.writer.write("\tTitle\tD_Support\tP_Support\tPrecision(%)\tRecall(%)\tF1-Score\n")

		doc_support = self.data.classifier.select_samples(doc_lines, True)
		pat_support = self.data.classifier.select_samples(pat_lines, True)
		precision, recall, f1_score = MutationPatternsWriter.__precision_recall__(doc_support, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\n".
						  format("UK-Line", len(doc_support), len(pat_support), precision, recall, f1_score))

		doc_support = self.data.classifier.select_samples(doc_lines, False)
		pat_support = self.data.classifier.select_samples(pat_lines, False)
		precision, recall, f1_score = MutationPatternsWriter.__precision_recall__(doc_support, pat_lines)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\n".
						  format("CC-Line", len(doc_support), len(pat_support), precision, recall, f1_score))

		doc_support = self.data.classifier.select_samples(doc_mutants, True)
		pat_support = self.data.classifier.select_samples(pat_mutants, True)
		precision, recall, f1_score = MutationPatternsWriter.__precision_recall__(doc_support, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\n".
						  format("UK-Muta", len(doc_support), len(pat_support), precision, recall, f1_score))

		doc_support = self.data.classifier.select_samples(doc_mutants, False)
		pat_support = self.data.classifier.select_samples(pat_mutants, False)
		precision, recall, f1_score = MutationPatternsWriter.__precision_recall__(doc_support, pat_mutants)
		self.writer.write("\t{}\t{}\t{}\t{}\t{}\t{}\n".
						  format("CC-Muta", len(doc_support), len(pat_support), precision, recall, f1_score))
		return

	def write_results(self, patterns, output_file: str):
		"""
		:param patterns:
		:param output_file:
		:return:
		"""
		with open(output_file, 'w') as writer:
			self.writer = writer
			self.__write_results__(patterns)
		return

	def write_best_patterns(self, patterns, output_file: str, line_or_mutant: bool, uk_or_cc: bool):
		"""
		:param data:
		:param patterns:
		:param output_file:
		:param line_or_mutant:
		:param uk_or_cc:
		:return:
		"""
		mutant_patterns = MutationPatterns.map_samples_to_patterns(patterns, False)
		with open(output_file, 'w') as writer:
			self.writer = writer
			for mutant, patterns in mutant_patterns.items():
				mutant: cmuta.Mutant
				line = "\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t\"{}\"\n"
				mutation = mutant.get_mutation()
				result = self.data.classifier.classify_one(mutant)
				writer.write(line.format(mutant.get_mut_id(), result,
										 mutation.mutation_class,
										 mutation.mutation_operator,
										 mutation.get_location().line_of(),
										 mutation.location.get_code(True),
										 mutation.parameter))
				best_pattern = MutationPatterns.get_best_pattern_in(patterns, line_or_mutant, uk_or_cc)
				if best_pattern is not None:
					self.__write_pattern_words__(best_pattern)
				self.writer.write("\n")
		return


def mining_patterns_on_none(input_directory: str, output_directory: str, file_name: str,
							line_or_mutant: bool, uk_or_cc: bool,
							min_support: int, max_confidence: float, max_length: int):
	"""
	:param file_name: name of file of the project
	:param input_directory: directory in which xxx.cpp, xxx.ast, xxx.cir features are provided
	:param output_directory: directory to preserve the patterns information
	:param line_or_mutant: to take line or mutant as sample for counting
	:param uk_or_cc: to estimate on non-killed or coincidental correctness samples
	:param min_support: minimal support required for pattern
	:param max_confidence: maximal confidence achieved for stopping generation of patterns
	:param max_length: maximal length of words required in patterns being generated
	:return:
	"""
	if not (os.path.exists(output_directory)):
		os.mkdir(output_directory)
	print("Testing on none for", file_name)
	project_directory = os.path.join(input_directory, file_name)
	c_project = cmuta.CProject(project_directory, file_name)
	c_document = c_project.load_execution_document(os.path.join(project_directory, file_name + ".sft"))
	print("\t(1) Load", len(c_document.get_lines()), "lines for", len(c_document.get_mutants()), "mutants with",
		  len(c_document.get_corpus()), "words from the project under test.")

	patterns_data = MutationPatterns(c_document, None)
	generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_confidence, max_length)
	generator.generate(patterns_data)
	min_patterns = MutationPatterns.get_minimal_patterns(patterns_data.get_patterns())
	print("\t(2) Generate", len(patterns_data.get_patterns()), "patterns with", len(min_patterns), "ones of minimal.")

	# TODO rebuild the writing methods...
	writer = MutationPatternsWriter(patterns_data)
	writer.write_patterns(min_patterns, os.path.join(output_directory, file_name + ".mpt"))
	writer.write_results(min_patterns, os.path.join(output_directory, file_name + ".rpt"))
	writer.write_best_patterns(min_patterns, os.path.join(output_directory, file_name + ".bpt"), line_or_mutant, uk_or_cc)
	print("\t(3) Write", len(min_patterns), "patterns to output files.")
	print()
	return


def mining_patterns_on_test(input_directory: str, output_directory: str, file_name: str,
							line_or_mutant: bool, uk_or_cc: bool,
							min_support: int, max_confidence: float, max_length: int):
	"""
	:param file_name: name of file of the project
	:param input_directory: directory in which xxx.cpp, xxx.ast, xxx.cir features are provided
	:param output_directory: directory to preserve the patterns information
	:param line_or_mutant: to take line or mutant as sample for counting
	:param uk_or_cc: to estimate on non-killed or coincidental correctness samples
	:param min_support: minimal support required for pattern
	:param max_confidence: maximal confidence achieved for stopping generation of patterns
	:param max_length: maximal length of words required in patterns being generated
	:return:
	"""
	if not (os.path.exists(output_directory)):
		os.mkdir(output_directory)
	print("Testing on test for", file_name)
	project_directory = os.path.join(input_directory, file_name)
	c_project = cmuta.CProject(project_directory, file_name)
	c_document = c_project.load_execution_document(os.path.join(project_directory, file_name + ".sft"))
	print("\t(1) Load", len(c_document.get_lines()), "lines for", len(c_document.get_mutants()), "mutants with",
		  len(c_document.get_corpus()), "words from the project under test.")

	selected_mutants = c_project.evaluation.select_mutants_by_classes(["STRP", "BTRP"])
	minimal_tests, __remains__ = c_project.evaluation.select_tests_for_mutants(selected_mutants)
	random_tests = c_project.evaluation.select_tests_for_random(int(len(c_project.test_space.test_cases) * 0.005))
	selected_tests = minimal_tests | random_tests
	killed, over_score, valid_score = c_project.evaluation.\
		evaluate_mutation_score(c_project.mutant_space.get_mutants(), selected_tests)
	print("\t\tSelect", len(selected_tests), "test cases with {}% ({}%).".format(over_score, valid_score))

	patterns_data = MutationPatterns(c_document, selected_tests)
	generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_confidence, max_length)
	generator.generate(patterns_data)
	min_patterns = MutationPatterns.get_minimal_patterns(patterns_data.get_patterns())
	print("\t(2) Generate", len(patterns_data.get_patterns()), "patterns with", len(min_patterns), "ones of minimal.")

	# TODO rebuild the writing methods...
	writer = MutationPatternsWriter(patterns_data)
	writer.write_patterns(min_patterns, os.path.join(output_directory, file_name + ".mpt"))
	writer.write_results(min_patterns, os.path.join(output_directory, file_name + ".rpt"))
	writer.write_best_patterns(min_patterns, os.path.join(output_directory, file_name + ".bpt"), line_or_mutant, uk_or_cc)
	print("\t(3) Write", len(min_patterns), "patterns to output files.")
	print()
	return


def mining_patterns_on_over(input_directory: str, output_directory: str, file_name: str,
							line_or_mutant: bool, uk_or_cc: bool,
							min_support: int, max_confidence: float, max_length: int):
	"""
	:param file_name: name of file of the project
	:param input_directory: directory in which xxx.cpp, xxx.ast, xxx.cir features are provided
	:param output_directory: directory to preserve the patterns information
	:param line_or_mutant: to take line or mutant as sample for counting
	:param uk_or_cc: to estimate on non-killed or coincidental correctness samples
	:param min_support: minimal support required for pattern
	:param max_confidence: maximal confidence achieved for stopping generation of patterns
	:param max_length: maximal length of words required in patterns being generated
	:return:
	"""
	if not (os.path.exists(output_directory)):
		os.mkdir(output_directory)
	print("Testing on over for", file_name)
	project_directory = os.path.join(input_directory, file_name)
	c_project = cmuta.CProject(project_directory, file_name)
	c_document = c_project.load_execution_document(os.path.join(project_directory, file_name + ".sft"))
	print("\t(1) Load", len(c_document.get_lines()), "lines for", len(c_document.get_mutants()), "mutants with",
		  len(c_document.get_corpus()), "words from the project under test.")

	patterns_data = MutationPatterns(c_document, c_project.test_space.get_test_cases())
	generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_confidence, max_length)
	generator.generate(patterns_data)
	min_patterns = MutationPatterns.get_minimal_patterns(patterns_data.get_patterns())
	print("\t(2) Generate", len(patterns_data.get_patterns()), "patterns with", len(min_patterns), "ones of minimal.")

	# TODO rebuild the writing methods...
	writer = MutationPatternsWriter(patterns_data)
	writer.write_patterns(min_patterns, os.path.join(output_directory, file_name + ".mpt"))
	writer.write_results(min_patterns, os.path.join(output_directory, file_name + ".rpt"))
	writer.write_best_patterns(min_patterns, os.path.join(output_directory, file_name + ".bpt"), line_or_mutant, uk_or_cc)
	print("\t(3) Write", len(min_patterns), "patterns to output files.")
	print()
	return



if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	none_path = "/home/dzt2/Development/Data/patterns/none"
	test_path = "/home/dzt2/Development/Data/patterns/test"
	over_path = "/home/dzt2/Development/Data/patterns/over"
	for file_name in os.listdir(prev_path):
		mining_patterns_on_none(prev_path, none_path, file_name, True, False, 2, 0.75, 1)
		mining_patterns_on_test(prev_path, test_path, file_name, True, False, 10, 0.80, 1)
		mining_patterns_on_over(prev_path, over_path, file_name, True, False, 50, 0.80, 1)
	print("Test end for all...")

