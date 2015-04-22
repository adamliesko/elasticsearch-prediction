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

import com.sdhu.elasticsearchprediction.spark.Spark_PredictorEngine;

public class GenericPredictorFactory {

	private GenericPredictorFactory() {
	}

	public static PredictorEngine getPredictor(PluginProperties pluginProperties) throws FileModelException {
		String clf_type = pluginProperties.getClassifier();
        String modelPath = pluginProperties.getModelPath();
        
        if (clf_type != null) {
            if (clf_type.startsWith("spark")) {
                Spark_PredictorEngine spt = new Spark_PredictorEngine(modelPath, clf_type);
                return spt.getPredictorEngine();
            }
            
			switch (clf_type) {
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
