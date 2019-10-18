package example


object TrimValueFunctionality {
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
      .flatMap( input => navigateToUrlInBrowserMonad)


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
