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
  lazy val schema: profile.SchemaDescription = Array(tBlog.schema, tBloguser.schema, tByrbbs.schema, tComment.schema, tFriendship.schema, tRealtimehot.schema, tRecommendation.schema, tUrlsave.schema, tUser.schema, tUserItem.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tBlog
   *  @param author Database column author SqlType(varchar), Length(100,true), Default(None)
   *  @param homeurl Database column homeUrl SqlType(varchar), Length(100,true), Default(None)
   *  @param content Database column content SqlType(varchar), Length(100000,true)
   *  @param time Database column time SqlType(int8), Default(None)
   *  @param like Database column like SqlType(varchar), Length(100,true), Default(None)
   *  @param forward Database column forward SqlType(varchar), Length(100,true), Default(None)
   *  @param comment Database column comment SqlType(varchar), Length(100,true), Default(None)
   *  @param commenturl Database column commentUrl SqlType(varchar), Length(100,true)
   *  @param ctime Database column ctime SqlType(int8), Default(None)
   *  @param item2int Database column item2int SqlType(serial), AutoInc */
  case class rBlog(author: Option[String] = None, homeurl: Option[String] = None, content: String, time: Option[Long] = None, like: Option[String] = None, forward: Option[String] = None, comment: Option[String] = None, commenturl: String, ctime: Option[Long] = None, item2int: Int)
  /** GetResult implicit for fetching rBlog objects using plain SQL queries */
  implicit def GetResultrBlog(implicit e0: GR[Option[String]], e1: GR[String], e2: GR[Option[Long]], e3: GR[Int]): GR[rBlog] = GR{
    prs => import prs._
    rBlog.tupled((<<?[String], <<?[String], <<[String], <<?[Long], <<?[String], <<?[String], <<?[String], <<[String], <<?[Long], <<[Int]))
  }
  /** Table description of table blog. Objects of this class serve as prototypes for rows in queries. */
  class tBlog(_tableTag: Tag) extends profile.api.Table[rBlog](_tableTag, "blog") {
    def * = (author, homeurl, content, time, like, forward, comment, commenturl, ctime, item2int) <> (rBlog.tupled, rBlog.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (author, homeurl, Rep.Some(content), time, like, forward, comment, Rep.Some(commenturl), ctime, Rep.Some(item2int)).shaped.<>({r=>import r._; _3.map(_=> rBlog.tupled((_1, _2, _3.get, _4, _5, _6, _7, _8.get, _9, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column author SqlType(varchar), Length(100,true), Default(None) */
    val author: Rep[Option[String]] = column[Option[String]]("author", O.Length(100,varying=true), O.Default(None))
    /** Database column homeUrl SqlType(varchar), Length(100,true), Default(None) */
    val homeurl: Rep[Option[String]] = column[Option[String]]("homeUrl", O.Length(100,varying=true), O.Default(None))
    /** Database column content SqlType(varchar), Length(100000,true) */
    val content: Rep[String] = column[String]("content", O.Length(100000,varying=true))
    /** Database column time SqlType(int8), Default(None) */
    val time: Rep[Option[Long]] = column[Option[Long]]("time", O.Default(None))
    /** Database column like SqlType(varchar), Length(100,true), Default(None) */
    val like: Rep[Option[String]] = column[Option[String]]("like", O.Length(100,varying=true), O.Default(None))
    /** Database column forward SqlType(varchar), Length(100,true), Default(None) */
    val forward: Rep[Option[String]] = column[Option[String]]("forward", O.Length(100,varying=true), O.Default(None))
    /** Database column comment SqlType(varchar), Length(100,true), Default(None) */
    val comment: Rep[Option[String]] = column[Option[String]]("comment", O.Length(100,varying=true), O.Default(None))
    /** Database column commentUrl SqlType(varchar), Length(100,true) */
    val commenturl: Rep[String] = column[String]("commentUrl", O.Length(100,varying=true))
    /** Database column ctime SqlType(int8), Default(None) */
    val ctime: Rep[Option[Long]] = column[Option[Long]]("ctime", O.Default(None))
    /** Database column item2int SqlType(serial), AutoInc */
    val item2int: Rep[Int] = column[Int]("item2int", O.AutoInc)
  }
  /** Collection-like TableQuery object for table tBlog */
  lazy val tBlog = new TableQuery(tag => new tBlog(tag))

  /** Entity class storing rows of table tBloguser
   *  @param nickname Database column nickname SqlType(varchar), Length(30,true), Default(None)
   *  @param homeurl Database column HomeUrl SqlType(varchar), Length(100,true), Default(None)
   *  @param imageurl Database column ImageUrl SqlType(varchar), Length(100,true), Default(None)
   *  @param gender Database column Gender SqlType(varchar), Length(10,true), Default(None)
   *  @param certification Database column Certification SqlType(varchar), Length(100,true), Default(None)
   *  @param introduction Database column Introduction SqlType(varchar), Length(100,true), Default(None)
   *  @param region Database column Region SqlType(varchar), Length(100,true), Default(None)
   *  @param birth Database column Birth SqlType(varchar), Length(100,true), Default(None)
   *  @param photo Database column Photo SqlType(varchar), Length(100,true), Default(None)
   *  @param follow Database column Follow SqlType(varchar), Length(1000000,true), Default(None)
   *  @param fans Database column Fans SqlType(varchar), Length(1000000,true), Default(None)
   *  @param password Database column password SqlType(varchar), Length(100,true), Default(None)
   *  @param u2int Database column u2int SqlType(serial), AutoInc */
  case class rBloguser(nickname: Option[String] = None, homeurl: Option[String] = None, imageurl: Option[String] = None, gender: Option[String] = None, certification: Option[String] = None, introduction: Option[String] = None, region: Option[String] = None, birth: Option[String] = None, photo: Option[String] = None, follow: Option[String] = None, fans: Option[String] = None, password: Option[String] = None, u2int: Int)
  /** GetResult implicit for fetching rBloguser objects using plain SQL queries */
  implicit def GetResultrBloguser(implicit e0: GR[Option[String]], e1: GR[Int]): GR[rBloguser] = GR{
    prs => import prs._
    rBloguser.tupled((<<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[Int]))
  }
  /** Table description of table bloguser. Objects of this class serve as prototypes for rows in queries. */
  class tBloguser(_tableTag: Tag) extends profile.api.Table[rBloguser](_tableTag, "bloguser") {
    def * = (nickname, homeurl, imageurl, gender, certification, introduction, region, birth, photo, follow, fans, password, u2int) <> (rBloguser.tupled, rBloguser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (nickname, homeurl, imageurl, gender, certification, introduction, region, birth, photo, follow, fans, password, Rep.Some(u2int)).shaped.<>({r=>import r._; _13.map(_=> rBloguser.tupled((_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column nickname SqlType(varchar), Length(30,true), Default(None) */
    val nickname: Rep[Option[String]] = column[Option[String]]("nickname", O.Length(30,varying=true), O.Default(None))
    /** Database column HomeUrl SqlType(varchar), Length(100,true), Default(None) */
    val homeurl: Rep[Option[String]] = column[Option[String]]("HomeUrl", O.Length(100,varying=true), O.Default(None))
    /** Database column ImageUrl SqlType(varchar), Length(100,true), Default(None) */
    val imageurl: Rep[Option[String]] = column[Option[String]]("ImageUrl", O.Length(100,varying=true), O.Default(None))
    /** Database column Gender SqlType(varchar), Length(10,true), Default(None) */
    val gender: Rep[Option[String]] = column[Option[String]]("Gender", O.Length(10,varying=true), O.Default(None))
    /** Database column Certification SqlType(varchar), Length(100,true), Default(None) */
    val certification: Rep[Option[String]] = column[Option[String]]("Certification", O.Length(100,varying=true), O.Default(None))
    /** Database column Introduction SqlType(varchar), Length(100,true), Default(None) */
    val introduction: Rep[Option[String]] = column[Option[String]]("Introduction", O.Length(100,varying=true), O.Default(None))
    /** Database column Region SqlType(varchar), Length(100,true), Default(None) */
    val region: Rep[Option[String]] = column[Option[String]]("Region", O.Length(100,varying=true), O.Default(None))
    /** Database column Birth SqlType(varchar), Length(100,true), Default(None) */
    val birth: Rep[Option[String]] = column[Option[String]]("Birth", O.Length(100,varying=true), O.Default(None))
    /** Database column Photo SqlType(varchar), Length(100,true), Default(None) */
    val photo: Rep[Option[String]] = column[Option[String]]("Photo", O.Length(100,varying=true), O.Default(None))
    /** Database column Follow SqlType(varchar), Length(1000000,true), Default(None) */
    val follow: Rep[Option[String]] = column[Option[String]]("Follow", O.Length(1000000,varying=true), O.Default(None))
    /** Database column Fans SqlType(varchar), Length(1000000,true), Default(None) */
    val fans: Rep[Option[String]] = column[Option[String]]("Fans", O.Length(1000000,varying=true), O.Default(None))
    /** Database column password SqlType(varchar), Length(100,true), Default(None) */
    val password: Rep[Option[String]] = column[Option[String]]("password", O.Length(100,varying=true), O.Default(None))
    /** Database column u2int SqlType(serial), AutoInc */
    val u2int: Rep[Int] = column[Int]("u2int", O.AutoInc)
  }
  /** Collection-like TableQuery object for table tBloguser */
  lazy val tBloguser = new TableQuery(tag => new tBloguser(tag))

  /** Entity class storing rows of table tByrbbs
   *  @param section Database column section SqlType(_varchar), Length(100,false), Default(None)
   *  @param board Database column board SqlType(_varchar), Length(100,false), Default(None)
   *  @param title Database column title SqlType(_varchar), Length(100,false), Default(None)
   *  @param content Database column content SqlType(_varchar), Length(100000,false), Default(None)
   *  @param author Database column author SqlType(_varchar), Length(100,false), Default(None)
   *  @param comment Database column comment SqlType(_varchar), Length(100000,false), Default(None) */
  case class rByrbbs(section: Option[String] = None, board: Option[String] = None, title: Option[String] = None, content: Option[String] = None, author: Option[String] = None, comment: Option[String] = None)
  /** GetResult implicit for fetching rByrbbs objects using plain SQL queries */
  implicit def GetResultrByrbbs(implicit e0: GR[Option[String]]): GR[rByrbbs] = GR{
    prs => import prs._
    rByrbbs.tupled((<<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table byrbbs. Objects of this class serve as prototypes for rows in queries. */
  class tByrbbs(_tableTag: Tag) extends profile.api.Table[rByrbbs](_tableTag, "byrbbs") {
    def * = (section, board, title, content, author, comment) <> (rByrbbs.tupled, rByrbbs.unapply)

    /** Database column section SqlType(_varchar), Length(100,false), Default(None) */
    val section: Rep[Option[String]] = column[Option[String]]("section", O.Length(100,varying=false), O.Default(None))
    /** Database column board SqlType(_varchar), Length(100,false), Default(None) */
    val board: Rep[Option[String]] = column[Option[String]]("board", O.Length(100,varying=false), O.Default(None))
    /** Database column title SqlType(_varchar), Length(100,false), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(100,varying=false), O.Default(None))
    /** Database column content SqlType(_varchar), Length(100000,false), Default(None) */
    val content: Rep[Option[String]] = column[Option[String]]("content", O.Length(100000,varying=false), O.Default(None))
    /** Database column author SqlType(_varchar), Length(100,false), Default(None) */
    val author: Rep[Option[String]] = column[Option[String]]("author", O.Length(100,varying=false), O.Default(None))
    /** Database column comment SqlType(_varchar), Length(100000,false), Default(None) */
    val comment: Rep[Option[String]] = column[Option[String]]("comment", O.Length(100000,varying=false), O.Default(None))
  }
  /** Collection-like TableQuery object for table tByrbbs */
  lazy val tByrbbs = new TableQuery(tag => new tByrbbs(tag))

  /** Entity class storing rows of table tComment
   *  @param reviewer Database column reviewer SqlType(varchar), Length(100,true)
   *  @param reviewed Database column reviewed SqlType(varchar), Length(100,true), Default(None)
   *  @param content Database column content SqlType(varchar), Length(10000,true), Default(None)
   *  @param commenturl Database column commentUrl SqlType(varchar), Length(100,true), Default(None)
   *  @param time Database column time SqlType(int8), Default(None) */
  case class rComment(reviewer: String, reviewed: Option[String] = None, content: Option[String] = None, commenturl: Option[String] = None, time: Option[Long] = None)
  /** GetResult implicit for fetching rComment objects using plain SQL queries */
  implicit def GetResultrComment(implicit e0: GR[String], e1: GR[Option[String]], e2: GR[Option[Long]]): GR[rComment] = GR{
    prs => import prs._
    rComment.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[Long]))
  }
  /** Table description of table Comment. Objects of this class serve as prototypes for rows in queries. */
  class tComment(_tableTag: Tag) extends profile.api.Table[rComment](_tableTag, "Comment") {
    def * = (reviewer, reviewed, content, commenturl, time) <> (rComment.tupled, rComment.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(reviewer), reviewed, content, commenturl, time).shaped.<>({r=>import r._; _1.map(_=> rComment.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column reviewer SqlType(varchar), Length(100,true) */
    val reviewer: Rep[String] = column[String]("reviewer", O.Length(100,varying=true))
    /** Database column reviewed SqlType(varchar), Length(100,true), Default(None) */
    val reviewed: Rep[Option[String]] = column[Option[String]]("reviewed", O.Length(100,varying=true), O.Default(None))
    /** Database column content SqlType(varchar), Length(10000,true), Default(None) */
    val content: Rep[Option[String]] = column[Option[String]]("content", O.Length(10000,varying=true), O.Default(None))
    /** Database column commentUrl SqlType(varchar), Length(100,true), Default(None) */
    val commenturl: Rep[Option[String]] = column[Option[String]]("commentUrl", O.Length(100,varying=true), O.Default(None))
    /** Database column time SqlType(int8), Default(None) */
    val time: Rep[Option[Long]] = column[Option[Long]]("time", O.Default(None))
  }
  /** Collection-like TableQuery object for table tComment */
  lazy val tComment = new TableQuery(tag => new tComment(tag))

  /** Entity class storing rows of table tFriendship
   *  @param username Database column username SqlType(_varchar), Length(100,false), Default(None)
   *  @param friendname Database column friendname SqlType(_varchar), Length(100,false), Default(None)
   *  @param weight Database column weight SqlType(int4), Default(None)
   *  @param userhome Database column userHome SqlType(_varchar), Length(100,false), Default(None)
   *  @param friendhome Database column friendHome SqlType(_varchar), Length(100,false), Default(None) */
  case class rFriendship(username: Option[String] = None, friendname: Option[String] = None, weight: Option[Int] = None, userhome: Option[String] = None, friendhome: Option[String] = None)
  /** GetResult implicit for fetching rFriendship objects using plain SQL queries */
  implicit def GetResultrFriendship(implicit e0: GR[Option[String]], e1: GR[Option[Int]]): GR[rFriendship] = GR{
    prs => import prs._
    rFriendship.tupled((<<?[String], <<?[String], <<?[Int], <<?[String], <<?[String]))
  }
  /** Table description of table friendship. Objects of this class serve as prototypes for rows in queries. */
  class tFriendship(_tableTag: Tag) extends profile.api.Table[rFriendship](_tableTag, "friendship") {
    def * = (username, friendname, weight, userhome, friendhome) <> (rFriendship.tupled, rFriendship.unapply)

    /** Database column username SqlType(_varchar), Length(100,false), Default(None) */
    val username: Rep[Option[String]] = column[Option[String]]("username", O.Length(100,varying=false), O.Default(None))
    /** Database column friendname SqlType(_varchar), Length(100,false), Default(None) */
    val friendname: Rep[Option[String]] = column[Option[String]]("friendname", O.Length(100,varying=false), O.Default(None))
    /** Database column weight SqlType(int4), Default(None) */
    val weight: Rep[Option[Int]] = column[Option[Int]]("weight", O.Default(None))
    /** Database column userHome SqlType(_varchar), Length(100,false), Default(None) */
    val userhome: Rep[Option[String]] = column[Option[String]]("userHome", O.Length(100,varying=false), O.Default(None))
    /** Database column friendHome SqlType(_varchar), Length(100,false), Default(None) */
    val friendhome: Rep[Option[String]] = column[Option[String]]("friendHome", O.Length(100,varying=false), O.Default(None))
  }
  /** Collection-like TableQuery object for table tFriendship */
  lazy val tFriendship = new TableQuery(tag => new tFriendship(tag))

  /** Entity class storing rows of table tRealtimehot
   *  @param rank Database column rank SqlType(int4), Default(None)
   *  @param title Database column title SqlType(varchar), Length(100,true), Default(None)
   *  @param hotnum Database column hotnum SqlType(int8), Default(None)
   *  @param url Database column url SqlType(varchar), Length(200,true) */
  case class rRealtimehot(rank: Option[Int] = None, title: Option[String] = None, hotnum: Option[Long] = None, url: String)
  /** GetResult implicit for fetching rRealtimehot objects using plain SQL queries */
  implicit def GetResultrRealtimehot(implicit e0: GR[Option[Int]], e1: GR[Option[String]], e2: GR[Option[Long]], e3: GR[String]): GR[rRealtimehot] = GR{
    prs => import prs._
    rRealtimehot.tupled((<<?[Int], <<?[String], <<?[Long], <<[String]))
  }
  /** Table description of table realtimehot. Objects of this class serve as prototypes for rows in queries. */
  class tRealtimehot(_tableTag: Tag) extends profile.api.Table[rRealtimehot](_tableTag, "realtimehot") {
    def * = (rank, title, hotnum, url) <> (rRealtimehot.tupled, rRealtimehot.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (rank, title, hotnum, Rep.Some(url)).shaped.<>({r=>import r._; _4.map(_=> rRealtimehot.tupled((_1, _2, _3, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column rank SqlType(int4), Default(None) */
    val rank: Rep[Option[Int]] = column[Option[Int]]("rank", O.Default(None))
    /** Database column title SqlType(varchar), Length(100,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(100,varying=true), O.Default(None))
    /** Database column hotnum SqlType(int8), Default(None) */
    val hotnum: Rep[Option[Long]] = column[Option[Long]]("hotnum", O.Default(None))
    /** Database column url SqlType(varchar), Length(200,true) */
    val url: Rep[String] = column[String]("url", O.Length(200,varying=true))
  }
  /** Collection-like TableQuery object for table tRealtimehot */
  lazy val tRealtimehot = new TableQuery(tag => new tRealtimehot(tag))

  /** Entity class storing rows of table tRecommendation
   *  @param user Database column user SqlType(int4)
   *  @param item Database column item SqlType(int4) */
  case class rRecommendation(user: Int, item: Int)
  /** GetResult implicit for fetching rRecommendation objects using plain SQL queries */
  implicit def GetResultrRecommendation(implicit e0: GR[Int]): GR[rRecommendation] = GR{
    prs => import prs._
    rRecommendation.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table recommendation. Objects of this class serve as prototypes for rows in queries. */
  class tRecommendation(_tableTag: Tag) extends profile.api.Table[rRecommendation](_tableTag, "recommendation") {
    def * = (user, item) <> (rRecommendation.tupled, rRecommendation.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(user), Rep.Some(item)).shaped.<>({r=>import r._; _1.map(_=> rRecommendation.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user SqlType(int4) */
    val user: Rep[Int] = column[Int]("user")
    /** Database column item SqlType(int4) */
    val item: Rep[Int] = column[Int]("item")
  }
  /** Collection-like TableQuery object for table tRecommendation */
  lazy val tRecommendation = new TableQuery(tag => new tRecommendation(tag))

  /** Entity class storing rows of table tUrlsave
   *  @param url Database column url SqlType(varchar), Length(100,true) */
  case class rUrlsave(url: String)
  /** GetResult implicit for fetching rUrlsave objects using plain SQL queries */
  implicit def GetResultrUrlsave(implicit e0: GR[String]): GR[rUrlsave] = GR{
    prs => import prs._
    rUrlsave(<<[String])
  }
  /** Table description of table UrlSave. Objects of this class serve as prototypes for rows in queries. */
  class tUrlsave(_tableTag: Tag) extends profile.api.Table[rUrlsave](_tableTag, "UrlSave") {
    def * = url <> (rUrlsave, rUrlsave.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(url).shaped.<>(r => r.map(_=> rUrlsave(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column url SqlType(varchar), Length(100,true) */
    val url: Rep[String] = column[String]("url", O.Length(100,varying=true))
  }
  /** Collection-like TableQuery object for table tUrlsave */
  lazy val tUrlsave = new TableQuery(tag => new tUrlsave(tag))

  /** Entity class storing rows of table tUser
   *  @param username Database column username SqlType(_varchar), Length(20,false), Default(None)
   *  @param password Database column password SqlType(_varchar), Length(20,false), Default(None) */
  case class rUser(username: Option[String] = None, password: Option[String] = None)
  /** GetResult implicit for fetching rUser objects using plain SQL queries */
  implicit def GetResultrUser(implicit e0: GR[Option[String]]): GR[rUser] = GR{
    prs => import prs._
    rUser.tupled((<<?[String], <<?[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class tUser(_tableTag: Tag) extends profile.api.Table[rUser](_tableTag, "user") {
    def * = (username, password) <> (rUser.tupled, rUser.unapply)

    /** Database column username SqlType(_varchar), Length(20,false), Default(None) */
    val username: Rep[Option[String]] = column[Option[String]]("username", O.Length(20,varying=false), O.Default(None))
    /** Database column password SqlType(_varchar), Length(20,false), Default(None) */
    val password: Rep[Option[String]] = column[Option[String]]("password", O.Length(20,varying=false), O.Default(None))
  }
  /** Collection-like TableQuery object for table tUser */
  lazy val tUser = new TableQuery(tag => new tUser(tag))

  /** Entity class storing rows of table tUserItem
   *  @param users Database column users SqlType(int4)
   *  @param item Database column item SqlType(int4)
   *  @param score Database column score SqlType(int4)
   *  @param time Database column time SqlType(int8) */
  case class rUserItem(users: Int, item: Int, score: Int, time: Long)
  /** GetResult implicit for fetching rUserItem objects using plain SQL queries */
  implicit def GetResultrUserItem(implicit e0: GR[Int], e1: GR[Long]): GR[rUserItem] = GR{
    prs => import prs._
    rUserItem.tupled((<<[Int], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table user_item. Objects of this class serve as prototypes for rows in queries. */
  class tUserItem(_tableTag: Tag) extends profile.api.Table[rUserItem](_tableTag, "user_item") {
    def * = (users, item, score, time) <> (rUserItem.tupled, rUserItem.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(users), Rep.Some(item), Rep.Some(score), Rep.Some(time)).shaped.<>({r=>import r._; _1.map(_=> rUserItem.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column users SqlType(int4) */
    val users: Rep[Int] = column[Int]("users")
    /** Database column item SqlType(int4) */
    val item: Rep[Int] = column[Int]("item")
    /** Database column score SqlType(int4) */
    val score: Rep[Int] = column[Int]("score")
    /** Database column time SqlType(int8) */
    val time: Rep[Long] = column[Long]("time")
  }
  /** Collection-like TableQuery object for table tUserItem */
  lazy val tUserItem = new TableQuery(tag => new tUserItem(tag))
}
