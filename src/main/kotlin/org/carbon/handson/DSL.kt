package org.carbon.handson

import java.awt.Desktop
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Soda 2018/07/15.
 */

interface Node {
    enum class TabType(val character: String) {
        Soft("    "),
        Hard("\t"),
    }

    companion object {
        var tabType: TabType = TabType.Soft
    }

    private operator fun String.times(cnt: Int) = this.repeat(cnt)

    fun StringBuilder.append(depth: Int, value: String) = this.append(getIndent(depth)).append(value)
    fun StringBuilder.appendln(depth: Int, value: String) = this.append(getIndent(depth)).append(value).appendln()

    fun render(base: StringBuilder, depth: Int)
    fun getIndent(depth: Int): String = tabType.character * depth
}

class InnerText(private val text: String) : Node {

    override fun render(base: StringBuilder, depth: Int) {
        base.appendln("${getIndent(depth)}$text")
    }
}

@DslMarker
annotation class HtmlDSL

// ===================================================================================
//                                                                          Tags
//                                                                          ==========
@HtmlDSL
abstract class Tag(private val name: String) : Node {
    protected val attributes = hashMapOf<String, String>()
    //    open protected fun begin(base: StringBuilder, depth: Int) {
    //        base.append(depth, "<$name ")
    //        applyAttribute(base)
    //    }
    //
    //    protected abstract fun modify(base: StringBuilder, depth: Int)
    //    protected abstract fun commit(base: StringBuilder, depth: Int)

    /**
     * render attribute
     * ## example
     * ```kotlin
     *     a {
     *         "href"*"https://www.google.com"
     *     }
     *     // => <a href="https://www.google.com" />
     * ```
     */
    operator fun String.times(x: String) {
        attributes[this] = x
    }

    infix protected fun StringBuilder.apply(apply: StringBuilder.(self: StringBuilder) -> Unit): StringBuilder {
        this.apply(this)
        return this
    }

    protected fun applyAttribute(base: StringBuilder) {
        for ((key, value) in attributes) {
            base.append("$key=\"$value\" ")
        }
    }
}

interface ChildRenderTrait {
    fun refParent(): TagWithChildren?
    fun <T : Tag> initTag(el: T, renderChild: T.() -> Unit = {}): T {
        el.renderChild()
        refParent()?.addChild(el)
        return el
    }
}

abstract class TagWithChildren(private val name: String) : Tag(name), ChildRenderTrait {
    protected val children = arrayListOf<Node>()

    override fun refParent(): TagWithChildren? = this

    fun addChild(value: Node) = children.add(value)

    override fun render(base: StringBuilder, depth: Int) {
        base.append(depth, "<$name ")
        if (children.isEmpty()) {
            base.apply { applyAttribute(it) }
                    .appendln("/>")
        } else {
            base
                    .apply { applyAttribute(it) }
                    .appendln(">")
            renderChildren(base, depth)
            base.appendln(depth, "</$name>")
        }
    }

    /**
     * Override to change the rendering way of the children
     */
    protected open fun renderChildren(base: StringBuilder, depth: Int) {
        children.forEach { it.render(base, depth + 1) }
    }

    /**
     * append text
     * ## example
     * ```kotlin
     *     p {
     *         +"hello world!"
     *     }
     *     // => <p>hello world!</p>
     * ```
     */
    operator fun String.unaryPlus() {
        children.add(InnerText(this))
    }
}

// ===================================================================================
//                                                                        Concrete Tag
//                                                                          ==========
class HTML : TagWithChildren("html") {
    override fun refParent(): TagWithChildren? = this
    fun head(content: Head.() -> Unit) = initTag(Head(), content)
    fun body(content: Body.() -> Unit) = initTag(Body(), content)
}

// -----------------------------------------------------
//                                               Head
//                                               -------
class Head : TagWithChildren("head") {
    override fun refParent(): TagWithChildren? = this
    fun title(content: Title.() -> Unit) = initTag(Title(), content)
    fun meta(content: Meta.() -> Unit) = initTag(Meta(), content)
}

class Title : TagWithChildren("title")
class Meta : TagWithChildren("meta")

// -----------------------------------------------------
//                                               Body
//                                               -------
interface BodyTrait : ChildRenderTrait {
    // cf. https://www.w3schools.com/tags/ref_byfunc.asp
    // -----------------------------------------------------
    //                                               Basic
    //                                               -------
    fun h1(fnMod: Body.InnerTag.() -> Unit): H1 = initTag(H1(), fnMod)

    fun h2(fnMod: Body.InnerTag.() -> Unit): H2 = initTag(H2(), fnMod)
    fun h3(fnMod: Body.InnerTag.() -> Unit): H3 = initTag(H3(), fnMod)
    fun h4(fnMod: Body.InnerTag.() -> Unit): H4 = initTag(H4(), fnMod)
    fun h5(fnMod: Body.InnerTag.() -> Unit): H5 = initTag(H5(), fnMod)
    fun h6(fnMod: Body.InnerTag.() -> Unit): H6 = initTag(H6(), fnMod)
    fun p(fnMod: Body.InnerTag.() -> Unit): P = initTag(P(), fnMod)
    fun br() = initTag(Br())
    fun hr() = initTag(Hr())
    // -----------------------------------------------------
    //                                               Formatting
    //                                               -------
    fun pre(fnMod: Body.InnerTag.() -> Unit): Pre = initTag(Pre(), fnMod)

    // -----------------------------------------------------
    //                                               Link
    //                                               -------
    fun a(fnMod: Body.InnerTag.() -> Unit): A = initTag(A(), fnMod)

    // -----------------------------------------------------
    //                                               Styles and Semantics
    //                                               -------
    fun div(fnMod: Body.InnerTag.() -> Unit): Div = initTag(Div(), fnMod)

    // -----------------------------------------------------
    //                                               List
    //                                               -------
    fun ol(fnMod: ListTag.() -> Unit): Ol = initTag(Ol(), fnMod)

    fun ul(fnMod: ListTag.() -> Unit): Ul = initTag(Ul(), fnMod)
}

interface ListTrait : ChildRenderTrait {
    fun li(fnMod: Li.() -> Unit): Li = initTag(Li(), fnMod)
}

abstract class ListTag(name: String) : TagWithChildren(name), ListTrait

class Body : TagWithChildren("body"), BodyTrait {
    abstract class InnerTag(name: String) : TagWithChildren(name), BodyTrait
}

class H1 : Body.InnerTag("h1")
class H2 : Body.InnerTag("h2")
class H3 : Body.InnerTag("h2")
class H4 : Body.InnerTag("h2")
class H5 : Body.InnerTag("h2")
class H6 : Body.InnerTag("h2")
class P : Body.InnerTag("p")
class Br : Body.InnerTag("br")
class Hr : Body.InnerTag("hr")
class Pre : Body.InnerTag("pre")
class A : Body.InnerTag("a")
class Div : Body.InnerTag("div")
class Ol : ListTag("ol")
class Ul : ListTag("ul")
class Li : Body.InnerTag("li")


fun html(fnMod: HTML.() -> Unit): HTML {
    val html = HTML()
    html.fnMod()
    return html
}

fun main(args: Array<String>) {
    val html =
            html {
                head {
                    meta { "charset" * "utf-8" }
                    meta { "description" * "this is generated by kotlin DSL" }
                    meta { "name" * "viewport"; "content" * "width=device-width, initial-scale=1" }
                    title { +"Carbon | Kotlin Sample" }
                }
                body {
                    h1 {
                        +"Carbon | Kotlin Sample"
                    }
                    h2 {
                        +"table of contents"
                    }
                    ul {
                        li {
                            a {
                                "href" * "#anchor_about"
                                +"About"
                            }
                        }
                        li {
                            a {
                                "href" * "#anchor_carbon"
                                +"Carbon"
                            }
                        }
                        li {
                            a {
                                "href" * "#anchor_kotlin"
                                +"Kotlin"
                            }
                        }
                        li {
                            a {
                                "href" * "#anchor_dsl"
                                +"DSL"
                            }
                        }
                    }
                    div {
                        h2 {
                            "id" * "anchor_about"
                            +"About"
                        }
                        pre {
                            +"This is type safe html powered by Kotlin DSL example"
                        }
                    }
                    hr()
                    div {
                        h2 {
                            "id" * "anchor_carbon"
                            +"Carbon"
                        }
                        pre {
                            +"Carbon is a java web framework inspired by Spring."
                        }
                    }
                    hr()
                    div {
                        h2 {
                            "id" * "anchor_kotlin"
                            +"Kotlin"
                        }
                        pre {
                            +"Statically typed programming language"
                            br()
                            +"for modern multiplatform applications"
                        }
                    }
                    hr()
                    div {
                        h2 {
                            "id" * "anchor_dsl"
                            +"DSL"
                        }
                        pre {
                            +"DSLs are small languages, focused on a particular aspect of a software system."
                        }
                    }
                }
            }
    val out = StringBuilder()
    html.render(out, 0)
    val htmlPath = Paths.get("kotlin.autogen.html")
    Files.newBufferedWriter(htmlPath).use {
        it.append(out.toString())
        it.flush()
    }
    Desktop.getDesktop().browse(htmlPath.toUri())
}
