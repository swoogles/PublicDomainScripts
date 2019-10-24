package example

object TrimRange {
  def safelyConstructed(start: Int, end: Int): Either[String, TrimRange] = {
    if (start > end)
      Left(s"start must be <= end. start=$start end=$end")
    else
      Right(TrimRange(start, end))
  }}

case class TrimRange(start: Int, end: Int) {
  assert(start < end)
}
