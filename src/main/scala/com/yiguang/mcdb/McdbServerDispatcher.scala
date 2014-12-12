package com.yiguang.mcdb

import com.twitter.finagle.Service
import com.twitter.finagle.dispatch.GenSerialServerDispatcher
import com.twitter.finagle.memcached.protocol.{Exists, Command, Response}
import com.twitter.finagle.transport.Transport
import com.twitter.util.{Future, Promise}

/**
 * Created by yigli on 14-12-3.
 */
class McdbServerDispatcher( val transport: Transport[Response,Command],
                          service: Service[Command, Response])

  extends GenSerialServerDispatcher[Command,Response ,Response, Command ](transport) {

  override protected def dispatch(req: Command, eos: Promise[Unit]): Future[Response] =
    service(req) ensure eos.setDone()

  override protected def handle(rep: Response): Future[Unit] = {
    if(rep == null){
      return Future.Unit
    }

    rep match {

      case Exists() => transport.close()

      case _ => transport.write(rep)
    }
  }
}

