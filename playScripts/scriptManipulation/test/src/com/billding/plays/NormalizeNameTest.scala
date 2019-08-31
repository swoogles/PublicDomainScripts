package com.billding.plays

import utest._

object NormalizedNameTest extends TestSuite {
  val tests = Tests {
    'AcceptSnakeCase - {
      println("Snake case only Conversion: " + pprint.pprintln(NormalizedName.fromRawName("snake_case_only")))
    }
    'DenyMixedCamelAndSnake - {
      println("First Conversion: " + pprint.pprintln(NormalizedName.fromRawName("mixedCaseAnd_snake")))
    }
    'HandleMultipleUppercaseLetters - {
      val result = NormalizedName.fromRawName("toKillAMockingBird")
      assert(result.get.camelCase == "toKillAMockingBird")
      assert(result.get.snakeCase == "to_kill_a_mocking_bird")

    }
    'aMidSummerNightsDream - {
      val result = NormalizedName.fromRawName("aMidSummerNightsDream")
      assert(result.get.camelCase == "aMidSummerNightsDream")
      assert(result.get.snakeCase == "a_mid_summer_nights_dream")

    }
    'allCapsName - {
      val result = NormalizedName.fromRawName("PIGGY")
      assert(result.get.camelCase == "piggy")
      assert(result.get.snakeCase == "piggy")

    }

    'allCapsWithSpaces - {
      val result = NormalizedName.fromRawName("SECOND GENTLEMAN")
      assert(result.get.camelCase == "secondGentleman")
      assert(result.get.snakeCase == "second_gentleman")
    }

    'allCapsSnake - {
      val result = NormalizedName.fromRawName("SECOND_GENTLEMAN")
      assert(result.get.camelCase == "secondGentleman")
      assert(result.get.snakeCase == "second_gentleman")
      assert(result.get.spacedUpperCase == "SECOND GENTLEMAN")
    }
    'SnakeAndSpaces - {
      val result = NormalizedName.fromRawName("First Citizen")
      assert(result.get.camelCase == "firstCitizen")
      assert(result.get.snakeCase == "first_citizen")
      assert(result.get.spacedUpperCase == "FIRST CITIZEN")
    }

    'allCapsWithTrailingPunctuation - {
      val result = NormalizedName.fromRawName("ROSENCRANTZ:")
      assert(result.get.camelCase == "rosencrantz")
      assert(result.get.snakeCase == "rosencrantz")
    }



  }
}

