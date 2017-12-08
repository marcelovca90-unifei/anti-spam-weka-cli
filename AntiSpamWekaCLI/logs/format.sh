#!/bin/bash

function join_by { local IFS="$1"; shift; echo "$*"; }

methods=(A1DE FastRandomForest J48 LibSVM MultilayerPerceptron NaiveBayes RBFNetwork SPegasos)
metrics=(name featureSelection noFeaturesBefore noFeaturesAfter classifier hamPrecision ci spamPrecision ci weightedPrecision ci hamRecall ci spamRecall ci weightedRecall ci hamAreaUnderPRC ci spamAreaUnderPRC ci weightedAreaUnderPRC ci hamAreaUnderROC ci spamAreaUnderROC ci weightedAreaUnderROC ci hamFMeasure ci spamFMeasure ci weightedFMeasure ci trainingTime ci testingTime ci)

# remove previously generated csv files
rm *.csv

for method in "${methods[@]}"
do
  # concatenate metrics and confidence intervals in header
  join_by ";" "${metrics[@]}" > $method.csv

  # echo results replacing tabs and plus/minus signs by semicolon
  cat $method.log | grep "±" | sed -E $'s/\t/;/g' | sed -E $'s/ ± /;/g' >> $method.csv
done
