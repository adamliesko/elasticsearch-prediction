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

package com.mahisoft.elasticsearchprediction.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Scorer;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.AbstractSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;

import com.mahisoft.elasticsearchprediction.facade.GenericPredictorFactory;
import com.mahisoft.elasticsearchprediction.plugin.domain.IndexAttributeDefinition;
import com.mahisoft.elasticsearchprediction.plugin.domain.IndexValue;
import com.mahisoft.elasticsearchprediction.plugin.engine.PredictorEngine;
import com.mahisoft.elasticsearchprediction.plugin.utils.PluginProperties;

import static com.mahisoft.elasticsearchprediction.domain.DataType.DOUBLE;

//bad
import static com.mahisoft.elasticsearchprediction.domain.DataType.STRING;
import com.mahisoft.elasticsearchprediction.domain.DataType;
import java.util.concurrent.CopyOnWriteArrayList;

public class PredictorScoreScript extends AbstractSearchScript {

	public static final String SCRIPT_NAME = "search-predictor";

	private static final ESLogger LOGGER = Slf4jESLoggerFactory.getLogger(PredictorScoreScript.class.getSimpleName());

	private static PredictorEngine engine;

	private static List<IndexAttributeDefinition> mapping;

	static {
		try {
			PluginProperties pluginProperties = PluginProperties.getInstance();
            List<IndexAttributeDefinition> mp = new CopyOnWriteArrayList<IndexAttributeDefinition>();

            LOGGER.info("Starting Predictor Engine");
			engine = GenericPredictorFactory.getPredictor(pluginProperties);
            
            LOGGER.info("Loaded engine, now loading mapping");
			mapping = pluginProperties.getMapping();
			
            LOGGER.info("Done starting Predictor Engine");
		} catch (Exception e) {
			LOGGER.error("Unable to create PredictorEngine", e);
			throw new RuntimeException("Unable to create PredictorEngine");
		}
	}

	public PredictorScoreScript() {
	}

	@Override
	public void setScorer(Scorer scorer) {
		// ignore
	}

	public static class Factory implements NativeScriptFactory {
		@Override
		public ExecutableScript newScript(@Nullable Map<String, Object> params) {
			return new PredictorScoreScript();
		}

	}

	@Override
	public Object run() {
		double estimation = 0.0;
		List<IndexValue> values = new ArrayList<>(mapping.size());

		try {

			for (IndexAttributeDefinition definition : mapping) {
				ScriptDocValues docValues = (ScriptDocValues) doc().get(definition.getName());
				boolean validDocValues = (docValues != null) && (!docValues.isEmpty());
                
			    // TODO: shortcut  making everything a string due to the way 
                // parsed on spark	
				if (definition.getType() == DOUBLE) {
					IndexValue v = new IndexValue(definition, validDocValues ? ((ScriptDocValues.Doubles) docValues)
							.getValue() : "0.0");
                    
                    values.add(v);
				} else {
					IndexValue v =new IndexValue(definition, validDocValues ? ((ScriptDocValues.Strings) docValues)
							.getValue() : "");

                    values.add(v);
				}

			}
			estimation = engine.getPrediction(values);
		} catch (Exception ex) {
			LOGGER.error("Problem predicting", ex);
		}

		return estimation;
	}

}