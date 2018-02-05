# anti-spam-weka-cli [![Build Status](https://travis-ci.org/marcelovca90/anti-spam-weka-cli.svg?branch=master)](https://travis-ci.org/marcelovca90/anti-spam-weka-cli) [![codecov](https://codecov.io/gh/marcelovca90/anti-spam-weka-cli/branch/master/graph/badge.svg)](https://codecov.io/gh/marcelovca90/anti-spam-weka-cli)  

Project of my master's degree in Computer Science ("Study and Research in Anti-Spam Systems").  

For instructions on how to clone, build and run the project, please refer to ["this guide"](https://github.com/marcelovca90/anti-spam-weka-data/blob/master/README.md).  

- - - -  

Machine learning library:  
- [Weka (Waikato Environment for Knowledge Analysis)](http://www.cs.waikato.ac.nz/ml/weka/)  

Data sets information:  
- There are four data sets ([Ling Spam](https://labs-repos.iit.demokritos.gr/skel/i-config/downloads/), [Spam Assassin](https://spamassassin.apache.org/old/publiccorpus/), TREC ([2005](https://plg.uwaterloo.ca/~gvcormac/treccorpus/), [2006](https://plg.uwaterloo.ca/~gvcormac/treccorpus06/) and [2007](http://plg.uwaterloo.ca/~gvcormac/treccorpus07/)) and [Unifei](http://www.gpesc.unifei.edu.br/)) available [here](https://github.com/marcelovca90/anti-spam-weka-data/). Each was pre-processed with three feature extraction methods ([CHI2](https://nlp.stanford.edu/IR-book/html/htmledition/feature-selectionchi2-feature-selection-1.html), [FD](https://nlp.stanford.edu/IR-book/html/htmledition/frequency-based-feature-selection-1.html) and [MI](https://nlp.stanford.edu/IR-book/html/htmledition/mutual-information-1.html)), with eight different feature vector sizes (8, 16, 32, 64, 128, 256, 512 and 1024).  

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
- [Precision, recall and F1 score](https://en.wikipedia.org/wiki/Precision_and_recall)  
- Area under Precision-Recall (PR) and [Receiver Operating Characteristic (ROC)](https://en.wikipedia.org/wiki/Receiver_operating_characteristic) curves  
- Training and testing times  
