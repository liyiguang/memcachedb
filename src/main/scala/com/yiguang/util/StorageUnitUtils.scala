package com.yiguang.util

/**
 * Created by yigli on 14-12-4.
 */

object StorageUnitUtils {

  private val SIZE_K = 1024L
  private val SIZE_M = 1024L * SIZE_K
  private val SIZE_G = 1024L * SIZE_M
  private val SIZE_T = 1024L * SIZE_G

  case class Size(val mul:Int) {

    def K = mul * SIZE_K
    def M = mul * SIZE_M
    def G = mul * SIZE_G
    def T = mul * SIZE_T
  }


  implicit def intToSize(mul:Int):Size = Size(mul)

}
