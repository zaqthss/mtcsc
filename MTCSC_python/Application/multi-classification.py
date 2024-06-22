"""
MTCSC multi dimension classification
"""

import torch
from torch.utils.data import DataLoader, Dataset
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.metrics import f1_score
import pandas as pd
import numpy as np


def readData(n, s, name, method, type):
    """
    :param n: the number of dimension
    :param s: the number of case
    :param name: data name
    :param type: train or test
    :return: data and label
    """
    tensors = []
    for i in range(s + 1):
        for j in range(1, n + 1):
            file_name = f'./{name}/{method}_dim{j}.csv'
            # if method != 'train' and type == 'train':
            #     df = pd.read_csv(file_name, header=None)
            # else:
            df = pd.read_csv(file_name)
            data = df.to_numpy()[i:i + 1, ~df.columns.str.contains('Unnamed')]
            if i < len(tensors):
                tensors[i].append(data)
            else:
                tensors.append([data])
    tensors.pop()
    array = np.array(tensors)
    array = np.squeeze(array, axis=2)
    data_tensor = torch.from_numpy(array)

    # file_name = f'./{name}/{type}_label.xlsx'
    # df = pd.read_excel(file_name, header=None)
    if type == 'train':
        file_name = f'./{name}/train_label.csv'
    elif type == 'test':
        file_name = f'./{name}/test_label.csv'
    df = pd.read_csv(file_name, header=None)
    data_array = df.iloc[:, 0].values
    label = torch.from_numpy(data_array)

    return data_tensor, label


class TimeSeriesDataset(Dataset):
    def __init__(self, X, y):
        self.X = X
        self.y = y

    def __len__(self):
        return len(self.y)

    def __getitem__(self, index):
        return self.X[index], self.y[index]


class KNNClassifier:
    def __init__(self, k):
        self.k = k

    def fit(self, X_train, y_train):
        self.X_train = X_train
        self.y_train = y_train

    def predict(self, X_test):
        y_pred = []
        for i in range(len(X_test)):
            distances = torch.norm(self.X_train - X_test[i], dim=(1, 2))  # Calculate Euclidean distance
            indices = distances.argsort()[:self.k]  # Retrieve the index of the top k most recent training samples
            knn_labels = self.y_train[indices]  # Retrieve the corresponding training sample labels based on the index
            y_pred.append(torch.mode(knn_labels).values.item())  # Select the label with the most occurrences among k samples as the prediction label
        return torch.tensor(y_pred)


def f1(n, s, name, method, k):
    train_data, train_label = readData(n, s, name, method, 'train')
    test_data, test_label = readData(n, s, name, 'test', 'test')
    knn = KNNClassifier(k=k)
    knn.fit(train_data, train_label)
    y_pred = knn.predict(test_data)
    f1 = f1_score(test_label, y_pred, average='micro')
    return f1


def avgF1(n, s, name, method, k):
    totalf1 = 0
    for i in range(10):
        train_data, train_label = readData(n, s, name, f'0.1_{i+1}_{method}', 'train')
        test_data, test_label = readData(n, s, name, 'test', 'test')
        knn = KNNClassifier(k=k)
        knn.fit(train_data, train_label)
        y_pred = knn.predict(test_data)
        f1 = f1_score(test_label, y_pred, average='micro')
        # print("f1：", f1)
        totalf1 += f1
    avgf1 = totalf1/10
    return avgf1


# AtrialFibrillation  k=4 0.4
print("Clean:", f1(2, 15, 'AtrialFibrillation', 'train', 4))  # Clean: 0.4000000000000001
print("Dirty:", avgF1(2, 15, 'AtrialFibrillation', 'Dirty', 4))  # Dirty: 0.33333333333333337
print("MTCSC:", avgF1(2, 15, 'AtrialFibrillation', 'MTCSC', 4))  # MTCSC: 0.35333333333333333
print("MTCSC-Uni:", avgF1(2, 15, 'AtrialFibrillation', 'MTCSC-Uni', 4))  # MTCSC-Uni: 0.35333333333333333
print("SCREEN:", avgF1(2, 15, 'AtrialFibrillation', 'SCREEN', 4))  # SCREEN: 0.33333333333333337
print("SpeedAcc:", avgF1(2, 15, 'AtrialFibrillation', 'SpeedAcc', 4))  # SpeedAcc: 0.34
print("LsGreedy:", avgF1(2, 15, 'AtrialFibrillation', 'LsGreedy', 4))  # LsGreedy: 0.24000000000000005
print("EWMA:", avgF1(2, 15, 'AtrialFibrillation', 'EWMA', 4))  # EWMA: 0.24000000000000005
print("HTD:", avgF1(2, 15, 'AtrialFibrillation', 'HTD', 4))  # HTD: 0.33333333333333337


# knn = KNNClassifier(k=4)
# knn.fit(train_data, train_label)
# y_pred = knn.predict(test_data)
# f1 = f1_score(test_label, y_pred, average='micro')
# print("f1：", f1)

# for k in range(1, 10):
#     knn = KNNClassifier(k=k)
#     knn.fit(train_data, train_label)
#     y_pred = knn.predict(test_data)
#     f1 = f1_score(test_label, y_pred, average='micro')
#     print("f1：", f1)
