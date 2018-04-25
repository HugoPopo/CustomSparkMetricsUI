package org.apache.spark.ui

import javax.servlet.http.HttpServletRequest

import scala.xml.Node

import org.apache.spark.ui.{SparkUI, SparkUITab, UIUtils, WebUI, WebUIPage}
import org.apache.spark.StatusHolder
import org.apache.spark.SparkContext

class CustomUITab(parent: SparkUI) extends SparkUITab(parent, "custom"){
  attachPage(new CustomWebUIPage(this))
}

class CustomWebUIPage(parent: SparkUITab) extends WebUIPage("") {
  def render(request: HttpServletRequest): Seq[Node] = {
    val content =
      <div>
        {
          <div id="active-executors" class="row-fluid"></div> ++
          <p>This is a custom tab</p>
          <p>max memory
            {StatusHolder.maxMemory}
          </p>
        }
      </div>
      UIUtils.headerSparkPage("Custom", content, parent)
  }
}

object SparkUIExtender {
  def extend(sparkContext: SparkContext): Unit = {
    val ui = sparkContext.ui
    if (ui.isDefined) {
      ui.get.attachTab(new CustomUITab(ui.get))
    }
  }
}

