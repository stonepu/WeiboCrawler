package com.neo.sk.todos2018.utils

import java.io.IOException
import java.nio.charset.CodingErrorAction

import org.apache.http._
import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.client.{CookieStore, HttpRequestRetryHandler}
import org.apache.http.config.{ConnectionConfig, MessageConstraints}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicHeader
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Zhong on 2017/7/20.
  */
object HttpClientUtil {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val httpHeaders = List[Header](
    new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
    new BasicHeader("Accept-Encoding", "gzip, deflate"),
    new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6"),
    new BasicHeader("Cache-Control", "max-age=0"),
    new BasicHeader("Connection", "keep-alive"),
    //    new BasicHeader("Host", "m.byr.cn"),
    new BasicHeader("Upgrade-Insecure-Requests", "1"),
    new BasicHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36")
  )

  val sinanHttpHeaders = List[Header](
    new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
    new BasicHeader("Accept-Encoding", "gzip, deflate"),
    new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"),
    new BasicHeader("Cache-Control", "max-age=0"),
    new BasicHeader("Connection", "keep-alive"),
    //    new BasicHeader("Host", "m.byr.cn"),
    new BasicHeader("Upgrade-Insecure-Requests", "1"),
    new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
  )



  private val globalConfig = RequestConfig.custom()
    //    .setCookieSpec(CookieSpecs.BEST_MATCH)
    .setCircularRedirectsAllowed(false)
    .setRedirectsEnabled(false)
    .setConnectTimeout(10000)
    .setSocketTimeout(10000)
    //        .setConnectionRequestTimeout(10000)
    .build()

  private val retryHeader = new HttpRequestRetryHandler() {
    /*override def retryRequest(exception: IOException, executionCount: Int, context: HttpContext): Boolean = {
      if (executionCount >= 2) { // Do not retry if over max retry count
        return false
      }
      if (exception.isInstanceOf[InterruptedIOException]) { // Timeout
        return false
      }
      if (exception.isInstanceOf[UnknownHostException]) { // Unknown host
        return false
      }
      if (exception.isInstanceOf[ConnectTimeoutException]) { // Connection refused
        return false
      }
      if (exception.isInstanceOf[SSLException]) { // SSL handshake exception
        return false
      }
      val clientContext = HttpClientContext.adapt(context)
      val request = clientContext.getRequest
      val idempotent = !request.isInstanceOf[HttpEntityEnclosingRequest]
      if (idempotent) { // Retry if the request is considered idempotent
        return true
      }
      false
    }*/
    override def retryRequest(exception: IOException, executionCount: Int, context: HttpContext): Boolean = false
  }

  private val threadNum = 100

  val messageConstraints: MessageConstraints =
    MessageConstraints.
      custom.setMaxHeaderCount(200).
      setMaxLineLength(5000).build

  val connectionConfig: ConnectionConfig = ConnectionConfig.custom.
    setMalformedInputAction(CodingErrorAction.IGNORE).
    setUnmappableInputAction(CodingErrorAction.IGNORE).
    setCharset(Consts.UTF_8).setBufferSize(64 * 1024).
    setMessageConstraints(messageConstraints).build

  val cm = new PoolingHttpClientConnectionManager
  cm.setMaxTotal(threadNum + 5)
  cm.setDefaultMaxPerRoute(threadNum + 5)
  cm.setDefaultConnectionConfig(connectionConfig)

  lazy val httpClient: CloseableHttpClient =
    HttpClientBuilder.create()
      .setConnectionManager(cm)
      .setDefaultHeaders(httpHeaders.asJava)
      .setRetryHandler(retryHeader)
      .setDefaultRequestConfig(globalConfig).build()

  def fetchCookie(url: String,
                  data: String
                 )(implicit executor: ExecutionContext)={
    try{
      val request = new HttpPost(url)
      val clientContext = new HttpClientContext()

      val config = RequestConfig.custom()
        .setConnectTimeout(10000)
        .setSocketTimeout(10000)
        .build()
      request.setConfig(config)
      val response = httpClient.execute(request, clientContext)
      val headers = response.getAllHeaders
      val statusCode = response.getStatusLine.getStatusCode
      val entity = response.getEntity
      response.close()

      /*if (statusCode == HttpStatus.SC_OK) {
        Right(SpiderTaskSuccess(str))
      } else {
        Left(SpiderTaskError(str,statusCode))
      }*/

    } catch {
      case e: Exception =>
        log.debug(s"fetch url:$url error: $e")
        throw e
    }
  }


/*  def fetch(url: String,
            //proxyOption: Option[ProxyInfo],
            headersOp: Option[List[Header]] = None,
            cookieStore: Option[CookieStore] = None,
            code:String = "UTF-8"
           ): Future[Either[SpiderTaskError, SpiderTaskSuccess]] = {
    Future {
      try {
        val request = new HttpGet(url)

        headersOp.foreach(h => request.setHeaders(h.toArray))

        val clientContext = new HttpClientContext()

        cookieStore.foreach { c =>
          clientContext.setCookieStore(c)
        }

        //set proxy
/*        if (proxyOption.nonEmpty) {
          val proxy = proxyOption.get

          val httpHost = new HttpHost(proxy.ip, proxy.port.toInt)
          val config = RequestConfig.custom()
            .setProxy(httpHost)
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setRedirectsEnabled(false)
            .setCircularRedirectsAllowed(false)
            .setCookieSpec(CookieSpecs.DEFAULT)
            .build()
          request.setConfig(config)
        }*/

        val response = httpClient.execute(request, clientContext)
        val headers = response.getAllHeaders
        val statusCode = response.getStatusLine.getStatusCode
        val entity = response.getEntity
        val str = EntityUtils.toString(entity, code)//EntityUtils.toString(entity, "utf-8")
        EntityUtils.consume(response.getEntity)
        response.close()

        if (statusCode == HttpStatus.SC_OK) {
          Right(SpiderTaskSuccess(str))
        } else {
          Left(SpiderTaskError(str,statusCode))
        }
      } catch {
        case e: Exception =>
          log.debug(s"fetch url:$url error: $e")
          //返回
          throw e
//          Left(SpiderTaskError(s"${e.getMessage}",code = -1))
      }
    }
  }*/

  def fetchImg[A](
                   url: String,
                   proxyOption: Option[String],
                   headersOp: Option[Array[Header]] = None,
                   cookieStore: Option[CookieStore] = None
                 ): Future[Either[String, (Array[Byte], String)]] = {
    Future {
      try {
        val request = new HttpGet(url)

        headersOp.foreach(h => request.setHeaders(h))

        val clientContext = new HttpClientContext()

        cookieStore.foreach { c =>
          //                  log.debug(c.getCookies.toString)
          clientContext.setCookieStore(c)
        }

        //set proxy
        if (proxyOption.nonEmpty) {
          val proxy = proxyOption.get.split(":")

          val httpHost = new HttpHost(proxy(0), proxy(1).toInt)
          val config = RequestConfig.custom()
            .setProxy(httpHost)
            .setConnectTimeout(10000)
            .setSocketTimeout(10000)
            .setRedirectsEnabled(false)
            .setCircularRedirectsAllowed(false)
            .setCookieSpec(CookieSpecs.DEFAULT)
            .build()
          request.setConfig(config)
        }

        val response = httpClient.execute(request, clientContext)
        val statusCode = response.getStatusLine.getStatusCode
        log.debug(s"img: $url, response: ${response.getAllHeaders.toList}")
        val entity = response.getEntity
        val str = EntityUtils.toByteArray(entity) //EntityUtils.toString(entity, "utf-8")
        EntityUtils.consume(response.getEntity)
        response.close()

        if (statusCode == HttpStatus.SC_OK) {
          val contentType = response.getHeaders("Content-Type")
          val fileType = contentType(0).toString.split("/").last
          Right((str, fileType))
        } else {
          log.debug(s"not ok error: url: $url str: $str")
          Left("not ok")
        }
      } catch {
        case e: Exception =>
          log.debug(s"fetch url:$url error: $e")
          Left(e.toString)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println(s"start")
    //fetch("http://sports.163.com", None, None, None)
    Thread.sleep(40000)
    println(s"end")
  }


}