package com.github.ai.leetcodequiz.entity

enum Difficulty {
  case UNDEFINED, EASY, MEDIUM, HARD
}

object Difficulty {
  def from(value: String): Option[Difficulty] = {
    value.toLowerCase() match {
      case "easy" => Some(Difficulty.EASY)
      case "medium" => Some(Difficulty.MEDIUM)
      case "hard" => Some(Difficulty.HARD)
      case _ => None
    }
  }
}
