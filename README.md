# anti-spam-weka-cli [![Build Status](https://travis-ci.org/marcelovca90/anti-spam-weka-cli.svg?branch=master)](https://travis-ci.org/marcelovca90/anti-spam-weka-cli)

Project of my master's degree in Computer Science ("Study and Research in Anti-Spam Systems").

For instructions on how to clone, build and run the project, please refer to ["How To"](https://github.com/marcelovca90/anti-spam-weka-cli/blob/master/HOW-TO.md).

- - - -

Machine learning library:
- [Weka (Waikato Environment for Knowledge Analysis)](http://www.cs.waikato.ac.nz/ml/weka/)

Data sets:
- [2017_BASE2](https://github.com/marcelovca90/anti-spam-weka-data/tree/master/2017_BASE2) (with 8, 16, 32, 64, 128, 256, 512 and 1024 features - CHI2, DF and MI)
- [2017_MULT10](https://github.com/marcelovca90/anti-spam-weka-data/tree/master/2017_MULT10) (with 10, 20, 30, 40, 50, 60, 70, 80, 90 and 100 features - CHI2, DF and MI)

Classification methods:
- [A1DE](http://weka.sourceforge.net/packageMetaData/AnDE/index.html) - Averaged 1-Dependence Estimator
- [A2DE](http://weka.sourceforge.net/packageMetaData/AnDE/index.html) - Averaged 2-Dependence Estimator
- [BFTREE](http://weka.sourceforge.net/doc.packages/bestFirstTree/weka/classifiers/trees/BFTree.html) - Best-first tree
- [CART](http://weka.sourceforge.net/doc.packages/simpleCART/weka/classifiers/trees/SimpleCart.html) - Classification And Regression Trees
- [DTNB](http://weka.sourceforge.net/doc.stable/weka/classifiers/rules/DTNB.html) - Decision Table/Naive Bayes Hybrid Classifier
- [FURIA](http://weka.sourceforge.net/packageMetaData/fuzzyUnorderedRuleInduction/index.html) - Fuzzy Unordered Rule Induction Algorithm
- [FRF](https://github.com/fracpete/fastrandomforest-weka-package) - Fast Random Forest
- [HP](http://weka.sourceforge.net/doc.packages/hyperPipes/weka/classifiers/misc/HyperPipes.html) - HyperPipe Classifier
- [IBK](http://weka.sourceforge.net/doc.dev/weka/classifiers/lazy/IBk.html) - K-Nearest Neighbours Classifier
- [J48](http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/J48.html) - C4.5 Decision Tree
- [J48C](http://weka.sourceforge.net/packageMetaData/J48Consolidated/index.html) - C4.5 Consolidated Decision Tree
- [J48G](http://weka.sourceforge.net/doc.packages/J48graft/weka/classifiers/trees/J48graft.html) - C4.5 Grafted Decision Tree
- [JRIP](http://weka.sourceforge.net/doc.stable/weka/classifiers/rules/JRip.html) - Repeated Incremental Pruning to Produce Error Reduction
- [LIBLINEAR](http://weka.sourceforge.net/doc.stable/weka/classifiers/functions/LibSVM.html) - Large Linear Classifier
- [LIBSVM](http://weka.sourceforge.net/doc.stable/weka/classifiers/functions/LibSVM.html) - Support Vector Machine
- [MLP](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/MultilayerPerceptron.html) - Multilayer Perceptron
- [NB](http://weka.sourceforge.net/doc.dev/weka/classifiers/bayes/NaiveBayes.html) - Naive Bayes classifier
- [NBTREE](http://weka.sourceforge.net/doc.stable/weka/classifiers/trees/NBTree.html) - Decision Tree with Naive Bayes Classifiers at the leaves
- [RBF](http://weka.sourceforge.net/doc.packages/RBFNetwork/weka/classifiers/functions/RBFNetwork.html) - Radial Basis Function network
- [RT](http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/RandomTree.html) - Random Tree
- [SGD](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SGD.html) - Stochastic Gradient Gescent
- [SMO](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SMO.html) - Sequential Minimal Optimization Algorithm
- [SPEGASOS](http://weka.sourceforge.net/doc.stable/weka/classifiers/functions/SPegasos.html) - Stochastic Primal Estimated sub-GrAdient SOlver for SVM
- [WRF](http://weka.sourceforge.net/doc.dev/weka/classifiers/trees/RandomForest.html) - Weka Random Forest

Metrics:
- Precision
- Recall
- Area under Precision-Recall Curve (PRC)
- Area under Receiver Operating Characteristic (ROC)
- F1 score (also known as F-score or F-measure)
- Training time
- Testing time
