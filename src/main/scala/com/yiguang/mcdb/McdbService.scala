package com.yiguang.mcdb

import java.util.concurrent._

import com.twitter.concurrent.{ExecutorScheduler, ThreadPoolScheduler, Scheduler}
import com.twitter.finagle.Service
import com.twitter.finagle.memcached.protocol.{ServerError, _}
import com.twitter.util.{Throw, Return, FutureTask, Future}
import com.yiguang.mcdb.storage.Storage
import com.yiguang.util.ChannelBufferUtils
import org.jboss.netty.buffer.ChannelBuffer
import org.slf4s.Logging
import scala.collection.mutable
/**
 * Created by yigli on 14-12-5.
 */
class McdbService(val storage:Storage) extends Service[Command, Response] with Logging {

  import com.yiguang.util.ChannelBufferUtils._

  override def apply(request: Command): Future[Response] = {

    request match {
      case Get(keys) =>
        if (keys.length == 1) get(keys(0)) else gets(keys)

      case Gets(keys) =>
        if (keys.length == 1) get(keys(0)) else gets(keys)

      case Set(key,flags,expiry,value) => set(key,value)

      case Delete(key) => delete(key)

      case Quit() => Future.value(Exists())

      case _ =>
        log.warn("Unspport Command:"+request)
        Future.value(Exists())
    }

  }

  def get(key:ChannelBuffer):Future[Response] = {

    db(storage.get(key)).map {
      v => Values(Seq(Value(key,v)))
    }.rescue{
      case e:Throwable =>
        log.error("Get key="+key + " failed",e)
        Future.value(Error(new ServerError(e.getMessage)))
    }
  }

  def gets(keys:Seq[ChannelBuffer]):Future[Response] = {

    if(keys.size > 64){
      return Future.value(Error(new ServerError("too much keys")))
    }

    var futures = mutable.Seq()

    for(key <- keys){
      val f = db(storage.get(key))
      futures :+ f
    }

    Future.collect(futures) map { s =>
      val datas = for((key,value) <-keys.zip(s)) yield Value(key,value)
      Values(datas)
    }rescue{
      case e:Throwable =>
        log.error("Get keys size="+keys.size + " failed",e)
        Future.value(Error(new ServerError(e.getMessage)))
    }
  }

  def set(key:ChannelBuffer,value:ChannelBuffer):Future[Response] = {

    db(storage.put(key,value)).map(_ => Stored()).rescue {
      case e:Throwable =>
        log.error("Get key="+key + " failed",e)
        Future.value(Error(new ServerError(e.getMessage)))
    }


  }

  def delete(key:ChannelBuffer):Future[Response] = {
    db(storage.delete(key)).map(_ => Deleted()).rescue {
      case e:Throwable =>
        log.error("Get key="+key + " failed",e)
        Future.value(Error(new ServerError(e.getMessage)))
    }
  }

  private[this] val scheduler: Scheduler = new MyScheduler("McdbService")

  private[this] def db[R](f: => R): Future[R] = {
    val task = FutureTask(f)
    scheduler.submit(task)
    task
  }


  private[this] class MyScheduler(
    val name: String,
    val executorFactory: ThreadFactory => ExecutorService
    ) extends Scheduler with ExecutorScheduler {
    def this(name: String) = this(name,threadPool(_))
  }

  private[this] def threadPool(threadFactory:ThreadFactory) = {
    new ThreadPoolExecutor(16, 16, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable], threadFactory)
  }
}
