"""This file defines the data model of basic token used in program modeling"""


import enum
import random


class CTokenClass(enum.Enum):
	"""
	It defines the class of CToken used as basic element in program modeling.
	"""
	NullPtr = 0
	Boolean = 1
	Character = 2
	Integer = 3
	Float = 4
	Complex = 5
	String = 6
	Keyword = 7
	Operator = 8
	Punctuate = 9
	DataType = 10
	AstNode = 11
	CirNode = 12
	CirExecution = 13
	AstCirNode = 14
	MutantID = 15
	TestID = 16
	SymbolID = 17


class CToken:
	"""
	It defines the data objects of CToken to represent basic element in our model.
	"""

	def __init__(self, token_class: CTokenClass, token_value):
		"""
		:param token_class: the class of this token
		:param token_value: the value of this token
		"""
		self.token_class = token_class
		self.token_value = token_value
		return

	def get_token_class(self):
		"""
		:return: the class of this token
		"""
		return self.token_class

	def get_token_value(self):
		"""
		:return: the value of this token
		"""
		return self.token_value

	def __str__(self):
		return str(self.token_value)

	@staticmethod
	def __decode_string__(content: str):
		"""
		:param content:
		:return: 	translate the normalized text into original type
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
		:param text: 	it parses the text to token object by following rules.
		:return:		n@null; 	b@bool; 	c@char; 	i@int; f@real; s@string; 	x@float@float;
						key@string; opr@string; pun@string; typ@string; 				sym@string@int;
						ast@int; 	cir@int; 	asc@int; 	mut@int; tst@int;			exe@string@int;
		"""
		items = text.strip().split('@')
		if len(items) > 0:
			title = items[0].strip()
			if title == 'n':  																			## n@null
				return CToken(CTokenClass(CTokenClass.NullPtr), None)
			elif title == 'b':  																		## b@bool
				return CToken(CTokenClass(CTokenClass.Boolean), bool(items[1].strip() == "true"))
			elif title == 'c':  																		## c@char
				return CToken(CTokenClass(CTokenClass.Character), int(items[1].strip()))
			elif title == 'i':  																		## i@int
				return CToken(CTokenClass(CTokenClass.Integer), int(items[1].strip()))
			elif title == 'f':  																		## f@real
				return CToken(CTokenClass(CTokenClass.Float), float(items[1].strip()))
			elif title == 'x':  																		## x@real@real
				value = (float(items[1].strip()), float(items[2].strip()))
				return CToken(CTokenClass(CTokenClass.Complex), value)
			elif title == 's':  																		##	s@txt
				return CToken(CTokenClass(CTokenClass.String), CToken.__decode_string__(items[1].strip()))
			elif title == 'key':																		##	key@txt
				return CToken(CTokenClass(CTokenClass.Keyword), CToken.__decode_string__(items[1].strip()))
			elif title == "opr":																		##	opr@txt
				return CToken(CTokenClass(CTokenClass.Operator), CToken.__decode_string__(items[1].strip()))
			elif title == "pun":																		##	pun@txt
				return CToken(CTokenClass(CTokenClass.Punctuate), CToken.__decode_string__(items[1].strip()))
			elif title == "typ":																		##	typ@txt
				return CToken(CTokenClass(CTokenClass.DataType), CToken.__decode_string__(items[1].strip()))
			elif title == "sym":																		##	sym@txt@int
				value = (items[1].strip(), int(items[2].strip()))
				return CToken(CTokenClass(CTokenClass.SymbolID), value)
			elif title == "ast":																		## 	ast@int
				return CToken(CTokenClass(CTokenClass.AstNode), int(items[1].strip()))
			elif title == "cir":																		## 	cir@int
				return CToken(CTokenClass(CTokenClass.CirNode), int(items[1].strip()))
			elif title == "asc":																		##	asc@int
				return CToken(CTokenClass(CTokenClass.AstCirNode), int(items[1].strip()))
			elif title == "exe":																		## 	exe@string@int
				value = (items[1].strip(), int(items[2].strip()))
				return CToken(CTokenClass(CTokenClass.CirExecution), value)
			elif title == "mut":																		## 	mut@int
				return CToken(CTokenClass(CTokenClass.MutantID), int(items[1].strip()))
			elif title == "tst":																		## 	tst@int
				return CToken(CTokenClass(CTokenClass.TestID), int(items[1].strip()))
			else:
				return None
		return None


class CType:
	"""
	Structural representation of CType
	"""

	def __init__(self, key: str, child):
		"""
		:param key: class of the CType
		:param child:
		"""
		self.key = key
		if child is None:
			self.child = None
		else:
			child: CType
			self.child = child
		return

	def get_key(self):
		return self.key

	def has_child(self):
		if self.child is None:
			return False
		return True

	def get_child(self):
		return self.child

	@staticmethod
	def parse_type(text: str):
		if text.endswith(')'):
			text = text[1: len(text) - 1].strip()
		if (text == "void") or (text == "char") or (text == "short") or (text == "int") or (text == "long"):
			return CType(text, None)
		elif text == "unsigned short":
			return CType("ushort", None)
		elif text == "unsigned int":
			return CType("uint", None)
		elif text == "unsigned long":
			return CType("ulong", None)
		elif text == "long long":
			return CType("llong", None)
		elif text == "unsigned long long":
			return CType("ullong", None)
		elif (text == "float") or (text == "double"):
			return CType(text, None)
		elif text == "long double":
			return CType("ldouble", None)
		elif text == "float _Complex":
			return CType("float_x", None)
		elif text == "double _Complex":
			return CType("double_x", None)
		elif text == "long double _Complex":
			return CType("ldouble_x", None)
		elif text == "float _Imaginary":
			return CType("float_i", None)
		elif text == "double _Imaginary":
			return CType("double_i", None)
		elif text == "long double _Imaginary":
			return CType("ldouble_i", None)
		elif text.endswith("]"):
			index = text.rindex('[')
			text = text[0: index].strip()
			childType = CType.parse_type(text)
			return CType("array", childType)
		elif text.endswith("*"):
			index = text.rindex('*')
			text = text[0: index].strip()
			childType = CType.parse_type(text)
			return CType("point", childType)
		elif text.endswith("#"):
			index = text.index('#')
			text = text[0: index].strip()
			childType = CType.parse_type(text)
			return CType("function", childType)
		elif text.startswith("struct"):
			index = text.rindex(' ')
			text = text[index + 1:].strip()
			childType = CType.parse_type(text)
			return CType("struct", childType)
		elif text.startswith("union"):
			index = text.rindex(' ')
			text = text[index + 1:].strip()
			childType = CType.parse_type(text)
			return CType("union", childType)
		else:
			return CType(text, None)

	def __str__(self):
		if self.child is None:
			return self.get_key()
		else:
			return "({} {})".format(self.key, str(self.child))


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


def strip_text(text: str, max_len: int):
	"""
	:param text:
	:param max_len:
	:return:
	"""
	result = ""
	for k in range(0, len(text)):
		ch = text[k]
		if ch.isspace() or (ch == '\n'):
			ch = ' '
		result += ch
		if len(result) >= max_len:
			result = result + "..."
			break
	return result

