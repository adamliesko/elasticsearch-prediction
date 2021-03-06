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

package com.mahisoft.elasticsearchprediction.domain;

public class SimpleDataSetResult {

	private Integer trainDataSetSize;

	private Integer testDataSetSize;

	private String results;

	public Integer getTrainDataSetSize() {
		return trainDataSetSize;
	}

	public void setTrainDataSetSize(Integer trainDataSetSize) {
		this.trainDataSetSize = trainDataSetSize;
	}

	public Integer getTestDataSetSize() {
		return testDataSetSize;
	}

	public void setTestDataSetSize(Integer testDataSetSize) {
		this.testDataSetSize = testDataSetSize;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "SimpleDataSetResult:\ntrainDataSetSize=" + trainDataSetSize + "\ntestDataSetSize=" + testDataSetSize
				+ "\nresults=" + results;
	}

}
