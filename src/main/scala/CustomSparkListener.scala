/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark

import java.util.Observable

import org.apache.spark.scheduler._

import scala.collection.mutable
// import org.apache.spark.executor.TaskMetrics

/**
 * Spark listener aiming at registering values from chosen events
 */
class CustomSparkListener extends SparkListener {
  override def onApplicationStart(applicationStart: SparkListenerApplicationStart): Unit = {
    println(s"Application ${applicationStart.appName} started by ${applicationStart.sparkUser}")
  }

  override def onBlockManagerAdded(blockManagerAdded: SparkListenerBlockManagerAdded): Unit = {
    StatusHolder.updateStatus("maxMemory",blockManagerAdded.maxMem.asInstanceOf[AnyRef])
    println(
      s"""
         |BlockManager ${blockManagerAdded.blockManagerId} added with ${blockManagerAdded.maxMem} max memory.
       """.stripMargin)
  }

  override def onTaskStart(taskStart: SparkListenerTaskStart): Unit = {
    val info = taskStart.taskInfo
    if (info != null) {
      println(s"""
           |Task ${info.taskId} started...
           |""".stripMargin)
    }
  }

  /*
  override def onExecutorMetricsUpdate(metricsUpdate: SparkListenerExecutorMetricsUpdate) {
    if(metricsUpdate.accumUpdates.nonEmpty){
      println(s"Information is available for ${metricsUpdate.execId}:")
      for (i <- metricsUpdate.accumUpdates) {
        val accInfo: Seq[AccumulableInfo] = i._4
        accInfo.foreach(u => /*if(u.update.get != 0)*/ println(s"Info: task:${i._1} stage:${i._2} id:${u.id} name:${u.name} update:${u.update.get}"))
      }
    }
  }
  */

  override def onBlockUpdated(blockUpdated: SparkListenerBlockUpdated): Unit = {
    StatusHolder.updateStatus(s"BlockMemSize", blockUpdated.blockUpdatedInfo.memSize.asInstanceOf[AnyRef])
    println(s"Block updated - id: ${blockUpdated.blockUpdatedInfo.blockId} memSize: ${blockUpdated.blockUpdatedInfo.memSize}")
    // TODO: get blockId and pass it to sc
  }

  override def onExecutorAdded(executorAdded: SparkListenerExecutorAdded): Unit = {
    StatusHolder.updateStatus("Executor added", executorAdded.executorId)
  }

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    println(s"""
         |Task ${taskEnd.taskInfo.taskId} ended with status ${taskEnd.taskInfo.status}.
         |""".stripMargin)
  }

  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    println(s"Stage ${stageCompleted.stageInfo.stageId} completed with ${stageCompleted.stageInfo.numTasks} tasks.")
  }
}

/**
 * An object containing all the values to be displayed or used in the
 * custom UI page
 */
object StatusHolder extends Observable {
  val status: mutable.HashMap[String, Any] = new mutable.HashMap[String, Any]

  def updateStatus(label: String, value: Object): Unit = {
    status.update(label, value)
    notifyObservers(label)
  }

  override def notifyObservers(arg: scala.Any): Unit = {
    setChanged()
    super.notifyObservers(arg)
  }
}