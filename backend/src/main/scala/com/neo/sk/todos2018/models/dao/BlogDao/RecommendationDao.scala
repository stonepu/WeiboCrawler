package com.neo.sk.todos2018.models.dao.BlogDao

import com.neo.sk.todos2018.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.todos2018.models.SlickTables._
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.todos2018.common.AppSettings
import org.slf4j.LoggerFactory

object RecommendationDao {
  private val log = LoggerFactory.getLogger(this.getClass)
  def addRecommend(user: Int, item: Int) = {
    db.run(tRecommendation += rRecommendation(user, item))
  }

}
