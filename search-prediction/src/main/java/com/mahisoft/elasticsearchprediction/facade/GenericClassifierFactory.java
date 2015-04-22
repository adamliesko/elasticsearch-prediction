/*====================================================================================
    Copyright 2015 Mahisoft Inc. (Yessika Labrador, Federico Ponte and Joaquin Delgado)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
======================================================================================*/

package com.mahisoft.elasticsearchprediction.facade;

import com.mahisoft.elasticsearchprediction.classifier.GenericClassifier;
import com.mahisoft.elasticsearchprediction.classifier.weka.WekaGenericClassifier;
import com.mahisoft.elasticsearchprediction.utils.DataProperties;

import com.sdhu.elasticsearchprediction.spark.Spark_Trainer;

public class GenericClassifierFactory {

	private GenericClassifierFactory() {
	}

	public static GenericClassifier getClassifier(DataProperties dataProperties) {
		String clf_type = dataProperties.getValue("classifier.lib");

		if (clf_type != null) {

            if (clf_type.startsWith("spark")) {
                Spark_Trainer spt = new Spark_Trainer(dataProperties);
                return spt.getGenericClassifier();
            }

			switch (clf_type) {
			case "weka":
				return new WekaGenericClassifier();
			case "another_lib":
				return null;
			default:
				return null;
			}
		} else {
			return null;
		}

	}

}
