package com.yiguang.mcdb.storage

import org.iq80.leveldb.{WriteOptions, ReadOptions}

/**
 * Created by yigli on 14-12-4.
 */
abstract class Storage {
  def put(key:Array[Byte],value:Array[Byte])

  def get(key:Array[Byte]):Array[Byte]

  def get(key:Array[Byte],opts:ReadOptions):Array[Byte]

  def delete(key:Array[Byte])

  def delete(key:Array[Byte],opts:WriteOptions)
}
