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
    It defines the propagation relationship between state error between different locations and error types as
    {source, target, state_constraints}
    """
    def __init__(self, source, target, state_constraints: mut.StateConstraints):
        """
        :param source:
        :param target:
        :param state_constraints: the conditions that need to be satisfied for the propagation to occur
        """
        self.source = source
        self.target = target
        self.state_constraints = state_constraints
        return

    def get_source(self):
        """
        :return: the state error in source location that is passed to another
        """
        self.source: StateErrorNode
        return self.source

    def get_target(self):
        """
        :return: the state error in target location that passed from another
        """
        self.target: StateErrorNode
        return self.target

    def get_state_constraints(self):
        """
        :return: the conditions that need to be satisfied for the propagation to occur
        """
        return self.state_constraints


class StateErrorNode:
    """
    It defines a set of state errors in some location of program, as the cause or effect of other groups in
    the propagation process, described as {graph, key; error, errors[key]; in_edges, ou_edges;}
    """

    def __key__(self):
        """
        :return: generate the string key of the node w.r.t. the state error(s) it represents.
        """
        words = list()
        for state_error in self.state_errors:
            words.append(str(state_error))
        words.sort()
        return str(words)

    def __init__(self, graph, state_error: mut.StateError):
        """
        :param graph: graph where this node is created
        :param state_error: the source error that is used to generate this error.
        """
        self.graph = graph
        self.state_error = state_error
        self.state_errors = state_error.get_error_set().extend(state_error)
        self.key = self.__key__()
        self.in_edges = list()
        self.ou_edges = list()
        return

    def get_graph(self):
        self.graph: StateErrorGraph
        return self.graph

    def get_key(self):
        """
        :return: unique key of this node in the graph
        """
        return self.key

    def get_state_error(self):
        """
        :return: source representative error generated the others in this node
        """
        return self.state_error

    def get_state_errors(self):
        """
        :return: set of state errors in this node
        """
        return self.state_errors

    def is_virtual(self):
        """
        :return: whether the number of state errors in this node is virtual
        """
        return len(self.state_errors) == 0

    def get_in_edges(self):
        """
        :return: error propagation from this node to others
        """
        return self.in_edges

    def get_ou_edges(self):
        """
        :return: error propagation to this node from others
        """
        return self.ou_edges

    def link_to(self, target, state_constraints: mut.StateConstraints):
        """
        :param target:
        :param state_constraints:
        :return: the edge that connects this node to the target or None if constraints is unsatisfied.
        """
        target: StateErrorNode
        if self == target:                                      # to avoid self-self-circle
            return None
        else:
            for edge in self.ou_edges:
                edge: StateErrorEdge
                if edge.get_target() == target:                 # to avoid a duplicated propagation
                    return edge
        sym_condition = state_constraints.sym_condition()
        if sym_condition.sym_type == sym.CSymbolType.Constant:
            constant = sym.sym_evaluator.__boolean__(sym_condition.content)
            if constant:
                state_constraints = mut.StateConstraints(True)  # all-true conjunction
            else:
                return None                                     # impossible propagation to target
        edge = StateErrorEdge(self, target, state_constraints)
        self.ou_edges.append(edge)
        target.in_edges.append(edge)
        return edge                                             # create new edge from source to target


class StateErrorGraph:
    """
    Describe the process of state error propagation.
    """
    def __init__(self, state_infection: mut.StateInfection):
        # initialization
        self.__infection__ = state_infection
        self.nodes = dict()
        self.__faulty_point__ = None
        self.__builder__ = StateErrorBuilder(self)
        # building phases
        self.__builder__.generate_infection_edges()
        self.__builder__.generate_coverage_paths()
        return

    def get_state_infection(self):
        """
        :return: the state infection from which the graph are generated.
        """
        return self.__infection__

    def get_mutant(self):
        """
        :return: mutant that the state error propagation graph describes
        """
        return self.__infection__.get_mutant()

    def get_faulty_point(self):
        """
        :return: node that represents execute(faulty_statement) for covering faulty statement
        """
        self.__faulty_point__: StateErrorNode
        return self.__faulty_point__

    def get_node(self, state_error: mut.StateError):
        """
        :param state_error:
        :return: get the existing node w.r.t. the source error in specified location or create a new one
        """
        new_node = StateErrorNode(self, state_error)
        new_key = new_node.get_key()
        if new_key not in self.nodes:
            self.nodes[new_key] = new_node
        node = self.nodes[new_key]
        node: StateErrorNode
        return node

    def get_keys(self):
        """
        :return: the set of keys corresponding to some nodes in this graph
        """
        return self.nodes.keys()

    def get_nodes(self):
        """
        :return: the set of nodes created in this graph
        """
        return self.nodes.values()


class StateErrorBuilder:
    """
    To build up the state error propagation
    """

    def __init__(self, graph: StateErrorGraph):
        self.graph = graph
        self.infection = self.graph.get_state_infection()
        self.state_errors = self.infection.get_mutant().get_mutant_space().state_errors
        self.program = self.infection.get_mutant().get_mutant_space().get_program()
        return

    # infection creation

    def __done__(self):
        return self.graph

    def generate_infection_edges(self):
        """
        :return: create the execute(faulty_point) and infection edges
        """
        faulty_statement = self.infection.get_faulty_execution().get_statement()
        head_error = self.state_errors.execute(faulty_statement)
        self.graph.__faulty_point__ = self.graph.get_node(head_error)
        for state_error, state_constraints in self.infection.error_infections.items():
            state_error: mut.StateError
            state_constraints: mut.StateConstraints
            target = self.graph.get_node(state_error)
            self.graph.__faulty_point__.link_to(target, state_constraints)
        return

    # coverage path generation

    def __constraints_of_instance_edge__(self, instance_edge: cirinst.CirInstanceEdge):
        """
        :param instance_edge:
        :return: state constraints for instance edge
        """
        self.__done__()
        if instance_edge.get_flow_type() == cirflow.CirExecutionFlowType.true_flow:
            execution = instance_edge.get_source().get_source_execution()
            cir_condition = instance_edge.get_source().get_source_statement().get_child(0)
            sym_condition = sym.sym_parser.parse_by_cir_tree(cir_condition)
            state_constraint = mut.StateConstraint()
            state_constraint.execution = execution
            state_constraint.condition = sym_condition
            state_constraints = mut.StateConstraints(True)
            state_constraints.constraints.append(state_constraint)
            return state_constraints
        elif instance_edge.get_flow_type() == cirflow.CirExecutionFlowType.false_flow:
            execution = instance_edge.get_source().get_source_execution()
            cir_condition = instance_edge.get_source().get_source_statement().get_child(0)
            sym_operand = sym.sym_parser.parse_by_cir_tree(cir_condition)
            sym_condition = sym.CSymbolNode(sym.CSymbolType.UnaryExpression,
                                            sym_operand.data_type, base.COperator.logic_not)
            sym_condition.add_child(sym_operand)
            state_constraint = mut.StateConstraint()
            state_constraint.execution = execution
            state_constraint.condition = sym_condition
            state_constraints = mut.StateConstraints(True)
            state_constraints.constraints.append(state_constraint)
            return state_constraints
        else:
            return mut.StateConstraints(True)   # always allowed for call or return

    def __generate_coverage_path__(self, dominance_path: cprog.CDominancePath):
        """
        :param dominance_path:
        :return: generate the cause-effect links based on dominance path from entry to faulty-point
        """
        source = self.graph.get_node(self.state_errors.execute(dominance_path.get_beg_node().get_source_statement()))
        state_constraints = mut.StateConstraints(True)
        for instance_edge in dominance_path.get_prev_edges():
            instance_edge: cirinst.CirInstanceEdge
            target = self.graph.get_node(self.state_errors.execute(instance_edge.get_source().get_source_statement()))
            source.link_to(target, state_constraints)
            # update the loop invariants of source and state_constraints
            source = target
            state_constraints = self.__constraints_of_instance_edge__(instance_edge)
        target = self.graph.get_node(self.state_errors.execute(dominance_path.get_mid_node().get_source_statement()))
        source.link_to(target, state_constraints)
        return

    def generate_coverage_paths(self):
        """
        :return: generate the dominance path from entry to the faulty statement
        """
        # 1. find the instance nodes w.r.t. the faulty execution
        instance_nodes = self.program.get_instance_graph().get_nodes_of(self.infection.get_faulty_execution())
        # 2. create the dominance path from each instance node for coverage
        for instance_node in instance_nodes:
            instance_node: cirinst.CirInstanceNode
            dominance_path = cprog.CDominancePath(instance_node)
            self.__generate_coverage_path__(dominance_path)
            # self.__generate_terminate_path__(dominance_path)
        return


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
                    writer.write("\t[node]\t" + error_node.get_state_error().generate_code(True) + "\n")
                    for error_edge in error_node.get_ou_edges():
                        error_edge: StateErrorEdge
                        writer.write("\t[edge]\t" + error_edge.get_target().get_state_error().generate_code(True))
                        writer.write(" for " + str(error_edge.get_state_constraints()) + "\n")
                writer.write("\n")
        print("\tProcess", len(mutant_space.get_mutants()), "mutants for", filename)
    print("Testing end for all.")
