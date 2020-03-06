import os
import src.cmodel.ccode as ccode
import src.cmodel.mutant as cmutant


class Program:
    """
    name, source_code, ast_tree, cir_tree, mutant_space
    """
    def __init__(self, dir_path: str):
        self.name = os.path.basename(dir_path)
        code_file = os.path.join(dir_path, self.name + '.c')
        ast_file = os.path.join(dir_path, self.name + '.ast')
        cir_file = os.path.join(dir_path, self.name + '.cir')
        mut_file = os.path.join(dir_path, self.name + '.mut')
        lab_file = os.path.join(dir_path, self.name + '.lab')
        sem_file = os.path.join(dir_path, self.name + '.sem')
        self.source_code = ccode.SourceCode(self, code_file)
        self.ast_tree = ccode.AstTree(self, ast_file)
        self.cir_tree = ccode.CirTree(self.ast_tree, cir_file)
        self.mutant_space = cmutant.MutantSpace(self.ast_tree, self.cir_tree, mut_file, lab_file, sem_file)
        return


def test_all():
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\data'
    for file_name in os.listdir(data_directory):
        file_directory = os.path.join(data_directory, file_name)
        program = Program(file_directory)
        print('Testing on', program.name)
    return


if __name__ == '__main__':
    test_all()
