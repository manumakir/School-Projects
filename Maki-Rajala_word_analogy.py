# Manu Mäki-Rajala
# This code implements a simple analogy search using word embeddings.
# The analogy search attempts to find relationships between the first 
# two words and applies that relationship to the third word. It then 
# finds the nearest neighbors to the resulting vector, providing the 
# best and second-best matches as analogies. The code relies on pre-trained 
# word embeddings to perform these computations.

# The project was implemented as part of the machine learning and 
# pattern recognition course.

import random
import numpy as np

# Return nearest neightbor to vector
def getNearestNeighbor(targetVector, compWords):
    nearestNeighbor = ""
    nearestDistance = 0
    for word in words:
        distance = np.linalg.norm(np.array(vectors[word]) - targetVector)
        if (nearestNeighbor == "" or nearestDistance > distance) and word not in compWords:
            nearestNeighbor = word
            nearestDistance = distance

    return nearestNeighbor

def searchAnalogy(inputWords):

    if len(inputWords) != 3:
        print("\nThere's %d word(s) in your input. I need three (3)." % (len(inputWords)))
        return []
    for word in inputWords:
        if word not in words:
            print("\n The word %s is unknown." % (word))
            return []

    v1 = np.array(vectors[inputWords[0]])
    v2 = np.array(vectors[inputWords[1]])
    v3 = np.array(vectors[inputWords[2]])

    targetVector = v3 + (v2-v1)

    inputWords = np.append(inputWords, getNearestNeighbor(targetVector, inputWords)) # Best mach
    inputWords = np.append(inputWords, getNearestNeighbor(targetVector, inputWords)) # Second best mach

    return inputWords


vocabulary_file = 'word_embeddings.txt'

# Read words
print('Read words...')
with open(vocabulary_file, 'r', encoding='utf-8') as f:
    words = [x.rstrip().split(' ')[0] for x in f.readlines()]

# Read word vectors
print('Read word vectors...')
with open(vocabulary_file, 'r', encoding='utf-8') as f:
    vectors = {}
    for line in f:
        vals = line.rstrip().split(' ')
        vectors[vals[0]] = [float(x) for x in vals[1:]]

vocab_size = len(words)
vocab = {w: idx for idx, w in enumerate(words)}
ivocab = {idx: w for idx, w in enumerate(words)}


# W contains vectors for
vector_dim = len(vectors[ivocab[0]])
W = np.zeros((vocab_size, vector_dim))
for word, v in vectors.items():
    if word == '<unk>':
        continue
    W[vocab[word], :] = v

# Main loop for analogy
while True:
    input_term = input("\nEnter three words using a space as a separator (EXIT to break): ")

    input_terms = input_term.split(' ')
    if input_term == 'EXIT':
        break
    else:
        analogy = searchAnalogy(np.array(input_terms))
        if len(analogy) == 5:
            print("\n The best mach: %s is to %s as %s is to %s\n" % (analogy[0], analogy[1],
                                                       analogy[2],analogy[3],))
            print("\n The second best mach: %s is to %s as %s is to %s\n" % (analogy[0], analogy[1],
                                                                  analogy[2], analogy[4],))