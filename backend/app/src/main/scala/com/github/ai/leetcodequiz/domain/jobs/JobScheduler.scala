package com.github.ai.leetcodequiz.domain.jobs

import com.github.ai.leetcodequiz.entity.exception.DomainError

import scala.collection.mutable
import zio.*

class JobScheduler[T] {

  def createSchedule(
    jobToDependenciesMap: Map[T, List[T]]
  ): IO[DomainError, List[T]] = {
    val resolved = mutable.LinkedHashSet[T]()
    val unresolved = mutable.LinkedHashSet[T]()

    for (job <- jobToDependenciesMap.keySet) {
      if (
        !resolved
          .contains(job) && !resolveJob(job, jobToDependenciesMap, resolved, mutable.HashSet())
      ) {
        unresolved.add(job)
      }
    }

    if (unresolved.isEmpty) {
      ZIO.succeed(resolved.toList)
    } else {
      ZIO.fail(DomainError(s"Failed to schedule jobs: $unresolved"))
    }
  }

  private def resolveJob(
    job: T,
    jobToDependenciesMap: Map[T, List[T]],
    resolved: mutable.LinkedHashSet[T],
    visited: mutable.HashSet[T]
  ): Boolean = {
    if (resolved.contains(job)) {
      return true
    }

    if (visited.contains(job)) {
      return false
    }

    visited.add(job)

    val unresolved = mutable.HashSet[T]()
    val dependencies = jobToDependenciesMap.getOrElse(job, List.empty)
    for (dependency <- dependencies) {
      if (!resolveJob(dependency, jobToDependenciesMap, resolved, visited)) {
        unresolved.add(dependency)
      }
    }

    if (unresolved.isEmpty) {
      resolved.add(job)
    }

    unresolved.isEmpty
  }
}
