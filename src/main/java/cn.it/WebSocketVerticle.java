package cn.it;

import cn.it.common.EventBusAddrConsts;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 演示类，包含路由功能，参数接受等功能
 *
 * @author chengtao
 * @date 2020/11/21 13:34:06
 */
public class WebSocketVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketVerticle.class);

  private static final HashMap<String, ServerWebSocket> WS_SESSION = new HashMap<>();

  @Override
  public void start() throws Exception {

    EventBus bus = vertx.eventBus();

    Router router = Router.router(vertx);

    // 设置body参数处理器
    router.route().handler(BodyHandler.create());

    router.route().handler(StaticHandler.create("webroot"));

    router.route().last().failureHandler(rc -> rc.response().end("error")).handler(rc -> rc.response().end("404"));

    HttpServer httpServer = vertx.createHttpServer();
    httpServer.requestHandler(router);

    httpServer.webSocketHandler(ws -> {
      auth(bus, ws);

      ws.textMessageHandler(msg -> {
        bus.request(EventBusAddrConsts.STORE_MSG, msg, reply -> {
          // 消息存储成功
          if (reply.succeeded() && (boolean) reply.result().body()) {
            ws.writeTextMessage(msg);
          } else {
            logger.error(reply.cause().getMessage(), reply.cause());
          }
        });
      });

      ws.closeHandler(h -> {
        WS_SESSION.remove(ws.binaryHandlerID());
        System.out.println("close " + ws.binaryHandlerID());
      });

      ws.exceptionHandler(e -> {
        System.out.println("出现异常 " + e.getMessage());
        WS_SESSION.remove(ws.binaryHandlerID());
      });
    });

    httpServer.listen(8888);
  }

  private void auth(EventBus bus, ServerWebSocket ws) {
    // 鉴权，根据head中的token检查身份信息
    String token = ws.headers().get("token");
    bus.request(EventBusAddrConsts.USER_AUTH, token, res -> {
      // 认证成功
      if (res.succeeded() && (boolean) res.result().body()) {
        if (!WS_SESSION.containsKey(ws.binaryHandlerID())) {
          WS_SESSION.put(ws.binaryHandlerID(), ws);
          System.out.println(ws.binaryHandlerID() + "  connect");
        }
      } else {
        // fail
        System.out.println("connect fail" + res.cause().getMessage());
        ws.close((short) 403, "authentication fail");
      }
    });
  }
}
