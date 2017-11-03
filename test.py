# -*- coding: utf-8 -*-
import pandas as pd
import numpy as np

from sklearn.feature_extraction.text import TfidfVectorizer
vectorizer = TfidfVectorizer()

'''
reviews_tr = pd.read_csv("reviews_tr.csv")
text_tr = reviews_tr["text"][:10]
'''

query_terms = ['haha is a dog', 'aa is not a cat', 'fish is not aninaml']
result = vectorizer.fit_transform(query_terms)#.toarray()
queryVector = result[0]
doc_vector = result[1:]
word2idx = vectorizer.vocabulary_

idx2word = {}
for key in word2idx:
    idx2word[word2idx[key]] = key


D_r = [0, 2]
b = result[D_r,:]
s = np.sum(b,axis=0)

sort = np.argsort(s)[::-1]
'''
query_terms = ['haha is a dog', 'aa is not a cat']
query = ' '.join([str(term) for term in query_terms])

'''