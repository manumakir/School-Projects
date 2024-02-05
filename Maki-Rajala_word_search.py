# Manu Maki-Rajala
# This code is related to word embeddings and finding nearest 
# neighbors for a given word in a vector space.

# The project was implemented as part of the machine learning and 
# pattern recognition course.

import random
import numpy as np

def countDistance(word1, word2):
    v1 = np.array(vectors[word1])
    v2 = np.array(vectors[word2])
    return np.linalg.norm(v1 - v2)

def getNearestNeighbors(targetWord, numOfNeighbors):
    if(targetWord not in words):
        return []

    neighbors = {}

    # Collect distances between target word and other words
    for word in words:
        distance = countDistance(word, targetWord)
        neighbors[distance] = word

    # Sort neighbors by distance
    sortedDistances = sorted(neighbors.keys())

    # Return array with 3 neighbors with shortest distances.
    nearestNeighbors = []
    for i in range(numOfNeighbors):
        nearestNeighbors.append(neighbors.get(sortedDistances[i]))

    return nearestNeighbors

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
    input_term = input("\nEnter a word (EXIT to break): ")
    if input_term == 'EXIT':
        break
    else:
        neighbors = getNearestNeighbors(input_term, 3)
        if len(neighbors) > 0:
            print("\n                               Word       Distance\n")
            print("---------------------------------------------------------\n")
            for neighbor in neighbors:
                print("%35s\t\t%f\n" % (neighbor, countDistance(neighbor, input_term)))

        else:
            print("The input word is not known.")
