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
  lazy val schema: profile.SchemaDescription = Labels.schema ++ LabelsPerQuestion.schema ++ Questions.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Labels
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param language Database column language SqlType(varchar), Length(2,true)
   *  @param value Database column value SqlType(varchar), Length(255,true) */
  case class LabelsRow(id: Int, language: String, value: String)
  /** GetResult implicit for fetching LabelsRow objects using plain SQL queries */
  implicit def GetResultLabelsRow(implicit e0: GR[Int], e1: GR[String]): GR[LabelsRow] = GR{
    prs => import prs._
    LabelsRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table labels. Objects of this class serve as prototypes for rows in queries. */
  class Labels(_tableTag: Tag) extends Table[LabelsRow](_tableTag, "labels") {
    def * = (id, language, value) <> (LabelsRow.tupled, LabelsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(language), Rep.Some(value)).shaped.<>({r=>import r._; _1.map(_=> LabelsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column language SqlType(varchar), Length(2,true) */
    val language: Rep[String] = column[String]("language", O.Length(2,varying=true))
    /** Database column value SqlType(varchar), Length(255,true) */
    val value: Rep[String] = column[String]("value", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Labels */
  lazy val Labels = new TableQuery(tag => new Labels(tag))

  /** Entity class storing rows of table LabelsPerQuestion
   *  @param questionId Database column question_id SqlType(int4)
   *  @param labelId Database column label_id SqlType(int4) */
  case class LabelsPerQuestionRow(questionId: Int, labelId: Int)
  /** GetResult implicit for fetching LabelsPerQuestionRow objects using plain SQL queries */
  implicit def GetResultLabelsPerQuestionRow(implicit e0: GR[Int]): GR[LabelsPerQuestionRow] = GR{
    prs => import prs._
    LabelsPerQuestionRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table labels_per_question. Objects of this class serve as prototypes for rows in queries. */
  class LabelsPerQuestion(_tableTag: Tag) extends Table[LabelsPerQuestionRow](_tableTag, "labels_per_question") {
    def * = (questionId, labelId) <> (LabelsPerQuestionRow.tupled, LabelsPerQuestionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(questionId), Rep.Some(labelId)).shaped.<>({r=>import r._; _1.map(_=> LabelsPerQuestionRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column question_id SqlType(int4) */
    val questionId: Rep[Int] = column[Int]("question_id")
    /** Database column label_id SqlType(int4) */
    val labelId: Rep[Int] = column[Int]("label_id")

    /** Primary key of LabelsPerQuestion (database name labels_per_question_pk) */
    val pk = primaryKey("labels_per_question_pk", (questionId, labelId))

    /** Foreign key referencing Labels (database name labels_per_question_label_id) */
    lazy val labelsFk = foreignKey("labels_per_question_label_id", labelId, Labels)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Questions (database name labels_per_question_question_id) */
    lazy val questionsFk = foreignKey("labels_per_question_question_id", questionId, Questions)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table LabelsPerQuestion */
  lazy val LabelsPerQuestion = new TableQuery(tag => new LabelsPerQuestion(tag))

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
