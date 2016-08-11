package com.xebia.bpt.persistence
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Labels.schema ++ Questions.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Labels
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param language Database column language SqlType(varchar), Length(2,true)
   *  @param value Database column value SqlType(varchar), Length(255,true)
   *  @param questionId Database column question_id SqlType(int4), Default(None) */
  case class LabelsRow(id: Int, language: String, value: String, questionId: Option[Int] = None)
  /** GetResult implicit for fetching LabelsRow objects using plain SQL queries */
  implicit def GetResultLabelsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]]): GR[LabelsRow] = GR{
    prs => import prs._
    LabelsRow.tupled((<<[Int], <<[String], <<[String], <<?[Int]))
  }
  /** Table description of table labels. Objects of this class serve as prototypes for rows in queries. */
  class Labels(_tableTag: Tag) extends Table[LabelsRow](_tableTag, "labels") {
    def * = (id, language, value, questionId) <> (LabelsRow.tupled, LabelsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(language), Rep.Some(value), questionId).shaped.<>({r=>import r._; _1.map(_=> LabelsRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column language SqlType(varchar), Length(2,true) */
    val language: Rep[String] = column[String]("language", O.Length(2,varying=true))
    /** Database column value SqlType(varchar), Length(255,true) */
    val value: Rep[String] = column[String]("value", O.Length(255,varying=true))
    /** Database column question_id SqlType(int4), Default(None) */
    val questionId: Rep[Option[Int]] = column[Option[Int]]("question_id", O.Default(None))

    /** Foreign key referencing Questions (database name question_fkey) */
    lazy val questionsFk = foreignKey("question_fkey", questionId, Questions)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Labels */
  lazy val Labels = new TableQuery(tag => new Labels(tag))

  /** Entity class storing rows of table Questions
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true) */
  case class QuestionsRow(id: Int, name: String)
  /** GetResult implicit for fetching QuestionsRow objects using plain SQL queries */
  implicit def GetResultQuestionsRow(implicit e0: GR[Int], e1: GR[String]): GR[QuestionsRow] = GR{
    prs => import prs._
    QuestionsRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table questions. Objects of this class serve as prototypes for rows in queries. */
  class Questions(_tableTag: Tag) extends Table[QuestionsRow](_tableTag, "questions") {
    def * = (id, name) <> (QuestionsRow.tupled, QuestionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({r=>import r._; _1.map(_=> QuestionsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))

    /** Uniqueness Index over (name) (database name unique_name) */
    val index1 = index("unique_name", name, unique=true)
  }
  /** Collection-like TableQuery object for table Questions */
  lazy val Questions = new TableQuery(tag => new Questions(tag))
}
