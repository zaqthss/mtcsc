"""
MTCSC multi dimension cluster
"""

import torch
from sklearn.metrics import adjusted_rand_score, rand_score
import pandas as pd
import numpy as np
import random


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


class KMeans:
    def __init__(self, n_clusters, max_iter=100, tol=1e-4, device='cpu'):
        self.n_clusters = n_clusters
        self.max_iter = max_iter
        self.tol = tol
        self.device = device

    def fit(self, X, seed=42):
        X = X.to(self.device)
        num_samples, sequence_length, num_features = X.shape

        torch.manual_seed(seed)
        torch.cuda.manual_seed(seed)
        random.seed(seed)
        np.random.seed(seed)

        random_indices = torch.randperm(num_samples)[:self.n_clusters]
        centroids = X[random_indices]

        for i in range(self.max_iter):
            distances = torch.stack([
                torch.norm(X - centroid, dim=(1, 2)) for centroid in centroids
            ], dim=1)

            labels = torch.argmin(distances, dim=1)

            new_centroids = torch.stack([X[labels == j].mean(dim=0) for j in range(self.n_clusters)])

            if torch.norm(new_centroids - centroids) < self.tol:
                break

            centroids = new_centroids

        self.centroids = centroids
        self.labels = labels
        return self

    def predict(self, X):
        X = X.to(self.device)

        distances = torch.stack([
            torch.norm(X - centroid, dim=(1, 2)) for centroid in self.centroids
        ], dim=1)

        labels = torch.argmin(distances, dim=1)
        return labels


def RI(n, s, name, method, k):
    train_data, train_label = readData(n, s, name, method, 'train')
    kmeans = KMeans(n_clusters=k, max_iter=100, tol=1e-4, device='cpu').fit(train_data)
    pred_labels = kmeans.labels
    RI = rand_score(train_label, pred_labels)
    return RI


def avgRI(n, s, name, method, k):
    totalRI = 0
    for i in range(10):
        train_data, train_label = readData(n, s, name, f'0.1_{i + 1}_{method}', 'train')
        kmeans = KMeans(n_clusters=k, max_iter=100, tol=1e-4, device='cpu').fit(train_data)
        pred_labels = kmeans.labels
        RI = rand_score(train_label, pred_labels)
        totalRI += RI
    avgri = totalRI / 10
    return avgri


# StandWalkJump
print("Clean:", RI(4, 12, 'StandWalkJump', 'train', 3))  # Clean: 0.5
print("Dirty:", avgRI(4, 12, 'StandWalkJump', 'Dirty', 3))  # Dirty: 0.40909090909090906
print("MTCSC:", avgRI(4, 12, 'StandWalkJump', 'MTCSC', 3))  # MTCSC: 0.5
print("MTCSC-Uni:", avgRI(4, 12, 'StandWalkJump', 'MTCSC-Uni', 3))  # MTCSC-Uni: 0.5
print("SCREEN:", avgRI(4, 12, 'StandWalkJump', 'SCREEN', 3))  # SCREEN: 0.47575757575757577
print("SpeedAcc:", avgRI(4, 12, 'StandWalkJump', 'SpeedAcc', 3))  # SpeedAcc: 0.47575757575757577
print("LsGreedy:", avgRI(4, 12, 'StandWalkJump', 'LsGreedy', 3))  # LsGreedy: 0.4878787878787879
print("EWMA:", avgRI(4, 12, 'StandWalkJump', 'EWMA', 3))  # EWMA: 0.40909090909090906
print("HTD:", avgRI(4, 12, 'StandWalkJump', 'HTD', 3))  # HTD: 0.40909090909090906


# 示例数据（多维时序数据）
# X = torch.rand(100, 20, 10)  # 100个样本，每个样本是20x10的矩阵
# y = torch.randint(0, 2, (100,))
#
# # 运行K-means聚类
# kmeans = KMeans(n_clusters=3, max_iter=100, tol=1e-4, device='cpu')
# kmeans.fit(X)
# labels = kmeans.predict(X)
# RI = rand_score(y, labels)
# print("RI:", RI)
