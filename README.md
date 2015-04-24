elasticsearch-prediction
======================

The project consists of two parts: generator and plugin.

##GENERATOR 

This part of the code will be on charge of reading a CSV file and with the contents generate:

 * Trained model with the given algorith=m.
 * Elasticsearch index with the corresponding types and values.
 * Configuration that will be used by the plugin.

Usage: = 

### 1. Configure all the options in a new properties file:

#### Data input properties
*	**data.filename** = CSV file that will be loaded to generatea all thing.
*	**data.columns** = List of the names of the attributes from the dataset that will be used to create the index and model. They need to be separated by comma and must include the variable that will be predicted. If the value used is "all", then it will use all the attributes of the datase.

#### Classification Training properties
*	**classifier.lib** = Which library is gonna be used to generate the model. Right now it only has weka.
*	**train.percentage** = Percentage of data that will be used on training. The rest of the data will be use to test the model.
*	**validate.options** = Options regarding the type of validation that will be done over the model. The options are the following:
	*	Sd: Tests the model using the test partition as the  evaluation set of the model.
	*	Cv: Tests the model using cross validator.
*	**validate.numFolds** = Number of partitions for the cross validator.
*	**model.filename** = Complete path where the model that will be created. 

#### Elasticsearch properties
*	**cluster.name** = Name of elasticsearch cluster.
*	**node.name** = Name of elasticsearch node.
*	**index.name** = Name of the index that will be created. It will be deleted before creating it again, so be careful.
*    mapping.filename = File with the mapping that will be needed for the plugin.
*	**host** = Elasticsearch host.
*	**port** = Elasticsearch port.

#### Spark specific properties
 * **spark.model.type** = *<string>* currently only linear models supported
 * **spark.model.parmas** = *<param1:val1,param2:val2,...>* params for model training
 * **spark.model.isregression** = *<boolean>*
 * **spark.model.binThreshold** = *<double>* specify threshold to binarize target variable
 * **spark.conf** = *<sparkConfKey1:sparkConfVal1,sparkConfKey2:sparkConfVal2>* set spark conf values such as master, parallelism, etc...


#### Weka specific properties
*	**weka.classifier.class** = complete name of the Weka Classifier that will be used for the prediction: package  + class name.
*	**weka.classifier.options** = List of options required by the classifier in use, separated by spaces. If the classifier does not require options, then remove this property.
*	**weka.data.saveArff** = Indicates if an ARFF file wants to be generated and saved. The required value is "true". With any other value the file is not generated. 
*	**weka.date.fileArff** = Complete path where the file that will be created. It needs to have .arff extension.


### Example using Spark:

	#Generic options

    data.filename=adult_num.csv
    data.columns=age,workclass,education,education_num,marital_status,occupation,relationship,race,sex,capital_gain,capital_loss,hours-per-week,native-country,probability
    dyata.type=csv

    #Classifier options
    classifier.lib=spark.logistic-regression
    train.percentage=80
    validate.options=SdCv
    validate.numFolds=5
    model.filename=adult.model

    #Elasticsearch options
    cluster.name=elasticsearch_sdhu
    node.name=Coral
    index.name=adult-logistic-regression
    mapping.filename=adult-logistic-regression.test
    host=localhost
    port=9300

    #Spark options
    spark.model.type=logistic-regression
    spark.model.isregression=false
    spark.model.binThreshold=0.5
    spark.model.params=numIterations:100,regParam:0.01,minBatchFraction:1.0,stepSize:1.0
    spark.model.numClasses=2
    spark.conf=spark.master:local[4],spark.driver.memory:512m


### Example using Weka:

    #Generic options
    data.filename=/path/to/csv/file
    data.columns=age,workclass,sex,capital_gain,capital_loss,native-country,probability

    #Classifier options
    classifier.lib=weka
    train.percentage=80
    validate.options=SdCv
    validate.numFolds=5
    model.filename=/path/where/model/file/will/be/saved

    #Elasticsearch options
    cluster.name=
    node.name=
    index.name=
    mapping.filename=/path/where/mapping/file/will/be/saved
    host=localhost
    port=9300

    #Weka options
    weka.classifier.class=weka.classifiers.trees.RandomTree
    weka.classifier.options=-S 3 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1 -h 0
    weka.data.saveArff=true
    weka.data.fileArff=/path/where/arff/file/will/be/saved

### 2. Compile with maven:


#### Compile API

```
export MAVEN_OPTS=-Xss2m

cd search-prediction-api

mvn clean

mvn package
```

#### Compile Spark Implementation
```
cd elasticsearch-prediction-spark

sbt assembly

```


#### Compile Weka Implementation

```
cd search-prediction-weka-impl

cp search-prediction-api/target/search-prediction-api-1.0.jar search-prediction/lib

mvn clean

mvn package
```

##### Compile Generator

```
cd search-prediction

cp ../search-prediction-api/target/search-prediction-api-1.0.jar lib

cp ../search-prediction-weka-impl/target/search-prediction-weka-impl-1.0.jar lib

cp ../elasticsearch-prediction-spark/target/scala-2.10/elasticsearchprediction-spark-assembly-0.1.jar ../search-prediction/lib/

mvn install:install-file -Dfile=lib/search-prediction-weka-impl-1.0.jar -DgroupId=com.mahisoft.elasticsearchprediction -DartifactId=search-prediction-weka-impl -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=lib/search-prediction-api-1.0.jar -DgroupId=com.mahisoft.elasticsearchprediction -DartifactId=search-prediction-api -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=lib/elasticsearchprediction-spark-assembly-0.1.jar -DgroupId=com.sdhu -DartifactId=elasticsearchprediction-spark -Dversion=0.1 -Dpackaging=jar

mvn clean

mvn -Pgenerator package
```

### 3. Run jar file generated:
```
  java -jar target/releases/search-prediction-1.0.jar /path/to/the/created/properties/file
```

If there are no errors, then the model and index were generated without any issue.

##PLUGIN

This will use the generated model and mapping to score documents in the created index.

Usage:

### 1. Configure all the options in the plugin.properties file:

* modelPath = Path where the generated model is.
* mapping = Contents of the mapping file generated
* classifier.lib = Which library is gonna be used to make the classification. Right now it only has weka.


Example:

```
modelPath=/path/to/model/file 
mapping=age:double,workclass:string,fnlwgt:double,education:string,education_num:double,marital_status:string,occupation:string,relationship:string,race:string,sex:string,capital_gain:double,capital_loss:double,hours-per-week:double,native-country:string
classifier.lib=weka
```

### 2. Install plugin:
```
mvn clean

mvn -Pplugin package

sudo /usr/share/elasticsearch/bin/plugin -remove search-predictor

sudo /usr/share/elasticsearch/bin/plugin -install search-predictor url file:///path/to/target/releases/search-prediction-1.0.zip

sudo service elasticsearch restart
```

### 3. Make a query:

```
{
  "query": {
    "function_score": {
      "query": {
        "match_all": {}
      },
      "functions": [
        {
          "script_score": {
            "script": "search-predictor",
            "lang": "native",
            "params": {}
          }
        }
      ],
      "score_mode": "sum",
      "boost_mode": "replace"
    }
  }
}
```
Make sure to use the following url: `http://host:port/indexname/_default_`