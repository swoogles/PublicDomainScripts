package example

import utest._
import zio.{DefaultRuntime, Task}

object TrimValueFunctionalityTest extends TestSuite {
  private val runtime = new DefaultRuntime {}

  def tests = utest.Tests {
    'ScalaJSExample {
      assert(0 == 0)
      val result: Set[Int] = runtime.unsafeRun(TrimValueFunctionality.indicesToKeep("0,5"))

      assert(result == Set(0,1,2,3,4,5))
    }

    'MultipleIndexRanges {
      assert(0 == 0)
      val result: Set[Int] = runtime.unsafeRun(TrimValueFunctionality.multipleIndexRangesToKeep("[0,3][6,10][15,22]"))

      assert(result == Set(0,1,2,3,4,5))
    }
  }
}
