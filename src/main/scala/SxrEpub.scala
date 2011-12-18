
import java.io.FileOutputStream

import nl.siegmann.epublib.domain.Author
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubWriter

import scalax.file.Path

object SxrEpub {
  def main(args: Array[String]) {
    println(args.toList)
    val output = args(0)
    val sxrdir = args(1)
    val title = args.drop(2).mkString(" ")

    val book = new Book
    book.getMetadata.addTitle(title)

    val root = Path(sxrdir)
    val dirs = root * "*" filter(_.isDirectory)

    book.addSection("Index", new Resource((root / "index.html" toURL).openStream, "index.html"))

    val commons = List("style.css").map(root / _)
    for (c <- commons)
      book.getResources.add(new Resource(c.toURL.openStream, c.relativize(root).path))

    for (d <- dirs; html <- d ** "*.html") {
      val name = html.relativize(root).path
      book.addSection(name, new Resource(html.toURL.openStream, name))
    }

    val epubWriter = new EpubWriter
    epubWriter.write(book, new FileOutputStream(output))

  }

}
