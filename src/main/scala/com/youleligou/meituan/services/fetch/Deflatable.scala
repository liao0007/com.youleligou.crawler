package com.youleligou.meituan.services.fetch

import java.util.Base64
import java.util.zip.Deflater

import play.api.libs.json.Json

/**
  * Created by liangliao on 8/5/17.
  */
trait Deflatable {
  def deflate(string: String): String = {
    //deflate string

    val input = string.getBytes("UTF-8")
    // Compress the bytes
    val output: Array[Byte]  = new Array[Byte](100)
    val compresser: Deflater = new Deflater
    compresser.setInput(input)
    compresser.finish()
    val compressedDataLength: Int = compresser.deflate(output)
    compresser.end()

    new String(Base64.getEncoder.encode(output))
  }

  def deflate(parameter: Map[String, String]): String = {
    //json stringify
    val jsonString = Json.toJson(parameter).toString()
    deflate(jsonString)
  }
}
