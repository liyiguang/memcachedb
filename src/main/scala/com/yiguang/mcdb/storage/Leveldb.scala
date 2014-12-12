package com.yiguang.mcdb.storage

import java.io.File

import org.fusesource.leveldbjni.JniDBFactory._
import org.iq80.leveldb._
import org.slf4s.Logging

import scala.beans.BeanProperty
import Leveldb._


/**
 * Created by yigli on 14-12-4.
 */
class Leveldb(val dir: String, val config: Config = new Config) extends Storage with Logging  {


  private[this] var db: DB = _

  def init = {

    db = factory.open(new File(dir), config.options)
    log.info("Open leveldb directory="+dir)
    log.info("Open config="+config)
    log.info("The leveldb status:"+status)

  }

  def put(key:Array[Byte],value:Array[Byte]) = {

    db.put(key,value)

  }

  def get(key:Array[Byte]) = db.get(key)

  def get(key:Array[Byte],opts:ReadOptions) = db.get(key,opts)

  def delete(key:Array[Byte]) = db.delete(key)

  def delete(key:Array[Byte],opts:WriteOptions) = db.delete(key,opts)

  def property(key:String) = db.getProperty(key)

  def close = {

    log.info("closing leveldb...")

    db.close()
  }


  def status = {
    property(KEY_STATS)
  }

}

object Leveldb {
  private val KEY_STATS = "leveldb.stats"


  class Config {

    import com.yiguang.util.StorageUnitUtils._

    var createIfMissing: Boolean = true
    var errorIfExists: Boolean = false
    var writeBufferSize: Long = 1024 M
    var maxOpenFiles: Int = 1000
    var blockRestartInterval: Int = 16
    var blockSize: Long = 4 K
    var compressionType: CompressionType = CompressionType.SNAPPY
    var verifyChecksums: Boolean = true
    var paranoidChecks: Boolean = false
    var comparator: Option[DBComparator] = None
    var logger: Option[Logger] = None
    var cacheSize: Long = 512 M


    def options = {
      val opts = new Options
      opts.createIfMissing(createIfMissing)
      opts.errorIfExists(errorIfExists)
      opts.writeBufferSize(writeBufferSize.toInt)
      opts.maxOpenFiles(maxOpenFiles)
      opts.blockRestartInterval(blockRestartInterval)
      opts.blockSize(blockSize.toInt)
      opts.compressionType(compressionType)
      opts.verifyChecksums(verifyChecksums)
      opts.paranoidChecks(paranoidChecks)
      comparator.map(opts.comparator(_))
      logger.map(opts.logger(_))
      opts.cacheSize(cacheSize)

      opts
    }

    override def toString = {
      "Config{" +
        "createIfMissing=" + createIfMissing +
        ", errorIfExists=" + errorIfExists +
        ", writeBufferSize=" + writeBufferSize +
        ", maxOpenFiles=" + maxOpenFiles +
        ", blockRestartInterval=" + blockRestartInterval +
        ", blockSize=" + blockSize +
        ", compressionType=" + compressionType +
        ", verifyChecksums=" + verifyChecksums +
        ", paranoidChecks=" + paranoidChecks +
        ", comparator=" + comparator +
        ", logger=" + logger +
        ", cacheSize=" + cacheSize +
        '}'
    }

  }

}
