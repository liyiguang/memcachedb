package com.yiguang.mcdb

import java.io.File

import org.iq80.leveldb._
import org.fusesource.leveldbjni.JniDBFactory._
import com.yiguang.util.StringUtils._
import org.scalatest.{Matchers, FlatSpec}

import scala.reflect.io.Path
import scala.util.Random

/**
 * Created by yigli on 14-12-4.
 */
class LeveldbSpec extends FlatSpec with Matchers {

  private var db: DB = _

  "db" should "open" in {
    val opt = new Options
    opt.createIfMissing(true)
    opt.logger()
    db = factory.open(new File("testdb"),opt)
  }


  "key value " should "save " in {
    val key = Random.nextString(5)
    val value = Random.nextString(20)
    db.put(key,value)

    val loaded = db.get(key)
    assert(fromBytes(loaded) == value)
  }

  "key " should "deleted " in {
    val key = Random.nextString(5)
    val value = Random.nextString(20)
    db.put(key,value)

    db.delete(key)

    val loaded = db.get(key)
    assert(loaded==null)
  }


  "db" should "close" in {
    db.close()

    Path("testdb").deleteRecursively()
  }

}
