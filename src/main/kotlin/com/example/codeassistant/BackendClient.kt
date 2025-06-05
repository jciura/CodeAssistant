package com.example.codeassistant

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object BackendClient {

    @Throws(Exception::class)
    fun askRagNode(question: String): String {
        val url = URL("http://localhost:8000/ask_rag_node")
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json; utf-8")
        con.setRequestProperty("Accept", "application/json")
        con.doOutput = true

        val jsonInputString = "{\"question\": \"${question.replace("\"", "\\\"")}\"}"

        con.outputStream.use { os: OutputStream ->
            val input = jsonInputString.toByteArray(StandardCharsets.UTF_8)
            os.write(input, 0, input.size)
        }

        return con.inputStream.bufferedReader(StandardCharsets.UTF_8).use(BufferedReader::readText)
    }
}
