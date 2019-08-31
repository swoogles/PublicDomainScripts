package com.billding.plays

import scala.util.{Try, Success, Failure}
import com.google.common.base.CaseFormat

case class NormalizedName(
    snakeCase: String,
    camelCase: String,
    spacedUpperCase: String
) {
  val upperSnakeCase: String = snakeCase.toUpperCase
}

object NormalizedName {
  def fromRawName(inputRaw: String): Try[NormalizedName] = {
    val middleInput =
      if (inputRaw.split(" +").length > 1 && inputRaw
            .split(" +")
            .exists(word => word.exists(_.isUpper) && word.exists(_.isLower))) {
        inputRaw.toUpperCase
      } else {
        inputRaw
      }
    val input = middleInput.replace(":", "").replaceAll(" +", " ").trim
    if (input.forall(
          letter => letter.isUpper || letter == '_' && letter != ' '
        )) {
      Success(
        NormalizedName(
          input.toLowerCase(),
          CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, input),
          input.replace("_", " ")
        )
      )

    } else if (input.contains('_')) {
      if (input.exists(_.isUpper))
        Failure(
          new Exception("If you have a snake, you shouldn't have any camels!")
        )
      else
        Success(
          NormalizedName(
            input,
            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, input),
            input.replace("_", " ").toUpperCase
          )
        )
    } else if (input.exists(_.isUpper) && !input.forall(
                 letter => letter.isUpper || letter == ' '
               )) {
      if (input.contains('_')) {
        Failure(
          new Exception("If you have a camel, you shouldn't have any snakes!")
        )
      } else {
        Success(
          NormalizedName(
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input),
            input,
            CaseFormat.UPPER_CAMEL
              .to(CaseFormat.LOWER_UNDERSCORE, input)
              .replace("_", " ")
              .toUpperCase
          )
        )
      }
    } else if (input.forall(letter => letter.isUpper || letter == ' ')) {
      // Name is all caps with spaces
      Success(
        NormalizedName(
          input.replace(' ', '_').toLowerCase(),
          CaseFormat.UPPER_UNDERSCORE
            .to(CaseFormat.LOWER_CAMEL, input.replace(' ', '_')),
          input
        )
      )
    } else {
      // Name is ambiguously simple. Both versions are the same.
      Success(
        NormalizedName(
          input,
          input,
          input.toUpperCase
        )
      )
    }

  }

  def snake2camel(text: String): String = ???

  def camel2snake(text: String) =
    text.drop(1).foldLeft(text.headOption.map(_.toLower + "") getOrElse "") {
      case (acc, c) if c.isUpper => acc + "_" + c.toLower
      case (acc, c)              => acc + c
    }
}
