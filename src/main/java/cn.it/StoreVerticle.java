package cn.it;

import cn.it.common.EventBusAddrConsts;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 持久化消息
 *
 * @author chengtao
 * @date 2020/11/22 13:05:00
 */
public class StoreVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(StoreVerticle.class);

  @Override
  public void start() throws Exception {

    EventBus bus = vertx.eventBus();

/*    Router router = Router.router(vertx);

    router.route("/store").handler( rc -> {
      rc.response().end("store verticle");
    });

    HttpServer httpServer = vertx.createHttpServer();

    httpServer.requestHandler(router);

    httpServer.listen(8888);*/

    bus.consumer(EventBusAddrConsts.STORE_MSG, msg -> {
      System.out.println(msg.body().toString());
      msg.reply(true);
      logger.debug("store success");
      //msg.fail(400,"fail store");
    });

  }
}
