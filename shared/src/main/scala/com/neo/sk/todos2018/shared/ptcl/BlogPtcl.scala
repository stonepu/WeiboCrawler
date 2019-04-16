package com.neo.sk.todos2018.shared.ptcl

object BlogPtcl {
  case class GetContentReq(
                          nickname: String
                          )

  case class GetContentRsp(
                          content: List[String],
                          //urlHome:String,
                          errorCode: Int = 0,
                          msg: String = "ok"
                          )

  case class GetContentByPageReq(
                                nickname: String,
                                page: Int=1
                                )

  case class GetContentByPageRsp(
                                content: List[String],
                                amount: Int,
                                errorCode: Int = 0,
                                msg: String = "ok"
                                )
}
