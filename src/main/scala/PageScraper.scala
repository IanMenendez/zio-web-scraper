import zio.*
import zio.Schedule.WithState
import zio.http.Client
import zio.stream.ZStream


case class ScrapeConfig(url : String, schedule: WithState[Long, Any, Any, Long])

trait PageResult

case class SuccessScrape(url: String, body: String) extends PageResult
case class ErrorScrape(url: String, err: Throwable) extends PageResult


class PageScraper(scrapeConfig: ScrapeConfig) {
    private val url = scrapeConfig.url
    private val schedule = scrapeConfig.schedule
    
    def scrapePage(): ZIO[Client, Throwable, PageResult] = {
        val response = for {
            response <- Client.request(url)
            body <- response.body.asString
        } yield SuccessScrape(url, body)

        response.catchAll(err => ZIO.succeed(ErrorScrape(url, err))) //TODO recover to success scrape from some errors
    }

    def pageStream(): ZStream[Client, Throwable, PageResult] = {
        ZStream.fromZIO(scrapePage()).repeat(schedule)
    }
}