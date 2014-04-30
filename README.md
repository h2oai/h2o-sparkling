h2o-sparkling-demo
==================

Demo showing how to access Spark from H2O and transfer data between both platforms.

## Installation

  - First compile latest version of spark with SQL component
  ```
  git clone spark
  cd spark
  sbt/sbt assembly publish-local
  ```
    
  - For Tachyon support please download Tachyon 0.4.1 from https://github.com/amplab/tachyon/releases/tag/v0.4.1
  
  - Compile sparkling demo
  ```
  cd h2o-sparkling-demo
  sbt assembly
  ```

Note: The assembly stage is important, since the demo is a Spark driver sending a jar-file containing implementation of a working job.

## Run demo

### Run local version
For this run no Spark cloud is required:
  - Execute an instance of H2O embedding Spark driver
  ```
  cd h2o-sparkling-demo
  sbt run
  ```

### Run distributed version
For this run a Spark cloud is required:
  - run master and one worker on local node
  ```
  cd spark/sbin
  ./start-master.sh
  ./start-slave.sh 1 "spark://localhost:7077"
  ```

  - assembly h2o-sparkling-demo jar file which can be sent by the driver to Spark cloud
  ```
  cd h2o-sparkling-demo
  sbt assembly
  sbt run
  ```

## Doc

 - http://ampcamp.berkeley.edu/wp-content/uploads/2012/06/matei-zaharia-part-2-amp-camp-2012-standalone-programs.pdf


## Running with Tachyon
  - start Tachyon
  ```
  cd tachyon/bin
  ./tachyon-start.sh
  ```
 - For more info please discuss instructions instructions on http://tachyon-project.org/Running-Spark-on-Tachyon.html


