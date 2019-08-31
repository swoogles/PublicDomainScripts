package example

sealed trait ScrollingTarget {
  val token: String
}
case object Prev extends ScrollingTarget {
  val token = "previous"

}
case object Next extends ScrollingTarget {
  val token = "next"
}

