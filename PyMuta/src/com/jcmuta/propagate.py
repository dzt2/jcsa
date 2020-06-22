import os
from collections import deque

import src.com.jcparse.base as base
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst
import src.com.jcparse.cprogram as cprog
import src.com.jcmuta.operator as oprt
import src.com.jcmuta.symbol as sym
import src.com.jcmuta.mutation as mut
import graphviz


# error propagation model

class StateErrorEdge:
    """
    It defines the error propagation relationship between two group of state errors in different location
    such that one causes or propagate to another as {source, target, constraints}.
    """

    def __init__(self, source, target, constraints: mut.StateConstraints):
        self.source = source
        self.target = target
        self.constraints = constraints
        return

    def get_source(self):
        """
        :return: source of state errors from which the propagation occurs
        """
        self.source: StateErrorNode
        return self.source

    def get_target(self):
        """
        :return: target of state errors that are caused from the source
        """
        self.target: StateErrorNode
        return self.target

    def get_constraints(self):
        """
        :return: symbolic constraints that are required for the propagation to occur.
        """
        return self.constraints


class StateErrorNode:
    """
    It defines a set of correlated state errors in one location that can cause errors in other place of the
    program, and defined as {graph, key; error, all_errors; in_edges, ou_edges;}
    """

    def __init__(self, graph, state_error: mut.StateError):
        self.graph = graph
        self.error = state_error
        self.necessary_errors = state_error.get_error_set().extend(state_error, True)
        self.all_errors = state_error.get_error_set().extend(state_error, False)
        self.in_edges = list()
        self.ou_edges = list()
        # generate the unique key of the node
        key_list = list()
        for error in self.necessary_errors:
            key_list.append(str(error))
        key_list.sort()
        self.key = str(key_list)
        return

    def get_graph(self):
        """
        :return: graph where the node is created
        """
        self.graph: StateErrorGraph
        return self.graph

    def get_key(self):
        """
        :return: the unique key that represents this node by its necessary set of state errors in one location
        """
        return self.key

    def get_error(self):
        """
        :return: the source error that generates necessary set of errors in this node of location
        """
        return self.error

    def get_necessary_errors(self):
        """
        :return: necessary or representative set of state errors in this node
        """
        return self.necessary_errors

    def get_representative_errors(self):
        """
        :return: necessary or representative set of state errors in this node
        """
        return self.necessary_errors

    def get_all_errors(self):
        """
        :return: all the state errors implied from the necessary set of errors in this node of location
        """
        return self.all_errors

    def get_cir_location(self):
        """
        :return: the location where the state errors in this node occur
        """
        return self.error.get_cir_location()

    def get_code_instances(self):
        """
        :return: the instances in flow graph that represent the code location where the error occurs in data flow
                 analysis.
        """
        cir_location = self.error.get_cir_location()
        if cir_location is None:
            return list()
        c_program = cir_location.get_tree().program
        c_program: cprog.CProgram
        instance_graph = c_program.get_instance_graph()
        return instance_graph.get_instances().get_instances_of_object(cir_location)

    def get_instance_nodes(self):
        """
        :return: the instances of execution of statement where the state error occurs in this node, which can be
                 used to translate the error to an instance in data flow analysis.
        """
        code_instances = self.get_code_instances()
        instance_nodes = list()
        for code_instance in code_instances:
            code_instance: cirinst.CirInstanceCode
            instance_nodes.append(code_instance.get_instance_node())
        return instance_nodes

    def get_in_edges(self):
        """
        :return: the propagation edges that cause this node of errors
        """
        return self.in_edges

    def get_ou_edges(self):
        """
        :return: the propagation edges caused from the errors in this node
        """
        return self.ou_edges

    def propagate(self, target, constraints: mut.StateConstraints):
        """
        :param target:
        :param constraints:
        :return: the propagation edge from this node to the target or None if the propagation is impossible
        """
        # to avoid self-self implications
        if self == target:
            return None
        # to avoid duplicated propagation edge
        target: StateErrorNode
        for ou_edge in self.ou_edges:
            ou_edge: StateErrorEdge
            if ou_edge.get_target() == target:
                return ou_edge
        # to simplify the state constraints
        sym_condition = constraints.sym_condition()
        if sym_condition.sym_type == sym.CSymbolType.Constant:
            constant = sym.sym_evaluator.__boolean__(sym_condition.content)
            if constant:
                constraints = mut.StateConstraints(True)
            else:
                return None  # no possible propagation
        # create the propagation edge from source to target
        edge = StateErrorEdge(self, target, constraints)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge

    def __str__(self):
        return str(self.error)

    def node_text(self, max_code_length):
        """
        :return:
        """
        buffer = str(self.error.get_error_type()) + "[ "
        for operand in self.error.get_operands():
            if isinstance(operand, cirtree.CirNode):
                operand: cirtree.CirNode
                if operand.ast_source is not None:
                    ast_node = operand.get_ast_source()
                    code = ast_node.get_code(True)
                    if len(code) > max_code_length:
                        code = code[0:max_code_length] + "..."
                    buffer += str(operand.cir_type) + "[" + str(ast_node.get_beg_line() + 1) + "]::\"" + code + "\"; "
                else:
                    code = operand.generate_code(True)
                    if len(code) > max_code_length:
                        code = code[0:max_code_length] + "..."
                    buffer += str(operand.cir_type) + "::\"" + code + "\"; "
            else:
                buffer += str(operand) + "; "
        buffer += "]"
        return buffer


class StateErrorGraph:
    """
    It defines a causality graph that describes the error propagation in state errors as
    {infection[mutant, error_set, program]; nodes, faulty_node;}
    """

    def __init__(self, infection: mut.StateInfection, distance: int):
        self.infection = infection
        self.nodes = dict()
        self.faulty_node = None
        self.invalid_chars = ["\""]
        builder = StateErrorBuilder(self)
        builder.generate_infection_layer()
        builder.generate_reaching_links()
        builder.generate_propagation(distance)
        return

    def get_infection(self):
        """
        :return: state infection used to generate the state error graph
        """
        return self.infection

    def get_mutant(self):
        """
        :return: the mutant from which the state error graph describes
        """
        return self.infection.get_mutant()

    def get_error_set(self):
        """
        :return: the state-errors that is used to generate state error in this graph
        """
        return self.infection.get_mutant().get_mutant_space().state_errors

    def get_program(self):
        """
        :return: the program from which the state errors in graph can occur
        """
        return self.infection.get_mutant().get_mutant_space().get_program()

    def get_faulty_node(self):
        """
        :return: node of execute(faulty_statement)
        """
        self.faulty_node: StateErrorNode
        return self.faulty_node

    def get_keys(self):
        """
        :return: keys of existing nodes in graph
        """
        return self.nodes.keys()

    def get_nodes(self):
        """
        :return: existing nodes
        """
        return self.nodes.values()

    def get_node(self, key: str):
        """
        :param key:
        :return: existing node w.r.t. the unique string key
        """
        node = self.nodes[key]
        node: StateErrorNode
        return node

    def get_node_of(self, state_error: mut.StateError):
        """
        :param state_error:
        :return: an existing node w.r.t. the error or newly created one
        """
        node = StateErrorNode(self, state_error)
        if node.get_key() not in self.nodes:
            self.nodes[node.key] = node
        return self.get_node(node.key)

    def __normalize_string__(self, text: str):
        buffer = ""
        for k in range(0, len(text)):
            char = text[k]
            if char in self.invalid_chars:
                char = "\'"
            buffer += char
        return buffer

    def write_dot_graph(self, file: str, max_code_length: int):
        """
        :param max_code_length:
        :param file:
        :return:
        """
        # 1. initialize the pdf graph
        graph_name = self.get_program().get_file_name() + "_mut_" + str(self.get_mutant().get_id())
        graph_mutant = self.get_mutant()
        graph_comment = "Mutant_%d of %s at Line %d as \"%s\" being mutated." % \
                        (graph_mutant.get_id(),
                         str(graph_mutant.get_mutation().get_mutation_operator()),
                         graph_mutant.get_mutation().get_location().get_beg_line() + 1,
                         graph_mutant.get_mutation().get_location().get_code(True))
        digraph = graphviz.Digraph(name=graph_name, comment=self.__normalize_string__(graph_comment), filename=file)
        # 2. create the nodes for each node in state error propagation graph
        for key, node in self.nodes.items():
            node: StateErrorNode
            digraph.node(name=key, label=node.node_text(max_code_length))
        # 3. create the edges for each propagation pair in the graph
        for source_key, source in self.nodes.items():
            source: StateErrorNode
            for edge in source.get_ou_edges():
                edge: StateErrorEdge
                target = edge.get_target()
                target_key = target.get_key()
                constraints = edge.get_constraints()
                code = str(constraints)
                digraph.edge(source_key, target_key, label=self.__normalize_string__(code))
        # 3. generate the dot file to pdf
        digraph.render(file)
        return


class StateErrorBuilder:
    """
    To build up the state error graph
    """

    def __init__(self, graph: StateErrorGraph):
        self.graph = graph
        return

    # generate infection layer
    def generate_infection_layer(self):
        """
        :return: generate the infection layer, including faulty-node and its infection edges to
                 the node of state errors directly caused by the mutation.
        """
        errors = self.graph.get_error_set()
        faulty_statement = self.graph.get_infection().get_faulty_execution().get_statement()
        source = self.graph.get_node_of(errors.execute(faulty_statement))
        self.graph.faulty_node = source
        for state_error, state_constraints in self.graph.get_infection().error_infections.items():
            target = self.graph.get_node_of(state_error)
            source.propagate(target, state_constraints)
        return

    # generate coverage layer

    @staticmethod
    def __constraints_of_flow__(flow):
        """
        :param flow: either execution flow or instance edge
        :return: the constraints to describe execution flow
        """
        constraints = mut.StateConstraints(True)
        if isinstance(flow, cirflow.CirExecutionFlow):  # execution-flow
            flow: cirflow.CirExecutionFlow
            if flow.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:  # cir_condition as true
                cir_condition = flow.get_source().get_statement().get_child(0)
                sym_condition = sym.sym_parser.parse_by_cir_tree(cir_condition)
                constraints.add_constraint(flow.get_source(), sym_condition)
            elif flow.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:  # cir_condition as false
                cir_condition = flow.get_source().get_statement().get_child(0)
                sym_operand = sym.sym_parser.parse_by_cir_tree(cir_condition)
                sym_condition = sym.CSymbolNode(sym.CSymbolType.UnaryExpression,
                                                base.CType(base.CMetaType.BoolType), base.COperator.logic_not)
                sym_condition.add_child(sym_operand)
                constraints.add_constraint(flow.get_source(), sym_condition)
            else:  # all-true for others
                pass
        else:  # instance_edge
            flow: cirinst.CirInstanceEdge
            if flow.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:  # cir_condition as false
                cir_condition = flow.get_source().get_source_statement().get_child(0)
                sym_condition = sym.sym_parser.parse_by_cir_tree(cir_condition)
                constraints.add_constraint(flow.get_source().get_source_execution(), sym_condition)
            elif flow.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:  # cir_condition as false
                cir_condition = flow.get_source().get_source_statement().get_child(0)
                sym_operand = sym.sym_parser.parse_by_cir_tree(cir_condition)
                sym_condition = sym.CSymbolNode(sym.CSymbolType.UnaryExpression,
                                                base.CType(base.CMetaType.BoolType), base.COperator.logic_not)
                sym_condition.add_child(sym_operand)
                constraints.add_constraint(flow.get_source().get_source_execution(), sym_condition)
            else:  # all-true for others
                pass
        return constraints

    def __generate_reaching_link__(self, beg_node: cirinst.CirInstanceNode, end_node: cirinst.CirInstanceNode, edges):
        """
        :param beg_node:
        :param end_node:
        :param edges:
        :return: generate the execution-link based on given instance path
        """
        if beg_node != end_node and len(edges) > 0:
            errors = self.graph.get_error_set()
            source = self.graph.get_node_of(errors.execute(beg_node.get_source_statement()))
            constraints = mut.StateConstraints(True)
            for edge in edges:
                edge: cirinst.CirInstanceEdge
                target = self.graph.get_node_of(errors.execute(edge.get_source().get_source_statement()))
                source.propagate(target, constraints)
                # update the loop-invariant
                source = target
                constraints = StateErrorBuilder.__constraints_of_flow__(edge)
            target = self.graph.get_node_of(errors.execute(end_node.get_source_statement()))
            source.propagate(target, constraints)
        return

    def generate_reaching_links(self):
        """
        :return: generate the execution-links for coverage conditions.
        """
        instance_nodes = self.graph.get_faulty_node().get_instance_nodes()
        for instance_node in instance_nodes:
            dominance_path = cprog.CDominancePath(instance_node)
            self.__generate_reaching_link__(dominance_path.get_beg_node(),
                                            dominance_path.get_mid_node(),
                                            dominance_path.get_prev_edges())
        return

    # generate propagation through operands in unary expression

    @staticmethod
    def __execution_of_cir_location__(cir_location: cirtree.CirNode):
        """
        :param cir_location:
        :return: the execution of the statement where the location is defined
        """
        c_program = cir_location.get_tree().program
        c_program: cprog.CProgram
        cir_statement = cir_location.statement_of()
        if cir_statement is not None:
            c_function = c_program.get_function_call_graph().get_function_of(cir_statement)
            return c_function.get_execution_flow_graph().get_execution_of(cir_statement)
        return None

    def __generate_on_arith_pos__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression: +{operand}
        :param error: to cause errors in expression from operand
        :return: error_dict: dict[StateError, constraints]
        """
        errors = self.graph.get_error_set()
        if error.error_type == mut.ErrorType.set_bool:  # set_numb
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.set_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:  # chg_numb
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.set_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.neg_numb:
            new_error = errors.neg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.rsv_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.dif_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.inc_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.dec_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(error.get_operand(1))
            new_error = errors.set_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.dif_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr:
            new_error = errors.chg_numb(expression)
        else:  # no more propagation from unavailable errors
            new_error = None
        if new_error is not None:
            error_dict[new_error] = mut.StateConstraints(True)
        return error_dict

    def __generate_on_arith_neg__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression: -{operand}
        :param error: to cause errors in expression from operand
        :return: error_dict: dict[StateError, constraints]
        """
        errors = self.graph.get_error_set()
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.set_numb(expression, -parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.set_numb(expression, -parameter)
        elif error.error_type == mut.ErrorType.neg_numb:
            new_error = errors.neg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.dif_numb(expression, -parameter)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.dec_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.inc_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.set_numb(expression, -parameter)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(error.get_operand(1))
            new_error = errors.set_numb(expression, -parameter)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr:
            new_error = errors.chg_numb(expression)
        else:
            new_error = None
        if new_error is not None:
            error_dict[new_error] = mut.StateConstraints(True)
        return error_dict

    def __generate_on_bitws_rsv__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression: ~operand
        :param error:
        :param error_dict:
        :return:
        """
        errors = self.graph.get_error_set()
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.set_numb(expression, ~parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.set_numb(expression, ~parameter)
        elif error.error_type == mut.ErrorType.neg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.rsv_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.dif_numb(expression, ~parameter)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.dec_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.inc_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__memory__.int_address(error.get_operand(1))
            new_error = errors.set_numb(expression, ~parameter)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.dif_numb(expression, ~parameter)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr:
            new_error = errors.chg_numb(expression)
        else:
            new_error = None
        if new_error is not None:
            error_dict[new_error] = mut.StateConstraints(True)
        return error_dict

    @staticmethod
    def __boolean_constraint__(expression: cirtree.CirNode, parameter: bool):
        """
        :param expression:
        :param parameter:
        :return: constraint that describes the boolean constraint on expression
        """
        constraints = mut.StateConstraints(True)
        sym_operand = sym.sym_parser.parse_by_cir_tree(expression)
        if parameter:
            if not expression.get_data_type().is_bool_type():  # expression != 0
                sym_condition = sym.CSymbolNode(sym.CSymbolType.BinaryExpression, base.CType(base.CMetaType.BoolType),
                                                base.COperator.not_equals)
                sym_condition.add_child(sym_operand)
                sym_condition.add_child(
                    sym.CSymbolNode(sym.CSymbolType.Constant, base.CType(base.CMetaType.IntType), 0))
            else:  # expression itself
                sym_condition = sym_operand
        else:
            if not expression.get_data_type().is_bool_type():  # expression == 0
                sym_condition = sym.CSymbolNode(sym.CSymbolType.BinaryExpression, base.CType(base.CMetaType.BoolType),
                                                base.COperator.equal_with)
                sym_condition.add_child(sym_operand)
                sym_condition.add_child(
                    sym.CSymbolNode(sym.CSymbolType.Constant, base.CType(base.CMetaType.IntType), 0))
            else:  # !expression
                sym_condition = sym.CSymbolNode(sym.CSymbolType.UnaryExpression, base.CType(base.CMetaType.BoolType),
                                                base.COperator.logic_not)
                sym_condition.add_child(sym_operand)
        constraints.add_constraint(StateErrorBuilder.__execution_of_cir_location__(expression), sym_condition)
        return constraints

    def __generate_on_logic_not__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression: !{operand}
        :param error:
        :param error_dict:
        :return:
        """
        errors = self.graph.get_error_set()
        new_error, constraints = None, None
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_bool(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.neg_numb:
            pass  # impossible influence
        elif error.error_type == mut.ErrorType.rsv_numb:
            parameter = True
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = True
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.inc_numb:
            parameter = True
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.chg_bool(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_bool(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(error.get_operand(1))
            parameter = sym.sym_evaluator.__boolean__(parameter)
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = True
            new_error = errors.set_bool(expression, not parameter)
            constraints = StateErrorBuilder.__boolean_constraint__(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_bool(expression)
            constraints = mut.StateConstraints(True)
        else:
            pass
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_address_of__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression: &operand
        :param error:
        :param error_dict:
        :return:
        """
        errors = self.graph.get_error_set()
        new_error, constraints = None, None
        if error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_addr(expression)
            constraints = mut.StateConstraints(True)
        else:
            pass
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_dereference__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression: *operand
        :param error:
        :param error_dict:
        :return:
        """
        errors = self.graph.get_error_set()
        new_error, constraints = None, None
        if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.failure()
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.set_numb:
            int_address = sym.sym_evaluator.__integer__(error.get_operand(1))
            sym_address = sym.sym_evaluator.__memory__.sym_address(int_address)
            if sym_address.startswith("#null") or sym_address.startswith("#invalid"):
                new_error = errors.failure()
            else:
                new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.failure()
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.dif_numb:
            new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.inc_numb or error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.set_addr:
            sym_address = str(error.get_operand(1))
            if sym_address.startswith("#null") or sym_address.startswith("#invalid"):
                new_error = errors.failure()
            else:
                new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.dif_addr:
            new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.mut_refer(expression)
            constraints = mut.StateConstraints(True)
        else:
            pass
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_type_cast__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        cast_type = expression.get_data_type()
        errors = self.graph.get_error_set()
        new_error, constraints = None, mut.StateConstraints(True)
        if error.error_type == mut.ErrorType.set_bool:
            if cast_type.is_bool_type():
                parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
                new_error = errors.set_bool(expression, parameter)
            elif cast_type.is_integer_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.set_numb(expression, parameter)
            elif cast_type.is_real_type():
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                new_error = errors.set_numb(expression, parameter)
            elif cast_type.is_address_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                parameter = sym.sym_evaluator.__memory__.sym_address(parameter)
                new_error = errors.set_addr(expression, parameter)
            else:
                pass
        elif error.error_type == mut.ErrorType.chg_bool:
            if cast_type.is_bool_type():
                new_error = errors.chg_bool(expression)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.chg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        elif error.error_type == mut.ErrorType.set_numb:
            if cast_type.is_bool_type():
                parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
                new_error = errors.set_bool(expression, parameter)
                constraints = StateErrorBuilder.__boolean_constraint__(expression, not parameter)
            elif cast_type.is_integer_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.set_numb(expression, parameter)
            elif cast_type.is_real_type():
                parameter = sym.sym_evaluator.__number__(error.get_operand(1)) + 0.0
                new_error = errors.set_numb(expression, parameter)
            elif cast_type.is_address_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                parameter = sym.sym_evaluator.__memory__.sym_address(parameter)
                new_error = errors.set_addr(expression, parameter)
            else:
                pass
        elif error.error_type == mut.ErrorType.neg_numb:
            if cast_type.is_bool_type():
                pass
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.neg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.set_addr(expression, "#invalid")
            else:
                pass
        elif error.error_type == mut.ErrorType.rsv_numb:
            if cast_type.is_bool_type():
                new_error = errors.set_bool(expression, True)
                constraints = StateErrorBuilder.__boolean_constraint__(expression, False)
            elif cast_type.is_integer_type():
                new_error = errors.rsv_numb(expression)
            elif cast_type.is_real_type():
                new_error = errors.chg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.set_addr(expression, "#invalid")
            else:
                pass
        elif error.error_type == mut.ErrorType.dif_numb:
            if cast_type.is_bool_type():
                new_error = errors.set_bool(expression, True)
                constraints = StateErrorBuilder.__boolean_constraint__(expression, False)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                new_error = errors.dif_numb(expression, parameter)
            elif cast_type.is_address_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.dif_addr(expression, parameter)
            else:
                pass
        elif error.error_type == mut.ErrorType.inc_numb:
            if cast_type.is_bool_type():
                new_error = errors.set_bool(expression, True)
                constraints = StateErrorBuilder.__boolean_constraint__(expression, False)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.inc_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        elif error.error_type == mut.ErrorType.dec_numb:
            if cast_type.is_bool_type():
                new_error = errors.chg_bool(expression)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.dec_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        elif error.error_type == mut.ErrorType.chg_numb:
            if cast_type.is_bool_type():
                new_error = errors.chg_bool(expression)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.chg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        elif error.error_type == mut.ErrorType.set_addr:
            int_address = sym.sym_evaluator.__integer__(error.get_operand(1))
            if cast_type.is_bool_type():
                parameter = sym.sym_evaluator.__boolean__(int_address)
                new_error = errors.set_bool(expression, parameter)
                constraints = StateErrorBuilder.__boolean_constraint__(expression, not parameter)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                parameter = sym.sym_evaluator.__integer__(int_address)
                new_error = errors.set_numb(expression, parameter)
            elif cast_type.is_address_type():
                parameter = sym.sym_evaluator.__memory__.sym_address(int_address)
                new_error = errors.set_addr(expression, parameter)
            else:
                pass
        elif error.error_type == mut.ErrorType.dif_addr:
            if cast_type.is_bool_type():
                new_error = errors.set_bool(expression, True)
                constraints = StateErrorBuilder.__boolean_constraint__(expression, False)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.dif_numb(expression, parameter)
            elif cast_type.is_address_type():
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.dif_addr(expression, parameter)
            else:
                pass
        elif error.error_type == mut.ErrorType.chg_addr:
            if cast_type.is_bool_type():
                new_error = errors.chg_bool(expression)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.chg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        elif error.error_type == mut.ErrorType.mut_expr:
            if cast_type.is_bool_type():
                new_error = errors.chg_bool(expression)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.chg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        elif error.error_type == mut.ErrorType.mut_refer:
            if cast_type.is_bool_type():
                new_error = errors.chg_bool(expression)
            elif cast_type.is_integer_type() or cast_type.is_real_type():
                new_error = errors.chg_numb(expression)
            elif cast_type.is_address_type():
                new_error = errors.chg_addr(expression)
            else:
                pass
        else:
            pass
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_wait_expression__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        cast_type = expression.get_data_type()
        errors = self.graph.get_error_set()
        new_error, constraints = None, mut.StateConstraints(True)
        if error.error_type == mut.ErrorType.syntax_error or error.error_type == mut.ErrorType.failure:
            pass
        elif cast_type.is_bool_type():
            new_error = errors.chg_bool(expression)
        elif cast_type.is_integer_type() or cast_type.is_real_type():
            new_error = errors.chg_numb(expression)
        elif cast_type.is_address_type():
            new_error = errors.chg_addr(expression)
        else:
            new_error = errors.mut_expr(expression)
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_assignment__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.set_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.neg_numb:
            new_error = errors.neg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.rsv_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.dif_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.inc_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.dec_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = str(error.get_operand(1))
            new_error = errors.set_addr(expression, parameter)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.dif_addr(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_addr(expression)
        elif error.error_type == mut.ErrorType.mut_expr:
            new_error = errors.mut_expr(expression)
        elif error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.mut_refer(expression)
        else:
            pass
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    # propagation through operand in binary-expression

    def __generate_on_arith_add__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :return:
        """
        # 1. get another operand w.r.t. the expression as given
        if expression.get_child(0) == error.get_cir_location():
            other_operand = expression.get_child(1)
        else:
            other_operand = expression.get_child(0)
        other_value = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(other_operand))
        # 2. generate new error and constraint based source error
        errors = self.graph.get_error_set()
        new_error, constraints = None, mut.StateConstraints(True)
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if other_value.sym_type == sym.CSymbolType.Constant:
                parameter = parameter + sym.sym_evaluator.__number__(other_value.content)
                new_error = errors.set_numb(expression, parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if other_value.sym_type == sym.CSymbolType.Constant:
                parameter = parameter + sym.sym_evaluator.__number__(other_value.content)
                new_error = errors.set_numb(expression, parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.neg_numb:
            if other_value.sym_type == sym.CSymbolType.Constant and \
                    sym.sym_evaluator.__number__(other_value.content) == 0:
                new_error = errors.neg_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            if other_value.sym_type == sym.CSymbolType.Constant and \
                    sym.sym_evaluator.__number__(other_value.content) == 0:
                new_error = errors.rsv_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.dif_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.inc_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.dec_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if other_value.sym_type == sym.CSymbolType.Constant:
                parameter = parameter + sym.sym_evaluator.__integer__(other_value.content)
                parameter = sym.sym_evaluator.__memory__.sym_address(parameter)
                new_error = errors.set_addr(expression, parameter)
            else:
                new_error = errors.chg_addr(expression)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            new_error = errors.dif_addr(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_addr(expression)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        else:
            pass
        # 3. save the results in error_dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_arith_sub__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declaration
        errors = self.graph.get_error_set()
        new_error, constraints = None, mut.StateConstraints(True)
        # 2. generate the new-error and constraints w.r.t. the expression
        if expression.get_child(0) == error.get_cir_location():  # expression := error - operand
            other_operand = expression.get_child(1)
            other_value = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(other_operand))
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if other_value.sym_type == sym.CSymbolType.Constant:
                    parameter -= sym.sym_evaluator.__number__(other_value.content)
                    new_error = errors.set_numb(expression, parameter)
                else:
                    if parameter:
                        new_error = errors.dif_numb(expression, 1)
                    else:
                        new_error = errors.dif_numb(expression, -1)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if other_value.sym_type == sym.CSymbolType.Constant:
                    parameter -= sym.sym_evaluator.__number__(other_value.content)
                    new_error = errors.set_numb(expression, parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                if other_value.sym_type == sym.CSymbolType.Constant and \
                        sym.sym_evaluator.__number__(other_value.content) == 0:
                    new_error = errors.neg_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.rsv_numb:
                if other_value.sym_type == sym.CSymbolType.Constant and \
                        sym.sym_evaluator.__number__(other_value.content) == 0:
                    new_error = errors.rsv_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                new_error = errors.dif_numb(expression, parameter)
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.inc_numb(expression)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.dec_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                if other_value.sym_type == sym.CSymbolType.Constant:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                    parameter -= sym.sym_evaluator.__number__(other_value.content)
                    new_error = errors.set_numb(expression, parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.dif_numb(expression, parameter)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr:
                new_error = errors.mut_expr(expression)
            elif error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.mut_expr(expression)
            else:
                pass
        else:  # expression := operand - error
            other_operand = expression.get_child(0)
            other_value = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(other_operand))
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if other_value.sym_type == sym.CSymbolType.Constant:
                    parameter = sym.sym_evaluator.__number__(other_value.content) - parameter
                    new_error = errors.set_numb(expression, parameter)
                else:
                    if parameter:
                        new_error = errors.dif_numb(expression, -1)
                    else:
                        new_error = errors.dif_numb(expression, 1)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if other_value.sym_type == sym.CSymbolType.Constant:
                    parameter = sym.sym_evaluator.__number__(other_value.content) - parameter
                    new_error = errors.set_numb(expression, parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                if other_value.sym_type == sym.CSymbolType.Constant and \
                        sym.sym_evaluator.__number__(other_value.content) == 0:
                    new_error = errors.neg_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.rsv_numb:
                if other_value.sym_type == sym.CSymbolType.Constant and \
                        sym.sym_evaluator.__number__(other_value.content) == 0:
                    new_error = errors.rsv_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                new_error = errors.dif_numb(expression, -parameter)
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.dec_numb(expression)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.inc_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                if other_value.sym_type == sym.CSymbolType.Constant:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                    parameter = sym.sym_evaluator.__number__(other_value.content) - parameter
                    new_error = errors.set_numb(expression, parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                new_error = errors.dif_numb(expression, -parameter)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr:
                new_error = errors.mut_expr(expression)
            elif error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.mut_expr(expression)
            else:
                pass
        # 3. save the error and its constraints
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_arith_mul__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error = self.graph.get_error_set(), None
        # 2. determine another operand and its constraints
        if expression.get_child(0) == error.get_cir_location():  # expression := error * another
            other_operand = expression.get_child(1)
        else:  # expression := another * error
            other_operand = expression.get_child(0)
        constraints = StateErrorBuilder.__boolean_constraint__(other_operand, True)
        # 3. determine the constant value hold by another operand or None
        other_sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(other_operand))
        if other_sym_operand.sym_type == sym.CSymbolType.Constant:
            other_value = sym.sym_evaluator.__number__(other_sym_operand.content)
        else:
            other_value = None
        # 4. type-based analysis
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if parameter == 0:
                new_error = errors.set_numb(expression, 0)
            elif other_value is not None:
                new_error = errors.set_numb(expression, parameter * other_value)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if parameter == 0:
                new_error = errors.set_numb(expression, 0)
            elif other_value is not None:
                new_error = errors.set_numb(expression, parameter * other_value)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.neg_numb:
            new_error = errors.neg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            if other_value is not None and other_value == 1:
                new_error = errors.rsv_numb(expression)
            else:
                new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if other_value is not None:
                new_error = errors.dif_numb(expression, parameter * other_value)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.inc_numb:
            if other_value is not None:
                if other_value > 0:
                    new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.dec_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            if other_value is not None:
                if other_value > 0:
                    new_error = errors.dec_numb(expression)
                else:
                    new_error = errors.inc_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if parameter == 0:
                new_error = errors.set_numb(expression, 0)
            elif other_value is not None:
                new_error = errors.set_numb(expression, parameter * other_value)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if other_value is not None:
                new_error = errors.dif_numb(expression, parameter * other_value)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        else:
            pass
        # 5. save the result in error_dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_arith_div__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declaration
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error and constraints based on error-type
        if expression.get_child(0) == error.get_cir_location():  # expression = error / operand
            # 2.1. get the operand, symbolic representation and constant of another
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            sym_constant = None
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            # 2. error based propagation
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter / sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter / sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                new_error = errors.neg_numb(expression)
            elif error.error_type == mut.ErrorType.rsv_numb:
                if sym_constant is not None and sym_constant == 1:
                    new_error = errors.rsv_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter != 0:
                    if sym_constant is not None:
                        new_error = errors.dif_numb(expression, parameter / sym_constant)
                    else:
                        new_error = errors.chg_numb(expression)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                if sym_constant is not None:
                    if sym_constant > 0:
                        new_error = errors.inc_numb(expression)
                    else:
                        new_error = errors.dec_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dec_numb:
                if sym_constant is not None:
                    if sym_constant > 0:
                        new_error = errors.dec_numb(expression)
                    else:
                        new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter / sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter != 0:
                    if sym_constant is not None:
                        new_error = errors.dif_numb(expression, parameter / sym_constant)
                    else:
                        new_error = errors.chg_numb(expression)
                else:
                    pass
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        else:  # expression = operand / error
            # 3.1. get the operand, symbolic expression and constant of the other
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            sym_constant = None
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            constraints = StateErrorBuilder.__boolean_constraint__(cir_operand, True)  # operand != 0
            # 3.2. generate error propagation for expression := operand / error
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.failure()
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant / parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.failure()
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant / parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                new_error = errors.neg_numb(expression)
            elif error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    if sym_constant is not None:
                        if sym_constant > 0:
                            new_error = errors.dec_numb(expression)
                        else:
                            new_error = errors.inc_numb(expression)
                    else:
                        new_error = errors.chg_numb(expression)
                else:
                    if sym_constant is not None:
                        if sym_constant > 0:
                            new_error = errors.inc_numb(expression)
                        else:
                            new_error = errors.dec_numb(expression)
                    else:
                        new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb:
                if sym_constant is not None:
                    if sym_constant > 0:
                        new_error = errors.dec_numb(expression)
                    else:
                        new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dec_numb:
                if sym_constant is not None:
                    if sym_constant > 0:
                        new_error = errors.inc_numb(expression)
                    else:
                        new_error = errors.dec_numb(expression)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter == 0:
                    new_error = errors.failure()
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant / parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    if sym_constant is not None:
                        if sym_constant > 0:
                            new_error = errors.dec_numb(expression)
                        else:
                            new_error = errors.inc_numb(expression)
                    else:
                        new_error = errors.chg_numb(expression)
                else:
                    if sym_constant is not None:
                        if sym_constant > 0:
                            new_error = errors.inc_numb(expression)
                        else:
                            new_error = errors.dec_numb(expression)
                    else:
                        new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    @staticmethod
    def __add_number_constraint__(constraints: mut.StateConstraints,
                                  expression: cirtree.CirNode, operator: base.COperator, constant):
        """
        :param expression:
        :param operator:
        :param constant:
        :return: operand operator value
        """
        sym_operand = sym.sym_parser.parse_by_cir_tree(expression)
        sym_constant = sym.CSymbolNode(sym.CSymbolType.Constant, expression.data_type, constant)
        sym_condition = sym.CSymbolNode(sym.CSymbolType.BinaryExpression, base.CType(base.CMetaType.BoolType), operator)
        sym_condition.add_child(sym_operand)
        sym_condition.add_child(sym_constant)
        execution = StateErrorBuilder.__execution_of_cir_location__(expression)
        return constraints.add_constraint(execution, sym_condition)

    def __generate_on_arith_mod__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declaration
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error and constraints based on error-type
        if expression.get_child(0) == error.get_cir_location():  # expression := error % operand
            # 2.1. determine the operand, symbolic expression and its operand value
            operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(operand))
            sym_constant = None
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
            # 2.2. reconstruct the state constraints
            StateErrorBuilder.__add_number_constraint__(constraints, operand, base.COperator.not_equals, 1)
            StateErrorBuilder.__add_number_constraint__(constraints, operand, base.COperator.not_equals, -1)
            # 2.3. error type based analysis
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter % sym_constant)
                elif parameter == 1 or parameter == -1:
                    new_error = errors.set_numb(expression, 1)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter % sym_constant)
                elif parameter == 1 or parameter == -1:
                    new_error = errors.set_numb(expression, 1)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                pass
            elif error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter % sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb or error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter % sym_constant)
                elif parameter == 1 or parameter == -1:
                    new_error = errors.set_numb(expression, 1)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter % sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        else:  # expression := operand % error
            # 2.1. determine the operand, symbolic expression and its operand value
            operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(operand))
            sym_constant = None
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
            # 2.2. reconstruct the state constraints
            StateErrorBuilder.__add_number_constraint__(constraints, operand, base.COperator.not_equals, 0)
            StateErrorBuilder.__add_number_constraint__(constraints, operand, base.COperator.not_equals, 1)
            StateErrorBuilder.__add_number_constraint__(constraints, operand, base.COperator.not_equals, -1)
            # 2.3. generate new-error by source error's type
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.failure()
                elif parameter == 1 or parameter == -1:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant % parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.failure()
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.failure()
                elif parameter == 1 or parameter == -1:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant % parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                pass
            elif error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb or error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter == 0:
                    new_error = errors.failure()
                elif parameter == 1 or parameter == -1:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant % parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_bitws_and__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations and get operand with its constant
        errors, new_error = self.graph.get_error_set(), None
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
        if sym_operand.sym_type == sym.CSymbolType.Constant:
            sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
        else:
            sym_constant = None
        constraints = StateErrorBuilder.__boolean_constraint__(cir_operand, True)
        # 2. generate new-error w.r.t the type of source state error
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if parameter == 0:
                new_error = errors.set_numb(expression, 0)
            elif sym_constant is not None:
                new_error = errors.set_numb(expression, parameter & sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if parameter == 0:
                new_error = errors.set_numb(expression, 0)
            elif sym_constant is not None:
                new_error = errors.set_numb(expression, parameter & sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.neg_numb:
            if sym_constant is not None and sym_constant == -1:
                new_error = errors.neg_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            if sym_constant is not None and sym_constant == -1:
                new_error = errors.rsv_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.dif_numb(expression, parameter & sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.inc_numb:
            if sym_constant is not None and sym_constant == -1:
                new_error = errors.inc_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            if sym_constant is not None and sym_constant == -1:
                new_error = errors.dec_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if parameter == 0:
                new_error = errors.set_numb(expression, 0)
            elif sym_constant is not None:
                new_error = errors.set_numb(expression, parameter & sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.dif_numb(expression, parameter & sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        else:
            pass
        # 3. save the new-error with its state constraints
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_bitws_ior__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations and get operand with its constant
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
        if sym_operand.sym_type == sym.CSymbolType.Constant:
            sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
        else:
            sym_constant = None
        StateErrorBuilder.__add_number_constraint__(constraints, cir_operand, base.COperator.not_equals, -1)
        # 2. generate new-error w.r.t the type of source state error
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if parameter == -1:
                new_error = errors.set_numb(expression, -1)
            elif sym_constant is not None:
                new_error = errors.set_numb(expression, parameter | sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if parameter == -1:
                new_error = errors.set_numb(expression, -1)
            elif sym_constant is not None:
                new_error = errors.set_numb(expression, parameter | sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.neg_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.neg_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.rsv_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.dif_numb(expression, parameter | sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.inc_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.inc_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.dec_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if parameter == -1:
                new_error = errors.set_numb(expression, -1)
            elif sym_constant is not None:
                new_error = errors.set_numb(expression, parameter | sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.dif_numb(expression, parameter | sym_constant)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        else:
            pass
        # 3. save the new-error with its state constraints
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_bitws_xor__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations and get operand with its constant
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
        if sym_operand.sym_type == sym.CSymbolType.Constant:
            sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
        else:
            sym_constant = None
        # 2. generate new-error w.r.t the type of source state error
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.set_numb(expression, sym_constant ^ parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.set_numb(expression, sym_constant ^ parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.neg_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.neg_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.rsv_numb(expression)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.dif_numb(expression, sym_constant ^ parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.inc_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.inc_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            if sym_constant is not None and sym_constant == 0:
                new_error = errors.dec_numb(expression)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if sym_constant is not None:
                new_error = errors.set_numb(expression, sym_constant ^ parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.dif_numb(expression, sym_constant ^ parameter)
            else:
                new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_numb(expression)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_numb(expression)
        else:
            pass
        # 3. save the new-error with its state constraints
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_bitws_lsh__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(0) == error.get_cir_location():  # expression := error << operand
            # 2.1. determine another operand with its symbolic expression and constant
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
            else:
                sym_constant = None
            StateErrorBuilder.__add_number_constraint__(constraints, cir_operand, base.COperator.smaller_eq, 32)
            # 2.2. error propagation based on type of source state error
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter << sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter << sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.dif_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter << sym_constant)
                elif parameter > 0:
                    new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.dec_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter == 0:
                    new_error = errors.dif_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter << sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.dif_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter << sym_constant)
                elif parameter > 0:
                    new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.dec_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        else:  # expression := operand << error
            # 2.1. determine another operand with its symbolic expression and constant
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
            else:
                sym_constant = None
            StateErrorBuilder.__add_number_constraint__(constraints, cir_operand, base.COperator.not_equals, 0)
            # 2.2. error propagation based on type of source state error
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter > 32:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant << parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter > 32:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant << parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb or error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter > 32:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant << parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_bitws_rsh__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(0) == error.get_cir_location():  # expression := error >> operand
            # 2.1. determine another operand with its symbolic expression and constant
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
            else:
                sym_constant = None
            StateErrorBuilder.__add_number_constraint__(constraints, cir_operand, base.COperator.smaller_eq, 32)
            # 2.2. error propagation based on source error type
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter >> parameter)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter >> sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter << sym_constant)
                elif parameter > 0:
                    new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.dec_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb or error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter == 0:
                    new_error = errors.set_numb(expression, 0)
                elif sym_constant is not None:
                    new_error = errors.set_numb(expression, parameter >> sym_constant)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.dif_numb(expression, parameter >> sym_constant)
                elif parameter > 0:
                    new_error = errors.inc_numb(expression)
                else:
                    new_error = errors.dec_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr or mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        else:  # expression := operand >> error
            # 2.1. determine another operand with its symbolic expression and constant
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__integer__(sym_operand.content)
            else:
                sym_constant = None
            StateErrorBuilder.__add_number_constraint__(constraints, cir_operand, base.COperator.not_equals, 0)
            # 2.2. error propagation based on source error type
            if error.error_type == mut.ErrorType.set_bool:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant >> parameter)
                elif parameter > 32:
                    new_error = errors.set_numb(expression, 0)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_bool:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant >> parameter)
                elif parameter > 32:
                    new_error = errors.set_numb(expression, 0)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.dec_numb(expression)
                else:
                    new_error = errors.inc_numb(expression)
            elif error.error_type == mut.ErrorType.inc_numb or error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if sym_constant is not None:
                    new_error = errors.set_numb(expression, sym_constant >> parameter)
                elif parameter > 32:
                    new_error = errors.set_numb(expression, 0)
                else:
                    new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.dec_numb(expression)
                else:
                    new_error = errors.inc_numb(expression)
            elif error.error_type == mut.ErrorType.chg_addr:
                new_error = errors.chg_numb(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_numb(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_logic_and__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. determine the operand, symbolic expression and its constant, constraints.
        errors, new_error = self.graph.get_error_set(), None
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        constraints = StateErrorBuilder.__boolean_constraint__(cir_operand, True)
        # 2. determine the error propagation based on source error type
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if parameter > 0:
                new_error = errors.set_bool(expression, True)
            else:
                new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            new_error = errors.set_bool(expression, parameter != 0)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if parameter > 0:
                new_error = errors.set_bool(expression, True)
            else:
                new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.mut_expr or mut.ErrorType.mut_refer:
            new_error = errors.set_bool(expression, True)
        else:
            pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_logic_ior__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. determine the operand, symbolic expression and its constant, constraints.
        errors, new_error = self.graph.get_error_set(), None
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        constraints = StateErrorBuilder.__boolean_constraint__(cir_operand, False)
        # 2. determine the error propagation based on source error type
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.dif_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if parameter > 0:
                new_error = errors.set_bool(expression, True)
            else:
                new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.inc_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            new_error = errors.set_bool(expression, parameter != 0)
        elif error.error_type == mut.ErrorType.dif_addr:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if parameter > 0:
                new_error = errors.set_bool(expression, True)
            else:
                new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.chg_bool(expression)
        else:
            pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_greater_tn__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(0) == error.get_cir_location():  # expression := error > operand
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, parameter > sym_constant)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_addr or error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, True)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, False)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb or \
                    error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_bool or error.error_type == mut.ErrorType.chg_addr:
                if sym_constant is not None and sym_constant < 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        else:  # expression := operand > error
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, sym_constant > parameter)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, False)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, True)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.neg_numb or \
                    error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_addr or error.error_type == mut.ErrorType.chg_bool:
                if sym_constant is not None and sym_constant < 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_greater_eq__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(0) == error.get_cir_location():  # expression := error > operand
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, parameter >= sym_constant)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_addr or error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, True)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, False)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb or \
                    error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_bool or error.error_type == mut.ErrorType.chg_addr:
                if sym_constant is not None and sym_constant <= 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        else:  # expression := operand > error
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, sym_constant >= parameter)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, False)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, True)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.neg_numb or \
                    error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_addr or error.error_type == mut.ErrorType.chg_bool:
                if sym_constant is not None and sym_constant <= 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_smaller_tn__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(1) == error.get_cir_location():  # expression := operand < error
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, parameter > sym_constant)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_addr or error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, True)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, False)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb or \
                    error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_bool or error.error_type == mut.ErrorType.chg_addr:
                if sym_constant is not None and sym_constant < 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        else:  # expression := error < operand
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, sym_constant > parameter)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, False)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, True)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.neg_numb or \
                    error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_addr or error.error_type == mut.ErrorType.chg_bool:
                if sym_constant is not None and sym_constant < 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_smaller_eq__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(1) == error.get_cir_location():  # expression := operand <= error
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(0)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, parameter >= sym_constant)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_addr or error.error_type == mut.ErrorType.dif_numb:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, True)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, False)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb or \
                    error.error_type == mut.ErrorType.chg_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_bool or error.error_type == mut.ErrorType.chg_addr:
                if sym_constant is not None and sym_constant <= 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        else:  # expression := error <= operand
            # 2.1. get the constant and description of another operand
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            else:
                sym_constant = None
            # 2.2. error propagation to new-error
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb or \
                    error.error_type == mut.ErrorType.set_addr:
                if error.error_type == mut.ErrorType.set_addr:
                    parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                else:
                    parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if sym_constant is not None:
                    new_error = errors.set_bool(expression, sym_constant >= parameter)
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.dif_addr:
                parameter = sym.sym_evaluator.__number__(error.get_operand(1))
                if parameter > 0:
                    new_error = errors.set_bool(expression, False)
                elif parameter < 0:
                    new_error = errors.set_bool(expression, True)
                else:
                    pass
            elif error.error_type == mut.ErrorType.inc_numb:
                new_error = errors.set_bool(expression, False)
            elif error.error_type == mut.ErrorType.dec_numb:
                new_error = errors.set_bool(expression, True)
            elif error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.neg_numb or \
                    error.error_type == mut.ErrorType.rsv_numb:
                new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.chg_addr or error.error_type == mut.ErrorType.chg_bool:
                if sym_constant is not None and sym_constant <= 0:
                    pass
                else:
                    new_error = errors.chg_bool(expression)
            elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.chg_bool(expression)
            else:
                pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_equal_with__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. determine the operand, symbolic expression and its constant, constraints.
        errors, new_error = self.graph.get_error_set(), None
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
        if sym_operand.sym_type == sym.CSymbolType.Constant:
            sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
        else:
            sym_constant = None
        constraints = mut.StateConstraints(True)
        # 2. error propagation based on error type
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.set_bool(expression, parameter == sym_constant)
            else:
                new_error = errors.set_bool(expression, False)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.set_bool(expression, False)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.set_bool(expression, parameter == sym_constant)
            else:
                new_error = errors.set_bool(expression, False)
        elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.set_bool(expression, False)
        elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.inc_numb or \
                error.error_type == mut.ErrorType.dec_numb or error.error_type == mut.ErrorType.dif_addr:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.set_bool(expression, False)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if sym_constant is not None:
                new_error = errors.set_bool(expression, parameter == sym_constant)
            else:
                new_error = errors.set_bool(expression, False)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.set_bool(expression, False)
        else:
            pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_not_equals__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. determine the operand, symbolic expression and its constant, constraints.
        errors, new_error = self.graph.get_error_set(), None
        if expression.get_child(0) == error.get_cir_location():
            cir_operand = expression.get_child(1)
        else:
            cir_operand = expression.get_child(0)
        sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
        if sym_operand.sym_type == sym.CSymbolType.Constant:
            sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
        else:
            sym_constant = None
        constraints = mut.StateConstraints(True)
        # 2. error propagation based on error type
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.set_bool(expression, parameter != sym_constant)
            else:
                new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if sym_constant is not None:
                new_error = errors.set_bool(expression, parameter != sym_constant)
            else:
                new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.neg_numb or error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.inc_numb or \
                error.error_type == mut.ErrorType.dec_numb or error.error_type == mut.ErrorType.dif_addr:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.chg_addr:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            if sym_constant is not None:
                new_error = errors.set_bool(expression, parameter != sym_constant)
            else:
                new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.mut_expr or error.error_type == mut.ErrorType.mut_refer:
            new_error = errors.set_bool(expression, True)
        else:
            pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_field_expression__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. propagation based on source error type
        if expression.get_child(0) == error.get_cir_location():  # error.field
            if error.error_type == mut.ErrorType.mut_expr:
                new_error = errors.mut_expr(expression)
            elif error.error_type == mut.ErrorType.mut_refer:
                new_error = errors.mut_refer(expression)
            else:
                pass
        else:  # body.error
            new_error = errors.mut_refer(expression)
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_on_initializer_body__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        errors = self.graph.get_error_set()
        if error.error_type != mut.ErrorType.syntax_error and error.error_type != mut.ErrorType.failure:
            error_dict[errors.mut_expr(expression)] = mut.StateConstraints(True)
        return error_dict

    # error propagation in one statement

    def __translate_as_condition__(self, expression: cirtree.CirNode, error: mut.StateError, error_dict: dict):
        """
        :param expression:
        :param error:
        :param error_dict:
        :return:
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. error translation
        if error.error_type == mut.ErrorType.set_bool:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_numb:
            parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
            new_error = errors.set_bool(expression, parameter)
        elif error.error_type == mut.ErrorType.neg_numb:
            pass
        elif error.error_type == mut.ErrorType.rsv_numb:
            new_error = errors.set_bool(expression, True)
        elif error.error_type == mut.ErrorType.dif_numb or error.error_type == mut.ErrorType.dif_addr or \
                error.error_type == mut.ErrorType.inc_numb:
            parameter = sym.sym_evaluator.__number__(error.get_operand(1))
            if parameter > 0:
                new_error = errors.set_bool(expression, True)
            else:
                new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.dec_numb:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.chg_numb:
            new_error = errors.chg_bool(expression)
        elif error.error_type == mut.ErrorType.set_addr:
            parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
            new_error = errors.set_bool(expression, parameter != 0)
        elif error.error_type == mut.ErrorType.chg_addr or \
                error.error_type == mut.ErrorType.mut_refer or error.error_type == mut.ErrorType.mut_expr:
            new_error = errors.chg_bool(expression)
        else:
            pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict

    def __generate_inner_statement__(self, error: mut.StateError):
        """
        :param error:
        :return: error_dict
        """
        error_dict = dict()
        if error.get_cir_location() is not None:
            cir_location = error.get_cir_location()
            cir_parent = cir_location.get_parent()
            if cir_parent is None:
                print("\t\t@WARN: None for", cir_location.generate_code(True), "of",
                      cir_location.ast_source.get_code(True), "at line", cir_location.ast_source.get_beg_line() + 1)
                pass
            elif cir_parent.cir_type == cirtree.CirType.defer_expression:
                self.__generate_on_dereference__(cir_parent, error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.field_expression:
                self.__generate_on_field_expression__(cir_parent, error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.address_expression:
                self.__generate_on_address_of__(cir_parent, error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.cast_expression:
                self.__generate_on_type_cast__(cir_parent, error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.initializer_body:
                self.__generate_on_initializer_body__(cir_parent, error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.wait_expression:
                self.__generate_on_wait_expression__(cir_parent, error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.arith_expression:
                operator = cir_parent.get_operator()
                if operator == base.COperator.positive:
                    self.__generate_on_arith_pos__(cir_parent, error, error_dict)
                elif operator == base.COperator.negative:
                    self.__generate_on_arith_neg__(cir_parent, error, error_dict)
                elif operator == base.COperator.arith_add:
                    self.__generate_on_arith_add__(cir_parent, error, error_dict)
                elif operator == base.COperator.arith_sub:
                    self.__generate_on_arith_sub__(cir_parent, error, error_dict)
                elif operator == base.COperator.arith_mul:
                    self.__generate_on_arith_mul__(cir_parent, error, error_dict)
                elif operator == base.COperator.arith_div:
                    self.__generate_on_arith_div__(cir_parent, error, error_dict)
                elif operator == base.COperator.arith_mod:
                    self.__generate_on_arith_mod__(cir_parent, error, error_dict)
                else:
                    pass
            elif cir_parent.cir_type == cirtree.CirType.bitws_expression:
                operator = cir_parent.get_operator()
                if operator == base.COperator.bitws_rsv:
                    self.__generate_on_bitws_rsv__(cir_parent, error, error_dict)
                elif operator == base.COperator.bitws_and:
                    self.__generate_on_bitws_and__(cir_parent, error, error_dict)
                elif operator == base.COperator.bitws_ior:
                    self.__generate_on_bitws_ior__(cir_parent, error, error_dict)
                elif operator == base.COperator.bitws_xor:
                    self.__generate_on_bitws_xor__(cir_parent, error, error_dict)
                elif operator == base.COperator.bitws_lsh:
                    self.__generate_on_bitws_lsh__(cir_parent, error, error_dict)
                elif operator == base.COperator.bitws_rsh:
                    self.__generate_on_bitws_rsh__(cir_parent, error, error_dict)
                else:
                    pass
            elif cir_parent.cir_type == cirtree.CirType.logic_expression:
                operator = cir_parent.get_operator()
                if operator == base.COperator.logic_not:
                    self.__generate_on_logic_not__(cir_parent, error, error_dict)
                elif operator == base.COperator.logic_and:
                    self.__generate_on_logic_and__(cir_parent, error, error_dict)
                elif operator == base.COperator.logic_ior:
                    self.__generate_on_logic_ior__(cir_parent, error, error_dict)
                else:
                    pass
            elif cir_parent.cir_type == cirtree.CirType.relational_expression:
                operator = cir_parent.get_operator()
                if operator == base.COperator.greater_tn:
                    self.__generate_on_greater_tn__(cir_parent, error, error_dict)
                elif operator == base.COperator.greater_eq:
                    self.__generate_on_greater_eq__(cir_parent, error, error_dict)
                elif operator == base.COperator.smaller_tn:
                    self.__generate_on_smaller_tn__(cir_parent, error, error_dict)
                elif operator == base.COperator.smaller_eq:
                    self.__generate_on_smaller_eq__(cir_parent, error, error_dict)
                elif operator == base.COperator.not_equals:
                    self.__generate_on_not_equals__(cir_parent, error, error_dict)
                elif operator == base.COperator.equal_with:
                    self.__generate_on_equal_with__(cir_parent, error, error_dict)
                else:
                    pass
            elif cir_parent.is_assign_statement() and cir_parent.get_child(1) == cir_location:
                self.__generate_on_assignment__(cir_parent.get_child(0), error, error_dict)
            elif cir_parent.cir_type == cirtree.CirType.if_statement or \
                    cir_parent.cir_type == cirtree.CirType.case_statement:
                self.__translate_as_condition__(cir_parent.get_child(0), error, error_dict)
            else:
                pass
        return error_dict

    def __propagate_iteration_inner_statement__(self, source: StateErrorNode):
        """
        :param source:
        :return: target_nodes from source to generate in error propagation
        """
        # 1. collect all the error-dicts in each necessary error in source
        target_error_dicts = dict()
        for source_error in source.get_necessary_errors():
            target_error_dict = self.__generate_inner_statement__(source_error)
            for target_error, constraints in target_error_dict.items():
                if target_error is not None:
                    target_error_dicts[target_error] = constraints
        # 2. simplify errors until the representative ones
        target_errors = source.get_error().get_error_set().representative_set(target_error_dicts.keys())
        # 3. generate propagation from source to each of its target errors nodes
        target_nodes = set()
        for target_error in target_errors:
            constraints = target_error_dicts[target_error]
            target = self.graph.get_node_of(target_error)
            if source != target:
                source.propagate(target, constraints)
                target_nodes.add(target)
        return target_nodes

    def __propagate_recursive_inner_statement__(self, source: StateErrorNode, solutions: dict):
        """
        :param source:
        :param solutions:
        :return:
        """
        if source not in solutions:
            targets = self.__propagate_iteration_inner_statement__(source)
            solutions[source] = targets
            for target in targets:
                self.__propagate_recursive_inner_statement__(target, solutions)
        return

    def __propagate_inner_statement__(self, source: StateErrorNode):
        """
        :param source:
        :return: leafs from which the source error propagates
        """
        solutions, leafs = dict(), set()
        self.__propagate_recursive_inner_statement__(source, solutions)
        for node, target_nodes in solutions.items():
            node: StateErrorNode
            if len(target_nodes) == 0:
                leafs.add(node)
        return leafs

    # error propagation through statements: condition, data-flow {argument-parameter, return-wait, normal} and call-wait

    def __information_nodes_of__(self, cir_location: cirtree.CirNode):
        """
        :param cir_location:
        :return: information nodes w.r.t. the location in cir-language
        """
        c_program = self.graph.get_program()
        instance_graph = c_program.get_instance_graph()
        code_instances = instance_graph.get_instances().get_instances_of_object(cir_location)
        information_graph = c_program.get_information_graph()
        information_nodes = set()
        for code_instance in code_instances:
            code_instance: cirinst.CirInstanceCode
            if information_graph.has_node(code_instance):
                information_node = information_graph.get_node(code_instance)
                information_nodes.add(information_node)
        return information_nodes

    def __propagate_on_condition__(self, source: StateErrorNode):
        """
        :param source:
        :return: propagate from errors in conditional statement
        """
        # 1. collect the statements in true and false branch of condition
        condition = source.get_error().get_cir_location()
        information_nodes = self.__information_nodes_of__(condition)
        true_branch, false_branch = set(), set()
        for information_node in information_nodes:
            for information_edge in information_node.get_ou_edges():
                information_edge: cprog.CInformationEdge
                target_instance = information_edge.get_target().get_instance()
                if isinstance(target_instance, cirinst.CirInstanceNode):
                    target_statement = target_instance.get_source_statement()
                else:
                    target_instance: cirinst.CirInstanceCode
                    target_statement = target_instance.get_cir_source_node()
                if target_statement.is_statement():
                    if information_edge.get_flow().get_flow_type() == cprog.CInformationFlowType.execute_in_true:
                        true_branch.add(target_statement)
                    elif information_edge.get_flow().get_flow_type() == cprog.CInformationFlowType.execute_in_false:
                        false_branch.add(target_statement)
        # 2. generate the errors in next generation
        errors, error_dict = self.graph.get_error_set(), dict()
        for error in source.get_necessary_errors():
            if error.error_type == mut.ErrorType.set_bool or error.error_type == mut.ErrorType.set_numb:
                parameter = sym.sym_evaluator.__boolean__(error.get_operand(1))
                if parameter:
                    for statement in true_branch:
                        next_error = errors.execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                    for statement in false_branch:
                        next_error = errors.not_execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                else:
                    for statement in false_branch:
                        next_error = errors.execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                    for statement in true_branch:
                        next_error = errors.not_execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                break
            elif error.error_type == mut.ErrorType.set_addr:
                parameter = sym.sym_evaluator.__memory__.int_address(str(error.get_operand(1)))
                if parameter != 0:
                    for statement in true_branch:
                        next_error = errors.execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                    for statement in false_branch:
                        next_error = errors.not_execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                else:
                    for statement in false_branch:
                        next_error = errors.execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                    for statement in true_branch:
                        next_error = errors.not_execute(statement)
                        error_dict[next_error] = mut.StateConstraints(True)
                break
            elif error.error_type == mut.ErrorType.chg_bool or error.error_type == mut.ErrorType.chg_numb or error.error_type == mut.ErrorType.chg_addr:
                for statement in true_branch:
                    error_dict[errors.execute(statement)] = StateErrorBuilder.__boolean_constraint__(condition, False)
                    error_dict[errors.not_execute(statement)] = StateErrorBuilder.__boolean_constraint__(condition, True)
                for statement in false_branch:
                    error_dict[errors.execute(statement)] = StateErrorBuilder.__boolean_constraint__(condition, True)
                    error_dict[errors.not_execute(statement)] = StateErrorBuilder.__boolean_constraint__(condition, False)
                break
        # 3. connect source to the target
        target_nodes = set()
        for target_error, constraints in error_dict.items():
            target = self.graph.get_node_of(target_error)
            source.propagate(target, constraints)
            target_nodes.add(target)
        return target_nodes

    def __path_constraints__(self, source: cirflow.CirExecution, target: cirflow.CirExecution):
        """
        TODO implement the algorithm!
        :param source:
        :param target:
        :return:
        """
        constraints = mut.StateConstraints(True)
        return constraints

    def __execution_of__(self, cir_location: cirtree.CirNode):
        c_program = self.graph.get_program()
        function_graph = c_program.get_function_call_graph()
        c_func = function_graph.get_function_of(cir_location)
        cir_statement = cir_location.statement_of()
        if cir_statement is None:
            return None
        else:
            return c_func.get_execution_flow_graph().get_execution_of(cir_statement)

    def __propagate_on_data_flow__(self, source: StateErrorNode):
        """
        :param source:
        :return:
        """
        # 1. get the information flow from source
        cir_location = source.get_error().get_cir_location()
        information_nodes = self.__information_nodes_of__(cir_location)
        use_expression_executions = dict()
        for information_node in information_nodes:
            for information_edge in information_node.get_ou_edges():
                information_edge: cprog.CInformationEdge
                if information_edge.get_flow().get_flow_type() == cprog.CInformationFlowType.du_assign:
                    target_instance = information_edge.get_target().get_instance()
                    target_instance: cirinst.CirInstanceCode
                    use_expression_executions[target_instance.get_cir_source_node()] = \
                        target_instance.get_instance_node().get_source_execution()
        # 2. link the errors through data flow propagation
        source_execution, target_constraints = self.__execution_of__(cir_location), dict()
        for use_expression, target_execution in use_expression_executions.items():
            constraints = self.__path_constraints__(source_execution, target_execution)
            error_dict = dict()
            self.__generate_on_assignment__(use_expression, source.get_error(), error_dict)
            for target_error, _ in error_dict.items():
                target = self.graph.get_node_of(target_error)
                target_constraints[target] = constraints
        # 3. generate the error propagation edges from source
        target_nodes = set()
        for target, constraints in target_constraints.items():
            source.propagate(target, constraints)
            target_nodes.add(target)
        return target_nodes

    def __propagate_inter_statement__(self, source: StateErrorNode):
        # 1. propagation through statements
        cir_location = source.get_error().get_cir_location()
        if cir_location is not None:
            cir_parent = cir_location.get_parent()
            if cir_parent is None:
                target_nodes = set()
            elif cir_parent.cir_type == cirtree.CirType.if_statement or \
                    cir_parent.cir_type == cirtree.CirType.case_statement:
                target_nodes = self.__propagate_on_condition__(source)
            elif cir_parent.is_assign_statement():
                target_nodes = self.__propagate_on_data_flow__(source)
            elif cir_parent.cir_type == cirtree.CirType.argument_list:
                target_nodes = self.__propagate_on_data_flow__(source)
            else:
                target_nodes = set()
        else:
            target_nodes = set()
        # 2. propagation within statement
        targets = set()
        for target in target_nodes:
            target: StateErrorNode
            next_targets = self.__propagate_inner_statement__(target)
            for next_target in next_targets:
                targets.add(next_target)
        return targets

    def __propagate__(self, source: StateErrorNode, distance: int):
        sources = self.__propagate_inner_statement__(source)
        if distance > 0:
            target_nodes = set()
            for source in sources:
                targets = self.__propagate_inter_statement__(source)
                for target in targets:
                    target_nodes.add(target)
            for target in target_nodes:
                self.__propagate__(target, distance - 1)
        return

    def generate_propagation(self, distance: int):
        for source_edge in self.graph.get_faulty_node().get_ou_edges():
            source_edge: StateErrorEdge
            source = source_edge.get_target()
            self.__propagate__(source, distance)
        return


if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\error_graphs"
    propagation_distance = 2
    max_code_length = 64
    print("Testing start from...")
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cprog.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        print("\tStart to proceed", len(mutant_space.mutants), "mutants in", filename)
        output_directory = os.path.join(postfix, filename)
        if not os.path.exists(output_directory):
            os.mkdir(output_directory)
        for mutant in mutant_space.get_mutants():
            if mutant.get_features() is not None:
                print("\t\t==> Proceed mutant", mutant.get_id())
                state_error_graph = StateErrorGraph(mutant.get_features(), propagation_distance)
                output_file = os.path.join(output_directory, filename + "." + str(mutant.id))
                state_error_graph.write_dot_graph(output_file, max_code_length)
                pdf_file = os.path.join(output_directory, filename + "." + str(mutant.id) + ".pdf")
                while not os.path.exists(pdf_file):
                    pass
                os.remove(output_file)
        print("\tProcess", len(mutant_space.get_mutants()), "mutants for", filename)
    print("Testing end for all.")
