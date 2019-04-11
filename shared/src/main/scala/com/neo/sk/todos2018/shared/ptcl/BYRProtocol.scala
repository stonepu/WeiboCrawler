package com.neo.sk.todos2018.shared.ptcl

object BYRProtocol {
	case class articleTable(
	                       section: String,
	                       board: String,
	                       title: String,
	                       content: String,
	                       author: String,
	                       comment: String
	                       )
	case class GetListRsp(
		                   list:Option[List[(String, String, String)]],
	                     /*title: String,
	                     content: String,
	                     author: String,*/
	                     errorCode: Int=0,
	                     msg: String="OK"
	                     )
	case class GetArticleReq(
	                            board: String
	                            )
	case class GetContentReq(
	                        title: String
	                        )
}
