/* Generated File */
package models.table.ddl

import java.time.LocalDateTime
import models.ddl.FlywaySchemaHistory
import services.database.slick.SlickQueryService.imports._

object FlywaySchemaHistoryTable {
  val query = TableQuery[FlywaySchemaHistoryTable]

  def getByPrimaryKey(installedRank: Long) = query.filter(_.installedRank === installedRank).result.headOption
  def getByPrimaryKeySeq(installedRankSeq: Seq[Long]) = query.filter(_.installedRank.inSet(installedRankSeq)).result
}

class FlywaySchemaHistoryTable(tag: slick.lifted.Tag) extends Table[FlywaySchemaHistory](tag, "flyway_schema_history") {
  val installedRank = column[Long]("installed_rank", O.PrimaryKey)
  val version = column[Option[String]]("version")
  val description = column[String]("description")
  val typ = column[String]("type")
  val script = column[String]("script")
  val checksum = column[Option[Long]]("checksum")
  val installedBy = column[String]("installed_by")
  val installedOn = column[LocalDateTime]("installed_on")
  val executionTime = column[Long]("execution_time")
  val success = column[Boolean]("success")

  override val * = (installedRank, version, description, typ, script, checksum, installedBy, installedOn, executionTime, success) <> (
    (FlywaySchemaHistory.apply _).tupled,
    FlywaySchemaHistory.unapply
  )
}

