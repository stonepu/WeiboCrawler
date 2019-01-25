package com.neo.sk.todos2018.models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.jdbc.PostgresProfile
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = tUser.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tUser
   *  @param username Database column username SqlType(varchar), Length(20,true), Default(None)
   *  @param password Database column password SqlType(varchar), Length(20,true), Default(None) */
  case class rUser(username: Option[String] = None, password: Option[String] = None)
  /** GetResult implicit for fetching rUser objects using plain SQL queries */
  implicit def GetResultrUser(implicit e0: GR[Option[String]]): GR[rUser] = GR{
    prs => import prs._
    rUser.tupled((<<?[String], <<?[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class tUser(_tableTag: Tag) extends profile.api.Table[rUser](_tableTag, "user") {
    def * = (username, password) <> (rUser.tupled, rUser.unapply)

    /** Database column username SqlType(varchar), Length(20,true), Default(None) */
    val username: Rep[Option[String]] = column[Option[String]]("username", O.Length(20,varying=true), O.Default(None))
    /** Database column password SqlType(varchar), Length(20,true), Default(None) */
    val password: Rep[Option[String]] = column[Option[String]]("password", O.Length(20,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table tUser */
  lazy val tUser = new TableQuery(tag => new tUser(tag))
}
