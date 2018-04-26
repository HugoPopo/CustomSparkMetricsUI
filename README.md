# Custom metrics in Spark UI

This project aims at implementing a custom metrics for Spark, based on [Spark Listener](https://jaceklaskowski.gitbooks.io/mastering-apache-spark/content/spark-SparkListener.html) approach and displayed in [Spark UI](https://jaceklaskowski.gitbooks.io/mastering-apache-spark/content/spark-webui.html). It relies mostly on [Jacek Laskoswki's exercise](https://github.com/jaceklaskowski/mastering-apache-spark-book/blob/master/exercises/spark-exercise-custom-scheduler-listener.adoc) and [Sanjosh's web UI extension](https://github.com/sanjosh/scala/tree/master/spark_extensions/webui). Refer to these projects for deeper understanding and improvements.

## Compiling

The project is compiled with [sbt](https://www.scala-sbt.org/), make sure you have it installed.

```shell
$ sbt package
```

If you want to start [spark-shell](https://spark.apache.org/docs/latest/quick-start.html) right after compiling, you can run [package_n_run.sh](https://github.com/HugoPopo/CustomSparkMetricsUI/blob/master/package_N_run.sh) with *1* in argument, otherwise this is equivalent to running spark-shell with your jar file added:

```shell
$ spark-shell --conf spark.logConf=true --conf spark.extraListeners=org.apache.spark.CustomSparkListener --driver-class-path target/scala-2.11/custom-spark-ui_2.11-1.0.jar
```

With this command line, the [custom listener](https://github.com/HugoPopo/CustomSparkMetricsUI/blob/master/src/main/scala/CustomSparkListener.scala) is directly registered and active. See below for further explanation on its behavior.

## Setting up the UI

The jar is added to your application, however this does not mean the custom tab is already set up in the UI. To do so, we need a special instruction:

```scala
import org.apache.spark.ui.SparkUIExtender

val myui = SparkUIExtender.extend(sc)
```

The [SparkUIExtender](https://github.com/HugoPopo/CustomSparkMetricsUI/blob/2335a8ab868de3e5838bc7a1994f2f0bd5c89039/src/main/scala/CustomUITab.scala#L58) is an object dedicated to add the custom tab to your application. *sc* refers to the [SparkContext](https://jaceklaskowski.gitbooks.io/mastering-apache-spark/content/spark-SparkContext.html) of your application. The *extend* method gets the UI from the SparkContext and attach the custom tab to it.

You can type these lines in your spark shell before starting your jobs or include them at the beginning of your application if you use spark submit.

## Rendering

You can see your Spark UI in your browser at <u>http://host:4040/custom</u>.

![Custom tab in Spark UI](https://raw.githubusercontent.com/HugoPopo/CustomSparkMetricsUI/master/doc/Custom_UI.png)

The metrics displayed here is the heap size of the JVM, this is not very relevant alone, but you can add your own metrics.

## How it works and how to improve it

### Custom listener

The different metrics (in our case the maximum memory) come from the events of Spark (in our case [onBlockManagerAdded](https://github.com/apache/spark/blob/20ca208bcda6f22fe7d9fb54144de435b4237536/core/src/main/scala/org/apache/spark/scheduler/SparkListener.scala#L93)). The custom listener extends the [SparkListener](https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/scheduler/SparkListener.scala), which has all the methods intercepting Spark events.

When overriding these methods (here [onBlockManagerAdded](https://github.com/apache/spark/blob/20ca208bcda6f22fe7d9fb54144de435b4237536/core/src/main/scala/org/apache/spark/scheduler/SparkListener.scala#L240)), you can get values from this event and collect new metrics.

The metrics are stored in [StatusHolder](https://github.com/HugoPopo/CustomSparkMetricsUI/blob/2335a8ab868de3e5838bc7a1994f2f0bd5c89039/src/main/scala/CustomSparkListener.scala#L64). The only purpose of this object is to store the data you need to send to the UI, so you can structure your variables as you want, as long as you know how to retrieve it.

### Custom UI

The custom UI relies on tab, which pages are attached to. The custom page of the project contains only basic HTML code, in the *render* method, with the maximum memory metrics, got from the StatusHolder.

You can add any page you want in yout tab with your own code, including scripts and style sheets.

Be careful though, if you use a [UI](https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/ui/WebUI.scala) different from SparkUI, you have to change the class the tab extends (here this is SparkUITab).

### Summary

1. Catch the event you want with the overriding of the appropriate method from SparkListener.
2. Get your metrics from the event and store it in StatusHolder.
3. Create your page with your structured information, including metrics from StatusHolder.
4. Create your custom tab and attach your page to it, with the *attachPage* method.
5. Add your tab in the UI, got from the Sparkcontext, in the SparkUIExtender object.
6. Run (with your jar file) your spark shell or spark submit with a call of the *extend* method of SparkUIExtender, getting the SparkContext as argument.
