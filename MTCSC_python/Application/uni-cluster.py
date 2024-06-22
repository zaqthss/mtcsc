"""
MTCSC one dimension clustering
"""

from sklearn.cluster import KMeans
import pandas as pd
from sklearn.metrics import adjusted_rand_score, rand_score


def process_data(filename):
    df = pd.read_csv(filename, delimiter='\t', header=None)
    x = df.iloc[:, 1:]
    y = df.iloc[:, 0]
    class_num = df.groupby(0).ngroups
    return x, y, class_num


def totalRI(dataname, method):
    totalRI = 0
    for i in range(10):
        seed = str(i+1)
        if method == None:
            trainfilename = "./" + dataname + "/0.1_"+seed+".tsv"
        elif method == 'train':
            if dataname == 'DSR':
                trainfilename = "./" + dataname + "/DiatomSizeReduction_TRAIN.tsv"
            else:
                trainfilename = "./" + dataname + "/" + dataname + "_TRAIN.tsv"
        else:
            trainfilename = "./" + dataname + "/0.1_"+seed+"_" + method + ".tsv"

        x, y, class_num = process_data(trainfilename)
        cluster = KMeans(n_clusters=class_num, random_state=0).fit(x)
        y_pred = cluster.labels_

        # print(rand_score(y, y_pred))
        # totalRI += adjusted_rand_score(y, y_pred)
        totalRI += rand_score(y, y_pred)
    totalRI /= 10
    return totalRI


# DSR
print("DSR:")
print("Clean:", totalRI("DSR", "train"))  # 1.0
print("Dirty:", totalRI("DSR", None))  # 0.6733333333333333
print("MTCSC:", totalRI("DSR", "My1"))  # 1.0
print("SCREEN:", totalRI("DSR", "Screen"))  # 0.9933333333333334
print("SpeedAcc:", totalRI("DSR", "SpeedAcc"))  # 0.9866666666666667
print("LsGreedy:", totalRI("DSR", "Lsgreedy"))  # 0.9491666666666667
print("EWMA:", totalRI("DSR", "Expsmooth"))  # 0.9041666666666666
print("HTD:", totalRI("DSR", "HTD"))  # 0.6733333333333333

# Meat
print("Meat:")
print("Clean:", totalRI("Meat", "train"))  # 0.731638418079096
print("Dirty:", totalRI("Meat", None))  # 0.471412429378531
print("MTCSC:", totalRI("Meat", "My1"))  # 0.7665536723163842
print("SCREEN:", totalRI("Meat", "Screen"))  # 0.5295480225988699
print("SpeedAcc:", totalRI("Meat", "SpeedAcc"))  # 0.5528248587570622
print("LsGreedy:", totalRI("Meat", "Lsgreedy"))  # 0.5006779661016949
print("EWMA:", totalRI("Meat", "Expsmooth"))  # 0.5751412429378531
print("HTD:", totalRI("Meat", "HTD"))  # 0.471412429378531

