package com.github.ai.leetcodequiz.domain

import com.github.ai.leetcodequiz.entity.HttpProtocol.HTTP
import com.github.ai.leetcodequiz.entity.{AppEnvironment, CliArguments, HttpProtocol}
import com.github.ai.leetcodequiz.entity.exception.{DomainError, ParsingError}
import zio.*
import zio.direct.*

import scala.collection.mutable

@SuppressWarnings(
  Array("org.wartremover.warts.MutableDataStructures", "org.wartremover.warts.Throw")
)
class CliArgumentParser {

  def parse(): ZIO[ZIOAppArgs, DomainError, CliArguments] = {
    defer {
      val args = ZIO.service[ZIOAppArgs].run
      parseArguments(args.getArgs.toList).run
    }
  }

  private def parseArguments(args: List[String]): IO[DomainError, CliArguments] = {
    ZIO
      .attempt {
        var environment = AppEnvironment.PROD
        var populateData = false
        var protocol: Option[HttpProtocol] = None

        val queue = mutable.Queue[String]()
        queue.addAll(args)

        while (queue.nonEmpty) {
          val optionName = queue.removeHead()
          optionName match {
            case CliOptions.Debug.cliName => environment = AppEnvironment.DEBUG
            case CliOptions.Protocol.cliName => {
              val protocolValue = queue
                .removeHeadOption()
                .flatMap(value => HttpProtocol.fromString(value))

              protocolValue match {
                case Some(p) => protocol = Some(p)
                case None =>
                  throw ParsingError(
                    s"Invalid option: ${CliOptions.Protocol.cliName}. Expected 'http' or 'https'"
                  )
              }
            }

            case _ =>
              throw ParsingError(s"Invalid option specified: '$optionName'")
          }
        }

        if (protocol.isEmpty) {
          throw ParsingError(s"Option '${CliOptions.Protocol.cliName}' is required ")
        }

        CliArguments(
          environment = environment,
          protocol = protocol.getOrElse(HTTP)
        )
      }
      .mapError(error => DomainError(cause = error))
  }

  private enum CliOptions(val cliName: String) {
    case Debug extends CliOptions("--debug")
    case Protocol extends CliOptions("--protocol")
  }
}
