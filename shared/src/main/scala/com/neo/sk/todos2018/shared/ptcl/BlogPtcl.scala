package com.neo.sk.todos2018.shared.ptcl

object BlogPtcl {
  case class CommonReq()
  case class CommonRsp(errCode: Int=0,
                       msg: String="ok")
  case class GetContentReq(
                          nickname: String
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
                      author: String="")

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

  case class MatrixInfo(user: Int,
                        item: Int)
  case class Matrixes(matrixInfo: List[MatrixInfo])

  case class JsonString(json: String)
}
