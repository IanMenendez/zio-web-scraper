import zio.*
import zio.Schedule.WithState
import zio.http.{Client, ZClient}
import zio.stream.ZSink

import java.io.IOException

object MainApp extends ZIOAppDefault {
    val defaultServices = Client.default ++ Scope.default //TODO use Client with different config, ips and proxy
    val scrapersStream = StreamHandler.buildStream()
    val nPar = 16

    def run = scrapersStream.flatMap(stream => StreamHandler.transformStreamResults(stream, nPar, Example.streamTransformerExample).run(Example.sinkConsumerExample)).provideLayer(defaultServices)


}
