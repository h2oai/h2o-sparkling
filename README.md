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
    
  - For Tachyon support please `XXX`
  
  - Compile sparkling demo
```
cd h2o-sparkling-demo
sbt assembly
```

Note: The assembly stage is important, since the demo is a Spark driver sending a jar-file containing implementation of a working job.

## Run demo
```
cd h2o-sparkling-demo
sbt run
```

## Doc

 - http://ampcamp.berkeley.edu/wp-content/uploads/2012/06/matei-zaharia-part-2-amp-camp-2012-standalone-programs.pdf




## Running with Tachyon
 - local


 - remote
  Follow instructions on http://tachyon-project.org/Running-Spark-on-Tachyon.html


