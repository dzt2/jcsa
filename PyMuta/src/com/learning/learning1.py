import os
import numpy as np
from typing import TextIO
import src.com.jcparse.astree as astree
import src.com.jcparse.cirtree as cirtree
import src.com.jcparse.cirflow as cirflow
import src.com.jcparse.cirinst as cirinst
import src.com.jcparse.cprogram as cpro
import src.com.jcmuta.mutation as mut
import src.com.jcmuta.symbol as sym
import sklearn.metrics as metrics
import sklearn.tree as stree
import sklearn.ensemble as ensemble
import sklearn.neural_network as nnet


class MutantKey:
    """
    program_name, mutant_id
    """
    def __init__(self, name: str, mid: int):
        self.name = name
        self.mid = mid
        return

    def get_name(self):
        return self.name

    def get_mid(self):
        return self.mid

    @staticmethod
    def __generate_code__(mutant: mut.Mutant, output_file: str):
        with open(output_file, 'w') as writer:
            comment = "/**\n" \
                      " * Mutant ID: %d\n" \
                      " * Class: %s\n" \
                      " * Operator: %s\n" \
                      " * Location: \"%s\" at line %d\n" \
                      " * Parameter: %s\n" \
                      " */\n\n"
            mid = mutant.id
            mclass = str(mutant.get_mutation().get_mutation_class())
            moperator = str(mutant.get_mutation().get_mutation_operator())
            location = mutant.get_mutation().get_location()
            location: astree.AstNode
            location_code = location.get_code(True)
            location_line = location.get_beg_line() + 9
            parameter = "None"
            if mutant.get_mutation().has_parameter():
                parameter = str(mutant.get_mutation().get_parameter())
            comment = comment % (mid, mclass, moperator, location_code, location_line, parameter)
            writer.write(comment)
            beg_index = location.beg_index
            end_index = location.end_index
            source_code = location.get_tree().get_source_code()
            source_code: astree.SourceCode
            writer.write(source_code.text[0: beg_index])
            before_line = True
            for k in range(beg_index, len(source_code.text)):
                char = source_code.text[k]
                if before_line and char == "\n":
                    writer.write("\t// SEEDED LINE")
                    before_line = False
                writer.write(char)
            if before_line:
                writer.write("\t// SEEDED LINE\n")
        return

    def interpret(self, mutant_space: mut.MutantSpace, output_file: str):
        """
        :param output_file:
        :param mutant_space:
        :return: write the mutation code to specified output file or False if no such mutant defined
        """
        if mutant_space.has_mutant(self.mid):
            mutant = mutant_space.get_mutant(self.mid)
            MutantKey.__generate_code__(mutant, output_file)
            return True
        else:
            return False


class MutantDataFrame:
    def __init__(self):
        self.mutant_id_list = list()
        self.labels = list()
        self.features = list()
        return

    def append(self, csv_file: str):
        """
        add more data items in the data-frame
        :param csv_file:
        :return:
        """
        with open(csv_file, 'r') as reader:
            first = True
            for line in reader:
                if first:
                    first = False
                else:
                    line = line.strip()
                    if len(line) > 0:
                        items = line.split(',')
                        name = items[0].strip()
                        mid = int(items[1].strip())
                        label = int(items[2].strip())
                        feature = list()
                        for k in range(3, len(items)):
                            feature.append(float(items[k].strip()))
                        self.mutant_id_list.append(MutantKey(name, mid))
                        self.labels.append(label)
                        self.features.append(feature)
        return

    def train_for(self, classifier):
        classifier.fit(self.features, self.labels)
        p_labels = classifier.predict(self.features)
        print("Training for classifier of", type(classifier))
        print(metrics.classification_report(self.labels, p_labels, target_names=["Killed", "Non-killed"]))
        return classifier

    def generate_predict_table(self, classifier):
        p_labels = classifier.predict(self.features)
        new_data_frame = MutantDataFrame()
        for k in range(0, len(p_labels)):
            if p_labels[k] != 0:
                new_data_frame.mutant_id_list.append(self.mutant_id_list[k])
                new_data_frame.labels.append(p_labels[k])
                new_data_frame.features.append(self.features[k])
        return new_data_frame

    def __len__(self):
        return len(self.mutant_id_list)


if __name__ == "__main__":
    data_frame = MutantDataFrame()
    prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\data"
    p_prefix = "C:\\Users\\yukimula\\git\\jcsa\\JCMuta\\results\\pdata"
    postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\training_files"
    p_postfix = "C:\\Users\\yukimula\\git\\jcsa\\PyMuta\\output\\predict_files"
    for filename in os.listdir(prefix):
        csv_file = os.path.join(postfix, filename + ".csv")
        data_frame.append(csv_file)
        print("\tProcessing samples for", filename)
    print("Load", len(data_frame), "mutant samples for training.")
    classifier = data_frame.train_for(stree.DecisionTreeClassifier())

    for filename in os.listdir(p_prefix):
        directory = os.path.join(p_prefix, filename)
        program = cpro.CProgram(directory)
        mutant_space = mut.MutantSpace(program)
        new_data_frame = MutantDataFrame()
        csv_file = os.path.join(p_postfix, filename + ".csv")
        new_data_frame.append(csv_file)
        stubborn_data_frame = new_data_frame.generate_predict_table(classifier)
        print("\tIdentify", len(stubborn_data_frame.mutant_id_list), "stubborn mutants in",
              len(new_data_frame.mutant_id_list), "for", filename)
        output_directory = os.path.join(p_postfix, filename)
        if not os.path.exists(output_directory):
            os.mkdir(output_directory)
        for mutant_key in stubborn_data_frame.mutant_id_list:
            mutant_key: MutantKey
            output_file = os.path.join(output_directory, filename + "." + str(mutant_key.get_mid()) + ".mc")
            mutant_key.interpret(mutant_space, output_file)
    print("Prediction ends for all.")


