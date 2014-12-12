package com.yiguang.util

import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}

/**
 * Created by yigli on 14-12-5.
 */
object ChannelBufferUtils {

  implicit def channelBufferToArrayByte(cb:ChannelBuffer):Array[Byte] = {
    val length = cb.readableBytes()
    val bytes = new Array[Byte](length)
    cb.getBytes(cb.readerIndex(), bytes, 0, length)
    bytes
  }

  implicit def arrayByteToChannelBuffer(a:Array[Byte]):ChannelBuffer = {
    ChannelBuffers.wrappedBuffer(a)
  }

  implicit def channelBufferToString(cb:ChannelBuffer):String = {
    StringUtils.fromBytes(channelBufferToArrayByte(cb))
  }
}
