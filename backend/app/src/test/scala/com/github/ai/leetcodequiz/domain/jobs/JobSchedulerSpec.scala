package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.entity.exception.DomainError
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.direct.*

object JobSchedulerSpec extends ZIOSpecDefault {

  private val scheduler = JobScheduler[Int]()

  def spec: Spec[Any, DomainError] = suite("JobScheduler")(
    test("should resolve basic case") {
      defer {
        // arrange
        val map = Map(1 -> List.empty, 2 -> List(1), 3 -> List(2))

        // act
        val result = scheduler.createSchedule(map).run

        // assert
        assertTrue(result == List(1, 2, 3))
      }
    },
    test("should fail when circular dependency exists") {
      defer {
        // arrange
        val map = Map(1 -> List(3), 2 -> List(1), 3 -> List(1))

        // act
        val result = scheduler.createSchedule(map).exit.run

        // assert
        assert(result)(fails(hasMessage(containsString("Failed to schedule jobs"))))

      }
    }
  )
}
