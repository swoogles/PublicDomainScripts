package example

import utest._

object ScalaJSExampleTest extends TestSuite {

  import ScalaJSExample._

  def tests = TestSuite {
    'ScalaJSExample {
      assert(0 == 0)
    }
  }
}
