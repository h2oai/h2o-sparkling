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
  sbt "run --local"
  ```

### Run distributed version
For this run a Spark cloud is required:
  - Run master and one worker on local node
  ```
  cd spark/sbin
  ./start-master.sh
  ./start-slave.sh 1 "spark://localhost:7077"
  ```

  - Assembly h2o-sparkling-demo jar file which can be sent by the driver to Spark cloud
  ```
  cd h2o-sparkling-demo
  sbt assembly
  sbt "run --remote"
  ```

### Run additional H2O node
```
cd h2o-sparkling-demo
sbt runH2O
```

 ### Select different RDD2Frame extractor

 Currently demo supports three extractors:

   - _dummy_ - pull all data into driver and create a frame
   - _file_ - ask Spark to save RDD as a file on local filesystem and then parse a stored file
   - _tachyon_ - ask Spark to save RDD to tachyon filesystem, then H2O load a file from tachyon FS

  The extractor can be selected via `--extractor` command line parameter, e.g., `--extractor==tachyon`


## Running with Tachyon
  - Start Tachyon
  ```
  cd tachyon/bin
  ./tachyon-start.sh
  ```

  - Look at http://localhost:19999/ to see list of files stored on the storage or type _tfs_ command `tachyon tfs ls /`

  - For more info details discuss instructions instructions on http://tachyon-project.org/Running-Spark-on-Tachyon.html


## Example

 Run a demo with Tachyon-based extractor againts remote Spark cloud:
 ```
 cd h2o-sparkling-demo
 sbt assembly
 sbt "run --remote --extractor=tachyon"
 ```

## Doc

  - Matei Zaharia' slides about Spark
    - http://ampcamp.berkeley.edu/wp-content/uploads/2012/06/matei-zaharia-part-2-amp-camp-2012-standalone-programs.pdf

