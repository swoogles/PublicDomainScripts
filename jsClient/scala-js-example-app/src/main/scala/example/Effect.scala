package example

trait Effect[A] {
  def unsafeRun(a: A): Void
  def map[B](func: Function[A, B]): Effect[B]
  def flatMap[B](func: Function[A, Effect[B]]): Effect[B]
}
trait ReadFromTheWorld[A] extends Effect[Void] {
  def getValue(): Effect[A]
}
trait WriteToTheWorld[A] extends Effect[A] {
  def writeValue(a: A): Effect[Void]
}

