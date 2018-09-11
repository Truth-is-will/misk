package misk.grpc

import com.google.inject.util.Modules
import misk.MiskServiceModule
import misk.grpc.protocclient.GrpcChannelFactory
import misk.grpc.protocserver.ProtocGrpcService
import misk.grpc.protocserver.RouteGuideProtocServiceModule
import misk.testing.MiskTest
import misk.testing.MiskTestModule
import misk.web.Http2Testing
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import routeguide.RouteGuideGrpc
import routeguide.RouteGuideProto.Feature
import routeguide.RouteGuideProto.Point
import javax.inject.Inject

@MiskTest(startService = true)
class ProtocClientProtocServerTest {
  @MiskTestModule
  val module = Modules.combine(MiskServiceModule(), RouteGuideProtocServiceModule())

  @Inject lateinit var protocGrpcService: ProtocGrpcService
  @Inject lateinit var grpcChannelFactory: GrpcChannelFactory

  @Test
  fun requestResponse() {
    assumeTrue(Http2Testing.isJava9OrNewer())

    val channel = grpcChannelFactory.createClientChannel(protocGrpcService.socketAddress)
    val stub = RouteGuideGrpc.newBlockingStub(channel)

    val feature = stub.getFeature(Point.newBuilder()
        .setLatitude(43)
        .setLongitude(-80)
        .build())
    assertThat(feature).isEqualTo(Feature.newBuilder()
        .setName("pine tree")
        .setLocation(Point.newBuilder()
            .setLatitude(43)
            .setLongitude(-80)
            .build())
        .build())
  }
}