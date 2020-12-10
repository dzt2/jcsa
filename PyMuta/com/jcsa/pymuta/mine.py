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


class MutationPatternWriter:
	"""
	It implements writing the information of patterns on output
	"""

	@staticmethod
	def __proportion__(x: int, y: int):
		if x == 0:
			return 0.0
		else:
			ratio = x / (y + 0.0)
			return int(ratio * 100000000) / 1000000.0

	@staticmethod
	def __prf_evaluation__(doc_samples: set, pat_samples: set):
		int_samples = doc_samples & pat_samples
		doc_size, pat_size, int_size = len(doc_samples), len(pat_samples), len(int_samples)
		if int_size > 0:
			precision = int_size / (pat_size + 0.0)
			recall = int_size / (doc_size + 0.0)
			f1_score = 2 * precision * recall / (precision + recall)
			return int(precision * 10000) / 100.0, int(1000000 * recall) / 10000.0, int(f1_score * 1000000) / 1000000.0
		return 0.0, 0.0, 0.0

	''' pattern writer '''

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
		writer.write(line2.format("Line", uc, ui, up, ki,
								  MutationPatternWriter.__proportion__(uk, uk + ki),
								  MutationPatternWriter.__proportion__(cc, cc + ki)))
		uc, ui, up, ki, uk, cc = pattern.counting(False)
		writer.write(line2.format("Mutant", uc, ui, up, ki,
								  MutationPatternWriter.__proportion__(uk, uk + ki),
								  MutationPatternWriter.__proportion__(cc, cc + ki)))
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
								annotation.get_execution().get_statement().get_cir_code(),
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
	def __write_patterns_summary__(writer: TextIO, data: MutationPatterns, patterns):
		"""
		:param writer:
		:param data:
		:return:
		"""
		doc_lines = data.get_doc_lines()
		doc_mutants = data.get_doc_mutants()
		pat_lines = data.get_pat_lines()
		pat_mutants = data.get_pat_mutants()
		classifier = data.classifier
		writer.write("Evaluation\n")
		writer.write("\tPatterns := {}\tLines := {}({}%)\tMutants := {}({}%)\n".
					 format(len(patterns),
							len(pat_lines),
							MutationPatternWriter.__proportion__(len(patterns), len(pat_lines)),
							len(pat_mutants),
							MutationPatternWriter.__proportion__(len(patterns), len(pat_mutants))))
		total, uk_support, precision = classifier.estimate_all(doc_lines, True)
		total, cc_support, precision = classifier.estimate_all(doc_lines, False)
		writer.write("\tPatterns := {}\tUK-Execs := {}({}%)\tUK-Execs := {}({}%)\n".
					 format(len(patterns),
							uk_support,
							MutationPatternWriter.__proportion__(len(patterns), uk_support),
							cc_support,
							MutationPatternWriter.__proportion__(len(patterns), cc_support)))
		writer.write("\tTitle\tPrecision(%)\tRecall(%)\tF1-Score\n")
		uk_lines = classifier.select_samples(doc_lines, True)
		cc_lines = classifier.select_samples(doc_lines, False)
		uk_mutants = classifier.select_samples(doc_mutants, True)
		cc_mutants = classifier.select_samples(doc_mutants, False)
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
	def write_patterns(data: MutationPatterns, patterns, output_file_path: str):
		with open(output_file_path, 'w') as writer:
			MutationPatternWriter.__write_patterns_summary__(writer, data, patterns)
			pid = 0
			for pattern in patterns:
				pattern: MutationPattern
				writer.write("\nBEG\t" + str(pid) + "\n")
				MutationPatternWriter.__write_pattern_count__(writer, pattern)
				MutationPatternWriter.__write_pattern_words__(writer, pattern, data.document.get_project().program)
				MutationPatternWriter.__write_pattern_lines__(writer, pattern)
				writer.write("END\t" + str(pid) + "\n")
				pid += 1
				writer.write("\n")
		return

	@staticmethod
	def write_best_patterns(data: MutationPatterns, patterns, output_file: str, line_or_mutant: bool, uk_or_cc: bool):
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
			for mutant, patterns in mutant_patterns.items():
				mutant: cmuta.Mutant
				line = "\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t\"{}\"\n"
				mutation = mutant.get_mutation()
				result = data.classifier.classify_one(mutant)
				writer.write(line.format(mutant.get_mut_id(), result,
										 mutation.mutation_class,
										 mutation.mutation_operator,
										 mutation.get_location().line_of(),
										 mutation.location.get_code(True),
										 mutation.parameter))
				best_pattern = MutationPatterns.get_best_pattern_in(patterns, line_or_mutant, uk_or_cc)
				if best_pattern is not None:
					MutationPatternWriter.__write_pattern_words__(writer, best_pattern,
																  mutant.get_space().get_project().program)
		return

	@staticmethod
	def write_mutant_results(data: MutationPatterns, patterns, output_file: str):
		"""
		:param data:
		:param patterns:
		:param output_file:
		:return:
		"""
		with open(output_file, 'w') as writer:
			''' project: doc_mutants, doc_lines, killed_mutants, over_score(valid_score) '''
			doc_lines = data.get_doc_lines()
			doc_mutants = data.get_doc_mutants()
			test_cases = data.classifier.all_tests
			if test_cases is None:
				test_cases = data.document.get_project().test_space.get_test_cases()
			killed_mutants, over_score, valid_score = data.document.\
				get_project().evaluation.evaluate_mutation_score(doc_mutants, test_cases)
			writer.write("Project\tMutants := {}\tLines := {}\tKilled := {}\tScore = {}%({}%)\n".
						 format(len(doc_mutants), len(doc_lines), killed_mutants, over_score, valid_score))

			''' mining: patterns, pat_lines(patterns/pat_lines), pat_mutants(patterns/pat_mutants) '''
			pat_lines = data.get_pat_lines()
			pat_mutants = data.get_pat_mutants()
			writer.write("Mining\tPatterns := {}\tLines := {}({}%)\tMutants := {}({}%)\n".
						 format(len(patterns),
								len(pat_lines),
								MutationPatternWriter.__proportion__(len(patterns), len(pat_lines)),
								len(pat_mutants),
								MutationPatternWriter.__proportion__(len(patterns), len(pat_mutants))))

			''' UK-count: doc_uk_support pat_uk_support pat_uk_precision pat_uk_recall '''
			doc_uk_total, doc_uk_support, doc_uk_precision = data.classifier.estimate_all(doc_mutants, True)
			pat_uk_total, pat_uk_support, pat_uk_precision = data.classifier.estimate_all(pat_mutants, True)
			writer.write("{}\tD_support := {}\tP_support := {}\tPrecision := {}%\tRecall := {}%\n".
						 format("UK-Count",
								doc_uk_support,
								pat_uk_support,
								int(pat_uk_precision * 100000000) / 1000000.0,
								MutationPatternWriter.__proportion__(pat_uk_support, doc_uk_support)))
			''' CC-count: doc_cc_support pat_cc_support pat_cc_precision pat_cc_recall '''
			doc_cc_total, doc_cc_support, doc_cc_precision = data.classifier.estimate_all(doc_mutants, False)
			pat_cc_total, pat_cc_support, pat_cc_precision = data.classifier.estimate_all(pat_mutants, False)
			writer.write("{}\tD_support := {}\tP_support := {}\tPrecision := {}%\tRecall := {}%\n".
						 format("CC-Count",
								doc_cc_support,
								pat_cc_support,
								int(pat_cc_precision * 100000000) / 1000000.0,
								MutationPatternWriter.__proportion__(pat_cc_support, doc_cc_support)))
		return


def mining_patterns_on_none(root_path: str, post_path: str):
	"""
	:param root_path:
	:param post_path:
	:return:
	"""
	line_or_mutant, uk_or_cc, min_support, max_precision, max_length = True, True, 2, 0.80, 1
	if not (os.path.exists(post_path)):
		os.mkdir(post_path)
	for file_name in os.listdir(root_path):
		''' 1. load project data '''
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		data = MutationPatterns(docs, None)
		print("Load", len(docs.get_lines()), "execution lines with", len(docs.corpus), "words from", file_name)

		''' 2. generate mutation patterns '''
		generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_precision, max_length)
		generator.generate(data)
		god_patterns = data.get_patterns()
		min_patterns = MutationPatterns.get_minimal_patterns(god_patterns)
		print("\t(1) Generate", len(god_patterns), "mutation patterns from the document with", len(min_patterns),
			  "of minimal set.")

		''' 3. output patterns information '''
		MutationPatternWriter.write_patterns(data, min_patterns, os.path.join(post_path, file_name + ".mpt"))
		print("\t(2) Write", len(min_patterns), "patterns of good set to output file")
		MutationPatternWriter.write_best_patterns(data, min_patterns,
												  os.path.join(post_path, file_name + ".bpt"), line_or_mutant, uk_or_cc)
		print("\t(3) Write best patterns on output file for", file_name)
		MutationPatternWriter.write_mutant_results(data, min_patterns, os.path.join(post_path, file_name + ".mtr"))
		print("\t(4) Write mutation-test results on output file.")
		print()
	return


def mining_patterns_on_test(root_path: str, post_path: str):
	line_or_mutant, uk_or_cc, min_support, max_precision, max_length, test_proportion = True, False, 2, 0.80, 1, 0.005
	if not (os.path.exists(post_path)):
		os.mkdir(post_path)
	for file_name in os.listdir(root_path):
		''' 1. load project data '''
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		print("Load", len(docs.get_lines()), "execution lines with", len(docs.corpus), "words from", file_name)

		''' 2. select test cases and generate frequent patterns '''
		selected_mutants = c_project.evaluation.select_mutants_by_classes(["STRP", "BTRP"])
		minimal_test_number = int(test_proportion * len(c_project.test_space.get_test_cases()))
		minimal_tests, __remain__ = c_project.evaluation.select_tests_for_mutants(selected_mutants)
		random_tests = c_project.evaluation.select_tests_for_random(minimal_test_number)
		selected_tests = minimal_tests | random_tests
		killed, over_score, valid_score = c_project.evaluation.\
			evaluate_mutation_score(c_project.mutant_space.get_mutants(), selected_tests)
		print("\tSelect {} test cases, killing {} mutants with score = {}%({}%).".
			  format(len(selected_tests), killed, over_score, valid_score))

		''' 3. generate patterns '''
		data = MutationPatterns(docs, selected_tests)
		generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_precision, max_length)
		generator.generate(data)
		god_patterns = data.get_patterns()
		min_patterns = MutationPatterns.get_minimal_patterns(god_patterns)
		print("\t(1) Generate", len(god_patterns), "mutation patterns from the document with", len(min_patterns),
			  "of minimal set.")

		''' 4. output patterns information '''
		MutationPatternWriter.write_patterns(data, min_patterns, os.path.join(post_path, file_name + ".mpt"))
		print("\t(2) Write", len(min_patterns), "patterns of good set to output file")
		MutationPatternWriter.write_best_patterns(data, min_patterns,
												  os.path.join(post_path, file_name + ".bpt"), line_or_mutant, uk_or_cc)
		print("\t(3) Write best patterns on output file for", file_name)
		MutationPatternWriter.write_mutant_results(data, min_patterns, os.path.join(post_path, file_name + ".mtr"))
		print("\t(4) Write mutation-test results on output file.")
		print()
	return


def mining_patterns_on_over(root_path: str, post_path: str):
	"""
	:param root_path:
	:param post_path:
	:return:
	"""
	line_or_mutant, uk_or_cc, min_support, max_precision, max_length = True, True, 50, 0.80, 1
	if not (os.path.exists(post_path)):
		os.mkdir(post_path)
	for file_name in os.listdir(root_path):
		''' 1. load project data '''
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		docs = c_project.load_execution_document(os.path.join(directory, file_name + ".sft"))
		data = MutationPatterns(docs, c_project.test_space.get_test_cases())
		print("Load", len(docs.get_lines()), "execution lines with", len(docs.corpus), "words from", file_name)

		''' 2. generate mutation patterns '''
		generator = MutationPatternGenerator(line_or_mutant, uk_or_cc, min_support, max_precision, max_length)
		generator.generate(data)
		god_patterns = data.get_patterns()
		min_patterns = MutationPatterns.get_minimal_patterns(god_patterns)
		print("\t(1) Generate", len(god_patterns), "mutation patterns from the document with", len(min_patterns),
			  "of minimal set.")

		''' 3. output patterns information '''
		MutationPatternWriter.write_patterns(data, min_patterns, os.path.join(post_path, file_name + ".mpt"))
		print("\t(2) Write", len(min_patterns), "patterns of good set to output file")
		MutationPatternWriter.write_best_patterns(data, min_patterns,
												  os.path.join(post_path, file_name + ".bpt"), line_or_mutant, uk_or_cc)
		print("\t(3) Write best patterns on output file for", file_name)
		MutationPatternWriter.write_mutant_results(data, min_patterns, os.path.join(post_path, file_name + ".mtr"))
		print("\t(4) Write mutation-test results on output file.")
		print()
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	none_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/patterns/none"
	test_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/patterns/test"
	alls_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/patterns/alls"
	mining_patterns_on_none(prev_path, none_path)
	mining_patterns_on_test(prev_path, test_path)
	mining_patterns_on_over(prev_path, alls_path)
	print("Test end for all...")

