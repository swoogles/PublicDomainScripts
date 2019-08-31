package com.billding.plays

case class PlayCharacter(name: String, description: String = "a character") {
  val normalizedName = NormalizedName.fromRawName(name).get // TODO Unsafe
}
