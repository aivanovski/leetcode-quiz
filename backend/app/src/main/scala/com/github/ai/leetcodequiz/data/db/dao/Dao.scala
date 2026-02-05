package com.github.ai.leetcodequiz.data.db.dao

import com.github.ai.leetcodequiz.entity.exception.DatabaseError
import zio.{IO, ZIO}
import slick.jdbc.SQLiteProfile.api.*

abstract class Dao[E, TableType <: Table[E]](
  private val db: Database,
  protected val table: TableQuery[TableType]
) {

  def query(
    predicate: TableType => Rep[Boolean]
  ): IO[DatabaseError, List[E]] = {
    ZIO
      .fromFuture { _ =>
        db.run[Seq[E]](table.filter(predicate).result)
      }
      .map(_.toList)
      .mapError(DatabaseError(_))
  }

  def queryAll(): IO[DatabaseError, List[E]] = {
    query(_ => true)
  }

  def queryOne(
    predicate: TableType => Rep[Boolean]
  ): IO[DatabaseError, Option[E]] = {
    query(predicate)
      .map(_.headOption)
  }

  def insertAll(entities: List[E]): IO[DatabaseError, List[E]] = {
    ZIO.collectAll(
      entities.map(entity => insert(entity))
    )
  }

  def insert(entity: E): IO[DatabaseError, E] = {
    ZIO
      .fromFuture { _ => db.run(table += entity) }
      .flatMap { count =>
        if (count != 0) {
          ZIO.succeed(entity)
        } else {
          ZIO.fail(DatabaseError(message = s"Failed to insert entity: $entity"))
        }
      }
      .mapError(DatabaseError(_))
  }

  def updateOne(
    predicate: TableType => Rep[Boolean],
    entity: E
  ): IO[DatabaseError, E] = {
    ZIO
      .fromFuture { _ =>
        db.run(table.filter(predicate).update(entity))
      }
      .flatMap { count =>
        if (count != 0) {
          ZIO.succeed(entity)
        } else {
          ZIO.fail(DatabaseError(message = s"Unable to update entity: $entity"))
        }
      }
      .mapError(DatabaseError(_))
  }

  def delete(
    predicate: TableType => Rep[Boolean]
  ): IO[DatabaseError, Unit] = {
    ZIO
      .fromFuture { _ =>
        db.run(table.filter(predicate).delete)
      }
      .map(_ => ())
      .mapError(DatabaseError(_))
  }

  def deleteOne(
    predicate: TableType => Rep[Boolean]
  ): IO[DatabaseError, Unit] = {
    ZIO
      .fromFuture { _ =>
        db.run(table.filter(predicate).delete)
      }
      .flatMap { count =>
        if (count != 0) {
          ZIO.succeed(())
        } else {
          ZIO.fail(DatabaseError(message = "Unable to delete entity"))
        }
      }
      .mapError(DatabaseError(_))
  }
}
