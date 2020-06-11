import os
from enum import Enum
import src.com.jcparse.base as base
import src.com.jcmuta.symbol as sym
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cprogram as cpro
import src.com.jcmuta.operator as mop


class ScoreVector:
    """
    test vector that kills a target mutant
    """
    def __init__(self, text: str):
        self.vector = list()
        self.text = text
        for k in range(0, len(text)):
            char = text[k]
            if char == '1':
                self.vector.append(True)
            else:
                self.vector.append(False)
        return

    def __str__(self):
        return self.text

    def get_vector(self):
        return self.vector

    def is_killed(self, test: int):
        return self.vector[test]

    def __len__(self):
        """
        :return: length of the score vector
        """
        return len(self.vector)


class ScoreVectors:
    """
    It ensures the uniqueness of score vector.
    """
    def __init__(self):
        self.vectors = dict()
        return

    def get_vector(self, text: str):
        """
        :param text:
        :return: get the score vector w.r.t. the string key as shown
        """
        if text not in self.vectors:
            self.vectors[text] = ScoreVector(text)
        vector = self.vectors[text]
        vector: ScoreVector
        return vector


class MutationCategory(Enum):
    killable = 0
    unkillable = 1


class MutantLabels:
    """
    {score_vector, kill_counter, kill_probability, category (K|U)}
    """

    def __init__(self, score_vector: ScoreVector):
        self.score_vector = score_vector
        self.kill_counter = 0
        for kill_flag in score_vector.get_vector():
            if kill_flag:
                self.kill_counter += 1
        self.kill_probability = self.kill_counter / len(score_vector)
        if self.kill_counter == 0:
            self.category = MutationCategory.unkillable
        else:
            self.category = MutationCategory.killable
        return

    def get_score_vector(self):
        return self.score_vector

    def get_kill_counter(self):
        """
        :return: how many tests kill it
        """
        return self.kill_counter

    def get_kill_probability(self):
        return self.kill_probability

    def get_category(self):
        self.category: MutationCategory
        return self.category

    def define_category(self, min_probability: float):
        """
        :param min_probability:
        :return: re-define the category of the label
        """
        if self.kill_probability <= min_probability:
            self.category = MutationCategory.unkillable
        else:
            self.category = MutationCategory.killable
        return


class StateConstraint:
    def __init__(self, constraint_lines: list, program: cpro.CProgram):
        self.execution = None
        condition_lines = list()
        for line in constraint_lines:
            line: str
            line = line.strip()
            if line.startswith("[execution]"):
                items = line.split('\t')
                self.execution = program.get_function_call_graph().get_execution(items[1].strip())
            elif line.startswith("[sym]"):
                condition_lines.append(line)
        self.condition = sym.sym_parser.parse_by_text_lines(condition_lines)
        return

    def get_execution(self):
        """
        :return: where the condition is evaluated
        """
        self.execution: cirflow.CirExecution
        return self.execution

    def get_condition(self):
        """
        :return: the symbolic condition being evaluated
        """
        self.condition: sym.CSymbolNode
        return self.condition

    def __str__(self):
        return str(self.execution) + "::{ " + self.condition.generate_code(True) + " }"


class StateConstraints:
    """
    {conjunct|disjunct; constraints}
    """
    def __init__(self, constraints_lines: list, program: cpro.CProgram):
        self.conjunct = False
        self.constraints = list()
        constraint_lines = list()
        for line in constraints_lines:
            line: str
            line = line.strip()
            if line.startswith("[type]"):
                items = line.split('\t')
                if items[1].strip() == "conjunct":
                    self.conjunct = True
            elif line.startswith("[constraint]"):
                constraint_lines.clear()
            elif line.startswith("[execution]") or line.startswith("[sym]"):
                constraint_lines.append(line.strip())
            elif line.startswith("[end_constraint]"):
                constraint = StateConstraint(constraint_lines, program)
                self.constraints.append(constraint)
        return

    def is_conjunct(self):
        return self.conjunct

    def is_disjunct(self):
        return not self.conjunct

    def get_constraints(self):
        return self.constraints

    def __str__(self):
        buffer = ""
        if self.conjunct:
            buffer += "["
        else:
            buffer += "<"
        for constraint in self.constraints:
            buffer += " " + str(constraint) + ";"
        if self.conjunct:
            buffer += " ]"
        else:
            buffer += " >"
        return buffer


class Mutation:
    """
    syntactic mutation is defined as {m_class, m_operator, location, parameter}
    """
    def __init__(self, m_class: mop.MutaClass, m_operator: mop.MutaOperator, location: astree.AstNode, parameter):
        self.m_class = m_class
        self.m_operator = m_operator
        self.location = location
        self.parameter = parameter
        return

    def get_mutation_class(self):
        return self.m_class

    def get_mutation_operator(self):
        return self.m_operator

    def get_location(self):
        self.location: astree.AstNode
        return self.location

    def get_parameter(self):
        return self.parameter

    def has_parameter(self):
        return self.parameter is not None


class Mutant:
    """
    mutant_space, id, mutation, labels, features
    """
    def __init__(self, space, id: int, m_class: mop.MutaClass, m_operator: mop.MutaOperator,
                 location: astree.AstNode, parameter):
        self.mutant_space = space
        self.id = id
        self.mutation = Mutation(m_class, m_operator, location, parameter)
        self.labels = None
        self.features = None    # wait for encoding...
        return

    def get_mutant_space(self):
        return self.mutant_space

    def get_id(self):
        return self.id

    def get_mutation(self):
        self.mutation: Mutation
        return self.mutation

    def get_labels(self):
        self.labels: MutantLabels
        return self.labels

    def set_labels(self, score_vector: ScoreVector):
        self.labels = MutantLabels(score_vector)
        return

    def get_features(self):
        self.features: StateInfection
        return self.features


class ErrorType(Enum):
    failure = 0
    syntax_error = 1
    execute = 2
    not_execute = 3
    execute_for = 4
    set_bool = 5
    chg_bool = 6
    set_numb = 7
    neg_numb = 8
    xor_numb = 9
    rsv_numb = 10
    dif_numb = 11
    inc_numb = 12
    dec_numb = 13
    chg_numb = 14
    dif_addr = 15
    set_addr = 16
    chg_addr = 17
    mut_expr = 18
    mut_refer = 19

    @staticmethod
    def parse(text: str):
        return ErrorType.__members__[text]

    def __str__(self):
        return self.name


class StateError:
    """
    (error_type, operand*)
    """
    def __init__(self, errors, error_line: str, program):
        errors: StateErrors
        self.errors = errors
        if len(error_line) > 0:
            items = error_line.strip().split('\t')
            self.error_type = ErrorType.parse(items[1].strip())
            self.operands = list()
            for k in range(2, len(items)):
                operand = base.get_content_of(items[k].strip())
                if items[k].startswith("cir@"):
                    operand = program.get_cir_tree().get_node(operand)
                self.operands.append(operand)
        else:
            self.error_type = None
            self.operands = list()
        return

    def get_error_set(self):
        self.errors: StateErrors
        return self.errors

    def get_error_type(self):
        return self.error_type

    def get_operands(self):
        return self.operands

    def get_operand(self, k: int):
        return self.operands[k]

    def __str__(self):
        buffer = str(self.error_type) + "("
        for operand in self.operands:
            buffer += " " + str(operand)
        buffer += " )"
        return buffer

    def get_cir_location(self):
        """
        :return: the cir-location as the operand to be described in this error.
        """
        for operand in self.operands:
            if isinstance(operand, cirtree.CirNode):
                operand: cirtree.CirNode
                return operand
        return None


class StateErrors:
    """
    To ensure the uniqueness of state error
    """
    def __init__(self):
        self.state_errors = dict()
        return

    def get_state_error(self, error_line: str, program):
        error_line = error_line.strip()
        state_error = StateError(self, error_line, program)
        return self.__record__(state_error)

    def __record__(self, state_error: StateError):
        if str(state_error) not in self.state_errors:
            self.state_errors[str(state_error)] = state_error
        return self.state_errors[str(state_error)]

    def failure(self):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.failure
        return self.__record__(state_error)

    def syntax_error(self):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.syntax_error
        return self.__record__(state_error)

    def execute(self, statement: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.execute
        state_error.operands.append(statement)
        return self.__record__(state_error)

    def not_execute(self, statement: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.not_execute
        state_error.operands.append(statement)
        return self.__record__(state_error)

    def execute_for(self, statement: cirtree.CirNode, loop_times: int):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.execute_for
        state_error.operands.append(statement)
        state_error.operands.append(loop_times)
        return self.__record__(state_error)

    def set_bool(self, expression: cirtree.CirNode, value: bool):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.set_bool
        state_error.operands.append(expression)
        state_error.operands.append(value)
        return self.__record__(state_error)

    def chg_bool(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.chg_bool
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def set_numb(self, expression: cirtree.CirNode, value):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.set_numb
        state_error.operands.append(expression)
        state_error.operands.append(value)
        return self.__record__(state_error)

    def neg_numb(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.neg_numb
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def xor_numb(self, expression: cirtree.CirNode, value: int):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.xor_numb
        state_error.operands.append(expression)
        state_error.operands.append(value)
        return self.__record__(state_error)

    def rsv_numb(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.rsv_numb
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def dif_numb(self, expression: cirtree.CirNode, value):
        if value != 0:
            state_error = StateError(self, "", None)
            state_error.error_type = ErrorType.dif_numb
            state_error.operands.append(expression)
            state_error.operands.append(value)
            return self.__record__(state_error)
        else:
            return None

    def inc_numb(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.inc_numb
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def dec_numb(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.dec_numb
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def chg_numb(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.chg_numb
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def dif_addr(self, expression: cirtree.CirNode, value: int):
        if value != 0:
            state_error = StateError(self, "", None)
            state_error.error_type = ErrorType.dif_addr
            state_error.operands.append(expression)
            state_error.operands.append(value)
            return self.__record__(state_error)
        else:
            return None

    def set_addr(self, expression: cirtree.CirNode, value):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.set_addr
        state_error.operands.append(expression)
        state_error.operands.append(value)
        return self.__record__(state_error)

    def chg_addr(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.chg_addr
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def mut_expr(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.mut_expr
        state_error.operands.append(expression)
        return self.__record__(state_error)

    def mut_refer(self, expression: cirtree.CirNode):
        state_error = StateError(self, "", None)
        state_error.error_type = ErrorType.mut_refer
        state_error.operands.append(expression)
        return self.__record__(state_error)


class StateInfection:
    """
    {mutant, faulty_statement, {state_error, constraints}, {state_error, extension_set}}
    """
    def __init__(self):
        self.mutant = None
        self.faulty_execution = None
        self.error_infections = dict()
        self.extension_set = dict()
        return

    def set_mutant(self, line: str, space):
        """
        :param line: [id] id
        :return:
        """
        items = line.split('\t')
        space: MutantSpace
        self.mutant = space.get_mutant(int(items[1].strip()))
        return

    def set_faulty_execution(self, line: str, program: cpro.CProgram):
        """
        :param line: [coverage] execution
        :param program:
        :return:
        """
        items = line.split('\t')
        self.faulty_execution = program.get_function_call_graph().get_execution(items[1].strip())
        return

    def add_error_infection(self, lines: list, state_errors: StateErrors, program: cpro.CProgram):
        """
        :param lines: [error]...[end_error]
        :param state_errors:
        :param program:
        :return:
        """
        state_error_line = None
        constraints_lines = list()
        in_constraint = False
        extension_lines = list()
        for line in lines:
            line: str
            line = line.strip()
            if line.startswith("[define]"):
                state_error_line = line.strip()
            elif line.startswith("[extend]"):
                extension_lines.append(line.strip())
            elif line.startswith("[constraints]"):
                in_constraint = True
            elif line.startswith("[end_constraints]"):
                in_constraint = False
            elif in_constraint:
                constraints_lines.append(line.strip())
        state_error = state_errors.get_state_error(state_error_line, program)
        extension_errors = set()
        for extension_line in extension_lines:
            extension_error = state_errors.get_state_error(extension_line, program)
            extension_errors.add(extension_error)
        state_constraints = StateConstraints(constraints_lines, program)
        self.error_infections[state_error] = state_constraints
        self.extension_set[state_error] = extension_errors
        return

    def get_mutant(self):
        self.mutant: Mutant
        return self.mutant

    def get_faulty_execution(self):
        self.faulty_execution: cirflow.CirExecution
        return self.faulty_execution

    def get_error_infections(self):
        return self.error_infections

    def get_state_errors(self):
        return self.error_infections.keys()

    def get_constraints_for(self, state_error: StateError):
        constraints = self.error_infections[state_error]
        constraints: StateConstraints
        return constraints

    def get_extension_set(self, state_error: StateError):
        """
        :param state_error:
        :return: set of errors extended from the source
        """
        return self.extension_set[state_error]


class MutantSpace:
    def __init__(self, program: cpro.CProgram):
        self.program = program
        self.mutants = dict()
        self.score_vectors = ScoreVectors()
        self.state_errors = StateErrors()
        mutant_file = os.path.join(program.get_directory(), program.get_file_name() + ".mut")
        labels_file = os.path.join(program.get_directory(), program.get_file_name() + ".lab")
        feature_file = os.path.join(program.get_directory(), program.get_file_name() + ".sem")
        self.__parse_mutations__(mutant_file)
        self.__parse_labels__(labels_file)
        self.__parse_features__(feature_file)
        return

    def __parse_mutation__(self, mutant_line: str):
        mutant_line = mutant_line.strip()
        if len(mutant_line) > 0:
            items = mutant_line.split('\t')
            id = int(items[0].strip())
            m_class = mop.MutaClass.parse(items[1].strip())
            m_operator = mop.MutaOperator.parse(items[2].strip())
            location = self.program.get_ast_tree().get_tree_node(base.get_content_of(items[3].strip()))
            parameter = None
            if len(items) > 4:
                parameter = base.get_content_of(items[4].strip())
                if m_class == mop.MutaClass.CTRP or m_class == mop.MutaClass.SGLR or m_class == mop.MutaClass.SRTR:
                    parameter = self.program.get_ast_tree().get_tree_node(parameter)
            mutant = Mutant(self, id, m_class, m_operator, location, parameter)
            self.mutants[id] = mutant
        return

    def __parse_mutations__(self, mutant_file: str):
        self.mutants.clear()
        with open(mutant_file, 'r') as reader:
            for line in reader:
                self.__parse_mutation__(line.strip())
        return

    def __parse_labels__(self, label_file: str):
        with open(label_file, 'r') as reader:
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    items = line.split('\t')
                    id = int(items[0].strip())
                    if id in self.mutants:
                        score_vector = self.score_vectors.get_vector(items[1].strip())
                        mutant = self.mutants[id]
                        mutant: Mutant
                        mutant.set_labels(score_vector)
        return

    def get_program(self):
        return self.program

    def get_mutants(self):
        return self.mutants.values()

    def get_mutant_id_set(self):
        return self.mutants.keys()

    def get_mutant(self, id: int):
        mutant = self.mutants[id]
        mutant: Mutant
        return mutant

    def __parse_features__(self, feature_file: str):
        with open(feature_file, 'r') as reader:
            state_infection, error_lines, in_errors = None, list(), False
            for line in reader:
                line = line.strip()
                if len(line) > 0:
                    if line.startswith("[mutant]"):
                        state_infection = StateInfection()
                    elif line.startswith("[id]"):
                        state_infection.set_mutant(line, self)
                    elif line.startswith("[coverage]"):
                        state_infection.set_faulty_execution(line, self.program)
                    elif line.startswith("[error]"):
                        in_errors = True
                        error_lines.clear()
                    elif line.startswith("[end_error]"):
                        in_errors = False
                        state_infection.add_error_infection(error_lines, self.state_errors, self.program)
                    elif in_errors:
                        error_lines.append(line.strip())
                    elif line.startswith("[end_mutant]"):
                        state_infection.get_mutant().features = state_infection
        return


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cpro.CProgram(directory)
        mutant_space = MutantSpace(program)
        print("Get mutation information for", program.get_file_name())
        output_file = os.path.join("C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\mutation", filename + ".mut")
        with open(output_file, 'w') as writer:
            for mutant in mutant_space.get_mutants():
                writer.write(str(mutant.get_id()) + "\t")
                writer.write(str(mutant.mutation.get_mutation_class()) + "\t")
                writer.write(str(mutant.mutation.get_mutation_operator()) + "\t")
                location = mutant.mutation.get_location()
                location: astree.AstNode
                writer.write(str(location.get_beg_line()) + "\t")
                writer.write("\"" + location.get_code(True) + "\"\t")
                if mutant.mutation.has_parameter():
                    writer.write(str(mutant.mutation.get_parameter()))
                else:
                    writer.write("")
                writer.write("\t")
                labels = mutant.labels
                labels: MutantLabels
                writer.write(str(labels.get_category()) + "\t")
                writer.write("\n")
                state_infection = mutant.get_features()
                state_infection: StateInfection
                writer.write("\tcoverage at:\t" + str(state_infection.get_faulty_execution()) + "\n")
                for state_error, constraints in state_infection.error_infections.items():
                    constraints: StateConstraints
                    writer.write("\tError of " + str(state_error) + "\tfor:\n")
                    writer.write("\t{\n")
                    for constraint in constraints.get_constraints():
                        constraint: StateConstraint
                        writer.write("\t\t")
                        sym_condition = sym.sym_evaluator.evaluate(constraint.get_condition())
                        writer.write(sym_condition.generate_code(True))
                        writer.write("\tat\t" + str(constraint.get_execution()) + "\n")
                        sym_cir_associations = sym.CSymTemplate.sym_cir_associations(
                            sym_condition, constraint.get_execution().get_statement())
                        for sym_reference, cir_references in sym_cir_associations.items():
                            if len(cir_references) > 0:
                                sym_template = sym.CSymTemplate.path_template(sym_reference, cir_references, 2)
                                writer.write("\t\t\t#Template: ")
                                writer.write(sym_template.get_sym_template().generate_code(True))
                                writer.write("\tfor\t")
                                writer.write(cir_references[0].generate_code(True))
                                writer.write("\n")
                    writer.write("\t}\n")
                    extension_errors = state_infection.get_extension_set(state_error)
                    writer.write("\t==>\t")
                    for extension_error in extension_errors:
                        writer.write(str(extension_error) + "; ")
                    writer.write("\n")
                writer.write("\n")
    print("Testing end for all...")
