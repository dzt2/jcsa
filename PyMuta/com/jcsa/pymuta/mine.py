"""
This file implements the frequent pattern mining algorithm to mine the pattern of mutation and execution lines.
"""

import os
from typing import TextIO
import com.jcsa.pymuta.muta as cmuta


class MutationPattern:
	"""
	The pattern of Mutation & MutationLine corresponds to a collection of words as annotations.
	"""
	''' constructor '''
	def __init__(self):
		self.words = list()
		self.lines = list()
		return

	''' words features '''
	def get_words(self):
		"""
		:return: the collection of annotation words in this pattern.
		"""
		return self.words

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_annotations(self, project: cmuta.CProject):
		"""
		:param project:
		:return: translate the words in the patterns to the set of annotations they correspond to
		"""
		annotations = list()
		for word in self.words:
			annotation = cmuta.CAnnotation.parse(project, word.strip())
			annotations.append(annotation)
		return annotations

	''' samples collections '''
	def get_lines(self):
		"""
		:return: the collection of mutation execution lines matching with this pattern
		"""
		return self.lines

	def get_mutations(self):
		"""
		:return: the collection of mutations of which any line matches with this pattern
		"""
		mutations = set()
		for line in self.lines:
			line: cmuta.MutationLine
			mutation = line.get_mutation()
			mutations.add(mutation)
		return mutations

	def __match__(self, line: cmuta.MutationLine):
		"""
		:param line:
		:return: whether the mutation execution line matches with the pattern
		"""
		for word in self.words:
			if not(word in line.get_words()):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: the collection of mutation execution lines in which the pattern goes to match with
		:return: None and update the lines matching within this pattern, which is computationally expansive
		"""
		self.lines.clear()
		for line in lines:
			line: cmuta.MutationLine
			if self.__match__(line):
				self.lines.append(line)
		return

	''' pattern generators '''
	def get_child(self, word: str):
		"""
		:param word: word being appended in the new child pattern
		:return: child pattern by inserting one new word or itself if the word exists in the current pattern
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = MutationPattern()
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: whether this pattern subsumes the target pattern, of which lines is the subset of this one
		"""
		pattern: MutationPattern
		for line in pattern.lines:
			if not(line in self.lines):
				return False
		return True


class MutationClassifier:
	"""
	It implements the classification of mutation or mutation execution line.
	"""

	@staticmethod
	def classify_sample(sample):
		"""
		:param sample: MutationLine or Mutation
		:return: UC (Uncovered); UI (Not-Infected); UP (Not-Propagate); KI (Killed);
		"""
		if isinstance(sample, cmuta.MutationLine):
			sample: cmuta.MutationLine
			if sample.get_killing_result():
				return "KI"
			elif sample.get_infection_result():
				return "UP"
			elif sample.get_coverage_result():
				return "UI"
			else:
				return "UC"
		else:
			sample: cmuta.Mutation
			if sample.get_result().is_killed():
				return "KI"
			elif sample.get_result().is_infected():
				return "UP"
			elif sample.get_result().is_covered():
				return "UI"
			else:
				return "UC"

	@staticmethod
	def classify_samples(samples):
		"""
		:param samples: set of mutation execution line (MutationLine) or mutation (Mutation)
		:return: mapping from [UC, UI, UP, KI] to collection of MutationLine or Mutation sample
		"""
		class_dict = dict()
		class_dict["UC"] = set()
		class_dict["UI"] = set()
		class_dict["UP"] = set()
		class_dict["KI"] = set()
		for sample in samples:
			sample_class = MutationClassifier.classify_sample(sample)
			class_dict[sample_class].add(sample)
		return class_dict

	@staticmethod
	def classify_pattern(pattern: MutationPattern, line_or_mutation: bool):
		"""
		:param pattern: of which lines (True) or mutations (False) to be classified
		:param line_or_mutation: true to classify the lines and false to classify the mutations
		:return: mapping from [UC, UI, UP, KI] to the set of lines (True) or mutations (False).
		"""
		if line_or_mutation:
			return MutationClassifier.classify_samples(pattern.get_lines())
		else:
			return MutationClassifier.classify_samples(pattern.get_mutations())

	@staticmethod
	def counting_pattern(pattern: MutationPattern, line_or_mutation: bool):
		"""
		:param pattern: of which lines (True) or mutations (False) to be counted
		:param line_or_mutation: true to classify the lines and false to classify the mutations
		:return: uc (not-covered); ui (not-infected); up (not-propagate); ki (killed); uk (not-killed);
				cc (coincidental correctness);
		"""
		class_table = MutationClassifier.classify_pattern(pattern, line_or_mutation)
		uc = len(class_table["UC"])
		ui = len(class_table["UI"])
		up = len(class_table["UP"])
		ki = len(class_table["KI"])
		uk = uc + ui + up
		cc = ui + up
		return uc, ui, up, ki, uk, cc

	@staticmethod
	def measure_pattern(pattern: MutationPattern, line_or_mutation: bool, uk_or_cc: bool):
		"""
		:param pattern: of which lines (true) or mutations (False) being classified
		:param line_or_mutation: true to classify the lines and false to classify the mutations
		:param uk_or_cc: true when measuring based on unkilled counter or false on coincidental correctness
		:return: total, support, precision
		"""
		uc, ui, up, ki, uk, cc = MutationClassifier.counting_pattern(pattern, line_or_mutation)
		total = uc + ui + up + ki
		if uk_or_cc:
			support = uk
		else:
			support = cc
		precision = 0.0
		if total > 0:
			precision = support / total
		return total, support, precision

	@staticmethod
	def filter_samples(samples, uk_or_cc: bool):
		"""
		:param samples: the collection of samples (either Mutation or MutationLine) being filtered
		:param uk_or_cc: true to select not-killed samples or false for coincidental correct ones
		:return: the collection of samples selected from inputs as not-killed (uk_or_cc = True) or lines otherwise
		"""
		class_dict = MutationClassifier.classify_samples(samples)
		selected_samples = class_dict["UI"] | class_dict["UP"]
		if uk_or_cc:
			selected_samples = selected_samples | class_dict["UC"]
		return selected_samples


class MutationPatternSelector:
	"""
	It implements the algorithm to select patterns or the lines or mutations among them
	"""
	@staticmethod
	def get_samples_in_document(document: cmuta.MutationDocument, line_or_mutation: bool):
		"""
		:param document: it provides the original lines and mutations being selected
		:param line_or_mutation: true to select line or false to select mutations
		:return: the collection of mutation lines or mutations selected from input
		"""
		samples = set()
		for line in document.get_lines():
			line: cmuta.MutationLine
			if line_or_mutation:
				samples.add(line)
			else:
				samples.add(line.get_mutation())
		return samples

	@staticmethod
	def get_samples_in_patterns(patterns, line_or_mutation: bool):
		"""
		:param patterns: the collection of mutation patterns being generated
		:param line_or_mutation: true to select lines as samples or false to select mutations
		:return: the collection of execution lines or mutations from the patterns
		"""
		samples = set()
		for pattern in patterns:
			pattern: MutationPattern
			for line in pattern.get_lines():
				line: cmuta.MutationLine
				if line_or_mutation:
					samples.add(line)
				else:
					samples.add(line.get_mutation())
		return samples

	@staticmethod
	def map_samples_to_patterns(patterns, line_or_mutation: bool):
		"""
		:param patterns: the collection of mutation patterns being generated
		:param line_or_mutation: true to select lines as samples or false to select mutations
		:return: mapping from line|mutation to the patterns that match with it
		"""
		sample_pattern_dict = dict()
		for pattern in patterns:
			pattern: MutationPattern
			for line in pattern.get_lines():
				line: cmuta.MutationLine
				if line_or_mutation:
					sample = line
				else:
					sample = line.get_mutation()
				if not(sample in sample_pattern_dict):
					sample_pattern_dict[sample] = set()
				sample_pattern_dict[sample].add(pattern)
		return sample_pattern_dict

	@staticmethod
	def select_minimal_patterns(patterns):
		"""
		:param patterns: the collection of patterns from which the subsuming patterns are found
		:return: the collection of subsuming patterns selected from the input
		"""
		remain_patterns, removed_patterns, minimal_patterns = set(), set(), set()
		for pattern in patterns:
			pattern: MutationPattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			removed_patterns.clear()
			subsume_pattern = None
			for pattern in remain_patterns:
				pattern: MutationPattern
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
			if subsume_pattern is not None:
				minimal_patterns.add(subsume_pattern)
		return minimal_patterns

	@staticmethod
	def select_largest_pattern(patterns, line_or_mutation: bool, uk_or_cc: bool, buffer_size=5):
		"""
		:param patterns: the collection of mutation patterns from which the best is found
		:param line_or_mutation: true for selecting on lines or mutations as false
		:param uk_or_cc: true for selecting based on non-killed samples or coincidental correct as false
		:param buffer_size: size of buffer to preserve mutation patterns
		:return: None or the pattern that best matched in the patterns
		"""
		''' 1. collect all the patterns into the remaining set '''
		remain_patterns, buffer_patterns = set(), set()
		for pattern in patterns:
			pattern: MutationPattern
			remain_patterns.add(pattern)

		''' 2. select the patterns with top-precision into buffer '''
		while len(buffer_patterns) < buffer_size:
			best_pattern, best_precision = None, 0.0
			for pattern in remain_patterns:
				total, support, precision = MutationClassifier.measure_pattern(pattern, line_or_mutation, uk_or_cc)
				if precision >= best_precision:
					best_precision = precision
					best_pattern = pattern
			if best_pattern is None:
				break
			buffer_patterns.add(best_pattern)
			remain_patterns.remove(best_pattern)

		''' 3. select the pattern with the top-support in buffer '''
		best_pattern, best_support = None, 0
		for pattern in buffer_patterns:
			total, support, precision = MutationClassifier.measure_pattern(pattern, line_or_mutation, uk_or_cc)
			if support >= best_support:
				best_support = support
				best_pattern = pattern
		return best_pattern


class MutationPatternGenerator:
	"""
	It uses the frequent pattern mining algorithm to generate mutation patterns.
	"""
	def __init__(self, line_or_mutation: bool, uk_or_cc: bool, min_support: int, max_precision: float, max_length: int):
		"""
		:param line_or_mutation: true to classify the execution lines or false to classify the mutations in the patterns
		:param uk_or_cc: true to measure patterns based on the unkilled samples or false for coincidentally correct ones
		:param min_support: the minimal number of unkilled (uk_or_cc = True) or coincidental correct (uk_or_cc = False)
		:param max_precision: the maximal precision to stop the recursive traversal in the mining algorithm
		:param max_length: the maximal length allowed in generated patterns
		"""
		self.line_or_mutation = line_or_mutation
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_precision = max_precision
		self.max_length = max_length
		self.patterns = dict()		# from string to MutationPattern
		self.solution = dict()		# from MutationPattern to [total, support, precision]
		return

	def __root__(self, document: cmuta.MutationDocument, word: str):
		"""
		:param document: it provides all the mutations lines along with the mutations for matched with new pattern
		:param word: the only word in the pattern
		:return: root pattern
		"""
		root_pattern = MutationPattern()
		root_pattern = root_pattern.get_child(word.strip())
		if not(str(root_pattern) in self.patterns):
			self.patterns[str(root_pattern)] = root_pattern
			root_pattern.set_lines(document.get_lines())	# only update matching lines when inserting into patterns
		root_pattern = self.patterns[str(root_pattern)]
		root_pattern: MutationPattern
		return root_pattern

	def __child__(self, parent: MutationPattern, word: str):
		"""
		:param parent:
		:param word:
		:return: the unique child pattern generated from the parent with inserting one word or the parent itself
		"""
		child = parent.get_child(word.strip())
		if child != parent and not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_lines(parent.get_lines())
		child = self.patterns[str(child)]
		child: MutationPattern
		return child

	def __filter__(self):
		"""
		:return: the collection of patterns generated as good as the given specifiers
		"""
		good_patterns = set()
		for key, pattern in self.patterns.items():
			pattern: MutationPattern
			solution = self.solution[pattern]
			support = solution[1]
			precision = solution[2]
			if support >= self.min_support and precision >= self.max_precision:
				good_patterns.add(pattern)
		return good_patterns

	def __generate__(self, parent: MutationPattern, words):
		"""
		:param parent: the pattern from which children will be generated in mining algorithm
		:param words: words to generate its child pattern
		:return:
		"""
		if not(parent in self.solution):
			total, support, precision = MutationClassifier.measure_pattern(parent, self.line_or_mutation, self.uk_or_cc)
			solution = [total, support, precision]
			self.solution[parent] = solution
		solution = self.solution[parent]
		support = solution[1]
		precision = solution[2]
		if len(parent.get_words()) < self.max_length and support >= self.min_support and precision <= self.max_precision:
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__generate__(parent, words)	# recursively mining the children patterns
		return

	def __init_lines__(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: select the mutation lines as the right class as given
		"""
		return MutationClassifier.filter_samples(document.get_lines(), self.uk_or_cc)

	def generate(self, document: cmuta.MutationDocument):
		"""
		:param document: it provides the execution lines and mutations for generating patterns
		:return: good_patterns generated from the given parameters
		"""
		init_lines = self.__init_lines__(document)
		self.patterns.clear()
		self.solution.clear()
		for line in init_lines:
			line: cmuta.MutationLine
			words = line.get_words()
			for word in words:
				root_pattern = self.__root__(document, word)
				self.__generate__(root_pattern, words)
		return self.__filter__()


class MutationPatternWriter:
	"""
	It implements the writing algorithm on mutation patterns.
	"""
	@staticmethod
	def __proportion__(x: int, y: int):
		"""
		:param x:
		:param y:
		:return: x / y (as %)
		"""
		ratio = 0.0
		if y > 0:
			ratio = x / y
		return int(ratio * 10000) / 100.0

	@staticmethod
	def __pr_metrics__(doc_samples: set, pat_samples: set):
		"""
		:param doc_samples: the collection of samples selected from original document
		:param pat_samples: the collection of samples selected from given patterns
		:return: precision, recall, f1_score
		"""
		int_samples = doc_samples & pat_samples
		doc_number, pat_number, int_number = len(doc_samples), len(pat_samples), 0
		if len(int_samples) > 0:
			int_number = len(int_samples)
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if int_number > 0:
			precision = int_number / (pat_number + 0.0)
			recall = int_number / (doc_number + 0.0)
			f1_score = 2 * precision * recall / (precision + recall)
		return precision, recall, f1_score

	@staticmethod
	def __write_patterns_summary__(writer: TextIO, document: cmuta.MutationDocument, patterns):
		"""
		:param writer:
		:param document:
		:param patterns:
		:return:
		"""
		doc_lines = MutationPatternSelector.get_samples_in_document(document, True)
		doc_mutations = MutationPatternSelector.get_samples_in_document(document, False)
		pat_lines = MutationPatternSelector.get_samples_in_patterns(patterns, True)
		pat_mutations = MutationPatternSelector.get_samples_in_patterns(patterns, False)
		pat_number, lin_number, mut_number = len(patterns), len(pat_lines), len(pat_mutations)
		pat_lin_optimize_rate = MutationPatternWriter.__proportion__(pat_number, lin_number)
		pat_mut_optimize_rate = MutationPatternWriter.__proportion__(pat_number, mut_number)
		writer.write("Evaluation\n")
		writer.write("\tPatterns = {}\tLINE := {}({}%)\tMUTATION := {}({}%)\n".
					 format(pat_number, lin_number, pat_lin_optimize_rate, mut_number, pat_mut_optimize_rate))
		doc_uk_lines = MutationClassifier.filter_samples(doc_lines, True)
		doc_cc_lines = MutationClassifier.filter_samples(doc_lines, False)
		doc_uk_mutations = MutationClassifier.filter_samples(doc_mutations, True)
		doc_cc_mutations = MutationClassifier.filter_samples(doc_mutations, False)
		writer.write("\tTitle\tPrecision(%)\tRecall(%)\tF1_Score\n")
		precision, recall, f1_score = MutationPatternWriter.__pr_metrics__(pat_lines, doc_uk_lines)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("UK-LINE", precision, recall, f1_score))
		precision, recall, f1_score = MutationPatternWriter.__pr_metrics__(pat_lines, doc_cc_lines)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("CC-LINE", precision, recall, f1_score))
		precision, recall, f1_score = MutationPatternWriter.__pr_metrics__(pat_mutations, doc_uk_mutations)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("UK-MUTA", precision, recall, f1_score))
		precision, recall, f1_score = MutationPatternWriter.__pr_metrics__(pat_mutations, doc_cc_mutations)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("CC-MUTA", precision, recall, f1_score))
		writer.write("\n")
		return

	@staticmethod
	def __write_pattern_counting__(writer: TextIO, pattern: MutationPattern):
		"""
		:param writer: output stream to write the summary of pattern
		:param pattern: to print its evaluation summary
		:return: words, lines, mutations
				UC, UI, UP, KI, UK(%), CC(%)
		"""
		head_template = "\tSummary\tLength\t{}\tLines\t{}\tMutations\t{}\n"
		writer.write(head_template.format(len(pattern.get_words()), len(pattern.get_lines()), len(pattern.get_mutations())))
		writer.write("\tMetrics\tUC\tUI\tUP\tKI\tUK(%)\tCC(%)\n")
		eval_template = "\t{}\t{}\t{}\t{}\t{}\t{}%\t{}%\n"
		uc, ui, up, ki, uk, cc = MutationClassifier.counting_pattern(pattern, True)
		total = uc + ui + up + ki
		writer.write(eval_template.format("Line", uc, ui, up, ki, MutationPatternWriter.__proportion__(uk, total),
										  MutationPatternWriter.__proportion__(cc, total)))
		uc, ui, up, ki, uk, cc = MutationClassifier.counting_pattern(pattern, False)
		total = uc + ui + up + ki
		writer.write(eval_template.format("Mutation", uc, ui, up, ki, MutationPatternWriter.__proportion__(uk, total),
										  MutationPatternWriter.__proportion__(cc, total)))
		writer.write("\n")
		return

	@staticmethod
	def __write_pattern_annotations__(writer: TextIO, pattern: MutationPattern, project: cmuta.CProject):
		"""
		:param writer: the output stream to write the annotations in the pattern
		:param pattern:
		:return:
		"""
		annotations = pattern.get_annotations(project)
		aid = 0
		writer.write("\tIndex\tType\tExecution\tStatement\tLocation\tParameter\n")
		for annotation in annotations:
			stmt_code = annotation.get_execution().get_statement().get_code()
			loct_code = annotation.get_location().get_code()
			parameter = str(annotation.get_parameter())
			writer.write(
				"\twords[{}]\t{}\t{}\t{}\t{}\t{}\n".format(aid, annotation.annotation_type, annotation.execution,
														   stmt_code, loct_code, parameter))
			aid += 1
		writer.write("\n")
		return

	@staticmethod
	def __write_pattern_mutations__(writer: TextIO, pattern: MutationPattern):
		"""
		:param writer:
		:param pattern:
		:return:
		"""
		writer.write("\tID\tRES\tCLASS\tOPRT\tLINE\tCODE\tPARAM\n")
		template = "\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n"
		for mutation in pattern.get_mutations():
			mid = mutation.get_muta_id()
			res = MutationClassifier.classify_sample(mutation)
			mclass = mutation.get_muta_class()
			moprt = mutation.get_muta_operator()
			location = mutation.get_location()
			line = location.get_line() + 1
			code = location.get_code(True)
			parameter = str(mutation.get_parameter())
			writer.write(template.format(mid, res, mclass, moprt, line, code, parameter))
		return

	@staticmethod
	def write_patterns(document: cmuta.MutationDocument, patterns, output_file_path: str):
		"""
		:param document:
		:param patterns:
		:param output_file_path:
		:return:
		"""
		with open(output_file_path, 'w') as writer:
			MutationPatternWriter.__write_patterns_summary__(writer, document, patterns)
			pid = 0
			for pattern in patterns:
				writer.write("#BEG\t" + str(pid) + "\n")
				MutationPatternWriter.__write_pattern_counting__(writer, pattern)
				MutationPatternWriter.__write_pattern_annotations__(writer, pattern, document.get_project())
				MutationPatternWriter.__write_pattern_mutations__(writer, pattern)
				writer.write("#END\t" + str(pid) + "\n")
				writer.write("\n")
				pid += 1
		return

	@staticmethod
	def __write_mutation_and_pattern__(writer: TextIO, mutation: cmuta.Mutation, pattern: MutationPattern,
									   project: cmuta.CProject):
		"""
		:param writer:
		:param mutation:
		:param pattern:
		:param project
		:return:
		"""
		template = "Muta\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n"
		mid = mutation.get_muta_id()
		res = MutationClassifier.classify_sample(mutation)
		mclass = mutation.get_muta_class()
		moprt = mutation.get_muta_operator()
		location = mutation.get_location()
		line = location.get_line() + 1
		code = location.get_code(True)
		parameter = str(mutation.get_parameter())
		writer.write(template.format(mid, res, mclass, moprt, line, code, parameter))
		MutationPatternWriter.__write_pattern_annotations__(writer, pattern, project)
		return

	@staticmethod
	def write_best_patterns(document: cmuta.MutationDocument, patterns, output_file_path: str,
							line_or_mutation: bool, uk_or_cc: bool):
		"""
		:param document:
		:param patterns:
		:param output_file_path:
		:param line_or_mutation:
		:param uk_or_cc:
		:return:
		"""
		with open(output_file_path, 'w') as writer:
			mutation_patterns_dict = MutationPatternSelector.map_samples_to_patterns(patterns, False)
			for mutation, patterns in mutation_patterns_dict.items():
				best_pattern = MutationPatternSelector.select_largest_pattern(patterns, line_or_mutation, uk_or_cc)
				MutationPatternWriter.__write_mutation_and_pattern__(writer, mutation, best_pattern, document.get_project())
				writer.write("\n")
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/patterns"
	line_or_mutation, uk_or_cc, min_support, max_precision, max_length = True, True, 2, 0.80, 1
	generator = MutationPatternGenerator(line_or_mutation, uk_or_cc, min_support, max_precision, max_length)
	for file_name in os.listdir(root_path):
		print("Testing on program", file_name)
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		print("\tLoad", len(c_project.muta_space.mutations), "mutations and", len(c_project.test_space.test_cases),
			  "tests for", file_name)
		sym_file_path = os.path.join(directory, file_name + ".sym")
		test_id = -1
		docs = c_project.load_document(sym_file_path, test_id)
		print("\tGet", len(docs.get_lines()), "lines of annotations with", len(docs.get_corpus()), "words.")
		good_patterns = generator.generate(docs)
		minimal_patterns = MutationPatternSelector.select_minimal_patterns(good_patterns)
		MutationPatternWriter.write_patterns(docs, good_patterns, os.path.join(post_path, file_name + ".gpt"))
		print("\tWrite", len(good_patterns), "selected set of patterns to output file.")
		MutationPatternWriter.write_patterns(docs, minimal_patterns, os.path.join(post_path, file_name + ".mgt"))
		print("\tWrite", len(minimal_patterns), "selected set of patterns to output file.")
		MutationPatternWriter.write_best_patterns(docs, minimal_patterns, os.path.join(post_path, file_name + ".bpt"), True, True)
		print("\tWrite", len(minimal_patterns), "selected best set of patterns to output file.")
		print()
	print("Testing end for all...")

