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

import com.mahisoft.elasticsearchprediction.plugin.engine.PredictorEngine;
import com.mahisoft.elasticsearchprediction.plugin.engine.weka.WekaPredictorEngine;
import com.mahisoft.elasticsearchprediction.plugin.exception.FileModelException;
import com.mahisoft.elasticsearchprediction.plugin.utils.PluginProperties;

import com.sdhu.elasticsearchprediction.spark.SparkLinear_PredictorEngine;

public class GenericPredictorFactory {

	private GenericPredictorFactory() {
	}

	public static PredictorEngine getPredictor(PluginProperties pluginProperties) throws FileModelException {
		String genericClassifierClass = pluginProperties.getClassifier();
        String modelPath = pluginProperties.getModelPath();

		if (genericClassifierClass != null) {

  //          if (genericClassifierClass.startsWith("spark"))
  //              return SparkLinear_PredictorEngine(modelPath, genericClassiferClass);
            
			switch (genericClassifierClass) {
			case "weka":
				return new WekaPredictorEngine(modelPath);
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
