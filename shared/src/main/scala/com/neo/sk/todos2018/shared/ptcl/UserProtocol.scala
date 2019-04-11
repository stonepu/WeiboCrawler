package com.neo.sk.todos2018.shared.ptcl

/**
  * User: sky
  * Date: 2018/6/1
  * Time: 14:50
  */
object UserProtocol {
  case class UserLoginReq(
                         userName:String,
                         pwd:String
                         )

  case class UserSignupReq(
                          username:String,
                          pwd:String,
                          pwdAgain:String
                          )

  case class SinaLoginReq(
                         username: String,
                         password: String
                         )
}
