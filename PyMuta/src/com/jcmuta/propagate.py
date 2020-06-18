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
    program, and defined as {graph, key; error, necessary_errors, all_errors; in_edges, ou_edges;}
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
                return None     # no possible propagation
        # create the propagation edge from source to target
        edge = StateErrorEdge(self, target, constraints)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge


class StateErrorGraph:
    """
    It defines a causality graph that describes the error propagation in state errors as
    {infection[mutant, error_set, program]; nodes, faulty_node;}
    """
    def __init__(self, infection: mut.StateInfection):
        self.infection = infection
        self.nodes = dict()
        self.faulty_node = None
        # TODO build up the state error graph
        builder = StateErrorBuilder(self)
        builder.generate_infection_layer()
        builder.generate_reaching_links()
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
        if isinstance(flow, cirflow.CirExecutionFlow):          # execution-flow
            flow: cirflow.CirExecutionFlow
            if flow.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:          # cir_condition as true
                cir_condition = flow.get_source().get_statement().get_child(0)
                sym_condition = sym.sym_parser.parse_by_cir_tree(cir_condition)
                constraints.add_constraint(flow.get_source(), sym_condition)
            elif flow.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:       # cir_condition as false
                cir_condition = flow.get_source().get_statement().get_child(0)
                sym_operand = sym.sym_parser.parse_by_cir_tree(cir_condition)
                sym_condition = sym.CSymbolNode(sym.CSymbolType.UnaryExpression,
                                                base.CType(base.CMetaType.BoolType), base.COperator.logic_not)
                sym_condition.add_child(sym_operand)
                constraints.add_constraint(flow.get_source(), sym_condition)
            else:                                                                       # all-true for others
                pass
        else:                                                   # instance_edge
            flow: cirinst.CirInstanceEdge
            if flow.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:          # cir_condition as false
                cir_condition = flow.get_source().get_source_statement().get_child(0)
                sym_condition = sym.sym_parser.parse_by_cir_tree(cir_condition)
                constraints.add_constraint(flow.get_source().get_source_execution(), sym_condition)
            elif flow.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:       # cir_condition as false
                cir_condition = flow.get_source().get_source_statement().get_child(0)
                sym_operand = sym.sym_parser.parse_by_cir_tree(cir_condition)
                sym_condition = sym.CSymbolNode(sym.CSymbolType.UnaryExpression,
                                                base.CType(base.CMetaType.BoolType), base.COperator.logic_not)
                sym_condition.add_child(sym_operand)
                constraints.add_constraint(flow.get_source().get_source_execution(), sym_condition)
            else:                                                                       # all-true for others
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
        if error.error_type == mut.ErrorType.set_bool:                          # set_numb
            parameter = sym.sym_evaluator.__integer__(error.get_operand(1))
            new_error = errors.set_numb(expression, parameter)
        elif error.error_type == mut.ErrorType.chg_bool:                        # chg_numb
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
        else:                                           # no more propagation from unavailable errors
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
            if not expression.get_data_type().is_bool_type():       # expression != 0
                sym_condition = sym.CSymbolNode(sym.CSymbolType.BinaryExpression, base.CType(base.CMetaType.BoolType),
                                                base.COperator.not_equals)
                sym_condition.add_child(sym_operand)
                sym_condition.add_child(sym.CSymbolNode(sym.CSymbolType.Constant, base.CType(base.CMetaType.IntType), 0))
            else:                                                   # expression itself
                sym_condition = sym_operand
        else:
            if not expression.get_data_type().is_bool_type():           # expression == 0
                sym_condition = sym.CSymbolNode(sym.CSymbolType.BinaryExpression, base.CType(base.CMetaType.BoolType),
                                                base.COperator.equal_with)
                sym_condition.add_child(sym_operand)
                sym_condition.add_child(
                    sym.CSymbolNode(sym.CSymbolType.Constant, base.CType(base.CMetaType.IntType), 0))
            else:                                                       # !expression
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
            pass        # impossible influence
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
        if expression.get_child(0) == error.get_cir_location():     # expression := error - operand
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
        else:                                                       # expression := operand - error
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
        if expression.get_child(0) == error.get_cir_location():     # expression := error * another
            other_operand = expression.get_child(1)
        else:                                                       # expression := another * error
            other_operand = expression.get_child(0)
        constraints = StateErrorBuilder.__boolean_constraint__(other_operand, True)
        # 3. determine the constant value hold by another operand or None
        other_sym_operand = sym.sym_evaluator.evaluate(other_operand)
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
        if expression.get_child(0) == error.get_cir_location():         # expression = error / operand
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
        else:                                                           # expression = operand / error
            # 3.1. get the operand, symbolic expression and constant of the other
            cir_operand = expression.get_child(1)
            sym_operand = sym.sym_evaluator.evaluate(sym.sym_parser.parse_by_cir_tree(cir_operand))
            sym_constant = None
            if sym_operand.sym_type == sym.CSymbolType.Constant:
                sym_constant = sym.sym_evaluator.__number__(sym_operand.content)
            constraints = StateErrorBuilder.__boolean_constraint__(cir_operand, True)   # operand != 0
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
                    if sym_constant > 0:
                        new_error = errors.dec_numb(expression)
                    else:
                        new_error = errors.inc_numb(expression)
                else:
                    if sym_constant > 0:
                        new_error = errors.inc_numb(expression)
                    else:
                        new_error = errors.dec_numb(expression)
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
                    if sym_constant > 0:
                        new_error = errors.dec_numb(expression)
                    else:
                        new_error = errors.inc_numb(expression)
                else:
                    if sym_constant > 0:
                        new_error = errors.inc_numb(expression)
                    else:
                        new_error = errors.dec_numb(expression)
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
        if expression.get_child(0) == error.get_cir_location():     # expression := error % operand
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
        else:                                                       # expression := operand % error
            # 2.1. determine the operand, symbolic expression and its operand value
            operand = expression.get_child(1)
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
        :return: TODO implement this method
        """
        # 1. declarations
        errors, new_error, constraints = self.graph.get_error_set(), None, mut.StateConstraints(True)
        # 2. generate new-error based on type of source error
        if expression.get_child(0) == error.get_cir_location():     # expression := error << operand
            pass
        else:                                                       # expression := operand << error
            pass
        # 3. save the result in error-dict
        if new_error is not None:
            error_dict[new_error] = constraints
        return error_dict



if __name__ == "__main__":
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\error_graphs"
    print("Testing start from...")
    for filename in os.listdir(prefix):
        directory = os.path.join(prefix, filename)
        program = cprog.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        with open(os.path.join(postfix, filename + ".err"), 'w') as writer:
            for mutant in mutant_space.get_mutants():
                mutant: mut.Mutant
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
                labels: mut.MutantLabels
                writer.write(str(labels.get_category()) + "\t")
                writer.write("\n")
                error_graph = StateErrorGraph(mutant.get_features())
                for error_node in error_graph.get_nodes():
                    error_node: StateErrorNode
                    writer.write("\t[node]\t" + error_node.get_error().generate_code(True) + "\n")
                    for error_edge in error_node.get_ou_edges():
                        error_edge: StateErrorEdge
                        writer.write("\t[edge]\t" + error_edge.get_target().get_error().generate_code(True))
                        writer.write(" for " + str(error_edge.get_constraints()) + "\n")
                writer.write("\n")
        print("\tProcess", len(mutant_space.get_mutants()), "mutants for", filename)
    print("Testing end for all.")
