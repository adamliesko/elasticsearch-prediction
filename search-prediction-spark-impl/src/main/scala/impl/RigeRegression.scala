package com.sdhu.elasticsearchprediction.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression._
import org.apache.spark.mllib.linalg.Vectors

object RidgeRegression_Helper extends Linear_SparkModelHelpers[RidgeRegressionModel]{
  val name = "RidgeRegression_Helper"

  override def sparkTrainClf(data: RDD[LabeledPoint], params: Map[String,String]): RidgeRegressionModel = {
    RidgeRegressionWithSGD.train(
      data,
      params.getOrElse("numIterations","3").toInt,
      params.getOrElse("stepSize","1.0").toDouble,
      params.getOrElse("regParam","0.01").toDouble,
      params.getOrElse("minBatchFraction","1.0").toDouble
      )
  }
  
  override def getOptClf(lm: LinearModelData): Option[RidgeRegressionModel] = {
    Some(new RidgeRegressionModel(Vectors.dense(lm.weights), lm.intercept))
  }
}


