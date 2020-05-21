"""
It defines the model and algorithm to generate state errors around the propagation path.
"""


import os
import src.com.jcparse.base as base
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst
import src.com.jcparse.cprogram as cpro
import src.com.jcmuta.operator as mop
import src.com.jcmuta.mutation as mut


class StateErrorExtension:
    @staticmethod
    def __is_boolean__(location: cirtree.CirNode):
        """
        :param location:
        :return: True if the expression is taken as a condition or boolean expression
        """
        if location.is_expression():
            parent = location.get_parent()
            parent: cirtree.CirNode
            if parent.get_cir_type() == cirtree.CirType.if_statement or \
                    parent.get_cir_type() == cirtree.CirType.case_statement:
                return True
            else:
                data_type = location.get_data_type()
                data_type: base.CType
                return data_type.is_bool_type()
        else:
            return False

    @staticmethod
    def __is_numeric__(location: cirtree.CirNode):
        if location.is_expression():
            data_type = location.get_data_type()
            data_type: base.CType
            return data_type.is_integer_type() or data_type.is_real_type()
        else:
            return False

    @staticmethod
    def __is_address__(location: cirtree.CirNode):
        if location.is_expression():
            data_type = location.get_data_type()
            data_type: base.CType
            return data_type.is_address_type()
        else:
            return False

    @staticmethod
    def __extend_at_execute_for__(state_error: mut.StateError, error_set: set):
        statement = state_error.get_operand(0)
        times = state_error.get_operand(1)
        statement: cirtree.CirNode
        times: int
        state_errors = state_error.get_error_set()
        if times == 1:
            StateErrorExtension.__extend_at__(state_errors.execute(statement))
        elif times > 1:
            error_set.add(state_error)
            StateErrorExtension.__extend_at__(state_errors.execute_for(statement, times - 1))
        else:
            pass
        return

    @staticmethod
    def __extend_at__(state_error: mut.StateError, error_set: set):
        state_errors = state_error.get_error_set()
        state_errors: mut.StateErrors
        if state_error.get_error_type() == mut.ErrorType.execute_for:
            StateErrorExtension.__extend_at_execute_for__(state_error, error_set)
        return











