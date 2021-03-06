package com.neo.sk.todos2018.common

import java.util.concurrent.TimeUnit

import com.neo.sk.todos2018.models.SlickTables._
import com.neo.sk.todos2018.utils.SessionSupport.SessionConfig
import com.typesafe.config.{Config, ConfigFactory}
import com.neo.sk.todos2018.models.{sectionModel}

import org.slf4j.LoggerFactory
import slick.lifted.TableQuery
import scala.collection.mutable

/**
  * User: Taoz
  * Date: 9/4/2015
  * Time: 4:29 PM
  */
object AppSettings {

  private implicit class RichConfig(config: Config) {
    val noneValue = "none"

    def getOptionalString(path: String): Option[String] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getString(path))

    def getOptionalLong(path: String): Option[Long] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getLong(path))

    def getOptionalDurationSeconds(path: String): Option[Long] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getDuration(path, TimeUnit.SECONDS))
  }

  val homeUrl = "https://weibo.cn/5634035539"

  var user = Map("test"->"test", "scala"->"scala")


  val log = LoggerFactory.getLogger(this.getClass)
  val config = ConfigFactory.parseResources("product.conf").withFallback(ConfigFactory.load())

  val appConfig = config.getConfig("app")
  val dependence = config.getConfig("dependence")


  val httpInterface = appConfig.getString("http.interface")
  val httpPort = appConfig.getInt("http.port")
  val httpHost = appConfig.getString("http.host")
  val httpDomain =appConfig.getString("http.domain")
  
  val urlFirst = appConfig.getString("myUrl")
  val cookie = appConfig.getString("cookie")

  //用户注册信息
  val userMap = {
    import collection.JavaConverters._
    val users = appConfig.getStringList("user.users").asScala
    val pwd = appConfig.getStringList("user.pwd").asScala
    require(users.length == pwd.length, "userList.length and pwd.length not equal.")
    users.zip(pwd).toMap
  }

  val bupt = appConfig.getStringList("bupt")

  val slickConfig = config.getConfig("slick.db")
  val slickUrl = slickConfig.getString("url")
  val slickUser = slickConfig.getString("user")
  val slickPassword = slickConfig.getString("password")
  val slickMaximumPoolSize = slickConfig.getInt("maximumPoolSize")
  val slickConnectTimeout = slickConfig.getInt("connectTimeout")
  val slickIdleTimeout = slickConfig.getInt("idleTimeout")
  val slickMaxLifetime = slickConfig.getInt("maxLifetime")


  val sConf = config.getConfig("session")
  val sessionConfig = SessionConfig(
    cookieName = sConf.getString("cookie.name"),
    serverSecret = sConf.getString("serverSecret"),
    domain = sConf.getOptionalString("cookie.domain"),
    path = sConf.getOptionalString("cookie.path"),
    secure = sConf.getBoolean("cookie.secure"),
    httpOnly = sConf.getBoolean("cookie.httpOnly"),
    maxAge = sConf.getOptionalDurationSeconds("cookie.maxAge"),
    sessionEncryptData = sConf.getBoolean("encryptData")
  )
  val sessionTimeOut = sConf.getInt("sessionTimeOut")


  val hestiaConfig = config.getConfig("hestia")
  val hestiaProtocol = hestiaConfig.getString("protocol")
  val hestiaHost = hestiaConfig.getString("host")
  val hestiaPort = hestiaConfig.getString("port")
  val hestiaDomain = hestiaConfig.getString("domain")
  val hestiaAppId = hestiaConfig.getString("appId")
  val hestiaSecureKey = hestiaConfig.getString("secureKey")
  val hestiaAddress = hestiaConfig.getString("address")
  
  
  val url0 = "https://bbs.byr.cn/"
  val crawlUser = "?_uid=xiaopuwrl"
  val para = List[(String, String)](("_uid","xiaopuwrl"),
    ("accept:","application/json, text/javascript, */*; q=0.01"))
  val headers = List[(String,String)](("cookie","_ga=GA1.2.1729146850.1542343793; login-user=xiaopuwrl; Hm_lvt_38b0e830a659ea9a05888b924f641842=1545653366,1547791338,1548079234,1548125872; nforum[UTMPUSERID]=xiaopuwrl; nforum[PASSWORD]=Yj2IkhMit%2FxUk%2FEBI9HDJw%3D%3D; nforum[BMODE]=2; nforum[XWJOKE]=hoho; nforum-left=010; nforum[UTMPKEY]=8386724; nforum[UTMPNUM]=7998; Hm_lpvt_38b0e830a659ea9a05888b924f641842=1548139134"),
    ("x-requested-with","XMLHttpRequest"),
    ("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"),
    ("Accept-Encoding", "gzip, deflate, br"),
    ("accept-language","zh-CN,zh;q=0.9"),
    ("referer", "https://bbs.byr.cn/"))
  
  
  val headersWithoutCookie = List[(String,String)](
    ("x-requested-with","XMLHttpRequest"),
    ("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"),
    ("Accept-Encoding", "gzip, deflate, br"),
    ("content-type","text/html;charset=GBK"),
    ("accept-language","zh-CN,zh;q=0.9"),
    ("referer", "https://bbs.byr.cn/"))
  
  
  
  val section0 = List(("论坛使用帮助" , "/board/BBShelp"), ("积分" , "/board/Score"),
    ("帐号事务管理", "/board/ID"), ("版主及助理招聘区", "/board/BM_Market"),
    ("更新与改进", "/board/Progress"), ("意见与建议", "/board/Advice"),
    ("竞猜", "/board/Bet"), ("站务公告栏", "/board/Announce"),
    ("本站系统讨论区", "/board/sysop"), ("业务合作与广告投放", "/board/Cooperation"),
    ("新手测试区", "/board/test"), ("北邮人论坛十二周年站庆", "/board/BYR12"),
    ("论坛委员会", "/board/ForumCommittee"))
  
  val section1 = List(("北邮教务处","/board/AcademicAffairs"),
    ("北邮欢迎你","/board/AimBUPT"),
    ("北邮生活","/board/BUPT"),
    ("校园网","/board/BUPTNet"),
    ("北邮邮局","/board/BUPTPost"),
    ("北邮人公告栏","/board/BYR_Bulletin"),
    ("北邮校园卡","/board/CampusCard"),
    ("悼念周先生","/board/daonian"),
    ("北邮EID","/board/EID"),
    ("北邮关注","/board/Focus"),
    ("毕业生之家","/board/Graduation"),
    ("北邮后勤处","/board/Houqin"),
    ("北邮基建处","/board/JiJianChu"),
    ("军训快报","/board/Junxun"),
    ("北邮图书馆","/board/Library"),
    ("北邮记忆","/board/MyBUPT"),
    ("热点活动","/board/Recommend"),
    ("北邮保卫处","/board/SecurityDivision"),
    ("助学之家","/board/Selfsupport"),
    ("北邮学生处","/board/StudentAffairs"),
    ("邮问有答","/board/StudentQuery"))
  
  val section2 = List(("算法与程序设计竞赛","/board/ACM_ICPC"),
    ("BBS安装管理","/board/BBSMan_Dev"),
    ("北邮人开放平台","/board/BBSOpenAPI"),
    ("电子电路","/board/Circuit"),
    ("通信技术","/board/Communications"),
    ("C/C++程序设计语言","/board/CPP"),
    ("数据库技术","/board/Database"),
    (".NET程序设计","/board/dotNET"),
    ("经济学","/board/Economics"),
    ("嵌入式系统","/board/Embedded_System"),
    ("Go语言","/board/Golang"),
    ("电脑硬件与维修","/board/HardWare"),
    ("创新实践","/board/Innovation"),
    ("Java技术","/board/Java"),
    ("JavaScript语言","/board/JavaScript"),
    ("Linux操作系统","/board/Linux"),
    ("创客与开源硬件","/board/Makerclub"),
    ("数学建模","/board/MathModel"),
    ("Matlab实验室","/board/Matlab"),
    ("机器学习与数据挖掘","/board/ML_DM"),
    ("移动互联网","/board/MobileInternet"),
    ("智能终端开发技术","/board/MobileTerminalAT"),
    ("笔记本电脑","/board/Notebook"),
    ("办公软件","/board/OfficeTool"),
    ("科研与论文","/board/Paper"),
    ("Python","/board/Python"),
    ("机器人","/board/Robot"),
    ("搜索引擎","/board/SearchEngine"),
    ("信息安全","/board/Security"),
    ("智能车","/board/Smartcar"),
    ("软件开发","/board/SoftDesign"),
    ("大数据可视化","/board/Visualization"),
    ("Windows操作系统","/board/Windows"),
    ("WWW技术","/board/WWWTechnology"))
  
  val section3 = List(("考研专版","/board/AimGraduate"),
    ("学为人师，行为世范","/board/BNU"),
    ("北邮互联网俱乐部","/board/BUPT_Internet_Club"),
    ("北邮人在上海","/board/BYRatSH"),
    ("深圳邮人家","/board/BYRatSZ"),
    ("认证考试","/board/Certification"),
    ("公务员","/board/CivilServant"),
    ("管理咨询","/board/Consulting"),
    ("创业交流","/board/Entrepreneurship"),
    ("家庭生活","/board/FamilyLife"),
    ("金融职场","/board/Financecareer"),
    ("金融投资","/board/Financial"),
    ("飞跃重洋","/board/GoAbroad"),
    ("安居乐业","/board/Home"),
    ("信息产业","/board/IT"),
    ("毕业生找工作","/board/Job"),
    ("招聘信息专版","/board/JobInfo"),
    ("跳槽就业","/board/Jump"),
    ("网络资源","/board/NetResources"),
    ("海外北邮人","/board/Overseas"),
    ("兼职实习信息","/board/ParttimeJob"),
    ("产品疯人院","/board/PMatBUPT"),
    ("学习交流区","/board/StudyShare"),
    ("天气预报","/board/Weather"),
    ("职场人生","/board/WorkLife"))
  
  val section4 = List(("天文","/board/Astronomy"),
    ("辩论","/board/Debate"),
    ("视频制作","/board/DV"),
    ("英语吧","/board/EnglishBar"),
    ("奇闻异事","/board/Ghost"),
    ("吉他","/board/Guitar"),
    ("日语学习","/board/Japanese"),
    ("韩流吧","/board/KoreanWind"),
    ("音乐交流区","/board/Music"),
    ("摄影","/board/Photo"),
    ("诗词歌赋","/board/Poetry"),
    ("心理健康在线","/board/PsyHealthOnline"),
    ("曲苑杂谈","/board/Quyi"),
    ("书屋","/board/Reading"),
    ("科幻奇幻","/board/ScienceFiction"),
    ("T恤文化","/board/Tshirt"))
  
  val section5 = List(("美容护肤","/board/Beauty"),
    ("北邮愿望树","/board/Blessing"),
    ("衣衣不舍","/board/Clothing"),
    ("星雨星愿","/board/Constellations"),
    ("数字生活","/board/DigiLife"),
    ("创意生活","/board/DIYLife"),
    ("环境保护","/board/Environment"),
    ("情感的天空","/board/Feeling"),
    ("秀色可餐","/board/Food"),
    ("缘来如此","/board/Friends"),
    ("健康保健","/board/Health"),
    ("悄悄话","/board/IWhisper"),
    ("失物招领与拾金不昧","/board/LostandFound"),
    ("谈天说地","/board/Talking"))
  
  val section6 = List(("汽车之家","/board/AutoMotor"),
    ("桌面游戏","/board/BoardGame"),
    ("动漫交流区","/board/Comic"),
    ("闪客帝国","/board/Flash"),
    ("煮酒论剑","/board/Hero"),
    ("笑口常开","/board/Joke"),
    ("K歌之王","/board/KaraOK"),
    ("杀人俱乐部","/board/KillBar"),
    ("电影","/board/Movie"),
    ("网络文学","/board/NetLiterature"),
    ("北邮人投票","/board/nVote"),
    ("宠物家园","/board/Pet"),
    ("贴图秀","/board/Picture"),
    ("绿色心情","/board/Plant"),
    ("BYR在线广播","/board/RadioOnline"),
    ("娱乐星天地","/board/SuperStar"),
    ("海天游踪","/board/Travel"),
    ("电视剧","/board/TV"),
    ("视频酷","/board/VideoCool"))
  
  val section7 = List(("田径","/board/Athletics"),
    ("羽毛球","/board/Badminton"),
    ("篮球咖啡屋","/board/Basketball"),
    ("台球","/board/Billiards"),
    ("棋牌","/board/Chess"),
    ("梦想单车","/board/Cycling"),
    ("舞蹈","/board/Dancing"),
    ("足球吧","/board/Football"),
    ("极速赛车","/board/GSpeed"),
    ("健身房","/board/Gymnasium"),
    ("武术","/board/Kungfu"),
    ("橄榄球","/board/Rugby"),
    ("天行毽","/board/Shuttlecock"),
    ("滑板名堂","/board/Sk8"),
    ("北邮刷天下","/board/Skating"),
    ("滑雪","/board/Ski_Snowboard"),
    ("碧水情深","/board/Swim"),
    ("乒乓球","/board/Tabletennis"),
    ("跆拳道","/board/Taekwondo"),
    ("网球","/board/Tennis"),
    ("排球","/board/Volleyball"))
  
  val section8 = List(("地下城与勇士","/board/BUPTDNF"),
    ("反恐精英","/board/CStrike"),
    ("暗黑破坏神","/board/Diablo"),
    ("Dota","/board/Dota"),
    ("足球经理","/board/FootballManager"),
    ("炉石传说","/board/Hearthstone"),
    ("英雄联盟","/board/LOL"),
    ("网络游戏","/board/OnlineGame"),
    ("守望先锋","/board/OverWatch"),
    ("电脑游戏","/board/PCGame"),
    ("跑跑卡丁车","/board/PopKart"),
    ("绝地求生","/board/PUBG"),
    ("电子游戏","/board/TVGame"),
    ("实况足球","/board/WE"),
    ("魔兽世界","/board/WOW"),
    ("梦幻西游","/board/Xyq"))
  
  val section9 = List(("情淮徽皖·安徽","/board/Anhui"),
    ("粤广茶餐厅·广东","/board/Cantonese"),
    ("巴渝人家·重庆","/board/Chongqing"),
    ("八闽玲珑·福建","/board/Fujian"),
    ("西凉故道·甘肃","/board/Gansu"),
    ("桂香南疆·广西","/board/Guangxi"),
    ("景秀黔城·贵州","/board/Guizhou"),
    ("天涯海角·海南","/board/Hainan"),
    ("燕赵情怀·河北","/board/Hebei"),
    ("豫韵悠悠·河南","/board/Henan"),
    ("楚天邮情·湖北","/board/Hubei"),
    ("潇湘天下·湖南","/board/Hunan"),
    ("翱翔雄鹰·内蒙古","/board/InnerMongolia"),
    ("江淮人家·江苏","/board/Jiangsu"),
    ("江南西道·江西","/board/Jiangxi"),
    ("东北一家人·东北","/board/NorthEast"),
    ("北京四合院·北京","/board/Peking"),
    ("青深似海·青海","/board/Qinghai"),
    ("三秦大地·陕西","/board/Shaanxi"),
    ("齐鲁大地·山东","/board/Shandong"),
    ("桐叶封晋·山西","/board/Shanxi"),
    ("蜀山邮侠·四川","/board/Sichuan"),
    ("九河下梢·天津","/board/Tianjin"),
    ("天山南北·新疆","/board/Xinjiang"),
    ("钱塘人家·浙江","/board/Zhejiang"))

  //val sections = new mutable.HashMap[String, sectionModel]()
  
  val sectionList = mutable.Map("section0"->section0, "section1"->section1, "section2"->section2,
    "section3"->section3, "section4"->section4, "section5"->section5,
    "section6"->section6, "section7"->section7, "section8"->section8, "section9"->section9)
  
  //val sec = Map("section0"->section0)
  
  val sectionArray = Array("本站站务", "北邮校园", "学术科技", "信息社会", "人文艺术",
    "生活时尚", "休闲娱乐", "体育健身", "游戏对战", "乡亲乡爱")
  
  val SectionMap = List(("本站站务", "section/0"), ("北邮校园", "section/1"),
  ("学术科技", "section/2"), ("信息社会", "section/3"),
  ("人文艺术", "section/4"), ("生活时尚", "section/5"),
  ("休闲娱乐", "section/6"), ("体育健身", "section/7"),
  ("游戏对战", "section/8"), ("乡亲乡爱", "section/9"))
  
  val urlList = List("https://bbs.byr.cn/board/AimGraduate?_uid=xiaopuwrl")
  val pageList = List(("section","board","article","author","content","comment"))
}
