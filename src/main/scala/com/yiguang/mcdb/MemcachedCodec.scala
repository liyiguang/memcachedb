package com.yiguang.mcdb

import com.twitter.finagle._
import com.twitter.finagle.memcached.protocol._
import com.twitter.finagle.memcached.protocol.text.{MemcachedClientPipelineFactory, MemcachedServerPipelineFactory}
import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import com.twitter.finagle.transport.Transport
import com.twitter.util.Closable

/**
 * Created by yigli on 14-12-3.
 */
object MemcachedCodec  {
  def apply(stats: StatsReceiver = NullStatsReceiver) = new Memcached(stats)
  def get() = apply()
}

class Memcached(stats: StatsReceiver) extends CodecFactory[Command, Response] {

  def this() = this(NullStatsReceiver)

  def server = Function.const {
    new Codec[Command, Response] {
      def pipelineFactory = MemcachedServerPipelineFactory

      override def newServerDispatcher(transport: Transport[Any, Any], service: Service[Command, Response]): Closable =
        new McdbServerDispatcher(transport.cast[Response, Command],service)
    }
  }

  def client = Function.const {
    new Codec[Command, Response] {
      def pipelineFactory = MemcachedClientPipelineFactory
    }
  }
}

