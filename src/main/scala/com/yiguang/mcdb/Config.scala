package com.yiguang.mcdb

import com.typesafe.config.ConfigFactory

/**
 * Created by yigli on 14-12-5.
 */
object Config {

  val conf = ConfigFactory.load()

  val serverPort = conf.getInt("server-port")

  object Leveldb {
    val directory = conf.getString("storage.leveldb.directory")
  }
}
