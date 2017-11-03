import numpy as np

class RocchioAlgorithm(object):
    def __init__(self):
        self.alpha = 1
        self.beta = 0.75
        self.gamma = 0.15
        pass

    def run(self, doc_vectors, q_vector, evaluations):
        # split evaluations to relevant and not relevant
        D_r = []
        D_nr = []
        for idx, evaluation in enumerate(evaluations):
            if evaluation:
                D_r.append(idx)
            else:
                D_nr.append(idx)

        new_q = self.alpha * q_vector + \
            self.beta * np.sum(doc_vectors[D_r, :], axis=0) / len(D_r) - \
            self.gamma * np.sum(doc_vectors[D_nr, :], axis=0) / len(D_nr)
        return new_q
