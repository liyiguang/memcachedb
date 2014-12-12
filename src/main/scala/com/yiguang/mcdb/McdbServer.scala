package com.yiguang.mcdb

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.netty3.Netty3Listener
import com.twitter.util.Duration
import com.yiguang.mcdb.storage.Leveldb
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory

import scala.concurrent.duration._

import com.twitter.finagle.netty3

/**
 * Created by yigli on 14-12-5.
 */
object McdbServer extends App {

  val leveldbConfig = new Leveldb.Config

  val leveldb = new Leveldb(Config.Leveldb.directory,leveldbConfig)
  leveldb.init

  val server = ServerBuilder()
    .name("McdbServer")
    .codec(MemcachedCodec())
    .bindTo(new InetSocketAddress(Config.serverPort))
    .recvBufferSize(256*1024*1024)
    .sendBufferSize(256*1024*1024)
    .hostConnectionMaxIdleTime(Duration(5,MINUTES))
    .channelFactory(new NioServerSocketChannelFactory(netty3.Executor,2,netty3.WorkerPool))
    .build(new McdbService(leveldb))


  println("...Started")


  sys addShutdownHook {
    server.close()
    leveldb.close
  }


}


