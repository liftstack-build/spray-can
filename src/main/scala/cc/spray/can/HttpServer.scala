/*
 * Copyright (C) 2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.spray.can

import akka.config.Supervision._
import akka.actor.{PoisonPill, Supervisor, Actor}

class HttpServer(val config: CanConfig) extends SelectActorComponent {
  private lazy val selectActor = Actor.actorOf(new SelectActor)

  def start() {
    // start and supervise the selectActor
    Supervisor(
      SupervisorConfig(
        OneForOneStrategy(List(classOf[Exception]), 3, 100),
        List(Supervise(selectActor, Permanent))
      )
    )
  }

  def shutdown() {
    selectActor ! PoisonPill
  }

  def blockUntilShutdown() {
    stopped.await()
  }

}