"""
ctype.py defines the data mining for describing the data type in C programming language semantics.
"""


__basic_type_keywords__ = {
    'void', 'bool', 'char', 'short', 'int',
    'long', 'float', 'double', 'complex',
    'imaginary'
}


class CType:
    """
    ctype   --> basic_type      [void|bool|char|short|int|long|float|double|complex|imaginary]
            --> array_type      (array <element_type>)
            --> point_type      (pointer <pointed_type>)
            --> function_type   (function <return_type>)
            --> struct_type     (struct <name>?)
            --> union_type      (union <name>?)
    """

    def __init__(self, keyword: str, child):
        """
        create type as (keyword <child>?)
        :param keyword:
        :param child: None means no child in current type
        """
        self.keyword = keyword
        self.child = child
        return

    def get_keyword(self):
        return self.keyword

    def get_child_type(self):
        return self.child

    def is_basic_type(self):
        return self.keyword in __basic_type_keywords__

    def is_array_type(self):
        return self.keyword == 'array'

    def is_pointer_type(self):
        return self.keyword == 'pointer'

    def is_function_type(self):
        return self.keyword == 'function'

    def is_struct_type(self):
        return self.keyword == 'struct'

    def is_union_type(self):
        return self.keyword == 'union'

    def __str__(self):
        if self.child is None:
            return self.keyword
        else:
            return '(' + self.keyword + ' ' + str(self.child) + ')'

    def has_child(self):
        return isinstance(self.child, CType)


class CTypeFactory:
    """
    Used to create the instances of CType
    """

    def __init__(self):
        self.types = dict()
        return

    def __get__(self, data_type: CType):
        key = str(data_type)
        if key not in self.types:
            self.types[key] = data_type
        return self.types[key]

    def new_basic(self, keyword: str):
        data_type = CType(keyword, None)
        return self.__get__(data_type)

    def new_array(self, element_type: CType):
        data_type = CType('array', element_type)
        return self.__get__(data_type)

    def new_pointer(self, pointed_type: CType):
        data_type = CType('pointer', pointed_type)
        return self.__get__(data_type)

    def new_function(self, return_type: CType):
        data_type = CType('function', return_type)
        return self.__get__(data_type)

    def new_struct(self, name: str):
        data_type = CType('struct', name)
        return self.__get__(data_type)

    def new_union(self, name: str):
        data_type = CType('union', name)
        return self.__get__(data_type)


class CTypeParser:
    """Used to parse the CType from string code"""
    def __init__(self):
        self.factory = CTypeFactory()
        return

    @staticmethod
    def __trim__(text: str):
        text = text.strip()
        if text[0] == '(':
            text = text[1:]
        if text[-1] == ')':
            text = text[0: len(text) - 1]
        return text.strip()

    @staticmethod
    def __split__(text: str):
        text = CTypeParser.__trim__(text)
        index = text.find(' ')
        if (index > 0) and (index < len(text)):
            return text[0:index].strip(), text[index+1:].strip()
        else:
            return text, None

    def parse(self, text: str):
        self.factory: CTypeFactory
        keyword, child = CTypeParser.__split__(text)
        if keyword in __basic_type_keywords__:
            return self.factory.new_basic(keyword)
        elif keyword == 'array':
            child_type = self.parse(child)
            return self.factory.new_array(child_type)
        elif keyword == 'pointer':
            child_type = self.parse(child)
            return self.factory.new_pointer(child_type)
        elif keyword == 'function':
            child_type = self.parse(child)
            return self.factory.new_function(child_type)
        elif keyword == 'struct':
            return self.factory.new_struct(child)
        elif keyword == 'union':
            return self.factory.new_union(child)
        else:
            return None


c_type_parser = CTypeParser()
