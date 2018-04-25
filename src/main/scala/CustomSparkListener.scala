package org.apache.spark

import org.apache.spark.scheduler._
// import org.apache.spark.executor.TaskMetrics

class CustomSparkListener extends SparkListener {
  override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
    println(s"Application ${applicationStart.appName} started by ${applicationStart.sparkUser}")
  }

  override def onBlockManagerAdded(blockManagerAdded: SparkListenerBlockManagerAdded): Unit = {
    StatusHolder.maxMemory = blockManagerAdded.maxMem
  }
  override def onJobStart(jobStart: SparkListenerJobStart) {
    println(s"Job started with ${jobStart.stageInfos.size} stages: $jobStart.")
    val stageInfos = jobStart.stageInfos
    stageInfos.foreach(i => println(s"""diskBytesSpilled on ${i.name}:
      details: ${i.stageId}""")) //taskMetrics.diskBytesSpilled)))
  }

  override def onTaskStart(taskStart: SparkListenerTaskStart): Unit = {
    val info = taskStart.taskInfo
    if (info != null) {
      println(s"""[TASK] Task Info: ${info.taskId}""")
    }
  }
  
  override def onExecutorMetricsUpdate(metricsUpdate: SparkListenerExecutorMetricsUpdate) {
	if(metricsUpdate.accumUpdates.nonEmpty){
	  println(s"Information is available for ${metricsUpdate.execId}:")
	  for (i <- metricsUpdate.accumUpdates) {
//      metricsUpdate.
		  val accInfo: Seq[AccumulableInfo] = i._4
		  accInfo.foreach(u => println(s"Info: task:${i._1} stage:${i._2} id:${u.id} name:${u.name} update:${u.update.get}"))
	  }
	}
  }

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    println(s"Stage ${stageCompleted.stageInfo.stageId} completed with ${stageCompleted.stageInfo.numTasks} tasks.")
  }
  
  override def onUnpersistRDD(unpersistRDD:  SparkListenerUnpersistRDD): Unit = {
	println(s"RDD ${unpersistRDD.rddId} has been uncached.")
  }
}

object StatusHolder {
  var maxMemory: Long = -1
  var currentExecutorMemory : Long = -1
}

