package com.github.ai.leetcodequiz.codegen.model

case class KotlinType(
  packageName: String,
  imports: List[String],
  typeName: String,
  fields: List[Field]
)
