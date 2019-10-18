package example


object TrimValueFunctionality {
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

  val getTrimButtons: Function[ReadFromTheWorld[Document], List[Button]] = ???
  val getTrimButtonsMonad: Effect[ReadFromTheWorld[Document]] = ???

  val trimButtonsIntoValues: Function[List[Button], TrimRange] = ???
  val applyTrimRangeToCurrentUrl: Function[TrimRange, Url] = ???
  val navigateToUrlInBrowser: Function[Url, WriteToTheWorld[Document]] = ???
  val navigateToUrlInBrowserMonad: WriteToTheWorld[Document] = ???

  val browseToStripDownScriptSpecifiedByInputFields: ReadFromTheWorld[Document] => WriteToTheWorld[Document] =
    getTrimButtons
      .andThen(trimButtonsIntoValues)
      .andThen(applyTrimRangeToCurrentUrl)
      .andThen(navigateToUrlInBrowser)

  val realProgramThatGetsTrimButtons: Effect[List[Button]] =
    getTrimButtonsMonad
      .map(getTrimButtons)

  val roundTripProgram: Effect[Document] =
    realProgramThatGetsTrimButtons
      .flatMap(input => navigateToUrlInBrowserMonad)


  def mainMethod () = {
    val documentFromEnvironment: Document = ???
    browseToStripDownScriptSpecifiedByInputFields
      .andThen(writeToTheWorld => writeToTheWorld.unsafeRun(documentFromEnvironment))
  }

  class Document
  class Button()
  class TrimRange(start: Int, end: Int)
  class QueryParameter(name: String, value: String)
  class Url()

  def createUrlWithNewTrimRange(baseUrl: String): Url = ???

}
