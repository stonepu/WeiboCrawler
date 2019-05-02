package com.neo.sk.todos2018

/**
  * Created by hongruying on 2018/2/16
  */
package object proxy {

  case class ProxyInfo(ip:String,port:String,timestamp:Long)

  case class ProxyRsp(proxy:Option[ProxyInfo])



}
