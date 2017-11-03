from googleapiclient.discovery import build
from RocchioAlgorithm import RocchioAlgorithm
import numpy as np

from sklearn.feature_extraction.text import TfidfVectorizer


class InformationRetrieval(object):
    def __init__(self, api_key, cse_id, initial_query_terms, wanted_precision):
        self.api_key = api_key
        self.cse_id = cse_id
        self.query_terms = initial_query_terms
        self.wanted_precision = wanted_precision

        self.query_results = []
        self.current_precision = 0


    def main_loops(self):
        """
        step 1: use google search API to get the top 10 results from the query
        step 2: extract 'snippet', 'link' and 'title' of each result and encapsulate
                them into a class called SimpleSearchResult
        step 3: if search result numbers < 10, then stop search and exit program;
                else do relevance feedback with user
        step 4: make judgement about whether augment query is needed:
                if current_precision > wanted_precision, then finish program and exit
                else if current_precision == 0, stop program
                else we need augment query, do step 5
        step 5: query modification and augmentation, then back to step 1
        
        """
        while True:
            # print parameters, including API key, engine id, query, precision
            # then search according to the query
            self.print_status().search()
            if self.need_evaluate():
                # only evaluate after search results == 10
                self.evaluate_search()  # relevance feedback
                if self.need_augment():
                    # only augment when precision is between 0 and desired precision
                    self.augment_query()
                else:
                    break
            else:
                break

    def print_status(self):
        print('Parameters:')
        print('Client key  = {0}'.format(self.api_key))
        print('Engine key  = {0}'.format(self.cse_id))
        print('Query       = {0}'.format(' '.join(self.query_terms)))
        print('Precision   = {0}'.format(self.wanted_precision))
        return self

    def search(self):
        """
        search according to the query
        """
        # begin query
        query = ' '.join([str(term) for term in self.query_terms])
        service = build("customsearch", "v1", developerKey=self.api_key)
        res = service.cse().list(
            q=query,
            cx=self.cse_id,
        ).execute()

        # remove redundant information
        origin_items = res['items']
        simple_items = []
        for origin_item in origin_items:
            simple_items.append(SimpleSearchResult(origin_item))
        self.query_results = simple_items
        return self

    def need_evaluate(self):
        """
        if search result numbers < 10, then stop search
        :return:
        """
        if len(self.query_results) < 10:
            return False
        else:
            return True

    def evaluate_search(self):
        """
        relevance feedback
        """
        print('Google Search Results:')
        print('======================')
        positive_feedback = 0
        for i, item in enumerate(self.query_results):
            print('Result {0}'.format(i+1))
            print('[')
            print('  url: {0}'.format(item.url))
            print('  title: {0}'.format(item.title))
            print('  summary: {0}'.format(item.description))
            print(']')
            print('')
            evaluation = input('Relevant (Y/N)? ')
            # mark all relevant item to positive evaluation
            if evaluation == 'Y' or evaluation == 'y':
                item.evaluation = True
                positive_feedback += 1
            else:
                item.evaluation = False
            print('')
        print("======================")
        print("FEEDBACK SUMMARY")
        print("Query {0}".format(' '.join(self.query_terms)))
        self.current_precision = positive_feedback/1.0/len(self.query_results)
        print("Precision: {0}".format(self.current_precision))
        pass

    def need_augment(self):
        """
        only the current precision can be augmented, then continue to augment
        """
        if self.current_precision>=self.wanted_precision:
            print('Desired precision reached, done')
            return False
        elif self.current_precision == 0:
            print('Below desired precision, but can no longer augment the query')
            return False
        else:
            return True

    def augment_query(self):
        """
        query-modification method 
        step 1: For each of the searched result, combine the snippet and title as a document;
                treat the query as one document, then merge them as a document list
        step 2: calculate the tf-idf vector of each of these documents after removing the stop words
        step 3: Use Rocchio Algorithm to get the modified query vector according to 
                user's feedback. In this grogram, we set alpha = 1, beta = 0.75, gamma = 0.15
        step 4: Sort each dimension of this vecter in descending order, choose two words 
                corresponding to the largest dimension value and append them to the query. 
                If the chosen words already exit in the query, append word with third-largest
                value, and so on.
        """
        print('Indexing results ....')
        docs = []
        query = ' '.join([str(term) for term in self.query_terms])
        docs.append(query) # treat query as a document
        for item in self.query_results:
            docs.append(item.to_string()) #combine the snippet and title as a document
        
        stop_words = []
        with open('proj1-stop.txt') as fp:
            for line in fp.readlines():
                stop_words.append(line.strip())
                
        vectorizer = TfidfVectorizer(stop_words=stop_words, smooth_idf=False, sublinear_tf=True)
        result = vectorizer.fit_transform(docs).toarray() #tf-idf
        word2idx = vectorizer.vocabulary_
        idx2word = {}
        for key in word2idx:
            idx2word[word2idx[key]] = key
        q_vector = result[0]
        doc_vectors = result[1:]
        

        evaluations = [item.evaluation for item in self.query_results]
        # use Rocchio Algorithm to get the modified query
        augmented_q_vector = RocchioAlgorithm().run(doc_vectors, q_vector, evaluations)

        append_query_terms = []
        for idx in np.argsort(augmented_q_vector)[::-1]:
            if idx2word[idx] in self.query_terms:
                continue
            else:
                if len(append_query_terms) < 2:
                    append_query_terms.append(idx2word[idx])
                else:
                    break
        print('Indexing results ....')
        print('Augmenting by  {0}'.format(' '.join(append_query_terms)))
        print("==============================================")
        print("==============================================")
        print(" ")
        self.query_terms.extend(append_query_terms)
        


class SimpleSearchResult(object):
    """
    extract 'snippet', 'link' and 'title' of each result and encapsulate
    them into a class called SimpleSearchResult
    """
    def __init__(self, origin_search_result):
        self.description = origin_search_result['snippet']
        self.url = origin_search_result['link']
        self.title = origin_search_result['title']

        self.evaluation = True

    def to_string(self):
        return self.description + ' ' + self.title