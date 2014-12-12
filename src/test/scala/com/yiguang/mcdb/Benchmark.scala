package com.yiguang.mcdb

import java.util.concurrent.CountDownLatch

import com.yiguang.mcdb.storage.Leveldb

import scala.util.Random

import com.yiguang.util.StringUtils._

/**
 * Created by yigli on 14-12-11.
 */
object Benchmark extends App {

  val nThread = 100

  val leveldbConfig = new Leveldb.Config

  val leveldb = new Leveldb(Config.Leveldb.directory,leveldbConfig)
  leveldb.init

  val count = 10000
  val latch = new CountDownLatch(nThread)

  val r = new Runnable {
    override def run(): Unit = {
      var c:Int = 0
      var time:Long = 0L
      while(c < count){
        val s = System.currentTimeMillis()
        leveldb.put(Random.nextString(8),Random.nextString(8))
        time += (System.currentTimeMillis() - s)
        c+=1
      }

      println("resp:"+ (time / c))

      latch.countDown()
    }
  }

  val start = System.currentTimeMillis()
  for(i <- 0 until nThread ){
    new Thread(r).start()
  }
  latch.await()
  leveldb.close

  val time = System.currentTimeMillis() - start
  val qps = count * nThread * 1000 / time


  println("Average QPS="+qps)
}
