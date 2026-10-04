package com.vektorraum.aviatorsbot.bot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Minimal HTTP status server for Docker health checks and preview.
  * Serves a simple status page on port 3000 so the app is visible in the preview.
  */
object StatusServer {

  def start(port: Int = 3000): Unit = {
    implicit val system: ActorSystem = ActorSystem("status-server")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
      pathEndOrSingleSlash {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, statusHtml))
        }
      } ~
      path("health") {
        complete(StatusCodes.OK)
      }

    Await.result(Http().bindAndHandle(route, "0.0.0.0", port), 30.seconds)
  }

  private val statusHtml =
    """<!DOCTYPE html>
      |<html lang="en">
      |<head>
      |  <meta charset="UTF-8">
      |  <meta name="viewport" content="width=device-width, initial-scale=1.0">
      |  <title>AviatorsBot</title>
      |  <style>
      |    body { font-family: -apple-system, system-ui, sans-serif; max-width: 600px; margin: 80px auto; text-align: center; color: #333; }
      |    h1 { font-size: 2em; }
      |    .icon { font-size: 4em; }
      |    a { color: #0066cc; }
      |    code { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; }
      |  </style>
      |</head>
      |<body>
      |  <div class="icon">&#9992;&#65039;</div>
      |  <h1>AviatorsBot is Running</h1>
      |  <p>A Telegram bot providing METAR/TAF aviation weather subscription services.</p>
      |  <p>Interact with the bot on Telegram: <a href="https://t.me/AviatorsBot">@AviatorsBot</a></p>
      |  <p>Send <code>/help</code> to the bot for a list of commands.</p>
      |</body>
      |</html>""".stripMargin
}
