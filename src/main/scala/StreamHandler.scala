import zio.stream.ZStream
import zio.*
import zio.http.Client

import scala.io.Source
import java.io.IOException

object StreamHandler {
    private val urlsPath = "src/main/resources/urls.txt"

    def acquire(name: => String): ZIO[Any, IOException, Source] =
        ZIO.attemptBlockingIO(Source.fromFile(name))

    def release(source: => Source): ZIO[Any, Nothing, Unit] =
        ZIO.succeedBlocking(source.close())

    def source(name: => String): ZIO[Scope, IOException, Source] =
        ZIO.acquireRelease(acquire(name))(release(_))

    def readUrls(): ZIO[zio.Scope, java.io.IOException, List[ScrapeConfig]] = {
        val defaultSchedule = Schedule.spaced(1.hour)

        source(urlsPath).flatMap { source =>
            ZIO.attemptBlockingIO(source.getLines().map(line => line.split(" +") match
                case Array(url, schedule) => ScrapeConfig(url, Schedule.spaced(schedule.toInt.hour))
                case Array(url) => ScrapeConfig(url, defaultSchedule)
            ).toList)
        }
    }

    def buildStream(): ZIO[zio.Scope, java.io.IOException, ZStream[Client, Throwable, PageResult]] = {
        for {
            configs <- readUrls()
            pageStreams = configs.map(config => PageScraper(ScrapeConfig(config.url, config.schedule)).pageStream())
        } yield ZStream.mergeAllUnbounded(configs.length)(pageStreams: _*)
    }

    def transformStreamResults(stream: ZStream[Client, Throwable, PageResult], nPar: Int, function: PageResult => Any): ZStream[Client, Throwable, Any] = {
        stream.mapZIOParUnordered(nPar)(res => ZIO.succeed(function(res)))
    }
}
