import os
import src.cmodel.ccode as ccode
import src.cmodel.mutant as cmutant
import src.cmodel.program as cprogram
import src.mining.encode as encode
import sklearn.tree as tree
import sklearn.metrics as metrics
import numpy as np
import scipy.sparse as sparse
import sklearn.ensemble as ensemble


class SparseClassifier:
    def __init__(self, constructor):
        self.classifier = constructor()
        return

    def __features_labels__(self, data_frame: encode.MutantDataFrame):
        row_index, labels, rows, columns, data_list = 0, list(), list(), list(), list()
        for mutant in data_frame.program.mutant_space.get_mutants():
            mutant: cmutant.Mutant
            feature_vector = mutant.feature_vector
            label = mutant.label
            labels.append(label)
            for column in feature_vector:
                rows.append(row_index)
                columns.append(column)
                data_list.append(1)
            row_index += 1
        rows = np.array(rows)
        columns = np.array(columns)
        data = np.array(data_list)
        self.features = sparse.csr_matrix((data, (rows, columns)), shape=(
            len(data_frame.program.mutant_space.mutants), len(data_frame.words)))
        self.labels = np.array(labels)
        return

    def fit(self, data_frame: encode.MutantDataFrame):
        self.__features_labels__(data_frame)
        self.classifier.fit(self.features, self.labels)
        p_labels = self.classifier.predict(self.features)
        print(metrics.classification_report(self.labels, p_labels))
        return


def get_decision_tree():
    return tree.DecisionTreeClassifier()


def get_random_forest_classifier():
    return ensemble.RandomForestClassifier()


def classification(encoding_class, constructor):
    data_directory = 'C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data'
    for file_name in os.listdir(data_directory):
        program_directory = os.path.join(data_directory, file_name)
        program = cprogram.Program(program_directory)
        data_frame = encode.MutantDataFrame(program, encoding_class())
        print('Load', len(data_frame.program.mutant_space.mutants), 'mutants from', file_name)
        classifier = SparseClassifier(constructor)
        classifier.fit(data_frame)
    return


if __name__ == '__main__':
    classification(encode.StateInfectionEncode, get_decision_tree)
