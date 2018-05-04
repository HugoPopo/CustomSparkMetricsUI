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

package org.apache.spark.ui

import java.util.{Observable, Observer}

import javax.servlet.http.HttpServletRequest

import scala.xml.Node
import org.apache.spark.StatusHolder
import org.apache.spark.SparkContext

/**
 * A standard class for an additional custom UI tab
 *
 * @param parent the UI registering the custom tab
 */
class CustomUITab(parent: SparkUI) extends SparkUITab(parent, "custom"){
  attachPage(new CustomWebUIPage(this))
}

/**
 * A standard class for an additional page in a tab
 *
 * @param parent the tab the page is attached to
 */
class CustomWebUIPage(parent: SparkUITab) extends WebUIPage("") with Observer {
  def render(request: HttpServletRequest): Seq[Node] = {
    val content =
      <div>
        {
          <p>This is a custom tab</p>
          <p>max memory
            {try {
              StatusHolder.status.get("maxMemory")
            } catch {
              case null => "not defined"
            }}
          </p>
        }
      </div>
      UIUtils.headerSparkPage("Custom", content, parent)
  }

  override def update(o: Observable, arg: scala.Any): Unit = {
    val updatedValue = StatusHolder.status.get(arg.toString)
    // TODO update the HTML
  }
}

/** An object registering the custom tab in the SparkUI, from SparkContext */
object SparkUIExtender {
  def extend(sparkContext: SparkContext): Unit = {
    val ui = sparkContext.ui
    if (ui.isDefined) {
      val customUITab = new CustomUITab(ui.get)
      ui.get.attachTab(customUITab)
      customUITab.pages.foreach {
        case page: CustomWebUIPage =>
          StatusHolder.addObserver(page)
        case _ =>
      }
    }
  }
}

