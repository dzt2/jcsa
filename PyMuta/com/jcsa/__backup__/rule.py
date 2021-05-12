"""This file implements the data model of killable prediction rules to support mutation analysis"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


# Encoding-Decoding: SymCondition --> str --> int


class SymCorpus:
	"""
	It manages the encoding and decoding for symbolic condition to unique string and its corresponding integer feature.
	"""

	def __init__(self, document: jctest.CDocument):
		"""
		:param document: it is used to decode the string word to symbolic condition
		"""
		self.document = document
		self.words = list()
		self.index = dict()
		return

	def get_document(self):
		"""
		:return: the document is used to decode string word to symbolic condition
		"""
		return self.document

	def get_words(self):
		"""
		:return: the collection of string words encoding symbolic conditions in the corpus
		"""
		return self.words

	def __str2int__(self, word: str):
		"""
		:param word: the string word to be encoded as integer
		:return: the unique integer ID of the word or -1 if the word is empty
		"""
		word = word.strip()
		if len(word) > 0:
			if not (word in self.index):
				self.index[word] = len(self.words)
				self.words.append(word)
			code = self.index[word]
			code: int
			return code
		else:
			return -1

	def __int2str__(self, code: int):
		"""
		:param code: the unique integer ID of the string word being decoded
		:return: the string word w.r.t. the integer ID or none if not exist
		"""
		if (code >= 0) and (code < len(self.words)):
			word = self.words[code]
			word: str
			return word
		else:
			return None

	def encode(self, condition: jctest.SymCondition):
		"""
		:param condition: the symbolic condition being encoded
		:return: the integer ID of the unique string word for encoding the symbolic condition
		"""
		return self.__str2int__(str(condition))

	def decode(self, feature: int):
		"""
		:param feature: the integer feature encoding a symbolic condition used in testing
		:return: symbolic condition or None if the integer feature is not encoded in corpus
		"""
		word = self.__int2str__(feature)
		if word is None:
			return None
		else:
			return self.document.get_conditions_lib().get_condition(word)

	def save_corpus(self, file_path: str):
		"""
		:param file_path: the file to preserve the word-int mapping
		:return: preserve the word-int mapping for encoding features
		"""
		with open(file_path, 'w') as writer:
			for word in self.words:
				writer.write(word + "\n")
		return

	def load_corpus(self, file_path: str):
		"""
		:param file_path: the file from which the word-int mapping is loaded
		:return: load the word-int corpus from the specified file
		"""
		self.words.clear()
		self.index.clear()
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__str2int__(line.strip())
		return


# Classification: status --> bool; mutant --> bool; SymExecution --> bool, Set[SymCondition]


class SymClassification:
	"""
	It is used to filter symbolic condition and classify mutant based on parameters.
	"""

	# feature selection	{status --> bool}

	@staticmethod
	def is_accepted_status(status: jctest.SymInstanceStatus):
		"""
		:param status:
		:return: only status that is accepted will not be filtered
		"""
		return status.is_accepted()

	@staticmethod
	def is_available_status(status: jctest.SymInstanceStatus):
		"""
		:param status:
		:return: only status that can be accepted will not be filtered
		"""
		return status.is_available()

	@staticmethod
	def is_executed_status(status: jctest.SymInstanceStatus):
		"""
		:param status:
		:return: any possible status that has been evaluated will be passed
		"""
		return status.is_executed()

	@staticmethod
	def pass_sym_conditions(instance_nodes, is_good_status):
		"""
		:param instance_nodes: 	the collection of SymInstanceNode
		:param is_good_status: 	(1)	is_accepted_status: select condition with accepted status
								(2) is_available_status: select condition with accept-able status
								(3) is_executed_status: select condition with evaluated status
								(4) None: select every condition of input nodes without filtering
		:return: the collection of symbolic conditions filtered from the input set
		"""
		conditions = list()
		for instance_node in instance_nodes:
			instance_node: jctest.SymInstanceNode
			if is_good_status is None:
				is_passed = True
			else:
				is_passed = is_good_status(instance_node.get_status())
			if is_passed:
				for condition in instance_node.get_conditions():
					conditions.append(condition)
		return conditions

	# sample labeling	{mutant --> bool}

	@staticmethod
	def classify_mutant_by(mutant: jcmuta.Mutant, used_tests):
		"""
		:param mutant: the mutant to be classified as killed or alive
		:param used_tests: the set of test cases used for killing the mutant
		:return: True -- killed or False -- alive
		"""
		return mutant.get_result().is_killed_in(used_tests)

	# parse the symbolic execution to pre-standard feature in form of {result, conditions}

	@staticmethod
	def translate(sym_execution: jctest.SymExecution, is_good_status, used_tests):
		"""
		:param sym_execution: 	the symbolic execution with conditions being translated
		:param is_good_status: 	(1)	is_accepted_status: select condition with accepted status
								(2) is_available_status: select condition with accept-able status
								(3) is_executed_status: select condition with evaluated status
								(4) None: select every condition of input nodes without filtering
		:param used_tests: 		the set of test cases used for killing the mutant
		:return: 				result, conditions (as initial features being encoded)
		"""
		return SymClassification.classify_mutant_by(sym_execution.get_mutant(), used_tests), \
			   SymClassification.pass_sym_conditions(sym_execution.get_condition_nodes(), is_good_status)


# feature line model


class SymFeatureLine:
	"""
	Each line represents the features of a symbolic execution using {exec_id, result, condition_id_list}
	"""

	def __init__(self, eid: int, res: bool, features):
		"""
		:param eid: the integer ID of symbolic execution to be represented by this line
		:param res: the boolean result of testing
		:param features: the set of integer ID of symbolic conditions incorporated
		"""
		self.eid = eid
		self.res = res
		self.feature_vector = list()
		for feature in features:
			feature: int
			self.feature_vector.append(feature)
		self.feature_vector.sort()
		return

	def get_exec_id(self):
		"""
		:return:  the integer ID of symbolic execution to be represented by this line
		"""
		return self.eid

	def get_result(self):
		"""
		:return: the boolean result of testing
		"""
		return self.res

	def get_feature_vector(self):
		"""
		:return: the sequence of integer ID of string word encoding the symbolic conditions used in line
		"""
		return self.feature_vector

	def __len__(self):
		"""
		:return: the length of line is the number of features used in
		"""
		return len(self.feature_vector)

	def __str__(self):
		return SymFeatureLines.line2str(self)

	def get_conditions(self, corpus: SymCorpus):
		"""
		:param corpus: it is used to decode the integer features into string words
		:return: the string words encoding the feature vector in the line
		"""
		conditions = list()
		for feature in self.feature_vector:
			condition = corpus.decode(feature)
			if not (condition is None):
				conditions.append(condition)
		return conditions


# feature line interfaces: parse, save, load


class SymFeatureLines:
	"""
	It is used to encode and decode symbolic feature lines.
	"""

	@staticmethod
	def line2str(line: SymFeatureLine):
		"""
		:param line: symbolic feature line to the unique string for printing
		:return: eid res feature+
		"""
		line: SymFeatureLine
		text = "{} {}".format(line.eid, line.res)
		for feature in line.feature_vector:
			text += " {}".format(feature)
		return text

	@staticmethod
	def str2line(line: str):
		"""
		:param line: eid res feature+
		:return: None if the line is empty
		"""
		line = line.strip()
		if len(line) > 0:
			items = line.split(' ')
			eid = int(items[0].strip())
			if items[1].strip() == "True":
				res = True
			else:
				res = False
			features = list()
			for k in range(2, len(items)):
				feature = int(items[k].strip())
				features.append(feature)
			return SymFeatureLine(eid, res, features)
		else:
			return None

	@staticmethod
	def save_lines(file_path: str, lines):
		"""
		:param file_path: the file to preserve the feature lines
		:param lines: the collection of feature lines being printed
		:return:
		"""
		with open(file_path, 'w') as writer:
			for line in lines:
				line: SymFeatureLine
				writer.write(SymFeatureLines.line2str(line).strip() + "\n")
		return

	@staticmethod
	def load_lines(file_path: str):
		"""
		:param file_path: the file to preserve the feature lines
		:return: the collection of symbolic feature lines being loaded
		"""
		lines = list()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					feature_line = SymFeatureLines.str2line(line)
					feature_line: SymFeatureLine
					lines.append(feature_line)
		return lines

	@staticmethod
	def exe2line(corpus: SymCorpus, sym_execution: jctest.SymExecution, is_good_status, used_tests):
		"""
		:param corpus:			the corpus is used to encode symbolic condition to integer ID
		:param sym_execution: 	the symbolic execution with conditions being translated
		:param is_good_status: 	(1)	is_accepted_status: select condition with accepted status
								(2) is_available_status: select condition with accept-able status
								(3) is_executed_status: select condition with evaluated status
								(4) None: select every condition of input nodes without filtering
		:param used_tests: 		the set of test cases used for killing the mutant
		:return:				SymFeatureLine being encoded and generated from corpus provided
		"""
		eid = sym_execution.get_id()
		res, conditions = SymClassification.translate(sym_execution, is_good_status, used_tests)
		features = set()
		for condition in conditions:
			features.add(corpus.encode(condition))
		return SymFeatureLine(eid, res, features)

	@staticmethod
	def line2exe(corpus: SymCorpus, line: SymFeatureLine):
		"""
		:param corpus: it is used to decode the feature (int) to symbolic condition
		:param line: feature line to be decoded
		:return: sym_execution, result, conditions
		"""
		sym_execution = corpus.get_document().get_execution(line.get_exec_id())
		result = line.get_result()
		conditions = line.get_conditions(corpus)
		return sym_execution, result, conditions


# feature-based rule used to predict whether a mutant is killed or not


class SymFeatureRule:
	"""
	The prediction rule based on features of symbolic conditions to predict the kill-ability of mutant(s).
	"""

	def __init__(self, corpus: SymCorpus):
		"""
		:param corpus: it is used to decode integer premises to condition instances.
		"""










