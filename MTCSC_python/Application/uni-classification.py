"""
MTCSC one dimension classification
"""

from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import f1_score
import pandas as pd
from sklearn.metrics import accuracy_score


def process_data(filename):
    df = pd.read_csv(filename, delimiter='\t', header=None)
    x = df.iloc[:, 1:]
    y = df.iloc[:, 0]
    return x, y


def totalKNN(K, dataname, method):
    totalF1 = 0
    for i in range(10):
        seed = str(i+1)
        if method == None:
            trainfilename = "./" + dataname + "/0.1_"+seed+".tsv"
        else:
            trainfilename = "./" + dataname + "/0.1_"+seed+"_" + method + ".tsv"
        train_x, train_y = process_data(trainfilename)

        testfilename = "./" + dataname + "/" + dataname + "_TEST.tsv"
        test_x, test_y = process_data(testfilename)
        knn = KNeighborsClassifier(n_neighbors=K)
        knn.fit(train_x, train_y)
        pre_test = knn.predict(test_x)
        totalF1 += f1_score(test_y, pre_test, average='micro')
    totalF1 /= 10
    return totalF1


# 0.8171428571428572 ArrowHead
train_x, train_y = process_data(r"./ArrowHead/ArrowHead_TRAIN.tsv")  # 0.8171428571428572
test_x, test_y = process_data(r"./ArrowHead/ArrowHead_TEST.tsv")
knn = KNeighborsClassifier(n_neighbors=2)
knn.fit(train_x, train_y)
pre_test = knn.predict(test_x)
print("ArrowHead  F1-score：")
print("Clean：", f1_score(test_y, pre_test, average='micro'))
print("Dirty：", totalKNN(2, "ArrowHead", None))  # 0.5514285714285715
print("MTCSC：", totalKNN(2, "ArrowHead", "My1"))  # 0.8194285714285714
print("SCREEN：", totalKNN(2, "ArrowHead", "Screen"))  # 0.5714285714285714
print("SpeedAcc：", totalKNN(2, "ArrowHead", "SpeedAcc"))  # 0.6257142857142857
print("LsGreedy：", totalKNN(2, "ArrowHead", "Lsgreedy"))  # 0.786857142857143
print("EWMA：", totalKNN(2, "ArrowHead", "Expsmooth"))  # 0.42457142857142854
print("HTD：", totalKNN(2, "ArrowHead", "HTD"))  # 0.5514285714285715


# 0.6666666666666666 Car
train_x, train_y = process_data(r"./Car/Car_TRAIN.tsv")  # 0.6666666666666666
test_x, test_y = process_data(r"./Car/Car_TEST.tsv")
knn = KNeighborsClassifier(n_neighbors=4)
knn.fit(train_x, train_y)
pre_test = knn.predict(test_x)
print("Car  F1-score：")
print("Clean：", f1_score(test_y, pre_test, average='micro'))
print("Dirty：", totalKNN(4, "Car", None))  # 0.5733333333333333
print("MTCSC：", totalKNN(4, "Car", "My1"))  # 0.6666666666666667
print("SCREEN：", totalKNN(4, "Car", "Screen"))  # 0.66
print("SpeedAcc：", totalKNN(4, "Car", "SpeedAcc"))  # 0.6566666666666667
print("LsGreedy：", totalKNN(4, "Car", "Lsgreedy"))  # 0.6516666666666667
print("EWMA：", totalKNN(4, "Car", "Expsmooth"))  # 0.3766666666666667
print("HTD：", totalKNN(4, "Car", "HTD"))  # 0.5733333333333333






