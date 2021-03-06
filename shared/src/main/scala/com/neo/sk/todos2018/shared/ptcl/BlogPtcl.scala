package com.neo.sk.todos2018.shared.ptcl

object BlogPtcl {
  case class CommonReq()
  case class CommonRsp(errCode: Int=0,
                       msg: String="ok")
  case class GetContentReq(
                          nickname: String="",
                          commentUrl: String=""
                          )

  case class GetContentRsp(
                          blog: List[BlogInfo],
                          //urlHome:String,
                          errorCode: Int = 0,
                          msg: String = "ok"
                          )

  case class GetContentByPageReq(
                                nickname: String,
                                page: Int=1
                                )
  case class BlogInfo(content: String,
                      commentUrl: String,
                      like: String,
                      forward: String,
                      comment: String,
                      time: Long,
                      author: String="",
                      comment2Int: Int=0)

  case class UserInfo(nickname: String = "None",
                      homeUrl: String = "None",
                      imageUrl: String = "None",
                      gender: String = "None",
                      certification: String = "None",
                      introduction: String = "None",
                      region: String = "None",
                      birth: String = "None",
                      photo: String = "None",
                      follow: List[String] = List[String](),
                      fans: List[String] = List[String]())

  case class GetUserInfoReq(nickname: String)

  case class GetUserInfoByPageReq(nickname: String,
                                  page: Int=1)

  case class GetUserInfoRsp(userInfo: List[UserInfo],
                            amount: Int,
                            errorCode: Int=0,
                            msg: String="ok"
                           )

  case class GetContentByPageRsp(
                                blog: List[BlogInfo],
                                amount: Int,
                                errorCode: Int = 0,
                                msg: String = "ok"
                                )

  case class PublishReq(username: String,
                        content: String)

  case class PublishRsp(errorCode: Int=0,
                        msg: String="ok")

  case class LikeReq(commentUrl: String,
                     like: String)

  case class MatrixElement(user: Int,
                           item: Int,
                           score: Int,
                           time: Long)

  case class Matrix(element: List[MatrixElement])

  case class HotInfo(rank: Int,
                     title: String,
                     hotNum: Long,
                     url: String)

  case class GetHotRsp(hotList: List[HotInfo])

  case class CommentInfo(reviewer: String,
                         reviewed: String,
                         content: String,
                         time: Long)

  case class GetCommentReq(commentUrl: String)
  case class GetCommentRsp(commentList: List[CommentInfo])
}
