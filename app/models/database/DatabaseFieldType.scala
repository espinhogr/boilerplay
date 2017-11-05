package models.database

import enumeratum._
import org.postgresql.jdbc.PgArray
import org.postgresql.util.PGobject

sealed abstract class DatabaseFieldType[T](val key: String, val isNumeric: Boolean = false) extends EnumEntry {
  def apply(row: Row, col: String): T = row.as[T](col)
  def opt(row: Row, col: String): Option[T] = row.asOpt[T](col)
}

object DatabaseFieldType extends Enum[DatabaseFieldType[_]] with DatabaseFieldHelper {
  case object StringType extends DatabaseFieldType[String]("string")
  case object BigDecimalType extends DatabaseFieldType[BigDecimal]("decimal", isNumeric = true) {
    override def apply(row: Row, col: String) = row.as[Any](col) match {
      case b: java.math.BigDecimal => new BigDecimal(b)
      case b: BigDecimal => b
    }
    override def opt(row: Row, col: String) = row.asOpt[Any](col).map {
      case b: java.math.BigDecimal => new BigDecimal(b)
      case b: BigDecimal => b
    }
  }

  case object BooleanType extends DatabaseFieldType[Boolean]("boolean") {
    override def apply(row: Row, col: String) = boolCoerce(row.as[Any](col))
    override def opt(row: Row, col: String) = row.asOpt[Any](col).map(boolCoerce)
  }
  case object ByteType extends DatabaseFieldType[Byte]("byte") {
    override def apply(row: Row, col: String) = byteCoerce(row.as[Any](col))
    override def opt(row: Row, col: String) = row.asOpt[Any](col).map(byteCoerce)
  }

  case object ShortType extends DatabaseFieldType[Short]("short", isNumeric = true)
  case object IntegerType extends DatabaseFieldType[Int]("int", isNumeric = true)
  case object LongType extends DatabaseFieldType[Long]("long", isNumeric = true)
  case object FloatType extends DatabaseFieldType[Float]("float", isNumeric = true)
  case object DoubleType extends DatabaseFieldType[Double]("double", isNumeric = true)

  case object ByteArrayType extends DatabaseFieldType[Array[Byte]]("byteArray") {
    override def apply(row: Row, col: String) = row.as[PgArray](col).getArray.asInstanceOf[Array[Byte]]
    override def opt(row: Row, col: String) = row.asOpt[PgArray](col).map(_.asInstanceOf[Array[Byte]])
  }
  case object LongArrayType extends DatabaseFieldType[Array[Long]]("longArray") {
    override def apply(row: Row, col: String) = row.as[PgArray](col).getArray.asInstanceOf[Array[Long]]
    override def opt(row: Row, col: String) = row.asOpt[PgArray](col).map(_.asInstanceOf[Array[Long]])
  }
  case object StringArrayType extends DatabaseFieldType[Array[String]]("stringArray") {
    override def apply(row: Row, col: String) = row.as[PgArray](col).getArray.asInstanceOf[Array[String]]
    override def opt(row: Row, col: String) = row.asOpt[PgArray](col).map(_.asInstanceOf[Array[String]])
  }
  case object UuidArrayType extends DatabaseFieldType[Array[java.util.UUID]]("uuidArray") {
    override def apply(row: Row, col: String) = row.as[PgArray](col).getArray.asInstanceOf[Array[java.util.UUID]]
    override def opt(row: Row, col: String) = row.asOpt[PgArray](col).map(_.asInstanceOf[Array[java.util.UUID]])
  }

  case object DateType extends DatabaseFieldType[java.time.LocalDate]("date") {
    override def apply(row: Row, col: String) = row.as[java.sql.Date](col).toLocalDate
    override def opt(row: Row, col: String) = row.asOpt[java.sql.Date](col).map(_.toLocalDate)
  }
  case object TimeType extends DatabaseFieldType[java.time.LocalTime]("time") {
    override def apply(row: Row, col: String) = row.as[java.sql.Time](col).toLocalTime
    override def opt(row: Row, col: String) = row.asOpt[java.sql.Time](col).map(_.toLocalTime)
  }
  case object TimestampType extends DatabaseFieldType[java.time.LocalDateTime]("timestamp") {
    override def apply(row: Row, col: String) = row.as[java.sql.Timestamp](col).toLocalDateTime
    override def opt(row: Row, col: String) = row.asOpt[java.sql.Timestamp](col).map(_.toLocalDateTime)
  }

  case object RefType extends DatabaseFieldType[String]("ref")
  case object XmlType extends DatabaseFieldType[String]("xml")
  case object UuidType extends DatabaseFieldType[java.util.UUID]("uuid")
  case object ObjectType extends DatabaseFieldType[String]("object")
  case object StructType extends DatabaseFieldType[String]("struct")

  case object JsonType extends DatabaseFieldType[io.circe.Json]("json") {
    import io.circe.parser._
    override def apply(row: Row, col: String) = parse(row.as[PGobject](col).getValue).right.get
    override def opt(row: Row, col: String) = row.asOpt[PGobject](col).map(x => parse(x.getValue).right.get)
  }

  case object TagsType extends DatabaseFieldType[Seq[models.tag.Tag]]("tags") {
    override def apply(row: Row, col: String) = tagsCoerce(row.as[Any](col))
    override def opt(row: Row, col: String) = row.asOpt[Any](col).map(tagsCoerce)
  }

  case object UnknownType extends DatabaseFieldType[String]("unknown")

  override val values = findValues
}
