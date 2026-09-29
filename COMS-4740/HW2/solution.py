import numpy as np
import sys
from helper import *


def show_images(data):
    """Show the input images and save them.

    Args:
        data: A stack of two images from train data with shape (2, 16, 16).
              Each of the image has the shape (16, 16)

    Returns:
        Do not return any arguments. Save the plots to 'image_1.*' and 'image_2.*' and
        include them in your report
    """
    ### YOUR CODE HERE
    for i in range(2):
        plt.imshow(data[i], cmap="gray")
        plt.axis("off")
        plt.savefig(f"image_{i+1}.png")
        plt.close()

    ### END YOUR CODE


def show_features(X, y, save=True):
    """Plot a 2-D scatter plot in the feature space and save it. 

    Args:
        X: An array of shape [n_samples, n_features].
        y: An array of shape [n_samples,]. Only contains 1 or -1.
        save: Boolean. The function will save the figure only if save is True.

    Returns:
        Do not return any arguments. Save the plot to 'train_features.*' and include it
        in your report.
    """
    ### YOUR CODE HERE
    plt.figure()

    plt.scatter( X[y==1,0], X[y==1,1], color="red",marker="*", label = "1")
    plt.scatter( X[y==-1,0], X[y==-1,1], color="blue",marker="+", label = "5")   
    plt.xlabel('Symmetry Feature')
    plt.ylabel('Average Intensity Feature')
    plt.legend()
    if save:
        plt.savefig('train_features.png')
    plt.close()

    ### END YOUR CODE


class Perceptron(object):
    
    def __init__(self, max_iter):
        self.max_iter = max_iter

    def fit(self, X, y):
        """Train perceptron model on data (X,y).

        Args:
            X: An array of shape [n_samples, n_features].
            y: An array of shape [n_samples,]. Only contains 1 or -1.

        Returns:
            self: Returns an instance of self.
        """
        ### YOUR CODE HERE
        a = np.zeros(X.shape[1]) 
        for _ in range(self.max_iter):
            for i in range(X.shape[0]):
                if y[i] * np.dot(a, X[i]) <= 0:  
                    a += y[i] * X[i]  

        self.W = a
        
        ### END YOUR CODE
        
        return self

    def get_params(self):
        """Get parameters for this perceptron model.

        Returns:
            W: An array of shape [n_features,].
        """
        if self.W is None:
            print("Run fit first!")
            sys.exit(-1)
        return self.W

    def predict(self, X):
        """Predict class labels for samples in X.

        Args:
            X: An array of shape [n_samples, n_features].

        Returns:
            preds: An array of shape [n_samples,]. Only contains 1 or -1.
        """
        ### YOUR CODE HERE
        Score_board = np.dot(X, self.W)
        preds = np.where(Score_board >= 0, 1, -1)
        return preds


        ### END YOUR CODE

    def score(self, X, y):
        """Returns the mean accuracy on the given test data and labels.

        Args:
            X: An array of shape [n_samples, n_features].
            y: An array of shape [n_samples,]. Only contains 1 or -1.

        Returns:
            score: An float. Mean accuracy of self.predict(X) wrt. y.
        """
        ### YOUR CODE HERE
        Pred_Scores = self.predict(X)
        score = np.mean(Pred_Scores == y)
        return score


        ### END YOUR CODE




def show_result(X, y, W):
    """Plot the linear model after training. 
       You can call show_features with 'save' being False for convenience.

    Args:
        X: An array of shape [n_samples, 2].
        y: An array of shape [n_samples,]. Only contains 1 or -1.
        W: An array of shape [n_features,].
    
    Returns:
        Do not return any arguments. Save the plot to 'result.*' and include it
        in your report.
    """
    ### YOUR CODE HERE
    show_features(X, y, save=False)
    x_val = np.linspace(np.min(X[:, 0]), np.max(X[:, 0]), 100)
    y_val = -(W[0] * x_val) / W[1]
    plt.plot(x_val, y_val, label='Decision Boundary')
    plt.legend()
    plt.savefig("result.png")
    ### END YOUR CODE



def test_perceptron(max_iter, X_train, y_train, X_test, y_test):

    # train perceptron
    model = Perceptron(max_iter)
    model.fit(X_train, y_train)
    train_acc = model.score(X_train, y_train)
    W = model.get_params()

    # test perceptron model
    test_acc = model.score(X_test, y_test)

    return W, train_acc, test_acc