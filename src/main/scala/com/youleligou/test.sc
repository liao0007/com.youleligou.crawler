val t: Option[Int] = Some(1)

t map {
  case Some(x: Int) => x
  case None => 1
} map {
  case Some(x) => x
  case None => 1
}