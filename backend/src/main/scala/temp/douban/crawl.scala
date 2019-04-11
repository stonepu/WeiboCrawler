package temp.douban

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.FileWriter
import java.util.concurrent.Executor

import com.neo.sk.todos2018.models.dao.BlogDao.{BlogDao, BlogUserDao}
import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import com.neo.sk.todos2018.service.CrawlWorker
import com.neo.sk.todos2018.utils.HttpUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element

import scala.collection.JavaConversions._
import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}
import com.neo.sk.todos2018.models.dao.ByrDAO
import com.neo.sk.todos2018.ptcl.Protocols.parseError
import com.neo.sk.todos2018.shared.ptcl.UserProtocol.SinaLoginReq
import com.neo.sk.todos2018.shared.ptcl.{ErrorRsp, SuccessRsp}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Random, Success}
//import akka.http.scaladsl.server.Directives._
import org.slf4j.LoggerFactory
import scala.language.experimental.macros
import scala.io.StdIn
import scala.util.{Failure, Success}
import com.neo.sk.todos2018.common.AppSettings._
import com.neo.sk.todos2018.models.dao.CrawlDAO

import java.util.Timer
import java.util.TimerTask
import java.util.Date
import io.circe.generic.auto._
import io.circe.syntax._


object crawl extends HttpUtil {
	
	
	val boardListConfig = List(("论坛使用帮助","/board/BBShelp"),("积分","/board/Score"))
	val articleListConfig = List(("【问题】身份证号或毕业证编号错误[2]","/article/ID/12539"),
	("账号登陆提示不存在或密码错误，但是我这个密码都没改过","/article/ID/12924"),
	("请帮忙修改手机号","/article/ID/12925"),
	("请管理员协助修改手机号","/article/ID/12927"),
	("手机号修改不了，不能发帖","/article/ID/12928"),
	("手机号已换，不能发帖","/article/ID/12929"))
	
	val log = LoggerFactory.getLogger(this.getClass)
	
	val page = List(("论坛使用帮助","/board/BBShelp"),("积分","/board/Score"))
	
	
	var boardAndLink = mutable.Map[String,String]()
	var articleLists = List[(String, String)]()
	
	val uu = "https://bbs.byr.cn/board/Advice?_uid=xiaopuwrl"
	//解析出board
	def parseBoard(html: String, i: Int)={      //通过使用for循环遍历所有section
		val doc = Jsoup.parse(html)
		val link = doc.select("a[href]")
		println(s"------section$i-------")
		for(links<- link){
			//println(links)
			if(links.attr("href").startsWith("/board")) {
				val s1 = links.text()
				val s2 = links.attr("href")
				boardAndLink += (links.text() -> links.attr("href"))
				//boardList += (links.text() -> links.attr("href"))
				val ss = "(\"" + s1 + "\"," + "\"" + s2 + "\"),"
				println(ss)
			}
		}
	}
	
	//解析出文章目录，返回文章目录列表
	def parseArticleCatalog(html: String)={
		//var article = List[(String, String)]()
		val doc = Jsoup.parse(html)
		val articles = doc.select("td[class=title_9]")
		for(elem<- articles){
			articleLists = (s"${elem.text()}" ,s"${elem.select("a[href]").attr("href")}")::articleLists
			println((s"${elem.text()}" ,s"${elem.select("a[href]").attr("href")}"))
		}
		//articleLists = (("*"*20, "_"*20))::articleLists
		
		//article
	}
	
	//解析文章内容
	def parseArticle(html: String, section: String)={
		val doc = Jsoup.parse(html)
		val article = doc.select(".a-content-wrap").first()
		val articleContent = article.text
		val authArray = articleContent.split("信区: ")
		val author = authArray(0).drop(5).dropRight(2)
		val boardTemp = authArray(1).split(" 标 题: ")
		val titleTemp = boardTemp(1).split(" 发信站: ")
		val contentArr = titleTemp(1).split("站内 ")(1).split(" -- ")
		var content = contentArr(0)
		content =  if(!content.startsWith("--")) content else ""
		CrawlDAO.update(section, boardTemp(0), titleTemp(0), content, author, "comment")
		println((s"$section", boardTemp(0), titleTemp(0), content, author))
	}
	
	def getBoard()= {
		for(i<-9 to 9) {
			val url = s"${url0}section/${i+crawlUser}"
			getRequestSend("get",url,para,headers,"GBK").onComplete{
				case Success(t) => t.map{
					case s =>
						parseBoard(s, i)
						Thread.sleep(7000)
						
						//getArticleCatalog(boardAndLink)
				}
				case Failure(error) =>
					log.error(s"error: $error")
			}
		}
	}
	
	def getArticleCatalog(boardList: List[(String, String)]) ={
		for(board<- boardList) {
			val urlBoard = s"${url0 + board._2.drop(1) + crawlUser}"
			println(urlBoard)
			getRequestSend("get", urlBoard, para, headers, "GBK").onComplete{
				case Success(t) => t.map{
					case s =>
						if(s.length<100) println(s)
						else{
							parseArticleCatalog(s)
						}
						//getArticle(articleLists)
				}
				case Failure(error) =>
					log.error(s"error: $error")
			}
			Thread.sleep(7000)
		}
	}
	
	def getArticle(articleList: List[(String, String)])= {
		for(articleAdd<- articleList) {
			val urlArticle = s"${url0 + articleAdd._2 + crawlUser}"
			getRequestSend("get", urlArticle, para, headers, "GBK").onComplete{
				case Success(t) => t.map{
					case sA =>
						if(sA.length<100) println(sA)
						else{
							parseArticle(sA,"")
						}
				}
				case Failure(error) =>
					log.error(s"some error: $error")
			}
			Thread.sleep(6000)
		}
	}

	
	class Task extends TimerTask {
		override def run(): Unit = {
			println("a")
		}
	}

	val cookieList = List(
		"_T_WM=5947a1d702c4dbdd4e454ec2179b1dfa; SCF=ApADI1wos8wHnm2s8fcD32DDN4QwoNYISvMiN4cWg8YgdVJgfsb1A0ghTb1X5AKV2ptdzZz9i9fVbWEmCCkau8M.; SUB=_2A25xrczyDeRhGeNI6FYR8yvJyDWIHXVTUdS6rDV6PUJbkdAKLW3fkW1NSGkjdSGglnBv3aj5PySK2iUnThRO5DSp; SUHB=08J7Oocqc1K_5p; SSOLoginState=1554627746",
		"_T_WM=67c43fc6f07d6f0bc72e12396c9dc229; SSOLoginState=1554627766; ALF=1557219487; SUB=_2A25xrczmDeRhGeNH7VsQ9C_JzTSIHXVTUdSurDV6PUJbkdAKLW3ckW1NSpiROQP1DU4E49KOlh2-KiHKv4lJBgWv; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5GPGZvcSE.QN45i16NBZ065JpX5KzhUgL.Fo-ce0B7e0-fe0.2dJLoIEBLxK.L1-eLBonLxKqLBo5L1KBLxK-LBo5L12qLxKqLBo-LBKqt; SUHB=0Jvg3Ot-4bKQPU; TMPTOKEN=a8peEGyfaQWUEbFl5ECpH5NUk4vYPLy2RcWuvsoJodvEYtvQWQrIX1ewMG0EnHTA; SCF=ApsGpacAzIDugActkKF_v8mNBt3OAN6cyB8pmXjH4RVCzuDix4OyuRYuOvuM_SmNg8T4VDLSJuj4ugq68IRzE0k."
	)

	val cookie = cookieList(Random.nextInt(2))
	val urltest = "https://weibo.com/p/1003061496852380/follow?from=page_100306&wvr=6&mod=headfollow#place\\"
	val uRL = "https://login.sina.com.cn/signup/signin.php?entry=sso"//登录
	val uus="https://weibo.com/u/5634035539/home?topnav=1&wvr=6"
	var headerss = List[(String, String)](
		("cookie",
			//"SINAGLOBAL=3394852794837.242.1552381969329; un=17730044033; wb_timefeed_5634035539=1; wvr=6; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5GPGZvcSE.QN45i16NBZ065JpX5KMhUgL.Fo-ce0B7e0-fe0.2dJLoIEBLxK.L1-eLBonLxKqLBo5L1KBLxK-LBo5L12qLxKqLBo-LBKqt; _s_tentry=-; Apache=7439775997205.327.1553134300266; YF-V5-G0=59104684d5296c124160a1b451efa4ac; ULV=1553134301073:12:12:6:7439775997205.327.1553134300266:1553062180115; ALF=1584674648; SSOLoginState=1553138648; SCF=AimQ_zZMR5J-WwDjh0kbA4JZXH1JsFNG_788XiSGQpVLbbqoxF9R6MHZH9C5LwGmnBfAAMu0Jf2yfu6rSj5ttkw.; SUB=_2A25xl3OJDeRhGeNI6FYR8yvJyDWIHXVS5eJBrDV8PUNbmtBeLUjlkW9NSGkjdR_mLx7fT0YvE25vqcj-FRvBB8oU; SUHB=0IIVz4dDYm2yNK; YF-Page-G0=da1eb9ea7ccc47f9e865137ccb4cf9f3|1553138653|1553138647; wb_view_log_5634035539=1536*8641.25; UOR=,,login.sina.com.cn; webim_unReadCount=%7B%22time%22%3A1553138666591%2C%22dm_pub_total%22%3A0%2C%22chat_group_pc%22%3A0%2C%22allcountNum%22%3A32%2C%22msgbox%22%3A0%7D"),
			//"Ugrow-G0=ea90f703b7694b74b62d38420b5273df; login_sid_t=f63a9d0b72d6334968bddfc42937faaa; cross_origin_proto=SSL; YF-V5-G0=a5a6106293f9aeef5e34a2e71f04fae4; wb_view_log=1536*8641.25; _s_tentry=passport.weibo.com; Apache=9048432341155.57.1554031559560; SINAGLOBAL=9048432341155.57.1554031559560; ULV=1554031559569:1:1:1:9048432341155.57.1554031559560:; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5GPGZvcSE.QN45i16NBZ065JpX5K2hUgL.Fo-ce0B7e0-fe0.2dJLoIEBLxK.L1-eLBonLxKqLBo5L1KBLxK-LBo5L12qLxKqLBo-LBKqt; ALF=1585567568; SSOLoginState=1554031568; SCF=AqX-xrLUyp2QwtVSxWgWaojxgaBqxQNcw223M1DPxwreY6OGwyTRSYONkj6E1OGnMGElHnMYpMGiklbHj9CAf8A.; SUB=_2A25xpNOADeRhGeNI6FYR8yvJyDWIHXVS0EJIrDV8PUNbmtAKLVfkkW9NSGkjdU8Qb2QHxLaA85brT3H_esUsskB5; SUHB=0N21q-zst14Gic; un=17730044033; wvr=6; wb_view_log_5634035539=1536*8641.25; wb_timefeed_5634035539=1; YF-Page-G0=89906ffc3e521323122dac5d52f3e959|1554031901|1554031872; webim_unReadCount=%7B%22time%22%3A1554039929943%2C%22dm_pub_total%22%3A0%2C%22chat_group_pc%22%3A0%2C%22allcountNum%22%3A36%2C%22msgbox%22%3A0%7D"),
			//"SINAGLOBAL=9048432341155.57.1554031559560; un=17730044033; wvr=6; wb_timefeed_5634035539=1; wb_view_log_5634035539=1536*8641.25; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=ed42be97dfbb9002c96cedb08eb4042d; cross_origin_proto=SSL; YF-V5-G0=3717816620d23c89a2402129ebf80935; _s_tentry=passport.weibo.com; UOR=,,www.baidu.com; wb_view_log=1536*8641.25; Apache=4800595011037.707.1554080752613; ULV=1554080752647:2:1:2:4800595011037.707.1554080752613:1554031559569; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5GPGZvcSE.QN45i16NBZ065JpX5K2hUgL.Fo-ce0B7e0-fe0.2dJLoIEBLxK.L1-eLBonLxKqLBo5L1KBLxK-LBo5L12qLxKqLBo-LBKqt; SSOLoginState=1554080763; ALF=1585616775; SCF=AqX-xrLUyp2QwtVSxWgWaojxgaBqxQNcw223M1DPxwreNz6Wp0wJAobakd7Q9SSfzdHX3UR7MZqJVoyyWSKp2Yc.; SUB=_2A25xpRRYDeRhGeNI6FYR8yvJyDWIHXVS0wKQrDV8PUNbmtBeLXX8kW9NSGkjdTRxa5hqvSKMdKnFcZwoRTJYlxvZ; SUHB=0qj0S79aO979T6; YF-Page-G0=bf52586d49155798180a63302f873b5e|1554083034|1554082847; webim_unReadCount=%7B%22time%22%3A1554083183669%2C%22dm_pub_total%22%3A0%2C%22chat_group_pc%22%3A0%2C%22allcountNum%22%3A32%2C%22msgbox%22%3A0%7D"),
			//"SINAGLOBAL=9048432341155.57.1554031559560; un=17730044033; wvr=6; wb_timefeed_5634035539=1; wb_view_log_5634035539=1536*8641.25; UOR=,,www.baidu.com; wb_view_log=1536*8641.25; webim_unReadCount=%7B%22time%22%3A1554095885956%2C%22dm_pub_total%22%3A0%2C%22chat_group_pc%22%3A0%2C%22allcountNum%22%3A33%2C%22msgbox%22%3A0%7D; _s_tentry=-; Apache=2725753084875.1333.1554096046003; ULV=1554096046957:3:2:3:2725753084875.1333.1554096046003:1554080752647; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5GPGZvcSE.QN45i16NBZ065JpX5KMhUgL.Fo-ce0B7e0-fe0.2dJLoIEBLxK.L1-eLBonLxKqLBo5L1KBLxK-LBo5L12qLxKqLBo-LBKqt; ALF=1585634343; SSOLoginState=1554098344; YF-V5-G0=3717816620d23c89a2402129ebf80935; SCF=AqX-xrLUyp2QwtVSxWgWaojxgaBqxQNcw223M1DPxwrewW-JC3niEqvpJ75WI1OIRxtAXWVT9tgk4bWbGGnUaX4.; SUB=_2A25xpdj5DeRhGeNI6FYR8yvJyDWIHXVS000xrDV8PUNbmtBeLUnskW9NSGkjdXN_pWAjNAsR-FuqunU0jcBxeCtl; SUHB=0wn1n54xoyN3IO; YF-Page-G0=854ebb7f403eecfa60ed1f0e977c6825|1554098346|1554098343"),
			//"SINAGLOBAL=9048432341155.57.1554031559560; un=17730044033; wvr=6; wb_timefeed_5634035539=1; UOR=,,www.baidu.com; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=dbf163e5b49df77c94b402d3f9720e82; cross_origin_proto=SSL; YF-V5-G0=c998e7c570da2f8537944063e27af755; WBStorage=201904021042|undefined; wb_view_log=1536*8641.25; _s_tentry=passport.weibo.com; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5GPGZvcSE.QN45i16NBZ065JpX5K2hUgL.Fo-ce0B7e0-fe0.2dJLoIEBLxK.L1-eLBonLxKqLBo5L1KBLxK-LBo5L12qLxKqLBo-LBKqt; ALF=1585708956; SSOLoginState=1554172957; SCF=AqX-xrLUyp2QwtVSxWgWaojxgaBqxQNcw223M1DPxwreuFond43HzQ4dMtXcaeFitrSgojdDSdNOScMku4bm7Bs.; SUB=_2A25xprxODeRhGeNI6FYR8yvJyDWIHXVS1aqGrDV8PUNbmtBeLXPNkW9NSGkjdVd_n55khHzEQWQWZVq5ZLBPukUu; SUHB=0S7C1RjyRyIJUL; Apache=1417573889845.9001.1554172960231; ULV=1554172960239:4:3:4:1417573889845.9001.1554172960231:1554096046957; YF-Page-G0=46f5b98560a83dd9bfdd28c040a3673e|1554172965|1554172965; wb_view_log_5634035539=1536*8641.25; webim_unReadCount=%7B%22time%22%3A1554172967160%2C%22dm_pub_total%22%3A0%2C%22chat_group_pc%22%3A0%2C%22allcountNum%22%3A32%2C%22msgbox%22%3A0%7D"),
			//"__T_WM=8804be73cafed6c5cba7d62d974d3ff7; SUB=_2A25xpr6VDeRhGeNI6FYR8yvJyDWIHXVTaMLdrDV6PUJbkdANLUfNkW1NSGkjdQKJkcPzi00rLUp33Sub7t1hMrFB; SUHB=0YhR3CdUYrckQr; SCF=ArXy39TfU9eDr_REvj4APwAVXexPhyCq4_1QxFRKnISZE_JJxTk_dO99jJhFI6odGVWpWV_BHKgB5if7H97OH4w.; SSOLoginState=1554173637"),
			"_T_WM=5947a1d702c4dbdd4e454ec2179b1dfa; SCF=ApADI1wos8wHnm2s8fcD32DDN4QwoNYISvMiN4cWg8YgdVJgfsb1A0ghTb1X5AKV2ptdzZz9i9fVbWEmCCkau8M.; SUB=_2A25xrczyDeRhGeNI6FYR8yvJyDWIHXVTUdS6rDV6PUJbkdAKLW3fkW1NSGkjdSGglnBv3aj5PySK2iUnThRO5DSp; SUHB=08J7Oocqc1K_5p; SSOLoginState=1554627746"),

		("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36"))
	val paras = List[(String, String)](("from","page_100306"))

  def getHtml(string: String)={
    var htmlList = List[String]()
    val str = string.replace("\\n", "").replace("\\r","").replace("\\t","").replace("\\","")
    val doc = Jsoup.parse(str)
    val scripts = doc.select("script")
    scripts.map{ script =>
      val pattern = "\"html.*?\\}".r
      val html = pattern.findFirstIn(script.toString).getOrElse("").dropRight(2).drop(8)
      if(!html.isEmpty){
        htmlList = html::htmlList
        println(html)
      }
    }
    //parseHtml(htmlList)
    htmlList
  }

	def parseHtml(list: List[String])={
		var urlList = List[String]()

		list.map{html =>
			val doc1 = Jsoup.parse(html)
			//val aLabel = doc1.getElementsByClass("user_atten clearfix W_f18").select("a[href]")
			val aLabel = doc1.getElementsByClass("tb_counter").select("a[href]")

			aLabel.map{a=>
				val attr = a.attr("href")
				if(!attr.isEmpty){
					val url = s"https://weibo.com$attr"
					urlList = url::urlList
					println(url)
				}
			}
		}
		println(urlList.length)
		urlList
	}

  def parseFollow(list: List[String]) = {
    var urlList = List[String]()
    list.map{ html =>
      val doc = Jsoup.parse(html)
      val aLabel = doc.getElementsByClass("mod_pic S_line1").select("a[href]")
      aLabel.map{ link =>
        val u = link.attr("href")
        if(!u.isEmpty){
          val url = s"https://weibo.com$u"
          urlList = url::urlList
          println(url)
        }
      }
    }
		println(urlList.length)
    urlList
  }

	def parse(string: String) = {
		var list = List[String]()
		val doc = Jsoup.parse(string)
		val body = doc.body()
		println(body)
		val href = body.getElementsByClass("tip2").select("a[href]")
		//println(href)
		href.foreach{link =>
			println(s"link: $link")
			val url = link.attr("href")
			println(url)
			list = url::list
		}
		list
	}




	//=========================real

	def parseBlogHtml(html: String) = {
		val urlList = mutable.HashMap[String, String]()
		if(html.length > 10){
			val doc = Jsoup.parse(html).body()
			val name = doc.getElementsByClass("ut").text().split(" ")(0)
			println(s"name =======$name============")
			val relation = doc.getElementsByClass("tip2").select("a[href]")//.attr("href")
			relation.forEach{link =>
				val str = "https://weibo.cn"
				val u = link.attr("href")
				println(str+u)
				urlList(s"${link.text()}") = s"${str+u}"
			}
		}
		urlList
	}

	def parseAtcl(url: String, html: String) = {
    //println(s"=====开始解析$url ========")
		if(html.length > 10){
			val doc = Jsoup.parse(html).body()
			var name = doc.getElementsByClass("ut")(0).text().split(" ")(0)
			if(name.contains("的微博")) name = name.dropRight(3)
			val contents = doc.getElementsByClass("c").drop(1).dropRight(2)
      //println(s"===contents.length = ${contents.length}=====")
      //println(contents(0))
			for(content<- contents){
				var time = "None"
				if(content.getElementsByClass("ct").length>0)
					time = content.getElementsByClass("ct")(0).text()
				val patternLike = "赞(.*?)]".r
				var like = patternLike.findFirstIn(content.toString).getOrElse("12None3").drop(2).dropRight(1)
				like = if(like.length > 8) "0" else like
				val patternForward = "转发(.*?)]".r
				var forward = patternForward.findFirstIn(content.toString).getOrElse("123None4").drop(3).dropRight(1)
				forward = if(forward.length > 8) "0" else forward
				val patternComment = "评论(.*?)]".r
				var comment = patternComment.findFirstIn(content.toString).getOrElse("123None4").drop(3).dropRight(1)
				comment = if(comment.length > 8) "0" else comment
        if(content.getElementsByClass("cc").length == 0){
          println("got wrong content!")
          println(content)
        }
        val commentUrl = content.getElementsByClass("cc")(0).attr("href")
				BlogDao.updateBlog(Some(name), Some(url), Some(content.toString),
					Some(time), Some(like), Some(forward), Some(comment), Some(commentUrl))
				/*println("文章信息=======")
				println(name)
				println(url)
				println(time)
				println(s"like: $like")
				println(s"forward: $forward")
				println(s"comment: $comment")
				println(s"commentUrl: $commentUrl")*/
			}
		}
	}

	def getInfo(html: String) = {
		if(html.length > 10){
			var nickname = "None"
			var gender: Char = 'n'
			var homeUrl = "None"
			var imgUrl = "None"
			var certificateInfo = "None"
			var introduce = "None"
			var region = "None"
			var birth = "None"
			var photoUrl = "None"
			var follow = "None"
			var fans = "None"

			val doc = Jsoup.parse(html).body()
			val img = doc.select("img[src]")
			for(link<- img){
				if(link.attr("alt") == "头像")
					imgUrl = link.attr("src")
			}
			val patternName = "昵称(.*?)\\n".r
			nickname = patternName.findFirstIn(doc.toString).getOrElse("123None4").drop(3).dropRight(1)

			val patternCertify = "认证:(.*?)\\n".r
			certificateInfo = patternCertify.findFirstIn(doc.toString).getOrElse("123None4").drop(3).dropRight(1)

			val patternGender = "性别:(.*?)\\n".r
			val tempGend = patternGender.findFirstIn(doc.toString).getOrElse("123n4").drop(3).dropRight(1).toCharArray
			gender = tempGend(0)
			val patternRegion = "地区:(.*?)\\n".r
			region = patternRegion.findFirstIn(doc.toString).getOrElse("123None4").drop(3).dropRight(1)

			val patternBirth = "生日:(.*?)\\n".r
			birth = patternBirth.findFirstIn(doc.toString).getOrElse("123None4").drop(3).dropRight(1)

			val patternProduct = "简介:(.*?)\\n".r
			introduce = patternProduct.findFirstIn(doc.toString).getOrElse("123None4").drop(3).dropRight(1)

			val patternHome = "手机版(.*?)\\n".r
			homeUrl = patternHome.findFirstIn(doc.toString).getOrElse("123None4").drop(4).dropRight(1)

			val aUrl = doc.select("a[href]")
			for(link<-aUrl){
				if(link.text().contains("相册")) photoUrl = "https://weibo.cn"+link.attr("href")
			}
			BlogUserDao.updateUser(Some(nickname), Some(homeUrl), Some(imgUrl), Some(gender), Some(certificateInfo),
				Some(introduce), Some(region), Some(birth), Some(photoUrl), Some(follow), Some(fans))
			/*println("用户信息=========")
			println(nickname)
			println(gender)
			println(certificateInfo)
			println(homeUrl)
			println(imgUrl)
			println(introduce)
			println(region)
			println(birth)
			println(photoUrl)*/
		}
	}

	def getPage(html: String) = {
		var input = "0"
		if(html.length > 10){
			val doc = Jsoup.parse(html).body()
      if(doc.getElementsByClass("pa").length != 0){//轻度用户不用分页
        val page = doc.getElementsByClass("pa")(0)
        input = page.getElementsByTag("input")(0).attr("value")
      }
		}
		input.toInt
	}

	def get4Url(html: String) = {
		val urlList = ListBuffer[String]()
		val urlMap = mutable.HashMap[String, String]()
		if(html.length > 10){
			val doc = Jsoup.parse(html).body()
			val info = doc.getElementsByClass("ut")(0).select("a[href]")
			val patternInfo = "([0-9]*)/info".r
			val infoUrl = patternInfo.findFirstIn(info.toString).get
			urlMap("info") = "https://weibo.cn/" + infoUrl
			val blog = doc.getElementsByClass("tip2")(0).getElementsByClass("tc").text().drop(3).dropRight(1)
			//urlMap("blog") = blog
			urlList += blog
			val relations = doc.getElementsByClass("tip2")(0).select("a[href]")
			for(link<- relations){
				val str = "https://weibo.cn"
				val url = str + link.attr("href")
				//if(!url.contains("attgroup")) urlList += url
				if(url.contains("follow")) urlMap("follow") = url
				else if(url.contains("fans")) urlMap("fans") = url
				else if(url.contains("at")) urlMap("at") = url
			}
		}
		urlMap
	}

	def fetch(url :String, paras: List[(String, String)] = List()):Future[String] = {
		var str = ""
		headerss = headerss.drop(1)
		headerss = ("cookie", cookieList(Random.nextInt(2)))::headerss
		getRequestSend("get", url, paras, headerss, "UTF-8").map{
			case Right(value) =>
				str = value
				str
			case Left(error) =>
				println(s"error: $error")
				str
		}
	}

  def parseMyHome(html: String) = {
		val urlList = ListBuffer[String]()
		if(html.length > 10){
			val doc = Jsoup.parse(html).body()
			val relations = doc.getElementsByClass("tip2")(0).select("a[href]")
			val follow = "https://weibo.cn" + relations(1).attr("href")
			val fans = "https://weibo.cn" + relations(2).attr("href")
			urlList += follow
			urlList += fans
			println(follow)
			println(fans)

		}
		urlList.toList
  }

	def parseFollow(html: String) = {//fans一样
		val urlList = ListBuffer[String]()
		if(html.length > 10){
			val doc = Jsoup.parse(html)
			val tables = doc.getElementsByTag("table")
			println(tables.length)
			for(table<- tables){
				val url = table.select("a[href]")(0).attr("href")
				urlList += url
				//println(url)
			}
		}
    println("已完成********************")
    urlList.toList
	}















	//=================================



	
	def main(args: Array[String]): Unit = {
		/*//定时器
		val timer = new Timer(true)
		val task = new Task()
		timer.schedule(task, 10, 1000)*/

		val infoUrl = "https://weibo.cn/2058586920/info"
		val infourl = "https://weibo.cn/2140522467/info"
		val myurl = "https://weibo.cn/u/5626860132 "
		val zwurl = "https://weibo.cn/jerrymusic"
		val zwfollow = "https://weibo.cn/1295674790/follow"
		val myfollow = "https://weibo.cn/5634035539/follow"
		val myfans = "https://weibo.cn/1746227731/fans"
		val sunurl = "https://weibo.cn/sunsonglin"
		getRequestSend("get", myurl, paras, headerss, "UTF-8").map{
			case Right(value) =>
//				val htmlList = getHtml(value)
//				println(htmlList)
				val doc = Jsoup.parse(value).body()
        //println(doc)
        parseAtcl(myurl, value)
				//parseFollow(value)
				//get4Url(value)
				//getPage(value)
				//parseAtcl(urlSun, value)
        //parseMyHome(value)
				println("")
				//parseBlogHtml(value)
				//parse(value)
			case Left(error) =>
				println(s"=====error:$error")
		}
//    val url = "https://weibo.com/5634035539/follow?rightmod=1&wvr=6"
//		getRequestSend("get",url, paras, headerss,"UTF-8").map{
//			case Right(value) =>
//				val folloList = parseFollow(getHtml(value))
//        //println(value)
//				for(urls<-folloList)	{
//					getRequestSend("get", urls, paras, headerss, "UTF-8").onComplete{
//						case Success(t) => t.map{
//							case value =>
//								parseHtml(getHtml(value))
//						}
//							Thread.sleep(7000)
//						case Failure(error) =>
//							println(s"error:$error=======第二行")
//							Thread.sleep(7000)
//					}
//				}
//			case Left(error) =>
//				println(s"=====error:$error")
//		}
//
		/*val data = (sinaUsername, sinaPassword).asJson.noSpaces

		postJsonRequestSend("post", uRL,paras, data,headerss,"GBK").map{
			case Right(html) =>
				val doc = Jsoup.parse(html)
				println(doc)
			case Left(error) =>
				println(s"=====error:$error")
		}*/

		// ---------------------------------------
		StdIn.readLine()
	}
}