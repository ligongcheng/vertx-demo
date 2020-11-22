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
public class UserVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(UserVerticle.class);

  @Override
  public void start() throws Exception {

    EventBus bus = vertx.eventBus();

/*    Router router = Router.router(vertx);

    router.route("/user").handler( rc -> {
      rc.response().end("user verticle");
    });

    HttpServer httpServer = vertx.createHttpServer();

    httpServer.requestHandler(router);

    httpServer.listen(8888);*/

    bus.consumer(EventBusAddrConsts.USER_AUTH, msg -> {
      msg.reply(true);
      logger.debug("auth success");
    });

  }
}
