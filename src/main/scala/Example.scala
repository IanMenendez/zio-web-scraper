import zio.Console
import zio.stream.ZSink

object Example {
    def streamTransformerExample(pageResult: PageResult): String = {
        pageResult match
            case ErrorScrape(url, err) => s"ERR $url with $err"
            case SuccessScrape(url, _) => s"SUCC $url"
    }

    val sinkConsumerExample = ZSink.foreach(res => Console.printLine(res))

}
