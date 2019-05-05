package com.neo.sk.utils


import java.io.{IOException, InterruptedIOException}
import java.net.UnknownHostException
import java.nio.charset.CodingErrorAction
import javax.net.ssl.SSLException

import org.apache.http.client.{CookieStore, HttpRequestRetryHandler}
import org.apache.http._
import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.concurrent.Future
import com.neo.sk.todos2018.Boot.executor
import org.apache.http.config.{ConnectionConfig, MessageConstraints}
import org.apache.http.conn.ConnectTimeoutException
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.protocol.HttpContext

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

  private val globalConfig = RequestConfig.custom()
    //    .setCookieSpec(CookieSpecs.BEST_MATCH)
    .setCircularRedirectsAllowed(false)
    .setRedirectsEnabled(false)
    .setConnectTimeout(10000)
    .setSocketTimeout(10000)
    //        .setConnectionRequestTimeout(10000)
    .build()

  private val retryHeader = new HttpRequestRetryHandler() {
    override def retryRequest(exception: IOException, executionCount: Int, context: HttpContext): Boolean = {
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
    }
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

  val proxy = Some("")

  def fetch(url: String, proxyOption: Option[String]=Some(""), headersOp: Option[Array[Header]] = None, cookieStore: Option[CookieStore] = None): Future[Either[String, String]] = {
    Future {
      try {
        val request = new HttpGet(url)

        headersOp.foreach(h => request.setHeaders(h))

        val clientContext = new HttpClientContext()

        cookieStore.foreach { c =>
          //        log.debug(c.getCookies.toString)
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
        val entity = response.getEntity
        val str = EntityUtils.toString(entity, "utf-8")
        EntityUtils.consume(response.getEntity)
        response.close()

        if (statusCode == HttpStatus.SC_OK) {
          Right(str)
        } else {
          Left(str)
        }
      } catch {
        case e: Exception =>
          log.debug(s"fetch url:$url error: $e")
          //        e.printStackTrace()

          //删除代理
          //        if (proxyOption.nonEmpty) {
          //          deleteProxy(proxyOption.get)
          //        }

          Left(e.toString)
      }
    }
  }

}
