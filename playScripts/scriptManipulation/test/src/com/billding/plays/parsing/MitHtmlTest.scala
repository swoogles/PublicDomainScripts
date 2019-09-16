package com.billding.plays.parsing
import utest._
import com.billding.plays.UnsafeWorld
import com.billding.plays.PlayCharacter

object MitHtmlTest extends TestSuite {
  import better.files.Dsl.cwd // TODO only using cwd
  val workingDirectory = cwd
  val unsafeWorld = new UnsafeWorld(workingDirectory)
  override def tests: Tests = Tests {
      val sceneTransition = """
<A NAME=speech55><b>ROMEO</b></a>
<blockquote>
<A NAME=2.2.201>Sleep dwell upon thine eyes, peace in thy breast!</A><br>
<A NAME=2.2.202>Would I were sleep and peace, so sweet to rest!</A><br>
<A NAME=2.2.203>Hence will I to my ghostly father's cell,</A><br>
<A NAME=2.2.204>His help to crave, and my dear hap to tell.</A><br>
<p><i>Exit</i></p>
</blockquote>
<h3>SCENE III. Friar Laurence's cell.</h3>
<p><blockquote>
<i>Enter FRIAR LAURENCE, with a basket</i>
</blockquote>
      """
      val actTransition = """
<A NAME=speech68><b>JULIET</b></a>
<blockquote>
<A NAME=3.5.248>Ancient damnation! O most wicked fiend!</A><br>
<A NAME=3.5.249>Is it more sin to wish me thus forsworn,</A><br>
<A NAME=3.5.250>Or to dispraise my lord with that same tongue</A><br>
<A NAME=3.5.251>Which she hath praised him with above compare</A><br>
<A NAME=3.5.252>So many thousand times? Go, counsellor;</A><br>
<A NAME=3.5.253>Thou and my bosom henceforth shall be twain.</A><br>
<A NAME=3.5.254>I'll to the friar, to know his remedy:</A><br>
<A NAME=3.5.255>If all else fail, myself have power to die.</A><br>
<p><i>Exit</i></p>
</blockquote><p>
<H3>ACT IV</h3>
<h3>SCENE I. Friar Laurence's cell.</h3>
<p><blockquote>
<i>Enter FRIAR LAURENCE and PARIS</i>
</blockquote>
      """
    val firstActTransition = """
<H3>ACT I</h3>
<h3>PROLOGUE</h3>
<blockquote>
<A NAME=1.0.1>Two households, both alike in dignity,</A><br>
<A NAME=1.0.2>In fair Verona, where we lay our scene,</A><br>
<A NAME=1.0.3>From ancient grudge break to new mutiny,</A><br>
<A NAME=1.0.4>Where civil blood makes civil hands unclean.</A><br>
<A NAME=1.0.5>From forth the fatal loins of these two foes</A><br>
<A NAME=1.0.6>A pair of star-cross'd lovers take their life;</A><br>
<A NAME=1.0.7>Whose misadventured piteous overthrows</A><br>
<A NAME=1.0.8>Do with their death bury their parents' strife.</A><br>
<A NAME=1.0.9>The fearful passage of their death-mark'd love,</A><br>
<A NAME=1.0.10>And the continuance of their parents' rage,</A><br>
<A NAME=1.0.11>Which, but their children's end, nought could remove,</A><br>
<A NAME=1.0.12>Is now the two hours' traffic of our stage;</A><br>
<A NAME=1.0.13>The which if you with patient ears attend,</A><br>
<A NAME=1.0.14>What here shall miss, our toil shall strive to mend.</A><br>
</blockquote>
<h3>SCENE I. Verona. A public place.</h3>
<p><blockquote>
<i>Enter SAMPSON and GREGORY, of the house of Capulet, armed with swords and bucklers</i>
</blockquote>
    """
    val htmlHeader = """
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
 "http://www.w3.org/TR/REC-html40/loose.dtd">
 <html>
 <head>
 <title>Romeo and Juliet: Entire Play
 </title>
 <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
 <LINK rel="stylesheet" type="text/css" media="screen"
       href="/shake.css">
 </HEAD>
 <body bgcolor="#ffffff" text="#000000">
    """

      val simpleString =
        """
            <A NAME=speech18><b>JULIET</b></a>
            <blockquote>
            <A NAME=3.5.60>O fortune, fortune! all men call thee fickle:</A><br>
            <A NAME=3.5.61>If thou art fickle, what dost thou with him.</A><br>
            <A NAME=3.5.62>That is renown'd for faith? Be fickle, fortune;</A><br>
            <A NAME=3.5.63>For then, I hope, thou wilt not keep him long,</A><br>
            <A NAME=3.5.64>But send him back.</A><br>
            </blockquote>
          """

          val closingLine = """
<A NAME=speech65><b>PRINCE</b></a>
<blockquote>
<A NAME=5.3.316>A glooming peace this morning with it brings;</A><br>
<A NAME=5.3.317>The sun, for sorrow, will not show his head:</A><br>
<A NAME=5.3.318>Go hence, to have more talk of these sad things;</A><br>
<A NAME=5.3.319>Some shall be pardon'd, and some punished:</A><br>
<A NAME=5.3.320>For never was a story of more woe</A><br>
<A NAME=5.3.321>Than this of Juliet and her Romeo.</A><br>
<p><i>Exeunt</i></p>
</body>
</html>



          """


    'ParseStageDirection - {
      val stageDirection =
        """
          <p><i>Exeunt</i></p>
        """
        val result = MitHtml.parseStageDirection(stageDirection, 3)
    }

    /*
    'RecognizeAndSplitSceneTransition - {
      val results = MitHtml.splitSceneTransitionWithoutActBreak(sceneTransition)
    }

    'RecognizeSceneTransition - {
      assert(
        MitHtml.Recognition.isSceneTransition(sceneTransition) == true
      )
    }

    'IgnoreNonSceneTransition - {
      assert(
        MitHtml.Recognition.isSceneTransition(actTransition) == false
      )
    }

    'RecognizeAndSplitActTransition - {
      val results = MitHtml.splitSceneTransitionWithActBreak(actTransition)
      results.map(_ + "\n").foreach(println)
    }

    'RecognizeActTransition - {
      assert(
        MitHtml.Recognition.isActTransition(actTransition) == true
      )
    }

    'IgnoreNonActTransition - {
      assert(
        MitHtml.Recognition.isActTransition(sceneTransition) == false
      )
    }

    'RecognizeActTransition - {
      assert(
        MitHtml.Recognition.isActTransition(firstActTransition) == true
      )
        val splitResult = MitHtml.splitSceneTransitionWithActBreak(firstActTransition)
    }

    'RecognizeHtmlBeginning - {
      assert(
      MitHtml.Recognition.isHtmlBeginning(htmlHeader) == true
      )
    }
    */

    'NormalizeFirstActOpening - {
        val splitResult = MitHtml.normalize(firstActTransition)
    }

    'NormalizeStandardActOpening - {
        val splitResult = MitHtml.normalize(actTransition)
    }

    'RecognizeSpokenLine - {
      assert(
      MitHtml.Recognition.isSpokenLine( simpleString) == true
      )
    }

    'RecognizeFinalSpokenLine - {
      assert(
      MitHtml.Recognition.isSpokenLine(closingLine) == true
      )
    }


    'ReadFileAsString - {
      unsafeWorld.getFileAsOneBigString("shakespeareGoodVersion.html").map(fileContents =>
      fileContents.split("\\n\\n")
      .flatMap(MitHtml.normalize)
      )
    }

    'ConvertFullScript - {
      unsafeWorld.getFileAsOneBigString("shakespeareGoodVersion.html").map { fileContents =>
        val results = fileContents.split("\\n\\n")
          .flatMap(MitHtml.normalize)
          .filter(!_.trim.isEmpty)
          .zipWithIndex
          .map { case (line, lineNumber) => MitHtml.Conversion.convert(line, lineNumber) }
          .map(_.toString)
          .toList
      }
//      unsafeWorld.writeNewLinesForPlay("romeoAndJuliet", "shakespeare" + ".parsed", results)
    }


    'SimpleParsing - {
      val result = MitHtml.parseSpokenLineTyped(simpleString, 5)
    }

    'ParseActTransition - {
      val input = "<H3>ACT IV</h3>"
      val result = MitHtml.parseActBreak(input, 2)
    }

    'ParseActTransition - {
      val input = "<H3>SCENE III</h3>"
      val result = MitHtml.parseSceneBreak(input, 8)
    }





  }

}
