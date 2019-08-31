package com.billding.plays

import utest._

object ContentManipulationTest extends TestSuite {
  val tests = Tests {
    'WordSurroundedWithQuotesAndPunctuation - {
      val word = "\"Yes,\""
      val expected = "\"Y,\""
      assert(
        ContentManipulation.firstLetterOfWord(word)
          == expected
      )
    }

    'Ellipse - {
      val lineWithQuotesAndEllipses = """ I’ll make him laugh just by being me ... I’ll love man unconditionally and remind man who he’s supposed to be. """
      val expected = "I m h l j b b m ... I l m u a r m w h s t b."

        assert(
          ContentManipulation.firstLetterOfEachWord(lineWithQuotesAndEllipses)
            == expected
        )
    }

    'WithQuotesAndEllipse - {
      val lineWithQuotesAndEllipses = """
      "Yes," Wolf said "but give me a chance to change man.
      I’ll make sure he goes outside every day to play, I’ll comfort him when he’s sick or sad,
      I’ll be his guide when he cannot see, protect him when he’s in danger, I’ll round up his sheep,
      I’ll make him laugh just by being me ... I’ll love man unconditionally and remind man who he’s supposed to be."
      """
      val expectedResult = "\"Y,\" W s \"b g m a c t c m. I m s h g o e d t p, I c h w h s o s, I b h g w h c s, p h w h i d, I r u h s, I m h l j b b m ... I l m u a r m w h s t b.\""

      val actualResults = ContentManipulation.firstLetterOfEachWord(lineWithQuotesAndEllipses)

      assert(actualResults == expectedResult)
    }

    'FirstWordOfMultipleSentences - {
      val multipleSentences = "I am Sam. I do not like ham! He does though."
      val expected =          "I __ ___. I __ ___ ____ ___! He ____ ______."

      assert(
        ContentManipulation.firstWordOfEachSentence(multipleSentences)
          == expected
      )
    }

    'LastSentenceOfLine - {
      val multipleSentences = "I am Sam. I do not like ham! He does though."
      val expected =          "He does though."
      assert(
        ContentManipulation.lastSentence(multipleSentences)
          == expected
      )
    }

  }
}
