"""This file develops the transformation of tokens and objects in testing"""


import enum
import os
import random


class CTokenType(enum.Enum):
	"""
	It defines the type of token defined in feature database
	"""
	Nullptr = 0
	Boolean = 1
	Character = 2
	Integer = 3
	Float = 4
	Complex = 5
	Keyword = 6
	Punctuate = 7
	Operator = 8
	AstNode = 9
	CirNode = 10
	MutantID = 11
	Execution = 12
	Instance = 13
	String = 14
	DataType = 15
	SymNode = 16
	TestID = 17


class CToken:
	"""
	It defines the basic data token defined in feature database.
	"""

	def __init__(self, token_type: CTokenType, token_value):
		"""
		:param token_type:
		:param token_value:
				Nullptr			-->		None
				Boolean			-->		Bool
				Character		-->		int
				Integer			-->		int
				Float			--> 	float
				Complex			-->		complex
				Keyword			-->		string
				Punctuate		-->		string
				Operator		-->		string
				AstNode			-->		int
				CirNode			-->		int
				MutantID		-->		int
				Execution		-->		(string, int)
				InstanceNode	-->		(context, string, int)
				DependNode		-->		(context, string, int)
				SymNode			-->		(type, int)
				DataType		-->		string
		"""
		self.token_type = token_type
		self.token_value = token_value
		return

	def get_token_type(self):
		return self.token_type

	def get_token_value(self):
		"""
		:return:
				Nullptr			-->		None
				Boolean			-->		Bool
				Character		-->		int
				Integer			-->		int
				Float			--> 	float
				Complex			-->		complex
				Keyword			-->		string
				Punctuate		-->		string
				Operator		-->		string
				AstNode			-->		int
				CirNode			-->		int
				MutantID		-->		int
				Execution		-->		(string, int)
				InstanceNode	-->		(context, string, int)
				DependNode		-->		(context, string, int)
				SymNode			-->		(type, int)
				DataType		-->		string
		"""
		return self.token_value

	def __str__(self):
		return str(self.token_value)

	@staticmethod
	def __denormalize__(content: str):
		"""
		:param content:
		:return: translate the normalized text into original type
					\\s --> space
					\\a --> @
					\\p --> $
		"""
		content = content.replace("\\s", ' ')
		content = content.replace("\\a", '@')
		content = content.replace("\\p", '$')
		return content

	@staticmethod
	def parse(text: str):
		"""
		:param text:
		:return:
				n@null	b@bool	c@char	i@numb	f@real	i@x@y	s@text
				key@kw	opr@op	pun@pu	typ@ty	ast@id	cir@id	mut@int	tst@int
				exe@fun@int		ins@txt@int@int	sym@txt@int
		"""
		items = text.strip().split('@')
		if len(items) > 0:
			title = items[0].strip()
			if title == 'n':																			## n@null
				return CToken(CTokenType(CTokenType.Nullptr), None)
			elif title == 'b':																			## b@bool
				return CToken(CTokenType(CTokenType.Boolean), bool(items[1].strip() == "true"))
			elif title == 'c':																			## c@char
				return CToken(CTokenType(CTokenType.Character), int(items[1].strip()))
			elif title == 'i':																			## i@int
				return CToken(CTokenType(CTokenType.Integer), int(items[1].strip()))
			elif title == 'f':																			## f@real
				return CToken(CTokenType(CTokenType.Float), float(items[1].strip()))
			elif title == 'x':																			## x@real@real
				value = (float(items[1].strip()), float(items[2].strip()))
				return CToken(CTokenType(CTokenType.Complex), value)
			elif title == 's':																			##	s@txt
				return CToken(CTokenType(CTokenType.String), CToken.__denormalize__(items[1].strip()))
			elif title == 'key':																		##	key@txt
				return CToken(CTokenType(CTokenType.Keyword), CToken.__denormalize__(items[1].strip()))
			elif title == "opr":																		##	opr@txt
				return CToken(CTokenType(CTokenType.Operator), CToken.__denormalize__(items[1].strip()))
			elif title == "pun":																		##	pun@txt
				return CToken(CTokenType(CTokenType.Punctuate), CToken.__denormalize__(items[1].strip()))
			elif title == "typ":																		##	typ@txt
				return CToken(CTokenType(CTokenType.DataType), CToken.__denormalize__(items[1].strip()))
			elif title == "ast":																		## 	ast@int
				return CToken(CTokenType(CTokenType.AstNode), int(items[1].strip()))
			elif title == "cir":																		## 	cir@int
				return CToken(CTokenType(CTokenType.CirNode), int(items[1].strip()))
			elif title == "mut":																		## 	mut@int
				return CToken(CTokenType(CTokenType.MutantID), int(items[1].strip()))
			elif title == "tst":																		## 	tst@int
				return CToken(CTokenType(CTokenType.TestID), int(items[1].strip()))
			elif title == "exe":																		##	exe@str@int
				value = (items[1].strip(), int(items[2].strip()))
				return CToken(CTokenType(CTokenType.Execution), value)
			elif title == "ins":																		## 	ins@str@int@int
				value = (items[1].strip(), int(items[2].strip()), int(items[3].strip()))
				return CToken(CTokenType(CTokenType.Instance), value)
			elif title == "sym":																		##	sym@txt@int
				value = (items[1].strip(), int(items[2].strip()))
				return CToken(CTokenType(CTokenType.SymNode), value)
			else:																						## invalid
				return None
		return None																						## empty-string


class SymbolNode:
	"""
	class ID source{Token as AstNode, CirNode, Execution, Constant or Nullptr} data_type content code parent children
	"""
	def __init__(self, class_name: str, class_id: int, source: CToken, data_type: CToken, content: CToken, code: str):
		"""
		:param class_name: class of symbolic node
		:param class_id: unique ID of symbolic node
		:param source: [AstNode, CirNode, Execution, Constant or None]
		:param data_type: code of data type
		:param content: Token as String, Operator, Constant or None
		:param code: simplified code to describe the symbolic node
		"""
		self.class_name = class_name
		self.class_id = class_id
		self.source = source
		self.data_type = data_type
		self.content = content
		self.code = code
		self.parent = None
		self.children = list()
		return

	def get_class_name(self):
		return self.class_name

	def get_class_id(self):
		return self.class_id

	def get_source(self):
		return self.source

	def get_data_type(self):
		return self.data_type

	def get_content(self):
		return self.content

	def get_code(self):
		return self.code

	def get_parent(self):
		return self.parent

	def get_children(self):
		return self.children

	def __str__(self):
		return self.code

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		child: SymbolNode
		child.parent = self
		self.children.append(child)
		return


class SymbolTree:
	"""
	It manages all the symbolic nodes and their structure read from xxx.sym file
	"""
	def __init__(self, sym_file_path: str):
		"""
		:param sym_file_path:
		"""
		self.sym_nodes = dict()		# string --> SymNode
		self.__parse__(sym_file_path)
		return

	def get_sym_nodes(self):
		return self.sym_nodes.values()

	def get_sym_node(self, key: str):
		"""
		:param key: sym@class@id
		:return:
		"""
		node = self.sym_nodes[key]
		node: SymbolNode
		return node

	def __parse__(self, sym_file_path: str):
		"""
		:param sym_file_path:
		:return:
		"""
		self.sym_nodes.clear()
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					key = items[0].strip()
					key_token = CToken.parse(key).get_token_value()
					class_name = items[1].strip()
					class_id = key_token[1]
					source = CToken.parse(items[2].strip())
					data_type = CToken.parse(items[3].strip())
					content = CToken.parse(items[4].strip())
					code = CToken.parse(items[5].strip())
					sym_node = SymbolNode(class_name, class_id, source, data_type, content, code.get_token_value())
					self.sym_nodes[key] = sym_node
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = self.sym_nodes[items[0].strip()]
					parent: SymbolNode
					children_items = items[6].strip().split(' ')
					for k in range(1, len(children_items) - 1):
						child_key = children_items[k].strip()
						child = self.sym_nodes[child_key]
						parent.add_child(child)
		return


def rand_select(objects):
	"""
	:param objects:
	:return: select a random object from the collection
	"""
	counter = random.randint(0, len(objects))
	selected_object = None
	for item in objects:
		selected_object = item
		counter -= 1
		if counter < 0:
			break
	return selected_object


def rand_resort(objects):
	"""
	:param objects:
	:return: list of objects randomly sorted from the inputs
	"""
	remains = set()
	for obj in objects:
		remains.add(obj)
	rlist = list()
	while len(remains) > 0:
		obj = rand_select(remains)
		remains.remove(obj)
		rlist.append(obj)
	return rlist


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext/features"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		sym_file = os.path.join(directory, file_name + ".sym")
		sym_tree = SymbolTree(sym_file)
		print("Load", len(sym_tree.get_sym_nodes()), "symbolic nodes from", file_name)
		for node in sym_tree.get_sym_nodes():
			node: SymbolNode
			print("\t{}[{}]\t<{}>\t\"{}\"".format(node.get_class_name(),
												  node.get_class_id(), node.get_data_type(), node.get_code()))
		print("End of", file_name, "\n")
	print()

