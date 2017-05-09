package com.youleligou.core.services

/** GeoHash encoding/decoding as per http://en.wikipedia.org/wiki/Geohash */
class GeoHash {

  val LAT_RANGE = (-90.0, 90.0)
  val LON_RANGE = (-180.0, 180.0)

  // Aliases, utility functions
  type Bounds = (Double, Double)
  private def mid(b: Bounds) = (b._1 + b._2) / 2.0
  implicit class BoundedNum(x: Double) { def in(b: Bounds): Boolean = x >= b._1 && x <= b._2 }

  /**
   * Encode lat/long as a base32 geohash.
   *
   * Precision (optional) is the number of base32 chars desired; default is 12, which gives precision well under a meter.
   */
  def encode(lat: Double, lon: Double, precision: Int=11): String = { // scalastyle:ignore
    require(lat in LAT_RANGE, "Latitude out of range")
    require(lon in LON_RANGE, "Longitude out of range")
    require(precision > 0, "Precision must be a positive integer")
    val rem = precision % 2 // if precision is odd, we need an extra bit so the total bits divide by 5
    val numbits = (precision * 5) / 2
    val latBits = findBits(lat, LAT_RANGE, numbits)
    val lonBits = findBits(lon, LON_RANGE, numbits + rem)
    val bits = intercalate(lonBits, latBits)
    bits.grouped(5).map(toBase32).mkString // scalastyle:ignore
  }

  private def findBits(part: Double, bounds: Bounds, p: Int): List[Boolean] = {
    if (p == 0) Nil
    else {
      val avg = mid(bounds)
      if (part >= avg) true :: findBits(part, (avg, bounds._2), p - 1) // >= to match geohash.org encoding
      else false :: findBits(part, (bounds._1, avg), p - 1)
    }
  }

  /**
   * Decode a base32 geohash into a tuple of (lat, lon)
   */
  def decode(hash: String): (Double, Double) = {
    require(isValid(hash), "Not a valid Base32 number")
    val (odd, even) = extracalate(toBits(hash))
    val lon = mid(decodeBits(LON_RANGE, odd))
    val lat = mid(decodeBits(LAT_RANGE, even))
    (lat, lon)
  }

  private def decodeBits(bounds: Bounds, bits: Seq[Boolean]) =
    bits.foldLeft(bounds)((acc, bit) => if (bit) (mid(acc), acc._2) else (acc._1, mid(acc)))



  val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"
  val BITS = Array(16, 8, 4, 2, 1) // scalastyle:ignore
  val TODEC = Map(BASE32.zipWithIndex: _*)

  /** Convert list of boolean bits to a base-32 character. Only the first 5 bits are considered.*/
  def toBase32(bin: Seq[Boolean]): Char = BASE32((BITS zip bin).collect { case (x, true) => x }.sum)

  private def intToBits(i: Int) = (4 to 0 by -1) map (x => (i >> x & 1) == 1)

  def isValid(s: String): Boolean = !s.isEmpty() && s.forall(TODEC.contains(_))

  /** Convert a base-32 string to a list of bits (booleans) */
  def toBits(s: String): Seq[Boolean] = (s.flatMap(TODEC andThen intToBits))


  /**
    * Interlace two lists.
    *
    * E.g. intercalate(List(1,3,5), List(2,4)) == List(1,2,3,4,5)
    *
    * "Extra" numbers, if the lists have unequal lengths, will be included on the tail of the output list.
    */
  def intercalate[A](a: List[A], b: List[A]): List[A] = a match {
    case h :: t => h :: intercalate(b, t)
    case _      => b
  }

  /**
    * De-interlace two lists.
    *
    * E.g. extracalate(List(1,2,3,4,5)) == (List(1,3,5), List(2,4))
    */
  def extracalate[A](a: Seq[A]): (List[A], List[A]) =
    a.foldRight((List[A](), List[A]())) { case (b, (a1, a2)) => (b :: a2, a1) }

}
