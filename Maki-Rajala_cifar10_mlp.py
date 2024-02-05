# Manu Mäki-Rajala
# This code is a simple neural network implementation
# Which was done as part of a machine learning and pattern 
# recognition course.

import pickle
import matplotlib.pyplot as plt
import numpy as np
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
import keras

# Unpickle function from the "cifar10-illustrate.py"
def unpickle(file):
    with open(file, 'rb') as f:
        dict = pickle.load(f, encoding="latin1")
    return dict

# Download data
datadict1 = unpickle('cifar-10-batches-py/data_batch_1')
datadict2 = unpickle('cifar-10-batches-py/data_batch_2')
datadict3 = unpickle('cifar-10-batches-py/data_batch_3')
datadict4 = unpickle('cifar-10-batches-py/data_batch_4')
datadict5 = unpickle('cifar-10-batches-py/data_batch_5')

testdict = unpickle('cifar-10-batches-py/test_batch')
labeldict = unpickle('cifar-10-batches-py/batches.meta')
# Create neural network with dense connections
model = Sequential()
model.add(Dense(50, input_dim=3072, activation='sigmoid')) # First here was just 5 neurons
model.add(Dense(10, activation='sigmoid'))

# Parameters (You can see examples for how I played with parameters from the end of this file)
learningR = 0.05 # Learning rate
numOfEpochs = 50

X1 = np.array(datadict1["data"])
X2 = np.array(datadict2["data"])
X3 = np.array(datadict3["data"])
X4 = np.array(datadict4["data"])
X5 = np.array(datadict5["data"])
# Normalized data for better accuracy
X = np.vstack((X1, X2, X3, X4, X5))/255.0


Y1 = np.array(datadict1["labels"])
Y2 = np.array(datadict2["labels"])
Y3 = np.array(datadict3["labels"])
Y4 = np.array(datadict4["labels"])
Y5 = np.array(datadict5["labels"])
Y_not_categorial = np.hstack((Y1, Y2, Y3, Y4, Y5))
Y = to_categorical(Y_not_categorial, 10)

# Train model with training data
model.compile(keras.optimizers.Adam(lr=learningR),
              loss='categorical_crossentropy', metrics=['accuracy'])
trainHist = model.fit(X, Y, epochs=numOfEpochs, verbose=1)

# Plot loss
plt.plot(trainHist.history['loss'])
plt.ylabel('Loss')
plt.xlabel('Epoch')
plt.legend(['training'], loc='upper right')
plt.show()

# Test model with Cifar-10 samples
# Normalized test data
# Test model with Cifar-10 samples
X_test = np.array(testdict["data"])
# Normalized test data
X_test = np.array(testdict["data"])/225.0
Y_test = to_categorical(testdict["labels"], 10)

# Calculate accuracy
testLoss, testAccuracy = model.evaluate(X_test, Y_test, verbose=2)
trainLoss, trainAccuracy = model.evaluate(X_test, Y_test, verbose=2)

print(f"Accuracy for test data: {testAccuracy * 100:.2f}%")
print()
print(f"Accuracy for train data: {trainAccuracy * 100:.2f}%")

# Playing with parameters:
# With 5 hidden layer neurons, learning rate: 0.01, epochs: 50 -> accuracy: 35,63%
# With 5 hidden layer neurons, learning rate: 0.01, epochs: 80 -> accuracy: 35,74%
# With 5 hidden layer neurons, learning rate: 0.001, epochs: 60 -> accuracy: 35,73%
# With 5 hidden layer neurons, learning rate: 0.005, epochs: 20 -> accuracy: 33,92%
# With 5 hidden layer neurons, learning rate: 0.005, epochs: 80 -> accuracy: 32,68%
# With 5 hidden layer neurons, learning rate: 0.01, epochs: 50 -> accuracy: 35,74%
# With 5 hidden layer neurons, learning rate: 0.5, epochs: 50 -> accuracy: 33,04%
# With 5 hidden layer neurons, learning rate: 0.05, epochs: 50 -> accuracy: 36,05%
# Then I started to add neurons
# With 10 hidden layer neurons, learning rate: 0.05, epochs: 50 -> accuracy: 40,25% (good enough but I wanted bit better)
# With 50 hidden layer neurons, learning rate: 0.05, epochs: 50 -> accuracy: 45,32% (good enough but I wanted bit better)
# With 100 hidden layer neurons, learning rate: 0.05, epochs: 50 -> accuracy: 45,73%
    # The runtime of the program increased significantly compared to the marginal improvement in accuracy,
    # so I decided to use these parameters:
# Number of hidden layer neurons: 50, lr:0.05 & ephocs: 50