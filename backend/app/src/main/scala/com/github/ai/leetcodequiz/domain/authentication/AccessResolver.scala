package com.github.ai.leetcodequiz.domain.authentication

import com.github.ai.leetcodequiz.data.db.repository.UserRepository
import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.direct.*

class AccessResolver(
  private val jwtTokenService: JwtTokeService,
  private val userRepository: UserRepository
) {

  def validateToken(jwtToken: String): IO[DomainError, Boolean] = defer {
    val userUid = jwtTokenService.validateToken(jwtToken).run
    val user = userRepository.findByUid(userUid).run
    if (user.isDefined) {
      true
    } else {
      ZIO.fail(DomainError("Invalid auth token")).run
    }
  }

}
