package com.example.demo.view

import javafx.scene.control.Label
import javafx.scene.control.SelectionMode
import tornadofx.*
import java.io.File
import java.lang.StringBuilder

class MainView : View("Lab work 2") {
    var countOfLines: Label by singleAssign()
    private var countOfLinesWithoutCommentsAndEmptyLines: Label by singleAssign()
    var constantCount: Label by singleAssign()
    override val root = gridpane {
        anchorpane {
            prefHeight = 337.0
            prefWidth = 502.0
            gridpane {
                layoutX = 5.0
                layoutY = 9.0
                prefHeight = 98.0
                prefWidth = 493.0
                row {
                    label(text = "Загальна кількість рядків коду")
                    countOfLines = label().gridpaneConstraints {
                        columnRowIndex(0, 1)
                    }
                }
                row {
                    label(text = "Кількість рядків без порожніх рядків та без коментарів") {
                        prefHeight = 33.0
                        prefWidth = 320.0
                    }
                    countOfLinesWithoutCommentsAndEmptyLines = label("шото там").gridpaneConstraints {
                        columnRowIndex(0, 1)
                    }
                }
                row {
                    label(text = "Кількість змінних, які носять тип – константа")
                    constantCount = label().gridpaneConstraints {
                        columnRowIndex(0, 1)
                    }
                }
            }
            label(text = "Список типів даних, використаних при описі змінних") {
                layoutX = 5.0
                layoutY = 120.0
            }
            val typeList = listview<String> {
                selectionModel.selectionMode = SelectionMode.MULTIPLE
                layoutX = 14.0
                layoutY = 137.0
                prefHeight = 200.0
                prefWidth = 200.0
            }
            button(text = "Обрахувати") {
                layoutX = 239.0
                layoutY = 225.0
                isMnemonicParsing = false
                setOnAction {
                    countOfLines.text = getCount("D:\\university\\test\\src\\Test.java").toString()
                    countOfLinesWithoutCommentsAndEmptyLines.text =
                            getCounWithoutCommentsAndEmptyLines("D:\\university\\test\\src\\Test.java")
                                    .toString()
                    constantCount.text = getConstantCount("D:\\university\\test\\src\\Test.java").toString()
                    for (i in findAllTypes("D:\\university\\test\\src\\Test.java")) {
                        typeList.items.add(i)
                    }
                }
            }
            button(text = "Записати") {
                layoutX = 351.0
                layoutY = 225.0
                isMnemonicParsing = false
                setOnAction {
                    File("someFile.txt").writeText(getTextFromComments("D:\\university\\test\\src\\Test.java"))
                }
            }
        }
    }

    private fun getCount(fileName: String) = File(fileName).readLines().count()

    private fun getCounWithoutCommentsAndEmptyLines(fileName: String): Int {
        var text = readFileLineUsingForEachLine(fileName)
        text = text.replace(Regex(pattern = """(/\*)+[^*]*\*+([^/*][^*]*\*+)*/"""), replacement = "")
        text = text.replace(Regex(pattern = """[/]+.*"""), replacement = "")
        text = text.replace("(?m)^[ \t]*\r?\n".toRegex(), replacement = "")

        return text.lines().count() - 1
    }

    private fun readFileLineUsingForEachLine(fileName: String) = File(fileName).readText()

    private fun getConstantCount(fileName: String): Int {
        val text = readFileLineUsingForEachLine(fileName)
        return """(final)""".toRegex().findAll(text).count()
    }

    private fun findAllTypes(fileName: String): MutableSet<String> {
        val text = readFileLineUsingForEachLine(fileName)
        val findAll = """(?<=new )(.*)(?=\b)|(int)|(boolean)|(char)|(float)|(double)|(long)|(short)""".toRegex().findAll(text)
        val set: MutableSet<String> = mutableSetOf()
        for (i in findAll) {
            set.add(i.value)
        }
        return set
    }

    private fun getTextFromComments(fileName: String): String {
        val text = readFileLineUsingForEachLine(fileName)
        val findAll = """(/\*)+[^*]*\*+([^/*][^*]*\*+)*/|[/]+.*""".toRegex().findAll(text)
        val newText = StringBuilder()
        for (i in findAll) {
            newText.append(i.value + "\n")
        }
        """(//)|(/\*)|(\*/)""".toRegex().replace(newText, "")
        return newText.toString()
    }
}