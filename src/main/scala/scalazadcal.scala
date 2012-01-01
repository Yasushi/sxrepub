import dispatch._
import dispatch.tagsoup.TagSoupHttp._

import nl.siegmann.epublib.domain.Author
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubWriter

import java.io.File
import java.io.FileOutputStream

import scala.io.Source
import scala.xml._
import scala.xml.parsing.XhtmlParser
import scala.xml.transform._
import scala.collection.mutable.{HashMap => MHashMap}

object scalazadcal extends App{
  val http = new Http

  val gists = new MHashMap[String, NodeSeq]

  def getGist(gistUrl: String) =
    gists.get(gistUrl) match {
      case Some(g) => g
      case None =>
        val g = http(url(gistUrl) >- {s =>
          """document\.write\('(<div .+)'\)""".r.findFirstMatchIn(s).map(_.group(1).replaceAll("""\\n""", "\n").replaceAll("""\\(.)""", "$1").replaceAll(" id=\"gist-1423217\"", "").replaceAll(""" id=["']LC\d+["']""", "")).map(g => XhtmlParser(Source.fromString(g)))
        }).getOrElse(NodeSeq.Empty)
        gists.put(gistUrl, g)
        g
    }

  def as_body(ns: NodeSeq) = {
    val removeFooter = new RewriteRule{
      override def transform(n: Node) =
        if (n.label == "div" && (n \ "@class").text == "post-footer")
          NodeSeq.Empty
        else
          n
    }
    val extractGist = new RewriteRule {
      override def transform(n: Node) =
        if (n.label == "script" && (n \ "@src").text.startsWith("https://gist.github.com/")) {
          getGist((n \ "@src").text)
        } else
          n
    }
    val removeInvalidAttrs = new RewriteRule {
      override def transform(n: Node) = n match {
        case a:Elem if a.label == "a" =>
          a.copy(attributes=a.attributes.remove("name"))
        case br:Elem if br.label == "br" =>
          br.copy(attributes=br.attributes.remove("clear"))
        case _ => n
      }
    }
    def isPostHentry(n: Node) =
      n.attribute("class").map(_.text=="post hentry").getOrElse(false)
    val transformer =
      new RuleTransformer(removeFooter, extractGist, removeInvalidAttrs)
    def toDoc(n: Node) =
      <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja">
        <head>
          <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
          <title></title>
          <link rel="stylesheet" href="gistembed.css"/>
        </head>
        <body>{n}</body>
      </html>

    (ns \\ "div").filter(isPostHentry).headOption.map(transformer).map(toDoc)
  }

  def getArticle(articleUrl: String) = {
    val req = url(articleUrl)
    http(req </> as_body) match {
      case Some(body) =>
        new Resource(Xhtml.toXhtml(body).getBytes("UTF-8"), (new File(articleUrl)).getName)
      case _ =>
        new Resource((new File(articleUrl)).getName)

    }
  }

  val book = new Book
  book.getMetadata.addTitle("一人 Scalaz Advent Calendar 2011")
  book.getMetadata.addAuthor(new Author("ねこはる"))

  book.addSection("Identity", getArticle("http://basking-cat.blogspot.com/2011/12/identity.html"))
  book.addSection("Equal", getArticle("http://basking-cat.blogspot.com/2011/12/equal.html"))
  book.addSection("Show", getArticle("http://basking-cat.blogspot.com/2011/12/show.html"))
  book.addSection("Semigroup, Zero, Monoid", getArticle("http://basking-cat.blogspot.com/2011/12/semigroup.html"))
  book.addSection("Order, Ordering", getArticle("http://basking-cat.blogspot.com/2011/12/oder-odering.html"))
  book.addSection("Pure", getArticle("http://basking-cat.blogspot.com/2011/12/pure.html"))
  book.addSection("Functor", getArticle("http://basking-cat.blogspot.com/2011/12/functor.html"))
  book.addSection("Bind", getArticle("http://basking-cat.blogspot.com/2011/12/bind.html"))
  book.addSection("Apply", getArticle("http://basking-cat.blogspot.com/2011/12/apply.html"))
  book.addSection("第二回Scala会議", getArticle("http://basking-cat.blogspot.com/2011/12/scala.html"))
  book.addSection("NonEmptyList, Validation", getArticle("http://basking-cat.blogspot.com/2011/12/nonemptylist.html"))
  book.addSection("Applicative, ApplicativeBuilder", getArticle("http://basking-cat.blogspot.com/2011/12/applicative-applicativebuilder.html"))
  book.addSection("Monad", getArticle("http://basking-cat.blogspot.com/2011/12/monad.html"))
  book.addSection("Length, Index", getArticle("http://basking-cat.blogspot.com/2011/12/length-index.html"))
  book.addSection("Empty, Each, Plus", getArticle("http://basking-cat.blogspot.com/2011/12/empty-each-foreach-plus-plus.html"))
  book.addSection("Traverse", getArticle("http://basking-cat.blogspot.com/2011/12/traverse.html"))
  book.addSection("Memo", getArticle("http://basking-cat.blogspot.com/2011/12/memo.html"))
  book.addSection("Digit", getArticle("http://basking-cat.blogspot.com/2011/12/digit.html"))
  book.addSection("IterV, Enumerator", getArticle("http://basking-cat.blogspot.com/2011/12/iterv.html"))
  book.addSection("Category, Endo, Dual", getArticle("http://basking-cat.blogspot.com/2011/12/category-endo-dual.html"))
  book.addSection("Reducer, Generator", getArticle("http://basking-cat.blogspot.com/2011/12/reducer.html"))
  book.addSection("Foldable", getArticle("http://basking-cat.blogspot.com/2011/12/foldable.html"))
  book.addSection("Writer, WriterT", getArticle("http://basking-cat.blogspot.com/2011/12/writer.html"))
  book.addSection("State, StateT", getArticle("http://basking-cat.blogspot.com/2011/12/state-statet.html"))
  book.addSection("Kleisli", getArticle("http://basking-cat.blogspot.com/2011/12/kleisli.html"))

  book.getResources.add(new Resource(new java.net.URL("https://gist.github.com/stylesheets/gist/embed.css").openStream, "gistembed.css"))
  val epubWriter = new EpubWriter
  epubWriter.write(book, new FileOutputStream("ScalazAdventCalendar2011.epub"))


  http.shutdown()

}
