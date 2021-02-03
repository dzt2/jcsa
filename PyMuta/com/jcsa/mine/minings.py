import os
from sklearn import tree
from sklearn import metrics
from scipy import sparse
import com.jcsa.libs.muta as jcmuta
import com.jcsa.mine.pattern as jcmpat
import pydotplus


# frequent pattern mining

class RIPFPMiner:
	"""
	Frequent pattern mining on RIP patterns
	"""

	def __init__(self, exe_or_mut: bool, uk_or_cc: bool, max_length: int, min_support: int,
				 min_confidence: float, max_confidence: float):
		"""
		:param exe_or_mut:
		:param uk_or_cc:
		:param max_length:
		:param min_support:
		:param min_confidence:
		:param max_confidence:
		"""
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.factory = None
		return

	@staticmethod
	def __word__(pattern: jcmpat.RIPPattern):
		words = set()
		for execution in pattern.get_executions():
			execution: jcmuta.RIPExecution
			for word in execution.get_words():
				words.add(word)
		return words

	def __mine__(self, parent: jcmpat.RIPPattern, words):
		self.factory: jcmpat.RIPPatternFactory
		total, support, confidence = self.factory.get_estimate(parent)
		if len(parent) < self.max_length and support >= self.min_support and confidence <= self.max_confidence:
			for word in words:
				child = self.factory.get_pattern(parent, word, True)
				if child != parent:
					self.__mine__(child, words)
		return

	def __outs__(self):
		self.factory: jcmpat.RIPPatternFactory
		good_patterns = set()
		for pattern in self.factory.estimate.keys():
			pattern: jcmpat.RIPPattern
			total, support, confidence = self.factory.get_estimate(pattern)
			if support >= self.min_support and confidence >= self.min_confidence:
				good_patterns.add(pattern)
		return jcmpat.RIPPatternSpace(self.factory.get_document(), self.factory.get_classifier(), good_patterns)

	def mine(self, document: jcmuta.RIPDocument):
		"""
		:param document:
		:return:
		"""
		self.factory = jcmpat.RIPPatternFactory(document, jcmpat.RIPClassifier(), self.exe_or_mut, self.uk_or_cc)
		init_executions = self.factory.classifier.select(document.get_executions(), self.uk_or_cc)
		for init_execution in init_executions:
			init_execution: jcmuta.RIPExecution
			words = init_execution.get_words()
			for word in words:
				root_pattern = self.factory.get_pattern(None, word, True)
				self.__mine__(root_pattern, words)
		space = self.__outs__()
		self.factory = None
		return space


def frequent_mining(document: jcmuta.RIPDocument, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
					min_confidence: float, max_confidence: float, max_length: int, output_directory: str):
	"""
	:param document: it provides lines and mutations in the program
	:param exe_or_mut: true to take line as sample or false to take mutant as sample
	:param uk_or_cc: true to estimate on non-killed samples or false on coincidental correctness samples
	:param min_support: minimal number of samples supporting the patterns
	:param min_confidence: minimal confidence required to select good patterns
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

		miner = RIPFPMiner(exe_or_mut, uk_or_cc, max_length, min_support, min_confidence, max_confidence)
		patterns = miner.mine(document)
		subsume_patterns = patterns.get_subsuming_patterns(None)
		print("\t(2) Generate", len(patterns.get_patterns()), "patterns with", len(subsume_patterns),
			  "of subsuming patterns set from.")

		writer = jcmpat.RIPPatternWriter()
		name = document.get_project().program.name
		writer.write_patterns(subsume_patterns, os.path.join(output_directory, name + ".mpt"))
		writer.write_matching(patterns, os.path.join(output_directory, name + ".bpt"), exe_or_mut, uk_or_cc)
		writer.write_evaluate(patterns, os.path.join(output_directory, name + ".sum"))
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def frequent_mining_testing(directory: str, file_name: str, t_value, f_value, n_value,
							exe_or_mut: bool, uk_or_cc: bool, stat_directory: str, dyna_directory):
	c_project = jcmuta.CProject(directory, file_name)

	docs = c_project.load_static_document(directory, t_value, f_value, n_value)
	frequent_mining(docs, exe_or_mut, uk_or_cc, 2, 0.70, 0.90, 1, os.path.join(stat_directory))

	if not (dyna_directory is None):
		docs = c_project.load_dynamic_document(directory, t_value, f_value, n_value)
		frequent_mining(docs, exe_or_mut, uk_or_cc, 20, 0.75, 0.90, 1, os.path.join(dyna_directory))
	return


# decision tree classifier

class RIPDTMiner:
	def __init__(self, exe_or_mut: bool, uk_or_cc: bool, max_length: int, min_support: int,
				 min_confidence: float, max_confidence: float):
		"""
		:param exe_or_mut:
		:param uk_or_cc:
		:param max_length:
		:param min_support:
		:param min_confidence:
		:param max_confidence:
		"""
		self.exe_or_mut = exe_or_mut
		self.uk_or_cc = uk_or_cc
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.factory = None
		return

	@staticmethod
	def __norm__(text: str):
		new_text = ""
		for k in range(0, len(text)):
			char = text[k]
			if char in ['{', '}', '\"']:
				char = ' '
			new_text += char
		return new_text

	def __word__(self, word: str):
		"""
		:param word:
		:return:
		"""
		self.factory: jcmpat.RIPPatternFactory
		document = self.factory.get_document()
		condition = document.decode(word.strip())
		category = condition.get_category()
		operator = condition.get_operator()
		execution = condition.get_execution()
		location = "\"" + condition.location.get_cir_code() + "\""
		parameter = "null"
		if condition.parameter is not None:
			parameter = condition.parameter.get_code()
		text = "[{}, {}, {}, {}, {}]".format(category, operator, execution, location, parameter)
		return RIPDTMiner.__norm__(text)

	def __encoding__(self):
		"""
		:return: X, Y, W, WN
		"""
		Y, W, WN, D = list(), list(), list(), dict()
		self.factory: jcmpat.RIPPatternFactory
		document = self.factory.get_document()
		classifier = jcmpat.RIPClassifier()
		for execution in document.get_executions():
			execution: jcmuta.RIPExecution
			if self.exe_or_mut:
				sample = execution
			else:
				sample = execution.get_mutant()
			total, support, confidence = classifier.estimate([sample], uk_or_cc=self.uk_or_cc)
			if support > 0:
				Y.append(1)
			else:
				Y.append(0)
			for word in execution.get_words():
				if not (word in D):
					D[word] = len(W)
					W.append(word)
					WN.append(self.__word__(word))
		xlist, ylist, dlist = list(), list(), list()
		line = 0
		for execution in document.get_executions():
			for word in execution.get_words():
				column = D[word]
				xlist.append(line)
				ylist.append(column)
				dlist.append(1)
			line += 1
		X = sparse.coo_matrix((dlist, (xlist, ylist)), shape=(len(document.get_executions()), len(W)))
		return X, Y, W, WN

	def __train__(self, X, Y, W, file_path):
		"""
		:return:
		"""
		classifier = tree.DecisionTreeClassifier(min_samples_leaf=self.min_support)
		classifier.fit(X, Y)
		YP = classifier.predict(X)
		print(metrics.classification_report(Y, YP))
		if not(file_path is None):
			dot_data = tree.export_graphviz(classifier, out_file=None, feature_names=W,
											class_names=["Killable", "Equivalent"])
			graph = pydotplus.graph_from_dot_data(dot_data)
			graph.write_pdf(file_path)
		return classifier

	def __get_decision_paths__(self, classifier: tree.DecisionTreeClassifier, X, Y):
		"""
		:param classifier:
		:param X:
		:param Y:
		:return: exe_id, node_path, leaf_id
		"""
		paths = dict()
		self.factory: jcmpat.RIPPatternFactory
		node_indicator = classifier.decision_path(X)
		leave_id = classifier.apply(X)
		for sample_id in range(0, len(self.factory.document.get_executions())):
			if Y[sample_id] == 1:
				leaf_node_id = leave_id[sample_id]
				node_index = node_indicator.indices[
							 node_indicator.indptr[sample_id]: node_indicator.indptr[sample_id + 1]]
				paths[sample_id] = (node_index, leaf_node_id)
		return paths

	def __out__(self, classifier: tree.DecisionTreeClassifier, X, Y, W):
		exec_paths = self.__get_decision_paths__(classifier, X, Y)
		word_paths = dict()		# [(word, bool)]
		features = classifier.tree_.feature
		self.factory: jcmpat.RIPPatternFactory
		executions = self.factory.get_document().get_executions()
		thresholds = classifier.tree_.threshold
		for exec_id, path_leaf in exec_paths.items():
			execution = executions[exec_id]
			execution: jcmuta.RIPExecution
			node_path = path_leaf[0]
			leaf_id = path_leaf[1]
			path = list()
			for node_id in node_path:
				if node_id != leaf_id:
					word = W[features[node_id]]
					word: str
					if X[exec_id, features[node_id]] <= thresholds[node_id]:
						select_or_remove = False
					else:
						select_or_remove = True
					path.append((word, select_or_remove))
			word_paths[execution] = path
		exec_patterns = dict()
		for execution, path in word_paths.items():
			total, support, confidence = self.factory.classifier.estimate([execution], self.uk_or_cc)
			if support > 0:
				pattern = self.factory.get_pattern(None, "", True)
				for item in path:
					word = item[0]
					select_or_remove = item[1]
					pattern = self.factory.get_pattern(pattern, word, select_or_remove)
				exec_patterns[execution] = pattern
		patterns = set()
		for execution, pattern in exec_patterns.items():
			patterns.add(pattern)
		good_patterns = set()
		for pattern in patterns:
			pattern.set_samples(None)
			total, support, confidence = pattern.estimate(self.exe_or_mut, self.uk_or_cc)
			if support >= self.min_support and confidence >= self.min_confidence:
				good_patterns.add(pattern)
		return good_patterns

	def mine(self, document: jcmuta.RIPDocument, tree_path: str):
		"""
		:param tree_path:
		:param document:
		:return:
		"""
		self.factory = jcmpat.RIPPatternFactory(document, jcmpat.RIPClassifier(), self.exe_or_mut, self.uk_or_cc)
		X, Y, W, WN = self.__encoding__()
		classifier = self.__train__(X, Y, WN, tree_path)
		return jcmpat.RIPPatternSpace(self.factory.document,
									  self.factory.classifier, self.__out__(classifier, X.toarray(), Y, W))


def decision_tree_mining(document: jcmuta.RIPDocument, exe_or_mut: bool, uk_or_cc: bool, min_support: int,
						 min_confidence: float, max_confidence: float, max_length: int, output_directory: str):
	if not(os.path.exists(output_directory)):
		os.mkdir(output_directory)
	if len(document.get_executions()) > 0:
		print("Testing on", document.get_project().program.name)
		print("\t(1) Load", len(document.get_executions()), "lines of", len(document.get_mutants()),
			  "mutants with", len(document.get_corpus()), "words.")
		name = document.project.program.name
		miner = RIPDTMiner(exe_or_mut, uk_or_cc, max_length, min_support, min_confidence, max_confidence)
		space = miner.mine(document, os.path.join(output_directory, name + ".pdf"))
		print("\t(2) Obtain Decision Tree Classification Model finally.")

		writer = jcmpat.RIPPatternWriter()
		writer.write_evaluate(space, os.path.join(output_directory, name + ".sum"))
		writer.write_matching(space, os.path.join(output_directory, name + ".bpt"), exe_or_mut, uk_or_cc)
		writer.write_patterns(space.get_subsuming_patterns(uk_or_cc), os.path.join(output_directory, name + ".mpt"))
		print("\t(3) Output the pattern, test results to output file finally...")
		print()
	return


def decision_tree_testing(directory: str, file_name: str, t_value, f_value, n_value,
						  exe_or_mut: bool, uk_or_cc: bool, stat_directory: str, dyna_directory):
	"""
	:param directory:
	:param file_name:
	:param t_value:
	:param f_value:
	:param n_value:
	:param exe_or_mut:
	:param uk_or_cc:
	:param stat_directory:
	:param dyna_directory:
	:return:
	"""
	c_project = jcmuta.CProject(directory, file_name)
	docs = c_project.load_static_document(directory, t_value, f_value, n_value)
	decision_tree_mining(docs, exe_or_mut, uk_or_cc, 2, 0.70, 0.90, 1, os.path.join(stat_directory))
	if not (dyna_directory is None):
		docs = c_project.load_dynamic_document(directory, t_value, f_value, n_value)
		decision_tree_mining(docs, exe_or_mut, uk_or_cc, 20, 0.75, 0.90, 1, os.path.join(dyna_directory))
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	stat_path = "/home/dzt2/Development/Data/patterns/stat"
	dyna_path = None
	# dyna_path = "/home/dzt2/Development/Data/patterns/dyna"
	for filename in os.listdir(prev_path):
		direct = os.path.join(prev_path, filename)
		decision_tree_testing(directory=direct, file_name=filename, t_value=True, f_value=False, n_value=True,
							  exe_or_mut=True, uk_or_cc=True, stat_directory=stat_path, dyna_directory=dyna_path)
#		frequent_mining_testing(directory=direct, file_name=filename, t_value=True, f_value=False, n_value=True,
#								exe_or_mut=True, uk_or_cc=True, stat_directory=stat_path, dyna_directory=dyna_path)

