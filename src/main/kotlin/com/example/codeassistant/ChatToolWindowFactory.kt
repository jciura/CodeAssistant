package com.example.codeassistant

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.json.JSONObject
import javax.swing.*
import java.awt.BorderLayout

class ChatToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = JPanel(BorderLayout())

        val chatArea = JTextArea().apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }

        val scrollPane = JScrollPane(chatArea).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        }

        val inputField = JTextField()

        panel.add(scrollPane, BorderLayout.CENTER)
        panel.add(inputField, BorderLayout.SOUTH)

        inputField.addActionListener {
            val question = inputField.text
            if (question.isNotBlank()) {
                chatArea.append("User: $question\n")
                inputField.text = ""

                Thread {
                    try {
                        val jsonResponse = BackendClient.askRagNode(question)
                        val obj = JSONObject(jsonResponse)
                        val answer = obj.optString("answer", "No answer")

                        SwingUtilities.invokeLater {
                            chatArea.append("Assistant: $answer\n")
                            chatArea.caretPosition = chatArea.document.length
                        }
                    } catch (ex: Exception) {
                        SwingUtilities.invokeLater {
                            chatArea.append("Assistant: Error contacting backend\n")
                        }
                        ex.printStackTrace()
                    }
                }.start()
            }
        }

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
